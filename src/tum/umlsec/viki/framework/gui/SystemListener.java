package tum.umlsec.viki.framework.gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


/**
 * @author pasha
 *
 */
public class SystemListener extends WindowAdapter {
	public SystemListener(TopFrame _frame) {
		frame = _frame;
	}
	public void windowClosing(WindowEvent e) {
		frame.OnExit();
		System.exit(0);
	}
	
	TopFrame frame;
}
