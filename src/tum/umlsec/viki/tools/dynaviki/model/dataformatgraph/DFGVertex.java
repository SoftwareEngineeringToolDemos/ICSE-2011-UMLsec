/*
 * Created on 05-Oct-2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package tum.umlsec.viki.tools.dynaviki.model.dataformatgraph;

import java.util.Enumeration;
import java.util.Hashtable;

import tum.umlsec.viki.framework.ExceptionProgrammLogicError;
import tum.umlsec.viki.tools.dynaviki.model.TreeNodeDataFormatGraph;

/**
 * @author pasha
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DFGVertex {
	
	public DFGVertex(DFGGraph _graph, String _fe, String _fea, int _parameterCount) {
		graph = _graph;
		formatExpression = _fe;
		formatExpressionAlphanumeric = _fea;
		parameterCount = _parameterCount;
		
		messageTypeId = runningMessageTypeId ++;
	}

	
/**
 * @return
 */
	public String getFormatExpression() {
		return formatExpression;
	} 

	public String getFormatExpressionAlphanumeric() {
		return formatExpressionAlphanumeric;
	} 

	public String getAsMessageType() {
		return "MT_" + formatExpressionAlphanumeric;
	} 

	public int getParameterCount() {
		return parameterCount ;
	}

	public int getMessageTypeId() {
		return messageTypeId;
	}

	public Enumeration EnumerateIncomingEdges() {
		return incomingEdges.elements();
	}

	public Enumeration EnumerateOutgoingEdges() {
		return outgoingEdges.elements();
	}
	
	public void addIncomigEdge(DFGVertex _vertex, DFGAbstractEdge _edge) {
		incomingEdges.put(_vertex, _edge);
	}

	public void addOutgoingEdge(DFGVertex _vertex, DFGAbstractEdge _edge) {
		outgoingEdges.put(_vertex, _edge);
	}


	public DFGEdgeApplyKey GetIncomingApplyKeyEdge() {
		for(Enumeration _e = EnumerateIncomingEdges(); _e.hasMoreElements() ;) {
			DFGAbstractEdge _edge = (DFGAbstractEdge)_e.nextElement();
			if(_edge instanceof DFGEdgeApplyKey) {
				return (DFGEdgeApplyKey)_edge;
			}
		}
		return null;
	}

	public DFGEdgeApplyKey GetOutgoingApplyKeyEdge() {
		for(Enumeration _e = EnumerateOutgoingEdges(); _e.hasMoreElements() ;) {
			DFGAbstractEdge _edge = (DFGAbstractEdge)_e.nextElement();
			if(_edge instanceof DFGEdgeApplyKey) {
				return (DFGEdgeApplyKey)_edge;
			}
		}
		return null;
	}


/**
 * @param _newVertex
 * @return
 */
	public DFGEdgeApplyKey FindCreateEdgeApplyKey(DFGVertex _newVertex) {
		DFGAbstractEdge _newEdge = (DFGAbstractEdge)outgoingEdges.get(_newVertex);
		if(_newEdge == null) {
			_newEdge = new DFGEdgeApplyKey(this, _newVertex);
//			outgoingEdges.put(_newEdge.getTargetVertex(), _newEdge);
		}
		if(!(_newEdge instanceof DFGEdgeApplyKey)) {
			throw new ExceptionProgrammLogicError("Wrong Edge type: not a DFGEdgeApplyKey");
		}
		return (DFGEdgeApplyKey)_newEdge;
	}

	/**
	 * @param _newVertex
	 * @return
	 */
	public DFGEdgeConcatenate FindCreateEdgeConcatenate(DFGVertex _targetVertex, DFGVertex _concatenatedVertex) {
		DFGAbstractEdge _newEdge = (DFGAbstractEdge)outgoingEdges.get(_targetVertex);
		if(_newEdge == null) {
			_newEdge = new DFGEdgeConcatenate(this, _targetVertex, _concatenatedVertex); 
//			outgoingEdges.put(_newEdge.getTargetVertex(), _newEdge);
		}
		if(!(_newEdge instanceof DFGEdgeConcatenate)) {
			throw new ExceptionProgrammLogicError("Wrong Edge type: not a DFGEdgeConcatenate");
		}
		return (DFGEdgeConcatenate)_newEdge;
	}


	/**
	 * @param _newVertex
	 * @param _index
	 * @return
	 */
	public DFGEdgeSelect FindCreateEdgeSelect(DFGVertex _targetVertex, int _index) {
		DFGAbstractEdge _newEdge = (DFGAbstractEdge)outgoingEdges.get(_targetVertex);
		if(_newEdge == null) {
			_newEdge = new DFGEdgeSelect(this, _targetVertex, _index); 
//			outgoingEdges.put(_newEdge.getTargetVertex(), _newEdge);
		}
		
		if(!(_newEdge instanceof DFGEdgeSelect)) {
			throw new ExceptionProgrammLogicError("Wrong Edge type: not a DFGEdgeSelect");
		}
		return (DFGEdgeSelect)_newEdge;
	}
	
	public TreeNodeDataFormatGraph getTreeViewNode() {
		return treeViewNode;
	}

	public void setTreeViewNode(TreeNodeDataFormatGraph _treeViewNode) {
		treeViewNode = _treeViewNode;
	}




// parent graph
	private DFGGraph graph;	


	private String formatExpression;


// we use this string in the PROMELA file in the #define
// it contains only alphanumeric symbols 
	private String formatExpressionAlphanumeric;

// inidicates how many parameters shall have this message to represent the whole expression
	private int parameterCount;
	
// indicated the integer which will appear in the DEFINE for this nessage type
	private int messageTypeId;	

//	map IncomingVertexFrom ==> Edge	
	private Hashtable incomingEdges = new Hashtable();
//	map TargetVertexTo ==> Edge	
	private Hashtable outgoingEdges = new Hashtable();

	private TreeNodeDataFormatGraph treeViewNode;
	
	
// incremented for every new vertex, value stored in the messageTypeId  
// Id = 1 is dedicated for the MT_garbage
	private static int runningMessageTypeId = 2;
}




