package tum.umlsec.viki.tools.berechtigungen;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.w3c.dom.Document;

import tum.umlsec.viki.framework.ExceptionProgrammLogicError;
import tum.umlsec.viki.framework.ILogOutput;
import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.mdr.IMdrContainer;
import tum.umlsec.viki.framework.toolbase.CommandDescriptor;
import tum.umlsec.viki.framework.toolbase.CommandParameterDescriptor;
import tum.umlsec.viki.framework.toolbase.IVikiToolBase;
import tum.umlsec.viki.framework.toolbase.IVikiToolConsole;
import tum.umlsec.viki.framework.toolbase.IVikiToolGui;
import tum.umlsec.viki.framework.toolbase.IVikiToolWeb;
import tum.umlsec.viki.tools.berechtigungen.mdrparser.MDRActivity;
import tum.umlsec.viki.tools.berechtigungen.mdrparser.MDRParser;
import tum.umlsec.viki.tools.berechtigungen.mdrparser.MDRUser;
import tum.umlsec.viki.tools.berechtigungen.xmlparser.XMLDOMParser;
import org.apache.log4j.*;

/**
 * @author E.Alter
 */

public class ToolBerechtigungen implements IVikiToolBase, IVikiToolConsole
{
	public static final int CID_BERECHTIGUNGEN_CHECKER = 1;
	public static final int CID_BERECHTIGUNGEN_TOMUCH_CHECKER = 2;
	public static final int CID_BERECHTIGUNGEN_CREATE = 3;
	public static final int CID_BERECHTIGUNGEN_RETURNXML = 4;
	public static final int CID_BERECHTIGUNGEN_INIT1 = 5;
	public static final int CID_BERECHTIGUNGEN_INIT2 = 6;
	public static final int CID_BERECHTIGUNGEN_CORRECT_AND_CHECK_PERMISSIONS = 7;

	public static final int CPID_FILE = 1;
	public static final int CPID_String = 2;

	private boolean XMLFileInitialised = false;

	public void initialiseBase(IMdrContainer _mdrContainer)
	{
		mdrContainer = _mdrContainer;
		_parameters.add(parameterFile);
		_parameters2.add(parameterString);
	}


	public Iterator getConsoleCommands()
	{
		commands = new Vector();
//		commands.add(cmdcheckBerechtigung);  		//alte Version
//		commands.add(cmdcheckToMuchBerechtigung);  	//alte Version
//		commands.add(cmdcreatePermissions);  		//alte Version
		commands.add(cmdinitwithoutXmlFile);
		commands.add(cmdinitwithXmlFile);

		if(XMLFileInitialised)
		{
			commands.add(cmdcorrectandcheckPermissions);
			commands.add(cmdreturnXMLFile);
		}
		return commands.iterator();
	}


	public void initialiseConsole()
	{
	}


	public void executeConsoleCommand(CommandDescriptor _command, Iterator _parameters, ITextOutput _mainOutput, ILogOutput _auxOutput)
	{
		Logger logger=Logger.getLogger("berechtigungen");
		BerTools _bertools = new BerTools(_mainOutput);

		MDRBerechtigungen _checker = new MDRBerechtigungen ();

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

				// Die URL abfragen:
				for (; _parameters.hasNext();)
				{
					CommandParameterDescriptor _parameter = (CommandParameterDescriptor) _parameters.next();
					if(_parameter.getId() == ToolBerechtigungen.CPID_String)
					{
						_url = _parameter.getAsString();
					}
				}

				xmldomparser.writeXMLFile(_url);

				break;

			case CID_BERECHTIGUNGEN_INIT1:
				// INITIALISIERUNG OHNE XML-DATEI:
				// -------------------------------
				// Es wrid das UML-Modell geparst und ausgehend daraus ein XML-Dummy erstellt,
				// das nur <Objekt> und <Benutzer>-Eintrage enthalt, damit fur das HashMap Paare
				// gebildet werden konnen, die allerdings als Value null haben.

				// uml parsen und initialisieren:
				mdrparser = new MDRParser(_mainOutput);
				activities = mdrparser.parseUMLDiagram(mdrContainer);

				alleUser = mdrparser.getAllUsers();
				Vector alleTeilAktivitaeten = mdrparser.getAllActivities();

				for(int i = 0; i < alleUser.size(); i++)
				{
					MDRUser _user = (MDRUser)alleUser.get(i);
					for(int y = 0; y < _user.getMyActivities().size(); y++)
					{
						MDRActivity _acti = (MDRActivity)_user.getMyActivities().get(y);
					}
				}

				xmldomparser = new XMLDOMParser(_mainOutput);
				xmldomparser.createXmlDocument(alleUser);
				xmldomparser.addNodes(alleUser, alleTeilAktivitaeten);

				permissions = xmldomparser.parse();
				alleInitialisedUser = xmldomparser.getAllInitialisedUsers();

				// Test starten:
				params = _bertools.doTest(activities, permissions);

				//TODO abfragen, ob es fehler gab!!!
				if(params[0].equals("fehlt"))
				{
					// Funktion aufrufen, die alles noetige auf den Schirm bringt und die Rollen vorschlaegt:
					rollenVorschlaege = _bertools.askForRolle2(params, xmldomparser, alleInitialisedUser, alleUser);
				}
				else
				{
					// Meldung ausgeben, dass es keine Fehler gab!
					_mainOutput.writeLn("The tests are finished.");
				}

				XMLFileInitialised = true;
				break;

			case CID_BERECHTIGUNGEN_INIT2:
			  try{
				// INTIALISIERUNG MIT XML-DATEI:
				// -----------------------------

				// uml parsen und initialisieren:
				mdrparser = new MDRParser(_mainOutput);

				activities = mdrparser.parseUMLDiagram(mdrContainer);
				alleUser = mdrparser.getAllUsers();

				// xml parsen und initialisieren:
				File _xmlFile = new File("");
				boolean _foundFile = false;
				for (; _parameters.hasNext();)
				{
					CommandParameterDescriptor _parameter = (CommandParameterDescriptor) _parameters.next();
					if(_parameter.getId() == ToolBerechtigungen.CPID_FILE)
					{
						_xmlFile = _parameter.getAsFile();
						_foundFile = true;
					}
				}
				if(_foundFile == false)
				{
					throw new ExceptionProgrammLogicError("Required parameter missing");
				}
				String url = _xmlFile.getAbsolutePath();

				xmldomparser = new XMLDOMParser(_mainOutput);
				xmldomparser.loadXMLFile(url);

				// HashMap mit den XML-Daten bilden:
				permissions = xmldomparser.parse();
				alleInitialisedUser = xmldomparser.getAllInitialisedUsers();
				XMLFileInitialised = true;

				// Test starten:
				params = _bertools.doTest(activities, permissions);

				//TODO abfragen, ob es fehler gab!!!
				if(params[0].equals("fehlt"))
				{
					// Funktion aufrufen, die alles noetige auf den Schirm bringt und die Rolle vorschlaegt.
					rollenVorschlaege = _bertools.askForRolle2(params, xmldomparser, alleInitialisedUser, alleUser);
				}
				else
				{
					// Meldung ausgeben, dass es keine Fehler gab!
					_mainOutput.writeLn("The tests are finished.");
				}

				break;
			  }catch(Exception e){
				logger.fatal("Exception while initialise with Xml-File");
				break;
			  }
	
			case CID_BERECHTIGUNGEN_CORRECT_AND_CHECK_PERMISSIONS:
				// Den Fehler korrigieren und einen neuen Test ausfuehren:

				String _rolleID = "";

				// Die RollID abfragen:
				for (; _parameters.hasNext();)
				{
					CommandParameterDescriptor _parameter = (CommandParameterDescriptor) _parameters.next();
					if(_parameter.getId() == ToolBerechtigungen.CPID_String)
					{
						_rolleID = _parameter.getAsString();
					}
				}

				// Korrektor:
				_bertools.correctXMLDocument2(xmldomparser, params, _rolleID, alleInitialisedUser);

				// neuen Test starten:
				permissions = xmldomparser.parse();

				alleInitialisedUser = xmldomparser.getAllInitialisedUsers();

				XMLFileInitialised = true;

				// Test starten:
				params = _bertools.doTest(activities, permissions);

				//abfragen, ob es fehler gab!!!
				if(params[0].equals("fehlt"))
				{
					// Funktion aufrufen, die alles noetige auf den Schirm bringt
					// und die Rolle vorschlaegt.
					rollenVorschlaege = _bertools.askForRolle2(params, xmldomparser, alleInitialisedUser, alleUser);
				}
				else
				{
					// Meldung ausgeben, dass es keine Fehler gab!
					_mainOutput.writeLn("The tests are finished.");
				}

				break;

		}	// End of switch()
	}

	public IVikiToolConsole getConsole() { return this; }
	public IVikiToolGui getGui() { return null; }
	public IVikiToolWeb getWeb() { return null; }

	public IVikiToolBase getBase() { return this; }

	public String getToolName() {			return "Activity-Diagram/Permission Analyzer";	}
	public String getToolDescription() {	return "Activity-Diagram/Permission Analyzer";	}

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

	CommandParameterDescriptor parameterFile = CommandParameterDescriptor.CommandParameterDescriptorFile(CPID_FILE, "File");
	//CommandParameterDescriptor parameterString = CommandParameterDescriptor.CommandParameterDescriptorFile(CPID_String, "String");
	CommandParameterDescriptor parameterString = CommandParameterDescriptor.CommandParameterDescriptorString(CPID_String, "String");

	Vector commands;
	HashMap activities;
	HashMap permissions;
	Document document;
	String[] params = new String[4];
	Vector rollenVorschlaege = new Vector();
	Vector alleInitialisedUser = new Vector();
	XMLDOMParser xmldomparser;
	MDRParser mdrparser;

}