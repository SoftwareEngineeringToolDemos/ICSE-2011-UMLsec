package tum.umlsec.viki.tools.dynaviki.model.promela;

import java.util.Iterator;

import tum.umlsec.viki.framework.ExceptionProgrammLogicError;
import tum.umlsec.viki.tools.dynaviki.model.ExceptionBadModel;
import tum.umlsec.viki.tools.dynaviki.model.MD_AssociationEnd;
import tum.umlsec.viki.tools.dynaviki.model.MD_Attribute;
import tum.umlsec.viki.tools.dynaviki.model.MD_Class;
import tum.umlsec.viki.tools.dynaviki.model.MD_Operation;
import tum.umlsec.viki.tools.dynaviki.model.ModelRoot;
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

/**
 * @author pasha
 */
public class VisitorCompileEffect extends VisitorAdaptor {
	
	public VisitorCompileEffect(ModelRoot _modelRoot, MD_Class _thisClassModel) {
		modelRoot = _modelRoot;
		thisClassModel = _thisClassModel;
		temporaryComplexVariableUsed = false;
		temporarySimpleVariableUsed = false;
	} 
	
	public void visit(USE_Variable _variable) {
		switch(_variable.getVariableType()) {
			
			case EnumVariableType.Association:
				_variable.setCompiledExpression(_variable.getName());
//				_variable.setCompileTimeComplexity(EnumExpressionComplexity.Simple);
			break;
			
			case EnumVariableType.Constant:
				_variable.setCompiledExpression(_variable.getName());
//				_variable.setCompileTimeComplexity(EnumExpressionComplexity.Simple);
			break;
			
			case EnumVariableType.LocalVariable:
//				switch(_variable.getExpressionComplexity()) {
//					case EnumExpressionComplexity.Simple:
//						_variable.setCompiledExpression(_variable.getName());
//					break;
//					
//					case EnumExpressionComplexity.Complex:
//						if(temporaryVariableUsed) {
//							throw new ExceptionProgrammLogicError("Not implemented - needs more then one temp var");
//						}
//						temporaryVariableUsed = true;
//						_variable.setCompiledExpression("\n\tAssignComplexToComplex(" + PromelaTranslator.RV_CURRENT_MESSAGE + ", " + _variable.getName() + ";");
//					break;
//					
//					case EnumExpressionComplexity.Undefined:
//						throw new ExceptionProgrammLogicError("Expression complexity is undefined");
//					
//					default:
//						throw new ExceptionProgrammLogicError("Unknown expression complexity");
//					
//				}
			
				_variable.setCompiledExpression(_variable.getName());
				
				
//				_variable.setCompileTimeComplexity(_variable.getExpressionComplexity());
			break;
			
			default:
				throw new ExceptionProgrammLogicError("Unknown variable type in VisitorCompileEffect");	
		}
	}	

	public void visit(USE_This _this) {
		_this.setCompiledExpression(PromelaTranslator.RV_THIS_ID); 
//		_this.setCompileTimeComplexity(EnumExpressionComplexity.Simple);
	}

	public void visit(USE_PublicKeyOf _publicKeyOf) {
		if(_publicKeyOf.getTerm().getExpressionComplexity() == EnumExpressionComplexity.Simple) {
			_publicKeyOf.setCompiledExpression("PublicKeyOf(" + _publicKeyOf.getTerm().getCompiledExpression() + ")");
//			_publicKeyOf.setCompileTimeComplexity(EnumExpressionComplexity.Simple);
		} else {
			// we need a "complex -> simple" runtime conversion function which
			// gives back a simple value if the expression is simple or garbage otherwise  
			throw new ExceptionProgrammLogicError("Not implemented");
//			!!!! set compile time complexity everywhere
		}
	}

	public void visit(USE_SecretKeyOf _secretKeyOf) { 
		if(_secretKeyOf.getTerm().getExpressionComplexity() == EnumExpressionComplexity.Simple) {
			_secretKeyOf.setCompiledExpression("SecretKeyOf(" + _secretKeyOf.getTerm().getCompiledExpression() + ")");
//			_secretKeyOf.setCompileTimeComplexity(EnumExpressionComplexity.Simple);
		} else {
			// we need a "complex -> simple" runtime conversion function which
			// gives back a simple value if the expression is simple or garbage otherwise  
			throw new ExceptionProgrammLogicError("Not implemented");
//			!!!! set compile time complexity everywhere
		}
	}

	public void visit(USE_SymmetricKeyOf _symmetricKeyOf) { 
		if(_symmetricKeyOf.getTerm().getExpressionComplexity() == EnumExpressionComplexity.Simple) {
			_symmetricKeyOf.setCompiledExpression("SymmetricKeyOf(" + _symmetricKeyOf.getTerm().getCompiledExpression() + ")");
//			_symmetricKeyOf.setCompileTimeComplexity(EnumExpressionComplexity.Simple);
		} else {
			// we need a "complex -> simple" runtime conversion function which
			// gives back a simple value if the expression is simple or garbage otherwise  
			throw new ExceptionProgrammLogicError("Not implemented");
//			!!!! set compile time complexity everywhere
		}
	}

	public void visit(USE_NonceOf _nonceOf) {
		if(_nonceOf.getTerm().getExpressionComplexity() == EnumExpressionComplexity.Simple) {
			_nonceOf.setCompiledExpression("NonceOf(" + _nonceOf.getTerm().getCompiledExpression() + ")");
//			_nonceOf.setCompileTimeComplexity(EnumExpressionComplexity.Simple);
		} else {
			// we need a "complex -> simple" runtime conversion function which
			// gives back a simple value if the expression is simple or garbage otherwise  
			throw new ExceptionProgrammLogicError("Not implemented");
//			!!!! set compile time complexity everywhere
		}
	}

	public void visit(USE_ApplyKey _applyKey) {
		USE_RTerm _rt =  _applyKey.getKey();
		
		
		StringBuffer _sb = new StringBuffer();
		switch(_applyKey.getKey().getExpressionComplexity()) {
			case EnumExpressionComplexity.Simple: {
				switch(_applyKey.getTerm().getExpressionComplexity()) {
					case EnumExpressionComplexity.Complex:
						USE_RTerm _term = _applyKey.getTerm();
						if (_term instanceof USE_Variable) {
							USE_Variable _var = (USE_Variable)_term;
					
							if(temporaryComplexVariableUsed) {
								throw new ExceptionProgrammLogicError("Not implemented - needs more then one temp var");
							}
							temporaryComplexVariableUsed = true;
							_sb.append("\tAssignComplexToComplex(" + PromelaTranslator.RV_CURRENT_MESSAGE + ", " + _var.getName() + ");\n");
							_sb.append("\tApplyKey(" + PromelaTranslator.RV_CURRENT_MESSAGE + ", " + _applyKey.getKey().getCompiledExpression() + ");");
						} else {
							_sb.append(_applyKey.getTerm().getCompiledExpression() + "\n");
							_sb.append("\tApplyKey(" + PromelaTranslator.RV_CURRENT_MESSAGE + ", " + _applyKey.getKey().getCompiledExpression() + ");");
						}
					break;
			
					case EnumExpressionComplexity.Simple:
//		   converting to the complex var
						if(temporaryComplexVariableUsed) {
							throw new ExceptionProgrammLogicError("Not implemented - several complex temp vars required");
						}
						temporaryComplexVariableUsed = true;
						_sb.append("\tAssignSimpleToComplex(" + PromelaTranslator.RV_CURRENT_MESSAGE + ", " +  _applyKey.getTerm().getCompiledExpression() + ");\n");
						_sb.append("\tApplyKey(" + PromelaTranslator.RV_CURRENT_MESSAGE + ", " + _applyKey.getKey().getCompiledExpression() + ");");
					break;
			
					case EnumExpressionComplexity.Undefined:
					default:
						throw new ExceptionProgrammLogicError("Undefined compile time complexity");
				}
			} break;
			
			
			case EnumExpressionComplexity.Complex: {
// extract simple key from the complex expression
				_sb.append("\tAssignComplexToSimple(" + PromelaTranslator.RV_CURRENT_SIMPLE_VAR + ", " + _applyKey.getKey().getCompiledExpression() + ");\n");
				USE_RTerm _term = _applyKey.getTerm();
	
				switch(_term.getExpressionComplexity()) {
					case EnumExpressionComplexity.Complex:
						if (_term instanceof USE_Variable) {
							USE_Variable _var = (USE_Variable)_term;
							if(temporaryComplexVariableUsed) {
								throw new ExceptionProgrammLogicError("Not implemented - needs more then one temp var");
							}
							temporaryComplexVariableUsed = true;
							_sb.append("\tAssignComplexToComplex(" + PromelaTranslator.RV_CURRENT_MESSAGE + ", " + _var.getName() + ");\n");
							_sb.append("\tApplyKey(" + PromelaTranslator.RV_CURRENT_MESSAGE + ", " + PromelaTranslator.RV_CURRENT_SIMPLE_VAR + ");");
						} else {
							_sb.append(_term.getCompiledExpression() + "\n");
							_sb.append("\tApplyKey(" + PromelaTranslator.RV_CURRENT_MESSAGE + ", " + PromelaTranslator.RV_CURRENT_SIMPLE_VAR + ");");
						}
					break;
			
					case EnumExpressionComplexity.Simple:
//	   converting to the complex var
						if(temporaryComplexVariableUsed) {
							throw new ExceptionProgrammLogicError("Not implemented - several complex temp vars required");
						}
						temporaryComplexVariableUsed = true;
						_sb.append("\tAssignSimpleToComplex(" + PromelaTranslator.RV_CURRENT_MESSAGE + ", " +  _term.getCompiledExpression() + ");\n");
						_sb.append("\tApplyKey(" + PromelaTranslator.RV_CURRENT_MESSAGE + ", " + PromelaTranslator.RV_CURRENT_SIMPLE_VAR + ");");
					break;
			
					case EnumExpressionComplexity.Undefined:
					default:
						throw new ExceptionProgrammLogicError("Undefined compile time complexity");
				}
			} break;
				
			
			case EnumExpressionComplexity.Undefined:
			default:
				throw new ExceptionProgrammLogicError("Undefined compile time complexity");
		}
		
		
		_applyKey.setCompiledExpression(_sb.toString());		
//		_applyKey.setCompileTimeComplexity(EnumExpressionComplexity.Complex);
	}













// ============== these are not yet implemented but must be
	public void visit(USE_SenderOf _senderOf) {
		_senderOf.setCompiledExpression(PromelaTranslator.RV_SENDER);
//		_senderOf.setCompileTimeComplexity(EnumExpressionComplexity.Simple);
	}
	
	
	public void visit(USE_FunctionCall _functionCall) {
		StringBuffer _sb = new StringBuffer();
		
		_sb.append("\t" + PromelaTranslator.RV_CHANNEL_OUT + "!" + PromelaTranslator.RV_THIS_ID + ";");
		_sb.append("\n\t" + PromelaTranslator.RV_CHANNEL_OUT + "!" +_functionCall.getCallTarget().getCompiledExpression() + ";");
		
		
		
		USE_Variable _tv = null;
		MD_AssociationEnd _anotherAssociationEnd = null;
		if(_functionCall.getCallTarget() instanceof USE_Variable) {
			_tv = (USE_Variable)_functionCall.getCallTarget();
			if(_tv.getVariableType() == EnumVariableType.Association) {
				_anotherAssociationEnd = _tv.getAssociationEndModel();
			}
		}
		
		MD_Operation _operation = null;
		if(_anotherAssociationEnd != null) {
			_operation = _anotherAssociationEnd.getAttachedClassModel().getOperation(_functionCall.getFunctionName());
		} else {
// if not, we have to loop through all events in the system
// we take the first foudn event, which may not be the correct choice though
			for(Iterator iter = modelRoot.getClassesAll().iterator(); iter.hasNext();) {
				MD_Class _class = (MD_Class)iter.next();
			   	_operation = _class.getOperation(_functionCall.getFunctionName());
			   	if(_operation != null) {
					break;
			   	}
		   }
		}

		if(_operation == null) {
			throw new ExceptionBadModel("Unresolved Operation Name: " + _functionCall.getFunctionName());
		}
		_sb.append("\n\t" + PromelaTranslator.RV_CHANNEL_OUT + "!" + _operation.getPromelaEventName() + ";\n");

// parameters - we allow only one
		if(!(_functionCall.getParList() instanceof USE_ParameterListEnd)) {
			throw new ExceptionProgrammLogicError("Only one function parameter is currently supported");
		}
		USE_ParameterListEnd _ple = (USE_ParameterListEnd)(_functionCall.getParList());
		_sb.append(_ple.getCompiledExpression());
		
		_sb.append("\n\tSendMessage(" + PromelaTranslator.RV_CHANNEL_OUT + ", " + PromelaTranslator.RV_CURRENT_MESSAGE + ");\n");
		
		
		
		_functionCall.setCompiledExpression(_sb.toString());
// 		_functionCall.setCompileTimeComplexity(EnumExpressionComplexity.Undefined);
	}


	public void visit(USE_ParameterListEnd _parameterListEnd) {
		if(_parameterListEnd.getParam().getExpressionComplexity() == EnumExpressionComplexity.Complex) {
			_parameterListEnd.setCompiledExpression(_parameterListEnd.getParam().getCompiledExpression());
		} else {
// converting to complex			
			if(temporaryComplexVariableUsed) {
				throw new ExceptionProgrammLogicError("Not implemented - several temp vars required");
			}
			temporaryComplexVariableUsed = true;
			_parameterListEnd.setCompiledExpression("\tAssignSimpleToComplex(" + PromelaTranslator.RV_CURRENT_MESSAGE + ", " + _parameterListEnd.getParam().getCompiledExpression() + ");");
		}
//		_parameterListEnd.setExpressionComplexity(EnumExpressionComplexity.Undefined);
	}












	
	
	
	
	
	
	public void visit(USE_Select uSE_Select) {
//		!!!! set compile time complexity everywhere
		throw new ExceptionProgrammLogicError("Not implemented");
	}
	
	
	
	
	public void visit(USE_Assignment _assignment) {
		_assignment.setExpressionComplexity(EnumExpressionComplexity.Undefined);
		
		USE_Variable _variable = _assignment.getVariable();
		USE_RTerm _rTerm = _assignment.getValue();
		
		StringBuffer _sb = new StringBuffer();
		
		if(_variable.getVariableType() != EnumVariableType.LocalVariable) {
			throw new ExceptionBadModel("Assignment to invalid variable type in VisitorCompileEffect::visit(USE_Assignment)");
		}
		MD_Attribute _attribute = _variable.getAttributeModel();
		if(_attribute.getVariableComplexity() == EnumExpressionComplexity.Simple) {
			if(_rTerm.getExpressionComplexity() == EnumExpressionComplexity.Simple) {
				_sb.append("\t" + _variable.getName() + " = " + _rTerm.getCompiledExpression() + ";\n");
			} else if (_rTerm.getExpressionComplexity() == EnumExpressionComplexity.Complex) {
				if(false == temporaryComplexVariableUsed) {
					throw new ExceptionProgrammLogicError("temporaryVariableUsed must be true in VisitorCompileEffect::visit(USE_Assignment)");
				}
				_sb.append(_rTerm.getCompiledExpression());
				_sb.append("\tAssignComplexToSimple(" + _variable.getName() + ", " + PromelaTranslator.RV_CURRENT_MESSAGE + ");\n");
			} else {
				throw new ExceptionProgrammLogicError("Unknown ExpressionType in VisitorCompileEffect::visit(USE_Assignment)");
			}
		} else if(_attribute.getVariableComplexity() == EnumExpressionComplexity.Complex) {
			if(_rTerm.getExpressionComplexity() == EnumExpressionComplexity.Simple) {
				_sb.append("\tAssignSimpleToComplex(" + _variable.getName() + ", " +  _rTerm.getCompiledExpression() + ");\n");
			} else if (_rTerm.getExpressionComplexity() == EnumExpressionComplexity.Complex) {
				if(false == temporaryComplexVariableUsed) {
					throw new ExceptionProgrammLogicError("temporaryVariableUsed must be true in VisitorCompileEffect::visit(USE_Assignment)");
				}
				_sb.append(_rTerm.getCompiledExpression());
				_sb.append("\tAssignComplexToComplex(" + _variable.getName() + ", " + PromelaTranslator.RV_CURRENT_MESSAGE + ");\n");
			} else {
				throw new ExceptionProgrammLogicError("Unknown ExpressionType in VisitorCompileEffect::visit(USE_Assignment)");
			}
		} else {
			throw new ExceptionProgrammLogicError("Unknown VariableType in VisitorCompileEffect::visit(USE_Assignment)");
		}

		_assignment.setCompiledExpression(_sb.toString());		
	}
	
	public void visit(USE_EffectList uSE_EffectList) {
//		!!!! set compile time complexity everywhere
		throw new ExceptionProgrammLogicError("Not implemented");
	}
	public void visit(USE_ParameterList uSE_ParameterList) {
//		!!!! set compile time complexity everywhere
		throw new ExceptionProgrammLogicError("Not implemented");
	}
	
	
	
	
	public void visit(USE_Concatenation uSE_Concatenation) {
//		!!!! set compile time complexity everywhere
		throw new ExceptionProgrammLogicError("Not implemented");
	}





	public void visit(USE_ParameterListCont uSE_ParameterListCont) {
//		!!!! set compile time complexity everywhere
		throw new ExceptionProgrammLogicError("Not implemented -  we allow only one parameter");
	}










	public void visit(USE_CompareOperatorEqual uSE_CompareOperatorEqual) {
		throw new ExceptionBadModel("Bad model: comparison in Effect"); 
	}
	public void visit(USE_CompareOperatorNotEqual uSE_CompareOperatorNotEqual) {
		throw new ExceptionBadModel("Bad model: comparison in Effect"); 
	}
	public void visit(USE_Guard uSE_Guard) { 
		throw new ExceptionBadModel("Bad model: guard in Effect"); 
	}
	public void visit(USE_GuardElse uSE_GuardElse) {
		throw new ExceptionBadModel("Bad model: guard in Effect"); 
	}







	ModelRoot modelRoot;	
	MD_Class thisClassModel;
	boolean temporaryComplexVariableUsed;
	boolean temporarySimpleVariableUsed;
}
