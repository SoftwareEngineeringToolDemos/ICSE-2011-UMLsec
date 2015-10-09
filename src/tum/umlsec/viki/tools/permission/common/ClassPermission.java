/*
 * Permission.java
 *
 * Created on 13. Februar 2004, 17:55
 */

package tum.umlsec.viki.tools.permission.common;

/**
 *
 * @author  Markus
 */
public class ClassPermission {
    
    private String owner;
    private String permission;
    
    /** Creates a new instance of Permission */
    public ClassPermission(String  _owner, String _permission) 
    {
        owner      = _owner;
        permission = _permission;
    }    
    
    
    public String getOwner()
    {
        return owner;
    }

    
    public String getPermission()
    {
        return permission;
    }
}

