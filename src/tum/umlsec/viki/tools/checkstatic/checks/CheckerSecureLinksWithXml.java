package tum.umlsec.viki.tools.checkstatic.checks;

//import tum.umlsec.viki.framework.mdr.IMdrWrapper;
//import tum.umlsec.viki.tools.checkstatic.IWriteText;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.omg.uml.behavioralelements.commonbehavior.CommonBehaviorPackage;
import org.omg.uml.behavioralelements.commonbehavior.ComponentInstance;
import org.omg.uml.behavioralelements.commonbehavior.Link;
import org.omg.uml.behavioralelements.commonbehavior.LinkClass;
import org.omg.uml.behavioralelements.commonbehavior.LinkEnd;
import org.omg.uml.behavioralelements.commonbehavior.NodeInstance;
import org.omg.uml.foundation.core.CorePackage;
import org.omg.uml.foundation.core.Dependency;
import org.omg.uml.foundation.core.DependencyClass;
import org.omg.uml.foundation.core.Stereotype;

import tum.umlsec.viki.framework.ExceptionProgrammLogicError;
import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.mdr.IMdrContainer;
import tum.umlsec.viki.framework.toolbase.CommandParameterDescriptor;
import tum.umlsec.viki.tools.checkstatic.StaticCheckerBase;
import tum.umlsec.viki.tools.checkstatic.ToolCheckStatic;


/**
 * @author shasha
 *
 */
public class CheckerSecureLinksWithXml extends StaticCheckerBase
{

	/* (non-Javadoc)
	 * @see tum.umlsec.viki.tools.checkstatic.StaticCheckerBase#check(tum.umlsec.viki.framework.mdr.IMdrWrapper)
	 */
	public boolean check(IMdrContainer _mdrContainer, Iterator _parameters, ITextOutput _textOutput) {
		ITextOutput textOutput = _textOutput;

		// xml parsen und initialisieren:
		File _xmlFile = new File("");
		boolean _foundFile = false;

		for (; _parameters.hasNext();) {
			//textOutput.writeLn("==========================_parameter ist eingelesen...");
			CommandParameterDescriptor _parameter = (CommandParameterDescriptor) _parameters.next();
			if(_parameter.getId() == ToolCheckStatic.CPID_FILE) {
				//textOutput.writeLn("==========================_parameter getId...");
				_xmlFile = _parameter.getAsFile();
				textOutput.writeLn("==========================XML file name :" + _xmlFile.getName());

				_foundFile = true;
			}

		}
		if(_foundFile == false) {
			throw new ExceptionProgrammLogicError("Required parameter missing");
		}

		//String url = _xmlFile.getAbsolutePath();
		XMLDOMParser xmldomparser = new XMLDOMParser(textOutput);
		HashMap hashMap = xmldomparser.loadXMLFile(_xmlFile);
		textOutput.writeLn("Here are all key/value pairs in the map.");
		textOutput.writeLn("map "+hashMap);
		String attacker_Name = xmldomparser.getAttackerName();

		textOutput.writeLn("The type of the attacker is " + attacker_Name);


		org.omg.uml.UmlPackage root ;

		CorePackage corePackage ;

		// replacement for the init function
		root = _mdrContainer.getUmlPackage();
		corePackage = root.getCore();

		boolean dump = true;
		CommonBehaviorPackage commonPackage= root.getCommonBehavior();
		textOutput.writeLn("\n\n=============================================== running CheckerSecureLinksWithXml...");
		// list all dependencies
		textOutput.writeLn("======= Dependencies, Component Instances, Node Instances and Communication Links");
		DependencyClass dependencyClass = corePackage.getDependency();
		for(Iterator iter1 = dependencyClass.refAllOfClass().iterator(); iter1.hasNext();)
		{
			Dependency dependency = (Dependency)iter1.next();
			String dependencyName = dependency.getName();
			textOutput.writeLn("The name of the dependency is " + dependencyName);

			for(Iterator it8 = (dependency.getSupplier()).iterator(); it8.hasNext();){
				ComponentInstance supplierComponent_1 = (ComponentInstance)it8.next();
				String supplierName_1 = supplierComponent_1.getName();
				NodeInstance nodeInstance_S1 = supplierComponent_1.getNodeInstance();
				String nodeName_sresident1 = nodeInstance_S1.getName();
				textOutput.writeLn("The name of the supplier component instance of the dependency is " + supplierName_1 + " and it is resident in the node instance " + nodeName_sresident1);
			}
			for(Iterator it2 = (dependency.getClient()).iterator(); it2.hasNext();)
			{
				ComponentInstance clientComponent = (ComponentInstance)it2.next();
				String clientName = clientComponent.getName();
				NodeInstance nodeInstance_C = clientComponent.getNodeInstance();
				String nodeName_cresident = nodeInstance_C.getName();
				textOutput.writeLn("The name of the client component instance of the dependency is " + clientName + " and it is resident in the node instance " + nodeName_cresident);
				for (Iterator it4 =(nodeInstance_C.getLinkEnd()).iterator(); it4.hasNext();){
					LinkEnd linkEnd_C = (LinkEnd)it4.next();
					Link link_C = linkEnd_C.getLink();
					String link_CName = link_C.getName();
					//textOutput.writeLn("Hello2, i am here by the name of link.");
					for(Iterator it5 = (dependency.getSupplier()).iterator(); it5.hasNext();){
						ComponentInstance supplierComponent = (ComponentInstance)it5.next();
						String supplierName = supplierComponent.getName();
						NodeInstance nodeInstance_S = supplierComponent.getNodeInstance();
						String nodeName_sresident = nodeInstance_S.getName();
						//textOutput.writeLn("Hello3, I am here: The name of the supplier component instance of the dependency is " + supplierName + " and it is resident in the node instance " + nodeName_sresident);
						for (Iterator it6 =(nodeInstance_S.getLinkEnd()).iterator(); it6.hasNext();){
							LinkEnd linkEnd_S = (LinkEnd) it6.next();
							Link link_S = linkEnd_S.getLink();
							String link_SName = link_S.getName();
							if (link_CName.equals(link_SName)){
								textOutput.writeLn("The name of the communication link is " + link_SName);
								for(Iterator it7 = link_S.getStereotype().iterator();it7.hasNext();){
									Stereotype stereotype_Link = (Stereotype)it7.next();
									String stereotypeName_link = stereotype_Link.getName();
									textOutput.writeLn("The stereotype of the communication link of the dependency with the name "+dependencyName+" is " + stereotypeName_link);
								}
							}

						}
					}
				}
			}



			//z_D++;
		}


		// list all the stereotypes of the links
		//textOutput.writeLn("=======Communication links");
		LinkClass linkClass = commonPackage.getLink();
		for(Iterator iter3 = linkClass.refAllOfClass().iterator(); iter3.hasNext();)
		{
			Link link = (Link)iter3.next();
			String linkName = link.getName();
			// textOutput.writeLn("The name of the communication link is " + linkName);
			for(Iterator iter4 = link.getStereotype().iterator();iter4.hasNext();)
			{
				Stereotype stereotype_L = (Stereotype)iter4.next();
				String stereotypeName_link = stereotype_L.getName();
				//textOutput.writeLn("The stereotype of the communication link is " + stereotypeName_link);
			}

			//z_L++;
		}

		textOutput.writeLn("=======Here begins the verification");
		DependencyClass dependencyClass_Test = corePackage.getDependency();
		//if (z_D!=0&&z_L!=0){
		for(Iterator iter1 = dependencyClass_Test.refAllOfClass().iterator(); iter1.hasNext();){
			Dependency dependency = (Dependency)iter1.next();
			String dependencyName = dependency.getName();
			textOutput.writeLn("The name of the dependency is " + dependencyName);

			for(Iterator it2 = (dependency.getClient()).iterator(); it2.hasNext();){
				ComponentInstance clientComponent = (ComponentInstance)it2.next();
				String clientName = clientComponent.getName();
				NodeInstance nodeInstance_C = clientComponent.getNodeInstance();
				String nodeName_cresident = nodeInstance_C.getName();
				//textOutput.writeLn("Hello1, I am here: The name of the client component instance of the dependency is " + clientName + " and it is resident in the node instance " + nodeName_cresident);
				for (Iterator it4 =(nodeInstance_C.getLinkEnd()).iterator(); it4.hasNext();){
					LinkEnd linkEnd_C = (LinkEnd)it4.next();
					Link link_C = linkEnd_C.getLink();
					String link_CName = link_C.getName();
					//textOutput.writeLn("Hello2, i am here by the name of link.");
					for(Iterator it5 = (dependency.getSupplier()).iterator(); it5.hasNext();){
						ComponentInstance supplierComponent = (ComponentInstance)it5.next();
						String supplierName = supplierComponent.getName();
						NodeInstance nodeInstance_S = supplierComponent.getNodeInstance();
						String nodeName_sresident = nodeInstance_S.getName();
						//textOutput.writeLn("Hello3, I am here: The name of the supplier component instance of the dependency is " + supplierName + " and it is resident in the node instance " + nodeName_sresident);
						for (Iterator it6 =(nodeInstance_S.getLinkEnd()).iterator(); it6.hasNext();){
							LinkEnd linkEnd_S = (LinkEnd) it6.next();
							Link link_S = linkEnd_S.getLink();
							String link_SName = link_S.getName();
							if (link_CName.equals(link_SName)){
								//textOutput.writeLn("Hello4, I am here: The name of the communication link is " + link_SName);
								for(Iterator it7 = link_S.getStereotype().iterator();it7.hasNext();){
									Stereotype stereotype_Link = (Stereotype)it7.next();
									String stereotypeName_link = stereotype_Link.getName();
									textOutput.writeLn("The stereotype of the communication link of the dependency "+dependencyName+" is " + stereotypeName_link);
									try{
										//Object stereotypeName_dependency = null;

										for(Iterator iter2 = dependency.getStereotype().iterator();iter2.hasNext();){
											Stereotype stereotype_D = (Stereotype)iter2.next();
											String stereotypeName_dependency = stereotype_D.getName();
											textOutput.writeLn("The stereotype of the dependency is: " + stereotypeName_dependency);

											Threaten threaten = new Threaten();
											Vector v1 = threaten.threaten(new Attacker(attacker_Name), new Stereotype_Link(stereotypeName_link), hashMap);
											Vector vEnc = threaten.threaten(new Attacker(attacker_Name), new Stereotype_Link("Encrypted"), hashMap);

											if (stereotypeName_dependency.equals("secrecy")){
												if (v1.contains("read") && !vEnc.contains("read")) {
													// insert <<encrypted>> stereotype here.
													textOutput.writeLn("The UML model violates the requirement of the stereotype secure links, but it has been fixed.");
													stereotype_D.setName("Encrypted");
												}
												else if (v1.contains("read")){
													textOutput.writeLn("The UML model violates the requirement of the stereotype secure links.");
													dump = false;
												}
												else{
													textOutput.writeLn("The UML model satisfies the requirement of the stereotype secure links.");
												}
											}

											if (stereotypeName_dependency.equals("integrity")){
												if (v1.contains("insert") && !vEnc.contains("insert")) {
													// insert <<encrypted>> stereotype here.
													textOutput.writeLn("The UML model violates the requirement of the stereotype secure links, but it has been fixed.");
													stereotype_D.setName("Encrypted");
												}
												else if (v1.contains("insert")){
													textOutput.writeLn("The UML model violates the requirement of the stereotype secure links.");
													dump = false;
												}
												else{textOutput.writeLn("The UML model satisfies the requirement of the stereotype secure links.");}
											}
											if (stereotypeName_dependency.equals("high")){
												if (v1.isEmpty()){
													textOutput.writeLn("The UML model satisfies the requirement of the stereotype secure links.");
												}
												else if (vEnc.isEmpty()) {
													// insert <<encrypted>> stereotype here.
													textOutput.writeLn("The UML model violates the requirement of the stereotype secure links, but it has been fixed.");
													stereotype_D.setName("Encrypted");
												}
												else{
													textOutput.writeLn("The UML model violates the requirement of the stereotype secure links.");
													dump = false;
												}
											}
											if (!stereotypeName_dependency.equals("secrecy")&&!stereotypeName_dependency.equals("integrity")&&!stereotypeName_dependency.equals("high"))
											{textOutput.writeLn("The UML model satisfies the requirement of the stereotype secure links.");}




										}
									}//end of try

									catch (Exception e){textOutput.writeLn(e.toString());} //end of catch

								}
							}

						}
					}
				}
			}
		} // end for
		return dump;
	}
}
