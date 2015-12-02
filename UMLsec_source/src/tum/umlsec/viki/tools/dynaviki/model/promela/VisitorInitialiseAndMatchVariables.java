package tum.umlsec.viki.tools.dynaviki.model.promela;

import java.util.Iterator;

import tum.umlsec.viki.tools.dynaviki.model.ExceptionScannerUnknownVariable;
import tum.umlsec.viki.tools.dynaviki.model.MD_Association;
import tum.umlsec.viki.tools.dynaviki.model.MD_AssociationEnd;
import tum.umlsec.viki.tools.dynaviki.model.MD_Attribute;
import tum.umlsec.viki.tools.dynaviki.model.MD_Class;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_Variable;
import tum.umlsec.viki.tools.dynaviki.model.scanner.VisitorAdaptor;
import tum.umlsec.viki.tools.dynaviki.model.symtable.S_Literal;
import tum.umlsec.viki.tools.dynaviki.model.symtable.SymbolBASE;
import tum.umlsec.viki.tools.dynaviki.model.symtable.SymbolTable;


/**
 * @author pasha
 * 
 * goes through all variables and identifies what are they. The possibilities are three: 
 * local variables; associations; parameters of the events feeding the previous state 
 * in the statechart. Whereby for the initial values in the variable declarations only
 * the "associations" is valid.
 */
public class VisitorInitialiseAndMatchVariables extends VisitorAdaptor {
	
	VisitorInitialiseAndMatchVariables(MD_Class _mdClass, SymbolTable _allLiterals, boolean _matchConstants, boolean _matchVariables, boolean _matchAssociations) {
		allLiterals = _allLiterals;
		
		matchConstants = _matchConstants;
		matchVariables = _matchVariables;
		matchAssociations = _matchAssociations;
		
		mdClass = _mdClass;
	}

	private boolean matchConstant(USE_Variable _variable) {
		if(!matchConstants) {
			return false;
		}
		SymbolBASE _symbol = allLiterals.findSymbol(_variable.getName());
		if(_symbol != null && _symbol instanceof S_Literal) { 
			_variable.setVariableType(EnumVariableType.Constant);
			return true;
		}
		return false;
	}

	private boolean matchAssociation(USE_Variable _variable) {
		if(!matchAssociations) {
			return false;
		}
		
		for(Iterator _iter01 = mdClass.getAssociationEnds().iterator(); _iter01.hasNext();) {
			MD_AssociationEnd _associationEnd = (MD_AssociationEnd)_iter01.next();				
			MD_Association _association = _associationEnd.getAssociationModel();
			MD_AssociationEnd _anotherAssociationEnd = _association.getAnotherEnd(_associationEnd);
			
			if(_variable.getName().compareTo(_anotherAssociationEnd.getName()) == 0) {
				_variable.setVariableType(EnumVariableType.Association);
				_variable.setAssociationEndModel(_anotherAssociationEnd);
				return true;
			}
		}
		
		return false;
	}

	private boolean matchVariable(USE_Variable _variable) {
		if(!matchVariables) {
			return false;
		}
		for(Iterator _iter01 = mdClass.getAttributes().iterator(); _iter01.hasNext();) {				
			MD_Attribute _attribute = (MD_Attribute)_iter01.next();
			
			if(_variable.getName().compareTo(_attribute.getName()) == 0) {
				_variable.setVariableType(EnumVariableType.LocalVariable);
				_variable.setAttributeModel(_attribute);
				return true;
			}
		}
		return false;
	}

	public void visit(USE_Variable _variable) {
		_variable.setExpressionComplexity(EnumExpressionComplexity.Undefined);
		MD_Attribute _attribute = mdClass.getAttribute(_variable.getName());
		if(_attribute != null) {
			_attribute.setVariableComplexity(EnumExpressionComplexity.Undefined);
		}
		
		_variable.setVariableType(EnumVariableType.Undefined);
		
		
		String h = _variable.getName();
		
		
		
		if(matchConstant(_variable)) 	{		return;	}
		if(matchAssociation(_variable)) {		return;	}
		if(matchVariable(_variable)) {			return;	}
		
		throw new ExceptionScannerUnknownVariable("Unknown variable: " + _variable.getName() + "\n");
	}

	
	boolean matchConstants;
	boolean matchAssociations;
	boolean matchVariables;
	
	MD_Class mdClass;
	
	SymbolTable allLiterals; 
}
