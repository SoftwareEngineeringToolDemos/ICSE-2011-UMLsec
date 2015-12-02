package tum.umlsec.viki.tools.dynaviki.model.promela;

import tum.umlsec.viki.tools.dynaviki.model.dataformatgraph.DFGGraph;
import tum.umlsec.viki.tools.dynaviki.model.dataformatgraph.DFGVertex;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_ApplyKey;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_Concatenation;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_NonceOf;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_PublicKeyOf;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_SecretKeyOf;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_Select;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_SenderOf;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_SymmetricKeyOf;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_This;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_Variable;
import tum.umlsec.viki.tools.dynaviki.model.scanner.VisitorAdaptor;

/**
 * @author pasha
 */
public class VisitorBuildTypeGraph extends VisitorAdaptor {
	
	public VisitorBuildTypeGraph(DFGGraph _dataFormatGraph) {
		dataFormatGraph = _dataFormatGraph;
	}
		
	//
	//	for the "variable" construction, we point to the "graph root" which is variable
	//  
	public void visit(USE_Variable _variable) {
		_variable.setDfgVertex(dataFormatGraph.getRootVertex());
	}
	
	//
	//	(AND WE DO THE SAME FOR THE TERMS WHICH DELIVER SINGLE VAR) 
	//
	public void visit(USE_SenderOf _x)		{ _x.setDfgVertex(dataFormatGraph.getRootVertex()); }
	public void visit(USE_This _x)			{ _x.setDfgVertex(dataFormatGraph.getRootVertex()); }

	public void visit(USE_PublicKeyOf _x)	{ dataFormatGraph.setUsesPublicKeyOf(true); _x.setDfgVertex(dataFormatGraph.getRootVertex()); }
	public void visit(USE_SecretKeyOf _x)	{ dataFormatGraph.setUsesSecretKeyOf(true); _x.setDfgVertex(dataFormatGraph.getRootVertex()); }
	public void visit(USE_SymmetricKeyOf _x){ dataFormatGraph.setUsesSymmetricKeyOf(true); _x.setDfgVertex(dataFormatGraph.getRootVertex()); }
	public void visit(USE_NonceOf _x)		{ dataFormatGraph.setUsesNonceOf(true); _x.setDfgVertex(dataFormatGraph.getRootVertex()); }


	
	//
	//	for the "ApplyKey" construction, we extend the expression which we are encrypting  
	//
	public void visit(USE_ApplyKey _expression) {
		DFGVertex _expressionVertex = _expression.getTerm().getDfgVertex();
		DFGVertex _newExpressionVertex = dataFormatGraph.FindCreateEdgeApplyKey(_expressionVertex);  
		_expression.setDfgVertex(_newExpressionVertex);
	}
	

	//	
	// we create the concatenated vertex	
	//	
	public void visit(USE_Concatenation _expression) {
		DFGVertex _leftVertex = _expression.getLTerm().getDfgVertex();
		DFGVertex _rightVertex = _expression.getRTerm().getDfgVertex();
		DFGVertex _newExpressionVertex = dataFormatGraph.FindCreateEdgeConcatenate(_leftVertex, _rightVertex);  
		_expression.setDfgVertex(_newExpressionVertex);
	}


	//
	// 	for the select construction we have to parse the whole expression and find 
	//	which part do we take (and point to that vertex)
	//
	//	example: our expression is (a::b::{c}k::d)[3] -> {c}k == expression {var}key
	//	if we cannot do this, we assing it to be a variable
	//
	public void visit(USE_Select _expression) {
		DFGVertex _vertex =  _expression.getTerm().getDfgVertex();
		int _index = _expression.getIndex();
		DFGVertex _newExpressionVertex = dataFormatGraph.FindCreateEdgeSelect(_vertex, _index);
		_expression.setDfgVertex(_newExpressionVertex);
	}






	private DFGGraph dataFormatGraph;
}
