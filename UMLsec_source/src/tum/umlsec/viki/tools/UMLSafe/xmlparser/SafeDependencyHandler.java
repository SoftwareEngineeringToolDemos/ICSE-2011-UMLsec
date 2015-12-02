/*
 * Created on 2004-9-22
 *
 */
package tum.umlsec.viki.tools.UMLSafe.xmlparser;

import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.tools.UMLSafe.ToolSafeLinksConstants;
import tum.umlsec.viki.tools.UMLSafe.util.safeDependencyXml;

/**
 * @author Shunwei
 *
 */
public class SafeDependencyHandler extends XMLFileHandler
{
    private ITextOutput textOutput;

    private HashMap SDInstanceMap = new HashMap();
    private float[] valueArray = new float[2];
    
    private safeDependencyXml _SDXml;
    
    /** 
     * Creates a new instance of MeasuresHandler
     */
    public SafeDependencyHandler(ITextOutput _textOutput)
    {
		textOutput = _textOutput;
    }
    
    public HashMap getSDInstanceMap()
    {
        return this.SDInstanceMap;
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
        System.out.println("Error parsing the file: "+e.getMessage());
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
        if (qName.equalsIgnoreCase(ToolSafeLinksConstants.INSTANCE))
        {
            _SDXml = new safeDependencyXml();
        	for (int att = 0; att < atts.getLength(); att++)
            {
                String attName = atts.getQName(att);
                if (attName.equalsIgnoreCase(ToolSafeLinksConstants.LEVEL))
                	_SDXml.setLevel(atts.getValue(attName));
                if (attName.equalsIgnoreCase(ToolSafeLinksConstants.GOAL))
                	_SDXml.setGoal(atts.getValue(attName));
            }
        }
    }

    public void endElement(
                String namespaceURI,
                String localName,
                String qName) throws SAXException
    {
        if (qName.equalsIgnoreCase(ToolSafeLinksConstants.INSTANCE))
        {
        	SDInstanceMap.put(_SDXml.getLevel(), _SDXml);
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
