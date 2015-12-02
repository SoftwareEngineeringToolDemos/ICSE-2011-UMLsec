package tum.umlsec.viki.tools.dynaviki.model;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Vector;

import tum.umlsec.viki.tools.dynaviki.model.dataformatgraph.DFGAbstractEdge;
import tum.umlsec.viki.tools.dynaviki.model.dataformatgraph.DFGVertex;
import tum.umlsec.viki.tools.dynaviki.model.interfaces.ITreeNodeCollection;

/**
 * @author pasha
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TreeNodeDataFormatGraph extends BaseObject implements ITreeNodeCollection {

	public TreeNodeDataFormatGraph(ModelRoot _root, DFGVertex _vertex) {
		super(_root);
		vertex = _vertex;
	}

	protected void createNodeStructure() {
		children.removeAllElements();
	}

	public void initialise() {
		createNodeStructure();
		
		for(Enumeration e = vertex.EnumerateOutgoingEdges(); e.hasMoreElements() ;) {
			DFGAbstractEdge _edge = (DFGAbstractEdge)e.nextElement();
			TreeNodeDataFormatGraph _newNode = _edge.getTargetVertex().getTreeViewNode();
			if(_newNode == null) {
				_newNode = new TreeNodeDataFormatGraph(getRoot(), _edge.getTargetVertex());
				_edge.getTargetVertex().setTreeViewNode(_newNode);
				_newNode.initialise();
			}
			children.add(_newNode);
		}
	}

	public Collection getChildren() {
		return children;
	}

	/* (non-Javadoc)
	 * @see tum.umlsec.viki.tools.dynaviki.model.interfaces.ITreeNode#getNodeName()
	 */
	public String getNodeName() {
		return vertex.getFormatExpression(); 
	}

	/* (non-Javadoc)
	 * @see tum.umlsec.viki.tools.dynaviki.model.interfaces.ITreeNode#getNodeText()
	 */
	public String getNodeText() {
		return "Parameter Count: " + vertex.getParameterCount();
	}


	private DFGVertex vertex;
	private Vector children = new Vector();
}
