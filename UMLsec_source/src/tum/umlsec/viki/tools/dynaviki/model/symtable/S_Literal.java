/*
 * Created on 17.10.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package tum.umlsec.viki.tools.dynaviki.model.symtable;

/**
 * @author pasha
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class S_Literal extends SymbolBASE {

	public S_Literal(String _name, boolean _knownToIntruder, boolean _key) {
		super(_name);
		knownToIntruder = _knownToIntruder;
		key = _key;
	}
	
	public boolean isKnownToIntruder() {
		return knownToIntruder;
	}
	
	public boolean isKey() {
		return key;
	}


	boolean knownToIntruder;
	boolean key;
}
