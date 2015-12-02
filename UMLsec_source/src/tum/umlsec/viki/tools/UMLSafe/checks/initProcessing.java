/*
 * Created on 2004-3-19
 * @author Shunwei Shen
 */
 
package tum.umlsec.viki.tools.UMLSafe.checks;

/**
 * Function:
 * initialize the processing of the diagramm
 */

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.omg.uml.UmlPackage;
import org.omg.uml.behavioralelements.commonbehavior.CommonBehaviorPackage;
import org.omg.uml.behavioralelements.commonbehavior.ComponentInstance;
import org.omg.uml.behavioralelements.commonbehavior.Instance;
import org.omg.uml.behavioralelements.commonbehavior.Link;
import org.omg.uml.behavioralelements.commonbehavior.LinkClass;
import org.omg.uml.behavioralelements.commonbehavior.LinkEnd;
import org.omg.uml.behavioralelements.commonbehavior.NodeInstance;
import org.omg.uml.foundation.core.CorePackage;
import org.omg.uml.foundation.core.Dependency;
import org.omg.uml.foundation.core.DependencyClass;
import org.omg.uml.foundation.core.Stereotype;
import org.omg.uml.foundation.core.StereotypeClass;
import org.omg.uml.foundation.core.TagDefinition;
import org.omg.uml.foundation.core.TaggedValue;
import org.omg.uml.foundation.core.UmlClass;
import org.omg.uml.foundation.core.UmlClassClass;
import org.omg.uml.modelmanagement.ModelManagementPackage;

import tum.umlsec.viki.framework.ExceptionProgrammLogicError;
import tum.umlsec.viki.framework.mdr.IMdrContainer;
import tum.umlsec.viki.tools.UMLSafe.util.diagComponent;
import tum.umlsec.viki.tools.UMLSafe.util.diagStereotype;


public class initProcessing
{
    IMdrContainer container;
    UmlPackage root;
    ModelManagementPackage modelpackage;
    CorePackage corepackage;
    CommonBehaviorPackage commonpackage;

//    private DependencyClass dependencyclass;
//    private LinkClass linkclass;
    
    HashMap dependencyMap = new HashMap();
    HashMap linkMap = new HashMap();
    HashMap classMap = new HashMap();
    HashMap dependCompInstanceMap = new HashMap();
    HashMap linkCompInstancceMap = new HashMap();
    HashMap dependSupplierMap = new HashMap();
    HashMap dependClientMap = new HashMap();

    /**
     * read the UML packages and disassemble the in MDR classes
     */
    public void init(IMdrContainer _container)
    {
    	container = _container;
        root = (UmlPackage) container.getUmlPackage();
        corepackage = root.getCore();
        commonpackage = root.getCommonBehavior();
    }
    
    /**
     * get the name of the stereotype of the diagram be parsed
     * @return the name of the stereotype of the diagram
     */
    public String getDiagramStereotype()
    {
        String nameDiagramStereotype = null;
        StereotypeClass stereotypeclass = corepackage.getStereotype();
        for (Iterator iterSt = stereotypeclass.refAllOfClass().iterator();iterSt.hasNext();)
        {
            Stereotype stereotypeOfDiagram = (Stereotype) iterSt.next();
            nameDiagramStereotype = stereotypeOfDiagram.getName();
        }
        return nameDiagramStereotype;
    }
    
    /**
     * List the relevant structur of every dependency components in the UML diagram
     */
    public void dumpDependencies()
    {
        DependencyClass dependencyclass = corepackage.getDependency();
        
        try
        {
            // list all dependencies  
            for(Iterator iterDep = dependencyclass.refAllOfClass().iterator(); iterDep.hasNext();)
            {
                diagComponent dDependency = new diagComponent();
                
                // all information about the Dependency can be accessed through the DependencyClass class here
                Dependency dependency = (Dependency)iterDep.next();
//                textOutput.writeLn("a Dependency named: " + dependency.getName());
                //System.out.println("a Dependency named: " + dependency.getName());
                dDependency.setName(dependency.getName());
                diagStereotype dStereotype =new diagStereotype();
                for (Iterator iterStereotype =dependency.getStereotype().iterator(); iterStereotype.hasNext();)
                {
                    Stereotype stereotype = (Stereotype) iterStereotype.next();
//                    textOutput.writeLn("This dependency has the stereotype named: " + stereotype.getName());
//                  System.out.println("This Dependency has the Stereotype named: " + dStereotype.getName());
                    dStereotype.setName(stereotype.getName());
                    HashMap sTagAndValueMap = new HashMap();
                    for (Iterator sIterTaggedVal = stereotype.getTaggedValue().iterator(); sIterTaggedVal.hasNext();)
                    {
                        TaggedValue sTtaggedValue = (TaggedValue) sIterTaggedVal.next();
                        TagDefinition sTagDefiontion = sTtaggedValue.getType();
                        String sTagName = sTagDefiontion.getName();
                        //System.out.println("This stereotype has the tag named: " + dTagName);
                        String sTagValue = null;
                        for (Iterator sIterDataVal = sTtaggedValue.getDataValue().iterator(); sIterDataVal.hasNext();)
                        {
                            sTagValue = (String) sIterDataVal.next();
                            //System.out.println("This tag has a Value: " + dDataValue);
                        }
                        sTagAndValueMap.put(sTagName, sTagValue);                  
                    }
                    dStereotype.setTagAndValue(sTagAndValueMap);
                }
                dDependency.setStereotype(dStereotype);
                HashMap dTagAndValueMap = new HashMap();
                for (Iterator dIterTaggedVal = dependency.getTaggedValue().iterator(); dIterTaggedVal.hasNext();)
                {
                    TaggedValue dTaggedValue = (TaggedValue) dIterTaggedVal.next();
                    TagDefinition dTagDefiontion = dTaggedValue.getType();
                    String dTagName = dTagDefiontion.getName();
                    //System.out.println("This stereotype has the tag named: " + dTagName);
                    String dTagValue = null;
                    for (Iterator dIterDataVal = dTaggedValue.getDataValue().iterator(); dIterDataVal.hasNext();)
                    {
                        dTagValue = (String) dIterDataVal.next();
                        //System.out.println("This tag has a Value: " + dDataValue);
                    }
                    dTagAndValueMap.put(dTagName, dTagValue);                  
                }
                dDependency.setTagAndValue(dTagAndValueMap);
                dependencyMap.put(dDependency.getName(), dDependency);
                
                String[] dependCompArray = new String[2];
                dependCompArray = setDependCompInstance(dependency);
                String dependCompString = combineString(dependCompArray);
                dependCompInstanceMap.put(dependCompString, dependency.getName());
            }
        }
        catch (Exception exc)
        {
            System.out.println(exc.toString());
            exc.printStackTrace();
        }
    }
    
    /**
     * list the relevant structure of the link components in the UML diagram
     */
    public void dumpLinks()
    {
        LinkClass linkclass = commonpackage.getLink();
        
        try
        {           
            // list all the stereotypes of the links
            //System.out.println("====== In this model there are: ======");
            for(Iterator iterLink = linkclass.refAllOfClass().iterator(); iterLink.hasNext();)
            {
            	diagComponent dLink = new diagComponent();
            	Link link = (Link)iterLink.next();
//               textOutput.writeLn("a link namme: " + link.getName());
               dLink.setName(link.getName());
               diagStereotype dStereotype =new diagStereotype();
               for (Iterator iterStereotype =link.getStereotype().iterator(); iterStereotype.hasNext();)
               {
                   Stereotype stereotype = (Stereotype) iterStereotype.next();
//                   textOutput.writeLn("This link has the stereotype named: " + stereotype.getName());
//                                 System.out.println("This Dependency has the Stereotype named: " + dStereotype.getName());
                   dStereotype.setName(stereotype.getName());
                   HashMap sTagAndValueMap = new HashMap();
                   for (Iterator sIterTaggedVal = stereotype.getTaggedValue().iterator(); sIterTaggedVal.hasNext();)
                   {
                       TaggedValue sTtaggedValue = (TaggedValue) sIterTaggedVal.next();
                       TagDefinition sTagDefiontion = sTtaggedValue.getType();
                       String sTagName = sTagDefiontion.getName();
                       //System.out.println("This stereotype has the tag named: " + dTagName);
                       String sTagValue = null;
                       for (Iterator sIterDataVal = sTtaggedValue.getDataValue().iterator(); sIterDataVal.hasNext();)
                       {
                           sTagValue = (String) sIterDataVal.next();
                           //System.out.println("This tag has a Value: " + dDataValue);
                       }
                       sTagAndValueMap.put(sTagName, sTagValue);                  
                   }
                   dStereotype.setTagAndValue(sTagAndValueMap);
               }
               dLink.setStereotype(dStereotype);
               HashMap dTagAndValueMap = new HashMap();
               for (Iterator dIterTaggedVal = link.getTaggedValue().iterator(); dIterTaggedVal.hasNext();)
               {
                   TaggedValue dTaggedValue = (TaggedValue) dIterTaggedVal.next();
                   TagDefinition dTagDefiontion = dTaggedValue.getType();
                   String dTagName = dTagDefiontion.getName();
                   //System.out.println("This stereotype has the tag named: " + dTagName);
                   String dTagValue = null;
                   for (Iterator dIterDataVal = dTaggedValue.getDataValue().iterator(); dIterDataVal.hasNext();)
                   {
                       dTagValue = (String) dIterDataVal.next();
                       //System.out.println("This tag has a Value: " + dDataValue);
                   }
                   dTagAndValueMap.put(dTagName, dTagValue);                  
               }
               dLink.setTagAndValue(dTagAndValueMap);
               linkMap.put(dLink.getName(), dLink);
               
               String[] linkCompArray = new String[2];
               linkCompArray = setLinkCompInstance(link);
               linkCompInstancceMap.put(link.getName(), linkCompArray);
           }
        }
        catch (Exception exc)
        {
            System.out.println(exc.toString());
            exc.printStackTrace();
        }  
    }
    
    /**
     * list the relevant structure of the class components in the UML diagram
     *
     */
    public void dumpClasses()
    {
        UmlClassClass umlclassclass = corepackage.getUmlClass();

        try
        {
            for(Iterator iterClass = umlclassclass.refAllOfClass().iterator(); iterClass.hasNext();)
            {
                diagComponent diagClass = new diagComponent();
                UmlClass umlclass = (UmlClass) iterClass.next();
                diagClass.setName(umlclass.getName());
                diagStereotype dStereotype = new diagStereotype();
                for (Iterator iterStereotype = umlclass.getStereotype().iterator(); iterStereotype.hasNext();)
                {
                    Stereotype stereotype = (Stereotype) iterStereotype.next();
                    dStereotype.setName(stereotype.getName());
                    HashMap sTagAndValueMap = new HashMap();
                    for (Iterator sIterTaggedVal = stereotype.getTaggedValue().iterator(); sIterTaggedVal.hasNext();)
                    {
                        TaggedValue sTtaggedValue = (TaggedValue) sIterTaggedVal.next();
                        TagDefinition sTagDefiontion = sTtaggedValue.getType();
                        String sTagName = sTagDefiontion.getName();
                        String sTagValue = null;
                        for (Iterator sIterDataVal = sTtaggedValue.getDataValue().iterator(); sIterDataVal.hasNext();)
                        {
                            sTagValue = (String) sIterDataVal.next();
                            //System.out.println("This tag has a Value: " + dDataValue);
                        }
                        sTagAndValueMap.put(sTagName, sTagValue);                  
                    }
                    dStereotype.setTagAndValue(sTagAndValueMap);
                }
                diagClass.setStereotype(dStereotype);
                HashMap dTagAndValueMap = new HashMap();
                for (Iterator dIterTaggedVal = umlclass.getTaggedValue().iterator(); dIterTaggedVal.hasNext();)
                {
                    TaggedValue dTaggedValue = (TaggedValue) dIterTaggedVal.next();
                    TagDefinition dTagDefiontion = dTaggedValue.getType();
                    String dTagName = dTagDefiontion.getName();
                    //System.out.println("This stereotype has the tag named: " + dTagName);
                    String dTagValue = null;
                    for (Iterator dIterDataVal = dTaggedValue.getDataValue().iterator(); dIterDataVal.hasNext();)
                    {
                        dTagValue = (String) dIterDataVal.next();
                        //System.out.println("This tag has a Value: " + dDataValue);
                    }
                    dTagAndValueMap.put(dTagName, dTagValue);                  
                }
                diagClass.setTagAndValue(dTagAndValueMap);
                classMap.put(diagClass.getName(), diagClass);
            }
        }
        catch (Exception exc)
        {
            System.out.println(exc.toString());
            exc.printStackTrace();
        } 
    }
    
    /**
     * get the name of supplier and client of the inputted dependency
     * 
     * @param _dependency: the dependency for analyse
     * @return an array of String that saves the name of the supplier and client
     */
    private String[] setDependCompInstance(Dependency _dependency)
    {
        String[] dependCompInstance = new String[2];
        String dependName = _dependency.getName();
        int j = 0;
        Collection dependSupplier = _dependency.getSupplier();
        for (Iterator itSupplier = dependSupplier.iterator(); itSupplier.hasNext();)
        {
        	java.lang.Object sup = itSupplier.next();
        	if (ComponentInstance.class.isInstance(sup))
            {
        		Instance compInstanceSupplier = (ComponentInstance) sup;
            	dependSupplierMap.put(dependName, compInstanceSupplier.getName());
                dependCompInstance[j] = compInstanceSupplier.getName();
            }
            else
            {
            	UmlClass compInstanceSupplier = (UmlClass) sup;
            	dependSupplierMap.put(dependName, compInstanceSupplier.getName());
                dependCompInstance[j] = compInstanceSupplier.getName();
            } 
        	//textOutput.writeLn("the SupplierInstance of the dependency is: " + compInstanceSupplier.getName());
            //System.out.println("the SupplierInstance of the dependency is: " + compInstanceSupplier.getName());
            j++;
        }
        Collection dependClient = _dependency.getClient();
        for (Iterator itClient = dependClient.iterator(); itClient.hasNext();)
        {
        	java.lang.Object cli = itClient.next();
        	 if (ComponentInstance.class.isInstance(cli))
             {
        	 	Instance compInstanceClient = (ComponentInstance) cli;
        	 	dependClientMap.put(dependName, compInstanceClient.getName());
                dependCompInstance[j] = compInstanceClient.getName();
             }
        	 else
        	 {
        	 	UmlClass compInstanceClient = (UmlClass) cli;
        	 	dependClientMap.put(dependName, compInstanceClient.getName());
                dependCompInstance[j] = compInstanceClient.getName();
        	 }
//            textOutput.writeLn("the ClientInstance of the dependency is: " + compInstanceClient.getName());
            //System.out.println("the ClientInstance of the dependency is: " + compInstanceClient.getName());
        }
        
        return dependCompInstance;
    }
    
    /*
     * get the name of linkends of the inputted link
     * @param _link the link for analyse
     * @return an array of String that saves the name of the linkends
     */
    private String[] setLinkCompInstance(Link _link)
    {
        String[] linkCompInstance = new String[2];
        int i = 0;
        Collection linkConnection = _link.getConnection();
        for (Iterator itConnection = linkConnection.iterator(); itConnection.hasNext();)
        {
            LinkEnd linkEnd = (LinkEnd) itConnection.next();
            NodeInstance nodeInstance = (NodeInstance)linkEnd.getInstance();
//            textOutput.writeLn("one NodeInstance name of the link is: " + nodeInstance.getName());
            Collection resident = nodeInstance.getResident();
            if (!resident.isEmpty())
            {
                for (Iterator itCompInstance = resident.iterator(); itCompInstance.hasNext();)
                {
                    ComponentInstance componentInstance = (ComponentInstance) itCompInstance.next();
                    //System.out.println("CompInstance name  " + componentInstance.getName());
                    linkCompInstance[i] = componentInstance.getName();
                    i++;
                }
            }
        }
        return linkCompInstance;
    }
    
    // combine the Strings in the String array in a String
    private String combineString(String[] toCombine)
    {
       StringBuffer combString = new StringBuffer();
       for (int s = 0; s < toCombine.length; s++)
       {
            combString.append(toCombine[s]);
       }
       return combString.toString();
    }
    
    /**
     * set the one-to-one correspondence relations of the links and the dependency
     * @param _linkName the name of the link for corresponding
     * @return the name of the dependency 
     */
    public String getRelations(String _linkName)
    {
        //set the relation between links and dependencies
        String dependName = null;
        String[] lLinkCompArray = new String[2];
        String[] tempArray = new String[2];
        String lLinkCompString = null;
        String tempString = null;
        
        //System.out.println("====== the one-to-one correspondence relations are: ======");
        try
        {
            lLinkCompArray = (String[]) linkCompInstancceMap.get(_linkName);
            lLinkCompString = combineString(lLinkCompArray);
            for (int t = 0; t < 2; t++)
            {
                tempArray[t] = lLinkCompArray[1 - t];
                //System.out.println("the got Array from linkComp is " + lLinkCompArray[t]);
            }
            tempString = combineString(tempArray);
            if (dependCompInstanceMap.containsKey(lLinkCompString))
            {
                dependName = (String) dependCompInstanceMap.get(lLinkCompString);
            }
            else if (dependCompInstanceMap.containsKey(tempString))
            {
                dependName = (String) dependCompInstanceMap.get(tempString);
            }
            else
            {
                throw new ExceptionProgrammLogicError("the searched dependency is not in the HashMap!");
                //System.out.println("the searched dependency is not in the HashMap!");
            }
        }
        catch (Exception exc)
        {
            System.out.println(exc.toString());
            exc.printStackTrace();
        }
        
        return dependName;
    }
        
    /**
     *  @return the HashMap in which the dependencies are saved
     */
    public HashMap getDependencyMap()
    {
        return this.dependencyMap;
    }
    
    /**
     * @return the HashMap in which the links are saved
     */
    public HashMap getLinkMap()
    {
        return this.linkMap;
    }
    
    /**
     *  @return the HashMap in which the supplier and the client of a dependency are saved
     */
    public HashMap getDependCompInstanceMap()
    {
        return this.dependCompInstanceMap;
    }
        
    /**
     *  @return the HashMap in which the two linkends of a link are saved
     */
    public HashMap getLinkCompInstanceMap()
    {
        return this.linkCompInstancceMap;
    }
}
