package tum.umlsec.viki.tools.dynaviki.model;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Vector;

import org.omg.uml.foundation.core.UmlClass;

import tum.umlsec.viki.framework.ExceptionProgrammLogicError;
import tum.umlsec.viki.tools.dynaviki.model.interfaces.ITreeNodeCollection;




/**
 * @author pasha
 */
public class MD_Object extends BaseObject implements ITreeNodeCollection {
	public MD_Object(ModelRoot _root, org.omg.uml.behavioralelements.commonbehavior.Object _mdrObject) {
		super(_root); 
		mdrObject = _mdrObject;
	}
	
	protected void createNodeStructure() {
		children.removeAllElements();
		
		children.add(linkEnds);
	}

	public void initialise() {
		createNodeStructure();
		
		classModel = getRoot().findClass(getFirstClassifier());
		componentInstanceModel = getRoot().findComponentInstance(mdrObject.getComponentInstance());
	}

	public void addAttachedLinkEnd(MD_LinkEnd _linkEndModel) {
		linkEnds.add(_linkEndModel);
	}

	
	public Collection getLinkEnds() {
		return linkEnds; 
	}

	public Collection getChildren() {
		return children;
	}
	
	public String getName() {
		return mdrObject.getName();
	}

	public String getNodeName() {
		return "Object: " + mdrObject.getName();
	}

	public String getNodeText() {
		return null;
	}

	public org.omg.uml.behavioralelements.commonbehavior.Object getMdrObject() {
		return mdrObject;
	}
	
	private UmlClass getFirstClassifier() {
		return (UmlClass)mdrObject.getClassifier().iterator().next();
	}
	
	public MD_Class getClassModel() {
		return classModel;
	}
	
	public MD_ComponentInstance getComponentInstanceModel() {
		return componentInstanceModel;
	}
	
	public void mapAssociationEndToLinkEnd(MD_AssociationEnd _associationEnd, MD_LinkEnd _linkEnd) {
		if(mapAssociationEndToLinkEnd.containsKey(_associationEnd)) {
			throw new ExceptionProgrammLogicError("Double-mapping associationEnd to linkEnd");
		}
		mapAssociationEndToLinkEnd.put(_associationEnd, _linkEnd);
	}
	
	public MD_LinkEnd getMappedLinkEnd(MD_AssociationEnd _associationEnd) {
		return (MD_LinkEnd)mapAssociationEndToLinkEnd.get(_associationEnd);
	}
	

	

	org.omg.uml.behavioralelements.commonbehavior.Object mdrObject;
	MD_Class classModel;
	MD_ComponentInstance componentInstanceModel;
	
	
	Hashtable mapAssociationEndToLinkEnd = new Hashtable();	

	AuxVector linkEnds = new AuxVector("Link Ends");
	
	Vector children = new Vector();
}
