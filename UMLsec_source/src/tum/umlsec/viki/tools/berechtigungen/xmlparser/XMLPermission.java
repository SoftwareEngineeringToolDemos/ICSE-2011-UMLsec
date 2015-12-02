
package tum.umlsec.viki.tools.berechtigungen.xmlparser;

public class XMLPermission
{
	private String namePermission;
	private int rolleID;
	private int permissionID;
	private int objectID;
	
	public XMLPermission(int bid, int rid, int oid, String perName)
	{
		namePermission  = perName;
		rolleID 		= rid;
		permissionID 	= bid;
		objectID 		= oid;
	}

	public String getPermissionName()
	{
		return namePermission;
	}
	
	public int getRoleID()
	{
		return rolleID;
	}
	
	public int getPermissionID()
	{
		return permissionID;
	}
	
	public int getObjectID()
	{
		return objectID;
	}

} // End of class