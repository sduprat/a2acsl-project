package org.eclipse.uml.a2acsl.oclgeneration;

import org.eclipse.ocl.expressions.AssociationClassCallExp;
import org.eclipse.ocl.expressions.BooleanLiteralExp;
import org.eclipse.ocl.expressions.CollectionItem;
import org.eclipse.ocl.expressions.CollectionLiteralExp;
import org.eclipse.ocl.expressions.CollectionRange;
import org.eclipse.ocl.expressions.IntegerLiteralExp;
import org.eclipse.ocl.expressions.InvalidLiteralExp;
import org.eclipse.ocl.expressions.IterateExp;
import org.eclipse.ocl.expressions.MessageExp;
import org.eclipse.ocl.expressions.NullLiteralExp;
import org.eclipse.ocl.expressions.RealLiteralExp;
import org.eclipse.ocl.expressions.StateExp;
import org.eclipse.ocl.expressions.StringLiteralExp;
import org.eclipse.ocl.expressions.TupleLiteralExp;
import org.eclipse.ocl.expressions.TupleLiteralPart;
import org.eclipse.ocl.expressions.UnlimitedNaturalLiteralExp;
import org.eclipse.ocl.expressions.UnspecifiedValueExp;
import org.eclipse.ocl.expressions.Variable;
import org.eclipse.ocl.utilities.AbstractVisitor;
import org.eclipse.ocl.utilities.ExpressionInOCL;
import org.eclipse.uml2.uml.CallOperationAction;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.EnumerationLiteral;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Parameter;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.SendSignalAction;
import org.eclipse.uml2.uml.State;

/**
 * An abstract class implementing the methods not needed by the
 * {@link OclReplacerVisitor}
 * 
 * @author A560169
 * 
 */
abstract class OclAbstractVisitor
		extends
		AbstractVisitor<String, Classifier, Operation, Property, EnumerationLiteral, Parameter, State, CallOperationAction, SendSignalAction, Constraint> {

	@Override
	public String visitIntegerLiteralExp(IntegerLiteralExp<Classifier> exp) {
		return exp.toString();
	}

	@Override
	public String visitMessageExp(
			MessageExp<Classifier, CallOperationAction, SendSignalAction> messageExp) {
		return messageExp.toString();
	}

	@Override
	public String visitAssociationClassCallExp(
			AssociationClassCallExp<Classifier, Property> exp) {
		return exp.toString();
	}

	@Override
	public String visitBooleanLiteralExp(BooleanLiteralExp<Classifier> exp) {
		return exp.toString();
	}

	@Override
	public String visitCollectionItem(CollectionItem<Classifier> exp) {
		return exp.toString();
	}

	@Override
	public String visitCollectionLiteralExp(CollectionLiteralExp<Classifier> exp) {
		return exp.toString();
	}

	@Override
	public String visitCollectionRange(CollectionRange<Classifier> exp) {
		return exp.toString();
	}

	@Override
	public String visitConstraint(Constraint exp) {
		return exp.toString();
	}

	@Override
	public String visitExpressionInOCL(
			ExpressionInOCL<Classifier, Parameter> exp) {
		return exp.toString();
	}

	@Override
	public String visitInvalidLiteralExp(InvalidLiteralExp<Classifier> exp) {
		return exp.toString();
	}

	@Override
	public String visitIterateExp(IterateExp<Classifier, Parameter> exp) {
		return exp.toString();
	}

	@Override
	public String visitNullLiteralExp(NullLiteralExp<Classifier> exp) {
		return exp.toString();
	}

	@Override
	public String visitRealLiteralExp(RealLiteralExp<Classifier> exp) {
		return exp.toString();
	}

	@Override
	public String visitStateExp(StateExp<Classifier, State> exp) {
		return exp.toString();
	}

	@Override
	public String visitStringLiteralExp(StringLiteralExp<Classifier> exp) {
		return exp.toString();
	}

	@Override
	public String visitTupleLiteralExp(TupleLiteralExp<Classifier, Property> exp) {
		return exp.toString();
	}

	@Override
	public String visitTupleLiteralPart(
			TupleLiteralPart<Classifier, Property> exp) {
		return exp.toString();
	}

	@Override
	public String visitUnlimitedNaturalLiteralExp(
			UnlimitedNaturalLiteralExp<Classifier> exp) {
		return exp.toString();
	}

	@Override
	public String visitUnspecifiedValueExp(UnspecifiedValueExp<Classifier> exp) {
		return exp.toString();
	}

	@Override
	public String visitVariable(Variable<Classifier, Parameter> exp) {
		return exp.toString();
	}
}
