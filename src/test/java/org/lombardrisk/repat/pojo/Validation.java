package org.lombardrisk.repat.pojo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kenny Wang
 * 
 */
public class Validation {
	public String no;
	public String level;
	public String expression;
	public String intancing;
	public String status;
	public String ifMissingRef;
	public String result;
	public List<ValidationResult> validationResults = new ArrayList<ValidationResult>();

	public class ValidationResult {
		public String cell;
		public String value;
		public String instanceId;
		public String pageName;
		public String form;
		public String processDate;

		public ValidationResult(String cell, String value, String instanceId,
				String pageName, String form, String processDate) {
			super();
			this.cell = cell;
			this.value = value;
			this.instanceId = instanceId;
			this.pageName = pageName;
			this.form = form;
			this.processDate = processDate;
		}
	}

	public Validation(String no, String level, String expression,
			String intancing, String status, String ifMissingRef, String result) {
		super();
		this.no = no;
		this.level = level;
		this.expression = expression;
		this.intancing = intancing;
		this.status = status;
		this.ifMissingRef = ifMissingRef;
		this.result = result;
	}

	public void addValidationResult(String cell, String value,
			String instanceId, String pageName, String form, String processDate) {
		validationResults.add(new ValidationResult(cell, value, instanceId,
				pageName, form, processDate));
	}
}
