package tum.umlsec.viki.tools.uml2java.javagenerator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.omg.uml.UmlPackage;
import org.omg.uml.behavioralelements.activitygraphs.ActivityGraph;
import org.omg.uml.behavioralelements.activitygraphs.Partition;
import org.omg.uml.foundation.core.Classifier;
import org.omg.uml.foundation.core.UmlClass;

import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.mdr.IMdrContainer;


public class ClassDiagram {
	private ArrayList<File> classFiles;
	private ArrayList<JavaClass> javaClasses = new ArrayList<JavaClass>();
	private IMdrContainer container;
	private String newLine = System.getProperty( "line.separator" );
	protected ITextOutput mainOutput;
	private UmlPackage umlPackage;
	
	public ClassDiagram(IMdrContainer _container, ITextOutput _mainOutput) throws IOException {
		container = _container;
		mainOutput = _mainOutput;
		classFiles = new ArrayList<File>();
		umlPackage = container.getUmlPackage();
		if (container.getUmlPackage() == null) {
			mainOutput.writeLn("No UML model loaded. Please load a model before trying to generate code.");
			return;
		}
		mainOutput.writeLn("Processing class diagrams");
		for (Iterator<UmlClass> iter = umlPackage.getCore().getUmlClass().refAllOfClass().iterator(); iter.hasNext(); ) {
			UmlClass umlClass = iter.next();
			JavaClass javaClass = new JavaClass(umlClass, mainOutput);
			javaClasses.add(javaClass);
			classFiles.add(javaClass.toFile());
		}
		for (Iterator<ActivityGraph> iter = umlPackage.getActivityGraphs().getActivityGraph().refAllOfClass().iterator(); iter.hasNext(); ) {
			ActivityGraph ag = iter.next();
			for (Iterator<Partition> iter2 = ag.getPartition().iterator(); iter2.hasNext(); ) {
				Partition part = iter2.next();
			}
		}
	}
	
	private File convertClass() {
		return new File("");
	}
	
	private String convertAttribute() {
		return "";
	}
	
	private String convertOperation() {
		return "";
	}
	
	private String createMethod(String name, List<String> arguments, String returnType, String mod) {
		String method = mod + " " + returnType + " " + name + "(";
		for (int i = 0; i < arguments.size(); i++) {
			if (i != 0)
				method = method + ", ";
			method = method + arguments.get(i);
		}
		method = method + ") {\n";
		/**
		 * TODO: generate method code
		 */
		method = method + "}";
		return method;
	}
	
	protected ArrayList<JavaClass> getJavaClasses() {
		return javaClasses;
	}
	
	protected ArrayList<File> getClassFiles() {
		return classFiles;
	}
}
