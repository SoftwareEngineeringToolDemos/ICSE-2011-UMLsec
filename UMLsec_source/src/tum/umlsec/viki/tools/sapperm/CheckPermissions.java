/*
 * CheckPermissions.java
 *
 * Created on 22. November 2004, 19:04
 */

package tum.umlsec.viki.tools.sapperm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import tum.umlsec.viki.tools.sapperm.mdrparser.MDRActivity2;
import tum.umlsec.viki.tools.sapperm.mdrparser.ModelPermission;
import tum.umlsec.viki.tools.sapperm.xmlparser.AuthorizationField;
import tum.umlsec.viki.tools.sapperm.xmlparser.AuthorizationObject;
import tum.umlsec.viki.tools.sapperm.xmlparser.Permission;
import tum.umlsec.viki.tools.sapperm.xmlparser.User;
/**
 *
 * @author  Milen
 */
public class CheckPermissions {
    
    public static final String STER_1 = "critical";
    public static final String STER_2 = "Separation of Duty";
    
    /** Creates a new instance of CheckPermissions */
    public CheckPermissions() {
    }
    public void run(){
        if(ToolSAPPerm.mdrparser.getAllActivities().isEmpty()){
            ToolSAPPerm.xmldomparser.log.writeLn("Model does not contain activities!");
            return;
        }
        //cycle through the activities of the model and search for stereotypes
        for(Iterator it = ToolSAPPerm.mdrparser.getAllActivities().iterator();it.hasNext();){
            
            MDRActivity2 act = (MDRActivity2)it.next();
            
            if(act.getStereotype() == null){
                continue;
            }
            else if(act.getStereotype() != null){
                if(act.getStereotype().name.equals(STER_1)){
                    //stereotype "critical", make necessary checks
                    check1(act);
                }
                if(act.getStereotype().name.equals(STER_2)){
                    //stereotype "critical", make necessary checks
                    check2(act);
                }
            }
        }
        
    }
    
    private void check1(MDRActivity2 ac){
        printActivityName(ac.getName(), STER_1);
        
        //Get all users in SAP, that have right to start "ac.trans_id".
        //For that every users must be found, that have for S_TCODE
        //in sapber.xml a value of "ac.trans_id".
        Set users = getTransactionUsers(ac.getID());
        
        //iterate through the users; check if there are other users
        //than those defined in the model; compare the permissions for the activity
        //from the model with their actual permissions
        for(Iterator it = users.iterator();it.hasNext();){
            ModelPermission mp = ToolSAPPerm.xmldomparser.getModelPermission(ac.getID());
            if(mp == null){
                ToolSAPPerm.xmldomparser.log.writeLn(
                "Model permissions are not defined for activity: " + ac.getName());
                return;
            }
            
            User u =(User)it.next();
            printUserName(u.name);
            
            boolean allowed = false;
            if( ac.getUsers().contains(u.name) ){
                allowed = true;
            }
            else{
                ToolSAPPerm.xmldomparser.log.writeLn("!!! START TRANSACTION WARNING: "
                + "User " + u.name + " can start " + "\"" + ac.getName() + "\"");
            }
            comparePermissions( u, ac, mp, allowed );
        }
        
        
    }
    
    private void check2(MDRActivity2 ac1){
        printActivityName(ac1.getName(), STER_2);
        
        String actName = ac1.getStereotype().getValue("activity");
        Vector users1, users2;
        
        users1 = new Vector();
        users2 = new Vector();
        
        MDRActivity2 ac2 = null;
        
        //cycle through the activities of the model and search for
        // activity with name actName
        for(Iterator it = ToolSAPPerm.mdrparser.getAllActivities().iterator();it.hasNext();){
            
            ac2 = (MDRActivity2)it.next();
            if( ac2.getName().equals(actName) ){
                users2 = ac2.getUsers();
                break;
            }
        }
        
        if (users2.isEmpty()){
            ToolSAPPerm.xmldomparser.log.writeLn("No users defined in model for Activity \""
            + ac2.getName() + "\"!");
            return;
        }
        
        users1 = ac1.getUsers();
        if (users1.isEmpty()){
            ToolSAPPerm.xmldomparser.log.writeLn("No users defined in model for Activity \""
            + ac1.getName() + "\"!");
            return;
        }
        
        //Iterate through users1 (from activity with stereotype "Separation of Duty").
        //If there is a user, who exists also in users2 -> there is inconsistency in
        //the model
        for(Iterator it1 = users1.iterator(); it1.hasNext();){
            String u = (String)it1.next();
            
            if(users2.contains(u)){
                ToolSAPPerm.xmldomparser.log.writeLn("!!!INCONSISTENCY IN MODEL: User "
                + u + " cannot be defined in both activities \"" + ac1.getName()
                +"\" and \"" + ac2.getName() + "\"");
            }
            
            //check also if user u has the necessary authorization to start ac1
            if( !isUserInSet(u, getTransactionUsers(ac1.getID())) ){
                
                ToolSAPPerm.xmldomparser.log.writeLn("!!!SEPARATION OF DUTY WARNING: User "
                + u + " has not the necessary authorization to start \"" + ac1.getName()
                +"\" ");
            }
        }
        
        //Check is someone in users2 can start ac1. If yes, issue a warning,
        //because this is unallowed
        for(Iterator it2 = users2.iterator(); it2.hasNext();){
            String u = (String)it2.next();
            
            if( isUserInSet(u, getTransactionUsers(ac1.getID())) ){
                
                ToolSAPPerm.xmldomparser.log.writeLn("!!!Separation of Duty Warning: User "
                + u + " is allowed to start \"" + ac1.getName()
                +"\" ");
                
            }
        }
        
    }
    
    private boolean isUserInSet(String userName, Set userSet){
        for(Iterator it = userSet.iterator(); it.hasNext();){
            User us = (User)it.next();
            
            if( us.name.equals(userName) ){
                return true;
            }
        }
        
        return false;
    }
    
    //get all users, which can start cpecified transaction
    private Set getTransactionUsers(String transId){
        Set us = new HashSet();
        
        //iterate through all SAP permssions searching for authorization object
        //"S_TCODE" with value of "transID" for authorization field "TCD"
        for(Iterator it = ToolSAPPerm.xmldomparser.getPermissions().iterator();it.hasNext();){
            Permission per = (Permission)it.next();
            
            if( per.lookForPermission("S_TCODE", "TCD", transId) ){
                //get the users, who has that permission
                us.addAll(getUsersWithPermission(per));
            }
        }
        
        return us;
    }
    
    private Vector getUsersWithPermission(Permission p){
        Vector res = null;
        
        //iterate through the roles of p and find
        //which users have these roles
        for(Iterator it = p.getRoles().iterator();it.hasNext();){
            String roleId = (String)it.next();
            
            for(Iterator it1 = ToolSAPPerm.xmldomparser.getUsers().iterator();
            it1.hasNext();){
                User u = (User)it1.next();
                if( u.getRoles().contains(roleId) ){
                    
                    if(res == null)
                        res = new Vector();
                    
                    res.add(u);
                }
            }
        }
        
        return res;
    }
    
    //compares model permissions with actual permissions
    //for a specified user
    public void comparePermissions(User u, MDRActivity2 act, ModelPermission p, boolean a){
        
        //iterate through the authorization objects of the
        //permission for the activity ac
        for(Iterator it = p.getAuthObjects().iterator(); it.hasNext();){
            AuthorizationObject ao = (AuthorizationObject)it.next();
            printAuthObjectName(ao.name);
            
            //iterate through the fields of the object
            for(Iterator it1 = ao.getAuthFields().iterator(); it1.hasNext();){
                AuthorizationField af = (AuthorizationField)it1.next();
                
                //get the values(mapped to the permissions from which they are taken)
                //for that field for that auth.object for the given user
                Map fieldsPermsMappings = getValuesFromSap(u, ao, af);
                Vector vals = new Vector(fieldsPermsMappings.keySet());
                
                //check if the model values are contained in the actual values
                if( vals.containsAll(af.getValues()) ){
                    //if unallowed user contains all values for field in
                    //auth.aobject -> Critical Warning
                    if(a == false){
                        ToolSAPPerm.xmldomparser.log.writeLn("!!! CRITICAL WARNING: "
                        + "Unautharized user " + u.name + " has unallowed set of values: "
                        + "\"" + af.getValues().toString() + "\""+ " for field "
                        + "\"" + af.name + "\"");
                        
                        continue;
                    }
/*                    else if(a == true){
                        //if allowed user has more actual values than
                        //defined in model -> Critical Warning
                        if( vals.size() > af.getValues().size() ){
                            ToolSAPPerm.xmldomparser.log.writeLn("!!! CRITICAL WARNING: "
                            + "User " + u.name + " has a biger range of values: "
                            + "\"" + vals.toString() + "\""
                            + " for field " + "\"" + af.name + "\""
                            + " than allowed: " + "\"" + af.getValues().toString() + "\"");
                            
                            continue;
                        }
                    }*/                    
                    
                }
                
                //compare values of a field from the model one ba one
                compareValues(u, a, af, fieldsPermsMappings);
            }
        }
    }
    
    private void compareValues(User user, boolean allowedUser, AuthorizationField f, Map map){
        
        Vector missingValues = new Vector();
        Vector additionalValues = new Vector();
        Map hm = new HashMap();
        Vector hashMaps = new Vector();
        String previousValue = "";
        Vector vals = new Vector(map.keySet());
       
        //Iterate through the values of the field (from model) and compare
        //them with the values from the user permissions.
        //Check also if velues don't come from diffrent permissions
        for(Iterator it2 = f.getValues().iterator(); it2.hasNext();){
            
            String value = (String)it2.next();
            if( !vals.contains(value) ){
                //missing authorization
                missingValues.add(value);
                continue;
            }
            
            if(previousValue == ""){
                //hm consists of field values as keys and only one
                //permission as value
                hm.put(value, map.get(value));
            }
            else if(previousValue != ""){
                //compare if the current value has different
                //permission as the previous
                if(map.get(value) != map.get(previousValue)){
                    
                    hashMaps.add(hm);
                    hm = new HashMap();
                    hm.put(value, map.get(value));
                }
                else{
                    hm.put(value, map.get(value));
                }
            }
            
            previousValue = value;
            
        }
        
        hashMaps.add(hm);
        //if haschMaps contains more than one HashMap, it means
        // we have kombination of values from diffrent permissions
        if(hashMaps.size() > 1){
            ToolSAPPerm.xmldomparser.log.writeLn("!!! COMBINATION OF VALUES WARNING: "
            + "User " + user.name + " has authorization for field \"" + f.name + "\""
            +  " from different permissions: " );
            for(Iterator it3 = hashMaps.iterator(); it3.hasNext();){
                HashMap m = (HashMap)it3.next();

                ToolSAPPerm.xmldomparser.log.writeLn("    values: " + m.keySet().toString()
                + " from permission: " + getPermName(m) + ";");
                
            }
            
        }
        
        for(Iterator it4 = vals.iterator(); it4.hasNext();){
            
            String value = (String)it4.next();
            if( !f.getValues().contains(value) ){
                //more authorization
                additionalValues.add(value);
            }
            
        }
        
        if(allowedUser == true){
            if( !missingValues.isEmpty() ){
                //issue a missng values warning
                ToolSAPPerm.xmldomparser.log.writeLn("!!! MISSING VALUES WARNING: "
                + "User " + user.name + " has a missing values " + "\"" + missingValues.toString()
                + "\"" + " for field " + "\"" + f.name + "\"");
            }
            if( !additionalValues.isEmpty() ){
                //issue a missng values warning
                ToolSAPPerm.xmldomparser.log.writeLn("!!! ADDITIONAL VALUES WARNING: "
                + "User " + user.name + " has additional values " + "\"" + additionalValues.toString()
                + "\"" + " for field " + "\"" + f.name + "\"");
            }
        }
        
    }
    
    private String getPermName(HashMap hm){
        Iterator it = hm.values().iterator();
        Permission p = (Permission)it.next();
        return p.perm_id;
    }
    
    private HashMap getValuesFromSap(User us,AuthorizationObject ao, AuthorizationField af){
        
        //        Vector users = ToolSAPPerm.xmldomparser.getUsers();
        //        User user = (User)users.elementAt(users.indexOf(us));
        
        //Values for field can come from different permissions.
        //They must be put in set, because two permissions can have
        //the same values for the same field, but we need that
        //values only one time
        //        Set fieldValues = new HashSet();
        HashMap fieldPerms = new HashMap();
        
        for(Iterator it = us.getRoles().iterator(); it.hasNext();){
            String role_id = (String)it.next();
            
            //iterate through the permissions to find role r
            for(Iterator it1 = ToolSAPPerm.xmldomparser.getPermissions().iterator();
            it1.hasNext();){
                Permission p = (Permission)it1.next();
                if( p.getRoles().contains(role_id) ){
                    Vector v = p.getFieldValues(ao, af);
                    
/*                    if(v != null){
                        fieldValues.addAll(v);
                    }*/
                    for(Iterator it2 = v.iterator(); it2.hasNext();){
                        fieldPerms.put(it2.next(), p);
                    }
                }
            }
            
        }
        
        return fieldPerms;
    }
    
    public void printActivityName(String name, String sterName){
        ToolSAPPerm.xmldomparser.log.writeLn("************************");
        ToolSAPPerm.xmldomparser.log.writeLn("************************");
        ToolSAPPerm.xmldomparser.log.writeLn("---- ACTIVITY: " + name
        + "; STEREOTYPE: " + sterName);
        ToolSAPPerm.xmldomparser.log.writeLn("--------");
    }
    
    private void printAuthObjectName(String name){
        ToolSAPPerm.xmldomparser.log.writeLn("--------------------- Authorization Object: " + name);
    }
    
    private void printUserName(String name){
        ToolSAPPerm.xmldomparser.log.writeLn("-------- USER: " + name);
    }
}
