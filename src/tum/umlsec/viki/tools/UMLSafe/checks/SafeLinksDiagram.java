/*
 * Created on 2004-3-19
 * @author Shunwei
 */
 
package tum.umlsec.viki.tools.UMLSafe.checks;

/**
 * Function:
 * analyse the safe links diagram and get the detail information
 * of its structur 
 */

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.mdr.IMdrContainer;
import tum.umlsec.viki.framework.toolbase.CommandParameterDescriptor;
import tum.umlsec.viki.tools.UMLSafe.ToolSafeLinksConstants;
import tum.umlsec.viki.tools.UMLSafe.ToolSafeTest;
import tum.umlsec.viki.tools.UMLSafe.util.diagComponent;
import tum.umlsec.viki.tools.UMLSafe.util.diagStereotype;
import tum.umlsec.viki.tools.UMLSafe.util.safeLinksXml;
import tum.umlsec.viki.tools.UMLSafe.xmlparser.FailuresHandler;
import tum.umlsec.viki.tools.UMLSafe.xmlparser.XMLFileParser;

public class SafeLinksDiagram extends initProcessing
{
    private ITextOutput textOutput;
    
    public boolean check(IMdrContainer _mdrContainer, Iterator _parameters, ITextOutput _mainOutput)
    {
		textOutput = _mainOutput;
		boolean dump;
		textOutput.writeLn("********************************************************************************************");
		textOutput.writeLn("*                                  Safe Links Check begin                                  *");
		textOutput.writeLn("********************************************************************************************");
      	textOutput.writeLn("");
		init(_mdrContainer);
		dumpDependencies();
		dumpLinks();
		dump = test(_parameters);
		return dump;
    }
    
    public boolean test(Iterator _parameters)
    {
    	boolean result = true;
    	File _xmlFile;
    	boolean _foundFile = false;
    	
    	HashMap lStereotypeMap = new HashMap();
    	String filePath = new String("");
    	
    	for (;_parameters.hasNext();)
    	{
    		CommandParameterDescriptor _parameter = (CommandParameterDescriptor) _parameters.next();
    		filePath = _parameter.getAsFile().getAbsolutePath();
            if(_parameter.getId() == ToolSafeTest.CID_SL_XML_FILE && !filePath.endsWith(ToolSafeLinksConstants.NOFILE))
            {
            	_xmlFile = _parameter.getAsFile();
            	if (_xmlFile.exists())
            	{
                	_foundFile = true;
            		FailuresHandler failuresHandler = new FailuresHandler(textOutput);
        			XMLFileParser xmlFailureFileParser = new XMLFileParser(filePath, failuresHandler);
        			lStereotypeMap = failuresHandler.getStereotypeMap();
        			
        			if (lStereotypeMap == null)
        			{
        				textOutput.writeLn(" the .xml file is empty or not corret!");
        				result = false;
                		return result;
        			}
            	}
            	else
            	{
            		textOutput.writeLn("The inputed file \"" + filePath + "\" is not existed!!!");
            		result = false;
            		return result;
            	}
            }
    	}
 	
		int p = 0;
		
		if (!linkMap.isEmpty())
        {
            for(Iterator iterLinkKey = linkMap.keySet().iterator(); iterLinkKey.hasNext();)
            {
                p++;
            	diagStereotype linkStereotype;
                safeLinksXml slXml;
                String failureLink1 = null;
                String failureLink2 = null;
                float failureValue1 = Float.NaN;
                float failureValue2 = Float.NaN;
                
                String dependRedundancy = null;
                String goalDependency = null;
                float dependValue = Float.NaN;
                
                boolean identicalRed = true;
                
                textOutput.writeLn("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            	textOutput.writeLn(">>>>>>>>>>                         the " + p + ". test is starting                        <<<<<<<<<");

                String linkName = (String) iterLinkKey.next();
                textOutput.writeLn("The link to test named: " + linkName);
                diagComponent linkToComp = (diagComponent) linkMap.get(linkName);
                if (linkToComp == null)
                {
                	textOutput.writeLn("This link isn\'t in the HashMap of links.");
                    result = false;
                    continue;
                }
                diagStereotype stereotypeOfLink = linkToComp.getStereotype();
                if (stereotypeOfLink == null)
                {
                	textOutput.writeLn("This link hasn\'t a stereotype.");
                    result = false;
                    continue;
                }
                String nameStereoOfLink = stereotypeOfLink.getName();
                textOutput.writeLn("This link has a stereotype named: " + nameStereoOfLink);
                String dependencyName = getRelations(linkName);
                if (dependencyName == null)
                {
                	textOutput.writeLn("The dependency in corresponding with the Link is not founnd.");
                    result = false;
                    continue;
                }
                textOutput.writeLn("The corresponding dependency named: " + dependencyName);
                if(dependencyName != null)
                {
                	diagComponent dependToComp = (diagComponent) dependencyMap.get(dependencyName);
                    HashMap TagValueOFDepend = dependToComp.getTagandValue();
                    for (Iterator iterDependTagKey = TagValueOFDepend.keySet().iterator(); iterDependTagKey.hasNext();)
                    {
                        String tagOfDepend = (String) iterDependTagKey.next();
                        if (tagOfDepend.equalsIgnoreCase(ToolSafeLinksConstants.REDUNDANCY))
                        {
                        	dependRedundancy = (String) TagValueOFDepend.get(tagOfDepend);
                        	if (dependRedundancy != null)
							{
			                    textOutput.writeLn("The dependency \"" + dependencyName + "\" has a redundancy named: \""
										+ dependRedundancy + "\".");								
							}
							else
							{
			                    textOutput.writeLn("The dependency \"" + dependencyName + "\" hasn\'t redundancy.");
							}
                        }
                    }
                    
                	if (_foundFile)
                	{
                		slXml = (safeLinksXml) lStereotypeMap.get(nameStereoOfLink);
                		if (slXml == null)
                        {
                        	textOutput.writeLn("There isn\'t a corresponding stereotypeo \"" + nameStereoOfLink 
                        			+ "\" for the link \"" + linkName + "\" in the Failures.xml file.");
                            result = false;
                            continue;
                        }
                		else
                			textOutput.writeLn("The link \"" + linkName + "\" has a corresponding stereotypeo \"" 
                					+ nameStereoOfLink + "\" in the Failures.xml file.");
                		HashMap redundancyMap = slXml.getRedundancyMap();
                		if (redundancyMap == null)
                        {
                        	textOutput.writeLn("The stereotypeo \"" + nameStereoOfLink 
                        			+ "\" in the Failures.xml file hasn\'t redundancy model and failures.");
                            result = false;
                            continue;
                        }
                		String[] failures = new String[2];
                		failures[0] = null;
                		failures[1] = null;
                		failures = (String[]) redundancyMap.get(dependRedundancy);
                		String failure1Str = new String();
                		failure1Str = failures[0];
                        if (failure1Str == null)
                        {
                        	textOutput.writeLn("The stereotype \"" + nameStereoOfLink + "\" of the Link \"" + 
                        			linkName+ "\" hasn\'t defined a failure.");                        	
                        	result = false;
                        	continue;
                        }                           	
                    	int cs1 = failure1Str.indexOf("(");
                        int ce1 = failure1Str.indexOf(")");
                        if (cs1 != -1 && ce1 != -1)
                        {
                            try
                            {
                                failureLink1 = failure1Str.substring(0, cs1);
                                String valueStr = failure1Str.substring(cs1+1, ce1);
                                failureValue1 = Float.parseFloat(valueStr);
                                if (failureValue1 == Float.NaN)
                                {
                                	textOutput.writeLn("the value of the Failure1 \"" + failureLink1 +
                                			"\" in the stereotype + \"" + nameStereoOfLink + 
											"\" of the Link \"" + linkName+ "\"is not defined.");                                	
                                	result = false;
                                	continue;                                	
                                }
                            }
                            catch (NumberFormatException exc)
                            {
                                textOutput.writeLn("The value of the Failure1 \"" + failureLink1 + "\" is not correct");                                
                                result = false;
                                continue;
                            }
                        }
                        String failure2Str = new String();
                		failure2Str = failures[1];
                        if (failure2Str == null || failure2Str.equals(""))
                        {
                        	textOutput.writeLn("The stereotype \"" + nameStereoOfLink + "\" of the Link \"" + 
                        			linkName+ "\" has defined only one failure.");                        	
                        }
                        else
                        {
	                    	int cs2 = failure2Str.indexOf("(");
	                        int ce2 = failure2Str.indexOf(")");
	                        if (cs2 != -1 && ce2 != -1)
	                        {
	                            try
	                            {
	                            	failureLink2 = failure2Str.substring(0, cs2);
	                                String valueStr = failure2Str.substring(cs2+1, ce2);
	                                failureValue2 = Float.parseFloat(valueStr);
	                                if (failureValue2 == Float.NaN)
	                                {
	                                	textOutput.writeLn("the value of the Failure2 \"" + failureLink2 +
	                                			"\" in the stereotype + \"" + nameStereoOfLink + 
												"\" of the Link \"" + linkName+ "\"is not defined.");                                	
	                                	result = false;
	                                	continue;                                	
	                                }
	                            }
	                            catch (NumberFormatException exc)
	                            {
	                                textOutput.writeLn("The value of the Failure2 \"" + failureLink2 + "\" is not correct");                                
	                                result = false;
	                                continue;
	                            }
	                        }
                        }
                    }
                    else
                    {
                        HashMap TagValueOfLStereo = stereotypeOfLink.getTagandValue();
                        for (Iterator iterLSteTagKey = TagValueOfLStereo.keySet().iterator(); iterLSteTagKey.hasNext();)
                        {
                            String ValueOFLStereo = null;
                        	String linkRedundancy = null;
                            String tagOfLStereo = (String) iterLSteTagKey.next();
                            if (tagOfLStereo.equalsIgnoreCase(ToolSafeLinksConstants.REDUNDANCY))
                            {
                            	linkRedundancy = (String) TagValueOfLStereo.get(tagOfLStereo);
                            	if (!linkRedundancy.equalsIgnoreCase(dependRedundancy))
								{
                            		textOutput.writeLn("The Link \"" + linkName + "\" and the corresponding dependency \"" + 
                            				dependencyName + "\" hasn\'t has not identical redundancy model. ");
                            		textOutput.writeLn("They can not be compared. Check for this link has failed."); 
                            		identicalRed = false;
                                	result = false;
                                	break;
								}
                            }
                            if (tagOfLStereo.equalsIgnoreCase(ToolSafeLinksConstants.FAILURE1))
                            {
                            	ValueOFLStereo = (String) TagValueOfLStereo.get(tagOfLStereo);
                                if (ValueOFLStereo == null)
                                {
                                	textOutput.writeLn("The stereotype \"" + stereotypeOfLink.getName() + "\" of the Link \"" + 
                                			linkName+ "\" hasn\'t tagged value.");                        	
                                	result = false;
                                	continue;
                                }
                                textOutput.writeLn("The stereotype \"" + stereotypeOfLink.getName() + "\" of the Link \"" + 
                            			linkName+ "\" hasn tagged value \"" + tagOfLStereo + "\" = \"" + ValueOFLStereo + "\" .");
                                int csu1 = ValueOFLStereo.indexOf("(");
                                int ceu1 = ValueOFLStereo.indexOf(")");
                                if (csu1 != -1 && ceu1 != -1)
                                {
                                    try
                                    {
                                        failureLink1 = ValueOFLStereo.substring(0, csu1);
                                        String valueStr = ValueOFLStereo.substring(csu1+1, ceu1);
                                        failureValue1 = Float.parseFloat(valueStr);
                                        if (failureValue1 == Float.NaN)
                                        {
                                        	textOutput.writeLn("the value of the Failure \"" + failureLink1 +
                                        			"\" in the stereotype + \"" + stereotypeOfLink.getName() + 
        											"\" of the Link \"" + linkName + "\"is not defined.");                                	
                                        	result = false;
                                        	continue;                                	
                                        }
                                    }
                                    catch (NumberFormatException exc)
                                    {
                                        textOutput.writeLn("The value of the Failure \"" + failureLink1 + "\" is not correct");                                
                                        result = false;
                                        continue;
                                    }
                                }                            	
                            }
                            if (tagOfLStereo.equalsIgnoreCase(ToolSafeLinksConstants.FAILURE2))
                            {
                            	ValueOFLStereo = (String) TagValueOfLStereo.get(tagOfLStereo);
                                if (ValueOFLStereo == null)
                                {
                                	textOutput.writeLn("The failure2 of the stereotype \"" + nameStereoOfLink 
                                			+ "\" of the Link \"" +	linkName+ "\" hasn\'t value."); 
                                	result = false;
                                	continue;
                                }
                                textOutput.writeLn("The stereotype \"" + stereotypeOfLink.getName() + "\" of the Link \"" + 
                            			linkName+ "\" has tagged value \"" + tagOfLStereo + "\" = \"" + ValueOFLStereo + "\" .");
                                int csu2 = ValueOFLStereo.indexOf("(");
                                int ceu2 = ValueOFLStereo.indexOf(")");
                                if (csu2 != -1 && ceu2 != -1)
                                {
                                    try
                                    {
                                    	failureLink2 = ValueOFLStereo.substring(0, csu2);
                                        String valueStr = ValueOFLStereo.substring(csu2+1, ceu2);
                                        failureValue2 = Float.parseFloat(valueStr);
                                        if (failureValue2 == Float.NaN)
                                        {
                                        	textOutput.writeLn("the value of the Failure \"" + failureLink2 +
                                        			"\" in the stereotype + \"" + stereotypeOfLink.getName() + 
        											"\" of the Link \"" + linkName + "\"is not defined.");                                	
                                        	result = false;
                                        	continue;                                	
                                        }
                                    }
                                    catch (NumberFormatException exc)
                                    {
                                        textOutput.writeLn("The value of the Failure \"" + failureLink2 + "\" is not correct");                                
                                        result = false;
                                        continue;
                                    }
                                }                            	
                            }
                        }
                    }
                    
                	if (!identicalRed)
                	{
                        textOutput.writeLn("");
                    	textOutput.writeLn(">>>>>>>>>>                         the " + p + ". test is finished.                       <<<<<<<<<");
                        textOutput.writeLn("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
                		continue;
                	}
                	
                    diagStereotype StereotypeOfDepend = dependToComp.getStereotype();
                    HashMap TagValueOfDStereo = StereotypeOfDepend.getTagandValue();
                    String ValueOFDStereo = null;
                    for (Iterator iterDSteTagKey = TagValueOfDStereo.keySet().iterator(); iterDSteTagKey.hasNext();)
                    {
                        String tagOfDStereo = (String) iterDSteTagKey.next();
                        if (tagOfDStereo.equalsIgnoreCase(ToolSafeLinksConstants.GOAL) || tagOfDStereo.equalsIgnoreCase(ToolSafeLinksConstants.GOAL1)
                        		||tagOfDStereo.equalsIgnoreCase(ToolSafeLinksConstants.GOAL2))
                        {
                        	ValueOFDStereo = (String) TagValueOfDStereo.get(tagOfDStereo);
                            textOutput.writeLn("The stereotype \"" + StereotypeOfDepend.getName() + "\" of dependency \"" + 
                            		dependToComp.getName() + "\" hast goal = \"" + ValueOFDStereo + "\".");
                        	
                            int cs = ValueOFDStereo.indexOf("(");
                            int ce = ValueOFDStereo.indexOf(")");
                            if (cs != -1 && ce != -1)
                            {
                                try
                                {
                                    goalDependency = ValueOFDStereo.substring(0, cs);
                                    String valueStr = ValueOFDStereo.substring(cs+1, ce);
                                    dependValue = Float.parseFloat(valueStr);
                                    if (dependValue == Float.NaN)
                                    {
                                    	textOutput.writeLn("the value of the goal \"" + goalDependency +
                                    			"\" in the stereotype + \"" + StereotypeOfDepend.getName() + 
        										"\" of the Link \"" + dependencyName+ "\"is not defined.");                            	
                                    	result = false;
                                    	continue;                                	
                                    }
                                }
                                catch (NumberFormatException exc)
                                {
                                    textOutput.writeLn("The value of the goal \"" + goalDependency + "\" is not correct");                            
                                    result = false;
                                    continue;
                                }
                            }                    
                            
                            if ((goalDependency.equalsIgnoreCase(ToolSafeLinksConstants.IMMEDIATE) && 
                            		(failureLink1.equalsIgnoreCase(ToolSafeLinksConstants.DELAY) || 
                            				failureLink2.equalsIgnoreCase(ToolSafeLinksConstants.DELAY)))|| 
        							(goalDependency.equalsIgnoreCase(ToolSafeLinksConstants.CORRECT) && 
        		                    (failureLink1.equalsIgnoreCase(ToolSafeLinksConstants.CORRUPTION) ||
        		                    		failureLink2.equalsIgnoreCase(ToolSafeLinksConstants.CORRUPTION))) ||
        							(goalDependency.equalsIgnoreCase(ToolSafeLinksConstants.EVENTUAL) && 
        				            (failureLink1.equalsIgnoreCase(ToolSafeLinksConstants.LOSS) ||
        				            		failureLink2.equalsIgnoreCase(ToolSafeLinksConstants.LOSS))))
                            {
                            	textOutput.writeLn("");
                                textOutput.writeLn("Ckecking for link responding to \"" + ValueOFDStereo +"\" ...");
                                if (goalDependency.equalsIgnoreCase(ToolSafeLinksConstants.IMMEDIATE))
                                {
                                    if (failureLink1.equalsIgnoreCase(ToolSafeLinksConstants.DELAY))
                                    {
                                    	if (failureValue1 <= dependValue + 0.000001)
                                        {
                                            textOutput.writeLn("This check for link \"" + linkName + "\" has succeeded.");                                
                                        }
                                        else
                                        {
                                            textOutput.writeLn("This check for link \"" + linkName + "\" has failed.\n" +
                                            		"The Value of Delay of the Failure \"" + failureValue1 + "\" is bigger as" +
                                            		" the Value of the Immediate of the Goal \"" + dependValue + "\".");                                
                                            result = false;
                                        }                            	
                                    }
                                    else if (failureLink2.equalsIgnoreCase(ToolSafeLinksConstants.DELAY))
        							{
                                       	if (failureValue2 <= dependValue + 0.000001)
                                        {
                                            textOutput.writeLn("This check for link \"" + linkName + "\" has succeeded.");                                
                                        }
                                        else
                                        {
                                            textOutput.writeLn("This check for link \"" + linkName + "\" has failed.\n" +
                                            		"The Value of Delay of the Failure \"" + failureValue2 + "\" is bigger as" +
                                            		" the Value of the Immediate of the Goal \"" + dependValue + "\".");                                
                                            result = false;
                                        }                              	
        							}
                                }
                                if (goalDependency.equalsIgnoreCase(ToolSafeLinksConstants.CORRECT))
        						{
                                	if (failureLink1.equalsIgnoreCase(ToolSafeLinksConstants.CORRUPTION))
                                    {
            							if (failureValue1 <=  (1 - dependValue) + 0.000001 )
            							{
            								textOutput.writeLn("This check for link \"" + linkName + "\" has succeeded.");							
            							}
            							else
            							{
            								textOutput.writeLn("This check for link \"" + linkName + "\" has failed.\n" +
            										"The Value of Corruption of the Failure \"" + failureValue1 + "\" is bigger as" +
                                            		" (1 - the Value of the Correct of the Goal \"" + dependValue + "\").");								
                                            result = false;	
            							}                        		
                                    }
                                	else if (failureLink2.equalsIgnoreCase(ToolSafeLinksConstants.CORRUPTION))
                                	{
            							if (failureValue2 <=  (1 - dependValue) + 0.000001 )
            							{
            								textOutput.writeLn("This check for link \"" + linkName + "\" has succeeded.");							
            							}
            							else
            							{
            								textOutput.writeLn("This check for link \"" + linkName + "\" has failed.\n" +
            										"The Value of Corruption of the Failure \"" + failureValue2 + "\" is bigger as" +
                                            		" (1 - the Value of the Correct of the Goal \"" + dependValue + "\").");								
                                            result = false;	
            							}                        		
                                       		
                                	}
        						}
                                if (goalDependency.equalsIgnoreCase(ToolSafeLinksConstants.EVENTUAL))
                                {
                                	if (failureLink1.equalsIgnoreCase(ToolSafeLinksConstants.LOSS))
                                    {
                                        if (failureValue1 <= (1- dependValue) + 0.000001)
                                        {
                                            textOutput.writeLn("This check link \"" + linkName + "\" has succeeded.");                                
                                        }
                                        else
                                        {
                                            textOutput.writeLn("This check for link \"" + linkName + "\" has failed.\n" +
            										"The Value of Loss of the Failure \"" + failureValue1 + "\" is bigger as" +
                                            		" (1 - the Value of the Eventual of the Goal \"" + dependValue + "\").");                
                                            result = false;
                                        }                        		
                                    }
                                	else if (failureLink2.equalsIgnoreCase(ToolSafeLinksConstants.LOSS))
                                	{
                                        if (failureValue2 <= (1- dependValue) + 0.000001)
                                        {
                                            textOutput.writeLn("This check for link \"" + linkName + "\" has succeeded.");                                
                                        }
                                        else
                                        {
                                            textOutput.writeLn("This check for link \"" + linkName + "\" has failed.\n" +
            										"The Value of Loss of the Failure \"" + failureValue2 + "\" is bigger as" +
                                            		" (1 - the Value of the Eventual of the Goal \"" + dependValue + "\").");                
                                            result = false;
                                        }                          		
                                	}                        		
                                }
                                textOutput.writeLn("");
                            }
                            else
                            {
                                textOutput.writeLn("The goal in the stereotype of the link \"" + linkName
                                		+ "\" and the dependency \"" + dependencyName + "\" is not corresponding.");                        
                                result = false;
                                continue;
                            } 
                        }
                    }
                    if (ValueOFDStereo == null)
                    {
                    	textOutput.writeLn("The stereotype of the dependency \"" + dependencyName+ "\" hasn\'t tagged value.");                    	
                    	result = false;
                    	continue;                                	
                    }
                }
                textOutput.writeLn("");
            	textOutput.writeLn(">>>>>>>>>>                         the " + p + ". test is finished.                       <<<<<<<<<");
                textOutput.writeLn("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            }
        }
		else
		{
			textOutput.writeLn("There isn\'t a Link in this Diagram.");                        
            result = false;
		}

    	return result;
    }
}
