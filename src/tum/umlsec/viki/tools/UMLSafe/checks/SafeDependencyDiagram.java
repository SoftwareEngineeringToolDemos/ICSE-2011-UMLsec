/*
 * Created on 2004-4-14
 * @author Shunwei Shen
 */
 
package tum.umlsec.viki.tools.UMLSafe.checks;

/**
 * Function:
 * parse and check the safety requiments of the SafeDependency diagram. 
 *
 */

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.mdr.IMdrContainer;
import tum.umlsec.viki.framework.toolbase.CommandParameterDescriptor;
import tum.umlsec.viki.tools.UMLSafe.ToolSafeLinksConstants;
import tum.umlsec.viki.tools.UMLSafe.ToolSafeTest;
import tum.umlsec.viki.tools.UMLSafe.util.diagComponent;
import tum.umlsec.viki.tools.UMLSafe.util.diagStereotype;
import tum.umlsec.viki.tools.UMLSafe.util.safeDependencyXml;
import tum.umlsec.viki.tools.UMLSafe.xmlparser.SafeDependencyHandler;
import tum.umlsec.viki.tools.UMLSafe.xmlparser.XMLFileParser;

public class SafeDependencyDiagram extends initProcessing
{
    private ITextOutput textOutput;
    private IMdrContainer mContainer;
    
    public boolean check(IMdrContainer _mdrContainer, Iterator _parameters, ITextOutput _mainOutput)
    {
        textOutput = _mainOutput;
        boolean dump;
		textOutput.writeLn("********************************************************************************************");
		textOutput.writeLn("*                                   Safe Dependency Check                                  *");
		textOutput.writeLn("********************************************************************************************");
      	textOutput.writeLn("");
		init(_mdrContainer);
		dumpDependencies();
		dumpClasses();
		dump = test(_parameters);
		return dump;
    }
    
    public boolean test(Iterator _parameters)
    {
        boolean result = true;
        File _xmlFile;
    	boolean _foundFile = false;
    	String filePath = new String("");
    	HashMap sdInstanceMap = new HashMap();
    	
    	for (;_parameters.hasNext();)
    	{
    		CommandParameterDescriptor _parameter = (CommandParameterDescriptor) _parameters.next();
    		filePath = _parameter.getAsFile().getAbsolutePath();
            try
			{
	    		if(_parameter.getId() == ToolSafeTest.CID_SD_XML_FILE && !filePath.equals(""))
	            {
	            	_xmlFile = _parameter.getAsFile();
	            	if (_xmlFile.exists())
	            	{
	                	_foundFile = true;
	            		SafeDependencyHandler SDXmlHandler = new SafeDependencyHandler(textOutput);
	        			XMLFileParser xmlFailureFileParser = new XMLFileParser(filePath, SDXmlHandler);
	        			sdInstanceMap = SDXmlHandler.getSDInstanceMap();
	        			
	        			if (sdInstanceMap == null)
	        			{
	        				textOutput.writeLn(" the .xml file is empty or not corret.");
	        				result = false;
	                		return result;
	        			}
	            	}
	            	else
	            	{
	            		textOutput.writeLn("The inputed file \"" + filePath + "\" is not existed.");
	            		result = false;
	            		return result;
	            	}
	            }
			}
            catch (Exception exc)
        	{
         		textOutput.writeLn(exc.toString());
        		result = false;
        		return result;
        	}
    	}

    	int p = 1;
        Set dependKeySets = dependencyMap.keySet();
        for (Iterator itKeys = dependKeySets.iterator();itKeys.hasNext(); )
        {
        	String CorS1 = null;
        	String CorS2 = null;
        	String compName = null;
        	String anName = null;
        	
            textOutput.writeLn("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        	textOutput.writeLn(">>>>>>>>>>                          the " + p + ". test is starting                        <<<<<<<<<");
            
            String dependName = (String) itKeys.next();
            textOutput.writeLn("");
            textOutput.writeLn("** There is a dependency named: \"" + dependName + "\". **");
            diagComponent dDepend = (diagComponent) dependencyMap.get(dependName);
            String clientName = (String) dependClientMap.get(dependName);
            diagComponent diagClient = (diagComponent) classMap.get(clientName);
            if (diagClient == null)
            {
            	textOutput.writeLn("** This Dependency hasn\'t Client. **");
            	result = false;
            	continue;
            }
            textOutput.writeLn("** The Client of this Dependency named: \"" + clientName + "\". **");
            String supplierName = (String) dependSupplierMap.get(dependName);
            diagComponent diagSupplier = (diagComponent) classMap.get(supplierName);
            if (diagSupplier == null)
            {
            	textOutput.writeLn("** This Dependency hasn\'t Supplier. **");
            	result = false;
            	continue;
            }
            textOutput.writeLn("** The Supplier of this Dependency named: \"" + supplierName + "\". **");
            
            String clientStereotypeName = diagClient.getStereotype().getName();
            String supplierStereotypeName = diagSupplier.getStereotype().getName();
            HashMap TagAndValue1;
            HashMap TagAndValue2;
            if (clientStereotypeName!= null && clientStereotypeName.equalsIgnoreCase(ToolSafeLinksConstants.CRITICAL))
            {
            	CorS1 = "Client";
            	CorS2 = "Supplier";
            	compName = clientName;
            	anName = supplierName;
            	TagAndValue1 = diagClient.getTagandValue();
            	TagAndValue2 = diagSupplier.getTagandValue();
            }
            else if (supplierStereotypeName != null && supplierStereotypeName.equalsIgnoreCase(ToolSafeLinksConstants.CRITICAL))
            {
            	CorS1 = "Supplier";
            	CorS2 = "Client";
            	compName = supplierName;
            	anName = clientName;
            	TagAndValue1 = diagSupplier.getTagandValue();
            	TagAndValue2 = diagClient.getTagandValue();
            }
            else
            {
            	textOutput.writeLn("** Neither Client nor Supplier of this Dependency should be checked. **");
            	result = false;
            	continue;
            }
            
            textOutput.writeLn(">>>>> These Client and Supplier of the Dependency must be checked! <<<<<");
            
            Set tvKeys1 = TagAndValue1.keySet();
            String cLevel = null;
            String cgString = null;
            String[] levelAim = new String[3];
            for (Iterator itTVKey1 = tvKeys1.iterator(); itTVKey1.hasNext();)
            {
                String tag = (String) itTVKey1.next();
                String value = (String) TagAndValue1.get(tag);
                textOutput.writeLn("-- The " + CorS1 + " \"" + compName + "\" has a Tag \"" + tag +
                		"\" and a Value of this Tag \"" + value + "\". --");
                if (sdInstanceMap.get(tag) != null)
                {
                    cLevel = tag;
                    cgString = value;
                }
            }
            if (cLevel == null)
            {
                textOutput.writeLn("-- The " + CorS1 + " \"" + compName + "\" of this dependency " +
                		"has not Safety LeveL.   !!!!!! Check failed!!!!!!  --");
                result = false;
                continue;
            }   
            
            Set tvKeys2 = TagAndValue2.keySet();
            String sgString = null;
            for (Iterator itTVKey2 = tvKeys2.iterator(); itTVKey2.hasNext();)
            {
                String tag = (String) itTVKey2.next();
                String value = (String) TagAndValue2.get(tag);
                textOutput.writeLn("-- The " + CorS2 + " \"" + anName + "\" has a Tag \"" + tag +
                		"\" and a Value of this Tag \"" + value + "\". --");
                if (tag.equalsIgnoreCase(cLevel))
                {
                	sgString = value;
                }
            }
            
            String cFeature = null;
            String sFeature = null;            
            if (sgString == null)
            {
                textOutput.writeLn("-- The " + CorS1 + " \"" + compName + "\" and the " + CorS2 + " \"" + anName + 
                		"\" of this dependency has not identical Safety LeveL.   !!!!!! Check failed!!!!!!  --");
                result = false;
                continue;
            }            
            else
            {
                if (cgString != null)
                {
                	int ccs = cgString.indexOf("(");
                    int cce = cgString.indexOf(")");
                    if (ccs != -1 && cce != -1)
                    {
                        try
                        {
                        	cFeature = cgString.substring(0, ccs);
                        }
                        catch (NumberFormatException exc)
                        {
                            textOutput.writeLn("-- The Value of the Level \"" + cLevel + "\" in the " + CorS1 + " \"" + 
                        		compName + "\" is not correct. --");
                            result = false;
                            continue;
                        }
                    }
                    else
                    {
                        textOutput.writeLn("-- The Value of the Level \"" + cLevel + "\" in the " + CorS1 + " \"" + 
                        		compName + "\" has not bracket. --");
                        result = false;
                        continue;                            	
                    }
                }
                else
                {
                    textOutput.writeLn("-- The Value of the Level \"" + cLevel + "\" in the " + CorS1 + " \"" + 
                    		compName + "\" is empty. --");
                    result = false;
                    continue;                            	
                }
        	
                int scs = sgString.indexOf("(");
                int sce = sgString.indexOf(")");
                if (scs != -1 && sce != -1)
                {
                    try
                    {
                    	sFeature = sgString.substring(0, scs);
                    }
                    catch (NumberFormatException exc)
                    {
                        textOutput.writeLn("-- The Value of the Level \"" + cLevel + "\" in the " + CorS2 + " \"" + 
                    		anName + "\" is not correct. --");
                        result = false;
                        continue;
                    }
                }
                else
                {
                    textOutput.writeLn("-- The Value of the Level \"" + cLevel + "\" in the " + CorS2 + " \"" + 
                    		anName + "\" has not bracket. --");
                    result = false;
                    continue;                            	
                }
            	
                String valueStr = null;
                String cGoal = null;
                String dGoal = null;
                float cValue = Float.NaN;
                float dValue = Float.NaN;
                if (!cFeature.equalsIgnoreCase(sFeature))
                {
                	textOutput.writeLn("-- The destination Features of " + CorS1 + " \"" + compName + "\" and the " 
                			+ CorS2 + " \"" + anName + "\" of this dependency are not identical.   " +
                					"!!!!!! Check failed!!!!!!  --");
                	result = false;
                    continue;  
                }
                else
                {
                	safeDependencyXml sdXml = (safeDependencyXml) sdInstanceMap.get(cLevel);
                    valueStr = sdXml.getGoal();
                    if (valueStr != null)
                    {
                    	int xcs = valueStr.indexOf("(");
                        int xce = valueStr.indexOf(")");
                        if (xcs != -1 && xce != -1)
                        {
                            try
                            {
                            	cGoal = valueStr.substring(0, xcs);
                            	String cValueStr = valueStr.substring(xcs+1, xce);
                                cValue = Float.parseFloat(cValueStr);
                            }
                            catch (NumberFormatException exc)
                            {
                                textOutput.writeLn("-- The Value of the Goal \"" + cGoal + "\" for the " + CorS1 + " \"" + 
                            		compName + "\" in the .xml file is not correct. --");
                                result = false;
                                continue;
                            }
                        }                        	
                    }
                    else
                    {
                    	textOutput.writeLn("-- The Value of the goal in the .xml file are empty. --");
                        result = false;
                        continue;                        	
                    }
                    
                    diagStereotype dependStereotype = dDepend.getStereotype();
                    HashMap depSteTagAndValue = dependStereotype.getTagandValue();
                    if (depSteTagAndValue != null)
                    {
                        String dstTaggedValueStr = (String) depSteTagAndValue.get(cLevel);
                        if (dstTaggedValueStr == null)
                        {
                            textOutput.writeLn("-- The Stereotype of the dependency \"" + dependName + "\" has not a Value " +
                            		"of Level \"" + cLevel +"\". --");
                            result = false;
                            continue;
                        }
                        else
                        {
                        	textOutput.writeLn("-- The Stereotype of the dependency \"" + dependName + "\" has a Tagged Value" +
                        			" with a Tag (Level) \"" + cLevel +"\" and a Value (Gaol) \"" + dstTaggedValueStr + "\". --");
                            int dcs = dstTaggedValueStr.indexOf("(");
                            int dce = dstTaggedValueStr.indexOf(")");
                            if (dcs != -1 && dce != -1)
                            {
                                try
                                {
                                	dGoal = valueStr.substring(0, dcs);
                                    String dValueStr = dstTaggedValueStr.substring(dcs+1, dce);
                                    dValue = Float.parseFloat(dValueStr);
                                }
                                catch (NumberFormatException exc)
                                {
                                    textOutput.writeLn("-- The value of TaggedValue of the stereotype of the dependency \"" +
                                    dependName + "\" is not correct. --");
                                    result = false;
                                    continue;
                                }
                            }
                            else
                            {
                                textOutput.writeLn("-- The value of taggedValue of the stereotype of the dependency \"" +
                                        dependName + "\" has not bracket. --");
                                result = false;
                                continue;                           	
                            }
                        }
                    }
                    else
                    {
                    	textOutput.writeLn("-- The Stereotype of the dependency \"" + dependName + "\" has not taggedValue. --");
                        result = false;
                        continue;
                    }
                    //TODO
                    if (cGoal.equalsIgnoreCase(dGoal))
                    {
                    	if (cGoal.equalsIgnoreCase(ToolSafeLinksConstants.IMMEDIATE))
                    	{
			                if (cValue != Float.NaN && dValue != Float.NaN && dValue <= cValue)
			                {
			                    textOutput.writeLn("### The check is successfully finished!!! ###");
			                }
			                else
			                {
			                	textOutput.writeLn("### The check has failed. Reason: \"the Goal of Dependency " + dValue + 
			                			" <= the Goal of " + CorS1 + " " + cValue + "\" not correct. ###");
			                	result = false;
			                }
                    	}
                    	else if (cGoal.equalsIgnoreCase(ToolSafeLinksConstants.CORRECT))
                    	{
                    		if (cValue != Float.NaN && dValue != Float.NaN && dValue >= cValue)
			                {
			                    textOutput.writeLn("### The check is successfully finished!!! ###");
			                }
			                else
			                {
			                	textOutput.writeLn("### The check has failed. Reason: \"the Goal of Dependency " + dValue + 
			                			" >= the Goal of " + CorS1 + " " + cValue + "\" not correct. ###");
			                	result = false;
			                }
                    	}
                    }
                    else
                    {
                    	textOutput.writeLn("-- The Goal of Safety Level \"" + cLevel + "\" in the Stereotype of the dependency \"" + 
                    			dependName + "\" is not identical with which required in the XML file. --");
                        result = false;
                        continue;
                    }
                }
            }
            textOutput.writeLn("");
        	textOutput.writeLn(">>>>>>>>>>                          the " + p + ". test is finished.                        <<<<<<<<<");
            textOutput.writeLn("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            p++;
        }
        return result;
    } 
}
