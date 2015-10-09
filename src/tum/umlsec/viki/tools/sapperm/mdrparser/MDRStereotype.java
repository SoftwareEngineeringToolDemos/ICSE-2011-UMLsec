/*
 * MDRStereotype.java
 *
 * Created on 13. November 2004, 21:26
 */

package tum.umlsec.viki.tools.sapperm.mdrparser;

import java.util.HashMap;
/**
 *
 * @author  Milen
 */
public class MDRStereotype {
    
    public String name;
    private HashMap taggedValue;
    /** Creates a new instance of MDRStereotype */
    public MDRStereotype(String name, Object tag, Object value) {
        this.name = name;
        taggedValue = new HashMap();
        taggedValue.put(tag, value);
    }
    
    public String getValue(Object tag){
        return (String)taggedValue.get(tag);
    }
    
}
