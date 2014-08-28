package org.lombardrisk.repat.pojo;

/**
 * @author Kenny Wang
 * 
 */
public class AdjustmentLog {
	public String cell;
	public String instance;
	public String gridKey;
	public String value;
	public String modifiedTo;
	public String editTime;
	public String user;
	public String comment;

	/**
	 * @param cell
	 * @param instance
	 * @param gridKey
	 * @param value
	 * @param modifiedTo
	 * @param editTime
	 * @param user
	 * @param comment
	 */
	public AdjustmentLog(String cell, String instance, String gridKey,
			String value, String modifiedTo, String editTime, String user,
			String comment) {
		this.cell = cell;
		this.instance = instance;
		this.gridKey = gridKey;
		this.value = value;
		this.modifiedTo = modifiedTo;
		this.editTime = editTime;
		this.user = user;
		this.comment = comment;
	}

	/**
	 * @param cell
	 * @param value
	 * @param modifiedTo
	 */
	public AdjustmentLog(String cell, String value, String modifiedTo) {
		this.cell = cell;
		this.value = value;
		this.modifiedTo = modifiedTo;
	}
}
