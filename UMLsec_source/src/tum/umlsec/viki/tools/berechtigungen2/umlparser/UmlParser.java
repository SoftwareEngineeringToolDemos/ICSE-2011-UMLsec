/*
 * Created on 22.07.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package tum.umlsec.viki.tools.berechtigungen2.umlparser;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.omg.uml.UmlPackage;
import org.omg.uml.behavioralelements.activitygraphs.ActivityGraph;
import org.omg.uml.behavioralelements.activitygraphs.ActivityGraphClass;
import org.omg.uml.behavioralelements.activitygraphs.ActivityGraphsPackage;
import org.omg.uml.behavioralelements.statemachines.Pseudostate;
import org.omg.uml.behavioralelements.statemachines.StateMachinesPackage;
import org.omg.uml.behavioralelements.statemachines.TransitionClass;
import org.omg.uml.behavioralelements.usecases.UseCasesPackage;
import org.omg.uml.foundation.core.CorePackage;
import org.omg.uml.foundation.core.TagDefinition;
import org.omg.uml.foundation.core.TaggedValue;
import org.omg.uml.foundation.core.TaggedValueClass;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.mdr.IMdrContainer;
import tum.umlsec.viki.tools.berechtigungen.mdrparser.MDRActivity;
import tum.umlsec.viki.tools.berechtigungen.mdrparser.MDRUser;
import tum.umlsec.viki.tools.berechtigungen.xmlparser.DOMXMLPermission;
import tum.umlsec.viki.tools.berechtigungen.xmlparser.DOMXMLUser;
import tum.umlsec.viki.tools.berechtigungen2.mdrparser.ProtectedClass;
import tum.umlsec.viki.tools.berechtigungen2.mdrparser.RoleClass;
import tum.umlsec.viki.tools.berechtigungen2.mdrparser.UserClass;

import com.sun.xml.tree.XmlDocument;


/**
 * @author josef
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

public class UmlParser 
{

	UmlPackage root;
	CorePackage corePackage;
	ActivityGraphsPackage activityPackage;
	UseCasesPackage usecasePackage;
	StateMachinesPackage stateMachines;
	TransitionClass transitionClasses;
	Pseudostate initialState;
	ActivityGraph actGraph;
	//XMLParser xmlparser;
	
	private static Logger logger;

	HashMap result;
	Vector allInitialisedUser;
	ITextOutput log;
	int modus;
	XmlDocument document;
	Vector allObjectIDs = new Vector();
	Vector _mdrUser = new Vector();
	Vector mdrVectoren = new Vector();
	
	public UmlParser(ITextOutput output)
	{
		log = output;
		logger=Logger.getLogger("berechtigungen2");
	}

	public XmlDocument getDocument()
	{
		return document;
	}
	
	public HashMap getHMap()
	{
		return (HashMap)mdrVectoren.get(0);
	}

	public Vector getAllInitialisedUsers()
	{
		return allInitialisedUser;
	}
	
	public void setVectors(Vector vec)
	{
		mdrVectoren = vec;
	}
	
	public void createXmlDocument()
	{
		try
		{
			document = new XmlDocument();
		
			Node root = document.createElement("information");
			document.appendChild(root);
		}
		catch(Exception ex)
		{
			logger.error("XMLCreate Error: " + ex.getClass() + " " + ex.getMessage());
		}
	}
	/**
	*This method creates the instance variable "document" through the inoformation, contained by the 
	*tagged values
	*/
	public void parseUmlModel(IMdrContainer _mdrWrapper)
	{
		// here you have to distinguish between modes:
		allInitialisedUser = new Vector();
		
		//extract all users from the UML-diagram
		root = _mdrWrapper.getUmlPackage();
		corePackage = root.getCore();
		Vector userIDs = new Vector();
		Vector roleIDs = new Vector();
		Vector uVector = new Vector();
		
		//create vectors out of the UML-diagram
		HashMap protectedHMap = (HashMap)mdrVectoren.get(0);
		HashMap roleHMap = new HashMap();
		HashMap userHMap = (HashMap)mdrVectoren.get(1);
		
		
		activityPackage = (ActivityGraphsPackage)root.getActivityGraphs();
		ActivityGraphClass activityClasses = activityPackage.getActivityGraph();
		
		TaggedValueClass taggedValueClass = (TaggedValueClass)corePackage.getTaggedValue();
	
		for (Iterator iter1 = taggedValueClass.refAllOfClass().iterator(); iter1.hasNext();) 
		{
			TaggedValue tagValue = (TaggedValue) iter1.next();
		
			//list all tagged values of the tagged type "right"
			if ((tagValue.getType().getName()).equals("right")) 
			{ 
				for (Iterator iter2 = (tagValue.getDataValue()).iterator(); iter2.hasNext();) 
				{
					String tagValue_right=(String)iter2.next();
					//print all the tagged values of tagged type "role"
					logger.trace("TaggedValue (Data) of right is:  "+tagValue_right);
				    
					if (tagValue_right!="")			
					{
						String[] right = makeArray(tagValue_right); 
						System.out.println("right[0]= " +right[0]+ "  right[1]= " +right[1]);		
						
						String role_id = "";
						String prot_id = "";
						//controls the input
						
						try
						{
							String rollenName = right[0];
							String berechtName = right[1];
					
							if (roleHMap.containsKey(rollenName))
							{
								RoleClass rolClass = (RoleClass)roleHMap.get(rollenName);
								role_id = rolClass.getRoleID();
							}
							else
							{
								RoleClass rolClass = new RoleClass(rollenName);
								roleHMap.put(rollenName, rolClass);
								role_id = rolClass.getRoleID();
							}
							
							if(protectedHMap.containsKey(berechtName))
							{
								ProtectedClass protClass = (ProtectedClass)protectedHMap.get(berechtName);
								prot_id = protClass.getProtID();
								//System.out.println("Prot.name: "+ berechtName + "  protID: "+ protClass.getProtID());
							}
							else
							{
								ProtectedClass newProt = new ProtectedClass(berechtName);
								prot_id = newProt.getProtID();
								//System.out.println("Prot.name: "+ berechtName + "  protID: "+ newProt.getProtID());
							}
						}
						catch(NumberFormatException nfe)
						{
							log.writeLn("");
							log.writeLn("-------------------------------------------------------------------- ");
							//log.writeLn("Falsche Eingabe! Das Eingabeformat fur das Tagged Value right ist folgendes: ");
							//log.writeLn("Berechtigungsname, bid, rid, uid");
							log.writeLn("Wrong input format! ");
							log.writeLn("-------------------------------------------------------------------- ");
							log.writeLn("");
							System.exit(0);
						}
						addPermission(role_id, prot_id ,right[1], prot_id);
					} //end if							      
				} //end for iter2
			} //end if right
									
			else if ((tagValue.getType().getName()).equals("protected"))
			{
				for (Iterator iter2 = (tagValue.getDataValue()).iterator(); iter2.hasNext();) 
				{
					String tagValue_protected=(String)iter2.next();
					String prot_id = "";
					
					if (tagValue_protected != null)			
					{
						try
						{
							ProtectedClass pClass = (ProtectedClass)protectedHMap.get(tagValue_protected);
							System.out.println("1:  " + tagValue_protected);
							prot_id = pClass.getProtID();
							addActivity(tagValue_protected, prot_id);							
						}
						catch(NumberFormatException nfe)
						{
							log.writeLn("");
							log.writeLn("-------------------------------------------------------------------- ");
							log.writeLn("Wrong intput format!");
							log.writeLn("-------------------------------------------------------------------- ");
							log.writeLn("");
							System.exit(0);
						}
						
						
						//print all the tagged values of tagged type "protected"
						//System.out.println("TaggedValue (Data) of protected is "+tagValue_protected +" id: "+ prot_id);
						
					} //end if							      
				} //end for iter2
				
			
				
			} //end if protected
							
			else if ((tagValue.getType().getName()).equals("role"))
			{
				for (Iterator iter2 = (tagValue.getDataValue()).iterator(); iter2.hasNext();) 
				{
					String tagValue_role=(String)iter2.next();
									    
					if (tagValue_role != "")			
					{
						//print all the tagged values of tagged type "role"
						//System.out.println("TaggedValue (Data) of role is "+tagValue_role);
						
						//role[0]= Benutzername
						//role[1]= Rollenname
						
						String userName = "";
						String roleName = "";
						String role_id = "";
						String user_id = "";
						
						String[] role = makeArray(tagValue_role); 
						
						try
						{
							userName = role[0];
							roleName = role[1];
							
							if (roleHMap.containsKey(roleName))
							{
								RoleClass rolClass = (RoleClass)roleHMap.get(roleName);
								role_id = rolClass.getRoleID();
							}
							else
							{
								RoleClass rolClass = new RoleClass(roleName);
								roleHMap.put(roleName, rolClass);
								role_id = rolClass.getRoleID();
							}
							
							UserClass uClass = (UserClass)userHMap.get(userName);
							user_id = uClass.getUserID();
						}
						catch(NumberFormatException nfe)
						{
							log.writeLn("");
							log.writeLn("-------------------------------------------------------------------- ");
							log.writeLn("Wrong intput format!");
							log.writeLn("-------------------------------------------------------------------- ");
							log.writeLn("");
							System.exit(0);
						}
						
						String Rollenname = roleName;
						String rid = role_id;
						String Benutzername = userName;
						String uid = user_id;
										
						if (!isInVector(rid, roleIDs))
						{
							roleIDs.add(rid);
							addRolle(Rollenname, rid);
						}
						
						String ids[] = new String[2];
						ids[0] = rid;
						ids[1] = uid;
						
						if (isInVector(uid, userIDs) && !isInVector(ids, uVector))
						{
							//provide the appropriate user with the new role
							Node n = document.getFirstChild();
							//System.out.println("neue Rolle:  "+ Rollenname);
							addXMLRolle(uid, rid, n);
							uVector.addElement(ids);
						}				
						if (!isInVector(uid, userIDs))
						{
							userIDs.add(uid);
							//System.out.println("neue Rolle:  "+ Rollenname+ "  id: "+rid );
							addBenutzer(Benutzername, rid, uid);
							uVector.addElement(ids);
						}
					} //end if							      
				} //end for iter2
			} //end if role
		} //end for iter1
		//} //end for it_act_I_2
	}
	
	
	private void addXMLRolle(String uid, String rid, Node n)
	{
		if (n == null)
		{
			logger.error("Node is null.");
			return;
		}
		
		if (n.getNodeName().equals("Benutzer"))
		{
			NodeList l = n.getChildNodes();
			for (int i = 0; i < l.getLength(); i++)
			{				
				Node k = l.item(i);
				Node kText = k.getChildNodes().item(0);
				
				if (k.getNodeName().equals("uid") && kText.getNodeValue().equals(uid))
				{
					addXMLRolle2(n, rid);
				}
			}
		} //end if 		
			
		NodeList nodes = n.getChildNodes();
		int anzElem = nodes.getLength();
		
		for(int i = 0; i < anzElem; i++)
		{
			Node r = nodes.item(i);
			if (r == null)
			{
				continue;
			}
			addXMLRolle(uid, rid, r);
		}
	}
	
	
	private void addXMLRolle2(Node n, String rid)
	{
		NodeList l = n.getChildNodes();
		for (int i = 0; i < l.getLength(); i++)
		{				
			Node k = l.item(i);
			Node kText = k.getChildNodes().item(0);
			
			if (k.getNodeName().equals("rid"))
			{
				String Rollen = kText.getNodeValue();
				//System.out.println("Rollen1: " +Rollen);
				Rollen = Rollen + ":" + rid;
				//System.out.println("Rollen2: " +Rollen);
				kText.setNodeValue(Rollen);
			}
		}
	}

	
	private boolean isInVector(String id, Vector lList)
	{
		if (!lList.contains(id))	
		{
			return false;
		}
		else 			
			return true;
		}
	
	private boolean isInVector(String[] id, Vector uList)
	{
		if (!uList.contains(id))	
		{
			return false;
		}
		else 			
			return true;
		}
	
	
	public HashMap parse()
	{
		// distinguish between modes:
		allInitialisedUser = new Vector();
		
		Node n = document.getFirstChild();
		doRecursive(n);
		
		//System.out.println("there are " + allInitialisedUser.size() + " User im System.");
		for (int i = 0; i < allInitialisedUser.size(); i++)
		{
			// assign the permissions to the users:
			//System.out.println("Starte die Zuordnung der Berechtigungen zu den einzelnen Usern!");
			((DOMXMLUser)(allInitialisedUser.get(i))).getAllPermission(document);
		}
						
		createPairs();
		return result;
	}
	
	
	public void writeUml()
	{
		Vector roleVector = new Vector();
		Vector userVector = new Vector();
		
		Vector rightVector = new Vector();
		Vector objectVector = new Vector();
		Vector rollenVector = new Vector();
		
		//import roles, permissions, objects and users from the document into the vectors.
		Node n = document.getFirstChild();
		rollenVector = buildRollenVector(roleVector, userVector);
		writeIntoModel(objectVector, rightVector, rollenVector);
	}
	
	public HashMap getRoleHashMap()
	{
		HashMap roleMap = new HashMap();
		Node n = document.getFirstChild();
		initializeRHashMap(n, roleMap);
	
		return roleMap;
	}
	
	private void initializeRHashMap(Node n, HashMap ro)
	{
		if (n == null)
		{
			logger.error("Node is null.");
			return;
		}
		
		else if (n.getNodeName().equals("Rolle"))
		{
			String role = getRole(n);
			//System.out.println(n.getNodeName()+": " + role );
			String[] help = makeArray2(role);
			ro.put(help[1], help[0]);
		}
		
		NodeList nodes = n.getChildNodes();
		int numElem = nodes.getLength();
		
		for(int i=0; i<numElem; i++)
		{
			Node p = nodes.item(i);
			if (p == null)
			{
				continue;
			}
			initializeRHashMap(p, ro );
		}
	}


	private void writeIntoModel(Vector prVector, Vector riVector, Vector roVector)
	{
		allInitialisedUser = new Vector();
		
		//extract all users of the UML-diagram
		corePackage = root.getCore();
		
		activityPackage = (ActivityGraphsPackage)root.getActivityGraphs();
		ActivityGraphClass activityClasses = activityPackage.getActivityGraph();
		
		TaggedValueClass taggedValueClass = (TaggedValueClass)corePackage.getTaggedValue();
	
		Vector vec = new Vector();
		TagDefinition right_Def;
		TagDefinition role_Def;
		TagDefinition protected_Def;
		boolean first_ri = false;
		boolean first_ro = false;
		boolean first_pr = false; 
		Vector helpVec = new Vector();
		
		for (Iterator itera = taggedValueClass.refAllOfClass().iterator(); itera.hasNext(); )
		{
			TaggedValue tValue = (TaggedValue) itera.next();
			TagDefinition def = (TagDefinition)tValue.getType();
			if((def.getName()).equals("right") && !first_ri) 
			{
				right_Def = def;
				first_ri = true;
				//System.out.println(right_Def.getName());
			}
			else if((def.getName()).equals("role") && !first_ro) 
			{
				role_Def = def;
				first_ro = true;
				//System.out.println(role_Def.getName());
				tValue.setName("Hallo");
				//System.out.println(tValue.getName());
			}
			else if((def.getName()).equals("protected") && !first_pr) 
			{
				protected_Def = def;
				first_pr = true;
				//System.out.println(protected_Def.getName());
			}
			else if(((def.getName()).equals("right") && first_ri) || ((def.getName()).equals("role") && first_ro) || ((def.getName()).equals("protected") && first_pr) )
			{
				helpVec.add(tValue);
			}
		}
		
		for (Iterator iterc = helpVec.iterator(); iterc.hasNext(); )
		{
			TaggedValue tagValue = (TaggedValue) iterc.next();
			tagValue.refDelete();
		}
		
		TaggedValue rightTag = null;
		TaggedValue protectedTag = null;
		TaggedValue roleTag = null;
		
		
		for (Iterator iterf = taggedValueClass.refAllOfClass().iterator(); iterf.hasNext(); )
		{
			TaggedValue tValue = (TaggedValue) iterf.next();
			TagDefinition def = (TagDefinition)tValue.getType();
			if((def.getName()).equals("protected")) 
			{
				protectedTag = tValue;
			}
			else if((def.getName()).equals("right")) 
			{
				rightTag = tValue;
			}
			else if((def.getName()).equals("role"))
			{
				roleTag = tValue;
			}				
		}
		
		for (Iterator prIt = prVector.iterator(); prIt.hasNext(); )
		{
			String elem = (String)prIt.next();
			TaggedValue tagV = taggedValueClass.createTaggedValue();
			tagV.setType((TagDefinition)protectedTag.getType());
			tagV.setName(elem);
		}
		
		for (Iterator riIt = riVector.iterator(); riIt.hasNext(); )
		{
			String elem = (String)riIt.next();
			TaggedValue tagV = taggedValueClass.createTaggedValue();
			tagV.setType((TagDefinition)rightTag.getType());
			tagV.setName(elem);
		}
		
		for (Iterator roIt = roVector.iterator(); roIt.hasNext(); )
		{
			String elem = (String)roIt.next();
			TaggedValue tagV = taggedValueClass.createTaggedValue();
			tagV.setType((TagDefinition)roleTag.getType());
			tagV.setName(elem);
		}	
		
		rightTag.refDelete();
		protectedTag.refDelete();
		roleTag.refDelete();
		
		for (Iterator iterf = taggedValueClass.refAllOfClass().iterator(); iterf.hasNext(); )
		{
			TaggedValue tValue = (TaggedValue) iterf.next();
			TagDefinition def = (TagDefinition)tValue.getType();
			//System.out.println("defi:  "+(tValue.getType()).getName()+ "Wert:  " + tValue.getName());
			
		}
	}
	
		
	private String getUser(Node n)
	{
		String result = "";
		NodeList nodes = n.getChildNodes();
		int numElem = nodes.getLength();
		
		for(int i=0; i<numElem; i++)
		{
			Node p = nodes.item(i);
			if (p == null)
			{
				continue;
			}
			//System.out.println(n.getNodeName());
			if (p.getNodeName().equals("name"))
			{
				String uName = p.getFirstChild().getNodeValue();
				result = uName;
			}
			if (p.getNodeName().equals("rid"))
			{
				String rid = p.getFirstChild().getNodeValue();
				//System.out.println("rid des behandelten User: " + rid);
				//ridFlag = true;
				String[] help = makeArray2(rid);
				String help2 = "";
				
				if(help.length > 1)
				{
					int g;
					for (g = 0; g < (help.length -1); g++)
					{
						help2 = help2 + help[g] + ","; 
					}
					rid = help2 + help[help.length - 1];
				}
				
				result = result+":"+rid;
			}
			if (p.getNodeName().equals("uid"))
			{
				String uid = p.getFirstChild().getNodeValue();
				//uidFlag = true;
				result = result+":"+uid;
			}
		}
	
		return result;
	}
	
	private String getRole(Node n)
	{
		String result = "";
		NodeList nodes = n.getChildNodes();
		int numElem = nodes.getLength();
		
		for(int i=0; i<numElem; i++)
		{
			Node p = nodes.item(i);
			if (p == null)
			{
				continue;
			}
			//System.out.println(n.getNodeName());
			if (p.getNodeName().equals("name"))
			{
				String uName = p.getFirstChild().getNodeValue();
				result = uName;
			}
			if (p.getNodeName().equals("rid"))
			{
				String rid = p.getFirstChild().getNodeValue();
				//System.out.println("rid des behandelten User: " + rid);
				//ridFlag = true;
				result = result+":"+rid;
			}
		}
	
		return result;
	}
	
	private String getObject(Node n)
	{
		String result = "";
		NodeList nodes = n.getChildNodes();
		int numElem = nodes.getLength();
		
		for(int i=0; i<numElem; i++)
		{
			Node p = nodes.item(i);
			if (p == null)
			{
				continue;
			}
			//System.out.println(n.getNodeName());
			if (p.getNodeName().equals("name"))
			{
				String oName = p.getFirstChild().getNodeValue();
				result = oName;
			}
			if (p.getNodeName().equals("oid"))
			{
				String oid = p.getFirstChild().getNodeValue();
				//System.out.println("rid des behandelten User: " + rid);
				//ridFlag = true;
				result = result+":"+oid;
			}
		}
	
		return result;
	}
	
	
	private String getRight(Node n)
	{
		String result = "";
		String result2 = "";
		NodeList nodes = n.getChildNodes();
		int numElem = nodes.getLength();
		
		for(int i=0; i<numElem; i++)
		{
			Node p = nodes.item(i);
			if (p == null)
			{
				continue;
			}
			//System.out.println(n.getNodeName());
			if (p.getNodeName().equals("rid"))
			{
				String rid = p.getFirstChild().getNodeValue();
				result = rid;
			}
			if (p.getNodeName().equals("bid"))
			{
				String bid = p.getFirstChild().getNodeValue();
				//System.out.println("rid des behandelten User: " + rid);
				//ridFlag = true;
				result = result+":"+bid;
			}
			if (p.getNodeName().equals("name"))
			{
				String name = p.getFirstChild().getNodeValue();
				//uidFlag = true;
				result = result+":"+name;
			}
			if (p.getNodeName().equals("oid"))
			{
				String oid = p.getFirstChild().getNodeValue();
				//System.out.println("rid des behandelten User: " + rid);
				//ridFlag = true;
				result = result+":"+oid;
			}
			
			String[] result1 = makeArray2(result);
			if (result1.length == 4)
			{	
				result2 = result1[2]+":"+result1[1]+":"+result1[0]+":"+result1[3];
			}
		}
	
		return result2;
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
			System.out.println("Node is null.");
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
					// create new XMLUser and put it into the Vector:
					allInitialisedUser.add(new DOMXMLUser(name,rid,uid));
				}
			}
		}
		
	}
	
	
	/**
	 * This method processes every user sequentially and builds pairs of the following form:
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
			
			// determin all objects, that should be considered
			Vector objectIDs = new Vector();
			for (int y = 0; y < _permissions.size(); y++)
			{
				int _objectID = ((DOMXMLPermission)(_permissions.get(y))).getObjectID();
				if(!objectExistsOlready(_objectID, objectIDs))
				{
					objectIDs.add(new Integer(_objectID));
				}
			}
			
			// run through all objects an build pairs
			for (int y = 0; y < objectIDs.size(); y++)
			{
				//System.out.println("behandelt wird: " + objectIDs.get(y).toString());
				String prefix = _name + ":" + objectIDs.get(y).toString();
				System.out.println("prefix: " + prefix);
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
				System.out.println("suffix: " + suffix);
				String endString = prefix + "=" + suffix;
				//System.out.println(endString);
			
				result.put(prefix, suffix);	
			}
			
		}
	}

	
	//add activity
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
		
		// add node:
		Node info = information.item(0);
		info.appendChild(newActivity);
	}

	// add permission
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
		
		// add node:
		Node info = information.item(0);
		info.appendChild(newPermission);
	}
	
	// delete permission
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

	
	// add role
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
		
		// add node:
		Node info = information.item(0);
		info.appendChild(newRolle);
	}
	
	// add user
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
		
		// add node:
		Node info = information.item(0);
		info.appendChild(newBenutzer);
	}
	
	
	/**
	 * This method creates a new XML-document. It contains only one XML-element: "information"
	 */
	public void createXmlDocument(Vector alleMDRUser)
	{
		// set modus:
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
			System.out.println("XMLCreate Error: " + ex.getClass() + " " + ex.getMessage());
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
	
	private XmlDocument buildNewDocument(XmlDocument xDoc)
	{
		XmlDocument resultDoc = new XmlDocument();
		HashMap ro = new HashMap();
		Vector ri = new Vector();
		Vector ob = new Vector();
		Vector us = new Vector();
		Vector roleVector = new Vector();
		Vector rightVector = new Vector();
		
		
		
		try
		{
			resultDoc = new XmlDocument();
			Node root = resultDoc.createElement("information");
			resultDoc.appendChild(root);
		}
		catch(Exception ex)
		{
			logger.fatal("XMLCreate Error: " + ex.getClass() + " " + ex.getMessage());
		}
		Node n = xDoc.getFirstChild();
		
		initializeVectors(n, ro, ri, ob, us);
		buildRoleVector(roleVector, ro, us);
		buildRightVector(rightVector, ro, ri);
				
		writeToXmlDocument(resultDoc, ob, rightVector, roleVector );
		
		return resultDoc;
	}
	
	private void writeToXmlDocument(XmlDocument xDoc, Vector protV, Vector rightV, Vector roleV)
	{
		for (Iterator it1 = protV.iterator(); it1.hasNext(); )
		{
			String prot = (String)it1.next();
			String[] help = makeArray2(prot);
			//System.out.println("protected: "+help[0]);
			addProtected(xDoc, help[0]);
		}
		
		for (Iterator it2 = rightV.iterator(); it2.hasNext(); )
		{
			String right = (String)it2.next();
			String[] help = makeArray2(right);
			//System.out.println("right: "+help[0]+" "+help[1]);
			addRight(xDoc, help[0], help[1]);
		}
		
		for (Iterator it3 = roleV.iterator(); it3.hasNext(); )
		{
			String role = (String)it3.next();
			String[] help = makeArray2(role);
			//System.out.println("role: "+help[0]+" "+help[1]);
			addRole(xDoc, help[0], help[1]);
		}
	}
	
	private void addProtected(XmlDocument doc, String protName)
	{
		Element newProtected = doc.createElement("protected");
		Element myName = doc.createElement("activityname");
		
		myName.appendChild(doc.createTextNode(protName));
		newProtected.appendChild(myName);
						
		NodeList information = doc.getElementsByTagName("information");
				
		// anhaengen:
		Node info = information.item(0);
		info.appendChild(newProtected);
	}
	
	private void addRight(XmlDocument doc, String roleName, String rightName)
	{
		Element newRight = doc.createElement("right");
		Element roName = doc.createElement("rolename");
		Element riName = doc.createElement("rightname");
		
		
		roName.appendChild(doc.createTextNode(roleName));
		riName.appendChild(doc.createTextNode(rightName));
		
		newRight.appendChild(roName);
		newRight.appendChild(riName);
		
		NodeList information = doc.getElementsByTagName("information");
		
		// add node:
		Node info = information.item(0);
		info.appendChild(newRight);
	}
	
	private void addRole(XmlDocument doc, String userName, String roleName)
	{
		Element newRole = doc.createElement("role");
		Element usName = doc.createElement("actorname");
		Element roName = doc.createElement("rolename");
		
		
		usName.appendChild(doc.createTextNode(userName));
		roName.appendChild(doc.createTextNode(roleName));
		
		newRole.appendChild(usName);
		newRole.appendChild(roName);
		
		NodeList information = doc.getElementsByTagName("information");
		
		// add node:
		Node info = information.item(0);
		info.appendChild(newRole);
	}
	
	
	
	private void buildRoleVector(Vector roleV, HashMap roHMap, Vector usV)
	{
		for(Iterator it1 = usV.iterator(); it1.hasNext(); )
		{
			String user = (String)it1.next();
			String[] help = makeArray2(user);
			String[] help1 = makeArray(help[1]);
			String result = "";
			
			for(int i = 0; i < help1.length; i++)
			{
				String roleName = (String)roHMap.get(help1[i]);
				result = help[0] +":"+ roleName;
				roleV.add(result);
			}
		}
	}
	
	private void buildRightVector(Vector rightV, HashMap roHMap, Vector riV)
	{
		String result = "";
		for (Iterator it1 = riV.iterator(); it1.hasNext(); )
		{
			String right = (String)it1.next();
			String[] help = makeArray2(right);
			
			result = (String)roHMap.get(help[2]);
			result = result +":"+ help[0];
			
			if(result != "")
			{
				rightV.add(result);
			}
		}
	}
	
	
	public void writeXMLFile(String adresse)
	{
		
		try
		{
			FileOutputStream fos = new FileOutputStream(new File(adresse));
			XmlDocument doc = buildNewDocument(document);
			doc.write(fos);
			File _tmpFile = new File(adresse);
			if(_tmpFile.exists())
			{
				//log.writeLn("Das schreiben der XML-Datei war erfolgreich.");
				log.writeLn("The generation of the XML file has been successful ");
			}
		}
		catch(Exception ee)
		{
			System.out.println(ee);
		}
		
	}
	
	
	
	private Vector buildRollenVector(Vector roleVector, Vector userVector)
	{
		Vector rollenVector = new Vector();
		Vector resultVector = new Vector();
		
		Iterator uIter = userVector.iterator();
		for ( ;uIter.hasNext(); )
		{
			String elem = (String)uIter.next();
			String[] elArray = makeArray2(elem); 
			
			String[] rollenIds = makeArray(elArray[1]);
			for (int i = 0; i < rollenIds.length; i++)
			{
				String rollenEintrag = rollenIds[i] +","+ elArray[0] +","+ elArray[2];
				rollenVector.add(rollenEintrag);
			}		
		}
		
		Iterator rollenIter = rollenVector.iterator();
		for ( ;rollenIter.hasNext(); )
		{	
			String el = (String)rollenIter.next();
			String[] elems = makeArray(el);
			String rid = elems[0];
			
			Iterator rIter = roleVector.iterator();
			for ( ;rIter.hasNext(); )
			{
				String rElem = (String)rIter.next();
				String[] rElems = makeArray2(rElem);
				if (rElems[1].equals(rid))
				{
					String result = rElems[0] +","+ el;
					resultVector.add(result);
				}
			}
			
		}
		
		Iterator it = resultVector.iterator();
		for (;it.hasNext();)
		{
			System.out.println((String)it.next());
		}
		
		return resultVector;
	}
	
	
	
	private void initializeVectors(Node n, HashMap ro, Vector ri, Vector ob, Vector us)
	{
		if (n == null)
		{
			logger.error("Node is null.");
			return;
		}
		
		else if (n.getNodeName().equals("Benutzer"))
		{
			//System.out.println(p.getNodeName());
			String user = getUser(n);
			us.add(user);
		}
		
		else if (n.getNodeName().equals("Rolle"))
		{
			//System.out.println(p.getNodeName());
			String role = getRole(n);
			String[] help = makeArray2(role);
			ro.put(help[1], help[0]);
		}
		
		else if (n.getNodeName().equals("Objekt"))
		{
			//System.out.println(p.getNodeName());
			String object = getObject(n);
			ob.add(object);
		}
		
		else if (n.getNodeName().equals("Berechtigung"))
		{
			//System.out.println(p.getNodeName());
			String right = getRight(n);
			ri.add(right);
		}
		
		NodeList nodes = n.getChildNodes();
		int numElem = nodes.getLength();
		
		for(int i=0; i<numElem; i++)
		{
			Node p = nodes.item(i);
			if (p == null)
			{
				continue;
			}
			initializeVectors(p, ro, ri, ob, us);
		}
	}
	

	
	/**
	 * This method adds a new role to an user of the assigned XML-document.
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
		
		// correct the roles:
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
	 * This method creates nodes in the XML-document for every user and activity
	 * @param _users
	 * @param _activities
	 */
	public void addNodes(Vector _mdrusers, Vector _mdractivities)
	{
		for(int i = 0; i < _mdrusers.size(); i++)
		{
			MDRUser _tmpUser = (MDRUser)_mdrusers.get(i);
			addBenutzer(_tmpUser.getUserName(), "", Integer.toString(_tmpUser.getUserID()));
		}
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
	
	
	/**
	 * This method divedes a string, that contains words divided by "," and creates an array of strings.
	 * @param myString
	 * @return
	 */
	private String[] makeArray(String myString)
	{
		String[] returnArray;
		
		if (myString.lastIndexOf(",")==-1)
		{
			returnArray = new String[1];
			returnArray[0] = myString;
		}
		else
		{
			returnArray = myString.split(",");
		}
		
		return returnArray;
		
	}
	
	private String[] makeArray2(String myString)
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
	
}
