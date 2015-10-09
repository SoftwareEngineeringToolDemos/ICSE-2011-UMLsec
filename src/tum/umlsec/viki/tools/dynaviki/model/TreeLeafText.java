package tum.umlsec.viki.tools.dynaviki.model;

import tum.umlsec.viki.tools.dynaviki.model.interfaces.ITreeNode;

/**
 * @author pasha
 */
public class TreeLeafText extends BaseObject implements ITreeNode {

	public TreeLeafText(ModelRoot _root, String _nodeName, String _nodeText) {
		super(_root);
		nodeName = _nodeName;
		nodeText = _nodeText;
	}

	protected void createNodeStructure() {
	}

	public void initialise() {
		createNodeStructure();
	}

	public String getNodeName() {
		return nodeName;
	}

	public String getNodeText() {
		return nodeText;
	}

	String nodeName;
	String nodeText;

}
