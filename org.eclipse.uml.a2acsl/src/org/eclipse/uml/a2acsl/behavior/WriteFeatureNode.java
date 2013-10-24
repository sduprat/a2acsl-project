package org.eclipse.uml.a2acsl.behavior;

import org.eclipse.uml.a2acsl.values.Value;
import org.eclipse.uml2.uml.Type;

/**
 * Describes a node that sets the value of a structural feature
 * 
 * @author A560169
 * 
 */
public class WriteFeatureNode extends Node {

	private Value value; // Value taken
	private String feature; // Name of feature
	private Type type; // Type of feature
	private Value source; // Source of structural feature

	/**
	 * Instantiates a new node
	 * 
	 * @param guard
	 * @param value
	 * @param feature
	 * @param type
	 * @param source
	 */
	public WriteFeatureNode(String guard, Value value, String feature,
			Type type, Value source) {
		super(guard);
		this.value = value;
		this.feature = feature;
		this.type = type;
		this.source = source;
	}

	/**
	 * Returns the feature's new value
	 * 
	 * @return
	 */
	public Value getValue() {
		return value;
	}

	/**
	 * Returns the feature's name
	 * 
	 * @return
	 */
	public String getFeature() {
		return feature;
	}

	/**
	 * Returns the feature's type
	 * 
	 * @return
	 */
	public Type getType() {
		return type;
	}

	/**
	 * Returns the value of the source
	 * 
	 * @return
	 */
	public Value getSource() {
		return source;
	}
}
