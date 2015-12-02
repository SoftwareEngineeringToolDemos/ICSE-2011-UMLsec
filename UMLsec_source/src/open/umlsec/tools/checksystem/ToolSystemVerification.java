package open.umlsec.tools.checksystem;

import java.io.File;
import java.util.Iterator;
import java.util.Vector;

import open.umlsec.tools.checksystem.checks.CheckReqList;
import open.umlsec.tools.checksystem.checks.Command;
import open.umlsec.tools.checksystem.checks.ToolDescriptor;
import open.umlsec.tools.checksystem.checks.ToolSelector;
import open.umlsec.tools.checksystem.gui.SystemVerificationLoader;

import org.omg.uml.UmlPackage;
import org.omg.uml.foundation.core.CorePackage;

import tum.umlsec.viki.framework.ILogOutput;
import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.mdr.IMdrContainer;
import tum.umlsec.viki.framework.toolbase.CPRGui;
import tum.umlsec.viki.framework.toolbase.CommandDescriptor;
import tum.umlsec.viki.framework.toolbase.CommandParameterDescriptor;
import tum.umlsec.viki.framework.toolbase.IVikiToolBase;
import tum.umlsec.viki.framework.toolbase.IVikiToolConsole;
import tum.umlsec.viki.framework.toolbase.IVikiToolGui;
import tum.umlsec.viki.framework.toolbase.IVikiToolWeb;


/**
 * @author ska
 */

public class ToolSystemVerification implements IVikiToolBase, IVikiToolConsole {
	
	public static final int CID_CHECK_REQUIREMENTS 	= 1;
	public static final int CID_CHECK_SYSTEM 		= 2;
	
	
	private SystemAnalysis 	systemAnalysis 	= null;
	private ToolSelector	toolSelector	= null;
	
	/* tool commands */
	private CheckReqList	checkRequirements = null;

	private CorePackage 	corePackage;
	private UmlPackage 		root ;
	private IMdrContainer 	mdrContainer;

	private ITextOutput 	textOutput;

	private Vector 			commands = new Vector();
	private Vector 			parametersEmpty = new Vector();

	
	CommandDescriptor cmdCheckReq = CommandDescriptor.CommandDescriptorConsole(
			CID_CHECK_REQUIREMENTS, "Requirements", 
			"Check the requirements list vs the model", true, parametersEmpty);
	CommandDescriptor cmdCheckSys = CommandDescriptor.CommandDescriptorConsole(
			CID_CHECK_SYSTEM, "CheckSystem", 
			"Check the complete model", true, parametersEmpty);
	
	
	public ToolSystemVerification () {
		toolSelector 		= new ToolSelector();
		checkRequirements 	= new CheckReqList();
		/* an empty analysis is needed in case just a model is loaded */
		createAnalysis ("", "", null);
		systemAnalysis.clearAll();
	}
	
	public void createAnalysis (String strName, String strDescription, File fiRequirementXML) {
		
		if (systemAnalysis == null) {
			systemAnalysis = new SystemAnalysis(strName, strDescription, fiRequirementXML);
		} else {
			
			systemAnalysis.clearAll();
			systemAnalysis.setName(strName);
			systemAnalysis.setDescription(strDescription);
			systemAnalysis.setReqXMLFile(fiRequirementXML);
		}
	   
		if (fiRequirementXML !=null){
			if (checkRequirements != null){
				checkRequirements.readFromFile(fiRequirementXML);
			}
		}
	}
	
	public SystemAnalysis getAnalysis(){
		return systemAnalysis;
	}
	
	public CheckReqList getReqReader(){
		return checkRequirements;
	}
	
	public void createToolList (ITextOutput _textOutput) {
		int i, j;
		Vector<String> cmdNames;
		
		mdrContainer = SystemVerificationLoader.getFramework().getMdrContainer();
		
		toolSelector.clearAll();
		//toolSelector.searchTools(mdrContainer);
                System.out.println("........Hier setzt dann wohl der Fehler ein!");
		toolSelector.searchToolsBySignature(mdrContainer);
		
		for (i = 0; i < toolSelector.getToolList().size(); i++){
			cmdNames = toolSelector.getToolList().get(i).getCommandNames();
			
			for (j = 0; j< cmdNames.size(); j++){
				_textOutput.writeLn("UMLsec tool: " + cmdNames.elementAt(j));
			}
		}
	}
	
	public void executeTools(){
		ToolDescriptor 				tool 	= null;
		CommandDescriptor 			command = null;
		Vector<Command> 			cmdList	= null;
		Vector<ToolDescriptor> 		toolList = toolSelector.getToolList();
		int i;
		
		for (i = 0; i < toolList.size(); i++){
    		tool = (ToolDescriptor)toolList.get(i);
    		
    		if (tool.isEnabled() == true){
        		cmdList = tool.getCommands();
        		for (Iterator<Command> itCmd = cmdList.iterator(); itCmd.hasNext();){
        			Command cmd = itCmd.next();
        			if (cmd.isEnabled() == true){
            			for (Iterator it = 
            				tool.getToolReference().getConsole().getConsoleCommands(); it.hasNext();){
            				command = (CommandDescriptor)it.next();
            				
            				if (command.getId() == cmd.getCommandId()){
            	        		CPRGui _parameterReader = new CPRGui(
            	        				SystemVerificationLoader.getGui().getComponent());
            	        		for (Iterator iter = command.getParameters(); iter.hasNext();) {
            	        			CommandParameterDescriptor _parameter = (
            	        						CommandParameterDescriptor) iter.next();
            	        			if(!_parameterReader.read(_parameter)) {
            	        				break; 
            	        			}
            	        		}
            	        		if (command != null){
            	        			tool.getCommandResult(cmd.getCommandId()).clearAll();
            	            		tool.getToolReference().getConsole().executeConsoleCommand(
            	           				command, command.getParameters(), 
            	           				tool.getCommandResult(cmd.getCommandId()), 
            	           				tool.getCommandResult(cmd.getCommandId()));
            	        		}
            				}
        	        		command = null;
            			}
        			}
        		}
    		}
		}
	}
	
	public Vector<ToolDescriptor> getToolList(){
		return toolSelector.getToolList();
	}
	
	public void addToolWithCmds(IVikiToolBase refTool, Vector<Integer>	cmdIds){
		toolSelector.addTool(refTool, cmdIds);
	}
	
	public void executeConsoleCommand(CommandDescriptor _command, Iterator _parameters, 
							ITextOutput _mainOutput, ILogOutput _auxOutput){
		
		switch(_command.getId()) {
		case CID_CHECK_REQUIREMENTS:
			checkRequirements.check(
					SystemVerificationLoader.getFramework().getMdrContainer(),
																	_mainOutput);
			break;
		
		case CID_CHECK_SYSTEM:
			executeTools();
			break;
		}
		
	}
	
	public void initialiseConsole() {

	}
	
	public Iterator getConsoleCommands(){
		return commands.iterator();
	}
	
	public IVikiToolBase 	getBase() 		{return this;}
	public IVikiToolConsole getConsole() 	{return null;}
	public IVikiToolGui 	getGui() 		{return null;}
	public IVikiToolWeb 	getWeb() 		{return null;}

	public void initialiseBase(IMdrContainer container){
		commands.add(cmdCheckReq);
		commands.add(cmdCheckSys);
	}
		
	public String getToolName() {return "System verification tool";}
	public String getToolDescription() {return "Loads and verifies an entire system model";}
	public ToolSelector getToolSelector(){return this.toolSelector;}
	
}