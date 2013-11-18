package org.eclipse.uml.a2acsl.oclgeneration;

import java.util.ArrayList;

import org.eclipse.emf.common.util.EList;
import org.eclipse.uml.a2acsl.utils.ModelUtils;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Parameter;
import org.eclipse.uml2.uml.ParameterDirectionKind;

/**
 * Generates OCL syntax
 * 
 * @author A560169
 * 
 */
public class OclGenerator {

	/**
	 * Generates an equality
	 * 
	 * @param left
	 * @param right
	 * @return
	 */
	public String generateEquality(String left, String right) {
		return left + " = " + right;
	}

	/**
	 * Generates the expression corresponding to an IN field of a context
	 * observer: name->at(counter)._in.param
	 * 
	 * @param param
	 * @param name
	 * @param counter
	 * @return
	 */
	public String generateIn(String param, String name, String counter) {
		return "(" + generateAt(name, counter) + ")._in." + param;
	}

	/**
	 * Generates the expression corresponding to the result field of a context
	 * observer : name->at(counter).result
	 * 
	 * @param name
	 * @param counter
	 * @return
	 */
	public String generateResult(String name, String counter) {
		return "(" + generateAt(name, counter) + ").result";
	}

	/**
	 * Generates the expression corresponding to an OT field of a context
	 * observer: name->at(counter)._out.param
	 * 
	 * @param param
	 * @param name
	 * @param counter
	 * @return
	 */
	public String generateOut(String param, String name, String counter) {
		return "(" + generateAt(name, counter) + ")._out." + param;
	}

	/**
	 * Generates the at OCL operator
	 * 
	 * @param name
	 * @param counter
	 * @return
	 */
	public String generateAt(String name, String counter) {
		return name + "->at(" + counter + ")";
	}

	/**
	 * Generates a list of let definitions corresponding to the specified list
	 * of variables
	 * 
	 * @param definitions
	 * @return
	 */
	public String generateDefinitions(ArrayList<VariableDefinition> definitions) {
		StringBuffer result = new StringBuffer();
		for (VariableDefinition def : definitions) {
			String var = def.getName();
			String type = def.getType();
			String value = def.getExpression();
			result.append(generateLet(var, type, value));
		}
		return result.toString();
	}

	/**
	 * Generates an OCL let definition
	 * 
	 * @param var
	 * @param type
	 * @param value
	 * @return
	 */
	public String generateLet(String var, String type, String value) {
		String result = "let " + var + " : " + type + " = " + value + " in ";
		return result;
	}

	/**
	 * Generates the insertAt OCL operator
	 * 
	 * @param tab
	 * @param index
	 * @param value
	 * @return
	 */
	public String generateInsertAt(String tab, String index, String value) {
		return tab + " -> insertAt( " + index + ", " + value + ")";
	}

	private String generateSetter(String variable, String feature,
			String value, String type) {
		String setter = "ocl_set(" + variable + ", " + "'" + feature + "'"
				+ ", " + value + ")";
		String var = variable.replaceAll(" ", "");
		String at = "->at(";
		if (var.contains(at)) {
			int indAt = var.indexOf(at);
			String index = var.substring(indAt + 5, var.length() - 1);
			return generateInsertAt(var.substring(0, indAt), index, setter
					+ ".oclAsType(" + type + ")");
		} else {
			return setter;
		}
	}

	/**
	 * For source parts {x,y} and feature f with value v corresponding to x.y.f
	 * = v, generates the expression : ocl_set(x,'y',ocl_set(x.y,'f',v))
	 * 
	 * @param parts
	 * @param feature
	 * @param value
	 * @return
	 */
	public String generateSetters(String[] parts, String feature, String value,
			String[] partsTypes) {
		int l = parts.length;
		if (l == 1)
			return generateSetter(parts[0], feature, value, partsTypes[0]);
		;
		StringBuffer var = new StringBuffer(parts[0]);
		for (int i = 1; i < l; i++) {
			var.append("." + parts[i]);
		}
		String lastValue = generateSetter(var.toString(), feature, value,
				partsTypes[0]);
		String[] newParts = new String[l - 1];
		for (int i = 0; i < l - 1; i++) {
			newParts[i] = parts[i];
		}
		String[] newPartsTypes = new String[l - 1];
		for (int i = 0; i < l - 1; i++) {
			newPartsTypes[i] = partsTypes[i];
		}
		return generateSetters(newParts, parts[l - 1], lastValue, newPartsTypes);
	}

	/**
	 * Generates the OCL signature of the specified operation
	 * 
	 * @param operation
	 * @return
	 */
	public String generateOperationSignature(Operation operation) {
		StringBuffer signature = new StringBuffer(operation.getClass_()
				.getName() + "::" + operation.getName() + "(");
		EList<Parameter> params = operation.getOwnedParameters();
		int nbParams = 0;
		for (Parameter p : params) {
			if (p.getDirection() != ParameterDirectionKind.RETURN_LITERAL) {
				signature.append(p.getName() + ":"
						+ ModelUtils.getType(p).getName() + ",");
				nbParams++;
			}
		}
		if (nbParams != 0) {
			signature = new StringBuffer(signature.substring(0,
					signature.length() - 1));
		}
		signature.append(")");
		return signature.toString();
	}

	/**
	 * Generates the subSequence Ocl operator
	 * 
	 * @param col
	 * @param index
	 * @return
	 */
	public String generatesSubSequence(String col, String index) {
		return col + " -> subSequence(0," + index + ")";
	}

	/**
	 * Generates context observers' name
	 * 
	 * @param caller
	 * @param callee
	 * @param owner
	 * @return
	 */
	public String generateContexts(String caller, String callee, String owner) {
		return caller + "_" + callee + "_context";
	}

	/**
	 * Generates context observers's call counter
	 * 
	 * @param caller
	 * @param callee
	 * @param owner
	 * @return
	 */
	public String generateCallCounter(String caller, String callee, String owner) {
		return "size_" + caller + "_" + callee + "_context";
	}

	/**
	 * Generates the expression corresponding to the IN structure of a context
	 * observer: name->at(counter)._in
	 * 
	 * @param contexts
	 * @param size
	 * @return
	 */
	public String generateIn(String contexts, String size) {
		return "(" + generateAt(contexts, size) + ")._in";
	}
}
