package open.umlsec.tools.checksystem;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import open.umlsec.tools.checksystem.checks.ToolDescriptor;
import open.umlsec.tools.checksystem.gui.SystemVerificationLoader;

/**
 * @author ska
 */

public class SystemAnalysis {
	
	private String 	strAnalysisName = null;
	private String 	strAnalysisDesc = null;
	private String 	strAnalysisRes	= "";
	private File	fiReqXMLFile 	= null;
	
	private boolean bIsEmpty = true;
	
	/**
	 *  constructor
	 */
	
	public SystemAnalysis()	{
		
	}
	
	public SystemAnalysis(String strName, String strDescription)	{
		strAnalysisName = strName;
		strAnalysisDesc = strDescription;
		bIsEmpty		= false;
	}

	public SystemAnalysis(String strName, String strDescription, File fiRequirementXML)	{
		strAnalysisName = strName;
		strAnalysisDesc = strDescription;
		fiReqXMLFile 	= fiRequirementXML;
		bIsEmpty		= false;
	}
	
	/**
	 *  public methods
	 */
	
	public boolean isEmpty() {
		return bIsEmpty;
	}
	
	public void clearAll() {
		bIsEmpty = true;
	}
	
	public void setName(String strName)	{
		strAnalysisName = strName;
		bIsEmpty		= false;
	}

	public void setDescription(String strDescription) {
		strAnalysisDesc = strDescription;
		bIsEmpty		= false;
	}
	
	public void setReqXMLFile(File fiRequirementXML) {
		fiReqXMLFile = fiRequirementXML;
		bIsEmpty		= false;
	}

	public String getName()	{
		return strAnalysisName;
	}

	public String getDescription() {
		return strAnalysisDesc;
	}
	
	public File getReqXMLFile() {
		return fiReqXMLFile;
	}
	
	public String getAllResults(){
		ToolSystemVerification 		systTool;
		ToolDescriptor 				tool;
	    int 						i, j, ID;
	    DateFormat 					dateFormat;
	    Date 						date;
	    
	    systTool = (ToolSystemVerification)
		SystemVerificationLoader.getTool(
			SystemVerificationLoader.TOOL_IDX_CHECKSYSTEM);
	    
	    strAnalysisRes = "";
	    
	    /* get the actual date and time */
	    dateFormat 	= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	    date 		= new Date();
	    System.out.println("Current Date Time : " + dateFormat.format(date));

	    strAnalysisRes = strAnalysisRes + "==================================="+ "\n"; 
	    strAnalysisRes = strAnalysisRes + "Name: " + this.getName() + "\n"; 
	    strAnalysisRes = strAnalysisRes + "Description: " 
	    										+ this.getDescription() + "\n"; 
	    strAnalysisRes = strAnalysisRes + "Results retrieved: " 
	    										+ dateFormat.format(date) + "\n"; 
	    
	    strAnalysisRes = strAnalysisRes + "==================================="+ "\n"; 

	    try {
			for (i = 0; i < systTool.getToolList().size(); i++){
    			tool = systTool.getToolList().get(i);
    			
    			for (j = 0; j < tool.getNrOfCommands(); j++){
    				ID = tool.getCmdIdlist()[j];
    				strAnalysisRes = strAnalysisRes + 
    							tool.getCommandResult(ID).getResultDetails();
    			}
			}
		} catch (Exception e) {
		      System.err.println("Error: " + e.getMessage());
		}

	    return strAnalysisRes;
	}
	
	/**
	 *  private methods
	 */
	
	
	
}
