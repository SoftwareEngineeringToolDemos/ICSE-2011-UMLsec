package tum.umlsec.viki.framework.toolbase;

import java.awt.Component;

import tum.umlsec.viki.framework.ExceptionProgrammLogicError;
import tum.umlsec.viki.framework.toolbase.parameterreader.gui.ParameterGuiDouble;
import tum.umlsec.viki.framework.toolbase.parameterreader.gui.ParameterGuiFile;
import tum.umlsec.viki.framework.toolbase.parameterreader.gui.ParameterGuiInteger;
import tum.umlsec.viki.framework.toolbase.parameterreader.gui.ParameterGuiString;

/**
 * @author pasha
 */
public class CPRGui extends CPRBASE {
	

	public CPRGui(Component _parentComponent) {
		parentComponent = _parentComponent;
	}
	
	
	

	/**
	 * @param _parameter
	 * @return
	 */
	public boolean read(CommandParameterDescriptor _parameter) {
		switch(_parameter.getType()) {
			case CommandParameterDescriptor.TypeString:
				return (new ParameterGuiString(parentComponent)).read(_parameter);
				
			case CommandParameterDescriptor.TypeFile:
				return (new ParameterGuiFile(parentComponent)).read(_parameter);
				
			case CommandParameterDescriptor.TypeInteger:
				return (new ParameterGuiInteger(parentComponent)).read(_parameter);
			
			case CommandParameterDescriptor.TypeDouble:
				return (new ParameterGuiDouble(parentComponent)).read(_parameter);
	
			default:			
				throw new ExceptionProgrammLogicError("Unknown Command Parameter type in CPRGui");
		}
	}
	
	Component parentComponent;
}

