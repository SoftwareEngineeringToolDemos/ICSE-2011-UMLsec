/**
 * 
 */
package open.umlsec.tools.checksystem.checks;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import org.apache.crimson.tree.XmlDocument;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/**
 * @author ska
 *
 */
public class XMLReqParser {
	private XmlDocument 		doc 	= null;
	private File				xmlFile = null;
	private Vector<Requirement> reqList = null;
	private String 				actualSubSystem = "";
	
	public XMLReqParser(){
		reqList = new Vector<Requirement>();
	}
	
	
	public void setXmlFile(File f){
		xmlFile = f;
		createDocument();
	}
	
	public void parseFile(){
		if (xmlFile != null){
			Node n = doc.getFirstChild();
			parseRecursive(n);
		}
	}
	
	public Vector<Requirement> getRequirementList(){
		return reqList;
	}
	
	private void createDocument(){
		String uri;
		uri = "file:" + xmlFile.getAbsolutePath();
		try {
			doc = XmlDocument.createXmlDocument(uri);
			
		}catch(SAXParseException spex){
			System.out.println("+============================+");
			System.out.println("|        *Parse Error       *|");
			System.out.println("+============================+");
			System.out.println("+ Line " + spex.getLineNumber() + ", uri " + spex.getSystemId());
			System.out.println(spex.getClass().toString());
			System.out.println(spex.getMessage());
			System.out.println("+============================+");
		}catch(SAXException saex){
			System.out.println("+============================+");
			System.out.println("|       *SAX XML Error*      |");
			System.out.println("+============================+");
			System.out.println(saex.toString());
		}catch(IOException ioex){
			System.out.println("+============================+");
			System.out.println("|    *Input/Output Error*    |");
			System.out.println("+============================+");
			System.out.println(ioex.toString());
		}
	}

	private void parseRecursive(Node p)
	{
		int i;
		int id 				= -1;
		int dependencyId 	= -1;
		
		String reqDescription = "";
		
		if (p == null)
		{
			System.out.println("Node is null.");
			return;
		}
		
		if (p.getNodeName().equals("sub-system")){
			NamedNodeMap attributes1 = p.getAttributes();
			
			for (i = 0; i < attributes1.getLength(); i++){
				Node attribute = attributes1.item(i);
				
				if (attribute.getNodeName().equals("name")){
					actualSubSystem = attribute.getNodeValue();
					break;
				} 
			}
		}else if (p.getNodeName().equals("requirement")){
			NamedNodeMap attributes2 = p.getAttributes();
			
			for (i = 0; i < attributes2.getLength(); i++){
				Node attribute = attributes2.item(i);
				
				if (attribute.getNodeName().equals("id")){
					id = Integer.parseInt(attribute.getNodeValue());
				} else if (attribute.getNodeName().equals("dependency")){
					dependencyId = Integer.parseInt(attribute.getNodeValue());
				} else if (attribute.getNodeName().equals("name")){
					reqDescription = attribute.getNodeValue();
				}
			}
			reqList.add(new Requirement(id, dependencyId, reqDescription, actualSubSystem));
		}
		
		
		NodeList nodes = p.getChildNodes();
		int numElem = nodes.getLength();
		
		for(i = 0; i < numElem; i++)
		{
			Node n = nodes.item(i);
			if (n == null)
			{
				continue;
			}
			parseRecursive(n);
		}
	}

}
