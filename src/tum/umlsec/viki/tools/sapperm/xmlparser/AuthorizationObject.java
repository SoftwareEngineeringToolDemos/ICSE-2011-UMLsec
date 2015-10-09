/*
 * AuthorizationObject.java
 *
 * Created on 14. November 2004, 23:37
 */

package tum.umlsec.viki.tools.sapperm.xmlparser;

import java.util.Iterator;
import java.util.Vector;
/**
 *
 * @author  Milen
 */
public class AuthorizationObject {
    public String name, obj_id;
    private Vector authFields;
    /** Creates a new instance of AuthorizationObject */
    public AuthorizationObject(String name, String id, Vector fields) {
        this.name = name;
        obj_id = id;
        authFields = fields;
    }
    
    public Vector getAuthFields(){
        return authFields;
    }
    
    //looks for field with a specified value
    public boolean lookForField(String fieldId, String value) {
        boolean result = false;
        
        for(Iterator it = authFields.iterator(); it.hasNext(); ){
            AuthorizationField af = (AuthorizationField)it.next();
            
            if(af.field_id.equals(fieldId)){
                if( af.getValues().contains(value) ){
                    result = true;
                    break;
                }
                else{
                    continue;
                }
            }
        }
        
        return result;
    }
    
    
}
