package org.eclipse.uml.a2acsl.ocl2acsl;

import java.util.ArrayList;

import org.eclipse.ocl.ParserException;
import org.eclipse.uml.a2acsl.oclcontracts.GlobalOclContract;
import org.eclipse.uml.a2acsl.oclcontracts.OclContract;
import org.eclipse.uml2.uml.Activity;
import org.eclipse.uml2.uml.Operation;

/**
 * Contains methods for ACSL annotations generation
 * 
 * @author A560169
 * 
 */
public class AcslGenerator {

	/**
	 * Translates given OCL contract to ACSL annotations
	 * 
	 * @param contract
	 * @param activity
	 * @param calledOperations
	 * @return
	 * @throws ParserException
	 */
	public String generateFunctionContract(GlobalOclContract contract,
			Activity activity, ArrayList<Operation> calledOperations)
			throws ParserException {
		StringBuffer result = new StringBuffer();
		ArrayList<OclContract> behaviors = contract.getBehaviors();
		result.append("/* ========== Function Contract ========== */\n/*@\n");
		// For each beahvior, translates ocl contract to acsl
		for (OclContract behaviorContract : behaviors) {
			result.append(oclContractToACSL(behaviorContract));
		}
		result.append("*/\n\n");
		return result.toString();
	}

	/**
	 * Translates stub OCL contracts to ACSL annotations
	 * 
	 * @param contract
	 * @param caller
	 * @return
	 * @throws ParserException
	 */
	public String generateStubs(GlobalOclContract contract, Operation caller)
			throws ParserException {
		StringBuffer result = new StringBuffer();
		ArrayList<OclContract> stubs = contract.getStubs();
		for (OclContract stub : stubs) {
			Operation operation = stub.getContext();
			String opName = operation.getName();
			result.append("/* ========== Annotations for called operation "
					+ opName + " ========== */\n");
			// Generates C structures describing observers
			result.append("/* ========== Definition of observers structure ========== */\n"
					+ CGenerator.generateOperationContextStruct(operation)
					+ "\n\n");
			// Genrates ghost instructions for observers declarations
			result.append("/* ========== Declaration of context observers ========== */\n"
					+ generateGhostInstructions(caller.getName(), opName,
							contract.getMaxNbCall(opName)) + "\n");
			// Translates stub annotations
			result.append("/* ========== Stub annotations ========== */\n"
					+ translateStubAnnotations(stub) + "\n\n");
		}
		return result.toString();
	}

	private String generateGhostInstructions(String caller, String callee,
			int nbCalls) {
		StringBuffer result = new StringBuffer();
		result.append("/*@ ghost int size_" + caller + "_" + callee
				+ "_context;*/\n");
		result.append("/*@ ghost " + callee + "_context " + caller + "_"
				+ callee + "_context[" + nbCalls + "];*/\n");
		return result.toString();
	}

	protected String translateStubAnnotations(OclContract stub)
			throws ParserException {
		String stubAcsl = oclContractToACSL(stub);
		return "/*@\n" + stubAcsl + "*/";
	}

	protected String oclContractToACSL(OclContract contract)
			throws ParserException {
		String name = contract.getName();
		StringBuffer result = new StringBuffer(name.isEmpty() ? ""
				: "behavior " + name + ":\n");
		if (contract.getAssigns().size() != 0) {
			result.append("	assigns ");
		}
		for (String assign : contract.getAssigns()) {
			String translation = Ocl2Acsl.oclExpression2acsl(assign,
					contract.getContext(), true);
			result.append(translation + ", ");
		}
		if (contract.getAssigns().size() != 0) {
			result = new StringBuffer(result.substring(0, result.length() - 2)
					+ ";\n");
		}
		for (String assumption : contract.getAssumptions()) {
			String translation = Ocl2Acsl.oclConstraint2acsl(assumption,
					contract.getContext(), true);
			result.append("	assumes " + translation + ";\n");
		}
		for (String precondition : contract.getPreconditions()) {
			String translation = Ocl2Acsl.oclConstraint2acsl(precondition,
					contract.getContext(), true);
			result.append("	requires " + translation + ";\n");
		}
		for (String postcondition : contract.getPostconditions()) {
			String translation = Ocl2Acsl.oclConstraint2acsl(postcondition,
					contract.getContext(), false);
			result.append("	ensures " + translation + ";\n");
		}
		return result.toString();
	}
}
