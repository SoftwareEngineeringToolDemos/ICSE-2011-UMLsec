package tum.umlsec.viki.framework.mdr;

import java.io.IOException;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class XmiSAXParser {
	private XMLReader saxReader;
	public XmiSAXParser(String uri) throws SAXException, IOException {
		saxReader = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
        saxReader.setContentHandler(new XmiIDSAXHandler());
        System.out.println("Ready to parse " + uri);
        saxReader.parse(uri);
	}
	
}
