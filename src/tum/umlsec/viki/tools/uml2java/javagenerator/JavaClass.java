package tum.umlsec.viki.tools.uml2java.javagenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.omg.uml.foundation.core.Attribute;
import org.omg.uml.foundation.core.Feature;
import org.omg.uml.foundation.core.Operation;
import org.omg.uml.foundation.core.UmlClass;

import tum.umlsec.viki.framework.ITextOutput;

public class JavaClass {
	private ArrayList<JavaMethod> methods = new ArrayList<JavaMethod>();
	private ArrayList<JavaAttribute> attributes = new ArrayList<JavaAttribute>();
	private String packageName;
	private String className;
	private String visibility;
	private boolean isStatic;
	private String newLine = System.getProperty( "line.separator" );
	
	public JavaClass(UmlClass umlClass, ITextOutput output) {
		output.writeLn("Processing class : " + umlClass.getName());
		className = umlClass.getName();
		visibility = umlClass.getVisibility().toString();
		visibility = visibility.substring(3); // remove those strange 'vk_' in the beginning of the String
		output.writeLn("Visibility : " + visibility);
		output.writeLn("Processing attributes");
		for (Iterator<Feature> iter = umlClass.getFeature().iterator(); iter.hasNext(); ) {
			Feature feat = iter.next();
			try {
				Attribute attr = (Attribute)feat;
				output.writeLn("Attribute : " + attr.getName());
				attributes.add(new JavaAttribute(attr));
			} catch (Exception e) {
				try {
					Operation op = (Operation) feat;
					output.writeLn("Operation : " + op.getName());
					methods.add(new JavaMethod(op));
				} catch (Exception ex) {
					output.write("We have a feature that's not an Attribute and that's not an operation. Skipping...");
					System.out.println(ex.toString() + ": " + e.getMessage());
				}
			}
		}
	}
	
	/**
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public File toFile() throws IOException {
		// Creating file
		String fileName = className + ".java";
		File classFile = new File(JavaGenerator.directory + System.getProperty("file.separator") + fileName);
		BufferedWriter output = new BufferedWriter(new FileWriter(classFile));
		
		// writing package information if package is provided
		if (packageName != null)
			output.write(packageName + newLine + newLine);
		
		// class signature
		String sig = visibility + " class " + className + " {" + newLine;
		output.write(sig);
		
		// Attributes declarations
		for (Iterator<JavaAttribute> iter = attributes.iterator(); iter.hasNext(); ) {
			JavaAttribute attr = iter.next();
			output.write(attr.toString());
		}
		
		// Methods
		for (Iterator<JavaMethod> iter = methods.iterator(); iter.hasNext(); ) {
			JavaMethod method = iter.next();
			output.write(method.toString());
		}
		// closing bracket
		output.write("}" + newLine);
		output.close();
		return classFile;
	}
	
	protected ArrayList<JavaMethod> getMethods() {
		return methods;
	}
}
