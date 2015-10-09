package tum.umlsec.viki.tools.riskFinder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import tum.umlsec.viki.framework.ITextOutput;
/**
 * 
 * @author Marc Peschke
 * marc.peschke@isst.fraunhofer.de<br>
 * 
 * Klasse mit Methoden zum XML-Handling
 *
 */
public class XMLHelper {
	
	private static Logger logger = Logger.getLogger("riskFinder.XmlHelper");
	
	private DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
	private DocumentBuilder builder;
	private Document doc;
	private String id = "128-0-1-1--5dddcd07:12843487ed8:-8000:0000000000000DFE"; //would be cool if not fix
	
	public XMLHelper(){
		
	}

	/**
	 * ändert ein attribut einer Figur. Hier speziell die Farbe. je Aufruf eine Farbe weiter.
	 * @param NodeList nList
	 * @param String name
	 * @param ITextOutput
	 * @return NodeList nList
	 */
	public NodeList alterAttribute(NodeList nList, String name, ITextOutput textOutput){

		// alter fillcolor in complete pgml-File, so we have a valid zargo Layou-File
		for (int i = 0; i < nList.getLength(); i++) { 
			for (int j = 0; j < nList.item(i).getChildNodes().getLength(); j++) {
				Node nNode = nList.item(i).getChildNodes().item(j);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
			          Element eElement = (Element) nNode;
			          if (eElement.getAttribute("name").equalsIgnoreCase(name)){
			        	if(!eElement.getAttribute("fillcolor").equalsIgnoreCase("red")){  
				        	if(eElement.getAttribute("fillcolor").equalsIgnoreCase("255 200 0")){
				        		eElement.setAttribute("fillcolor", "red");
				        		//logger.info("altering fillcolor to red in " + name + "!");
				        	}
				        	if(eElement.getAttribute("fillcolor").equalsIgnoreCase("255 255 0")){
				        		eElement.setAttribute("fillcolor", "255 200 0");
				        		//logger.info("altering fillcolor to orange in " + name + "!");
				        	}
				        	if(eElement.getAttribute("fillcolor").equalsIgnoreCase("white")){
				        		eElement.setAttribute("fillcolor", "255 255 0");
				        		//logger.info("altering fillcolor to yellow in " + name + "!");
				        	}
			          	}
			          }
			    }
			}
		}	
		return nList;
	}

	/**
	 * liefert den Wert eines Attributes
	 * @param Nodelist nList
	 * @param String nodeName
	 * @param String value
	 * @param iTextValue
	 * @return String value
	 */
	public String getAttribute(NodeList nList, String nodeName, String value, ITextOutput textOutput){

		String attrValue="";
		for (int i = 0; i < nList.getLength(); i++) {
			Node nNode = nList.item(i);	    
		       if (nNode.getNodeType() == Node.ELEMENT_NODE) {
		          Element eElement = (Element) nNode;
		          if (eElement.getNodeName().equalsIgnoreCase(nodeName)){
		        	  attrValue =  eElement.getAttribute(value);  
		          }
		       }
		}
		return attrValue;
	}

	/**
	 * speichert ein dom Dokument in ein File
	 * @param File xmlFile
	 * @param Document domDoc
	 */
	public void saveXMLFile(File xmlFile, Document domDoc){

		try {
		  TransformerFactory transformerFactory = TransformerFactory.newInstance();
		  Transformer transformer = transformerFactory.newTransformer();
		  DOMSource source = new DOMSource(domDoc);
		  StreamResult result =  new StreamResult(xmlFile);
		  transformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			System.out.println("saveXMLFile TransformerConfig Error");
		} catch (TransformerException e) {
			System.out.println("saveXMLFile Transformer Error");
		}
		 
	}

	/**
	 * macht aus einer xml-Datei ein Document
	 * @param File xmlFile
	 * @return Document doc
	 */
	public Document parseXMLFile(File xmlFile){

		logger.info("xmlParser running");
		try {
			builder = fact.newDocumentBuilder();
			doc = builder.parse(xmlFile);
		} catch (ParserConfigurationException e) {
			logger.fatal("parseXML ParserConfig error");
		} catch (SAXException e) {
			logger.fatal("parseXML Sax Exception");
		} catch (IOException e) {
			logger.fatal("parseXML IOException");
		}
		return doc;
	}

	/**
	 * parst eine xml-Datei. hier speziel eine pgml Datei von ArgoUML
	 * @param File xmlFile
	 * @return Nodelist
	 */
	public NodeList pgmlXMLParser (File xmlF){


		logger.info("pgmlParser running");

		//del bad line in pgml
		final String delString = "<!DOCTYPE pgml SYSTEM \"pgml.dtd\">";
		
		try{
			//load file
			FileReader pgmlRead = new FileReader (xmlF);
		    BufferedReader br = new BufferedReader(pgmlRead);
		    StringBuffer sb = new StringBuffer();
		    String eachLine = br.readLine();
		    while (eachLine != null) {
		    	boolean hit = false;
				// del Doctype pgml.dtd 
		    	if (eachLine.equalsIgnoreCase(delString) && !hit){
		    		eachLine = "";
		    		hit = true;
		    	} 
		    		sb.append(eachLine);
		    		sb.append("\n");
		    		eachLine = br.readLine();
		    }

			//save altered file
			FileWriter pgmlWriter = new FileWriter(xmlF);
		    pgmlWriter.write(sb.toString());
		    pgmlRead.close();
		    pgmlWriter.close();
		    
		    //DOM Part
			DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = fact.newDocumentBuilder();
			Document newdoc = builder.parse(xmlF);
			CheckerRiskfinder.doc = newdoc;
			NodeList nList = newdoc.getElementsByTagName("pgml");
			
			return nList;
		}
		catch(Exception e){
			logger.fatal("pgmlParser Exception");
		}
		return null;
	}
	
	/**
	 * Hilfsmethode. gibt den Inhalt einer Nodelist aus
	 * @param Nodelist nList
	 * @param String what
	 * @param ITextOutput
	 */
	@SuppressWarnings("unused")
	public void pgmlDOMPrinter(NodeList nList, String what, ITextOutput textOutput){
		
		for (int i = 0; i < nList.getLength(); i++) {
			textOutput.writeLn(nList.getLength() + " Elements in RootNode. ");
			textOutput.writeLn("Name: " + nList.item(0).getNodeName() + "");
			textOutput.writeLn(nList.item(i).getChildNodes().getLength() + " Elements.");
			//textOutput.writeLn(what + " and Type:");
			for (int j = 0; j < nList.item(i).getChildNodes().getLength(); j++) {
				Node nNode = nList.item(i).getChildNodes().item(j);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) { 
					Element eElement = (Element) nNode;
			          // textOutput.writeLn(j + ": " + eElement.getAttribute(what) + " | " + Node.ELEMENT_NODE);
			    }
			}
		}	
	}

	/**
	 * ermittelt die Figure, die zu verändern ist, mittels der argoID
	 * @param Nodelist nList
	 * @param String argoID
	 * @param ITextOutput
	 * @return String figute
	 */
	public String getFigToAlter ( NodeList nListFull, String argoID, ITextOutput textOutput){

		String figure = "";
		for (int i = 0; i < nListFull.getLength(); i++) { 
			for (int j = 0; j < nListFull.item(i).getChildNodes().getLength(); j++) {
				Node nNode = nListFull.item(i).getChildNodes().item(j);	    
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
		          Element eElement = (Element) nNode;
		          if (eElement.getAttribute("href").equals(argoID) && figure.equals("")){
		        	  figure = eElement.getAttribute("name");
		        	  //logger.info("found " + figure + " with ID: " + argoID);
		          }
		       }
			}
		}
		return figure;
	}

	/**
	 * speichert DOM in xml-File
	 * @param Nodelist nList
	 * @param File pgmlFile
	 * @return File pgmlFile
	 */
	public File dom2File (NodeList nList, File pgmlFile){

		// saving DOM to XML-File
		try {
			
			// Get the first <slide> element in the DOM; hopefully <pgml>
			Node node = nList.item(0); 
			
			// Use a Transformer for output
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();

			DOMSource source = new DOMSource(node);
			StreamResult result = new StreamResult(pgmlFile);
			transformer.transform(source, result);
			
			//add DocType
			final String addString = "<!DOCTYPE pgml SYSTEM \"pgml.dtd\">";

			//load file
			FileReader pgmlRead = new FileReader (pgmlFile);
		    BufferedReader br = new BufferedReader(pgmlRead);
		    StringBuffer sb = new StringBuffer();
		    String eachLine = br.readLine();
		    while (eachLine != null) {
		    	if (eachLine.contains("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>")) {
				    eachLine = eachLine.replace("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" , "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n\n" + addString + "\n");
		    	}
		    	if(eachLine.contains("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")){
		    		eachLine = eachLine.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" , "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n\n" + addString + "\n");
		    	}
				// add Doctype pgml.dtd;
		    		sb.append(eachLine);
		    		sb.append("\n");
		    		eachLine = br.readLine();
		    }
			//save altered file
			FileWriter pgmlWriter = new FileWriter(pgmlFile);
		    pgmlWriter.write(sb.toString());
		    pgmlRead.close();
		    pgmlWriter.close();
		    
		} catch (TransformerFactoryConfigurationError e) {
			logger.fatal("dom2File Transformer error");
		} catch (TransformerConfigurationException e) {
			logger.fatal("dom2File Transformer error");
		} catch (TransformerException e) {
			logger.fatal("dom2File Transformer error");
		} catch (IOException e) {
			logger.fatal("dom2File IO error");
		}
		return pgmlFile;
	}

	/**
	 * fügt eine Notiz mit Kanditaten in die NodeList ein.
	 * @param NodeList nList
	 * @param HashMap<String, Vector<String>> candidatesMap
	 * @param ITextOutput
	 * @return NodeList
	 */
	public NodeList insertInfoNode(NodeList nList, HashMap<String, Vector<String>> candidatesMap, ITextOutput textOutput) {

		int maxFig = 0;
		
		//get max Figure
		for (int i = 0; i < nList.getLength(); i++){
			Node iNode = nList.item(i);
			for (int j = 0; j < iNode.getChildNodes().getLength(); j++){
				Node mNode = iNode.getChildNodes().item(j);
				if (mNode.getNodeType() == Node.ELEMENT_NODE) {
			          Element eElement = (Element) mNode;
			          if (eElement.getAttribute("name").contains("Fig")){
			        	   int indexB = eElement.getAttribute("name").indexOf("g");
			        	   int indexE = eElement.getAttribute("name").length();    //eElement.getAttribute("name").indexOf(".");
			        	   if(maxFig < Integer.parseInt(eElement.getAttribute("name").substring(indexB+1,indexE))){
			        		   maxFig = Integer.parseInt(eElement.getAttribute("name").substring(indexB+1,indexE));
			        	   }
			          }
				}
			}
		}
		
		//make new Figures/Infonodes
		CheckerRiskfinder.doc.getElementsByTagName("pgml").item(0).appendChild(makeInfoNode(maxFig +1, "red", candidatesMap, 0));
		
		return nList;
	}

	private Node makeInfoNode(int figNummer, String fillColor, HashMap<String, Vector<String>> candidatesMap, int x){
		
		//build new Comment for pgml
		Element newElement = CheckerRiskfinder.doc.createElement("group");
		newElement.setAttribute("name", "Fig"+figNummer);
		newElement.setAttribute("description","org.argouml.uml.diagram.static_structure.ui.FigComment[20, 20, 80, 60]pathVisible=false;stereotypeView=0;");
		newElement.setAttribute("href", id);
		newElement.setAttribute("fill", "1");
		newElement.setAttribute("fillcolor", "white");
		newElement.setAttribute("stroke", "1");
		newElement.setAttribute("strokecolor", "black");
		
		Element priv = CheckerRiskfinder.doc.createElement("private");
		newElement.appendChild(priv);
		
		Element rectangle = CheckerRiskfinder.doc.createElement("rectangle");
		rectangle.setAttribute("name", "Fig"+figNummer+".0");
		rectangle.setAttribute("x", "20");
		rectangle.setAttribute("y", "20");
		rectangle.setAttribute("width", "80");
		rectangle.setAttribute("height", "60");
		rectangle.setAttribute("fill", "0");
		rectangle.setAttribute("fillcolor", "white");
		rectangle.setAttribute("stroke", "0");
		rectangle.setAttribute("strokecolor", "black");
		newElement.appendChild(rectangle);
		
		Element path1 = CheckerRiskfinder.doc.createElement("path");
		path1.setAttribute("name", "Fig"+figNummer+".1");
		path1.setAttribute("description", "org.tigris.gef.presentation.FigPoly");
		path1.setAttribute("fill","1");
		path1.setAttribute("fillcolor","white");
		path1.setAttribute("stroke","1");
		path1.setAttribute("strokecolor","black");
		Element moveto = CheckerRiskfinder.doc.createElement("moveto");
		moveto.setAttribute("x", "20");
		moveto.setAttribute("y", "20");
		path1.appendChild(moveto);
		Element lineto1 = CheckerRiskfinder.doc.createElement("lineto");
		lineto1.setAttribute("x", "89");
		lineto1.setAttribute("y", "20");
		path1.appendChild(lineto1);
		Element lineto2 = CheckerRiskfinder.doc.createElement("lineto");
		lineto2.setAttribute("x", "99");
		lineto2.setAttribute("y", "30");
		path1.appendChild(lineto2);
		Element lineto3 = CheckerRiskfinder.doc.createElement("lineto");
		lineto3.setAttribute("x", "99");
		lineto3.setAttribute("y", "79");
		path1.appendChild(lineto3);
		Element lineto4 = CheckerRiskfinder.doc.createElement("lineto");
		lineto4.setAttribute("x", "20");
		lineto4.setAttribute("y", "79");
		path1.appendChild(lineto4);
		Element lineto5 = CheckerRiskfinder.doc.createElement("lineto");
		lineto5.setAttribute("x", "20");
		lineto5.setAttribute("y", "20");
		path1.appendChild(lineto5);
		newElement.appendChild(path1);
		
		Element path2 = CheckerRiskfinder.doc.createElement("path");
		path2.setAttribute("name", "Fig"+figNummer+".2");
		path2.setAttribute("description", "org.tigris.gef.presentation.FigPoly");
		path2.setAttribute("fill","1");
		path2.setAttribute("fillcolor","178 178 178");
		path2.setAttribute("stroke","1");
		path2.setAttribute("strokecolor","black");
		Element moveto2 = CheckerRiskfinder.doc.createElement("moveto");
		moveto2.setAttribute("x", "89");
		moveto2.setAttribute("y", "20");
		path2.appendChild(moveto2);
		Element lineto6 = CheckerRiskfinder.doc.createElement("lineto");
		lineto6.setAttribute("x", "99");
		lineto6.setAttribute("y", "30");
		path2.appendChild(lineto6);
		Element lineto7 = CheckerRiskfinder.doc.createElement("lineto");
		lineto7.setAttribute("x", "99");
		lineto7.setAttribute("y", "30");
		path2.appendChild(lineto7);
		Element lineto8 = CheckerRiskfinder.doc.createElement("lineto");
		lineto8.setAttribute("x", "89");
		lineto8.setAttribute("y", "20");
		path2.appendChild(lineto8);
		newElement.appendChild(path2);
		
		Element group = CheckerRiskfinder.doc.createElement("group");
		group.setAttribute("name", "Fig"+figNummer+".3");
		group.setAttribute("description", "org.argouml.uml.diagram.ui.FigStereotypesGroup[22, 22, 66, 0]");
		group.setAttribute("href", id);
		group.setAttribute("fill", "0");
		group.setAttribute("fillcolor", "white");
		group.setAttribute("stroke", "0");
		group.setAttribute("strokecolor", "black");
		
		Element priv2 = CheckerRiskfinder.doc.createElement("private");
		group.appendChild(priv2);
		
		Element rectangle2 = CheckerRiskfinder.doc.createElement("rectangle");
		rectangle2.setAttribute("name", "Fig"+figNummer+".3.0");
		rectangle2.setAttribute("x", "22");
		rectangle2.setAttribute("y", "22");
		rectangle2.setAttribute("width", "66");
		rectangle2.setAttribute("height", "0");
		rectangle2.setAttribute("fill", "0");
		rectangle2.setAttribute("fillcolor", "white");
		rectangle2.setAttribute("stroke", "0");
		rectangle2.setAttribute("strokecolor", "black");
		group.appendChild(rectangle2);
		newElement.appendChild(group);

		Element text = CheckerRiskfinder.doc.createElement("text");
		text.setAttribute("name", "Fig"+figNummer+".4");
		text.setAttribute("x", "22");
		text.setAttribute("y", "22");
		text.setAttribute("width", "66");
		text.setAttribute("height", "56");
		text.setAttribute("fill", "0");
		text.setAttribute("fillcolor", "white");
		text.setAttribute("stroke", "0");
		text.setAttribute("strokecolor", "black");
		text.setAttribute("textcolor", "black");
		text.setAttribute("font", "Dialog");
		text.setAttribute("italic", "false");
		text.setAttribute("bold", "false");
		text.setAttribute("textsize", "12");
		text.setAttribute("justification", "Left");
		newElement.appendChild(text);
		
		return (Node)newElement;
	}

	/**
	 * fügt Veränderungen zum Einfügen einer Notiz in die xmi-Datei hinzu
	 * @param File xmiFile
	 * @param HashMap<String, Vector<String>> candidatesMap
	 * @return File xmiFile
	 */
	public File addCommentToXMI(File xmiFile, HashMap<String, Vector<String>> candidatesMap){

		//adds comment to xmi
		
		//load dom from file
		Document doc = parseXMLFile(xmiFile);
		NodeList nList = doc.getElementsByTagName("XMI");
		
		for (int i = 0; i < nList.getLength(); i++){
			//XMI
			Node iNode = nList.item(i);
			for (int j = 0; j < iNode.getChildNodes().getLength(); j++){
				// header /content
				Node mNode = iNode.getChildNodes().item(j);
				for (int k=0; k< mNode.getChildNodes().getLength(); k++){
					//UMLmodel
					Node kNode = mNode.getChildNodes().item(k);
					for(int l = 0; l < kNode.getChildNodes().getLength(); l++){
						Node lNode = kNode.getChildNodes().item(l);
							// add comment
							if (lNode.getNodeType() == Node.ELEMENT_NODE && lNode.getNodeName().equalsIgnoreCase("UML:Namespace.ownedElement")) {   
								Element neu = doc.createElement("UML:Comment");
								neu.setAttribute("xmi.id", id);
								neu.setAttribute("isSpecification", "false");
								neu.setAttribute("name", "Comment");
								//add List of patterns
								neu.setAttribute("body", makeList(candidatesMap)); 
								lNode.appendChild(neu);
							}
						}
					}
				}
			}
		
		//save dom to file
		xmiFile = dom2File(nList,xmiFile);
		return xmiFile;
	}
	
	private String makeList(HashMap<String, Vector<String>> candidatesMap){
		
		//makes list of activity/pattern for comments
		String list = "";
		Object[] keys = candidatesMap.keySet().toArray();
		if(keys.length > 0){
			for (int i = 0; i < keys.length; i++){
				list = list + "Risikoverdacht bei [" + keys[i].toString() + "]:\n";
				Vector<String> tmp = candidatesMap.get(keys[i]);
				for (int k = 0; k < tmp.size(); k++){
					list = list + (k+1) + " | " + tmp.elementAt(k) + "\n";
				}
			list = list + "---\n";
			}
		}

		return list;
	}
	
}
	
	


