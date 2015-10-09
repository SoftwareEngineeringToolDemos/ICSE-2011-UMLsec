package tum.umlsec.viki.tools.dynaviki.model;

import java.util.Collection;
import java.util.Vector;

import org.omg.uml.behavioralelements.commonbehavior.ComponentInstance;
import org.omg.uml.behavioralelements.commonbehavior.Instance;
import org.omg.uml.behavioralelements.commonbehavior.LinkEnd;
import org.omg.uml.behavioralelements.commonbehavior.NodeInstance;

import tum.umlsec.viki.framework.ExceptionProgrammLogicError;
import tum.umlsec.viki.tools.dynaviki.model.interfaces.ITreeNodeCollection;

/**
 * @author pasha
 */
public class MD_LinkEnd extends BaseObject implements ITreeNodeCollection {
	public static final int LET_OBJECTS = 1;
	public static final int LET_NODEINSTANCES = 2;
	public static final int LET_COMPONENTINSTANCES = 3;
	
	
	
	
	public MD_LinkEnd(ModelRoot _root, LinkEnd _link) {
		super(_root);
		mdrLinkEnd = _link;
		linkModel = getRoot().findLink(_link.getLink());
	}


	protected void createNodeStructure() {
		children.removeAllElements();
	}

	public void initialise() {
		createNodeStructure();

		Instance _instance = mdrLinkEnd.getInstance();
		if(_instance instanceof org.omg.uml.behavioralelements.commonbehavior.Object) {
			attachedObject = getRoot().findObject((org.omg.uml.behavioralelements.commonbehavior.Object)_instance);
			children.add(attachedObject);
			attachedObjectType = LET_OBJECTS;
		} else if(_instance instanceof NodeInstance) {
			attachedNodeInstance = getRoot().findNodeInstance((NodeInstance)_instance);
			children.add(attachedNodeInstance);
			attachedObjectType = LET_NODEINSTANCES;
		} else if(_instance instanceof ComponentInstance) {
			attachedComponentInstance = getRoot().findComponentInstance((ComponentInstance)_instance);
			children.add(attachedComponentInstance);
			attachedObjectType = LET_COMPONENTINSTANCES;
		} else {
			throw new ExceptionBadModel("Not attached link found");
		}
	}

	public int getAttachedObjectType() {
		return attachedObjectType;
	}

	public Collection getChildren() {
		return children;
	}

	public String getNodeName() {
		return "Link End: " + mdrLinkEnd.getName();
	}

	public String getNodeText() {
		return mdrLinkEnd.getName();
	}

	public MD_Object getAttachedObjectModel() {
		if(attachedObject == null) {
			throw new ExceptionProgrammLogicError("Incorrect call to MD_LinkEnd::getAttachedObject");
		}
		return attachedObject;
	}

	public MD_NodeInstance getAttachedNodeInstanceModel() {
		if(attachedNodeInstance == null) {
			throw new ExceptionProgrammLogicError("Incorrect call to MD_LinkEnd::getAttachedNodeInstance");
		}
		return attachedNodeInstance;
	}
	
	public MD_ComponentInstance getAttachedComponentInstanceModel() {
		if(attachedComponentInstance == null) {
			throw new ExceptionProgrammLogicError("Incorrect call to MD_LinkEnd::getAttachedComponentInstance");
		}
		return attachedComponentInstance;
	}
	
	public MD_Link getLinkModel() {
		return linkModel;
	}

	public boolean isMatchedToAssociation() {
		return matchedToAssociation;
	}
	
	public void setMatchedToAssociation(boolean _x) {
		matchedToAssociation = _x;
	}
	
	


	LinkEnd mdrLinkEnd;
	
	MD_Object attachedObject = null;
	MD_NodeInstance attachedNodeInstance = null;
	MD_ComponentInstance attachedComponentInstance = null;
	
	MD_Link linkModel;
	
	Vector children = new Vector();
	
	boolean matchedToAssociation = false;
	
	int attachedObjectType = 0;
}
