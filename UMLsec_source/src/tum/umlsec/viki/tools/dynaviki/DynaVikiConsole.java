package tum.umlsec.viki.tools.dynaviki;

import java.util.Iterator;

import tum.umlsec.viki.framework.ILogOutput;
import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.toolbase.CommandDescriptor;
import tum.umlsec.viki.framework.toolbase.IVikiToolBase;
import tum.umlsec.viki.framework.toolbase.IVikiToolConsole;

/**
 * @author pasha
 */
public class DynaVikiConsole implements IVikiToolConsole {
//	private final int CID_GENERATEPROMELA = 1;





	public DynaVikiConsole(VikiToolDynamic _baseTool) {
		baseTool = _baseTool;
	}

	public IVikiToolBase getBase() {
		return baseTool;
	}

	VikiToolDynamic baseTool;



 

	public void initialiseConsole() {
		// TODO Auto-generated method stub
		
	}

	public Iterator getConsoleCommands() {
		// TODO Auto-generated method stub
		return null;
	}

	public void executeConsoleCommand(CommandDescriptor _command, Iterator _parameters, ITextOutput _mainOutput, ILogOutput _auxOutput) {
		// TODO Auto-generated method stub
		
	}





//
//
//
//
//
//
//
//
//
//		private void onCommandCollectInfo() {
//			modelRoot.emptyModel();
//			modelRoot.initialise();
//		
//			try {
//				modelRoot.parse();
//			} catch(ExceptionBadModel x) {
//				JOptionPane.showMessageDialog(splitter, x.getMessage(), "Parse Error", JOptionPane.WARNING_MESSAGE);			
//			}
//		
//			treeModel.updateTree();
//		}
//
//		private void onCommandGenerate() {
//		
//			promelaTranslator.eat(modelRoot); 
//		
////			modelRoot.generatePromelaCode();
//		}
//
//
//
//		IVikiFramework mainFrame;
//		IMdrWrapper mdrWrapper;
//
//		JSplitPane splitter = new JSplitPane();
//		JTextArea textArea = new JTextArea();
//	
//		TreeNode root; 
//		TreeModel treeModel;
//		Tree tree;
//
//		ModelRoot modelRoot;
//		PromelaTranslator promelaTranslator = new PromelaTranslator();
//	
//
////		ToolDescriptor toolDescriptor = new ToolDescriptor(true, true, "Dynamic Verifier", "Dynamic Verifier", "Dynamic", "Verifies dynamic model properties");
//
//
}
