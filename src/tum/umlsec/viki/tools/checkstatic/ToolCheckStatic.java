package tum.umlsec.viki.tools.checkstatic;


import java.util.Iterator;
import java.util.Vector;

import tum.umlsec.viki.framework.ILogOutput;
import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.mdr.IMdrContainer;
import tum.umlsec.viki.framework.toolbase.CommandDescriptor;
import tum.umlsec.viki.framework.toolbase.CommandParameterDescriptor;
import tum.umlsec.viki.framework.toolbase.IVikiToolBase;
import tum.umlsec.viki.framework.toolbase.IVikiToolConsole;
import tum.umlsec.viki.framework.toolbase.IVikiToolGui;
import tum.umlsec.viki.framework.toolbase.IVikiToolWeb;
import tum.umlsec.viki.tools.checkstatic.checks.CheckerFairExchange;
import tum.umlsec.viki.tools.checkstatic.checks.CheckerFreshness;
import tum.umlsec.viki.tools.checkstatic.checks.CheckerGuardedAccess;
import tum.umlsec.viki.tools.checkstatic.checks.CheckerNodownFlow;
import tum.umlsec.viki.tools.checkstatic.checks.CheckerProvable;
import tum.umlsec.viki.tools.checkstatic.checks.CheckerSecrecyIntegrity;
import tum.umlsec.viki.tools.checkstatic.checks.CheckerSecureDependency;
import tum.umlsec.viki.tools.checkstatic.checks.CheckerSecureLinks;
import tum.umlsec.viki.tools.checkstatic.checks.CheckerSecureLinksWithXml;

/**
 * @author Shasha Meng
 */
public class ToolCheckStatic implements IVikiToolBase, IVikiToolConsole {

	public static final int CID_SECRECY_INTEGRITY = 1;
	public static final int CID_FAIR_EXCHANGE = 2;
	public static final int CID_GUARDED_ACCESS = 3;
	public static final int CID_PROVABLE = 4;
	public static final int CID_SECURE_DEPENDENCY = 5;
	public static final int CID_SECURE_LINKS = 6;
	public static final int CID_LINK_XML = 7;
	public static final int CID_FRESHNESS = 8;
	public static final int CID_NODOWNFLOW = 9;

    public static final int CPID_FILE = 1;

    private boolean XMLFileInitialised = false;


	public IVikiToolConsole getConsole() { return this; }
	public IVikiToolGui getGui() { return null; }
	public IVikiToolWeb getWeb() { return null; }

	public IVikiToolBase getBase() {return this;}

	public String getToolName() {return "UMLsec Static Check I";}
	public String getToolDescription() {return "Verifies static model properties";}

	IMdrContainer mdrContainer;
	boolean returnValue;
	ITextOutput _textOutput;

	Vector parametersEmpty = new Vector();
	Vector _parameters = new Vector();
	Vector commands = new Vector();


	public void initialiseBase(IMdrContainer _mdrContainer) {

		mdrContainer = _mdrContainer;

		_parameters.add(parameterFile);
        commands.add(cmdSecrecyIntegrity);
    commands.add(cmdFairExhange);
    commands.add(cmdGuardedAccess);
    commands.add(cmdProvable);
    commands.add(cmdSecureDependency);
    commands.add(cmdSecureLinks);
    commands.add(cmdSecureLinksWithXML);
    commands.add(cmdfresh);
    commands.add(cmdnodownflow);
		}
	public Iterator getConsoleCommands(){


		return commands.iterator();
	}

	public void initialiseConsole() {
		int y = 0;
	}

//	public Iterator getConsoleCommands() {
//		return commands.iterator();
//	}

	public void executeConsoleCommand(CommandDescriptor _command, Iterator _parameters, ITextOutput _mainOutput, ILogOutput _auxOutput) {

		StaticCheckerBase _checker = null;

		switch(_command.getId()) {
			case CID_SECRECY_INTEGRITY:
				_checker = new CheckerSecrecyIntegrity();
				returnValue = _checker.check(mdrContainer, _parameters, _mainOutput);

			break;

			case CID_FAIR_EXCHANGE:
				_checker = new CheckerFairExchange();
				returnValue = _checker.check(mdrContainer, _parameters, _mainOutput);
			break;

			case CID_GUARDED_ACCESS:
				_checker = new CheckerGuardedAccess();
				returnValue = _checker.check(mdrContainer, _parameters, _mainOutput);
			break;

			case CID_PROVABLE:
				_checker = new CheckerProvable();
				returnValue = _checker.check(mdrContainer, _parameters, _mainOutput);
			break;

			case CID_SECURE_DEPENDENCY:
				_checker = new CheckerSecureDependency();
				returnValue = _checker.check(mdrContainer, _parameters, _mainOutput);
			break;

			case CID_SECURE_LINKS:
				_checker = new CheckerSecureLinks();
				returnValue = _checker.check(mdrContainer, _parameters, _mainOutput);
			break;

			case CID_LINK_XML:
        _checker = new CheckerSecureLinksWithXml();
          returnValue = _checker.check(mdrContainer, _parameters, _mainOutput);
			/*	_textOutput = _mainOutput;

			// xml parsen und initialisieren:
			File _xmlFile = new File("");
			boolean _foundFile = false;

			for (; _parameters.hasNext();) {
				_textOutput.writeLn("==========================_parameter sit eingelesen...");
				CommandParameterDescriptor _parameter = (CommandParameterDescriptor) _parameters.next();
				if(_parameter.getId() == ToolCheckStatic.CPID_FILE) {
					_textOutput.writeLn("==========================_parameter getId...");
					_xmlFile = _parameter.getAsFile();
					_textOutput.writeLn("==========================XML file name :" + _xmlFile.getName());

					_foundFile = true;
				}

			}
			if(_foundFile == false) {
			 	throw new ExceptionProgrammLogicError("Required parameter missing");
			}

			//String url = _xmlFile.getAbsolutePath();
			XMLDOMParser xmldomparser = new XMLDOMParser(_mainOutput);
			HashMap hashMap = xmldomparser.loadXMLFile(_xmlFile);
      _textOutput.writeLn("Here are all key/value pairs in the map.");
      _textOutput.writeLn("map "+hashMap);
			String attacker_Name = xmldomparser.getAttackerName();

			_textOutput.writeLn("The name of the attacker is " + attacker_Name);

			XMLFileInitialised = true;

			org.omg.uml.UmlPackage root ;

			CorePackage corePackage ;


			root = mdrContainer.getUmlPackage();
			corePackage = root.getCore();

			boolean dump = true;
			CommonBehaviorPackage commonPackage= root.getCommonBehavior();
			_textOutput.writeLn("==========================running CheckerSecureLinksWithXml...");
      _textOutput.writeLn("=======Here begins the verification");
      DependencyClass dependencyClass_Test = corePackage.getDependency();
      //if (z_D!=0&&z_L!=0){
    for(Iterator iter1 = dependencyClass_Test.refAllOfClass().iterator(); iter1.hasNext();){
    Dependency dependency = (Dependency)iter1.next();
    String dependencyName = dependency.getName();
    _textOutput.writeLn("The name of the dependency is " + dependencyName);
   /* for(Iterator iter2 = dependency.getStereotype().iterator();iter2.hasNext();){
    Stereotype stereotype_D = (Stereotype)iter2.next();
    String stereotypeName_dependency = stereotype_D.getName();
    textOutput.writeLn("The stereotype of the dependency is: " + stereotypeName_dependency);
    }
    */
    /*for(Iterator it2 = (dependency.getClient()).iterator(); it2.hasNext();){
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
          _textOutput.writeLn("The stereotype of the communication link of the dependency "+dependencyName+" is " + stereotypeName_link);
          try{
          //Object stereotypeName_dependency = null;

          for(Iterator iter2 = dependency.getStereotype().iterator();iter2.hasNext();){
            Stereotype stereotype_D = (Stereotype)iter2.next();
            String stereotypeName_dependency = stereotype_D.getName();
            _textOutput.writeLn("The stereotype of the dependency is: " + stereotypeName_dependency);

          Threaten threaten = new Threaten();
          Vector v1 = threaten.threaten(new Attacker(attacker_Name), new Stereotype_Link(stereotypeName_link), hashMap);

          /*String aName1="default";
          String aName2="insider";

          // create a instance of the class Threaten
          Threaten threaten =new Threaten();

          // Threats from the default attacker
          Vector v1=threaten.threaten(new Attacker(aName1),new Stereotype_Link(stereotypeName_link));
          // Threats from the insider attacker
          Vector v2=threaten.threaten(new Attacker(aName2),new Stereotype_Link(stereotypeName_link));
          */

          //textOutput.writeLn("::::::::Against Default Attacker");
          /*if (stereotypeName_dependency.equals("secrecy")){
            if (v1.contains("read")){
            // _textOutput.writeLn("Hello, I am here!!!!");
            _textOutput.writeLn("The UML model violates the requirement <<secure links>>.");
            dump = false;
             }
            else{
            _textOutput.writeLn("The UML model satisfies the requirement <<secure links>>.");
            }
           }

           if (stereotypeName_dependency.equals("integrity")){
            if (v1.contains("insert")){
            _textOutput.writeLn("The UML model violates the requirement <<secure links>>");
            dump = false;
             }
            else{_textOutput.writeLn("The UML model satisfies the requirement <<secure links>>.");}
            }
            if (stereotypeName_dependency.equals("high")){
            if (v1.isEmpty()){
              _textOutput.writeLn("The UML model satisfies the requirement <<secure links>>.");
             }
             else{
              _textOutput.writeLn("The UML model violates the requirement <<secure links>>.");
              dump = false;
              }
             }

             /*textOutput.writeLn("::::::::Against Inside Attacker");
             if (stereotypeName_dependency.equals("secrecy")){
            if (v2.contains("read")){
              textOutput.writeLn("The UML model violates the requirement <<secure links>>.");
              dump = false;}
            else {
              textOutput.writeLn("The UML model satisfies the requirement <<secure links>>.");
             }
            }
            if (stereotypeName_dependency.equals("integrity")){
              if (v2.contains("insert")){
              textOutput.writeLn("The UML model violates the requirement <<secure links>>");
              dump = false;
               }
               else{
              textOutput.writeLn("The UML model satisfies the requirement <<secure links>>.");
               }
            }
            if (stereotypeName_dependency.equals("high")){
              if (v2.isEmpty()){
              textOutput.writeLn("The UML model satisfies the requirement <<secure links>>.");
               }
              else{
              textOutput.writeLn("The UML model violates the requirement <<secure links>>.");
              dump = false;
              }
            }
            */

         /* }
          }//end of try

          catch (Exception e){_textOutput.writeLn(e.toString());} //end of catch

         }
         }

       }
       }
     }
    }
   } // end for




 /* else
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
  } // end else
  */

			/*LinkClass linkClass_T = commonPackage.getLink();
			for(Iterator itera = linkClass_T.refAllOfClass().iterator(); itera.hasNext();)
			{
			    Link link_T = (Link)itera.next();
               			 for(Iterator itera1 =link_T.getStereotype().iterator();itera1.hasNext();)
				{
				    	Stereotype stereotypeLink = (Stereotype)itera1.next();
					String stereotypeName_link = stereotypeLink.getName();
			        		Threaten threaten = new Threaten();
		            		Vector v1 = threaten.threaten(new Attacker(attacker_Name), new Stereotype_Link(stereotypeName_link), hashMap);
                    				        DependencyClass dependencyClass_ST = corePackage.getDependency();
						for(Iterator itera2 = dependencyClass_ST.refAllOfClass().iterator(); itera2.hasNext();)
						{
							Dependency dependency_ST = (Dependency)itera2.next();
						    for(Iterator itera3 = dependency_ST.getStereotype().iterator();itera3.hasNext();)
							{
								Stereotype stereotype_DT = (Stereotype)itera3.next();
								if (stereotype_DT.getName().equals("secrecy"))
							    {
							    	if (v1.contains("read"))
									{
										_textOutput.writeLn("The UML model violates the requirement <<secure links>>.");
								        dump = false;
									}
									else
									{
									    _textOutput.writeLn("The UML model satisfies the requirement <<secure links>>.");
									}
								 }

								 if (stereotype_DT.getName().equals("integrity"))
								 {
								    if (v1.contains("insert"))
									{
									    _textOutput.writeLn("The UML model violates the requirement <<secure links>>");
										dump = false;
									 }
									 else
									 {
									     _textOutput.writeLn("The UML model satisfies the requirement <<secure links>>.");
									 }
								  }

								  if (stereotype_DT.getName().equals("high"))
								  {
								     if (v1.isEmpty())
									 {
									    _textOutput.writeLn("The UML model satisfies the requirement <<secure links>>.");

									  }
									  else
									  {
									    	_textOutput.writeLn("The UML model violates the requirement <<secure links>>.");
											dump = false;
									  }
			                      }

								}
						}
					}
			   }
			   */

			break;
			case CID_FRESHNESS:
			_checker = new CheckerFreshness();
			returnValue = _checker.check(mdrContainer, _parameters, _mainOutput);
			break;
			case CID_NODOWNFLOW:
			_checker = new CheckerNodownFlow();
			returnValue = _checker.check(mdrContainer, _parameters, _mainOutput);
			break;
			default:
				// TODO throw something nice
		}

		//returnValue = _checker.check(mdrContainer, _parameters, _mainOutput);
	}





//	public JPanel getUiPanel() {
//		JPanel _panel = new JPanel(new BorderLayout());
//		mainTextArea = new JTextArea();
//		_panel.add(mainTextArea, BorderLayout.CENTER );


	CommandDescriptor cmdSecrecyIntegrity = CommandDescriptor.CommandDescriptorConsole(CID_SECRECY_INTEGRITY, "SecretyIntegrity", "Check the secrecy/integrity property", true, parametersEmpty);
	CommandDescriptor cmdFairExhange = CommandDescriptor.CommandDescriptorConsole(CID_FAIR_EXCHANGE, "FairExchange", "Check the Fair Exchange property", true, parametersEmpty);
	CommandDescriptor cmdGuardedAccess = CommandDescriptor.CommandDescriptorConsole(CID_GUARDED_ACCESS, "GuardedAccess", "Check the Guarded Access property", true, parametersEmpty);
	CommandDescriptor cmdProvable = CommandDescriptor.CommandDescriptorConsole(CID_PROVABLE, "Provable", "Check the Provable property", true, parametersEmpty);
	CommandDescriptor cmdSecureDependency = CommandDescriptor.CommandDescriptorConsole(CID_SECURE_DEPENDENCY, "SecureDependency", "Check the Secure Dependency property", true, parametersEmpty);
	CommandDescriptor cmdSecureLinks = CommandDescriptor.CommandDescriptorConsole(CID_SECURE_LINKS, "SecureLinks", "Check the Secure Links property", true, parametersEmpty);
	CommandDescriptor cmdSecureLinksWithXML = CommandDescriptor.CommandDescriptorConsole(CID_LINK_XML, "SecureLinks with Xml", "Check the Secure Links property", true, _parameters);
	CommandDescriptor cmdfresh = CommandDescriptor.CommandDescriptorConsole(CID_FRESHNESS, "Freshness", "Check the Freshness", true, parametersEmpty);
	CommandDescriptor cmdnodownflow = CommandDescriptor.CommandDescriptorConsole(CID_NODOWNFLOW, "No down-flow", "Check the Nodownflow", true, parametersEmpty);

	CommandParameterDescriptor parameterFile = CommandParameterDescriptor.CommandParameterDescriptorFile(CPID_FILE, "File");
}