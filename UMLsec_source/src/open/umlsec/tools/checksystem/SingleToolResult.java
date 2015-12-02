package open.umlsec.tools.checksystem;

import tum.umlsec.viki.framework.ILogOutput;
import tum.umlsec.viki.framework.ITextOutput;

/**
 * @author ska
 */

public class SingleToolResult implements ILogOutput, ITextOutput {
	
	private boolean 	checkPassed 	= false;
	private String 		checkDetails 	= null;
	private String 		checkDateTime	= null;

	public void write(String _s){
		checkDetails = checkDetails + _s;
	}
	
	public void writeLn(String _s){
		checkDetails = checkDetails + _s + "\n";
	}
	
	public void writeLn(){
		checkDetails = checkDetails + "\n";
	}
	
    public void appendLog(String str) {
		checkDetails = checkDetails + str;
	}

	public void appendLogLn(String str) {
		checkDetails = checkDetails + str + "\n";
	}

	public void appendLogLn() {
		checkDetails = checkDetails + "\n";
	}
	
	public SingleToolResult(){
		checkDetails 	= new String();
		checkDateTime 	= new String();
	}
	
	public void addResultString(String result){
		checkDetails = checkDetails + result + "\n";
	}
	
	public void setDateTime(String dateTime){
		checkDateTime = dateTime;
	}
	
	public void setResult(boolean bResult){
		checkPassed = bResult;
	}
	
	public String getResultDetails(){
		return checkDetails;
	}

	public String getDateTime(){
		return checkDateTime;
	}
	
	public boolean getResult(){
		return checkPassed;
	}
	
	public void clearAll(){
		checkDetails 	= "";
		checkDateTime 	= "";
		checkPassed 	= false;
	}
}
