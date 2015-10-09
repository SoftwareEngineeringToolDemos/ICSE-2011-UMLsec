package tum.umlsec.viki.tools.mdrview;

import java.util.Iterator;
import java.util.Vector;

import tum.umlsec.viki.framework.ExceptionProgrammLogicError;
import tum.umlsec.viki.framework.ILogOutput;
import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.toolbase.CommandDescriptor;
import tum.umlsec.viki.framework.toolbase.IVikiToolBase;
import tum.umlsec.viki.framework.toolbase.IVikiToolConsole;

/**
 * @author pasha
 */
public class ModeConsole implements IVikiToolConsole {
	private final int CID_LISTCLASSES = 1;
	private final int CID_LISTOBJECTS = 2;
	
	
	public ModeConsole(VikiToolMdrViewer _baseTool) {
		baseTool = _baseTool;
	}

	public IVikiToolBase getBase() {
		return baseTool;
	}

	public void initialiseConsole() {
		commands.add(cmdClasses); 
		commands.add(cmdObjects); 
	}

	public Iterator getConsoleCommands() {
		return commands.iterator();
	}

	public void executeConsoleCommand(CommandDescriptor _command, Iterator _parameters, ITextOutput _mainOutput, ILogOutput _auxOutput) {
		switch(_command.getId()) {
			case CID_LISTCLASSES:
				_mainOutput.writeLn("Enumerating classes ........ TODO");
			break;		
			case CID_LISTOBJECTS:
				_mainOutput.writeLn("Enumerating objects ........ TODO");
			break;		
			default:
				throw new ExceptionProgrammLogicError("Unknown command");
		}
	}



	VikiToolMdrViewer baseTool;
	
	Vector parametersEmpty = new Vector();
	
	CommandDescriptor cmdClasses = CommandDescriptor.CommandDescriptorConsole(CID_LISTCLASSES, "Classes", "List classes", true, parametersEmpty); 
	CommandDescriptor cmdObjects = CommandDescriptor.CommandDescriptorConsole(CID_LISTOBJECTS, "Objects", "List objects", true, parametersEmpty); 
	 
	Vector commands = new Vector();
	
}
