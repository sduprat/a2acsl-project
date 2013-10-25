package org.eclipse.uml.a2acsl.ocl2acsl;

import java.util.ArrayList;

import org.eclipse.ocl.expressions.IteratorExp;
import org.eclipse.ocl.expressions.OCLExpression;
import org.eclipse.ocl.expressions.OperationCallExp;
import org.eclipse.ocl.expressions.PropertyCallExp;
import org.eclipse.ocl.expressions.StringLiteralExp;
import org.eclipse.ocl.expressions.VariableExp;
import org.eclipse.ocl.uml.SequenceType;
import org.eclipse.ocl2acsl.OCLVisitor;
import org.eclipse.uml.a2acsl.parsing.ModelParser;
import org.eclipse.uml.a2acsl.utils.AcslUtils;
import org.eclipse.uml.a2acsl.utils.ModelUtils;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.DataType;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Parameter;
import org.eclipse.uml2.uml.ParameterDirectionKind;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Type;

/**
 * Completes OCL2ACSL to take into account some application-specific cases
 * 
 * @author A560169
 * 
 */
public class Ocl2AcslVisitor extends OCLVisitor {

	private boolean inPreState;

	public void setInPreState(boolean inPreState) {
		this.inPreState = inPreState;
	}

	@Override
	public String visitOperationCallExp(
			OperationCallExp<Classifier, Operation> callExp) {
		// Translates the insertAt OCL operator to the ACSL array modifier \with
		String opName = callExp.getReferredOperation().getName();
		OCLExpression<Classifier> source = callExp.getSource();
		if (opName.equals("insertAt")) {
			OCLExpression<Classifier> indexArg = callExp.getArgument().get(0);
			OCLExpression<Classifier> valueArg = callExp.getArgument().get(1);
			Type type = valueArg.getType();
			String cType;
			if (type instanceof SequenceType) {
				cType = AcslUtils.ocl2CType(((SequenceType) type)
						.getElementType());
				String size = getSizeParamName(valueArg);
				cType = cType + "[" + size + "]";
			} else {
				cType = AcslUtils.ocl2CType(type);
			}
			return "{" + source.accept(this) + " \\with " + "["
					+ indexArg.accept(this) + "]" + " = (" + cType + ") ("
					+ valueArg.accept(this) + " ) }";
		}
		// Translates the subsequence OCL operator to an array range array[i..n]
		if (opName.equals("subSequence")) {
			String tab = source.accept(this);
			String bInd = callExp.getArgument().get(0).accept(this);
			String eInd = callExp.getArgument().get(1).accept(this);
			return tab + "[" + bInd + ".. " + eInd + "]";
		}
		// Translates setter to ACSL field modifier \with
		if (opName.equals("ocl_set")) {
			OCLExpression<Classifier> objArg = callExp.getArgument().get(0);
			StringLiteralExp<Classifier> featureArg = (StringLiteralExp<Classifier>) callExp
					.getArgument().get(1);
			OCLExpression<Classifier> valueArg = callExp.getArgument().get(2);
			Type type = valueArg.getType();
			String cType;
			if (type instanceof SequenceType) {
				cType = AcslUtils.ocl2CType(((SequenceType) type)
						.getElementType());
				String size = getSizeParamName(valueArg);
				cType = cType + "[" + size + "]";
			} else {
				cType = AcslUtils.ocl2CType(type);
			}
			return "{ " + objArg.accept(this) + " \\with " + "."
					+ featureArg.getStringSymbol() + " = (" + cType + ")" + "("
					+ valueArg.accept(this) + ")}";
		}
		return super.visitOperationCallExp(callExp);
	}

	@Override
	public String visitPropertyCallExp(
			PropertyCallExp<Classifier, Property> callExp) {
		OCLExpression<Classifier> sourceExp = callExp.getSource();
		Property property = callExp.getReferredProperty();
		Type sourceType = callExp.getSource().getType();
		if (sourceType.getName().equals(property.getName())
				&& callExp.getType() instanceof SequenceType) {
			if (sourceType instanceof DataType) {
				DataType dataType = (DataType) sourceType;
				if (dataType.getOwnedAttributes().size() == 1) {
					String sourceRes = callExp.getSource().accept(this);
					return maybeAtPre(callExp, sourceRes);
				}
			}
		}
		if (ModelUtils.isGeneratedType(sourceExp.getType()))
			return sourceExp.accept(this) + "."
					+ AcslUtils.getACSLName(property.getName());
		if (inPreState) {
			callExp.setMarkedPre(false);
		}
		return super.visitPropertyCallExp(callExp);
	}

	@Override
	public String visitVariableExp(VariableExp<Classifier, Parameter> exp) {
		Parameter p = exp.getReferredVariable().getRepresentedParameter();
		// Translates INOUT parameters to references to corresponding pointer
		if (p != null
				&& (AcslUtils.isIn(p.getName()) || AcslUtils.isOut(p.getName()))) {
			String name = p.getName();
			String acslName = AcslUtils.getACSLName(name);
			if (!ModelUtils.isConstantCollection(p)) {
				acslName = "*" + acslName;
			}
			if (AcslUtils.isIn(name) && !inPreState) {
				acslName = "\\old(" + acslName + ")";
			}
			return acslName;
		}
		return super.visitVariableExp(exp);
	}

	@Override
	public String visitEqualNonEqualCall(
			OperationCallExp<Classifier, Operation> callExp, String oper) {
		OCLExpression<Classifier> array1 = callExp.getSource();
		OCLExpression<Classifier> array2 = callExp.getArgument().get(0);
		Boolean equals = oper.equals("==");
		String tab1 = array1.accept(this);
		String tab2 = array2.accept(this);
		tab1 = maybePointerProperty(array1, tab1);
		tab2 = maybePointerProperty(array2, tab2);
		return tab1 + (equals ? " == " : " != ") + tab2;
	}

	@SuppressWarnings("unchecked")
	public String getSizeParamName(OCLExpression<Classifier> array) {
		// insertAt returns a sequence that has the same size as its source
		if (array instanceof OperationCallExp
				&& ((OperationCallExp<Classifier, Operation>) array)
						.getReferredOperation().getName().equals("insertAt")) {
			OCLExpression<Classifier> source = ((OperationCallExp<Classifier, Operation>) array)
					.getSource();
			return getSizeParamName(source);
		}
		if (array instanceof VariableExp) {
			// Result keyword
			if (array.getName().equals("result")) {
				Operation op = Ocl2Acsl.getContext();
				return op.getReturnResult().getLower() + "";
			}
			Parameter p = ((VariableExp<Classifier, Parameter>) array)
					.getReferredVariable().getRepresentedParameter();
			if (p == null) {
				// Local variable -> size of its initialization
				OCLExpression<Classifier> init = ((VariableExp<Classifier, ?>) array)
						.getReferredVariable().getInitExpression();
				return getSizeParamName(init);
			} else {
				// Parameter
				if (ModelUtils.isConstantCollection(p))
					return ModelUtils.getElementSize(p);
			}

		}
		if (array instanceof PropertyCallExp) {
			OCLExpression<Classifier> propSource = ((PropertyCallExp<Classifier, ?>) array)
					.getSource();
			// Array of observers
			if (ModelUtils.isGeneratedType(propSource.getType())) {
				String propName = ((PropertyCallExp<Classifier, Property>) array)
						.getReferredProperty().getName();
				String opName = array.toString().split("_")[1];
				Operation op = ModelParser.getOperation(opName);
				for (Parameter p : op.getOwnedParameters()) {
					if (p.getName().equals(propName)
							|| (p.getDirection() == ParameterDirectionKind.RETURN_LITERAL && propName
									.equals("result")))
						return ModelUtils.getElementSize(p);
				}
				ArrayList<Property> sideEffects = ModelUtils.getSideEffects(op);
				for (Property p : sideEffects) {
					if (p.getName().equals(propName))
						return ModelUtils.getElementSize(p);
				}
			}
		}
		return super.getSizeParamName(array);
	}
	
	public String visitIteratorExp(IteratorExp<Classifier, Parameter> exp) {
		if (exp.getName().equals("collect")){
			return exp.getSource().accept(this);
		}
		return super.visitIteratorExp(exp);
	}
}
