package tum.umlsec.viki.framework.console;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

import tum.umlsec.viki.framework.FrameworkBase;
import tum.umlsec.viki.framework.ILogOutput;
import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.toolbase.CPRConsole;
import tum.umlsec.viki.framework.toolbase.CommandDescriptor;
import tum.umlsec.viki.framework.toolbase.CommandParameterDescriptor;
import tum.umlsec.viki.framework.toolbase.IVikiToolBase;
import tum.umlsec.viki.framework.toolbase.IVikiToolConsole;


/**
 * @author pasha
 */
public class FrameworkConsole extends FrameworkBase implements ITextOutput, ILogOutput {
	private final int PARAM_FILE = 0;
	private final int PARAM_TOOL = 1;
//	private final int PARAM_CMD = 2;


	public FrameworkConsole(String[] _argv) {
		argv = _argv;
	}

	public void run() {
		installTools();
		
		writeLn("UML(sec) tool, Munich Technical University. http://www4.in.tum.de/~umlsec");
		
		String _fileName = argv[PARAM_FILE];
		File _file = new File(_fileName);
		if(!_file.exists()) {
			writeLn("There is no such file: " + _fileName);
			PrintGlobalHelp();
			return;
		}

// file is there but we don't have enough parameters 
// (must be at least TOOL and COMMAND)
		if(argv.length == 2) {
			PrintGlobalHelp();
			return;
		}  		

		if(argv.length == 3 && findToolByName(argv[PARAM_TOOL]) == null) {
			writeLn("There is no such tool installed: " + argv[PARAM_TOOL]);
			writeLn("Installed tools are: ");
			PrintToolList();
			return ;
		}

// now we try to load the file
		try {
   			write("Loading file: " + _file.getName());
			getMdrContainer().loadFromFile(_file);
	   	} catch (Exception ex) {
			writeLn();
			writeLn("Error loading file: " + _file.getName());
	   		writeLn(ex.getMessage());
	   		return;
	   	}
	   	writeLn(" ... complete");
				
		if(argv.length > 1) {
			runBatchMode(argv);
		} else {
			runInteractiveMode();
		}		
	}
	
	
	private void runBatchMode(String [] _argv) {
		IVikiToolBase _toolBase = findToolByName(_argv[PARAM_TOOL]);
		if(_toolBase == null) {
			writeLn("No such tool: " + _argv[PARAM_TOOL]);
			return;
		}

		IVikiToolConsole _toolConsole = _toolBase.getConsole();
		
//		get list of 		
		
		// TODO
		
		
		
		
		
		
	}


	private String readLine() {
		InputStreamReader inputStreamReader = new InputStreamReader ( System.in );
		BufferedReader stdin = new BufferedReader ( inputStreamReader );
		try {
			return stdin.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}

	private void runInteractiveMode() {
		IVikiToolBase _tool = null;

		boolean quit = false;
		boolean found = false;

		
// select tool
		_tool = null;
		found = false;
		while(found == false) {
			writeLn("==== Available tools: =======================================================");
			PrintToolList();
			writeLn("==== Please select one of the tools by typing its name (type q to quit): ====");
			
			String _toolName = readLine();
			
			if(_toolName.compareToIgnoreCase("q") == 0) {
				quit = true;
				break;
			}
			
			for (int i = 0; i < getTools().length; i++) {
				_tool = getTools()[i];
				if(_tool.getToolName().compareTo(_toolName) == 0) {
					found = true;
					break;
				}
			}
		}


	
		IVikiToolConsole _toolConsole = _tool.getConsole();
		
		while(quit == false) {		 
			
			CommandDescriptor _command = null;
			found = false;
			while(found == false && quit == false) {

				writeLn("---- Available commands: ----------------------------------------------------");
				for (Iterator _iter = _toolConsole.getConsoleCommands(); _iter.hasNext();) {
					_command = (CommandDescriptor) _iter.next();
					if(!_command.isEnabled()) {
						write("[DISABLED] ");
					}
					writeLn(_command.getName() + ": " + _command.getDescription());
				}
				writeLn("---- Please select one by typing its name (type q to quit): -----------------");
				
				String _commandName = readLine();
				
				if(_commandName.compareToIgnoreCase("q") == 0) {
					quit = true;
					break;
				}
				
				_command = null;
				for(Iterator _iter = _toolConsole.getConsoleCommands(); _iter.hasNext();) {
					_command = (CommandDescriptor) _iter.next();
					if(_command.getName().compareTo(_commandName) == 0 && _command.isEnabled()) {
						found = true;
						break;
					}
				} 
			}
				
			if(quit) {
				break;
			}
		
		
// collect params
			CPRConsole _parameterReader = new CPRConsole(this);
			boolean _firstPass = true;
			for (Iterator iter = _command.getParameters(); iter.hasNext();) {
				if(_firstPass) {
					writeLn("---- Enter parameter values (type q to quit): -------------------------------");
					_firstPass = false;
				}
				CommandParameterDescriptor _parameter = (CommandParameterDescriptor) iter.next();
				if(!_parameterReader.read(_parameter)) {
					quit = true;
					break; 
				}
			}

			if(quit) {
				break;
			}

// execute
			_toolConsole.executeConsoleCommand(_command, _command.getParameters(), this, this);
		}
		
	}




	private IVikiToolBase findToolByName(String _name) {
		for (int i = 0; i < getTools().length; i++) {
			IVikiToolBase _tool = getTools()[i];
			if(_tool.getToolName().compareTo(_name) == 0) {
				return _tool;
			}
		}
		return null;
	}
	
	private void installTools() {
		for (int i = 0; i < getTools().length; i++) {
			IVikiToolBase _tool = getTools()[i];
			_tool.getConsole().initialiseConsole();
		}
	}
	
	private void PrintGlobalHelp() {
		writeLn("Run in the interactive mode:");
		writeLn("tum.umlsec.viki.framework.Loader <XMI file>");
		writeLn();
		writeLn("Run in the batch mode:");
		writeLn("tum.umlsec.viki.framework.Loader <XMI file> [<tool> <command> <parameter>*]");
		writeLn();
		writeLn("Installed tools:");
		PrintToolList();
	}
	
	private void PrintToolList() {
		for (int i = 0; i < getTools().length; i++) {
			IVikiToolBase _tool = getTools()[i];
			writeLn(_tool.getToolName() + ": " + _tool.getToolDescription());
		}
	}
	
	
	public void write(String _s) {			System.out.print(_s); 	}
	public void writeLn(String _s) {		System.out.println(_s); }
	public void writeLn() {					System.out.println();	}
	
	public void appendLog(String _s) {		System.out.print(_s);	}
	public void appendLogLn(String _s) {	System.out.println(_s);	}
	public void appendLogLn() {				System.out.println();	}




	private String[] argv;
//	private IVikiToolBase [] consoleTools;
}
