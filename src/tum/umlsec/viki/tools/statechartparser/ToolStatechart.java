/**
 * Integration of the UML model parser for statechart diagrams in the framework viki:
 *  - returns one possible flow that was chosen by the user
 *  - only one UML diagram (must be a statechart diagram) can be stored in the MDR repository
 *  - pseudo states are included in the resulting flows
 *  - the user can send asynchronous messages to the diagram with the following syntax: messagename(parameter), e.g. msg1(2)
 *  - 'messagename' can be chosen by the user
 *  - 'parameter' must be an integer
 *  - effects and guards for transitions are supported
 *  - triggers for transitions are supported with the following syntax: messagename(parameter), e.g. msg2(x)
 *  - 'messagename' represents the name of the message the transition is waiting for
 *  - 'parameter' is either empty or the name of the variable the integer will be assigned to 
 *  - conditions and assignments are evaluated with integer variables
 *  - each fork state has its own corresponding join state
 *  - synchronization is supported with fork and join states only 
 *  - super states, history states and synchronization states can not be used
 *  - entry and exit actions are not considered
 *  - do-activities are not considered
 */

package tum.umlsec.viki.tools.statechartparser;

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
public class ToolStatechart implements IVikiToolBase, IVikiToolConsole {
    
    public static final int CID_INITIALISE = 1;
    public static final int CID_NEXT = 2;
    public static final int CID_MESSAGE = 3;
    public static final int CID_STOP = 4;
    public static final int CPID_NEXT = 1;
    public static final int CPID_MESSAGE = 2;
    
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
        return "Statechart Parser";
    }

    // method to return the description of the parser
    public String getToolDescription() {
        return "Statechart Parser";
    }

    // method to initialize the tool
    public void initialiseBase(IMdrContainer _mdrContainer) {
        mdrContainer = _mdrContainer;
	Vector _parametersNext = new Vector();
        _parametersNext.add(parameterNext);
        Vector _parametersMessage = new Vector();
        _parametersMessage.add(parameterMessage);
        Vector _parametersEmpty = new Vector();
        commandInitialise = CommandDescriptor.CommandDescriptorConsole(CID_INITIALISE, "Initialize", "Start Parsing", true, _parametersEmpty);
	commandNext = CommandDescriptor.CommandDescriptorConsole(CID_NEXT, "Next Transition", "Choose Next Transition", true, _parametersNext);
        commandMessage = CommandDescriptor.CommandDescriptorConsole(CID_MESSAGE, "Message", "Send Message", true, _parametersMessage);
        commandStop = CommandDescriptor.CommandDescriptorConsole(CID_STOP, "Stop", "Stop Parsing", true, _parametersEmpty);
	commands = new Vector();
	commands.add(commandInitialise);
        commands.add(commandNext);
        commands.add(commandMessage);
        commands.add(commandStop);
    }

    // method to initialize the console
    public void initialiseConsole() {
    }

    // method to return the possible commands of the console
    public Iterator getConsoleCommands() {
        Vector _commands = new Vector();
        _commands.add(commandInitialise);
        if (initialised) {
            _commands.add(commandNext);
            _commands.add(commandMessage);
            _commands.add(commandStop);
        }
        return _commands.iterator();
    }

    // method to execute the chosen commands of the console
    public void executeConsoleCommand(CommandDescriptor _command, Iterator _parameters, ITextOutput _mainOutput, ILogOutput _auxOutput) {
        boolean found = false;
        switch (_command.getId()) {
            case CID_INITIALISE:
                initialised = true;
                parser = new StatechartParser(mdrContainer, _mainOutput);
                parser.init(_mainOutput);
                break;
            case CID_NEXT:
                if (!initialised) {
                    throw new ExceptionProgrammLogicError("Not initialised.");
                }
                int transnum = 1;
                for (; _parameters.hasNext();) {
                    CommandParameterDescriptor _p = (CommandParameterDescriptor) _parameters.next();
                    if (_p.getId() == CPID_NEXT) {
                        transnum = _p.getAsInteger();
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    throw new ExceptionProgrammLogicError("Required parameter missing.");
                }
                if (parser.nextTransition(transnum, _mainOutput) == false) initialised = false;
                break;
            case CID_MESSAGE:
                if (!initialised) {
                    throw new ExceptionProgrammLogicError("Not initialised.");
                }
                String msg = "";
                for (; _parameters.hasNext();) {
                    CommandParameterDescriptor _p = (CommandParameterDescriptor) _parameters.next();
                    if (_p.getId() == CPID_MESSAGE) {
                        msg = _p.getAsString();
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    throw new ExceptionProgrammLogicError("Required parameter missing.");
                }
                parser.sendMessage(msg, _mainOutput);
		break; 
            case CID_STOP:
                if (!initialised) {
                    throw new ExceptionProgrammLogicError("Not initialised.");
                }
                parser.stopParsing(_mainOutput);
                initialised = false;
		break; 
        }
    }
    
    private IMdrContainer mdrContainer;
    private CommandParameterDescriptor parameterNext = CommandParameterDescriptor.CommandParameterDescriptorInteger(CPID_NEXT, "Next Transition", 1, Integer.MAX_VALUE); 
    private CommandParameterDescriptor parameterMessage = CommandParameterDescriptor.CommandParameterDescriptorString(CPID_MESSAGE, "Message"); 
    private CommandDescriptor commandInitialise;
    private CommandDescriptor commandNext;
    private CommandDescriptor commandMessage;
    private CommandDescriptor commandStop;
    private Vector commands;
    private boolean initialised = false;
    private StatechartParser parser;

}