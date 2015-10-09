package tum.umlsec.viki.framework.mdr;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;

/**
 * A ListElement is an element of the IdNameList.
 * It contains the element's (unique) argoID, which is the xmi.id used by ArgoUML, a name, 
 * an optional toolID that describe the UMLsec tool xmi.id, a type, and a list of parents that can be empty. 
 * Parents are other ListElement objects and are identified by their ArgoID.
 * 
 * @author Lionel Montrieux <lmontrie@info.fundp.ac.be>
 *
 */
public class ListElement {
	private String argoID;
	private String name;
	private String toolID;
	private String type;
	private List<String> parents;
	private boolean used;
	
	private static Logger logger = Logger.getLogger("ListElement");
	
	/**
	 * Creates a new ListElement, with no toolID and an empty list of parents
	 * @param argoID its argoID
	 * @param name its name
	 * @param type its type
	 */
	protected ListElement(String argoID, String name, String type) {
		this.argoID = argoID;
		this.name = name;
		this.type = type;
		parents = new ArrayList<String>();
		toolID = null;
		used = false;
		logger.trace("New ListElement. Name : " + name + " ArgoID : " + argoID + " type : " + type);
	}
	
	protected String getArgoID() {
		return argoID;
	}
	
	protected String getName() {
		return name;
	}
	
	protected String getToolID() {
		return toolID;
	}
	
	protected String getType() {
		return type;
	}
	
	protected void setArgoID(String argoID) {
		this.argoID = argoID;
	}
	
	protected void setName(String name) {
		this.name = name;
	}
	
	protected void setToolID(String toolID) {
		this.toolID = toolID;
	}
	
	/**
	 * Adds a parent to the list of parents. The list can't contain duplicate elements
	 * @param parent the id of the parent
	 * @throws NoDuplicateException if the provided parent id is already in the list
	 */
	protected void addParent(String parent) throws NoDuplicateException {
		if (parents.contains(parent))
			throw new NoDuplicateException("There's already a parent " + parent + " in the " + argoID+ " element's parent list");
		else {
			parents.add(parent);
			logger.trace("Adding parent " + parent + " to the element " + argoID + " : " + name);
		}
	}
	
	/**
	 * Removes a parent from the list of parents
	 * @param parent the id of the parent to remove
	 */
	protected void removeParent(String parent) {
		for (int i = 0; i < parents.size(); i++) {
			if (parents.get(i).equals(parent)) {
				parents.remove(i);
				logger.trace("Removing parent " + parent + " from element " + argoID + " : " + name);
				return;
			}
		}
		throw new NoSuchElementException("Can't delete parent " + parent + " : not found");
	}
	
	/**
	 * Tells whether an element is parent of the element
	 * @param parent the parent id
	 * @return true if the parent is in the element's parent list
	 */
	protected boolean hasParent(String parent) {
		for (int i = 0; i < parents.size(); i++) {
			if (parents.get(i).equals(parent))
				return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @return true if the element has no parents, false if it has one or more parents
	 */
	protected boolean hasNoParent() {
		if (parents.size() == 0)
			return true;
		else
			return false;
	}
	
	protected void setType(String type) {
		this.type = type;
	}
	
	public String toString() {
		String res = "Name : " + name + "\t ArgoID : " + argoID + "\t toolID : " + toolID + "\t type : " + type;
		for (int i = 0; i < parents.size(); i++)
			res += "\t parent : " + parents.get(i);
		return res;
	}
	
	protected List<String> getParents() {
		return parents;
	}
	
	protected boolean isUsed() {
		return used;
	}
	
	protected void setUsed(boolean value) {
		used = value;
	}
	
}
