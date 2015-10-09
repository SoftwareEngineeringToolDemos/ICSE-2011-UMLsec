package open.umlsec.tools.checksystem.gui;

import javax.swing.UIManager;

import tum.umlsec.viki.framework.FrameworkBase;

/**
 * 
 * @author 
 *
 */
public class SystemVerificationGui extends FrameworkBase {

	@Override
	public void run() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
