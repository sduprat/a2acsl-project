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
		String result = "";
		ArrayList<OclContract> behaviors = contract.getBehaviors();
		result += "/* ========== Function Contract ========== */\n/*@\n";
		// For each beahvior, translates ocl contract to acsl
		for (OclContract behaviorContract : behaviors) {
			result += oclContractToACSL(behaviorContract);
		}
		result += "*/\n\n";
		return result;
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
		String result = "";
		ArrayList<OclContract> stubs = contract.getStubs();
		for (OclContract stub : stubs) {
			Operation operation = stub.getContext();
			String opName = operation.getName();
			result += "/* ========== Annotations for called operation "
					+ opName + " ========== */\n";
			// Generates C structures describing observers
			result += "/* ========== Definition of observers structure ========== */\n"
					+ CGenerator.generateOperationContextStruct(operation)
					+ "\n\n";
			// Genrates ghost instructions for observers declarations
			result += "/* ========== Declaration of context observers ========== */\n"
					+ generateGhostInstructions(caller.getName(), opName,
							contract.getMaxNbCall(opName)) + "\n";
			// Translates stub annotations
			result += "/* ========== Stub annotations ========== */\n"
					+ translateStubAnnotations(stub) + "\n\n";
		}
		return result;
	}

	private String generateGhostInstructions(String caller, String callee,
			int nbCalls) {
		String result = "/*@ ghost int size_" + caller + "_" + callee
				+ "_context;*/\n";
		result += "/*@ ghost " + callee + "_context " + caller + "_" + callee
				+ "_context[" + nbCalls + "];*/\n";
		return result;
	}

	protected String translateStubAnnotations(OclContract stub)
			throws ParserException {
		String stubAcsl = oclContractToACSL(stub);
		return "/*@\n" + stubAcsl + "*/";
	}

	protected String oclContractToACSL(OclContract contract)
			throws ParserException {
		String name = contract.getName();
		String result = name.isEmpty() ? "" : "behavior " + name + ":\n";
		if (contract.getAssigns().size() != 0) {
			result += "	assigns ";
		}
		for (String assign : contract.getAssigns()) {
			String translation = Ocl2Acsl.oclExpression2acsl(assign,
					contract.getContext(), true);
			result += translation + ", ";
		}
		if (contract.getAssigns().size() != 0) {
			result = result.substring(0, result.length() - 2) + ";\n";
		}
		for (String assumption : contract.getAssumptions()) {
			String translation = Ocl2Acsl.oclConstraint2acsl(assumption,
					contract.getContext(), true);
			result += "	assumes " + translation + ";\n";
		}
		for (String precondition : contract.getPreconditions()) {
			String translation = Ocl2Acsl.oclConstraint2acsl(precondition,
					contract.getContext(), true);
			result += "	requires " + translation + ";\n";
		}
		for (String postcondition : contract.getPostconditions()) {
			String translation = Ocl2Acsl.oclConstraint2acsl(postcondition,
					contract.getContext(), false);
			result += "	ensures " + translation + ";\n";
		}
		return result;
	}
}
