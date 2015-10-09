package tum.umlsec.viki.tools.dynaviki.model;


import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import org.omg.uml.behavioralelements.statemachines.FinalState;
import org.omg.uml.behavioralelements.statemachines.Pseudostate;
import org.omg.uml.behavioralelements.statemachines.SimpleState;
import org.omg.uml.behavioralelements.statemachines.StateVertex;
import org.omg.uml.behavioralelements.statemachines.Transition;
import org.omg.uml.foundation.datatypes.PseudostateKind;
import org.omg.uml.foundation.datatypes.PseudostateKindEnum;

import tum.umlsec.viki.tools.dynaviki.model.interfaces.ITreeNodeCollection;

/**
 * @author pasha
 */
public class MD_State extends BaseObject implements ITreeNodeCollection {
	public MD_State(ModelRoot _root, StateVertex _mdrStateVertex, MD_StateMachine _stateMachineModel) {
		super(_root);
		mdrStateVertex = _mdrStateVertex;
		stateMachineModel = _stateMachineModel;

		if(_mdrStateVertex instanceof FinalState) {
			finalState = true;
			return;
		}
		
		if(_mdrStateVertex instanceof SimpleState) {
			return;
		}
		
		if(_mdrStateVertex instanceof Pseudostate) {
			Pseudostate _ps = (Pseudostate)_mdrStateVertex;
			PseudostateKind _pskind = _ps.getKind();
				
			if(_pskind == PseudostateKindEnum.PK_INITIAL) {
				initialState = true;					
			} else {
				// TODO explain where
				throw new ExceptionBadModel("Unknown Pseudostate: " + _pskind.toString());		
			}
		}
	}
	
	protected void createNodeStructure() {
		children.removeAllElements();
		
		children.add(incomingTransitions);
		children.add(outgoingTransitions);
	}
	
	
	public void initialise() {
		createNodeStructure();
						
		for(Iterator _iter = mdrStateVertex.getOutgoing().iterator(); _iter.hasNext();) {
			Transition _transition = (Transition)_iter.next();
			MD_Transition _transitionModel = stateMachineModel.findTransition(_transition);
			outgoingTransitions.add(_transitionModel);
		}	
			
		for(Iterator _iter = mdrStateVertex.getIncoming().iterator(); _iter.hasNext();) {
			Transition _transition = (Transition)_iter.next();
			MD_Transition _transitionModel = stateMachineModel.findTransition(_transition);
			incomingTransitions.add(_transitionModel);
		}	
	} 
	
	
	public Iterator getOutgoingTransactions() {
		return outgoingTransitions.iterator();
	}
	

	public boolean isInitial() {
		return initialState;
	}
	public boolean isFinal() {
		return finalState;
	}
	
	public StateVertex getMdrState() {
		return mdrStateVertex;
	}
	
	public Collection getChildren() {
		return children;
	}

	public String getName() {
		return mdrStateVertex.getName();
	}
	
	public String getNodeName() {
		String _name = mdrStateVertex.getName();
		if(isInitial()) {
			_name += " [initial]";
		}
		if(isFinal()) {
			_name += " [final]";
		}
		
		return _name;
	}

	public String getNodeText() {
		return null;
	}

	public void setPromelaStateIdName(String _stateIdName) {
		promelaStateIdName = _stateIdName;
	}
	
	public String getPromelaStateIdName() {
		return promelaStateIdName;
	}
	
	public MD_StateMachine getStateMachineModel() {
		return stateMachineModel;
	}
	
	
	



	private MD_StateMachine stateMachineModel;
	private StateVertex mdrStateVertex;
	private boolean initialState = false;
	private boolean finalState = false;
	
	private AuxVector incomingTransitions = new AuxVector("Incoming Transitions");
	private AuxVector outgoingTransitions = new AuxVector("Outgoing Transitions");
	
	private Vector children = new Vector();
	
	private String promelaStateIdName;
	
}
