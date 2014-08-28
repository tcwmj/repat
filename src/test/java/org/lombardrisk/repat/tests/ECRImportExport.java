package org.lombardrisk.repat.tests;

import static org.lombardrisk.repat.utils.Business.getReturnForm;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.databene.benerator.anno.Source;
import org.databene.feed4testng.FeedTest;
import org.lombardrisk.repat.pages.HomePage;
import org.lombardrisk.repat.pages.LoginPage;
import org.lombardrisk.repat.pages.ReturnPage;
import org.lombardrisk.repat.pojo.AdjustmentLog;
import org.lombardrisk.repat.utils.BrowserDriver;
import org.lombardrisk.repat.utils.Business;
import org.lombardrisk.repat.utils.Property;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * @author Kenny Wang
 * 
 */
public class ECRImportExport extends FeedTest {

	private static Logger logger = Logger.getLogger(ECRImportExport.class);
	private BrowserDriver driver;

	private LoginPage loginpage;
	private HomePage homepage;
	private ReturnPage returnpage;

	@BeforeMethod
	@Parameters({ "os", "os_version", "browser", "browser_version",
			"resolution" })
	public void setUp(@Optional String os, @Optional String os_version,
			@Optional String browser, @Optional String browser_version,
			@Optional String resolution) {
		driver = new BrowserDriver(os, os_version, browser, browser_version,
				resolution);
		driver.get(Property.baseUrl);
		loginpage = new LoginPage(driver);
	}

	@AfterMethod
	public void tearDown() {
		try {
			if (returnpage != null && returnpage.isCurrentPage())
				homepage = returnpage.closeReturnPage();
			if (homepage != null && homepage.isCurrentPage())
				loginpage = homepage.logout();
		} catch (Exception e) {
			logger.error("error occurred on logout");
			e.printStackTrace();
		} finally {
			if (driver != null)
				driver.quit();
		}
	}

	@Test(dataProvider = "feeder")
	@Source("data/ECRImportExport.xls")
	@DataProvider(parallel = true)
	public void mainTest(String id, String regulator, String group,
			String returnType, String returnVersion, String processDate,
			String excel, String csv, String excelImportPath1,
			String csvImportPath, String excelImportPath2,
			String createFromExcelPath, String comments, String title,
			String value) throws NumberFormatException, Exception {

		logger.info("======================================================");
		logger.info("Try to test ECR product " + returnType + " v"
				+ returnVersion);
		String form = getReturnForm(returnType, returnVersion);
		homepage = loginpage.loginAs(Property.defaultUser, Property.defaultPwd);
		homepage.selectRegulator(regulator);
		homepage.selectGroup(group);
		homepage.selectForm(form);
		homepage.selectAvaiableDate(processDate);

		returnpage = homepage.openReturn(returnType, processDate);
		String excelFile = returnpage.exportToExcel(returnType, returnVersion,
				group, processDate);
		Business.assertFileExists(excelFile);
		Business.assertFileEquals(excelFile, excel);

		String csvFile = returnpage.exportToCSV(returnType, returnVersion,
				group, processDate);
		Business.assertFileExists(csvFile);
		Business.assertFileEquals(csvFile, csv);

		List<AdjustmentLog> logs = new ArrayList<AdjustmentLog>();
		logs.add(new AdjustmentLog(title, null, null, null, value, null, null,
				null));

		DecimalFormat df = new DecimalFormat("#,###");
		String value1 = df.format(df.parse(returnpage.getCellValue(title))
				.intValue() + df.parse(value).intValue())
				+ " ";
		returnpage.importAdjustments(excelImportPath1, true);
		returnpage.assertCellValue(title, value1);
		returnpage.viewAdjustmentLog();
		returnpage.assertAdjustmentLog(logs);

		String value2 = df.format(df.parse(value1).intValue()
				+ df.parse(value).intValue())
				+ " ";
		returnpage.importAdjustments(csvImportPath, true);
		returnpage.assertCellValue(title, value2);
		returnpage.viewAdjustmentLog();
		returnpage.assertAdjustmentLog(logs);

		String value3 = df.format(df.parse(value2).intValue()
				+ df.parse(value).intValue())
				+ " ";
		homepage = returnpage.closeReturnPage();
		returnpage = (ReturnPage) homepage.importAdjustments(excelImportPath2,
				true, true);
		returnpage.assertCellValue(title, value3);
		returnpage.viewAdjustmentLog();
		returnpage.assertAdjustmentLog(logs);
		homepage = returnpage.closeReturnPage();

		returnpage = (ReturnPage) homepage.createFromExcel(createFromExcelPath,
				comments, true, true);
		returnpage.assertCellValue(title, value);
		returnpage.assertAdjustmentLogEmpty();

	}
}