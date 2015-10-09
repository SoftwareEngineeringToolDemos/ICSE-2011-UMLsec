/*
 * Created on 22.07.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package tum.umlsec.viki.tools.berechtigungen2;

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

import tum.umlsec.viki.framework.ILogOutput;
import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.mdr.IMdrContainer;
import tum.umlsec.viki.tools.berechtigungen.mdrparser.MDRParser;
import tum.umlsec.viki.tools.berechtigungen.xmlcreater.XMLCreater;
import tum.umlsec.viki.tools.berechtigungen.xmlparser.XMLParser;
import tum.umlsec.viki.tools.berechtigungen2.umlparser.UmlParser;


/**
 * @author josef
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MDRBerechtigungen2 
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
		
		// Stage 1: parse UML-Diagramm:
		MDRParser mdrparser = new MDRParser(_mainOutput);
		activities = mdrparser.parseUMLDiagram(_mdrContainer);
		UmlParser umlParser;
		
		umlParser = new UmlParser(_mainOutput);
		umlParser.createXmlDocument();
		
		// build HashMap with XML-data:
		umlParser.parseUmlModel(_mdrContainer);
		permissions = umlParser.parse();
		
		//_mainOutput.writeLn("all permissions data:");
		Iterator it2 = permissions.keySet().iterator();
		while(it2.hasNext())
		{
			String key = (String)it2.next();
			//_mainOutput.writeLn("key: " + key);
			//_mainOutput.writeLn("value: " + permissions.get(key));
		}
		
		// Two HashMaps contain the whole data. Now the data can be processed.
		
		BerTools2 bertool = new BerTools2( _mainOutput);
		
		_mainOutput.writeLn();
		_mainOutput.writeLn("------------ test begins now ------------");
		_mainOutput.writeLn();
		// are there enough permissions?
		_mainOutput.writeLn("--- checkPermissions ---");
		bertool.checkPermissions(activities, permissions);
		
	}
	
	public void check2(IMdrContainer _mdrContainer, Iterator _parameters, ITextOutput _mainOutput, ILogOutput _auxOutput) 
	{
		// Stage 1: parse UML-Diagramm:
		MDRParser mdrparser = new MDRParser(_mainOutput);
		activities = mdrparser.parseUMLDiagram(_mdrContainer);
		UmlParser umlParser;
		
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
		
		umlParser = new UmlParser(_mainOutput);
		umlParser.createXmlDocument();
		
		// build HashMap with XML-data:
		umlParser.parseUmlModel(_mdrContainer);
		permissions = umlParser.parse();
		
		//_mainOutput.writeLn("all permissions data:");
		Iterator it2 = permissions.keySet().iterator();
		while(it2.hasNext())
		{
			String key = (String)it2.next();
			//_mainOutput.writeLn("key: " + key);
			//_mainOutput.writeLn("value: " + permissions.get(key));
		}
		
		//Two HashMaps contain the whole data. Now the data can be processed.
		BerTools2 bertool = new BerTools2( _mainOutput);

		_mainOutput.writeLn();
		_mainOutput.writeLn("------------ test begins now ------------");
		_mainOutput.writeLn();
		// are there enough permissions?
		_mainOutput.writeLn("--- checkZuVieleBerechtigungen ---");
		bertool.checktomuchPermissions(activities, permissions);
		
	}
     
	public void check3(IMdrContainer _mdrContainer, Iterator _parameters, ITextOutput _mainOutput, ILogOutput _auxOutput) 
	{
		// create a xml-file with all necessary permissions:
		//System.out.println("--- create XML-SAP-Permissions ---");
		XMLCreater xmlcreater = new XMLCreater(_mainOutput);
		xmlcreater.createXMLPermissions(_mdrContainer);
	}
}
