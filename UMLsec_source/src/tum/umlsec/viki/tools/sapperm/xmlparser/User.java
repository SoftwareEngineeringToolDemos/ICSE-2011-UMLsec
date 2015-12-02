/*
 * user.java
 *
 * Created on 14. November 2004, 21:06
 */

package tum.umlsec.viki.tools.sapperm.xmlparser;

import java.util.Vector;
/**
 *
 * @author  Milen
 */
public class User {
    
    public String name;
    public Vector roles;
    /** Creates a new instance of user */
    public User(String name, Vector r) {
        this.name = name;
        roles = r;
    }
    
    public void addRole(Object role){
        roles.add(role);
    }
    
    public Vector getRoles(){
        return roles;
    }
}
