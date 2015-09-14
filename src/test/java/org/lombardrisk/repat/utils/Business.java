package org.lombardrisk.repat.utils;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hamcrest.MatcherAssert;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.zeroturnaround.zip.ZipUtil;

import bad.robot.excel.matchers.Matchers;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

/**
 * @author Kenny Wang
 * 
 */
public class Business {

	public static String toDate(String date, String fromFormat, String toFormat) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(fromFormat);
		try {
			Date d = dateFormat.parse(date);
			return (new SimpleDateFormat(toFormat)).format(d);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	public static boolean isDate(String date, String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		try {
			dateFormat.parse(date);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean isDate(String date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				I18N.DATE_PATTERN_EN_US);
		try {
			dateFormat.parse(date);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static String getReturnForm(String returnType, String version) {
		return returnType + " v" + version;
	}

	/**
	 * download file with IE Browser
	 * 
	 * @param driver
	 */
	public static void downloadfileWithIEBrowser(BrowserDriver driver) {
		Capabilities cap = ((RemoteWebDriver) driver.getDriver())
				.getCapabilities();
		Integer version = Integer.valueOf((String) cap
				.getCapability(CapabilityType.VERSION));
		try {
			Thread.sleep(2000);
			Robot robot = new Robot();
			robot.keyPress(KeyEvent.VK_ALT);// press key alt
			robot.keyPress(KeyEvent.VK_S);// press key S
			Thread.sleep(2000);
			robot.keyRelease(KeyEvent.VK_S); // release key S
			robot.keyRelease(KeyEvent.VK_ALT);
			Thread.sleep(2000);

			if (version < 9) {

				robot.keyPress(KeyEvent.VK_ALT);// press key alt
				robot.keyPress(KeyEvent.VK_S);// press key S
				Thread.sleep(2000);

				robot.keyRelease(KeyEvent.VK_S); // release key S
				robot.keyRelease(KeyEvent.VK_ALT);
				Thread.sleep(2000);

				robot.keyPress(KeyEvent.VK_Y);
				robot.keyRelease(KeyEvent.VK_Y);
				Thread.sleep(4000);

				robot.keyPress(KeyEvent.VK_ENTER);
				robot.keyRelease(KeyEvent.VK_ENTER);
				Thread.sleep(2000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void assertFileExists(String filepath) {
		assertFileExists(filepath, true, Property.fileToBeAccessed);
	}

	/**
	 * @param filepath
	 * @param exist
	 * @param timeout
	 */
	public static void assertFileExists(String filepath, Boolean exist,
			int timeout) {
		File file = new File(filepath);
		long curtime = System.currentTimeMillis();
		while (System.currentTimeMillis() - curtime <= timeout * 1000) {
			if (file.exists())
				return;
		}
		Assert.fail("fail to get file " + filepath + " in " + timeout
				+ " seconds");
	}

	public static void assertFileEquals(String sourceFile, String newFile) {
		String ext = sourceFile.substring(sourceFile.lastIndexOf(".") + 1);
		switch (ext.toLowerCase()) {
		case "zip":
			assertZipFileEquals(sourceFile, newFile);
			break;
		case "xls":
			compareExcel(sourceFile, newFile);
			break;
		case "xlsx":
			compareExcel(sourceFile, newFile);
			break;
		default:
			assertTextFileEquals(sourceFile, newFile);
		}
	}

	private static void assertZipFileEquals(String sourceFile, String newFile) {
		Assert.assertTrue(ZipUtil.archiveEquals(new File(sourceFile), new File(
				newFile)));
	}

	private static void assertTextFileEquals(String sourceFile, String newFile) {
		List<String> original = readTxtFile(sourceFile);
		List<String> revised = readTxtFile(newFile);

		Patch patch = DiffUtils.diff(original, revised);
		if (patch.getDeltas().size() > 0) {
			StringBuffer strB = new StringBuffer();
			for (Delta delta : patch.getDeltas()) {
				strB.append(delta);
			}
			Assert.fail(" AssertTextFileEquals fail : " + strB.toString());
		}
	}

	public static void deleteFile(String filepath) {
		File file = new File(filepath);
		file.delete();
	}

	private static void compareExcel(String sourceFile, String newFile) {
		InputStream ins1 = null;
		InputStream ins2 = null;
		Workbook workbook1 = null;
		Workbook workbook2 = null;
		try {
			ins1 = new FileInputStream(sourceFile);
			ins2 = new FileInputStream(newFile);
			if (sourceFile.toLowerCase().endsWith(".xlsx")
					&& newFile.toLowerCase().endsWith(".xlsx")) {
				workbook1 = new XSSFWorkbook(ins1);
				workbook2 = new XSSFWorkbook(ins2);
			} else if (sourceFile.toLowerCase().endsWith(".xls")
					&& newFile.toLowerCase().endsWith(".xls")) {
				workbook1 = new HSSFWorkbook(ins1);
				workbook2 = new HSSFWorkbook(ins2);
			} else {
				throw new RuntimeException(
						"compareExcel Fail : Please check file name: [sourceFile : "
								+ sourceFile + "], [newFile : " + newFile + "]");
			}

			MatcherAssert.assertThat(workbook1,
					Matchers.sameWorkbook(workbook2));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (ins1 != null) {
				try {
					ins1.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (ins2 != null) {
				try {
					ins2.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static List<String> readTxtFile(String filename) {
		List<String> lines = new LinkedList<String>();
		BufferedReader in = null;
		String line = "";
		try {
			in = new BufferedReader(new FileReader(filename));
			while ((line = in.readLine()) != null) {
				lines.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return lines;
	}

	public static void prepareResultExcelFiles(String excelFileName) {
		String excelPath = new File("").getAbsolutePath() + "\\target\\"
				+ excelFileName;

		Workbook workbook = openExcel(excelPath);

		Sheet sheet = workbook.createSheet();
		Row row = sheet.createRow(0);

		row.createCell(0).setCellValue("TestCaseID");
		row.createCell(1).setCellValue("Status");

		saveAndCloseExcel(workbook, excelPath);
	}

	private static Workbook openExcel(String excelPath) {
		FileInputStream ins = null;
		Workbook workbook = null;
		try {
			File file = new File(excelPath);
			if (!file.exists()) {
				workbook = new HSSFWorkbook();
			} else {
				ins = new FileInputStream(excelPath);
				workbook = new HSSFWorkbook(ins);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (ins != null) {
				try {
					ins.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return workbook;
	}

	private static void saveAndCloseExcel(Workbook workbook, String excelPath) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(excelPath);
			workbook.write(fos);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fos != null)
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}

	}

	public static void setCaseRunStatus(String excelFileName, String caseId,
			String status) {
		String excelPath = new File("").getAbsolutePath() + "\\target\\"
				+ excelFileName;
		Workbook workbook = openExcel(excelPath);
		Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> it = sheet.iterator();
		Integer rowNum = null;
		while (it.hasNext()) {
			Row row = it.next();
			String strID = row.getCell(0).getStringCellValue();
			if (caseId.equals(strID)) {
				row.getCell(1).setCellValue(status);
				rowNum = row.getRowNum();
				break;
			}
		}
		if (rowNum == null) {
			rowNum = sheet.getPhysicalNumberOfRows();
		}

		Row row = sheet.createRow(rowNum);
		row.createCell(0).setCellValue(caseId);
		row.createCell(1).setCellValue(status);

		saveAndCloseExcel(workbook, excelPath);
	}
}
