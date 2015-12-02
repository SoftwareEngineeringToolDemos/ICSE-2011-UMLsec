
package tum.umlsec.viki.tools.sequencediagram;

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

// class to integrate the parser into the framework
public class ToolSequenceDiagramLocal implements IVikiToolBase, IVikiToolConsole  {
    
	public static final int CID_SEQUENCE_PARSER = 1;
	public static final int CPID_DEPTH = 1;
	public static final int CID_OUTPUT = 1;
        
	// method to return the instance that implements the interface for the console
	public IVikiToolConsole getConsole() { 
		return this; 
	}
	
	// method to return the instance that implements the interface for the GUI
	public IVikiToolGui getGui() { 
		return null; 
	}
	
	// method to return the instance that implements the interface for the web
	public IVikiToolWeb getWeb() {
		return null; 
	}
        
	// method to return the instance that implements the basic functions of the parser
	public IVikiToolBase getBase() {
		return this; 
	}

	// method to return the name of the parser
	public String getToolName() {
		return "Sequence-Diagram Authenticity FOL-Analyzer";	
	}
	
	// method to return the desription of the parser
	public String getToolDescription() {
		return "Sequence-Diagram Authenticity FOL-Analyzer";	
	}

	// method to initialize the tool
	public void initialiseBase(IMdrContainer _mdrContainer) {
		mdrContainer = _mdrContainer;
		//Vector _parameters = new Vector();
		//_parameters.add(parameterFilename);
		Vector _parametersEmpty = new Vector();
		commandParser = CommandDescriptor.CommandDescriptorConsole(CID_SEQUENCE_PARSER, "Parse", "Parse Messages", true, _parametersEmpty);
		
		commands = new Vector();
		commands.add(commandParser);
	}

	// method to initialize the console
	public void initialiseConsole() {
	}

	// method to return the possible commands of the console
	public Iterator getConsoleCommands() {
		return commands.iterator();
	}

	// method to execute the chosen commands of the console
	public void executeConsoleCommand(CommandDescriptor _command, Iterator _parameters, ITextOutput _mainOutput, ILogOutput _auxOutput) 
	{
		SequenceMessage[] messages;
		Vector forallVariable = new Vector();
		
		if(_command.getId()==CID_SEQUENCE_PARSER)
		{	
//			String filename = "";
			//check,if the file has exist 
			OutputTptp outputTptp = new OutputTptp();
//			filename = outputTptp.checkFileExist(_parameters);
			String filename = System.getProperty("user.dir")+"\\"+"result.tptp";

//			if (filename.equalsIgnoreCase("exist!"))						
//				_mainOutput.writeLn("The file is existed! Please give a new name or output in other path.");
//			else
//			{
				// Sequencediagramm Parser
				SequenceParser _parser = new SequenceParser();
				_parser.check(mdrContainer,  _parameters, _mainOutput);
				_mainOutput.writeLn("Parsing finished!");
//				_mainOutput.writeLn("It's available to download by folowing the link below:");
//				_mainOutput.writeLn("<a href='http://www4.in.tum.de:8180/vikinew/result.tptp'>result.tptp</a>");
//				_mainOutput.writeLn();
//				_mainOutput.writeLn();
				_mainOutput.writeLn(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>Result: TPTP file<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
				_mainOutput.writeLn();
				_mainOutput.writeLn();
				
				messages = _parser.getMessage();
				forallVariable = _parser.getForallVariable();
				
				outputTptp.getMessagesForallvariableFilename(messages, forallVariable, filename,_parameters);
				outputTptp.writeTptp(_mainOutput);
			}			
//		} 
		else if(_command.getId()==CID_OUTPUT)
		{}
		else
		{
			throw new ExceptionProgrammLogicError("Unknown command");
		}
		
	}

	private IMdrContainer mdrContainer;
//	private CommandParameterDescriptor parameterFilename = CommandParameterDescriptor.CommandParameterDescriptorString(CPID_DEPTH, "Please input the name of TPTP_file"); 
	private CommandDescriptor commandParser;
	private Vector commands;
    
}