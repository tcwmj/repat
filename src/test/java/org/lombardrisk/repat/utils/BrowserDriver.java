package org.lombardrisk.repat.utils;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.awt.AWTException;
import java.awt.Robot;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.testng.ITestResult;
import org.testng.Reporter;

/**
 * @author Kenny Wang
 * 
 */
public class BrowserDriver {
	private static Logger logger = Logger.getLogger(BrowserDriver.class);

	private static final String WEB_DRIVER_CHROME = "lib/chromedriver.exe";
	private static final String WEB_DRIVER_IE_X86 = "lib/x64/IEDriverServer.exe";
	private static final String WEB_DRIVER_IE_X64 = "lib/x86/IEDriverServer.exe";
	private static final String SCREENSHOT_DIR = "target/screenshot";

	public static final int TIMEOUT_INTERVAL = Property.timeoutInterval;
	public static final int POLLING_INTERVAL = Property.pollingInterval;

	public static final String WINDOWS = "WINDOWS";
	public static final String OSX = "OS X";
	public static final String WINDOWS_XP = "XP";
	public static final String WINDOWS_7 = "7";
	public static final String WINDOWS_8 = "8";
	public static final String WINDOWS_8_1 = "8.1";
	public static final String CHROME = "chrome";
	public static final String FIREFOX = "firefox";
	public static final String INTERNETEXPLORER = "ie";
	public static final String DEFAULT_BROWSER = Property.defaultBroswer
			.toLowerCase();

	public final String os;
	public final String os_version;
	public final String browser;
	public final String browser_version;
	public final String resolution;
	public final String downloaddir;

	private static WebDriver driver;

	public WebDriver getDriver() {
		return driver;
	}

	private final Wait<WebDriver> wait;

	public BrowserDriver(String os, String os_version, String browser,
			String browser_version, String resolution) {
		this.os = (null == os) ? WINDOWS : os;
		this.os_version = (null == os_version) ? WINDOWS_7 : os_version;
		this.browser = (null == browser) ? DEFAULT_BROWSER : browser;
		this.browser_version = browser_version;
		this.resolution = resolution;

		if (Property.remote) {
			driver = setupRemoteBrowser();
			this.downloaddir = getRemoteDownloadDir();
		} else {
			driver = setupLocalBrowser();
			this.downloaddir = getLocalDownloadDir();
		}
		driver.manage().timeouts()
				.implicitlyWait(TIMEOUT_INTERVAL, TimeUnit.SECONDS);
		driver.manage().window().maximize();
		wait = new FluentWait<WebDriver>(driver)
				.withTimeout(TIMEOUT_INTERVAL, TimeUnit.SECONDS)
				.pollingEvery(POLLING_INTERVAL, TimeUnit.SECONDS)
				.ignoring(StaleElementReferenceException.class)
				.ignoring(NoSuchElementException.class);
	}

	private WebDriver setupRemoteBrowser() {
		DesiredCapabilities capability = new DesiredCapabilities();
		if (os != null)
			capability.setCapability("os", os);
		if (os_version != null)
			capability.setCapability("os_version", os_version);
		capability.setCapability("browser", browser);
		if (browser_version != null)
			capability.setCapability("browser_version", browser_version);
		if (resolution != null)
			capability.setCapability("resolution", resolution);
		capability.setCapability("project", Property.project);
		capability.setCapability("build", Property.build);
		capability.setCapability("browserstack.local",
				Property.browserstacklocal);
		capability.setCapability("browserstack.localIdentifier",
				Property.browserstacklocalIdentifier);
		capability.setCapability("browserstack.debug",
				Property.browserstackdebug);
		URL url = null;
		try {
			url = new URL(Property.browserstackurl);
		} catch (MalformedURLException e) {
			logger.error("url " + Property.browserstackurl + " is malformed");
			e.printStackTrace();
		}
		RemoteWebDriver rwd = new RemoteWebDriver(url, capability);
		rwd.setFileDetector(new LocalFileDetector());
		return rwd;
	}

	private WebDriver setupLocalBrowser() {
		switch (browser.toLowerCase()) {
		case CHROME:
			return setupChome();
		case INTERNETEXPLORER:
			return setupInternetExplorer();
		default:
			return setupFireFox();
		}
	}

	private WebDriver setupChome() {
		System.setProperty("webdriver.chrome.driver", WEB_DRIVER_CHROME);
		return new ChromeDriver();
	}

	private WebDriver setupInternetExplorer() {
		if (isOSX64()) {
			System.setProperty("webdriver.ie.driver", WEB_DRIVER_IE_X64);
		} else {
			System.setProperty("webdriver.ie.driver", WEB_DRIVER_IE_X86);
		}
		return new InternetExplorerDriver();
	}

	private WebDriver setupFireFox() {
		if (Property.firefoxDir != null
				&& !Property.firefoxDir.trim().isEmpty())
			System.setProperty("webdriver.firefox.bin", Property.firefoxDir);
		FirefoxProfile firefoxProfile = new FirefoxProfile();
		firefoxProfile.setPreference("browser.helperApps.neverAsk.saveToDisk",
				"application/zip,application/vnd.ms-excel");

		return new FirefoxDriver();
	}

	private Boolean isOSX64() {
		Properties props = System.getProperties();
		String arch = props.getProperty("os.arch");
		return arch.contains("64");
	}

	private String getRemoteDownloadDir() {
		if (os == OSX)
			return Property.downloadOSXDir;
		else {
			if (os_version == WINDOWS_XP)
				return Property.downloadWINXPDir;
			else
				return Property.downloadWINDir;
		}
	}

	private String getLocalDownloadDir() {
		Properties props = System.getProperties();
		String osname = props.getProperty("os.name").toUpperCase();
		String username = props.getProperty("user.name");
		if (osname.compareTo(WINDOWS) >= 0)
			if (osname.compareTo(WINDOWS_XP) >= 0)
				return "C:/Documents and Settings/" + username
						+ "/My Documents/Downloads";
			else
				return "C:/Users/" + username + "/Downloads";
		else
			return "/Users/" + username + "/Downloads";
	}

	public void get(String url) {
		logger.debug("Try to navigate to url " + url);
		driver.get(url);
	}

	public void quit() {
		driver.quit();
	}

	public void click(By by) {
		logger.debug("Try to click " + by.toString());
		wait.until(ExpectedConditions.elementToBeClickable(by)).click();
	}

	public void doubleClick(By by) {
		logger.debug("Try to double click " + by.toString());
		WebElement element = wait.until(ExpectedConditions
				.elementToBeClickable(by));
		Actions action = new Actions(driver);
		action.doubleClick(element).build().perform();
	}

	public void type(By by, String value) {
		logger.debug("Try to type value " + value + " on " + by.toString());
		if (value != null)
			wait.until(ExpectedConditions.visibilityOfElementLocated(by))
					.sendKeys(value);
	}

	public void clear(By by) {
		logger.debug("Try to clear value on " + by.toString());
		wait.until(ExpectedConditions.visibilityOfElementLocated(by)).clear();
	}

	public void input(By by, String value) {
		if (value != null)
			clear(by);
		type(by, value);
	}

	public void selectByVisibleText(final By by, final String text) {
		logger.debug("Try to select value " + text + " on " + by.toString());
		if (text != null) {
			WebElement element = wait.until(ExpectedConditions
					.visibilityOfElementLocated(by));
			new Select(element).selectByVisibleText(text);
		}
	}

	/**
	 * @param by
	 * @param text
	 */
	public void waitTextSelected(By by, String text) {
		wait.until(ExpectedConditions.textToBePresentInElementLocated(by, text));
	}

	/**
	 * @param by
	 * @param text
	 */
	public void waitTextTyped(By by, String text) {
		wait.until(ExpectedConditions.textToBePresentInElementValue(by, text));
	}

	public Boolean isTextSelectable(By by, String text) {
		WebElement element = wait.until(ExpectedConditions
				.visibilityOfElementLocated(by));
		List<WebElement> elements = element.findElements(By.tagName("option"));
		for (WebElement e : elements) {
			if (text.equals(e.getText())) {
				return true;
			}
		}
		return false;
	}

	public void moveToElement(By by) {
		logger.debug("Try to move mouse to " + by.toString());
		WebElement element = wait.until(ExpectedConditions
				.elementToBeClickable(by));
		Actions action = new Actions(driver);
		action.moveToElement(element).build().perform();
	}

	public boolean isElementPresent(By by) {
		try {
			driver.findElement(by);
			return true;
		} catch (NoSuchElementException | StaleElementReferenceException e) {
			return false;
		}
	}

	public boolean isAlertPresent() {
		try {
			driver.switchTo().alert();
			return true;
		} catch (NoAlertPresentException e) {
			return false;
		}
	}

	public boolean isElementEnabled(By by) {
		try {
			return driver.findElement(by).isEnabled();
		} catch (NoSuchElementException | StaleElementReferenceException e) {
			return false;
		}
	}

	public boolean isElementDisplayed(By by) {
		try {
			return driver.findElement(by).isDisplayed();
		} catch (NoSuchElementException | StaleElementReferenceException e) {
			return false;
		}
	}

	public boolean isElementSelected(By by) {
		try {
			return driver.findElement(by).isSelected();
		} catch (NoSuchElementException | StaleElementReferenceException e) {
			return false;
		}
	}

	public void assertElementEnabled(By by, Boolean enabled) {
		Boolean actual = isElementEnabled(by);
		if (enabled) {
			assertTrue(actual);
		} else {
			assertFalse(actual);
		}
	}

	public void assertElementDisplayed(By by, Boolean displayed) {
		Boolean actual = isElementDisplayed(by);
		if (displayed) {
			assertTrue(actual);
		} else {
			assertFalse(actual);
		}
	}

	public void assertElementSelected(By by, Boolean selected) {
		Boolean actual = isElementSelected(by);
		if (selected) {
			assertTrue(actual);
		} else {
			assertFalse(actual);
		}
	}

	public void assertElementText(By by, String text) {
		WebElement element = wait.until(ExpectedConditions
				.visibilityOfElementLocated(by));
		assertEquals(element.getText(), text);
	}

	public String getElementAttribute(By by, String attribute) {
		WebElement element = wait.until(ExpectedConditions
				.presenceOfElementLocated(by));
		return element.getAttribute(attribute);
	}

	public void assertElementAttribute(By by, String attribute, String value) {
		String actual = getElementAttribute(by, attribute);
		assertEquals(actual, value);
	}

	public void assertElementAriaDisabled(By by, String value) {
		assertElementAttribute(by, "aria-disabled", value);
	}

	public void assertElementAriaSelected(By by, String value) {
		assertElementAttribute(by, "aria-selected", value);
	}

	public void waitElementVisiable(By by) {
		wait.until(ExpectedConditions.visibilityOfElementLocated(by));
	}

	public void waitElementInVisiable(By by) {
		wait.until(ExpectedConditions.invisibilityOfElementLocated(by));
	}

	public void assertListValue(By by, String value) {

	}

	public void assertListValues(By by, String values[]) {

	}

	public void assertListDefaultValue(By by, String value) {

	}

	public void assertTableColumns(By by, String columnNames[]) {

	}

	public void assertTableRowCount(By by, int Count) {

	}

	public void saveScreenShot(String fileName) {
		if (!(new File(SCREENSHOT_DIR).isDirectory())) {
			new File(SCREENSHOT_DIR).mkdir();
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
		String time = sdf.format(new Date());
		TakesScreenshot tsDriver;
		if (Property.remote)
			tsDriver = (TakesScreenshot) (new Augmenter().augment(driver));
		else
			tsDriver = (TakesScreenshot) driver;
		File image = new File(SCREENSHOT_DIR + File.separator + time + "_"
				+ fileName == null ? "" : fileName + ".png");
		tsDriver.getScreenshotAs(OutputType.FILE).renameTo(image);
		logger.debug("take screenshot to " + image.getPath());
	}

	public static void saveScreenShot(ITestResult tr) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
		String mDateTime = formatter.format(new Date());
		String fileName = mDateTime + "_" + tr.getName();
		String filePath = "";
		TakesScreenshot tsDriver;
		if (Property.remote)
			tsDriver = (TakesScreenshot) (new Augmenter().augment(driver));
		else
			tsDriver = (TakesScreenshot) driver;

		try {
			File screenshot = tsDriver.getScreenshotAs(OutputType.FILE);
			filePath = SCREENSHOT_DIR + File.separator + fileName + ".jpg";
			File destFile = new File(filePath);
			FileUtils.copyFile(screenshot, destFile);
		} catch (Exception e) {
			filePath = fileName + " saveScreentshot failure:" + e.getMessage();
			logger.error(filePath);
			e.printStackTrace();
		}

		if (!"".equals(filePath)) {
			Reporter.setCurrentTestResult(tr);

			filePath = filePath.replaceAll("\\\\", "/");
			Reporter.log(filePath);
			Reporter.log("<a href = 'javascript:void(0)' onclick=\"window.open ('../../../"
					+ filePath
					+ "','newwindow','height=600,width=800,top=0,left=0,toolbar=no,menubar=no,scrollbars=no, resizable=no,location=no, status=no')\"><img src=\"../../../"
					+ filePath + "\" width=\"400\" height=\"300\"/></a>");

		}
	}

	public void assertPageTitle(String title) {
		wait.until(ExpectedConditions.titleIs(title));
	}

	public String getCSSAtribute(By by, String attribute) {
		WebElement element = wait.until(ExpectedConditions
				.presenceOfElementLocated(by));
		return element.getCssValue(attribute);
	}

	public void assertCSSAtribute(By by, String attribute, String value) {
		String actual = getCSSAtribute(by, attribute);
		assertEquals(actual, value);
	}

	public void typeKeyEvent(int key) {
		logger.debug("Try to type key event " + key);
		Robot robot;
		try {
			robot = new Robot();
			robot.keyPress(key);
		} catch (AWTException e) {
			logger.error("typeKeyEvent error " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Force to wait specified seconds
	 * 
	 * @param millis
	 *            Milliseconds
	 */
	public void forceWait(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param by
	 * @return string
	 */
	public String getText(By by) {
		WebElement element = wait.until(ExpectedConditions
				.visibilityOfElementLocated(by));
		return element.getText();
	}
}
