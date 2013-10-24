package org.eclipse.uml.a2acsl.behavior;

import org.eclipse.uml.a2acsl.values.Value;
import org.eclipse.uml2.uml.Type;

/**
 * Describes a node that sets the value of a local variable
 * 
 * @author A560169
 * 
 */
public class WriteVariableNode extends Node {

	private String variable; // Variable name
	private Type type; // Variable type
	private Value value; // Value taken

	/**
	 * Instantiates a new node
	 * 
	 * @param guard
	 * @param variable
	 * @param type
	 * @param value
	 */
	public WriteVariableNode(String guard, String variable, Type type,
			Value value) {
		super(guard);
		this.variable = variable;
		this.type = type;
		this.value = value;
	}

	/**
	 * Returns the variable name
	 * 
	 * @return
	 */
	public String getVariable() {
		return variable;
	}

	/**
	 * Returns the variable type
	 * 
	 * @return
	 */
	public Type getType() {
		return type;
	}

	/**
	 * Returns the variable new value
	 * 
	 * @return
	 */
	public Value getValue() {
		return value;
	}
}
