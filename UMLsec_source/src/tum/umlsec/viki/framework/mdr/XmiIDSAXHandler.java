package tum.umlsec.viki.framework.mdr;

import java.util.Stack;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class XmiIDSAXHandler implements ContentHandler {
	static Logger logger = Logger.getLogger("UMLsec tool");
	// Stack containing a name ([0]) and a type ([1]) of an element. Useful to determine the current element's parent.
	private Stack<String[]> stack = null;
	
	public XmiIDSAXHandler() {
		super();
		stack = new Stack<String[]>();
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub

	}

	@Override
	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub

	}

	@Override
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		// TODO Auto-generated method stub
		if (uri.equals("org.omg.xmi.namespace.UML") && stack.peek()[0].equals(localName))
			stack.pop();
	}

	@Override
	public void endPrefixMapping(String prefix) throws SAXException {
		// TODO Auto-generated method stub

	}

	@Override
	public void ignorableWhitespace(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub

	}

	@Override
	public void processingInstruction(String target, String data)
			throws SAXException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDocumentLocator(Locator locator) {
		// TODO Auto-generated method stub

	}

	@Override
	public void skippedEntity(String name) throws SAXException {
		// TODO Auto-generated method stub

	}

	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
		stack.clear();

	}

	@Override
	public void startElement(String uri, String localName, String name,
			Attributes atts) throws SAXException {
		if ((atts != null) /*&& (!localName.equals("Stereotype"))*/){
			for (int idx = 0; idx < atts.getLength(); idx++) {
				// If it doesn't have a name, we don't need to store its value
				if ((atts.getLocalName(idx).equals("xmi.id")) && (atts.getValue("name") != null)){
					try {
						IdNameList.getInstance().addElement(atts.getValue("", "name"), atts.getValue(idx), localName);
						logger.debug("Element name : " + localName);
						stack.push(new String[]{atts.getValue("", "name"), localName});
					} catch (NoDuplicateException e) {
						System.out.println(e.getMessage());
					}
				}
				else if (atts.getLocalName(idx).equals("xmi.idref") && localName.equals("Stereotype")) {
					// This is a reference to a stereotype.
					// We add a new parent to the stereotype's parents list
					logger.debug("Adding new parent to a stereotype");
					try {
						IdNameList.getInstance().addParent(atts.getValue(idx), IdNameList.getInstance().getID(stack.peek()[0], stack.peek()[1]));
					} catch (NoDuplicateException e) {
						throw new SAXException(e.getMessage());
					}
				}
			}
		}

	}

	@Override
	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
		// TODO Auto-generated method stub

	}

}
