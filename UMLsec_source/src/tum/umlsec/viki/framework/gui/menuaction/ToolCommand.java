package tum.umlsec.viki.framework.gui.menuaction;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import tum.umlsec.viki.framework.gui.InstalledGuiToolDescriptor;
import tum.umlsec.viki.framework.gui.TopFrame;
import tum.umlsec.viki.framework.toolbase.CommandDescriptor;

/**
 * @author pasha
 */
public class ToolCommand implements ActionListener {
	public ToolCommand(TopFrame _topFrame, InstalledGuiToolDescriptor _descriptor, CommandDescriptor _command) {
		topFrame = _topFrame;
		toolDescriptor = _descriptor;
		command = _command;
	}
	
	public void actionPerformed(ActionEvent e) {
		topFrame.executeToolCommand(toolDescriptor, command);
	}


	private TopFrame topFrame;
	private InstalledGuiToolDescriptor toolDescriptor;
	private CommandDescriptor command;

}
