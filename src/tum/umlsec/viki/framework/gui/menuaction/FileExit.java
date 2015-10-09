package tum.umlsec.viki.framework.gui.menuaction;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import tum.umlsec.viki.framework.gui.TopFrame;

public class FileExit implements ActionListener {
	public FileExit(TopFrame _topFrame) {
		topFrame = _topFrame;
	}
	
    public void actionPerformed(ActionEvent e) {
    	topFrame.OnExit();
		System.exit(0);
    }
    
    private TopFrame topFrame;
}
