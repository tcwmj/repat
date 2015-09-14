package org.lombardrisk.repat.utils;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class Retry implements IRetryAnalyzer {
	public static int retryCount = 0;
	private int maxRetryCount = Property.reRunTimes;

	@Override
	public synchronized boolean retry(ITestResult result) {
		if (retryCount < maxRetryCount) {
			retryCount++;
			return true;
		}
		return false;
	}

}
