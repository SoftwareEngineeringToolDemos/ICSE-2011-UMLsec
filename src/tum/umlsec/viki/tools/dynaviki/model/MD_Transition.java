package tum.umlsec.viki.tools.dynaviki.model;

import java.util.Collection;
import java.util.Vector;

import org.omg.uml.behavioralelements.statemachines.StateVertex;
import org.omg.uml.behavioralelements.statemachines.Transition;

import tum.umlsec.viki.tools.dynaviki.model.interfaces.ITreeNodeCollection;

/**
 * @author pasha
 */
public class MD_Transition extends BaseObject implements ITreeNodeCollection {
	public MD_Transition(ModelRoot _root, Transition _mdrTransition, MD_StateMachine _stateMachineModel) {
		super(_root);
		mdrTransition = _mdrTransition;
		stateMachineModel = _stateMachineModel;
	}

	protected void createNodeStructure() {
		children.removeAllElements();
	}

	public void initialise() {
		createNodeStructure();
		
		children.add(stateMachineModel.findState((StateVertex)mdrTransition.getSource()));
		children.add(stateMachineModel.findState((StateVertex)mdrTransition.getTarget()));
				
// TRIGGER
		if(mdrTransition.getTrigger() != null) {
			trigger = new MD_Trigger(getRoot(), mdrTransition.getTrigger(), this);
			children.add(trigger);
			trigger.initialise();
		}

// GUARD
		if(mdrTransition.getGuard() != null) {
			guard = new MD_Guard(getRoot(), mdrTransition.getGuard());
			children.add(guard);
			guard.initialise();
		}

// EFFECT
		if(mdrTransition.getEffect() != null) {
			effect = new MD_Effect(getRoot(), mdrTransition.getEffect());
			children.add(effect);
			effect.initialise();
		}
	}
	
	
	
	public MD_Trigger getTrigger() {
		return trigger;
	}
	
	public MD_Guard getGuardModel() {
		return guard;
	}
	
	public MD_Effect getEffectModel() {
		return effect;
	}
	
	public boolean getGuardProcessed() {
		return guard != null && guard.getExpressionTree() != null;
	}
	
	public boolean getEffectProcessed() {
		return effect != null && effect.getExpressionTree() != null;
	}
	


	public String getName() {
		return mdrTransition.getName();
	}

	public String getNodeName() {
		String _name = mdrTransition.getName();
		if(_name == null || _name.length() == 0) {
			_name = getSourceState().getName() + " to " + getTargetState().getName(); 
		}
		return _name;
	}
	
	public String getNodeText() {
		return null;
	}

	public Transition getMdrTransition() {
		return mdrTransition;
	}

	public Collection getChildren() {
		return children;
	}
	
	public MD_StateMachine getStateMachineModel() {
		return stateMachineModel;
	}
	
	public MD_State getSourceState() {
		return stateMachineModel.findState((StateVertex)mdrTransition.getSource());
	}
	
	public MD_State getTargetState() {
		return stateMachineModel.findState((StateVertex)mdrTransition.getTarget());
	}
	


	private MD_Trigger trigger = null;
	private MD_Guard guard = null;
	private MD_Effect effect = null;


	private Transition mdrTransition;
	private MD_StateMachine stateMachineModel;
	
	private Vector children = new Vector();
}

