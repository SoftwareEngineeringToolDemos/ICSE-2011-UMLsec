package tum.umlsec.viki.tools.mdrview.tree.nodes;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author pasha
 */
public abstract class AbstractNode extends DefaultMutableTreeNode {
	public abstract boolean fillChildren();
}
