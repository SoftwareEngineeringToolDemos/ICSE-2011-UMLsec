package tum.umlsec.viki.framework;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ConfigTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetPropertyEmpty() {
		assertEquals("", Config.getProperty("prolog_path"));
	}
	
	@Test
	public void testGetPropertyNotEmpty() {
		try {
			Config.setProperty("prolog_path", "/usr/bin/prolog");
		} catch (UnknownPropertyException e) {
			fail();
		}
		assertEquals("/usr/bin/prolog", Config.getProperty("prolog_path"));
	}
	
	@Test
	public void testGetPropertyNotExists() {
		if (Config.getProperty("prolog_config") != null)
			fail();
	}

	@Test
	public void testSetProperty() {
		try {
			Config.setProperty("prolog_path", "/usr/bin/prolog");
		} catch (UnknownPropertyException e) {
			fail();
		}
	}
	
	@Test
	public void testSetPropertyNotExists() {
		try {
			Config.setProperty("prolog", "/usr/bin/prolog");
		} catch (UnknownPropertyException e) {
			assertTrue(true);
			return;
		}
		fail();
	}

}
