package tum.umlsec.viki.framework.gui;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import tum.umlsec.viki.framework.FrameworkBase;


/**
 * @author pasha
 */
public class FrameworkGui extends FrameworkBase {

	public void run() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		TopFrame _topFrame = new TopFrame(getMdrContainer(), getAppSettings(), getTools());
		_topFrame.setBounds(getAppSettings().getWindowRec());
		if(getAppSettings().getMaximized()) {
			_topFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		}
			
		_topFrame.setVisible(true);
		_topFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		_topFrame.addWindowListener(new SystemListener(_topFrame));
	}
}
