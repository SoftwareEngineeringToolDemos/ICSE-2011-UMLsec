
package tum.umlsec.viki.tools.berechtigungen.xmlparser;

import java.util.Vector;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DOMXMLUser
{
	private String userName;
	private int userID;
	private Vector permission;
	private String[] roles;
	private static Logger logger;

// die Rolle wird nun als String (durch : getrennt) uebergeben...

	public DOMXMLUser (String name, String rolle, int userid)
	{
		logger=Logger.getLogger("berechtigungen");
		userName = name;
		userID = userid;
		permission = new Vector();
		roles = rolle.split(":");
	}
	
	public DOMXMLUser (String name, int userid)
	{
		userName = name;
		userID = userid;
	}
		
	public String getUserName()
	{
		return userName;
	}
	
	public String[] getUserRolle()
	{
		return roles;
	}
	
	public int getUserID()
	{
		return userID;
	}
	
	public Vector getUserPermissions()
	{
		return permission;
	}
	
	public void getAllPermission(Document doc)
	{
		Node p = doc.getFirstChild();
		doRecursive(p);
	}

	protected void doRecursive(Node p)
	{
		if (p == null)
		{
			logger.error("Node is null.");
			return;
		}
		
		if (p.getNodeName().equals("Berechtigung"))
		{
			//System.out.println(p.getNodeName());
			initializePermission(p);
		}
		
		NodeList nodes = p.getChildNodes();
		int numElem = nodes.getLength();
		
		for(int i=0; i<numElem; i++)
		{
			Node n = nodes.item(i);
			if (n == null)
			{
				continue;
			}
			doRecursive(n);
		}
	}

	private void initializePermission(Node p)
	{
		//System.out.println("------------------------------------------");
		//System.out.println("initialisierung der Berechtigung laeuft...");
		String name = "";
		int bid = 0;
		int rid = 0;
		int oid = 0;
		
		boolean nameFlag, bidFlag, ridFlag, oidFlag;
		
		nameFlag = bidFlag = ridFlag = oidFlag = false;
		
		if (p == null)
		{
			logger.error("Node is null.");
			return;
		}
		
		if (p.getNodeName().equals("Berechtigung"))
		{
			NodeList nodes = p.getChildNodes();
			int numElem = nodes.getLength();
			
			for(int i=0; i<numElem; i++)
			{
				Node n = nodes.item(i);
				if (n == null)
				{
					continue;
				}
				//System.out.println(n.getNodeName());
				if (n.getNodeName().equals("name"))
				{
					//System.out.println("name: " + n.getFirstChild().getNodeValue());
					name = n.getFirstChild().getNodeValue();
					nameFlag = true;
				}
				if (n.getNodeName().equals("rid"))
				{
					rid = Integer.parseInt(n.getFirstChild().getNodeValue());
					//System.out.println("rid = " + rid);
					ridFlag = true;
					if (!existing(rid))
					{
						// sicherstellen, dass nur die eigene Rolle angeschaut wird
						nameFlag = bidFlag = ridFlag = oidFlag = false;
						break;
					}
				}
				if (n.getNodeName().equals("bid"))
				{
					bid = Integer.parseInt(n.getFirstChild().getNodeValue());
					//System.out.println("bid = " + bid);
					bidFlag = true;
				}
				if (n.getNodeName().equals("oid"))
				{
					oid = Integer.parseInt(n.getFirstChild().getNodeValue());
					//System.out.println("oid = " + oid);
					oidFlag = true;
				}	
				System.out.println(checkExisting(bid, permission));
				//System.out.println("nameFlag:" + nameFlag);	
				//System.out.println("bidFlag:" + bidFlag);	
				//System.out.println("ridFlag:" + ridFlag);	
				//System.out.println("oidFlag:" + oidFlag);			
				if (nameFlag && bidFlag && ridFlag && oidFlag &&(!checkExisting(bid, permission)))
				{
					// neuen XMLUser erzeugen und in den Vector schieben:
					//System.out.println("Die Berechtigung wird hinzugefuegt.");
					permission.add(new DOMXMLPermission(rid, oid, name, bid));
				}
			}
		}
		//System.out.println("initialisierung der Berechtigung gelaufen...");
		//System.out.println("-------------------------------------------");
	}

	/**
	 * 
	 * @param bid is the berechtigungID
	 * @param v is Vector with all initialised Permissions
	 * @return true if there is a permission with permissionID bid; false if there is no permission
	 * with this id number.
	 */
	private boolean checkExisting(int bid, Vector v)
	{
		boolean returnValue;
		returnValue = false; 
		
		if (v.isEmpty())
		{
			logger.error("fall out!");
			return returnValue;
		}
		else
		{
			for (int i = 0; i < v.size(); i++)
			{
				DOMXMLPermission tmpXmlPermission = (DOMXMLPermission)v.get(i);
				//System.out.println("vergliechen wird " + bid + " mit " + tmpXmlPermission.getPermissionID());
				if (tmpXmlPermission.getPermissionID() == bid)
				{
					returnValue = true;
				}
			}
		}	
		return returnValue;
	}
	
	private boolean existing(int rid)
	{	
		for (int i = 0; i <roles.length; i++)
		{
			//System.out.println("length = " + roles.length);
			//System.out.println(roles[i].toString());
			if ((roles[i].toString()+"").equals(""))
			{
				continue;
			}
			int tmp = Integer.parseInt(roles[i]);
			if (tmp == rid)
			{
				return true;
			}
		}
		
		return false;
	}
	
} // End of class 