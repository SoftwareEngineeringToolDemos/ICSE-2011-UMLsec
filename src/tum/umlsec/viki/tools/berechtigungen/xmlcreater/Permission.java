
package tum.umlsec.viki.tools.berechtigungen.xmlcreater;

public class Permission
{
	private String namePermission;
	private int permissionID;
	
	public Permission(String name, int id)
	{
		namePermission = name;
		permissionID = id;
	}
	
	public int getPermissionID()
	{
		return permissionID;
	}
	
	public String getPermissionName()
	{
		return namePermission;
	}
	
} // End of class