/*
 * Created on 22.07.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package tum.umlsec.viki.tools.berechtigungen2;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.tools.berechtigungen.mdrparser.MDRActivity;
import tum.umlsec.viki.tools.berechtigungen.mdrparser.MDRUser;
import tum.umlsec.viki.tools.berechtigungen.xmlparser.DOMXMLPermission;
import tum.umlsec.viki.tools.berechtigungen.xmlparser.DOMXMLUser;
import tum.umlsec.viki.tools.berechtigungen.xmlparser.NumberGeneratorBID;
import tum.umlsec.viki.tools.berechtigungen2.mdrparser.ProtectedClass;
import tum.umlsec.viki.tools.berechtigungen2.umlparser.UmlParser;

/**
 * @author josef
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BerTools2 
{
//	 Counter for the BID's:
	NumberGeneratorBID ngBID = new NumberGeneratorBID();
	
	ITextOutput _mainOutput;
	private static Logger logger;
	public BerTools2(ITextOutput mainOutput)
	{
		_mainOutput = mainOutput;
		logger=Logger.getLogger("berechtigungen2");
	}
	
	/**
	 * This method checks, if every user has got enough permissions.
	 * @param activity Hier steht, was der Anwender mindestens haben muss.
	 * @param permissions Hier steht, was der Anwender tatsaechlich hat.
	 */
	public void checkPermissions(HashMap activity, HashMap permissions)
	{
		// start test-method for every single activity:
		Iterator act_it = activity.keySet().iterator();
		while(act_it.hasNext())
		{
			String a_key = (String)act_it.next();
			String a_value = (String)activity.get(a_key);
		
			String p_value = (String)permissions.get(a_key);
			if (a_value != null && p_value != null)
			{
				checkAusreichendBerechtigungen(a_value, p_value);
			}
			else
			{
				logger.error("one of the values is null");
			}
		}
	}
	
	/**
	 * This method checks, if the strind "perm" contains every permission of the string "act"
	 * @param act
	 * @param perm
	 */
	private void checkAusreichendBerechtigungen(String act, String perm)
	{
		String[] actArray;
		String[] permArray;
		boolean gefundenFlag;
		
		actArray  = makeArray(act);
		permArray = makeArray(perm);		
		
		for (int i = 0; i < actArray.length; i++)
		{
			gefundenFlag = false;
			
			for (int y = 0; y < permArray.length; y++)
			{
				if (actArray[i].equals(permArray[y]))
				{
					//System.out.println("gefunden: " + actArray[i]);
					gefundenFlag = true;
					break;
				}
			}
			if (!gefundenFlag)
			{
				_mainOutput.writeLn("The permission " + actArray[i] + " is missing!");
			}
		}
		
	}
	
	/**
	 * This method checks, if the permissions-HashMap contains more permissions, as required by the
	 * activity-HashMap.
	 * @param activity
	 * @param permissions
	 */
	public void checktomuchPermissions(HashMap activity, HashMap permissions)
	{
		// start test-method for every single activity:
		Iterator act_it = activity.keySet().iterator();
		while(act_it.hasNext())
		{
			String a_key = (String)act_it.next();
			String a_value = (String)activity.get(a_key);
		
			String p_value = (String)permissions.get(a_key);
			if (a_value != null && p_value != null)
			{
				checkZuvieleBerechtigungen(a_value, p_value);
			}
			else
			{
				logger.error("one of the values is null");
				return;
			}
			
		}
	}
	
	/**
	 * This method checks, if the string "perm" contains more permissions, as required by the
	 * the string "act".
	 * @param act
	 * @param perm
	 */
	private boolean checkZuvieleBerechtigungen(String act, String perm)
	{
		String[] actArray;
		String[] permArray;
		boolean gefundenFlag;
		boolean returnValue;
		returnValue = false;
		logger.trace("act: " + act);
		logger.trace("perm: " + perm);
		permArray = makeArray(perm);	
		if(act != null)
		{
			actArray  = makeArray(act);		
		}
		else
		{
			actArray = new String[0];
		}
		
		for (int i = 0; i < permArray.length; i++)
		{
			
			gefundenFlag = false;
			
			for (int y = 0; y < actArray.length; y++)
			{
				if (permArray[i].equals(actArray[y]))
				{
					//System.out.println("gefunden: " + actArray[i]);
					gefundenFlag = true;
					returnValue = true;
					break;
				}
			}
			if (!gefundenFlag)
			{
				_mainOutput.writeLn("The permission " + permArray[i] + " is not used!");
				returnValue = false;
				// if found --> leaf the loop!
				break;
			}
		}
		
		return returnValue;
	}	

	/**
	 *This method divedes a string, that contains words divided by ":" and creates an array of strings.
	 * @param myString
	 * @return
	 */
	private String[] makeArray(String myString)
	{
		String[] returnArray;
		
		if (myString.lastIndexOf(":")==-1)
		{
			returnArray = new String[1];
			returnArray[0] = myString;
		}
		else
		{
			returnArray = myString.split(":");
		}
		
		return returnArray;
		
	}
	
	/**
	 * This method processes both tests sequentially. If the algorithm finds an error, the test stopps 
	 * and returns a string-array with all necessary values.
	 * 
	 * resultValue[0] = index for missing or unneeded permissions
	 * resultValue[1] = name of missing or unneeded permission
	 * resultValue[2] = user-name
	 * resultValue[3] = ID of activity
	 * 
	 * @author josef
	 *
	 * To change the template for this generated type comment go to
	 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
	 */
	public String[] doTest(HashMap akt, HashMap perm)
	{
		boolean fehler, ersterDurchlaufFertig;
		fehler = ersterDurchlaufFertig = false;

		String ergebnis = "";
		String[] resultValue = new String[4];
		resultValue[0] = "";
		resultValue[1] = "";
		resultValue[2] = "";
		resultValue[3] = "";
		
		// erster Test:
		Iterator act_it = akt.keySet().iterator();
		while(act_it.hasNext())
		{
			String a_key = (String)act_it.next();
			String a_value = (String)akt.get(a_key);
			String p_value = (String)perm.get(a_key);

			if(p_value == null)
			{
				p_value = "";
			}
			if (a_value != null && p_value != null)
			{
				ergebnis = checkAusreichendBerechtigungen2(a_value, p_value);
				if (!ergebnis.equals("noTHIng"))
				{
					// error found!
					fehler = true;
					resultValue[0] = "fehlt";
					resultValue[1] = ergebnis;
					int doppelPunktPosition = a_key.indexOf(":");
					resultValue[2] = a_key.substring(0, doppelPunktPosition);
					resultValue[3] = a_key.substring(doppelPunktPosition+1, a_key.length());
					break;
				}
				else
				{
					fehler = false;
				}
			}
			else
			{
				logger.error("One of the values is null");
			}
		}
		
		if(!fehler)
		{
			_mainOutput.writeLn("-----------------------------------------------------------------------------------------------");
			_mainOutput.writeLn("Unused permissions for the current business process:");
			_mainOutput.writeLn("-----------------------------------------------------------------------------------------------");
			
			// second test:
												
			Iterator perm_it2 = perm.keySet().iterator();
			while(perm_it2.hasNext())
			{
				String p_key = (String)perm_it2.next();
				String p_value = (String)perm.get(p_key);
				
				
				boolean eTest = false;
				Iterator akt_it3 = akt.keySet().iterator();
				while(akt_it3.hasNext())
				{
					String a_key = (String)akt_it3.next();
					String a_value = (String)akt.get(a_key);
					
					if (p_value.equals(a_value))
					{
						eTest = true;
						break;
					}
				}
				
				if(eTest == false)
				{
					_mainOutput.writeLn("The permission " + p_value + " is not used!");
				}
			}
			
		}
		
		return resultValue;
	}
		
		
	/**
	 * This method checks, if the string "perm" contains all permissions, required by the string "act".
	 * @param act
	 * @param perm
	 */
	private String checkAusreichendBerechtigungen2(String act, String perm)
	{
		String[] actArray;
		String[] permArray;
		boolean gefundenFlag;
		String returnString = "noTHIng";
		
		actArray  = makeArray(act);
		permArray = makeArray(perm);		
		
		for (int i = 0; i < actArray.length; i++)
		{
			gefundenFlag = false;
			
			for (int y = 0; y < permArray.length; y++)
			{
				
				//System.out.println("vergliechen wird: " + actArray[i] + " mit " + permArray[y]);
				if (actArray[i].equals(permArray[y]))
				{
					//System.out.println("gefunden: " + actArray[i]);
					gefundenFlag = true;
					break;
				}
			}
			if (!gefundenFlag)
			{
				_mainOutput.writeLn("The permission " + actArray[i] + " is missing!");
				returnString = actArray[i];
				break;
			}
		}
		
		return returnString;
	}
	
	
	private String checkZuvieleBerechtigungen2(String act, String perm)
	{
		String[] actArray;
		String[] permArray;
		boolean gefundenFlag;
		String returnString;
		returnString = "noTHIng";
		
		actArray  = makeArray(act);
		permArray = makeArray(perm);		
		
		for (int i = 0; i < permArray.length; i++)
		{
			gefundenFlag = false;
			
			for (int y = 0; y < actArray.length; y++)
			{
				if (permArray[i].equals(actArray[y]))
				{
					//System.out.println("gefunden: " + actArray[i]);
					gefundenFlag = true;
					break;
				}
			}
			if (!gefundenFlag)
			{
				_mainOutput.writeLn("The permission " + permArray[i] + " is not required!");
				returnString = permArray[i];
				break;
			}
		}
		
		return returnString;
	}	
	
	

/**
	* params[0] = Index for missing or unneeded permissions
	* params[1] = name of missing or unneeded permission
	* params[2] = user-name
	* params[3] = ID of the activity
*/ 
	public Vector askForRolle2(String[] params, UmlParser umlParser, Vector allUser, Vector alleMDRUser, HashMap roleHMap)
	{

		// Flags:
		boolean gefunden, verboten;
		gefunden = verboten = false;
		
		String rolles = "";
		String[] meineRollen;
		Vector rollenVorschlagsVector = new Vector();

		_mainOutput.writeLn("");
		_mainOutput.writeLn("User: " + params[2]);
		_mainOutput.writeLn("");
		_mainOutput.writeLn("The program has found out, that the permission " + params[1] + " is missing" + ".");
		
		if(params[0].equals("fehlt"))
		{
			_mainOutput.writeLn("Please select one of the proposed roleIDs for a correct placement of this " +
			"permission");
			_mainOutput.writeLn("Please execute the 'Correct Profile and check permissions' command to correct " +
			" the diagram.");
		}

		// define all roles of the users:
		NodeList nlist = umlParser.getDocument().getElementsByTagName("Benutzer");
		for (int i = 0; i < nlist.getLength(); i++)
		{
			Node n = nlist.item(i);
			NodeList childnodes = n.getChildNodes();
			for (int y = 0; y < childnodes.getLength(); y++)
			{
				Node node = childnodes.item(y);
				if (node.getNodeName().equals("name"))
				{
					NodeList nameNodeList = node.getChildNodes();
					Node tmpNameNode = nameNodeList.item(0);
					if(tmpNameNode.getNodeValue().equals(params[2]))
					{
						gefunden = true;
						rolles = getUserRolles(n);
					}
				}
				if (gefunden)
				{
					break;
				}
			}
			if(gefunden)
			{
				break;
			}
		} // End of for()
		
		if (rolles.equals(""))
		{
			//System.out.println("Rolles sind ein leerer String!");
		}
		else
		{
			// put single roles in an array:
			meineRollen = rolles.split(":");

			for(int i = 0; i < meineRollen.length; i++)
			{
				verboten = false;
				for(int n = 0; n < allUser.size(); n++)
				{
					DOMXMLUser tmpUser = ((DOMXMLUser)allUser.get(n));
					if(!tmpUser.getUserName().equals(params[2]))
					{			
						String[] tmpUserRolles = tmpUser.getUserRolle();
						// comparison of the roles:
						for (int y = 0; y < tmpUserRolles.length; y++)
						{
							if (tmpUserRolles[y].equals(meineRollen[i]))
							{
								// there is a overlap between roles
								// es gibt eine Ueberschneidung in den Rollen
								// It has to be checked, if the roles execute the same activity.
								// Es muss geprueft werden, ob auch die gleiche TA ausgefuehrt wird: (params[3] = oid)
								Vector tmpUserPermissions = tmpUser.getUserPermissions();
								for(int x = 0; x < tmpUserPermissions.size(); x++)
								{
									DOMXMLPermission tmpdomxmlpermission = ((DOMXMLPermission)tmpUserPermissions.get(x));
									if(tmpdomxmlpermission.getObjectID()==Integer.parseInt(params[3]))
									{
										// the other user executes the same activity 										
										verboten = false;
									}
									else
									{
										// the other user doesn't execute the considered activity, that role 
										// don't has to be offered 
										
										// By now it has to be seen, if there is another activity, where the considered roles 
										// of the two users overlap.
										// Es muss hier aber auch geschaut werden, ob es eine andere TA gibt,
										// in der sich die beiden User in der betrachteten Rolle ueberschneiden:
										
										boolean _gefunden;
										_gefunden = false;
										
										for(int nn = 0; nn < allUser.size(); nn++)
										{
											DOMXMLUser _tmpser = ((DOMXMLUser)allUser.get(nn));
											// sicher gehen, dass 'ich' betrachtet werde:
											if(_tmpser.getUserName().equals(params[2]))
											{
												// all my permissions:
												Vector _tmpAlleMeinePermissions = _tmpser.getUserPermissions();
												for(int mm = 0; mm < _tmpAlleMeinePermissions.size(); mm++)
												{
													// a single permission of me:
													DOMXMLPermission _eineMeinePermission = ((DOMXMLPermission)_tmpAlleMeinePermissions.get(mm));
													int _meineTMPOID = _eineMeinePermission.getObjectID();
												
													for(int ii = 0; ii < tmpUserPermissions.size(); ii++)
													{
														DOMXMLPermission _tmpUserPermission = ((DOMXMLPermission)tmpUserPermissions.get(ii));
														int _myTMPOID = _tmpUserPermission.getObjectID();
														// abfangen, dass nur die aktuelle Rolle betrachtet wird:
														if((_eineMeinePermission.getRoleID() == Integer.parseInt(meineRollen[i])) && (_tmpUserPermission.getRoleID() == Integer.parseInt(meineRollen[i])))
														{
															if(_meineTMPOID == _myTMPOID)
															{
																// it has to be checked, if both users execute this activity
																boolean testAufVorkommen = wirdTABenutzt(_myTMPOID, alleMDRUser, params[2], tmpUser.getUserName());
																if(testAufVorkommen)
																{
																	_gefunden = true;
																}
																else
																{
																	_gefunden = false;
																}
																break;
															}
														}
													}
													if(_gefunden)
													{
														break;
													}
												}
											}
											if(_gefunden)
											{
												break;
											}												
										}
		
										if(_gefunden)
										{
											verboten = false;
											break;						
										}
										else
										{
											verboten = true;
											break;						
										}

									}
								}// End of for(x)
							}
						
							if(verboten)
							{
								break;
							}
					
						}// End for(y)
				
						if (verboten)
						{
							break;
						}
					}// end of if(!tmpUser.getUserName().equals(params[2]))
				}// End for(n)
				if(!verboten)
				{
					rollenVorschlagsVector.add(meineRollen[i]);
				}
			
			}// End for(i)
		}
		
		// By now, the roles, that contain the considered permission, can be proposed to the user.
		Vector andereVorschlagsRollen = new Vector();
		// all users
		for(int i = 0; i < allUser.size(); i++)
		{
			DOMXMLUser _userTMP = (DOMXMLUser)allUser.get(i);
			if(!_userTMP.getUserName().equals(params[2]))
			{
				Vector _userTMPAllPermissions = _userTMP.getUserPermissions();
				// all permissions of the users
				for (int y = 0; y < _userTMPAllPermissions.size(); y++)
				{
					DOMXMLPermission _userTMPOnePermission = (DOMXMLPermission)_userTMPAllPermissions.get(y);
					if(_userTMPOnePermission.getPermissionName().equals(params[1]))
					{
						int _tmpRollID = _userTMPOnePermission.getRoleID();
						String _tmpRollID_String = Integer.toString(_tmpRollID);
						if(!isTheRolleInTheVector(andereVorschlagsRollen, _tmpRollID_String))
						{
							andereVorschlagsRollen.add(_tmpRollID_String);
						}
					}
				}
			}
		}
		
		_mainOutput.writeLn("Because of security relevant reasons the following roleIDs were proposed for");
		_mainOutput.writeLn("correct placement of the missing permission:");
		_mainOutput.writeLn(" - new roleID");
		
		for(int i = 0; i < rollenVorschlagsVector.size(); i++)
		{
			_mainOutput.writeLn(" - " + rollenVorschlagsVector.get(i)+":  " + roleHMap.get(rollenVorschlagsVector.get(i)));
		}
		if(andereVorschlagsRollen.size() > 0)
		{
			_mainOutput.writeLn("Furthermore the tool has found roles, that contain the considered permission, ");
			_mainOutput.writeLn("but are currently not assigned to the user: ");
			for(int i = 0; i < andereVorschlagsRollen.size(); i++)
			{
				_mainOutput.writeLn(" - " + andereVorschlagsRollen.get(i));
			}
		}
		_mainOutput.writeLn("Please execute the command 'Correct Profile and check permissions' ");
		_mainOutput.writeLn("and enter one of the proposed roleIDs into the appearing window, or ");
		_mainOutput.writeLn("enter a '*' followed by an integer, that will be the roleID of a new role.");
	
		return rollenVorschlagsVector;
	}


	private boolean isTheRolleInTheVector(Vector v, String s)
	{
		boolean returnValue;
		returnValue = false;
		if (v.size() > 0)
		{
			for(int n = 0; n < v.size(); n++)
			{
				String value = (String)v.get(n);
				if (value.equals(s))
				{
					returnValue = true;
					break;
				}
			}
		}
		
		return returnValue;
	}
	
	private String getUserRolles(Node n)
	{
		
		String rollen = "";
		
		NodeList _nodelist = n.getChildNodes();
		for(int i = 0; i < _nodelist.getLength(); i++)
		{
			Node _node = _nodelist.item(i);
			if(_node.getNodeName().equals("rid"))
			{
				NodeList rolleNodeList = _node.getChildNodes();
				Node tmpRolleNode = rolleNodeList.item(0);
				
				rollen = tmpRolleNode.getNodeValue();
			}
		}
		
		return rollen;
	}
	
	/**
	 * This method corrects the XML-document corresponding to the users needs.
	 * params[0] = index for missing or unneeded permissions
	 * params[1] = name of the missing or unneeded permission
	 * params[2] = user-name
	 * params[3] = ID of the activity
	 * @param rollID  ID of the role, that should be eypanded by the permission
	 */
	
	public void correctXMLDocument2(UmlParser parser, String[] params, String rollID, Vector alleBenutzer, HashMap protHMap)
	{
		// Flags:
		boolean userIstDa, gefundenOID;
		userIstDa = gefundenOID = false;
		
		String rid = "";
				
		if(rollID.startsWith("*"))
		{
			// create new role:
			rid = rollID.substring(1, rollID.length());
		}
		else
		{
			// expand a role:
			rid = rollID;
		}
		
		// is the input correct? the _rolleID of the role should be a number or *number!
		int ridInt;
				
		try
		{
			ridInt = Integer.parseInt(rid);
		}
		catch(NumberFormatException nfe)
		{
			_mainOutput.writeLn("");
			_mainOutput.writeLn("-------------------------------------------------------------------- ");
			_mainOutput.writeLn("wrong input value - it has to be an integer or a '*' followed by an integer");
			_mainOutput.writeLn("-------------------------------------------------------------------- ");
			_mainOutput.writeLn("");
			return;
		}

		// --------------------------------------
		// The program tests, if the user already exists. If not, he should be added
		for(int i = 0; i < alleBenutzer.size(); i++)
		{
			DOMXMLUser tmpuser = ((DOMXMLUser)alleBenutzer.get(i));
			if (tmpuser.getUserName().equals(params[2]))
			{
				userIstDa = true;
				//System.out.println("User " + params[2] + " bereits vorhanden.");
				break;
			}
		}
		
		if(!userIstDa)
		{
			// create user:
			parser.addBenutzer(params[2], rid, "000");

		}
		
		// The program tests, if the object already exists. If not, it should be added
		NodeList objektNodeList = parser.getDocument().getElementsByTagName("Objekt");
		for(int i = 0; i < objektNodeList.getLength(); i++)
		{
			Node tmpNode = objektNodeList.item(i);
			NodeList tmpchildnodes = tmpNode.getChildNodes();
			for(int y = 0; y < tmpchildnodes.getLength(); y++)
			{
				Node tmpChildNode = tmpchildnodes.item(y);
				if (tmpChildNode.getNodeName().equals("oid"))
				{
					NodeList tmpChildNodeNodeList = tmpChildNode.getChildNodes();
					Node childOftmpChildNode = tmpChildNodeNodeList.item(0);
					if(childOftmpChildNode.getNodeValue().equals(params[3]))
					{
						gefundenOID = true;
					}					
				}
			
				if (gefundenOID)
				{
					break;
				}
			}
		}
		
		if(!gefundenOID)
		{
			// create activity:
			parser.addActivity(params[3], params[3]);
		}		
		
		// create permission:
			String permIDString = ((ProtectedClass)protHMap.get(params[1])).getProtID();
			
			// Does the permission already exist?
			boolean berechtigungVorhanden;
			berechtigungVorhanden = false;
			
			NodeList alleBerechtigungnen = parser.getDocument().getElementsByTagName("Berechtigung");
			for(int i = 0; i < alleBerechtigungnen.getLength(); i++)
			{
				Node tmpNode = alleBerechtigungnen.item(i);			// a permission node
				NodeList _childsList = tmpNode.getChildNodes();		// all inner nodes
				for(int y = 0; y < _childsList.getLength(); y++)
				{
					Node _tmp = _childsList.item(y);			// an inner node
					if(_tmp.getNodeName().equals("name"))
					{
						NodeList nameList = _tmp.getChildNodes();
						Node nameNode = nameList.item(0);
						
						if (nameNode.getNodeValue().equals(params[1]))
						{
							berechtigungVorhanden = true;
						}
					}
				}
			}
			if(!berechtigungVorhanden)
			{
				parser.addPermission(rid, permIDString, params[1], params[3]);
			}
		

		boolean rolleBereitsVorhanden;
		rolleBereitsVorhanden = false;

		if (rollID.startsWith("*"))
		{
			// 1.case: there is a new role:
			// ------------------------------------
			
			//create role only if it doesn't already exist:
			NodeList alleObjekte = parser.getDocument().getElementsByTagName("Rolle");
			for(int i = 0; i < alleObjekte.getLength(); i++)
			{
				Node tmpNode = alleObjekte.item(i);			// a role node
				NodeList _childsList = tmpNode.getChildNodes();	// all inner nodes
				for(int y = 0; y < _childsList.getLength(); y++)
				{
					Node _tmp = _childsList.item(y);		// an inner node
					if(_tmp.getNodeName().equals("rid"))
					{
						NodeList oidsList = _tmp.getChildNodes();
						Node oidNode = oidsList.item(0);
						if (oidNode.getNodeValue().equals(rid))
						{
							rolleBereitsVorhanden = true;
						}
					}
				}
			}
			if (!rolleBereitsVorhanden)
			{
				parser.addRolle(rid, rid);
			}
			
			//
			// adjust the role-entry of the user:
			if(userIstDa)
			{
				parser.addRolleToUser(params[2], rid);			
			}
		}
		
	}	
	
	
	
	/**
	 * @param _id
	 * @param _alleMDRUser
	 * @param _name1
	 * @param _name2
	 * @return
	 */
	private boolean wirdTABenutzt(int _id, Vector _alleMDRUser, String _name1, String _name2)
	{
		boolean _boolUser1 = false;
		boolean _boolUser2 = false;
		
		for (int i = 0; i < _alleMDRUser.size(); i++)
		{
			MDRUser _tmpMDRUSER = (MDRUser)_alleMDRUser.get(i);
			if(_tmpMDRUSER.getUserName().equals(_name1))
			{
				Vector _allActivitiesOfThisMDRUser = _tmpMDRUSER.getMyActivities();
				for (int y = 0; y < _allActivitiesOfThisMDRUser.size(); y++)
				{
					MDRActivity _tmpMDRUSERActivity = (MDRActivity)_allActivitiesOfThisMDRUser.get(y);
					int _tmpMDRUSERActivityName = Integer.parseInt(_tmpMDRUSERActivity.getID());
					if (_tmpMDRUSERActivityName == _id)
					{
						_boolUser1 = true;
						break;		
					}
				}
			}
			
			if(_tmpMDRUSER.getUserName().equals(_name2))
			{
				Vector _allActivitiesOfThisMDRUser = _tmpMDRUSER.getMyActivities();
				for (int y = 0; y < _allActivitiesOfThisMDRUser.size(); y++)
				{
					MDRActivity _tmpMDRUSERActivity = (MDRActivity)_allActivitiesOfThisMDRUser.get(y);
					int _tmpMDRUSERActivityName = Integer.parseInt(_tmpMDRUSERActivity.getID());
					if (_tmpMDRUSERActivityName == _id)
					{
						_boolUser2 = true;
						break;		
					}
				}
			}
		}
		
		if (_boolUser1 && _boolUser2)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
		
}
