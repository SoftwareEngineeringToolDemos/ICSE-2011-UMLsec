/* Generated by Together */

package tum.umlsec.viki.tools.sample;

import java.util.Iterator;
import java.util.Vector;

import tum.umlsec.viki.framework.ExceptionProgrammLogicError;
import tum.umlsec.viki.framework.ILogOutput;
import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.mdr.IMdrContainer;
import tum.umlsec.viki.framework.toolbase.CommandDescriptor;
import tum.umlsec.viki.framework.toolbase.IVikiToolBase;
import tum.umlsec.viki.framework.toolbase.IVikiToolConsole;
import tum.umlsec.viki.framework.toolbase.IVikiToolGui;
import tum.umlsec.viki.framework.toolbase.IVikiToolWeb;

public class VikiToolSample implements IVikiToolBase, IVikiToolConsole {
	private final int CID_COMMAND01 = 1;
	private final int CID_COMMAND02 = 2;
	
	
	public IVikiToolConsole getConsole() { return this; }
	public IVikiToolGui getGui() { return null; }
	public IVikiToolWeb getWeb() { return null; }

	public IVikiToolBase getBase() { return this; }

	public String getToolName() {	return "SampleTool";	}
	public String getToolDescription() {	return "This is a sample viki tool";	}


	public void initialiseBase(IMdrContainer _mdrContainer) {
		mdrContainer = _mdrContainer;
		
		commands.add(cmd01); 
		commands.add(cmd02); 
	}



	
	public void initialiseConsole() {
	}
	public Iterator getConsoleCommands() {
		return commands.iterator();
	}
	public void executeConsoleCommand(CommandDescriptor _command, Iterator _parameters, ITextOutput _mainOutput, ILogOutput _auxOutput) {
		switch(_command.getId()) {
			case CID_COMMAND01:
				_mainOutput.writeLn("Executing command 01 ........");
			break;		
			case CID_COMMAND02:
				_mainOutput.writeLn("Executing command 02 ........");
			break;		
			default:
				throw new ExceptionProgrammLogicError("Unknown command");
		}
		
		
	}
	



	

	Vector parametersEmpty = new Vector();
	
	CommandDescriptor cmd01 = CommandDescriptor.CommandDescriptorConsole(CID_COMMAND01, "Command01", "First test command", true, parametersEmpty); 
	CommandDescriptor cmd02 = CommandDescriptor.CommandDescriptorConsole(CID_COMMAND02, "Command02", "Second test command", true, parametersEmpty); 
	 
	Vector commands = new Vector();
	

	IMdrContainer mdrContainer;
}