package tum.umlsec.viki.framework.toolbase;

import tum.umlsec.viki.framework.mdr.IMdrContainer;

/**
 * @author pasha
 */
public interface IVikiToolBase {
	IVikiToolConsole getConsole();
	IVikiToolGui getGui();
	IVikiToolWeb getWeb();

	void initialiseBase(IMdrContainer container);
		
	String getToolName();
	String getToolDescription();
}
