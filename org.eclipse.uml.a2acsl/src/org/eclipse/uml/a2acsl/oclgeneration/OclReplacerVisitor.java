package org.eclipse.uml.a2acsl.oclgeneration;

import java.util.HashMap;

import org.eclipse.emf.common.util.EList;
import org.eclipse.ocl.expressions.EnumLiteralExp;
import org.eclipse.ocl.expressions.IfExp;
import org.eclipse.ocl.expressions.IteratorExp;
import org.eclipse.ocl.expressions.LetExp;
import org.eclipse.ocl.expressions.OCLExpression;
import org.eclipse.ocl.expressions.OperationCallExp;
import org.eclipse.ocl.expressions.PropertyCallExp;
import org.eclipse.ocl.expressions.TypeExp;
import org.eclipse.ocl.expressions.Variable;
import org.eclipse.ocl.expressions.VariableExp;
import org.eclipse.uml.a2acsl.parsing.ModelParser;
import org.eclipse.uml.a2acsl.utils.ModelUtils;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.EnumerationLiteral;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Parameter;
import org.eclipse.uml2.uml.Property;

/**
 * Visits an OCL expression and replaces occurrences of modified variables by
 * the name of the variable that holds its current value Example : Variable x,
 * in behavior B0, if x has been modified 3 times all occurrences of x will be
 * replaced by x_B0_3
 * 
 * @author A560169
 * 
 */
public class OclReplacerVisitor extends OclAbstractVisitor {

	private HashMap<String, VariableObserver> variablesContexts;

	public OclReplacerVisitor(
			HashMap<String, VariableObserver> variablesContexts) {
		super();
		this.variablesContexts = variablesContexts;
	}

	@Override
	public String visitOperationCallExp(
			OperationCallExp<Classifier, Operation> callExp) {
		String opName = callExp.getReferredOperation().getName();
		OCLExpression<Classifier> source = callExp.getSource();
		if (opName.equals("at"))
			return source.accept(this) + "->at("
					+ callExp.getArgument().get(0).accept(this) + ")";
		if (opName.equals("first"))
			return source.accept(this) + "->first()";
		if (opName.equals("insertAt")) {
			String arg1 = callExp.getArgument().get(0).accept(this);
			String arg2 = callExp.getArgument().get(1).accept(this);
			return source.accept(this) + "->insertAt(" + arg1 + ", " + arg2
					+ ")";
		}
		if (opName.contains("FirstMatch")) {
			OCLExpression<Classifier> argument = callExp.getArgument().get(0);
			String argRes = argument.accept(this);
			return opName + "(" + argRes + ")";
		}
		if (opName.equals("ocl_set")) {
			String obj = callExp.getArgument().get(0).accept(this);
			String feature = callExp.getArgument().get(1).toString();
			String value = callExp.getArgument().get(2).accept(this);
			return opName + "(" + obj + ", " + feature + ", " + value + ")";
		}
		if (opName.equals("oclAsType")) {
			String sourceRes = callExp.getSource().accept(this);
			String argRes = callExp.getArgument().get(0).accept(this);
			return sourceRes + ".oclAsType(" + argRes + ")";
		}
		String sourceRes = source.accept(this);
		if (callExp.getArgument().isEmpty())
			return opName + "(" + sourceRes + ")";
		else {
			OCLExpression<Classifier> argument = callExp.getArgument().get(0);
			String argRes = argument.accept(this);
			if (argument instanceof OperationCallExp) {
				argRes = "(" + argRes + ")";
			}
			if (isInfix(opName)) {
				return sourceRes + " " + opName + " " + argRes;
			} else {
				return sourceRes + "." + opName + "(" + argRes + ")";
			}
		}

	}

	private boolean isInfix(String oper) {
		return oper.equals("*") || oper.equals("/") || oper.equals("+")
				|| oper.equals("-") || oper.equals("<") || oper.equals(">")
				|| oper.equals("<=") || oper.equals(">=") || oper.equals("=")
				|| oper.equals("<>") || oper.equals("or") || oper.equals("xor")
				|| oper.equals("and") || oper.equals("implies")
				|| oper.equals("mod");
	}

	@SuppressWarnings("unchecked")
	@Override
	public String visitPropertyCallExp(
			PropertyCallExp<Classifier, Property> callExp) {
		OCLExpression<Classifier> sourceExp = callExp.getSource();
		Property property = callExp.getReferredProperty();
		String feature = property.getName();

		// Check if context spec
		if (ModelUtils.isGeneratedType(property.getType())) {
			if (sourceExp.toString().equals("self"))
				return feature;
			else
				return sourceExp.accept(this) + "." + feature;
		}
		if (sourceExp instanceof PropertyCallExp) {
			if (ModelUtils
					.isGeneratedType(((PropertyCallExp<Classifier, Property>) sourceExp)
							.getReferredProperty().getType())) {
				if (sourceExp.toString().equals("self"))
					return feature;
				else
					return sourceExp.accept(this) + "." + feature;
			}
		}
		// Global Variable
		if (sourceExp.toString().equals("self")) {
			VariableObserver context = variablesContexts.get(feature);
			if (context != null) {
				int cpt = context.getCpt();
				return feature.replaceAll("\\.", "") + "_" + cpt + "_"
						+ context.getName();
			} else {
				if (property.isReadOnly()) {
					return feature;
				} else
					return feature + "@pre";
			}
		}

		// Other properties
		if (sourceExp != null) {
			String source = sourceExp.accept(this);
			if (property.isStatic())
				feature = source + "::" + feature;
			else
				feature = source + "." + feature;
		}
		return feature;
	}

	@Override
	public String visitVariableExp(VariableExp<Classifier, Parameter> exp) {
		String var = exp.getReferredVariable().getName();
		VariableObserver context = variablesContexts.get(var);
		if (context != null) {
			int cpt = context.getCpt();
			return var.replaceAll("\\.", "") + "_" + cpt + "_"
					+ context.getName();
		}
		return exp.toString();
	}

	@Override
	public String visitLetExp(LetExp<Classifier, Parameter> exp) {
		String var = exp.getVariable().getName();
		OCLExpression<Classifier> initExp = exp.getVariable()
				.getInitExpression();
		String init = initExp.accept(this);
		String type = exp.getVariable().getType().getQualifiedName();
		OCLExpression<Classifier> bodyExp = exp.getIn();
		String body = bodyExp.accept(this);
		return "let " + var + " : " + type + " = " + init + " in " + body;
	}

	@Override
	public String visitIteratorExp(IteratorExp<Classifier, Parameter> exp) {
		String name = exp.getName();
		String body = exp.getBody().accept(this);
		String source = exp.getSource().accept(this);
		StringBuffer result = new StringBuffer(source + " -> " + name + "(");
		EList<Variable<Classifier, Parameter>> its = exp.getIterator();
		for (Variable<Classifier, Parameter> var : its) {
			result.append(var.getName() + ",");
		}
		if (!its.isEmpty()) {
			result = new StringBuffer(result.substring(0, result.length() - 1)
					+ " | ");
		}
		result.append(body + ")");
		return result.toString();
	}

	@Override
	public String visitTypeExp(TypeExp<Classifier> exp) {
		String modelName = ModelParser.getModel().getName();
		return exp.toString().replace(modelName + "::", "")
				.replace("oclstdlib::", "");
	}

	@Override
	public String visitEnumLiteralExp(
			EnumLiteralExp<Classifier, EnumerationLiteral> exp) {
		String modelName = ModelParser.getModel().getName();
		return exp.toString().replace(modelName + "::", "");
	}

	@Override
	public String visitIfExp(IfExp<Classifier> exp) {
		String cond = exp.getCondition().accept(this);
		String thenExp = exp.getThenExpression().accept(this);
		StringBuffer result = new StringBuffer("if " + cond + " then "
				+ thenExp);
		if (exp.getElseExpression() != null) {
			result.append(" else " + exp.getElseExpression().accept(this));
		}
		result.append(" endif");
		return result.toString();
	}
}
