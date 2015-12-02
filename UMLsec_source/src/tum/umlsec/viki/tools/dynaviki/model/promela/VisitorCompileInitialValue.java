package tum.umlsec.viki.tools.dynaviki.model.promela;

import tum.umlsec.viki.framework.ExceptionProgrammLogicError;
import tum.umlsec.viki.tools.dynaviki.model.ExceptionBadModel;
import tum.umlsec.viki.tools.dynaviki.model.dataformatgraph.DFGGraph;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_ApplyKey;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_Assignment;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_CompareOperatorEqual;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_CompareOperatorNotEqual;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_Concatenation;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_EffectList;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_FunctionCall;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_Guard;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_GuardElse;
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
import tum.umlsec.viki.tools.dynaviki.model.symtable.SymbolTable;


/**
 * @author pasha
 *
 */
public class VisitorCompileInitialValue extends VisitorAdaptor {
	
	public VisitorCompileInitialValue(DFGGraph _dataFormatGraph, SymbolTable _symbolTable) {
		dataFormatGraph = _dataFormatGraph;
		symbolTable = _symbolTable;
	}
	
	public void visit(USE_This _this) {
		_this.setInitialValueExpression(PromelaTranslator.RV_THIS_ID);
	}
	
	public void visit(USE_Variable _variable) {
		if(_variable.getVariableType() == EnumVariableType.Association) {
			_variable.setInitialValueExpression(_variable.getName());
			_variable.setInitialValueComplexity(EnumExpressionComplexity.Simple);
		} else if(_variable.getVariableType() == EnumVariableType.Constant) {
			_variable.setInitialValueExpression(_variable.getName());
			_variable.setInitialValueComplexity(EnumExpressionComplexity.Simple);
		} else if(_variable.getVariableType() == EnumVariableType.LocalVariable) {
			throw new ExceptionBadModel("Illegal symbol in Initial Value: " + _variable.getName());
		} else {
			throw new ExceptionProgrammLogicError("Unknown var type in the VisitorCompileInitialValue");
		}
	}
	
	public void visit(USE_PublicKeyOf _pkeyof) {
		USE_RTerm _term = _pkeyof.getTerm();
		
		
		if(_term instanceof USE_This) {
			_pkeyof.setInitialValueExpression("PublicKeyOf(" + PromelaTranslator.RV_THIS_ID + ")");
			_pkeyof.setInitialValueComplexity(EnumExpressionComplexity.Simple);
		} else if(_term instanceof USE_Variable) {
			USE_Variable _var = (USE_Variable) _term;
			String _varName = _var.getName();
			if(_var.getVariableType() == EnumVariableType.Association) {
				_pkeyof.setInitialValueExpression("PublicKeyOf(" + _var.getInitialValueExpression() + ")");
				_pkeyof.setInitialValueComplexity(EnumExpressionComplexity.Simple);
			} else if(_var.getVariableType() == EnumVariableType.Constant) {
				if(_varName.substring(_varName.length() - 2).compareTo("id") == 0) {
					_pkeyof.setInitialValueExpression(_varName.substring(0, _varName.length() - 3) + "pk");
					_pkeyof.setInitialValueComplexity(EnumExpressionComplexity.Simple);
				} else {
					throw new ExceptionBadModel("Error in Initial Value: invalid argument for PublicKeyOf: " + _var.getName());
				}
			} else if(_var.getVariableType() == EnumVariableType.LocalVariable) {
				throw new ExceptionBadModel("Illegal symbol in Initial Value: " + _var.getName());
			} else {
				throw new ExceptionProgrammLogicError("Unknown var type in the VisitorCompileInitialValue");
			}
		} else {
			throw new ExceptionProgrammLogicError("Not implemented: VisitorCompileInitialValue::visit(USE_PublicKeyOf)");
		}
	}
	
	public void visit(USE_SecretKeyOf _skeyof) {
		USE_RTerm _term = _skeyof.getTerm();
		
		if(_term instanceof USE_This) {
			_skeyof.setInitialValueExpression("SecretKeyOf(" + PromelaTranslator.RV_THIS_ID + ")");
			_skeyof.setInitialValueComplexity(EnumExpressionComplexity.Simple);
		} else if(_term instanceof USE_Variable) {
			USE_Variable _var = (USE_Variable) _term;
			String _varName = _var.getName();
			if(_var.getVariableType() == EnumVariableType.Association) {
				_skeyof.setInitialValueExpression("SecretKeyOf(" + _var.getInitialValueExpression() + ")");
				_skeyof.setInitialValueComplexity(EnumExpressionComplexity.Simple);
			} else if(_var.getVariableType() == EnumVariableType.Constant) {
				if(_varName.substring(_varName.length() - 2).compareTo("id") == 0) {
					_skeyof.setInitialValueExpression(_varName.substring(0, _varName.length() - 3) + "sk");
					_skeyof.setInitialValueComplexity(EnumExpressionComplexity.Simple);
				} else {
					throw new ExceptionBadModel("Error in Initial Value: invalid argument for SecretKeyOf: " + _var.getName());
				}
			} else if(_var.getVariableType() == EnumVariableType.LocalVariable) {
				throw new ExceptionBadModel("Illegal symbol in Initial Value: " + _var.getName());
			} else {
				throw new ExceptionProgrammLogicError("Unknown var type in the VisitorCompileInitialValue");
			}
		} else {
			throw new ExceptionProgrammLogicError("Not implemented: VisitorCompileInitialValue::visit(USE_SecretKeyOf)");
		}
	}
	
	public void visit(USE_SymmetricKeyOf _keyof) {
		USE_RTerm _term = _keyof.getTerm();
		
		if(_term instanceof USE_This) {
			_keyof.setInitialValueExpression("SymmetricKeyOf(" + PromelaTranslator.RV_THIS_ID + ")");
			_keyof.setInitialValueComplexity(EnumExpressionComplexity.Simple);
		} else if(_term instanceof USE_Variable) {
			USE_Variable _var = (USE_Variable) _term;
			String _varName = _var.getName();
			if(_var.getVariableType() == EnumVariableType.Association) {
				_keyof.setInitialValueExpression("SymmetricKeyOf(" + _var.getInitialValueExpression() + ")");
				_keyof.setInitialValueComplexity(EnumExpressionComplexity.Simple);
			} else if(_var.getVariableType() == EnumVariableType.Constant) {
				if(_varName.substring(_varName.length() - 2).compareTo("id") == 0) {
					_keyof.setInitialValueExpression(_varName.substring(0, _varName.length() - 3) + "k");
					_keyof.setInitialValueComplexity(EnumExpressionComplexity.Simple);
				} else {
					throw new ExceptionBadModel("Error in Initial Value: invalid argument for SymmetricKeyOf: " + _var.getName());
				}
			} else if(_var.getVariableType() == EnumVariableType.LocalVariable) {
				throw new ExceptionBadModel("Illegal symbol in Initial Value: " + _var.getName());
			} else {
				throw new ExceptionProgrammLogicError("Unknown var type in the VisitorCompileInitialValue");
			}
		} else {
			throw new ExceptionProgrammLogicError("Not implemented: VisitorCompileInitialValue::visit(USE_SymmetricKeyOf)");
		}
	}
	
	public void visit(USE_NonceOf _nonceof) {
		USE_RTerm _term = _nonceof.getTerm();
		
		
		if(_term instanceof USE_This) {
			_nonceof.setInitialValueExpression("NonceOf(" + PromelaTranslator.RV_THIS_ID + ")");
			_nonceof.setInitialValueComplexity(EnumExpressionComplexity.Simple);
		} else if(_term instanceof USE_Variable) {
			USE_Variable _var = (USE_Variable) _term;
			String _varName = _var.getName();
			if(_var.getVariableType() == EnumVariableType.Association) {
				_nonceof.setInitialValueExpression("NonceOf(" + _var.getInitialValueExpression() + ")");
				_nonceof.setInitialValueComplexity(EnumExpressionComplexity.Simple);
			} else if(_var.getVariableType() == EnumVariableType.Constant) {
				if(_varName.substring(_varName.length() - 2).compareTo("id") == 0) {
					_nonceof.setInitialValueExpression(_varName.substring(0, _varName.length() - 3) + "n");
					_nonceof.setInitialValueComplexity(EnumExpressionComplexity.Simple);
				} else {
					throw new ExceptionBadModel("Error in Initial Value: invalid argument for NonceOf: " + _var.getName());
				}
			} else if(_var.getVariableType() == EnumVariableType.LocalVariable) {
				throw new ExceptionBadModel("Illegal symbol in Initial Value: " + _var.getName());
			} else {
				throw new ExceptionProgrammLogicError("Unknown var type in the VisitorCompileInitialValue");
			}
		} else {
			throw new ExceptionProgrammLogicError("Not implemented: VisitorCompileInitialValue::visit(USE_NonceOf)");
		}
	}

	public void visit(USE_Concatenation uSE_Concatenation) {
		throw new ExceptionProgrammLogicError("Not implemented: VisitorCompileInitialValue::visit(USE_Concatenation)");
	}
	
	public void visit(USE_Select uSE_Select) { 
		throw new ExceptionProgrammLogicError("Not implemented: VisitorCompileInitialValue::visit(USE_Select)");
	}
	
	public void visit(USE_ApplyKey _applyKey) {
		_applyKey.setInitialValueExpression("ApplyKey(" + _applyKey.getTerm() + ", " + _applyKey.getKey() + ")");
		_applyKey.setInitialValueComplexity(EnumExpressionComplexity.Complex);
	}








	public void visit(USE_SenderOf uSE_SenderOf) { 
		throw new ExceptionBadModel("Wrong Expression in Initial Value for a variable.");  
	}
	public void visit(USE_FunctionCall uSE_FunctionCall) {
		throw new ExceptionBadModel("Wrong Expression in Initial Value for a variable.");  
	}
	public void visit(USE_Assignment uSE_Assignment) { 
		throw new ExceptionBadModel("Wrong Expression in Initial Value for a variable.");  
	}
	public void visit(USE_EffectList uSE_EffectList) { 
		throw new ExceptionBadModel("Wrong Expression in Initial Value for a variable.");  
	}
	public void visit(USE_Guard uSE_Guard) { 
		throw new ExceptionBadModel("Wrong Expression in Initial Value for a variable.");  
	}
	public void visit(USE_GuardElse uSE_GuardElse) { 
		throw new ExceptionBadModel("Wrong Expression in Initial Value for a variable.");  
	}
	public void visit(USE_CompareOperatorEqual uSE_CompareOperatorEqual) { 
		throw new ExceptionBadModel("Wrong Expression in Initial Value for a variable.");  
	}
	public void visit(USE_CompareOperatorNotEqual uSE_CompareOperatorNotEqual) { 
		throw new ExceptionBadModel("Wrong Expression in Initial Value for a variable.");  
	}
	public void visit(USE_ParameterList uSE_ParameterList) {  
		throw new ExceptionBadModel("Wrong Expression in Initial Value for a variable.");  
	}
	public void visit(USE_ParameterListCont uSE_ParameterList) {  
		throw new ExceptionBadModel("Wrong Expression in Initial Value for a variable.");  
	}
	public void visit(USE_ParameterListEnd uSE_ParameterList) {  
		throw new ExceptionBadModel("Wrong Expression in Initial Value for a variable.");  
	}







	
	




	
	
	
	
	
	
	
	
	
	
	
	

//
//
//	// TODO 
//	public void visit(USE_Concatenation _concatenation) {
//		
////		_concatenation.setInitialValueExpression(_concatenation.getLTerm().getInitialValueExpression())
//		  
//		throw new ExceptionProgrammLogicError("Not implemented");
//	
//	}
//
//
//
//
//
//	// TODO
//	public void visit(USE_ApplyKey uSE_ApplyKey) { 
//		 
//		throw new ExceptionProgrammLogicError("Not implemented");
//		 
//		 
//	}
//	
//	
	
	
	
//	public void visit(USE_Select uSE_Select) { visit(); }



	private DFGGraph dataFormatGraph;
	private SymbolTable symbolTable; 
}
