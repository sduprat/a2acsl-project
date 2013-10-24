package org.eclipse.uml.a2acsl.oclcontracts;

import java.util.ArrayList;

import org.eclipse.uml.a2acsl.oclgeneration.OclGenerator;
import org.eclipse.uml2.uml.Operation;

/**
 * Describes an OCL contract
 * 
 * @author A560169
 * 
 */
public class OclContract {

	private String name; // Behavior name if describing a behavior
	private ArrayList<String> assumptions; // Assumptions for behavior execution
	private ArrayList<String> preconditions; // Preconditions for behavior
												// execution
	private ArrayList<String> postconditions; // Postcondition of behavior
												// execution
	private ArrayList<String> assigns; // Memory locations assigned by the
										// behavior
	private Operation context; // Context operation

	/**
	 * Initializes an OCL contract with the specified name and context
	 * 
	 * @param name
	 * @param context
	 */
	public OclContract(String name, Operation context) {
		this.name = name;
		this.context = context;
		assumptions = new ArrayList<String>();
		preconditions = new ArrayList<String>();
		postconditions = new ArrayList<String>();
		assigns = new ArrayList<String>();
	}

	/**
	 * Adds an assumption to the contract
	 * 
	 * @param assumption
	 */
	public void addAssumption(String assumption) {
		assumptions.add(assumption);
	}

	/**
	 * Adds a precondition to the contract
	 * 
	 * @param precondition
	 */
	public void addPrecondition(String precondition) {
		preconditions.add(precondition);
	}

	/**
	 * Adds a postcondition to the contract
	 * 
	 * @param postcondition
	 */
	public void addPostcondition(String postcondition) {
		postconditions.add(postcondition);
	}

	/**
	 * Adds an assigned variable to the contract
	 * 
	 * @param assign
	 */
	public void addAssigns(String assign) {
		assigns.add(assign);
	}

	/**
	 * Returns behavior name
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns contract assumptions
	 * 
	 * @return
	 */
	public ArrayList<String> getAssumptions() {
		return assumptions;
	}

	/**
	 * Returns contract preconditions
	 * 
	 * @return
	 */
	public ArrayList<String> getPreconditions() {
		return preconditions;
	}

	/**
	 * Returns contract postconditions
	 * 
	 * @return
	 */
	public ArrayList<String> getPostconditions() {
		return postconditions;
	}

	@Override
	public String toString() {
		String result = name.isEmpty() ? "--Called Operation Contract\n"
				: "--Behavior :" + name + "\n";
		String signature = (new OclGenerator())
				.generateOperationSignature(context);
		result += "context " + signature + "\n";
		if (assigns.size() != 0) {
			result += "--Modifies :";
			for (String assign : assigns) {
				result += assign + ", ";
			}
			result = result.substring(0, result.length() - 2) + "\n";
		}
		if (preconditions.size() != 0) {
			result += "pre:\n";
			if (assumptions.size() != 0) {
				for (String assumption : assumptions) {
					result += "  " + assumption + " and\n";
				}
				result = result.substring(0, result.length() - 5)
						+ " implies\n(\n";
			}
			for (String precondition : preconditions) {
				result += "  " + precondition + " and\n";
			}
			result = result.substring(0, result.length() - 5) + "\n";
			if (assumptions.size() != 0) {
				result += ")\n";
			}
		}
		if (postconditions.size() != 0) {
			result += "post:\n";
			if (assumptions.size() != 0) {
				for (String assumption : assumptions) {
					result += "  " + assumption + " and\n";
				}
				result = result.substring(0, result.length() - 5)
						+ " implies\n(\n";
			}
			for (String postcondition : postconditions) {
				result += "  " + postcondition + " and\n";
			}
			result = result.substring(0, result.length() - 5) + "\n";
			if (assumptions.size() != 0) {
				result += ")\n";
			}
		}
		return result + "\n";
	}

	/**
	 * Returns contract context
	 * 
	 * @return
	 */
	public Operation getContext() {
		return context;
	}

	/**
	 * Returns contract assigned variables
	 * 
	 * @return
	 */
	public ArrayList<String> getAssigns() {
		return assigns;
	}

	/**
	 * Removes the last added precondition
	 */
	public void removeLastPrecondition() {
		preconditions.remove(preconditions.size() - 1);
	}
}
