/*
 * ClassDelegation.java
 *
 * Created on 23. April 2004, 16:38
 */

package tum.umlsec.viki.tools.permission.common;

import java.util.HashSet;
/**
 *
 * @author  Markus
 */
public class ClassDelegation {
    
    private String object;
    private String permission;
    private HashSet roles = new HashSet();
    
/** Creates a new instance of ClassDelegation */

    public ClassDelegation(String  _object, String _permission) 
    {
        object     = _object;
        permission = _permission;
    }    
    
    
    public void addRole(String role)
    {
        if ( role.equals("*") )
            this.roles.clear();
        else
            this.roles.add(role);    
    }
    
    
    public boolean delegateToRole(String role)
    {
        return (roles.contains(role) || roles.isEmpty() );
    }

    
    public boolean delegateToRole(HashSet roles)
    {
        return (roles.containsAll(roles));
    }

    
    public HashSet getRoles()
    {
        return this.roles;
    }
    
    
    public String getObject()
    {
        return object;
    }

    
    public String getPermission()
    {
        return permission;
    }
}
