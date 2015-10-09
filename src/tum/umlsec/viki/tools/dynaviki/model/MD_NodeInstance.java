package tum.umlsec.viki.tools.dynaviki.model;

import java.util.Collection;
import java.util.Vector;

import org.omg.uml.behavioralelements.commonbehavior.NodeInstance;

import tum.umlsec.viki.tools.dynaviki.model.interfaces.ITreeNodeCollection;

public class MD_NodeInstance extends BaseObject implements ITreeNodeCollection {

	public MD_NodeInstance(ModelRoot _root, NodeInstance _mdrNodeInstance) {
		super(_root);
		mdrNodeInstance = _mdrNodeInstance;
	}

	protected void createNodeStructure() {
		children.removeAllElements();
	}

	public void initialise() {
		createNodeStructure();
		children.add(linkEnds);
	}

	public Collection getChildren() {
		return children;
	}

	public String getNodeName() {
		return mdrNodeInstance.getName();
	}

	public String getNodeText() {
		return mdrNodeInstance.getName();
	}

	public NodeInstance getMdrNodeInstance() {
		return mdrNodeInstance;
	}

	public void addAttachedLinkEnd(MD_LinkEnd _linkEndModel) {
		linkEnds.add(_linkEndModel);
	}


	
	
	NodeInstance mdrNodeInstance;
	AuxVector linkEnds = new AuxVector("Link Ends");
	Vector children = new Vector();
}
