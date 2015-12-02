package tum.umlsec.viki.tools.checkstatic.checks;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

/**
 * @author Shasha Meng
 *
 */

public class Threaten
{

	public Threaten() {}
	
	public Vector threaten(Attacker a, Stereotype_Link s)
	{
		Vector v = new Vector();
		if(a.getName().equals("default"))
		{
  			if(s.getName().equals("Internet"))
  			{
  				v.add("read");
   				v.add("delete");
   				v.add("insert");
  			}
  			if(s.getName().equals("encrypted"))
  			{ 
  				v.add("delete");
  			} 
		}

		if(a.getName().equals("insider"))
		{
  			if(s.getName().equals("Internet"))
  			{
  				v.add("read");
   				v.add("delete");
   				v.add("insert");
 			 }
  			if(s.getName().equals("encrypted"))
  			{
  				v.add("read");
   				v.add("delete");
   				v.add("delete");
  			} 
  			if(s.getName().equals("LAN"))
  			{
  				v.add("read");
   				v.add("delete");
   				v.add("delete");
  			} 
  			if(s.getName().equals("wire"))
  			{
  				v.add("read");
   				v.add("delete");
   				v.add("delete");
  			}
  			if(s.getName().equals("POS device"))
  			{ 
  				v.add("access");
  			}
  			if(s.getName().equals("issuer node"))
  			{ 
  				v.add("access");
  			}    
		}
   
	return v;
	
	}
    public Vector threaten(Attacker a, Stereotype_Link s,HashMap map)
	{
		Vector v=new Vector();
		for(Iterator it = map.keySet().iterator();it.hasNext();){
			String stereotypeName_link =(String)it.next();
			if(s.getName().equals(stereotypeName_link)){
				java.util.Collection c=(java.util.Collection)map.get(stereotypeName_link);
				for(Iterator itc=c.iterator();itc.hasNext();){
					v.add((String)itc.next());
				}
				
			}
		}

		 
	return v;
	
	}
}