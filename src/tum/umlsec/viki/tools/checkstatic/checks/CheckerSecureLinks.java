package tum.umlsec.viki.tools.checkstatic.checks;

//import tum.umlsec.viki.framework.mdr.IMdrWrapper;
//import tum.umlsec.viki.tools.checkstatic.IWriteText;


import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.Logger;
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

import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.mdr.IMdrContainer;
import tum.umlsec.viki.framework.mdr.IdNameList;
import tum.umlsec.viki.tools.checkstatic.StaticCheckerBase;


/**
 * @author Shasha Meng
 *
 */
public class CheckerSecureLinks extends StaticCheckerBase
{
	static Logger logger = Logger.getLogger("SecureLinks");
	int z_D=0;
	int z_L=0;

	/* (non-Javadoc)
	 * @see
tum.umlsec.viki.tools.checkstatic.StaticCheckerBase#check(tum.umlsec.viki.framework.mdr.IMdrWrapper)
	 */
	public boolean check(IMdrContainer _mdrContainer, Iterator _parameters, ITextOutput _textOutput) {
		ITextOutput textOutput = _textOutput;
		logger.info("********** Starting Secure Links check **********");

		org.omg.uml.UmlPackage root ;

		CorePackage corePackage ;

		// replacement for the init function
		root = _mdrContainer.getUmlPackage();
		corePackage = root.getCore();

		boolean dump = true;
		boolean changed = false;
		CommonBehaviorPackage commonPackage= root.getCommonBehavior();
		

		textOutput.writeLn("=============================running CheckerSecureLinks...");

		// list all dependencies
		textOutput.writeLn("======= Dependencies, Component Instances, Node Instances and Communication Links");
		DependencyClass dependencyClass = corePackage.getDependency();
		for(Iterator iter1 = dependencyClass.refAllOfClass().iterator(); iter1.hasNext();)
		{
			Dependency dependency = (Dependency)iter1.next();
			String dependencyName = dependency.getName();
			if (dependencyName != null){
				textOutput.writeLn("The name of the dependency is " + dependencyName);
			}

			for(Iterator it8 = (dependency.getSupplier()).iterator(); it8.hasNext();){
				Object o = (Object)it8.next();
				if (o instanceof ComponentInstance){
					ComponentInstance supplierComponent_1 = (ComponentInstance)o; 
					String supplierName_1 = supplierComponent_1.getName();
					NodeInstance nodeInstance_S1 = supplierComponent_1.getNodeInstance();
					String nodeName_sresident1 = nodeInstance_S1.getName();
					textOutput.writeLn("The name of the supplier component instance of the dependency is " + supplierName_1 + " and it is resident in the node instance " + nodeName_sresident1);
				}
			}
			for(Iterator it2 = (dependency.getClient()).iterator(); it2.hasNext();)
			{
				Object o = (Object)it2.next();
				if (o instanceof ComponentInstance){
					ComponentInstance clientComponent = (ComponentInstance)o; 
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
			}



			z_D++;
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

			z_L++;
		}

		// hier begint der test.

		textOutput.writeLn("=======Here begins the verification");
		DependencyClass dependencyClass_Test = corePackage.getDependency();
		if (z_D!=0&&z_L!=0){
			for(Iterator iter1 = dependencyClass_Test.refAllOfClass().iterator(); iter1.hasNext();){
				Dependency dependency = (Dependency)iter1.next();
				String dependencyName = dependency.getName();
				if (dependencyName != null){
					textOutput.writeLn("The name of the dependency is " + dependencyName);
				}
				/* for(Iterator iter2 = dependency.getStereotype().iterator();iter2.hasNext();){
    Stereotype stereotype_D = (Stereotype)iter2.next();
    String stereotypeName_dependency = stereotype_D.getName();
    textOutput.writeLn("The stereotype of the dependency is: " + stereotypeName_dependency);
    }
				 */
				for(Iterator it2 = (dependency.getClient()).iterator(); it2.hasNext();){
					Object o = (Object)it2.next();
					//ComponentInstance clientComponent = (Object)it2.next();
					if (o instanceof ComponentInstance){
						ComponentInstance clientComponent = (ComponentInstance)o; 
					
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
											textOutput.writeLn("The stereotype of the communication link of the dependency with the name "+dependencyName+" is " + stereotypeName_link);
											try{
												//Object stereotypeName_dependency = null;
	
												for(Iterator iter2 = dependency.getStereotype().iterator();iter2.hasNext();){
													Stereotype stereotype_D = (Stereotype)iter2.next();
													String stereotypeName_dependency = stereotype_D.getName();
													textOutput.writeLn("The stereotype of the dependency is " + stereotypeName_dependency);
	
													String aName1="default";
													String aName2="insider";
	
													// create a instance of the class Threaten
													Threaten threaten =new Threaten();
	
													// Threats from the default attacker
													Vector v1=threaten.threaten(new Attacker(aName1),new Stereotype_Link(stereotypeName_link));
													// Threats from the insider attacker
													Vector v2=threaten.threaten(new Attacker(aName2),new Stereotype_Link(stereotypeName_link));
	
													textOutput.writeLn("::::::::Against Default Attacker");
													if (stereotypeName_dependency.equals("secrecy")){
														if (v1.contains("read")){
															textOutput.writeLn("The UML model violates the requirement of the stereotype secure links.");
															dump = false;
															// insert <<encrypted>> stereotype here.
															logger.info("Changing " + link_SName + " link's stereotype <<" + stereotypeName_link + ">> to <<Encrypted>>");
															textOutput.writeLn("The UML model violates the requirement of the stereotype secure links, but it has been fixed.");
															IdNameList.getInstance().changeName(stereotype_Link.getName(), "Stereotype", "Encrypted", IdNameList.getInstance().getID(link_S.getName(), "Link"));
															stereotype_Link.setName("Encrypted");
															changed = true;
														}
														else{
															textOutput.writeLn("The UML model satisfies the requirement of the stereotype secure links.");
														}
													}
	
													if (stereotypeName_dependency.equals("integrity")){
														if (v1.contains("insert")){
															textOutput.writeLn("The UML model violates the requirement of the stereotype secure links");
															dump = false;
															// insert <<encrypted>> stereotype here.
															logger.info("Changing " + link_SName + " link's stereotype <<" + stereotypeName_link + ">> to <<Encrypted>>");
															textOutput.writeLn("The UML model violates the requirement of the stereotype secure links, but it has been fixed.");
															IdNameList.getInstance().changeName(stereotype_Link.getName(), "Stereotype", "Encrypted", IdNameList.getInstance().getID(link_S.getName(), "Link"));
															stereotype_Link.setName("Encrypted");
															changed = true;
														}
														else{textOutput.writeLn("The UML model satisfies the requirement of the stereotype secure links.");}
													}
													if (stereotypeName_dependency.equals("high")){
														if (v1.isEmpty()){
															textOutput.writeLn("The UML model satisfies the requirement of the stereotype secure links.");
														}
														// If v1 is not empty and doesn't contain delete, adding <<encrypted>> can fix the model.
														else if (!v1.contains("delete")) {
															// insert <<encrypted>> stereotype here.
															logger.info("Changing " + link_SName + " link's stereotype <<" + stereotypeName_link + ">> to <<Encrypted>>");
															textOutput.writeLn("The UML model violates the requirement of the stereotype secure links, but it has been fixed.");
															IdNameList.getInstance().changeName(stereotype_Link.getName(), "Stereotype", "Encrypted", IdNameList.getInstance().getID(link_S.getName(), "Link"));
															stereotype_Link.setName("Encrypted");
															changed = true;
														}
														else{
															textOutput.writeLn("The UML model violates the requirement of the stereotype secure links.");
															dump = false;
														}
													}
													if (stereotypeName_dependency.equals("integrity")){
														if (v1.contains("insert")){
															textOutput.writeLn("The UML model violates the requirement of the stereotype secure links");
															dump = false;
															// insert <<encrypted>> stereotype here.
															logger.info("Changing " + link_SName + " link's stereotype <<" + stereotypeName_link + ">> to <<Encrypted>>");
															textOutput.writeLn("The UML model violates the requirement of the stereotype secure links, but it has been fixed.");
															IdNameList.getInstance().changeName(stereotype_Link.getName(), "Stereotype", "Encrypted", IdNameList.getInstance().getID(link_S.getName(), "Link"));
															stereotype_Link.setName("Encrypted");
															changed = true;
														}
														else{textOutput.writeLn("The UML model satisfies the requirement of the stereotype secure links.");}
													}
													if (stereotypeName_dependency.equals("high")){
														if (v1.isEmpty()){
															textOutput.writeLn("The UML model satisfies the requirement of the stereotype secure links.");
														}
														// If v1 is not empty and doesn't contain delete, adding <<encrypted>> can fix the model.
														else if (!v1.contains("delete")) {
															// insert <<encrypted>> stereotype here.
															logger.info("Changing " + link_SName + " link's stereotype <<" + stereotypeName_link + ">> to <<Encrypted>>");
															textOutput.writeLn("The UML model violates the requirement of the stereotype secure links, but it has been fixed.");
															IdNameList.getInstance().changeName(stereotype_Link.getName(), "Stereotype", "Encrypted", IdNameList.getInstance().getID(link_S.getName(), "Link"));
															stereotype_Link.setName("Encrypted");
															changed = true;
														}
														else{
															textOutput.writeLn("The UML model violates the requirement of the stereotype secure links.");
															dump = false;
														}
													}
													if (!stereotypeName_dependency.equals("secrecy")&&!stereotypeName_dependency.equals("integrity")&&!stereotypeName_dependency.equals("high"))
													{textOutput.writeLn("The UML model satisfies the requirement of the stereotype secure links.");}
												}
											}
											catch (Exception e){textOutput.writeLn(e.toString());} //end of catch
										}
									}
									for(Iterator it7 = link_S.getStereotype().iterator();it7.hasNext();){
										Stereotype stereotype_Link = (Stereotype)it7.next();
										String stereotypeName_link = stereotype_Link.getName();
										textOutput.writeLn("The stereotype of the communication link of the dependency with the name "+dependencyName+" is " + stereotypeName_link);
										try{
											for(Iterator iter2 = dependency.getStereotype().iterator();iter2.hasNext();){
												Stereotype stereotype_D = (Stereotype)iter2.next();
												String stereotypeName_dependency = stereotype_D.getName();
												textOutput.writeLn("The stereotype of the dependency is " + stereotypeName_dependency);

												String aName1="default";
												String aName2="insider";

												// create a instance of the class Threaten
												Threaten threaten =new Threaten();

												// Threats from the default attacker
												Vector v1=threaten.threaten(new Attacker(aName1),new Stereotype_Link(stereotypeName_link));
												// Threats from the insider attacker
												Vector v2=threaten.threaten(new Attacker(aName2),new Stereotype_Link(stereotypeName_link));
												
												textOutput.writeLn("::::::::Against Inside Attacker");
												if (stereotypeName_dependency.equals("secrecy")){
													if (v2.contains("read")){
														dump = false;
														// insert <<encrypted>> stereotype here.
														logger.info("Changing " + link_SName + " link's stereotype <<" + stereotypeName_link + ">> to <<Encrypted>>");
														textOutput.writeLn("The UML model violates the requirement of the stereotype secure links, but it has been fixed.");
														IdNameList.getInstance().changeName(stereotype_Link.getName(), "Stereotype", "Encrypted", IdNameList.getInstance().getID(link_S.getName(), "Link"));
														stereotype_Link.setName("Encrypted");
														changed = true;
													}
													else {
														textOutput.writeLn("The UML model satisfies the requirement of the stereotype secure links.");
													}
												}
												if (stereotypeName_dependency.equals("integrity")){
													if (v2.contains("insert")){
														dump = false;
														// insert <<encrypted>> stereotype here.
														logger.info("Changing " + link_SName + " link's stereotype <<" + stereotypeName_link + ">> to <<Encrypted>>");
														textOutput.writeLn("The UML model violates the requirement of the stereotype secure links, but it has been fixed.");
														IdNameList.getInstance().changeName(stereotype_Link.getName(), "Stereotype", "Encrypted", IdNameList.getInstance().getID(link_S.getName(), "Link"));
														stereotype_Link.setName("Encrypted");
														changed = true;
													}
													else{
														textOutput.writeLn("The UML model satisfies the requirement of the stereotype secure links.");
													}
												}
												if (stereotypeName_dependency.equals("high")){
													if (v2.isEmpty()){
														textOutput.writeLn("The UML model satisfies the requirement of the stereotype secure links.");
													}
													// <<encrypted>> stereotype provides protection against "insert" for the inside attacker
													else if (v2.contains("insert") && (v2.size() == 1)) {
														// insert <<encrypted>> stereotype here.
														logger.info("Changing " + link_SName + " link's stereotype <<" + stereotypeName_link + ">> to <<Encrypted>>");
														textOutput.writeLn("The UML model violates the requirement of the stereotype secure links, but it has been fixed.");
														IdNameList.getInstance().changeName(stereotype_Link.getName(), "Stereotype", "Encrypted", IdNameList.getInstance().getID(link_S.getName(), "Link"));
														stereotype_Link.setName("Encrypted");
														//addEncryption = true;
														//toChange = stereotype_Link;
														changed = true;
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
			}
		}// end if

		else
		{
			if (z_D==0)
			{
				textOutput.writeLn("There are no dependencies in the diagram.");
			}
			if (z_L==0)
			{
				textOutput.writeLn("There are no links in the diagram.");
			}

			textOutput.writeLn("The UML model satisfies the requirement of the stereotype secure links");
		} // end else

		logger.info("********** End of Secure Links check **********");
		return dump;
	}
}


/*textOutput.writeLn("=======Hier begins the verification");
    LinkClass linkClass_ST = commonPackage.getLink();
    if (z_D!=0&&z_L!=0)
    {
      for(Iterator itera = linkClass_ST.refAllOfClass().iterator(); itera.hasNext();)
      {
        Link link_ST = (Link)itera.next();
                boolean ia_alarm = false;
                boolean da_alarm = false;

        for(Iterator itera1 =link_ST.getStereotype().iterator();itera1.hasNext();)
        {
          Stereotype stereotype_LT = (Stereotype)itera1.next();
          //textOutput.writeLn("Test for the link with the name "+ stereotype_LT.getName());
          try
          {

            String aName1="default";

            String aName2="insider";
            //textOutput.writeLn("The name of attacker is "+aName1 + " and "+aName2);
            String stereotypeName = stereotype_LT.getName();
            // create a instance of the class Threaten
            Threaten threaten =new Threaten();
            // Threats from the default attacker

            Vector v1=threaten.threaten(new Attacker(aName1),new Stereotype_Link(stereotypeName));
            // Threats from the insider attacker
            Vector v2=threaten.threaten(new Attacker(aName2),new Stereotype_Link(stereotypeName));

            // dependency
            DependencyClass dependencyClass_ST = corePackage.getDependency();
            for(Iterator itera2 = dependencyClass_ST.refAllOfClass().iterator(); itera2.hasNext();)
            {
              Dependency dependency_ST = (Dependency)itera2.next();
              // the stereotypes of the dependencies
              for(Iterator itera3 = dependency_ST.getStereotype().iterator();itera3.hasNext();)
              {
                Stereotype stereotype_DT = (Stereotype)itera3.next();

                textOutput.writeLn("::::::::Against Default Attacker");
                if (stereotype_DT.getName().equals("secrecy"))
                {
                  if (v1.contains("read"))
                  {
                    textOutput.writeLn("The UML model violates the requirement <<secure links>>.");
                                        da_alarm = true;
                    //link_ST.setName(link_ST.getName() + "_DA-Alarm_");
                                        //textOutput.writeLn("-->" +link_ST.getName());
                    dump = false;
                  }
                  else
                  {
                    textOutput.writeLn("The UML model satisfies the requirement <<secure links>>.");
                  }
                }
                if (stereotype_DT.getName().equals("integrity"))
                {
                  if (v1.contains("insert"))
                  {
                    textOutput.writeLn("The UML model violates the requirement <<secure links>>");
                                        da_alarm = true;
                    //link_ST.setName(link_ST.getName() + "_DA-Alarm_");
                                        //textOutput.writeLn("-->" +link_ST.getName());
                    dump = false;
                  }
                  else
                  {
                    textOutput.writeLn("The UML model satisfies the requirement <<secure links>>.");
                  }
                }
                if (stereotype_DT.getName().equals("high"))
                {
                  if (v1.isEmpty())
                  {
                    textOutput.writeLn("The UML model satisfies the requirement <<secure links>>.");
                  }
                  else
                  {
                    textOutput.writeLn("The UML model violates the requirement <<secure links>>.");
                                        da_alarm = true;
                    //link_ST.setName(link_ST.getName() + "_DA-Alarm_");
                                        //textOutput.writeLn("-->" +link_ST.getName());
                    dump = false;
                  }
                }
                textOutput.writeLn("::::::::Against Inside Attacker");
                if (stereotype_DT.getName().equals("secrecy"))
                {
                  if (v2.contains("read"))
                  {
                    textOutput.writeLn("The UML model violates the requirement <<secure links>>.");
                                        ia_alarm = true;
                    //link_ST.setName(link_ST.getName() + "_IA-Alarm_");
                                        //textOutput.writeLn("-->" +link_ST.getName());
                    dump = false;
                  }
                  else
                  {
                    textOutput.writeLn("The UML model satisfies the requirement <<secure links>>.");
                  }
                }
                if (stereotype_DT.getName().equals("integrity"))
                {
                  if (v2.contains("insert"))
                  {
                    textOutput.writeLn("The UML model violates the requirement <<secure links>>");
                                        ia_alarm = true;
                    //link_ST.setName(link_ST.getName() + "_IA-Alarm_");
                                        //textOutput.writeLn("-->" +link_ST.getName());
                    dump = false;
                  }
                  else
                  {
                    textOutput.writeLn("The UML model satisfies the requirement <<secure links>>.");
                  }
                }
                if (stereotype_DT.getName().equals("high"))
                {
                  if (v2.isEmpty())
                  {
                    textOutput.writeLn("The UML model satisfies the requirement <<secure links>>.");
                  }
                  else
                  {
                    textOutput.writeLn("The UML model violates the requirement <<secure links>>.");
                                        ia_alarm = true;
                    //link_ST.setName(link_ST.getName() + "_IA-Alarm_");
                                        //textOutput.writeLn("-->" +link_ST.getName());
                    dump = false;
                  }
                }
                //String sName=stereotype_DT.getName();
                //if (!dump && (sName.equals("secrecy")||sName.equals("integrity")||sName.equals("high"))){
                //textOutput.writeLn("The name of dependency is " + dependency_ST.getName());
                //textOutput.writeLn("The name of stereotype of dependency is " + sName);

                }
              }
            }
          catch (Exception e)
          {
            System.out.println(e.toString());
          }
                    //if (ia_alarm){
                      //if (link_ST.getName().indexOf("_IA-Alarm_") == -1){
                        //link_ST.setName(link_ST.getName() + "_IA-Alarm_");
                      //}
                    //}
                    //if (da_alarm){
                      //if (link_ST.getName().indexOf("_DA-Alarm_") == -1){
                        //link_ST.setName(link_ST.getName() + "_DA-Alarm_");
                      //}
                  //}
                    //textOutput.writeLn("-->" +link_ST.getName());
                }
            }
    }
    else
    {
      if (z_D==0)
      {
        textOutput.writeLn("There are no dependencies in the diagram.");
      }
      if (z_L==0)
      {
        textOutput.writeLn("There are no links in the diagram.");
      }

      textOutput.writeLn("The UML model satisfies the requirement <<secure links>>");
    }*/

// list the node instances of the communication link

//textOutput.writeLn("=======LinkEndtoNodeintanceName");
//ALinkConnection aLink =commonPackage.getALinkConnection();
//LinkEndClass linkEndClasses=commonPackage.getLinkEnd();
//for(Iterator iter5 = linkEndClasses.refAllOfClass().iterator(); iter5.hasNext();)
//{
//LinkEnd linkend = (LinkEnd)iter5.next();
//textOutput.writeLn((linkend.getInstance().getComponentInstance().getNodeInstance()).getName());
//}


//list the component instances and the node instances
/* textOutput.writeLn("=======ComponentInstance");
     ComponentInstanceClass componentInstClasses =commonPackage.getComponentInstance();
     for(Iterator iter7 = componentInstClasses.refAllOfClass().iterator(); iter7.hasNext();){
      ComponentInstance componentinst = (ComponentInstance)iter7.next();
      textOutput.writeLn(componentinst.getName());
      textOutput.writeLn("It is resident in the following node instance:");
      textOutput.writeLn((componentinst.getNodeInstance()).getName());

      }
 */
// the client (component instance) of the dependency

/* for(Iterator it1 = dependencyClass.refAllOfClass().iterator(); it1.hasNext();){

      Dependency dependency_C = (Dependency)it1.next();
      textOutput.writeLn(dependency_C.getClass().getName());
      textOutput.writeLn("=======The name of the client component instance of the dependency");
      for(Iterator it2 = (dependency_C.getClient()).iterator(); it2.hasNext();){
        System.out.println(":::::::");
        System.out.println(((ComponentInstance)it2.next()).getName());
        }
// the supplier (component instance) of the dependency
      System.out.println("=======The name of the supplier comonent instance of the dependency");
       for(Iterator it3 = (dependency_C.getSupplier()).iterator(); it3.hasNext();){
        System.out.println(":::::::");
        System.out.println(((ComponentInstance)it3.next()).getName());
        }
          }

    return dump;

  }

}
 */