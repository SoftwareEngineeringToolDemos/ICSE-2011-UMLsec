package tum.umlsec.viki.framework.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;

import tum.umlsec.viki.framework.AppSettings;
import tum.umlsec.viki.framework.ExceptionProgrammLogicError;
import tum.umlsec.viki.framework.ILogOutput;
import tum.umlsec.viki.framework.gui.menuaction.FileClear;
import tum.umlsec.viki.framework.gui.menuaction.FileExit;
import tum.umlsec.viki.framework.gui.menuaction.FileLoad;
import tum.umlsec.viki.framework.gui.menuaction.FileSave;
import tum.umlsec.viki.framework.gui.menuaction.FileSaveAs;
import tum.umlsec.viki.framework.gui.menuaction.Fontsize_dec;
import tum.umlsec.viki.framework.gui.menuaction.Fontsize_inc;
import tum.umlsec.viki.framework.gui.menuaction.ToolCommand;
import tum.umlsec.viki.framework.gui.menuaction.WindowSelectTool;
import tum.umlsec.viki.framework.mdr.MdrContainer;
import tum.umlsec.viki.framework.toolbase.CPRGui;
import tum.umlsec.viki.framework.toolbase.CommandDescriptor;
import tum.umlsec.viki.framework.toolbase.CommandParameterDescriptor;
import tum.umlsec.viki.framework.toolbase.IVikiToolBase;
import tum.umlsec.viki.framework.toolbase.IVikiToolGui;
import tum.umlsec.viki.tools.dynaviki.model.ExceptionBadModel;


/**
 * @author pasha
 *
 */
public class TopFrame extends JFrame implements ILogOutput {
	private static Logger logger = Logger.getLogger("TopFrame");
	public TopFrame(MdrContainer _mdrContainer, AppSettings _appSettings, IVikiToolBase [] _tools) {
		mdrContainer = _mdrContainer;
		appSettings = _appSettings;
		logArea.setFont(new java.awt.Font("Courier new",fontsize,fontsize));
		menuTool.setFont(new java.awt.Font("Arial",16,16));
		menuWindow.setFont(new java.awt.Font("Arial",16,16));
		tools = _tools;
		toolsGui = new InstalledGuiToolDescriptor [_tools.length];
		
		createGUI();
		pack();
	}

	private void createGUI() {
		BorderLayout _layout = new BorderLayout();
		getContentPane().setLayout(_layout);

		setTitle("Viki UML(sec) verifier");

		JPopupMenu.setDefaultLightWeightPopupEnabled(false);

// create splitter
		mainHorisontalSplitter.setOrientation(JSplitPane.VERTICAL_SPLIT);
		getContentPane().add(mainHorisontalSplitter, BorderLayout.CENTER);
		mainHorisontalSplitter.setDividerLocation(getAppSettings().getSplitterPosition());

		mainHorisontalSplitter.setRightComponent(new JScrollPane(logArea));
		mainHorisontalSplitter.setLeftComponent(toolArea);
        
		toolArea.setTabPlacement(JTabbedPane.BOTTOM);
        
// create status bar
		JPanel _statusPanel = new JPanel();

		_statusPanel.setBorder(BorderFactory.createEtchedBorder());
		_statusPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		_statusPanel.add(statusBar);
		getContentPane().add(_statusPanel, BorderLayout.SOUTH);

		
		for(int i = 0; i < tools.length; i++) {
			installTool(i, tools[i]);
		}		


//		create menu
		buildMenu();
		
		updateToolCommands();		
	}






	private void installTool(int _index, IVikiToolBase _toolBase) {
		IVikiToolGui _toolGui = _toolBase.getGui();
		if(_toolGui == null) {
			_toolGui = new DefaultGuiWrapper(_toolBase);
		}
		InstalledGuiToolDescriptor _toolDescriptor = new InstalledGuiToolDescriptor(_toolGui);
		toolsGui[_index] = _toolDescriptor;
		
		menuTool.add(_toolDescriptor.getToolMenu());
		_toolDescriptor.getWindowMenuItem().addActionListener(new WindowSelectTool(this, _toolGui));
		menuWindow.add(_toolDescriptor.getWindowMenuItem());
		toolArea.setFont(new java.awt.Font("Arial",16,16));
		toolpanel = _toolGui.getUiPanel();
		toolpanel.setFont(new java.awt.Font("Courier new",fontsize,fontsize));
		toolArea.add(_toolBase.getToolName(), toolpanel);

		_toolGui.initialiseGui(this); 
	}



	private void buildMenu() {
		JMenuBar _menubar = new JMenuBar();
		
//		---------------------------------
		JMenu _menuFile = new JMenu("File");
		_menuFile.setFont(new java.awt.Font("Arial",16,16));
		JMenuItem _menuItemClear = new JMenuItem("Clear");
		_menuItemClear.setFont(new java.awt.Font("Arial",16,16));
		JMenuItem _menuItemLoad = new JMenuItem("Load...");
		_menuItemLoad.setFont(new java.awt.Font("Arial",16,16));
		JMenuItem _menuItemSave = new JMenuItem("Save");
		_menuItemSave.setFont(new java.awt.Font("Arial",16,16));
		JMenuItem _menuItemSaveAs = new JMenuItem("Save As...");
		_menuItemSaveAs.setFont(new java.awt.Font("Arial",16,16));
		JMenuItem _menuItemExit = new JMenuItem("Exit");
		_menuItemExit.setFont(new java.awt.Font("Arial",16,16));
	
		_menuItemClear.addActionListener(new FileClear(this));
		_menuItemLoad.addActionListener(new FileLoad(this));
		_menuItemSave.addActionListener(new FileSave(this));
		_menuItemSaveAs.addActionListener(new FileSaveAs(this));
		_menuItemExit.addActionListener(new FileExit(this));

		_menuFile.add(_menuItemClear);
		_menuFile.add(_menuItemLoad);
		_menuFile.add(_menuItemSave);
		_menuFile.add(_menuItemSaveAs);
		_menuFile.addSeparator();
		_menuFile.add(_menuItemExit);
		
		JMenu _menuAbout = new JMenu("About");
		_menuAbout.setFont(new java.awt.Font("Arial", 16, 16));
		JMenuItem _menuItemAbout = new JMenuItem("About");
		_menuItemAbout.setFont(new java.awt.Font("Arial", 16, 16));
		_menuItemAbout.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				JFrame frame = new JFrame();
				AboutDialog about = new AboutDialog(frame, true);
				frame.setVisible(true);
				about.setVisible(true);
			}
		});
		_menuAbout.add(_menuItemAbout);

		fontsize_inc.addActionListener(new Fontsize_inc(this));
		fontsize_dec.addActionListener(new Fontsize_dec(this));

//	---------------------------------
		_menubar.add(_menuFile);			
		_menubar.add(menuTool);			
		_menubar.add(menuWindow);
		_menubar.add(_menuAbout);
		_menubar.add(fontsize_inc);
		_menubar.add(fontsize_dec);
		
		setJMenuBar(_menubar);
	}

	private void updateToolCommands() {
		for (int i = 0; i < toolsGui.length; i++) {
			InstalledGuiToolDescriptor _toolDescriptor = toolsGui[i];
			
			_toolDescriptor.getToolMenu().removeAll();
			_toolDescriptor.getToolMenu().setEnabled(_toolDescriptor.getToolInterface().isEnabledGui());
			
			for(Iterator iter = _toolDescriptor.getToolInterface().getGuiCommands(); iter.hasNext();) {
				CommandDescriptor _command = (CommandDescriptor) iter.next();
				
				JMenuItem _menuItem = new JMenuItem(_command.getName());
				_menuItem.setFont(new java.awt.Font("Arial",16,16));
				_menuItem.addActionListener(new ToolCommand(this, _toolDescriptor, _command));
				_menuItem.setEnabled(_command.isEnabled());
				
				_toolDescriptor.getToolMenu().add(_menuItem);
			}
		}
	}

	public void activateTool(IVikiToolGui _toolGui) {
		for(int _cnt = 0; _cnt < toolArea.getTabCount(); _cnt++) {
			if(_toolGui.getBase().getToolName().compareTo(toolArea.getTitleAt(_cnt)) == 0) {
				toolArea.setSelectedIndex(_cnt);
				break;  
			}
		}
	}

	public void executeToolCommand(InstalledGuiToolDescriptor _toolDescriptor, CommandDescriptor _command) {
		logger.trace("executing " + _command.getName());
		boolean quit = false;
		CPRGui _parameterReader = new CPRGui(this);
		for (Iterator iter = _command.getParameters(); iter.hasNext();) {
			CommandParameterDescriptor _parameter = (CommandParameterDescriptor) iter.next();
			if(!_parameterReader.read(_parameter)) {
				quit = true;
				break; 
			}
		}
		if(quit) {
			return;
		}
		
		try {
			_toolDescriptor.getToolInterface().executeGuiCommand(_command, _command.getParameters());
		} catch(ExceptionProgrammLogicError x) {
			x.printStackTrace();
			appendLogLn("ExceptionProgrammLogicError: " + x.getMessage());
		} catch(ExceptionBadModel x) {
			x.printStackTrace();
			appendLogLn("ExceptionBadModel: " + x.getMessage());
		}
		
		updateToolCommands();
	}




	public void appendLog(String str) {
		logArea.append(str);
	}

	public void appendLogLn(String str) {
		logArea.append(str + "\n");
	}

	public void appendLogLn() {
		logArea.append("\n");
	}

	public void fontsize_inc() {
		fontsize++;
		logArea.setFont(new java.awt.Font("Courier new",fontsize,fontsize));
//		_toolGui.setfontsize(fontsize);
		for (int i = 0; i < toolsGui.length; i++) {
			toolsGui[i].getToolInterface().setfontsize(fontsize);
		}
	}
	
	public void fontsize_dec() {
		fontsize--;
		logArea.setFont(new java.awt.Font("Courier new",fontsize,fontsize));
		toolpanel.setFont(new java.awt.Font("Arial",fontsize,fontsize));
		for (int i = 0; i < toolsGui.length; i++) {
			toolsGui[i].getToolInterface().setfontsize(fontsize);
		}
	}


	public void OnExit() {
		
		// TODO check that this one is not called twice when i select File->Exit
		getAppSettings().setMaximized(getExtendedState() == MAXIMIZED_BOTH);
		getAppSettings().setWindowRec(getBounds());
		getAppSettings().setSplitterPosition(mainHorisontalSplitter.getDividerLocation());
		
// notify tools about exit
//		for(Iterator _it = installedTools.iterator(); _it.hasNext(); ) {
//			IVikiToolGui _tool = ((InstalledGuiToolDescriptor)_it.next()).getTool();
//			_tool.onCloseGui();
//		}
	}


	public MdrContainer getMdrContainer() {		return mdrContainer;		}
	public AppSettings getAppSettings() {		return appSettings;			}


	private MdrContainer mdrContainer;
	private AppSettings appSettings;
	private IVikiToolBase [] tools;
	private InstalledGuiToolDescriptor [] toolsGui;
	
	
	private JSplitPane mainHorisontalSplitter = new JSplitPane();
	public JTextArea logArea = new JTextArea();
	private JTabbedPane toolArea = new JTabbedPane();    
	private JLabel statusBar = new JLabel("Ready");
	private JMenu menuTool = new JMenu("Tool");
	private JMenu menuWindow = new JMenu("Window");
	private JButton fontsize_inc = new JButton("Font+");
	private JButton fontsize_dec = new JButton("Font-");
	private JPanel toolpanel;
	public int fontsize = 16;
}
