package tum.umlsec.viki.framework.gui.menuaction;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import tum.umlsec.viki.framework.gui.TopFrame;


public class FileLoad implements ActionListener {


	public FileLoad(TopFrame _topFrame) {
		topFrame = _topFrame;
	}


    public void actionPerformed(ActionEvent e) {
    	
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileFilterUml());
    	
		fileChooser.setCurrentDirectory(new File(topFrame.getAppSettings().getModelDirectory()));

// TODO check for null
		int _userSelection = fileChooser.showOpenDialog(topFrame); 
		topFrame.getAppSettings().setModelDirectory(fileChooser.getSelectedFile().getParent());
		if(_userSelection != JFileChooser.APPROVE_OPTION) {
			return;
		}
    	
		if(!topFrame.getMdrContainer().isEmpty()) {
			if(JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(topFrame, "This will unload the current model. Continue?", "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
				return;
			}
		}
    	
		topFrame.getMdrContainer().Empty();
		topFrame.appendLog("Loading model from file: " + fileChooser.getSelectedFile().getName() + "\n");
		try {
			topFrame.getMdrContainer().loadFromFile(fileChooser.getSelectedFile());
			topFrame.appendLogLn("success");
		} catch (Exception x) {
			topFrame.appendLogLn("ERROR: " + x.getMessage());
		}
    }


	private TopFrame topFrame;
}
