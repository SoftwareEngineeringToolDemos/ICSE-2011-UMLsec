/*
 * Created on 05-Oct-2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package tum.umlsec.viki.tools.dynaviki.model.dataformatgraph;

import java.util.Collection;
import java.util.Hashtable;

import tum.umlsec.viki.framework.ExceptionProgrammLogicError;

/**
 * @author pasha
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DFGGraph {
	
	public DFGGraph() {
		rootVertex = new DFGVertex(this, "v", "v", 1);
		allVertexes.put(rootVertex.getFormatExpression(), rootVertex);
	}


	public DFGVertex getRootVertex() {
		return rootVertex;
	}


	/**
	 * @param _expressionVertex
	 * @return
	 */
	public DFGVertex FindCreateEdgeApplyKey(DFGVertex _expressionVertex) {
		
// build the new format expression		
		String _newFormatExpression = "{" + _expressionVertex.getFormatExpression() + "}k";
		String _newFormatExpressionAlphanumeric = "L" + _expressionVertex.getFormatExpressionAlphanumeric() + "Rk";
		
// check whether we have the vertex with the new format expression already
// create it if not		
		DFGVertex _newVertex = (DFGVertex)allVertexes.get(_newFormatExpression);
		if(_newVertex == null) {
			_newVertex = new DFGVertex(this, _newFormatExpression, _newFormatExpressionAlphanumeric, _expressionVertex.getParameterCount() + 1);
			allVertexes.put(_newVertex.getFormatExpression(), _newVertex);
		}

// ensure that there is a corresponding edge connecting these vertexes
		DFGEdgeApplyKey _newEdge = _expressionVertex.FindCreateEdgeApplyKey(_newVertex);
		
		return _newVertex;
	}


	/**
	 * @param _leftVertex
	 * @param _rightVertex
	 * @return
	 */
	public DFGVertex FindCreateEdgeConcatenate(DFGVertex _leftVertex, DFGVertex _rightVertex) {
		
//		build the new format expression		
		String _newFormatExpression = _leftVertex.getFormatExpression() + "::" + _rightVertex.getFormatExpression();
		String _newFormatExpressionAlphanumeric = _leftVertex.getFormatExpressionAlphanumeric() + _rightVertex.getFormatExpressionAlphanumeric();
			 
//		check whether we have the vertex with the new format expression already
//		create it if not		
		DFGVertex _newVertex = (DFGVertex)allVertexes.get(_newFormatExpression);
		if(_newVertex == null) {
			_newVertex = new DFGVertex(this, _newFormatExpression, _newFormatExpressionAlphanumeric, _leftVertex.getParameterCount() + _rightVertex.getParameterCount());
			allVertexes.put(_newVertex.getFormatExpression(), _newVertex);
		}
		 
// ensure that there is a corresponding edge connecting these vertexes
		DFGEdgeConcatenate _newEdge = _leftVertex.FindCreateEdgeConcatenate(_newVertex, _rightVertex);
			 
		return _newVertex;
	}


	/**
	 * 
	 	for the select construction we have to parse the whole expression and find 
		which part do we take (and point to that vertex)
	
		example: our expression is (a::b::{c}k::d)[3] -> {c}k == expression {var}key
		if we cannot do this, we assing it to be a variable
	 * 
	 * @param _vertex
	 * @param _index
	 * @return
	 */
	public DFGVertex FindCreateEdgeSelect(DFGVertex _vertex, int _index) {
		DFGVertex _newVertex = null;
		String _expression = _vertex.getFormatExpression(); 
		
//		try to find "our" subexpression
		int _indexCopy = _index;
		while(_indexCopy > 0) {
			_expression = EatConcatenationHead(_expression); 
			_indexCopy --;
			
		}
 	
// if we didn't find it, make it "variable" 	
 		if(_expression == null) {
			_newVertex = getRootVertex();
 		} else {
 			_expression = EatConcatenationTail(_expression);
			_newVertex = (DFGVertex)allVertexes.get(_expression);
			if(_newVertex == null) {
				throw new ExceptionProgrammLogicError("DataFormatGraph: first we concatenated a vertex, and now we can't find it...");
			}
 		}
//		ensure that there is a corresponding edge connecting these vertexes
		DFGEdgeSelect _newEdge = _vertex.FindCreateEdgeSelect(_newVertex, _index);
			 
		return _newVertex;
	}
	
	
	//
	//	returns format string without the first concatenated part. If the string is not a concatenation, 
	//  null is returned
	private String EatConcatenationHead(String _expression) {
		if(_expression == null) {
			return null;
		}
		
		int _encryptionDepth = 0;
		int cnt;
		for(cnt = 0; cnt < _expression.length(); cnt ++) {
			if(_encryptionDepth == 0 && _expression.charAt(cnt) == ':') {
				break;
			}
			if(_expression.charAt(cnt) == '{') { _encryptionDepth ++; }
			if(_expression.charAt(cnt) == '}') { _encryptionDepth --; }
		}
		
		if(cnt >= _expression.length() - 1) {
			return null;
		}
		
		return _expression.substring(cnt + 2);		
	}
	
	private String EatConcatenationTail(String _expression) {
		int _encryptionDepth = 0;
		int cnt;
		for(cnt = 0; cnt < _expression.length(); cnt ++) {
			if(_encryptionDepth == 0 && _expression.charAt(cnt) == ':') {
				break;
			}
			if(_expression.charAt(cnt) == '{') { _encryptionDepth ++; }
			if(_expression.charAt(cnt) == '}') { _encryptionDepth --; }
		}
		
		if(cnt >= _expression.length() - 1) {
			return _expression;
		}
		
		return _expression.substring(0, cnt);
	}
	
	public Collection getAllVertexes() {
		return allVertexes.values();	
	}

	public void setUsesPublicKeyOf(boolean _usesPublicKeyOf) {			this.usesPublicKeyOf = _usesPublicKeyOf; }
	public boolean isUsesPublicKeyOf() {	return usesPublicKeyOf;	}
	
	public void setUsesSecretKeyOf(boolean _usesSecretKeyOf) {			this.usesSecretKeyOf = _usesSecretKeyOf; }
	public boolean isUsesSecretKeyOf() {	return usesSecretKeyOf; }

	public void setUsesSymmetricKeyOf(boolean _usesSymmetricKeyOf) {	this.usesSymmetricKeyOf = _usesSymmetricKeyOf; }
	public boolean isUsesSymmetricKeyOf() {	return usesSymmetricKeyOf; }

	public void setUsesNonceOf(boolean _usesNonceOf) {					this.usesNonceOf = _usesNonceOf; }
	public boolean isUsesNonceOf() {		return usesNonceOf; }
	
	
	private DFGVertex rootVertex;
	Hashtable allVertexes = new Hashtable();

	
	private boolean usesPublicKeyOf = false;
	private boolean usesSecretKeyOf = false;
	private boolean usesSymmetricKeyOf = false;
	private boolean usesNonceOf = false;
}




