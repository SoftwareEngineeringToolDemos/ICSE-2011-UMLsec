package tum.umlsec.viki.tools.mdrview.tree.nodes;

import java.util.Collection;

/**
 * @author pasha
 */
public abstract class ListGeneric extends AbstractNode {

	public ListGeneric(Collection _mdrCollection) {
		mdrCollection = _mdrCollection;
	}

	protected Collection mdrCollection;
}
