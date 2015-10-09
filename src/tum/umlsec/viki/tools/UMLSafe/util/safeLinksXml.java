package tum.umlsec.viki.tools.UMLSafe.util;

/**
 * @author Shunwei
 *
 */
import java.util.HashMap;

/*
 * This class saves the information from parsing of 
 * Failures.xml file 
 */
public class safeLinksXml
{
	private String name;
	private HashMap redundancyMap = new HashMap();
	private String redundancy;
	private String failure1;
	private String failure2;
	
    //  get the name of the stereotype
    public String getName()
    {
        return this.name;
    }
    
    // set the name of the stereotype
	public void setName(String _name)
	{
		this.name = _name;
	}
	
    //  get the redundancy model of the stereotype
    public String getRedundancy()
    {
        return this.redundancy;
    }
    
    // set the redundancy model of the stereotype
	public void setRedundancy(String _redundancy)
	{
		this.redundancy = _redundancy;
	}
	
    //  get the failure1 of the stereotype
    public String getFailure1()
    {
        return this.failure1;
    }
    
    // set the failure1 of the stereotype
	public void setFailure1(String _failure1)
	{
		this.failure1 = _failure1;
	}
	
    //  get the failure2 of the stereotype
    public String getFailure2()
    {
        return this.failure2;
    }
    
    // set the failure2 of the stereotype
	public void setFailure2(String _failure2)
	{
		this.failure2 = _failure2;
	}
	
    //  get the Redundancy Map of the stereotype
    public HashMap getRedundancyMap()
    {
        return this.redundancyMap;
    }
    
    // set the Redundancy Map of the stereotype
	public void setRedundancyMap()
	{
		String[] redundancyArray = new String[2];
		redundancyArray[0] = this.failure1;
		redundancyArray[1] = this.failure2;
		this.redundancyMap.put(this.redundancy, redundancyArray);
	}
}
