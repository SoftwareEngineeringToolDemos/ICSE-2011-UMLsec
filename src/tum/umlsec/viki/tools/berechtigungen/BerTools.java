
package tum.umlsec.viki.tools.berechtigungen;

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
import tum.umlsec.viki.tools.berechtigungen.xmlparser.XMLDOMParser;

public class BerTools
{
	// Zaehler fuer die BID's:
	NumberGeneratorBID ngBID = new NumberGeneratorBID();
	
	ITextOutput _mainOutput;
	private static Logger logger;
	public BerTools(ITextOutput mainOutput)
	{
		_mainOutput = mainOutput;
	
	logger=Logger.getLogger("berechtigungen");
	}
	
	/**
	 * Diese Methode checkt, ob die Berechtigungen des Users fuer die Ausfuehrung aller
	 * seinen Teilaktivitaeten ausreichen.
	 * @param activity Hier steht, was der Anwender mindestens haben muss.
	 * @param permissions Hier steht, was der Anwender tatsaechlich hat.
	 */
	public void checkPermissions(HashMap activity, HashMap permissions)
	{
		// der Reihe nach einzelne activity-Inhalte abarbeiten
		// und fuer jede einzelne Teilaktivitaet die Testmethode anwerfen:
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
				System.out.println("One of the values is null");
			}
		}
	}
	
	/**
	 * Diese Methode checkt, ob im String perm alle permissions aus dem 
	 * String act vorhanden sind.
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
	 * Diese Methode checkt, ob in permissions-HashMap nicht zu viele Berechtigungen
	 * eingetragen sind, als es activity-HashMap fordert.
	 * @param activity
	 * @param permissions
	 */
	public void checktomuchPermissions(HashMap activity, HashMap permissions)
	{
		// der Reihe nach einzelne activity-Inhalte abarbeiten
		// und fuer jede einzelne Teilaktivitaet die Testmethode anwerfen:
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
				logger.error("One of the values is null");
				return;
			}
			
		}
	}
	
	/**
	 * Diese Methode checkt, ob im String perm nicht zu viele Berechtigungen 
	 * vorhanden sind, als es in act verlangt wird.  
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
					returnValue = true;
					break;
				}
			}
			if (!gefundenFlag)
			{
				_mainOutput.writeLn("The permission " + permArray[i] + " is not used!");
				returnValue = false;
				// falls was gefunden --> raus aus der Schleife!
				break;
			}
		}
		
		return returnValue;
	}	

	/**
	 * Hilfsfunktion, macht aus einem String, der eventuell durch : getrennte 
	 * Inhalte hat einen Array mit diesen Inhalten.
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
	 * Diese Methode fuehrt die beiden Tests der Reihe nach durch.
	 * Sobald ein Fehler gefunden wird, wird der Test abgebrochen und ein String-Array
	 * mit den noetigen Werten zurueck uebergeben.
	 * 
	 * resultValue[0] = Index fuer fehlende oder unnoetige Berechtigung.
	 * resultValue[1] = name der gefundenen Berechtigung, die entweder fehlt oder unnoetig erscheint
	 * resultValue[2] = Username
	 * resultValue[3] = ID der Teilaktivitaet.
	 * 
	 * @author Alter
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
					// fehler gefunden!
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
			
			// zweiter test:
			Iterator act_it2 = akt.keySet().iterator();
			while(act_it2.hasNext())
			{
				String a_key = (String)act_it2.next();
				String a_value = (String)akt.get(a_key);
			
				String p_value = (String)perm.get(a_key);
				if (a_value != null && p_value != null)
				{
					checkZuvieleBerechtigungen(a_value, p_value);
				}
				else
				{
					logger.error("One of the values is null");
				}
			}
			
		}
		
		return resultValue;
	}
		
		
		/**
	 * Diese Methode checkt, ob im String perm alle permissions aus dem 
	 * String act vorhanden sind.
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
	* params[0] = Index fuer fehlende oder unnoetige Berechtigung.
	* params[1] = name der gefundenen Berechtigung, die entweder fehlt oder unnoetig erscheint
	* params[2] = Username
	* params[3] = ID der Teilaktivitaet.
*/ 
	public Vector askForRolle2(String[] params, XMLDOMParser parser, Vector allUser, Vector alleMDRUser)
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
		_mainOutput.writeLn("The program has found out, that the permission " + params[1] + " is missing.");
		
		if(params[0].equals("fehlt"))
		{
			_mainOutput.writeLn("Please select one of the proposed roleIDs for a correct placement of this " +
			"permission");
			_mainOutput.writeLn("Please execute the 'Correct Profile and check permissions' command to correct " +
			" the diagram.");
		}

		// alle Rollen des Benutzers bestimmen:
		NodeList nlist = parser.getDocument().getElementsByTagName("Benutzer");
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
					//System.out.println("tmpNameNode.getNodeValue()= " + tmpNameNode.getNodeValue());
					if(tmpNameNode.getNodeValue().equals(params[2]))
					{
						//System.out.println("drin");
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
		

		//System.out.println("Alle Rollen des benutzers: " + rolles);
		if (rolles.equals(""))
		{
			//System.out.println("Rolles sind ein leerer String!");
		}
		else
		{
			// die einzelnen Rollen in den Array schieben:
			meineRollen = rolles.split(":");

			//System.out.println("Laenge von meineRollen: " + meineRollen.length);
			//System.out.println("Alle meine Rollen einzeln:");
			for(int ii = 0; ii < meineRollen.length; ii++)
			{
				//System.out.println(ii + ": " + meineRollen[ii]);
			}
			//System.out.println("--Auswahl der passenden Rollen--");
			//pro eigene Rolle (rid) alle andere User durchlaufen und deren Rollen bestimmen:
			for(int i = 0; i < meineRollen.length; i++)
			{
				verboten = false;
				//System.out.println("Abarbeitung der eigenen Rolle: " + meineRollen[i]);
				for(int n = 0; n < allUser.size(); n++)
				{
					DOMXMLUser tmpUser = ((DOMXMLUser)allUser.get(n));
					//System.out.println("Betrachtet wird ein anderer User: " + tmpUser.getUserName());
					
					// sicher gehen, dass nur andere User betrachtet werden:
					if(!tmpUser.getUserName().equals(params[2]))
					{			
						String[] tmpUserRolles = tmpUser.getUserRolle();
						// Vergleich der Rollen (rid) durchfuehren:
						for (int y = 0; y < tmpUserRolles.length; y++)
						{
							//System.out.println("ein RolleName des anderen Users: " + tmpUserRolles[y]);
							if (tmpUserRolles[y].equals(meineRollen[i]))
							{
								// es gibt eine Ueberschneidung in den Rollen
								// Es muss geprueft werden, ob auch die gleiche TA ausgefuehrt wird: (params[3] = oid)
								Vector tmpUserPermissions = tmpUser.getUserPermissions();
								for(int x = 0; x < tmpUserPermissions.size(); x++)
								{
									DOMXMLPermission tmpdomxmlpermission = ((DOMXMLPermission)tmpUserPermissions.get(x));
									if(tmpdomxmlpermission.getObjectID()==Integer.parseInt(params[3]))
									{
										// der andere User fuehrt die betrachtete Teilaktivitaet auch aus:
										//System.out.println("der andere User fuehrt die betrachtete TA auch aus: " + params[3]);
										verboten = false;
									}
									else
									{
										// der andere User fuehrt die betrachtete Teilaktivitaet NICHT aus:
										// diese Rolle darf nicht angeboten werden!
										//System.out.println("der andere User fuehrt die betrachtete TA NICHT aus: " + params[3]);
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
												// alle meine Permissions:
												Vector _tmpAlleMeinePermissions = _tmpser.getUserPermissions();
												for(int mm = 0; mm < _tmpAlleMeinePermissions.size(); mm++)
												{
													// eine einzelne Permission von mir:
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
																//System.out.println("Es gibt aber ueber die Rolle eine gemeinsame TA: " + _myTMPOID);
																// es muss untersucht werden, ob diese TA von den beiden User
																// im GP auch tatsaechlich ausgefuehrt wird.
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
				//System.out.println("verboten = " + verboten);
				if(!verboten)
				{
					//System.out.println("die meine Rolle " + meineRollen[i] + " wird erlaubt...");
					rollenVorschlagsVector.add(meineRollen[i]);
				}
			
			}// End for(i)
		}
		
		// an dieser Stelle sollen die Rollen vorgeschlagen werden, die die betrachtete
		// Berechtigung enthalten. Die Rollen aber dem User nicht zugeordnet sind:
		Vector andereVorschlagsRollen = new Vector();
		// alle User
		for(int i = 0; i < allUser.size(); i++)
		{
			DOMXMLUser _userTMP = (DOMXMLUser)allUser.get(i);
			if(!_userTMP.getUserName().equals(params[2]))
			{
				Vector _userTMPAllPermissions = _userTMP.getUserPermissions();
				// alle Berechtigungen des Users
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
		
		// ausgeben von Rollen vorschlaegen:
		_mainOutput.writeLn("Because of security relevant reasons the following roleIDs were proposed for");
		_mainOutput.writeLn("correct placement of the missing permission:");
		_mainOutput.writeLn(" - new roleID");
		for(int i = 0; i < rollenVorschlagsVector.size(); i++)
		{
			_mainOutput.writeLn(" - " + rollenVorschlagsVector.get(i));
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
	 * Diese Methode korrigiert das XMLDocument den Wuenschen des Anwenders entsprechend
	 * params[0] = Index fuer fehlende oder unnoetige Berechtigung.
	 * params[1] = name der gefundenen Berechtigung, die entweder fehlt oder unnoetig erscheint
	 * params[2] = Username
	 * params[3] = ID der Teilaktivitaet.
	 * @param rollID  Die ID der Rollen, der die Berechtigung hinzugefuegt werden soll.
	 */
	
	public void correctXMLDocument2(XMLDOMParser parser, String[] params, String rollID, Vector alleBenutzer)
	{
		// Flags:
		boolean userIstDa, gefundenOID;
		userIstDa = gefundenOID = false;
		
		String rid = "";
				
		if(rollID.startsWith("*"))
		{
			// neue Rolle soll erstellt werden:
			rid = rollID.substring(1, rollID.length());
		}
		else
		{
			// alte Rolle soll erweitert werden:
			rid = rollID;
		}
		
		// Test auf richtige Eingabe (_rolleID) der RollenID (soll eine Zahl oder *Zahl sein!)
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

		//System.out.println("Uebergeben an correctXMLDocument wurde: " + rollID);
		//System.out.println("Das bereinigte rid ist: " + rid);
		
		// Ablauf abhaengig von Gegebenheiten:
		// --------------------------------------
		// Testsen, ob der User bereits vorhanden ist, falls nein hinzufuegen:
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
			// User erzeugen:
			//System.out.println("ein neuer User wird angelegt:");
			parser.addBenutzer(params[2], rid, "000");

		}
		
		// Testen, ob das Objekt(Teilaktivitaet) bereits vorhanden ist, falls nein hinzufuegen:
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
			// Teilaktivitaet anlegen:
			//mydocument = xmlmanager.addActivity(params[3], params[3], mydocument);
			//System.out.println("neue TA wird angelegt.");
			parser.addActivity(params[3], params[3]);
		}		
		
		// Berechtigung anlegen:
			//mydocument = xmlmanager.addPermission(rollID, "123", params[1], params[3], mydocument);
//			int permID = DOMXMLPermission.getPermissionID() + 1;
			int permID = NumberGeneratorBID.getNextBID();
			String permIDString = Integer.toString(permID);
			
			// Es muss abgefangen werden, ob diese Berechtigung bereits existiert:
			
			boolean berechtigungVorhanden;
			berechtigungVorhanden = false;
			
			NodeList alleBerechtigungnen = parser.getDocument().getElementsByTagName("Berechtigung");
			for(int i = 0; i < alleBerechtigungnen.getLength(); i++)
			{
				Node tmpNode = alleBerechtigungnen.item(i);			// ein Berechtigung-Knoten
				NodeList _childsList = tmpNode.getChildNodes();		// alle inneren Knoten
				for(int y = 0; y < _childsList.getLength(); y++)
				{
					Node _tmp = _childsList.item(y);			// ein innerer Knoten
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
			// 1.Fall: Eine neue Rolle kommt hinzu:
			// ------------------------------------
			
			// Rolle anlegen, nur wenn sie noch nicht vorhanden ist:			
			// Einschub
			//System.out.println("Uebersicht ueber alle Rollen:");
			//System.out.println("-----------------------------");
			NodeList alleObjekte = parser.getDocument().getElementsByTagName("Rolle");
			for(int i = 0; i < alleObjekte.getLength(); i++)
			{
				Node tmpNode = alleObjekte.item(i);			// ein Rolle-Knoten
				NodeList _childsList = tmpNode.getChildNodes();	// alle inneren Knoten
				for(int y = 0; y < _childsList.getLength(); y++)
				{
					Node _tmp = _childsList.item(y);		// ein innerer Knoten
					//System.out.println(_tmp.getNodeName() + ": " + _tmp.getNodeValue());
					if(_tmp.getNodeName().equals("rid"))
					{
						NodeList oidsList = _tmp.getChildNodes();
						Node oidNode = oidsList.item(0);
						//System.out.println("Wert: " + oidNode.getNodeValue());
						if (oidNode.getNodeValue().equals(rid))
						{
							rolleBereitsVorhanden = true;
						}
					}
				}
			}
			// Einschub - Ende
			if (!rolleBereitsVorhanden)
			{
				parser.addRolle(rid, rid);
			}
			
			// den RollenEintrag beim Benutzer anpassen:
			if(userIstDa)
			{
				parser.addRolleToUser(params[2], rid);			
			}
		}
		
	}	
	
	
	
	/**
	 * Diese Funktion ueberprueft, ob eine TA mit der id _id von beiden Usern (_name1 und _name2) im GT ausgefuehrt wird.
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
	
} // End of class
