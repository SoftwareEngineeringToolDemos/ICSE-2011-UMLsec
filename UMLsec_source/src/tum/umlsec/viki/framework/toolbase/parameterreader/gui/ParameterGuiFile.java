package tum.umlsec.viki.framework.toolbase.parameterreader.gui;

import java.awt.Component;
import java.io.File;

import tum.umlsec.viki.framework.ExceptionProgrammLogicError;
import tum.umlsec.viki.framework.toolbase.CommandParameterDescriptor;

/**
 * @author pasha
 */
public class ParameterGuiFile extends ParameterGuiBASE {
	public ParameterGuiFile(Component _parentComponent) {
		super(_parentComponent);
	}




	public boolean read(CommandParameterDescriptor _parameter) {
		if(_parameter.getType() != CommandParameterDescriptor.TypeFile) {
			throw new ExceptionProgrammLogicError("Wrong parameter reader type");
		}
				
		String _answer = showQueryDialog(_parameter.getDescription(), "Required parameter: File"); 
		if(_answer == null) {
			return false;
		}
		_parameter.setValue(new File(_answer));
		return true;		
	}

}
