package tum.umlsec.viki.tools.riskFinder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.jdom.input.DOMBuilder;
import org.jdom.output.DOMOutputter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
/**
 * 
 * @author Marc Peschke
 * marc.peschke@isst.fraunhofer.de<br>
 * Schnittstelle von Riskfinder zu Repository
 *
 */
public class RepositoryHelper {
	private static Logger logger = Logger.getLogger("riskFinder.RepositoryHelper");
	private GuiHelper guihelper;
	private secReqHelper secReqHelper;
	public static Vector<String> data = new Vector<String>();
	public static Vector<String> g1 = new Vector<String>();
	public static Vector<String> g2 = new Vector<String>();
	public static Vector<String> g3 = new Vector<String>();
	public static Vector<String> g4 = new Vector<String>();
	public static Vector<String> g5 = new Vector<String>();
	public static Vector<String> g6 = new Vector<String>();
	public static Vector<String> m1 = new Vector<String>();
	public static Vector<String> m2 = new Vector<String>();
	public static Vector<String> m3 = new Vector<String>();
	public static Vector<String> m4 = new Vector<String>();
	public static Vector<String> m5 = new Vector<String>();
	public static Vector<String> m6 = new Vector<String>();
	public static Vector<String> m7 = new Vector<String>();
	public static Vector<String> allG = new Vector<String>();
	public static Vector<String> allM = new Vector<String>();
	private static HashMap<String, String> secReqWords;

	public RepositoryHelper(){
		guihelper = new GuiHelper();
		secReqHelper = new secReqHelper();
		secReqWords = secReqHelper.getSecReq();
	}

	/**
	 * resettet alle Vectoren
	 */
	public void resetAllVectors(){

		data.removeAllElements();
		g1.removeAllElements();
		g2.removeAllElements();
		g3.removeAllElements();
		g4.removeAllElements();
		g5.removeAllElements();
		g6.removeAllElements();
		m1.removeAllElements();
		m2.removeAllElements();
		m3.removeAllElements();
		m4.removeAllElements();
		m5.removeAllElements();
		m6.removeAllElements();
		m7.removeAllElements();
	}

	/**
	 * resettet das Repository
	 * @param Repository in xml-Form
	 */
	public void resetRepository(File xmlFile){

		int res = JOptionPane.showConfirmDialog(null, "reset the Repository?");
		if (res == JOptionPane.OK_OPTION){
			logger.info("resetting the Security Repository");
			backupRepository(xmlFile);
			org.jdom.Document doc = makeNewDocument();
			//write the content into xml file
			try {
				org.jdom.output.XMLOutputter oOut = new org.jdom.output.XMLOutputter();
			    oOut.output(doc, new FileOutputStream(xmlFile));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Hilfsmethode. schreibt eine Jdom-Datenstruktur in eine Datei
	 * @param xmlFile
	 * @param jdom.Document
	 */
	public void writeJdomToFile(File xmlFile, org.jdom.Document doc){

		try {
			org.jdom.output.XMLOutputter oOut = new org.jdom.output.XMLOutputter();
		    oOut.output(doc, new FileOutputStream(xmlFile));
		} catch (IOException e) {
			logger.fatal("writeJdom2File IO error");
		}
	}
	
	private org.jdom.Document makeNewDocument(){
	    // create empty document with root element 
		org.jdom.Element root = new org.jdom.Element("UMLsecRepository");
		org.jdom.Document doc = new org.jdom.Document(root);
	    
	    // add  nodes to the document:
	    //  first, create an empty element and add content
		org.jdom.Element pattern = new org.jdom.Element("Pattern");
	    pattern.addContent(new org.jdom.Element("B_1_Übergreifende_Aspekte").addContent(""));
	    pattern.addContent(new org.jdom.Element("B_2_Infrastruktur").addContent(""));
	    pattern.addContent(new org.jdom.Element("B_3_IT-Systeme").addContent(""));
	    pattern.addContent(new org.jdom.Element("B_4_Netze").addContent(""));
	    pattern.addContent(new org.jdom.Element("B_5_Anwendungen").addContent(""));
	    root.addContent(pattern);
		
		return doc;
	}

	/**
	 * macht ein Backup ins Home-Verzeichnis<br>
	 * @param File repositoryFile
	 */
	public void backupRepository(File repositoryFile){

		try {
			logger.info("making Backup of Repository into your Home-Dir");
			File fileTest = new File(System.getProperty("user.home") + File.separator + "backup_" + repositoryFile.getName());
		    if (fileTest.exists() && !fileTest.isDirectory()) {
		    	fileTest.delete();
		    }
			
	        String aktline = "";
	        BufferedReader inFile = new BufferedReader (new FileReader (repositoryFile));
	        BufferedWriter inFile2 = new BufferedWriter (new FileWriter (System.getProperty("user.home") + File.separator + "backup_" + repositoryFile.getName()));
	
	        aktline = inFile.readLine();
	
	        while (aktline!= null)
	        {
	           inFile2.write(aktline);
	           inFile2.write("\n");
	           aktline = inFile.readLine();
	        }
	        inFile.close();
	        inFile2.close();
		}
		 
        catch(Exception ex)
         {
        	logger.fatal("error backup!");
         }
	}

	/**
	 * löscht ein Pattern im Repository
	 * @param File xmlPatternFile
	 * @param String name
	 * @return boolean
	 */
	public boolean delPattern(File XmlPatternFile, String name){

		//String name = (String)JOptionPane.showInputDialog("Name?");
		logger.info("deleting Node " + name + "!");
		try {
			//load xmlfile
			DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder= fact.newDocumentBuilder();
			Document docPatternFile = builder.parse(XmlPatternFile);
			//del pattern in xml
			Element element = (Element) docPatternFile.getElementsByTagName(name).item(0);
			element.getParentNode().removeChild(element);
			docPatternFile.normalize();
			//save xmlfile
			writeJdomToFile(XmlPatternFile, dom2Jdom(docPatternFile));
		} catch (ParserConfigurationException e) {
			logger.fatal("delPattern Parser error");
		} catch (SAXException e) {
			logger.fatal("delPattern sax error");
		} catch (IOException e) {
			logger.fatal("delPattern io error");
		}
		return false;
	}

	/**
	 * macht ein Frame zum Hinzufügen eines Pattern
	 */
	public void makeAddGui(){

		if (guihelper == null){
			guihelper = new GuiHelper();
			guihelper.newPatternFrame();
		}else{
			guihelper.newPatternFrame();
		}
	}
	
	/**
	 * macht ein Frame zum Löschen eines Pattern
	 */	
	public void makeDelGui(){

		if (guihelper == null){
			guihelper = new GuiHelper();
			guihelper.delPatternFrame();
		}else{
			guihelper.delPatternFrame();
		}
	}

	/**
	 * fügt ein Pattern in das XML-Repository File ein
	 * @param File xmlPatternFile
	 * @param Vector<String> allG
	 * @param Vector<String> allM
	 * @param String name
	 * @param String typ
	 */
	public void addUMLsecPatternToXml(File XmlPatternFile, Vector<String> allG, Vector<String> allM, String name, String typ){

		logger.info("adding Node...");
		try {
			//load xmlfile
			DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder= fact.newDocumentBuilder();
			Document docPatternFile = builder.parse(XmlPatternFile);

			//generate XML-Body with Patterninformation
			Node newPattern = docPatternFile.createElement(name.replaceAll(" ", "_"));

			Node G = docPatternFile.createElement("G");
			newPattern.appendChild(G);
			Node M = docPatternFile.createElement("M");
			newPattern.appendChild(M);
			
		    
			//allG
			for (int i=0; i<allG.size(); i++){	
				Node einG = docPatternFile.createElement(allG.elementAt(i).toString().replaceAll(" ", "_"));
				G.appendChild(einG);
			}
			//allM
			for (int i=0; i<allM.size(); i++){
				Node einM = docPatternFile.createElement(allM.elementAt(i).toString().replaceAll(" ", "_"));
				M.appendChild(einM);
			}

		//add XML doc to xmlFile
		NodeList nList = docPatternFile.getElementsByTagName("UMLsecRepository");
		for (int i = 0; i < nList.getLength(); i++){
			//UMLsecRepository
			NodeList jList = nList.item(i).getChildNodes();
				//Pattern
				for (int j = 0 ; j < jList.getLength(); j++){
					NodeList aList = jList.item(i).getChildNodes();
					//Typ
					for (int a = 0 ; a < aList.getLength(); a++){
						Node aNode = aList.item(a);
						// add new Node to Repository
						if (aNode.getNodeType() == Node.ELEMENT_NODE && aNode.getNodeName().equalsIgnoreCase(typ)) { 
							logger.info("...done!");
							aNode.appendChild(newPattern);
						}
					}
				}
		}
		writeJdomToFile(XmlPatternFile, dom2Jdom(docPatternFile));
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}

	/**
	 * liefert eine Liste alles Pattern im xml Repository File
	 * @param File xmlPatternFile
	 * @return Vector<String> patternListe
	 */
	public Vector<String> getPatternList(File XmlPatternFile){

		Vector<String>patternList = new Vector<String>();
		//get pattern in xml
		try {
			//load xmlfile
			DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder= fact.newDocumentBuilder();
			Document docPatternFile = builder.parse(XmlPatternFile);
			//del pattern in xml
			NodeList nList = docPatternFile.getElementsByTagName("UMLsecRepository");
			for (int i = 0; i < nList.getLength(); i++){
				//UMLsecRepository
				NodeList jList = nList.item(i).getChildNodes();
					//Pattern
					for (int j = 0 ; j < jList.getLength(); j++){
						NodeList aList = jList.item(j).getChildNodes();
						//Typ
						for (int a = 0 ; a < aList.getLength(); a++){
							NodeList pList = aList.item(a).getChildNodes();
							for (int d = 0 ; d < pList.getLength(); d++){
								Node dNode = pList.item(d);
								if(dNode.getNodeType() == Node.ELEMENT_NODE){
									patternList.add(dNode.getNodeName());
								}
							}
						}
					}	
			}
		
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return patternList;
	}

	/**
	 * Hilfsmethode, die ein jdom Doc in ein dom Doc umwandelt
	 * @param dom.Document doc
	 * @return jdom.Document jdoc
	 */
	public org.jdom.Document dom2Jdom(org.w3c.dom.Document dom){

		try {
            DOMBuilder builder = new DOMBuilder();
            org.jdom.Document document = builder.build(dom);
            return document;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
	}

	/**
	 * Hilfsmethode, die ein dom Doc in ein jdom Doc umwandelt
	 * @param jdom.Document jdoc
	 * @return dom.Document doc
	 */
	public org.w3c.dom.Document jdom2Dom(org.jdom.Document document){

		try {
            DOMOutputter output = new DOMOutputter();
            org.w3c.dom.Document dom = output.output(document);
            return dom;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
	}

	/**
	 * liefert alle SicherheitsPattern, die im SicherheitsRepository gespeichert sind.
	 * @param File XmlPatternFile
	 * @return Vector<UMLsecPattern> allPatterns
	 */
	@SuppressWarnings("unchecked")
	public Vector<UMLsecPattern> getAllUMLsecPatterns(File XmlPatternFile){

		Vector<UMLsecPattern> allPatternObjects = new Vector<UMLsecPattern>();
		Vector<String> allPatterns = new Vector<String>();
		String name ="";
		String typ ="";
		Vector<String> keywords = new Vector<String>();
		Vector<String> keywordsTmp = new Vector<String>();
		Vector<String> solutions = new Vector<String>();
		Vector<String> risks = new Vector<String>();
		int riskFactor = 1;
		
		// get Patterns from XmlFile
		allPatterns = getPatternList(XmlPatternFile);  
		
		for (int i = 0 ; i < allPatterns.size(); i++){
			
			//get name
			name = allPatterns.elementAt(i);
			try {		
				DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder= fact.newDocumentBuilder();
				Document docPatternFile = builder.parse(XmlPatternFile);
				Element element = (Element) docPatternFile.getElementsByTagName(name).item(0);
	
				//get typ
				typ = element.getParentNode().getNodeName();
				//get risks
				NodeList gList = element.getElementsByTagName("G");
				NodeList addGList = gList.item(0).getChildNodes();
					for (int g=0; g< addGList.getLength();g++){	
						if (addGList.item(g).getNodeType() == Node.ELEMENT_NODE) {
							risks.add(addGList.item(g).getNodeName());
						}
					}
				//get solutions
				NodeList mList = element.getElementsByTagName("M");
				NodeList addMList = mList.item(0).getChildNodes();
					for (int m=0; m< addMList.getLength();m++){
						if (addMList.item(m).getNodeType() == Node.ELEMENT_NODE) {
							solutions.add(addMList.item(m).getNodeName());
						}
					}
				
			} catch (Exception ex) {
				logger.fatal("getAllUMLsecPatterns error");
	        }

			//make keyWords vector
			keywordsTmp.addAll(getAllWords(risks));
			keywordsTmp.addAll(getAllWords(solutions));
			//filter for real security words :-)
			if (CheckerRiskfinder.secReqOn){
				keywords = filterKeywords(keywordsTmp, secReqWords);
			}else{
				keywords = keywordsTmp;
			}
			
			//generate pattern objects and add to vector
			allPatternObjects.add(new UMLsecPattern(name, typ, (Vector<String>) keywords.clone(), (Vector<String>) solutions.clone(), (Vector<String>) risks.clone(), riskFactor));
		
			//reset Vectors
			keywords.removeAllElements();
			solutions.removeAllElements();
			risks.removeAllElements();
		}
		return allPatternObjects;
	}
	
	private Vector<String> filterKeywords(Vector<String> keywordsTmp, HashMap<String, String> secWords) {
		Vector<String> tmp = new Vector<String>();
		for (int i = 0; i < keywordsTmp.size(); i++){
			//look for every keyword, if it is in secReq and if its riskvalue is bigger 0.8
			if (secWords.containsKey(keywordsTmp.get(i))){
				String key = keywordsTmp.get(i);
				String value = secWords.get(keywordsTmp.get(i));
				int iValue = Integer.parseInt(value);
				if(iValue > 0.8){
					//secReq told us, this word is most likely a riskWord
					if(!tmp.contains(keywordsTmp.get(i)))
						tmp.add(keywordsTmp.get(i));
				}
			}
		}
		return tmp;
	}

	private Vector<String> getAllWords(Vector<String> keys){
		
		Vector<String>words=new Vector<String>();
		//look in Hashmaps G1 - M6 for key
		//if contains key, get MapValue and put every word into the vector

		for (int i = 0; i < keys.size(); i++){
		
			if(guihelper.getBsicontent().getGefaehrdungsKatalog().get("G 1 Höhere Gewalt").containsKey(keys.get(i).replace("_", " "))){
				words.addAll(getWordsFromString(guihelper.getBsicontent().getGefaehrdungsKatalog().get("G 1 Höhere Gewalt").get(keys.get(i).replace("_", " "))));
			}
			if(guihelper.getBsicontent().getGefaehrdungsKatalog().get("G 2 Organisatorische Mängel").containsKey(keys.get(i).replace("_", " "))){
				words.addAll(getWordsFromString(guihelper.getBsicontent().getGefaehrdungsKatalog().get("G 2 Organisatorische Mängel").get(keys.get(i).replace("_", " "))));
			}
			if(guihelper.getBsicontent().getGefaehrdungsKatalog().get("G 3 Menschliche Fehlhandlung").containsKey(keys.get(i).replace("_", " "))){
				words.addAll(getWordsFromString(guihelper.getBsicontent().getGefaehrdungsKatalog().get("G 3 Menschliche Fehlhandlung").get(keys.get(i).replace("_", " "))));
			}
			if(guihelper.getBsicontent().getGefaehrdungsKatalog().get("G 4 Technisches Versagen").containsKey(keys.get(i).replace("_", " "))){
				words.addAll(getWordsFromString(guihelper.getBsicontent().getGefaehrdungsKatalog().get("G 4 Technisches Versagen").get(keys.get(i).replace("_", " "))));
			}
			if(guihelper.getBsicontent().getGefaehrdungsKatalog().get("G 5 Vorsätzliche Handlungen").containsKey(keys.get(i).replace("_", " "))){
				words.addAll(getWordsFromString(guihelper.getBsicontent().getGefaehrdungsKatalog().get("G 5 Vorsätzliche Handlungen").get(keys.get(i).replace("_", " "))));
			}
			if(guihelper.getBsicontent().getGefaehrdungsKatalog().get("G 6 Datenschutz").containsKey(keys.get(i).replace("_", " "))){
				words.addAll(getWordsFromString(guihelper.getBsicontent().getGefaehrdungsKatalog().get("G 6 Datenschutz").get(keys.get(i).replace("_", " "))));
			}

			if(guihelper.getBsicontent().getMassnahmenKatalog().get("M 1 Infrastruktur").containsKey(keys.get(i).replace("_", " "))){
				words.addAll(getWordsFromString(guihelper.getBsicontent().getMassnahmenKatalog().get("M 1 Infrastruktur").get(keys.get(i).replace("_", " "))));
			}
			if(guihelper.getBsicontent().getMassnahmenKatalog().get("M 2 Organisation").containsKey(keys.get(i).replace("_", " "))){
				words.addAll(getWordsFromString(guihelper.getBsicontent().getMassnahmenKatalog().get("M 2 Organisation").get(keys.get(i).replace("_", " "))));
			}
			if(guihelper.getBsicontent().getMassnahmenKatalog().get("M 3 Personal").containsKey(keys.get(i).replace("_", " "))){
				words.addAll(getWordsFromString(guihelper.getBsicontent().getMassnahmenKatalog().get("M 3 Personal").get(keys.get(i).replace("_", " "))));
			}
			if(guihelper.getBsicontent().getMassnahmenKatalog().get("M 4 Hardware und Software").containsKey(keys.get(i).replace("_", " "))){
				words.addAll(getWordsFromString(guihelper.getBsicontent().getMassnahmenKatalog().get("M 4 Hardware und Software").get(keys.get(i).replace("_", " "))));
			}
			if(guihelper.getBsicontent().getMassnahmenKatalog().get("M 5 Kommunikation").containsKey(keys.get(i).replace("_", " "))){
				words.addAll(getWordsFromString(guihelper.getBsicontent().getMassnahmenKatalog().get("M 5 Kommunikation").get(keys.get(i).replace("_", " "))));
			}
			if(guihelper.getBsicontent().getMassnahmenKatalog().get("M 6 Notfallvorsorge").containsKey(keys.get(i).replace("_", " "))){
				words.addAll(getWordsFromString(guihelper.getBsicontent().getMassnahmenKatalog().get("M 6 Notfallvorsorge").get(keys.get(i).replace("_", " "))));
			}
			if(guihelper.getBsicontent().getMassnahmenKatalog().get("M 7 Datenschutz").containsKey(keys.get(i).replace("_", " "))){
				words.addAll(getWordsFromString(guihelper.getBsicontent().getMassnahmenKatalog().get("M 7 Datenschutz").get(keys.get(i).replace("_", " "))));
			}
		}
		return words;
	}
	
	private Vector<String> getWordsFromString(String text){
		
		Vector<String> words = new Vector<String>();
		if (text.contains(" ")){
			String[]tmp = text.split(" ");
			for (int i = 0 ; i < tmp.length; i++){
				if(tmp[i].toString().contains("-")){
					words.add(delSatzzeichenFrontEnd(tmp[i].toString()));
					String[]inTmp = tmp[i].toString().split("-");
					for (int in = 0; in < inTmp.length; in++){
						if(words.contains(inTmp[in].toString())){
						}else{
							words.add(delSatzzeichenFrontEnd(inTmp[in].toString()));
						}
					}
				} else {
					if(words.contains(tmp[i].toString())){
					}else{
						words.add(delSatzzeichenFrontEnd(tmp[i].toString()));
					}
				}
			}
		}else{
			if(!words.contains(text))
				text = delSatzzeichenFrontEnd(text);
				words.add(text);
		}	
		return words;
	}

	private String delSatzzeichenFrontEnd(String word) {
		//delete ; etc from word
		String[] zeichen = {";",",",":","."};
		for(int z = 0 ; z < zeichen.length; z++){
			if (word.startsWith(zeichen[z])){
				word = word.substring(1);
			}
			if (word.endsWith(zeichen[z])){
				word = word.substring(0, word.length()-1);
			}
		}
		return word;
	}

	/**
	 * Hilfmethode zum temporären speichern eines Strings
	 * @param String worte
	 */
	public void writeToTempFile(String worte){
	
	FileWriter fw = null;
	try {
	  fw = new FileWriter(System.getProperty("user.home") + File.separator + "words_BSI.txt", true); // Daten anhängen
	  fw.append(worte + " \n");
	}
	catch (IOException e) {
	  e.printStackTrace();
	}
	finally {
	  if (fw != null) try { fw.close(); } catch (IOException e) {}
	}
	
	
	}	
	
}
