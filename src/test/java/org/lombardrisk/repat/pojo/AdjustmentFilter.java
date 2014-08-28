package org.lombardrisk.repat.pojo;

/**
 * @author Kenny Wang
 * 
 */
public class AdjustmentFilter {
	public String cell;
	public String user;
	public String from;
	public String to;

	/**
	 * @param cell
	 * @param user
	 * @param from
	 * @param to
	 */
	public AdjustmentFilter(String cell, String user, String from, String to) {
		this.cell = cell;
		this.user = user;
		this.from = from;
		this.to = to;
	}

}
