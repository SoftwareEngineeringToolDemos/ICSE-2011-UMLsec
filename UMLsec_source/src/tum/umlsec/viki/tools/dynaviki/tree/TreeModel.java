package tum.umlsec.viki.tools.dynaviki.tree;

import java.util.Enumeration;

import javax.swing.JTextArea;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;


/**
 * @author pasha
 */
public class TreeModel extends DefaultTreeModel implements TreeWillExpandListener, TreeSelectionListener {
	
	public TreeModel(TreeNode _nodeRoot, JTextArea _textArea) {
		super(_nodeRoot);
		emptyTree();
		textArea = _textArea;
	}

	private void removeChildren(DefaultMutableTreeNode _node) {
		if(_node == null) {
			return;
		}
		for (Enumeration e = _node.children() ; e.hasMoreElements();) {
			removeChildren((DefaultMutableTreeNode)e.nextElement());
		}
		_node.removeAllChildren();
	}

	private void emptyTree() {
		removeChildren((DefaultMutableTreeNode)getRoot());
		modelEmpty = true;
	}
	
	public void updateTree() {
		emptyTree();
		
		modelEmpty = false;
	
		getRootTyped().fillChildren();
		TreePath _pathToRoot = new TreePath(getRootTyped());
		getTree().collapsePath(_pathToRoot);
		getTree().expandPath(_pathToRoot);
		
		
		
//		getRootTyped().fillChildren();
//		modelEmpty = false;
	}
	
	
	public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
		TreeNode _node = (TreeNode)event.getPath().getLastPathComponent();
		boolean changes = false;
		
		
		if(_node.getChildCount() == 0) {
			changes |= _node.fillChildren();
		}

		for (Enumeration e = _node.children() ; e.hasMoreElements();) {
			changes |= ((TreeNode)e.nextElement()).fillChildren();
		}

		if(changes) {
			this.nodeStructureChanged(_node);
		}
	}

	public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {

	}

	public void valueChanged(TreeSelectionEvent _event) {		
		TreePath _selectionPath = _event.getPath();
		if(_selectionPath != null) {
			Object _x = _selectionPath.getLastPathComponent();
			if (_x instanceof TreeNode) {
				TreeNode _treeNode = (TreeNode) _x;
				textArea.setText(_treeNode.getNodeText());
				return;
			}
		}
		textArea.setText("");
	}
	
	protected TreeNode getRootTyped() {
		return (TreeNode)getRoot();
	}

	protected Tree getTree() {
		return tree;
	}
	
	public void setTree(Tree _tree) {
		tree = _tree;
	}


	
	private boolean modelEmpty;
	private Tree tree;
	private JTextArea textArea;
}
