package org.eclipse.uml.a2acsl.behavior;

import java.util.ArrayList;

import org.eclipse.uml.a2acsl.parsing.ActivityParser;
import org.eclipse.uml.a2acsl.tree.TreeNode;

/**
 * Describes an activity behavior, that is a list of consecutive actions in a
 * possible execution
 * 
 * @author A560169
 * 
 */
public class Behavior {

	private ArrayList<Node> nodes;
	private String name;

	/**
	 * Instantiates an empty behavior
	 */
	public Behavior() {
		nodes = new ArrayList<Node>();
	}

	/**
	 * Sets the name of the behavior
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Parses and adds a node from the Behavior Tree to this behavior
	 * 
	 * @param treeNode
	 */
	public void addNode(TreeNode treeNode) {
		ArrayList<Node> parsedNodes = ActivityParser.getNodeParser().parseNode(
				treeNode);
		String guard = treeNode.getGuard();
		// Treenode generates no behavior nodes but has guard
		if (parsedNodes.isEmpty() && !guard.equals("true")) {
			if (nodes.isEmpty()) {
				nodes.add(new Node(guard));
			} else {
				nodes.get(0).addGuard(guard);
			}
		} else {
			// Add new nodes and increase call counters if operation call
			for (Node node : parsedNodes) {
				nodes.add(0, node);
				if (node instanceof CallNode) {
					incCallCounters(((CallNode) node).getName());
				}
			}
		}
	}

	// Updates call counters when a new call to an operation is added to the
	// behavior
	private void incCallCounters(String operation) {
		for (int i = 1; i < nodes.size(); i++) {
			Node n = nodes.get(i);
			if (n instanceof CallNode) {
				CallNode callNode = (CallNode) n;
				String name = callNode.getName();
				if (name.equals(operation)) {
					callNode.incCallCounter();
				}
			}
		}
	}

	/**
	 * Returns the call node with that has the specified hashcode
	 * 
	 * @param hashcode
	 * @return
	 */
	public CallNode getCallNode(int hashcode) {
		for (Node node : nodes) {
			if (node instanceof CallNode) {
				CallNode callNode = (CallNode) node;
				int currentHash = callNode.getHashcode();
				if (currentHash == hashcode)
					return callNode;
			}
		}
		return null;
	}

	/**
	 * Returns behavior name
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns behavior nodes
	 * 
	 * @return
	 */
	public ArrayList<Node> getNodes() {
		return nodes;
	}
}
