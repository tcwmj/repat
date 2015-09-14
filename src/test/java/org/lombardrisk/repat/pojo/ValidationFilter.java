package org.lombardrisk.repat.pojo;

/**
 * @author Kenny Wang
 * 
 */
public class ValidationFilter {
	public String page;
	public String cell;
	public String level;
	public String result;
	public String instancing;

	public ValidationFilter(String page, String cell, String level,
			String result, String instancing) {
		this.page = page;
		this.cell = cell;
		this.level = level;
		this.result = result;
		this.instancing = instancing;
	}
}
