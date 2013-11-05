package org.eclipse.uml.a2acsl.oclgeneration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.ocl.ParserException;
import org.eclipse.ocl.uml.SequenceType;
import org.eclipse.uml.a2acsl.behavior.Behavior;
import org.eclipse.uml.a2acsl.behavior.CallNode;
import org.eclipse.uml.a2acsl.behavior.Node;
import org.eclipse.uml.a2acsl.behavior.ParameterValueNode;
import org.eclipse.uml.a2acsl.behavior.WriteCollectionItemFeatureNode;
import org.eclipse.uml.a2acsl.behavior.WriteCollectionItemVariableNode;
import org.eclipse.uml.a2acsl.behavior.WriteFeatureNode;
import org.eclipse.uml.a2acsl.behavior.WriteVariableNode;
import org.eclipse.uml.a2acsl.oclcontracts.OclContract;
import org.eclipse.uml.a2acsl.parsing.ActivityParser;
import org.eclipse.uml.a2acsl.utils.ModelUtils;
import org.eclipse.uml.a2acsl.values.FeatureValue;
import org.eclipse.uml.a2acsl.values.OpaqueValue;
import org.eclipse.uml.a2acsl.values.ReturnValue;
import org.eclipse.uml.a2acsl.values.ReturnValue.ReturnType;
import org.eclipse.uml.a2acsl.values.StringValue;
import org.eclipse.uml.a2acsl.values.Value;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.TypedElement;
import org.eclipse.uml2.uml.Variable;

/**
 * Generates a behavior OCL contract
 * 
 * @author A560169
 * 
 */
public class BehaviorOclContractGenerator {

	protected OclContract contract; // Calculated contract
	private Behavior behavior; // Current behavior
	private HashMap<String, VariableObserver> variablesObservers; // Keeps track
																	// of
																	// variables
																	// modifications
	private ArrayList<VariableDefinition> definitions; // List of introduced
														// local variables
	protected OclGenerator generator; // Generates Ocl syntax

	/**
	 * Initializes the OCL syntax generator
	 * 
	 * @param generator
	 */
	public void initializeGen(OclGenerator generator) {
		this.generator = generator;
	}

	protected void initialize(Behavior behavior, Operation context) {
		contract = new OclContract(behavior.getName(), context);
		variablesObservers = new HashMap<String, VariableObserver>();
		definitions = new ArrayList<VariableDefinition>();
		this.behavior = behavior;
	}

	/**
	 * Generates a behavior contract by parsing the behavior nodes
	 * 
	 * @param behavior
	 * @param context
	 * @return
	 * @throws ParserException
	 */
	public OclContract generateBehaviorOCLContract(Behavior behavior,
			Operation context) throws ParserException {
		initialize(behavior, context);
		for (Node node : behavior.getNodes()) {
			generatePartialOCLContract(node);
		}
		// Additionnal postconditions expressing values taken by modified
		// properties
		// Example : Variable x with observer having counter set to 3 and last
		// value set to v with v containing y_1 and z_2 generates :
		// let y_1 = y_value in let z_2 = z_value in let x_3 = v(y_1,z_2) in x =
		// x_3
		for (String variable : variablesObservers.keySet()) {
			VariableObserver varContext = variablesObservers.get(variable);
			if (!varContext.isLocal()) {
				String varName = generateVariableName(variable,
						varContext.getCpt());
				String postcondition = generator.generateEquality(variable,
						varName);
				contract.addPostcondition(appendDefinitions(postcondition));
			}
		}
		return contract;
	}

	// Updates contract with constraints induced by given node
	private void generatePartialOCLContract(Node node) throws ParserException {
		String guard = node.getGuard();
		if (!guard.equals("true")) {
			guard = OclReplacer.updateVariablesInConstraint(guard);
			contract.addAssumption(appendDefinitions(guard));
		}
		if (node instanceof CallNode) {
			handleCallNode((CallNode) node);
		} else if (node instanceof ParameterValueNode) {
			handleParameterValueNode((ParameterValueNode) node);
		} else if (node instanceof WriteCollectionItemFeatureNode) {
			handleWriteCollectionItemFeatureNode((WriteCollectionItemFeatureNode) node);
		} else if (node instanceof WriteFeatureNode) {
			handleWriteFeatureNode((WriteFeatureNode) node);
		} else if (node instanceof WriteCollectionItemVariableNode) {
			handleWriteCollectionItemVariableNode((WriteCollectionItemVariableNode) node);
		} else if (node instanceof WriteVariableNode) {
			handleWriteVariableNode((WriteVariableNode) node);
		}
	}

	// Handles a node that changes the value of a structural feature by updating
	// the corresponding variable context with its new value
	// Example1 : x.y.z = 0, when the observer counter of x is 2 updates x's
	// observer with : x_3 =
	// ocl_set(x_2,'y',ocl_set(x_2.y,'z',0)).oclAsType(type_of_x)
	// Example2 : x->at(i).y = 0, when the observer counter of x is 2 updates
	// x's
	// observer with : x_3 = x_2 -> insertAt(i,ocl_set(x_2->at(i),'y',0))
	// The new definition of x_3 is kept in definitions
	private void handleWriteFeatureNode(WriteFeatureNode node)
			throws ParserException {
		boolean isVariable = false;
		String modifiedvariable = node.getFeature();
		Type type = node.getType();
		String value = getValue(node.getValue());
		Value sourceValue = node.getSource();
		if (sourceValue != null) {
			String source = getValue(sourceValue);
			String[] sourceParts = source.split("\\.");
			modifiedvariable = parseSource(sourceParts[0]);
			TypedElement p = ActivityParser.getElement(modifiedvariable);
			type = ModelUtils.getType(p);
			value = generator.generateSetters(sourceParts, node.getFeature(),
					value, parsePartsTypes(sourceParts, type));
			isVariable = (p instanceof Variable);
			String typeName;
			if (type instanceof SequenceType) {
				typeName = ModelUtils.getTypeName(((SequenceType) type)
						.getElementType());
			} else {
				typeName = ModelUtils.getTypeName(type);
			}
			value += ".oclAsType(" + typeName + ")";
		}
		value = OclReplacer.updateVariablesInExpression(value);
		updateDefinitions(modifiedvariable, type, value, isVariable);
	}

	// Handles a node that changes the value of an item of a structural feature
	// by updating the corresponding variable observer with its new value
	// Example : y.z->at(i) = 0, when the observer counter of y is 3 updates y's
	// observer with the value :
	// y_3 = ocl_set(y,'z',y.z->insertAt(i,0)).oclAsType(type_of_y)
	private void handleWriteCollectionItemFeatureNode(
			WriteCollectionItemFeatureNode node) throws ParserException {
		boolean isVariable = false;
		String modifiedvariable = node.getFeature();
		Type type = node.getType();
		Value nodeValue = node.getValue();
		String value = getValue(nodeValue);
		if (node.getSource() == null) {
			value = generator.generateInsertAt(modifiedvariable,
					node.getIndex(), value);
		} else {
			String source = getValue(node.getSource());
			value = generator.generateInsertAt(source + "." + modifiedvariable,
					node.getIndex(), value);
			String[] sourceParts = source.split("\\.");
			modifiedvariable = parseSource(sourceParts[0]);
			TypedElement p = ActivityParser.getElement(modifiedvariable);
			type = p.getType();
			value = generator.generateSetters(sourceParts, node.getFeature(),
					value, parsePartsTypes(sourceParts, type));
			isVariable = (p instanceof Variable);
			String typeName;
			if (type instanceof SequenceType) {
				typeName = ModelUtils.getTypeName(((SequenceType) type)
						.getElementType());
			} else {
				typeName = ModelUtils.getTypeName(type);
			}
			value += ".oclAsType(" + typeName + ")";
		}
		value = OclReplacer.updateVariablesInExpression(value);
		updateDefinitions(modifiedvariable, type, value, isVariable);
	}

	// Parses a string to check if it contains the at construct
	// x -> at(i) returns x, x returns x
	private String parseSource(String source) {
		String var = source.replaceAll(" ", "");
		String at = "->at(";
		if (var.contains(at)) {
			int ind = var.indexOf(at);
			source = var.substring(0, ind);
		}
		return source;
	}

	private String getTypeName(Type type) {
		if (type instanceof SequenceType) {
			return ModelUtils.getTypeName(((SequenceType) type)
					.getElementType());
		} else {
			return ModelUtils.getTypeName(type);
		}
	}

	private String[] parsePartsTypes(String[] parts, Type type) {
		String[] types = new String[parts.length];
		types[0] = getTypeName(type);
		Classifier current = (Classifier) type;
		for (int i = 1; i < parts.length; i++) {
			String part = parseSource(parts[i]);
			for (Property p : current.getAllAttributes()) {
				if (p.getName().equals(part)) {
					current = (Classifier) p.getType();
					types[i] = getTypeName(current);
				}
			}
			if (current instanceof SequenceType)
				current = ((SequenceType) current).getElementType();
		}
		return types;
	}

	// Handles a node that changes the value of a local variable by updating the
	// corresponding observer with the new value
	private void handleWriteVariableNode(WriteVariableNode node)
			throws ParserException {
		String variable = node.getVariable();
		Type type = node.getType();
		String value = getValue(node.getValue());
		value = OclReplacer.updateVariablesInExpression(value);
		updateDefinitions(variable, type, value, true);
	}

	// Handles a node that changes the value of an item of a local variable by
	// updating the corresponding observer with the new value
	private void handleWriteCollectionItemVariableNode(
			WriteCollectionItemVariableNode node) throws ParserException {
		String variable = node.getVariable();
		Type type = node.getType();
		Value nodeValue = node.getValue();
		String value = getValue(nodeValue);
		value = generator.generateInsertAt(variable, node.getIndex(), value);
		String updatedValue = OclReplacer.updateVariablesInExpression(value);
		updateDefinitions(variable, type, updatedValue, true);
	}

	// Handles a call node by adding the necessary clauses to the contract and
	// updating properties observers if operation has side effects
	protected void handleCallNode(CallNode node) throws ParserException {
		Operation contextOp = contract.getContext();
		String contextName = contextOp.getName();
		String opOwner = node.getOwner();
		String contexts = generator.generateContexts(contextName,
				node.getName(), opOwner);
		int callCounter = node.getCallCounter();
		// Counter initialization if first call
		if (callCounter == 0) {
			String cpt = generator.generateCallCounter(contextName,
					node.getName(), opOwner);
			String pre = generator.generateEquality(cpt, "0");
			contract.addPrecondition(pre);
		}
		HashMap<String, Value> parameters = node.getInputs();
		// Constraints on values taken by IN parameters
		for (String param : parameters.keySet()) {
			String value = getValue(parameters.get(param));
			value = OclReplacer.updateVariablesInExpression(value);
			String postcondition = generator.generateEquality(
					generator.generateIn(param, contexts, callCounter + ""),
					value);
			contract.addPostcondition(appendDefinitions(postcondition));
		}
		// Constraints expressing operation side effects
		for (Property p : node.getSideEffects()) {
			String variable = p.getName();
			Type type = ModelUtils.getType(p);
			String replaced = OclReplacer.updateVariablesInExpression(generator
					.generateOut(variable, contexts, callCounter + ""));
			updateDefinitions(variable, type, replaced, false);
		}
	}

	// Handles a node that sets an activity parameter
	private void handleParameterValueNode(ParameterValueNode node)
			throws ParserException {
		String value = getValue(node.getValue());
		value = OclReplacer.updateVariablesInExpression(value);
		String postcondition = generator.generateEquality(node.getParameter(),
				value);
		contract.addPostcondition(appendDefinitions(postcondition));
	}

	// Updates variables observers
	private void updateDefinitions(String variable, Type type, String value,
			boolean isVariable) {
		VariableObserver observer = variablesObservers.get(variable);
		String typeName = ModelUtils.getTypeName(type);
		if (observer == null) {
			// Variable never modified -> add variable@pre as first value if
			// property
			observer = new VariableObserver(type, contract.getName(),
					isVariable);
			if (!isVariable) {
				String varName = generateVariableName(variable, 0);
				VariableDefinition def = new VariableDefinition(varName,
						typeName, variable + "@pre");
				definitions.add(def);
			}
		}
		// Update observer with new value
		observer.incCpt();
		String varName = generateVariableName(variable, observer.getCpt());
		VariableDefinition def = new VariableDefinition(varName, typeName,
				value);
		definitions.add(def);
		variablesObservers.put(variable, observer);
	}

	// Returns the string representation of the specified value
	private String getValue(Value input) {
		if (input instanceof StringValue) {
			String value = ((StringValue) input).getValue();
			return value;
		}
		if (input instanceof ReturnValue) {
			ReturnValue returnValue = (ReturnValue) input;
			String contextName = contract.getContext().getName();
			CallNode callNode = behavior.getCallNode(returnValue
					.getCallHashCode());
			String contexts = generator.generateContexts(contextName,
					callNode.getName(), callNode.getOwner());
			if (returnValue.getType() == ReturnType.RETURN)
				return generator.generateResult(contexts,
						callNode.getCallCounter() + "");
			else {
				String result = generator.generateOut(
						returnValue.getReturnName(), contexts,
						callNode.getCallCounter() + "");
				return result;
			}
		}
		if (input instanceof FeatureValue) {
			FeatureValue featureValue = (FeatureValue) input;
			Value sourceValue = featureValue.getSource();
			String result = "";
			if (sourceValue != null) {
				result += getValue(sourceValue) + ".";
			}
			result += featureValue.getFeature();
			String index = featureValue.getIndex();
			if (!index.equals("-1")) {
				result = generator.generateAt(result, index);
			}
			return result;
		}
		if (input instanceof OpaqueValue) {
			OpaqueValue opaqueValue = (OpaqueValue) input;
			String result = "";
			for (String in : opaqueValue.getInputs().keySet()) {
				String value = getValue(opaqueValue.getInputs().get(in));
				String type = opaqueValue.getTypes().get(in);
				result += generator.generateLet(in, type, value);
			}
			String output = opaqueValue.getOutput();
			String type = opaqueValue.getTypes().get(output);
			String value = opaqueValue.getConstraint().split("=")[1].trim();
			result += generator.generateLet(output, type, value);
			result += output;
			return result;
		}
		return null;
	}

	// Appends let definitions for variables used in the constraint
	private String appendDefinitions(String constraint) {
		ArrayList<VariableDefinition> neededDefs = getNeededDefinitions(constraint);
		return generator.generateDefinitions(neededDefs) + constraint;
	}

	// Searches for variables in the expression and returns the corresponding
	// variables definitions
	private ArrayList<VariableDefinition> getNeededDefinitions(String expression) {
		HashSet<Integer> indexes = new HashSet<Integer>();
		getIndexesOfNeededDefinitions(expression, indexes);
		ArrayList<Integer> listIndexes = new ArrayList<Integer>(indexes);
		java.util.Collections.sort(listIndexes);
		ArrayList<VariableDefinition> defs = new ArrayList<VariableDefinition>();
		for (Integer i : listIndexes) {
			defs.add(definitions.get(i));
		}
		return defs;
	}

	// Returns a set of indexes corresponding to the needed definitions
	private void getIndexesOfNeededDefinitions(String expression,
			HashSet<Integer> indexes) {
		for (VariableDefinition def : definitions) {
			if (expression.contains(def.getName())) {
				indexes.add(definitions.indexOf(def));
				getIndexesOfNeededDefinitions(def.getExpression(), indexes);
			}
		}
	}

	private String generateVariableName(String variable, int cpt) {
		return variable.replaceAll("\\.", "") + "_" + cpt + "_"
				+ contract.getName();
	}

	/**
	 * Returns variables observers
	 * 
	 * @return
	 */
	public HashMap<String, VariableObserver> getVariablesObservers() {
		return variablesObservers;
	}

	/**
	 * Returns local variables
	 * 
	 * @return
	 */
	public HashMap<String, VariableObserver> getLocalVariables() {
		HashMap<String, VariableObserver> localVariables = new HashMap<String, VariableObserver>();
		for (String variable : variablesObservers.keySet()) {
			VariableObserver context = variablesObservers.get(variable);
			if (context.isLocal()) {
				localVariables.put(variable, context);
			}
		}
		return localVariables;
	}

	/**
	 * Returns operation context
	 * 
	 * @return
	 */
	public Operation getcontext() {
		return contract.getContext();
	}

}
