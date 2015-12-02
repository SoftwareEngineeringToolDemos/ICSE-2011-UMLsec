package tum.umlsec.viki.framework.mdr;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;

public class IdNameList {
	private static IdNameList instance = new IdNameList();
	private List<ListElement> list = new ArrayList<ListElement>();
	private List<String[]> tempParentList = new ArrayList<String[]>();
	public static Logger logger = Logger.getLogger("IdNameList");
	private static int ARGOID = 0;
	private static int PARENTID = 1;
	
	private IdNameList() {
		// nothing
	}
	
	public static IdNameList getInstance() {
		return instance;
	}
	
	/**
	 * Adds an element to the list. There can't be two elements with the same ArgoID, nor two elements with the same name and type
	 * @param name the name of the element
	 * @param argoID the ArgoUML id (xmi.id) of the element
	 * @param type type of the element
	 * @param parent id of the parent element
	 * @throws NoDuplicateException if two elements are conflicting (same ArgoID, or same name and type)
	 */
	public void addElement(String name, String argoID, String type) throws NoDuplicateException {
		assert(name != null);
		assert(argoID != null);
		assert(type!=null);
		
		ListElement current = null;
		// First we make sure that there's no conflict with existing elements
		for (ListIterator<ListElement> iter = list.listIterator(); iter.hasNext(); ) {
			current = iter.next();
			if (current.getArgoID().equals(argoID))
				throw new NoDuplicateException("There's already an element with the ArgoID " + argoID + " on the list");
			if (current.getName().equals(name) && current.getType().equals(type) && !current.getType().equals("Stereotype"))
				throw new NoDuplicateException("There's already an element with the name " + name + " and type " + type);
		}
		// Then we create the element
		ListElement elt = new ListElement(argoID, name, type);
		
		// If there are parents waiting in tempParentList, we add then to the parents list
		String[] cur = null;
		for (int i = 0; i < tempParentList.size(); i++) {
			cur = tempParentList.get(i);
			if (cur[ARGOID].equals(argoID)) {
				elt.addParent(cur[PARENTID]);
				tempParentList.remove(cur);
				i--;
			}
		}
		
		// Finally we add it to the list
		list.add(elt);
	}

	/**
	 * Get the id of an element of the list from its name and type, and that doesn't have a parent. Throws a NoSuchElementException if the corresponding 
	 * element hasn't been found
	 * @param name the name of the element
	 * @param type the type of the element
	 * @return the id of the element
	 */
	public String getID(String name, String type) {
		ListElement current = null;
		for (ListIterator<ListElement> iter = list.listIterator(); iter.hasNext(); ) {
			current = iter.next();
			if (current.getName().equals(name) && current.getType().equals(type))
				return current.getArgoID();
		}
		throw new NoSuchElementException("There's no element with name " + name + " and type " + type + " on the list");
	}
	
	/**
	 * Get the id of an element of the list from its name, type and parent. Throws a NoSuchElementException if the corresponding 
	 * element hasn't been found
	 * @param name the name of the element
	 * @param type the type of the element
	 * @param parent the id of the parent element
	 * @return the id of the element
	 */
	public String getID(String name, String type, String parent) {
		ListElement current = null;
		for (ListIterator<ListElement> iter = list.listIterator(); iter.hasNext(); ) {
			current = iter.next();
			if (current.getName().equals(name) && current.getType().equals(type) && (current.hasParent(parent)))
				return current.getArgoID();
		}
		throw new NoSuchElementException("There's no element with name " + name + " and type " + type + " and parent " + parent + " on the list");
	}
	
	/**
	 * Changes the name of an element that has no parent. The element's type can *not* be 'Stereotype'.
	 * @param oldName the old name
	 * @param type the type of the element. Can't be a stereotype
	 * @param newName the new name
	 */
	public void changeName(String oldName, String type, String newName) throws NoDuplicateException {
		assert(!type.equals("Stereotype"));
		ListElement current = null;
		ListElement toChange = null;
		for (ListIterator<ListElement> iter = list.listIterator(); iter.hasNext(); ) {
			current = iter.next();
			if (current.getName().equals(oldName) && current.getType().equals(type) && current.hasNoParent())
				toChange = current;
			if (current.getName().equals(newName) && current.getType().equals(type) && current.hasNoParent())
				throw new NoDuplicateException("There's already an element with name " + newName + " and type " + type + " that doesn't have any parent on the list");
		}
		if (toChange != null) {
			toChange.setName(newName);
			logger.debug("ChangeName : from " + oldName + " to " + newName);
			logger.trace("ChangeName : Element " + toChange.toString());
		}
		else {
			throw new NoSuchElementException("IdNameSwitch : No element with name " + oldName + " and type " + type + " found");
		}
	}
	
	/**
	 * Changes the name of an element that has (at least one) parent(s)
	 * @param oldName the old name
	 * @param type the type of the element
	 * @param newName the new name
	 * @param parent a parent of the element
	 */
	public void changeName(String oldName, String type, String newName, String parent) throws NoDuplicateException {
		ListElement current;
		ListElement toChange = null;
		boolean found = false;
		for (ListIterator<ListElement> iter = list.listIterator(); (iter.hasNext()) && !found; ) {
			toChange = iter.next();
			if (toChange.getName().equals(oldName) && (toChange.hasParent(parent)) && toChange.getType().equals(type))
				found = true;
		}
		if (found) {
			for (ListIterator<ListElement> iter = list.listIterator(); iter.hasNext(); ) {
				current = iter.next();
				if (current.getName().equals(newName) && current.getType().equals(type) && sameParent(current, toChange))
					throw new NoDuplicateException("There's already an element with name " + newName + " and type " + type + " and at least one conflicting parent");
			}
			toChange.setName(newName);
			logger.debug("ChangeName : from " + oldName + " to " + newName);
			logger.trace("ChangeName : Element " + toChange.toString());
			return;
		}
		throw new NoSuchElementException("IdNameSwitch : No element with name " + oldName + " and parent " + parent + " found");
	}
	
	/**
	 * Checks if two elements have at least one parent in common
	 * @param elt1 the first element
	 * @param elt2 the second element
	 * @return true if there's at least one parent in common, false if there's no parent in common
	 */
	private boolean sameParent(ListElement elt1, ListElement elt2) {
		String parent1;
		String parent2;
		for(Iterator<String> it1 = elt1.getParents().iterator(); it1.hasNext(); ) {
			parent1 = it1.next();
			for (Iterator<String> it2 = elt2.getParents().iterator(); it2.hasNext(); ) {
				parent2 = it2.next();
				if (parent1.equals(parent2))
					return true;
			}
		}
		return false;
	}
	
	/**
	 * Gets the type of an element from its ArgoID
	 * @param argoID the argoID of the element
	 * @return its type
	 */
	public String getType(String argoID) {
		ListElement current = null;
		for (Iterator<ListElement> iter = list.iterator(); iter.hasNext(); ) {
			current = iter.next();
			if (current.getArgoID().equals(argoID))
				return current.getType();
		}
		throw new NoSuchElementException("There's no ListElement with argoID " + argoID + " in the list");
	}
	
	/**
	 * Gets an element from its argoID
	 * @param argoID the argoID
	 * @return the element that has the provided argoID
	 */
	public ListElement getElement(String argoID) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getArgoID().equals(argoID))
				return list.get(i);
		}
		throw new NoSuchElementException("Can't find element with argoID " + argoID);
	}
	
	/**
	 * Add UMLSecTool id to an element identified by his input id
	 * @param argoId the input id of the element
	 * @param toolId the id to add to the element
	 */
	public void addToolId(String argoId, String toolId) throws NoDuplicateException {
		ListElement match = null;
		boolean found = false;
		for (int i = 0; i < list.size(); i++) {
			if ((list.get(i).getToolID() != null) && (list.get(i).getToolID().equals("toolId")))
				throw new NoDuplicateException("There's already an element with toolID " + toolId + " on the list");
			if (list.get(i).getArgoID().equals(argoId) && (list.get(i).getToolID() == null) && !found) {
				match = list.get(i);
				found = true;
			}
		}
		if (found) {
			match.setToolID(toolId);
			logger.trace("Adding toolID " + toolId + " to element " + match.getName() + " : " + match.getArgoID());
			return;
		}
		logger.error("Couldn't find element " + argoId + " without toolID");
		throw new NoSuchElementException("Couldn't find element " + argoId);
	}
	
	/**
	 * Gets the input id of an element from its UMLSecTool id
	 * @param toolId the UMLSecTool id of the element
	 * @return the input id
	 * @throws NoSuchElementException thrown if there's no element with the specified UMLSecTool id
	 */
	public String getIdFromToolId(String toolId) throws NoSuchElementException {
		logger.trace("Looking for an element with toolID " + toolId);
		ListElement elt;
		System.out.println("List size : " + list.size());
		for (int i = 0; i < list.size(); i++) {
			elt = list.get(i);
			if (elt.getToolID() != null) {
				if (elt.getToolID().equals(toolId)) {
					return elt.getArgoID();
				}
			}
		}
		throw new NoSuchElementException();
	}
	
	/**
	 * Removes an element from the list
	 * @param argoID the argoid of the element to be removed
	 * @throws NoSuchElementException thrown if there's no element with this id in the list
	 */
	public void removeElement(String argoID) throws NoSuchElementException {
		logger.debug("Removing element " + argoID);
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getArgoID().equals(argoID)) {
				list.remove(i);
				return;
			}
		}
		throw new NoSuchElementException("There's no element with id " + argoID + " in the list.");
	}
	
	/**
	 * Gives the id of the element with the provided name
	 * @param name the name of the element
	 * @return the id of the element
	 * @throws NoSuchElementException thrown if there's no element with the provided name in the list
	 */
	public String getArgoId(String name, String type) throws NoSuchElementException {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getName().equals(name) && list.get(i).getType().equals(type)) {
				return list.get(i).getArgoID();
			}
		}
		throw new NoSuchElementException("There's no element with name " + name + " in the list.");
	}
	
	public String getUnusedArgoId(String name, String type) throws NoSuchElementException {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getName().equals(name) && list.get(i).getType().equals(type) && !list.get(i).isUsed()) {
				list.get(i).setUsed(true);
				return list.get(i).getArgoID();
			}
		}
		throw new NoSuchElementException("There's no unused element with name " + name + " in the list.");
	}
	
	public void addParent(String argoID, String parent) throws NoDuplicateException {
		ListElement current = null;
		for (int i = 0; i < list.size(); i++) {
			current = list.get(i);
			if (current.getArgoID().equals(argoID)) {
				current.addParent(parent);
				return;
			}
		}
		// We didn't find an element with the right ArgoID, so we store the parent into the temporary parents list.
		String[] elt = new String[2];
		elt[ARGOID] = argoID;
		elt[PARENTID] = parent;
		tempParentList.add(elt);
		logger.trace("New parent added to the temporary parent list : " + parent + " parent of " + argoID);
	}
	
	protected List<String[]> getTempParentList() {
		return tempParentList;
	}
	
	/**
	 * Removes all elements from the list.
	 */
	public void clear() {
		logger.debug("Clearing IdNameList");
		list.clear();
		tempParentList.clear();
	}
	
	public void setAllUnused() {
		for (int i = 0; i < list.size(); i++)
			list.get(i).setUsed(false);
		return;
	}
}
