package org.eclipse.uml.a2acsl.behavior;

import org.eclipse.uml.a2acsl.values.Value;

/**
 * Describes a node that sets the value of an output of the activity
 * 
 * @author A560169
 * 
 */
public class ParameterValueNode extends Node {

	private Value value; // Value taken
	private String parameter; // Param name

	/**
	 * Instantiates a new node
	 * 
	 * @param guard
	 * @param value
	 * @param parameter
	 */
	public ParameterValueNode(String guard, Value value, String parameter) {
		super(guard);
		this.value = value;
		this.parameter = parameter;
	}

	/**
	 * Returns the new value taken by the parameter
	 * 
	 * @return
	 */
	public Value getValue() {
		return value;
	}

	/**
	 * Returns the parameter name
	 * 
	 * @return
	 */
	public String getParameter() {
		return parameter;
	}
}
