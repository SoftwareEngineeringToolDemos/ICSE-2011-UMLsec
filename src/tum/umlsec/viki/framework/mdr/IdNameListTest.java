package tum.umlsec.viki.framework.mdr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.NoSuchElementException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class IdNameListTest {
	ListElement elt;

	@Before
	public void setUp() throws Exception {
		IdNameList.getInstance().clear();
		elt = new ListElement("argoID", "myName", "Link");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAddOneElement() {
		try {
			IdNameList.getInstance().addElement("myName", "argoID", "type");
		} catch (NoDuplicateException e) {
			System.out.println(e.getMessage());
			assertTrue(false);
		}
		assertEquals("myName", IdNameList.getInstance().getElement("argoID").getName());
		assertEquals("argoID", IdNameList.getInstance().getArgoId("myName", "type"));
		assertEquals("type", IdNameList.getInstance().getType("argoID"));
	}
	
	@Test
	public void testAddMultipleElements() {
		try {
			IdNameList.getInstance().addElement("name1", "argoID1", "type1");
			IdNameList.getInstance().addElement("name2", "argoID2", "type2");
		} catch (NoDuplicateException e) {
			assertTrue(false);
		}
		assertEquals("name1", IdNameList.getInstance().getElement("argoID1").getName());
		assertEquals("argoID1", IdNameList.getInstance().getArgoId("name1", "type1"));
		assertEquals("type1", IdNameList.getInstance().getType("argoID1"));

		assertEquals("name2", IdNameList.getInstance().getElement("argoID2").getName());
		assertEquals("argoID2", IdNameList.getInstance().getArgoId("name2", "type2"));
		assertEquals("type2", IdNameList.getInstance().getType("argoID2"));
	}
	
	@Test
	public void testAddElementsSameNameDifferentType() {
		try {
			IdNameList.getInstance().addElement("name", "argoID1", "type1");
			IdNameList.getInstance().addElement("name", "argoID2", "type2");
		} catch (NoDuplicateException e) {
			assertTrue(false);
		}
		assertEquals("name", IdNameList.getInstance().getElement("argoID1").getName());
		assertEquals("argoID1", IdNameList.getInstance().getArgoId("name", "type1"));
		assertEquals("type1", IdNameList.getInstance().getType("argoID1"));

		assertEquals("name", IdNameList.getInstance().getElement("argoID2").getName());
		assertEquals("argoID2", IdNameList.getInstance().getArgoId("name", "type2"));
		assertEquals("type2", IdNameList.getInstance().getType("argoID2"));
	}
	
	@Test
	public void testAddElementsSameNameStereotype() {
		try {
			IdNameList.getInstance().addElement("name", "argoID1", "Stereotype");
			IdNameList.getInstance().addElement("name", "argoID2", "Stereotype");
		} catch (NoDuplicateException ex) {
			assertTrue(false);
		}
		assertTrue(true);
	}
	
	@Test
	public void testAddElementsSameType() {
		try {
			IdNameList.getInstance().addElement("name1", "argoID1", "type");
			IdNameList.getInstance().addElement("name2", "argoID2", "type");
		} catch (NoDuplicateException ex) {
			assertTrue(false);
		}
		assertEquals("name1", IdNameList.getInstance().getElement("argoID1").getName());
		assertEquals("argoID1", IdNameList.getInstance().getArgoId("name1", "type"));
		assertEquals("type", IdNameList.getInstance().getType("argoID1"));

		assertEquals("name2", IdNameList.getInstance().getElement("argoID2").getName());
		assertEquals("argoID2", IdNameList.getInstance().getArgoId("name2", "type"));
		assertEquals("type", IdNameList.getInstance().getType("argoID2"));
	}
	
	@Test
	public void testAddConflictingElementsArgoID() {
		try {
			IdNameList.getInstance().addElement("name", "argoID", "type");
		} catch (NoDuplicateException ex) {
			assertTrue(false);
		}
		try {
			IdNameList.getInstance().addElement("name2", "argoID", "type2");
		} catch (NoDuplicateException ex) {
			assertTrue(true);
			return;
		}
		assertTrue(false);
	}
	
	@Test
	public void testAddConflictingElementsNameType() {
		try {
			IdNameList.getInstance().addElement("name", "argoID1", "type");
		} catch (NoDuplicateException ex) {
			assertTrue(false);
		}
		try {
			IdNameList.getInstance().addElement("name", "argoID2", "type");
		} catch (NoDuplicateException ex) {
			assertTrue(true);
			return;
		}
		assertTrue(false);
	}
	
	@Test
	public void addParentsToExistingElement() {
		try {
			IdNameList.getInstance().addElement("name", "argoID", "Stereotype");
		} catch (NoDuplicateException ex) {
			assertTrue(false);
		}
		try {
			IdNameList.getInstance().addParent("argoID", "parentID");
		} catch (NoDuplicateException ex) {
			assertTrue(false);
		}
		assertTrue(IdNameList.getInstance().getElement("argoID").hasParent("parentID"));
	}
	
	@Test
	public void addParentsToNonExistingElement() {
		try {
			IdNameList.getInstance().addParent("argoID", "parentID");
		} catch (NoDuplicateException ex) {
			assertTrue(false);
		}
		assertEquals("argoID", IdNameList.getInstance().getTempParentList().get(0)[0]);
		assertEquals("parentID", IdNameList.getInstance().getTempParentList().get(0)[1]);
	}
	
	@Test
	public void testAddElementWithParents() {
		try {
			IdNameList.getInstance().addParent("argoID", "argoP1");
			IdNameList.getInstance().addParent("argoID", "argoP2");
		} catch (NoDuplicateException e) {
			assertTrue(false);
		}
		try {
			IdNameList.getInstance().addElement("name", "argoID", "type");
		} catch (NoDuplicateException ex) {
			assertTrue(false);
		}
		assertTrue(IdNameList.getInstance().getElement("argoID").hasParent("argoP1"));
		assertTrue(IdNameList.getInstance().getElement("argoID").hasParent("argoP2"));
	}

	@Test
	public void testChangeNameStringStringString() {
		try {
			IdNameList.getInstance().addElement("name", "argoID", "type");
		} catch (NoDuplicateException e) {
			assertTrue(false);
		}
		try {
			IdNameList.getInstance().changeName("name", "type", "newName");
		} catch (NoDuplicateException ex) {
			assertTrue(false);
		}
		assertEquals("newName", IdNameList.getInstance().getElement("argoID").getName());
	}
	
	@Test
	public void testChangeNameStrStrStrNoConflict() {
		try {
			IdNameList.getInstance().addElement("name1", "argoID1", "type1");
			IdNameList.getInstance().addElement("name2", "argoID2", "type2");
		} catch (NoDuplicateException e) {
			assertTrue(false);
		}
		try {
			IdNameList.getInstance().changeName("name1", "type1", "name2");
		} catch (NoDuplicateException ex) {
			assertTrue(false);
		}
	}
	
	@Test
	public void testChangeNameStrStrStrConflict() {
		try {
			IdNameList.getInstance().addElement("name1", "argoID1", "type");
			IdNameList.getInstance().addElement("name2", "argoID2", "type");
		} catch (NoDuplicateException e) {
			assertTrue(false);
		}
		try {
			IdNameList.getInstance().changeName("name1", "type", "name2");
		} catch (NoDuplicateException ex) {
			assertTrue(true);
			return;
		}
		assertTrue(false);
	}
	
	@Test
	public void testChangeNameStringStringStringString() {
		try {
			IdNameList.getInstance().addElement("name", "argoID", "Stereotype");
			IdNameList.getInstance().addParent("argoID", "parentID");
		} catch (NoDuplicateException e) {
			assertTrue(false);
		}
		try {
			IdNameList.getInstance().changeName("name", "Stereotype", "newName", "parentID");
		} catch (NoDuplicateException ex) {
			assertTrue(false);
		}
		assertEquals("newName", IdNameList.getInstance().getElement("argoID").getName());
	}
	
	@Test
	public void testChangeNameStrStrStrStrSameNameNoConflict() {
		try {
			IdNameList.getInstance().addElement("name1", "argoID1", "Stereotype");
			IdNameList.getInstance().addParent("argoID1", "parentID1");
			IdNameList.getInstance().addElement("name2", "argoID2", "Stereotype");
			IdNameList.getInstance().addParent("argoID2", "parentID2");
		} catch (NoDuplicateException e) {
			assertTrue(false);
		}
		try {
			IdNameList.getInstance().changeName("name1", "Stereotype", "name2", "parentID1");
		} catch (NoDuplicateException ex) {
			assertTrue(false);
		}
	}
	
	@Test
	public void testChangeNameStrStrStrStrConflict() {
		try {
			IdNameList.getInstance().addElement("name1", "argoID1", "Stereotype");
			IdNameList.getInstance().addElement("name2", "argoID2", "Stereotype");
			IdNameList.getInstance().addParent("argoID1", "parentID");
			IdNameList.getInstance().addParent("argoID2", "parentID");
		} catch (NoDuplicateException e) {
			assertTrue(false);
		}
		try {
			IdNameList.getInstance().changeName("name1", "Stereotype", "name2", "parentID");
		} catch (NoDuplicateException e) {
			assertTrue(true);
			return;
		}
		assertTrue(false);
	}

	@Test
	public void testAddToolId() {
		try {
			IdNameList.getInstance().addElement("name", "argoID", "type");
			IdNameList.getInstance().addElement("name2", "argoID2", "type2");
			IdNameList.getInstance().addElement("name3", "argoID3", "type3");
			IdNameList.getInstance().addToolId("argoID", "toolId");
		} catch (NoDuplicateException ex) {
			assertTrue(false);
		}
		assertEquals("toolId", IdNameList.getInstance().getElement("argoID").getToolID());
	}
	
	@Test
	public void testAddToolIdConflict() {
		try {
			IdNameList.getInstance().addElement("name1", "argoID1", "type1");
			IdNameList.getInstance().addElement("name2", "argoID2", "type2");
			IdNameList.getInstance().addToolId("argoID1", "toolId");
		} catch (NoDuplicateException ex) {
			assertTrue(false);
		}
		try {
			IdNameList.getInstance().addToolId("argoID2", "toolId");
		} catch (NoDuplicateException ex) {
			assertTrue(true);
			return;
		}
		assertTrue(false);
	}
	
	@Test
	public void testGetIdFromToolId() {
		try {
			IdNameList.getInstance().addElement("name", "argoID", "type");
			IdNameList.getInstance().addToolId("argoID", "toolID");
		} catch (NoDuplicateException ex) {
			assertTrue(false);
		}
		assertEquals("argoID", IdNameList.getInstance().getIdFromToolId("toolID"));
	}

	@Test
	public void testRemoveElement() {
		try {
			IdNameList.getInstance().addElement("name", "argoID", "type");
		} catch (NoDuplicateException ex) {
			assertTrue(false);
		}
		IdNameList.getInstance().removeElement("argoID");
		try {
			IdNameList.getInstance().getElement("argoID");
		} catch (NoSuchElementException ex) {
			assertTrue(true);
			return;
		}
		assertTrue(false);
	}
	
	@Test
	public void testRemoveNonExistingElement() {
		try {
			IdNameList.getInstance().addElement("name", "argoID", "type");
		} catch (NoDuplicateException ex) {
			assertTrue(false);
		}
		try {
			IdNameList.getInstance().removeElement("argoID222");
		} catch (NoSuchElementException ex) {
			assertTrue(true);
			return;
		}
		assertTrue(false);
	}

	@Test
	public void testClear() {
		try {
			IdNameList.getInstance().addElement("name1", "argoID1", "type1");
			IdNameList.getInstance().addElement("name2", "argoID2", "type2");
			IdNameList.getInstance().addElement("name3", "argoID3", "type3");
		} catch (NoDuplicateException ex) {
			assertTrue(false);
		}
		IdNameList.getInstance().clear();
		try {
			IdNameList.getInstance().getElement("argoID1");
		} catch (NoSuchElementException ex) {
			assertTrue(true);
			try {
				IdNameList.getInstance().getElement("argoID2");
			} catch (NoSuchElementException ex2) {
				assertTrue(true);
				try {
					IdNameList.getInstance().getElement("argoID3");
				} catch (NoSuchElementException ex3) {
					assertTrue(true);
					return;
				}
			}
		}
		assertTrue(false);
	}

}
