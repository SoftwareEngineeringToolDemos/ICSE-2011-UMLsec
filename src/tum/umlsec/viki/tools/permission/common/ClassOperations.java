/*
 * ClassDelegation.java
 *
 * Created on 23. April 2004, 16:38
 */

package tum.umlsec.viki.tools.permission.common;

import java.util.HashSet;
import java.util.Iterator;
/**
 *
 * @author  Markus
 */
public class ClassOperations {
    
    private String operation;
    private HashSet permissions = new HashSet();
    
/** Creates a new instance of ClassDelegation */

    public ClassOperations(String _operation) 
    {
        operation = _operation;
    }    
    
    
    public void addPermission(String _permission)
    {
        this.permissions.add(_permission);    
    }
    

    public boolean needsPermission(String _permission)
    {
        return this.permissions.contains(_permission);
    }

    public boolean allPermissionsFound(HashSet _permissions)
    {
        int countPerm = this.permissions.size();
        
        for(Iterator iterPerm = _permissions.iterator(); iterPerm.hasNext();)
        {
            if (this.permissions.contains((String)iterPerm.next())) countPerm--;                     
        }

        return (countPerm == 0 );
    }
    
    public String getOperation()
    {
        return operation;
    }
}
