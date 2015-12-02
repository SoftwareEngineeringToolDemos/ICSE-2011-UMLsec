package tum.umlsec.viki.tools.dynaviki.model.promela;

import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_ApplyKey;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_Assignment;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_BASE;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_CompareOperator;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_CompareOperatorEqual;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_CompareOperatorNotEqual;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_Concatenation;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_EffectBase;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_EffectList;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_FunctionCall;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_Guard;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_GuardElse;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_GuardVirt;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_NonceOf;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_ParameterList;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_ParameterListCont;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_ParameterListEnd;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_PublicKeyOf;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_RTerm;
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
public class VisitorInitialiseAll extends VisitorAdaptor {
	
	public void visit(USE_FunctionCall _functionCall) {		v(_functionCall);	}
	public void visit(USE_Assignment _assignment) { 		v(_assignment);		}
	public void visit(USE_BASE _BASE) { 					v(_BASE);			}
	public void visit(USE_EffectBase _effectBase) { v(_effectBase); }
	public void visit(USE_EffectList _effectList) { v(_effectList); }
	public void visit(USE_GuardVirt _guardVirt) { v(_guardVirt); }
	public void visit(USE_Guard _guard) { v(_guard); }
	public void visit(USE_GuardElse _guardElse) { v(_guardElse); }
	public void visit(USE_CompareOperator _compareOperator) { v(_compareOperator); }
	public void visit(USE_CompareOperatorEqual _compareOperatorEqual) { v(_compareOperatorEqual); }
	public void visit(USE_CompareOperatorNotEqual _compareOperatorNotEqual) { v(_compareOperatorNotEqual); }
	public void visit(USE_ParameterList _parameterList) { v(_parameterList); }
	public void visit(USE_ParameterListCont _parameterListCont) { v(_parameterListCont); }
	public void visit(USE_ParameterListEnd _parameterListEnd) { v(_parameterListEnd); }
	public void visit(USE_RTerm _RTerm) { v(_RTerm); }
	public void visit(USE_Concatenation _concatenation) { v(_concatenation); }
	public void visit(USE_Select _select) { v(_select); }
	public void visit(USE_Variable _variable) { v(_variable); }
	public void visit(USE_ApplyKey _applyKey) { v(_applyKey); }
	public void visit(USE_SenderOf _senderOf) { v(_senderOf); }
	public void visit(USE_PublicKeyOf _publicKeyOf) { v(_publicKeyOf); }
	public void visit(USE_SecretKeyOf _secretKeyOf) { v(_secretKeyOf); }
	public void visit(USE_SymmetricKeyOf _symmetricKeyOf) { v(_symmetricKeyOf); }
	public void visit(USE_NonceOf _nonceOf) { v(_nonceOf); }
	public void visit(USE_This _this) { v(_this); }
	

	private void v(USE_BASE _base) {
		_base.setInitialValueComplexity(EnumExpressionComplexity.Undefined);
//		_base.setCompileTimeComplexity(EnumExpressionComplexity.Undefined); 
	}

}
