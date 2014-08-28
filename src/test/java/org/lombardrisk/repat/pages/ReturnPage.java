package org.lombardrisk.repat.pages;

import static org.lombardrisk.repat.utils.ColorExt.RGBAtoHex;
import static org.testng.Assert.assertEquals;

import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.File;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.lombardrisk.repat.pojo.AdjustmentFilter;
import org.lombardrisk.repat.pojo.AdjustmentLog;
import org.lombardrisk.repat.pojo.FormInstance;
import org.lombardrisk.repat.pojo.Problem;
import org.lombardrisk.repat.pojo.SummingAllocation;
import org.lombardrisk.repat.pojo.TransactionAllocation;
import org.lombardrisk.repat.pojo.Validation;
import org.lombardrisk.repat.pojo.ValidationFilter;
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
public class ReturnPage extends Page {

	/**
	 * Page right background color in red
	 */
	public static final String HEAD_CRITICAL_COLOR = "#CC0033";
	/**
	 * Page right background color in orange
	 */
	public static final String HEAD_X_CRITICAL_COLOR = "#FF6633";
	/**
	 * Cell background color in red
	 */
	public static final String H_X_VAL_CRITICAL_COLOR = "#FACDBD";
	/**
	 * Page background color in blue
	 */
	public static final String HIGHLIGHT_ROW_BLUE = "#B5D9F5";
	/**
	 * Cell outline color in blue
	 */
	public static final String HIGHLIGHT_BORDER_BLUE = "#78ACDD";

	protected By lockButton = By.id("formHeader:lockBtn");
	protected By unlockButton = By.id("formHeader:unlockBtn");
	protected By adjustButton = By.id("formHeader:adjust_button");
	protected By viewAdjustmentLog = By.xpath("//*[text()='"
			+ i18n.toString("View adjustment log") + "']");
	protected By exportButton = By.id("formHeader:exportToFile_button");
	protected By doValidationBtn = By.id("formHeader:doValidationBtn");
	protected By undoValidationBtn = By.id("formHeader:unDoValidationBtn");
	protected By validateButton = By.id("formHeader:validateNowBtn");

	protected By currentPageInstance = By
			.id("formHeader:currentPageInstance_label");
	protected String instancePanelID = "formHeader:currentPageInstance_panel";
	protected By instanceCreateBtn = By.id("formHeader:showCreateDialogButton");
	protected By instanceDeleteBtn = By.id("formHeader:showDeleteDialogButton");
	protected By instanceIdTextBox = By
			.id("formAddInstanceNoSet:txtInstanceId");
	protected By instanceAcceptBtn = By.id("formAddInstanceNoSet:accept2");
	protected By instanceCancelBtn = By.id("formAddInstanceNoSet:cancel2");
	protected By instanceConfirmRemoveBtn = By
			.xpath("//div[contains(@id, 'deleteDialog')]/div/center/div/button[span='"
					+ i18n.toString("Remove") + "']");

	protected By okAdjustmentButton = By
			.id("formInstContentForm:okAdjustmentBtn");
	protected By cancelAdjustmentButton = By
			.id("formInstContentForm:cancelAdjustmentBtn");
	protected By rowActionsIcon = By.id("addRowDivExtDBGrid1");
	protected By insertRowAbove = By.xpath("//a[span('"
			+ i18n.toString("Insert row above") + "']");
	protected By insertRowBelow = By.xpath("//a[span('"
			+ i18n.toString("Insert row below") + "']");
	protected By deleteRow = By.xpath("//a[span('"
			+ i18n.toString("Delete row") + "']");
	protected By confirmRemove = By
			.xpath("//body/form/div/div/center/div/button[span='"
					+ i18n.toString("Remove") + "']");
	protected By cancelRemove = By
			.xpath("//body/form/div/div/center/div/button[span='"
					+ i18n.toString("Cancel") + "']");
	protected By closeReturn = By
			.xpath("//img[contains(@src,'Close.png.jsf')]");
	protected By returnTable = By.id("formInstContentForm");

	protected By adjustmentLink = By.linkText(i18n.toString("ADJUSTMENTS"));
	protected By adjustmentFilterCell = By
			.id("formInstDetailFooterTabView:adjustmentLogForm:cell");
	protected By adjustmentFilterUser = By
			.id("formInstDetailFooterTabView:adjustmentLogForm:user");
	protected By adjustmentFilterFrom = By
			.id("formInstDetailFooterTabView:adjustmentLogForm:fromDate_input");
	protected By adjustmentFilterTo = By
			.id("formInstDetailFooterTabView:adjustmentLogForm:toDate_input");
	protected By adjustmentClearImage = By
			.id("formInstDetailFooterTabView:adjustmentLogForm:clearImage");
	protected String adjustmentLogTableDataID = "formInstDetailFooterTabView:adjustmentLogForm:adjustmentLogTable_data";

	protected By validationLink = By.linkText(i18n.toString("VALIDATION"));
	protected By validationPageList = By
			.id("formInstDetailFooterTabView:validationForm:page");
	protected By validationCellTextBox = By
			.id("formInstDetailFooterTabView:validationForm:cell");
	protected By validationLevelList = By
			.xpath("//select[contains(@id, 'formInstDetailFooterTabView:validationForm') and option='"
					+ i18n.toString("Level") + "']");
	protected By validationResultlList = By
			.xpath("//select[contains(@id, 'formInstDetailFooterTabView:validationForm') and option='"
					+ i18n.toString("Result") + "']");
	protected By validationInstancingList = By
			.xpath("//select[contains(@id, 'formInstDetailFooterTabView:validationForm') and option='"
					+ i18n.toString("Instancing") + "']");
	protected By validationClearImage = By
			.id("formInstDetailFooterTabView:validationForm:clearImage");
	protected String validationTableDataID = "formInstDetailFooterTabView:validationForm:validationTable_data";

	protected By problemsLink = By.linkText(i18n.toString("PROBLEMS"));
	protected String problemsTableDataID = "formInstDetailFooterTabView:errorForm:errorTable_data";

	protected By allocations = By.linkText(i18n.toString("ALLOCATIONS"));
	protected String allocationSummingTableDataID = "formInstDetailFooterTabView:drilldownForm:summingTable_data";
	protected String allocationTransactionTableDataID = "formInstDetailFooterTabView:drilldownForm:transactionTable_data";

	protected By sumConfirmBtn = By.id("warnConfirm");
	protected By sumDeclineBtn = By.id("sumDecline");

	protected By infoPrompt = By
			.xpath("//*[@id='growl_container']/div/div/div[span='"
					+ i18n.toString("Info") + "']");

	public ReturnPage(BrowserDriver driver) {
		super(driver);
		driver.waitElementVisiable(closeReturn);
		waitStatusDialog();
		driver.assertPageTitle(title);
	}

	public void assertReturnTableDisplayed(Boolean displayed) {
		driver.assertElementDisplayed(returnTable, displayed);
	}

	public void assertPages(String pages[]) {
		assertPagesCount(pages.length);
		assertPagesName(pages);
	}

	public void assertPagesCount(int count) {

	}

	public void assertPagesName(String pages[]) {

	}

	public void switchToPage(String pagename) {
		logger.info("Try to switch to page=" + pagename);
		driver.click(getPageLocator(pagename));
		waitStatusDialog();
	}

	public void lockReturn() {
		logger.info("Try to lock return");
		driver.click(lockButton);
		waitStatusDialog();
	}

	public void unloReturnck() {
		logger.info("Try to unlock return");
		driver.click(unlockButton);
		waitStatusDialog();
	}

	public void setCellValue(Boolean extendReturn, String title, String value,
			Boolean dicision) {
		if (extendReturn)
			setCellValueExt(title, value, dicision);
		else
			setCellValue(title, value, dicision);
	}

	public void setCellValueExt(String title, String value, Boolean dicision) {
		logger.info("Try to set extend cell value with title=" + title
				+ ", value=" + value + ", dicision=" + dicision);
		By by = getCellLocator(title, title);
		driver.moveToElement(by);
		driver.click(by);
		driver.input(by, value);
		if (dicision) {
			driver.click(okAdjustmentButton);
			waitStatusDialog();
		} else {
			driver.click(cancelAdjustmentButton);
		}
	}

	public void setCellValue(String title, String value, Boolean dicision) {
		logger.info("Try to set cell value with title=" + title + ", value="
				+ value + ", dicision=" + dicision);
		By outpute = getCellLocator(title, "output" + title);
		driver.moveToElement(outpute);
		driver.click(outpute);
		By inpute = getCellLocator(title, "input" + title);
		driver.input(inpute, value);
		if (dicision) {
			driver.click(okAdjustmentButton);
			waitStatusDialog();
		} else {
			driver.click(cancelAdjustmentButton);
		}
	}

	public String getCellValue(String title) {
		By by = getCellLocator(title);
		return driver.getElementAttribute(by, "value");
	}

	public void assertCellValue(String title, String value) {
		By by = getCellLocator(title);
		driver.assertElementAttribute(by, "value", value);
	}

	public void insertRowAbove(String title) {
		logger.info("Try to insert row above title=" + title);
		By by = getCellLocator(title);
		driver.moveToElement(by);
		driver.click(rowActionsIcon);
		driver.click(insertRowAbove);
		waitStatusDialog();
	}

	public void insertRowBelow(String title) {
		logger.info("Try to insert row below title=" + title);
		By by = getCellLocator(title);
		driver.moveToElement(by);
		driver.click(rowActionsIcon);
		driver.click(insertRowBelow);
		waitStatusDialog();
	}

	public void deleteRow(String title, Boolean confirm) {
		logger.info("Try to delete row with title=" + title + ", confirm="
				+ confirm);
		By by = getCellLocator(title);
		driver.moveToElement(by);
		driver.click(rowActionsIcon);
		driver.click(deleteRow);
		waitStatusDialog();
		if (confirm) {
			driver.click(confirmRemove);
			waitStatusDialog();
		} else {
			driver.click(cancelRemove);
		}
	}

	public By getCellLocator(String title) {
		String xpath = "//input[@title='" + title + "']";
		return By.xpath(xpath);
	}

	public By getCellLocator(String title, String alt) {
		String xpath = "//input[@title='" + title + "' and @alt='" + alt + "']";
		return By.xpath(xpath);
	}

	public By getPageLocator(String pagename) {
		String xpath = "//tr[td/span='" + pagename + "']";
		return By.xpath(xpath);
	}

	public HomePage closeReturnPage() {
		logger.info("Try to close return page");
		driver.click(closeReturn);
		waitStatusDialog();
		return new HomePage(driver);
	}

	public void assertCellOutlineColor(String title, String color) {
		String xpath = "//*[@title='" + title + "']/..";
		By by = By.xpath(xpath);
		assertEquals(RGBAtoHex(driver.getCSSAtribute(by, "outlineColor"))
				.toLowerCase(), color.toLowerCase());
	}

	public void assertCellBackgroundColor(String title, String color) {
		String xpath = "//*[@title='" + title + "']";
		By by = By.xpath(xpath);
		String rgbColor = driver.getCSSAtribute(by, "backgroundColor");
		String hexColor = RGBAtoHex(rgbColor);
		assertEquals(hexColor.toLowerCase(), color.toLowerCase());
	}

	public void assertPageBackgroundColor(String pagename, String color) {
		String xpath = "//tr[td/span='" + pagename + "']";
		By by = By.xpath(xpath);
		assertEquals(RGBAtoHex(driver.getCSSAtribute(by, "backgroundColor"))
				.toLowerCase(), color.toLowerCase());
	}

	public void assertPageCriticalCount(String pagename, String count) {
		String xpath = "//td[span='"
				+ pagename
				+ "']/../td/span[@class='header_right']/span[@class='headNumClass head_critical']";
		By by = By.xpath(xpath);
		driver.assertElementText(by, count);
	}

	public void assertPageXCriticalCount(String pagename, String count) {
		String xpath = "//td[span='"
				+ pagename
				+ "']/../td/span[@class='header_right']/span[@class='headNumClass head_x_critical']";
		By by = By.xpath(xpath);
		driver.assertElementText(by, count);
	}

	public void assertPageCriticalColor(String pagename, String color) {
		String xpath = "//td[span='"
				+ pagename
				+ "']/../td/span[@class='header_right']/span[@class='headNumClass head_critical']";
		By by = By.xpath(xpath);
		assertEquals(RGBAtoHex(driver.getCSSAtribute(by, "backgroundColor"))
				.toLowerCase(), color.toLowerCase());
	}

	public void assertPageXCriticalColor(String pagename, String color) {
		String xpath = "//td[span='"
				+ pagename
				+ "']/../td/span[@class='header_right']/span[@class='headNumClass head_x_critical']";
		By by = By.xpath(xpath);
		assertEquals(RGBAtoHex(driver.getCSSAtribute(by, "backgroundColor"))
				.toLowerCase(), color.toLowerCase());
	}

	public Boolean isCurrentPage() {
		return driver.isElementDisplayed(closeReturn);
	}

	public void importAdjustments(String filepath, Boolean importCancel) {
		filepath = filepath.replaceAll("/", "\\\\");
		filepath = (new File(filepath)).getAbsolutePath();
		logger.info("Try to import Adjustments, file path : " + filepath);
		driver.click(By.id("formHeader:adjust"));
		driver.click(By.xpath("//div[@id='formHeader:adjust_menu']/ul/li[1]/a"));
		driver.waitElementInVisiable(By.id("importFilesDialog"));
		driver.click(By
				.xpath("//div[@id='importFileForm:importFileUpload']/div[1]/label"));
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

		if (importCancel) {
			driver.click(By.id("importFileForm:importBtn"));
			waitStatusDialog();
		} else {
			driver.click(By.id("importFileForm:cancelBtn"));
		}
	}

	public String exportToExcel(String returnType, String returnVersion,
			String returnGroup, String processDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
		@SuppressWarnings("deprecation")
		String p = sdf.format(new Date(processDate));
		String filename = driver.downloaddir + "/" + returnType + "_v"
				+ returnVersion + "_" + returnGroup + "_" + p + ".xlsx";
		logger.info("try to delete file " + filename);
		Business.deleteFile(filename);

		logger.info("Try to export To Excel");
		driver.click(exportButton);
		driver.click(By
				.xpath("//div[@id='formHeader:exportToFile_menu']/ul/li[1]/a"));
		waitStatusDialog();
		if (BrowserDriver.INTERNETEXPLORER.equals(driver.browser)) {
			Business.downloadfileWithIEBrowser(driver);
		}

		return filename;
	}

	public String exportToCSV(String returnType, String returnVersion,
			String returnGroup, String processDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
		@SuppressWarnings("deprecation")
		String p = sdf.format(new Date(processDate));
		String filename = driver.downloaddir + "/" + returnType + "_v"
				+ returnVersion + "_" + returnGroup + "_" + p + ".csv";
		logger.info("try to delete file " + filename);
		Business.deleteFile(filename);

		logger.info("Try to export To CSV");
		driver.click(exportButton);
		driver.click(By
				.xpath("//div[@id='formHeader:exportToFile_menu']/ul/li[2]/a"));
		waitStatusDialog();
		if (BrowserDriver.INTERNETEXPLORER.equals(driver.browser)) {
			Business.downloadfileWithIEBrowser(driver);
		}

		return filename;
	}

	public String exportToXBRL(XBRL xbrl) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		@SuppressWarnings("deprecation")
		String date = sdf.format(new Date(xbrl.date));
		String filename = driver.downloaddir + "/" + xbrl.moduleCode + "_"
				+ Property.productType + "_" + xbrl.group + "_" + date
				+ ".xbrl.zip";
		logger.info("try to delete file " + filename);
		Business.deleteFile(filename);

		logger.info("Try to export To XBRL ");
		driver.click(exportButton);
		driver.click(By
				.xpath("//div[@id='formHeader:exportToFile_menu']/ul/li[3]/a"));
		driver.waitElementVisiable(By.id("transmitDialog"));
		driver.selectByVisibleText(By.id("transmitForm:selectModule"),
				xbrl.module);
		waitStatusDialog();

		driver.selectByVisibleText(By.id("transmitForm:selectModuleCode"),
				xbrl.moduleCode);
		waitStatusDialog();

		for (FormInstance form : xbrl.getList()) {
			String formName = form.formName;
			logger.info("forminstance xpath : "
					+ "//tbody[@id='transmitForm:formInstanceListTable_data']/tr/td[text()=\""
					+ formName + "\"]/preceding-sibling::td[1]/div");
			WebElement e = driver
					.getDriver()
					.findElement(
							By.xpath("//tbody[@id='transmitForm:formInstanceListTable_data']/tr/td[text()=\""
									+ formName
									+ "\"]/preceding-sibling::td[1]/div"));
			logger.info("forminstance xpath : "
					+ "//tbody[@id='transmitForm:formInstanceListTable_data']/tr/td[text()=\""
					+ formName + "\"]/preceding-sibling::td[1]/div");
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

			if (BrowserDriver.INTERNETEXPLORER.equals(driver.browser)) {
				Business.downloadfileWithIEBrowser(driver);
			}
			try {
				driver.click(By.id("transmitLogForm:cancelBtn"));
			} catch (NoSuchElementException e) {
			}
		} else {
			driver.click(By.id("transmitSubmitForm:cancelBtn"));
		}

		return filename;
	}

	/**
	 * View adjustment log from the menu
	 */
	public void viewAdjustmentLog() {
		logger.info("Try to view adjustment log");
		driver.click(adjustButton);
		driver.click(viewAdjustmentLog);
		waitStatusDialog();
	}

	/**
	 * View validation from the menu
	 */
	public void viewValidation() {
		viewAdjustmentLog();
		logger.info("Try to view validation");
		driver.click(validationLink);
	}

	/**
	 * View problems from the menu
	 */
	public void viewProblems() {
		viewAdjustmentLog();
		logger.info("Try to view problems");
		driver.click(problemsLink);
	}

	/**
	 * View such cell allocations
	 * 
	 * @param title
	 *            cell title
	 */
	public void viewAllocations(String title) {
		logger.info("Try to view allocations of title " + title);
		By by = getCellLocator(title);
		driver.doubleClick(by);
		By bye = getCellLocator(title, "input" + title);
		if (driver.isElementDisplayed(bye))
			driver.doubleClick(bye);
		waitStatusDialog();
	}

	/**
	 * Set adjustment filter on the footer form
	 * 
	 * @param filter
	 *            adjustment filter
	 */
	public void setAdjustmentFilter(AdjustmentFilter filter) {
		logger.info("Try to set adjustment filter");
		driver.click(adjustmentClearImage);
		driver.input(adjustmentFilterCell, filter.cell);
		if (filter.cell != null) {
			driver.typeKeyEvent(KeyEvent.VK_ENTER);
			waitStatusDialog();
		}
		driver.input(adjustmentFilterUser, filter.user);
		if (filter.user != null) {
			driver.typeKeyEvent(KeyEvent.VK_ENTER);
			waitStatusDialog();
		}
		// driver.input(adjustmentFilterFrom, filter.from);
		// driver.input(adjustmentFilterTo, filter.to);
	}

	/**
	 * Assert adjustment log exist
	 * 
	 * @param logs
	 *            list of adjustment log
	 */
	public void assertAdjustmentLog(List<AdjustmentLog> logs) {
		for (AdjustmentLog log : logs) {
			String xpath = "//*[@id='" + adjustmentLogTableDataID + "'";
			if (log.cell != null)
				xpath = xpath + " and tr/td='" + log.cell + "'";
			if (log.instance != null)
				xpath = xpath + " and tr/td='" + log.instance + "'";
			if (log.gridKey != null)
				xpath = xpath + " and tr/td='" + log.gridKey + "'";
			if (log.value != null)
				xpath = xpath + " and tr/td='" + log.value + "'";
			if (log.modifiedTo != null)
				xpath = xpath + " and tr/td='" + log.modifiedTo + "'";
			if (log.editTime != null)
				xpath = xpath + " and tr/td='" + i18n.toDate(log.editTime)
						+ "'";
			if (log.user != null)
				xpath = xpath + " and tr/td='" + log.user + "'";
			if (log.comment != null)
				xpath = xpath + " and tr/td='" + log.comment + "'";
			xpath = xpath + "]";
			driver.assertElementDisplayed(By.xpath(xpath), true);
		}
	}

	/**
	 * Assert adjustment log is empty
	 */
	public void assertAdjustmentLogEmpty() {
		String xpath = "//*[@id='" + adjustmentLogTableDataID
				+ "' and text()='No records found.']";
		driver.assertElementDisplayed(By.xpath(xpath), true);
	}

	/**
	 * Do live validation on return page
	 */
	public void doLiveValidation() {
		logger.info("Try to do live validation");
		driver.click(doValidationBtn);
		waitStatusDialog();
	}

	/**
	 * Undo live validation on return page
	 */
	public void undoLiveValidation() {
		logger.info("Try to undo live validation");
		driver.click(undoValidationBtn);
		waitStatusDialog();
	}

	/**
	 * Validate the return right now
	 */
	public void validateNow() {
		logger.info("Try to validate now");
		driver.click(validateButton);
		waitStatusDialog();
	}

	/**
	 * Create a new instance for this return
	 * 
	 * @param instance
	 *            instance id you'd like to create
	 */
	public void createInstance(String instance) {
		logger.info("Try to create isntance " + instance);
		driver.click(instanceCreateBtn);
		driver.input(instanceIdTextBox, instance);
		driver.click(instanceAcceptBtn);
		waitInfoPrompt();
	}

	/**
	 * Delete a existing instance from this return
	 * 
	 * @param instance
	 *            instance id you'd like to delete
	 */
	public void deleteInstance(String instance) {
		logger.info("Try to delete instance " + instance);
		driver.click(currentPageInstance);
		driver.click(By.xpath("//*[@id='" + instancePanelID
				+ "']/div/table/tbody/tr[@data-label='" + instance + "']"));
		waitStatusDialog();
		driver.click(instanceDeleteBtn);
		driver.click(instanceConfirmRemoveBtn);
		waitInfoPrompt();
	}

	/**
	 * Wait info prompt appearing and disappearing
	 */
	public void waitInfoPrompt() {
		logger.debug("Try to wait for info prompt appearing");
		driver.waitElementVisiable(infoPrompt);
		logger.debug("Try to wait for info prompt disappearing");
		driver.waitElementInVisiable(infoPrompt);
	}

	/**
	 * Set validation filter on the footer form
	 * 
	 * @param filter
	 *            validation filter
	 */
	public void setValidationFilter(ValidationFilter filter) {
		logger.info("Try to set validation filter");
		driver.click(validationClearImage);
		if (filter.page != null)
			driver.forceWait(3000);
		driver.selectByVisibleText(validationPageList, filter.page);
		if (filter.page != null)
			driver.forceWait(3000);
		driver.input(validationCellTextBox, filter.cell);
		if (filter.cell != null) {
			driver.typeKeyEvent(KeyEvent.VK_ENTER);
			waitStatusDialog();
			driver.forceWait(3000);
		}
		driver.selectByVisibleText(validationLevelList,
				i18n.toString(filter.level));
		if (filter.level != null)
			driver.forceWait(3000);
		driver.selectByVisibleText(validationResultlList,
				i18n.toString(filter.result));
		if (filter.result != null)
			driver.forceWait(3000);
		driver.selectByVisibleText(validationInstancingList,
				i18n.toString(filter.instancing));
		if (filter.instancing != null)
			driver.forceWait(3000);
	}

	/**
	 * Assert validation fail count on validation footer form
	 * 
	 * @param criticalCount
	 *            Critical count
	 * @param warningCount
	 *            Warning count
	 * @param xCriticalCount
	 *            X critical count
	 */
	public void assertValidaitonFailCount(String criticalCount,
			String warningCount, String xCriticalCount) {
		assertValidationCriticalCount(criticalCount);
		assertValidationWarningCount(warningCount);
		assertValidationXCriticalCount(xCriticalCount);
	}

	/**
	 * Assert validation critical count on validation footer form
	 * 
	 * @param count
	 *            Critical count
	 */
	public void assertValidationCriticalCount(String count) {

	}

	/**
	 * Assert validation warning count on validation footer form
	 * 
	 * @param count
	 *            Warning count
	 */
	public void assertValidationWarningCount(String count) {

	}

	/**
	 * Assert validation X critical count on validation footer form
	 * 
	 * @param count
	 *            X critical count
	 */
	public void assertValidationXCriticalCount(String count) {

	}

	/**
	 * Assert validation on the footer form
	 * 
	 * @param validations
	 *            validation list
	 */
	public void assertValidation(List<Validation> validations) {
		String xpath;
		for (Validation validation : validations) {
			xpath = "//*[@id='" + validationTableDataID + "']/tr[@role='row'";
			if (validation.no != null) {
				xpath = xpath
						+ " and td/span='"
						+ validation.no.replace("Validation ",
								i18n.toString("Validation ")) + "'";
			}
			if (validation.level != null)
				xpath = xpath + " and td='" + i18n.toString(validation.level)
						+ "'";
			if (validation.expression != null)
				xpath = xpath + " and td/span='" + validation.expression + "'";
			if (validation.intancing != null)
				xpath = xpath + " and td='"
						+ i18n.toString(validation.intancing) + "'";
			if (validation.status != null)
				xpath = xpath + " and td/span='"
						+ i18n.toString(validation.status) + "'";
			if (validation.ifMissingRef != null)
				xpath = xpath + " and td='" + validation.ifMissingRef + "'";
			if (validation.result != null)
				xpath = xpath + " and td/span='" + validation.result + "'";
			xpath = xpath + "]";
			logger.debug("Try to assert xpath exists " + xpath);
			driver.assertElementDisplayed(By.xpath(xpath), true);

			if (validation.validationResults != null) {
				driver.click(By.xpath(xpath + "/td/div"));
				waitStatusDialog();
				for (Validation.ValidationResult validationResult : validation.validationResults) {
					xpath = "//*[@id='" + validationTableDataID
							+ "']/tr/td/div/div/table/tbody/tr[@role='row'";
					if (validationResult.cell != null)
						xpath = xpath + " and td/a='" + validationResult.cell
								+ "'";
					if (validationResult.value != null)
						xpath = xpath + " and td='" + validationResult.value
								+ "'";
					if (validationResult.instanceId != null)
						xpath = xpath + " and td='"
								+ validationResult.instanceId + "'";
					if (validationResult.pageName != null)
						xpath = xpath + " and td='" + validationResult.pageName
								+ "'";
					if (validationResult.form != null)
						xpath = xpath + " and td='" + validationResult.form
								+ "'";
					if (validationResult.processDate != null)
						xpath = xpath + " and td='"
								+ validationResult.processDate + "'";
					xpath = xpath + "]";
					logger.debug("Try to assert xpath exists " + xpath);
					driver.assertElementDisplayed(By.xpath(xpath), true);
				}
			}
		}
	}

	/**
	 * Accept summing re-do action on the prompt dialog
	 */
	public void acceptSummingRedo() {
		logger.info("Try to accept summing redo");
		driver.click(sumConfirmBtn);
	}

	/**
	 * Cancel summing re-do action on the prompt dialog
	 */
	public void cancelSummingRedo() {
		logger.info("Try to cancel summing redo");
		driver.click(sumDeclineBtn);
	}

	/**
	 * Assert problems on the return footer form
	 * 
	 * @param problems
	 *            problem list
	 */
	public void assertProblems(List<Problem> problems) {
		for (Problem problem : problems) {
			String xpath = "//*[@id='" + problemsTableDataID
					+ "']/tr[@role='row'";
			if (problem.no != null)
				xpath = xpath
						+ " and td='"
						+ problem.no.replace("Cross Validation ",
								i18n.toString("Cross Validation ")) + "'";
			if (problem.destination != null)
				xpath = xpath + " and td='" + problem.destination + "'";
			if (problem.expression != null)
				xpath = xpath + " and td/span='" + problem.expression + "'";
			if (problem.level != null)
				xpath = xpath + " and td/span='" + problem.level + "'";
			if (problem.error != null)
				xpath = xpath + " and td/span='" + problem.error + "'";
			xpath = xpath + "]";
			logger.debug("Try to assert xpath exists " + xpath);
			driver.assertElementDisplayed(By.xpath(xpath), true);
		}
	}

	/**
	 * Assert summing allocation on return footer form
	 * 
	 * @param allocation
	 *            summing allocation
	 */
	public void assertAllocations(SummingAllocation allocation) {
		String xpath = "//*[@id='" + allocationSummingTableDataID
				+ "']/tr[@role='row'";
		if (allocation.cell != null)
			xpath = xpath + " and td='" + allocation.cell + "'";
		if (allocation.value != null)
			xpath = xpath + " and td/span='" + allocation.value + "'";
		if (allocation.instance != null)
			xpath = xpath + " and td/span='" + allocation.instance + "'";
		if (allocation.description != null)
			xpath = xpath + " and td/span='" + allocation.description + "'";
		if (allocation.expression != null)
			xpath = xpath + " and td/span='" + allocation.expression + "'";
		xpath = xpath + "]";
		logger.debug("Try to assert xpath exists " + xpath);
		driver.assertElementDisplayed(By.xpath(xpath), true);
		for (SummingAllocation child : allocation.children) {
			assertAllocations(child);
		}
	}

	/**
	 * Assert transaction allocation on return footer form
	 * 
	 * @param allocations
	 *            transaction allocation list
	 */
	public void assertAllocations(List<TransactionAllocation> allocations) {
		String fieldValue;
		for (TransactionAllocation allocation : allocations) {
			String xpath = "//*[@id='" + allocationTransactionTableDataID
					+ "']/tr[@role='row'";
			Field[] fields = allocation.getClass().getDeclaredFields();
			for (Field field : fields) {
				fieldValue = null;
				try {
					fieldValue = (String) field.get(allocation);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (fieldValue != null)
					if (Business.isDate(fieldValue))
						i18n.toDate(fieldValue);
				xpath = xpath + " and td/label='" + fieldValue + "'";
			}
			xpath = xpath + "]";
			logger.debug("Try to assert xpath exists " + xpath);
			driver.assertElementDisplayed(By.xpath(xpath), true);
		}
	}
}
