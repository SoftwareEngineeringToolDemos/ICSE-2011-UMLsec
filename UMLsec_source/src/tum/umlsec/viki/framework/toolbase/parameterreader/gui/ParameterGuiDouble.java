package tum.umlsec.viki.framework.toolbase.parameterreader.gui;

import java.awt.Component;

import tum.umlsec.viki.framework.ExceptionProgrammLogicError;
import tum.umlsec.viki.framework.toolbase.CommandParameterDescriptor;

/**
 * @author pasha
 */
public class ParameterGuiDouble extends ParameterGuiBASE {
	public ParameterGuiDouble(Component _parentComponent) {
		super(_parentComponent);
	}

	public boolean read(CommandParameterDescriptor _parameter) {
		if(_parameter.getType() != CommandParameterDescriptor.TypeDouble) {
			throw new ExceptionProgrammLogicError("Wrong parameter reader type");
		}
		
		while(true) {		
			String _answer = showQueryDialog(_parameter.getDescription(), "Required parameter: Double");
			if(_answer == null) {
				return false;
			}
			
			double _doubleval;
			try {
				_doubleval = Double.parseDouble(_answer);
			} catch (NumberFormatException x) {
				continue;
			}
			
			_parameter.setValue(new Double(_doubleval));
			return true; 
		}
	}
}
