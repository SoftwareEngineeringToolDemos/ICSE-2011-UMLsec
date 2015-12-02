package tum.umlsec.viki.tools.permission.permission;

import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.omg.uml.behavioralelements.collaborations.ClassifierRole;
import org.omg.uml.behavioralelements.collaborations.ClassifierRoleClass;
import org.omg.uml.behavioralelements.collaborations.CollaborationsPackage;
import org.omg.uml.behavioralelements.collaborations.Interaction;
import org.omg.uml.behavioralelements.collaborations.InteractionClass;
import org.omg.uml.behavioralelements.commonbehavior.CallAction;
import org.omg.uml.foundation.core.Classifier;
import org.omg.uml.foundation.core.CorePackage;
import org.omg.uml.foundation.core.Generalization;
import org.omg.uml.foundation.core.Operation;
import org.omg.uml.foundation.core.Stereotype;
import org.omg.uml.foundation.core.TaggedValue;
import org.omg.uml.foundation.core.UmlClass;

import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.mdr.IMdrContainer;
import tum.umlsec.viki.tools.checkstatic.StaticCheckerBase;
import tum.umlsec.viki.tools.permission.common.ClassDelegation;
import tum.umlsec.viki.tools.permission.common.ClassPermission;
import tum.umlsec.viki.tools.permission.common.StaticParser;
import tum.umlsec.viki.tools.permission.common.StaticParserException;

/**
 * @author Markus
 *
 */
public class PermissionChecker extends StaticCheckerBase 
{
	private Hashtable mdrObjects  = new Hashtable();
	private HashSet seqMessages = new HashSet();
	private static Logger logger = Logger.getLogger("PermissionChecker");


	public boolean check(IMdrContainer _mdrContainer, Iterator _parameter, ITextOutput _textOutput) 
	{

		ITextOutput textOutput       = _textOutput;
		Hashtable   objectClasses    = new Hashtable();
		Hashtable   objectClassNames = new Hashtable();
		Hashtable   classObjects     = new Hashtable();

		org.omg.uml.UmlPackage root ;

		CorePackage corePackage ;

		// replacement for the init function
		root = _mdrContainer.getUmlPackage();

		corePackage = root.getCore();

		boolean dump = true;

		CollaborationsPackage collab = root.getCollaborations();
		textOutput.writeLn("=======   checking sequence diagrams  ======= ");				
		textOutput.writeLn("======= searching classes for objects ======= ");

		/* 
		 *  first run:
		 *  search objects in sequence diagrams and get the appropriate classes
		 *  for each class there will parsed 
		 *   -permission
		 *   -delegation
		 */

		ClassifierRoleClass classifierRoleClass = collab.getClassifierRole();
		for(Iterator<ClassifierRole> it = classifierRoleClass.refAllOfClass().iterator(); it.hasNext(); ) {                        
			ClassifierRole cRole = it.next();    

			textOutput.writeLn("checking object: " + cRole.getName() );

			/* 
			 * find Class defining the Object
			 */
			//Collection inst = cRole.getConformingInstance();
			Collection inst = cRole.getBase();
			//Collection inst = object.getClassifier();
			for(Iterator<UmlClass> iter = inst.iterator(); iter.hasNext();) {

				UmlClass temp= iter.next();
				logger.trace("Base class : " + temp.getName());
				textOutput.writeLn("         class: " + temp.getName());

				if ( !classObjects.containsKey(temp) ) {
					/* 
					 * new class definition found
					 */

					SecClassRep secClass = 
						new SecClassRep( temp.getName(), temp, textOutput );

					/* 
					 * find superclasses
					 */                    
					SecClassRep superClass = superclass(temp, textOutput, corePackage );
					if (superClass != null) {
						superClass.compareClass(secClass);
					}

					for (Iterator<Operation> it_op = corePackage.getOperation().refAllOfClass().iterator(); it_op.hasNext();) {
						/* 
						 * searching for methods
						 */

						Operation operation = it_op.next();
						if (operation.getOwner().equals(temp)) {
							secClass.setOperation(operation);
						}

					}                    



					// ref MDRclass -> secClass
					classObjects.put( temp, secClass);

					// ref MDRobject -> secClass
					objectClasses.put(cRole, secClass);
					objectClassNames.put(cRole.getName().toUpperCase(), secClass);
					//objectClasses.put(object, secClass);                           
					//objectClassNames.put(object.getName().toUpperCase(), secClass);                    
				}
				else {
					SecClassRep secClass = (SecClassRep) classObjects.get(temp);

					// ref MDRobject -> secClass
					objectClasses.put(cRole, secClass);
					objectClassNames.put(cRole.getName().toUpperCase(), secClass);
					//objectClasses.put(object, secClass);                           
					//objectClassNames.put(object.getName().toUpperCase(), secClass);                    
				}
			}
		}

		/* 
		 *  second run:
		 *  the objectdefinition will be compared to the classdefinition
		 */
		textOutput.writeLn("======= checking integrety of objects ======= ");
		textOutput.writeLn("=======     checking permissions      ======= ");

		classifierRoleClass = collab.getClassifierRole();
		logger.trace("checking permissions : " + classifierRoleClass.refAllOfClass().size() + " Collaboration Roles");
		//ObjectClass objectClass = commonPackage.getObject();
		for(Iterator<ClassifierRole> it = classifierRoleClass.refAllOfClass().iterator(); it.hasNext(); ) {                        
			ClassifierRole cRole = it.next();    
			logger.trace("ClassifierRole : " + cRole.getName());
			/* 
			 * find stereotype "permission" of the object ....
			 */
			Collection collStereo = cRole.getStereotype();
			logger.trace("This role has " + collStereo.size() + " stereotypes");
			boolean bPermissionObject = false;
			for(Iterator<Stereotype> iter = collStereo.iterator(); iter.hasNext(); ) {                        
				Stereotype stereo = iter.next();
				logger.trace("Stereotype : " + stereo.getName());
				if ( stereo.getName().equalsIgnoreCase("permission-secured") ) {
					bPermissionObject = true;
					break;
				}
			}

			/* 
			 * ... and if found search for permissions
			 */
			if (bPermissionObject) {
				Collection collTags = cRole.getTaggedValue();
				for(Iterator<TaggedValue> iter = collTags.iterator(); iter.hasNext();) {
					TaggedValue tagVal  = iter.next();
					if ( tagVal.getType().getName().equalsIgnoreCase("permission-secured") ) {
						for(Iterator<String> tagValueStr = tagVal.getDataValue().iterator(); tagValueStr.hasNext();) {
							String      strValue = tagValueStr.next(); 
							try {
								ClassPermission clsPerm   = StaticParser.parsePermission( strValue );
								SecClassRep     secClass  = (SecClassRep) objectClasses.get(cRole);
								SecClassRep     permClass = (SecClassRep) objectClassNames.get(clsPerm.getOwner().toUpperCase());

								if (permClass != null && secClass != null) {
									if (!secClass.hasPermission( permClass.getClassName(), clsPerm.getPermission() ) ) {
										textOutput.write(" ERROR: permission "      + clsPerm.getPermission()   );
										textOutput.write(" on object "              + clsPerm.getOwner()        );                                    
										textOutput.write(" of class "               + permClass.getClassName()  );
										textOutput.write(" not defined in class "   + secClass.getClassName()   );
										textOutput.writeLn("");

										strValue = "NOT DEFINED IN CLASS" + secClass.getClassName() + ": " + strValue;
										dump = false;
									}
								} 
								else {
									textOutput.write(" ERROR: class of object "    + cRole.getName()  );
									textOutput.write(" not defined classdiagram!"                      );
									textOutput.writeLn("");
									strValue = "NOT PARSEABLE: " + strValue;
									dump = false;
								}
							}
							catch (StaticParserException spe) {
								textOutput.writeLn("ERROR: Tag for permission not parseable (object " + cRole.getName() + ")");
								strValue = "NOT PARSEABLE: " + strValue;
								dump = false;

							}                    
						}
					}

					if ( tagVal.getType().getName().equalsIgnoreCase("delegation") ) {
						for(Iterator<String> tagValueStr = tagVal.getDataValue().iterator(); tagValueStr.hasNext();) {
							String      strValue = tagValueStr.next(); 
							try {
								ClassDelegation clsDeleg  = StaticParser.parseDelegation( strValue );
								SecClassRep     secClass  = (SecClassRep) objectClasses.get(cRole);
								SecClassRep     permClass = (SecClassRep) objectClassNames.get(clsDeleg.getObject().toUpperCase());

								if (permClass != null && secClass != null) {
									if (! secClass.hasDelegation( permClass.getClassName(), clsDeleg.getPermission(), clsDeleg.getRoles()) ) {
										// new 
										textOutput.write( " ERROR: permission "     + clsDeleg.getPermission()  );
										textOutput.write(" on object "              + clsDeleg.getObject()      );                                    
										textOutput.write(" of class "               + permClass.getClassName()  );
										textOutput.write(" not defined for delegation in class " + secClass.getClassName());
										textOutput.writeLn("");
										strValue = "NOT DEFINED IN CLASS" + secClass.getClassName() + ": " + strValue;
										dump = false;
									}
								}
								else {
									textOutput.writeLn("ERROR: Tag for delegation not parseable (object " + cRole.getName() + ")");
									strValue = "NOT PARSEABLE: " + strValue;
									dump = false;
								}
							}
							catch (StaticParserException spe) {
								textOutput.writeLn("ERROR: Tag for delegation not parseable (object " + cRole.getName() + ")");
								strValue = "NOT PARSEABLE: " + strValue;
								dump = false;

							}                    
						}
					}
				}
			}
		}
		/* 
		 *  third run:
		 *  search for stimuli in seqencediagrams and compare definitions to the
		 *  ones in the classdiagrams.
		 *
		 */
		textOutput.writeLn("=======     checking methodcalls      ======= ");

		InteractionClass interactionClass = collab.getInteraction();
		for(Iterator<Interaction> it = interactionClass.refAllOfClass().iterator(); it.hasNext(); ) {
			Interaction inter = it.next();
			for (Iterator<org.omg.uml.behavioralelements.collaborations.Message> itMsg = inter.getMessage().iterator(); itMsg.hasNext(); ) {
				org.omg.uml.behavioralelements.collaborations.Message msg = itMsg.next();
				Collection stTypes = msg.getStereotype();
				for(Iterator<Stereotype> iter = stTypes.iterator(); iter.hasNext();) {                      
					Stereotype stereo = iter.next();

					if ( stereo.getName().equalsIgnoreCase("permission_needed") ) {
						try {
							ClassifierRole sender        = msg.getSender();
							ClassifierRole receiver      = msg.getReceiver();        
							CallAction action      = (CallAction) msg.getAction();
							Operation op           = action.getOperation();
							HashSet hsPermissions  = new HashSet();

							/*
							 * find appropriate classdefinition
							 */
							SecClassRep recClass = null;
							for(Iterator<Classifier> iter1 = receiver.getBase().iterator(); iter1.hasNext();) {                        
								Classifier temp      = iter1.next();
								recClass = (SecClassRep) classObjects.get(temp);
								break;
							}

							/*
							 * premission-tag definitions
							 */
							if (recClass != null) {

								for(Iterator<TaggedValue> iter1 = msg.getTaggedValue().iterator(); iter1.hasNext(); ) {                        
									TaggedValue tagVal  = iter1.next();
									if ( tagVal.getType().getName().equalsIgnoreCase("permission-secured") ) {
										for(Iterator<String> tagValueStr = tagVal.getDataValue().iterator(); tagValueStr.hasNext(); ) {
											String strValue = tagValueStr.next(); 

											/*
											 * find permission in classdefinition
											 */
											if ( recClass.hasOpPermDef(op.getName(), strValue) ) {
												hsPermissions.add(strValue);
											}
											else {
												textOutput.writeLn(" ERROR: Permission '" + strValue+ "' of message '" + msg.getName() + "' is not defined in classdefiniton");
												strValue ="NOT DEFINED IN CLASS:" + strValue;
												dump = false;
											}
										}
									}
								}
							}
							if (!recClass.hasOpAllPermissions(op.getName(), hsPermissions)) {
								textOutput.writeLn(" ERROR: there is at least one more permission more defined in classdiagram (message '" + msg.getName() + "')");
								msg.setName("NOT ALL PERMISSIONS DEFINED:" + msg.getName());
								dump = false;
							}
						}
						catch (ClassCastException cce) {
							/* 
							 * Stimulus is not a CallAction => ignore this Stimulus
							 */
						}
					}
				}
			}
		}

		return dump;				
	}


	private SecClassRep superclass(UmlClass umlClass, ITextOutput textOutput, CorePackage corePackage ) {
		/* 
		 * find and parse the superclass of umlClass
		 */
		SecClassRep superClass = null;
		Collection<Generalization> inst = umlClass.getGeneralization() ;
		for(Iterator<Generalization> iter = inst.iterator(); iter.hasNext();) {                        
			Generalization gen      = iter.next();
			UmlClass       genClass = (UmlClass)       gen.getParent();   

			textOutput.writeLn("    superclass: " + genClass.getName());

			superClass = new SecClassRep( genClass.getName(), genClass, textOutput ); 

			for (Iterator<Operation> it_op = corePackage.getOperation().refAllOfClass().iterator(); it_op.hasNext();) {
				/* 
				 * searching for methods
				 */

				Operation operation = it_op.next();
				if (operation.getOwner().equals(genClass)) {
					superClass.setOperation(operation);
				}
			}                    

			/* 
			 *find next superclass
			 */

			SecClassRep superSuperClass = superclass(genClass, textOutput, corePackage);
			if (superSuperClass != null) {
				superSuperClass.compareClass(superClass);
			}
		}
		return superClass;
	}
}
