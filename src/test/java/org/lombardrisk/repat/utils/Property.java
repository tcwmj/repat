package org.lombardrisk.repat.utils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.testng.log4testng.Logger;

/**
 * @author Kenny Wang
 * 
 */
public class Property {

	private static Logger logger = Logger.getLogger(Property.class);
	private static String PROPERTY_FILE = "config/test.properties";
	private static Properties props = null;

	public static int timeoutInterval = Integer
			.parseInt(getProperty("timeoutInterval"));
	public static int pollingInterval = Integer
			.parseInt(getProperty("pollingInterval"));
	public static int statusDialogAppearIn = Integer
			.parseInt(getProperty("statusDialogAppearIn"));
	public static int statusDialogDisappearIn = Integer
			.parseInt(getProperty("statusDialogDisappearIn"));
	public static int fileToBeAccessed = Integer
			.parseInt(getProperty("fileToBeAccessed"));

	public static Boolean remote = Boolean.parseBoolean(getProperty("remote"));
	public static String defaultBroswer = getProperty("defaultBroswer");
	public static String firefoxDir = getProperty("firefoxDir");
	public static String downloadWINXPDir = getProperty("downloadWINXPDir");
	public static String downloadWINDir = getProperty("downloadWINDir");
	public static String downloadOSXDir = getProperty("downloadOSXDir");

	public static String baseUrl = getProperty("baseUrl");
	public static String defaultUser = getProperty("defaultUser");
	public static String defaultPwd = getProperty("defaultPwd");
	public static String productType = getProperty("productType");
	public static String lang = getProperty("lang");

	public static String browserstackurl = getProperty("browserstackurl");
	public static String project = getProperty("project");
	public static String build = getProperty("build");
	public static String browserstacklocal = getProperty("browserstacklocal");
	public static String browserstacklocalIdentifier = getProperty("browserstacklocalIdentifier");
	public static String browserstackdebug = getProperty("browserstackdebug");
	public static Integer reRunTimes = Integer
			.valueOf(getProperty("reRunTimes"));

	// public static String testResultExcelPath = new File("").getAbsolutePath()
	// + "\\target\\TestResult.xls";

	public static void getProperties() {
		logger.info("try to get test properties from external file "
				+ PROPERTY_FILE);
		props = new Properties();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(
					PROPERTY_FILE));
			props.load(in);
			in.close();
		} catch (Exception e) {
			logger.info(e.getMessage());
			e.printStackTrace();
		}
	}

	public static String getProperty(String key) {
		if (props == null) {
			getProperties();
		}
		return props.getProperty(key);
	}

	public enum CaseRunStatus {
		Passed("Passed"), Failed("Failed"), NoRun("No Run");
		public final String text;

		private CaseRunStatus(String text) {
			this.text = text;
		}
	}
}
