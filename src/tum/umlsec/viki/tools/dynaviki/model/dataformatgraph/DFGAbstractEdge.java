/*
 * Created on 06-Oct-2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package tum.umlsec.viki.tools.dynaviki.model.dataformatgraph;


/**
* @author pasha
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
*/
public class DFGAbstractEdge 
{

	public DFGAbstractEdge(DFGVertex _sourceVertex, DFGVertex _targerVertex) {
		sourceVertex = _sourceVertex;
		targetVertex = _targerVertex;
		
		sourceVertex.addOutgoingEdge(targetVertex, this);
		targetVertex.addIncomigEdge(sourceVertex, this);
	}
		
	public DFGVertex  getSourceVertex() {
		return sourceVertex;
	}

	public DFGVertex  getTargetVertex() {
		return targetVertex;
	}

	
	private DFGVertex sourceVertex;
	private DFGVertex targetVertex;
}
