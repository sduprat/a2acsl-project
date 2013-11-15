package org.eclipse.uml.a2acsl.oclcontracts;

import java.util.ArrayList;
import java.util.HashMap;

// Describes a global ocl contract = behavior contracts and stub contracts
/**
 * Describes a global OCL contract that is a list of behavior contracts and a
 * list of stub contracts
 * 
 * @author A560169
 * 
 */
public class GlobalOclContract {

	private ArrayList<OclContract> stubs; // Stub Ocl contracts
	private ArrayList<OclContract> behaviors; // Behavior Ocl contracts
	private HashMap<String, Integer> maxNbCalls; // For each called operation,
													// keeps the maximal number
													// of calls in a behavior

	/**
	 * Creates a new contract
	 */
	public GlobalOclContract() {
		stubs = new ArrayList<OclContract>();
		behaviors = new ArrayList<OclContract>();
		maxNbCalls = new HashMap<String, Integer>();
	}

	/**
	 * Adds a stub contract
	 * 
	 * @param stubContract
	 */
	public void addStubContract(OclContract stubContract) {
		stubs.add(stubContract);
	}

	/**
	 * Adds a behavior contract
	 * 
	 * @param behaviorContract
	 */
	public void addBehaviorContract(OclContract behaviorContract) {
		behaviors.add(behaviorContract);
	}

	/**
	 * Returns stub contracts
	 * 
	 * @return
	 */
	public ArrayList<OclContract> getStubs() {
		return stubs;
	}

	/**
	 * Returns behaviors contracts
	 * 
	 * @return
	 */
	public ArrayList<OclContract> getBehaviors() {
		return behaviors;
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		for (OclContract stub : stubs) {
			result.append(stub.toString());
		}
		for (OclContract behavior : behaviors) {
			result.append(behavior.toString());
		}
		return result.toString();
	}

	/**
	 * Sets the maximum number of time the specified operation has been called
	 * in all behaviors
	 * 
	 * @param operation
	 * @param nb
	 */
	public void setMaxNbCalls(String operation, int nb) {
		maxNbCalls.put(operation, nb);
	}

	/**
	 * Returns the maximum number of time the specified operation has been
	 * called in all behaviors
	 * 
	 * @param operation
	 * @return
	 */
	public int getMaxNbCall(String operation) {
		return maxNbCalls.get(operation);
	}

}
