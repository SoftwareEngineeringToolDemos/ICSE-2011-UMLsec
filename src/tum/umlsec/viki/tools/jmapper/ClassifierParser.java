package tum.umlsec.viki.tools.jmapper;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.omg.uml.UmlPackage;
import org.omg.uml.foundation.core.Abstraction;
import org.omg.uml.foundation.core.AssociationEnd;
import org.omg.uml.foundation.core.Attribute;
import org.omg.uml.foundation.core.Classifier;
import org.omg.uml.foundation.core.Constraint;
import org.omg.uml.foundation.core.Dependency;
import org.omg.uml.foundation.core.Generalization;
import org.omg.uml.foundation.core.Interface;
import org.omg.uml.foundation.core.ModelElement;
import org.omg.uml.foundation.core.Operation;
import org.omg.uml.foundation.core.Parameter;
import org.omg.uml.foundation.core.Stereotype;
import org.omg.uml.foundation.core.TagDefinition;
import org.omg.uml.foundation.core.TaggedValue;
import org.omg.uml.foundation.core.UmlAssociation;
import org.omg.uml.foundation.core.UmlClass;
import org.omg.uml.foundation.datatypes.BooleanExpression;
import org.omg.uml.foundation.datatypes.Multiplicity;
import org.omg.uml.foundation.datatypes.MultiplicityRange;

/**
 * class is responsible for parsing classifiers (interfaces/classes)
 */
public class ClassifierParser {
	// line separator
	private final String lineSeparator = System.getProperty("line.separator");
	// Root Package
	private UmlPackage p;
	// all Classes/Interfaces (Classifier c,String package)
	private HashMap classifiers;
	// all Packages (UmlPackage package, String path)
	private HashMap packages;
	// all Package Dependencies (String package,Vector Dependecies)
	private HashMap packageDependencies;
	// Abstraction with Stereotype "realize" per Interface
	private Vector interfaceRealizations = new Vector();
	// needed Imports per Class
	private Vector cImports = new Vector();
	// classifier
	private Classifier classifier2parse;
	// specifies the needed security requirements
	private String bioExchangeStereotype = "";
	
	public ClassifierParser (UmlPackage _p,HashMap _classifiers, HashMap _packages, HashMap _packageDependencies, Classifier _c) {
		p = _p;
		classifiers = _classifiers;
		packages = _packages;
		packageDependencies = _packageDependencies;
		classifier2parse = _c;
	}
	
	public Vector[] getFeatures() {
		Vector _att = new Vector();
		Vector _op = new Vector();
		// Vector _meth = new Vector();
		
		Collection a = classifier2parse.getFeature();
		for (Iterator it = a.iterator(); it.hasNext(); ) {
			Object _o = it.next();
			if (_o instanceof Attribute)
				_att.addElement((Attribute) _o);
			else if (_o instanceof Operation)
				_op.addElement((Operation) _o);
			// else // not necessary
				// _meth.addElement((Method) _o);
		}
			
		return new Vector[] {_att, _op};
	}
	
	public Vector getAttributes(Vector _att) {
		Vector _v = new Vector();
		for (Iterator iter = _att.iterator(); iter.hasNext(); ) {
			Attribute _a = (Attribute)iter.next();
			int[] _mult = this.extractMulitplicity(_a.getMultiplicity());
			String _scope = _a.getOwnerScope().toString();
			// default is changeable, only specified if nonchangable
			String _changea = (_a.getChangeability() != null ? _a.getChangeability().toString() : "");
			String _iv = (_a.getInitialValue() != null ? _a.getInitialValue().getBody() : "");
			HashMap _tv = this.extractTaggedValues(_a);
			// visibility name : type-expression [ multiplicity ordering ] = initial-value { property-string }
			_v.addElement( 
				lineSeparator +
				"\t/**" +
				lineSeparator +
				"\t * " + (_tv.containsKey("documentation") ? "<p>" + _tv.get("documentation") + "</p>" : "<p></p>") + 
				lineSeparator + 
				"\t */" +
				lineSeparator +
				"\t" +
				this.extractVisibility(_a.getVisibility().toString()) + // visibility 
				//(_scope.contains("classifier") ? " static" : "") + // scope
				(this.isInString(_scope,"classifier") ? " static" : "") + // scope
				//(_changea.contains("frozen") ? " final" : "") + // changeability
				(this.isInString(_changea,"frozen") ? " final" : "") + // changeability
				" " + (_mult[1] == -1 || _mult[1] > 1 ? "Collection" : _a.getType().getName()) + // type-expression depends on multiplicity
				" " + _a.getName() + // name
				// ordering of attributes is not supported in Poseidon
				(_iv.equals("") ? "" : " = " + _iv) + // initial-value
				";" + 
				(_mult[1] == -1 || _mult[1] > 1 ? " // of type " + _a.getType().getName() : "") // comments 
				// property-string is not supported in Poseidon
			); 
			if (!cImports.contains("java.util.Collection;") && (_mult[1] == -1 || _mult[1] > 1))  
				cImports.addElement("java.util.Collection;");
			if (_a.getType() instanceof UmlClass || _a.getType() instanceof Interface) {
				Classifier _c = (Classifier) _a.getType();
				this.getTypeRelatedDependecy(_c);
			}
		}
		return _v;
	}
	
	public Vector getOperations(Vector _op, boolean isAbstract) {
		Vector _v = new Vector();
 		// visibility name ( parameter-list ) : return-type-expression { property-string }
		for (Iterator iter = _op.iterator(); iter.hasNext(); ) {
			Operation _o = (Operation)iter.next();
			String _scope = _o.getOwnerScope().toString();
			String[] _params = this.extractParameter(_o.getParameter());
			String _ret = _params[0];
			String _conc = _o.getConcurrency().toString();
			HashMap _tv = this.extractTaggedValues(_o);

			String _tmp =
			   lineSeparator + 				
			   "\t/**" +
			   lineSeparator +
			   "\t * " + (_tv.containsKey("documentation") ?  "<p>" + _tv.get("documentation") + "</p>" : "<p></p>") + 
			   lineSeparator + 
			   (_params[2].equals("") ? "" : _params[2]) +
			   // (_o.isQuery() ? lineSeparator + "\t * query" : "") + // query
			   // lineSeparator +
			   "\t */" +
			   lineSeparator + 
			   "\t" +
			   this.extractVisibility(_o.getVisibility().toString()) + // visibility
			   (_o.isAbstract() ? " abstract" : "") + // isAbstract
			   // (_scope.contains("classifier") ? " static" : "") + // scope 
			   (this.isInString(_scope,"classifier") ? " static" : "") + // scope 
			   // (_conc.contains("guarded") ? "synchronized" : "") + // concurrency
			   (this.isInString(_conc,"guarded") ? "synchronized" : "") + // concurrency
			   " " + (_ret.equals("") ? "void" : _ret) + // return
			   " " + _o.getName() + // name
			   "(" + (_params[1].equals("") ? "" : _params[1].substring(0,_params[1].length()-1)) + ")"; 
			   if (isAbstract || _o.isAbstract()) {
			   		_tmp += ";";
			   }
			   else {
			   		_tmp += 
			   	   "{" + // inout param 
				   lineSeparator + 
				   "\t\t// body" + 
				   (_ret.equals("void") ? "" : lineSeparator + "\t\t" + "return null;") +
				   lineSeparator + 
				   "\t}";
			   }
			_v.addElement(_tmp);
		}
				
		return _v;
	}
	
	public String[] extractParameter(List _paramList) {
		String _ret = "";
		String _iao = "";
		String _jd = "";
	
		for (Iterator iter = _paramList.iterator(); iter.hasNext(); ) {
			Parameter _param = (Parameter) iter.next();
			String _dv = "";
			if (_param.getDefaultValue() != null)
				_dv = _param.getDefaultValue().getBody();
			if (this.isInString(_param.getKind().toString(),"return")) {
				_ret = _param.getType().getName();
				if (_param.getType() instanceof UmlClass || _param.getType() instanceof Interface) {
					Classifier _c = (Classifier) _param.getType();
					this.getTypeRelatedDependecy(_c);
				}
			}
			else {
				_iao +=  _param.getType().getName() + " " + _param.getName() + (_dv != "" ? " = " + _dv : "") + ",";// (iter.hasNext() ? "," : "");
				_jd += "\t * @param " + _param.getName() + lineSeparator;
			}
		}
		return new String[] {_ret, _iao, _jd};
	}
	
	public int[] extractMulitplicity(Multiplicity _m) {
		if (_m != null) {
			int _lower = -11;
			int _upper = -11;
			MultiplicityRange _mr = (MultiplicityRange) _m.getRange().iterator().next();
			_lower = _mr.getLower();
			_upper = _mr.getUpper();
			
			return new int[] {_lower, _upper};
		}
		else
			return new int[] {1, 1};
	}
	
	public String extractVisibility(String _v) {
		StringTokenizer st = new StringTokenizer(_v,"_",false);
		for (int i = 0; i< st.countTokens();i++) {
			st.nextToken();
		}
		return st.nextToken();
	}
	
	public Vector extractStereotype(Collection _c) {
		Vector _tmp = new Vector();
		for (Iterator iter = _c.iterator(); iter.hasNext(); ) {
			Stereotype st = (Stereotype) iter.next();
			_tmp.addElement(st.getName());
		}
		return _tmp;
	}
	
	public HashMap extractTaggedValues(ModelElement _me) {
		HashMap _hm = new HashMap();
		for (Iterator iter = _me.getTaggedValue().iterator(); iter.hasNext(); ) {
			TaggedValue _tv = (TaggedValue) iter.next();
			TagDefinition _td = (TagDefinition) _tv.getType();
			Collection _cdv = _tv.getDataValue();
			String _dv = "";
			for (Iterator iter2 = _cdv.iterator(); iter2.hasNext(); ) {
				 _dv += (String)iter2.next();
			}
			_hm.put(_td.getName(),_dv);
		}
		return _hm;
	}
	
	public Vector extractConstraints() {
		Vector _tmp = new Vector();
		// In Poseidon 1.6 there is only one stereotype pro Element in other versions will be more applicable
		for (Iterator iter = classifier2parse.getConstraint().iterator(); iter.hasNext(); ) {
			Constraint _co = (Constraint)iter.next();
			BooleanExpression _be = (BooleanExpression)_co.getBody();
			_tmp.addElement(_be.getBody());
		}
		return _tmp;
	}
	
	public HashSet extractDependencySupplier(Classifier _c) {
	
		HashSet _hs = new HashSet();
		for (Iterator iter = _c.getClientDependency().iterator(); iter.hasNext(); ) {
			Dependency _oi = (Dependency)iter.next();
			if (_oi instanceof Abstraction && this.extractStereotype(_oi.getStereotype()).contains("realize"))
				interfaceRealizations.addElement(_oi);
			for (Iterator iter2 = _oi.getSupplier().iterator(); iter2.hasNext();) {
				Object _os = iter2.next();
				_hs.add(_os);
			}
		}
		
		if (_hs.size() < 1)
			_hs.add("");
		
		return _hs;
	}
	
	public void getImports(String _path) {
		Vector _allupCd = new Vector();
		String[] _tmp = _path.split("\\.");
		String _tmpPath = "";
		// dependencies of the packages
		if (!_path.equals("")) {
			for (int i=1; i<_tmp.length; i++) {
				_tmpPath += "." + _tmp[i];
				_allupCd.addAll((Collection) packageDependencies.get(_tmpPath));
			}
		}
		// dependencies of the class / interface
		_allupCd.addAll(this.extractDependencySupplier(classifier2parse));
		// parse dependencies
		for (Iterator iter = _allupCd.iterator(); iter.hasNext();) {
			Object _o = (Object)iter.next();
			
			if (_o instanceof org.omg.uml.modelmanagement.UmlPackage) {
				org.omg.uml.modelmanagement.UmlPackage _pa = (org.omg.uml.modelmanagement.UmlPackage) _o;
				String _ppath = (String)packages.get(_pa) ;
				cImports.addElement(_ppath.substring(1) + ".*;");
			}
			else if (_o instanceof UmlClass) {
				UmlClass _uci = (UmlClass) _o;
				
				String _tmpcp = (String)classifiers.get(_uci);
				// otherwise the whole package is already imported
				if (!_tmpcp.equals("") && !cImports.contains(_tmpcp.substring(1) + ".*;")) {
					String _cpath = _tmpcp + "." + _uci.getName() + ";";
					cImports.addElement(_cpath.substring(1));
				}
			}
		}
	}
	
	public void getTypeRelatedDependecy(Classifier _c) {
		String _tmpcp = (String)classifiers.get(_c);
		String _tmp = (String)classifiers.get(classifier2parse);
		String _cpath = null;
		// java.lang does not need to be imported
		if (!_tmp.equals(_tmpcp) && !_tmpcp.equals(".java.lang")) {
			// otherwise the whole package is already imported 
			if (_tmpcp.length() > 1 && !cImports.contains(_tmpcp.substring(1) + ".*;"))
				_cpath = _tmpcp.substring(1) + "." + _c.getName() + ";";
			else if (!cImports.contains(_tmpcp.substring(1) + ".*;"))
				_cpath = _c.getName() + ";";
			// otherwise import exists already
			if (_cpath != null && !cImports.contains(_cpath))
				cImports.addElement(_cpath);
		}
	}
	
	public Vector getAssociations() {
		Vector _tmp = new Vector();
		Collection _aa = p.getCore().getUmlAssociation().refAllOfClass();
		for (Iterator iter = _aa.iterator(); iter.hasNext(); ) {
			Object _o = (Object) iter.next();
			UmlAssociation _ua = (UmlAssociation)_o;

			List li = _ua.getConnection();
			for (int i=0; i < li.size(); i++) {
				Object _o2 = (Object)li.get(i);
				if (_o2 instanceof AssociationEnd) {
					AssociationEnd _ae = (AssociationEnd) _o2;
					if ((_ae.getParticipant() instanceof UmlClass || _ae.getParticipant() instanceof Interface) && _ae.getParticipant().equals(classifier2parse)) {
						Vector _otherPart = new Vector();
						// get all other AssocationEnds  
						if (i-1 > 0)
							_otherPart.addAll(li.subList(0,i-1));
						else if (i-1 == 0)
							_otherPart.addElement(li.get(0));
						if (i+1 < li.size()-1)
							_otherPart.addAll(li.subList(i+1,li.size()-1));
						else if (i+1 == li.size()-1)
							_otherPart.addElement(li.get(i+1));
						for (int j=0; j < _otherPart.size(); j++) {
							AssociationEnd _part = (AssociationEnd)_otherPart.get(j);
							if (_part.isNavigable()) {
								int[] _mult = this.extractMulitplicity(_part.getMultiplicity());
								String _changea = (_part.getChangeability() != null ? _part.getChangeability().toString() : "");
								String _cn = _part.getParticipant().getName();
								HashMap _tv = this.extractTaggedValues(_ua);
								_tmp.addElement( 
										lineSeparator 
										+ "\t/**" 
										+ lineSeparator
										+ "\t * " + (!_tv.containsKey("documentation") ? "<p></p>" : "<p>" + _tv.get("documentation") + "</p>") 
										+ lineSeparator 
										+ "\t */"
										+ lineSeparator
										+ "\t"
										+ this.extractVisibility(_part.getVisibility().toString())
										// + (_changea.contains("frozen") ? " final" : "") // changeability
										+ (this.isInString(_changea,"frozen") ? " final" : "") // changeability
										+ " " + (_mult[1] == -1 || _mult[1] > 1 ? "Collection" : _cn) // type-expression depends on multiplicity
										+ " " + (_part.getName() != null ? _part.getName() : _cn.toLowerCase())
										+ ";" 
										+ (_mult[1] == -1 || _mult[1] > 1 ? " // of type " + _cn : "") // comments 
								);
								if (!cImports.contains("java.util.Collection;") && (_mult[1] == -1 || _mult[1] > 1)) 
									cImports.addElement("java.util.Collection;");
							}
						}
					}
					else {
						// TODO kann eigentlich nur bei AssociationClasses vorkommen, check
					}
				}
			}
		}
		return _tmp;
	}
	
	public String extractGeneralization() {
		String _tmp = "";
		for (Iterator iter = classifier2parse.getGeneralization().iterator(); iter.hasNext();) {
			Object _o = iter.next();
			if (_o instanceof Generalization) {
				Generalization _g = (Generalization) _o;
				Object _o2 = _g.getParent();
				if (_o2 instanceof UmlClass || _o2 instanceof Interface) {
					Classifier _genEl = (Classifier)_o2;
					this.getTypeRelatedDependecy(_genEl);
					if (_tmp.equals(""))
						_tmp = _genEl.getName();
					else // in uml multiple inheritance of classes is allowed in java it is not
						_tmp += "," + _genEl.getName();
				}	
			}
		}
		return _tmp;
	}
	
	public String extractRealization() {
		String _tmp = "";
		for (Iterator iter = interfaceRealizations.iterator(); iter.hasNext(); ) {
			Abstraction _a = (Abstraction) iter.next();
			for (Iterator iter2 = _a.getSupplier().iterator(); iter2.hasNext(); ) {
				Classifier _o = (Classifier)iter2.next();
				if (_tmp.equals(""))
					_tmp = _o.getName();
				else 
					_tmp += "," + _o.getName();			
			}
		}
		interfaceRealizations.clear();
		return _tmp;
	}
	
	public void extractBioExchangeStereotype() {
		for (Iterator iter = classifier2parse.getClientDependency().iterator(); iter.hasNext(); ) {
			Dependency _oi = (Dependency)iter.next();
			for (Iterator iter2 = _oi.getSupplier().iterator(); iter2.hasNext();) {
				Object _o = iter2.next();
				if (_o instanceof UmlClass || _o instanceof Interface) {
					Classifier _sup = (Classifier)_o;
					HashMap _tv = this.extractTaggedValues(_sup);
					if (_tv.containsKey("bioExchange") && _tv.get("bioExchange").equals("yes")) {
						cImports.addElement("xcbf.*;");
						Vector _s = this.extractStereotype(_oi.getStereotype());
						// assumes that only one stereotype per dependencie, in Poseidon 1.6 its the cases
						if (!_s.isEmpty())
							this.setBioExchangeStereotype((String)_s.get(0));
						else
							this.setBioExchangeStereotype("");
					}
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
	
	public String getBioExchangeStereotype() {
		return this.bioExchangeStereotype;
	}
	
	public Classifier getClassifier2Parse() {
		return this.classifier2parse;
	}
	
	public Vector getcImports() {
		return this.cImports;
	}
	
	// checks wethers String a contains String b - not very efficient
	public boolean isInString(String a, String b) {
		int i = 0;
		int j = 0;
		while (i <= a.length() - b.length()) {
			while (a.charAt(i+j) == b.charAt(j)) {
				j++;
				if (j == b.length())
					return true;
			}
			i++;
			j = 0;
		}
		return false;
	}
}
