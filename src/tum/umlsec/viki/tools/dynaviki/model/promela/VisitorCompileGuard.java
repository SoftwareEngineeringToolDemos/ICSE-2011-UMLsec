package tum.umlsec.viki.tools.dynaviki.model.promela;

import tum.umlsec.viki.framework.ExceptionProgrammLogicError;
import tum.umlsec.viki.tools.dynaviki.model.ExceptionBadModel;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_ApplyKey;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_Assignment;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_CompareOperatorEqual;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_CompareOperatorNotEqual;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_Concatenation;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_EffectBase;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_EffectList;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_FunctionCall;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_Guard;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_GuardElse;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_NonceOf;
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
public class VisitorCompileGuard extends VisitorAdaptor {
	
	public void initialise() {
		complexVariableRoot = null;
	}
	
	
	
	public void visit(USE_Variable _variable) {
		_variable.setCompiledExpression(_variable.getName());	
		
		switch(_variable.getVariableType()) {
			case EnumVariableType.Association:
				_variable.setExpressionComplexity(EnumExpressionComplexity.Simple);
			break;
			
			case EnumVariableType.Constant:
				_variable.setExpressionComplexity(EnumExpressionComplexity.Simple);
			break;
			
			case EnumVariableType.LocalVariable:
				_variable.setExpressionComplexity(_variable.getAttributeModel().getVariableComplexity());
			break;

			default:
				throw new ExceptionProgrammLogicError("Unknown Variable Type in VisitorCompileGuard");			
		}		
	}

	public void visit(USE_ApplyKey _applyKey) {
		USE_RTerm _term = _applyKey.getTerm();
		USE_RTerm _key = _applyKey.getKey();
		
		
		if(_term instanceof USE_Variable) {
			USE_Variable _variable = (USE_Variable) _term;
			
			if(_variable.getVariableType() != EnumVariableType.LocalVariable) {
				throw new ExceptionProgrammLogicError("Strange GUARD format: non-variable under ApplyKey");
			} 
			if(_variable.getAttributeModel().getVariableComplexity() != EnumExpressionComplexity.Complex) {
				throw new ExceptionProgrammLogicError("Strange GUARD format: not a complex variable under ApplyKey");
			}
			complexVariableRoot = _variable;
			_applyKey.setCompiledExpression("\tApplyKey(" + _variable.getCompiledExpression() + ", " + _key.getCompiledExpression() + ");\n");
		} else {
			_applyKey.setCompiledExpression(_term.getCompiledExpression() + "\tApplyKey(" + complexVariableRoot.getCompiledExpression() + ", " + _key.getCompiledExpression() + ");\n");
		}
		_applyKey.setExpressionComplexity(EnumExpressionComplexity.Complex);
 	}
	
	public void visit(USE_CompareOperatorEqual _compareOperatorEqual) { }
	public void visit(USE_CompareOperatorNotEqual uSE_CompareOperatorNotEqual) { }
	
	
	public void visit(USE_Guard _guard) {
		USE_RTerm _term1 = _guard.getLTerm();
		USE_RTerm _term2 = _guard.getRTerm();
		
		USE_Variable _simplePart;
		USE_RTerm _complexPart;
		
		if(_term1.getExpressionComplexity() == EnumExpressionComplexity.Simple && 
								_term2.getExpressionComplexity() == EnumExpressionComplexity.Complex) {
			_simplePart = (USE_Variable)_term1;
			_complexPart = _term2;
		} else if(_term1.getExpressionComplexity() == EnumExpressionComplexity.Complex && 
								_term2.getExpressionComplexity() == EnumExpressionComplexity.Simple) {
			_complexPart = _term1;
			_simplePart = (USE_Variable)_term2;
		} else {
			throw new ExceptionBadModel("Not supported guard format");
		}
		
		_guard.setCompiledExpression(_complexPart.getCompiledExpression() +
			"\t" + complexVariableRoot.getCompiledExpression() + ".messageType == MT_v;\n" +
			"\t" + complexVariableRoot.getCompiledExpression() + ".param1 == " + _simplePart.getCompiledExpression() + ";\n");
	}
	
	public void visit(USE_GuardElse _guard) {
		_guard.setCompiledExpression("\telse ->\n");
	}
	
	
// ======================================================================================================	

	private void incorrectGuardExpression() {
		throw new ExceptionBadModel("Bad guard expression in model." );
	} 

	public void visit(USE_FunctionCall uSE_FunctionCall) {				incorrectGuardExpression(); }
	public void visit(USE_Assignment uSE_Assignment) { 					incorrectGuardExpression(); }
	public void visit(USE_ParameterListCont uSE_ParameterListCont) { 	incorrectGuardExpression(); }
	public void visit(USE_ParameterListEnd uSE_ParameterListEnd) { 		incorrectGuardExpression(); }
	public void visit(USE_EffectBase uSE_EffectBase) {					incorrectGuardExpression(); }
	public void visit(USE_EffectList uSE_EffectList) {					incorrectGuardExpression(); }






	public void visit(USE_Concatenation uSE_Concatenation) {
		throw new ExceptionProgrammLogicError("Not implemented");
	}
	public void visit(USE_Select uSE_Select) {
		throw new ExceptionProgrammLogicError("Not implemented");
	}
	public void visit(USE_SenderOf uSE_SenderOf) {
		throw new ExceptionProgrammLogicError("Not implemented");
	}
	public void visit(USE_PublicKeyOf uSE_PublicKeyOf) {
		throw new ExceptionProgrammLogicError("Not implemented");
	}
	public void visit(USE_SecretKeyOf uSE_SecretKeyOf) {
		throw new ExceptionProgrammLogicError("Not implemented");
	}
	public void visit(USE_SymmetricKeyOf uSE_SymmetricKeyOf) {
		throw new ExceptionProgrammLogicError("Not implemented");
	}
	public void visit(USE_NonceOf uSE_NonceOf) {
		throw new ExceptionProgrammLogicError("Not implemented");
	}
	public void visit(USE_This uSE_This) {
		throw new ExceptionProgrammLogicError("Not implemented");
	}
	



	private USE_Variable complexVariableRoot; 
}
