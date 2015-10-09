package tum.umlsec.viki.tools.jmapper;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.omg.uml.UmlPackage;
import org.omg.uml.foundation.core.Classifier;

import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.mdr.IMdrContainer;
import tum.umlsec.viki.tools.jmapper.xcbf.XcbfGenerator;

/**
 * responsible for mapping class diagrams to java classes
 */
public class ClassDiagramMapper {
	// ouput directory
	private String rootPath = System.getProperty("user.home"); // + "\\Desktop\\output";
	// root package
	private UmlPackage p;
	// handles output
	private ITextOutput mainOutput;
	// specifies the needed security requirements
	private String bioExchangeStereotype = "";
	// model management parser
	private ModelManagementParser mmp;
	// 2file or 2display
	private boolean write2fs;
	
	public ClassDiagramMapper(IMdrContainer _mdrContainer, ITextOutput _mainOutput) {
		if (_mdrContainer.getUmlPackage() == null) {
			_mainOutput.write("No XMI file loaded");
			return;
		}
		p = _mdrContainer.getUmlPackage();
		mainOutput = _mainOutput;
	}
	
	public void doMap() {
		boolean _err = false; 
		
		mmp = new ModelManagementParser(p,rootPath,write2fs);
		mmp.parseModel();
		mmp.parsePackages();
		
		for (Iterator iter = mmp.getClassifiers().keySet().iterator(); iter.hasNext();) {
			Classifier _c = (Classifier)iter.next();
			String _path = (String)mmp.getClassifiers().get(_c);
			if (!_path.equals(".java") && (_path.length() < 7 || !_path.substring(0,6).equals(".java."))) {
				ClassifierParser _cp = new ClassifierParser(p,mmp.getClassifiers(),mmp.getPackages(),mmp.getPackageDependencies(),_c);
				ClassifierWriter _cw = new ClassifierWriter(_cp);
				_cw.writeClassifier(_path);
				if (write2fs) {
					try {
						if (rootPath.equals("/usr/share/tomcat4")) {
							rootPath = System.getProperty("user.dir") + System.getProperty("file.separator") + "output";
						}
						_cw.write2File(rootPath,_path);
					} catch (IOException e) {
						_err = true;
						mainOutput.writeLn(e.toString());
					}
				}
				else {
					_cw.write2display(mainOutput);
				}
				
				if (!_cp.getBioExchangeStereotype().equals(""))
					this.setBioExchangeStereotype(_cp.getBioExchangeStereotype());
			}
		}
		
		if (!_err && write2fs) {
			mainOutput.writeLn("class diagram mapped");
			mainOutput.writeLn("source files in: " + rootPath );
		}
		
		if (!bioExchangeStereotype.equals("")) {
			File _fi = new File(rootPath);
			if (rootPath.equals("/usr/share/tomcat4")) {
				rootPath = System.getProperty("user.dir") + System.getProperty("file.separator") + "output";
			}
			XcbfGenerator _xg = new XcbfGenerator(rootPath);
			try {
				_xg.xcbf2Java(this.bioExchangeStereotype);
			} catch (Exception e) {
				mainOutput.writeLn(e.toString());
			}
			if (write2fs) {
				mainOutput.writeLn("xcbf files generated");
			}
			else {
				try {
					_xg.writeXcbf2display(mainOutput);
				} catch (Exception e) {
					mainOutput.writeLn(e.toString());
				}
			}
		}
	}
		
	public void setBioExchangeStereotype(String _st) {
		if (bioExchangeStereotype.equals("integrityandsecrecy"))
			return;
		else if (_st.equals("integrityandsecrecy"))
			bioExchangeStereotype = _st;
		else if (_st.equals("") && bioExchangeStereotype.equals(""))
			bioExchangeStereotype = "unprotected";
		else if ((_st.equals("integrity") && bioExchangeStereotype.equals("secrecy")) || (_st.equals("secrecy") && bioExchangeStereotype.equals("integrity")))
			bioExchangeStereotype = "integrityandsecrecy";
		else if (!_st.equals(""))
			bioExchangeStereotype = _st;
	}
	
	public void setWrite2fs(boolean _new) {
		write2fs = _new;
	}
}


