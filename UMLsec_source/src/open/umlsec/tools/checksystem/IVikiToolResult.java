package open.umlsec.tools.checksystem;

import java.util.Iterator;

import tum.umlsec.viki.framework.ILogOutput;
import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.toolbase.CommandDescriptor;

/**
 * @author ska
 *
 */

public interface IVikiToolResult {
	
	
	
	void executeConsoleCommand(CommandDescriptor _command, Iterator _parameters, 
			ITextOutput _mainOutput, ILogOutput _auxOutput, SingleToolResult _result);
}
