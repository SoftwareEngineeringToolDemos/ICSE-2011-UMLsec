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
public class DFGEdgeSelect extends DFGAbstractEdge {

	/**
	 * @param _targetVertex
	 * @param _index
	 */
	public DFGEdgeSelect(DFGVertex _sourceVertex, DFGVertex _targetVertex, int _index) {
		super(_sourceVertex, _targetVertex);
		index = _index;
	}


	private int index; 
}
