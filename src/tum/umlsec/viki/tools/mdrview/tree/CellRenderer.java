package tum.umlsec.viki.tools.mdrview.tree;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import tum.umlsec.viki.tools.mdrview.tree.nodes.ListGeneric;


/**
 * @author pasha
 *
 */
public class CellRenderer extends DefaultTreeCellRenderer {
	public Component getTreeCellRendererComponent(
		JTree tree,
		Object value,
		boolean sel,
		boolean expanded,
		boolean leaf,
		int row,
		boolean hasFocus
	)
	{
		if(value instanceof ListGeneric) {
			return super.getTreeCellRendererComponent(tree, value, sel, expanded, false, row, hasFocus);
		}

		return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
	}
}
