
package tum.umlsec.viki.tools.berechtigungen.mdrparser;

import java.util.HashMap;
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
import org.omg.uml.behavioralelements.usecases.UseCasesPackage;
import org.omg.uml.foundation.core.CorePackage;
import org.omg.uml.foundation.core.TaggedValue;

import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.mdr.IMdrContainer;
import tum.umlsec.viki.tools.berechtigungen.xmlparser.XMLParser;

public class MDRParser
{
	UmlPackage root;
	CorePackage corePackage;
	ActivityGraphsPackage activityPackage;
	UseCasesPackage usecasePackage;
	StateMachinesPackage stateMachines;
	TransitionClass transitionClasses;
	Pseudostate initialState;
	ActivityGraph actGraph;
	XMLParser xmlparser;
	
	private Vector allNamesOfActivities;
	Vector allUser = new Vector();
	Vector alleAktivitaeten = new Vector();
	
	String oname = "";
	
	ITextOutput log;
	
	HashMap result = new HashMap();
	
	private static Logger logger;
	
	public MDRParser(ITextOutput output)
	{
		log = output;
		logger=Logger.getLogger("berechtigungen");
	}

	public HashMap parseUMLDiagram(IMdrContainer _mdrWrapper)
	{
		allNamesOfActivities = new Vector();
		
		root = _mdrWrapper.getUmlPackage();
		corePackage = root.getCore();
		
		activityPackage = (ActivityGraphsPackage)root.getActivityGraphs();
		stateMachines = (StateMachinesPackage)activityPackage.getStateMachines();
		transitionClasses = (TransitionClass)stateMachines.getTransition();

/*
		usecasePackage = (UseCasesPackage)root.getUseCases();
		UseCaseClass uc_tmp = usecasePackage.getUseCase();
		log.writeLn("hier!");
		for (Iterator it_uc = uc_tmp.refAllOfClass().iterator(); it_uc.hasNext();) 
		{
			UseCase tmpusecase = (UseCase)it_uc.next();
			log.writeLn("!: " + tmpusecase.getName());
		}

*/

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
					String _id, _perm, _user;
					boolean _idFlag, _permFlag, _userFlag;
					_id = _perm = _user = "";
					_idFlag = _permFlag = _userFlag = false;
					
					// test, ob diese Aktivitaet bereits behandelt wurde:
					if(!isNameinVector(allNamesOfActivities, tmp.getName()))
					{
						oname = tmp.getName().toString();
						//log.writeLn(tmp.getName().toString());
						for (Iterator it_taggedValueIt = tmp.getTaggedValue().iterator(); it_taggedValueIt.hasNext();)
						{
							TaggedValue tmp_taggedValue = (TaggedValue)it_taggedValueIt.next();
							String tag = tmp_taggedValue.getType().getName();						
							
							if (tag.equals("permission"))
							{
								_permFlag = true;
								try 
								{
									for(Iterator it_tagVa_A = (tmp_taggedValue.getDataValue()).iterator(); it_tagVa_A.hasNext();)
									{
										String tagValue_Da_A=(String)it_tagVa_A.next();
										if (tagValue_Da_A!=null)
										{
											_perm =  tagValue_Da_A;
										}
									}
								} 
								catch(Exception ep) 
								{
									logger.fatal("exception bei  find class : "+ep.getMessage());
								}
								
							}
							
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
						
							if (tag.equals("id"))
							{
								_idFlag = true;
								try 
								{
									for(Iterator it_tagVa_A = (tmp_taggedValue.getDataValue()).iterator(); it_tagVa_A.hasNext();)
									{
										String tagValue_Da_A=(String)it_tagVa_A.next();
										if (tagValue_Da_A!=null)
										{
											_id =  tagValue_Da_A;
										}
									}
								} 
								catch(Exception ep) 
								{
									logger.fatal("exception bei  find class : "+ep.getMessage());
								}
								
							}
													
						
						}
					
					} // end of if(!isNameinVector(allNamesOfActivities, tmp.getName()))
					
					if (_idFlag && _userFlag && _permFlag)
					{
						// in die HashMap ablegen:
						result.put(_user + ":" + _id, _perm);
						
						MDRActivity _tmpMDRactivity = new MDRActivity(oname, _id);
						alleAktivitaeten.add(_tmpMDRactivity);
						
						if(!userIstImVector(_user))
						{
							MDRUser _tmpMDRUser = new MDRUser(_user);
							_tmpMDRUser.addActivity(_tmpMDRactivity);
							allUser.add(_tmpMDRUser);
						}
						else
						{
							for(int ii = 0; ii < allUser.size(); ii++)
							{
								MDRUser user = (MDRUser)allUser.get(ii);
								if(user.getUserName().equals(_user))
								{
									user.addActivity(_tmpMDRactivity);
									break;
								}
							}
						}
						
					}
				}
			}
		}
						
		return result;
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
			if (((String)vec.get(i)).equals(name))
			{
				//TODO: diese Suche nach anderen Kriterien ermoeglichen!
				//returnValue = true;
				returnValue = false;
				//log.writeLn(name + " war schon drin!!!!!!!!!!!!!!!!!!!");
				break;
			}
		}
		// diser Name ist nicht im Vector:
		if (!returnValue)
		{
			allNamesOfActivities.add(name);
		}
		return returnValue;
	}
	
	public Vector getAllUsers()
	{
		return allUser;
	}
	
	public Vector getAllActivities()
	{
		return alleAktivitaeten;
	}
	
	private boolean userIstImVector(String name)
	{
		boolean resultValue = false;
		
		for(int z = 0; z < allUser.size(); z++)
		{
			MDRUser _tmpUser = (MDRUser)allUser.get(z);
			if(_tmpUser.getUserName().equals(name))
			{
				// User bereits drin:
				resultValue = true;
				break;
			}
		}
		
		return resultValue;		
	}
		
} // End of class