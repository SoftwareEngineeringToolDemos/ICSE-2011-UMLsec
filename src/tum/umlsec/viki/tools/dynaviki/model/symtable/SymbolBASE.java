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
public abstract class SymbolBASE {
	public SymbolBASE(String _name) {
		name = _name;
	}
	
	public String getName() {
		return name;
	}

	
	
	protected String name;
}
