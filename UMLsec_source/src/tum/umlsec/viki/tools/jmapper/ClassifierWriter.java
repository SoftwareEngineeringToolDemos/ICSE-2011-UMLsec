package tum.umlsec.viki.tools.jmapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.omg.uml.foundation.core.Interface;

import tum.umlsec.viki.framework.ITextOutput;

/**
 * writes classifiers to file system 
 */
public class ClassifierWriter {
	// line separator
	private String lineSeparator = System.getProperty("line.separator");
	// file Separator 
	private String fileSeparator = System.getProperty("file.separator");
	// classifier parser
	private ClassifierParser cp;
	// the java source file
	private StringBuffer javaSource = new StringBuffer();
	
	
	public ClassifierWriter(ClassifierParser _cp) {
		cp = _cp;
	}
	
	public void writeClassifier(String _path){	
		String _package;
		
		// package	
		if (_path.length() > 1) {
			_package = (_path.substring(1));
			_package = "package " + _package + ";";
		}
		else
			_package = "";
		
		// dependencys	
		cp.getImports(_path);
		// features
		Vector[] _features = cp.getFeatures();
		// attributes
		Vector _att = cp.getAttributes(_features[0]);
		// operations
		// Vector _op = cp.getOperations(_features[1], (cp.getClassifier2Parse() instanceof Interface || cp.getClassifier2Parse().isAbstract() ? true : false));
		Vector _op = cp.getOperations(_features[1], (cp.getClassifier2Parse() instanceof Interface ? true : false));
		// assoc
		Vector _assoc = cp.getAssociations();
		// constraints
		Vector _con = cp.extractConstraints();
		// generalization
		String _gen  = cp.extractGeneralization();
		// realization
		String _real = cp.extractRealization();
		// stereotype
		Vector _st = cp.extractStereotype(cp.getClassifier2Parse().getStereotype());
		// Tagged Values
		HashMap _tv = cp.extractTaggedValues(cp.getClassifier2Parse());
		// isBioExchange
		cp.extractBioExchangeStereotype();
		
		if (_tv.containsKey("bioExchange") && _tv.get("bioExchange").equals("yes")) {
			
			if (!cp.getcImports().contains("xcbf.*;"))
				cp.getcImports().addElement("xcbf.*;");
		}
		
		if (!_package.equals("")) {
			javaSource.append(_package);
			javaSource.append(lineSeparator);
		}
		
		// imports
		this.writeCollection(cp.getcImports(),"import ");
		if (!cp.getcImports().isEmpty()) 
			javaSource.append(lineSeparator);
		// javadoc
		javaSource.append("/**" + lineSeparator);
		javaSource.append(" * @author " + System.getProperty("user.name") + lineSeparator);
		javaSource.append(" * " + (_tv.containsKey("documentation") ? "<p>" + _tv.get("documentation") + "</p>" : "<p></p>"));
		javaSource.append(lineSeparator);
		javaSource.append(" */" + lineSeparator);
		
		// class head
		javaSource.append(cp.extractVisibility(cp.getClassifier2Parse().getVisibility().toString()) + 
					(cp.getClassifier2Parse().isAbstract() ? " abstract" : "") +
					// TODO changeability
					(_st.contains("interface") ? " interface " : " class ")
					+ cp.getClassifier2Parse().getName() + 
					(_gen.equals("") ? "" : " extends " + _gen) +
					(_real.equals("") ? "" : " implements " + _real) +
				" {"
				);
		javaSource.append(lineSeparator);
		// write attributes
		if (!_att.isEmpty())
			javaSource.append(lineSeparator + "\t// attributes");
		this.writeCollection(_att,"");
		// assocs
		if (!_assoc.isEmpty())
			javaSource.append(lineSeparator + "\t// associations");
		this.writeCollection(_assoc,"");
		// write operations
		this.writeCollection(_op,"");
		// javaSource.append(lineSeparator);
		javaSource.append("}");
	}
	
	public void writeCollection(Collection _c, String _pre) {
		if (!_c.isEmpty()) 
			javaSource.append(lineSeparator);
			
		for (Iterator iter = _c.iterator(); iter.hasNext();) {
			javaSource.append(_pre + (String)iter.next());
			javaSource.append(lineSeparator);
		}
	}
	
	public void write2File(String _rootPath, String _path) throws IOException {
		File _tmpFi = new File(_rootPath + fileSeparator + 
				_path.replace('.',fileSeparator.charAt(0)) + fileSeparator 
				+ cp.getClassifier2Parse().getName() + ".java");

		FileWriter _fw = new FileWriter(_tmpFi); 
		_fw.write(javaSource.toString());
		_fw.close();
	}
	
	public void write2display(ITextOutput _mainOutput) {
		javaSource.append(lineSeparator);
		javaSource.append("**************************************************************************************************************");
		javaSource.append(lineSeparator);
		_mainOutput.write(javaSource.toString());
	}
}
