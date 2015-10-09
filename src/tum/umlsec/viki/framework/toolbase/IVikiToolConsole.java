package tum.umlsec.viki.framework.toolbase;

import java.util.Iterator;

import tum.umlsec.viki.framework.ILogOutput;
import tum.umlsec.viki.framework.ITextOutput;

/**
 * @author pasha
 */
public interface IVikiToolConsole {
	IVikiToolBase getBase();
	
	void initialiseConsole();
	Iterator getConsoleCommands();
	void executeConsoleCommand(CommandDescriptor _command, Iterator _parameters, ITextOutput _mainOutput, ILogOutput _auxOutput);
}
