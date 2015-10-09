
package tum.umlsec.viki.tools.berechtigungen.xmlparser;

public class DOMXMLPermission
{
	private String namePermission;
	private int rolleID;
	private int permissionID;
	private int objectID;
	
//	public DOMXMLPermission(int rid, int oid, String perName)
//	{
//		namePermission  = perName;
//		rolleID 		= rid;
//		permissionID 	= permissionID +1;
//		objectID 		= oid;
//	}
	
	public DOMXMLPermission(int rid, int oid, String perName, int permid)
	{
		namePermission  = perName;
		rolleID 		= rid;
		objectID		= oid;
		permissionID	= permid;
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