package tum.umlsec.viki.tools.berechtigungen.xmlparser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLManager
{
	Document document;
	
	public XMLManager(Document document)
	{
		this.document = document;
	}
	
	// Benutzer hinzufuegen
	public Document addBenutzer(String name, String rid, String uid, Document document)
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
		
		return document;
	}
	
	public Document addActivity(String name, String oid, Document document)
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
		
		return document;
	}
	
	// Berechtigung hinzufuegen
	public Document addPermission(String rid, String bid, String name, String oid, Document document)
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
		
		return document;
	}
	
	// Rolle hinzufuegen
	public Document addRolle(String name, String rid, Document document)
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
		
		return document;
	}
	
	
	// Rolle hinzufuegen
	public Document addRolle2(String name, String rid)
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
		
		return document;
	}
	
	
	/**
	 * Diese Funktion fuegt in dem uebergebenen XML-Document dem User eine neue Rolle hinzu.
	 * @param document
	 * @param userName
	 * @param newRolle
	 * @return
	 */
	public Document addRolleToUser(Document document, String userName, String newRolle)
	{
		boolean gefunden;
		gefunden = false;
		int userNummer;
		userNummer = -1;
		
		NodeList alleuser = document.getElementsByTagName("Benutzer");
		for(int i = 0; i < alleuser.getLength(); i++)
		{
			Node tmpUser = alleuser.item(i);
			NodeList tmpUserChildNodes = tmpUser.getChildNodes();
			for(int n = 0; n < tmpUserChildNodes.getLength(); n++)
			{
				Node tmpUserChildNode = tmpUserChildNodes.item(n);
				if (tmpUserChildNode.getNodeName().equals("name"))
				{
					NodeList _tmpNodeList = tmpUserChildNode.getChildNodes();
					Node _userNameKnoten = _tmpNodeList.item(0);
					if(_userNameKnoten.getNodeValue().equals(userName))
					{
						gefunden = true;
						userNummer = i;
						break;
					}
				}
			} // End of for(n)
			
			if(gefunden)
			{
				break;
			}
			
		} // End of for(i)
		
		// den Rolleneintrag anpassen:
		if (gefunden)
		{
			Node user = alleuser.item(userNummer);
			NodeList userChilds = user.getChildNodes();
			for(int i = 0; i < userChilds.getLength(); i++)
			{
				Node userNode = userChilds.item(i);
				if (userNode.getNodeName().equals("rid"))
				{
					userNode.setNodeValue(userNode.getNodeValue() + ":" + newRolle);
					break;
				}
			}
		}
		
		return document;
	}
	
} // End of class