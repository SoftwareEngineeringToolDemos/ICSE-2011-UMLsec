package tum.umlsec.viki.tools.dynaviki.model;

import org.omg.uml.foundation.core.Parameter;

import tum.umlsec.viki.tools.dynaviki.model.interfaces.ITreeNode;

/**
 * @author pasha
 */
public class MD_OperationParameter extends BaseObject implements ITreeNode {
	public MD_OperationParameter(ModelRoot _root, Parameter _parameter) {
		super(_root);
		mdrParameter = _parameter;
	}



	protected void createNodeStructure() {
		// TODO Auto-generated method stub

	}

	public void initialise() {
		// TODO Auto-generated method stub

	}
	
	public String getName() {
		return mdrParameter.getName();
	}

	public String getNodeName() {
		return mdrParameter.getName();
	}

	public String getNodeText() {
		return null;
	}


	private Parameter mdrParameter;
}
