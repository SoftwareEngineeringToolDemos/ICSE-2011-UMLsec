package tum.umlsec.viki.tools.sequence2prolog;

import java.util.Iterator;
import java.util.Vector;

import tum.umlsec.viki.framework.ExceptionProgrammLogicError;
import tum.umlsec.viki.framework.ILogOutput;
import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.mdr.IMdrContainer;
import tum.umlsec.viki.framework.toolbase.CommandDescriptor;
import tum.umlsec.viki.framework.toolbase.CommandParameterDescriptor;
import tum.umlsec.viki.framework.toolbase.IVikiToolBase;
import tum.umlsec.viki.framework.toolbase.IVikiToolConsole;
import tum.umlsec.viki.framework.toolbase.IVikiToolGui;
import tum.umlsec.viki.framework.toolbase.IVikiToolWeb;

public class ToolSequence2Prolog implements IVikiToolBase, IVikiToolConsole {
	private final int CID_DISPLAY = 1;
	private final int CID_RUN = 2;
	private final int CPID_TIMEOUT = 1;
	
	
	public IVikiToolConsole getConsole() { return this; }
	public IVikiToolGui getGui() { return null; }
	public IVikiToolWeb getWeb() { return null; }

	public IVikiToolBase getBase() { return this; }

	public String getToolName() {	return "Sequence2Prolog";	}
	public String getToolDescription() {	return "Export UML Sequence Diagrams into Prolog";	}


	public void initialiseBase(IMdrContainer _mdrContainer) {
		mdrContainer = _mdrContainer;
		Vector parametersEmpty = new Vector();
		
		Vector _parameters = new Vector();
		_parameters.add(timeoutParameter);
		display = CommandDescriptor.CommandDescriptorConsole(CID_DISPLAY, "Display Prolog", "Dysplay Prolog-file", true, parametersEmpty);
		run= CommandDescriptor.CommandDescriptorConsole(CID_RUN, "Run Attack Generator", "Run Attack Generator", true, _parameters);
		
		commands.add(display); 
		commands.add(run); 
	}



	
	
	public void initialiseConsole() {
	}
	public Iterator getConsoleCommands() {
		return commands.iterator();
	}
	public void executeConsoleCommand(CommandDescriptor _command, Iterator _parameters, ITextOutput _mainOutput, ILogOutput _auxOutput) {
		extractor = new Sequence2Prolog(mdrContainer, _mainOutput, _auxOutput);
		switch(_command.getId()) {
			case CID_DISPLAY:
				extractor.extract(true);			
			break;
			
			case CID_RUN:
				boolean found = false;
				int timeout=10;
				for (; _parameters.hasNext();) {
					CommandParameterDescriptor _p = (CommandParameterDescriptor) _parameters.next();
					if(_p.getId() == CPID_TIMEOUT) {
						timeout = _p.getAsInteger();
						found = true;
						break;
					}
				}

				if(!found) {
					throw new ExceptionProgrammLogicError("Required parameter missing.");
				}
				extractor.runProlog(timeout);
			
				break;
				
				default:
					throw new ExceptionProgrammLogicError("Unknown command");	

		}
		
		
	}
	



	

	Vector parametersEmpty = new Vector();
	
	CommandDescriptor display; 
	CommandDescriptor run;
	CommandParameterDescriptor timeoutParameter = CommandParameterDescriptor.CommandParameterDescriptorInteger(CPID_TIMEOUT,"Please input a timeout value in seconds:",1,32767);
	 
	Vector commands = new Vector();
	
	
	IMdrContainer mdrContainer;	
	Sequence2Prolog extractor;
}
