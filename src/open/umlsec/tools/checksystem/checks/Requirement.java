/**
 * 
 */
package open.umlsec.tools.checksystem.checks;


/**
 * @author ska
 *
 */
public class Requirement {
	private int id 				= 0;
	private int dependencyId 	= 0;
	
	private String reqDescription = "";
	private String subSystem = "";
	private String idString = "";
	private String status = "NOT ANALYSED";

	public Requirement(int _id, int _depId, String _desc, String _subSystem){
		id 				= _id;
		dependencyId 	= _depId;
		reqDescription 	= _desc;
		subSystem		= _subSystem;
	}
	
	public int getId(){
		return id;
	}
	
	public int getDependency(){
		return dependencyId;
	}
	
	public String getDescription(){
		return reqDescription;
	}

	public String getSubSystem(){
		return subSystem;
	}
	
	public String getIDString(){
		
		if (dependencyId != 0){
			idString = dependencyId + "." + id;
		}else {
			idString = String.valueOf(id);
		}
		
		return idString;
	}
	
	public String getStatus(){
		return status;
	}
	
	public void setStatus(String stat){
		status = stat;
	}
}
