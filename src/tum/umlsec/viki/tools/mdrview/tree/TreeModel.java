package tum.umlsec.viki.tools.mdrview.tree;

import java.util.Enumeration;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;

import tum.umlsec.viki.framework.mdr.IMdrContainer;
import tum.umlsec.viki.framework.mdr.IMdrContainer.ClearEvent;
import tum.umlsec.viki.framework.mdr.IMdrContainer.LoadEvent;
import tum.umlsec.viki.tools.mdrview.tree.nodes.AbstractNode;
import tum.umlsec.viki.tools.mdrview.tree.nodes.Root;

/**
 * @author pasha
 */
public class TreeModel extends DefaultTreeModel implements IMdrContainer.IMdrContainerListener, TreeWillExpandListener {
	public TreeModel(Root _nodeRoot, IMdrContainer _mdrContainer) {
		super(_nodeRoot);
		fillEmptyModel();
		_mdrContainer.addListener(this);
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
	}

	private void fillEmptyModel() {
		emptyTree();
		getRootTyped().fillChildren();
		modelEmpty = true;
	}

	public Root getRootTyped() {
		return (Root)getRoot();
	}
	
	public void onClearMdr(ClearEvent e) {
		if(!modelEmpty) {
			fillEmptyModel();
		}
	}

	public void onLoadMdr(LoadEvent e) {
		fillEmptyModel();

		modelEmpty = false;
		TreePath _pathToRoot = new TreePath(getRootTyped());
		getTree().collapsePath(_pathToRoot);
		getTree().expandPath(_pathToRoot);
	}

	public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
		AbstractNode _node = (AbstractNode)event.getPath().getLastPathComponent();
		boolean changes = false;
		if(_node.getChildCount() == 0) {
			changes |= _node.fillChildren();
		}

		for (Enumeration e = _node.children() ; e.hasMoreElements();) {
			changes |= ((AbstractNode)e.nextElement()).fillChildren();
		}

		if(changes) {
			this.nodeStructureChanged(_node);
		}
	}

	public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {

	}

	protected Tree getTree() {
		return tree;
	}
	
	public void setTree(Tree _tree) {
		tree = _tree;
	}
	
	private boolean modelEmpty;
	private Tree tree;
}
