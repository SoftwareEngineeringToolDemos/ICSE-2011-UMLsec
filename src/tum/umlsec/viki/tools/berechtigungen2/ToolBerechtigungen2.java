/*
 * Created on 22.07.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package tum.umlsec.viki.tools.berechtigungen2;

import java.util.HashMap;
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
import tum.umlsec.viki.tools.berechtigungen.ToolBerechtigungen;
import tum.umlsec.viki.tools.berechtigungen.xmlparser.NumberGeneratorBID;
import tum.umlsec.viki.tools.berechtigungen2.mdrparser.MDRParser2;
import tum.umlsec.viki.tools.berechtigungen2.umlparser.UmlParser;

/**
 * @author josef
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ToolBerechtigungen2 implements IVikiToolBase, IVikiToolConsole 
{
	
	public static final int CID_BERECHTIGUNGEN_CHECKER = 1;
	public static final int CID_BERECHTIGUNGEN_TOMUCH_CHECKER = 2;
	public static final int CID_BERECHTIGUNGEN_CREATE = 3;
	public static final int CID_BERECHTIGUNGEN_RETURNXML = 4;
	public static final int CID_BERECHTIGUNGEN_INIT1 = 5;
	public static final int CID_BERECHTIGUNGEN_INIT2 = 6;
	public static final int CID_BERECHTIGUNGEN_CORRECT_AND_CHECK_PERMISSIONS = 7;
	public static final int CID_BERECHTIGUNGEN_INIT3 = 8;
	public static final int CID_BERECHTIGUNGEN_RETURNUML = 9;
	
	public static final int CPID_FILE = 1;
	public static final int CPID_String = 2;
	
	
	
	IMdrContainer mdrContainer;

	Vector _parameters = new Vector();
	Vector _parameters2 = new Vector();
	Vector _parametersEmpty = new Vector();
	Vector alleUser = new Vector();

	CommandDescriptor cmdcheckBerechtigung = CommandDescriptor.CommandDescriptorConsole(CID_BERECHTIGUNGEN_CHECKER, "Check Permissions", "Check SAP-Permissions", true, _parameters); 
	CommandDescriptor cmdcheckToMuchBerechtigung = CommandDescriptor.CommandDescriptorConsole(CID_BERECHTIGUNGEN_TOMUCH_CHECKER, "Check ToMuch Permissions", "Check to much SAP-Permissions", true, _parameters); 
	CommandDescriptor cmdcreatePermissions = CommandDescriptor.CommandDescriptorConsole(CID_BERECHTIGUNGEN_CREATE, "Create Permissions", "Create SAP-Permissions", true, _parameters2); 
	CommandDescriptor cmdreturnXMLFile = CommandDescriptor.CommandDescriptorConsole(CID_BERECHTIGUNGEN_RETURNXML, "return XML-File", "get XML File", true, _parameters2);
	CommandDescriptor cmdinitwithoutXmlFile = CommandDescriptor.CommandDescriptorConsole(CID_BERECHTIGUNGEN_INIT1, "Initialise without XML-File", "Initialize without XML-File", true, _parametersEmpty);
	CommandDescriptor cmdinitwithXmlFile = CommandDescriptor.CommandDescriptorConsole(CID_BERECHTIGUNGEN_INIT2, "Initialise with XML-File", "Initialize with XML-File", true, _parameters);
	CommandDescriptor cmdcorrectandcheckPermissions = CommandDescriptor.CommandDescriptorConsole(CID_BERECHTIGUNGEN_CORRECT_AND_CHECK_PERMISSIONS, "Correct Profile and check permissions", "Correct Profile and check permissions", true, _parameters2);
	CommandDescriptor cmdreturnUML = CommandDescriptor.CommandDescriptorConsole(CID_BERECHTIGUNGEN_RETURNUML, "return correct UML model", "return UML model", true, _parametersEmpty);
	
	CommandParameterDescriptor parameterFile = CommandParameterDescriptor.CommandParameterDescriptorFile(CPID_FILE, "File"); 
	//CommandParameterDescriptor parameterString = CommandParameterDescriptor.CommandParameterDescriptorFile(CPID_String, "String");
	CommandParameterDescriptor parameterString = CommandParameterDescriptor.CommandParameterDescriptorString(CPID_String, "String");

	//------------------------------
	CommandDescriptor cmdinitwithUmlBeschreibung = CommandDescriptor.CommandDescriptorConsole(CID_BERECHTIGUNGEN_INIT3, "Initialize through the UML-model", "Initialize through the UML-model", true, _parametersEmpty);
	//------------------------------
	
	
	Vector commands;
	HashMap activities;
	HashMap permissions;	
	String[] params = new String[4];
	Vector rollenVorschlaege = new Vector();
	Vector alleInitialisedUser = new Vector();
	MDRParser2 mdrparser;
	UmlParser umlParser;
	

	private boolean XMLFileInitialised = false;
	private boolean UMLBerechtEingelesen = false;

	public void initialiseBase(IMdrContainer _mdrContainer) 
	{
		mdrContainer = _mdrContainer;
		_parameters.add(parameterFile);
		_parameters2.add(parameterString);	
	}
	
	
	public Iterator getConsoleCommands() 
	{
		commands = new Vector();
		commands.add(cmdinitwithUmlBeschreibung);
		if(UMLBerechtEingelesen) 
		{
			commands.add(cmdcorrectandcheckPermissions);
			commands.add(cmdreturnXMLFile);	
			// commands.add(cmdreturnUML);
		}
		return commands.iterator();
	}
	
	
	public void initialiseConsole() 
	{
	}
	

	public void executeConsoleCommand(CommandDescriptor _command, Iterator _parameters, ITextOutput _mainOutput, ILogOutput _auxOutput)
	{
		BerTools2 _bertools = new BerTools2(_mainOutput);
		
		MDRBerechtigungen2 _checker = new MDRBerechtigungen2();
		
		switch(_command.getId()) 
		{
			case CID_BERECHTIGUNGEN_CHECKER:
				_checker.check1(mdrContainer, _parameters, _mainOutput, _auxOutput);
				break;
			
			case CID_BERECHTIGUNGEN_TOMUCH_CHECKER:
				_checker.check2(mdrContainer, _parameters, _mainOutput, _auxOutput);
				break;
			
			case CID_BERECHTIGUNGEN_CREATE:
				_checker.check3(mdrContainer, _parameters, _mainOutput, _auxOutput);
				break;
			
			case CID_BERECHTIGUNGEN_RETURNXML:
				
				String _url = "";
				
				// get the URL:
				for (; _parameters.hasNext();) 
				{
					CommandParameterDescriptor _parameter = (CommandParameterDescriptor) _parameters.next();
					if(_parameter.getId() == ToolBerechtigungen.CPID_String) 
					{
						_url = _parameter.getAsString();
					}
				}
				
				umlParser.writeXMLFile(_url);
				
				break;
			
			case CID_BERECHTIGUNGEN_RETURNUML:
				umlParser.writeUml();
				
				break;
				
				//Initialisation through the UML model:
			case CID_BERECHTIGUNGEN_INIT3:
				//set number-generator to 0:
				NumberGeneratorBID.reset();
				
				// parse UML model and initialise:
				mdrparser = new MDRParser2(_mainOutput);
				
				activities = mdrparser.parseUMLDiagram(mdrContainer);
				alleUser = mdrparser.getAllUsers();
				
				umlParser = new UmlParser(_mainOutput);
				umlParser.createXmlDocument();
				
				//the umlParser gets the vectors protVector, rolVector, userVector, so he can use the existing IDs, and generate new ones, if necessary!
				Vector allVectors = mdrparser.getVectors();
				umlParser.setVectors(allVectors);
				
				// build HashMap with XML-data:
				umlParser.parseUmlModel(mdrContainer);
				permissions = umlParser.parse();
				alleInitialisedUser = umlParser.getAllInitialisedUsers();
				UMLBerechtEingelesen = true;
				
				//roleID -> roleName
				HashMap roleHashMap = umlParser.getRoleHashMap();
				//------------------------------------------------------
				
				// test starts now:
				params = _bertools.doTest(activities, permissions);
				
				if(params[0].equals("fehlt"))
				{
					// call function, that displays possible roles
					rollenVorschlaege = _bertools.askForRolle2(params, umlParser, alleInitialisedUser, alleUser, roleHashMap);
					
				}
				else
				{
					_mainOutput.writeLn("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
					_mainOutput.writeLn("The test has been successful.");
					_mainOutput.writeLn("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
				}
				
				break;
				
		//--------------------------------------------------------------------		
				
			case CID_BERECHTIGUNGEN_CORRECT_AND_CHECK_PERMISSIONS:
				// coorect the error and start the test
				HashMap protHMap = umlParser.getHMap();
				
				String _rolleID = "";
				
				// ask roleID:
				for (; _parameters.hasNext();) 
				{
					CommandParameterDescriptor _parameter = (CommandParameterDescriptor) _parameters.next();
					if(_parameter.getId() == ToolBerechtigungen2.CPID_String) 
					{
						_rolleID = _parameter.getAsString();
					}
				}
				
				// corrector:
				_bertools.correctXMLDocument2(umlParser, params, _rolleID, alleInitialisedUser, protHMap);

				// start new test:
				permissions = umlParser.parse();

				alleInitialisedUser = umlParser.getAllInitialisedUsers();

				XMLFileInitialised = true;
				
				// start test:
				params = _bertools.doTest(activities, permissions);	
				
				//was there any error?
				if(params[0].equals("fehlt"))
				{
//					roleID -> roleName
					HashMap roleHMap = umlParser.getRoleHashMap();
					// call function, that displays possible roles
					rollenVorschlaege = _bertools.askForRolle2(params, umlParser, alleInitialisedUser, alleUser, roleHMap);
				}
				else
				{
					_mainOutput.writeLn("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
					_mainOutput.writeLn("The test has been successful.");
					_mainOutput.writeLn("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
				}			
				
				break;
				
		}	// End of switch()
	}

	public IVikiToolConsole getConsole() { return this; }
	public IVikiToolGui getGui() { return null; }
	public IVikiToolWeb getWeb() { return null; }

	public IVikiToolBase getBase() { return this; }

	public String getToolName() {			return "Activity-Diagram RBAC Analyzer";	}
	public String getToolDescription() {	return "Activity-Diagram RBAC Analyzer";	}

	
}
