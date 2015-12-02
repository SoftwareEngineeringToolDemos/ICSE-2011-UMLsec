package tum.umlsec.viki.framework.mdr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class XmiIDSwitch {
	static Document document;
	static Element root;
	static Logger logger = Logger.getLogger("XMIIDSwitch");
	
	public static void swapIds(File file) throws JDOMException, IOException {
		readFile(file);
		swap();
		writeFile(file);
	}
	
	/**
	 * Reads a file using a SAX parser. Updates document and root global variables.
	 * @param file the xmi file to be read
	 * @throws JDOMException JDOM error
	 * @throws IOException IO error
	 */
	private static void readFile(File file) throws JDOMException, IOException {
		SAXBuilder sxb = new SAXBuilder();
		document = sxb.build(file);
		root = document.getRootElement();
	}
	
	private static void swap() {
		Element content = root.getChild("XMI.content");
		analyzeChildren(content);
		analyzeChildrenXmiIdRef(content);
	}
	
	/**
	 * Recursively analyzes xmi elements and modifies the xmi.id attributes when possible.
	 * @param elt the root element
	 */
	private static void analyzeChildren(Element elt) {
		List children = elt.getChildren();
		ListIterator it = children.listIterator();
		Element current;
		String argoID;
		while(it.hasNext()) {
			current = (Element)it.next();
			if ((current.getAttribute("xmi.id") != null) && (current.getAttribute("name") != null)) {
				try {
					argoID = IdNameList.getInstance().getUnusedArgoId(current.getAttributeValue("name"), current.getName());
					try {
						IdNameList.getInstance().addToolId(argoID, current.getAttributeValue("xmi.id"));
					} catch (NoDuplicateException e) {
						e.printStackTrace();
					}
					current.removeAttribute("xmi.id");
					current.setAttribute("xmi.id", argoID);
					//IdNameList.getInstance().removeElement(argoID);
				} catch (NoSuchElementException ex) {
					// This element doesn't exist, it probably has been created by the tool. No need to swap its id.
					logger.error("Can't process element " + current.getAttributeValue("name"));
				}
			}
			analyzeChildren(current);
		}
	}
	
	/**
	 * Recursively analyzes xmi elements and modifies the xmi.idref attributes when possible.
	 * @param elt the root element
	 */
	private static void analyzeChildrenXmiIdRef(Element elt) {
		List children = elt.getChildren();
		ListIterator it = children.listIterator();
		Element current;
		while(it.hasNext()) {
			current = (Element)it.next();
			if (current.getAttribute("xmi.idref") != null) {
				String id = current.getAttributeValue("xmi.idref");
				logger.trace("reference to id " + id);
				try {
					current.setAttribute("xmi.idref", IdNameList.getInstance().getIdFromToolId(id));
				} catch (NoSuchElementException e) {
					// Probably an element that has been added by the tool. We don't care about its xmi.id
					logger.info("XmiIDSwitch.analyzeChildrenXmiIdRef : Ignoring idref " + id);
				}
			}
			analyzeChildrenXmiIdRef(current);
		}
	}
	
	private static void writeFile(File file) throws FileNotFoundException, IOException {
		XMLOutputter output = new XMLOutputter(Format.getPrettyFormat());
		output.output(document, new FileOutputStream(file));
	}
}
