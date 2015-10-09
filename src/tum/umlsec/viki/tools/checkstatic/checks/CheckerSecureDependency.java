package tum.umlsec.viki.tools.checkstatic.checks;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.omg.uml.behavioralelements.activitygraphs.ActivityGraphsPackage;
import org.omg.uml.foundation.core.Abstraction;
import org.omg.uml.foundation.core.AbstractionClass;
import org.omg.uml.foundation.core.Classifier;
import org.omg.uml.foundation.core.CorePackage;
import org.omg.uml.foundation.core.Dependency;
import org.omg.uml.foundation.core.DependencyClass;
import org.omg.uml.foundation.core.Feature;
import org.omg.uml.foundation.core.ModelElement;
import org.omg.uml.foundation.core.Operation;
import org.omg.uml.foundation.core.OperationClass;
import org.omg.uml.foundation.core.Stereotype;
import org.omg.uml.foundation.core.StereotypeClass;
import org.omg.uml.foundation.core.TagDefinition;
import org.omg.uml.foundation.core.TaggedValue;
import org.omg.uml.foundation.core.UmlClass;
import org.omg.uml.foundation.core.UmlClassClass;

import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.mdr.IMdrContainer;
import tum.umlsec.viki.tools.checkstatic.StaticCheckerBase;

/**
 * @author Shasha Meng
 *
 */

public class CheckerSecureDependency extends StaticCheckerBase {
	private static Logger logger = Logger.getLogger("Secure Dependency");

	boolean bedingen1=true;
	//	   this boolean variable bedingen2 checks the second condition;
	//	   that is: If a message name in I appears in the tag {secret} in C then the dependency is stereotyped <<secrecy>>.
	boolean bedingen2=false;
	//		boolean bedingen3=false;
	//		boolean bedingen=false;
	//	   list all the tagged values of all the clients of the dependency
	ArrayList list_C_all=new ArrayList();
	//	   list all the features of the supplier
	ArrayList list_S = new ArrayList();
	//	   list all the tagged values of the clients of the abstraction
	ArrayList list_C_ab_all=new ArrayList();
	ITextOutput textOutput;
	CorePackage corePackage;


	public boolean check(IMdrContainer _mdrContainer, Iterator _parameters, ITextOutput _textOutput) {
		textOutput = _textOutput;

		//				   global declarations
		bedingen1=true;
		bedingen2=false;
		ArrayList startList = new ArrayList();
		org.omg.uml.UmlPackage root ;

		//CorePackage corePackage ;
		ActivityGraphsPackage activityPackage;

		//				   replacement for the init function
		root = _mdrContainer.getUmlPackage();
		corePackage = root.getCore();
		activityPackage=(ActivityGraphsPackage)root.getActivityGraphs();

		//				   dump
		textOutput.writeLn("===================running CheckerSecureDependency...");

		boolean dump = true;

		// list all classes
		textOutput.writeLn("======= All Classes");
		UmlClassClass umlClassClass = corePackage.getUmlClass();
		for (Iterator it = umlClassClass.refAllOfClass().iterator(); it.hasNext();) {
			// all information about the Class can be accessed through the UmlClass class here
			UmlClass umlClass = (UmlClass) it.next();
			textOutput.writeLn (umlClass.getName());
		}
		// list all dependencies
		//textOutput.writeLn ("======= All Dependencies");
		DependencyClass dependencyClass = corePackage.getDependency();
		for (Iterator it = dependencyClass.refAllOfClass().iterator(); it.hasNext();) {
			// all information about the Dependency can be accessed through the DependencyClass class here
			Dependency dependency = (Dependency) it.next();
			//textOutput.writeLn(dependency.getName());
		}

		// list all the stereotypes
		textOutput.writeLn ("======= All Stereotypes");
		StereotypeClass stereotypeClasses = corePackage.getStereotype();
		for (Iterator iter = stereotypeClasses.refAllOfClass().iterator(); iter.hasNext();) {
			Stereotype stereotype = (Stereotype) iter.next();
			textOutput.writeLn (stereotype.getName());
		}
		// list all of the stereotypes of the dependencies
		textOutput.writeLn ("=======Stereotypes of Dependencies");
		//first list all the dependecies
		DependencyClass dependencyClass_S = corePackage.getDependency();
		for (Iterator iter1 = dependencyClass_S.refAllOfClass().iterator(); iter1.hasNext();) {
			Dependency dependency_S = (Dependency)iter1.next();
			//list all the stereotypes of every dependency
			for (Iterator iter2 =dependency_S.getStereotype().iterator(); iter2.hasNext();) {
				Stereotype stereotype_D = (Stereotype) iter2.next();
				textOutput.writeLn (stereotype_D.getName());
			}
		}
		// list all of the operations
		textOutput.writeLn ("=============All Operations");
		OperationClass operationClasses=corePackage.getOperation();
		for (Iterator iterOp = operationClasses.refAllOfClass().iterator(); iterOp.hasNext();) {
			Operation operation = (Operation)iterOp.next();
			textOutput.writeLn ("The operation "+operation.getName()+" is in the class "+operation.getOwner().getName());
			//textOutput.writeLn (operation.getOwner().getName());
		}
		//here begins the test.
		textOutput.writeLn ("==============Stereotypes of Abstractions");
		//list all the abstractions in the diagram.
		AbstractionClass abstractionClasses = corePackage.getAbstraction();
		for (Iterator iter_abstra = abstractionClasses.refAllOfClass().iterator(); iter_abstra.hasNext();) {
			Abstraction abstraction_ = (Abstraction) iter_abstra.next();
			//print the name of the abstraction
			//textOutput.writeLn (abstraction_.getName());
			//It reads the stereotype von Abstraction
			for (Iterator iter_Stere = (abstraction_.getStereotype()).iterator(); iter_Stere.hasNext();) {
				Stereotype stere = (Stereotype) iter_Stere.next();
				String stere_name = stere.getName();
				if (stere_name!=null)
					//print the stereotype of the abstaction
				{textOutput.writeLn ("The stereotype of abstraction is "+stere_name);}
				//when the stereotype of the abstraction is equals "realize", then continue
				if (stere.getName().equals("realize")) {
					//list the supplier of the abstraction
					for (Iterator it2_ab2 = (abstraction_.getSupplier()).iterator(); it2_ab2.hasNext();) {
						textOutput.writeLn("====== Supplier of Abstraction");
						Classifier classifier_S_ab2=(Classifier)it2_ab2.next();
						textOutput.writeLn(classifier_S_ab2.getName());


						//list the all of the dependencies
						for (Iterator it1_de = dependencyClass.refAllOfClass().iterator(); it1_de.hasNext();) {
							Dependency dependency_S = (Dependency) it1_de.next();
							textOutput.writeLn("=======Supplier of Dependency");
							//list the supplier of every dependency
							for (Iterator it3_de = (dependency_S.getSupplier()).iterator(); it3_de.hasNext();) {
								//textOutput.writeLn(":::::::");
								try {
									Classifier classifier_S = (Classifier) it3_de.next();
									//print the name of the supplier of the dependency
									if (!classifier_S.getName().startsWith("org.omg.uml.foundation.core")) {
										textOutput.writeLn (classifier_S.getName());
									}
									//Then the program examines whether the name of the Abstraction��s supplier is equal to the name of the dependency��s supplier
									if (classifier_S_ab2 == classifier_S) {
										logger.info("Checking condition 1 for : Dependency : " + dependency_S.getName() + "\t Classifier : " + classifier_S.getName() + "\t Abstraction : " + abstraction_.getName());
										//wenn the supplier of the abstraction is equal to the supplier of the dependency
										//then continue,call the self defined method in order to check the first condition
										//If there is a <<call>> or  <<send>> dependency from an object C to an interface I of an object D
										// then the first condition is: For any message name n in I, n appears in the tag {secret} in C if and only if it does so in D.
										bedingen1 = checkTaggedValues(dependency_S, classifier_S, abstraction_);
									}
								} catch (Exception e) {
									//textOutput.writeLn(e.getMessage());
									logger.error(e.getMessage());
								}
							} //end for dependency Supplier
							//list the stereotypes of the dependecy
							for (Iterator iter2_D = dependency_S.getStereotype().iterator(); iter2_D.hasNext();)
							{
								Stereotype stereotype_D2 = (Stereotype)iter2_D.next();
								if (!stereotype_D2.getName().startsWith("org.omg.uml.foundation.core"))
								{textOutput.writeLn ("The stereotype of the dependency between the supplier and the client is "+stereotype_D2.getName());}


								//examine whether one of them is <<secrecy>>.
								if ((stereotype_D2.getName()).equals("secrecy")) {
									bedingen2=true;
									textOutput.writeLn("bedingen2 is now true");
								}
							}	//end for dependency stereotype
							if (bedingen2) {
								textOutput.writeLn("bedingen2 true");
							}
							if (!bedingen2) {
								Stereotype secrecy = corePackage.getStereotype().createStereotype();
								secrecy.setName("Secrecy");
								dependency_S.getStereotype().add(secrecy);
								textOutput.writeLn("The model has been corrected");
							}

						} // end for dependency
					}// end for abstraction supplier


				} // end if realize
			} // end for abstration stereotype
		} // end for  all abstration

		//When bedingen1&&bedingen2== true, the system has the wanted property.
		if (bedingen1&&bedingen2){

			textOutput.writeLn ("The UML model satisfies the requirement of the stereotype secure dependency.");
			dump = true;
		} else {

			textOutput.writeLn ("The UML model violates the requirement of the stereotype secure dependency.");
			dump = false;
		}
		return dump;
	}

	/**
	 * Checks the condition 1 : for every operation in Interface I, the operation appears in the Class C's {secret} tag
	 * if and only if it appears in the Class D's {secret} tag.
	 * @param dependency the dependency leading to the Class C
	 * @param classifier the Interface
	 * @param abstraction the abstraction leading to Class D
	 * @return true if condition 1 is met, false otherwise
	 */
	private boolean checkTaggedValues(Dependency dependency, Classifier classifier, Abstraction abstraction) {
		List<List<TaggedValue>> taggedValDep = getTaggedValList(dependency);
		List<List<TaggedValue>> taggedValAbs = getTaggedValList(abstraction);
		List<String> operations = getOperations(classifier);
		
		for (Iterator<String> itOps = operations.iterator(); itOps.hasNext(); ) {
			String op = itOps.next();
			logger.trace("Looking for operation " + op + " in the dependency client");
			boolean checkOne = checkSecretTag(op + "()", taggedValDep);
			logger.trace("Looking for operation " + op + " in the abstraction client");
			boolean checkTwo = checkSecretTag(op + "()", taggedValAbs);
			if ((checkOne && checkTwo) || (!checkOne && !checkTwo)) {
				textOutput.writeLn("Condition 1 is fulfilled");
				return true;
			} else {
				textOutput.writeLn("Condition 1 is not fulfilled : the operation " + op + " is in one on the secret tagged values but not in the other one");
				if (!checkOne)
					addOpToSecretTag(op + "()", dependency);
				else
					addOpToSecretTag(op + "()", abstraction);
				textOutput.writeLn("The model has been fixed regarding condition 1");
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Adds an operation to the secret tag of the client side of a dependency. If there's not secret tag yet, it's created.
	 * @param operation the operation to add to the {secret} tag
	 * @param dependency the dependency
	 */
	private void addOpToSecretTag(String operation, Dependency dependency) {
		boolean added = false;
		for (Iterator<ModelElement> iter = dependency.getClient().iterator(); iter.hasNext(); ) {
			ModelElement element = iter.next();
			for (Iterator<TaggedValue> itTval = element.getTaggedValue().iterator(); iter.hasNext(); ) {
				TaggedValue tval = itTval.next();
				if (tval.getType().getName().equals("secret")) {
					tval.getDataValue().add(operation);
					added = true;
				}
			}
			if (!added) {
				TaggedValue tval = corePackage.getTaggedValue().createTaggedValue();
				TagDefinition tdef = corePackage.getTagDefinition().createTagDefinition();
				tdef.setName("secret");
				tval.setType(tdef);
				tval.getDataValue().add(operation);
				element.getTaggedValue().add(tval);
			}
		}
	}
	
	/**
	 * Checks if an operation name is part of the secret tag in a list of (lists of) tagged values
	 * @param operation the name of the considered operation
	 * @param list the first list of lists of tagged values
	 * @return true if the operation is the list's secret tag
	 */
	private boolean checkSecretTag(String operation, List<List<TaggedValue>> list) {
		for (Iterator<List<TaggedValue>> itList = list.iterator(); itList.hasNext(); ) {
			List<TaggedValue> subList = itList.next();
			for (Iterator<TaggedValue> itSubList = subList.iterator(); itSubList.hasNext(); ) {
				TaggedValue tval = itSubList.next();
				if (tval.getType().getName().equals("secret")) {
					for (Iterator<String> itValues = tval.getDataValue().iterator(); itValues.hasNext(); ) {
						String val = itValues.next();
						if (val.equals(operation)) {
							logger.debug("found operation " + operation + " in a secret tag");
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * Get a list (of lists) of tagged values from a dependency's client
	 * @param element the dependency
	 * @return a list of lists of TaggedValues
	 */
	private List<List<TaggedValue>> getTaggedValList(Dependency element) {
		List<List<TaggedValue>> list = new ArrayList<List<TaggedValue>>();
		// If the element is a Dependency
		for (Iterator<ModelElement> iter = ((Dependency)element).getClient().iterator(); iter.hasNext(); ) {
			ModelElement modelElt = iter.next();
			List<TaggedValue> list_elt = new ArrayList<TaggedValue>();
			for (Iterator<TaggedValue> iter2 = modelElt.getTaggedValue().iterator(); iter2.hasNext(); ) {
				TaggedValue taggedVal = iter2.next();
				if (taggedVal != null)
					list_elt.add(taggedVal);
			}
			if (!list_elt.isEmpty())
				list.add(list_elt);
		}
		return list;
	}
	
	/**
	 * Get the operations of a classifier
	 * @param classifier the classifier from which we extract the operations
	 * @return a List of Strings that represents the operations (without their parameters)
	 */
	private List<String> getOperations(Classifier classifier) {
		List<String> list = new ArrayList<String>();
		
		for (Iterator<Feature> iter = classifier.getFeature().iterator(); iter.hasNext(); ) {
			Feature feat = iter.next();
			String name = feat.getName();
			if (name != null) {
				list.add(name);
				textOutput.writeLn("The feature of the supplier of the dependency is " + name + "()");
			}
		}
		return list;
	}
}
