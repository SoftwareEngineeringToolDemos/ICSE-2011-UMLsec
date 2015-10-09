package tum.umlsec.viki.tools.dynaviki.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import org.omg.uml.behavioralelements.statemachines.CompositeState;
import org.omg.uml.behavioralelements.statemachines.StateMachine;
import org.omg.uml.behavioralelements.statemachines.StateVertex;
import org.omg.uml.behavioralelements.statemachines.Transition;

import tum.umlsec.viki.tools.dynaviki.model.interfaces.ITreeNodeCollection;

/**
 * @author pasha
 */
public class MD_StateMachine extends BaseObject implements ITreeNodeCollection {

	public MD_StateMachine(ModelRoot _root, StateMachine _mdrStateMachine, MD_Class _classModel) {
		super(_root);
		mdrStateMachine = _mdrStateMachine;
		classModel = _classModel;
	}

	protected void createNodeStructure() {
		children.removeAllElements();
		
		children.add(states);		
		children.add(transitions);		
	}


	public Collection getStates() {
		return states;	
	}
	
	public Collection getTransitions() {
		return transitions;	
	}


	public Collection getChildren() {
		return children;
	}

	public String getName() {
		return mdrStateMachine.getName();
	}
	
	public String getClassName() {
		return mdrStateMachine.getContext().getName(); 
	}

	public void initialise() {
		createNodeStructure();				
				
		StateVertex _s = mdrStateMachine.getTop();
		
		if(!(_s instanceof CompositeState)) {
			throw new ExceptionBadModel("The topmost State in the Statechart for the class " + getClassName() + " has wrong type.");		
		}
		
		CompositeState _top = (CompositeState)_s;
		
		boolean foundStart = false;
		for (Iterator _iter = _top.getSubvertex().iterator(); _iter.hasNext();) {
			StateVertex _sv =(StateVertex)_iter.next();
			MD_State _stateModel = findState(_sv);
			if(foundStart && _stateModel.isInitial()) {
				throw new ExceptionBadModel("More then one initial state in the Statemachine for the Class " + getClassName());
			}
			foundStart |= _stateModel.isInitial();
		}
		
		// TODO check that 1) we have start and 2) only one
		
		
		
// add the transition to the state machine		
//		for (Iterator _iter = mdrStateMachine.getTransitions().iterator(); _iter.hasNext();) {
//			Transition _transition = (Transition) _iter.next();
//			MD_Transition _transitionModel = findTransition(_transition);
//			transitions.add(_transitionModel);
//		}
	

//		auxParseCollection(_top.getClientDependency(), "ClientDependency");
//		auxParseCollection(_top.getComment(), "Comment"); 
//		auxParseCollection(_top.getConstraint(), "Constraint"); 
//		CompositeState _cs01 = _top.getContainer();
//		auxParseCollection(_top.getDeferrableEvent(), "Deferrable events"); 
//		Action _do = _top.getDoActivity();
//		Action _entry = _top.getEntry();  				
//		Action _exit = _top.getExit();
//		auxParseCollection(_top.getIncoming(), "Incoming"); 
//		auxParseCollection(_top.getInternalTransition(), "InternalTransition");
//		auxParseCollection(_top.getOutgoing(), "Outgoing");
//		auxParseCollection(_top.getSourceFlow(), "SourceFlow");
//		auxParseCollection(_top.getStereotype(), "Stereotype");
//		auxParseCollection(_top.getSubvertex(), "Subvertex");
//		auxParseCollection(_top.getTargetFlow(), "Targetflow");
	}


	public MD_State findState(StateVertex _state) {
		return getRoot().findState(_state, this);
	}

	public MD_Transition findTransition(Transition _transition) {
		return getRoot().findTransition(_transition, this);
	}

	public StateMachine getMdrStateMachine() {
		return mdrStateMachine;
	}

	public String getNodeName() {
		return "StateMachine: " + getName();
	}

	public String getNodeText() {
		return null;
	}
	
	public MD_Class getClassModel() {
		return classModel;
	}

	public void addStateToList(MD_State _newModel) {
		states.add(_newModel);
	}

	public void addTransitionToList(MD_Transition _newModel) {
		transitions.add(_newModel);
	}



	
	// TODO
	private AuxVector states = new AuxVector("states");
	private AuxVector transitions = new AuxVector("transitions");


	private StateMachine mdrStateMachine;
	private MD_Class classModel;
	private Vector children = new Vector();
}
