package org.eclipse.uml.a2acsl.ocl2acsl;

import java.util.ArrayList;

import org.eclipse.uml.a2acsl.utils.AcslUtils;
import org.eclipse.uml.a2acsl.utils.ModelUtils;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Parameter;
import org.eclipse.uml2.uml.ParameterDirectionKind;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Type;

/**
 * Contains methods for C syntax generation
 * 
 * @author A560169
 * 
 */
class CGenerator {

	/**
	 * Generates a C structure describing the operation context
	 * 
	 * @param operation
	 * @return
	 */
	public static String generateOperationContextStruct(Operation operation) {
		String callee = operation.getName();
		String result = "typedef struct _" + callee + "_context {\n";
		result += generateInStruct(operation);
		result += generateOutStruct(operation);
		result += generateResultField(operation);
		result += "}" + callee + "_context;";
		return result;
	}

	/**
	 * Generates a C structure describing the operation's IN parameters
	 * 
	 * @param operation
	 * @return
	 */
	public static String generateInStruct(Operation operation) {
		String result = "";
		ArrayList<Property> properties = ModelUtils.getSideEffects(operation);
		ArrayList<Parameter> inParams = ModelUtils.getParameters(operation,
				ParameterDirectionKind.IN_LITERAL);
		if (inParams.size() != 0 || properties.size() != 0) {
			result = "	struct{\n";
		}
		for (Parameter p : inParams) {
			result += "		" + generateDeclaration(p, false) + ";\n";
		}
		for (Property p : properties) {
			result += "		" + generateDeclaration(p) + ";\n";
		}
		if (inParams.size() != 0 || properties.size() != 0) {
			result += "	} _in;\n";
		}
		return result;
	}

	/**
	 * Generates a C structure describing the operation's OUT parameters
	 * 
	 * @param operation
	 * @return
	 */
	public static String generateOutStruct(Operation operation) {
		String result = "";
		ArrayList<Property> properties = ModelUtils.getSideEffects(operation);
		ArrayList<Parameter> outParams = ModelUtils.getParameters(operation,
				ParameterDirectionKind.OUT_LITERAL);
		if (outParams.size() != 0 || properties.size() != 0) {
			result = "	struct{\n";
		}
		for (Parameter p : outParams) {
			result += "		" + generateDeclaration(p, false) + ";\n";
		}
		for (Property p : properties) {
			result += "		" + generateDeclaration(p) + ";\n";
		}
		if (outParams.size() != 0 || properties.size() != 0) {
			result += "	} _out;\n";
		}
		return result;
	}

	/**
	 * Generates a C declaration for the return parameter
	 * 
	 * @param operation
	 * @return
	 */
	public static String generateResultField(Operation operation) {
		String result = "";
		Parameter p = operation.getReturnResult();
		if (p != null) {
			result += "		" + generateDeclaration(p, false) + ";\n";
		}
		return result;
	}

	/**
	 * Generates a C declaration for the specified parameter
	 * 
	 * @param param
	 * @param isPointer
	 * @return
	 */
	private static String generateDeclaration(Parameter param, boolean isPointer) {
		String name = "";
		if (param.getDirection() == ParameterDirectionKind.RETURN_LITERAL) {
			name = "result";
		} else {
			name = AcslUtils.getACSLName(param.getName());
		}
		Type type = param.getType();
		String cType = AcslUtils.ocl2CType(type);
		if (ModelUtils.isConstantCollection(param)) {
			String size = ModelUtils.getElementSize(param);
			return cType + " " + name + "[" + size + "]";
		}
		return cType + (isPointer ? "* " : " ") + name;
	}

	/**
	 * Generates a C declaration for the specified property
	 * 
	 * @param property
	 * @return
	 */
	private static String generateDeclaration(Property property) {
		String name = property.getName();
		Type type = property.getType();
		String cType = AcslUtils.ocl2CType(type);
		if (ModelUtils.isConstantCollection(property)) {
			String size = ModelUtils.getElementSize(property);
			return cType + " " + name + "[" + size + "]";
		}
		return cType + " " + name;
	}
}
