
package tum.umlsec.viki.tools.berechtigungen;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

import org.omg.uml.UmlPackage;
import org.omg.uml.behavioralelements.activitygraphs.ActivityGraph;
import org.omg.uml.behavioralelements.activitygraphs.ActivityGraphsPackage;
import org.omg.uml.behavioralelements.statemachines.Pseudostate;
import org.omg.uml.behavioralelements.statemachines.StateMachinesPackage;
import org.omg.uml.behavioralelements.statemachines.TransitionClass;
import org.omg.uml.foundation.core.CorePackage;

import tum.umlsec.viki.framework.ExceptionProgrammLogicError;
import tum.umlsec.viki.framework.ILogOutput;
import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.mdr.IMdrContainer;
import tum.umlsec.viki.framework.toolbase.CommandParameterDescriptor;
import tum.umlsec.viki.tools.berechtigungen.mdrparser.MDRParser;
import tum.umlsec.viki.tools.berechtigungen.xmlcreater.XMLCreater;
import tum.umlsec.viki.tools.berechtigungen.xmlparser.DOMXMLParser;
import tum.umlsec.viki.tools.berechtigungen.xmlparser.XMLParser;

public class MDRBerechtigungen 
{

	UmlPackage root;
	CorePackage corePackage;
	ActivityGraphsPackage activityPackage;
	StateMachinesPackage stateMachines;
	TransitionClass transitionClasses;
	Pseudostate initialState;
	ActivityGraph actGraph;
	XMLParser xmlparser;
	HashMap activities;
	HashMap permissions;	

	/**
	 * @param mdrContainer
	 * @param _parameters
	 * @param _mainOutput
	 * @param _auxOutput
	 */
	public void check1(IMdrContainer _mdrContainer, Iterator _parameters, ITextOutput _mainOutput, ILogOutput _auxOutput) 
	{
		
		// Stage 1: UML-Diagramm parsen:
		MDRParser mdrparser = new MDRParser(_mainOutput);
		activities = mdrparser.parseUMLDiagram(_mdrContainer);
				
		// Stage 2: XML-File einlesen und parsen:
		File _xmlFile = new File("");
		boolean _foundFile = false; 
		for (; _parameters.hasNext();) {
			CommandParameterDescriptor _parameter = (CommandParameterDescriptor) _parameters.next();
			if(_parameter.getId() == ToolBerechtigungen.CPID_FILE) {
				_xmlFile = _parameter.getAsFile();
				_foundFile = true;
			}
		}
		if(_foundFile == false) {
			throw new ExceptionProgrammLogicError("Required parameter missing");
		}
		
		String filePath = new String("");
		filePath = _xmlFile.getAbsolutePath();
		//_mainOutput.writeLn("filePath ist: " + filePath);
		
		// test-einschub fuer den neuen XML-Parser
		DOMXMLParser domxmlparser = new DOMXMLParser(_mainOutput);
		domxmlparser.loadXMLFile(filePath);
		// Ende vom Test-Einschub
		
		
		xmlparser = new XMLParser();
		//permissions = xmlparser.parse("C:\\new_test3.xml");
		permissions = xmlparser.parse(filePath);
		
		//_mainOutput.writeLn("all permissions data:");
		Iterator it2 = permissions.keySet().iterator();
		while(it2.hasNext())
		{
			String key = (String)it2.next();
			//_mainOutput.writeLn("key: " + key);
			//_mainOutput.writeLn("value: " + permissions.get(key));
		}
		
		// Die einzelnen Daten liegen nun in beiden HashMaps vor und 
		// koennen bearbeitet werden.	
		
		BerTools bertool = new BerTools( _mainOutput);
		
		_mainOutput.writeLn();
		_mainOutput.writeLn("------------ starte Test ------------");
		_mainOutput.writeLn();
		// checken ob genuegend Berechtigungen vorhanden sind:
		_mainOutput.writeLn("--- checkPermissions ---");
		bertool.checkPermissions(activities, permissions);
		
	}
	
	public void check2(IMdrContainer _mdrContainer, Iterator _parameters, ITextOutput _mainOutput, ILogOutput _auxOutput) 
	{
		// Stage 1: UML-Diagramm parsen:
		MDRParser mdrparser = new MDRParser(_mainOutput);
		activities = mdrparser.parseUMLDiagram(_mdrContainer);
		
		//System.out.println("all Activities data:");
		Iterator it = activities.keySet().iterator();
		while(it.hasNext())
		{
			String key = (String)it.next();
			//System.out.println("key: " + key);
			//System.out.println("value: " + activities.get(key));
		}
		
		// Stage 2: XML-File einlesen und parsen:
		File _xmlFile = new File("");
		boolean _foundFile = false; 
		for (; _parameters.hasNext();) {
			CommandParameterDescriptor _parameter = (CommandParameterDescriptor) _parameters.next();
			if(_parameter.getId() == ToolBerechtigungen.CPID_FILE) {
				_xmlFile = _parameter.getAsFile();
				_foundFile = true;
			}
		}
		if(_foundFile == false) {
			throw new ExceptionProgrammLogicError("Required parameter missing");
		}
		
		String filePath = new String("");
		filePath = _xmlFile.getAbsolutePath();
		
		xmlparser = new XMLParser();
		permissions = xmlparser.parse(filePath);
		
		//_mainOutput.writeLn("all permissions data:");
		Iterator it2 = permissions.keySet().iterator();
		while(it2.hasNext())
		{
			String key = (String)it2.next();
			//_mainOutput.writeLn("key: " + key);
			//_mainOutput.writeLn("value: " + permissions.get(key));
		}
		
		// Die einzelnen Daten liegen nun in beiden HashMaps vor und 
		// koennen bearbeitet werden.	
	
		BerTools bertool = new BerTools( _mainOutput);

		_mainOutput.writeLn();
		_mainOutput.writeLn("------------ starte Test ------------");
		_mainOutput.writeLn();
		// checken ob nicht zu viele Berechtigungen vergeben wurden:
		_mainOutput.writeLn("--- checkZuVieleBerechtigungen ---");
		bertool.checktomuchPermissions(activities, permissions);
		
	}
     
	public void check3(IMdrContainer _mdrContainer, Iterator _parameters, ITextOutput _mainOutput, ILogOutput _auxOutput) 
	{
		// ausgehend von dem UML-Diagramm eine XML-Datei
		// mit den notwendigen Berechtigungen erzeugen:
		//System.out.println("--- create XML-SAP-Permissions ---");
		XMLCreater xmlcreater = new XMLCreater(_mainOutput);
		xmlcreater.createXMLPermissions(_mdrContainer);
	}  
	
} // End of class 