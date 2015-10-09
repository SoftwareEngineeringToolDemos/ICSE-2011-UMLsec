package tum.umlsec.viki.tools.dynaviki.model;

import java.util.Collection;
import java.util.Vector;

import org.omg.uml.behavioralelements.statemachines.Guard;

import tum.umlsec.viki.framework.ExceptionProgrammLogicError;
import tum.umlsec.viki.tools.dynaviki.model.interfaces.ITreeNodeCollection;
import tum.umlsec.viki.tools.dynaviki.model.scanner.SyntaxNode;
import tum.umlsec.viki.tools.dynaviki.model.scanner.USE_BASE;

/**
 * @author pasha
 */
public class MD_Guard extends BaseObject implements ITreeNodeCollection {

	public MD_Guard(ModelRoot _root, Guard _mdrGuard) {
		super(_root);
		if(_mdrGuard == null) {
			throw new ExceptionProgrammLogicError("Creating an empty guard.");
		}
		mdrGuard = _mdrGuard;
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
		return mdrGuard.getExpression().getLanguage().trim().compareToIgnoreCase("umlsec") == 0; 
	}
	
	public String getExpression() {
		return mdrGuard.getExpression().getBody(); 
	}

	public String getNodeName() {
		return "GUARD: " + mdrGuard.getName() + " (nodename)";
	}

	public String getNodeText() {
		if(mdrGuard.getExpression() == null) {
			return "empty (TRUE)";
		} 
		return "Language: " + mdrGuard.getExpression().getLanguage() + "\nBody:\n" + mdrGuard.getExpression().getBody();				
	}

	public String getName() {
		return "GUARD: " + mdrGuard.getName() + " (name)";
	}

	public Collection getChildren() {
		return children;
	}

	public SyntaxNode getExpressionTree() {
		return expressionTree;
	}
	
	public Guard getMdrGuard() {
		return mdrGuard;
	}


	private Guard mdrGuard;
	private Vector children = new Vector();

	private SyntaxNode expressionTree;	
	
 }
