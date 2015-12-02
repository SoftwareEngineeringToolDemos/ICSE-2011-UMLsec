package tum.umlsec.viki.tools.berechtigungen.xmlparser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.tools.berechtigungen.mdrparser.MDRActivity;
import tum.umlsec.viki.tools.berechtigungen.mdrparser.MDRUser;

import com.sun.xml.tree.XmlDocument;

public class XMLDOMParser
{
	private static Logger logger;
	XmlDocument document;
	HashMap result;
	Vector allInitialisedUser;
	ITextOutput log;
	int modus;
	Vector allObjectIDs = new Vector();
	
	Vector _mdrUser = new Vector();
	
	public XmlDocument getDocument()
	{
		return document;
	}
	
	public XMLDOMParser(ITextOutput output)
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
		allObjectIDs.add(oid);
		
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
	
	/**
	 * Diese Methode erzeugt ein XML-Dokument, das nur
	 * die Versionsinformationen und ein Tag <information> enthaelt.
	 *
	 */
	public void createXmlDocument(Vector alleMDRUser)
	{
		// Modus setzen:
		modus = 1;
		
		_mdrUser = alleMDRUser;
		
		try
		{
			document = new XmlDocument();
		
			Node root = document.createElement("information");
			document.appendChild(root);
		}
		catch(Exception ex)
		{
			logger.fatal("XMLCreate Error: " + ex.getClass() + " " + ex.getMessage());
		}
	}
	
	public void loadXMLFile(String filename)
	{
		// Modus setzen:
		modus = 2;
		
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

	}
		
	public HashMap parse()
	{
		// Hier muss nach dem Modus unterschieden werden:
		allInitialisedUser = new Vector();
		
		Node n = document.getFirstChild();
		doRecursive(n);
		
		//System.out.println("there are " + allInitialisedUser.size() + " User im System.");
		for (int i = 0; i < allInitialisedUser.size(); i++)
		{
			// Zu jedem User die Berechtigungen zuordnen:
			//System.out.println("Starte die Zuordnung der Berechtigungen zu den einzelnen Usern!");
			((DOMXMLUser)(allInitialisedUser.get(i))).getAllPermission(document);
		}
		
		for (int i = 0; i < allInitialisedUser.size(); i++)
		{
			//System.out.println("User #" + i);
			//System.out.println("his ID is " + ((DOMXMLUser)(allInitialisedUser.get(i))).getUserID());
			//System.out.println("his name is " + ((DOMXMLUser)(allInitialisedUser.get(i))).getUserName());
			//System.out.println("his roles have the following rollID's: ");
			String[] tmprolles = ((DOMXMLUser)(allInitialisedUser.get(i))).getUserRolle();
			for(int ii = 0; ii < tmprolles.length; ii++)
			{
				//System.out.println(tmprolles[ii]);
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
		if (modus == 2)
		{
			createPairs();
		}
		else
		{
			createPairs2();
		}
		
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
	
	/**
	* Diese Funktion la"uft alle User durch und bildet Paare der folgenden Form:
	* "user+Object"--> Berechtigungen.
	* Beachtet aber gleichzeitid den _mdrUser-Vector()
	*
	*/
	private void createPairs2()
	{
		result = new HashMap();
		for(int i = 0; i < allInitialisedUser.size(); i++)
		{
			String _name = ((DOMXMLUser)(allInitialisedUser.get(i))).getUserName();
			Vector _permissions = ((DOMXMLUser)(allInitialisedUser.get(i))).getUserPermissions();
			
			// alle Objects, die betrachtet werden bestimmmen:
			Vector objectIDs = new Vector();
			for(int n = 0; n < _mdrUser.size(); n++)
			{
				MDRUser _tmpMDRUser = (MDRUser)_mdrUser.get(n);
				if(_tmpMDRUser.getUserName().equals(_name))
				{
					Vector _tmpACTVector = _tmpMDRUser.getMyActivities();
					for(int k = 0; k < _tmpACTVector.size(); k++)
					{
						MDRActivity _activity = (MDRActivity)_tmpACTVector.get(k);
						String id = _activity.getID();
						objectIDs.add(id);
					}
					break;
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
			System.out.println(p.getNodeName());
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
					//System.out.println("rid des behandelten User: " + rid);
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
			File _tmpFile = new File(adresse);
			if(_tmpFile.exists())
			{
				log.writeLn("Das schreiben der XML-Datei war erfolgreich.");
			}
		}
		catch(Exception ee)
		{
			logger.fatal(ee);
		}
		
	}
	
	
	/**
	 * Diese Funktion fuegt in dem uebergebenen XML-Document dem User eine neue Rolle hinzu.
	 * @param document
	 * @param userName
	 * @param newRolle
	 * @return
	 */
	public void addRolleToUser(String userName, String newRolle)
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
			//System.out.println("Der RollenEintrag wird angepasst!!!");
			Node user = alleuser.item(userNummer);
			NodeList userChilds = user.getChildNodes();
			for(int i = 0; i < userChilds.getLength(); i++)
			{
				Node userNode = userChilds.item(i);
				if (userNode.getNodeName().equals("rid"))
				{
					NodeList _kinderKnoten = userNode.getChildNodes();
					Node ridNode = _kinderKnoten.item(0);
					if(ridNode.getNodeValue().equals(""))
					{
						ridNode.setNodeValue(newRolle);
					}
					else
					{
						ridNode.setNodeValue(ridNode.getNodeValue() + ":" + newRolle);
					}
					//System.out.println("Der neue Wert: " + ridNode.getNodeValue());
					break;
				}
			}
		}
		
	}	
	
	/**
	 * Diese Funktion erzeugt fuer alle User und Teilaktivitaeten Knoten im XML Dokument
	 * @param _users
	 * @param _activities
	 */
	public void addNodes(Vector _mdrusers, Vector _mdractivities)
	{
		// alle User abarbeiten:
		for(int i = 0; i < _mdrusers.size(); i++)
		{
			MDRUser _tmpUser = (MDRUser)_mdrusers.get(i);
			addBenutzer(_tmpUser.getUserName(), "", Integer.toString(_tmpUser.getUserID()));
		}
		// Alle Teilaktivitaeten abarbeiten:
		for(int i = 0; i < _mdractivities.size(); i++)
		{
			MDRActivity _tmpAct = (MDRActivity)_mdractivities.get(i);
			if(!isIdInVector(_tmpAct.getID()))
			{
				addActivity(_tmpAct.getName(), _tmpAct.getID());
			}
		}
	}
	
	private boolean isIdInVector(String id)
	{
		boolean returnValue;
		returnValue = false;
		
		for (int i = 0; i < allObjectIDs.size(); i++)
		{
			if(((String)allObjectIDs.get(i)).equals(id))
			{
				returnValue = true;
				break;
			}
		}
		
		return returnValue;
	}
	
}