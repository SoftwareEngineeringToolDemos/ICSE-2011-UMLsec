package tum.umlsec.viki.tools.dynaviki.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import org.omg.uml.behavioralelements.statemachines.StateMachine;
import org.omg.uml.foundation.core.Attribute;
import org.omg.uml.foundation.core.Operation;
import org.omg.uml.foundation.core.UmlClass;

import tum.umlsec.viki.tools.dynaviki.model.interfaces.ITreeNodeCollection;


/**
 * @author pasha
 */
public class MD_Class extends BaseObject implements ITreeNodeCollection {
	public MD_Class(ModelRoot _root, UmlClass _umlClass) {
		super(_root); 
		mdrClass = _umlClass;
	}
	
	protected void createNodeStructure() {
		children.removeAllElements();
		
		children.add(operations);
		children.add(attributes);
		children.add(associationEnds);
	}
	
	public Collection getAttributes() {
		return attributes; 
	}
	public Collection getOperations() {
		return operations; 
	}
	public Collection getAssociationEnds() {
		return associationEnds; 
	}
	
	public void initialise() {
		createNodeStructure();


		for (Iterator _iter = mdrClass.getFeature().iterator(); _iter.hasNext();) {
			Object _x = _iter.next();
			
			if(_x instanceof Operation) {
				Operation _operation = (Operation)_x;
				MD_Operation _newModel = new MD_Operation(getRoot(), _operation, this);
				operations.add(_newModel);
				_newModel.initialise();
			}
			
			if(_x instanceof Attribute) {
				Attribute _attribute = (Attribute)_x;
				MD_Attribute _newModel = new MD_Attribute(getRoot(), _attribute, this);
				attributes.add(_newModel);
				_newModel.initialise();
			}
		}

		for(Iterator _it = mdrClass.getOwnedElement().iterator(); _it.hasNext(); ) {
			Object _x = _it.next();
			
			if(_x instanceof StateMachine) {
				stateMachine = getRoot().findStateMachine((StateMachine)_x, this);
				children.add(stateMachine);
				continue;
			}
		}
	}
	
	public MD_Attribute getAttribute(String _name) {
		for (Iterator iter = attributes.iterator(); iter.hasNext();) {
			MD_Attribute _attribute = (MD_Attribute) iter.next();
			if(_attribute.getName().compareTo(_name) == 0) {
				return _attribute;
			}
		}
		return null;
	}
	
	public MD_Operation getOperation(String _name) {
		for (Iterator iter = operations.iterator(); iter.hasNext();) {
			MD_Operation _operation = (MD_Operation) iter.next();
			if(_operation.getName().compareTo(_name) == 0) {
				return _operation;
			}
		}
		return null;
	}
	
	public void addAttachedAssociationEnd(MD_AssociationEnd _associationEndModel) {
		associationEnds.add(_associationEndModel);
	}


	public Collection getChildren() {
		return children;
	}

	public String getName() {
		return mdrClass.getName();
	}

	public String getNodeName() {
		return "Class: " + mdrClass.getName();
	}

	public String getNodeText() {
		return promelaCode;
	}
	
	public UmlClass getMdrClass() {
		return mdrClass;
	}
	
	public MD_StateMachine getStateMachine() {
		return stateMachine;
	}

	public void setPromelaCode(String _code) {
		promelaCode = _code;
	}
	
//	public SymbolTable getSymbolTable() {
//		return symbolTable;
//	}
//	
//	public void setSymbolTable(SymbolTable _symbolTable) {
//		symbolTable = _symbolTable;
//	}
	
	
	
	

	private UmlClass mdrClass;
	private MD_StateMachine stateMachine;
	
	private AuxVector attributes = new AuxVector("Attributes");
	private AuxVector operations = new AuxVector("Operations");
	private AuxVector associationEnds = new AuxVector("AssociationEnds");

	private Vector children = new Vector();
	
	private String promelaCode;
//	private SymbolTable symbolTable;
}



