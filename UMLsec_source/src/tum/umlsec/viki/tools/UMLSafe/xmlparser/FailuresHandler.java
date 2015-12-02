package tum.umlsec.viki.tools.UMLSafe.xmlparser;

import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.tools.UMLSafe.ToolSafeLinksConstants;
import tum.umlsec.viki.tools.UMLSafe.util.safeLinksXml;

//import java.util.Iterator;

/**
 *
 * @author  Shunwei
 */
public class FailuresHandler extends XMLFileHandler
{
    private ITextOutput textOutput;

    private HashMap stereotypeMap = new HashMap();
    private safeLinksXml slInstance;
    
    /** 
     * Creates a new instance of MeasuresHandler
     */
    public FailuresHandler(ITextOutput _textOutput)
    {
		textOutput = _textOutput;
    }
    
    public HashMap getStereotypeMap()
    {
        return this.stereotypeMap;
    }

    public void error (SAXParseException e)
    {
		textOutput.writeLn("Error parsing the file: "+e.getMessage());
    }

    public void warning (SAXParseException e)
    {
		textOutput.writeLn("Problem parsing the file: "+e.getMessage());
    }

    public void fatalError (SAXParseException e)
    {
		textOutput.writeLn("Error parsing the file: "+e.getMessage());
		textOutput.writeLn("Cannot continue.");
        System.exit(1);
    }

    public void startDocument() throws SAXException
    {
    	textOutput.writeLn("------------------------------------------------------------------------");
    	textOutput.writeLn("XML file start");
    }

    public void startElement(
            String namespaceURI, 
            String localName, 
            String qName, 
            Attributes atts) throws SAXException
    {
        if (qName.equalsIgnoreCase(ToolSafeLinksConstants.STEREOTYPE))
        {
            slInstance = new safeLinksXml();
            for (int att = 0; att < atts.getLength(); att++)
            {
                String attName = atts.getQName(att);
                if (attName.equalsIgnoreCase(ToolSafeLinksConstants.NAME))
                {
                	slInstance.setName(atts.getValue(attName));
                	textOutput.writeLn("Stereotype: \"" + atts.getValue(attName) + "\"");
                }
            }
        }
        if (qName.equalsIgnoreCase(ToolSafeLinksConstants.INSTANCE))
        {
        	for (int att = 0; att < atts.getLength(); att++)
            {
                String attName = atts.getQName(att);
                if (attName.equalsIgnoreCase(ToolSafeLinksConstants.REDUNDANCY))
                {
                	slInstance.setRedundancy(atts.getValue(attName));
                	textOutput.writeLn("	A redundancy \"" + atts.getValue(attName) + "\"is defined.");
                }
                if (attName.equalsIgnoreCase(ToolSafeLinksConstants.FAILURE1))
                {
                	slInstance.setFailure1(atts.getValue(attName));
                	textOutput.writeLn("		A \"failure1\" = \"" + atts.getValue(attName) + "\"is for the redundancy defined.");
                }
                if (attName.equalsIgnoreCase(ToolSafeLinksConstants.FAILURE2))
                {
                	slInstance.setFailure2(atts.getValue(attName));
                	if (!atts.getValue(attName).equals(""))
                		textOutput.writeLn("		A \"failure2\" = \"" + atts.getValue(attName) + "\"is for the redundancy defined.");
                }
            }
        }
    }

    public void endElement(
                String namespaceURI,
                String localName,
                String qName) throws SAXException
    {
    	if (qName.equalsIgnoreCase(ToolSafeLinksConstants.STEREOTYPE))
        {
    		stereotypeMap.put(slInstance.getName(), slInstance);
    		textOutput.writeLn("");
        }
    	if (qName.equalsIgnoreCase(ToolSafeLinksConstants.INSTANCE))
        {
            slInstance.setRedundancyMap();
        }
    }

    public void characters(char[] ch,
                    int start,
                    int length) throws SAXException
    {
    }

    public void endDocument() throws SAXException
    {
		textOutput.writeLn("XML file end");
	  	textOutput.writeLn("------------------------------------------------------------------------");
      	textOutput.writeLn("");
    }
}
