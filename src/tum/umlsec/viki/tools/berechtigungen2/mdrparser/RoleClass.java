/*
 * Created on 19.08.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package tum.umlsec.viki.tools.berechtigungen2.mdrparser;

/**
 * @author josef
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RoleClass {
	
	static int id = 0;
	
	//private String username;
	private String rolename;
	//private int uid;
	private int rid;

	public RoleClass(String rolName)
	{
		//username = uName;
		id++;
		rolename = rolName;
		//uid = uID;
		rid = id;	
		//System.out.println("Rollenname: "+ rolName + "  RollenId: " + rid);
	}

	public String getRoleName()
	{
		return rolename;
	}
	
	public String getRoleID()
	{
		return String.valueOf(rid);
	}
	
	public static void resetRoleId()
	{
		id = 0;
	}

}
