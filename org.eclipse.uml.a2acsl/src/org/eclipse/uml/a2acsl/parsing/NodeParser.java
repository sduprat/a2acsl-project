package org.eclipse.uml.a2acsl.parsing;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.emf.common.util.EList;
import org.eclipse.uml.a2acsl.behavior.CallNode;
import org.eclipse.uml.a2acsl.behavior.Node;
import org.eclipse.uml.a2acsl.behavior.ParameterValueNode;
import org.eclipse.uml.a2acsl.behavior.WriteCollectionItemFeatureNode;
import org.eclipse.uml.a2acsl.behavior.WriteCollectionItemVariableNode;
import org.eclipse.uml.a2acsl.behavior.WriteFeatureNode;
import org.eclipse.uml.a2acsl.behavior.WriteVariableNode;
import org.eclipse.uml.a2acsl.tree.TreeNode;
import org.eclipse.uml.a2acsl.utils.ModelUtils;
import org.eclipse.uml.a2acsl.values.FeatureValue;
import org.eclipse.uml.a2acsl.values.OpaqueValue;
import org.eclipse.uml.a2acsl.values.ReturnValue;
import org.eclipse.uml.a2acsl.values.ReturnValue.ReturnType;
import org.eclipse.uml.a2acsl.values.StringValue;
import org.eclipse.uml.a2acsl.values.Value;
import org.eclipse.uml2.uml.Action;
import org.eclipse.uml2.uml.ActivityEdge;
import org.eclipse.uml2.uml.ActivityNode;
import org.eclipse.uml2.uml.ActivityParameterNode;
import org.eclipse.uml2.uml.AddStructuralFeatureValueAction;
import org.eclipse.uml2.uml.AddVariableValueAction;
import org.eclipse.uml2.uml.CallOperationAction;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.ForkNode;
import org.eclipse.uml2.uml.InputPin;
import org.eclipse.uml2.uml.ObjectFlow;
import org.eclipse.uml2.uml.OpaqueAction;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.OutputPin;
import org.eclipse.uml2.uml.Parameter;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.ReadStructuralFeatureAction;
import org.eclipse.uml2.uml.ReadVariableAction;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.ValuePin;
import org.eclipse.uml2.uml.ValueSpecificationAction;
import org.eclipse.uml2.uml.Variable;

/**
 * Contains methods that parse nodes in the behavior tree
 * 
 * @author A560169
 * 
 */
public class NodeParser {

	/**
	 * Parses an activity node from the tree and returns the nodes to be
	 * considered in the behavior
	 * 
	 * @param node
	 * @return
	 */
	public ArrayList<Node> parseNode(TreeNode node) {
		ArrayList<Node> nodes = new ArrayList<Node>();
		ActivityNode activityNode = node.getNode();
		String guard = node.getGuard();
		if (activityNode instanceof CallOperationAction) {
			Operation operation = ((CallOperationAction) activityNode)
					.getOperation();
			ActivityParser.addCalledOperation(operation);
			nodes.addAll(parseValueAction((Action) activityNode, "true"));
			nodes.add(parseCallAction((CallOperationAction) activityNode, guard));
		} else if (activityNode instanceof ValueSpecificationAction
				|| activityNode instanceof ReadStructuralFeatureAction
				|| activityNode instanceof ReadVariableAction
				|| activityNode instanceof OpaqueAction) {
			nodes.addAll(parseValueAction((Action) activityNode, guard));
		} else if (activityNode instanceof AddStructuralFeatureValueAction) {
			nodes.add(parseAddFeatureAction(
					(AddStructuralFeatureValueAction) activityNode, guard));
		} else if (activityNode instanceof AddVariableValueAction) {
			nodes.add(parseAddVariableAction(
					(AddVariableValueAction) activityNode, guard));
		}
		return nodes;
	}

	/**
	 * Parses a {@link CallOperationAction} and returns a {@link CallNode}
	 * containing information about the call inputs and the side effects
	 * 
	 * @param action
	 * @param guard
	 * @return
	 */
	protected CallNode parseCallAction(CallOperationAction action, String guard) {
		Operation operation = action.getOperation();
		Class owner = operation.getClass_();
		HashMap<String, Value> parameters = new HashMap<String, Value>();
		InputPin targetPin = action.getTarget();
		for (InputPin input : action.getInputs()) {
			if (!input.equals(targetPin)) {
				String param = input.getName();
				Value value = parseInputValue(input);
				parameters.put(param, value);
			}
		}
		CallNode callNode = new CallNode(guard, operation.getName(),
				owner.getName(), parameters, action.hashCode(),
				ModelUtils.getSideEffects(operation));
		return callNode;
	}

	/**
	 * Parses the values provided by action nodes if they set activity
	 * parameters and returns the corresponding {@link ParameterValueNode}s
	 * 
	 * @param valueAction
	 * @param guard
	 * @return
	 */
	private ArrayList<ParameterValueNode> parseValueAction(Action valueAction,
			String guard) {
		ArrayList<ParameterValueNode> pvNodes = new ArrayList<ParameterValueNode>();
		for (OutputPin output : valueAction.getOutputs()) {
			for (ActivityEdge edge : output.getOutgoings()) {
				if (edge instanceof ObjectFlow) {
					ActivityNode target = edge.getTarget();
					if (target instanceof ActivityParameterNode) {
						Parameter param = ((ActivityParameterNode) target)
								.getParameter();
						String name = param.getName();
						ParameterValueNode pvNode = new ParameterValueNode(
								guard, parseOutputValue(output), name);
						pvNodes.add(pvNode);
					}
					if (target instanceof ForkNode) {
						for (ActivityEdge fork : target.getOutgoings()) {
							target = fork.getTarget();
							if (target instanceof ActivityParameterNode) {
								Parameter param = ((ActivityParameterNode) target)
										.getParameter();
								String name = param.getName();
								ParameterValueNode pvNode = new ParameterValueNode(
										guard, parseOutputValue(output), name);
								pvNodes.add(pvNode);
							}
						}
					}
				}
			}
		}
		return pvNodes;
	}

	/**
	 * Parses an {@link AddStructuralFeatureValueAction} and returns the
	 * corresponding {@link WriteFeatureNode}
	 * 
	 * @param action
	 * @param guard
	 * @return
	 */
	private WriteFeatureNode parseAddFeatureAction(
			AddStructuralFeatureValueAction action, String guard) {
		Value value = parseInputValue(action.getValue());
		Property feature = (Property) action.getStructuralFeature();
		String featureName = feature.getName();
		Type type = ModelUtils.getType(feature);

		Value source = null;
		if (action.getObject() != null) {
			source = parseInputValue(action.getObject());
		}
		if (ModelUtils.isConstantCollection(feature)) {
			ValuePin pin = (ValuePin) action.getInsertAt();
			String index = pin.getValue().stringValue();
			return new WriteCollectionItemFeatureNode(guard, value,
					featureName, type, index, source);
		} else
			return new WriteFeatureNode(guard, value, featureName, type, source);
	}

	/**
	 * Parses an {@link AddVariableValueAction} and returns the corresponding
	 * {@link WriteVariableNode}
	 * 
	 * @param action
	 * @param guard
	 * @return
	 */
	private WriteVariableNode parseAddVariableAction(
			AddVariableValueAction action, String guard) {
		Variable variable = action.getVariable();
		String varName = action.getVariable().getName();
		Type varType = ModelUtils.getType(action.getVariable());
		Value value = parseInputValue(action.getValue());
		if (ModelUtils.isConstantCollection(variable)) {
			ValuePin pin = (ValuePin) action.getInsertAt();
			String index = pin.getValue().stringValue();
			return new WriteCollectionItemVariableNode(guard, varName, varType,
					value, index);
		} else
			return new WriteVariableNode(guard, varName, varType, value);
	}

	/**
	 * Returns the value provided by the specified output pin
	 * 
	 * @param output
	 * @return
	 */
	protected Value parseOutputValue(OutputPin output) {
		ActivityNode node = (ActivityNode) output.getOwner();
		if (node instanceof ValueSpecificationAction) {
			String value = ((ValueSpecificationAction) node).getValue()
					.stringValue();
			StringValue stringValue = new StringValue(value);
			return stringValue;
		} else if (node instanceof CallOperationAction) {
			ReturnType type = null;
			String name = "";
			if (output.getName().equals("result")) {
				type = ReturnType.RETURN;
			} else {
				Operation operation = ((CallOperationAction) node)
						.getOperation();
				for (Parameter p : operation.getOwnedParameters()) {
					if (p.getName().equals(output.getName())) {
						type = ReturnType.OUT;
						name = p.getName();
					}
				}
			}
			ReturnValue returnValue = new ReturnValue(node.hashCode(), name,
					type);
			return returnValue;
		} else if (node instanceof ReadStructuralFeatureAction) {
			InputPin object = ((ReadStructuralFeatureAction) node).getObject();
			String feature = ((ReadStructuralFeatureAction) node)
					.getStructuralFeature().getName();
			Value value = null;
			if (object != null) {
				value = parseInputValue(object);
			}
			String index = "-1";
			EList<Constraint> pres = ((ReadStructuralFeatureAction) node)
					.getLocalPreconditions();
			if (!pres.isEmpty()) {
				String annot = pres.get(0).getSpecification().stringValue();
				String[] annotParts = annot.split(" ");
				if (annotParts[0].equals("@index"))
					index = annotParts[1];
			}
			FeatureValue featureValue = new FeatureValue(feature, value, index);
			return featureValue;
		} else if (node instanceof ReadVariableAction) {
			String varName = ((ReadVariableAction) node).getVariable()
					.getName();
			return new StringValue(varName);
		} else if (node instanceof OpaqueAction) {
			HashMap<String, Value> inputs = new HashMap<String, Value>();
			String outputName;
			HashMap<String, String> types = new HashMap<String, String>();
			String constraint = ((Action) node).getLocalPostconditions().get(0)
					.getSpecification().stringValue();

			for (InputPin input : ((Action) node).getInputs()) {
				Value value = parseInputValue(input);
				inputs.put(input.getName(), value);
				Type type = ModelUtils.getType(input);
				String typeName = type.getName();
				types.put(input.getName(), typeName);
			}

			OutputPin outputPin = ((Action) node).getOutputs().get(0);
			outputName = outputPin.getName();
			types.put(outputName, ModelUtils.getType(outputPin).getName());
			return new OpaqueValue(inputs, outputName, types, constraint);

		}
		return null;
	}

	/**
	 * Parses the value provided to the specified input pin
	 * 
	 * @param input
	 * @return
	 */
	private Value parseInputValue(ActivityNode input) {
		if (input instanceof ValuePin) {
			String value = ((ValuePin) input).getValue().stringValue();
			return new StringValue(value);
		}
		ObjectFlow flow = (ObjectFlow) input.getIncomings().get(0);
		ActivityNode source = flow.getSource();
		if (source instanceof ActivityParameterNode) {
			String value = ((ActivityParameterNode) source).getParameter()
					.getName();
			StringValue strValue = new StringValue(value);
			return strValue;
		} else if (source instanceof ForkNode)
			return parseInputValue(source);
		else if (source instanceof OutputPin)
			return parseOutputValue((OutputPin) source);
		return null;
	}
}
