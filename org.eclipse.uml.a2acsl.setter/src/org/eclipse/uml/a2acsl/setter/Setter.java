package org.eclipse.uml.a2acsl.setter;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ocl.expressions.ExpressionsFactory;
import org.eclipse.ocl.expressions.Variable;
import org.eclipse.ocl.uml.UMLOCLStandardLibrary;
import org.eclipse.ocl2acsl.additionalOperations.CustomOperation;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Parameter;

/**
 * Extends OCL operations by adding a setter operation. Used by A2Acsl.
 * 
 * @author A560169
 * 
 */
public class Setter implements CustomOperation {

	private UMLOCLStandardLibrary oclLib = new UMLOCLStandardLibrary();

	@Override
	public List<Variable<Classifier, Parameter>> getParameters() {
		List<Variable<Classifier, Parameter>> params = new ArrayList<Variable<Classifier, Parameter>>();
		Variable<Classifier, Parameter> objVar = ExpressionsFactory.eINSTANCE
				.createVariable();
		objVar.setName("obj");
		objVar.setType(oclLib.getOclAny());
		Variable<Classifier, Parameter> featureVar = ExpressionsFactory.eINSTANCE
				.createVariable();
		featureVar.setName("feature");
		featureVar.setType(oclLib.getString());
		Variable<Classifier, Parameter> valueVar = ExpressionsFactory.eINSTANCE
				.createVariable();
		valueVar.setName("value");
		valueVar.setType(oclLib.getOclAny());
		params.add(objVar);
		params.add(featureVar);
		params.add(valueVar);
		return params;
	}

	@Override
	public Classifier getType() {
		return oclLib.getOclAny();
	}

	@Override
	public Classifier getClassifier() {
		return oclLib.getOclAny();
	}

	@Override
	public String getName() {
		return "ocl_set";
	}

}
