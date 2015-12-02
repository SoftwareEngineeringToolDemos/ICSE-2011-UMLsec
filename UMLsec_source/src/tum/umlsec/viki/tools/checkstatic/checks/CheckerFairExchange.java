package tum.umlsec.viki.tools.checkstatic.checks;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.omg.uml.behavioralelements.activitygraphs.ActionState;
import org.omg.uml.behavioralelements.activitygraphs.ActionStateClass;
import org.omg.uml.behavioralelements.activitygraphs.ActivityGraph;
import org.omg.uml.behavioralelements.activitygraphs.ActivityGraphsPackage;
import org.omg.uml.behavioralelements.statemachines.FinalStateClass;
import org.omg.uml.behavioralelements.statemachines.StateMachinesPackage;
import org.omg.uml.behavioralelements.statemachines.StateVertex;
import org.omg.uml.behavioralelements.statemachines.Transition;
import org.omg.uml.foundation.core.CorePackage;
import org.omg.uml.foundation.core.TaggedValue;

import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.mdr.IMdrContainer;
import tum.umlsec.viki.tools.checkstatic.StaticCheckerBase;


/**
 * @author Shasha Meng
 */
public class CheckerFairExchange extends StaticCheckerBase {
	private static Logger logger = Logger.getLogger("StaticCheckerBase");


	public boolean check(IMdrContainer _mdrContainer, Iterator _parameters, ITextOutput _textOutput) {
		textOutput = _textOutput;
		boolean dump = true;
		textOutput.writeLn("==================running CheckerFairExchange...");

		// global declarations
		//boolean bedingen3=false;
		boolean bedingen=false;
		ArrayList<String> startList = new ArrayList<String>();
		org.omg.uml.UmlPackage root ;

		CorePackage corePackage ;
		ActivityGraphsPackage activityPackage;

		// replacement for the init function
		root = _mdrContainer.getUmlPackage();
		corePackage = root.getCore();
		activityPackage=(ActivityGraphsPackage)root.getActivityGraphs();
		stateMachines=(StateMachinesPackage)activityPackage.getStateMachines();

		for (Iterator<ActivityGraph> itGraph = root.getActivityGraphs().getActivityGraph().refAllOfClass().iterator(); itGraph.hasNext(); ) {

			graph = itGraph.next();
			logger.trace("Activity Graph : " + graph.getName());
			textOutput.writeLn("================ Checking Activity Graph : " + graph.getName());
			startList.clear();
			stopList.clear();
			// dump

			transitionClasses = graph.getTransitions();


			// list all ActivityGraph
			/*textOutput.writeLn("======= ActivityGraphs");
   		ActivityGraphClass activityClasses = activityPackage.getActivityGraph();
  		for(Iterator it_act_G = activityClasses.refAllOfClass().iterator(); it_act_G.hasNext();){
	   		ActivityGraph activityGraph = (ActivityGraph)it_act_G.next();
			textOutput.writeLn(activityGraph.getName());
   		}
			 */

			// list all the action states in the activity diagram.
			textOutput.writeLn("======= All Action States");
			ActionStateClass actionstateClasses =(ActionStateClass)activityPackage.getActionState();
			for(Iterator it_act_S = actionstateClasses.refAllOfClass().iterator(); it_act_S.hasNext();){
				//for(Iterator it_act_S = action.iterator(); it_act_S.hasNext(); ) {
				ActionState actionState = (ActionState)it_act_S.next();
				textOutput.writeLn(actionState.getName());
				Z_S++;
			}

			textOutput.writeLn("There are "+Z_S+" Action States in the diagram.");
			textOutput.writeLn("======= All Tagged Values");

			for(Iterator<TaggedValue> itTag = graph.getTaggedValue().iterator(); itTag.hasNext(); ) {
				TaggedValue tag = itTag.next();
				logger.trace("Tagged value : " + tag.getType().getName());
				if (tag.getType().getName().equals("start")) {
					for(Iterator it_tagVa_A = (tag.getDataValue()).iterator(); it_tagVa_A.hasNext();){
						String tagValue_Da_A=(String)it_tagVa_A.next();
						textOutput.writeLn("TaggedValue (Data) of start is " + tagValue_Da_A);
						if (tagValue_Da_A!=null) {
							startList.add(tagValue_Da_A);
						}
					}
				}
				if (tag.getType().getName().equals("stop")) {
					for(Iterator it_tagVa_B = (tag.getDataValue()).iterator(); it_tagVa_B.hasNext();) {
						String tagValue_Da_B=(String)it_tagVa_B.next();
						textOutput.writeLn("TaggedValue (Data) of stop is "+tagValue_Da_B);
						if (tagValue_Da_B!=null) {
							stopList.add(tagValue_Da_B);
						}
					}
				}
			}

			//textOutput.writeLn("========= All transitions");
			/** Test startet.
	  All the transitions will be read, when the source of one transition
	  equals to the action state "Pay" for instance, then the program checks
	  whether the target of this transition equals to the action state
	  "Reclaim" or "Delivery". If not, it reads the transition, which with the
	  source equals to this target, and check whether the target of this
	  transition is equal to one of the action state "Reclaim" or "Delivery"
	  until the target of the transition is FinalState.
			 **/
			//z_t_all= transitionClasses.refAllOfClass().size();
			z_t_all = transitionClasses.size();
			textOutput.writeLn("There are "+z_t_all+" transitions in the diagram.");
			//for(Iterator it_Tran= transitionClasses.refAllOfClass().iterator(); it_Tran.hasNext();){
			for(Iterator it_Tran= transitionClasses.iterator(); it_Tran.hasNext();){
				Transition transition=(Transition)it_Tran.next();
				StateVertex sourcevertex=(StateVertex)transition.getSource();
				StateVertex targetvertex=(StateVertex)transition.getTarget();
				logger.trace("Transition from " + sourcevertex.getName() + " to " + targetvertex.getName());

				for(Iterator it_list_sta = startList.iterator(); it_list_sta.hasNext();){
					String startName = (String)it_list_sta.next();
					if (startName.equals(sourcevertex.getName())) {
						Z_Path = 1;
						recursive(transition);
						Z_T = 0;
					}
				}
			}

			//if (bedingen1&&bedingen2)
			if (!(result.contains("n"))){
				//textOutput.writeLn("The UML model satisfies the requirement " + "\<" + "\<" + "fair exchange" + "\>" + "\>");
				textOutput.writeLn("The UML model satisfies the requirement of the stereotype fair exchange.");
				//return dump = true;
			} else {
				//textOutput.writeLn("The UML model violates the requirement " + "\<" + "\<" + "fair exchange" + "\>" + "\>");
				textOutput.writeLn("The UML model violates the requirement of the stereotype fair exchange.");
				//textOutput.writeLn("The potential security design flaws are marked in the modified UML model with '???'!");
				//return dump=false;
				dump = false;
			}
		}
		return dump;
	}



	private ArrayList successor(StateVertex sv) {
		ArrayList list_T1=new ArrayList();
		//for(Iterator it_Tran= transitionClasses.refAllOfClass().iterator(); it_Tran.hasNext();){
		for(Iterator it_Tran= transitionClasses.iterator(); it_Tran.hasNext();){
			Transition transition=(Transition)it_Tran.next();
			StateVertex sourcevertex=(StateVertex)transition.getSource();

			if (sv==sourcevertex) list_T1.add(transition);
		}
		return list_T1;
	}

	/**
	Private ArrayList lastTransitions(StateVertex sv) {
		ArrayList alist = new ArrayList();
		for(Iterator it_Tran= transitionClasses.refAllOfClass().iterator(); it_Tran.hasNext();){
			  Transition transition=(Transition)it_Tran.next();
			  StateVertex targetvertex = (StateVertex)transition.getTarget();

			  if (sv==targetvertex) alist.add(transition);
		}
		 return alist;
	}
	 **/
	private StateVertex getTarget_S (Transition tran) {
		StateVertex sv_tar=(StateVertex)tran.getTarget();
		return sv_tar;
	}

	private boolean isFinalState (StateVertex sv) {
		FinalStateClass finalstateClasses =(FinalStateClass)stateMachines.getFinalState();
		for(Iterator it_final= finalstateClasses.refAllOfClass().iterator(); it_final.hasNext();){
			StateVertex finalstate =(StateVertex)it_final.next();
			if (sv==finalstate) return true;
		}
		return false;
	}

	private boolean inStopList (StateVertex sv) {
		for (Iterator<String> it_list_sto= stopList.iterator(); it_list_sto.hasNext();){
			String stopName = it_list_sto.next();
			if ((sv.getName() != null)  && (sv.getName()).equals(stopName)) 
				return true;
		}
		return false;
	}

	private boolean isTransactionEnd(Transition tran){
		StateVertex sver=getTarget_S(tran);
		if(inStopList(sver)) {
			bedingen2 = true; //a bug is over
			bedingen1 = true;
			//System.out.println("Dedag isStopList is true: bedingen1 = true");
			return true;
		}
		if(isFinalState(sver)) {
			bedingen2 = false;
			//System.out.println("Dedag isFianlState is true: bedingen2 = true");
			//textOutput.writeLn("There are "+z_t_all+" transitions in the diagram.");
			return true;
		}
		//System.out.println("Dedag list does not end: return false");
		return false;
	}

	private void recursive(Transition tran){
		if (Z_T>Z_S){
			bedingen2=false;
			//textOutput.writeLn("There is a cycle in the diagram.");
			return;
		}
		else {Z_T++;}
		//if the transction ended, then
		if (isTransactionEnd(tran)) {
			Z_Path--;
			/**
			   if the transition ended and it's target is in the stop list,
			   that means the UML Model is correct.
			 **/

			if (bedingen1){
				result.add("y");
				//System.out.println("Dedag: bedingen1 is true: result.add y");
			}

			/**
			   The transaction ended but it's target is in the final state list, that means the UML is
			   not correct. The question marks "???" will be marked after the name of the actionstate.
			 **/
			if ( bedingen2 == false ){
				result.add("n");
				//System.out.println("Dedag: bedingen1 is false: result.add n");
				StateVertex statever=(StateVertex)tran.getSource();
				String svname = statever.getName();
				bedingen3=set.add(svname);
				if (bedingen3==true){
					//statever.setName(svname +"???" );
					//if (bedingen3==true){
					textOutput.writeLn("StateVertex: "+ svname + "???");
					textOutput.writeLn("There are errors in the UML model!");}
			}
			return;
		}
		ArrayList tempTranClasses=(successor(getTarget_S(tran)));
		Z_Path+=(tempTranClasses.size()-1);
		for(Iterator it_Tran1= tempTranClasses.iterator(); it_Tran1.hasNext();){
			Transition transition1=(Transition)it_Tran1.next();
			recursive(transition1);
		}
	}



	Collection transitionClasses;
	StateMachinesPackage stateMachines;
	ActivityGraph graph;

	ArrayList<String> stopList = new ArrayList<String>();
	Vector result = new Vector();
	Set set = new HashSet();

	boolean bedingen1=true;
	boolean bedingen2=true;
	boolean bedingen3 = true;

	int Z_S=0;
	int Z_T=0;
	int Z_Path=1;
	int z_t_all=0;


	ITextOutput textOutput;

}
