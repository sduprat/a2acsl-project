package org.eclipse.uml.a2acsl.behavior;

/**
 * Describes a generic node
 * 
 * @author A560169
 * 
 */
public class Node {

	protected String guard;

	/**
	 * Instantiates a new node
	 * 
	 * @param guard
	 */
	public Node(String guard) {
		this.guard = guard;
	}

	/**
	 * Returns the guard constraint before the executions of this node
	 * 
	 * @return
	 */
	public String getGuard() {
		return guard;
	}

	/**
	 * Appends the specified guard to the node's guard
	 * 
	 * @param guard
	 */
	public void addGuard(String guard) {
		if (this.guard.equals("true")) {
			this.guard = guard;
		} else {
			this.guard += " and " + guard;
		}
	}
}
