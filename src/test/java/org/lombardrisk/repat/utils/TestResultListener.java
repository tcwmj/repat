package org.lombardrisk.repat.utils;

import static org.lombardrisk.repat.utils.BrowserDriver.saveScreenShot;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

/**
 * Test result Listener.
 */
public class TestResultListener extends TestListenerAdapter {

	private static Logger logger = Logger.getLogger(TestResultListener.class);

	@Override
	public void onTestFailure(ITestResult tr) {
		super.onTestFailure(tr);
		saveScreenShot(tr);
		logger.info(tr.getName() + " Failure");
		setCaseStatus(tr, Property.CaseRunStatus.Failed.text);
	}

	@Override
	public void onTestSkipped(ITestResult tr) {
		super.onTestSkipped(tr);
		saveScreenShot(tr);
		logger.info(tr.getName() + " Skipped");
		setCaseStatus(tr, Property.CaseRunStatus.NoRun.text);
	}

	@Override
	public void onTestSuccess(ITestResult tr) {
		super.onTestSuccess(tr);
		logger.info(tr.getName() + " Success");
		setCaseStatus(tr, Property.CaseRunStatus.Passed.text);

	}

	@Override
	public void onTestStart(ITestResult tr) {
		super.onTestStart(tr);
		logger.info(tr.getName() + " Start");
		setCaseStatus(tr, Property.CaseRunStatus.NoRun.text);
	}

	@Override
	public void onStart(ITestContext testContext) {
		super.onStart(testContext);
		String excelFileName = testContext.getCurrentXmlTest().getName()
				+ ".xls";
		;
		Business.prepareResultExcelFiles(excelFileName);
	}

	@Override
	public void onFinish(ITestContext testContext) {
		super.onFinish(testContext);

		// List of test results which we will delete later
		ArrayList<ITestResult> testsToBeRemoved = new ArrayList<ITestResult>();
		// collect all id's from passed test
		Set<Integer> passedTestIds = new HashSet<Integer>();
		for (ITestResult passedTest : testContext.getPassedTests()
				.getAllResults()) {
			logger.info("PassedTests = " + passedTest.getName());
			passedTestIds.add(getId(passedTest));
		}

		// Eliminate the repeat methods
		Set<Integer> skipTestIds = new HashSet<Integer>();
		for (ITestResult skipTest : testContext.getSkippedTests()
				.getAllResults()) {
			logger.info("skipTest = " + skipTest.getName());
			// id = class + method + dataprovider
			int skipTestId = getId(skipTest);

			if (skipTestIds.contains(skipTestId)
					|| passedTestIds.contains(skipTestId)) {
				testsToBeRemoved.add(skipTest);
			} else {
				skipTestIds.add(skipTestId);
			}
		}

		// Eliminate the repeat failed methods
		Set<Integer> failedTestIds = new HashSet<Integer>();
		for (ITestResult failedTest : testContext.getFailedTests()
				.getAllResults()) {
			logger.info("failedTest = " + failedTest.getName());
			// id = class + method + dataprovider
			int failedTestId = getId(failedTest);

			// if we saw this test as a failed test before we mark as to be
			// deleted
			// or delete this failed test if there is at least one passed
			// version
			if (failedTestIds.contains(failedTestId)
					|| passedTestIds.contains(failedTestId)
					|| skipTestIds.contains(failedTestId)) {
				testsToBeRemoved.add(failedTest);
			} else {
				failedTestIds.add(failedTestId);
			}
		}

		// finally delete all tests that are marked
		for (Iterator<ITestResult> iterator = testContext.getFailedTests()
				.getAllResults().iterator(); iterator.hasNext();) {
			ITestResult testResult = iterator.next();
			if (testsToBeRemoved.contains(testResult)) {
				logger.info("Remove repeat Fail Test: " + testResult.getName());
				iterator.remove();
			}
		}

	}

	private int getId(ITestResult result) {
		int id = result.getTestClass().getName().hashCode();
		id = id + result.getMethod().getMethodName().hashCode();
		id = id
				+ (result.getParameters() != null ? Arrays.hashCode(result
						.getParameters()) : 0);

		return id;
	}

	private void setCaseStatus(ITestResult result, String status) {
		Object[] parameters = result.getParameters();
		String caseId = null;
		if (parameters.length > 0) {
			caseId = String.valueOf(result.getParameters()[0]);
		} else {
			try {
				Field field = result.getInstance().getClass()
						.getDeclaredField("caseId");
				if (!field.isAccessible()) {
					field.setAccessible(true);
				}
				caseId = (String) field.get(result.getInstance());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if ((caseId == null) || ("".equals(caseId))) {
			throw new RuntimeException(
					" caseId can NOT be found OR caseId is \"\" !");
		}
		String excelFileName = result.getMethod().getXmlTest().getName()
				+ ".xls";
		Business.setCaseRunStatus(excelFileName, caseId, status);
	}

}
