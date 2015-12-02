package tum.umlsec.viki.framework.gui.menuaction;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import tum.umlsec.viki.framework.gui.TopFrame;
import tum.umlsec.viki.framework.toolbase.IVikiToolGui;

/**
 * @author pasha
 */
public class WindowSelectTool implements ActionListener {
	public WindowSelectTool(TopFrame _topFrame, IVikiToolGui _toolGui) {
		topFrame = _topFrame;
		toolGui = _toolGui;
	}

	public void actionPerformed(ActionEvent _arg0) {
		topFrame.activateTool(toolGui);
	}


	private TopFrame topFrame;
	private IVikiToolGui toolGui;
}
