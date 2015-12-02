package tum.umlsec.viki.framework.toolbase;

import java.util.Iterator;

import tum.umlsec.viki.framework.ILogOutput;
import tum.umlsec.viki.framework.ITextOutput;


/**
 * @author pasha
 */
public interface IVikiToolWeb {
	IVikiToolBase getBase();

	void initialiseWeb();

	Iterator getWebCommands();
	void executeWebCommand(CommandDescriptor _command, Iterator _parameters, ITextOutput _mainOutput, ILogOutput _auxOutput);
}


