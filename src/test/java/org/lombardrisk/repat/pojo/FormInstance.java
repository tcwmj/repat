package org.lombardrisk.repat.pojo;

/**
 * @author Kenny Wang
 *
 */
public class FormInstance {
	public final String formName;
	public final String version[];


	/**
	 * @param formName
	 * @param version
	 */
	public FormInstance(String formName, String ...version) {
		this.formName = formName;
		this.version = version;
	}
}
