package org.lombardrisk.repat.pojo;

import java.util.ArrayList;
import java.util.List;

public class XBRL {
	public final String group;
	public final String date;
	public final String module;
	public final String moduleCode;
	public final List<FormInstance> formInstanceList;
	public final Boolean export;

	/**
	 * Construct Method
	 * 
	 * @param group
	 * @param date
	 * @param module
	 * @param moduleCode
	 * @param export
	 */
	public XBRL(String group, String date, String module, String moduleCode,
			Boolean export) {
		this.group = group;
		this.date = date;
		this.module = module;
		this.moduleCode = moduleCode;
		this.export = true;
		formInstanceList = new ArrayList<FormInstance>();
	}

	public List<FormInstance> getList() {
		return formInstanceList;
	}

}
