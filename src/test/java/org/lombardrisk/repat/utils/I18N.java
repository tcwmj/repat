package org.lombardrisk.repat.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

/**
 * @author Kenny Wang
 * 
 */
public class I18N {
	private final String langPath = "config/lang";

	private final String lang;

	private HashMap<String, String> map;

	public static final String DATE_PATTERN_EN_US = "MM/dd/yyyy";
	private static final String DATE_PATTERN_EN = "dd/MM/yyyy";
	private static final String DATE_PATTERN_ZH_CN = "yyyy-MM-dd";

	/**
	 * @param lang
	 */
	public I18N(String lang) {
		this.lang = lang;
		this.map = loadLang(lang);
	}

	/**
	 * @param lang
	 * @return string
	 */
	private HashMap<String, String> loadLang(String lang) {
		Properties props = new Properties();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(
					langPath + File.separator + lang + ".properties"));
			props.load(in);
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		HashMap<String, String> ret = new HashMap<String, String>();
		try {
			Enumeration<?> en = props.propertyNames();
			while (en.hasMoreElements()) {
				String key = (String) en.nextElement();
				String value = props.getProperty(key);
				value = new String(value.getBytes("ISO-8859-1"), "UTF-8");
				ret.put(key, value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * @param date
	 * @return date
	 */
	public String toDate(String date) {
		switch (lang) {
		case "zh_CN":
			return Business
					.toDate(date, DATE_PATTERN_EN_US, DATE_PATTERN_ZH_CN);
		case "en":
			return Business.toDate(date, DATE_PATTERN_EN_US, DATE_PATTERN_EN);
		}
		return date;
	}

	/**
	 * @param string
	 * @return string
	 */
	public String toString(String string) {
		if (map.containsKey(string))
			return map.get(string);
		else
			return string;
	}

	public static void main(String args[]) {
		I18N i18n = new I18N("zh_CN");
		System.out.println(i18n.toString("Logout"));
	}
}
