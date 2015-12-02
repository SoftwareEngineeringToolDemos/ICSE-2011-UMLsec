package tum.umlsec.viki.tools.checkstatic.checks;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.omg.uml.behavioralelements.activitygraphs.ActivityGraphsPackage;
import org.omg.uml.behavioralelements.commonbehavior.Action;
import org.omg.uml.behavioralelements.statemachines.Guard;
import org.omg.uml.behavioralelements.statemachines.StateMachinesPackage;
import org.omg.uml.behavioralelements.statemachines.Transition;
import org.omg.uml.behavioralelements.statemachines.TransitionClass;
import org.omg.uml.foundation.core.CorePackage;
import org.omg.uml.foundation.core.TaggedValue;
import org.omg.uml.foundation.core.TaggedValueClass;
import org.omg.uml.foundation.core.UmlClass;
import org.omg.uml.foundation.datatypes.ActionExpression;
import org.omg.uml.foundation.datatypes.BooleanExpression;

import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.mdr.IMdrContainer;
import tum.umlsec.viki.tools.checkstatic.StaticCheckerBase;

/**
 * @author Shasha Meng
 *
 */

public class CheckerGuardedAccess extends StaticCheckerBase {

	private static Logger logger;
	boolean bedingen1;
	boolean bedingen2;
	HashMap obj_Val = new HashMap();
	//	  defines a list for the names of all the classes
	ArrayList list_objname=new ArrayList();
	ArrayList list_all=new ArrayList();

	CorePackage corePackage ;
	ActivityGraphsPackage activityPackage;
	StateMachinesPackage stateMachines;
	TransitionClass transitionClasses;
	ITextOutput textOutput;
	//	  initial

	public CheckerGuardedAccess(){
		logger=Logger.getLogger("StaticChecker");
	}
	public boolean check(IMdrContainer _mdrContainer, Iterator _parameters, ITextOutput _textOutput) {
		textOutput = _textOutput;

		//						   global declarations
		bedingen1=false;
		bedingen2=true;
		ArrayList startList = new ArrayList();
		org.omg.uml.UmlPackage root ;

		CorePackage corePackage ;
		ActivityGraphsPackage activityPackage;

		//						   replacement for the init function
		root = _mdrContainer.getUmlPackage();
		corePackage = root.getCore();
		activityPackage=(ActivityGraphsPackage)root.getActivityGraphs();
		stateMachines = (StateMachinesPackage) activityPackage.getStateMachines();
		transitionClasses = (TransitionClass) stateMachines.getTransition();
		//						   dump
		textOutput.writeLn("=============================================== running CheckerGuardedAccess...");

		boolean dump = true;
		//list all the tagged values in the diagram
		textOutput.writeLn ("======= All Tagged Values of Classes");
		TaggedValueClass tagvalueClasses = (TaggedValueClass) corePackage.getTaggedValue();
		for (Iterator it_Tag_V = tagvalueClasses.refAllOfClass().iterator(); it_Tag_V.hasNext();) {
			TaggedValue tagValue = (TaggedValue) it_Tag_V.next();
			//if the tagged type equal to "guard"
			//reads all the names of the classes with the tagged type equal to "guard" in a list list_objname.
			logger.trace("Tag name: " + tagValue.getType().getName());
			//		if ((tagValue.getType()).getTagType().equals("guard")) {
			if ((tagValue.getType()).getName().equals("guard")) {
				String objname = null;
				//defines a list for the tagged values of the "guard" of every class
				ArrayList list_valname = new ArrayList();
				try {
					//list all the classes in the diagram
					UmlClass uml_C = (UmlClass) (tagValue.getModelElement());
					//list the name of the classes
					objname = uml_C.getName();
					//print the name of the classes
					textOutput.writeLn ("The name of the model element (class) is "+objname);
					if (objname!=null)
						//the name of the class is added to the list_objname
						list_objname.add(objname);
					for (Iterator it_tagVa_A = (tagValue.getDataValue()).iterator(); it_tagVa_A.hasNext();) {
						String tagValue_Da_A = (String) it_tagVa_A.next();
						//list the tagged values of the "guard" for every class
						textOutput.writeLn ("TaggedValue (Data) of guard is "+tagValue_Da_A);
						//Then it reads all the tagged values of the tagged type "guard" of every class in the list list_objname in a list with the name list_valname.
						if (tagValue_Da_A!=null)
							//all the tagged values of the "guard" are added to the list_valname
							list_valname.add(tagValue_Da_A);
					}
				} catch (Exception ep) {
					textOutput.writeLn ("exceptions: "+ep.getMessage());
				}
				//It defines a hash map with the name obj_Val, the key of it is the name of the classes objname in the list list_objname, and the value of the key is the list list_valname.
				if (list_valname != null && objname != null)
					obj_Val.put(objname,list_valname);
			}
		}
		textOutput.writeLn("========= All Transitions");
		//here begins the Test.
		//reads the value of the obj of the guard of the transition in the activity diagram.
		//reads the action of this transition and checks whether it is equal to the value of the key according to the hash map obj_Val.
		for (Iterator it_Tran= transitionClasses.refAllOfClass().iterator(); it_Tran.hasNext();) {
			Transition transition = (Transition) it_Tran.next();
			String transition_name = transition.getName();
			Guard guard = (Guard) transition.getGuard();
			Action action = (Action) transition.getEffect();
			if (guard!=null&&action!=null) {
				BooleanExpression b_Expression = (BooleanExpression) guard.getExpression();
				ActionExpression a_Expression = (ActionExpression) action.getScript();
				String bodyname_a = (String) a_Expression.getBody();
				String bodyname = (String) b_Expression.getBody();
				int i = bodyname.indexOf("=");
				int j = bodyname_a.indexOf(".");
				String vor_a;
				if (j!=-1) {
					vor_a=bodyname_a.substring(0,j);
				} else {
					vor_a = new String (bodyname_a);
				}
				textOutput.writeLn ("Important action part of the transition "+transition_name+" is "+ vor_a);
				String vor = bodyname.substring(0,i);
				String nach = bodyname.substring(i+1);
				if (vor.equals("obj")) {
					try {
						if (list_objname.contains(nach)) {
							ArrayList list_val = (ArrayList) obj_Val.get(nach);
							bedingen1 = list_val.contains(vor_a);
							//textOutput.writeLn ("bedingen1 is "+bedingen1);
							if(!bedingen1)
								bedingen2 = false;
						}
						textOutput.writeLn ("Important guard part of the transition " + transition_name + " is "+nach);
					} catch (Exception ep) {
						textOutput.writeLn ("exceptions: "+ep.getMessage());
					}
				}
			}
		}
		if (bedingen1&&bedingen2){
			dump=true;
			//textOutput.writeLn("The UML model satisfies the requirement " + "\<" + "\<" + "guarded access" + "\>" + "\>");
			textOutput.writeLn ("The UML model satisfies the requirement of the stereotype guarded access.");
		}  else {
			dump=false;
			//textOutput.writeLn("The UML model violates the requirement " + "\<" + "\<" + "guarded access" + "\>" + "\>");
			textOutput.writeLn ("The UML model violates the requirement of the stereotype guarded access.");
		}
		return dump;
	}

}
