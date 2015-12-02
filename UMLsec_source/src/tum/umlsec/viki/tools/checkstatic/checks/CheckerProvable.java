package tum.umlsec.viki.tools.checkstatic.checks;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import org.omg.uml.behavioralelements.activitygraphs.ActionState;
import org.omg.uml.behavioralelements.activitygraphs.ActionStateClass;
import org.omg.uml.behavioralelements.activitygraphs.ActivityGraphsPackage;
import org.omg.uml.behavioralelements.statemachines.Pseudostate;
import org.omg.uml.behavioralelements.statemachines.StateMachinesPackage;
import org.omg.uml.behavioralelements.statemachines.StateVertex;
import org.omg.uml.behavioralelements.statemachines.Transition;
import org.omg.uml.behavioralelements.statemachines.TransitionClass;
import org.omg.uml.foundation.core.CorePackage;
import org.omg.uml.foundation.core.TaggedValue;
import org.omg.uml.foundation.core.TaggedValueClass;

import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.mdr.IMdrContainer;
import tum.umlsec.viki.tools.checkstatic.StaticCheckerBase;

/**
 * @author Shasha Meng
 *
 */

public class CheckerProvable extends StaticCheckerBase {

    //checks whether the source of a transition is contained in the tag {action}
    boolean bedingen1 = false;
    //checks whether the source of a transition is not an initial state
    boolean bedingen2=false;


    //denotes the list of all the tagged values of the tagged type "cert"
    ArrayList list_cert = new ArrayList();
    //denotes the list of all the tagged values of the tagged type "action"
    ArrayList list_action = new ArrayList();
    Vector result = new Vector();
    //  UmlPackage root ;
    StateMachinesPackage stateMachines;
    TransitionClass transitionClasses;

	  public boolean check(IMdrContainer _mdrContainer, Iterator _parameters, ITextOutput _textOutput) {

		 textOutput = _textOutput;
     //global declarations
     org.omg.uml.UmlPackage root ;
     CorePackage corePackage ;
		 ActivityGraphsPackage activityPackage;
     //replacement for the init function
		 root = _mdrContainer.getUmlPackage();
		 corePackage = root.getCore();
		 activityPackage=(ActivityGraphsPackage)root.getActivityGraphs();
		 stateMachines=(StateMachinesPackage)activityPackage.getStateMachines();
		 transitionClasses=(TransitionClass)stateMachines.getTransition();
		 textOutput.writeLn("=============================================== running CheckerProvable...");
     boolean dump = true;
     // list all the action states in the activity diagram.
	   textOutput.writeLn ("======= All Action States");
	   ActionStateClass actionstateClasses = (ActionStateClass) activityPackage.getActionState();
	   for (Iterator it_act_S = actionstateClasses.refAllOfClass().iterator(); it_act_S.hasNext();) {
	    ActionState actionState = (ActionState) it_act_S.next(); 
	    //list the name of the action states
	    textOutput.writeLn (actionState.getName());
	    Z_S++;
	   }
     textOutput.writeLn("There are "+Z_S+" Action States in the diagram.");
	   //list all the tagged values
	   textOutput.writeLn ("======= All TaggedValues");
	   TaggedValueClass tagvalueClasses = (TaggedValueClass) corePackage.getTaggedValue();
		for (Iterator it_Tag_D = tagvalueClasses.refAllOfClass().iterator(); it_Tag_D.hasNext();) {
			TaggedValue tag = (TaggedValue) it_Tag_D.next();
		     //list all tagged values of the tagged type "action"
			if (tag.getType().getName().equals("action")) {
				for (Iterator it_tagVa_Action = tag.getDataValue().iterator();it_tagVa_Action.hasNext();) {
					String tagValue_Da_Action = (String) it_tagVa_Action.next();
					//print the tagged values of the "action"
					textOutput.writeLn ("TaggedValue (Data) of action is "+tagValue_Da_Action);
					if (tagValue_Da_Action!=null)
					  //tagged value is added to the list list_action
						list_action.add(tagValue_Da_Action);
				}
			}
		     //list all tagged values of the tagged type "cert"
			if (tag.getType().getName().equals("cert")) {
				for (Iterator it_tagVa_Cert = tag.getDataValue().iterator(); it_tagVa_Cert.hasNext();) {
					String tagValue_Da_Cert = (String) it_tagVa_Cert.next();
					//print the tagged values of the "action"
					textOutput.writeLn ("TaggedValue (Data) of cert is "+tagValue_Da_Cert);
					if (tagValue_Da_Cert!=null)
					  //tagged value is added to the list list_action
					 list_cert.add(tagValue_Da_Cert);
				}
			}
		}
	   
//	   for (Iterator it_Tag_V = tagvalueClasses.refAllOfClass().iterator(); it_Tag_V.hasNext();) {
//	     TaggedValue tagValue = (TaggedValue) it_Tag_V.next();
//	     //list all tagged values of the tagged type "action"
//	     if ((tagValue.getType()).getTagType().equals("action")) {
//		   for (Iterator it_tagVa_A = (tagValue.getDataValue()).iterator(); it_tagVa_A.hasNext();) {
//		    String tagValue_Da_A = (String) it_tagVa_A.next();
//		    //print the tagged values of the "action"
//		    textOutput.writeLn ("TaggedValue (Data) of action is "+tagValue_Da_A);
//		    if (tagValue_Da_A!=null)
//			  //tagged value is added to the list list_action
//			     list_action.add(tagValue_Da_A);
//		   }
//	     }
//	  //list all tagged values of the tagged type "cert"
//	  if ((tagValue.getType()).getTagType().equals("cert")) {
//		for (Iterator it_tagVa_B = (tagValue.getDataValue()).iterator(); it_tagVa_B.hasNext();) {
//		  String tagValue_Da_B = (String) it_tagVa_B.next();
//		  //print of the tagged values of "cert"
//		  textOutput.writeLn ("TaggedValue (Data) of cert is "+tagValue_Da_B);
//		  if (tagValue_Da_B!=null)
//			//tagged value is added to the list list_cert
//			list_cert.add(tagValue_Da_B);
//		}
//	  }
//	}
	//list all the transitions in the diagram
	textOutput.writeLn("========= All Transitions");
  z_t_all= transitionClasses.refAllOfClass().size();
  textOutput.writeLn("There are "+z_t_all+" transitions in the diagram.");

  textOutput.writeLn("========= Here begins the verification.");
	for (Iterator it_Tran= transitionClasses.refAllOfClass().iterator(); it_Tran.hasNext();) {
	  Transition transition=(Transition)it_Tran.next();
	  //list the action state as the source of every transition
	  StateVertex stateVertex=(StateVertex)transition.getSource();
	  //list the action state as the target of every transition
	  StateVertex stateVertex_tar=(StateVertex)transition.getTarget();

    if(isFinalState(stateVertex_tar) == false && isJunctionState(stateVertex_tar) == false && isChoiceState(stateVertex_tar) == false && isForkState(stateVertex_tar) == false &&isJoinState(stateVertex_tar) == false)
	  {
	   for (Iterator it_list_cer= list_cert.iterator(); it_list_cer.hasNext();) {
		 String certName = (String) it_list_cer.next();
		 //checks whether the target of the transition is equal to the tagged value of tagged type "cert"
		 if (certName.equals(stateVertex_tar.getName())) {
		  //Z_Path = 1;
      selber(transition);
		  //Z_T = 0;
		 }
		}
	  }
	}

    if (result.contains("n")){

  textOutput.writeLn ("The UML model violates the requirement of the stereotype provable.");

	return dump = false;
	}else  {

  textOutput.writeLn("The UML model satisfies the requirement of the stereotype provable.");
	return dump = true;
	}
  }

//	defines a private method, it takes an action state as the parameter
//	and returns a list of transitions which take this action state as the target
  private ArrayList beforeTransitions (StateVertex sv) {
	//creates a list of the transitions for the result of the method
	ArrayList list_T1 = new ArrayList();
	Iterator it_Tran = sv.getIncoming().iterator();
  while(it_Tran.hasNext()){
	  Transition transition = (Transition) it_Tran.next();
    list_T1.add(transition);
  }
	//list all the transitions
	/*for (Iterator it_Tran= transitionClasses.refAllOfClass().iterator(); it_Tran.hasNext();) {
	  Transition transition = (Transition) it_Tran.next();
	  //get the target of every transition
	  StateVertex stateVertex = (StateVertex) transition.getTarget();
	  //wenn the target of the transition is equal to the parameter
	  if (sv == stateVertex)
		//then this transition is added to the list
		list_T1.add(transition);
	}
	*/
	return list_T1;
  }
//	defines a private method, and it takes a transition as parameter
//	and returns the source of this transition
  private StateVertex getSource_S (Transition tran) {
	StateVertex sv_tar = (StateVertex) tran.getSource();
	return sv_tar;
  }
//	defines a private method and it takes an action state as parameter
//	and checks if this action state is initialstate

// method to check for initial states
    /*private static boolean isInitialState(StateVertex state) {
        if (state.getClass().getName().endsWith("Pseudostate$Impl")) {
            if (((Pseudostate)state).getKind().toString() == "initial") {
                    return true;
                }
                else {
                    return false;
                }
        }
        else return false;
    }
    */

  private boolean isInitialState(StateVertex sv){
        ArrayList list_T2 = new ArrayList();
      Iterator iterator_Tran = sv.getIncoming().iterator();

      while(iterator_Tran.hasNext()){
         Transition transition = (Transition) iterator_Tran.next();
         list_T2.add(transition);
       }
       //textOutput.writeLn("Hi1");
      if(list_T2.size()==0) return true;
      else return false;
  }

  /*private boolean obIninalstate (StateVertex sv) {
    ArrayList list_T2 = new ArrayList();
     //textOutput.writeLn("Hi1");
	PseudostateClass initialstateClasses = (PseudostateClass) stateMachines.getPseudostate();
	for (Iterator it_initial = initialstateClasses.refAllOfClass().iterator(); it_initial.hasNext();) {
	  //list all the initial states in the diagram
	  Pseudostate initialstate = (Pseudostate) it_initial.next();
    textOutput.writeLn("Hi1");
	  if ((initialstate.getKind().toString()).equals("initial"))
	  {
      textOutput.writeLn("Hi1");
      //for every initialstate checks whether it is equal to the parameter
		  if (sv == ((StateVertex)initialstate))
		  {
         textOutput.writeLn ("Hi, I am here1.");
		     return true;
		  }
	  }
	  }
	return false;
  }
*/
//	defines a private method and it takes an action state as parameter
//	and checks if this action state is contained in the list of tagged values of the tagged type "action"
  private boolean obinactionlist (StateVertex sv) {
    if(isFinalState(sv) == false && isJunctionState(sv) == false && isChoiceState(sv) == false && isForkState(sv) == false &&isJoinState(sv) == false)
    {
	    for (Iterator it_list_action= list_action.iterator(); it_list_action.hasNext();)
	    {
	      String actionName = (String) it_list_action.next();
	      if ((sv.getName()).equals(actionName))
		       return true;
	    }
	  }
	return false;
  }
//	defines a private method and it takes a transition as parameter
  private boolean obtrannichtend (Transition tran) {

	//list the source action state of this transition
	StateVertex sver = getSource_S(tran);
	//if es is contained in the tagged value of the tagged type "action"
	if (obinactionlist(sver)) {
    bedingen2 = true;
	  bedingen1 = true;
	  //textOutput.writeLn ("bedingen1 is true");
	  return false;
	}
	//if es is an initial state in the diagram
  if (isInitialState(sver)) {
	  bedingen1=false;
	  bedingen2 = false;
	  //if es is an initial state, then bedingen2 is false
	  //textOutput.writeLn ("Hi, I am here.");
	  return false;
	}

	return true;
  }
//	 recursive call and it reads all the transitions, when the target of one transition is equal to the tagged value of "cert"
//	then the program checks whether the source of this transition is equal to the tagged values of "action"
//	Wenn not, then it reads the transition with the target equal to this source, and examines whether the source of this transition is equal to the tagged value of "action"
//	until the source of the transition is InitialState.
  private void selber(Transition tran) {


  if (!obtrannichtend(tran))
  {
    //Z_Path--;
    if (bedingen1&&bedingen2){result.add("y");}
    else
    {result.add("n");}

    return;
    }
    else
    {
    ArrayList tempTranClasses = (beforeTransitions(getSource_S(tran)));
    //textOutput.writeLn(Integer.toString(tempTranClasses.size()));
    for (Iterator it_Tran1= tempTranClasses.iterator(); it_Tran1.hasNext();)
    {

	     Transition transition1=(Transition)it_Tran1.next();

	     selber(transition1);

	  }
	  }
  }

      // method to check for choice states
    private static boolean isChoiceState(StateVertex state) {
        if (state.getClass().getName().endsWith("Pseudostate$Impl")) {
            if (((Pseudostate)state).getKind().toString() == "choice") {
                    return(true);
                }
                else {
                    return(false);
                }
        }
        else return(false);
    }

    // method to check for junction states
    private static boolean isJunctionState(StateVertex state) {
        if (state.getClass().getName().endsWith("Pseudostate$Impl")) {
            if (((Pseudostate)state).getKind().toString() == "junction") {
                    return(true);
                }
                else {
                    return(false);
                }
        }
        else return(false);
    }

     // method to check for fork states
    private static boolean isForkState(StateVertex state) {
        if (state.getClass().getName().endsWith("Pseudostate$Impl")) {
            if (((Pseudostate)state).getKind().toString() == "fork") {
                    return(true);
                }
                else {
                    return(false);
                }
        }
        else return(false);
    }

     // method to check for join states
    private static boolean isJoinState(StateVertex state) {
        if (state.getClass().getName().endsWith("Pseudostate$Impl")) {
            if (((Pseudostate)state).getKind().toString() == "join") {
                    return(true);
                }
                else {
                    return(false);
                }
        }
        else return(false);
    }

// method to check for final states
    private static boolean isFinalState(StateVertex state) {
        if (state.getClass().getName().endsWith("FinalState$Impl")) {
                return(true);
        }
        else return(false);
    }




  int Z_S = 0;
  //int Z_T = 0;
  int Z_Path=0;
  int z_t_all=0;

  ITextOutput textOutput;
}
