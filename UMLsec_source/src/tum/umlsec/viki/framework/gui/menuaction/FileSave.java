package tum.umlsec.viki.framework.gui.menuaction;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import tum.umlsec.viki.framework.gui.TopFrame;

public class FileSave implements ActionListener {
	public FileSave(TopFrame _topFrame) {
		topFrame = _topFrame;
	}
	
    public void actionPerformed(ActionEvent e) {
    }

	private TopFrame topFrame;
}
