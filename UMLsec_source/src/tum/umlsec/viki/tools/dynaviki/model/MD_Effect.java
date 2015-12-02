package tum.umlsec.viki.tools.dynaviki.model;

import java.util.Collection;
import java.util.Vector;

import org.omg.uml.behavioralelements.commonbehavior.Action;

import tum.umlsec.viki.framework.ExceptionProgrammLogicError;
import tum.umlsec.viki.tools.dynaviki.model.interfaces.ITreeNodeCollection;
import tum.umlsec.viki.tools.dynaviki.model.scanner.SyntaxNode;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_BASE;

/**
 * @author pasha
 */
public class MD_Effect extends BaseObject implements ITreeNodeCollection {
	
	public MD_Effect(ModelRoot _root, Action _mdrEffect) {
		super(_root);
		if(_mdrEffect == null) {
			throw new ExceptionProgrammLogicError("Creating an empty effect.");
		}
		mdrEffect  = _mdrEffect;
	}

	protected void createNodeStructure() {
		children.removeAllElements();
	}

	public void initialise() {
		createNodeStructure();
	}
	
	public void addExpressionTree(SyntaxNode parse_tree) {
		expressionTree = parse_tree;
		TreeNodeScannerExpression _expressionTreeView = new TreeNodeScannerExpression(getRoot(), parse_tree, null);
		_expressionTreeView.initialise(); 
		((USE_BASE)expressionTree).setTreeViewNode(_expressionTreeView);
			
		children.add(_expressionTreeView);
	}
	
	public boolean isUmlsecLanguage() {
		return mdrEffect.getScript().getLanguage().trim().compareToIgnoreCase("umlsec") == 0; 
	}

	public String getExpression() {
		return mdrEffect.getScript().getBody();
	}



	public String getNodeName() {
		return "EFFECT: " + mdrEffect.getName();
	}

	public String getNodeText() {
		if(mdrEffect.getScript() == null) {
			return "empty";
		}
		
		return "Language: " + mdrEffect.getScript().getLanguage() + "\nBody:\n" + mdrEffect.getScript().getBody();				
	}

	public String getName() {
		return "EFFECT: " + mdrEffect.getName() + " (name)";
	}


	public Collection getChildren() {
		return children;
	}

	public SyntaxNode getExpressionTree() {
		return expressionTree;
	}
	
	
	private Action mdrEffect;
	private Vector children = new Vector();

	private SyntaxNode expressionTree = null;	
}
