package tum.umlsec.viki.framework.toolbase;

import java.util.Iterator;

import javax.swing.JPanel;

import tum.umlsec.viki.framework.ILogOutput;



/**
 * @author pasha
 *
 */
public interface IVikiToolGui {
	IVikiToolBase getBase();

	JPanel getUiPanel();
	void initialiseGui(ILogOutput _log);

	boolean isEnabledGui();

	Iterator getGuiCommands();

	void executeGuiCommand(CommandDescriptor _command, Iterator _parameters);
	
	void setfontsize(int _fontsize);
	

// TODO which of the below do we miss?
	
//
//	String getMenuName();
//	String getTabName();
//	String getDescriptionGui();
//
//
//	/**
//	 * guaranteed to be called only once
//	 * 
//	 * @return
//	 */
//	JPanel getUiPanel();
//	
//	// TODO
//	Icon getMenuIcon();
//	Icon getTabIcon();
//
//	void onCloseGui();
//
}
