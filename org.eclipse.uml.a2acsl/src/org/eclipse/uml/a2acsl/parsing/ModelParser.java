package org.eclipse.uml.a2acsl.parsing;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.papyrus.sysml.activities.ActivitiesPackage;
import org.eclipse.papyrus.sysml.allocations.AllocationsPackage;
import org.eclipse.papyrus.sysml.blocks.BlocksPackage;
import org.eclipse.papyrus.sysml.constraints.ConstraintsPackage;
import org.eclipse.papyrus.sysml.interactions.InteractionsPackage;
import org.eclipse.papyrus.sysml.portandflows.PortandflowsPackage;
import org.eclipse.papyrus.sysml.requirements.RequirementsPackage;
import org.eclipse.uml2.uml.Activity;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.PrimitiveType;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;

/**
 * Contains methods that parses the UML model
 * 
 * @author A560169
 * 
 */
public class ModelParser {

	private static URI PLUGIN_PATH = URI
			.createURI("jar:file:/D:/Topcased-5.3.1/plugins/org.eclipse.uml2.uml.resources_3.1.100.v201008191510.jar!/libraries/");
	private static ResourceSet resourceSet;

	private static ArrayList<Activity> activities;
	private static ArrayList<Operation> operations;
	private static Model model;

	private static void initialize() {

		resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
				.put(UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);
		resourceSet.getPackageRegistry().put(UMLPackage.eNS_URI,
				UMLPackage.eINSTANCE);
		resourceSet.getPackageRegistry().put(BlocksPackage.eNS_URI,
				BlocksPackage.eINSTANCE);
		resourceSet.getPackageRegistry().put(RequirementsPackage.eNS_URI,
				RequirementsPackage.eINSTANCE);
		resourceSet.getPackageRegistry().put(AllocationsPackage.eNS_URI,
				AllocationsPackage.eINSTANCE);
		resourceSet.getPackageRegistry().put(ActivitiesPackage.eNS_URI,
				ActivitiesPackage.eINSTANCE);
		resourceSet.getPackageRegistry().put(PortandflowsPackage.eNS_URI,
				PortandflowsPackage.eINSTANCE);
		resourceSet.getPackageRegistry().put(ConstraintsPackage.eNS_URI,
				ConstraintsPackage.eINSTANCE);
		resourceSet.getPackageRegistry().put(InteractionsPackage.eNS_URI,
				InteractionsPackage.eINSTANCE);
		resourceSet.getPackageRegistry().put(BlocksPackage.eNS_URI,
				BlocksPackage.eINSTANCE);
		resourceSet.getURIConverter().getURIMap()
				.put(URI.createURI("pathmap://UML_LIBRARIES/"), PLUGIN_PATH);
		activities = new ArrayList<Activity>();
		operations = new ArrayList<Operation>();
	}

	/**
	 * Parses the UML model at the specified path
	 * 
	 * @param modelPath
	 */
	public static void parseModel(String modelPath) {
		initialize();
		EObject modelObject = resourceSet
				.getResource(URI.createFileURI(modelPath), true).getContents()
				.get(0);
		model = (Model) modelObject;
		for (Iterator<EObject> i = model.eAllContents(); i.hasNext();) {
			EObject object = i.next();
			if (object instanceof Activity) {
				activities.add((Activity) object);
			} else if (object instanceof Operation) {
				operations.add((Operation) object);
			}
		}
	}

	/**
	 * Returns the activities contained in the model
	 * 
	 * @return
	 */
	public static ArrayList<Activity> getActivities() {
		return activities;
	}

	/**
	 * Returns the current model
	 * 
	 * @return
	 */
	public static Model getModel() {
		return model;
	}

	/**
	 * Returns an instance of the {@link PrimitiveType} having the provided name
	 * 
	 * @param name
	 * @return
	 */
	public static PrimitiveType getPrimitiveType(String name) {
		return (PrimitiveType) resourceSet
				.getResource(
						URI.createURI("pathmap://UML_LIBRARIES/UMLPrimitiveTypes.library.uml"),
						true).getEObject(name);
	}

	/**
	 * Returns the model operation having the specified name
	 * 
	 * @param name
	 * @return
	 */
	public static Operation getOperation(String name) {
		for (Operation op : operations) {
			if (op.getName().equals(name))
				return op;
		}
		return null;
	}
}
