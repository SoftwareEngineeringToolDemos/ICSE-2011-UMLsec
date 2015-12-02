package tum.umlsec.viki.framework.gui;

import java.awt.BorderLayout;
import java.util.Iterator;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import tum.umlsec.viki.framework.ILogOutput;
import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.toolbase.CommandDescriptor;
import tum.umlsec.viki.framework.toolbase.IVikiToolBase;
import tum.umlsec.viki.framework.toolbase.IVikiToolGui;

/**
 * @author pasha
 */
public class DefaultGuiWrapper implements IVikiToolGui, ITextOutput {

	public DefaultGuiWrapper(IVikiToolBase _toolBase) {
		toolBase = _toolBase;
	}

	public IVikiToolBase getBase() {
		return toolBase;
	}

	public JPanel getUiPanel() {
		textArea = new JTextArea();
		textArea.setFont(new java.awt.Font("Courier new",fontsize,fontsize));
		scrollPane = new JScrollPane(textArea);  
		toolPanel = new JPanel(new BorderLayout());
		
		toolPanel.add(scrollPane, BorderLayout.CENTER);
		return toolPanel;
	}

	public void initialiseGui(ILogOutput _log) {
		log = _log;
		toolBase.getConsole().initialiseConsole();
	}

	public boolean isEnabledGui() {
		return true;
	}

	public Iterator getGuiCommands() {
		return toolBase.getConsole().getConsoleCommands();
	}

	public void executeGuiCommand(CommandDescriptor _command, Iterator _parameters) {
		toolBase.getConsole().executeConsoleCommand(_command, _parameters, this, log);
	}
	
	public void write(String _s) {
		textArea.append(_s);
	}

	public void writeLn(String _s) {
		textArea.append(_s + "\n");
	}

	public void writeLn() {
		textArea.append("\n");
	}
	
	public void setfontsize(int _fontsize) {
		fontsize = _fontsize;
		textArea.setFont(new java.awt.Font("Courier new",fontsize,fontsize));
	}
	
	
	
	 
	IVikiToolBase toolBase;


	JPanel toolPanel;
	JScrollPane scrollPane;
	JTextArea textArea;
	int fontsize = 16;


	ILogOutput log;
}
