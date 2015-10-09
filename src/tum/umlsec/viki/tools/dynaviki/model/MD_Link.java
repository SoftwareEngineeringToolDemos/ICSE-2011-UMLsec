package tum.umlsec.viki.tools.dynaviki.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import org.omg.uml.behavioralelements.commonbehavior.LinkEnd;
import org.omg.uml.foundation.core.Stereotype;

import tum.umlsec.viki.framework.ExceptionProgrammLogicError;
import tum.umlsec.viki.tools.dynaviki.model.interfaces.ITreeNodeCollection;



public class MD_Link extends BaseObject implements ITreeNodeCollection, IStereotypedModelElement {
	public MD_Link(ModelRoot _root, org.omg.uml.behavioralelements.commonbehavior.Link _mdrLink) {
		super(_root); 
		mdrLink = _mdrLink;
	}







	protected void createNodeStructure() {
		children.removeAllElements();
	}

	public void initialise() {
		createNodeStructure();
		
		String _ln = mdrLink.getName();
	
		int count = 0;
		for (Iterator _iter = mdrLink.getConnection().iterator(); _iter.hasNext();) {
			LinkEnd _link = (LinkEnd) _iter.next();
			MD_LinkEnd _linkEndModel = new MD_LinkEnd(getRoot(), _link);
			if(count == 0) {
				linkEnd1 = _linkEndModel;
			} else {
				linkEnd2 = _linkEndModel;
			}			
			children.add(_linkEndModel);
			_linkEndModel.initialise();
			
			count ++;
		}
		if(count != 2) {
			throw new ExceptionBadModel("Unsupported link found. It has not two connection points");
		}
		
		
		collectStereotypes();
		children.add(stereotypes);
	}
	
	
	private void collectStereotypes() {
		String _ln = mdrLink.getName();
		
		for (Iterator _iter = mdrLink.getStereotype().iterator(); _iter.hasNext();) {
			stereotypes.add(getRoot().findStereotype((Stereotype)_iter.next()));
		}
		
	}



	public Collection getChildren() {
		return children;
	}

	public String getNodeName() {
		String _name = mdrLink.getName();
		if(_name == null || _name.length() == 0) {
			_name = "[no name]"; 
		}
		return "Link: " + _name;
	}

	public String getNodeText() {
		return null;
	}




	public org.omg.uml.behavioralelements.commonbehavior.Link getMdrLink() {
		return mdrLink;
	}

	public MD_LinkEnd getLinkEnd1() {
		return linkEnd1;
	}

	public MD_LinkEnd getLinkEnd2() {
		return linkEnd2;
	}

	public MD_LinkEnd getAnotherEnd(MD_LinkEnd _thisEnd) {
		if(getLinkEnd1() == _thisEnd) {
			return getLinkEnd2();
		}
		if(getLinkEnd2() == _thisEnd) {
			return getLinkEnd1();
		}
		throw new ExceptionProgrammLogicError("Invalid call to the MD_LinkEnd::getAnotherEnd in the DynaViki tool.");
	}
	
	public Collection getStereotypes() {
		return stereotypes;
	}






	org.omg.uml.behavioralelements.commonbehavior.Link mdrLink;
	MD_LinkEnd linkEnd1;   
	MD_LinkEnd linkEnd2;   

	Vector children = new Vector();
	AuxVector stereotypes = new AuxVector("Stereotypes");
}

