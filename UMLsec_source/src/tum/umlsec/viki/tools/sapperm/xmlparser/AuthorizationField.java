/*
 * AuthorizationField.java
 *
 * Created on 15. November 2004, 00:07
 */

package tum.umlsec.viki.tools.sapperm.xmlparser;

import java.util.StringTokenizer;
import java.util.Vector;

/**
 *
 * @author  Milen
 */
public class AuthorizationField {
    
    public String name, field_id;
    private Vector values;
    
    /** Creates a new instance of AuthorizationField */
    public AuthorizationField(String name, String id, String vals) {
        this.name = name;
        field_id = id;
        values = new Vector();
        setValues(vals);
    }

    public Vector getValues(){
        return values;
    }
    
    private void setValues(String str){
        StringTokenizer st = new StringTokenizer(str, ",");
        while (st.hasMoreTokens()) {
            values.add(st.nextElement());
        }
        
    }
}
