/*
 * Permission.java
 *
 * Created on 14. November 2004, 23:25
 */

package tum.umlsec.viki.tools.sapperm.xmlparser;

import java.util.Iterator;
import java.util.Vector;
/**
 *
 * @author  Milen
 */
public class Permission {
    
    public String perm_id;
    private Vector role_ids, authObjects;
    
    /** Creates a new instance of Permission */
    public Permission(String permId, Vector r, Vector auth) {
        perm_id = permId;
        role_ids = r;
        authObjects = auth;
    }
    
    public Vector getRoles(){
        return role_ids;
    }
    
    public Vector getAuthObjects(){
        return authObjects;
    }
    
    //looks if value (permission) exists for a field of a specified object
    public boolean lookForPermission(String objectId, String fieldId, String value) {
        boolean result = false;
        
        for(Iterator it = authObjects.iterator(); it.hasNext(); ){
            AuthorizationObject ao = (AuthorizationObject)it.next();
            
            if(ao.obj_id.equals(objectId)){
                result = ao.lookForField(fieldId, value);
                break;
            }
        }
        
        return result;
    }
    
    public Vector getFieldValues(AuthorizationObject ao, AuthorizationField af){
        
        for(Iterator it = authObjects.iterator(); it.hasNext();){            
            AuthorizationObject o = (AuthorizationObject)it.next();
            
            if(o.name.equals(ao.name)){
                                
                for(Iterator it1 = o.getAuthFields().iterator(); it1.hasNext();){
                    AuthorizationField f = (AuthorizationField)it1.next();
                    
                    if(f.name.equals(af.name)){
                        return f.getValues();
                    }
                }
            }
        }
        
        return null;
    }
    
    //checks is object with a given name is contained in that permission
    
}
