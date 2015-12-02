package tum.umlsec.viki.tools.checkstatic.checks;


import java.util.ArrayList;
import java.util.Iterator;

import org.omg.uml.behavioralelements.activitygraphs.ActionState;
import org.omg.uml.behavioralelements.activitygraphs.ActionStateClass;
import org.omg.uml.behavioralelements.activitygraphs.ActivityGraphsPackage;
import org.omg.uml.behavioralelements.statemachines.StateMachinesPackage;
import org.omg.uml.foundation.core.CorePackage;
import org.omg.uml.foundation.core.Dependency;
import org.omg.uml.foundation.core.DependencyClass;
import org.omg.uml.foundation.core.Stereotype;
import org.omg.uml.foundation.core.TaggedValue;

import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.mdr.IMdrContainer;
import tum.umlsec.viki.tools.checkstatic.StaticCheckerBase;

/**
 * @author Shasha Meng
 *
 */

public class CheckerSecrecyIntegrity extends StaticCheckerBase {


	boolean bedingen1 = true;

	CorePackage corePackage ;
	ActivityGraphsPackage activityPackage;
	StateMachinesPackage stateMachines;
	DependencyClass dependencyClass_S;
	ITextOutput textOutput;
	public boolean check(IMdrContainer _mdrContainer, Iterator _parameters, ITextOutput _textOutput) {
		textOutput = _textOutput;

//				   global declarations
						bedingen1=true;

						ArrayList startList = new ArrayList();
						org.omg.uml.UmlPackage root ;

						CorePackage corePackage ;
						ActivityGraphsPackage activityPackage;

//				   replacement for the init function
						root = _mdrContainer.getUmlPackage();
						corePackage = root.getCore();
						activityPackage=(ActivityGraphsPackage)root.getActivityGraphs();
						dependencyClass_S = corePackage.getDependency();

//				   dump
						textOutput.writeLn("=============================================== running CheckerSecrcyIntegrity...");

						boolean dump = true;

	  //list all the action states
	  textOutput.writeLn ("======= All Action States");
	  ActionStateClass actionstateClasses = (ActionStateClass) activityPackage.getActionState();
	  for (Iterator it_act_S = actionstateClasses.refAllOfClass().iterator(); it_act_S.hasNext();) {
		//for every action state
		ActionState actionState = (ActionState) it_act_S.next();
		//call the private method and return the name of stereotype of this action state
		String strS = obstere_S (actionState);
		if (strS!=null) {
		  //then print the name of the stereotype of this action state
		  textOutput.writeLn ("Action State with the stereotype "+strS+" is "+ actionState.getName());
		  //for every dependency of the list,which is returned of the method dep_mit_sua()
		  for (Iterator it_a_b = (dep_mit_sua(habesource(actionState),habeswimlane(actionState))).iterator();it_a_b.hasNext();) {
			Dependency dependency_A = (Dependency) it_a_b.next();
			//for every stereotype of the dependency
			for (Iterator iter2 = dependency_A.getStereotype().iterator();iter2.hasNext();) {
			  Stereotype stereotype_D = (Stereotype) iter2.next();
			  //print the name of stereotype of the dependency
			  textOutput.writeLn ("The stereotype of the dependency is "+stereotype_D.getName());
			  //when the stereotyp is equal to the stereotype of the action state
			  if((stereotype_D.getName()).equals(strS))
				//then the boolean variable bedingen1 is true
				bedingen1=true;
			 }
		   }
		 }
	  }
	  if (bedingen1){
		dump = true;
		textOutput.writeLn ("The UML model satisfies the requirement of secrecy and integrity.");
	  }else{
		dump = false;
		textOutput.writeLn ("The UML model violates the requirement of secrecy and integrity.");
	  }
	  return dump;
	}
//	  defines a private method und it takes the action state as parameter
//	  and returns the stereotype as the result
	private String obstere_S (ActionState a) {
	  for (Iterator it_A_S= (a.getStereotype()).iterator(); it_A_S.hasNext();) {
		//for all the stereotypes of the action state
		Stereotype stere = (Stereotype) it_A_S.next();
		//when the stereotype is equal to "secrecy" or "integrity"
		if ((stere.getName()).equals("secrecy")||(stere.getName()).equals("integrity"))
		  //then the name of the stereotype is as result returned.
		  return stere.getName();
	  }
	  return null;
	}
//	  defines a private method and it takes an action state as parameter
//	  and returns a list of tagged values of "source"
	private ArrayList habesource (ActionState a) {
	  //defines a list for the returned result
	  ArrayList list_s = new ArrayList();
	  //for every tagged values of this action states
	  for (Iterator it_Tag_A= (a.getTaggedValue()).iterator(); it_Tag_A.hasNext();) {
		TaggedValue tagValue = (TaggedValue) it_Tag_A.next();
		//when the tagged type is equal to "source" then continue
		if ((tagValue.getType()).getTagType().equals("source")) {
		  try {
			//for every tagged value of "source"
			for (Iterator it_tagVa_A = (tagValue.getDataValue()).iterator(); it_tagVa_A.hasNext();) {
			  String tagValue_Da_A = (String) it_tagVa_A.next();
			  if (tagValue_Da_A!=null)
				//the tagged value is added to the list
				list_s.add(tagValue_Da_A);
			 }
		   } catch(Exception ep) {
			 textOutput.writeLn("exceptions: "+ep.getMessage());
		   }
		}
	  }
	  //returns the list
	  return list_s;
	}
//	  defines a private method and it takes an action state as parameter
//	  and returns the list of the tagged values of "swimlane"
	private ArrayList habeswimlane (ActionState a) {
	  //defines a list for the result
	  ArrayList list_s = new ArrayList();
	  //for every tagged value of this action state
	  for (Iterator it_Tag_A= (a.getTaggedValue()).iterator(); it_Tag_A.hasNext();) {
		TaggedValue tagValue = (TaggedValue) it_Tag_A.next();
		//when the tagged type is equal to "swimlane"
		if ((tagValue.getType()).getTagType().equals("swimlane")) {
		  try {
			//for every tagged value of "swimlane"
			for (Iterator it_tagVa_1 = (tagValue.getDataValue()).iterator(); it_tagVa_1.hasNext();) {
			  String tagValue_Da_1 = (String) it_tagVa_1.next();
			  if (tagValue_Da_1!=null)
				//the tagged value is added to the list
				list_s.add(tagValue_Da_1);
			 }
		   } catch(Exception ep) {
			 textOutput.writeLn ("exceptions: "+ep.getMessage());
		   }
		}
	  }
	  //return the list
	  return list_s;
	}
//	  defines a private method and it takes two lists as parameter
//	  and returns a list of dependencis as result
	private ArrayList dep_mit_sua (ArrayList list1,ArrayList list2) {
	  //defines a list as result
	  ArrayList list_d = new ArrayList();
	  //for every dependeny
	  for (Iterator iter1 = dependencyClass_S.refAllOfClass().iterator(); iter1.hasNext();) {
		Dependency dependency_S = (Dependency) iter1.next();
		boolean b1 = false;
		boolean b2 = false;
		//for every supplier of the dependency
		for (Iterator it3_de = (dependency_S.getSupplier()).iterator(); it3_de.hasNext();) {
		  org.omg.uml.foundation.core.ModelElement classifier_S = (org.omg.uml.foundation.core.ModelElement)it3_de.next();
		  //when the supplier of the dependency is contained in the list of tagged values of "source"
		  if (list1.contains(classifier_S.getName()))
			//then the boolean variable b1 is true
			b1=true;
		 } //end for dependency Supplier
		 //for every client of the dependency
		 for (Iterator it2 = (dependency_S.getClient()).iterator(); it2.hasNext();) {
		   org.omg.uml.foundation.core.ModelElement modelElement_C = (org.omg.uml.foundation.core.ModelElement)it2.next();
		   //when the client of the dependency is contained in the list of tagged values of "swimlane" of the action state
		   if (list2.contains(modelElement_C.getName()))
			 //then the boolean variable b2 is true
			 b2=true;
		  }
		  //when two conditions are fulfilled
		  if (b1&&b2)
			//then the dependency is added to the list list_d
			list_d.add(dependency_S);
	  }
	  //return this list of dependencies
	  return list_d;
	}
}
