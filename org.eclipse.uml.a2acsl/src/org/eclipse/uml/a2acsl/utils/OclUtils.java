package org.eclipse.uml.a2acsl.utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.ocl.Environment;
import org.eclipse.ocl.OCL;
import org.eclipse.ocl.ParserException;
import org.eclipse.ocl.expressions.OCLExpression;
import org.eclipse.ocl.expressions.Variable;
import org.eclipse.ocl.helper.OCLHelper;
import org.eclipse.ocl.uml.ExpressionInOCL;
import org.eclipse.ocl.uml.UMLEnvironment;
import org.eclipse.ocl.uml.UMLEnvironmentFactory;
import org.eclipse.ocl.uml.UMLFactory;
import org.eclipse.ocl2acsl.Activator;
import org.eclipse.ocl2acsl.additionalOperations.CustomOperation;
import org.eclipse.uml.a2acsl.oclgeneration.VariableObserver;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Parameter;
import org.eclipse.uml2.uml.Property;

/**
 * Contains static helpers for OCL expressions generation
 * 
 * @author A560169
 * 
 */
public class OclUtils {

	private static UMLEnvironmentFactory envFact = new UMLEnvironmentFactory();
	private static OCL<?, Classifier, Operation, Property, ?, ?, ?, ?, ?, Constraint, ?, ?> ocl = OCL
			.newInstance(envFact);
	private static OCLHelper<Classifier, Operation, Property, Constraint> helper = ocl
			.createOCLHelper();
	private static UMLFactory umlFact = UMLFactory.eINSTANCE;
	protected static String EXT_ID = "customOperation";
	protected static List<CustomOperation> allOperations = getAllOperations();

	/**
	 * Generates an {@link OCLExpression} object by parsing a string
	 * specification of an expression
	 * 
	 * @param stringExp
	 *            :
	 * @return
	 */
	public static OCLExpression<Classifier> generateOCLExpression(
			String stringExp) throws ParserException {
		try {
			stringExp = stringExp.replaceAll("@pre", "");
			OCLExpression<Classifier> expression = helper
					.createQuery(stringExp);
			return expression;
		} catch (ParserException e) {
			ParserException e2 = new ParserException(e.getMessage()
					+ " in expression " + stringExp);
			e2.setStackTrace(e.getStackTrace());
			throw e2;
		}
	}

	/**
	 * Generates an {@link OCLExpression} object by parsing a string
	 * specification of a boolean constraint
	 * 
	 * @param stringExp
	 *            :
	 * @return
	 */
	public static OCLExpression<Classifier> generateOCLConstraint(
			String stringSpec) throws ParserException {
		try {
			Constraint constraint = helper.createPostcondition(stringSpec);
			OCLExpression<Classifier> consSpec = ((ExpressionInOCL) constraint
					.getSpecification()).getBodyExpression();
			return consSpec;
		} catch (ParserException e) {
			ParserException e2 = new ParserException(e.getMessage()
					+ " in expression " + stringSpec);
			e2.setStackTrace(e.getStackTrace());
			throw e2;
		}
	}

	/**
	 * Sets the operation context of the {@link OCLHelper}
	 * 
	 * @param context
	 *            :
	 */
	public static void setOperationContext(Operation context) {
		helper.setOperationContext(context.getClass_(), context);
	}

	/**
	 * Sets the environment of the parser by adding additional variables and
	 * custom operations
	 * 
	 * @param variables
	 *            :
	 */
	public static void setEnvironment(
			HashMap<String, VariableObserver> variables) {
		@SuppressWarnings("unchecked")
		Environment<?, Classifier, Operation, Property, ?, Parameter, ?, ?, ?, Constraint, ?, ?> env = (Environment<?, Classifier, Operation, Property, ?, Parameter, ?, ?, ?, Constraint, ?, ?>) helper
				.getEnvironment();
		for (String variable : variables.keySet()) {
			VariableObserver context = variables.get(variable);
			Variable<Classifier, Parameter> var = umlFact.createVariable();
			var.setName(variable);
			var.setType((Classifier) context.getType());
			env.addElement(variable, var, true);
		}
		for (CustomOperation c : allOperations) {
			addOperationToEnvironment(c);
		}
	}

	private static List<CustomOperation> getAllOperations() {
		List<CustomOperation> result = new LinkedList<CustomOperation>();
		if (Platform.isRunning()) {
			IConfigurationElement[] extensions = Platform
					.getExtensionRegistry().getConfigurationElementsFor(
							Activator.PLUGIN_ID, EXT_ID);
			for (IConfigurationElement c : extensions) {
				try {
					CustomOperation op = (CustomOperation) c
							.createExecutableExtension("instance");
					result.add(op);
				} catch (CoreException e) {
					e.printStackTrace();
				}

			}
		}
		return result;
	}

	private static void addOperationToEnvironment(CustomOperation operation) {
		if (operation != null && operation.getName() != null
				&& !operation.getName().isEmpty()) {
			UMLEnvironment umlEnvironment = (UMLEnvironment) ocl
					.getEnvironment();
			umlEnvironment.defineOperation(operation.getClassifier(), operation
					.getName(), operation.getType(), operation.getParameters(),
					org.eclipse.uml2.uml.UMLFactory.eINSTANCE
							.createConstraint());
		}
	}
}
