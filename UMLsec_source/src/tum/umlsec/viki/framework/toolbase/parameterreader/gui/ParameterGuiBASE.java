package tum.umlsec.viki.framework.toolbase.parameterreader.gui;

import java.awt.Component;

import javax.swing.JOptionPane;

import tum.umlsec.viki.framework.toolbase.CommandParameterDescriptor;
import tum.umlsec.viki.framework.toolbase.parameterreader.ParameterBASE;

/**
 * @author pasha
 */
public abstract class ParameterGuiBASE extends ParameterBASE {
	public ParameterGuiBASE(Component _parentComponent) {
		parentComponent = _parentComponent;
	}
	
	public abstract boolean read(CommandParameterDescriptor _parameter);
	
	protected String showQueryDialog(String _message, String _title) {
		return JOptionPane.showInputDialog(parentComponent, _message, _title, JOptionPane.OK_CANCEL_OPTION | JOptionPane.QUESTION_MESSAGE);
	}
	
	
	Component parentComponent;
}
