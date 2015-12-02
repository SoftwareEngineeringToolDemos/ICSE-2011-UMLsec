package tum.umlsec.viki.tools.sample2;

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
 * @author pasha
 */
public class VikiToolSample2 implements IVikiToolBase, IVikiToolConsole {
	private final int CID_INITIALISE = 1;
	private final int CID_INCREMENT = 2;
	private final int CID_DECREMENT = 3;
	
	private final int CPID_INITIAL_VALUE = 1;
	
	

	public IVikiToolConsole getConsole() { return this; }
	public IVikiToolGui getGui() { return null; }
	public IVikiToolWeb getWeb() { return null; }


	public IVikiToolBase getBase() {
		return this;
	}
	 







	public void initialiseBase(IMdrContainer _container) {
		mdrContainer = _container;
		
		Vector _parametersForIncrement = new Vector();
		_parametersForIncrement.add(parameterInitialValue);
		
		Vector _parametersEmpty = new Vector();
		
		commandInitialise = CommandDescriptor.CommandDescriptorConsole(CID_INITIALISE, "Initialise", "Preset the counter value", true, _parametersForIncrement);
		commandIncrement = CommandDescriptor.CommandDescriptorConsole(CID_INCREMENT, "Increment", "Increment counter", true, _parametersEmpty);
		commandDecrement = CommandDescriptor.CommandDescriptorConsole(CID_DECREMENT, "Decrement", "Decrement counter", true, _parametersEmpty);
	}

	public String getToolName() {
		return "TestTool2";
	}

	public String getToolDescription() {
		return "This is a first tool built for the new viki architecture";
	}

	public Iterator getConsoleCommands() {
		Vector _commands = new Vector();
		_commands.add(commandInitialise);
		if(initialised) {
			_commands.add(commandIncrement);
			_commands.add(commandDecrement);
		}
		return _commands.iterator();
	}
	
	public void initialiseConsole() {
	}
	


	public void executeConsoleCommand(CommandDescriptor _command, Iterator _parameters, ITextOutput _mainOutput, ILogOutput _auxOutput) {
		switch(_command.getId()) {
			case CID_INITIALISE:
				boolean found = false;
				for (; _parameters.hasNext();) {
					CommandParameterDescriptor _p = (CommandParameterDescriptor) _parameters.next();
					if(_p.getId() == CPID_INITIAL_VALUE) {
						value = _p.getAsInteger();
						found = true;
						initialised = true;
						break;
					}
				}

				if(!found) {
					throw new ExceptionProgrammLogicError("Required parameter missing.");
				}
			break;
			
			case CID_INCREMENT:
				if(!initialised) {
					throw new ExceptionProgrammLogicError("Changing a non-initialised counter.");
				}
				value ++;
			break;
			
			case CID_DECREMENT:
				if(!initialised) {
					throw new ExceptionProgrammLogicError("Changing a non-initialised counter.");
				}
				value --;
			break; 
		}
		_mainOutput.writeLn("New counter value: " + value);
	}
	
	
	
	
	
	
	IMdrContainer mdrContainer;
	
	CommandParameterDescriptor parameterInitialValue = CommandParameterDescriptor.CommandParameterDescriptorInteger(CPID_INITIAL_VALUE, "Initial value", Integer.MIN_VALUE, Integer.MAX_VALUE); 
	
	CommandDescriptor commandInitialise;
	CommandDescriptor commandIncrement;
	CommandDescriptor commandDecrement;
	
	private int value;
	private boolean initialised = false;

}
