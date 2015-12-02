package tum.umlsec.viki.tools.dynaviki.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import org.omg.uml.foundation.core.Attribute;
import org.omg.uml.foundation.core.Stereotype;

import tum.umlsec.viki.tools.dynaviki.model.interfaces.ITreeNodeCollection;
import tum.umlsec.viki.tools.dynaviki.model.promela.EnumExpressionComplexity;
import tum.umlsec.viki.tools.dynaviki.model.scanner.SyntaxNode;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_BASE;

/**
 * @author pasha
 */
public class MD_Attribute extends BaseObject implements ITreeNodeCollection, IStereotypedModelElement {
	public MD_Attribute(ModelRoot _root, Attribute _attribute, MD_Class _parentClass) {
		super(_root);
		mdrAttribute = _attribute;
		constant = true;
		parentClass = _parentClass;
	}

	protected void createNodeStructure() {
		children.removeAllElements();
	}

	public void initialise() {
		createNodeStructure();

		collectStereotypes();
		children.add(stereotypes);
	}
	
	private void collectStereotypes() {
		for (Iterator _iter = mdrAttribute.getStereotype().iterator(); _iter.hasNext();) {
			MD_Stereotype _stereotype = getRoot().findStereotype((Stereotype)_iter.next()); 
			stereotypes.add(_stereotype);
			if(_stereotype.getStereotypeType() == MD_Stereotype.SECRECY) {
				stereotypedAsSecrecy = true;
			}
		}
	}

	public Collection getChildren() {
		return children;
	}
	
	public Collection getStereotypes() {
		return stereotypes;
	}
	
	public void addExpressionTree(SyntaxNode parse_tree) {
		initialValue = parse_tree;
		TreeNodeScannerExpression _expressionTreeView = new TreeNodeScannerExpression(getRoot(), parse_tree, null);
		_expressionTreeView.initialise(); 
		((USE_BASE)initialValue).setTreeViewNode(_expressionTreeView);
		children.add(_expressionTreeView);
	}
	
	public boolean isInitialValueUmlsecLanguage() {
		return mdrAttribute.getInitialValue().getLanguage().trim().compareToIgnoreCase("umlsec") == 0;
	}

	public String getInitialValueExpressionRaw() {
		return mdrAttribute.getInitialValue().getBody(); 
	}

	public String getName() {
		return mdrAttribute.getName(); 
	}

	public String getNodeName() {
		return mdrAttribute.getName();
	}
	
	public boolean hasInitialValue() {
		if(mdrAttribute.getInitialValue() == null) {
			return false;
		}
		if(mdrAttribute.getInitialValue().getBody().trim().length() == 0) {
			return false;
		}
		return true;
	} 

	public String getNodeText() {
		String _ret = "Readonly: " + constant;
		_ret += "\nComplexity: " + EnumExpressionComplexity.getComplexity(variableComplexity);
		
		if(hasInitialValue()) {
			_ret += "\nInitial value (language: " + mdrAttribute.getInitialValue().getLanguage() + "): " + mdrAttribute.getInitialValue().getBody();
			
			_ret += "\n\nInitial value compiled: " + ((USE_BASE)getInitialValueExpressionTree()).getInitialValueExpression(); 
			_ret += "\nInitial value complexity: " + EnumExpressionComplexity.getComplexity(((USE_BASE)getInitialValueExpressionTree()).getInitialValueComplexity()); 
		}
		
		return _ret; 
	}

	public SyntaxNode getInitialValueExpressionTree() {
		return initialValue;
	}
	
	public String getInitialValueExpressionCompiled() {
		if(getInitialValueExpressionTree() == null) {
			return null;
		}
		return ((USE_BASE)getInitialValueExpressionTree()).getInitialValueExpression();
	}
	
	public boolean isConstant() {
		return constant;
	}
	
	public void setConstant(boolean _constant) {
		constant = _constant;
	}

	public void setVariableComplexity(int _variableComplexity) {
		variableComplexity = _variableComplexity;
	}

	public int getVariableComplexity() {
		return variableComplexity;
	}


/*
	private void compileInitialValue() {
		if(initialValueCompiled == true) {
			return;
		}
		initialValueCompiled = true;

		if(!(initialValue instanceof USE_RTerm)) {
			throw new ExceptionBadModel("Incorrect initial value format: " + mdrAttribute.getInitialValue().getBody());
		}
		
		VisitorCompileInitialValue _visitor = new VisitorCompileInitialValue(dataFormatGraph, parentClass.getSymbolTable());
		initialValue.traverseBottomUp(_visitor);
	}
	*/

//
//	public String getCompiledInitialValue() {
//		if(initialValue == null) {
//			return null;
//		}
//		compileInitialValue();
//		return ((USE_BASE)initialValue).getInitialValueExpression();
//	}
//
//	public SyntaxNode getInitialValue() {
//		compileInitialValue();
//		return initialValue;
//	}

	public boolean isStereotypedAsSecrecy() {
		return stereotypedAsSecrecy;		
	}




	private MD_Class parentClass;

	private Attribute mdrAttribute;
	private boolean constant;
	private int variableComplexity = EnumExpressionComplexity.Undefined;
	

	private Vector children = new Vector();
	
	private SyntaxNode initialValue = null;
	AuxVector stereotypes = new AuxVector("Stereotypes");
	
	boolean stereotypedAsSecrecy = false;
}
