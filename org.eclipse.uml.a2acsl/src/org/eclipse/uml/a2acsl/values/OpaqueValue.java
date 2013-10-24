package org.eclipse.uml.a2acsl.values;

import java.util.HashMap;

/**
 * Describes the value taken by the output of an Opaque Action
 * 
 * @author A560169
 * 
 */
public class OpaqueValue implements Value {

	private HashMap<String, Value> inputs;
	private String output;
	private HashMap<String, String> types;
	private String constraint;

	/**
	 * Creates a new OpaqueValue
	 * 
	 * @param inputs
	 * @param output
	 * @param types
	 * @param constraint
	 */
	public OpaqueValue(HashMap<String, Value> inputs, String output,
			HashMap<String, String> types, String constraint) {
		this.inputs = inputs;
		this.output = output;
		this.types = types;
		this.constraint = constraint;
	}

	/**
	 * Returns the corresponding action inputs
	 * 
	 * @return
	 */
	public HashMap<String, Value> getInputs() {
		return inputs;
	}

	/**
	 * Returns the output of the action
	 * 
	 * @return
	 */
	public String getOutput() {
		return output;
	}

	/**
	 * Returns the types of inputs/output
	 * 
	 * @return
	 */
	public HashMap<String, String> getTypes() {
		return types;
	}

	/**
	 * Returns the constraint expressing the output in terms of the inputs
	 * 
	 * @return
	 */
	public String getConstraint() {
		return constraint;
	}

}
