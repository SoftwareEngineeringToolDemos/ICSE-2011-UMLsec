package tum.umlsec.viki.tools.sapperm.xmlparser;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.tools.sapperm.mdrparser.ModelPermission;

import com.sun.xml.tree.XmlDocument;

public class XMLDOMParser2
{
	private Document document;
	private Vector users, roles, permissions, modelPerms;
	public ITextOutput log;
	
//	Vector allObjectIDs = new Vector();
	
	Vector _mdrUser = new Vector();
        
        private boolean isModelPerm = false;
	
	public Document getDocument()
	{
		return document;
	}
	
	public XMLDOMParser2(ITextOutput output)
	{
		log = output;
	}
	
	public Vector getUsers()
	{
		return users;
	}	
	

	/**
	 * Diese Methode erzeugt ein XML-Dokument, das nur
	 * die Versionsinformationen und ein Tag <information> enthaelt.
	 *
	 */
	
	public void loadXMLFile(String filename)
	{
		// Modus setzen:
		
		FileInputStream inStream;
		String xmlDocumentPath = "file:" + new File(filename).getAbsolutePath();
		try
		{
			document = XmlDocument.createXmlDocument(xmlDocumentPath);
		}
		catch(Exception e)
		{
                    log.writeLn("Wrong XML file");
                    System.out.println("Fehler beim Parsen der XML-Datei: " + e);
		}

	}
		
	public void parse()
	{
                users = new Vector();
                roles = new Vector();
                permissions = new Vector();
                
                parseUsers();
                parseRoles();
                parsePermissions();
                
	}


        private void parseUsers(){
            NodeList nl = document.getElementsByTagName("user");
            for(int i = 0; i < nl.getLength(); i++){
                Node n = nl.item(i);
                parseUserNode(n);
            }
        }
        
        private void parseUserNode(Node p){
            NodeList nodes = p.getChildNodes();
            String name = "";
            Vector roles = new Vector();
            
            for(int i=0; i<nodes.getLength(); i++){
                Node n = nodes.item(i);
                if (n == null) {
                    continue;
                }
                //System.out.println(n.getNodeName());
                if (n.getNodeName().equals("name")) {
                    name = n.getFirstChild().getNodeValue();
                }
                
                if (n.getNodeName().equals("role_id")) {
                    roles.add(n.getFirstChild().getNodeValue());
                }
            }
            
            users.add(new User(name, roles));
        }
        
        private void parseRoles(){
            NodeList nl = document.getElementsByTagName("role");
            for(int i = 0; i < nl.getLength(); i++){
                Node n = nl.item(i);
                parseRoleNode(n);
            }
            
        }
        
        private void parseRoleNode(Node p){
            NodeList nodes = p.getChildNodes();
            String name = "";
            String id = "";
            
            for(int i=0; i<nodes.getLength(); i++){
                Node n = nodes.item(i);
                if (n == null) {
                    continue;
                }
                //System.out.println(n.getNodeName());
                if (n.getNodeName().equals("name")) {
                    name = n.getFirstChild().getNodeValue();
                }
                
                if (n.getNodeName().equals("role_id")) {
                    id = n.getFirstChild().getNodeValue();
                }
            }
            
            roles.add(new Role(name, id));
        }
            
         private void parsePermissions(){
            NodeList nl = document.getElementsByTagName("permission");
            for(int i = 0; i < nl.getLength(); i++){
                Node n = nl.item(i);
                parsePermissionNode(n);
            }
       }
         
        private void parsePermissionNode(Node p){
            NodeList nodes = p.getChildNodes();
            String idPerm = "";
            String transId = "";
            Vector perRoles = new Vector();
            Vector authObjects = new Vector();
            
            for(int i=0; i<nodes.getLength(); i++){
                Node n = nodes.item(i);
                if (n == null) {
                    continue;
                }
                
               if(isModelPerm == false){                    
                    if (n.getNodeName().equals("perm_id")) {
                        idPerm = n.getFirstChild().getNodeValue();
                        continue;
                    }
                    
                    if (n.getNodeName().equals("role_id")) {
                        perRoles.add(n.getFirstChild().getNodeValue());
                        continue;
                    }
                    
                    if (n.getNodeName().equals("authObj")) {
                        authObjects.add(parseAuthObjNode(n));
                        continue;
                    }
                }
                
                else if(isModelPerm == true){
                    if (n.getNodeName().equals("trans_id")) {
                        transId = n.getFirstChild().getNodeValue();
                        continue;
                    }
                    
                    if (n.getNodeName().equals("authObj")) {
                        authObjects.add(parseAuthObjNode(n));
                        continue;
                    }
                    
                }
                
            }
            
            if(isModelPerm == false){
                permissions.add(new Permission(idPerm, perRoles, authObjects));
            }            
            else if(isModelPerm == true){
                modelPerms.add(new ModelPermission(transId, authObjects));
            }
                
        }

       private AuthorizationObject parseAuthObjNode(Node p){
            NodeList nodes = p.getChildNodes();
            String name = "";
            String id = "";
            Vector authFields = new Vector();
            
            for(int i=0; i<nodes.getLength(); i++){
                Node n = nodes.item(i);
                if (n == null) {
                    continue;
                }
                //System.out.println(n.getNodeName());
                if (n.getNodeName().equals("name")) {
                    name = n.getFirstChild().getNodeValue();
                    continue;
                }
                
                if (n.getNodeName().equals("obj_id")) {
                    id = n.getFirstChild().getNodeValue();
                    continue;
                }
                
                if (n.getNodeName().equals("authField")) {
                    authFields.add(parseAuthFieldNode(n));
                    continue;
                }
            }
            
            return new AuthorizationObject(name, id, authFields);
        }
        
      private AuthorizationField parseAuthFieldNode(Node p){
            NodeList nodes = p.getChildNodes();
            String name = "";
            String id = "";
            String values = "";
            
            for(int i=0; i<nodes.getLength(); i++){
                Node n = nodes.item(i);
                if (n == null) {
                    continue;
                }
                //System.out.println(n.getNodeName());
                if (n.getNodeName().equals("name")) {
                    name = n.getFirstChild().getNodeValue();
                    continue;
                }
                
                if (n.getNodeName().equals("field_id")) {
                    id = n.getFirstChild().getNodeValue();
                    continue;
                }
                
                if (n.getNodeName().equals("value")) {
                    values = n.getFirstChild().getNodeValue();
                    continue;
                }
            }
            
            return new AuthorizationField(name, id, values);
        }
        
	
        public void parseModelPerm() {
            isModelPerm = true;
            modelPerms = new Vector();
            parsePermissions();
            isModelPerm = false;
        }
        
        public Vector getPermissions() {
            return permissions;
        }
        
        public Vector getRoles(){
            return roles;
        }
        
        public ModelPermission getModelPermission(String transId) {
            try{
                for(Iterator it = modelPerms.iterator(); it.hasNext();){
                    ModelPermission mp = (ModelPermission)it.next();
                    
                    if(mp.trans_id.equals(transId)){
                        return mp;
                    }
                }
            }
            catch(Exception e){
                log.writeLn(e.toString());
            }
            
            return null;
        }
        
}