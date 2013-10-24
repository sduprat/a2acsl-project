package org.eclipse.uml.a2acsl.oclgeneration;

import java.util.ArrayList;

import org.eclipse.uml.a2acsl.behavior.Behavior;
import org.eclipse.uml.a2acsl.behavior.CallNode;
import org.eclipse.uml.a2acsl.behavior.Node;
import org.eclipse.uml.a2acsl.oclcontracts.GlobalOclContract;
import org.eclipse.uml.a2acsl.oclcontracts.OclContract;
import org.eclipse.uml.a2acsl.parsing.ModelParser;
import org.eclipse.uml.a2acsl.utils.ModelUtils;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Interface;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Parameter;
import org.eclipse.uml2.uml.ParameterDirectionKind;
import org.eclipse.uml2.uml.Property;

/**
 * Contains methods for OCL contract generation
 * 
 * @author A560169
 * 
 */
public class OclContractGenerator {

	private static OclGenerator generator;
	private static BehaviorOclContractGenerator behaviorGenerator;
	private static StubOclContractGenerator stubGenerator;

	/**
	 * Initializes the generator with an {@link OclGenerator} for OCL syntax
	 * generation, a {@link BehaviorOclContractGenerator} for behaviors
	 * contracts generation and a {@link StubOclContractGenerator} for stub
	 * contract generation
	 * 
	 * @param generator
	 * @param behaviorGenerator
	 * @param stubGenerator
	 */
	public static void initialize(OclGenerator generator,
			BehaviorOclContractGenerator behaviorGenerator,
			StubOclContractGenerator stubGenerator) {
		OclContractGenerator.generator = generator;
		OclContractGenerator.behaviorGenerator = behaviorGenerator;
		OclContractGenerator.stubGenerator = stubGenerator;
	}

	/**
	 * Generates function OCL contract by generating the behaviors contracts
	 * 
	 * @param calledOperations
	 * @param behaviors
	 * @param context
	 * @return
	 */
	public static GlobalOclContract generateFunctionOclContract(
			ArrayList<Operation> calledOperations,
			ArrayList<Behavior> behaviors, Operation context) {
		GlobalOclContract globalContrat = new GlobalOclContract();
		for (Behavior behavior : behaviors) {
			behaviorGenerator.initializeGen(generator);
			OclContract behaviorContract = behaviorGenerator
					.generateBehaviorOCLContract(behavior, context);
			globalContrat.addBehaviorContract(behaviorContract);
		}
		for (Operation operation : calledOperations) {
			String opName = operation.getName();
			globalContrat.setMaxNbCalls(opName,
					getMaxCallNumber(opName, behaviors));
		}
		return globalContrat;
	}

	/**
	 * Generates OCL stub contracts
	 * 
	 * @param calledOperations
	 * @param contract
	 * @param context
	 */
	public static void generateStubContracts(
			ArrayList<Operation> calledOperations, GlobalOclContract contract,
			Operation context) {
		stubGenerator.initialize(generator);
		for (Operation operation : calledOperations) {
			OclContract stubContract = stubGenerator.generateStubContract(
					operation, context);
			contract.addStubContract(stubContract);
		}
	}

	private static int getMaxCallNumber(String operation,
			ArrayList<Behavior> behaviors) {
		int nb = 0;
		for (Behavior behavior : behaviors) {
			int local = 0;
			for (Node node : behavior.getNodes()) {
				if (node instanceof CallNode) {
					if (((CallNode) node).getName().equals(operation)) {
						local++;
					}
				}
			}
			if (local > nb) {
				nb = local;
			}
		}
		return nb;
	}

	/**
	 * Adds types needed for generation to model
	 * 
	 * @param model
	 * @param module
	 */
	public static void prepareModel(Model model, Class module) {
		model.createOwnedInterface("OperationContext");
		model.createOwnedInterface("In");
		model.createOwnedInterface("Out");
	}

	/**
	 * Adds observers classes and contexts definitions to model
	 * 
	 * @param model
	 * @param context
	 * @param calledOperations
	 */
	public static void addObserversToModel(Model model, Operation context,
			ArrayList<Operation> calledOperations) {
		org.eclipse.uml2.uml.Class module = context.getClass_();
		String opContext = context.getName();
		for (Operation operation : calledOperations) {
			Boolean isExtern = true;
			Class opModule = operation.getClass_();
			if (opModule.equals(module)) {
				isExtern = false;
			}
			String opName = operation.getName();
			String contextsName = generator.generateContexts(opContext, opName,
					"");
			String size = generator.generateCallCounter(opContext, opName, "");
			Class op_context = addContextClass(model, opName);

			ArrayList<Property> properties = ModelUtils
					.getSideEffects(operation);
			Class op_in = addInOutClasses(properties, model, operation,
					ParameterDirectionKind.IN_LITERAL);
			Class op_out = addInOutClasses(properties, model, operation,
					ParameterDirectionKind.OUT_LITERAL);

			if (op_in != null)
				op_context.createOwnedAttribute("_in", op_in);
			if (op_out != null)
				op_context.createOwnedAttribute("_out", op_out);

			Parameter result = operation.getReturnResult();
			if (result != null) {
				Property res = op_context.createOwnedAttribute("result",
						result.getType());
				ModelUtils.setProperties(res, result);
			}
			Property contexts = module.createOwnedAttribute(contextsName,
					op_context);
			ModelUtils.setProperties(contexts, false, true, 0, -1);
			module.createOwnedAttribute(size,
					ModelParser.getPrimitiveType("Integer"));

			if (isExtern) {
				Property contexts2 = opModule.createOwnedAttribute(
						contextsName, op_context);
				ModelUtils.setProperties(contexts2, false, true, 0, -1);
				opModule.createOwnedAttribute(size,
						ModelParser.getPrimitiveType("Integer"));
			}
		}
	}

	private static Class addContextClass(Model model, String opName) {
		Interface contextInterface = (Interface) model
				.getOwnedMember("OperationContext");
		Class op_context = model.createOwnedClass(opName + "_context", false);
		op_context.createInterfaceRealization("", contextInterface);
		return op_context;
	}

	private static Class addInOutClasses(ArrayList<Property> properties,
			Model model, Operation operation, ParameterDirectionKind direction) {
		boolean in = (direction == ParameterDirectionKind.IN_LITERAL);
		String opName = operation.getName();
		Interface _interface = (Interface) model.getOwnedMember(in ? "In"
				: "Out");
		ArrayList<Parameter> params = ModelUtils.getParameters(operation,
				direction);
		if (params.size() != 0 || properties.size() != 0) {
			org.eclipse.uml2.uml.Class _class = model.createOwnedClass(opName
					+ (in ? "_in" : "_out"), false);
			_class.createInterfaceRealization("", _interface);
			for (Parameter param : params) {
				Property p = _class.createOwnedAttribute(param.getName(),
						param.getType());
				ModelUtils.setProperties(p, param);
			}
			for (Property prop : properties) {
				Property p = _class.createOwnedAttribute(prop.getName(),
						prop.getType());
				ModelUtils.setProperties(p, prop);
			}
			return _class;
		}
		return null;
	}

	/**
	 * Returns the {@link BehaviorOclContractGenerator} used by this generator
	 * 
	 * @return
	 */
	public static BehaviorOclContractGenerator getBehaviorGenerator() {
		return behaviorGenerator;
	}

}
