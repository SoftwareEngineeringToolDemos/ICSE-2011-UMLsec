/**
 * Created by JCreator.
 * User: shasha meng
 * Date: Jan 22, 2003
 * Time: 8:59:27 PM
 * To change this template use Options | File Templates.
 */
package tum.umlsec.viki.tools.checkstatic.checks;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.omg.uml.behavioralelements.activitygraphs.ActivityGraphsPackage;
import org.omg.uml.foundation.core.Attribute;
import org.omg.uml.foundation.core.AttributeClass;
import org.omg.uml.foundation.core.CorePackage;
import org.omg.uml.foundation.core.Stereotype;
import org.omg.uml.foundation.core.TaggedValue;
import org.omg.uml.foundation.core.UmlClass;
import org.omg.uml.foundation.core.UmlClassClass;

import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.mdr.IMdrContainer;
import tum.umlsec.viki.tools.checkstatic.StaticCheckerBase;
public class CheckerFreshness extends StaticCheckerBase {
	private static Logger logger;
	
	public CheckerFreshness(){
		logger=Logger.getLogger("StaticChecker");
	}

	boolean bedingen1=true;
//	   this boolean variable bedingen2 checks the second condition;
//	   that is: If a message name in I appears in the tag {secre} in C then the dependency is stereotyped <<secrecy>>.
//	boolean bedingen2=true;
//		boolean bedingen3=false;
//		boolean bedingen=false;
//	   list all the tagged values of all the clients of the dependency
//	ArrayList list_C_all=new ArrayList();
//	   list all the features of the supplier

	List set_list=new ArrayList();
//	   list all the tagged values of the clients of the abstraction
//	ArrayList list_C_ab_all=new ArrayList();
	ITextOutput textOutput;


	public boolean check(IMdrContainer _mdrContainer, Iterator _parameters, ITextOutput _textOutput) {
		textOutput = _textOutput;

//				   global declarations
						bedingen1=true;
//						bedingen2=true;
						ArrayList startList = new ArrayList();
						org.omg.uml.UmlPackage root ;

						CorePackage corePackage ;
						ActivityGraphsPackage activityPackage;

//				   replacement for the init function
						root = _mdrContainer.getUmlPackage();
						corePackage = root.getCore();
						activityPackage=(ActivityGraphsPackage)root.getActivityGraphs();

//				   dump
						textOutput.writeLn("====================running CheckerFreshness...");

						boolean dump = true;


	  textOutput.writeLn("=======Attributes and their Classes");
	  AttributeClass attributeClass = corePackage.getAttribute();
	  for (Iterator ita=attributeClass.refAllOfClass().iterator();ita.hasNext();){
		Attribute att = (Attribute)ita.next();



		if (att!=null&&att.getInitialValue()!=null){
			UmlClass uc=(UmlClass) att.getOwner();
			textOutput.writeLn(uc.getName());
			String bodyname=att.getInitialValue().getBody();
			textOutput.writeLn(bodyname);
			List templist= new ArrayList();
			templist.add(uc.getName());
			templist.add(bodyname);
			set_list.add(templist);
		}

	  }


	  // list all classes
	  textOutput.writeLn("======= Classes");
	  UmlClassClass umlClassClass = corePackage.getUmlClass();
	  for (Iterator it = umlClassClass.refAllOfClass().iterator(); it.hasNext();) {
	  // all information about the Class can be accessed through the UmlClass class here
		UmlClass umlClass = (UmlClass) it.next();

		String u_Name=umlClass.getName();
		if (!u_Name.equals("String")) textOutput.writeLn ("The name of the class is "+u_Name);
		for (Iterator iter2 =umlClass.getStereotype().iterator(); iter2.hasNext();){
			Stereotype stereotype_D = (Stereotype) iter2.next();
			String s_Name=stereotype_D.getName();
			textOutput.writeLn ("The stereotype of "+u_Name+" is "+s_Name);
			if (s_Name.equals("critical")){
				List set_list_in=new ArrayList();
				for(Iterator it_tagv=umlClass.getTaggedValue().iterator();it_tagv.hasNext();){
					TaggedValue tagv=(TaggedValue)it_tagv.next();
					String t_Name=tagv.getType().getName();
//					String t_Name=tagv.getName();
					if (!t_Name.equals("element.uuid")) textOutput.writeLn("The name of the tag is "+t_Name);

					if(t_Name!=null&&t_Name.equals("fresh")){

					for(Iterator it_v=tagv.getDataValue().iterator();it_v.hasNext();){
						String value=(String)it_v.next();
						textOutput.writeLn("Tag: "+t_Name+ " has the value: "+value);

						if (value!=null&&!this.checkerStringInList(value,set_list_in)){
						set_list_in.add(value);
						}else{

						textOutput.writeLn("wrong input");
						}
					}
					}
				}
			  for (Iterator sl= set_list.iterator();sl.hasNext();){
			  	List l =(List)sl.next();
			  	String name=(String)l.get(0);
			  	if (u_Name!=null&&!u_Name.equals(name)){
					Iterator i=set_list_in.iterator();
					  while(i.hasNext()){
					  String v=(String)i.next();
					  if (v!=null){
					  String attl=(String)l.get(1);
					  if(!v.equals(attl)){
					  //nothing
					  } else {

					 textOutput.writeLn(attl+"is the initial value of an attribute in the class "+name );
					 bedingen1=false;
					 break;
					  }
					  }
			  	}
			  }
//			  if (set_list_in!=null&&set_list_in.size()>0){
//




			  }
			}
		}


	  }
//	  // list all dependencies
//	  textOutput.writeLn ("======= Dependencies");
//	  DependencyClass dependencyClass = corePackage.getDependency();
//	  for (Iterator it = dependencyClass.refAllOfClass().iterator(); it.hasNext();) {
//	  // all information about the Dependency can be accessed through the DependencyClass class here
//		Dependency dependency = (Dependency) it.next();
//		textOutput.writeLn(dependency.getName());
//	  }
	  // list all the stereotypes
//	  textOutput.writeLn ("=======Stereotypes");
//	  StereotypeClass stereotypeClasses = corePackage.getStereotype();
//	  for (Iterator iter = stereotypeClasses.refAllOfClass().iterator(); iter.hasNext();) {
//		Stereotype stereotype = (Stereotype) iter.next();
//		textOutput.writeLn (stereotype.getName());
//	  }
//	  // list all of the stereotypes of the dependencies
//	  textOutput.writeLn ("=======Stereotypes of dependencies");
//	  //first list all the dependecies
//	  DependencyClass dependencyClass_S = corePackage.getDependency();
//	  for (Iterator iter1 = dependencyClass_S.refAllOfClass().iterator(); iter1.hasNext();) {
//		Dependency dependency_S = (Dependency)iter1.next();
//		//list all the stereotypes of every dependency
//		for (Iterator iter2 =dependency_S.getStereotype().iterator(); iter2.hasNext();) {
//		  Stereotype stereotype_D = (Stereotype) iter2.next();
//		  textOutput.writeLn (stereotype_D.getName());
//		}
//	  }
//	  // list all of the operations
//	  textOutput.writeLn ("=============All operations");
//	  OperationClass operationClasses=corePackage.getOperation();
//	  for (Iterator iterOp = operationClasses.refAllOfClass().iterator(); iterOp.hasNext();) {
//		Operation operation = (Operation)iterOp.next();
//		textOutput.writeLn (operation.getName());
//		textOutput.writeLn (operation.getOwner().getName());
//	  }
//	  //here begins the test.
//	  textOutput.writeLn ("==============All abstractions");
//	  //list all the abstractions in the diagram.
//	  AbstractionClass abstractionClasses = corePackage.getAbstraction();
//	  for (Iterator iter_abstra = abstractionClasses.refAllOfClass().iterator(); iter_abstra.hasNext();) {
//		Abstraction abstraction_ = (Abstraction) iter_abstra.next();
//		//print the name of the abstraction
//		textOutput.writeLn (abstraction_.getName());
//		//It reads the stereotype von Abstraction
//		for (Iterator iter_Stere = (abstraction_.getStereotype()).iterator(); iter_Stere.hasNext();) {
//		  Stereotype stere = (Stereotype) iter_Stere.next();
//		  //print the stereotype of the abstaction
//		  textOutput.writeLn ("The stereotype of abstraction is "+stere.getName());
//		  //when the stereotype of the abstraction is equals "realize", then continue
//		  if (stere.getName().equals("realize")) {
//		  //list the supplier of the abstraction
//		  for (Iterator it2_ab2 = (abstraction_.getSupplier()).iterator(); it2_ab2.hasNext();) {
//			textOutput.writeLn("::::::: test abstraction's supplier: ");
//			Classifier classifier_S_ab2=(Classifier)it2_ab2.next();
//			textOutput.writeLn(classifier_S_ab2.getName());
//			//list the all of the dependencies
//			for (Iterator it1_de = dependencyClass.refAllOfClass().iterator(); it1_de.hasNext();) {
//			  Dependency dependency_S = (Dependency) it1_de.next();
//			  textOutput.writeLn("=======DependencytoSuppliername");
//			  //list the supplier of every dependency
//			  for (Iterator it3_de = (dependency_S.getSupplier()).iterator(); it3_de.hasNext();) {
//				textOutput.writeLn(":::::::");
//				try {
//				  Classifier classifier_S = (Classifier) it3_de.next();
//				  //print the name of the supplier of the dependency
//				  textOutput.writeLn (classifier_S.getName());
//				  //Then the program examines whether the name of the Abstraction's supplier is equal to the name of the dependency's supplier
//				  if (classifier_S_ab2 == classifier_S)
//					//wenn the supplier of the abstraction is equal to the supplier of the dependency
//					//then continue,call the self defined method in order to check the first condition
//					//If there is a <<call>> or  <<send>> dependency from an object C to an interface I of an object D
//					// then the first condition is: For any message name n in I, n appears in the tag {secret} in C if and only if it does so in D.
//					bedingen1 = testinterandkeygenerator (dependency_S, classifier_S, abstraction_);
//				} catch (Exception e) {
//				  //textOutput.writeLn(e.);
//				}
//			  } //end for dependency Supplier
//			  //list the stereotypes of the dependecy
//			  for (Iterator iter2_D = dependency_S.getStereotype().iterator(); iter2_D.hasNext();) {
//				Stereotype stereotype_D2 = (Stereotype)iter2_D.next();
//				textOutput.writeLn (stereotype_D2.getName());
//				//examine whether one of them is <<secrecy>>.
//				if ((stereotype_D2.getName()).equals("secrecy"))
//				  bedingen2=true;
//			  }	//end for dependency stereotype
//
//			} // end for dependency
//		  }// end for abstraction supplier
//
//
//		  } // end if realize
//		} // end for abstration stereotype
//	  } // end for  all abstration
//
//	  //When bedingen1&&bedingen2== true, the system has the wanted property.
	  if (bedingen1){


		textOutput.writeLn ("The UML model satisfies the requirement of freshness.");
		dump = true;
	  } else {

		textOutput.writeLn ("The UML model violates the requirement of freshness.");
		dump = false;
	  }
	  for(Iterator its=set_list.iterator();its.hasNext();){
	  logger.trace((String)its.next());
	  }
//
//	  //list the interfaces and the features of the interfaces
//	  textOutput.writeLn ("===============All interfaces");

//	  for (Iterator it_bed2_C2=list_C_ab.iterator(); it_bed2_C2.hasNext();) {
//			TaggedValue tagVa_C_ab = (TaggedValue)it_bed2_C2.next();
//			for (Iterator iter_tagVa_C_Da = (tagVa_C_ab.getDataValue()).iterator(); iter_tagVa_C_Da.hasNext();) {
//			  String tagValue_Da_ab=(String)iter_tagVa_C_Da.next();
//			  //print the tagged value of the client of the abstraction
//			  textOutput.writeLn("TaggedValue (Data) is "+tagValue_Da_ab);
//			  //checks whether the tagged value of it is equals to the feature of the supplier
//			  //und checks whether the tag is equals to "secret"
//			  if (opName.equals(tagValue_Da_ab)&&((tagVa_C_ab.getType()).getName()).equals("secret")) {
//				unterbedingen2=true;
//				textOutput.writeLn ("unterbedingung2 is true");
//			  } else {
//				textOutput.writeLn("unterbedingung2 is false");
//			  }//end if
//			 }
//			}
//		  }
//		}
	  return (dump);

	  }
	  public boolean checkerStringInList(String s , List l){
	  	boolean b = false;
	  	for(Iterator it=l.iterator();it.hasNext();){
	  		String temp=(String)it.next();
	  		if (s.equals(temp)) b = true;
	  	}

	  	return b;
	  }

}
