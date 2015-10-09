package tum.umlsec.viki.tools.dynaviki.model;

import java.util.Collection;
import java.util.Vector;

import org.omg.uml.foundation.core.AssociationEnd;
import org.omg.uml.foundation.core.Classifier;
import org.omg.uml.foundation.core.UmlClass;

import tum.umlsec.viki.tools.dynaviki.model.interfaces.ITreeNodeCollection;

/**
 * @author pasha
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class MD_AssociationEnd extends BaseObject implements ITreeNodeCollection {
	
	public MD_AssociationEnd(ModelRoot _root, AssociationEnd _mdrAssociationEnd) {
		super(_root);
		mdrAssociationEnd = _mdrAssociationEnd;
		associationModel = getRoot().findAssociation(mdrAssociationEnd.getAssociation());
	}

	protected void createNodeStructure() {
		children.removeAllElements();
	}

	public void initialise() {
		createNodeStructure();
			
		Classifier _classifier = mdrAssociationEnd.getParticipant();
		if(!(_classifier instanceof UmlClass)) {
			throw new ExceptionBadModel("Not supported: association attached to something which is not a class.");
		}
		
		attachedClassModel = getRoot().findClass((UmlClass)_classifier);
		children.add(attachedClassModel);
	}
	
	public MD_Class getAttachedClassModel() {
		return attachedClassModel; 
	}
	
	public MD_Association getAssociationModel() {
		return associationModel;
	}


	public Collection getChildren() {
		return children;
	}

	public String getName() {
		return mdrAssociationEnd.getName();
	}

	public String getNodeName() {
		return mdrAssociationEnd.getName();
	}

	public String getNodeText() {
		return null;
	}



	
	AssociationEnd mdrAssociationEnd;
	
	MD_Association associationModel;
	MD_Class attachedClassModel;
	
	private Vector children = new Vector();

}
