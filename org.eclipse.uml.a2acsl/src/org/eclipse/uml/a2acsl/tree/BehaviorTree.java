package org.eclipse.uml.a2acsl.tree;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.uml.a2acsl.behavior.Behavior;

/**
 * Describes all the possible behaviors of an activity
 * 
 * @author A560169
 * 
 */
public class BehaviorTree {

	private TreeNode rootNode;
	private ArrayList<BehaviorTree> subtrees;

	/**
	 * Creates a new BehaviorTree with the specified node as Root
	 * 
	 * @param rootNode
	 */
	public BehaviorTree(TreeNode rootNode) {
		this.rootNode = rootNode;
		subtrees = new ArrayList<BehaviorTree>();
	}

	/**
	 * Adds a child to the tree having the specified node as root
	 * 
	 * @param node
	 */
	public void addSubtree(TreeNode node) {
		BehaviorTree subtree = new BehaviorTree(node);
		subtrees.add(subtree);
	}

	/**
	 * Adds as much children as there are nodes in the specified list
	 * 
	 * @param nodes
	 */
	public void addSubtrees(ArrayList<TreeNode> nodes) {
		for (TreeNode node : nodes) {
			addSubtree(node);
		}
	}

	/**
	 * Returns the subtree at the specified index
	 * 
	 * @param index
	 * @return
	 */
	public BehaviorTree getSubtree(int index) {
		return subtrees.get(index);
	}

	/**
	 * Generates a list of behaviors, each one corresponds to a branch in the
	 * tree
	 * 
	 * @return
	 */
	public ArrayList<Behavior> generateListBehaviors() {
		ArrayList<Behavior> behaviors = new ArrayList<Behavior>();
		if (subtrees.size() == 0) {
			Behavior behavior = new Behavior();
			behavior.addNode(rootNode);
			behaviors.add(behavior);
			return behaviors;
		}
		for (BehaviorTree subtree : subtrees) {
			ArrayList<Behavior> subBehaviors = subtree.generateListBehaviors();
			for (Behavior behavior : subBehaviors) {
				behavior.addNode(rootNode);
			}
			behaviors.addAll(subBehaviors);
		}
		return behaviors;
	}

	private void print(String prefix, boolean isTail) {
		System.out.println(prefix + (isTail ? "'__ " : "|__ ")
				+ rootNode.toString());
		for (Iterator<BehaviorTree> iterator = subtrees.iterator(); iterator
				.hasNext();) {
			iterator.next().print(prefix + (isTail ? "    " : "|   "),
					!iterator.hasNext());
		}
	}

	/**
	 * Prints a graphical representation of the behavior tree
	 */
	public void print() {
		print("", true);
	}
}
