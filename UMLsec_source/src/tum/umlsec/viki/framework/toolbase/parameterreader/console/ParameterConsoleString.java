package tum.umlsec.viki.framework.toolbase.parameterreader.console;

import tum.umlsec.viki.framework.ExceptionProgrammLogicError;
import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.toolbase.CommandParameterDescriptor;


/**
 * @author pasha
 */
public class ParameterConsoleString extends ParameterConsoleBASE {

	public ParameterConsoleString(ITextOutput _textOutput) {
		super(_textOutput);
	}

	public boolean read(CommandParameterDescriptor _parameter) {
		if(_parameter.getType() != CommandParameterDescriptor.TypeString) {
			throw new ExceptionProgrammLogicError("Wrong parameter reader type");
		}
		
		while(true) {
			textOutput.write(_parameter.getDescription() + ": ");
			String _answer = readLine();
			
			if(_answer.compareToIgnoreCase("q") == 0) {
				return false;
			}
			 
			_parameter.setValue(_answer);
			return true; 
		}
	}
}

