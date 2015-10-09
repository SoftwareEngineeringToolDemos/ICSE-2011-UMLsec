
package tum.umlsec.viki.tools.sapperm.mdrparser;

import java.util.StringTokenizer;
import java.util.Vector;

public class MDRActivity2
{

	private String actName;
	private String trans_id;
        private Vector usersVect;
        private MDRStereotype ster;
        
	public MDRActivity2(String name, String id, String users, MDRStereotype st)
	{
		actName =  name;
		trans_id = id;
                ster = st;
                usersVect = new Vector();
                setUsers(users);
	}

	private void setUsers(String userStr)
	{
            StringTokenizer st = new StringTokenizer(userStr, ";");
            while (st.hasMoreTokens())
            {
                usersVect.add(st.nextElement());
            }
		
	}
        
	public String getName()
	{
		return actName;
	}
	
	public String getID()
	{
		return trans_id;
	}
        
	public Vector getUsers()
	{
		return usersVect;
	}

       	public MDRStereotype getStereotype()
	{
		return ster;
	}
 
} // End of class