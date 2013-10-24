package org.eclipse.uml.a2acsl.oclgeneration;

import java.util.HashMap;

import org.eclipse.ocl.expressions.OCLExpression;
import org.eclipse.uml.a2acsl.utils.OclUtils;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Operation;

/**
 * Replaces modified variables by the names of variables that hold their current
 * values
 * 
 * @author A560169
 * 
 */
class OclReplacer {

	/**
	 * Updates variables' names in a constraint
	 * 
	 * @param constraint
	 * @return
	 */
	public static String updateVariablesInConstraint(String constraint) {
		HashMap<String, VariableObserver> features = OclContractGenerator
				.getBehaviorGenerator().getVariablesObservers();
		Operation context = OclContractGenerator.getBehaviorGenerator()
				.getcontext();
		OclReplacerVisitor replacer = new OclReplacerVisitor(features);
		OclUtils.setOperationContext(context);
		OclUtils.setEnvironment(OclContractGenerator.getBehaviorGenerator()
				.getLocalVariables());
		OCLExpression<Classifier> consSpec = OclUtils
				.generateOCLConstraint(constraint);
		return consSpec.accept(replacer);
	}

	/**
	 * Updates variables' names in an expression
	 * 
	 * @param expression
	 * @return
	 */
	public static String updateVariablesInExpression(String expression) {
		HashMap<String, VariableObserver> features = OclContractGenerator
				.getBehaviorGenerator().getVariablesObservers();
		Operation context = OclContractGenerator.getBehaviorGenerator()
				.getcontext();
		OclReplacerVisitor replacer = new OclReplacerVisitor(features);
		OclUtils.setOperationContext(context);
		OclUtils.setEnvironment(OclContractGenerator.getBehaviorGenerator()
				.getLocalVariables());
		OCLExpression<Classifier> exp = OclUtils
				.generateOCLExpression(expression);
		return exp.accept(replacer);
	}
}
