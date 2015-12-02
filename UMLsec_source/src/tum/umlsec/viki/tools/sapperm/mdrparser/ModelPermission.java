/*
 * permission.java
 *
 * Created on 11. November 2004, 23:32
 */

package tum.umlsec.viki.tools.sapperm.mdrparser;

import java.util.Vector;
/**
 *
 * @author  Milen
 */
public class ModelPermission {
 
    public String trans_id;
    private Vector authObjects;
    
    /** Creates a new instance of permission */
    public ModelPermission(String id, Vector auth) {
        trans_id = id;
        authObjects = auth;
    }
    
    public Vector getAuthObjects(){
        return authObjects;
    }
    
}
