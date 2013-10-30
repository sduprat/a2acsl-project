package org.eclipse.uml.a2acsl.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.eclipse.uml.a2acsl.behavior.Behavior;
import org.eclipse.uml.a2acsl.ocl2acsl.AcslGenerator;
import org.eclipse.uml.a2acsl.ocl2acsl.Ocl2Acsl;
import org.eclipse.uml.a2acsl.ocl2acsl.Ocl2AcslVisitor;
import org.eclipse.uml.a2acsl.oclcontracts.GlobalOclContract;
import org.eclipse.uml.a2acsl.oclgeneration.BehaviorOclContractGenerator;
import org.eclipse.uml.a2acsl.oclgeneration.OclContractGenerator;
import org.eclipse.uml.a2acsl.oclgeneration.OclGenerator;
import org.eclipse.uml.a2acsl.oclgeneration.StubOclContractGenerator;
import org.eclipse.uml.a2acsl.parsing.ActivityParser;
import org.eclipse.uml.a2acsl.parsing.ModelParser;
import org.eclipse.uml.a2acsl.parsing.NodeParser;
import org.eclipse.uml2.uml.Activity;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Operation;

/**
 * Main class, generates for each activity - OCL contract (in .ocl file) - ACSL
 * contract (in .acsl file) - Stub annotations (in _stubs.h file)
 * 
 * @author A560169
 * 
 */
public class Activity2Acsl {

	public static void generateACSLContracts(File modelFile) {
		// Initialization
		String modelName = modelFile.getName().split("\\.")[0];
		String modelPathParent = modelFile.getParent();
		String genPath = modelPathParent + "\\" + modelName + "\\";
		String modelPath = modelFile.getPath();
		try {
			OclGenerator generator = new OclGenerator();
			BehaviorOclContractGenerator behaviorGenerator = new BehaviorOclContractGenerator();
			StubOclContractGenerator stubGenerator = new StubOclContractGenerator();
			OclContractGenerator.initialize(generator, behaviorGenerator,
					stubGenerator);
			AcslGenerator acslGenerator = new AcslGenerator();
			Ocl2Acsl.initialize(new Ocl2AcslVisitor());
			// Model parsing
			ModelParser.parseModel(modelPath);
			Model model = ModelParser.getModel();
			for (Activity a : ModelParser.getActivities()) {
				Operation op = (Operation) a.getSpecification();
				org.eclipse.uml2.uml.Class module = op.getClass_();
				OclContractGenerator.prepareModel(model, module);
				// Activity parsing
				ActivityParser.parseActivity(a, new NodeParser());
				ArrayList<Operation> calledOperations = ActivityParser
						.getCalledOperations();
				ArrayList<Behavior> behaviors = ActivityParser
						.getListBehaviors();
				// Model completion with observers
				OclContractGenerator.addObserversToModel(model, op,
						calledOperations);
				// OCL contract generation
				GlobalOclContract globalContract = OclContractGenerator
						.generateFunctionOclContract(calledOperations,
								behaviors, op);
				// OCL stub contracts generation
				OclContractGenerator.generateStubContracts(calledOperations,
						globalContract, op);
				String ocl = globalContract.toString();
				// Translation of OCL contract to ACSL
				String acsl = acslGenerator.generateFunctionContract(
						globalContract, a, calledOperations);
				// Stub generation
				String stubs = acslGenerator.generateStubs(globalContract, op);
				String name = a.getName();
				String filePath = genPath + name + "\\";
				// Files generation
				writeToFile(stubs, filePath + name + "_stubs.h");
				writeToFile(ocl, filePath + name + ".ocl");
				writeToFile(acsl, filePath + name + ".h");
			}
		} catch (Exception e) {
			File errorFile = new File(modelPathParent + "\\error.log");
			try {
				PrintStream ps = new PrintStream(errorFile);
				e.printStackTrace(ps);
			} catch (FileNotFoundException e1) {
				writeToFile(e1.getMessage(), modelPathParent + "\\error.log");
			}

		}
	}

	// Writes content to provided path
	private static void writeToFile(String content, String filePath) {
		File file = new File(filePath);
		File parent = file.getParentFile();
		if (!parent.exists()) {
			parent.mkdirs();
		}
		file.delete();
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(
					new FileWriter(filePath, true)));
			out.println(content);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// Debug, first argument : Path to uml model
	public static void main(String args[]) {
		Activity2Acsl
				.generateACSLContracts(new File(
						"D:\\documents\\A560169\\workspace\\a2acslmodeltests\\ModelTests.uml"));
	}

}
