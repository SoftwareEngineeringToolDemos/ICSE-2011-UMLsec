package tum.umlsec.viki.tools.dynaviki.tree;

import java.util.Collection;
import java.util.Iterator;

import javax.swing.tree.DefaultMutableTreeNode;

import tum.umlsec.viki.tools.dynaviki.model.interfaces.ITreeNode;
import tum.umlsec.viki.tools.dynaviki.model.interfaces.ITreeNodeCollection;


/**
 * @author pasha
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TreeNode extends DefaultMutableTreeNode {

	public TreeNode(ITreeNode _node) {
		if(_node == null) {
			int t = 0;
		}
		

		nn = _node.getNodeName();		
		node = _node;
		
	}



	public boolean fillChildren() {
		if(childrenCollected) {
			return false;
		}
		childrenCollected = true;
		
		if(!hasChildren()) {
			return false; 
		}
		
		Collection children = ((ITreeNodeCollection)node).getChildren();
		if(children == null) {
			return false;
		}
		for(Iterator _it = children.iterator(); _it.hasNext(); ) {
			TreeNode _newNode = new TreeNode((ITreeNode)(_it.next()));
			add(_newNode);			
//			_newNode.fillChildren();
		}
		return true;
	}

	boolean hasChildren() {
		return node instanceof ITreeNodeCollection;
	}
	
	public String toString() {
		return node.getNodeName(); 
	}
	
	public String getNodeText() {
		return node.getNodeText(); 
	}



	private ITreeNode node;
	private String nn;
	private boolean childrenCollected = false; 
}
