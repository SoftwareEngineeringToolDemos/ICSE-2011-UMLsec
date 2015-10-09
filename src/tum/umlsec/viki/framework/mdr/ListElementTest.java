package tum.umlsec.viki.framework.mdr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.NoSuchElementException;

import org.junit.Test;

public class ListElementTest {
	
	public void setUp() {
	}

	@Test
	public void testListElement() {
		ListElement elt = new ListElement("argoID", "name", "type");
		assertEquals("argoID", elt.getArgoID());
		assertEquals("name", elt.getName());
		assertEquals("type", elt.getType());
		assertNull(elt.getToolID());
		assertTrue(elt.hasNoParent());
	}
	
	@Test
	public void testSetName() {
		ListElement elt = new ListElement("id", "name", "Stereotype");
		elt.setName("newName");
		assertEquals("newName", elt.getName());
	}

	@Test
	public void testSetToolID() {
		ListElement elt = new ListElement("id", "name", "Stereotype");
		elt.setToolID("toolID");
		assertEquals("toolID", elt.getToolID());
	}

	@Test
	public void testAddParent() throws NoDuplicateException {
		ListElement elt = new ListElement("id", "name", "Stereotype");
		elt.addParent("parent3");
		assertTrue(elt.hasParent("parent3"));
		assertFalse(elt.hasParent("parent5"));
	}
	
	@Test
	public void testAddMultipleParents() throws NoDuplicateException {
		ListElement elt = new ListElement("id", "name", "Stereotype");
		elt.addParent("parent3");
		elt.addParent("parent4");
		elt.addParent("parent1");
		elt.addParent("parent0");
		assertTrue(elt.hasParent("parent3"));
		assertTrue(elt.hasParent("parent4"));
		assertTrue(elt.hasParent("parent1"));
		assertTrue(elt.hasParent("parent0"));
		assertFalse(elt.hasParent("parent5"));
	}
	
	@Test
	public void testAddExistingParent() {
		ListElement elt = new ListElement("id", "name", "type");
		try {
			elt.addParent("parent3");
			elt.addParent("parent4");
		} catch(NoDuplicateException ex) {
			assertTrue(false);
		}
		try {
			elt.addParent("parent3");
		} catch(NoDuplicateException ex2) {
			assertTrue(true);
			return;
		}
		assertTrue(false);
	}
	
	@Test
	public void testRemoveParent() {
		ListElement elt = new ListElement("id", "name", "type");
		try {
			elt.addParent("parent3");
			elt.addParent("parent4");
			elt.addParent("parent5");
		} catch (NoDuplicateException e) {
			assertTrue(false);
		}
		assertTrue(elt.hasParent("parent3"));
		assertTrue(elt.hasParent("parent4"));
		assertTrue(elt.hasParent("parent5"));
		assertFalse(elt.hasNoParent());
		
		elt.removeParent("parent3");
		assertFalse(elt.hasParent("parent3"));
		elt.removeParent("parent4");
		assertFalse(elt.hasParent("parent4"));
		elt.removeParent("parent5");
		assertFalse(elt.hasParent("parent5"));
		assertTrue(elt.hasNoParent());	
	}
	
	@Test
	public void testRemoveNonExistingParent() {
		ListElement elt = new ListElement("id", "name", "type");
		try {
			elt.removeParent("parent0");
		} catch(NoSuchElementException ex) {
			assertTrue(true);
			try {
				elt.addParent("parent3");
				elt.addParent("parent4");
			} catch (NoDuplicateException ex2) {
				assertTrue(false);
			}
			try {
				elt.removeParent("parent7");
			} catch (NoSuchElementException ex3) {
				assertTrue(true);
				return;
			}
		}
		assertTrue(false);
	}

	@Test
	public void testSetType() {
		ListElement elt = new ListElement("id", "name", "Stereotype");
		elt.setType("Link");
		assertEquals("Link", elt.getType());
	}

}
