package tum.umlsec.viki.tools.dynaviki;

import java.awt.BorderLayout;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

import tum.umlsec.viki.framework.ExceptionProgrammLogicError;
import tum.umlsec.viki.framework.ILogOutput;
import tum.umlsec.viki.framework.toolbase.CommandDescriptor;
import tum.umlsec.viki.framework.toolbase.IVikiToolBase;
import tum.umlsec.viki.framework.toolbase.IVikiToolGui;
import tum.umlsec.viki.tools.dynaviki.model.ExceptionBadModel;
import tum.umlsec.viki.tools.dynaviki.model.ModelRoot;
import tum.umlsec.viki.tools.dynaviki.model.promela.PromelaTranslator;
import tum.umlsec.viki.tools.dynaviki.tree.Tree;
import tum.umlsec.viki.tools.dynaviki.tree.TreeModel;
import tum.umlsec.viki.tools.dynaviki.tree.TreeNode;

/**
 * @author pasha
 */
public class DynaVikiGui implements IVikiToolGui {
	private final int CID_GENERATE = 0;

	private final int CID_PASS01 = 1;
	private final int CID_PASS02 = 2;
	private final int CID_PASS03 = 3;
	private final int CID_PASS04 = 4;
	private final int CID_PASS05 = 5;


	public DynaVikiGui(VikiToolDynamic _baseTool) {
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
	
	public boolean isEnabledGui() {
		return true;
	}
	
	public void initialiseGui(ILogOutput _log) {
		log = _log;
		
		modelRoot = new ModelRoot(baseTool.getMdrContainer(), log);

		splitter.setOneTouchExpandable(true);

		root = new TreeNode(modelRoot);
		treeModel = new TreeModel(root, textArea);
		tree = new Tree(treeModel);
		tree.addTreeWillExpandListener(treeModel);
		tree.addTreeSelectionListener(treeModel);
		tree.setFont(new java.awt.Font("Arial",fontsize,fontsize));
		textArea.setFont(new java.awt.Font("Arial",fontsize,fontsize));


		
//		_tree.addTreeWillExpandListener(_model);
		
		splitter.setLeftComponent(new JScrollPane(tree));
		splitter.setRightComponent(new JScrollPane(textArea));
		
		commands.add(cmd00);
		
		commands.add(cmd01);
		commands.add(cmd02);
		commands.add(cmd03);
		commands.add(cmd04);
		commands.add(cmd05);
	}

	public Iterator getGuiCommands() {
		return commands.iterator();
	}

	public void executeGuiCommand(CommandDescriptor _command, Iterator _parameters) {
		switch(_command.getId()) {
			case CID_GENERATE:
				onCommandGenerate();
			break;
			
			case CID_PASS01:	onCommandPass01();		break;
			case CID_PASS02:	onCommandPass02();		break;
			case CID_PASS03:	onCommandPass03();		break;
			case CID_PASS04:	onCommandPass04();		break;
			case CID_PASS05:	onCommandPass05();		break;
			
			default:
				throw new ExceptionProgrammLogicError("Undefined command in DynaVikiGui");
		}
	}

	public void setfontsize(int _fontsize) {
		fontsize = _fontsize;
		textArea.setFont(new java.awt.Font("Courier new",fontsize,fontsize));
		tree.setFont(new java.awt.Font("Arial",fontsize,fontsize));
	}

	private boolean onCommandPass01() {
		modelRoot.emptyModel();
		modelRoot.initialise();
		promelaTranslator = new PromelaTranslator(modelRoot, log);
		try {
			promelaTranslator.pass01_parseExpressions(); 
		} catch (ExceptionBadModel x) {
			log.appendLogLn(x.getMessage());
			return false;
		}
		return true;
	}

	private boolean onCommandPass02() {
		try {	
			promelaTranslator.pass02_buildTypeGraph();	
		} catch (ExceptionBadModel x) {
			log.appendLogLn(x.getMessage());
			return false;
		}
		return true;
	}

	private boolean onCommandPass03() {
		try {
			promelaTranslator.pass03_detectVariables(); 
		} catch (ExceptionBadModel x) {
			log.appendLogLn(x.getMessage());
			return false;
		}
		return true;
	}

	private boolean onCommandPass04() {
		try {
			promelaTranslator.pass04_compileInitialValue(); 
		} catch (ExceptionBadModel x) {
			log.appendLogLn(x.getMessage());
			return false;
		}
		return true;
	}

	private boolean onCommandPass05() {
		try {
			promelaTranslator.pass05_findWritableVars(); 
		} catch (ExceptionBadModel x) {
			log.appendLogLn(x.getMessage());
			return false;
		}
		return true;
	}
	
	private boolean onCommandPass06() {
		try {
			promelaTranslator.pass06_findSimpleVariables(); 
		} catch (ExceptionBadModel x) {
			log.appendLogLn(x.getMessage());
			return false;
		}
		return true;
	}
	
	private boolean onCommandPass07() {
		try {
			promelaTranslator.pass07_compileEffects(); 
		} catch (ExceptionBadModel x) {
			log.appendLogLn(x.getMessage());
			return false;
		}
		return true;
	}
	
	private boolean onCommandPass08() {
		try {
			promelaTranslator.pass08_compileTriggers(); 
		} catch (ExceptionBadModel x) {
			log.appendLogLn(x.getMessage());
			return false;
		}
		return true;
	}
	
	private boolean onCommandPass09() {
		try {
			promelaTranslator.pass09_compileGuards(); 
		} catch (ExceptionBadModel x) {
			log.appendLogLn(x.getMessage());
			return false;
		}
		return true;
	}
	
	private boolean onCommandPass10() {
		try {
			promelaTranslator.pass10_buildObjectGraph(); 
		} catch (ExceptionBadModel x) {
			log.appendLogLn(x.getMessage());
			return false;
		}
		return true;
	}
	
	private boolean onCommandPass11() {
		try {
			promelaTranslator.pass11_mapAssociationsToLinks(); 
		} catch (ExceptionBadModel x) {
			log.appendLogLn(x.getMessage());
			return false;
		}
		return true;
	}
	
	private boolean onCommandPass12() {
		try {
			promelaTranslator.pass12_mapLinkLogicalToPhysical(); 
		} catch (ExceptionBadModel x) {
			log.appendLogLn(x.getMessage());
			return false;
		}
		return true;
	}
	
	private boolean onCommandPass13() {
		try {
			promelaTranslator.pass13(); 
		} catch (ExceptionBadModel x) {
			log.appendLogLn(x.getMessage());
			return false;
		}
		return true;
	}
	
	private boolean onCommandPass14() {
		try {
			promelaTranslator.pass14_searchSecretAttribute(); 
		} catch (ExceptionBadModel x) {
			log.appendLogLn(x.getMessage());
			return false;
		}
		return true;
	}
	
	private boolean onCommandPass15() {
		try {
			promelaTranslator.pass15(); 
		} catch (ExceptionBadModel x) {
			log.appendLogLn(x.getMessage());
			return false;
		}
		return true;
	}
	
	private boolean onCommandPass16() {
		try {
			promelaTranslator.pass16(); 
		} catch (ExceptionBadModel x) {
			log.appendLogLn(x.getMessage());
			return false;
		}
		return true;
	}
	
	
	
	

	// TODO allow and perform next step only if previous was successfull 

	private void onCommandGenerate() {
		if(!onCommandPass01()) {	treeModel.updateTree(); return;	}
		if(!onCommandPass02()) {	treeModel.updateTree(); return;	}
		if(!onCommandPass03()) {	treeModel.updateTree(); return;	}
		if(!onCommandPass04()) {	treeModel.updateTree(); return;	}
		if(!onCommandPass05()) {	treeModel.updateTree(); return;	}
		if(!onCommandPass06()) {	treeModel.updateTree(); return;	}
		if(!onCommandPass07()) {	treeModel.updateTree(); return;	}
		if(!onCommandPass08()) {	treeModel.updateTree(); return;	}
		if(!onCommandPass09()) {	treeModel.updateTree(); return;	}
		if(!onCommandPass10()) {	treeModel.updateTree(); return;	}
		if(!onCommandPass11()) {	treeModel.updateTree(); return;	}
		if(!onCommandPass12()) {	treeModel.updateTree(); return;	}
		if(!onCommandPass13()) {	treeModel.updateTree(); return;	}
		if(!onCommandPass14()) {	treeModel.updateTree(); return;	}
		if(!onCommandPass15()) {	treeModel.updateTree(); return;	}
		if(!onCommandPass16()) {	treeModel.updateTree(); return;	}
		
		
		
		treeModel.updateTree();
		
  	
	}



	VikiToolDynamic baseTool;
	ILogOutput log;

	ModelRoot modelRoot;
	PromelaTranslator promelaTranslator;
	
	TreeNode root; 
	TreeModel treeModel;
	Tree tree;

	JSplitPane splitter = new JSplitPane();
	JTextArea textArea = new JTextArea();


	Vector emptyParametersVector = new Vector();
	
	CommandDescriptor cmd00 = CommandDescriptor.CommandDescriptorConsole(CID_GENERATE, "Generate", "Generate PROMELA code", true, emptyParametersVector); 
	 
	CommandDescriptor cmd01 = CommandDescriptor.CommandDescriptorConsole(CID_PASS01, "ParseExpressions", "Parse all expressions", true, emptyParametersVector);
	CommandDescriptor cmd02 = CommandDescriptor.CommandDescriptorConsole(CID_PASS02, "TypeGraph", "Build Type Graph", true, emptyParametersVector);
	CommandDescriptor cmd03 = CommandDescriptor.CommandDescriptorConsole(CID_PASS03, "MatchVariables", "Match Variables to Model Elements", true, emptyParametersVector);
	CommandDescriptor cmd04 = CommandDescriptor.CommandDescriptorConsole(CID_PASS04, "3", "3", true, emptyParametersVector);
	CommandDescriptor cmd05 = CommandDescriptor.CommandDescriptorConsole(CID_PASS05, "4", "4", true, emptyParametersVector);
	
	 
	Vector commands = new Vector();
	int fontsize = 16;

}
