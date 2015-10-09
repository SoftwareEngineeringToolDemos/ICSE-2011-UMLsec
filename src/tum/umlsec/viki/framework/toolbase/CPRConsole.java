package tum.umlsec.viki.framework.toolbase;

import tum.umlsec.viki.framework.ExceptionProgrammLogicError;
import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.toolbase.parameterreader.console.ParameterConsoleDouble;
import tum.umlsec.viki.framework.toolbase.parameterreader.console.ParameterConsoleFile;
import tum.umlsec.viki.framework.toolbase.parameterreader.console.ParameterConsoleInteger;
import tum.umlsec.viki.framework.toolbase.parameterreader.console.ParameterConsoleString;

/**
 * @author pasha
 */
public class CPRConsole extends CPRBASE {
	public CPRConsole(ITextOutput _textOutput) {
		textOutput = _textOutput;
	}

	public boolean read(CommandParameterDescriptor _parameter) {
		switch(_parameter.getType()) {
			case CommandParameterDescriptor.TypeString:
				return (new ParameterConsoleString(textOutput)).read(_parameter);
				
			case CommandParameterDescriptor.TypeFile:
				return (new ParameterConsoleFile(textOutput)).read(_parameter);
				
			case CommandParameterDescriptor.TypeInteger:
				return (new ParameterConsoleInteger(textOutput)).read(_parameter);
			
			case CommandParameterDescriptor.TypeDouble:
				return (new ParameterConsoleDouble(textOutput)).read(_parameter);
	
			default:			
				throw new ExceptionProgrammLogicError("Unknown Command Parameter type in CPRConsole");
		}
	}

	ITextOutput textOutput;
}
