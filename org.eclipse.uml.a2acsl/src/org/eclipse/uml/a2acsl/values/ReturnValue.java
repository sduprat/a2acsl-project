package org.eclipse.uml.a2acsl.values;

/**
 * Represents a value returned by an operation call. It can be a return value or
 * an OUT parameter
 * 
 * @author A560169
 * 
 */
public class ReturnValue implements Value {

	private ReturnType type;
	private int callHashCode;
	private String returnName;

	/**
	 * Creates a new ReturnValue
	 * 
	 * @param callHashCode
	 * @param returnName
	 * @param type
	 */
	public ReturnValue(int callHashCode, String returnName, ReturnType type) {
		super();
		this.callHashCode = callHashCode;
		this.type = type;
		this.returnName = returnName;
	}

	/**
	 * Returns the hashcode of the operation call returning this value
	 * 
	 * @return
	 */
	public int getCallHashCode() {
		return callHashCode;
	}

	/**
	 * Sets the name of the OUT parameter
	 * 
	 * @param returnName
	 */
	public void setReturnName(String returnName) {
		this.returnName = returnName;
	}

	/**
	 * Returns the type of this value
	 * 
	 * @return
	 */
	public ReturnType getType() {
		return type;
	}

	/**
	 * Returns the name of the OUT parameter
	 * 
	 * @return
	 */
	public String getReturnName() {
		return returnName;
	}

	/**
	 * Describes types of ReturnValues : From an operation result or an OUT
	 * parameter
	 * 
	 * @author A560169
	 * 
	 */
	public enum ReturnType {
		RETURN, OUT
	};
}
