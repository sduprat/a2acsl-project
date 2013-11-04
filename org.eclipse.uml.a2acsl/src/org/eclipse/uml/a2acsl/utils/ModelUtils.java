package org.eclipse.uml.a2acsl.utils;

import java.util.ArrayList;

import org.eclipse.ocl.uml.SequenceType;
import org.eclipse.ocl.uml.UMLFactory;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.InterfaceRealization;
import org.eclipse.uml2.uml.MultiplicityElement;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Parameter;
import org.eclipse.uml2.uml.ParameterDirectionKind;
import org.eclipse.uml2.uml.PrimitiveType;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.TypedElement;

/**
 * Contains static helpers on model objects
 * 
 * @author A560169
 * 
 */
public class ModelUtils {

	private static UMLFactory umlFact = UMLFactory.eINSTANCE;

	/**
	 * Returns true if the element is a constant collection i.e. has equal
	 * bounds that are not set to 1
	 * 
	 * @param element
	 *            :
	 * @return
	 */
	public static boolean isConstantCollection(MultiplicityElement element) {
		return element.getLower() == element.getUpper()
				&& element.getLower() != 1;
	}

	/**
	 * Returns the size of a collection
	 * 
	 * @param element
	 * @return
	 */
	public static String getElementSize(MultiplicityElement element) {
		return element.getLower() + "";
	}

	/**
	 * Updates the first element's properties with those of the second element
	 * 
	 * @param newElement
	 * @param element
	 */
	public static void setProperties(MultiplicityElement newElement,
			MultiplicityElement element) {
		newElement.setLower(element.getLower());
		newElement.setUpper(element.getUpper());
		newElement.setIsOrdered(element.isOrdered());
		newElement.setIsUnique(element.isUnique());
		if (newElement instanceof Property) {
			((Property) newElement).setIsReadOnly(((Property) element)
					.isReadOnly());
		}
	}

	/**
	 * Sets properties of an element with the specified values
	 * 
	 * @param newElement
	 * @param element
	 */
	public static void setProperties(MultiplicityElement element,
			boolean isUnique, boolean isOrdered, int lower, int upper) {
		element.setIsUnique(isUnique);
		element.setIsOrdered(isOrdered);
		element.setLower(lower);
		element.setUpper(upper);
	}

	/**
	 * Returns the type of an element : If the element is a collection, returns
	 * a {@link SequenceType}, otherwise it returns its UML type
	 * 
	 * @param element
	 * @return
	 */
	public static Type getType(TypedElement element) {
		Type type = element.getType();
		if (element instanceof MultiplicityElement) {
			MultiplicityElement mElement = (MultiplicityElement) element;
			if (mElement.getLower() != mElement.getUpper()
					|| mElement.getLower() != 1) {
				SequenceType seqType = umlFact.createSequenceType();
				seqType.setElementType((Classifier) type);
				type = seqType;
			}
		}
		return type;
	}

	/**
	 * Returns the complete name of given type
	 * 
	 * @param type
	 * @return
	 */
	public static String getTypeName(Type type) {
		String typeName;
		if (type instanceof PrimitiveType)
			typeName = type.getName();
		else if (type instanceof SequenceType) {
			Type elementType = ((SequenceType) type).getElementType();
			typeName = "Sequence( " + getTypeName(elementType) + " )";
		} else {
			typeName = ((NamedElement) type.getOwner()).getName() + "::"
					+ type.getName();
		}
		return typeName;

	}

	/**
	 * Returns true if the type belongs to the original model
	 * 
	 * @param type
	 * @return
	 */
	public static Boolean isGeneratedType(Type type) {
		Boolean isGenerated = false;
		if (type instanceof org.eclipse.uml2.uml.Class) {
			org.eclipse.uml2.uml.Class _class = (org.eclipse.uml2.uml.Class) type;
			for (InterfaceRealization realization : _class
					.getInterfaceRealizations()) {
				String name = realization.getContract().getName();
				if (name.equals("In") || name.equals("Out")
						|| name.equals("OperationContext")) {
					isGenerated = true;
				}
			}
		}
		return isGenerated;
	}

	/**
	 * Returns all parameters of the specified operation that have the specified
	 * direction
	 * 
	 * @param operation
	 * @param direction
	 * @return
	 */
	public static ArrayList<Parameter> getParameters(Operation operation,
			ParameterDirectionKind direction) {
		ArrayList<Parameter> params = new ArrayList<Parameter>();
		for (Parameter p : operation.getOwnedParameters()) {
			if (p.getDirection() == direction) {
				params.add(p);
			}
		}
		return params;
	}

	/**
	 * Returns the list of properties concerned by the operation side effects
	 * 
	 * @param operation
	 * @return
	 */
	public static ArrayList<Property> getSideEffects(Operation operation) {
		ArrayList<Property> effects = new ArrayList<Property>();
		for (Constraint cons : operation.getOwnedRules()) {
			for (Element element : cons.getConstrainedElements()) {
				if (element instanceof Property)
					effects.add((Property) element);
			}
		}
		return effects;
	}
}
