/*
 * Created on 19.08.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package tum.umlsec.viki.tools.berechtigungen2.mdrparser;

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

/**
 * @author josef
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MDRParser2 {
	private static Logger logger;
	
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
		//private HashMap rolHMap = new HashMap(); 
		private HashMap protHMap = new HashMap(); 
		private HashMap userHMap = new HashMap();
		private Vector alleVectoren = new Vector();
		
		//String oname = "";
		
		ITextOutput log;
		
		HashMap result = new HashMap();
		
		public MDRParser2(ITextOutput output)
		{
			log = output;
			logger=Logger.getLogger("berechtigungen2");
		}
		
		public Vector getVectors()
		{
			alleVectoren.add(protHMap);
			//alleVectoren.add(rolHMap);
			alleVectoren.add(userHMap);
			return 	alleVectoren;
		}

		public HashMap parseUMLDiagram(IMdrContainer _mdrWrapper)
		{
			allNamesOfActivities = new Vector();
			
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
			
			//reset IDs
			RoleClass.resetRoleId();
			UserClass.resetUserId();
			ProtectedClass.resetProtectedId();
			
			for (Iterator it_act_I_2 = activityClasses.refAllOfClass().iterator(); it_act_I_2.hasNext();) 
			{
				actGraph = (ActivityGraph)it_act_I_2.next();
				for (Iterator it_act_I_3 = actGraph.getTransitions().iterator(); it_act_I_3.hasNext();)
				{
					Transition tmp_transition = (Transition)it_act_I_3.next();
					StateVertex tmp = (StateVertex)tmp_transition.getTarget();
					if (tmp.getClass().toString().equals("class org.omg.uml.behavioralelements.activitygraphs.ActionState$Impl"))
					{	
						String _user = "";
						boolean helpFlag = false;
						boolean userFlag = false;
						ProtectedClass prot = null;
						UserClass uClass = null;
						
						// tests, if this activity was processed
						if(!isNameinVector(allNamesOfActivities, tmp.getName()))
						{
							if (!protHMap.containsKey(tmp.getName()))
							{
								prot = new ProtectedClass(tmp.getName());
								//protVector contains the ProtectedClass objects with unique id
								protHMap.put(prot.getProtName(),prot);
								//System.out.println("protectedName: "+prot.getProtName()+"    id: " +prot.getProtID());
							}
							
							for (Iterator it_taggedValueIt = tmp.getTaggedValue().iterator(); it_taggedValueIt.hasNext();)
							{
								TaggedValue tmp_taggedValue = (TaggedValue)it_taggedValueIt.next();
								String tag = tmp_taggedValue.getType().getName();						
								
								if (tag.equals("user"))
								{	
									helpFlag = true;
									
									try 
									{
										for(Iterator it_tagVa_A = (tmp_taggedValue.getDataValue()).iterator(); it_tagVa_A.hasNext();)
										{
											String taggedValue=(String)it_tagVa_A.next();
											if (taggedValue!=null)
											{
												_user = taggedValue;
											
												boolean help2 = userExists(_user);
																		
												if (!help2)
												{
													uClass = new UserClass(_user);
													userHMap.put(uClass.getUserName(), uClass);
													userFlag = true;
												}
												else
												{
													uClass = (UserClass)userHMap.get(_user);
												}
												//System.out.println("userName: "+uClass.getUserName()+"    id: " +uClass.getUserID());
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
						
						if (helpFlag && prot != null)
						{
							// put into HashMap: put(Object key, Object value)
							result.put(_user + ":" + prot.getProtID(), prot.getProtName());
							uClass.addProtected(prot);
							//System.out.println("fur die result Hashmap: "+ _user + ":" + prot.getProtID()+","+ prot.getProtName());
						}
					} // end if (tmp.getClass().toString().equals("class org.omg.uml.behavioralelements.activitygraphs.ActionState$Impl"))
				}//end for
			} // end for
						
			return result;
		}
		
		
		private boolean userExists(String user)
		{
			if(userHMap.containsKey(user))
			{
				return true;
			}
			else 
			{
				return false;
			}
		}
		
		/**
		 * This method checks, if the activity has been processed. If not, the name of the activity
		 * will be added to an array.
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
					returnValue = false;
					break;
				}
			}
			// this activity-name is not in the vector:
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
} //end of class
