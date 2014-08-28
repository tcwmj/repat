package org.lombardrisk.repat.pojo;

/**
 * @author Kenny Wang
 * 
 */
public class Problem {
	public String no;
	public String destination;
	public String expression;
	public String level;
	public String error;

	/**
	 * @param no
	 * @param destination
	 * @param expression
	 * @param level
	 * @param error
	 */
	public Problem(String no, String destination, String expression,
			String level, String error) {
		this.no = no;
		this.destination = destination;
		this.expression = expression;
		this.level = level;
		this.error = error;
	}
}
