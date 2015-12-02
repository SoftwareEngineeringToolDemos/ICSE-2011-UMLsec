package tum.umlsec.viki.tools.mdrview;

import tum.umlsec.viki.framework.mdr.IMdrContainer;
import tum.umlsec.viki.framework.toolbase.IVikiToolBase;
import tum.umlsec.viki.framework.toolbase.IVikiToolConsole;
import tum.umlsec.viki.framework.toolbase.IVikiToolGui;
import tum.umlsec.viki.framework.toolbase.IVikiToolWeb;

/**
 * @author pasha
 */
public class VikiToolMdrViewer implements IVikiToolBase {
	public IVikiToolConsole getConsole() {
		if(consoleClass == null) {		
			consoleClass = new ModeConsole(this);  
		}
		return consoleClass;	
	}
	public IVikiToolGui getGui() {
		if(guiClass == null) {				
			guiClass = new ModeGui(this);
		}
		return guiClass;	
	}
	public IVikiToolWeb getWeb() {				return null;			}

	public void initialiseBase(IMdrContainer _mdrContainer) {
		mdrContainer = _mdrContainer;
	}
	
	public String getToolName() {				return "MdrViewer";									}
	public String getToolDescription() {		return "Displays content of the MDR repository.";	}
	public IMdrContainer getMdrContainer() {	return mdrContainer;								}


	ModeGui guiClass = null;
	ModeConsole consoleClass = null; 
	IMdrContainer mdrContainer;
}
