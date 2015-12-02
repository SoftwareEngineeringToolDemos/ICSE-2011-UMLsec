
package tum.umlsec.viki.tools.sapperm.xmlparser;

import java.util.Vector;

import org.apache.crimson.tree.XmlDocument;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLUser
{
	private String userName;
	private int userID;
	private Vector permission;
	private String[] roles;

// die Rolle wird nun als String (durch : getrennt) uebergeben...

	public XMLUser (String name, String rolle, int userid)
	{
		userName = name;
		userID = userid;
		permission = new Vector();
		roles = rolle.split(":");
	}
	
	public XMLUser (String name, int userid)
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
	
	public void getAllPermission(XmlDocument doc)
	{
		Node p = doc.getFirstChild();
		doRecursive(p);
	}

	protected void doRecursive(Node p)
	{
		if (p == null)
		{
			System.out.println("Node is null.");
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
		//System.out.println("initialisierung laeuft...");
		String name = "";
		int bid = 0;
		int rid = 0;
		int oid = 0;
		
		boolean nameFlag, bidFlag, ridFlag, oidFlag;
		
		nameFlag = bidFlag = ridFlag = oidFlag = false;
		
		if (p == null)
		{
			System.out.println("Node is null.");
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
					name = n.getFirstChild().getNodeValue();
					nameFlag = true;
				}
				if (n.getNodeName().equals("rid"))
				{
					rid = Integer.parseInt(n.getFirstChild().getNodeValue());
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
					bidFlag = true;
				}
				if (n.getNodeName().equals("oid"))
				{
					oid = Integer.parseInt(n.getFirstChild().getNodeValue());
					oidFlag = true;
				}				
				if (nameFlag && bidFlag && ridFlag && oidFlag &&(!checkExisting(bid, permission)))
				{
					// neuen XMLUser erzeugen und in den Vector schieben:
					permission.add(new XMLPermission(bid, rid, oid, name));
				}
			}
		}
		
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
			return returnValue;
		}
		else
		{
			for (int i = 0; i < v.size(); i++)
			{
				XMLPermission tmpXmlPermission = (XMLPermission)v.get(i);
				if (tmpXmlPermission.getPermissionID() == bid)
				{
					returnValue = true;
					break;
				}
			}
		}	
		return returnValue;
	}
	
	private boolean existing(int rid)
	{	
		for (int i = 0; i < roles.length; i++)
		{
			//System.out.println("length = " + roles.length);
			//System.out.println(roles[i].toString());
			int tmp = Integer.parseInt(roles[i]);
			if (tmp == rid)
			{
				return true;
			}
		}
		
		return false;
	}
	
} // End of class 