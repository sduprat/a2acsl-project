package org.eclipse.uml.a2acsl.values;

/**
 * Represents a string value containing an expression expressed in OCL Examples:
 * 10, x(parameter), v(local variable), obj.id(access)
 * 
 * @author A560169
 */
public class StringValue implements Value {

	private String value;

	/**
	 * Create a new StringValue from a string representation
	 * 
	 * @param value
	 */
	public StringValue(String value) {
		this.value = value;
	}

	/**
	 * Returns the string value
	 * 
	 * @return
	 */
	public String getValue() {
		return value;
	}
}
