package org.eclipse.uml.a2acsl.oclgeneration;

import org.eclipse.uml2.uml.Type;

/**
 * Describes a variable observer that keeps track of the number of times the
 * variable has been modified
 * 
 * @author A560169
 * 
 */
public class VariableObserver {

	private Type type;
	private String name;
	private int cpt;
	private Boolean isLocal;

	/**
	 * Instantiates a new variable observer
	 * 
	 * @param type
	 * @param name
	 * @param isLocal
	 */
	public VariableObserver(Type type, String name, Boolean isLocal) {
		this.type = type;
		this.name = name;
		cpt = 0;
		this.isLocal = isLocal;
	}

	/**
	 * Increments the observer's counter
	 */
	public void incCpt() {
		cpt++;
	}

	/**
	 * Returns the observer's counter
	 * 
	 * @return
	 */
	public int getCpt() {
		return cpt;
	}

	/**
	 * Returns the observed variable type
	 * 
	 * @return
	 */
	public Type getType() {
		return type;
	}

	/**
	 * Returns the current behavior name
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns true if the variable is local, false if it is a property
	 * 
	 * @return
	 */
	public boolean isLocal() {
		return isLocal;
	}

	@Override
	public String toString() {
		return name + " " + type.getName() + " " + cpt + " " + isLocal;
	}

}
