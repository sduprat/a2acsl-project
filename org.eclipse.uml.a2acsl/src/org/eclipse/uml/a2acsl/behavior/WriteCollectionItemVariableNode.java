package org.eclipse.uml.a2acsl.behavior;

import org.eclipse.uml.a2acsl.values.Value;
import org.eclipse.uml2.uml.Type;

/**
 * Describes a node that sets the value of an item of a local variable
 * 
 * @author A560169
 * 
 */
public class WriteCollectionItemVariableNode extends WriteVariableNode {

	private String index;// Item index

	/**
	 * Instantiates a new node
	 * 
	 * @param guard
	 * @param variable
	 * @param type
	 * @param value
	 * @param index
	 */
	public WriteCollectionItemVariableNode(String guard, String variable,
			Type type, Value value, String index) {
		super(guard, variable, type, value);
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
