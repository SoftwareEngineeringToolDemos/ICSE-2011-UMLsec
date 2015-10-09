package tum.umlsec.viki.framework.gui.menuaction;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import tum.umlsec.viki.framework.gui.TopFrame;


public class Fontsize_inc implements ActionListener {


	public Fontsize_inc(TopFrame _topFrame) {
		topFrame = _topFrame;
	}


    public void actionPerformed(ActionEvent e) {
		topFrame.fontsize_inc();
    }


	private TopFrame topFrame;
}
