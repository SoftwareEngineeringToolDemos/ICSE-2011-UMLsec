package tum.umlsec.viki.tools.riskFinder;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import open.umlsec.tools.checksystem.gui.SystemVerificationLoader;

import org.apache.log4j.Logger;
import org.omg.uml.UmlPackage;
import org.omg.uml.behavioralelements.activitygraphs.ActivityGraph;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import tum.umlsec.viki.framework.FrameworkBase;
import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.mdr.IMdrContainer;
import tum.umlsec.viki.tools.checkstatic.StaticCheckerBase;

/**
 * 
 * @author Marc Peschke
 * marc.peschke@isst.fraunhofer.de <br>
 * 
 * beinhaltet die Analysen-Aufrufe
 * @extends StaticCheckerBase
 *
 */
public class CheckerRiskfinder extends StaticCheckerBase {
	private static Logger logger = Logger.getLogger("riskFinder.CheckerRiskfinder");
	
	private UmlPackage root;
	private ActivityGraph graph;
	private File zargoFile;
	private File checkedZargoFile;
	private File pgmlFile;
	private File xmiFile;
	public static RiskHelper riskhelper;
	public static XMLHelper xmlhelper;
	public static ZargoHelper zargohelper;
	public static NodeList domResult;
	public static Document securityRepository;
	private Vector<String> patternFounds;
	private static HashMap<String, Vector<String>> candidatesMap;
	public static Document doc;
	// to adjust success probability
	private int hitLimit = 1 ;
	// boolean to turn on/off the filter
	public static boolean wortschatzOn = false;
	public static boolean secReqOn = true;
	
	public CheckerRiskfinder(){
		
	}

	/**
	 * Methode führt Analyse aus und regelt Aufrufe von den einzelnen Teilen des Algorithmus
	 * 
	 * @param mdrContainer
	 * @param Iterator über parameters
	 * @param ITextOutput
	 * @return boolean
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean check(IMdrContainer mdrContainer, Iterator parameters, ITextOutput textOutput) {
	

		
		logger.info("RiskFinder is working.....");
		textOutput.writeLn("Riskfinder is working.....");
		//load zargoFile
		FrameworkBase framework = SystemVerificationLoader.getFramework();
		zargoFile = framework.getModelFile();
		riskhelper = new RiskHelper();
		xmlhelper = new XMLHelper();
		zargohelper = new ZargoHelper();
		root = mdrContainer.getUmlPackage();
		
		// make ResultVector for riskfounds
		patternFounds = new Vector<String>();
		candidatesMap = new HashMap<String, Vector<String>>();
		
		// load security repository
		securityRepository = xmlhelper.parseXMLFile(SystemVerificationLoader.getGui().getRepositoryFile());
		
		//processes all Diagramms and found Activity-Diagramm
		for (Iterator<ActivityGraph> graph_it = root.getActivityGraphs().getActivityGraph().refAllOfClass().iterator(); graph_it.hasNext(); ) {
			graph = graph_it.next();
			textOutput.writeLn("Activity Graph : " + graph.getName());		
			
			//riskhelper.testLionel(riskhelper.getActivity(mdrContainer), textOutput, mdrContainer);
			
			//gets Activities
			riskhelper.printActivity(riskhelper.getActivity(mdrContainer), textOutput);
			
			//gets Pseudostates
			riskhelper.printPseudoStates(riskhelper.getPseudoStates(mdrContainer), textOutput);
			
			//gets Transitions
			riskhelper.printTransition(riskhelper.getTransition(mdrContainer), textOutput);
			
			//gets TaggedValues
			riskhelper.printTaggedValues(graph, textOutput);
						
			//find Risk by keyword
			patternFounds = riskhelper.getRiskByPattern(candidatesMap, riskhelper.getActivity(mdrContainer), SystemVerificationLoader.getGui().getRepositoryHelper().getAllUMLsecPatterns(SystemVerificationLoader.getGui().getRepositoryFile()), hitLimit);
			
		}
		
		// load pgml-File
		if (patternFounds.size()>0){
			logger.info("Risks found!");
			textOutput.writeLn("---");
		   	textOutput.writeLn("Risks found!");
		   	textOutput.writeLn("Please load the checked Zargo-File to examine the results!");
			pgmlFile = zargohelper.loadPgml(zargoFile, textOutput);
			xmiFile = zargohelper.loadXMI(zargoFile, textOutput);
			
			if (pgmlFile.exists() && pgmlFile.length() > 0){
				//DOM from pgml File
				domResult = xmlhelper.pgmlXMLParser(pgmlFile);
				//xmlhelper.pgmlDOMPrinter(domResult, "name", textOutput);
				
				// mark Risks in ArgoUML
				for (int i = 0; i < patternFounds.size(); i++){
					pgmlFile = riskhelper.markRisksOnto(domResult, pgmlFile, textOutput, parameters, patternFounds.elementAt(i));
				}
				// insert InfoNodes
				pgmlFile = riskhelper.insertInfoNodes(xmiFile, domResult ,pgmlFile, candidatesMap, textOutput);
				
				// save Risks
				if (zargohelper.saveZargo(zargoFile, checkedZargoFile, pgmlFile, xmiFile)){
					textOutput.writeLn("Checked .zargo File written: " + zargohelper.getzipFileName());
					logger.info("zargo-File saved: " + zargohelper.getzipFileName());
				}else {
					logger.error("error while saving zargo-File");
				}
			} else {
				logger.error("error. no pgmlFile loaded!");
			}
		}else{
			logger.info("no Risks found.");
		   	textOutput.writeLn("no Risks found.");
		}
		textOutput.writeLn("---");
		return true;
	}
	
	/**
	 * gibt die endgültige Zargo-Datei zurück. <br>
	 * nach Analyse mit veränderten Diagramm
	 * @return File
	 */
	public File getCheckedZargoFile(){		
		return checkedZargoFile;
	}
	
}
