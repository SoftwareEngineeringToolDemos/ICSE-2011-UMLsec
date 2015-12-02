package tum.umlsec.viki.tools.modulTemplate;

import java.util.Iterator;
import java.util.Vector;

import tum.umlsec.viki.framework.ILogOutput;
import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.mdr.IMdrContainer;
import tum.umlsec.viki.framework.toolbase.CommandDescriptor;
import tum.umlsec.viki.framework.toolbase.CommandParameterDescriptor;
import tum.umlsec.viki.framework.toolbase.IVikiToolBase;
import tum.umlsec.viki.framework.toolbase.IVikiToolConsole;
import tum.umlsec.viki.framework.toolbase.IVikiToolGui;
import tum.umlsec.viki.framework.toolbase.IVikiToolWeb;
import tum.umlsec.viki.tools.checkstatic.StaticCheckerBase;

/**
 * @author Marc Peschke
 *
 */

// your Module still missing?
// - did you add your Module in open.umlsec.tools.checksystem.gui.SystemVerificationLoader to 'IVikiToolBase []tools'?
// - did you add a index in open.umlsec.tools.checksystem.gui.SystemVerificationLoader?

public class ModulTemplate implements IVikiToolBase, IVikiToolConsole {

	public static final int CID_TEMPLATE = 1;
    public static final int CPID_FILE = 1;

	public IVikiToolConsole getConsole() { return this; }
	public IVikiToolGui getGui() { return null; }
	public IVikiToolWeb getWeb() { return null; }

	public IVikiToolBase getBase() {return this;}

	public String getToolName() {return "Template";}
	public String getToolDescription() {return "Template";}

	IMdrContainer mdrContainer;
	boolean returnValue;
	ITextOutput _textOutput;

	Vector parametersEmpty = new Vector();
	Vector parameters = new Vector();
	Vector commands = new Vector();

	//defines the command in the ToolGui
	CommandDescriptor cmdGetInfo = CommandDescriptor.CommandDescriptorConsole(CID_TEMPLATE, "Template", "Template", true, parametersEmpty);
	
	//defines the parameter for the module. 
	CommandParameterDescriptor parameterFile = CommandParameterDescriptor.CommandParameterDescriptorFile(CPID_FILE, "File");
	//CommandParameterDescriptor parameterInt = CommandParameterDescriptor.CommandParameterDescriptorInteger(CPID_INT, "ein int", 0, _max)(CPID_FILE, 100);

	public void initialiseBase(IMdrContainer _mdrContainer) {
		
		mdrContainer = _mdrContainer;
		
		parameters.add(parameterFile);
		//adds the command to the list
        commands.add(cmdGetInfo);
		}

	public Iterator getConsoleCommands(){

		return commands.iterator();
	}

	public void initialiseConsole() {
		int y = 0;
	}

	public void executeConsoleCommand(CommandDescriptor command, Iterator parameters, ITextOutput mainOutput, ILogOutput auxOutput) {

		StaticCheckerBase checker = null;
		//execute the commands by ID
		switch(command.getId()) {
			case CID_TEMPLATE:
				checker = new CheckerModulTemplate();
				returnValue = checker.check(mdrContainer, parameters, mainOutput);

			break;

			default:
				// TODO throw something nice
		}
		
	}

}