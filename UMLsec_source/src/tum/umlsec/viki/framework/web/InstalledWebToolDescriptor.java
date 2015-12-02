package tum.umlsec.viki.framework.web;

import tum.umlsec.viki.framework.toolbase.IVikiToolWeb;

/**
 * @author pasha
 */
public class InstalledWebToolDescriptor {
	public InstalledWebToolDescriptor(IVikiToolWeb _toolWeb) {
		toolWeb = _toolWeb;
	}
	
	public IVikiToolWeb getToolInterface() {
		return toolWeb;
	}

	IVikiToolWeb toolWeb;
}
