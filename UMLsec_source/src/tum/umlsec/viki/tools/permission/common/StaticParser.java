/*
 * StaticParser.java
 *
 * Created on 23. April 2004, 10:44
 */

package tum.umlsec.viki.tools.permission.common;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 *
 * @author  Markus
 */
public class StaticParser {
    
    /** Creates a new instance of StaticParser */
    public StaticParser() 
    {
                
    }
   
    
    /**
     * creates a new Permission object out of a tag for a permission
     */    
    public static ClassPermission parsePermission(String tag) 
        throws StaticParserException
    {
        
        Pattern p = Pattern.compile("(\\w{1,})\\s{0,},\\s{0,}(\\w{1,})");
        Matcher m = p.matcher(tag);
                
        if (m.find())
        {
            return new ClassPermission(m.group(1), m.group(2));
        }
        else
        {
            throw new StaticParserException();
        }
    }
    
    /**
     * creates a new Delegation object out of a tag "delegation"
     */        
    public static ClassDelegation parseDelegation(String tag) 
        throws StaticParserException
    {
        
        Pattern p = Pattern.compile("(\\w{1,})\\s{0,},\\s{0,}(\\w{1,})"+
                                    "\\s{0,},\\s{0,}\\[(.{1,})\\]\\s{0,}");
        Matcher m = p.matcher(tag);
        
        String match1 = null;
        String match2 = null;
        String match3 = null;
        
        if (m.find()){
            match1 = m.group(1);
            match2 = m.group(2);
            match3 = m.group(3);
            ClassDelegation deleg = new ClassDelegation(m.group(1), m.group(2));
    
            while (!match3.equals(""))
            {
                Pattern pattern = Pattern.compile("(\\w{1,})\\s{0,},{0,1}\\s{0,}(.{0,})");
                Matcher matcher = pattern.matcher(match3);
                match3 = "";
                if (matcher.find())
                {
                    match1 = matcher.group(1);
                    deleg.addRole(match1);
                    match3 = matcher.group(2);
                }
            }
            return deleg;
        }
        else
        {
            throw new StaticParserException();
        }
    }
    

    /**
     * creates a new Certificat object out of a tag "certification"
     */        
    public static ClassCertificate parseCertificate(String tag) 
        throws StaticParserException
    {
        
        Pattern p = Pattern.compile("(\\w{1,})" +
                                    "\\s{0,},{0,1}\\s{0,}(\\w{1,})" +
                                    "\\s{0,},{0,1}\\s{0,}(\\w{1,})" +
                                    "\\s{0,},{0,1}\\s{0,}(\\w{1,})" +
                                    "\\s{0,},{0,1}\\s{0,}(\\w{1,})" +
                                    "\\s{0,},{0,1}\\s{0,}([-]{0,1}\\w{1,})" + 
                                    "\\s{0,},{0,1}\\s{0,}([-]{0,1}\\w{1,})");
        Matcher m = p.matcher(tag);
        String match1 = null;
        String match2 = null;
        String match3 = null;
        
        if (m.find()){
            return new ClassCertificate(m.group(1),
                                        m.group(2),
                                        m.group(3),
                                        m.group(4),
                                        m.group(5), 
                                        m.group(6), 
                                        m.group(7));
        }
            
        else
        {
            throw new StaticParserException();
        }
    }
}