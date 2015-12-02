package tum.umlsec.viki.tools.dynaviki.model.promela;

import tum.umlsec.viki.framework.ExceptionProgrammLogicError;
import tum.umlsec.viki.tools.dynaviki.model.ExceptionBadModel;
import tum.umlsec.viki.tools.dynaviki.model.MD_Attribute;
import tum.umlsec.viki.tools.dynaviki.model.MD_Class;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_ApplyKey;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_Assignment;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_BASE;
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
public class VisitorFindSimpleVariables extends VisitorAdaptor {
	
	public VisitorFindSimpleVariables(MD_Class _classModel) {
		classModel = _classModel;
	}
	
	
	
	public void visit(USE_PublicKeyOf _publicKeyOf) {
		_publicKeyOf.setExpressionComplexity(EnumExpressionComplexity.Simple);
	}
	public void visit(USE_SecretKeyOf _secretKeyOf) {
		_secretKeyOf.setExpressionComplexity(EnumExpressionComplexity.Simple);
	}
	public void visit(USE_SymmetricKeyOf _symmetricKeyOf) {
		_symmetricKeyOf.setExpressionComplexity(EnumExpressionComplexity.Simple);
	}
	public void visit(USE_NonceOf _nonceOf) {
		_nonceOf.setExpressionComplexity(EnumExpressionComplexity.Simple);
	}
	public void visit(USE_This _this) {
		_this.setExpressionComplexity(EnumExpressionComplexity.Simple);
	}
	public void visit(USE_SenderOf _senderOf) {
		_senderOf.setExpressionComplexity(EnumExpressionComplexity.Simple);
	}

	
	public void visit(USE_ApplyKey _applyKey) { 
		_applyKey.setExpressionComplexity(EnumExpressionComplexity.Complex);
	}
	public void visit(USE_Concatenation _concatenation) {
		_concatenation.setExpressionComplexity(EnumExpressionComplexity.Complex);
	}

	public void visit(USE_Variable _variable) {
				
		switch(_variable.getVariableType()) {
			case EnumVariableType.Association:
				_variable.setExpressionComplexity(EnumExpressionComplexity.Simple);
			break;
			
			case EnumVariableType.Constant:
				_variable.setExpressionComplexity(EnumExpressionComplexity.Simple);
			break;
			
			case EnumVariableType.LocalVariable:
// smart-copy the value from the attribute
				MD_Attribute _attribute = classModel.getAttribute(_variable.getName());
				if(_attribute.getVariableComplexity() == EnumExpressionComplexity.Complex) {
					if(_variable.getExpressionComplexity() != EnumExpressionComplexity.Complex) {
						_variable.setExpressionComplexity(EnumExpressionComplexity.Complex);
						setChangesMade(true);
					}
				} else if(_attribute.getVariableComplexity() == EnumExpressionComplexity.Simple) {
					if(_variable.getExpressionComplexity() == EnumExpressionComplexity.Undefined) {
						_variable.setExpressionComplexity(EnumExpressionComplexity.Simple);
						setChangesMade(true);
					}
				} else if(_attribute.getVariableComplexity() == EnumExpressionComplexity.Undefined) {
					if(_attribute.isConstant()) {
						if(((USE_BASE)_attribute.getInitialValueExpressionTree()).getInitialValueComplexity() == EnumExpressionComplexity.Simple) {
							_attribute.setVariableComplexity(EnumExpressionComplexity.Simple);
							setChangesMade(true);
						} else if(((USE_BASE)_attribute.getInitialValueExpressionTree()).getInitialValueComplexity() == EnumExpressionComplexity.Simple) {
							_attribute.setVariableComplexity(EnumExpressionComplexity.Complex);
							setChangesMade(true);
						}
					}
				}
			break;
			
			default:
				String mm = classModel.getName();
				String ss = _variable.getName();
				throw new ExceptionProgrammLogicError("Unknown Variable Type in VisitorFindSimpleVariables");			
		}
	}

	public void visit(USE_Assignment _assignment) {
		
		switch(_assignment.getVariable().getVariableType()) {
			case EnumVariableType.Association:
				throw new ExceptionBadModel("Association is assigned a value: " + _assignment.getVariable().getName());			
			
			case EnumVariableType.Constant:
				throw new ExceptionBadModel("Constant is assigned a value: " + _assignment.getVariable().getName());			
			
			case EnumVariableType.LocalVariable:
// this one is OKAY			
			break;
			
			default:
				throw new ExceptionProgrammLogicError("Unknown Variable Type in VisitorFindSimpleVariables");			
		}
				
				
		int _classAttributeComplexity = classModel.getAttribute(_assignment.getVariable().getName()).getVariableComplexity();
		int _valueComplexity = _assignment.getValue().getExpressionComplexity();

		switch(_classAttributeComplexity) {
			case EnumExpressionComplexity.Undefined:
				if(_assignment.getValue().getExpressionComplexity() != EnumExpressionComplexity.Undefined) {
					_assignment.getVariable().setExpressionComplexity(_valueComplexity);
					classModel.getAttribute(_assignment.getVariable().getName()).setVariableComplexity(_valueComplexity);
					setChangesMade(true);
				}
			break;
			
			case EnumExpressionComplexity.Simple:
				if(_assignment.getValue().getExpressionComplexity() == EnumExpressionComplexity.Complex) {
					_assignment.getVariable().setExpressionComplexity(EnumExpressionComplexity.Complex);
					classModel.getAttribute(_assignment.getVariable().getName()).setVariableComplexity(EnumExpressionComplexity.Complex);
					setChangesMade(true);
				}
			break;
			
			case EnumExpressionComplexity.Complex:
				// once it is complex, we do not change it any more
			break;
			
			default:
				throw new ExceptionProgrammLogicError("Unknown Variable Complexity in VisitorFindSimpleVariables");			
		}
	}


// TODO maybe you can improve this statement?
// (so that it is not always Complex)	
	public void visit(USE_Select _select) { 
		_select.setExpressionComplexity(EnumExpressionComplexity.Complex);
	}






	public boolean isChagesMade() {
		return changesMade;
	}
	public void setChangesMade(boolean _changes) {
		changesMade = _changes;
	}

	boolean changesMade = false;
	MD_Class classModel;
}


