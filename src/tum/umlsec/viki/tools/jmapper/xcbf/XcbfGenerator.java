package tum.umlsec.viki.tools.jmapper.xcbf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.exolab.castor.builder.SourceGenerator;

import tum.umlsec.viki.framework.ITextOutput;

/**
 * maps the needed (in respect of the given umlsec stereotype) xml schema to java classes 
 */
public class XcbfGenerator {
	// file Separator
	private String fileSeparator = System.getProperty("file.separator");
	// line Separator
	private String lineSeparator = System.getProperty("line.separator");
	// maps form umlsec stereotype to xcbf schema
	private HashMap stereotype2Xcbf = new HashMap();
	// output directory
	private String rootPath;
	// destination package
	private String destPackage = "xcbf";
	
	public XcbfGenerator(String _rootPath) {
		rootPath = _rootPath;
		
		stereotype2Xcbf.put("unprotected","xcbf_unprotected.xsd");
		stereotype2Xcbf.put("integrity","xcbf_integrity.xsd");
		stereotype2Xcbf.put("secrecy","xcbf_privacy.xsd");
		stereotype2Xcbf.put("integrityandsecrecy","xcbf_privacyandintegrity.xsd");
	}
	
	public void xcbf2Java(String _stereotype) throws Exception {
		if (stereotype2Xcbf.containsKey(_stereotype)) {
			String _srcPath = System.getProperty("user.dir") + fileSeparator +
				// "bin" + fileSeparator +
				"tum" + fileSeparator + 
				"umlsec" + fileSeparator + 
				"viki" + fileSeparator + 
				"tools" + fileSeparator + 
				"jmapper" + fileSeparator + 
				"xcbf" + fileSeparator;
			
			String _srcPathWeb = System.getProperty("user.dir") + fileSeparator +
				"WEB-INF" + fileSeparator + 
				"classes" + fileSeparator + 
				"tum" + fileSeparator + 
				"umlsec" + fileSeparator + 
				"viki" + fileSeparator + 
				"tools" + fileSeparator + 
				"jmapper" + fileSeparator + 
				"xcbf" + fileSeparator;
			
			File _f;
			_f = new File(_srcPath + fileSeparator + (String)stereotype2Xcbf.get(_stereotype));
			if (!_f.exists()) {
				_f = new File(_srcPathWeb + fileSeparator + (String)stereotype2Xcbf.get(_stereotype));
			}

			FileReader _fr = new FileReader(_f);
			SourceGenerator _sg = new SourceGenerator();
			_sg.setSuppressNonFatalWarnings(true);
			_sg.setDestDir(rootPath);
			_sg.generateSource(_fr,destPackage);
				
			File _fi = new File(_srcPath + fileSeparator + "Constants.txt");
			
			if (!_fi.exists()) {
				_fi = new File(_srcPathWeb + fileSeparator + "Constants.txt");
			}
			
			File _newConstants = new File(rootPath + fileSeparator + destPackage + fileSeparator + "Constants.java");
			// copy contents
			byte buffer[] = new byte[0xffff];
		    int nbytes;
		    
		    FileInputStream fis = new FileInputStream(_fi);
		    FileOutputStream fos = new FileOutputStream(_newConstants);
		    
		    while ( (nbytes = fis.read(buffer)) != -1 ) {
		    	fos.write( buffer, 0, nbytes );
		    }
		}
		/*
		else {
			File fi = new File(rootPath + fileSeparator + "xcbf");
			if (!fi.isDirectory())
				fi.mkdir();
		}*/
	}
	
	public void writeXcbf2display(ITextOutput _mainOutput) throws IOException {
		File _fi = new File(rootPath + fileSeparator + destPackage);

		File[] _fil = _fi.listFiles();
		for (int i=0;i<_fil.length;i++) {
			if (_fil[i].isFile()) {
				writeFile2display(_mainOutput,_fil[i]);
				_mainOutput.writeLn(lineSeparator);
				_mainOutput.writeLn("**************************************************************************************************************");
				_mainOutput.writeLn(lineSeparator);
			}
		}
		
		_fi = new File(rootPath + fileSeparator + destPackage + fileSeparator + "types");
		_fil = _fi.listFiles();
		for (int i=0;i<_fil.length;i++) {
			writeFile2display(_mainOutput,_fil[i]);
			_mainOutput.writeLn(lineSeparator);
			_mainOutput.writeLn("**************************************************************************************************************");
			_mainOutput.writeLn(lineSeparator);
		}
	}
	
	public void writeFile2display(ITextOutput _mainOutput, File _fi) throws IOException {
		String line;
		BufferedReader _br = new BufferedReader(new FileReader(_fi));
		while((line = _br.readLine()) != null) {
			_mainOutput.writeLn(line);
		}
		_br.close();
	}
}
