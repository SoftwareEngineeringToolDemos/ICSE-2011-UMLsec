package tum.umlsec.viki.tools.dynaviki.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import org.omg.uml.behavioralelements.commonbehavior.ComponentInstance;
import org.omg.uml.behavioralelements.commonbehavior.NodeInstance;
import org.omg.uml.behavioralelements.statemachines.StateMachine;
import org.omg.uml.behavioralelements.statemachines.StateVertex;
import org.omg.uml.behavioralelements.statemachines.Transition;
import org.omg.uml.foundation.core.Stereotype;
import org.omg.uml.foundation.core.UmlAssociation;
import org.omg.uml.foundation.core.UmlClass;
import org.omg.uml.modelmanagement.Model;

import tum.umlsec.viki.framework.ExceptionProgrammLogicError;
import tum.umlsec.viki.framework.ILogOutput;
import tum.umlsec.viki.framework.mdr.IMdrContainer;
import tum.umlsec.viki.tools.dynaviki.model.dataformatgraph.DFGGraph;
import tum.umlsec.viki.tools.dynaviki.model.interfaces.ITreeNodeCollection;

/**
 * @author pasha
 *
 */
public class ModelRoot extends BaseObject implements ITreeNodeCollection {
	public ModelRoot(IMdrContainer _mdrContainer, ILogOutput _log) {
		super(null);
		setRoot(this);
		empty = true;
		mdrContainer = _mdrContainer;
		
		createNodeStructure();
		
		log = _log; 
	}

	public Collection getSubpackagesAll() {				return packagesAll; }
	public Collection getClassesAll() {					return classesAll; }
	public Collection getAssociationsAll() {			return associationsAll; }
	public Collection getObjectsAll() {					return objectsAll; }
	public Collection getNodeInstancesAll() {			return nodeInstancesAll; }
	public Collection getLinksAll() {					return linksAll; }
	public Collection getStateMachinesAll() {			return stateMachinesAll; }
	public Collection getStatesAll() {					return statesAll; }
	public Collection getTransitionsAll() {				return transitionsAll; }
	public Collection getStereotypesAll() {	return stereotypesAll; }
	
	
	protected void createNodeStructure() {
		children.removeAllElements();
		
		children.add(packagesAll);		
		children.add(classesAll);		
		children.add(associationsAll);		
		children.add(objectsAll);
		children.add(componentInstancesAll);		
		children.add(nodeInstancesAll);		
		children.add(linksAll);		
		children.add(stateMachinesAll);	
		children.add(statesAll);	
		children.add(transitionsAll);
		children.add(stereotypesAll);
		
		children.add(dataFormatGraphView);
	}


	
	public void initialise() {
	}



	public void prepareGeneration() {
		collectModelInformation();
	}



	private void collectModelInformation() {		
		root = mdrContainer.getUmlPackage();
		
//	collect children packages
		for(Iterator _it = root.getModelManagement().getUmlPackage().refAllOfClass().iterator(); _it.hasNext();) {
			org.omg.uml.modelmanagement.UmlPackage _newMdrPackage = (org.omg.uml.modelmanagement.UmlPackage)(_it.next());
			MD_Package _packageModel = findPackage(_newMdrPackage);
	   		if(_newMdrPackage.getNamespace() instanceof Model) {
				children.add(_packageModel);
			}
		}

//		collect classes
		for(Iterator _it = root.getCore().getUmlClass().refAllOfClass().iterator(); _it.hasNext();) {
			UmlClass _newMdrClass = (UmlClass)(_it.next());
			MD_Class _classModel = findClass(_newMdrClass);
			if(_newMdrClass.getNamespace() instanceof Model) {
				children.add(_classModel);
			}
		}

//		collect associations
		for(Iterator _it = root.getCore().getUmlAssociation().refAllOfClass().iterator(); _it.hasNext();) {
			UmlAssociation _newMdrAssociation = (UmlAssociation)(_it.next());
			MD_Association _associationModel = findAssociation(_newMdrAssociation);
			if(_newMdrAssociation.getNamespace() instanceof Model) {
				children.add(_associationModel);
			}
		}

//		collect children objecs
		for(Iterator _it = root.getCommonBehavior().getObject().refAllOfClass().iterator(); _it.hasNext();) {
			org.omg.uml.behavioralelements.commonbehavior.Object _newMdrObject = (org.omg.uml.behavioralelements.commonbehavior.Object)(_it.next());
			MD_Object _objectModel = findObject(_newMdrObject);
			if(_newMdrObject.getNamespace() instanceof Model) {
				children.add(_objectModel);
			}
		}
		
//		collect children Component Instances
		for(Iterator _it = root.getCommonBehavior().getComponentInstance().refAllOfClass().iterator(); _it.hasNext();) {
			ComponentInstance _mdrComponentInstance = (ComponentInstance)(_it.next());
			MD_ComponentInstance _componentInstanceModel = findComponentInstance(_mdrComponentInstance);
			if(_mdrComponentInstance.getNamespace() instanceof Model) {
				children.add(_componentInstanceModel);
			}
		}
		
//		collect children Node Instances
		for(Iterator _it = root.getCommonBehavior().getNodeInstance().refAllOfClass().iterator(); _it.hasNext();) {
			NodeInstance _mdrNodeInstance = (NodeInstance)(_it.next());
			MD_NodeInstance _nodeInstanceModel = findNodeInstance(_mdrNodeInstance);
			if(_mdrNodeInstance.getNamespace() instanceof Model) {
				children.add(_nodeInstanceModel);
			}
		}
		
//		collect links
			 for(Iterator _it = root.getCommonBehavior().getLink().refAllOfClass().iterator(); _it.hasNext();) {
				 org.omg.uml.behavioralelements.commonbehavior.Link _newMdrLink = (org.omg.uml.behavioralelements.commonbehavior.Link)(_it.next());
				 MD_Link _linkModel = findLink(_newMdrLink);
				 if(_newMdrLink.getNamespace() instanceof Model) {
					 children.add(_linkModel);
				 }
			 }
		
//		collect stereotypes
			 for(Iterator _it = root.getCore().getStereotype().refAllOfClass().iterator(); _it.hasNext();) {
				 Stereotype _newMdrStereotype = (Stereotype)(_it.next());
				 MD_Stereotype _stereotypeModel = findStereotype(_newMdrStereotype);
			 }
		
		

// NOTE we do not have to collect the 
//		statemachines
//		states
//		transitions
// here because they can't appear at the top level in the model (directly under the root)


// Each Association is added to classes at its both ends
		for (Iterator _iter = associationsAll.iterator(); _iter.hasNext();) {
			MD_Association _associationModel = (MD_Association) _iter.next();
			
			MD_AssociationEnd _ae1 = _associationModel.getAssociationEnd1();
			MD_AssociationEnd _ae2 = _associationModel.getAssociationEnd2();
			
			_ae1.getAttachedClassModel().addAttachedAssociationEnd(_ae1);
			_ae2.getAttachedClassModel().addAttachedAssociationEnd(_ae2);
   		}


//		Each Link is added to objects at both ends
		 for (Iterator _iter = linksAll.iterator(); _iter.hasNext();) {
			 MD_Link _linkModel = (MD_Link) _iter.next();
		
			 MD_LinkEnd _le1 = _linkModel.getLinkEnd1();
			 MD_LinkEnd _le2 = _linkModel.getLinkEnd2();
			 
			 if(_le1.getAttachedObjectType() == MD_LinkEnd.LET_OBJECTS) {
				_le1.getAttachedObjectModel().addAttachedLinkEnd(_le1);
				_le2.getAttachedObjectModel().addAttachedLinkEnd(_le2);
			 } else if(_le1.getAttachedObjectType() == MD_LinkEnd.LET_COMPONENTINSTANCES) {
				throw new ExceptionProgrammLogicError("Link connecting Component Instances is not allowed");
			 } else if(_le1.getAttachedObjectType() == MD_LinkEnd.LET_NODEINSTANCES) {
				_le1.getAttachedNodeInstanceModel().addAttachedLinkEnd(_le1);
				_le2.getAttachedNodeInstanceModel().addAttachedLinkEnd(_le2);
			 } else {
			 	throw new ExceptionProgrammLogicError("Link connecting unknown elements");
			 }
		 }

   		
// each trigger is mapped to a corresponding operation
		for (Iterator iter = transitionsAll.iterator(); iter.hasNext();) {
			MD_Transition _transition = (MD_Transition)iter.next();
			MD_Class _class = _transition.getStateMachineModel().getClassModel();
			MD_Trigger _trigger = _transition.getTrigger();
			if(_trigger != null) {
				_trigger.mapToOperation(_class);
			}
		}
		
// assure that each trigger and each operation have not more then one parameter
// and that parameter count for trigger and corresponding operation are equal
		for(Iterator iter = transitionsAll.iterator(); iter.hasNext();) {
			MD_Transition _transition = (MD_Transition)iter.next();
	   		MD_Class _class = _transition.getStateMachineModel().getClassModel();
	   		MD_Trigger _trigger = _transition.getTrigger();
	   		if(_trigger != null) {
	   			if(_trigger.getParameterCount() > 1) {
					throw new ExceptionBadModel("Unsupported: Mode then one parameter for trigger " + getName());
	   			}
				if(_trigger.getAssociatedOperation().getParameterCount() > 1) {
					throw new ExceptionBadModel("Unsupported: Mode then one parameter for operation " + getName());
				}
				if(_trigger.getParameterCount() != _trigger.getAssociatedOperation().getParameterCount()) {
					throw new ExceptionBadModel("Different parameter count for trigger and operation " + getName());
				}
	   		}
   		}
   		
   		
//		create intruder		
		 intruder = new Intruder(this);
		 intruder.initialise();
		 children.add(intruder);

   		
   		
	}

	public MD_Package findPackage(org.omg.uml.modelmanagement.UmlPackage _p) {
		for(Iterator _it = packagesAll.iterator(); _it.hasNext(); ) {
			MD_Package _pm = (MD_Package)(_it.next());
			if(_pm.getMdrPackage() == _p) {
				return _pm;
			}
		}		
		
		MD_Package _n = new MD_Package(getRoot(), _p);
		packagesAll.add(_n);
		_n.initialise();
		return _n;
	}
	
	public MD_Class findClass(UmlClass _c) {
		for(Iterator _it = classesAll.iterator(); _it.hasNext(); ) {
			MD_Class _cm = (MD_Class)(_it.next());
			if(_cm.getMdrClass() == _c) {
				return _cm;
			}
		}
		
		MD_Class _n = new MD_Class(getRoot(), _c);
		classesAll.add(_n);
		_n.initialise();
		return _n;
	}
	
	public MD_Association findAssociation(UmlAssociation _association) {
		for(Iterator _it = associationsAll.iterator(); _it.hasNext(); ) {
			MD_Association _associationModel = (MD_Association)(_it.next());
			if(_associationModel.getMdrAssociation() == _association) {
				return _associationModel;
			}
		}
		
		MD_Association _newModel = new MD_Association(getRoot(), _association);
		associationsAll.add(_newModel);
		_newModel.initialise();
		return _newModel;
	}
	
	public MD_Object findObject(org.omg.uml.behavioralelements.commonbehavior.Object _o) {
		for(Iterator _it = objectsAll.iterator(); _it.hasNext(); ) {
			MD_Object _om = (MD_Object)(_it.next());
			if(_om.getMdrObject() == _o) {
				return _om;
			}
		}
		
		MD_Object _n = new MD_Object(getRoot(), _o);
		objectsAll.add(_n);
		_n.initialise();
		return _n;
	}
	
	public MD_NodeInstance findNodeInstance(NodeInstance _nodeInstance) {
		for(Iterator _it = nodeInstancesAll.iterator(); _it.hasNext(); ) {
			MD_NodeInstance _nodeInstanceModel = (MD_NodeInstance)(_it.next());
			if(_nodeInstanceModel.getMdrNodeInstance() == _nodeInstance) {
				return _nodeInstanceModel;
			}
		}
		
		MD_NodeInstance _newNodeInstanceModel = new MD_NodeInstance(getRoot(), _nodeInstance);
		nodeInstancesAll.add(_newNodeInstanceModel);
		_newNodeInstanceModel.initialise();
		return _newNodeInstanceModel;
	}
	
	public MD_ComponentInstance findComponentInstance(ComponentInstance _componentInstance) {
		for(Iterator _it = componentInstancesAll.iterator(); _it.hasNext(); ) {
			MD_ComponentInstance _componentInstanceModel = (MD_ComponentInstance)(_it.next());
			if(_componentInstanceModel.getMdrComponentInstance() == _componentInstance) {
				return _componentInstanceModel;
			}
		}
		
		MD_ComponentInstance _newComponentInstanceModel = new MD_ComponentInstance(getRoot(), _componentInstance);
		componentInstancesAll.add(_newComponentInstanceModel);
		_newComponentInstanceModel.initialise();
		return _newComponentInstanceModel;
	}
	
	public MD_Link findLink(org.omg.uml.behavioralelements.commonbehavior.Link _link) {
		for(Iterator _it = linksAll.iterator(); _it.hasNext(); ) {
			MD_Link _linkModel = (MD_Link)(_it.next());
			if(_linkModel.getMdrLink() == _link) {
				return _linkModel;
			}
		}
		
		MD_Link _newLinkModel = new MD_Link(getRoot(), _link);
		linksAll.add(_newLinkModel);
		_newLinkModel.initialise();
		return _newLinkModel;
	}
	
	public MD_Stereotype findStereotype(Stereotype _stereotype) {
		for(Iterator _it = stereotypesAll.iterator(); _it.hasNext(); ) {
			MD_Stereotype _stereotypeModel = (MD_Stereotype)(_it.next());
			if(_stereotypeModel.getMdrStereotype() == _stereotype) {
				return _stereotypeModel;
			}
		}
		
		MD_Stereotype _newStereotypeModel = new MD_Stereotype(getRoot(), _stereotype);
		stereotypesAll.add(_newStereotypeModel);
		_newStereotypeModel.initialise();
		return _newStereotypeModel;
	}
	
	public MD_StateMachine findStateMachine(StateMachine _sm, MD_Class _classModel) {
		for(Iterator _it = stateMachinesAll.iterator(); _it.hasNext(); ) {
			MD_StateMachine _stateMachineModel = (MD_StateMachine)(_it.next());
			if(_stateMachineModel.getMdrStateMachine() == _sm) {
				return _stateMachineModel;
			}
		}
		
		MD_StateMachine _newModel = new MD_StateMachine(getRoot(), _sm, _classModel);
		stateMachinesAll.add(_newModel);
		_newModel.initialise();
		return _newModel;
	}

	public MD_State findState(StateVertex _state, MD_StateMachine _sm) {
		for(Iterator _it = statesAll.iterator(); _it.hasNext(); ) {
			MD_State _stateModel = (MD_State)(_it.next());
			if(_stateModel.getMdrState() == _state) {
				return _stateModel;
			}
		}
		
		MD_State _newModel = new MD_State(getRoot(), _state, _sm);
		statesAll.add(_newModel);
		_sm.addStateToList(_newModel);
		_newModel.initialise();
		return _newModel;
	}

	public MD_Transition findTransition(Transition _t, MD_StateMachine _sm) {
		
		String f = _t.getSource().getName();
		String t = _t.getTarget().getName();
		
		
		for(Iterator _it = transitionsAll.iterator(); _it.hasNext(); ) {
			MD_Transition _transitionModel = (MD_Transition)(_it.next());
			if(_transitionModel.getMdrTransition() == _t) {
				return _transitionModel;
			}
		}
		
		MD_Transition _newModel = new MD_Transition(getRoot(), _t, _sm);
		transitionsAll.add(_newModel);
		_sm.addTransitionToList(_newModel);
		_newModel.initialise();
		return _newModel;
	}










	
	public void emptyModel() {
		packagesAll.removeAllElements();
		classesAll.removeAllElements();
		associationsAll.removeAllElements();
		objectsAll.removeAllElements();
		stateMachinesAll.removeAllElements();
		transitionsAll.removeAllElements();
				
		
		createNodeStructure();
		
		// TODO
		
		empty = true;
	}
	
	public boolean isEmpty() {
		return empty;
	}
	
	public Collection getChildren() {
		return children;
	}

	public String getName() {
		if(empty) {
			return null;
		}
		return root.toString();
	}

	public String getNodeName() {
		if(empty) {
			return "Root Package";
		} else {
			return "Root Package: " + root.toString();
		}
	}

	public String getNodeText() {
		return promelaCode;
	}

//	private void DebugLog(String _msg) {
//		log.appendLogLn(_msg);
//	}

	public void setPromelaCode(String _code) {
		promelaCode = _code;
	}
	
	
	
	
	public void visualiseDataFormatGraph(DFGGraph _graph) {			
		TreeNodeDataFormatGraph _dfg = new TreeNodeDataFormatGraph(getRoot(), _graph.getRootVertex());
		_graph.getRootVertex().setTreeViewNode(_dfg);
		dataFormatGraphView.add(_dfg); 
	 	_dfg.initialise();
	}
	
	public Intruder getIntruder() {
		return intruder;
	}
		
	

	
	private IMdrContainer mdrContainer;
	private org.omg.uml.UmlPackage root;
	private boolean empty;	
	
	private AuxVector packagesAll = new AuxVector("[+] Packages - All");
	private AuxVector classesAll = new AuxVector("[+] Classes - All");
	private AuxVector associationsAll = new AuxVector("[+] Associations - All");
	private AuxVector objectsAll = new AuxVector("[+] Objects - All");
	private AuxVector nodeInstancesAll = new AuxVector("[+] Node Instances - All");
	private AuxVector componentInstancesAll = new AuxVector("[+] Component Instances - All");
	private AuxVector linksAll = new AuxVector("[+] Links - All");
	private AuxVector stateMachinesAll = new AuxVector("[+] State Machines - All");
	private AuxVector statesAll = new AuxVector("[+] States - All");
	private AuxVector transitionsAll = new AuxVector("[+] Transitions - All");
	private AuxVector stereotypesAll = new AuxVector("[+] Stereotypes - All");
	
	private AuxVector dataFormatGraphView = new AuxVector("DataFormatGraph");
	
	
	private Vector children = new Vector();

	private String promelaCode;
	
	private ILogOutput log;
	
	private Intruder intruder;
	
}
