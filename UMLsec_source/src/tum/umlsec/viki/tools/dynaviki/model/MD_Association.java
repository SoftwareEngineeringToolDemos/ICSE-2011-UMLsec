package tum.umlsec.viki.tools.dynaviki.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import org.omg.uml.foundation.core.AssociationEnd;
import org.omg.uml.foundation.core.UmlAssociation;

import tum.umlsec.viki.framework.ExceptionProgrammLogicError;
import tum.umlsec.viki.tools.dynaviki.model.interfaces.ITreeNodeCollection;

/**
 * @author pasha
 */
public class MD_Association extends BaseObject implements ITreeNodeCollection {
	public MD_Association(ModelRoot _root, UmlAssociation _mdrAssociation) {
		super(_root);
		mdrAssociation = _mdrAssociation;
	}

	protected void createNodeStructure() {
		children.removeAllElements();
	}

	public void initialise() {
		createNodeStructure();
		

		int count = 0;
		for (Iterator _iter = mdrAssociation.getConnection().iterator(); _iter.hasNext();) {
			AssociationEnd _association = (AssociationEnd) _iter.next();
			MD_AssociationEnd _associationEndModel = new MD_AssociationEnd(getRoot(), _association);
			if(count == 0) {
				associationEnd1 = _associationEndModel;
			} else {
				associationEnd2 = _associationEndModel;
			}			
			children.add(_associationEndModel);
			_associationEndModel.initialise();
			
			count ++;
		}
		if(count != 2) {
			throw new ExceptionBadModel("Unsupported association found. It has not two connection points");
		}
	}

	public Collection getChildren() {
		return children;
	}

	public String getNodeName() {
		String _name = mdrAssociation.getName();
		if(_name == null || _name.length() == 0) {
			_name = "[no name]"; 
		}
		return _name;
	}
	
	public String getName() {
		return mdrAssociation.getName();
	}
	
	public String getNodeText() {
		return null;
	}
	
	public UmlAssociation getMdrAssociation() {
		return mdrAssociation;
	} 

	public MD_AssociationEnd getAssociationEnd1() {
		return associationEnd1;
	}

	public MD_AssociationEnd getAssociationEnd2() {
		return associationEnd2;
	}
	
	/* (non-Javadoc)
	 * @see tum.umlsec.viki.tools.dynaviki.model.interfaces.IAssociation#getAnotherEnd(tum.umlsec.viki.tools.dynaviki.model.interfaces.IClass)
	 */
	public MD_AssociationEnd getAnotherEnd(MD_AssociationEnd _thisEnd) {
		if(getAssociationEnd1() == _thisEnd) {
			return getAssociationEnd2();
		}
		if(getAssociationEnd2() == _thisEnd) {
			return getAssociationEnd1();
		}
		throw new ExceptionProgrammLogicError("Invalid call to the IAssociation::getAnotherEnd in the DynaViki tool.");
	}





	private UmlAssociation mdrAssociation;
	private MD_AssociationEnd associationEnd1;   
	private MD_AssociationEnd associationEnd2;   
	
	private Vector children = new Vector();

}
