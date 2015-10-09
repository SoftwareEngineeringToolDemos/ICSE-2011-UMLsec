/**
 * Integration of the UML model parser for subsystems in the framework viki:
 *  - saves and returns all possible flows
 *  - more UML diagrams (must only be statechart diagrams representing the subsystem) can be stored in the MDR repository
 *  - the names of the statechart diagrams are saved as the tagged value 'Diagram' of the initial states
 *  - pseudo states are included in the resulting flows
 *  - asynchronous messages between statechart diagrams are supported with the following syntax: send(diagramname.messagename(parameter)), e.g. D1.msg1(2) 
 *  - 'send' is a keyword indicating a message
 *  - 'diagramname' represents the name of the diagram the message is sent to
 *  - 'messagename' can be chosen by the user
 *  - 'parameter' must be a valid expression
 *  - effects and guards for transitions are supported
 *  - triggers for transitions are supported with the following syntax: messagename(parameter), e.g. msg2(x)
 *  - 'messagename' represents the name of the message the transition is waiting for
 *  - 'parameter' is either empty or the name of the variable the result of the expression will be assigned to
 *  - conditions and assignments are evaluated with integer variables
 *  - each fork state has its own corresponding join state
 *  - synchronization is supported with fork and join states only 
 *  - super states, history states and synchronization states can not be used
 *  - entry and exit actions are not considered
 *  - do-activities are not considered
 */

package tum.umlsec.viki.tools.subsystemparser;

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

// class to integrate the parser into the framework
public class ToolSubsystem implements IVikiToolBase, IVikiToolConsole  {
	
    public static final int CID_SUBSYSTEM_PARSER = 1;
    public static final int CPID_DEPTH = 1;
    
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
        return "Subsystem Parser";	
    }

    // method to return the description of the parser
    public String getToolDescription() {
        return "Subsystem Parser";	
    }
    
    // method to initialize the tool
    public void initialiseBase(IMdrContainer _mdrContainer) {
	mdrContainer = _mdrContainer;
	Vector _parameters = new Vector();
	_parameters.add(parameterDepth);
	commandDepth = CommandDescriptor.CommandDescriptorConsole(CID_SUBSYSTEM_PARSER, "Parse", "Parse Subsystem", true, _parameters);
	commands = new Vector();
	commands.add(commandDepth);
    }

    // method to initialize the console
    public void initialiseConsole() {
    }

    // method to return the possible commands of the console
    public Iterator getConsoleCommands() {
	return commands.iterator();
    }

    // method to execute the chosen commands of the console
    public void executeConsoleCommand(CommandDescriptor _command, Iterator _parameters, ITextOutput _mainOutput, ILogOutput _auxOutput) {
	if(_command.getId() == CID_SUBSYSTEM_PARSER) {
            SubsystemParser _parser = new SubsystemParser();
            _parser.check(mdrContainer,  _parameters, _mainOutput);
	} else {
            throw new ExceptionProgrammLogicError("Unknown command");
	}
    }
    
    private IMdrContainer mdrContainer;
    private CommandParameterDescriptor parameterDepth = CommandParameterDescriptor.CommandParameterDescriptorInteger(CPID_DEPTH, "Trace Depth", 1, Integer.MAX_VALUE); 
    private CommandDescriptor commandDepth;
    private Vector commands;

}
