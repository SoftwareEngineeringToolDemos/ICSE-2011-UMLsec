/*
 * ClassRepresentation.java
 *
 * Created on 22. April 2004, 16:44
 */

package tum.umlsec.viki.tools.permission.permission;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.omg.uml.foundation.core.Operation;
import org.omg.uml.foundation.core.Stereotype;
import org.omg.uml.foundation.core.TaggedValue;
import org.omg.uml.foundation.core.UmlClass;

import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.tools.permission.common.ClassDelegation;
import tum.umlsec.viki.tools.permission.common.ClassOperations;
import tum.umlsec.viki.tools.permission.common.ClassPermission;
import tum.umlsec.viki.tools.permission.common.StaticParser;
import tum.umlsec.viki.tools.permission.common.StaticParserException;

/**
 *
 * @author  Markus
 */
public class SecClassRep {
    
    private String  className;
    private HashSet permissions = new HashSet();
    private HashSet delegations = new HashSet();
    private HashSet operations  = new HashSet();
    
    /** Creates a new instance of ClassRepresentation */
    public SecClassRep(String      _className, 
                       UmlClass    _mdr_class, 
                       ITextOutput _textOutput)
    {
        this.className = _className;

        /* 
         * parsing classes' tagged values to find "permissions" and "delegations"
         */
        Collection collTags = _mdr_class.getTaggedValue();
        for(Iterator iter = collTags.iterator(); iter.hasNext();)
        {
            TaggedValue tagVal  = (TaggedValue) iter.next();
            if ( tagVal.getType().getName().equalsIgnoreCase("permission") ){
                for(Iterator tagValueStr = tagVal.getDataValue().iterator(); 
                    tagValueStr.hasNext();)

                {
                    String strValue = (String) tagValueStr.next(); 
                    try
                    {
                        ClassPermission clsPermission = 
                            StaticParser.parsePermission(strValue);
                        permissions.add(clsPermission);
                    }
                    catch (StaticParserException spe){
                        
                        strValue = "NOT PARSEABLE: " + strValue;
                    }                    
                }
            }
        }

        
        collTags = _mdr_class.getTaggedValue();
        for(Iterator iter = collTags.iterator(); iter.hasNext();)
        {
            TaggedValue tagVal  = (TaggedValue) iter.next();
            if ( tagVal.getType().getName().equalsIgnoreCase("delegation") ){
                for(Iterator tagValueStr = tagVal.getDataValue().iterator(); 
                    tagValueStr.hasNext();)
                {
                    String strValue = (String) tagValueStr.next(); 
                    try
                    {
                        ClassDelegation clsDelegation = 
                                StaticParser.parseDelegation(strValue);
                        
                        if ( this.hasPermission(clsDelegation.getObject(), 
                                                clsDelegation.getPermission())
                            )
                        {
                            delegations.add(clsDelegation);
                        }
                        else
                        {
                            strValue = "PERMISSION NOT FOUND: " + strValue;
                        }
                    }
                    catch (StaticParserException spe){

                        strValue = "NOT PARSEABLE: " + strValue;
                    }                    
                }
            }
        }        
   }
        
    
    public boolean hasPermission(String object, String permission)
    {
        for(Iterator iter = this.permissions.iterator(); iter.hasNext();)
        {
            ClassPermission clsPermission = (ClassPermission) iter.next();
            if ( clsPermission.getOwner().equalsIgnoreCase(object) &&
                 clsPermission.getPermission().equalsIgnoreCase(permission))
                 return true;
        }
        return false;
    }
    
    
    public void addPermission(ClassPermission perm)
    {
        permissions.add(perm);        
    }

    
    public boolean hasDelegation(String object, String permission, HashSet roles)
    {
        for(Iterator iter = this.delegations.iterator(); iter.hasNext();)
        {
            ClassDelegation clsDelegation = (ClassDelegation) iter.next();
            if ( clsDelegation.getObject().equalsIgnoreCase(object)         &&
                 clsDelegation.getPermission().equalsIgnoreCase(permission) &&
                 clsDelegation.delegateToRole(roles) )
                 return true;
        }        
        return false;
    }
    
    
    public void addDelegation(ClassDelegation delegation)
    {
        delegations.add(delegation);        
    }
    
    
    public boolean hasOpPermDef(String _operation, String _permission)
    {
        for(Iterator iter = this.operations.iterator(); iter.hasNext();)
        {
            ClassOperations clsOperation= (ClassOperations) iter.next();
            
            if ( clsOperation.getOperation().equalsIgnoreCase(_operation) &&
                 clsOperation.needsPermission(_permission)                     
               )
               return true;
        }        
        return false;
    }

    public boolean hasOpAllPermissions(String _operation, HashSet _permission){
        for(Iterator iter = this.operations.iterator(); iter.hasNext();)
        {
            ClassOperations clsOperation= (ClassOperations) iter.next();
                        
            if ( clsOperation.getOperation().equalsIgnoreCase(_operation))
            {
                return clsOperation.allPermissionsFound( _permission );
            }
        }        
        return false;
    }
    
    public boolean hasOperation(String operationName)
    {
        for(Iterator iter = this.operations.iterator(); iter.hasNext();)
        {
            ClassOperations clsOperation= (ClassOperations) iter.next();
            
            if ( clsOperation.getOperation().equalsIgnoreCase(operationName))
            {
                return true;
            }        
        }
        return false;      
    }
    
       
    public String getClassName()
    {
        return this.className;
    }

    
    public void setOperation(ClassOperations operation)
    {
        operations.add(operation);
    }
    
    
    public String setOperation(Operation _operation)
    {
        String tmpReturn = " --- operation found: " + _operation.getName() +"\n" ;
        
        
        /*
         * parse permissions of operation
         */
                
        ClassOperations ops = new ClassOperations( _operation.getName() );
        this.operations.add(ops);
        
        boolean bPermission = false;
        for (Iterator iter = _operation.getStereotype().iterator(); iter.hasNext(); )        
        {
            Stereotype stereo = (Stereotype) iter.next();
            if ( stereo.getName().equalsIgnoreCase("permission_needed") )
            {
                bPermission = true;
                break;
            }
        }
        
        if (bPermission)
        {
            Collection tags = _operation.getTaggedValue();
            for(Iterator iter = tags.iterator(); iter.hasNext();)
            {
                TaggedValue tagVal  = (TaggedValue) iter.next();
                if ( tagVal.getType().getName().equalsIgnoreCase("permission") )
                {
                    for(Iterator tagValueStr = tagVal.getDataValue().iterator(); 
                                tagValueStr.hasNext();)
                    {
                        String strValue = (String) tagValueStr.next(); 
                        ops.addPermission(strValue);
                        tmpReturn += ("     permission: " + strValue +"\n");
                        
                    }
                }
            }
        }            
        return tmpReturn + " --- operation set: " + _operation.getName();

    }
    
    
    public void compareClass(SecClassRep compClass)
    {
        if (compClass == null) return;

        /* permissions */        
        for(Iterator iter = this.permissions.iterator(); iter.hasNext();)
        {
            ClassPermission clsPermission = (ClassPermission) iter.next();
            if (! compClass.hasPermission(
                        clsPermission.getOwner(),clsPermission.getPermission()))
            {
                compClass.addPermission(clsPermission);
            }
        }
        
        /*delegations */
        for(Iterator iter = this.delegations.iterator(); iter.hasNext();)
        {
            ClassDelegation clsDelegation = (ClassDelegation) iter.next();
            if (! compClass.hasDelegation(
                    clsDelegation.getObject(),
                    clsDelegation.getPermission(),
                    clsDelegation.getRoles()))
            {
                compClass.addDelegation(clsDelegation);
            }
        }
        
        for(Iterator iter = this.operations.iterator(); iter.hasNext();)
        {
            ClassOperations clsOperation = (ClassOperations) iter.next();
            if (! compClass.hasOperation(clsOperation.getOperation()))
            {
                compClass.setOperation(clsOperation);
            }
        }        
    }
}
