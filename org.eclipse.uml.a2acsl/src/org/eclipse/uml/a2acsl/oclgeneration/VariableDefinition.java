package org.eclipse.uml.a2acsl.oclgeneration;

/**
 * Represents a definition of a variable : an identifier, a type and an
 * initialization expression
 * 
 * @author A560169
 * 
 */
public class VariableDefinition {

	private String name;
	private String type;
	private String expression;

	/**
	 * Instantiates a new definition
	 * 
	 * @param name
	 * @param type
	 * @param expression
	 */
	public VariableDefinition(String name, String type, String expression) {
		super();
		this.name = name;
		this.type = type;
		this.expression = expression;
	}

	/**
	 * Returns the variable name
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the variable type
	 * 
	 * @return
	 */
	public String getType() {
		return type;
	}

	/**
	 * Returns the variable initialization expression
	 * 
	 * @return
	 */
	public String getExpression() {
		return expression;
	}

	@Override
	public String toString() {
		return name + " " + type + " " + expression;
	}
}
