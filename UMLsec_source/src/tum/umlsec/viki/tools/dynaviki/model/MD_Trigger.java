package tum.umlsec.viki.tools.dynaviki.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import org.omg.uml.foundation.core.Parameter;

import tum.umlsec.viki.framework.ExceptionProgrammLogicError;
import tum.umlsec.viki.tools.dynaviki.model.interfaces.ITreeNodeCollection;

/**
 * @author pasha
 */
public class MD_Trigger extends BaseObject implements ITreeNodeCollection {
	/**
	 * @param _root
	 */
	public MD_Trigger(ModelRoot _root, org.omg.uml.behavioralelements.statemachines.Event _mdrTrigger, MD_Transition _parentTransitionModel) {
		super(_root);
		if(_mdrTrigger == null) {
			throw new ExceptionProgrammLogicError("Creating an empty trigger.");
		}
		mdrTrigger = _mdrTrigger;
		parentTransitionModel = _parentTransitionModel;
	}


	protected void createNodeStructure() {
		children.removeAllElements();
		
		children.add(parameters);
	}

	public void initialise() {
		createNodeStructure();
		
		for (Iterator _iter = mdrTrigger.getParameter().iterator(); _iter.hasNext();) {
			Parameter _p = (Parameter)(_iter.next());
			MD_TriggerParameter _tp = new MD_TriggerParameter(getRoot(), this, _p);
			parameters.add(_tp);
			_tp.initialise();
		}
	}


/*
	public boolean isUmlsecLanguage() {
		return mdrTrigger.getScript().getLanguage().trim().compareToIgnoreCase("umlsec") == 0; 
	}

	public String getBody() {
		return mdrEffect.getScript().getBody();
	}
	*/


	public String getName() {
		return mdrTrigger.getName();
	}

	public String getNodeName() {
		return "TRIGGER: " + mdrTrigger.getName() + " (nodename)";
	}

	public String getNodeText() {
		return mdrTrigger.getName();
	}

	public Collection getChildren() {
		return children;
	}
	
	public Collection getParameters() {
		return parameters;
	}
	
	public MD_Operation getAssociatedOperation() {
		return associatedOperation;
	}
	
	/**
	 * locates the corresponding operation within the class
	 * 
	 * @param _class
	 */
	public void mapToOperation(MD_Class _class) {
		for(Iterator iter = _class.getOperations().iterator(); iter.hasNext();) {
			MD_Operation _operation = (MD_Operation)iter.next();
			
			String ss = _operation.getName();
			String hh = getName();
			
			if(_operation.getName().compareTo(getName()) == 0) {
				if(associatedOperation != null) {
					throw new ExceptionBadModel("Duplicate operation name in class " + _class.getName() + ": " + _operation.getName());
				}
				associatedOperation = _operation;
			}
		}
		if(associatedOperation == null) {
			throw new ExceptionBadModel("No corresponding operation for trigger " + getName() + " in class " + _class.getName());
		}
	}

	public int getParameterCount() {
		return parameters.size();
	}
	
	MD_Transition getParentTransitionModel() {
		return parentTransitionModel ;
	}
	



	private AuxVector parameters = new AuxVector("Parameters");
	private Vector children = new Vector();
	
	private org.omg.uml.behavioralelements.statemachines.Event mdrTrigger;
	
	MD_Transition parentTransitionModel;
	MD_Operation associatedOperation = null;
}
