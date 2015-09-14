package org.lombardrisk.repat.pages;

import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.lombardrisk.repat.pojo.FormInstance;
import org.lombardrisk.repat.pojo.XBRL;
import org.lombardrisk.repat.utils.BrowserDriver;
import org.lombardrisk.repat.utils.Business;
import org.lombardrisk.repat.utils.Property;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * @author Kenny Wang
 * 
 */
public class HomePage extends Page {

	public enum Pagination {
		FIFTEEN("15"), FIFTY("50"), HUNDRED("100");
		private String value;

		private Pagination(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	protected String returnTableXpath = "//tbody[@id='formInstanceListForm:formInstanceListTable_data']";
	protected By regulatorList = By
			.xpath("//select[contains(@id,'regulator')]");
	protected By formHeader = By.id("formHeader:lblUser_button");
	protected By logout = By.xpath("//a[span='" + i18n.toString("Logout")
			+ "']");
	protected By preferences = By.xpath("//a[span='"
			+ i18n.toString("Preferences") + "']");
	protected By timezoneCheckBox = By
			.xpath("//*[@id='preferencesForm:timeZoneCheck']/div/span[contains(@class,'ui-icon-check')]");
	protected By currentLocationLabel = By
			.id("preferencesForm:defaultTimeZone");
	protected By timezoneList = By.id("preferencesForm:selectTimeZone");
	protected By languageCheckBox = By
			.xpath("//*[@id='preferencesForm:languageCheck']/div/span[contains(@class,'ui-icon-check')]");
	protected By regionLanguageLabel = By
			.xpath("//label[2][../label[contains(text(),'"
					+ i18n.toString("Regional language") + "')]]");
	protected By languageList = By.id("preferencesForm:selectLanguage");
	protected By savePreferenceBtn = By.id("preferencesForm:confirm");
	protected By cancelPreferenceBtn = By.id("preferencesForm:cancel");

	protected By groupList = By.xpath("//select[contains(@id,'selectGroup')]");
	protected By formList = By.xpath("//select[contains(@id,'selectForm')]");
	protected By avaiableDateList = By
			.xpath("//select[contains(@id,'selectProcessDate')]");
	protected By createNewButton = By
			.xpath("//button[contains(@id,'createNew')]");
	protected By createNewLink = By.linkText(i18n.toString("Create New"));
	protected By createFromExcelLink = By.linkText(i18n
			.toString("Create from Excel"));
	protected By computeReturnButton = By
			.xpath("//button[contains(@id,'compute')]");
	protected By exportButton = By
			.xpath("//button[contains(@id,'exportFile')]");
	protected By firstArrow = By
			.xpath("//span[@class='ui-icon ui-icon-seek-first']");
	protected By previousArrow = By
			.xpath("//span[@class='ui-icon ui-icon-seek-prev']");
	protected By nextArrow = By
			.xpath("//span[@class='ui-icon ui-icon-seek-next']");
	protected By lastArrow = By
			.xpath("//span[@class='ui-icon ui-icon-seek-end']");
	protected By paginationList = By
			.xpath("//*[@id='formInstanceListForm:formInstanceListTable_paginator_bottom']/select");
	protected By returnTable = By
			.id("formInstanceListForm:formInstanceListTable");
	protected By createGroupList = By.id("createForm:selectGroup");
	protected By createFormList = By.id("createForm:selectFromInstance");
	protected By createDateInput = By.id("createForm:processDate_input");
	protected By copyDataFromCheck = By
			.xpath("//div[@id='createForm:cloneCheck']/div[span]");
	protected By copyDataFromCheckSpan = By
			.xpath("//div[@id='createForm:cloneCheck']/div/span");
	protected By copyDataFromList = By.id("createForm:selectCloneDate");
	protected By createButton = By.id("createForm:create");
	protected By cancelButton = By.id("createForm:cancel");
	protected By createConfirmButton = By.id("createconfirm");
	protected By createDeclineButton = By.id("createdecline");
	protected By datepicker = By.id("ui-datepicker-div");
	protected By datepickerMonth = By.cssSelector("select.ui-datepicker-month");
	protected By datepickerYear = By.cssSelector("select.ui-datepicker-year");

	public HomePage(BrowserDriver driver) {
		super(driver);
		driver.waitElementVisiable(formHeader);
		waitStatusDialog();
		driver.assertPageTitle(title);
	}

	public void selectRegulator(String regulator) {
		logger.info("Try to select regulator=" + regulator);
		driver.selectByVisibleText(regulatorList, regulator);
		waitStatusDialog();
	}

	public void assertFormHeader(String username) {
		driver.assertElementText(formHeader, i18n.toString("hi") + " "
				+ username.toUpperCase());
	}

	public void assertLanguge(String language) {

	}

	public void selectGroup(String group) {
		logger.info("Try to select group=" + group);
		driver.selectByVisibleText(groupList, group);
		waitStatusDialog();
	}

	public void selectForm(String form) {
		logger.info("Try to select form=" + form);
		driver.selectByVisibleText(formList, form);
		waitStatusDialog();
	}

	public void selectAvaiableDate(String avaiableDate) {
		logger.info("Try to select avaiableDate=" + i18n.toDate(avaiableDate));
		driver.selectByVisibleText(avaiableDateList, i18n.toDate(avaiableDate));
		waitStatusDialog();
	}

	public void assertReturnTableColumn(String columnName) {

	}

	public void showNextReturns() {
	}

	public void selectPagination(String pagination) {
		logger.info("Try to select pagination=" + pagination);
		driver.selectByVisibleText(paginationList, pagination);
		waitStatusDialog();
	}

	public ReturnPage openReturn(String type, String... property) {
		String xpath = returnTableXpath + "/tr/td/a[text()='" + type + "'";
		for (String p : property) {
			if (Business.isDate(p))
				p = i18n.toDate(p);
			else
				p = i18n.toString(p);
			xpath = xpath + " and ../../td='" + p + "'";
		}
		xpath = xpath + "]";
		logger.info("Try to open return xpath=" + xpath);
		driver.click(By.xpath(xpath));
		waitStatusDialog();
		return new ReturnPage(driver);
	}

	public void assertReturnExists(String type, String... property) {
		String xpath = returnTableXpath + "/tr/td/a[text()='" + type + "'";
		for (String p : property) {
			if (Business.isDate(p))
				p = i18n.toDate(p);
			else
				p = i18n.toString(p);
			xpath = xpath + " and ../../td='" + p + "'";
		}
		xpath = xpath + "]";
		driver.assertElementDisplayed(By.xpath(xpath), true);
	}

	public void assertPaginationButtonEnabled(WebElement element,
			Boolean enabled) {

	}

	/**
	 * @return LoginPage
	 */
	public LoginPage logout() {
		logger.info("Try to logout from home page");
		driver.click(formHeader);
		driver.click(logout);
		return new LoginPage(driver);
	}

	/**
	 * @param group
	 * @param form
	 * @param date
	 * @param copyDataFrom
	 * @param fromDate
	 * @param dicision
	 * @param confirm
	 * @return ReturnPage
	 */
	public ReturnPage createNew(String group, String form, String date,
			Boolean copyDataFrom, String fromDate, Boolean dicision,
			Boolean confirm) {
		logger.info("Try to create new return with group=" + group + ", form="
				+ form + ", date=" + i18n.toDate(date) + ", copyDataFrom="
				+ copyDataFrom + ", fromDate=" + i18n.toDate(fromDate)
				+ ", dicision=" + dicision + ", confirm=" + confirm);
		driver.click(createNewButton);
		driver.click(createNewLink);
		waitStatusDialog();
		if (group != null) {
			driver.selectByVisibleText(createGroupList, group);
			waitStatusDialog();
		}

		if (form != null) {
			driver.selectByVisibleText(createFormList, form);
			waitStatusDialog();
		}

		if (date != null) {
			// type(createDateInput, date);
			setCreateDateInput(date);
			waitStatusDialog();
		}

		if (copyDataFrom != null) {
			tickCopyDataFromCheck(copyDataFrom);
			if (copyDataFrom)
				if (fromDate != null)
					driver.selectByVisibleText(copyDataFromList,
							i18n.toDate(fromDate));
		}

		if (dicision) {
			driver.click(createButton);
		} else {
			driver.click(cancelButton);
		}
		waitStatusDialog();

		if (confirm) {
			if (driver.isElementDisplayed(createConfirmButton)) {
				driver.click(createConfirmButton);
				waitStatusDialog();
			}
		} else {
			if (driver.isElementDisplayed(createDeclineButton)) {
				driver.click(createDeclineButton);
				waitStatusDialog();
			}
		}
		return new ReturnPage(driver);
	}

	public void tickCopyDataFromCheck(Boolean checked) {
		String attr = driver
				.getElementAttribute(copyDataFromCheckSpan, "class");
		if (checked) {
			if (attr.equals("ui-chkbox-icon ui-c")) {
				driver.click(copyDataFromCheck);
				waitStatusDialog();
			}
		} else {
			if (attr.equals("ui-chkbox-icon ui-c ui-icon ui-icon-check")) {
				driver.click(copyDataFromCheck);
				waitStatusDialog();
			}
		}
	}

	public void setCreateDateInput(String date) {
		String dateArray[] = date.split(" ");
		String month = dateArray[0];
		String day = dateArray[1].replace(",", "");
		String year = dateArray[2];

		driver.click(createDateInput);
		driver.waitElementVisiable(datepicker);
		driver.selectByVisibleText(datepickerMonth, month);
		driver.selectByVisibleText(datepickerYear, year);
		driver.click(By.linkText(day));
		driver.waitElementInVisiable(datepicker);
	}

	/**
	 * Export To XBRL
	 * 
	 * @param xbrl
	 */
	public String exportToXBRL(XBRL xbrl) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		@SuppressWarnings("deprecation")
		String date = sdf.format(new Date(xbrl.date));
		String filename = driver.downloaddir + "/" + xbrl.moduleCode + "_"
				+ Property.productType + "_" + xbrl.group + "_" + date
				+ ".xbrl.zip";
		logger.info("try to delete file " + filename);
		Business.deleteFile(filename);

		driver.click(By.xpath("//button[contains(@id,'xbrlSubmit')]"));
		driver.waitElementVisiable(By.id("transmitDialog"));

		driver.selectByVisibleText(By.id("transmitForm:selectGroup"),
				xbrl.group);
		waitStatusDialog();

		driver.selectByVisibleText(By.id("transmitForm:selectProcessDate"),
				xbrl.date);
		waitStatusDialog();

		driver.selectByVisibleText(By.id("transmitForm:selectModule"),
				xbrl.module);
		waitStatusDialog();

		driver.selectByVisibleText(By.id("transmitForm:selectModuleCode"),
				xbrl.moduleCode);
		waitStatusDialog();

		for (FormInstance form : xbrl.getList()) {
			String formName = form.formName;
			// driver.click(By.xpath("//tbody[@id='transmitForm:formInstanceListTable_data']/tr/td[text()=\""
			// + formName + "\"]/preceding-sibling::td[1]"));
			WebElement e = driver
					.getDriver()
					.findElement(
							By.xpath("//tbody[@id='transmitForm:formInstanceListTable_data']/tr/td[text()=\""
									+ formName
									+ "\"]/preceding-sibling::td[1]/div"));
			e.click();
			waitStatusDialog();
			for (String version : form.version) {
				driver.click(By
						.xpath("//tbody[@id='transmitForm:formInstanceListTable_data']/tr/td[text()=\""
								+ formName
								+ "\"]/following::td[text()=\""
								+ version
								+ "\"]/preceding-sibling::td[1]/div/div[2]"));
				waitStatusDialog();
			}
		}
		if (xbrl.export) {

			logger.info("xbrl.filepath-->" + driver.downloaddir);
			driver.click(By.id("transmitSubmitForm:nextBtn"));
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			waitStatusDialog();

			if (BrowserDriver.INTERNETEXPLORER.equals(driver.browser))
				Business.downloadfileWithIEBrowser(driver);

			try {
				driver.click(By.id("transmitLogForm:cancelBtn"));
			} catch (NoSuchElementException e) {
			}
		} else {
			driver.click(By.id("transmitSubmitForm:cancelBtn"));
		}

		return filename;
	}

	public Page createFromExcel(String filepath, String comments,
			Boolean openReturn, Boolean importCancel) {
		filepath = filepath.replaceAll("/", "\\\\");
		logger.info("Try to create new return from Excel ,filepath = "
				+ filepath + ", comments = " + comments
				+ ", openReturnAfterExport = " + openReturn + ", import = "
				+ importCancel);

		driver.click(createNewButton);
		driver.click(createFromExcelLink);
		driver.waitElementVisiable(By.id("createFromExcelDialog"));

		driver.click(By
				.xpath("//div[@id='createFromExcelForm:importFileUpload']/div/label"));
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		StringSelection stringSelection = new StringSelection(filepath);
		Toolkit.getDefaultToolkit().getSystemClipboard()
				.setContents(stringSelection, null);

		try {
			Robot robot = new Robot();
			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_V);
			Thread.sleep(3000);
			robot.keyRelease(KeyEvent.VK_V);
			robot.keyRelease(KeyEvent.VK_CONTROL);

			Thread.sleep(1000);
			robot.keyPress(KeyEvent.VK_ALT);
			robot.keyPress(KeyEvent.VK_O);

			Thread.sleep(1000);
			robot.keyRelease(KeyEvent.VK_O);
			robot.keyRelease(KeyEvent.VK_ALT);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Toolkit.getDefaultToolkit().getSystemClipboard().setContents(null,
		// null);

		waitStatusDialog();

		WebElement textarea = driver.getDriver().findElement(
				By.id("createFromExcelForm:errorTextarea"));
		textarea.sendKeys(comments);

		WebElement openCheckBox = driver
				.getDriver()
				.findElement(
						By.xpath("//*[@id=\"createFromExcelForm\"]/div/table/tbody/tr/td[1]/div[1]"));
		if (!openReturn)
			openCheckBox.click();

		if (importCancel) {
			WebElement importButton = driver.getDriver().findElement(
					By.id("createFromExcelForm:listimportBtn"));
			if (importButton.isEnabled()) {
				logger.info(" begin import...");
				importButton.click();
			} else {
				logger.info(" import button is disabled.");
			}
		} else {
			WebElement cancelButton = driver.getDriver().findElement(
					By.id("createFromExcelForm:listcancelBtn"));
			cancelButton.click();
		}

		if (openReturn && importCancel)
			return new ReturnPage(driver);
		else
			return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.lombardrisk.repat.pages.Page#isCurrentPage()
	 */
	public Boolean isCurrentPage() {
		return driver.isElementDisplayed(formHeader);
	}

	/**
	 * importAdjustments
	 * 
	 * @param filepath : should be absolute path, as : D:/a.xls
	 * @param openReturn <br/>
	 *            true : tick "Open return after importing" <br/>
	 *            false : untick "Open return after importing" <br/>
	 * @param importMode <br/>
	 *            0 : select "Replace existing return(if any)" <br/>
	 *            1 : select "Add to existing value (Numeric cells only)" <br/>
	 * @param importCancel <br/>
	 *            true : will click import button <br/>
	 *            false : will click cancel button <br/>
	 * @return
	 */
	public Page importAdjustments(String filepath, Boolean openReturn,
			Integer importMode, Boolean importCancel) {
		filepath = filepath.replaceAll("/", "\\\\");
		filepath = (new File(filepath)).getAbsolutePath();
		logger.info("Try to import Adjustments, file path : " + filepath);
		driver.click(By.xpath("//button[contains(@id,'importFile')]"));
		driver.waitElementInVisiable(By.id("listImportFilesDialog"));
		if (importMode == 1) {
			driver.click(By.xpath("//table[@id='listImportFileForm:importMode']//td[3]//span"));
		}
		driver.click(By
				.xpath("//div[@id='listImportFileForm:importFileUpload']/div[1]/span"));
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		StringSelection stringSelection = new StringSelection(filepath);
		Toolkit.getDefaultToolkit().getSystemClipboard()
				.setContents(stringSelection, null);

		try {
			Robot robot = new Robot();
			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_V);
			Thread.sleep(3000);
			robot.keyRelease(KeyEvent.VK_V);
			robot.keyRelease(KeyEvent.VK_CONTROL);

			Thread.sleep(1000);
			robot.keyPress(KeyEvent.VK_ALT);
			robot.keyPress(KeyEvent.VK_O);

			Thread.sleep(1000);
			robot.keyRelease(KeyEvent.VK_O);
			robot.keyRelease(KeyEvent.VK_ALT);
		} catch (Exception e) {
			e.printStackTrace();
		}
		waitStatusDialog();
		
		if (!openReturn) {
			driver.click(By
					.xpath("//form[@id='listImportFileForm']//table[2]//div[contains(@id,'listImportFileForm')]//span"));
		}
		if (importCancel) {
			driver.click(By.id("listImportFileForm:listimportBtn"));
			waitStatusDialog();
		} else {
			driver.click(By.id("listImportFileForm:listcancelBtn"));
		}

		if (openReturn && importCancel)
			return new ReturnPage(driver);
		else
			return this;
	}

	/**
	 * @param location
	 * @param language
	 */
	public void setPreferences(String location, String language) {
		logger.info("Try to set preferences on home page");
		driver.click(formHeader);
		driver.click(preferences);
		if (location != null) {
			driver.click(timezoneCheckBox);
			driver.selectByVisibleText(timezoneList, location);
		}
		if (language != null) {
			driver.click(languageCheckBox);
			driver.selectByVisibleText(languageList, language);
		}
		driver.click(savePreferenceBtn);
	}

	/**
	 * @return string
	 */
	public String getCurrentLocation() {
		driver.click(formHeader);
		driver.click(preferences);
		String ret = driver.getText(currentLocationLabel);
		driver.click(cancelPreferenceBtn);
		return ret;
	}

	/**
	 * @return string
	 */
	public String getRegionLanguage() {
		driver.click(formHeader);
		driver.click(preferences);
		String ret = driver.getText(regionLanguageLabel);
		driver.click(cancelPreferenceBtn);
		return ret;
	}
}
