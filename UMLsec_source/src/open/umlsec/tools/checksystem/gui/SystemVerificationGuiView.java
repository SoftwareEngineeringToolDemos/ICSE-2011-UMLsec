package open.umlsec.tools.checksystem.gui;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;

import javax.swing.ActionMap;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.GroupLayout.Alignment;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import open.umlsec.tools.checksystem.ToolSystemVerification;
import open.umlsec.tools.checksystem.checks.Requirement;
import open.umlsec.tools.checksystem.checks.ToolDescriptor;

import org.apache.log4j.Logger;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;

import tum.umlsec.viki.framework.AppSettings;
import tum.umlsec.viki.framework.FrameworkBase;
import tum.umlsec.viki.framework.ILogOutput;
import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.gui.menuaction.FileFilterUml;
import tum.umlsec.viki.framework.mdr.MdrContainer;
import tum.umlsec.viki.framework.toolbase.CommandDescriptor;
import tum.umlsec.viki.tools.riskFinder.RepositoryHelper;

/**
 * 
 * The applications mainframe
 * 
 * @author (GUI) Serge Kater
 * @author (refactoring) Bianca Batsch, bianca.batsch@isst.fraunhofer.de
 * 
 */
public class SystemVerificationGuiView extends FrameView implements ILogOutput,
		ITextOutput {

	private static Logger logger;
	private JButton btnAnalyse;

	private JTextArea descriptionField;

	private JLabel statusAnimationLabel;
	private JLabel statusMessageLabel;

	private JTextArea globalOutputArea;
	private JTextArea pluginResultArea;
	private JTextField nameTextField;

	private JPanel mainPanel;
	// private JPanel statusPanel;

	private JList modelList;
	private JList toolList;

	private JSeparator statusPanelSeparator;

	private JTable tableRequirements;
	private JTable modelsTable;

	private JScrollPane filesLoadedScrollPane;

	private JProgressBar progressBar;

	private JTabbedPane tabMain;

	private JMenuBar menuBar;

	// Security Repository Menu
	private JMenu repositoryMenu;
	private JMenuItem repositoryResetItem;
	private JMenuItem repositoryBackupItem;
	private JMenuItem repositoryAddItem;
	private JMenuItem repositoryDelItem;

	private static File repositoryFile;

	private static RepositoryHelper repositoryhelper = new RepositoryHelper();
	// ENDOF Security Repository

	private DefaultListModel toolListModel;
	private DefaultListModel cmdListModel;

	private final Timer messageTimer;
	@SuppressWarnings("unused")
	private final Timer busyIconTimer;
	private final Icon idleIcon;
	private final Icon[] busyIcons = new Icon[15];
	private int busyIconIndex = 0;

	private JDialog aboutBox;

	private AppSettings appSettings;
	private SystemVerificationLoader appLoader;
	private MdrContainer mdrContainer;
	private JDialog newAnalysisBox;

	private int modelListIdx = 9999999;
	private ActionJList actionList = null;

	private JPopupMenu pmnuToolList = null;
	private JMenuItem mnuToolsEnable = null;
	private JMenuItem mnuToolsDisable = null;
	private JPopupMenu pmnuCmdList = null;
	private JMenuItem mnuCmdsEnable = null;
	private JMenuItem mnuCmdsDisable = null;

	private JDialog addToolBox;

	// Logger Menu
	private JMenu loggerMenu;
	private ButtonGroup loggerButtonGroup;
	private JRadioButtonMenuItem loggerAllItem;
	private JRadioButtonMenuItem loggerTraceItem;
	private JRadioButtonMenuItem loggerDebugItem;
	private JRadioButtonMenuItem loggerInfoItem;
	private JRadioButtonMenuItem loggerWarningItem;
	private JRadioButtonMenuItem loggerErrorItem;
	private JRadioButtonMenuItem loggerFatalItem;
	private JRadioButtonMenuItem loggerOffItem;

	// ENDOF Logger Menu

	/**
	 * 
	 * @param application
	 */
	public SystemVerificationGuiView(SingleFrameApplication application) {
		super(application);

		init();

		// status bar initialization - message timeout, idle icon and busy
		// animation, etc
		ResourceMap resourceMap = getResourceMap();
		int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
		messageTimer = new Timer(messageTimeout, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				statusMessageLabel.setText("");
			}
		});

		messageTimer.setRepeats(false);
		int busyAnimationRate = resourceMap
				.getInteger("StatusBar.busyAnimationRate");
		for (int i = 0; i < busyIcons.length; i++) {
			busyIcons[i] = resourceMap
					.getIcon("StatusBar.busyIcons[" + i + "]");
		}
		busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
				statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
			}
		});

		idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
		statusAnimationLabel.setIcon(idleIcon);
		progressBar.setVisible(false);

		// connecting action tasks to status bar via TaskMonitor
		// TaskMonitor taskMonitor = new
		// TaskMonitor(getApplication().getContext());
		// taskMonitor.addPropertyChangeListener(new
		// java.beans.PropertyChangeListener() {
		// public void propertyChange(java.beans.PropertyChangeEvent evt) {
		// String propertyName = evt.getPropertyName();
		// if ("started".equals(propertyName)) {
		// if (!busyIconTimer.isRunning()) {
		// statusAnimationLabel.setIcon(busyIcons[0]);
		// busyIconIndex = 0;
		// busyIconTimer.start();
		// }
		// progressBar.setVisible(true);
		// progressBar.setIndeterminate(true);
		// } else if ("done".equals(propertyName)) {
		// busyIconTimer.stop();
		// statusAnimationLabel.setIcon(idleIcon);
		// progressBar.setVisible(false);
		// progressBar.setValue(0);
		// } else if ("message".equals(propertyName)) {
		// String text = (String)(evt.getNewValue());
		// statusMessageLabel.setText((text == null) ? "" : text);
		// messageTimer.restart();
		// } else if ("progress".equals(propertyName)) {
		// int value = (Integer)(evt.getNewValue());
		// progressBar.setVisible(true);
		// progressBar.setIndeterminate(false);
		// progressBar.setValue(value);
		// }
		// }
		// });
	}

	@Action
	public void showAboutBox() {
		if (aboutBox == null) {
			JFrame mainFrame = SystemVerificationLoader.getApplication()
					.getMainFrame();
			aboutBox = new SystemVerificationGuiAboutBox(mainFrame);
			aboutBox.setLocationRelativeTo(mainFrame);
		}
		SystemVerificationLoader.getApplication().show(aboutBox);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings({ "unchecked", "serial" })
	private void init() {
		btnAnalyse = new JButton();

		mainPanel = new JPanel();

		tabMain = new JTabbedPane();

		modelsTable = new JTable();
		tableRequirements = new JTable();

		nameTextField = new JTextField();

		descriptionField = new JTextArea();
		globalOutputArea = new JTextArea();
		pluginResultArea = new JTextArea();

		toolList = new JList();
		modelList = new JList();

		statusMessageLabel = new JLabel();
		statusAnimationLabel = new JLabel();

		statusPanelSeparator = new JSeparator();

		progressBar = new JProgressBar();

		menuBar = new JMenuBar();

		// Security Repository Menu
		repositoryMenu = new JMenu();
		repositoryResetItem = new JMenuItem();
		repositoryBackupItem = new JMenuItem();
		repositoryAddItem = new JMenuItem();
		repositoryDelItem = new JMenuItem();
		// ENDOF Security Repository

		// Logger Menu
		loggerMenu = new JMenu();
		loggerButtonGroup = new ButtonGroup();
		loggerAllItem = new JRadioButtonMenuItem();
		loggerTraceItem = new JRadioButtonMenuItem();
		loggerDebugItem = new JRadioButtonMenuItem();
		loggerInfoItem = new JRadioButtonMenuItem();
		loggerWarningItem = new JRadioButtonMenuItem();
		loggerErrorItem = new JRadioButtonMenuItem();
		loggerFatalItem = new JRadioButtonMenuItem();
		loggerOffItem = new JRadioButtonMenuItem();
		// ENDOF Logger Menu

		mainPanel.setName("mainPanel");

		tabMain.setName("tabMain");

		filesLoadedScrollPane = new JScrollPane();
		filesLoadedScrollPane.setName("jScrollPane1");

		// TODO: durch Methode ersetzen

		String[] identifiers = new String[] { "Name", "Path", "Status" };

		modelsTable.setModel(new DefaultTableModel(new Object[5][3],
				new String[] { "Name", "Path", "Status" }) {
			Class[] types = new Class[] { String.class, String.class,
					String.class };
			boolean[] canEdit = new boolean[] { false, false, false };

			public Class getColumnClass(int columnIndex) {
				return types[columnIndex];
			}

			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return canEdit[columnIndex];
			}
		});

		modelsTable.setName("modelsTable");
		filesLoadedScrollPane.setViewportView(modelsTable);

		ResourceMap resourceMap = Application
				.getInstance(SystemVerificationLoader.class).getContext()
				.getResourceMap(SystemVerificationGuiView.class);

		tabMain.addTab(
				resourceMap.getString("panelOverview.TabConstraints.tabTitle"),
				buildOverviewTab(resourceMap));
		tabMain.addTab(
				resourceMap.getString("panelDetails.TabConstraints.tabTitle"),
				buildDetailsTab(resourceMap));
		tabMain.addTab(resourceMap
				.getString("panelRequirements.TabConstraints.tabTitle"),
				buildRequirementsTab(resourceMap));

		buildMainPanel();

		menuBar.setName("menuBar");
		menuBar.add(buildFileMenu(resourceMap));
		menuBar.add(buildModelMenu(resourceMap));
		menuBar.add(buildToolsMenu(resourceMap));
		menuBar.add(buildHelpMenu(resourceMap));

		// Security Repository Menu
		repositoryMenu.setText("Security Repository");
		repositoryMenu.setName("repositoryMenu");

		repositoryResetItem.setText("reset");
		repositoryResetItem.setName("repositoryResetMenuItem");
		repositoryResetItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				repositoryhelper.resetRepository(repositoryFile);
			}
		});
		repositoryBackupItem.setText("backup");
		repositoryBackupItem.setName("repositoryBackupMenuItem");
		repositoryBackupItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				repositoryhelper.backupRepository(repositoryFile);
			}
		});
		repositoryAddItem.setText("add");
		repositoryAddItem.setName("repositoryAddMenuItem");
		repositoryAddItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				repositoryhelper.makeAddGui();
			}
		});
		repositoryDelItem.setText("delete");
		repositoryDelItem.setName("repositoryDelMenuItem");
		repositoryDelItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				repositoryhelper.makeDelGui();
			}
		});
		repositoryMenu.add(repositoryResetItem);
		repositoryMenu.add(repositoryBackupItem);
		repositoryMenu.add(repositoryAddItem);
		repositoryMenu.add(repositoryDelItem);
		repositoryMenu.setVisible(false);
		repositoryMenu.setEnabled(false);
		menuBar.add(repositoryMenu);

		loggerMenu.setText("Debug level");
		loggerMenu.setName("loggerMenu");

		loggerAllItem.setText("All");
		loggerAllItem.setName("loggerAllItem");
		loggerAllItem.setActionCommand("All");
		loggerButtonGroup.add(loggerAllItem);

		loggerTraceItem.setText("Trace");
		loggerTraceItem.setName("loggerTraceItem");
		loggerTraceItem.setActionCommand("Trace");
		loggerButtonGroup.add(loggerTraceItem);

		loggerDebugItem.setText("Debug");
		loggerDebugItem.setName("loggerDebugItem");
		loggerDebugItem.setActionCommand("Debug");
		loggerButtonGroup.add(loggerDebugItem);

		loggerInfoItem.setText("Info");
		loggerInfoItem.setName("loggerInfoItem");
		loggerInfoItem.setActionCommand("Info");
		loggerButtonGroup.add(loggerInfoItem);

		loggerWarningItem.setText("Warning");
		loggerWarningItem.setName("loggerWarningItem");
		loggerWarningItem.setActionCommand("Warning");
		loggerButtonGroup.add(loggerWarningItem);

		loggerErrorItem.setText("Error");
		loggerErrorItem.setName("loggerErrorItem");
		loggerErrorItem.setActionCommand("Error");
		loggerButtonGroup.add(loggerErrorItem);

		loggerFatalItem.setText("Fatal");
		loggerFatalItem.setName("loggerFatalItem");
		loggerFatalItem.setActionCommand("Fatal");
		loggerButtonGroup.add(loggerFatalItem);

		loggerOffItem.setText("Off");
		loggerOffItem.setName("loggerOffItem");
		loggerOffItem.setActionCommand("Off");
		loggerButtonGroup.add(loggerOffItem);

		loggerMenu.add(loggerAllItem);
		loggerMenu.add(loggerTraceItem);
		loggerMenu.add(loggerDebugItem);
		loggerMenu.add(loggerInfoItem);
		loggerMenu.add(loggerWarningItem);
		loggerMenu.add(loggerErrorItem);
		loggerMenu.add(loggerFatalItem);
		loggerMenu.add(loggerOffItem);
		logger = Logger.getRootLogger();
		if (logger.getLevel() == org.apache.log4j.Level.ALL) {
			loggerAllItem.setSelected(true);
		}
		if (logger.getLevel() == org.apache.log4j.Level.TRACE) {
			loggerTraceItem.setSelected(true);
		}
		if (logger.getLevel() == org.apache.log4j.Level.DEBUG) {
			loggerDebugItem.setSelected(true);
		}
		if (logger.getLevel() == org.apache.log4j.Level.INFO) {
			loggerInfoItem.setSelected(true);
		}
		if (logger.getLevel() == org.apache.log4j.Level.WARN) {
			loggerWarningItem.setSelected(true);
		}
		if (logger.getLevel() == org.apache.log4j.Level.ERROR) {
			loggerErrorItem.setSelected(true);
		}
		if (logger.getLevel() == org.apache.log4j.Level.FATAL) {
			loggerFatalItem.setSelected(true);
		}
		if (logger.getLevel() == org.apache.log4j.Level.OFF) {
			loggerOffItem.setSelected(true);
		}

		ActionListener loggerActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Logger logger = Logger.getRootLogger();
				if (e.getActionCommand().equals("All")) {
					logger.setLevel(org.apache.log4j.Level.ALL);
				}
				if (e.getActionCommand().equals("Trace")) {
					logger.setLevel(org.apache.log4j.Level.TRACE);
				}
				if (e.getActionCommand().equals("Debug")) {
					logger.setLevel(org.apache.log4j.Level.DEBUG);
				}
				if (e.getActionCommand().equals("Info")) {
					logger.setLevel(org.apache.log4j.Level.INFO);
				}
				if (e.getActionCommand().equals("Warn")) {
					logger.setLevel(org.apache.log4j.Level.WARN);
				}
				if (e.getActionCommand().equals("Error")) {
					logger.setLevel(org.apache.log4j.Level.ERROR);
				}
				if (e.getActionCommand().equals("Fatal")) {
					logger.setLevel(org.apache.log4j.Level.FATAL);
				}
				if (e.getActionCommand().equals("Off")) {
					logger.setLevel(org.apache.log4j.Level.OFF);
				}
				// System.out.println(logger.getLevel());
			}
		};

		loggerAllItem.addActionListener(loggerActionListener);
		loggerTraceItem.addActionListener(loggerActionListener);
		loggerDebugItem.addActionListener(loggerActionListener);
		loggerInfoItem.addActionListener(loggerActionListener);
		loggerWarningItem.addActionListener(loggerActionListener);
		loggerErrorItem.addActionListener(loggerActionListener);
		loggerFatalItem.addActionListener(loggerActionListener);
		loggerOffItem.addActionListener(loggerActionListener);
		menuBar.add(loggerMenu);

		setComponent(mainPanel);
		setMenuBar(menuBar);
		setStatusBar(buildStatusPanel(resourceMap));

		pmnuToolList = new JPopupMenu();
		mnuToolsEnable = new JMenuItem("Enable");
		mnuToolsEnable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ToolSystemVerification systTool;
				ToolDescriptor tool;

				systTool = (ToolSystemVerification) SystemVerificationLoader
						.getTool(SystemVerificationLoader.TOOL_IDX_CHECKSYSTEM);

				try {
					tool = systTool.getToolList().get(
							modelList.getSelectedIndex());
					tool.enableTool(true);
					updateToolList();
				} catch (Exception e2) {

				}
			}
		});
		pmnuToolList.add(mnuToolsEnable);
		mnuToolsDisable = new JMenuItem("Disable");
		mnuToolsDisable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ToolSystemVerification systTool;
				ToolDescriptor tool;

				systTool = (ToolSystemVerification) SystemVerificationLoader
						.getTool(SystemVerificationLoader.TOOL_IDX_CHECKSYSTEM);

				try {
					tool = systTool.getToolList().get(
							modelList.getSelectedIndex());
					tool.enableTool(false);
					updateToolList();
				} catch (Exception e2) {

				}
			}
		});
		pmnuToolList.add(mnuToolsDisable);

		pmnuCmdList = new JPopupMenu();
		mnuCmdsEnable = new JMenuItem("Enable");
		mnuCmdsEnable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int i;
				ToolSystemVerification systTool;
				ToolDescriptor tool;
				Object[] cmdSelection;

				cmdSelection = toolList.getSelectedValues();

				systTool = (ToolSystemVerification) SystemVerificationLoader
						.getTool(SystemVerificationLoader.TOOL_IDX_CHECKSYSTEM);
				tool = systTool.getToolList().get(modelList.getSelectedIndex());

				for (i = 0; i < cmdSelection.length; i++) {
					try {
						tool.getCommand(
								cmdSelection[i].toString().replaceAll(
										"  --DISABLED--", "")).enableCommand(
								true);
					} catch (Exception e2) {

					}
				}
				updateCommandList(modelList.getSelectedIndex());
			}
		});
		pmnuCmdList.add(mnuCmdsEnable);
		mnuCmdsDisable = new JMenuItem("Disable");
		mnuCmdsDisable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int i;
				ToolSystemVerification systTool;
				ToolDescriptor tool;
				Object[] cmdSelection;

				cmdSelection = toolList.getSelectedValues();

				systTool = (ToolSystemVerification) SystemVerificationLoader
						.getTool(SystemVerificationLoader.TOOL_IDX_CHECKSYSTEM);
				tool = systTool.getToolList().get(modelList.getSelectedIndex());

				for (i = 0; i < cmdSelection.length; i++) {
					try {
						tool.getCommand(cmdSelection[i].toString())
								.enableCommand(false);
					} catch (Exception e2) {
					}
				}

				updateCommandList(modelList.getSelectedIndex());
			}
		});
		pmnuCmdList.add(mnuCmdsDisable);

		toolListModel = new DefaultListModel();
		cmdListModel = new DefaultListModel();

		modelList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		modelList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent evt) {
				modelListValueChanged(evt);
			}
		});

		modelList.addMouseListener(new ActionJList(modelList) {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					pmnuToolList.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});

		toolList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				toolListValueChanged(e);
			}
		});

		toolList.addMouseListener(new ActionJList(toolList) {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					pmnuCmdList.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});

		modelList.setModel(toolListModel);
		toolList.setModel(cmdListModel);

	}// </editor-fold>

	// TODO: <Bianca> am Refactoring: NOCH NICHT FERTIG
//	public JTable generateTable(int rowCount, int columnCount,
//			String[] columnIdentifiers) {
//		DefaultTableModel defaultTableModel = new DefaultTableModel();
//		defaultTableModel.setColumnCount(columnCount);
//		defaultTableModel.setRowCount(rowCount);
//		defaultTableModel.setColumnIdentifiers(columnIdentifiers);
//
//		for (int i = 1; i <= rowCount; i++) {
//			for (int j = 1; j <= columnCount; j++) {
//				
//			}
//		}
//
//		// Class[] types = new Class [] {
//		// String.class, String.class, String.class
//		// };
//		// boolean[] canEdit = new boolean [] {
//		// false, false, false
//		// };
//		//
//		// public Class getColumnClass(int columnIndex) {
//		// return types [columnIndex];
//		// }
//		//
//		// public boolean isCellEditable(int rowIndex, int columnIndex) {
//		// return canEdit [columnIndex];
//		// }
//
//		JTable table = new JTable();
//		table.setModel(defaultTableModel);
//		return table;
//	}

	@SuppressWarnings("serial")
	private JTable buildRequirementsTableModel(JTable tableRequirements) {
		tableRequirements.setModel(new DefaultTableModel(new Object[8][4],
				new String[] { "ID", "Description", "Subsystem", "Status" }) {
			@SuppressWarnings("unchecked")
			Class[] types = new Class[] { String.class, String.class,
					String.class, String.class };
			boolean[] canEdit = new boolean[] { false, false, false, false };

			@SuppressWarnings("unchecked")
			public Class getColumnClass(int columnIndex) {
				return types[columnIndex];
			}

			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return canEdit[columnIndex];
			}
		});

		return tableRequirements;
	}

	private JMenu buildHelpMenu(ResourceMap resourceMap) {

		JMenu helpMenu = new JMenu();
		JMenuItem aboutMenuItem = new JMenuItem();

		ActionMap actionMap = Application
				.getInstance(SystemVerificationLoader.class).getContext()
				.getActionMap(SystemVerificationGuiView.class, this);

		helpMenu.setAction(actionMap.get("showAboutBox"));
		helpMenu.setText(resourceMap.getString("helpMenu.text"));
		helpMenu.setName("helpMenu");

		aboutMenuItem.setAction(actionMap.get("showAboutBox"));
		aboutMenuItem.setName("aboutMenuItem");
		helpMenu.add(aboutMenuItem);

		return helpMenu;
	}

	private JMenu buildToolsMenu(ResourceMap resourceMap) {

		JMenu toolsMenu = new JMenu();
		JMenuItem addMenuItem = new JMenuItem();
		JMenuItem removeMenuItem = new JMenuItem();

		toolsMenu.setText(resourceMap.getString("toolsMenu.text"));
		toolsMenu.setActionCommand(resourceMap
				.getString("toolsMenu.actionCommand"));
		toolsMenu.setName("toolsMenu");

		addMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
				InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK));
		addMenuItem.setText(resourceMap.getString("addMenuItem.text"));
		addMenuItem.setActionCommand(resourceMap
				.getString("addMenuItem.actionCommand"));
		addMenuItem.setName("addMenuItem");
		addMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				addMenuItemActionPerformed(evt);
			}
		});
		toolsMenu.add(addMenuItem);

		removeMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,
				InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK));
		removeMenuItem.setText(resourceMap.getString("removeMenuItem.text"));
		removeMenuItem.setActionCommand(resourceMap
				.getString("removeMenuItem.actionCommand"));
		removeMenuItem.setName("removeMenuItem");
		removeMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				removeMenuItemActionPerformed(evt);
			}
		});
		toolsMenu.add(removeMenuItem);

		return toolsMenu;
	}

	private JMenu buildModelMenu(ResourceMap resourceMap) {

		JMenu modelMenu = new JMenu();
		JMenuItem loadMenuItem = new JMenuItem();

		modelMenu.setText(resourceMap.getString("modelMenu.text"));
		modelMenu.setActionCommand(resourceMap
				.getString("modelMenu.actionCommand"));
		modelMenu.setName("modelMenu");

		loadMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L,
				InputEvent.CTRL_MASK));
		loadMenuItem.setText(resourceMap.getString("loadMenuItem.text"));
		loadMenuItem.setActionCommand(resourceMap
				.getString("loadMenuItem.actionCommand"));
		loadMenuItem.setName("loadMenuItem");
		loadMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				loadMenuItemActionPerformed(evt);
			}
		});
		modelMenu.add(loadMenuItem);

		return modelMenu;
	}

	private JMenu buildFileMenu(ResourceMap resourceMap) {

		JMenu fileMenu = new JMenu();
		JMenuItem newMenuItem = new JMenuItem();
		JMenuItem saveMenuItem = new JMenuItem();
		JMenuItem exitMenuItem = new JMenuItem();

		fileMenu.setText(resourceMap.getString("fileMenu.text"));
		fileMenu.setName("fileMenu");

		newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
				InputEvent.CTRL_MASK));
		newMenuItem.setText(resourceMap.getString("newMenuItem.text"));
		newMenuItem.setActionCommand(resourceMap
				.getString("newMenuItem.actionCommand"));
		newMenuItem.setName("newMenuItem");
		newMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				newMenuItemActionPerformed(evt);
			}
		});
		fileMenu.add(newMenuItem);

		saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				InputEvent.CTRL_MASK));
		saveMenuItem.setText(resourceMap.getString("saveMenuItem.text"));
		saveMenuItem.setActionCommand(resourceMap
				.getString("saveMenuItem.actionCommand"));
		saveMenuItem.setName("saveMenuItem");
		saveMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				saveMenuItemActionPerformed(evt);
			}
		});

		fileMenu.add(saveMenuItem);

		exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
				InputEvent.CTRL_MASK));
		exitMenuItem.setText(resourceMap.getString("exitMenuItem.text"));
		exitMenuItem.setName("exitMenuItem");
		exitMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				exitMenuItemActionPerformed(evt);
			}
		});

		fileMenu.add(exitMenuItem);

		return fileMenu;
	}

	private JPanel buildStatusPanel(ResourceMap resourceMap) {

		JPanel statusPanel = new JPanel();

		statusPanel.setName("statusPanel");

		statusPanelSeparator.setName("statusPanelSeparator");

		statusMessageLabel.setName("statusMessageLabel");
		statusAnimationLabel.setHorizontalAlignment(SwingConstants.LEFT);
		statusAnimationLabel.setName("statusAnimationLabel");

		progressBar.setName("progressBar");

		btnAnalyse.setText(resourceMap.getString("btnAnalyse.text"));
		btnAnalyse.setActionCommand(resourceMap
				.getString("btnAnalyse.actionCommand"));
		btnAnalyse.setName("btnAnalyse");
		btnAnalyse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				btnAnalyseActionPerformed(evt);
			}
		});

		GroupLayout statusPanelLayout = new GroupLayout(statusPanel);
		statusPanel.setLayout(statusPanelLayout);
		statusPanelLayout
				.setHorizontalGroup(statusPanelLayout
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								statusPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												statusPanelLayout
														.createParallelGroup(
																Alignment.LEADING)
														.addComponent(
																statusMessageLabel)
														.addComponent(
																btnAnalyse))
										.addGap(18, 18, 18)
										.addComponent(statusPanelSeparator,
												GroupLayout.DEFAULT_SIZE, 454,
												Short.MAX_VALUE)
										.addPreferredGap(
												LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												statusPanelLayout
														.createParallelGroup(
																Alignment.TRAILING)
														.addComponent(
																statusAnimationLabel)
														.addComponent(
																progressBar,
																GroupLayout.PREFERRED_SIZE,
																234,
																GroupLayout.PREFERRED_SIZE))
										.addContainerGap()));
		statusPanelLayout
				.setVerticalGroup(statusPanelLayout
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								statusPanelLayout
										.createSequentialGroup()
										.addComponent(statusPanelSeparator,
												GroupLayout.PREFERRED_SIZE, 2,
												GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												LayoutStyle.ComponentPlacement.RELATED,
												31, Short.MAX_VALUE)
										.addGroup(
												statusPanelLayout
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(
																statusMessageLabel)
														.addComponent(
																statusAnimationLabel))
										.addGap(3, 3, 3))
						.addGroup(
								statusPanelLayout
										.createSequentialGroup()
										.addGroup(
												statusPanelLayout
														.createParallelGroup(
																Alignment.TRAILING,
																false)
														.addComponent(
																progressBar,
																Alignment.LEADING,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.addComponent(
																btnAnalyse,
																Alignment.LEADING,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE))
										.addContainerGap()));

		return statusPanel;
	}

	private void buildMainPanel() {
		GroupLayout mainPanelLayout = new GroupLayout(mainPanel);
		mainPanel.setLayout(mainPanelLayout);
		mainPanelLayout.setAutoCreateContainerGaps(true);
		mainPanelLayout.setAutoCreateGaps(true);

		mainPanelLayout.setHorizontalGroup(mainPanelLayout
				.createSequentialGroup().addComponent(tabMain,
						GroupLayout.DEFAULT_SIZE, 848, Short.MAX_VALUE));

		mainPanelLayout.setVerticalGroup(mainPanelLayout
				.createSequentialGroup().addComponent(tabMain));
	}

	private JPanel buildRequirementsTab(ResourceMap resourceMap) {

		JScrollPane requirementsScrollPane = new JScrollPane();
		JPanel panelRequirements = new JPanel();

		panelRequirements.setName("panelRequirements");

		buildRequirementsTableModel(tableRequirements);

		requirementsScrollPane.setName("jScrollPane7");
		tableRequirements.setName("tableRequirements");
		requirementsScrollPane.setViewportView(tableRequirements);
		tableRequirements
				.getColumnModel()
				.getColumn(0)
				.setHeaderValue(
						resourceMap
								.getString("tableRequirements.columnModel.title0"));
		tableRequirements
				.getColumnModel()
				.getColumn(1)
				.setHeaderValue(
						resourceMap
								.getString("tableRequirements.columnModel.title1"));
		tableRequirements
				.getColumnModel()
				.getColumn(2)
				.setHeaderValue(
						resourceMap
								.getString("tableRequirements.columnModel.title2"));
		tableRequirements
				.getColumnModel()
				.getColumn(3)
				.setHeaderValue(
						resourceMap
								.getString("tableRequirements.columnModel.title3"));

		GroupLayout panelRequirementsLayout = new GroupLayout(panelRequirements);
		panelRequirements.setLayout(panelRequirementsLayout);
		panelRequirementsLayout.setAutoCreateContainerGaps(true);
		panelRequirementsLayout.setAutoCreateGaps(true);

		panelRequirementsLayout.setHorizontalGroup(panelRequirementsLayout
				.createSequentialGroup().addComponent(requirementsScrollPane,
						GroupLayout.DEFAULT_SIZE, 1064, Short.MAX_VALUE));

		panelRequirementsLayout.setVerticalGroup(panelRequirementsLayout
				.createSequentialGroup().addComponent(requirementsScrollPane,
						GroupLayout.DEFAULT_SIZE, 620, Short.MAX_VALUE));

		return panelRequirements;
	}

	private JPanel buildDetailsTab(ResourceMap resourceMap) {

		JScrollPane toolListScrollPane = new JScrollPane();
		JScrollPane selectedCommandsScrollPane = new JScrollPane();
		JScrollPane commandResultScrollPane = new JScrollPane();

		JLabel toolListLabel = new JLabel();
		JLabel selectedCommandsLabel = new JLabel();
		JLabel commandResultsLabel = new JLabel();

		JPanel panelDetails = new JPanel();

		panelDetails.setName("panelDetails");

		selectedCommandsLabel.setText(resourceMap.getString("jLabel5.text"));
		selectedCommandsLabel.setName("jLabel5");

		selectedCommandsScrollPane.setName("jScrollPane4");

		// toolList.setCellRenderer(new CheckListRenderer());
		toolList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		toolList.setName("toolList");
		selectedCommandsScrollPane.setViewportView(toolList);

		commandResultsLabel.setText(resourceMap.getString("jLabel6.text"));
		commandResultsLabel.setName("jLabel6");

		commandResultScrollPane.setName("jScrollPane5");

		pluginResultArea.setColumns(20);
		pluginResultArea.setEditable(false);
		pluginResultArea.setRows(5);
		pluginResultArea.setName("pluginResultArea");
		commandResultScrollPane.setViewportView(pluginResultArea);

		toolListScrollPane.setName("jScrollPane6");

		// modelList.setCellRenderer(new CheckListRenderer());
		modelList.setName("modelList");
		toolListScrollPane.setViewportView(modelList);

		toolListLabel.setText(resourceMap.getString("jLabel7.text"));
		toolListLabel.setName("jLabel7");

		GroupLayout panelDetailsLayout = new GroupLayout(panelDetails);
		panelDetails.setLayout(panelDetailsLayout);
		panelDetailsLayout.setAutoCreateContainerGaps(true);
		panelDetailsLayout.setAutoCreateGaps(true);

		panelDetailsLayout
				.setHorizontalGroup(panelDetailsLayout
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								panelDetailsLayout
										.createSequentialGroup()
										.addGroup(
												panelDetailsLayout
														.createParallelGroup(
																Alignment.LEADING)
														.addComponent(
																toolListLabel)
														.addComponent(
																toolListScrollPane,
																GroupLayout.DEFAULT_SIZE,
																480,
																Short.MAX_VALUE))
										.addGroup(
												panelDetailsLayout
														.createParallelGroup(
																Alignment.LEADING)
														.addComponent(
																selectedCommandsLabel)
														.addComponent(
																selectedCommandsScrollPane,
																GroupLayout.PREFERRED_SIZE,
																320,
																GroupLayout.PREFERRED_SIZE)))
						.addComponent(commandResultsLabel)
						.addComponent(commandResultScrollPane,
								GroupLayout.DEFAULT_SIZE, 812,
								GroupLayout.DEFAULT_SIZE)

				);

		panelDetailsLayout
				.setVerticalGroup(panelDetailsLayout
						.createSequentialGroup()
						.addGroup(
								panelDetailsLayout
										.createParallelGroup(Alignment.LEADING)
										.addGroup(
												panelDetailsLayout
														.createSequentialGroup()
														.addComponent(
																toolListLabel)
														.addComponent(
																toolListScrollPane,
																GroupLayout.DEFAULT_SIZE,
																202,
																Short.MAX_VALUE))
										.addGroup(
												panelDetailsLayout
														.createSequentialGroup()
														.addComponent(
																selectedCommandsLabel)
														.addComponent(
																selectedCommandsScrollPane,
																GroupLayout.DEFAULT_SIZE,
																202,
																Short.MAX_VALUE)))
						.addComponent(commandResultsLabel)
						.addComponent(commandResultScrollPane,
								GroupLayout.PREFERRED_SIZE, 332,
								GroupLayout.PREFERRED_SIZE));

		return panelDetails;
	}

	private JPanel buildOverviewTab(ResourceMap resourceMap) {

		JPanel panelOverview = new JPanel();
		JScrollPane descriptionScrollPane = new JScrollPane();
		JScrollPane resultsScrollPane = new JScrollPane();
		JLabel nameLabel = new JLabel();
		JLabel descriptionLabel = new JLabel();
		JLabel filesLoadedLabel = new JLabel();
		JLabel resultsLabel = new JLabel();

		panelOverview.setName("panelOverview");

		nameLabel.setText(resourceMap.getString("jLabel1.text"));
		nameLabel.setName("jLabel1");

		nameTextField.setEditable(false);
		nameTextField.setText(resourceMap.getString("nameField.text"));
		nameTextField.setName("nameField");

		descriptionLabel.setText(resourceMap.getString("jLabel2.text"));
		descriptionLabel.setName("jLabel2");

		descriptionScrollPane.setName("jScrollPane2");

		descriptionField.setColumns(20);
		descriptionField.setEditable(false);
		descriptionField.setRows(5);
		descriptionField.setName("descriptionField");
		descriptionScrollPane.setViewportView(descriptionField);

		filesLoadedLabel.setText(resourceMap.getString("jLabel3.text"));
		filesLoadedLabel.setName("jLabel3");

		resultsScrollPane.setName("jScrollPane3");

		globalOutputArea.setColumns(20);
		globalOutputArea.setEditable(false);
		globalOutputArea.setRows(5);
		globalOutputArea.setName("jTextArea1");
		resultsScrollPane.setViewportView(globalOutputArea);

		resultsLabel.setText(resourceMap.getString("jLabel4.text"));
		resultsLabel.setName("jLabel4");

		GroupLayout panelOverviewLayout = new GroupLayout(panelOverview);
		panelOverview.setLayout(panelOverviewLayout);
		panelOverviewLayout.setAutoCreateContainerGaps(true);
		panelOverviewLayout.setAutoCreateGaps(true);

		panelOverviewLayout.setHorizontalGroup(panelOverviewLayout
				.createParallelGroup(Alignment.LEADING)
				.addComponent(nameLabel)
				.addComponent(nameTextField, GroupLayout.DEFAULT_SIZE, 812,
						Short.MAX_VALUE)
				.addComponent(descriptionLabel)
				.addComponent(descriptionScrollPane, GroupLayout.DEFAULT_SIZE,
						812, Short.MAX_VALUE)
				.addComponent(filesLoadedLabel)
				.addComponent(filesLoadedScrollPane, GroupLayout.DEFAULT_SIZE,
						812, Short.MAX_VALUE)
				.addComponent(resultsLabel)
				.addComponent(resultsScrollPane, GroupLayout.DEFAULT_SIZE, 812,
						Short.MAX_VALUE));

		panelOverviewLayout.setVerticalGroup(panelOverviewLayout
				.createSequentialGroup()
				.addComponent(nameLabel)
				.addComponent(nameTextField, GroupLayout.PREFERRED_SIZE,
						GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(descriptionLabel)
				.addComponent(descriptionScrollPane,
						GroupLayout.PREFERRED_SIZE, 81,
						GroupLayout.PREFERRED_SIZE)
				.addComponent(filesLoadedLabel)
				.addComponent(filesLoadedScrollPane,
						GroupLayout.PREFERRED_SIZE, 118,
						GroupLayout.PREFERRED_SIZE)
				.addComponent(resultsLabel)
				.addComponent(resultsScrollPane, GroupLayout.PREFERRED_SIZE,
						277, GroupLayout.PREFERRED_SIZE));

		return panelOverview;
	}

	private void saveMenuItemActionPerformed(ActionEvent evt) {
		JFileChooser fileChooser = new JFileChooser();
		ToolSystemVerification systTool;
		FileWriter fstream;
		BufferedWriter out;

		systTool = (ToolSystemVerification) SystemVerificationLoader
				.getTool(SystemVerificationLoader.TOOL_IDX_CHECKSYSTEM);

		int _userSelection = fileChooser
				.showSaveDialog(SystemVerificationLoader.getApplication()
						.getMainFrame());
		if (_userSelection == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			// Create file
			try {
				fstream = new FileWriter(file.getAbsolutePath());
				out = new BufferedWriter(fstream);
				out.write(systTool.getAnalysis().getAllResults());
				// Close the output stream
				out.close();
			} catch (IOException e) {
				System.err.println("Error: " + e.getMessage());
			}
		}
	}

	private void loadMenuItemActionPerformed(ActionEvent evt) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileFilterUml());
		FrameworkBase _framework = SystemVerificationLoader.getFramework();
		ToolSystemVerification systTool = (ToolSystemVerification) SystemVerificationLoader
				.getTool(SystemVerificationLoader.TOOL_IDX_CHECKSYSTEM);

		fileChooser.setCurrentDirectory(new File(_framework.getAppSettings()
				.getModelDirectory()));
		int _userSelection = fileChooser
				.showOpenDialog(SystemVerificationLoader.getApplication()
						.getMainFrame());

		if (_userSelection != JFileChooser.APPROVE_OPTION) {
			return;
		}

		_framework.getAppSettings().setModelDirectory(
				fileChooser.getSelectedFile().getParent());

		if (!_framework.getMdrContainer().isEmpty()) {
			if (JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(
					SystemVerificationLoader.getApplication().getMainFrame(),
					"This will unload the current model. Continue?",
					"Confirmation", JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE)) {
				return;
			}
		}

		Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
		this.getFrame().setCursor(hourglassCursor);

		_framework.getMdrContainer().Empty();
		appendLog("Loading model from file: "
				+ fileChooser.getSelectedFile().getName() + "\n");
		try {
			// speichere benutze Datei
			_framework.setModelFile(fileChooser.getSelectedFile());
			// ----

			_framework.getMdrContainer().loadFromFile(
					fileChooser.getSelectedFile());
			this.appendLogLn("success");

			setUMLModelFileName(fileChooser.getSelectedFile().getName(),
					fileChooser.getSelectedFile().getPath());
			/* parse the model in order to create the tool list */
			systTool.createToolList(this);

			/* update user interface toollist */
			updateToolList();

			tabMain.setSelectedIndex(0);
			tabMain.getModel().setSelectedIndex(0);

		} catch (Exception x) {
			appendLogLn("ERROR: " + x.toString() + ": " + x.getMessage());
		}
		Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
		this.getFrame().setCursor(normalCursor);
	}

	private void newMenuItemActionPerformed(ActionEvent evt) {
		JFrame mainFrame = SystemVerificationLoader.getApplication()
				.getMainFrame();

		ToolSystemVerification systTool = (ToolSystemVerification) SystemVerificationLoader
				.getTool(SystemVerificationLoader.TOOL_IDX_CHECKSYSTEM);

		if (!systTool.getAnalysis().isEmpty()) {
			if (JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(
					SystemVerificationLoader.getApplication().getMainFrame(),
					"This will clear all current results. Continue?",
					"Confirmation", JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE)) {
				return;
			}
		}
		systTool.getAnalysis().clearAll();
		clearAllResults();
		newAnalysisBox = new NewSystemAnalysisGui(mainFrame, true);
		newAnalysisBox.setLocationRelativeTo(mainFrame);
		SystemVerificationLoader.getApplication().show(newAnalysisBox);
	}

	private void exitMenuItemActionPerformed(ActionEvent e) {
		System.exit(0);
	}

	@SuppressWarnings("unchecked")
	private void btnAnalyseActionPerformed(ActionEvent e) {
		ToolDescriptor tool;
		ToolSystemVerification systTool = (ToolSystemVerification) SystemVerificationLoader
				.getTool(SystemVerificationLoader.TOOL_IDX_CHECKSYSTEM);

		if (!SystemVerificationLoader.getFramework().getMdrContainer()
				.isEmpty()) {
			/* clear all previous results */
			for (Iterator<ToolDescriptor> it = systTool.getToolList()
					.iterator(); it.hasNext();) {
				tool = it.next();
				tool.clearAllResults();
			}

			for (Iterator i = systTool.getConsoleCommands(); i.hasNext();) {
				CommandDescriptor _command = (CommandDescriptor) i.next();

				systTool.executeConsoleCommand(_command,
						_command.getParameters(), this, this);
			}
		} else
			appendLog("Cant start an analysis yet. Please load a model first!\n");

		updateUI();
	}

	private void addMenuItemActionPerformed(ActionEvent e) {
		JFrame mainFrame = SystemVerificationLoader.getApplication()
				.getMainFrame();
		addToolBox = new AddToolGui(mainFrame, true);

		addToolBox.setLocationRelativeTo(mainFrame);
		SystemVerificationLoader.getApplication().show(addToolBox);
	}

	private void removeMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		ToolSystemVerification systTool;

		systTool = (ToolSystemVerification) SystemVerificationLoader
				.getTool(SystemVerificationLoader.TOOL_IDX_CHECKSYSTEM);

		if (!modelList.isSelectionEmpty() && modelList.getSelectedIndex() >= 0) {
			systTool.getToolSelector().removeTool(
					modelList.getSelectedValue().toString());
			this.updateToolList();
		}
	}

	private void modelListValueChanged(ListSelectionEvent e) {

		updateCommandList(modelList.getSelectedIndex());

	}

	private void toolListValueChanged(ListSelectionEvent evt) {
		int toolIdx, i;
		Object[] cmdSelection;

		toolIdx = modelList.getSelectedIndex();
		cmdSelection = toolList.getSelectedValues();

		pluginResultArea.setText("");

		for (i = 0; i < cmdSelection.length; i++) {
			updateCommandResult(toolIdx, cmdSelection[i].toString());
		}
	}

	public void updateToolList() {
		Vector<ToolDescriptor> toolList;
		ToolSystemVerification systTool;
		int i;

		toolListModel.clear();

		systTool = (ToolSystemVerification) SystemVerificationLoader
				.getTool(SystemVerificationLoader.TOOL_IDX_CHECKSYSTEM);

		toolList = systTool.getToolList();

		for (i = 0; i < toolList.size(); i++) {
			toolListModel.addElement(toolList.get(i).getToolReference()
					.getToolName()
					+ toolList.get(i).getToolStatusString());
		}

	}

	private void updateCommandList(int toolIdx) {
		ToolSystemVerification systTool;
		ToolDescriptor tool;
		int i;

		cmdListModel.clear();

		systTool = (ToolSystemVerification) SystemVerificationLoader
				.getTool(SystemVerificationLoader.TOOL_IDX_CHECKSYSTEM);

		try {
			tool = systTool.getToolList().get(toolIdx);
			for (i = 0; i < tool.getCommands().size(); i++) {
				cmdListModel.addElement(tool.getCommands().elementAt(i)
						.getCommandName()
						+ tool.getCommands().elementAt(i)
								.getCommandStatusString());
			}
		} catch (Exception e) {
		}

	}

	private void updateCommandResult(int toolIdx, String name) {
		ToolSystemVerification systTool;
		ToolDescriptor tool;

		systTool = (ToolSystemVerification) SystemVerificationLoader
				.getTool(SystemVerificationLoader.TOOL_IDX_CHECKSYSTEM);

		try {
			SystemVerificationLoader.logger.debug("Name: " + name);
			tool = systTool.getToolList().get(toolIdx);
			pluginResultArea.append(tool.getCommand(name).getCommandResult()
					.getResultDetails());

		} catch (Exception e) {
		}

	}

	private void clearAllResults() {
		DefaultTableModel table = (DefaultTableModel) modelsTable.getModel();
		while (table.getRowCount() > 0) {
			table.removeRow(0);
		}
		globalOutputArea.setText("");
		pluginResultArea.setText("");
		cmdListModel.clear();
		toolListModel.clear();
	}

	private void updateUI() {
		int toolIdx, i;
		Object[] cmdSelection;

		toolIdx = modelList.getSelectedIndex();
		cmdSelection = toolList.getSelectedValues();

		pluginResultArea.setText("");

		for (i = 0; i < cmdSelection.length; i++) {
			updateCommandResult(toolIdx, cmdSelection[i].toString());
		}
		updateRequirementList();
	}

	public void appendLog(String str) {
		globalOutputArea.append(str);
	}

	public void appendLogLn(String str) {
		globalOutputArea.append(str + "\n");
	}

	public void appendLogLn() {
		globalOutputArea.append("\n");
	}

	public void write(String _s) {
		globalOutputArea.append(_s);
	}

	public void writeLn(String _s) {
		globalOutputArea.append(_s + "\n");
	}

	public void writeLn() {
		globalOutputArea.append("\n");
	}

	public void setAnalysisName(String strName) {
		nameTextField.setText(strName);
	}

	public void setAnalysisDescription(String strDesc) {
		descriptionField.setText(strDesc);
	}

	public void setAnalysisXMLFileName(String xmlFile, String path) {
		DefaultTableModel table = (DefaultTableModel) modelsTable.getModel();
		table.insertRow(0, new Object[] { xmlFile, path, "loaded" });
	}

	public void setUMLModelFileName(String umlFile, String path) {
		int i, count;
		DefaultTableModel table = (DefaultTableModel) modelsTable.getModel();

		count = table.getRowCount();

		/* show all previously loaded files as unloaded */

		for (i = 0; i < count; i++) {
			if ((table.getValueAt(i, 0) != null)) {
				table.setValueAt("unloaded", i, 2);
			}
		}

		table.insertRow(0, new Object[] { umlFile, path, "loaded" });
	}

	public void updateRequirementList() {
		Requirement req;
		ToolSystemVerification systTool;
		DefaultTableModel table = (DefaultTableModel) tableRequirements
				.getModel();

		table.setRowCount(0);

		systTool = (ToolSystemVerification) SystemVerificationLoader
				.getTool(SystemVerificationLoader.TOOL_IDX_CHECKSYSTEM);

		for (int i = 0; i < systTool.getReqReader().getReqFromXMLFile().size(); i++) {
			req = systTool.getReqReader().getReqFromXMLFile().elementAt(i);
			table.insertRow(
					0,
					new Object[] { req.getIDString(), req.getDescription(),
							req.getSubSystem(), req.getStatus() });
		}

	}

	public void switchToMainTab() {
		tabMain.setSelectedIndex(0);
		tabMain.getModel().setSelectedIndex(0);
	}

	public AppSettings getAppSettings() {
		return appSettings;
	}

	public void setAppSettings(AppSettings appSet) {
		appSettings = appSet;
	}

	public MdrContainer getMdrContainer() {
		return mdrContainer;
	}

	public void setMdrContainer(MdrContainer mdr) {
		mdrContainer = mdr;
	}

	public void showRepositoryMenu() {
		repositoryMenu.setVisible(true);
		repositoryFile = new File("bin" + File.separator + "tum"
				+ File.separator + "umlsec" + File.separator + "viki"
				+ File.separator + "tools" + File.separator + "riskFinder"
				+ File.separator + "repository" + File.separator
				+ "securityRepository.xml");
		if (repositoryFile.exists())
			repositoryMenu.setEnabled(true);
	}

	public void enableRepositoryMenu() {
		repositoryMenu.setEnabled(true);
	}

	public File getRepositoryFile() {
		return repositoryFile;
	}

	public RepositoryHelper getRepositoryHelper() {
		return repositoryhelper;
	}

}
