package org.eclipse.uml.a2acsl.utils;

import org.eclipse.uml2.uml.Type;

/**
 * Contains static helpers for ACSL
 * 
 * @author A560169
 * 
 */
public class AcslUtils {

	/**
	 * In the model, an INOUT parameter x is described by two parameters x_in,
	 * x_out corresponding to the pointer x in ACSL. This method returns the
	 * ACSL equivalent name.
	 * 
	 * @param name
	 * @return
	 */
	public static String getACSLName(String name) {
		int l = name.length();
		if (AcslUtils.isIn(name))
			return name.substring(0, l - 3);
		if (AcslUtils.isOut(name))
			return name.substring(0, l - 4);
		return name;
	}

	/**
	 * Checks if the name corresponds to the IN part of an INOUT parameter i.e.
	 * ends by '_in'
	 * 
	 * @param inout
	 * @return
	 */
	public static Boolean isIn(String inout) {
		int l = 0;
		if (inout != null) {
			l = inout.length();
		}
		return (inout != null && l > 3 && inout.contains("_in") && (inout
				.substring(l - 3, l).equals("_in")));
	}

	/**
	 * Checks if the name corresponds to the OUT part of an INOUT parameter i.e.
	 * ends by '_out'
	 * 
	 * @param inout
	 * @return
	 */
	public static Boolean isOut(String inout) {
		int l = 0;
		if (inout != null) {
			l = inout.length();
		}
		return (inout != null && l > 4 && inout.contains("_out") && (inout
				.substring(l - 4, l).equals("_out")));
	}

	/**
	 * Translates the name of a UML type to its C equivalent.
	 * 
	 * @param type
	 * @return
	 */
	public static String ocl2CType(Type type) {
		String typeName = type.getName();
		if (typeName.equals("Integer") || typeName.equals("Boolean"))
			return "int";
		if (typeName.equals("Real"))
			return "float";
		return typeName;
	}

}
