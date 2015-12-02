package tum.umlsec.viki.tools.dynaviki.tree;

import javax.swing.JTree;

/**
 * @author pasha
 */
public class Tree extends JTree {
	public Tree(TreeModel _model) {
		super(_model);
		_model.setTree(this);
		setCellRenderer(new CellRenderer());
	}
}
