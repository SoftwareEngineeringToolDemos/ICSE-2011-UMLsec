package tum.umlsec.viki.framework.gui.menuaction;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import tum.umlsec.viki.framework.gui.TopFrame;

public class FileClear implements ActionListener {
	public FileClear(TopFrame _topFrame) {
		topFrame = _topFrame;
	}
	
	
	
    public void actionPerformed(ActionEvent e) {
    	// TODO
    	
		/*
        if(UmlSecDocument.getInstance().isEmpty()) {
            return;
        }

        if(JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(UmlSecStatecharts.getInstance(), "Unload the model?", "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
            return;
        }

        UmlSecDocument.getInstance().Empty();

        UmlSecStatecharts.appendOutput("Model unloaded\n");

        // todo redraw

    */
    }
    
    
	private TopFrame topFrame;
}
