package tum.umlsec.viki.tools.permission.prologGenerator;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import org.omg.uml.behavioralelements.commonbehavior.Action;
import org.omg.uml.behavioralelements.commonbehavior.CallAction;
import org.omg.uml.behavioralelements.commonbehavior.Stimulus;
import org.omg.uml.foundation.core.Stereotype;
import org.omg.uml.foundation.core.TaggedValue;

import tum.umlsec.viki.tools.permission.common.ClassCertificate;
import tum.umlsec.viki.tools.permission.common.StaticParser;
import tum.umlsec.viki.tools.permission.common.StaticParserException;

/**
 *
 * @author  Markus
 */
public class StimulusDefinition {
    
    private HashSet  permissions  = new HashSet();
    private HashSet  certificates = new HashSet();
    private HashSet  noPermission = new HashSet();
    private Action   action;
    private Stimulus stimulus;
    private String   objectName;
    
    
    /** Creates a new instance of ObjectDefinition */
    public StimulusDefinition(Stimulus _stimulus) 
    {
        stimulus = _stimulus;
        action   = _stimulus.getDispatchAction();   
        
        try{
            CallAction callAction = (CallAction) this.action;
            String     strOp      = callAction.getOperation().getName();
            Collection collTags   = callAction.getOperation().getTaggedValue();
            
            for(Iterator iterTags = collTags.iterator(); iterTags.hasNext();)
            {
                TaggedValue taggedVal  = (TaggedValue) iterTags.next();

                if ( taggedVal.getType().getName().equalsIgnoreCase(
                        "no_permission_needed") )
                {
                    Collection collDataValue = taggedVal.getDataValue();
                    for(Iterator iterTag = collDataValue.iterator(); 
                        iterTag.hasNext();)
                    {
                        String strDataValue = (String) iterTag.next(); 
                        this.noPermission.add("noPermission('" + strOp + 
                          "', " + (_stimulus.getReceiver().getName()).toLowerCase()+
                          ", "  + (strDataValue).toLowerCase() + ")." );
                    }
                }
            }
        }               
        catch (Exception exc)
        {}


        /*
         * find "permission_needed
         */
        boolean bPermission = false;
        Collection collStereo = _stimulus.getStereotype();
        for(Iterator iterStereo = collStereo.iterator(); iterStereo.hasNext();)
        {                        
            Stereotype stereotype = (Stereotype) iterStereo.next();
            if ( stereotype.getName().equalsIgnoreCase("permission_needed") 
                    ||
                 stereotype.getName().equalsIgnoreCase("delegation"))
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
            Collection collTags = _stimulus.getTaggedValue();
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
                            this.permissions.add( strDataValue.trim() );

                            
                    }        
                }
                if ( taggedVal.getType().getName().equalsIgnoreCase("certificate") )
                {
                    Collection collDataValue = taggedVal.getDataValue();
                    for(Iterator iterTag = collDataValue.iterator(); 
                        iterTag.hasNext();)
                    {
                            
                        String strDataValue = (String) iterTag.next(); 
                        try
                        {
                            this.certificates.add( 
                                StaticParser.parseCertificate( strDataValue ) );
                        }
                        catch (StaticParserException spe)
                        {}
                    }        
                }
            }
        }
    }
    
    
    public String getAction()    
    /* 
     * returns the operations name if this action is a call action
     * otherwise the actions's name is returned
     */        
    {
        try{
            CallAction callAction = (CallAction) this.action;
            String strOp = callAction.getOperation().getName();
            if (strOp != "")
		return strOp;
	    else 
		return action.getName();
        }   
        catch (Exception exc)
        {
            return action.getName();
        }
    }
    
    
    public Vector prologNoPerm()
    {
        Vector vecReturn = new Vector();
        vecReturn.addAll(this.noPermission);
        return vecReturn;
    }
    
    
    public Vector prologMessage(int msgNr)
    {
        StringBuffer msg = new StringBuffer(); 
        Vector vecReturn = new Vector();
        
        msg.append("msg("+ msgNr + ", " );
        msg.append(stimulus.getSender().getName().toLowerCase() +", ");
        msg.append(stimulus.getReceiver().getName().toLowerCase() + ", ");
        msg.append("'" + getAction() + "', ");
        
        // building permission list
        msg.append("[" );
        boolean komma = false;
        for ( Iterator it = this.permissions.iterator(); it.hasNext();)
        {
            String perm = (String) it.next();
            if (komma)
            {
                msg.append(", ");
            }   
            msg.append("perm(" + stimulus.getReceiver().getName().toLowerCase() + 
                       " , " + perm.toLowerCase() + ")");
            komma = true;
        }
        msg.append("], [");

        // building certifiacte list
        komma = false;
        for ( Iterator it = this.certificates.iterator(); it.hasNext();)
        {
            ClassCertificate cert = (ClassCertificate) it.next();
            if (komma)
            {
                msg.append(", ");
            }   
            msg.append( cert.prologCertificate() );
            komma = true;
        }
        msg.append("] ).");
     
        vecReturn.add(msg.toString());
        msg = new StringBuffer();
                
        return vecReturn;
    }    
}
