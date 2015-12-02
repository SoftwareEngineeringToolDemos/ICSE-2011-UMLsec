package tum.umlsec.viki.tools.dynaviki.model;

import java.util.Collection;
import java.util.Vector;

import org.omg.uml.behavioralelements.commonbehavior.ComponentInstance;

import tum.umlsec.viki.tools.dynaviki.model.interfaces.ITreeNodeCollection;

/**
 * @author pasha
 */
public class MD_ComponentInstance extends BaseObject implements ITreeNodeCollection {

	public MD_ComponentInstance(ModelRoot _root, ComponentInstance _mdrComponentInstance) {
		super(_root);
		mdrComponentInstance = _mdrComponentInstance;
	}

	protected void createNodeStructure() {
		children.removeAllElements();
	}

	public void initialise() {
		createNodeStructure();
		children.add(linkEnds);
	
		nodeInstanceModel = getRoot().findNodeInstance(mdrComponentInstance.getNodeInstance());	
	}

	public Collection getChildren() {
		return children;
	}

	public String getNodeName() {
		return mdrComponentInstance.getName();
	}

	public String getNodeText() {
		return mdrComponentInstance.getName();
	}
	
	public ComponentInstance getMdrComponentInstance() {
		return mdrComponentInstance;
	}
	
	public void addAttachedLinkEnd(MD_LinkEnd _linkEndModel) {
		linkEnds.add(_linkEndModel);
	}
	
	
	public MD_NodeInstance getNodeInstanceModel() {
		return nodeInstanceModel;
	}
	


	ComponentInstance mdrComponentInstance;
	
	MD_NodeInstance nodeInstanceModel;
	
	AuxVector linkEnds = new AuxVector("Link Ends");
	Vector children = new Vector();

}


