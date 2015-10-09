
package tum.umlsec.viki.tools.berechtigungen.mdrparser;

import java.util.Vector;

public class MDRUser
{
	static int id = 0;
	Vector _myObjects = new Vector();
	
	private String userName;
	private int userID;
	
	public MDRUser(String name)
	{
		id++;
		userName = name;
		userID = id;
	}
	
	public int getAnzahlUser()
	{
		return id;
	}
	
	public int getUserID()
	{
		return userID;
	}
	
	public String getUserName()
	{
		return userName;
	}
	
	public Vector getMyActivities()
	{
		return _myObjects;
	}
	
	public void addActivity(MDRActivity _act)
	{
		_myObjects.add(_act);
	}
} // End of class