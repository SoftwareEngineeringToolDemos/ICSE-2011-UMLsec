/*
 * Role.java
 *
 * Created on 14. November 2004, 22:57
 */

package tum.umlsec.viki.tools.sapperm.xmlparser;

import java.util.Vector;
/**
 *
 * @author  Milen
 */
public class Role {
    
    public String name;
    public String role_id;
    /** Creates a new instance of Role */
    public Role(String n, String id) {
        name = n;
        role_id = id;
    }
    
    public Vector getUsers(Vector users){
        return new Vector();
    }
}
