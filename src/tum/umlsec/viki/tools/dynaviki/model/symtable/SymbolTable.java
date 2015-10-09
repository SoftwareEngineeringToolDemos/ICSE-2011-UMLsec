/*
 * Created on 17.10.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package tum.umlsec.viki.tools.dynaviki.model.symtable;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import tum.umlsec.viki.framework.ExceptionProgrammLogicError;

/**
 * @author pasha
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class SymbolTable {
	
	public SymbolTable(SymbolTable _subTable) {
		subTable = _subTable;
	}

	private void CheckUniqueName(String _name) {
		if(findSymbol(_name) != null) {
			throw new ExceptionProgrammLogicError("Adding Duplicate symbol to SymbolTable: " + _name);
		}
	}
	
	public void addLiteral(String _name, boolean _knownToIntruder, boolean _key) {
		CheckUniqueName(_name);
		symbols.put(_name, new S_Literal(_name, _knownToIntruder, _key));
	}
		
//	public void addVariable(String _name) {
//		CheckUniqueName(_name);
//		symbols.put(_name, new S_Variable(_name));
//	}
	
	public Collection getLiterals() {
		Vector _v = new Vector();
		
		for (Iterator _iter = symbols.values().iterator(); _iter.hasNext();) {
			SymbolBASE _symbol = (SymbolBASE)_iter.next();
			if(_symbol instanceof S_Literal) {
				_v.add(_symbol);
			}
		}
		return _v;
	}	

//	Hashtable


	public SymbolBASE findSymbol(String _name) {
		SymbolBASE _sym = null;
		if(subTable != null) {
			_sym = subTable.findSymbol(_name);
		}
		if(_sym != null) {
			return _sym;
		}
			
		return (SymbolBASE)(symbols.get(_name));	
	}
	
	
	

	private SymbolTable subTable;
	public Hashtable symbols = new Hashtable();	
}
