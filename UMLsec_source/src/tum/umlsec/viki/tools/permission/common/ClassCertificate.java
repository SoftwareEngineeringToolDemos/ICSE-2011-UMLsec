/*
 * ClassCertificate.java
 *
 * Created on 5. Mai 2004, 15:07
 */

package tum.umlsec.viki.tools.permission.common;

/**
 *
 * @author  Markus
 */
public class ClassCertificate {
    
    private String signer;
    private String delegat;
    private String permissionO;
    private String permission;
    private String delegationClass;
    private String validity;
    private String sequence;
                            
    
    /** Creates a new instance of ClassCertificate */
    public ClassCertificate(String _signer, 
                            String _delegat,
                            String _permissionO,
                            String _permission,
                            String _delegationClass,
                            String _validity,
                            String _sequence
                            ) 
   {
        signer            = _signer;
        delegat           = _delegat;
        permissionO       = _permissionO;
        permission        = _permission;
        delegationClass   = _delegationClass;
        validity          = _validity;
        sequence          = _sequence;
    }
    
   public String prologCertificate()
   {
        StringBuffer prolog = new StringBuffer();
        prolog.append("cert(");
        prolog.append(signer.toLowerCase());
        prolog.append(", ");
        if (!delegat.equalsIgnoreCase("null"))
            prolog.append(delegat.toLowerCase());
        else
            prolog.append("null");
        prolog.append(",perm(");
        prolog.append(permissionO.toLowerCase());
        prolog.append(", ");
        prolog.append(permission.toLowerCase());
        prolog.append("), ");
        if (!delegationClass.equalsIgnoreCase("null"))
            prolog.append(delegationClass.toLowerCase());
        else
            prolog.append("null");
        prolog.append(", ");
        prolog.append(validity.toLowerCase());
        prolog.append(", ");
        prolog.append(sequence.toLowerCase());
        prolog.append(")");
        
        return prolog.toString();
   }
}
