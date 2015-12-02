/**
 * 
 */
package tum.umlsec.viki.tools.checkstatic.checks;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import tum.umlsec.viki.framework.FrameworkBase;
import tum.umlsec.viki.framework.console.FrameworkConsole;
import tum.umlsec.viki.framework.mdr.MdrContainer;
import tum.umlsec.viki.framework.toolbase.IVikiToolBase;
import tum.umlsec.viki.tools.checkstatic.ToolCheckStatic;

/**
 * @author lionel
 *
 */
public class CheckerFairExchangeTest {
	// Add here the tools that you need to load for your unit tests
	static IVikiToolBase []tools = { 
		new ToolCheckStatic()
	};
	private MdrContainer mdrContainer;
	private FrameworkBase framework;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		//mdrContainer = new MdrContainer(null, null);
		//UmlPackage uml = mdrContainer.getUmlPackage();
		//uml.getActivityGraphs().getActivityGraph().createActivityGraph();
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link tum.umlsec.viki.tools.checkstatic.checks.CheckerFairExchange#check(tum.umlsec.viki.framework.mdr.IMdrContainer, java.util.Iterator, tum.umlsec.viki.framework.ITextOutput)}.
	 */
	@Test
	public void testCheck() {
		// Name of the XMI (or zargo) file to be processed
		String filename = "";
		// Name of the tool to be run
		String tool = "";
		String[] args = {filename, tool};
		framework = new FrameworkConsole(args);
		framework.initialiseBase(tools, null, null);
		framework.run();
	}

}
