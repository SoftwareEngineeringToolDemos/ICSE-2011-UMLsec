package tum.umlsec.viki.tools.dynaviki.model;

import org.omg.uml.foundation.core.Stereotype;

import tum.umlsec.viki.tools.dynaviki.model.interfaces.ITreeNode;

/**
 * @author pasha
 */
public class MD_Stereotype extends BaseObject implements ITreeNode {
	public static final int LAN = 1;
	public static final int INTERNET = 2;
	public static final int WIRELESS = 3;
	public static final int SECRECY = 4;
	public static final int DATASECURITY = 5;
	
	
	public static final int UNKNOWN = 100;
	
	


	

	public MD_Stereotype(ModelRoot _root, Stereotype _mdrStereotype) {
		super(_root);
		mdrStereotype = _mdrStereotype;
	}

	protected void createNodeStructure() {
	}

	public void initialise() {
		createNodeStructure();
	}

	public String getNodeName() {
		return "<<" + mdrStereotype.getName() + ">>";
	}




	public String getNodeText() {
		return null;
	}
	
	public Stereotype getMdrStereotype() {
		return mdrStereotype;
	}
	
	public int getStereotypeType() {
		String _stereotype = mdrStereotype.getName();
		
		if(_stereotype.compareToIgnoreCase("lan") == 0) {
			return LAN;
		}
		if(_stereotype.compareToIgnoreCase("internet") == 0) {
			return INTERNET;
		}
		if(_stereotype.compareToIgnoreCase("wireless") == 0) {
			return WIRELESS;
		}
		if(_stereotype.compareToIgnoreCase("secrecy") == 0) {
			return SECRECY;
		}
		if(_stereotype.compareToIgnoreCase("data security") == 0) {
			return DATASECURITY;
		}
		
		return UNKNOWN;
	}
	
	
	
	Stereotype mdrStereotype;

}
