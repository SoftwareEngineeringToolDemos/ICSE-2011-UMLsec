package tum.umlsec.viki.framework.toolbase.parameterreader.console;

import tum.umlsec.viki.framework.ExceptionProgrammLogicError;
import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.toolbase.CommandParameterDescriptor;


/**
 * @author pasha
 */
public class ParameterConsoleInteger extends ParameterConsoleBASE {

	public ParameterConsoleInteger(ITextOutput _textOutput) {
		super(_textOutput);
	}


	public boolean read(CommandParameterDescriptor _parameter) {
		if(_parameter.getType() != CommandParameterDescriptor.TypeInteger) {
			throw new ExceptionProgrammLogicError("Wrong parameter reader type");
		}
		
		while(true) {
			textOutput.write(_parameter.getDescription() + ": ");
			String _answer = readLine();
			
			if(_answer.compareToIgnoreCase("q") == 0) {
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

