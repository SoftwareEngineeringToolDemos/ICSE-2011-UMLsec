/**
 * Call for UML model parser for activity diagrams:
 *  - saves and returns all possible flows
 *  - each fork has its own corresponding join
 *  - tagged values are not considered
 */

package tum.umlsec.viki.tools.activityparser;

import java.util.Iterator;
import java.util.Vector;

import org.omg.uml.UmlPackage;
import org.omg.uml.behavioralelements.activitygraphs.ActivityGraphClass;
import org.omg.uml.behavioralelements.activitygraphs.ActivityGraphsPackage;
import org.omg.uml.behavioralelements.statemachines.FinalState;
import org.omg.uml.behavioralelements.statemachines.FinalStateClass;
import org.omg.uml.behavioralelements.statemachines.Pseudostate;
import org.omg.uml.behavioralelements.statemachines.PseudostateClass;
import org.omg.uml.behavioralelements.statemachines.StateMachinesPackage;
import org.omg.uml.behavioralelements.statemachines.StateVertex;
import org.omg.uml.behavioralelements.statemachines.Transition;
import org.omg.uml.behavioralelements.statemachines.TransitionClass;
import org.omg.uml.foundation.core.CorePackage;

import tum.umlsec.viki.framework.ExceptionProgrammLogicError;
import tum.umlsec.viki.framework.ILogOutput;
import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.mdr.IMdrContainer;
import tum.umlsec.viki.framework.toolbase.CommandParameterDescriptor;

/**
 * wird diese Klasse überhaupt genutzt?
 * @author 
 *
 */
public class MdrActivityParser {
	UmlPackage root;
	CorePackage corePackage;
	ActivityGraphsPackage activityPackage;
	StateMachinesPackage stateMachines;
	TransitionClass transitionClasses;
	Pseudostate initialState;
	/**
	 * main result
	 */
	Vector res;
	/**
	 * result between synchronization bars
	 */
	Vector syncres;
	
	public MdrActivityParser(){
	}

	public void check(IMdrContainer _mdrContainer, Iterator _parameters,
			ITextOutput _mainOutput, ILogOutput _auxOutput) {
		root = _mdrContainer.getUmlPackage();
		corePackage = root.getCore();
		activityPackage = (ActivityGraphsPackage) root.getActivityGraphs();
		stateMachines = (StateMachinesPackage) activityPackage
				.getStateMachines();
		transitionClasses = (TransitionClass) stateMachines.getTransition();

		// ActivityGraphClass activityClasses =
		// activityPackage.getActivityGraph();
		PseudostateClass pseudostateClasses = (PseudostateClass) stateMachines
				.getPseudostate();

		int _iterations = 4;
		boolean _foundIterations = false;
		for (; _parameters.hasNext();) {
			CommandParameterDescriptor _parameter = (CommandParameterDescriptor) _parameters
					.next();
			if (_parameter.getId() == ToolActivity.CPID_DEPTH) {
				_iterations = _parameter.getAsInteger();
				_foundIterations = true;
			}
		}
		if (_foundIterations == false) {
			throw new ExceptionProgrammLogicError("Required parameter missing");
		}

		// searching for initial state
		initialState = null;
		for (Iterator it_act_I = pseudostateClasses.refAllOfClass().iterator(); it_act_I
				.hasNext();) {
			initialState = (Pseudostate) it_act_I.next();
			if (initialState.getKind().toString().equals("pk_initial")) {
				break;
			}
		}

		// initializing main result
		res = new Vector();

		// preparing call value for tmpres
		Vector temp = new Vector();
		temp.addElement((StateVertex) initialState);

		// calling recursive with initial state
		recursive((StateVertex) initialState, new Vector(), new Vector(),
				new Vector(temp), _iterations, false);

		// printing main result
		printres(_mainOutput);

		_mainOutput.writeLn("Parsing finished.");
	}

	/**
	 * @param _source
	 *            current state
	 * @param _before
	 *            all computed flows at current state
	 * @param _v
	 *            all computed flows following last fork at current state
	 * @param _tmpres
	 *            all computed flows for current arm in-between synchronisation
	 *            bars with a state as fist element
	 * @param _iterations
	 *            maximum number of activities
	 * @param _inFork
	 *            true in-between synchronization bars
	 */
	private Vector recursive(StateVertex _source, Vector _before, Vector _v,
			Vector _tmpres, int _iterations, boolean _inFork) {

		// checking for final state
		if (isFinalState(_source.getName())) {
			Vector temp = cpVec(_before);
			// adding name of final state to before
			addActionState(temp, _source.getName());
			// adding all computed flows at current state to res
			addVec(res, temp);
		}

		// checking for choice state
		else if (isChoiceState(_source.getName())) {
			// getting all outgoing transitions
			Iterator it_Tran2 = _source.getOutgoing().iterator();
			while (it_Tran2.hasNext()) {
				StateVertex ctarget = (StateVertex) ((Transition) it_Tran2
						.next()).getTarget();
				// calling recursive for all successor states
				_tmpres = recursive(ctarget, _before, _v, _tmpres, _iterations,
						_inFork);
			}
		}
		// checking for junction state
		else if (isJunctionState(_source.getName())) {
			// getting outgoing transition
			Iterator it_Tran2 = _source.getOutgoing().iterator();
			StateVertex jtarget = (StateVertex) ((Transition) it_Tran2.next())
					.getTarget();
			// calling recursive for successor state
			_tmpres = recursive(jtarget, _before, _v, _tmpres, _iterations,
					_inFork);
		}
		// checking for fork state
		else if (isForkState(_source.getName())) {
			// saving value of inFork
			boolean ftemp = _inFork;
			_inFork = true;
			// saving value of tmpres
			Vector stmpres = cpTmpRes(_tmpres);
			int l;
			l = _iterations - minLength(_before);
			// getting all outgoing transitions
			Iterator it_Tran2 = _source.getOutgoing().iterator();
			// ftarget: first arm
			StateVertex ftarget = (StateVertex) ((Transition) it_Tran2.next())
					.getTarget();
			Vector temp = new Vector();
			StateVertex jtarget = (StateVertex) initialState;
			temp.add(jtarget);
			// calling recursive for first arm and resetting v and tempres
			_tmpres = recursive(ftarget, _before, new Vector(new Vector()),
					temp, _iterations, _inFork);
			// saving last join of first arm in jtarget if first arm is
			// completed
			if ((StateVertex) _tmpres.firstElement() != (StateVertex) initialState) {
				jtarget = (StateVertex) _tmpres.firstElement();
			}
			_tmpres.remove(0);
			// copying flows of first arm to syncres
			syncres = cpVec(_tmpres);
			Vector tsyncres = cpVec(syncres);
			// remaining arms
			while (it_Tran2.hasNext()) {
				// ftarget: next arm
				ftarget = (StateVertex) ((Transition) it_Tran2.next())
						.getTarget();
				// calling recursive for next arm and resetting v and tempres
				temp = new Vector();
				temp.add((StateVertex) initialState);
				_tmpres = recursive(ftarget, _before, new Vector(new Vector()),
						temp, _iterations, _inFork);
				// saving last join of next arm in jtarget if next arm is
				// completed
				if ((StateVertex) _tmpres.firstElement() != (StateVertex) initialState) {
					jtarget = (StateVertex) _tmpres.firstElement();
				}
				_tmpres.remove(0);
				// combining flows of this arm with the flows of syncres
				syncres = cpVec(tsyncres);
				combine(_tmpres, l);
				tsyncres = cpVec(syncres);
			}
			// concatenating combined flows to v
			_tmpres = cpVec(syncres);
			concatenate(_v, _iterations);
			_v = cpVec(syncres);
			// concatenating combined flows to before
			syncres = _tmpres;
			concatenate(_before, _iterations);
			_before = cpVec(syncres);
			// restoring value of tmpres
			_tmpres = stmpres;
			// restoring value of inFork
			_inFork = ftemp;
			// clearing v if not in-between synchronization bars
			if (_inFork == false) {
				_v = new Vector();
			}
			// checking length of before
			if (itCheck(_before, _v, _tmpres, _iterations, _inFork) == false) {
				// at least one current flow is shorter than the maximum number
				// of activities
				it_Tran2 = jtarget.getOutgoing().iterator();
				jtarget = (StateVertex) ((Transition) it_Tran2.next())
						.getTarget();
				// calling recursive for successor state
				_tmpres = recursive(jtarget, _before, _v, _tmpres, _iterations,
						_inFork);
			}
		}
		// checking for join state
		else if (isJoinState(_source.getName())) {
			// adding all computed flows following last fork at current state to
			// tmpres
			addVec(_tmpres, _v);
			// saving last join state
			_tmpres.set(0, _source);
		}
		// action states
		else {
			Vector temp1 = cpVec(_before);
			Vector temp2 = cpVec(_v);
			// adding name of current state to v
			if (_inFork == true) {
				addActionState(temp2, _source.getName());
			}
			// adding name of current state to before
			addActionState(temp1, _source.getName());
			// checking length of before
			if (itCheck(temp1, temp2, _tmpres, _iterations, _inFork) == false) {
				// at least one current flow is shorter than the maximum number
				// of activities
				Iterator it_Tran2 = _source.getOutgoing().iterator();
				StateVertex atarget = (StateVertex) ((Transition) it_Tran2
						.next()).getTarget();
				// calling recursive for successor state
				_tmpres = recursive(atarget, temp1, temp2, _tmpres,
						_iterations, _inFork);
			}
		}
		return _tmpres;
	}

	/**
	 * method to check for choice states
	 * 
	 * @param stateName
	 * @return
	 */
	private boolean isChoiceState(String stateName) {
		PseudostateClass pseudostateClasses = (PseudostateClass) stateMachines
				.getPseudostate();
		for (Iterator it_act_P = pseudostateClasses.refAllOfClass().iterator(); it_act_P
				.hasNext();) {
			Pseudostate choiceState = (Pseudostate) it_act_P.next();
			if (choiceState.getName() == stateName) {
				if (choiceState.getKind().toString().equals("pk_choice")) {
					return (true);
				} else {
					return (false);
				}
			}
		}
		return (false);
	}

	/**
	 * method to check for junction states
	 * 
	 * @param stateName
	 * @return
	 */
	private boolean isJunctionState(String stateName) {
		PseudostateClass pseudostateClasses = (PseudostateClass) stateMachines
				.getPseudostate();
		for (Iterator it_act_P = pseudostateClasses.refAllOfClass().iterator(); it_act_P
				.hasNext();) {
			Pseudostate junctionState = (Pseudostate) it_act_P.next();
			if (junctionState.getName() == stateName) {
				if (junctionState.getKind().toString().equals("pk_junction")) {
					return (true);
				} else {
					return (false);
				}
			}
		}
		return (false);
	}

	/**
	 * method to check for fork states
	 * 
	 * @param stateName
	 * @return
	 */
	private boolean isForkState(String stateName) {
		PseudostateClass pseudostateClasses = (PseudostateClass) stateMachines
				.getPseudostate();
		for (Iterator it_act_P = pseudostateClasses.refAllOfClass().iterator(); it_act_P
				.hasNext();) {
			Pseudostate forkState = (Pseudostate) it_act_P.next();
			if (forkState.getName() == stateName) {
				if (forkState.getKind().toString().equals("pk_fork")) {
					return (true);
				} else {
					return (false);
				}
			}
		}
		return (false);
	}

	/**
	 * method to check for join states
	 * 
	 * @param stateName
	 * @return
	 */
	private boolean isJoinState(String stateName) {
		PseudostateClass pseudostateClasses = (PseudostateClass) stateMachines
				.getPseudostate();
		for (Iterator it_act_P = pseudostateClasses.refAllOfClass().iterator(); it_act_P
				.hasNext();) {
			Pseudostate joinState = (Pseudostate) it_act_P.next();
			if (joinState.getName() == stateName) {
				if (joinState.getKind().toString().equals("pk_join")) {
					return (true);
				} else {
					return (false);
				}
			}
		}
		return (false);
	}

	/**
	 * method to check for final states
	 * 
	 * @param stateName
	 * @return
	 */
	private boolean isFinalState(String stateName) {
		FinalStateClass finalstateClasses = (FinalStateClass) stateMachines
				.getFinalState();
		for (Iterator it_act_F = finalstateClasses.refAllOfClass().iterator(); it_act_F
				.hasNext();) {
			FinalState finalState = (FinalState) it_act_F.next();
			if (finalState.getName().equals(stateName.toString())) {
				return (true);
			}
		}
		return (false);
	}

	/**
	 * method to add the name of a state to flows
	 * 
	 * @param v
	 * @param name
	 */
	private void addActionState(Vector v, String name) {
		if (v.isEmpty() == false) {
			for (int i = 0; i < v.size(); i++) {
				((Vector) v.elementAt(i)).addElement(name);
			}
		} else {
			Vector temp = new Vector();
			temp.addElement(name);
			v.addElement(temp);
		}
	}

	/**
	 * method to add flows to flows
	 * 
	 * @param v1
	 * @param v2
	 */
	private void addVec(Vector v1, Vector v2) {
		if (v2.isEmpty() == false) {
			if (v1.isEmpty() == false) {
				v1.addAll(v1.size(), v2);
			} else {
				v1.addAll(v2);
			}
		}
	}

	/**
	 * method to check length of elements of before
	 * 
	 * @param before
	 * @param v
	 * @param tmpres
	 * @param i
	 * @param inFork
	 * @return
	 */
	private boolean itCheck(Vector before, Vector v, Vector tmpres, int i,
			boolean inFork) {
		int j = 0;
		while (j < before.size()) {
			// checking if current flow of before reaches maximum number of
			// activities
			if (((Vector) before.elementAt(j)).size() >= i) {
				// adding current flow of before to main result if not
				// in-between synchronization bars
				if (inFork == false) {
					res.add((Vector) before.elementAt(j));
				}
				// removing current flow from before
				before.remove(j);
			} else
				j++;
		}
		if (before.isEmpty()) {
			// adding all computed flows following last fork at current state to
			// tmpres if in-between synchronization bars
			if (inFork == true) {
				addVec(tmpres, v);
			}
			return true;
		} else
			return false;
	}

	/**
	 * method to copy flows
	 * 
	 * @param v
	 * @return
	 */
	private Vector cpVec(Vector v) {
		Vector temp1 = new Vector();
		for (int i = 0; i < v.size(); i++) {
			Vector temp2 = new Vector((Vector) v.elementAt(i));
			temp1.add(temp2);
		}
		return (temp1);
	}

	/**
	 * method to copy tmpres
	 * 
	 * @param v
	 * @return
	 */
	private Vector cpTmpRes(Vector v) {
		Vector temp1 = new Vector();
		StateVertex stateVertex = (StateVertex) v.firstElement();
		temp1.add(stateVertex);
		for (int i = 1; i < v.size(); i++) {
			Vector temp2 = new Vector((Vector) v.elementAt(i));
			temp1.add(temp2);
		}
		return (temp1);
	}

	/**
	 * method to combine two arms in-between synchronization bars
	 * 
	 * @param v
	 * @param l
	 */
	private void combine(Vector v, int l) {
		// saving value of syncres
		Vector temp1 = cpVec(syncres);
		// clearing syncres
		syncres = new Vector();
		Vector temp2 = new Vector();
		int k;
		for (int i = 0; i < temp1.size(); i++) {
			for (int j = 0; j < v.size(); j++) {
				// calling recursivecomb with each flow of syncres and each flow
				// of v
				k = l;
				if (((Vector) temp1.elementAt(i)).size()
						+ ((Vector) v.elementAt(j)).size() < k) {
					k = ((Vector) temp1.elementAt(i)).size()
							+ ((Vector) v.elementAt(j)).size();
				}
				recursivecomb((Vector) temp1.elementAt(i),
						(Vector) v.elementAt(j), 0, 0, new Vector(), k);
				// adding resulting flows of interleaving to temp2
				temp2.addAll(syncres);
				// clearing syncres
				syncres = new Vector();
			}
		}
		// allocating all resulting flows to syncres
		syncres = temp2;
	}

	/**
	 * method to interleave two concurrent flows
	 * 
	 * @param v1
	 *            : flow
	 * @param v2
	 *            : flow
	 * @param pos1
	 *            : position in v1
	 * @param pos2
	 *            : position in v2
	 * @param temp1
	 *            : current interleaved flow
	 * @param l
	 */
	private void recursivecomb(Vector v1, Vector v2, int pos1, int pos2,
			Vector temp1, int l) {
		// adding current interleaved flow to syncres if length of temp1 is
		// equal to the sum of the length of v1 and v2
		if (pos1 + pos2 == l) {
			Vector temp2 = new Vector(temp1);
			syncres.addElement(temp2);
		} else if (pos1 == v1.size()) {
			// adding the rest of the elements of v2 to temp1
			Vector temp2 = new Vector(temp1);
			for (int i = pos2; i < pos2 + l - temp1.size(); i++) {
				temp2.addElement(v2.elementAt(i));
			}
			// adding current interleaved flow to syncres
			syncres.addElement(temp2);
		} else if (pos2 == v2.size()) {
			// adding the rest of the elements of v1 to temp1
			Vector temp2 = new Vector(temp1);
			for (int i = pos1; i < pos1 + l - temp1.size(); i++) {
				temp2.addElement(v1.elementAt(i));
			}
			// adding current interleaved flow to syncres
			syncres.addElement(temp2);
		} else {
			temp1.add(pos1 + pos2, v1.elementAt(pos1));
			// calling recursivecomb with current element of v1 and increased
			// pos1
			recursivecomb(v1, v2, pos1 + 1, pos2, temp1, l);
			temp1.set(pos1 + pos2, v2.elementAt(pos2));
			// calling recursivecomb with current element of v2 and increased
			// pos2
			recursivecomb(v1, v2, pos1, pos2 + 1, temp1, l);
			temp1.remove(temp1.size() - 1);
		}
	}

	/**
	 * method to concatenate flows
	 * 
	 * @param v
	 * @param s
	 */
	private void concatenate(Vector v, int s) {
		Vector temp1 = new Vector();
		if (v.isEmpty() == false) {
			if (syncres.isEmpty() == false) {
				for (int i = 0; i < v.size(); i++) {
					for (int j = 0; j < syncres.size(); j++) {
						// concatenating each flow of v with each flow of
						// syncres and adding to temp1
						Vector temp2 = new Vector();
						temp2.addAll((Vector) v.elementAt(i));
						temp2.addAll((Vector) syncres.elementAt(j));
						temp1.add(temp2);
					}
				}
				Vector temp3 = new Vector();
				for (int i = 0; i < temp1.size(); i++) {
					// checking if current flow is greater than s
					if (((Vector) temp1.elementAt(i)).size() > s) {
						// removing all elements of current flow greater than s
						Vector temp2 = new Vector((Vector) temp1.elementAt(i));
						// checking if current flow is greater than s
						if (((Vector) temp1.elementAt(i)).size() > s) {
							temp2.subList(s, temp2.size()).clear();
						}
						// adding current flow to temp3 if not already included
						if (isIncluded(temp3, temp2) == false) {
							temp3.add(temp2);
						}
					} else {
						Vector temp2 = new Vector((Vector) temp1.elementAt(i));
						temp3.add(temp2);
					}
				}
				// allocating all resulting flows to syncres
				syncres = new Vector(temp3);
			} else {
				syncres = new Vector(v);
			}
		}
	}

	/**
	 * method to check if one flow v2 is included in a list of flows v1
	 * 
	 * @param v1
	 * @param v2
	 * @return
	 */
	private boolean isIncluded(Vector v1, Vector v2) {
		for (int i = 0; i < v1.size(); i++) {
			if (((Vector) v1.elementAt(i)).equals(v2)) {
				return (true);
			}
		}
		return (false);
	}

	/**
	 * method to ...
	 * 
	 * @param v
	 * @return
	 */
	private int minLength(Vector v) {
		int l = 0;
		if (v.isEmpty() == false) {
			l = ((Vector) v.firstElement()).size();
			for (int i = 1; i < v.size(); i++) {
				if (((Vector) v.elementAt(i)).size() < l) {
					l = ((Vector) v.elementAt(i)).size();
				}
			}
		}
		return l;
	}

	/**
	 * method to print the main result
	 * 
	 * @param _textOutput
	 */
	private void printres(ITextOutput _textOutput) {
		for (int i = 0; i < res.size(); i++) {
			_textOutput.writeLn(String.valueOf(((Vector) res.elementAt(i))
					.size()));
			// writeLn("<br>");
			_textOutput.writeLn(" activities: ");
			// writeLn("<br>");
			for (int j = 0; j < ((Vector) res.elementAt(i)).size(); j++) {
				_textOutput.writeLn((String) (((Vector) res.elementAt(i))
						.elementAt(j)) + " ");
				// writeLn("<br>");
			}
			// System.out.println();
		}
		_textOutput.writeLn("Number of possibilities: ");
		_textOutput.writeLn(String.valueOf(res.size()));
		// writeLn("<br>");
		// System.out.println(res.size());
	}

}