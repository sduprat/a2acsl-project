package org.eclipse.uml.a2acsl.behavior;

import org.eclipse.uml.a2acsl.values.Value;
import org.eclipse.uml2.uml.Type;

/**
 * Describes a node that sets the value of an item of a structural feature
 * 
 * @author A560169
 * 
 */
public class WriteCollectionItemFeatureNode extends WriteFeatureNode {

	private String index; // Item index

	/**
	 * Instantiates a new node
	 * 
	 * @param guard
	 * @param value
	 * @param feature
	 * @param type
	 * @param index
	 * @param source
	 */
	public WriteCollectionItemFeatureNode(String guard, Value value,
			String feature, Type type, String index, Value source) {
		super(guard, value, feature, type, source);
		this.index = index;
	}

	/**
	 * Returns the index of the written item
	 * 
	 * @return
	 */
	public String getIndex() {
		return index;
	}

}
