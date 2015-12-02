package tum.umlsec.viki.tools.riskFinder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.omg.uml.UmlPackage;
import org.omg.uml.behavioralelements.activitygraphs.ActionState;
import org.omg.uml.behavioralelements.activitygraphs.ActionStateClass;
import org.omg.uml.behavioralelements.activitygraphs.ActivityGraph;
import org.omg.uml.behavioralelements.activitygraphs.ActivityGraphsPackage;
import org.omg.uml.behavioralelements.commonbehavior.CallActionClass;
import org.omg.uml.behavioralelements.statemachines.Pseudostate;
import org.omg.uml.behavioralelements.statemachines.PseudostateClass;
import org.omg.uml.behavioralelements.statemachines.StateMachinesPackage;
import org.omg.uml.behavioralelements.statemachines.Transition;
import org.omg.uml.behavioralelements.statemachines.TransitionClass;
import org.omg.uml.foundation.core.TaggedValue;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.mdr.IMdrContainer;
import tum.umlsec.viki.framework.mdr.IdNameList;
/**
 * 
 * @author Marc Peschke
 * marc.peschke@isst.fraunhofer.de <br>
 * Beinhaltet Methoden zur Analyse und Manipulation eines UML-Diagramms
 *
 */
public class RiskHelper {

	private UmlPackage root;
	private ActivityGraphsPackage activityPackage;
	private ActionStateClass actionstateClasses;
	private ActionState actionState;
	private StateMachinesPackage stateMachines;
	private PseudostateClass pseudostateClasses;
	private TransitionClass transitionClasses;
	private WortschatzHelper wortschatz;
	private boolean filterOn = false;
	private static Logger logger=Logger.getLogger("riskFinder.RiskHelper");
	// atm not used
	@SuppressWarnings("unused")
	private Pseudostate pseudoState;
	@SuppressWarnings("unused")
	private Transition transition;
		
	public RiskHelper(){
		wortschatz = new WortschatzHelper();
	}

	/**
	 * liefert alle Aktivitaeten eines UML-Diagramms
	 * @param mdrContainer
	 * @return ActionstateClass
	 */
	public ActionStateClass getActivity(IMdrContainer mdrContainer){
		root = mdrContainer.getUmlPackage();
		activityPackage = (ActivityGraphsPackage)root.getActivityGraphs();
		actionstateClasses =(ActionStateClass)activityPackage.getActionState();
		return actionstateClasses;
	}

	/**
	 * gibt Aktivitaeten aus
	 * @param ActionStateClasses
	 * @param ITextOutput
	 */
	public void printActivity(ActionStateClass actionstateClasses, ITextOutput textOutput){
		textOutput.writeLn("There are " + actionstateClasses.refAllOfClass().size() + " Activities.");
		for(Iterator activi_it = actionstateClasses.refAllOfClass().iterator(); activi_it.hasNext();){
			actionState = (ActionState)activi_it.next();
				//textOutput.write( "" + actionState.getName() + "|");

		}
	}
	
	/**
	 * liefert alle PseudoStateClasses eines UML-Diagramms
	 * @param mdrContainer
	 * @return PseudoStateClasses
	 */	
	public PseudostateClass getPseudoStates(IMdrContainer mdrContainer){
		root = mdrContainer.getUmlPackage();
		activityPackage = (ActivityGraphsPackage)root.getActivityGraphs();
		stateMachines = (StateMachinesPackage)activityPackage.getStateMachines();
		pseudostateClasses = (PseudostateClass)stateMachines.getPseudostate();
		return pseudostateClasses;
	}

	/**
	 * gibt PseudoStates aus
	 * @param PseudoStateClasses
	 * @param ITextOutput
	 */
	public void printPseudoStates(PseudostateClass pseudostateClasses, ITextOutput textOutput){
		textOutput.writeLn("There are " + pseudostateClasses.refAllOfClass().size() + " Pseudostates.");
		for(Iterator pseudo_it = pseudostateClasses.refAllOfClass().iterator(); pseudo_it.hasNext();){
			pseudoState = (Pseudostate)pseudo_it.next();
				//textOutput.write( "" + actionState.getName() + "|");
		}
	}	

	/**
	 * liefert alle Transitionen  eines UML-Diagramms
	 * @param mdrContainer
	 * @return TransitionClass
	 */	
	public TransitionClass getTransition(IMdrContainer mdrContainer){
		root = mdrContainer.getUmlPackage();
		activityPackage = (ActivityGraphsPackage)root.getActivityGraphs();
		stateMachines = (StateMachinesPackage)activityPackage.getStateMachines();
		transitionClasses = (TransitionClass)stateMachines.getTransition();
		return transitionClasses;
	}
	
	/**
	 * gibt Transitionen aus
	 * @param TransitionClasses
	 * @param ITextoutput
	 */	
	public void printTransition(TransitionClass transitionClasses, ITextOutput textOutput){
		textOutput.writeLn("There are " + transitionClasses.refAllOfClass().size() + " Transitions.");
		for(Iterator trans_it= transitionClasses.refAllOfClass().iterator(); trans_it.hasNext();){
			transition=(Transition)trans_it.next();
			//textOutput.write( "" + transition.getName() + "|");
		}	
	}

	/**
	 * gibt Tagged Values aus
	 * @param ActivityGraph graph
	 * @param ITextOutput
	 */	
	public void printTaggedValues(ActivityGraph graph,ITextOutput textOutput){
		int count = 0;
		for(Iterator<TaggedValue> tag_it = graph.getTaggedValue().iterator(); tag_it.hasNext(); ) {
			@SuppressWarnings("unused")
			TaggedValue tag = tag_it.next();
			//textOutput.writeLn(tag.getType().getName());
			count++;
		}
		textOutput.writeLn("There are " + count +" Tagged values.");
	}

	/**
	 * makiert ein Risiko. <br>
	 * @param NodeList nList
	 * @param File pgmlFile
	 * @param ITextOutput
	 * @param Iterator parameter
	 * @param String pattern
	 * @return File pgmlFile
	 */
	public File markRisksOnto ( NodeList nList, File pgmlF, ITextOutput textOutput, Iterator parameters, String pattern){
		//get argoId for critical Activity
		IdNameList idListe = IdNameList.getInstance();
		String argoId = "";
		try{
			argoId = idListe.getArgoId(pattern, "ActionState" );
		}catch ( NoSuchElementException e){
			argoId = "";
		}
		
		if (argoId.equals("")|| argoId == null){
		   	return pgmlF;
		}else {
			String figure ="";		    

			//getFigToAlter
			figure = CheckerRiskfinder.xmlhelper.getFigToAlter(nList, argoId, textOutput);
			
			//alterAttribute
			nList = CheckerRiskfinder.xmlhelper.alterAttribute(nList, figure, textOutput);
				
			//saveXMLFile
			pgmlF = CheckerRiskfinder.xmlhelper.dom2File(nList, pgmlF);
			
			return pgmlF;
		}
	}

	/**
	 * Body für eine weitere Analysemethode.<br>
	 * hat keine Verwendung zur Zeit.
	 */
	public Vector<String> getRiskByStrukture(ActionStateClass actionStateClass, Document doc){
		Vector<String> allStructures = new Vector<String>();
		Vector<String> foundRisks = new Vector<String>();
		
		//load all Infos from XML
		NodeList nList = doc.getElementsByTagName("UMLsecRepository");
		// Elemente unter repository
		for (int i = 0; i < nList.item(0).getChildNodes().getLength(); i++) {
		Node iNode = nList.item(0).getChildNodes().item(i);
			if (iNode.getNodeType() == Node.ELEMENT_NODE && iNode.getNodeName().equalsIgnoreCase("structure")) {
				// Elemte unter keywords/strucutre
				for (int j = 0; j < iNode.getChildNodes().getLength(); j++) {
					Node jNode = iNode.getChildNodes().item(j);
					if (jNode.getNodeType() == Node.ELEMENT_NODE) {
						Element eElement = (Element) jNode;
						 allStructures.add(eElement.getNodeName());
					}
				}
			}
		}	
		
		//look for critical structure in diagramm
		// get the pseudostateclasses. add childs/parents to vector (Dumy methode)
		
		return foundRisks;
	}
	/**
	 * Hauptmethode! <br>
	 * sucht nach Risiken mittels Sicherheits-Pattern
	 * @param HashMap<String, Vector<String>> candidatesMap
	 * @param ActionStateClass actionStateClass
	 * @param Vector<UMLsecPattern> UMLsecPatternVector 
	 * @param int hitLimit
	 * @return Vector<String> results
	 * 
	 */
	public Vector<String> getRiskByPattern(HashMap<String, Vector<String>> candidatesMap, ActionStateClass actionStateClass, Vector<UMLsecPattern> UMLsecPatternVector, int hitLimit) {

		Vector<String> candidates = new Vector<String>();
		Vector<String> synonymResult = new Vector<String>();
		Vector<String> keywords = new Vector<String>();
		int hits = 0;
		
		// look in every Activity
		for(Iterator activi_it = actionStateClass.refAllOfClass().iterator(); activi_it.hasNext();){
			actionState = (ActionState)activi_it.next();
			synonymResult.removeAllElements();
			keywords.removeAllElements();
			
				// get name and build partial names/words
				//search for "-" " " in Activityname and split
				if (actionState.getName().contains(" ")){
					String [] tmp1 = actionState.getName().split(" ");
					for (int i = 0; i < tmp1.length; i++){
						if (tmp1[i].contains("-")){
							if(notInBlacklist(tmp1[i], getStopwords())){
								keywords.add(tmp1[i]);
							}
							String [] tmp2 = actionState.getName().split("-");
							for (int j = 0; j < tmp2.length; j++){
								if(notInBlacklist(tmp2[j], getStopwords())){
									keywords.add(tmp2[j]);
								}
							}
						}else{
							if(notInBlacklist(tmp1[i], getStopwords())){
								keywords.add(tmp1[i]);
							}
						}
					}
				}
				
				if (CheckerRiskfinder.wortschatzOn){
					//wortmenge erweitern durch synonym und wortstamm
					if (keywords != null && keywords.size() > 0){
						for (int i = 0 ; i< keywords.size(); i++){
							synonymResult.addAll(askWortschatz(keywords.get(i)));
						}
					}
					
				}else{
					synonymResult.addAll(keywords);
				}
				// für jedes wort...
				for (int sy = 0; sy < synonymResult.size(); sy++ ){
					//stopwords rausfiltern...
					if(notInBlacklist(synonymResult.get(sy), getStopwords())){
						for (int p = 0; p < UMLsecPatternVector.size(); p++){
							//... und jedem wort im keyVector des Pattern....
							Vector<String> keywordsPattern = UMLsecPatternVector.get(p).getKeywords();							
							for (int co = 0; co < keywordsPattern.size(); co++ ){
								//...vergleichen... 
								if (synonymResult.get(sy).toLowerCase().equalsIgnoreCase(keywordsPattern.get(co).toLowerCase())){				
									// ... dann treffer und funde merken wenn hitSchranke überschritten
									hits++;
									if(hits > hitLimit){
										if (!candidatesMap.containsKey(actionState.getName())){
											// wenn activity nicht in keys
											Vector<String> newVector = new Vector<String>();
											newVector.add(UMLsecPatternVector.get(p).getName());
											candidatesMap.put(actionState.getName(), newVector);
											candidates.add(actionState.getName());
										}
											// wenn ja dann
										else {
											// pattern nicht mehrfach einsortieren bei multiHits
											if(!candidatesMap.get(actionState.getName()).contains(UMLsecPatternVector.get(p).getName())){
												candidatesMap.get(actionState.getName()).add(UMLsecPatternVector.get(p).getName());
												candidates.add(actionState.getName());
											}
										}
									}
								}
							}
						}
					}
				}		
		}
		return candidates;
	}
	
	@SuppressWarnings("unused")
	private Vector<String> askWortschatz(String word){
		Vector<String> foundWords = new Vector<String>();
		
		
		foundWords.add(word);
		// Synonyms and Cooccurrences from Leipzig
		// good feature, but not necessary
		wortschatz.askWortschatz(word);   

		//add to Vector. empty till wortschatz is not working
		foundWords.addAll(wortschatz.getSynonyms());
		foundWords.addAll(wortschatz.getCooccurrences());
		
		return foundWords;
	}
	
	private Vector<String> getStopwords(){
		Vector<String> blacklist = new Vector<String>();
		String dir ="bin"+ File.separator + "tum"+ File.separator +"umlsec"+ File.separator +"viki"+ File.separator +"tools"+ File.separator +"riskFinder"+ File.separator + "stopwords" + File.separator ;
		String aktline = "";
		BufferedReader inFile = null;
		try {
			//ger
        	inFile = new BufferedReader (new FileReader (new File(dir + "stopwords.ger")));
			aktline = inFile.readLine();
	        while (aktline!= null)
	        {
	        	blacklist.add(aktline);
	        	aktline = inFile.readLine();
	        }
	        inFile.close();
	        
	        //eng
	        inFile = new BufferedReader (new FileReader (new File(dir + "stopwords.eng")));
			aktline = inFile.readLine();
	        while (aktline!= null)
	        {
	        	blacklist.add(aktline);
	        	aktline = inFile.readLine();
	        }
	        inFile.close();
        } catch (IOException e) {
        	logger.fatal("stopwords io error");
		}
        return blacklist;
	}
	
	private boolean notInBlacklist(String word, Vector<String> blacklist){
		
        for (int b=0; b < blacklist.size(); b++){
			if (blacklist.get(b).equalsIgnoreCase(word)){
				return false;
			}
		}
		return true;
	}

	/**
	 * ruft das Einfügen der Notiz in die xml-Dateien auf
	 * @param File xmiFile
	 * @param Nodelist nList
	 * @param File pgmlFile
	 * @param HashMap<String, Vector<String>> candidatesMap
	 * @param ITextOutput
	 * @return File pgmlFile
	 */
	public File insertInfoNodes(File xmiFile, NodeList nList, File pgmlFile, HashMap<String, Vector<String>> candidatesMap, ITextOutput textOutput) {
		//alterAttribute
		nList = CheckerRiskfinder.xmlhelper.insertInfoNode(nList, candidatesMap, textOutput);
			
		//saveXMLFile
		pgmlFile = CheckerRiskfinder.xmlhelper.dom2File(nList, pgmlFile);
		
		//add comment to xmi
		xmiFile = CheckerRiskfinder.xmlhelper.addCommentToXMI(xmiFile, candidatesMap);
		
		return pgmlFile;
	}
	
/*	public void testLionel(ActionStateClass actionstateClasses, ITextOutput textOutput, IMdrContainer mdrContainer){
		textOutput.writeLn("There are " + actionstateClasses.refAllOfClass().size() + " Activities.");

//		for(Iterator activi_it = actionstateClasses.refAllOfClass().iterator(); activi_it.hasNext();){
//			actionState = (ActionState)activi_it.next();
//				textOutput.write( "" + actionState.getName() + "|\n");
//				textOutput.write( "" + actionState.getEntry() + "|\n");
//				textOutput.write( "" + actionState.getEntry().getName() + "|\n");
//				textOutput.write( "--------\n");				
//		}

		root = mdrContainer.getUmlPackage();
		activityPackage = (ActivityGraphsPackage)root.getActivityGraphs();
		actionstateClasses =(ActionStateClass)activityPackage.getActionState();
		
		CallActionClass calls = root.getCommonBehavior().getCallAction();
		Object[] refCalls = calls.refAllOfClass().toArray();
		textOutput.write( "" + refCalls.length + "|\n");
		for (int i = 0; i < refCalls.length; i++){
			textOutput.write( "" + refCalls[i].getClass().getName() + "|\n");
		}
		
	}
*/
		
}
