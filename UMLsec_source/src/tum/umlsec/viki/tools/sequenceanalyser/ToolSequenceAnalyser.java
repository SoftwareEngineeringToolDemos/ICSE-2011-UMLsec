package tum.umlsec.viki.tools.sequenceanalyser;

import java.io.File;
import java.util.Iterator;
import java.util.Vector;

import tum.umlsec.viki.framework.ILogOutput;
import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.mdr.IMdrContainer;
import tum.umlsec.viki.framework.toolbase.CommandDescriptor;
import tum.umlsec.viki.framework.toolbase.IVikiToolBase;
import tum.umlsec.viki.framework.toolbase.IVikiToolConsole;
import tum.umlsec.viki.framework.toolbase.IVikiToolGui;
import tum.umlsec.viki.framework.toolbase.IVikiToolWeb;


/**
 * @author Andreas
 */
public class ToolSequenceAnalyser implements IVikiToolBase, IVikiToolConsole {
	public static final int CID_MESSAGES = 1;
	public static final int CID_DISPLAYTPTP = 2;
	public static final int CID_SAVETPTP = 3;
	public static final int CID_RUNESETHEO = 4;
	public static final int CID_CHECKVARS = 5;
	public static final int CID_RUNESETHEO_STATUS = 6;
	public static final int CID_RUNSPASS = 7;
	public static final int CID_RUNSPASS_STATUS = 8;

	//String tmpPath = "c:\\";
	//String tmpPath = "/tmp/";
	String tmpPath = System.getProperty("java.io.tmpdir")+File.separator;

	
	

	public IVikiToolConsole getConsole() { return this; }

	public IVikiToolGui getGui() { return null; }

	public IVikiToolWeb getWeb() { return null; }

	public IVikiToolBase getBase() { return this; }

	public void initialiseBase(IMdrContainer _container) {

		mdrContainer = _container;
		Vector _parametersEmpty = new Vector();
		commandMessages = CommandDescriptor.CommandDescriptorConsole(CID_MESSAGES, "Print messages", "Print all messages", true, _parametersEmpty);
		commandDisplayTPTP = CommandDescriptor.CommandDescriptorConsole(CID_DISPLAYTPTP, "Display TPTP", "Display TPTP", true, _parametersEmpty);
		commandRunESetheo = CommandDescriptor.CommandDescriptorConsole(CID_RUNESETHEO, "Run E-SETHEO", "Run E-SETHEO", true, _parametersEmpty);
		commandRunESetheoStatus = CommandDescriptor.CommandDescriptorConsole(CID_RUNESETHEO_STATUS, "Run E-SETHEO (with status-information)", "Run E-SETHEO (with status-information)", true, _parametersEmpty);
		commandSaveTPTP = CommandDescriptor.CommandDescriptorConsole(CID_SAVETPTP, "Save TPTP", "Save TPTP", true, _parametersEmpty);
		commandCheckVars = CommandDescriptor.CommandDescriptorConsole(CID_CHECKVARS, "Check Variables", "Check Variables", true, _parametersEmpty);
		commandRunSpass = CommandDescriptor.CommandDescriptorConsole(CID_RUNSPASS, "Run SPASS", "Run SPASS", true, _parametersEmpty);
		commandRunSpassStatus = CommandDescriptor.CommandDescriptorConsole(CID_RUNSPASS_STATUS, "Run SPASS (with status-information)", "Run SPASS (with status-information)", true, _parametersEmpty);
	}

	public String getToolName() {
		return "Sequence-Diagram Crypto FOL-Analyzer";
	}

	public String getToolDescription() {
		return "Sequence-Diagram Crypto FOL-Analyzer";
	}

	public Iterator getConsoleCommands() {
		Vector _commands = new Vector();
		// Diese Kommando dient nur fur Testzwecke
		// _commands.add(commandMessages);
		_commands.add(commandDisplayTPTP);
		_commands.add(commandSaveTPTP);
		//@bugfix buerger
		// As E-SETHEO isn't really available to the public in 2010,
		// we deactivate it here.
		//_commands.add(commandRunESetheo);
		//_commands.add(commandRunESetheoStatus);
		_commands.add(commandRunSpass);
		_commands.add(commandRunSpassStatus);
		_commands.add(commandCheckVars);
		return _commands.iterator();
	}
	
	public void initialiseConsole() {
	}
	


	public void executeConsoleCommand(CommandDescriptor _command, Iterator _parameters, ITextOutput _mainOutput, ILogOutput _auxOutput) {
		SequenceAnalyser _analyser = new SequenceAnalyser();


		switch(_command.getId()) {
		
			case CID_MESSAGES:
				_analyser.printMessages(_analyser.messages(mdrContainer, _mainOutput), _mainOutput, mdrContainer);
				// Gebe alle tagged Values aus
				//_analyser.getAllTaggedValues(mdrContainer, _mainOutput);
			  	break;
			case CID_SAVETPTP:
				String out = _analyser.writeString(_analyser.getTPTP(_analyser.messages(mdrContainer, _mainOutput), mdrContainer), tmpPath, "test", "tptp", _mainOutput);
				if (!out.equals(""))
					_mainOutput.writeLn("Writing TPTP-File \"" + tmpPath + out + "\" ok."); 
				break;
			case CID_RUNESETHEO_STATUS:
				_analyser.runESetheo(mdrContainer, _mainOutput, true);
				break;
			case CID_RUNESETHEO:
				_analyser.runESetheo(mdrContainer, _mainOutput, false);
				break;
			case CID_DISPLAYTPTP:
				_analyser.displayString(_analyser.getTPTP(_analyser.messages(mdrContainer, _mainOutput), mdrContainer), _mainOutput);
				break;
			case CID_CHECKVARS:
				_analyser.checkVars(_analyser.messages(mdrContainer, _mainOutput), _analyser.getArgNotation(mdrContainer), _mainOutput);
				break;
			case CID_RUNSPASS:
				_analyser.runSpass(mdrContainer, _mainOutput, false);
				break;
			case CID_RUNSPASS_STATUS:
				_analyser.runSpass(mdrContainer, _mainOutput, true);
				break;
			
		}

	}
	
	
	
	
	
	
	IMdrContainer mdrContainer;
	CommandDescriptor commandMessages;
	CommandDescriptor commandDisplayTPTP;
	CommandDescriptor commandSaveTPTP;
	CommandDescriptor commandRunESetheo;
	CommandDescriptor commandRunESetheoStatus;
	CommandDescriptor commandCheckVars;
	CommandDescriptor commandRunSpass;
	CommandDescriptor commandRunSpassStatus;
	

}
