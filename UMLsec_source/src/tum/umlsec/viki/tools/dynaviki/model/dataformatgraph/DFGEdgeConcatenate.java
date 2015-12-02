/*
 * Created on 07-Oct-2003
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
public class DFGEdgeConcatenate extends DFGAbstractEdge {


	/**
	 * @param _targetVertex
	 * @param _concatenatedVertex
	 */
	public DFGEdgeConcatenate(DFGVertex _sourceVertex, DFGVertex _targetVertex, DFGVertex _concatenatedVertex) {
		super(_sourceVertex, _targetVertex);
		concatenatedVertex = _concatenatedVertex;
	}

	private DFGVertex concatenatedVertex;	
}
