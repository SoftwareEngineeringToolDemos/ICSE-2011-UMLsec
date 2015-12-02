/*
 * Created on 04-Oct-2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package tum.umlsec.viki.tools.dynaviki.model;

import java.util.Collection;
import java.util.Vector;

import tum.umlsec.viki.framework.ExceptionProgrammLogicError;
import tum.umlsec.viki.tools.dynaviki.model.interfaces.ITreeNodeCollection;
import tum.umlsec.viki.tools.dynaviki.model.scanner.SyntaxNode;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_ApplyKey;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_Assignment;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_BASE;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_CompareOperatorEqual;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_CompareOperatorNotEqual;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_Concatenation;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_EffectList;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_FunctionCall;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_Guard;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_GuardElse;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_NonceOf;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_ParameterListCont;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_ParameterListEnd;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_PublicKeyOf;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_SecretKeyOf;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_Select;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_SenderOf;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_SymmetricKeyOf;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_This;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_Variable;

/**
 * @author pasha
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TreeNodeScannerExpression
	extends BaseObject
	implements ITreeNodeCollection {

		
	/**
	 * @param _root
	 */
	public TreeNodeScannerExpression(ModelRoot _root, SyntaxNode _expressionNode, String _description) {
		super(_root);
		expressionNode = _expressionNode;
		description = _description;
	}
	
	/* (non-Javadoc)
	 * @see tum.umlsec.viki.tools.dynaviki.model.BaseObject#createNodeStructure()
	 */
	protected void createNodeStructure() {
		children.removeAllElements();
	}


	private TreeNodeScannerExpression GetTreeViewForGrammarNode(ModelRoot _root, USE_BASE _grammarNode, String _description) {
		if(_grammarNode == null) {
			return new TreeNodeScannerExpression(_root, _grammarNode, _description);
		}

		TreeNodeScannerExpression _ret = _grammarNode.getTreeViewNode();
		if(_ret != null) {
			return _ret;
		}
		_ret = new TreeNodeScannerExpression(_root, _grammarNode, _description);
		_ret.initialise();
		_grammarNode.setTreeViewNode(_ret);
		return _ret;
	}

	/* (non-Javadoc)
	 * @see tum.umlsec.viki.tools.dynaviki.model.BaseObject#initialise()
	 */
	public void initialise() {
		createNodeStructure();
		TreeNodeScannerExpression _nn;
		
		if(expressionNode == null) {
			return;
		}
		
		if (expressionNode instanceof USE_ApplyKey) {
			USE_ApplyKey _newNode = (USE_ApplyKey) expressionNode;
				_nn = GetTreeViewForGrammarNode(getRoot(), _newNode.getTerm(), "Expression");
				children.add(_nn);
				_nn = GetTreeViewForGrammarNode(getRoot(), _newNode.getKey(), "Key"); 
				children.add(_nn);
				
		} else if(expressionNode instanceof USE_Assignment) {
			USE_Assignment _newNode = (USE_Assignment) expressionNode;
				_nn = GetTreeViewForGrammarNode(getRoot(), _newNode.getVariable(), "Variable"); 
				children.add(_nn);
				_nn = GetTreeViewForGrammarNode(getRoot(), _newNode.getValue(), "Value"); 
				children.add(_nn);
			
		} else if(expressionNode instanceof USE_CompareOperatorEqual) {
			USE_CompareOperatorEqual _newNode = (USE_CompareOperatorEqual) expressionNode;
				_nn = GetTreeViewForGrammarNode(getRoot(), null, "=="); 
				children.add(_nn);
			
		} else if(expressionNode instanceof USE_CompareOperatorNotEqual) {
			USE_CompareOperatorNotEqual _newNode = (USE_CompareOperatorNotEqual) expressionNode;
				_nn = GetTreeViewForGrammarNode(getRoot(), null, "!="); 
				children.add(_nn);
			
		} else if(expressionNode instanceof USE_Concatenation) {
			USE_Concatenation _newNode = (USE_Concatenation) expressionNode;
				_nn = GetTreeViewForGrammarNode(getRoot(), _newNode.getLTerm(), " Left"); 
				children.add(_nn);
				_nn = GetTreeViewForGrammarNode(getRoot(), _newNode.getRTerm(), "Right"); 
				children.add(_nn);

		} else if(expressionNode instanceof USE_EffectList) {
			USE_EffectList _newNode = (USE_EffectList) expressionNode;
				_nn = GetTreeViewForGrammarNode(getRoot(), _newNode.getEffect1(), " Left"); 
				children.add(_nn);
				_nn = GetTreeViewForGrammarNode(getRoot(), _newNode.getEffect2(), "Right"); 
				children.add(_nn);
				
		} else if(expressionNode instanceof USE_GuardElse) {
			USE_GuardElse _newNode = (USE_GuardElse) expressionNode;
				_nn = GetTreeViewForGrammarNode(getRoot(), null, "else"); 
				children.add(_nn);
			
		} else if(expressionNode instanceof USE_FunctionCall) {
			USE_FunctionCall _newNode = (USE_FunctionCall) expressionNode;
				_nn = GetTreeViewForGrammarNode(getRoot(), _newNode.getCallTarget(), "CallTarget"); 
				children.add(_nn);
				_nn = GetTreeViewForGrammarNode(getRoot(), null, "FunctionName: " + _newNode.getFunctionName()); 
				children.add(_nn);
				_nn = GetTreeViewForGrammarNode(getRoot(), _newNode.getParList(), "ParameterList"); 
				children.add(_nn);
			
		} else if(expressionNode instanceof USE_Guard) {
			USE_Guard _newNode = (USE_Guard) expressionNode;
				_nn = GetTreeViewForGrammarNode(getRoot(), _newNode.getLTerm(), "L"); 
				children.add(_nn);
				_nn = GetTreeViewForGrammarNode(getRoot(), _newNode.getOp(), null); 
				children.add(_nn);
				_nn = GetTreeViewForGrammarNode(getRoot(), _newNode.getRTerm(), "R"); 
				children.add(_nn);
			
		} else if(expressionNode instanceof USE_NonceOf) {
			USE_NonceOf _newNode = (USE_NonceOf) expressionNode;
				_nn = GetTreeViewForGrammarNode(getRoot(), _newNode.getTerm(), "NonceOf"); 
				children.add(_nn);
			
		} else if(expressionNode instanceof USE_ParameterListCont) {
			USE_ParameterListCont _newNode = (USE_ParameterListCont) expressionNode;
				_nn = GetTreeViewForGrammarNode(getRoot(), _newNode.getParam(), "Param"); 
				children.add(_nn);
				_nn = GetTreeViewForGrammarNode(getRoot(), _newNode.getParamList(), "List"); 
				children.add(_nn);
			
		} else if(expressionNode instanceof USE_ParameterListEnd) {
			USE_ParameterListEnd _newNode = (USE_ParameterListEnd) expressionNode;
				_nn = GetTreeViewForGrammarNode(getRoot(), _newNode.getParam(), "Param"); 
				children.add(_nn);
			
		} else if(expressionNode instanceof USE_PublicKeyOf) {
			USE_PublicKeyOf _newNode = (USE_PublicKeyOf) expressionNode;
				_nn = GetTreeViewForGrammarNode(getRoot(), _newNode.getTerm(), null); 
				children.add(_nn);
			
		} else if(expressionNode instanceof USE_SecretKeyOf) {
			USE_SecretKeyOf _newNode = (USE_SecretKeyOf) expressionNode;
				_nn = GetTreeViewForGrammarNode(getRoot(), _newNode.getTerm(), null); 
				children.add(_nn);
			
		} else if(expressionNode instanceof USE_SymmetricKeyOf) {
			USE_SymmetricKeyOf _newNode = (USE_SymmetricKeyOf) expressionNode;
				_nn = GetTreeViewForGrammarNode(getRoot(), _newNode.getTerm(), null); 
				children.add(_nn);
			
		} else if(expressionNode instanceof USE_Select) {
			USE_Select _newNode = (USE_Select) expressionNode;
				_nn = GetTreeViewForGrammarNode(getRoot(), _newNode.getTerm(), "Term"); 
				children.add(_nn);
				_nn = GetTreeViewForGrammarNode(getRoot(), null, (new Integer(_newNode.getIndex())).toString()); 
				children.add(_nn);
			
		} else if(expressionNode instanceof USE_SenderOf) {
			USE_SenderOf _newNode = (USE_SenderOf) expressionNode;
				_nn = GetTreeViewForGrammarNode(getRoot(), _newNode.getVariable(), "Variable"); 
				children.add(_nn);
			
		} else if(expressionNode instanceof USE_This) {
			USE_This _newNode = (USE_This) expressionNode;
				_nn = GetTreeViewForGrammarNode(getRoot(), null, "this"); 
				children.add(_nn);
			
		} else if(expressionNode instanceof USE_Variable) {
			USE_Variable _newNode = (USE_Variable) expressionNode;
				_nn = GetTreeViewForGrammarNode(getRoot(), null, "VarName: " + _newNode.getName()); 
				children.add(_nn);
			
		} else {
			throw new ExceptionProgrammLogicError("Unknown USE_xxx type");
		}
		
	}

	public Collection getChildren() {
		return children;
	}

	public String getNodeName() {
		if(expressionNode == null) {
			if(description == null) {
				throw new ExceptionProgrammLogicError("Completely empty ScannerExceptionTreeNode detected.");
			} else {
				return description;
			}
		} else {
			if(description == null) {
				return expressionNode.getClass().toString();
			} else {
				return description + ": " + expressionNode.getClass().toString();
			}
		}
	}

	public String getNodeText() {
		if(expressionNode == null) {
			return "";
		} else {
			return expressionNode.toString();
		}
	}




	private SyntaxNode expressionNode;
	private String description;
	private Vector children = new Vector();
	

}
