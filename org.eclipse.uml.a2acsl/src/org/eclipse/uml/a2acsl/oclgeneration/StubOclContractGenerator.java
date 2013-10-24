package org.eclipse.uml.a2acsl.oclgeneration;

import java.util.ArrayList;

import org.eclipse.uml.a2acsl.oclcontracts.OclContract;
import org.eclipse.uml.a2acsl.utils.ModelUtils;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Parameter;
import org.eclipse.uml2.uml.ParameterDirectionKind;
import org.eclipse.uml2.uml.Property;

/**
 * Contains methods for stub contracts generation
 * 
 * @author A560169
 * 
 */
public class StubOclContractGenerator {

	protected static OclGenerator generator;

	/**
	 * Initialize the stub generator with specified instance of
	 * {@link OclGenerator}
	 * 
	 * @param generator
	 */
	public void initialize(OclGenerator generator) {
		StubOclContractGenerator.generator = generator;
	}

	/**
	 * Generates a stub contract for the specified operation
	 * 
	 * @param operation
	 * @param caller
	 * @return
	 */
	public OclContract generateStubContract(Operation operation,
			Operation caller) {
		OclContract contract = new OclContract("", operation);
		String opContext = caller.getName();
		String opName = operation.getName();
		String owner = operation.getClass_().getQualifiedName();
		String contexts = generator.generateContexts(opContext, opName, owner);
		String size = generator.generateCallCounter(opContext, opName, owner);

		ArrayList<Parameter> inParams = ModelUtils.getParameters(operation,
				ParameterDirectionKind.IN_LITERAL);
		for (Parameter param : inParams) {
			String paramName = param.getName();
			String postcondition = generator.generateEquality(
					generator.generateIn(paramName, contexts, size + "@pre"),
					paramName);
			contract.addPostcondition(postcondition);
		}

		ArrayList<Parameter> outParams = ModelUtils.getParameters(operation,
				ParameterDirectionKind.OUT_LITERAL);
		for (Parameter param : outParams) {
			String paramName = param.getName();
			String postcondition = generator.generateEquality(
					generator.generateOut(paramName, contexts, size + "@pre"),
					paramName);
			contract.addPostcondition(postcondition);
			if (ModelUtils.isConstantCollection(param)) {
				String colSize = ModelUtils.getElementSize(param);
				contract.addAssigns(paramName + "-> subSequence(0," + colSize
						+ "-1)");
			} else {
				contract.addAssigns(paramName);
			}
		}

		Parameter result = operation.getReturnResult();
		if (result != null) {
			String postcondition = generator
					.generateEquality(
							generator.generateResult(contexts, size + "@pre"),
							"result");
			contract.addPostcondition(postcondition);
		}

		ArrayList<Property> properties = ModelUtils.getSideEffects(operation);
		for (Property p : properties) {
			String propName = p.getName();
			String postcondition = generator.generateEquality(
					generator.generateIn(propName, contexts, size + "@pre"),
					propName + "@pre");
			contract.addPostcondition(postcondition);
			postcondition = generator.generateEquality(
					generator.generateOut(propName, contexts, size + "@pre"),
					propName);
			contract.addPostcondition(postcondition);
			if (ModelUtils.isConstantCollection(p)) {
				String colSize = ModelUtils.getElementSize(p);
				contract.addAssigns(generator.generatesSubSequence(propName,
						colSize + "-1"));
			} else {
				contract.addAssigns(propName);
			}
		}

		contract.addAssigns(size);
		if (inParams.size() != 0 || properties.size() != 0) {
			contract.addAssigns(generator.generateIn(contexts, size));
		}
		contract.addPostcondition(generator.generateEquality(size, size
				+ "@pre + 1"));
		return contract;
	}
}
