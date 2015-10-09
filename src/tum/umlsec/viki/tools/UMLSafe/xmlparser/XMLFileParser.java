package tum.umlsec.viki.tools.UMLSafe.xmlparser;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/**
 *
 * @author  Shunwei
 */
public class XMLFileParser
{
    /** Creates a new instance of XMLFileParser */
    public XMLFileParser(String pFileURI, XMLFileHandler phandlerClass)
    {
        parserFile(pFileURI, phandlerClass);
    }
    
    private void parserFile(String pURI, XMLFileHandler lHandlerClass)
    {
        XMLReader xmlReader = null;
        try
        {
            SAXParserFactory spfactory = SAXParserFactory.newInstance();
            spfactory.setValidating(false);
            SAXParser saxParser = spfactory.newSAXParser();
            xmlReader = saxParser.getXMLReader();
            xmlReader.setContentHandler(lHandlerClass.getInstance());
            xmlReader.setErrorHandler(lHandlerClass.getInstance());
            InputSource source = new InputSource(pURI);
            xmlReader.parse(source);
        }
        catch (Exception e)
        {
            System.err.println(e);
            System.exit(1);
        }
    }
}
