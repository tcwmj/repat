package org.lombardrisk.repat.tests;

import static org.lombardrisk.repat.utils.Business.getReturnForm;

import org.apache.log4j.Logger;
import org.databene.benerator.anno.Source;
import org.databene.feed4testng.FeedTest;
import org.lombardrisk.repat.pages.HomePage;
import org.lombardrisk.repat.pages.LoginPage;
import org.lombardrisk.repat.pages.ReturnPage;
import org.lombardrisk.repat.pojo.FormInstance;
import org.lombardrisk.repat.pojo.XBRL;
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
public class ECRProductsBasic extends FeedTest {

	private static Logger logger = Logger.getLogger(ECRProductsBasic.class);
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
	@Source("data/ECRProductsBasic.xls")
	@DataProvider(parallel = true)
	public void mainTest(String id, String regulator, String group,
			String returnType, String returnTitle1, String returnValue1,
			String returnCheck1, String returnTitle2, String returnValue2,
			String returnCheck2, String returnVersion, String returnDate1,
			String copyDataDate, String returnDate2, String returnDefault1,
			String returnDefault2, String extendReturn, String module,
			String moduleCode, String subVersions, String xbrlFile1,
			String xbrlFile2) {

		logger.info("======================================================");
		logger.info("Try to test ECR product " + returnType + " v"
				+ returnVersion);
		String form = getReturnForm(returnType, returnVersion);
		homepage = loginpage.loginAs(Property.defaultUser, Property.defaultPwd);
		homepage.selectRegulator(regulator);
		homepage.selectGroup(group);
		homepage.selectForm(form);
		homepage.selectAvaiableDate(copyDataDate);

		returnpage = homepage.openReturn(returnType, copyDataDate);
		returnpage.setCellValue(Boolean.parseBoolean(extendReturn),
				returnTitle1, returnValue1, true);
		returnpage.assertCellValue(returnTitle1, returnCheck1);
		returnpage.setCellValue(Boolean.parseBoolean(extendReturn),
				returnTitle2, returnValue2, true);
		returnpage.assertCellValue(returnTitle2, returnCheck2);

		homepage = returnpage.closeReturnPage();

		returnpage = homepage.createNew(group, form, returnDate1, true,
				copyDataDate, true, true);
		returnpage.assertCellValue(returnTitle1, returnCheck1);
		returnpage.assertCellValue(returnTitle2, returnCheck2);

		homepage = returnpage.closeReturnPage();

		returnpage = homepage.createNew(group, form, returnDate2, false, null,
				true, true);
		returnpage.assertCellValue(returnTitle1, returnDefault1);
		returnpage.assertCellValue(returnTitle1, returnDefault2);

		homepage = returnpage.closeReturnPage();

		XBRL xbrl = new XBRL(group, copyDataDate, module, moduleCode, true);
		String subVersion[] = subVersions.split(",");
		xbrl.formInstanceList.add(new FormInstance(returnType, subVersion));

		returnpage = homepage.openReturn(returnType, copyDataDate);
		String file1 = returnpage.exportToXBRL(xbrl);
		Business.assertFileExists(file1);
		Business.assertFileEquals(file1, xbrlFile1);

		homepage = returnpage.closeReturnPage();
		String file2 = homepage.exportToXBRL(xbrl);
		Business.assertFileExists(file2);
		Business.assertFileEquals(file2, xbrlFile2);
	}
}