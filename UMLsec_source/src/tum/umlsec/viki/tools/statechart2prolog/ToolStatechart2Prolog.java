package tum.umlsec.viki.tools.statechart2prolog;

import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.Logger;

import tum.umlsec.viki.framework.ExceptionProgrammLogicError;
import tum.umlsec.viki.framework.ILogOutput;
import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.PropertyNotSetException;
import tum.umlsec.viki.framework.mdr.IMdrContainer;
import tum.umlsec.viki.framework.toolbase.CommandDescriptor;
import tum.umlsec.viki.framework.toolbase.CommandParameterDescriptor;
import tum.umlsec.viki.framework.toolbase.IVikiToolBase;
import tum.umlsec.viki.framework.toolbase.IVikiToolConsole;
import tum.umlsec.viki.framework.toolbase.IVikiToolGui;
import tum.umlsec.viki.framework.toolbase.IVikiToolWeb;


/**
 * Viki plug-in to create a Prolog-attack-generator from statechart diagrams:<br>
 *  - returns executable Prolog code<br>
 *  - can execute this code as thread with 'timeout'; 'timeout' must be set by user<br>
 *  - only one UML file (must include a statechart diagram) can be stored in the MDR repository<br>
 *  - triggers for transitions are supported with the following syntax: messagename(Parameter1,...,ParameterN), e.g. msg2(X,Y)<br>
 *  - guards for transitions are supported with the following syntax: equal(Expression,ParameterN), e.g. equal(sign(X,k),Y)<br>
 *  - effects for transitions are supported with the following syntax: messagename(Expression), e.g. msg2(Z::Y)<br>
 * @author Dimitri Kopjev: kopjev@in.tum.de	
 */
public class ToolStatechart2Prolog implements IVikiToolBase, IVikiToolConsole {
	Logger logger = Logger.getLogger("statechart2prolog.ToolStatechart2Prolog");
	
	/** Sets command ID for "display" command. */
	private final int CID_DISPLAY = 1;
	
	/** Sets command ID for "run" command. */
	private final int CID_RUN = 2;
	
	/** Sets command parameter ID for "timeout". */
	private final int CPID_TIMEOUT = 1;

	/**
	 *	Method to return the instance that implements the interface for the console
	 *	@return Instance that implements the interface for the console
	 */
	public IVikiToolConsole getConsole() { return this; }
	
	/**
	 *	Method to return the instance that implements the interface for the GUI
	 *	@return Instance that implements the interface for the GUI
	 */
	public IVikiToolGui getGui() { return null; }
	
	/**
	 *	Method to return the instance that implements the interface for the Webiterface
	 *	@return Instance that implements the interface for the Webinterface
	 */
	public IVikiToolWeb getWeb() { return null; }
	
	/**
	 *	Method to return the instance that implements the basic functions of the plug-in
	 *	@return Instance that implements the the basic functions of the plug-in
	 */
	public IVikiToolBase getBase() { return this; }

	/**
	 *	Method to return the name of the plug-in
	 *	@return Name of the plug-in
	 */
	public String getToolName() {	return "Statechart Crypto Attack Generator";	}
	
	/**
	 *	Method to return the description of the plug-in
	 *	@return Description of the plug-in
	 */
	public String getToolDescription() {	return "Statechart Crypto Attack Generator";	}

	/**
	 *	Method to initialize the tool
	 *	@param _mdrContainer MDR Container to use
	 */
	public void initialiseBase(IMdrContainer _mdrContainer) {
		mdrContainer = _mdrContainer;
		Vector parametersEmpty = new Vector();
		Vector _parameters = new Vector();
		_parameters.add(timeoutParameter);
		display = CommandDescriptor.CommandDescriptorConsole(CID_DISPLAY, "Display Prolog", "Dysplay Prolog-file", true, parametersEmpty);
		run= CommandDescriptor.CommandDescriptorConsole(CID_RUN, "Run SWI-Prolog", "Run SWI-Prolog", true, _parameters);
		commands.add(display);
		commands.add(run);
	}

	/**
	 *	Method to initialize the console
	 */
	public void initialiseConsole() {
	}

	/**
	 *	Method to return the possible commands of the console
	 *	@return Iterator with commands
	 */
	public Iterator getConsoleCommands() {
		return commands.iterator();
	}
	
	/**
	 *	Method to execute the chosen commands of the console
	 *	@param _command The command to execute
	 *	@param _parameters Parameters of the command
	 *	@param _mainOutput ITextOutput object used by framework to output of main text messages
	 *	@param _auxOutput ILogOutput object used by framework to output of log messages
	 */
	public void executeConsoleCommand(CommandDescriptor _command, Iterator _parameters, ITextOutput _mainOutput, ILogOutput _auxOutput) {
		try {
			extractor =  new Statechart2Prolog(mdrContainer, _mainOutput, _auxOutput);
		} catch (FileNotFoundException e1) {
			_mainOutput.writeLn("Can't open Prolog header file. Aborting.");
			logger.error("FileNotFoundException while trying to create a new Statechart2Prolog");
			return;
		}
		switch(_command.getId()) {
			case CID_DISPLAY:
				extractor.extract();
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
				try {
					extractor.runProlog(mdrContainer, _mainOutput, timeout);
				} catch (PropertyNotSetException e) {
					logger.error("Can't run prolog. Aborting.");
					return;
				}
				break;			
			default:
				throw new ExceptionProgrammLogicError("Unknown command");
		}	
	}

	/** Descriptor for the command "display". */
	CommandDescriptor display;
	
	/** Descriptor for the command "run". */
	CommandDescriptor run;

	/** Descriptor for the command parameter "timeout". */
	CommandParameterDescriptor timeoutParameter = CommandParameterDescriptor.CommandParameterDescriptorInteger(CPID_TIMEOUT,"Please input a timeout value in seconds:",1,32767);

	/** Vector including all tool commands. */
	Vector commands = new Vector();
	
	/** MDR Container to use. */
	IMdrContainer mdrContainer;
	
	/** Instance of the class Statechart2Prolog that implements all tool commands. */
	Statechart2Prolog extractor;
}
