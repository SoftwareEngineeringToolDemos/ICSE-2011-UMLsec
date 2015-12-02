package tum.umlsec.viki.framework.web;

import java.util.Iterator;

import tum.umlsec.viki.framework.ILogOutput;
import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.toolbase.CommandDescriptor;
import tum.umlsec.viki.framework.toolbase.IVikiToolBase;
import tum.umlsec.viki.framework.toolbase.IVikiToolWeb;



/**
 * @author pasha
 */
public class DefaultWebWrapper implements IVikiToolWeb {

	public DefaultWebWrapper(IVikiToolBase _toolBase) {
		toolBase = _toolBase;
	}

	public IVikiToolBase getBase() {
		return toolBase;
	}

	public void initialiseWeb() {
		toolBase.getConsole().initialiseConsole();
	}

	public Iterator getWebCommands() {
		return toolBase.getConsole().getConsoleCommands();
	}
		
	public void executeWebCommand(CommandDescriptor _command, Iterator _parameters, ITextOutput _mainOutput, ILogOutput _auxOutput) {
		toolBase.getConsole().executeConsoleCommand(_command, _parameters, _mainOutput, _auxOutput);
	}



/*
	public void write(String _s) {
		textArea.append(_s);
	}

	public void writeLn(String _s) {
		textArea.append(_s + "\n");
	}

	public void writeLn() {
		textArea.append("\n");
	}
*/


	
	IVikiToolBase toolBase;

}


