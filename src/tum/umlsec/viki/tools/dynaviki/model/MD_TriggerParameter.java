package tum.umlsec.viki.tools.dynaviki.model;

import org.omg.uml.foundation.core.Parameter;

import tum.umlsec.viki.tools.dynaviki.model.interfaces.ITreeNode;

/**
 * @author pasha
 *
 */
public class MD_TriggerParameter extends BaseObject implements ITreeNode {

	public MD_TriggerParameter(ModelRoot _root, MD_Trigger _trigerModel, Parameter _mdrParameter) {
		super(_root);
		triggerModel = _trigerModel;
		mdrParameter = _mdrParameter;
	}


	protected void createNodeStructure() {

	}

	public void initialise() {
		createNodeStructure();
		mapToAttribute();
	}

	public String getNodeName() {
		return mdrParameter.getName();
	}

	public String getNodeText() {
		return "Name: " + mdrParameter.getName() + "  Type: " + mdrParameter.getType().getName();
	}

	public String getName() {
		return mdrParameter.getName();
	}
	
	private void mapToAttribute() {
		MD_Class _class = triggerModel.getParentTransitionModel().getStateMachineModel().getClassModel();
		associatedAttribute = _class.getAttribute(getName());
		if(associatedAttribute == null) {
			throw new ExceptionBadModel("Trigger Parameter " + getName() + " cannot be mapped to an attribute in class " + _class.getName());			
		}
	}
	
	public MD_Attribute getAssociatedAtribute() {
		return associatedAttribute ;
	}
	


	MD_Trigger triggerModel;
	MD_Attribute associatedAttribute = null;
	private Parameter mdrParameter;
}







