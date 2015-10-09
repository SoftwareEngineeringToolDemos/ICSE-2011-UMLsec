package tum.umlsec.viki.tools.dynaviki.model.promela;

import tum.umlsec.viki.tools.dynaviki.model.MD_Attribute;
import tum.umlsec.viki.tools.dynaviki.model.MD_Class;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_Assignment;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_Variable;
import tum.umlsec.viki.tools.dynaviki.model.scanner.VisitorAdaptor;


/**
 * @author pasha
 *
 */
public class VisitorFindWritableVariables extends VisitorAdaptor {

	/**
	 * @param _class
	 */
	public VisitorFindWritableVariables(MD_Class _parentClass) {
		parentClass = _parentClass;
	}

	public void visit(USE_Assignment _assignment) {
		USE_Variable _targetVariable = _assignment.getVariable();
		
		MD_Attribute _attribute = parentClass.getAttribute(_targetVariable.getName());
		if(_attribute != null) {
			_attribute.setConstant(false);
		}
	}

	
//	public void visit(USE_FunctionCall uSE_FunctionCall) { visit(); }
//	public void visit(USE_BASE uSE_BASE) { visit(); }
//	public void visit(USE_EffectBase uSE_EffectBase) { visit(); }
//	public void visit(USE_EffectList uSE_EffectList) { visit(); }
//	public void visit(USE_GuardVirt uSE_GuardVirt) { visit(); }
//	public void visit(USE_Guard uSE_Guard) { visit(); }
//	public void visit(USE_GuardElse uSE_GuardElse) { visit(); }
//	public void visit(USE_CompareOperator uSE_CompareOperator) { visit(); }
//	public void visit(USE_CompareOperatorEqual uSE_CompareOperatorEqual) { visit(); }
//	public void visit(USE_CompareOperatorNotEqual uSE_CompareOperatorNotEqual) { visit(); }
//	public void visit(USE_ParameterList uSE_ParameterList) { visit(); }
//	public void visit(USE_ParameterListCont uSE_ParameterListCont) { visit(); }
//	public void visit(USE_ParameterListEnd uSE_ParameterListEnd) { visit(); }
//	public void visit(USE_RTerm uSE_RTerm) { visit(); }
//	public void visit(USE_Concatenation uSE_Concatenation) { visit(); }
//	public void visit(USE_Select uSE_Select) { visit(); }
//	public void visit(USE_Variable uSE_Variable) { visit(); }
//	public void visit(USE_ApplyKey uSE_ApplyKey) { visit(); }
//	public void visit(USE_SenderOf uSE_SenderOf) { visit(); }
//	public void visit(USE_PublicKeyOf uSE_PublicKeyOf) { visit(); }
//	public void visit(USE_SecretKeyOf uSE_SecretKeyOf) { visit(); }
//	public void visit(USE_SymmetricKeyOf uSE_SymmetricKeyOf) { visit(); }
//	public void visit(USE_NonceOf uSE_NonceOf) { visit(); }
//	public void visit(USE_This uSE_This) { visit(); }
	

	MD_Class parentClass;
}
