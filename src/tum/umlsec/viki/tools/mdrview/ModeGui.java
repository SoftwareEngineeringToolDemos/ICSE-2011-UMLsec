package tum.umlsec.viki.tools.mdrview;

import java.awt.BorderLayout;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

import tum.umlsec.viki.framework.ILogOutput;
import tum.umlsec.viki.framework.toolbase.CommandDescriptor;
import tum.umlsec.viki.framework.toolbase.IVikiToolBase;
import tum.umlsec.viki.framework.toolbase.IVikiToolGui;
import tum.umlsec.viki.tools.mdrview.tree.Tree;
import tum.umlsec.viki.tools.mdrview.tree.TreeModel;
import tum.umlsec.viki.tools.mdrview.tree.nodes.Root;

/**
 * @author pasha
 */
public class ModeGui implements IVikiToolGui {

	public ModeGui(VikiToolMdrViewer _baseTool) {
		baseTool = _baseTool;
	}

	public IVikiToolBase getBase() {
		return baseTool;
	}

	public JPanel getUiPanel() {
		JPanel _panel = new JPanel(new BorderLayout());
		_panel.add(splitter, BorderLayout.CENTER );
		return _panel;
	}

	public void initialiseGui(ILogOutput _log) {
		log = _log;
		
		splitter.setOneTouchExpandable(true);

		Root _root = new Root(baseTool.getMdrContainer());
		TreeModel _model = new TreeModel(_root, baseTool.getMdrContainer());
		Tree _tree = new Tree(_model);
		_tree.addTreeWillExpandListener(_model);
		
		splitter.setLeftComponent(new JScrollPane(_tree));
		splitter.setRightComponent(new JScrollPane(textArea));
		
	}
	
	public boolean isEnabledGui() {
		return true;
	}

	public Iterator getGuiCommands() {
		return emptyVector.iterator();
	}

	public void executeGuiCommand(CommandDescriptor _command, Iterator _parameters) {
		
	}
	
	public void setfontsize(int _fontsize) {
		fontsize = _fontsize;
		textArea.setFont(new java.awt.Font("Courier new",fontsize,fontsize));
	}
	
	
	VikiToolMdrViewer baseTool;

	JSplitPane splitter = new JSplitPane();
	JTextArea textArea = new JTextArea();
	ILogOutput log;

	Vector emptyVector = new Vector();
	int fontsize = 16;
}
