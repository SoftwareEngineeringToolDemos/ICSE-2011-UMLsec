package tum.umlsec.viki.tools.dynaviki.model;

import java.util.Collection;
import java.util.Vector;

import tum.umlsec.viki.tools.dynaviki.model.interfaces.ITreeNodeCollection;


/**
 * @author pasha
 */
public class AuxVector extends Vector implements ITreeNodeCollection {
	
	public AuxVector() {
		
		// TODO Auto-generated constructor stub
	}

	public AuxVector(String _name) {
		nodeName = _name;
	}

	public Collection getChildren() {
		return this;
	}


	public String getNodeName() {
		return nodeName;
	}
	
	public String getNodeText() {
		return null;
	}
	
	
	private String nodeName;
}
