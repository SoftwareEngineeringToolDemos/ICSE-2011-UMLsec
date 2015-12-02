/**
 * Integration of the UML model parser for activity diagrams into the framework viki:
 *  - saves and returns all possible flows
 *  - only one UML diagram (must be an activity diagram) can be stored in the MDR repository
 *  - pseudo states are included in the resulting flows
 *  - effects and guards for transitions are supported
 *  - conditions and assignments are evaluated with integer variables
 *  - each fork must have its own corresponding join
 */

package tum.umlsec.viki.tools.activityparser;

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

/**
 * class to integrate the parser into the framework
 * 
 * @author
 * 
 */
public class ToolActivity implements IVikiToolBase, IVikiToolConsole {

	public static final int CID_ACTIVITY_PARSER = 1;
	public static final int CPID_DEPTH = 1;

	/**
	 * method to return the instance that implements the interface for the
	 * console
	 */
	public IVikiToolConsole getConsole() {
		return this;
	}

	/**
	 * method to return the instance that implements the interface for the GUI
	 */
	public IVikiToolGui getGui() {
		return null;
	}

	/**
	 * method to return the instance that implements the interface for the web
	 */
	public IVikiToolWeb getWeb() {
		return null;
	}

	/**
	 * method to return the instance that implements the basic functions of the
	 * parser
	 */
	public IVikiToolBase getBase() {
		return this;
	}

	/**
	 * method to return the name of the parser
	 */
	public String getToolName() {
		return "Activity-Diagram Parser";
	}

	/**
	 * method to return the description of the parser
	 */
	public String getToolDescription() {
		return "Activity-Diagram Parser";
	}

	/**
	 * method to initialize the tool
	 */
	public void initialiseBase(IMdrContainer _mdrContainer) {
		mdrContainer = _mdrContainer;
		Vector _parameters = new Vector();
		_parameters.add(parameterDepth);
		commandDepth = CommandDescriptor.CommandDescriptorConsole(
				CID_ACTIVITY_PARSER, "Parse", "Parse Activities", true,
				_parameters);
		commands = new Vector();
		commands.add(commandDepth);
	}

	/**
	 * method to initialize the console
	 */
	public void initialiseConsole() {
	}

	/**
	 * method to return the possible commands of the console
	 */
	public Iterator getConsoleCommands() {
		return commands.iterator();
	}

	/**
	 * method to execute the chosen commands of the console
	 */
	public void executeConsoleCommand(CommandDescriptor _command,
			Iterator _parameters, ITextOutput _mainOutput, ILogOutput _auxOutput) {
		if (_command.getId() == CID_ACTIVITY_PARSER) {
			ActivityParser _parser = new ActivityParser();
			_parser.check(mdrContainer, _parameters, _mainOutput);
		} else {
			throw new ExceptionProgrammLogicError("Unknown command");
		}
	}

	private IMdrContainer mdrContainer;
	private CommandParameterDescriptor parameterDepth = CommandParameterDescriptor
			.CommandParameterDescriptorInteger(CPID_DEPTH, "Trace Depth", 1,
					Integer.MAX_VALUE);
	private CommandDescriptor commandDepth;
	private Vector commands;

}