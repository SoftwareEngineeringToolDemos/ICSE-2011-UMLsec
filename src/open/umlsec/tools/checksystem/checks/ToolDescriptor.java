/**
 * 
 */
package open.umlsec.tools.checksystem.checks;

import java.util.Iterator;
import java.util.Vector;

import open.umlsec.tools.checksystem.SingleToolResult;
import tum.umlsec.viki.framework.toolbase.IVikiToolBase;

/**
 * @author ska
 *
 */
public class ToolDescriptor{
	private static final int 			MAX_CMD				= 10;
	
	private IVikiToolBase 				toolReference 		= null;
	private String 						toolName;
	private String 						toolDescription;
	private Vector<Command> 			cmdList 			= null;
	private Vector<String> 				cmdNames 			= null;
	private int[] 						cmdIdList 			= null;
	private int 						cmdIdx 				= 0;
	private Vector<SingleToolResult>  	cmdResults			= null;
	private boolean						isEnabled			= true;
	private String 						toolStatusString 	= "";
	
	
	public ToolDescriptor () {
		cmdList 	= new Vector<Command>();
		cmdIdList 	= new int[MAX_CMD];
		cmdResults 	= new Vector<SingleToolResult>();
		cmdNames 	= new Vector<String>();
	}
	
	public ToolDescriptor (String name) {
		toolName = name;
		cmdList = new Vector<Command>();
		cmdIdList = new int[MAX_CMD];
	}
	
	public void setToolReference(IVikiToolBase toolRef){
		toolReference = toolRef;
	}
	
	public void setToolName(String name){
		toolName = name;
	}
	
	public void addCommand(Command cmd){
		if (cmdList != null){
			cmdList.add(cmd);
		}
	}
	
	public void addCommandId(int ID){
		if (cmdIdList != null){
			if (cmdIdx < MAX_CMD){
				cmdIdList[cmdIdx++] = ID;
			}
		}
	}
	
	public void addCommandName(String name){
		if (cmdNames != null){
			cmdNames.add(name);
		}
	}

	public void addCommandResult(SingleToolResult result){
		if (cmdResults != null){
			cmdResults.add(result);
		}
	}
	
	public Vector<String> getCommandNames(){
		Vector<String>  names = new Vector<String>();

		for (Iterator<Command> it = cmdList.iterator(); it.hasNext();){
			Command cmd = it.next();
			
			names.add(cmd.getCommandName());
		}

		return names;
	}
	
	public String getCommandName(int ID){
		String name =null;
		for (Iterator<Command> it = cmdList.iterator(); it.hasNext();){
			Command cmd = it.next();
			
			if (cmd.getCommandId() == ID){
				name = cmd.getCommandName();
				break;
			}
		}
		return name;
	}
	
	public SingleToolResult getCommandResult(int ID){
		SingleToolResult res = null;
//		int i;
//		for (i = 0; i< cmdIdList.length; i++){
//			if (ID == cmdIdList[i]){
//				break;
//			}
//		}
		for (Iterator<Command> it = cmdList.iterator(); it.hasNext();){
			Command cmd = it.next();
			
			if (cmd.getCommandId() == ID){
				res = cmd.getCommandResult();
				break;
			}
		}

		return res;
	}
	
	public Command getCommand(int ID){
		Command cmd = null;
		for (Iterator<Command> it = cmdList.iterator(); it.hasNext();){
			cmd = it.next();
			
			if (cmd.getCommandId() == ID){
				break;
			}
		}
		return cmd;
	}
	
	public Command getCommand(String name){
		Command cmd = null;
		for (Iterator<Command> it = cmdList.iterator(); it.hasNext();){
			cmd = it.next();
			
			if (cmd.getCommandName().equals(name)){
				break;
			}else {
				cmd = null;
			}
		}
		return cmd;
	}

	public Vector<Command> getCommands () {
		return cmdList;
	}

	public int[] getCmdIdlist () {
		return cmdIdList;
	}
	
	public int getNrOfCommands(){
		return cmdList.size();
	}

	public boolean isEnabled(){
		return isEnabled;
	}
	
	public String getToolStatusString(){
		return toolStatusString;
	}
	
	public boolean hasCommandId(int ID){
		boolean bFound =false;
		
//		for (int i = 0; i< cmdIdList.length; i++){
//			if (ID == cmdIdList[i]){
//				bFound = true;
//				break;
//			}
//		}
		
		for (Iterator<Command> it = cmdList.iterator(); it.hasNext();){
			Command cmd = it.next();
			
			if (cmd.getCommandId() == ID){
				bFound = true;
				break;
			}
		}
		
		return bFound;
	}
	
	public void enableTool(boolean state){
		isEnabled = state;
		
		if (state == true){
			toolStatusString = "";
		}else {
			toolStatusString = "  --DISABLED--";
		}
	}
	
	public void clearAllResults(){
		Command cmd = null;
		for (Iterator<Command> it = cmdList.iterator(); it.hasNext();){
			cmd = it.next();
			cmd.clearCommandResult();
		}
	}
		
	public String getToolName(){return toolName;}
	public String getToolDescription(){return toolDescription;}
	
	public IVikiToolBase getToolReference() {return toolReference;}
	
}
