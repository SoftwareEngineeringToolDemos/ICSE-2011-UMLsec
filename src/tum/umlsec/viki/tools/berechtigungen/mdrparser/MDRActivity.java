
package tum.umlsec.viki.tools.berechtigungen.mdrparser;

public class MDRActivity
{

	private String oname;
	private String oid;

	public MDRActivity(String name, String id)
	{
		oname =  name;
		oid = id;
	}

	public String getName()
	{
		return oname;
	}
	
	public String getID()
	{
		return oid;
	}

} // End of class