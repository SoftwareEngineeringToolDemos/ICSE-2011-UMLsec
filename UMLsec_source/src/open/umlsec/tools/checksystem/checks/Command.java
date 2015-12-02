/**
 * 
 */
package open.umlsec.tools.checksystem.checks;

import open.umlsec.tools.checksystem.SingleToolResult;
import open.umlsec.tools.checksystem.gui.SystemVerificationLoader;

/**
 * @author ska
 *
 */
public class Command {

	private boolean 			enabled 	= true;
	private SingleToolResult 	cmdResult 	= null;
	private int 				Id;
	private String 				cmdName;
	private String 				cmdStatusString = "";
	
	public Command (int id, String name){
		Id 			= id;
		cmdName 	= name;
		cmdResult 	= new SingleToolResult();
	}
	
	public void enableCommand(boolean state){
		enabled = state;
		
		if (state == true){
			cmdStatusString = "";
		}else {
			cmdStatusString = "  --DISABLED--";
		}
		
		SystemVerificationLoader.logger.debug("Command: " + cmdName + " state: " 
													+ String.valueOf(enabled));
	}
	
	public boolean isEnabled(){
		return enabled;
	}
	
	public int getCommandId(){
		return Id;
	}
	
	public String getCommandName(){
		return cmdName;
	}
	
	public String getCommandStatusString() {
		return cmdStatusString;
	}
	
	public SingleToolResult getCommandResult(){
		return cmdResult;
	}
	
	public void clearCommandResult(){
		cmdResult.clearAll();
	}
	
}
