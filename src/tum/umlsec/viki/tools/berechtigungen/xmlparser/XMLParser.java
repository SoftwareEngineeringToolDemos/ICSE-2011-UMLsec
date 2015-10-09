
package tum.umlsec.viki.tools.berechtigungen.xmlparser;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import org.apache.crimson.tree.XmlDocument;
import org.apache.log4j.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XMLParser
{
	XmlDocument doc;
	HashMap result;
	
	public Vector allInitialisedUser;
	private static Logger logger;


	public XMLParser()
	{
		super();
		logger=Logger.getLogger("berechtigungen");
	}
	
	public HashMap parse(String fileName)
	{
		allInitialisedUser = new Vector();
		try
		{
			System.err.println("Parsing " + fileName + "...");
			
			String uri = "file:" + new File(fileName).getAbsolutePath();
			
			doc = XmlDocument.createXmlDocument(uri);
			logger.trace("xml-file parsed.");
			Node n = doc.getFirstChild();
			doRecursive(n);

		}
		catch(SAXParseException spex)
		{
			logger.error("+============================+");
			logger.error("|        *Parse Error       *|");
			logger.error("+============================+");
			logger.error("+ Line " + spex.getLineNumber() + ", uri " + spex.getSystemId());
			logger.error(spex.getClass());
			logger.error(spex.getMessage());
			logger.error("+============================+");
		}
		catch(SAXException saex)
		{
			logger.error("+============================+");
			logger.error("|       *SAX XML Error*      |");
			logger.error("+============================+");
			logger.error(saex.toString());
		}
		catch(IOException ioex)
		{
			logger.error("+============================+");
			logger.error("|    *Input/Output Error*    |");
			logger.error("+============================+");
			logger.error(ioex.toString());
		}
		
		System.out.println("there are " + allInitialisedUser.size() + " User im System.");
		for (int i = 0; i < allInitialisedUser.size(); i++)
		{
			((XMLUser)(allInitialisedUser.get(i))).getAllPermission(doc);
		}
		
		for (int i = 0; i < allInitialisedUser.size(); i++)
		{
			//System.out.println("User #" + i);
			//System.out.println("his ID is " + ((XMLUser)(allInitialisedUser.get(i))).getUserID());
			//System.out.println("his name is " + ((XMLUser)(allInitialisedUser.get(i))).getUserName());
			//System.out.println("his roles have the following ID's: ");
			String[] tmprolles = ((XMLUser)(allInitialisedUser.get(i))).getUserRolle();
			for (int x = 0; x < tmprolles.length; x++)
			{
				//System.out.println(tmprolles[x] + " ");
			}
			//System.out.println("his permissions are: ");
			Vector tmpPerm = ((XMLUser)(allInitialisedUser.get(i))).getUserPermissions();
			for (int y = 0; y < tmpPerm.size(); y++)
			{
				XMLPermission tmpxmlPermission = (XMLPermission)tmpPerm.get(y);
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
			String _name = ((XMLUser)(allInitialisedUser.get(i))).getUserName();
			Vector _permissions = ((XMLUser)(allInitialisedUser.get(i))).getUserPermissions();
			
			// alle Objects, die betrachtet werden bestimmmen:
			Vector objectIDs = new Vector();
			for (int y = 0; y < _permissions.size(); y++)
			{
				int _objectID = ((XMLPermission)(_permissions.get(y))).getObjectID();
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
					String berechtigung_name = ((XMLPermission)_permissions.get(n)).getPermissionName();
					int _oid = ((XMLPermission)_permissions.get(n)).getObjectID();
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
			logger.fatal("Node is null.");
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
			logger.fatal("Node is null.");
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
					allInitialisedUser.add(new XMLUser(name,rid,uid));
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
				XMLUser tmpXmlUser = (XMLUser)v.get(i);
				if (tmpXmlUser.getUserID() == uid)
				{
					returnValue = true;
					break;
				}
			}
		}	
		return returnValue;
	}
	
} // End of class