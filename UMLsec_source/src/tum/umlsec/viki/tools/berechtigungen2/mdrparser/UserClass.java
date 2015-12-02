/*
 * Created on 19.08.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package tum.umlsec.viki.tools.berechtigungen2.mdrparser;

import java.util.Vector;

/**
 * @author josef
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class UserClass {
	
	static int id = 0;
	private String uName;
	private int uid;
	Vector _myObjects = new Vector();

	public UserClass(String _uName)
	{
		id++;
		uName =  _uName;
		uid = id;
	}

	public String getUserName()
	{
		return uName;
	}
	
	public String getUserID()
	{
		return String.valueOf(uid);
	}
	
	public Vector getProtected()
	{
		return _myObjects;
	}
	
	public void addProtected(ProtectedClass _prot)
	{
		_myObjects.add(_prot);
	}
	
	public static void resetUserId()
	{
		id = 0;
	}
}
