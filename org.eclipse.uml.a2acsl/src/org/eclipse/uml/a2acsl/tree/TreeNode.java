package org.eclipse.uml.a2acsl.tree;

import org.eclipse.uml2.uml.ActivityNode;

/**
 * Describes a node in the behavior tree of an activity
 * 
 * @author A560169
 * 
 */
public class TreeNode {

	private String guard;
	private ActivityNode node;

	/**
	 * Isntantiates a new node
	 * 
	 * @param guard
	 * @param node
	 */
	public TreeNode(String guard, ActivityNode node) {
		this.guard = guard;
		this.node = node;
	}

	/**
	 * Returns the {@link ActivityNode} corresponding to this node
	 * 
	 * @return
	 */
	public ActivityNode getNode() {
		return node;
	}

	/**
	 * Returns the guard on the incoming edge of the corresponding activity node
	 * 
	 * @return
	 */
	public String getGuard() {
		return guard;
	}

	@Override
	public String toString() {
		String result = node.getName() + "(" + guard + ")";
		return result;
	}
}
