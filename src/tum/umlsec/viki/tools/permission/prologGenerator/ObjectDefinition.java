/*
 * ObjectDefinition.java
 *
 * Created on 30. April 2004, 13:33
 */

package tum.umlsec.viki.tools.permission.prologGenerator;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import org.omg.uml.behavioralelements.commonbehavior.Instance;
import org.omg.uml.foundation.core.Classifier;
import org.omg.uml.foundation.core.Stereotype;
import org.omg.uml.foundation.core.TaggedValue;

import tum.umlsec.viki.tools.permission.common.ClassDelegation;
import tum.umlsec.viki.tools.permission.common.ClassPermission;
import tum.umlsec.viki.tools.permission.common.StaticParser;
import tum.umlsec.viki.tools.permission.common.StaticParserException;

/**
 *
 * @author  Markus
 */
public class ObjectDefinition {
    
    private HashSet permissions = new HashSet();
    private HashSet delegations = new HashSet();
    private String  className;
    private String  objectName;
    
    
    /** Creates a new instance of ObjectDefinition */
    public ObjectDefinition(Instance _object) 
    {

        this.objectName= _object.getName();
        Collection collClass = _object.getClassifier();
        for(Iterator iterClass = collClass.iterator(); iterClass.hasNext();)
        {                        
            Classifier classif = (Classifier) iterClass.next();
            this.className = classif.getName();        
        }        

        
        /*
         * search for right stereotype ...
         */
        boolean bPermission = false;
        Collection collStereo = _object.getStereotype();
        for(Iterator iterStereo = collStereo.iterator(); iterStereo.hasNext();)
        {                        
            Stereotype stereotype = (Stereotype) iterStereo.next();
            if ( stereotype.getName().equalsIgnoreCase("permission") )
            {
                bPermission = true;
                break;
            }
        }

        /* 
        * ... and if found search for permissions
        */
        if (bPermission)
        {
            Collection collTags = _object.getTaggedValue();
            for(Iterator iterTags = collTags.iterator(); iterTags.hasNext();)
            {
                TaggedValue taggedVal  = (TaggedValue) iterTags.next();
                if ( taggedVal.getType().getName().equalsIgnoreCase("permission") )
                {
                    Collection collDataValue = taggedVal.getDataValue();
                    for(Iterator iterTag = collDataValue.iterator(); 
                        iterTag.hasNext();)
                    {
                            
                        String strDataValue = (String) iterTag.next(); 
                        try
                        {
                            this.permissions.add( 
                                StaticParser.parsePermission( strDataValue ) );
                        }
                        catch (StaticParserException spe)
                        {}
                    }        
                }
                if ( taggedVal.getType().getName().equalsIgnoreCase("delegation") )
                {
                    Collection collDataValue = taggedVal.getDataValue();
                    for(Iterator iterTag = collDataValue.iterator(); 
                        iterTag.hasNext();)
                    {
                            
                        String strDataValue = (String) iterTag.next(); 
                        try
                        {
                            this.delegations.add( 
                                StaticParser.parseDelegation( strDataValue ) );
                        }
                        catch (StaticParserException spe)
                        {}
                    }        
                }
            }
        }
    }
    
    
    public Vector prologPermissions()
    {
        
        StringBuffer strbReturn = new StringBuffer();
        Vector       vecReturn  = new Vector();
        for(Iterator iterPerm = this.permissions.iterator(); 
            iterPerm.hasNext();)
        {
            ClassPermission perm = (ClassPermission) iterPerm.next();
            strbReturn.append("permission(");
            strbReturn.append(this.objectName.toLowerCase());
            strbReturn.append(", perm(");
            strbReturn.append(perm.getOwner().toLowerCase());
            strbReturn.append(", ");
            strbReturn.append(perm.getPermission().toLowerCase());
            strbReturn.append(")).");
            
            vecReturn.add(strbReturn.toString());
            strbReturn = new StringBuffer();
        }
        return vecReturn;
    }

    
    public Vector prologDelegation()
    {
        StringBuffer strbReturn = new StringBuffer();
        Vector       vecReturn  = new Vector();
        
        for(Iterator iterDeleg = this.delegations.iterator(); 
            iterDeleg.hasNext();)
        {
            ClassDelegation deleg = (ClassDelegation) iterDeleg.next();
            strbReturn.append("delegate(");
            strbReturn.append(this.objectName.toLowerCase());
            strbReturn.append(", perm(");
            strbReturn.append(deleg.getObject().toLowerCase());
            strbReturn.append(", ");
            strbReturn.append(deleg.getPermission().toLowerCase());
            strbReturn.append(")");
            
            if (! deleg.getRoles().isEmpty() )
            {
                strbReturn.append(",[");        
            }
            
            boolean first = true;
            for(Iterator iterRoles = deleg.getRoles().iterator(); 
                iterRoles.hasNext();)
            {
                String role = (String) iterRoles.next();
                if (first) 
                {
                    strbReturn.append("");
                    first = false;
                }
                else
                {
                    strbReturn.append(", ");                    
                }
                strbReturn.append(role.toLowerCase());
            }
            
            if (! deleg.getRoles().isEmpty() )
            {
                strbReturn.append("]");
            }
            
            strbReturn.append(").");
            
            vecReturn.add(strbReturn.toString());            
            strbReturn = new StringBuffer();
        }
        return vecReturn;
        
    }

    
    public String prologClassmapping()
    {
        
        return "isOf("+ this.objectName.toLowerCase() + ", " + 
               this.className.toLowerCase() + ").";
        
    }    
}
