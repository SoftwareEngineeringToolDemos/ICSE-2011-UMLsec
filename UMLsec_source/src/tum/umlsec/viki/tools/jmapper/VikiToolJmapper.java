package tum.umlsec.viki.tools.jmapper;

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

public class VikiToolJmapper implements IVikiToolBase, IVikiToolConsole {
	private final int CID_COMMAND01 = 1;
	private final int CID_COMMAND02 = 2;
	
	
	public IVikiToolConsole getConsole() { return this; }
	public IVikiToolGui getGui() { return null; }
	public IVikiToolWeb getWeb() { return null; }

	public IVikiToolBase getBase() { return this; }

	public String getToolName() {	return "Jmapper";	}
	public String getToolDescription() {	return "Mapps Uml Class Diagrams to Java Classes";	}


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
		/*
		switch(_command.getId()) {
			case CID_COMMAND01:
				_mainOutput.writeLn("class diagram mapping started");
			break;
			case CID_COMMAND02:
			break;
			default:
				throw new ExceptionProgrammLogicError("Unknown command");
		}
		*/
		ClassDiagramMapper _cdm;
		if (_command.getId() == 1 || _command.getId() == 2) {
			_cdm = new ClassDiagramMapper(mdrContainer,_mainOutput);
		}
		else {
			throw new ExceptionProgrammLogicError("Unknown command");
		}
		if (_command.getId() == 1) {
			_mainOutput.writeLn("class diagram mapping started");
			_cdm.setWrite2fs(true);
		}
		else if (_command.getId() == 2) {
			_cdm.setWrite2fs(false);
		}
		
		_cdm.doMap();
	}
	
	Vector parametersEmpty = new Vector();
	
	CommandDescriptor cmd01 = CommandDescriptor.CommandDescriptorConsole(CID_COMMAND01, "class diagram to file", "map class diagram", true, parametersEmpty); 
	CommandDescriptor cmd02 = CommandDescriptor.CommandDescriptorConsole(CID_COMMAND02, "class diagram to display", "map Class diagram", true, parametersEmpty); 
	
	Vector commands = new Vector();

	IMdrContainer mdrContainer;
}
