package org.lombardrisk.repat.pojo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kenny Wang
 * 
 */
public class SummingAllocation {
	public String cell;
	public String value;
	public String instance;
	public String description;
	public String expression;
	public List<SummingAllocation> children = new ArrayList<SummingAllocation>();

	/**
	 * @param cell
	 * @param value
	 * @param instance
	 * @param description
	 * @param expression
	 */
	public SummingAllocation(String cell, String value, String instance,
			String description, String expression) {
		this.cell = cell;
		this.value = value;
		this.instance = instance;
		this.description = description;
		this.instance = expression;
	}
}
