package org.eclipse.uml.a2acsl.parsing;

import java.util.ArrayList;

import org.eclipse.uml.a2acsl.behavior.Behavior;
import org.eclipse.uml.a2acsl.tree.BehaviorTree;
import org.eclipse.uml.a2acsl.tree.TreeNode;
import org.eclipse.uml2.uml.Activity;
import org.eclipse.uml2.uml.ActivityEdge;
import org.eclipse.uml2.uml.ActivityFinalNode;
import org.eclipse.uml2.uml.ActivityNode;
import org.eclipse.uml2.uml.ControlFlow;
import org.eclipse.uml2.uml.DecisionNode;
import org.eclipse.uml2.uml.InitialNode;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Parameter;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.TypedElement;
import org.eclipse.uml2.uml.Variable;

/**
 * 
 * Contains methods to parse an activity
 * 
 * @author A560169
 * 
 */
public class ActivityParser {

	private static Activity activity;
	private static Operation context;
	private static BehaviorTree behaviorTree;
	private static ArrayList<Behavior> behaviors;
	private static ArrayList<Operation> calledOperations;
	private static NodeParser nodeParser;

	private static void initialize(Activity activity) {
		ActivityParser.activity = activity;
		context = (Operation) activity.getSpecification();
		calledOperations = new ArrayList<Operation>();
	}

	/**
	 * Returns the instance of the node parser
	 * 
	 * @return
	 */
	public static NodeParser getNodeParser() {
		return nodeParser;
	}

	private static ActivityNode getInitialNode() {
		for (ActivityNode node : activity.getNodes()) {
			if (node instanceof InitialNode)
				return node;
		}
		return null;
	}

	/**
	 * Parses an activity with the given node parser by first constructing the
	 * behavior tree and then generating the possible behaviors
	 * 
	 * @param activity
	 * @param nodeParser
	 */
	public static void parseActivity(Activity activity, NodeParser nodeParser) {
		initialize(activity);
		ActivityParser.nodeParser = nodeParser;
		ActivityNode initial = getInitialNode();
		TreeNode initialNode = new TreeNode("true", initial);
		behaviorTree = new BehaviorTree(initialNode);
		parseNodes(behaviorTree, initialNode);
		generateListBehaviors();
	}

	private static void parseNodes(BehaviorTree tree, TreeNode currentNode) {
		ActivityNode current = currentNode.getNode();
		if (current instanceof ActivityFinalNode)
			return;
		else if (current instanceof DecisionNode) {
			ArrayList<TreeNode> nextNodes = getNextNodes((DecisionNode) current);
			tree.addSubtrees(nextNodes);
			for (TreeNode n : nextNodes) {
				int ind = nextNodes.indexOf(n);
				parseNodes(tree.getSubtree(ind), n);
			}
		} else {
			TreeNode next = getNextNode(current);
			tree.addSubtree(next);
			parseNodes(tree.getSubtree(0), next);
		}
	}

	private static TreeNode getNextNode(ActivityNode node) {
		TreeNode treeNode;
		for (ActivityEdge edge : node.getOutgoings()) {
			if (edge instanceof ControlFlow) {
				ActivityNode target = edge.getTarget();
				treeNode = new TreeNode(edge.getGuard().stringValue(), target);
				return treeNode;
			}
		}

		return null;
	}

	private static ArrayList<TreeNode> getNextNodes(DecisionNode decisionNode) {
		ArrayList<TreeNode> nextNodes = new ArrayList<TreeNode>();
		for (ActivityEdge edge : decisionNode.getOutgoings()) {
			if (edge instanceof ControlFlow) {
				ActivityNode target = edge.getTarget();
				TreeNode node = new TreeNode(edge.getGuard().stringValue(),
						target);
				nextNodes.add(node);
			}
		}
		return nextNodes;
	}

	private static void generateListBehaviors() {
		behaviors = behaviorTree.generateListBehaviors();
		for (Behavior behavior : behaviors) {
			behavior.setName("B" + behaviors.indexOf(behavior));
		}
	}

	/**
	 * Adds the specified operation to the called operations of the activity
	 * when first called
	 * 
	 * @param operation
	 */
	public static void addCalledOperation(Operation operation) {
		if (!calledOperations.contains(operation)) {
			calledOperations.add(operation);
		}
	}

	/**
	 * Returns the list of behaviors corresponding tot he activity
	 * 
	 * @return
	 */
	public static ArrayList<Behavior> getListBehaviors() {
		return behaviors;
	}

	/**
	 * Returns the property, variable or parameter having the specified name
	 * 
	 * @param name
	 * @return
	 */
	public static TypedElement getElement(String name) {
		for (Property p : context.getClass_().getAllAttributes()) {
			if (p.getName().equals(name))
				return p;
		}
		for (Variable v : activity.getVariables()) {
			if (v.getName().equals(name))
				return v;
		}
		for (Parameter p : context.getOwnedParameters()) {
			if (p.getName().equals(name))
				return p;
		}
		return null;
	}

	/**
	 * Returns the operations called within this activity
	 * 
	 * @return
	 */
	public static ArrayList<Operation> getCalledOperations() {
		return calledOperations;
	}
}
