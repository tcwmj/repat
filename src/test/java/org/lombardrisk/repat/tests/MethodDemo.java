package org.lombardrisk.repat.tests;

import static org.lombardrisk.repat.utils.Business.getReturnForm;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.databene.benerator.anno.Source;
import org.databene.feed4testng.FeedTest;
import org.lombardrisk.repat.pages.HomePage;
import org.lombardrisk.repat.pages.LoginPage;
import org.lombardrisk.repat.pages.ReturnPage;
import org.lombardrisk.repat.pojo.Problem;
import org.lombardrisk.repat.pojo.SummingAllocation;
import org.lombardrisk.repat.pojo.TransactionAllocation;
import org.lombardrisk.repat.pojo.Validation;
import org.lombardrisk.repat.pojo.ValidationFilter;
import org.lombardrisk.repat.utils.BrowserDriver;
import org.lombardrisk.repat.utils.Property;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class MethodDemo extends FeedTest {

	private static Logger logger = Logger.getLogger(MethodDemo.class);
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
	@Source("data/LiveValidationTest.xls")
	@DataProvider(parallel = true)
	public void LiveValidationTest(String id, String regulator, String group,
			String returnType, String returnVersion, String copyDataDate,
			String extendReturn) {

		logger.info("LiveValidationTest");
		String form = getReturnForm(returnType, returnVersion);
		homepage = loginpage.loginAs(Property.defaultUser, Property.defaultPwd);
		homepage.selectRegulator(regulator);
		homepage.selectGroup(group);
		homepage.selectForm(form);
		homepage.selectAvaiableDate(copyDataDate);

		returnpage = homepage.openReturn(returnType, copyDataDate);
		returnpage.cancelSummingRedo();
		returnpage.undoLiveValidation();
		returnpage.doLiveValidation();
	}

	@Test(dataProvider = "feeder")
	@Source("data/InstanceTest.xls")
	@DataProvider(parallel = true)
	public void InstanceTest(String id, String regulator, String group,
			String returnType, String returnVersion, String copyDataDate,
			String extendReturn, String instanceId) {

		logger.info("InstanceTest");
		String form = getReturnForm(returnType, returnVersion);
		homepage = loginpage.loginAs(Property.defaultUser, Property.defaultPwd);
		homepage.selectRegulator(regulator);
		homepage.selectGroup(group);
		homepage.selectForm(form);
		homepage.selectAvaiableDate(copyDataDate);

		returnpage = homepage.openReturn(returnType, copyDataDate);
		returnpage.createInstance(instanceId);
		returnpage.deleteInstance(instanceId);
	}

	@Test(dataProvider = "feeder")
	@Source("data/ValidationCheckTest.xls")
	@DataProvider(parallel = true)
	public void ValidationCheckTest(String id, String regulator, String group,
			String returnType, String returnVersion, String copyDataDate,
			String extendReturn, String page, String cell, String level,
			String result, String instancing, String vNo, String vLevel,
			String vExpression, String vIntancing, String vStatus,
			String vIfMissingRef, String vResult, String vrCell,
			String vrValue, String vrInstanceId, String vrPageName,
			String vrForm, String vrProcessDate) {

		logger.info("ValidationCheckTest");
		String form = getReturnForm(returnType, returnVersion);
		homepage = loginpage.loginAs(Property.defaultUser, Property.defaultPwd);
		homepage.selectRegulator(regulator);
		homepage.selectGroup(group);
		homepage.selectForm(form);
		homepage.selectAvaiableDate(copyDataDate);

		returnpage = homepage.openReturn(returnType, copyDataDate);
		returnpage.cancelSummingRedo();
		returnpage.viewValidation();
		ValidationFilter vf = new ValidationFilter(page, cell, level, result,
				instancing);
		returnpage.setValidationFilter(vf);

		Validation va = new Validation(vNo, vLevel, vExpression, vIntancing,
				vStatus, vIfMissingRef, vResult);
		va.addValidationResult(vrCell, vrValue, vrInstanceId, vrPageName,
				vrForm, vrProcessDate);
		ArrayList<Validation> val = new ArrayList<Validation>();
		val.add(va);
		returnpage.assertValidation(val);
	}

	@Test(dataProvider = "feeder")
	@Source("data/ProblemsCheckTest.xls")
	@DataProvider(parallel = true)
	public void ProblemsCheckTest(String id, String regulator, String group,
			String returnType, String returnVersion, String copyDataDate,
			String extendReturn, String no, String destination,
			String expression, String level, String error) {

		logger.info("ProblemsCheckTest");
		String form = getReturnForm(returnType, returnVersion);
		homepage = loginpage.loginAs(Property.defaultUser, Property.defaultPwd);
		homepage.selectRegulator(regulator);
		homepage.selectGroup(group);
		homepage.selectForm(form);
		homepage.selectAvaiableDate(copyDataDate);

		returnpage = homepage.openReturn(returnType, copyDataDate);
		returnpage.cancelSummingRedo();
		returnpage.viewProblems();
		ArrayList<Problem> problems = new ArrayList<Problem>();
		problems.add(new Problem(no, destination, expression, level, error));
		returnpage.assertProblems(problems);
	}

	@Test(dataProvider = "feeder")
	@Source("data/AllocationsCheckTest.xls")
	@DataProvider(parallel = true)
	public void AllocationsCheckTest(String id, String regulator, String group,
			String returnType, String returnVersion, String copyDataDate,
			String extendReturn, String title1, String title2, String cell,
			String value, String instance, String description,
			String expression, String cell1, String value1, String instance1,
			String description1, String expression1, String cell2,
			String value2, String instance2, String description2,
			String expression2, String stbimpindex, String stbimpdate,
			String stbmapid, String stbstatus, String stbdrillalias,
			String stbdrillref, String stbdrilltable, String sbranch,
			String sflag, String sformalphavalue, String sformvalue,
			String sglc1, String sglc2, String sglc3, String sglc4,
			String sglc5, String sgoodwillind, String sgrosslongposition,
			String stbfield, String stradingbookind, String snetlongposition,
			String sweightedamount, String sworkingamount, String snewrecord,
			String snumericref1, String snumericref3, String sownfundsamount,
			String sownfundsind, String sissuer, String scardeductref,
			String sdeductedamount, String sapplicablepercentage,
			String squalifyownfundperc, String sownfundsubsidarytype,
			String stransitionalprovind, String sdateref1, String snumericref2,
			String sfcarryingamou, String sprovisionamount) {

		logger.info("AllocationsCheckTest");
		String form = getReturnForm(returnType, returnVersion);
		homepage = loginpage.loginAs(Property.defaultUser, Property.defaultPwd);
		homepage.selectRegulator(regulator);
		homepage.selectGroup(group);
		homepage.selectForm(form);
		homepage.selectAvaiableDate(copyDataDate);

		returnpage = homepage.openReturn(returnType, copyDataDate);
		returnpage.cancelSummingRedo();

		// check summing allocation
		returnpage.viewAllocations(title1);
		SummingAllocation sa = new SummingAllocation(cell, value, instance,
				description, expression);
		sa.children.add(new SummingAllocation(cell1, value1, instance1,
				description1, expression1));
		sa.children.get(0).children.add(new SummingAllocation(cell2, value2,
				instance2, description2, expression2));
		returnpage.assertAllocations(sa);

		// check transaction allocation
		returnpage.viewAllocations(title2);
		TransactionAllocation ta = new TransactionAllocation(stbimpindex,
				stbimpdate, stbmapid, stbstatus, stbdrillalias, stbdrillref,
				stbdrilltable, sbranch, sflag, sformalphavalue, sformvalue,
				sglc1, sglc2, sglc3, sglc4, sglc5, sgoodwillind,
				sgrosslongposition, stbfield, stradingbookind,
				snetlongposition, sweightedamount, sworkingamount, snewrecord,
				snumericref1, snumericref3, sownfundsamount, sownfundsind,
				sissuer, scardeductref, sdeductedamount, sapplicablepercentage,
				squalifyownfundperc, sownfundsubsidarytype,
				stransitionalprovind, sdateref1, snumericref2, sfcarryingamou,
				sprovisionamount);
		ArrayList<TransactionAllocation> tas = new ArrayList<TransactionAllocation>();
		tas.add(ta);
		returnpage.assertAllocations(tas);
	}
}