package tum.umlsec.viki.framework.toolbase.parameterreader.gui;

import java.awt.Component;

import tum.umlsec.viki.framework.ExceptionProgrammLogicError;
import tum.umlsec.viki.framework.toolbase.CommandParameterDescriptor;


/**
 * @author pasha
 */
public class ParameterGuiString extends ParameterGuiBASE {
	public ParameterGuiString(Component _parentComponent) {
		super(_parentComponent);
	}




	public boolean read(CommandParameterDescriptor _parameter) {
		if(_parameter.getType() != CommandParameterDescriptor.TypeString) {
			throw new ExceptionProgrammLogicError("Wrong parameter reader type");
		}
		
		
		
		String _answer = showQueryDialog(_parameter.getDescription(), "Required parameter: String");
		if(_answer == null) {
			return false;
		}
		_parameter.setValue(_answer);
		return true;		
	}

}
