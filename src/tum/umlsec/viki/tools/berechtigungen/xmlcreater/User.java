
package tum.umlsec.viki.tools.berechtigungen.xmlcreater;

public class User
{
	private String nameUser;
	private int userID;
	static int correntUser = 0;
	
	
	public User(String name, int id)
	{
		nameUser = name;
		userID = id;
		correntUser++;
	}
	
	public int getUserID()
	{
		return userID;
	}
	
	public String getUserName()
	{
		return nameUser;
	}
	
} // End of class