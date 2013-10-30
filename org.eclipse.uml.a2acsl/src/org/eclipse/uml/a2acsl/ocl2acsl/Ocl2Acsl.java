package org.eclipse.uml.a2acsl.ocl2acsl;

import org.eclipse.ocl.ParserException;
import org.eclipse.ocl.expressions.OCLExpression;
import org.eclipse.uml.a2acsl.oclgeneration.OclContractGenerator;
import org.eclipse.uml.a2acsl.utils.OclUtils;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Operation;

/**
 * Translates OCL constraints and expressions to ACSL
 * 
 * @author A560169
 * 
 */
public class Ocl2Acsl {

	private static Operation context;
	private static Ocl2AcslVisitor visitor;

	/**
	 * Initializes the translator with and {@link Ocl2AcslVisitor}
	 * 
	 * @param visitor
	 */
	public static void initialize(Ocl2AcslVisitor visitor) {
		Ocl2Acsl.visitor = visitor;
	}

	/**
	 * Translates a boolean OCL constraint to ACSL
	 * 
	 * @param oclConstraint
	 * @param context
	 * @param inPreState
	 * @return
	 * @throws ParserException
	 */
	public static String oclConstraint2acsl(String oclConstraint,
			Operation context, boolean inPreState) throws ParserException {
		Ocl2Acsl.context = context;
		OclUtils.setOperationContext(context);
		OclUtils.setEnvironment(OclContractGenerator.getBehaviorGenerator()
				.getLocalVariables());
		OCLExpression<Classifier> constraint = OclUtils
				.generateOCLConstraint(oclConstraint);
		visitor.setInPreState(inPreState);
		String result = constraint.accept(visitor);
		return result;
	}

	/**
	 * Translates an OCL expression to ACSL
	 * 
	 * @param oclExpression
	 * @param context
	 * @param inPreState
	 * @return
	 * @throws ParserException
	 */
	public static String oclExpression2acsl(String oclExpression,
			Operation context, boolean inPreState) throws ParserException {
		Ocl2Acsl.context = context;
		OclUtils.setOperationContext(context);
		OclUtils.setEnvironment(OclContractGenerator.getBehaviorGenerator()
				.getLocalVariables());
		OCLExpression<Classifier> expression = OclUtils
				.generateOCLExpression(oclExpression);
		visitor.setInPreState(inPreState);
		return expression.accept(visitor);
	}

	/**
	 * Returns the operation context
	 * 
	 * @return
	 */
	public static Operation getContext() {
		return context;
	}

}
