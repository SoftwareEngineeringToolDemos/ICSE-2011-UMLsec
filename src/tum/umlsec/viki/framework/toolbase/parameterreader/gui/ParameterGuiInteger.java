package tum.umlsec.viki.framework.toolbase.parameterreader.gui;

import java.awt.Component;

import tum.umlsec.viki.framework.ExceptionProgrammLogicError;
import tum.umlsec.viki.framework.toolbase.CommandParameterDescriptor;


/**
 * @author pasha
 */
public class ParameterGuiInteger extends ParameterGuiBASE {

	public ParameterGuiInteger(Component _parentComponent) {
		super(_parentComponent);
	}

	public boolean read(CommandParameterDescriptor _parameter) {
		if(_parameter.getType() != CommandParameterDescriptor.TypeInteger) {
			throw new ExceptionProgrammLogicError("Wrong parameter reader type");
		}

		while(true) {		
			String _answer = showQueryDialog(_parameter.getDescription(), "Required parameter: Integer");
			if(_answer == null) {
				return false;
			}
			
			int _intval;
			try {
				_intval = Integer.parseInt(_answer);
			} catch (NumberFormatException x) {
				continue;
			}
			
			_parameter.setValue(new Integer(_intval));
			return true; 
		}
	}

}
