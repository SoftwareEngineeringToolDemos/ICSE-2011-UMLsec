package tum.umlsec.viki.tools.jmapper;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.omg.uml.UmlPackage;
import org.omg.uml.foundation.core.Classifier;
import org.omg.uml.foundation.core.Dependency;
import org.omg.uml.foundation.core.Interface;
import org.omg.uml.foundation.core.UmlClass;
import org.omg.uml.modelmanagement.Model;

/**
 * this class reads classifiers (interfaces/classes) and packages (including dependencies)
 * from the given model and creates directorys
 */
public class ModelManagementParser {
	// file Separator 
	private String fileSeparator = System.getProperty("file.separator");
	// root package
	private UmlPackage p;
	// rootPath
	private String rootPath;
	// all Classes/Interfaces (Classifier c,String package)
	private HashMap classifiers = new HashMap();
	// all Packages (UmlPackage package, String path)
	private HashMap packages = new HashMap();
	// all Package Dependencies (String package,Vector Dependecies)
	private HashMap packageDependencies = new HashMap();
	// 2file or 2display
	private boolean write2fs;
	 
	public ModelManagementParser(UmlPackage _p, String _rootPath, boolean _write2fs) {
		p = _p;
		rootPath = _rootPath;
		write2fs = _write2fs;
	}
	
	public void parseModel() {
		Collection m = p.getModelManagement().getModel().refAllOfType();
		for (Iterator _miter = m.iterator();_miter.hasNext();) {
			Object _o = (Object) _miter.next();
			if (_o instanceof Model) {
				Model _mo = (Model)_o;
				Collection moe = _mo.getOwnedElement();
				for (Iterator moeiter = moe.iterator();moeiter.hasNext();) {
					Object _moeobj = (Object) moeiter.next();
					if (_moeobj instanceof UmlClass || _moeobj instanceof Interface) {
						Classifier _c = (Classifier) _moeobj;
						// no anonymous classifiers
						if (!_c.getName().equals(""))
							classifiers.put(_c,"");
					}
				}
			}
		}
	}
	
	public void parsePackages() {
		// get all Packages
		Collection _mm = p.getModelManagement().getUmlPackage().refAllOfClass();
		// package path
		String _path = "";
		// parse packages
		for (Iterator paiter = _mm.iterator();paiter.hasNext();) {
			Object _o = (Object) paiter.next();
			if (_o instanceof org.omg.uml.modelmanagement.UmlPackage) {
				org.omg.uml.modelmanagement.UmlPackage _pa = (org.omg.uml.modelmanagement.UmlPackage) _o;
				if (_pa.getNamespace().getNamespace() == null) {
					_path = "." + _pa.getName();
				}
				else {
					if (_path.length() > 1) {
						String[] _arrpath = _path.substring(1).split("\\.");
						String _tmppath = "";
						for (int i=0; i<_arrpath.length; i++) {
							if (!_arrpath[i].equals(""))
								_tmppath += "." + _arrpath[i];
							if (_arrpath[i].equals(_pa.getNamespace().getName()))
								break;
						}	
						_path = _tmppath + "." + _pa.getName();
					}
				}
				packages.put(_pa,_path);
				packageDependencies.put(_path,this.extractDependencySupplier(_pa));

				if (write2fs) {
					File _fi = new File(rootPath + _path.replace('.',fileSeparator.charAt(0)));
					if (!_fi.isDirectory() && !_path.equals(".java") && (_path.length() < 7 || !_path.substring(0,6).equals(".java."))) {
						_fi.mkdir();
					}
				}
			
				for (Iterator uit = _pa.getOwnedElement().iterator();uit.hasNext();) {
					Object _o2 = (Object) uit.next();
					if (_o2 instanceof UmlClass || _o2 instanceof Interface) {
						Classifier _c = (Classifier) _o2;
						// no anonymous classifiers
						if (!_c.getName().equals(""))
							classifiers.put(_c,_path);
					}
				}
			}
		}
	}
	
	public HashSet extractDependencySupplier(org.omg.uml.modelmanagement.UmlPackage _pa) {
		HashSet _hs = new HashSet();
		for (Iterator iter = _pa.getClientDependency().iterator(); iter.hasNext(); ) {
			Dependency _oi = (Dependency)iter.next();
			// TODO 1 dependency hat mehrere Supplier? Theoretisch ja aber in Poseidon????
			for (Iterator iter2 = _oi.getSupplier().iterator(); iter2.hasNext();) {
				Object _os = iter2.next();
				_hs.add(_os);
			}
		}
		
		if (_hs.size() < 1)
			_hs.add("");
		
		return _hs;
	}
	
	public HashMap getClassifiers() {
		return this.classifiers;
	}
	
	public HashMap getPackages() {
		return this.packages;
	}
	
	public HashMap getPackageDependencies() {
		return this.packageDependencies;
	}
}
