package tum.umlsec.viki.framework.gui.menuaction;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import tum.umlsec.viki.framework.gui.TopFrame;


public class Fontsize_dec implements ActionListener {


	public Fontsize_dec(TopFrame _topFrame) {
		topFrame = _topFrame;
	}


    public void actionPerformed(ActionEvent e) {
		topFrame.fontsize_dec();
    }


	private TopFrame topFrame;
}
