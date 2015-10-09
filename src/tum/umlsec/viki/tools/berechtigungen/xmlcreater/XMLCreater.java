package tum.umlsec.viki.tools.berechtigungen.xmlcreater;

import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.omg.uml.UmlPackage;
import org.omg.uml.behavioralelements.activitygraphs.ActivityGraph;
import org.omg.uml.behavioralelements.activitygraphs.ActivityGraphClass;
import org.omg.uml.behavioralelements.activitygraphs.ActivityGraphsPackage;
import org.omg.uml.behavioralelements.statemachines.Pseudostate;
import org.omg.uml.behavioralelements.statemachines.PseudostateClass;
import org.omg.uml.behavioralelements.statemachines.StateMachinesPackage;
import org.omg.uml.behavioralelements.statemachines.StateVertex;
import org.omg.uml.behavioralelements.statemachines.Transition;
import org.omg.uml.behavioralelements.statemachines.TransitionClass;
import org.omg.uml.foundation.core.CorePackage;
import org.omg.uml.foundation.core.TaggedValue;

import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.mdr.IMdrContainer;

public class XMLCreater 
{
	UmlPackage root;
	CorePackage corePackage;
	ActivityGraphsPackage activityPackage;
	StateMachinesPackage stateMachines;
	TransitionClass transitionClasses;
	Pseudostate initialState;
	ActivityGraph actGraph;
	
	Vector allNamesOfActivities = new Vector();
	Vector allActivities = new Vector();
	Vector allUsers = new Vector();
	Vector allPermissions = new Vector();
	
	ITextOutput log;
	
	private static Logger logger;
	
	public XMLCreater(ITextOutput output)
	{
		log = output;
		logger=Logger.getLogger("berechtigungen");
	}
	
	public void createXMLPermissions(IMdrContainer _mdrWrapper)
	{
		root = _mdrWrapper.getUmlPackage();
		corePackage = root.getCore();
		activityPackage = (ActivityGraphsPackage)root.getActivityGraphs();
		stateMachines = (StateMachinesPackage)activityPackage.getStateMachines();
		transitionClasses = (TransitionClass)stateMachines.getTransition();

		ActivityGraphClass activityClasses = activityPackage.getActivityGraph();
		PseudostateClass pseudostateClasses = (PseudostateClass)stateMachines.getPseudostate();
		
		initialState = null;
		for (Iterator it_act_I = pseudostateClasses.refAllOfClass().iterator(); it_act_I.hasNext();) 
		{
			initialState = (Pseudostate)it_act_I.next();
		}
		
		for (Iterator it_act_I_2 = activityClasses.refAllOfClass().iterator(); it_act_I_2.hasNext();) 
		{
			actGraph = (ActivityGraph)it_act_I_2.next();
			// von allen Transitionen die Aktivitaeten anschauen:
			for (Iterator it_act_I_3 = actGraph.getTransitions().iterator(); it_act_I_3.hasNext();)
			{
				String _userName;
				_userName = "";
				int userID;
				userID = -1;
				int actID;
				actID = -1;
				String _permissions;
				_permissions = "";
				boolean idFlag, userFlag, permissionFlag;
				idFlag = userFlag = permissionFlag = false;
								
				Transition tmp_transition = (Transition)it_act_I_3.next();
				StateVertex tmp = (StateVertex)tmp_transition.getTarget();
				if (tmp.getClass().toString().equals("class org.omg.uml.behavioralelements.activitygraphs.ActionState$Impl"))
				{
					// test, ob die Aktivitaet nicht bereits behandelt wurde:
					if(!isNameinVector(allNamesOfActivities, tmp.getName()))
					{
						//Activity:
						String actName = tmp.getName().toString();
						log.writeLn("Name der behandelten Teilaktivitaet: " + actName);
						
						for (Iterator it_taggedValueIt = tmp.getTaggedValue().iterator(); it_taggedValueIt.hasNext();)
						{
							TaggedValue tmp_taggedValue = (TaggedValue)it_taggedValueIt.next();
							String tag = tmp_taggedValue.getType().getName();
							if (tag.equals("id"))						
							{						
								try 
								{
									for(Iterator it_tagVa_A = tmp_taggedValue.getDataValue().iterator(); it_tagVa_A.hasNext();)
									{
										String wert_hier = (String)it_tagVa_A.next();
										if (wert_hier!=null)
										{
											//System.out.println("do");
											actID=Integer.parseInt(wert_hier);
											idFlag = true;
											break;
										}
									}
								} 
								catch(Exception ep) 
								{
									logger.fatal("exception bei  find class 1 : "+ep.getMessage());
									logger.fatal(ep.toString());
								}
							}						
						} // end of for()
					
						if (idFlag)
						{
							log.writeLn("adding new Object");
							allActivities.add(new Activity(actName, actID));
						}
					
						//-------------------					
						// User:
					
						for (Iterator it_taggedValueIt = tmp.getTaggedValue().iterator(); it_taggedValueIt.hasNext();)
						{
							TaggedValue tmp_taggedValue = (TaggedValue)it_taggedValueIt.next();
							String tag = tmp_taggedValue.getType().getName();						
												
							if (tag.equals("user"))
							{
								try 
								{
									for(Iterator it_tagVa_A = (tmp_taggedValue.getDataValue()).iterator(); it_tagVa_A.hasNext();)
									{
										String tagValue_Da_A=(String)it_tagVa_A.next();
										if (tagValue_Da_A!=null)
										{
											_userName =  tagValue_Da_A;
											userFlag = true;
											break;
										}
									}
								} 
								catch(Exception ep) 
								{
									logger.fatal("exception bei  find class 2 : "+ep.getMessage());
								}
								
							}
														
							
						} // end of for()
					
						//testen, ob der User bereits vorliegt:
						//TODO: abfangen, falls "" als _userName kommt!
						int tmp_userID = existUser(allUsers,_userName);
						if (tmp_userID == -1)
						{
							// dieser User liegt noch nicht vor:
							userID = User.correntUser+1;
							if (userFlag)
							{
								log.writeLn("adding new User...");
								allUsers.add(new User(_userName, userID));
							}
						}
						else
						{
							// dieser User liegt bereits vor:
							userID = tmp_userID;
						}
						
						//-------------------
						//permissions:
						
						for (Iterator it_taggedValueIt = tmp.getTaggedValue().iterator(); it_taggedValueIt.hasNext();)
						{
							TaggedValue tmp_taggedValue = (TaggedValue)it_taggedValueIt.next();
							String tag = tmp_taggedValue.getType().getName();						
													
							if (tag.equals("permission"))
							{
								try 
								{
									for(Iterator it_tagVa_A = (tmp_taggedValue.getDataValue()).iterator(); it_tagVa_A.hasNext();)
									{
										String tagValue_Da_A=(String)it_tagVa_A.next();
										if (tagValue_Da_A!=null)
										{
											_permissions =  tagValue_Da_A;
											permissionFlag = true;
											break;
										}
									}
								} 
								catch(Exception ep) 
								{
									logger.fatal("exception bei  find class 3 : "+ep.getMessage());
								}
								
							}
							if(permissionFlag)
							{
								allPermissions.add(new Permission(_permissions, userID));
							}
							
						} // end of for
					}	
				}
				
			}
		}
		
		log.writeLn("XMLCreator ergebnisse:");
		log.writeLn("---");
		log.writeLn("All Activities: ");
		for(int i = 0; i < allActivities.size(); i++)
		{
			Activity tmp = (Activity)allActivities.get(i);
			log.writeLn("Name: " + tmp.getActivityName());
			log.writeLn("id: " + tmp.getActivityID());
		}
		
		log.writeLn("All permissions");
		for(int i = 0; i < allPermissions.size(); i++)
		{
			Permission tmp = (Permission)allPermissions.get(i);
			log.writeLn("Name: " + tmp.getPermissionName());
			log.writeLn("id: " + tmp.getPermissionID());
		}
		
		log.writeLn("All users");
		for(int i = 0; i < allUsers.size(); i++)
		{
			User tmp = (User)allUsers.get(i);
			log.writeLn("Name: " + tmp.getUserName());
			log.writeLn("id: " + tmp.getUserID());
		}
				
	} // End of createXMLPermissions()
	
	private Vector getAllUser(IMdrContainer _mdrWrapper)
	{
		// return value:
		Vector allUserNames = new Vector();
		
		root = _mdrWrapper.getUmlPackage();
		corePackage = root.getCore();
		activityPackage = (ActivityGraphsPackage)root.getActivityGraphs();
		stateMachines = (StateMachinesPackage)activityPackage.getStateMachines();
		transitionClasses = (TransitionClass)stateMachines.getTransition();

		ActivityGraphClass activityClasses = activityPackage.getActivityGraph();
		PseudostateClass pseudostateClasses = (PseudostateClass)stateMachines.getPseudostate();
		
		initialState = null;
		for (Iterator it_act_I = pseudostateClasses.refAllOfClass().iterator(); it_act_I.hasNext();) 
		{
			initialState = (Pseudostate)it_act_I.next();
		}
		
		for (Iterator it_act_I_2 = activityClasses.refAllOfClass().iterator(); it_act_I_2.hasNext();) 
		{
			actGraph = (ActivityGraph)it_act_I_2.next();
			for (Iterator it_act_I_3 = actGraph.getTransitions().iterator(); it_act_I_3.hasNext();)
			{
				Transition tmp_transition = (Transition)it_act_I_3.next();
				StateVertex tmp = (StateVertex)tmp_transition.getTarget();
				if (tmp.getClass().toString().equals("class org.omg.uml.behavioralelements.activitygraphs.ActionState$Impl"))
				{
					String _user;
					boolean _userFlag;
					_user = "";
					_userFlag = false;
					//System.out.println(tmp.getName().toString());
					for (Iterator it_taggedValueIt = tmp.getTaggedValue().iterator(); it_taggedValueIt.hasNext();)
					{
						TaggedValue tmp_taggedValue = (TaggedValue)it_taggedValueIt.next();
						String tag = tmp_taggedValue.getType().getName();						
												
						if (tag.equals("user"))
						{
							_userFlag = true;
							try 
							{
								for(Iterator it_tagVa_A = (tmp_taggedValue.getDataValue()).iterator(); it_tagVa_A.hasNext();)
								{
									String tagValue_Da_A=(String)it_tagVa_A.next();
									if (tagValue_Da_A!=null)
									{
										_user =  tagValue_Da_A;
									}
								}
							} 
							catch(Exception ep) 
							{
								logger.fatal("exception bei  find class : "+ep.getMessage());
							}
							
						}
													
						
					}
					
					if (_userFlag)
					{
						// in die HashMap ablegen:
						allUserNames.add(_user);
					}
				}
			}
		}
		
		
		return allUserNames;
	}
	
	private Vector getallTransactions(IMdrContainer _mdrWrapper)
	{
		// return value:
		Vector allTransactions = new Vector();
		
		root = _mdrWrapper.getUmlPackage();
		corePackage = root.getCore();
		activityPackage = (ActivityGraphsPackage)root.getActivityGraphs();
		stateMachines = (StateMachinesPackage)activityPackage.getStateMachines();
		transitionClasses = (TransitionClass)stateMachines.getTransition();

		ActivityGraphClass activityClasses = activityPackage.getActivityGraph();
		PseudostateClass pseudostateClasses = (PseudostateClass)stateMachines.getPseudostate();
		
		initialState = null;
		for (Iterator it_act_I = pseudostateClasses.refAllOfClass().iterator(); it_act_I.hasNext();) 
		{
			initialState = (Pseudostate)it_act_I.next();
		}
		
		for (Iterator it_act_I_2 = activityClasses.refAllOfClass().iterator(); it_act_I_2.hasNext();) 
		{
			actGraph = (ActivityGraph)it_act_I_2.next();
			for (Iterator it_act_I_3 = actGraph.getTransitions().iterator(); it_act_I_3.hasNext();)
			{
				Transition tmp_transition = (Transition)it_act_I_3.next();
				StateVertex tmp = (StateVertex)tmp_transition.getTarget();
				if (tmp.getClass().toString().equals("class org.omg.uml.behavioralelements.activitygraphs.ActionState$Impl"))
				{
					String _name;
					boolean _idFlag, _nameFlag;
					_name = "";
					_idFlag = _nameFlag = false;
					
					_name = tmp.getName().toString();
					for (Iterator it_taggedValueIt = tmp.getTaggedValue().iterator(); it_taggedValueIt.hasNext();)
					{
						TaggedValue tmp_taggedValue = (TaggedValue)it_taggedValueIt.next();
						String tag = tmp_taggedValue.getType().getName();						
						
						if (tag.equals("id"))
						{
							_idFlag = true;
						}		
						
					}
					
					if (_idFlag)
					{
						allTransactions.add(_name);
					}
				}
			}
		}
		
		return allTransactions;
	}
	
	private int existUser(Vector alleUser, String username)
	{
		
		for (int i = 0; i < alleUser.size(); i++)
		{
			User tmp = (User)alleUser.get(i);
			if (tmp.getUserName().equals(username))
			{
				return tmp.getUserID();
			}
		}
		
		return -1;
	}
	
	/**
	 * Die Funktion ueberprueft, ob die Aktivitaet bereits behandelt wurde,
	 * falls nein wird der Name der Aktivitaet dem Array hinzugefuegt.
	 * @param vec Vector mit allen bisher behandelten Aktivitaeten
	 * @param name Name der aktuell betrachteten Aktivitaet
	 * @return true, falls die Aktivitaet bereits behandelt wurde, false sonst.
	 */
	private boolean isNameinVector(Vector vec, String name)
	{
		
		boolean returnValue;
		
		returnValue = false;
		
		for (int i = 0; i < vec.size(); i++)
		{
			log.writeLn("vergliechen wird: " + vec.get(i).toString() + " mit " + name);
			if (((String)vec.get(i)).equals(name))
			{
				returnValue = true;
				//log.writeLn(name + " war schon drin.");
				break;
			}
		}
		// diser Name ist nicht im Vector:
		if (!returnValue)
		{
			log.writeLn("der Wert " + name + " wird dem Vector hinzugefuegt.");
			allNamesOfActivities.add(name);
		}
		return returnValue;
	}

} // End of class