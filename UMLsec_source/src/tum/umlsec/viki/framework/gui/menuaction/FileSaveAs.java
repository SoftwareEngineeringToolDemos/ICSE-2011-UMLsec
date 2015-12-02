package tum.umlsec.viki.framework.gui.menuaction;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;

import tum.umlsec.viki.framework.gui.TopFrame;
import tum.umlsec.viki.framework.mdr.MdrContainer;

public class FileSaveAs implements ActionListener {
	public FileSaveAs(TopFrame _topFrame) {
		topFrame = _topFrame;
	}

    public void actionPerformed(ActionEvent e) {
    	JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showOpenDialog(topFrame);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
        	MdrContainer container = topFrame.getMdrContainer();
        	container.write(container.getUmlPackage(), chooser.getSelectedFile());
        }

    }

	private TopFrame topFrame;
}
