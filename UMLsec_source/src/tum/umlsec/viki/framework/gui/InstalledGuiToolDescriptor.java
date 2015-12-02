package tum.umlsec.viki.framework.gui;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import tum.umlsec.viki.framework.toolbase.IVikiToolGui;

/**
 * @author pasha
 */
public class InstalledGuiToolDescriptor {

	public InstalledGuiToolDescriptor(IVikiToolGui _toolGui) {
		toolGui = _toolGui;
		
		toolMenu = new JMenu(toolGui.getBase().getToolName());
		toolMenu.setFont(new java.awt.Font("Arial",16,16));
		windowMenuItem = new JMenuItem(toolGui.getBase().getToolName());
		windowMenuItem.setFont(new java.awt.Font("Arial",16,16));
	}
	

	
	public IVikiToolGui getToolInterface() {
		return toolGui;
	}
	public JMenu getToolMenu() {
		return toolMenu;
	}
	public JMenuItem getWindowMenuItem() {
		return windowMenuItem;
	}
	


	IVikiToolGui toolGui;

	JMenu toolMenu;
	JMenuItem windowMenuItem;
}
