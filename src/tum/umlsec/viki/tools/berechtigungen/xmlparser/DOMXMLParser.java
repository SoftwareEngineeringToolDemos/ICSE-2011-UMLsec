package tum.umlsec.viki.tools.berechtigungen.xmlparser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import tum.umlsec.viki.framework.ITextOutput;

import com.sun.xml.tree.XmlDocument;

public class DOMXMLParser
{
	XmlDocument document;
	HashMap result;
	Vector allInitialisedUser;
	ITextOutput log;
	private static Logger logger;
	
	public Document getDocument()
	{
		return document;
	}
	
	public DOMXMLParser(ITextOutput output)
	{
		log = output;
		logger=Logger.getLogger("berechtigungen");
	}
	
	public Vector getAllInitialisedUsers()
	{
		return allInitialisedUser;
	}
	
	// Aktivitaet hinzufuegen
	public void addActivity(String name, String oid)
	{
		Element newActivity = document.createElement("Objekt");
		Element myName = document.createElement("name");
		Element myOid = document.createElement("oid");
		
		myName.appendChild(document.createTextNode(name));
		myOid.appendChild(document.createTextNode(oid));
		
		newActivity.appendChild(myName);
		newActivity.appendChild(myOid);
		
		NodeList information = document.getElementsByTagName("information");
		
		// anhaengen:
		Node info = information.item(0);
		info.appendChild(newActivity);
	}
	
	// Berechtigung hinzufuegen
	public void addPermission(String rid, String bid, String name, String oid)
	{
		Element newPermission = document.createElement("Berechtigung");
		Element myRID = document.createElement("rid");
		Element myBID = document.createElement("bid");
		Element myNAME = document.createElement("name");
		Element myOID = document.createElement("oid");
		
		myRID.appendChild(document.createTextNode(rid));
		myBID.appendChild(document.createTextNode(bid));
		myNAME.appendChild(document.createTextNode(name));
		myOID.appendChild(document.createTextNode(oid));

		newPermission.appendChild(myRID);
		newPermission.appendChild(myBID);
		newPermission.appendChild(myNAME);
		newPermission.appendChild(myOID);
		
		NodeList information = document.getElementsByTagName("information");
		
		// anhaengen:
		Node info = information.item(0);
		info.appendChild(newPermission);
	}
	
	// Berechtigung entfernen
	public void deletePermissionByBID(String bid)
	{
		NodeList nodelist = document.getElementsByTagName("Berechtigung");
		
		for(int i=0; i<nodelist.getLength(); i++)
		{
			Node n = nodelist.item(i);
			if (n == null)
			{
				continue;
			}
			NodeList nodes = n.getChildNodes();
			
			for(int x=0; x<nodes.getLength(); x++)
			{
				Node tmp = nodes.item(x);
				if (tmp == null)
				{
					continue;
				}
				
				if (tmp.getNodeName().equals("bid"))
				{
					String mybid = tmp.getFirstChild().getNodeValue();
					if (mybid.equals(bid))
					{
						//remove this Permission:
						removeNode(n);
					}
				}

			}
		}
	}
	
	public void removeNode(Node remNode)
	{
		NodeList information = document.getElementsByTagName("information");
		
		Node info = information.item(0);
		info.removeChild(remNode);
	}
	
	// Rolle hinzufuegen
	public void addRolle(String name, String rid)
	{
		Element newRolle = document.createElement("Rolle");
		Element myName = document.createElement("name");
		Element myRid = document.createElement("rid");
		
		myName.appendChild(document.createTextNode(name));
		myRid.appendChild(document.createTextNode(rid));
		
		newRolle.appendChild(myName);
		newRolle.appendChild(myRid);
		
		NodeList information = document.getElementsByTagName("information");
		
		// anhaengen:
		Node info = information.item(0);
		info.appendChild(newRolle);
	}
		
	// Benutzer hinzufuegen
	public void addBenutzer(String name, String rid, String uid)
	{
		Element newBenutzer= document.createElement("Benutzer");
		Element myNAME = document.createElement("name");
		Element myRID = document.createElement("rid");
		Element myUID = document.createElement("uid");
		
		myNAME.appendChild(document.createTextNode(name));
		myRID.appendChild(document.createTextNode(rid));
		myUID.appendChild(document.createTextNode(uid));

		newBenutzer.appendChild(myNAME);
		newBenutzer.appendChild(myRID);
		newBenutzer.appendChild(myUID);
		
		NodeList information = document.getElementsByTagName("information");
		
		// anhaengen:
		Node info = information.item(0);
		info.appendChild(newBenutzer);
	}
	
	
	public Document loadXMLFile(String filename)
	{
		
		FileInputStream inStream;
		String xmlDocumentPath = "file:" + new File(filename).getAbsolutePath();
		try
		{
			document = XmlDocument.createXmlDocument(xmlDocumentPath);
		}
		catch(Exception e)
		{
			logger.fatal("Fehler beim Parsen der XML-Datei: " + e);
		}
		
		return document;

	}
	
		
	public HashMap parse(Document doc)
	{
		allInitialisedUser = new Vector();
		
		Node n = doc.getFirstChild();
		doRecursive(n);
		
		//System.out.println("there are " + allInitialisedUser.size() + " User im System.");
		for (int i = 0; i < allInitialisedUser.size(); i++)
		{
			((DOMXMLUser)(allInitialisedUser.get(i))).getAllPermission(doc);
		}
		
		for (int i = 0; i < allInitialisedUser.size(); i++)
		{
			//System.out.println("User #" + i);
			//System.out.println("his ID is " + ((DOMXMLUser)(allInitialisedUser.get(i))).getUserID());
			//System.out.println("his name is " + ((DOMXMLUser)(allInitialisedUser.get(i))).getUserName());
			//System.out.println("his roles have the following ID's: ");
			String[] tmprolles = ((DOMXMLUser)(allInitialisedUser.get(i))).getUserRolle();
			for (int x = 0; x < tmprolles.length; x++)
			{
				//System.out.println(tmprolles[x] + " ");
			}
			//System.out.println("his permissions are: ");
			Vector tmpPerm = ((DOMXMLUser)(allInitialisedUser.get(i))).getUserPermissions();
			for (int y = 0; y < tmpPerm.size(); y++)
			{
				DOMXMLPermission tmpxmlPermission = (DOMXMLPermission)tmpPerm.get(y);
				//System.out.println("Permission #" + y);
				//System.out.println("bezueglich Objekt " + tmpxmlPermission.getObjectID() + " hat er die folgende Berechtigung: ");
				//System.out.println(tmpxmlPermission.getPermissionName());
			}
		}
		createPairs();
		
		return result;
	}

	/**
	* Diese Funktion la"uft alle User durch und bildet Paare der folgenden Form:
	* "user+Object"--> Berechtigungen 
	*
	*/
	private void createPairs()
	{
		result = new HashMap();
		for(int i = 0; i < allInitialisedUser.size(); i++)
		{
			String _name = ((DOMXMLUser)(allInitialisedUser.get(i))).getUserName();
			Vector _permissions = ((DOMXMLUser)(allInitialisedUser.get(i))).getUserPermissions();
			
			// alle Objects, die betrachtet werden bestimmmen:
			Vector objectIDs = new Vector();
			for (int y = 0; y < _permissions.size(); y++)
			{
				int _objectID = ((DOMXMLPermission)(_permissions.get(y))).getObjectID();
				if(!objectExistsOlready(_objectID, objectIDs))
				{
					objectIDs.add(new Integer(_objectID));
				}
			}
			
			// nun alle Objects durchgehen und Paare bilden
			for (int y = 0; y < objectIDs.size(); y++)
			{
				//System.out.println("behandelt wird: " + objectIDs.get(y).toString());
				String prefix = _name + ":" + objectIDs.get(y).toString();
				//System.out.println("prefix: " + prefix);
				String suffix = "";
				int count = 0;
				for (int n = 0; n < _permissions.size(); n++)
				{
					String berechtigung_name = ((DOMXMLPermission)_permissions.get(n)).getPermissionName();
					int _oid = ((DOMXMLPermission)_permissions.get(n)).getObjectID();
					if(_oid == Integer.parseInt(objectIDs.get(y).toString()))
					{
					
						if (count == 0)
						{
							suffix = berechtigung_name;
							count++;
						}
						else
						{
							suffix = suffix + ":" + berechtigung_name;
						}
					}
				}
				//System.out.println("suffix: " + suffix);
				String endString = prefix + "=" + suffix;
				//System.out.println(endString);
			
				result.put(prefix, suffix);	
			}
			
		}
	}

	private boolean objectExistsOlready(int id, Vector vec)
	{
		for (int n = 0; n < vec.size(); n++)
		{
			if (Integer.parseInt(vec.get(n).toString()) == id)
			{
				return true;
			}
		}
		return false;
	}

	protected void doRecursive(Node p)
	{
		if (p == null)
		{
			logger.error("Node is null.");
			return;
		}
		
		if (p.getNodeName().equals("Benutzer"))
		{
			//System.out.println(p.getNodeName());
			initializeUser(p);
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
	
	private void initializeUser(Node p)
	{
		String name = "";
		int uid = 0;
		String rid = "";
		boolean nameFlag, uidFlag, ridFlag;
		
		nameFlag = uidFlag = ridFlag = false;
		
		if (p == null)
		{
			logger.error("Node is null.");
			return;
		}
		
		if (p.getNodeName().equals("Benutzer"))
		{
			//System.out.println(p.getNodeName());
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
					rid = n.getFirstChild().getNodeValue();
					ridFlag = true;
				}
				if (n.getNodeName().equals("uid"))
				{
					uid = Integer.parseInt(n.getFirstChild().getNodeValue());
					uidFlag = true;
				}
				if (nameFlag && uidFlag && ridFlag && (!checkExisting(uid, allInitialisedUser)))
				{
					// neuen XMLUser erzeugen und in den Vector schieben:
					allInitialisedUser.add(new DOMXMLUser(name,rid,uid));
				}
			}
		}
		
	}
	
	/**
	 * 
	 * @param uid is the userID
	 * @param v is Vector with all initialised User
	 * @return true if there is an User with UserID uid; false if there is no User with this id number.
	 */
	private boolean checkExisting(int uid, Vector v)
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
				DOMXMLUser tmpXmlUser = (DOMXMLUser)v.get(i);
				if (tmpXmlUser.getUserID() == uid)
				{
					returnValue = true;
					break;
				}
			}
		}	
		return returnValue;
	}
	
	public void writeXMLFile(String adresse)
	{
		
		try
		{
			FileOutputStream fos = new FileOutputStream(new File(adresse));
			document.write(fos);
		}
		catch(Exception ee)
		{
			logger.fatal(ee);
		}
		
	}
	
}