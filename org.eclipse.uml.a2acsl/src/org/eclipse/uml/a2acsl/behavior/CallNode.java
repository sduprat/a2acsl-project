package org.eclipse.uml.a2acsl.behavior;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.uml.a2acsl.values.Value;
import org.eclipse.uml2.uml.Property;

/**
 * Describes an operation call
 * 
 * @author A560169
 * 
 */
public class CallNode extends Node {

	private int callCounter; // Call number
	private String name; // Operation name
	private String owner; // Operation class
	private HashMap<String, Value> inputs; // Inputs with their values
	private int hashcode; // Action hashcode
	private ArrayList<Property> sideEffects; // Operation side effects

	/**
	 * Instantiates a new node
	 * 
	 * @param guard
	 * @param name
	 * @param owner
	 * @param inputs
	 * @param nbParams
	 * @param hashcode
	 * @param sideEffects
	 */
	public CallNode(String guard, String name, String owner,
			HashMap<String, Value> inputs, int hashcode,
			ArrayList<Property> sideEffects) {
		super(guard);
		callCounter = 0;
		this.name = name;
		this.owner = owner;
		this.inputs = inputs;
		this.hashcode = hashcode;
		this.sideEffects = sideEffects;
	}

	/**
	 * Returns the called operation's owner
	 * 
	 * @return
	 */
	public String getOwner() {
		return owner;
	}

	protected void incCallCounter() {
		callCounter++;
	}

	/**
	 * Returns the call number
	 * 
	 * @return
	 */
	public int getCallCounter() {
		return callCounter;
	}

	/**
	 * Returns the operation name
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the call inputs
	 * 
	 * @return
	 */
	public HashMap<String, Value> getInputs() {
		return inputs;
	}

	@Override
	public String toString() {
		String result = name;
		result += "[" + callCounter + "]";
		result += "(" + guard + ")";
		result += inputs;
		return result;
	}

	/**
	 * Returns the corresponding activity node hashcode
	 * 
	 * @return
	 */
	public int getHashcode() {
		return hashcode;
	}

	/**
	 * Returns the properties modified by this operation
	 * 
	 * @return
	 */
	public ArrayList<Property> getSideEffects() {
		return sideEffects;
	}
}
