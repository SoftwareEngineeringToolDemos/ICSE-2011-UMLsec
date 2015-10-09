package tum.umlsec.viki.tools.activityparser;

import java.util.Iterator;
import java.util.Vector;

import org.omg.uml.behavioralelements.statemachines.Pseudostate;
import org.omg.uml.behavioralelements.statemachines.StateVertex;
import org.omg.uml.behavioralelements.statemachines.Transition;

import tum.umlsec.viki.framework.ITextOutput;

/**
 * class to save the current flow of one activity diagram
 * 
 *  @author (refactoring) Bianca Batsch, bianca.batsch@isst.fraunhofer.de
 * 
 */
public class Flow {

	/**
	 * hierarchy of fork states in case of synchronization
	 */
	private static Vector children = new Vector();
	private static Vector parents = new Vector();
	/**
	 * states of current flow
	 */
	private Vector flow;
	/**
	 * all transitions that can be continued with in the next step
	 */
	private Vector alternatives;
	/**
	 * variable values
	 */
	private ExpressionParser variables;

	public Flow(StateVertex initialState, Transition t) {
		flow = new Vector();
		flow.add(initialState);
		alternatives = new Vector();
		alternatives.add(new Alternatives(initialState, t));
		variables = new ExpressionParser();
	}

	public Flow(Flow f) {
		flow = new Vector(f.flow);
		alternatives = new Vector();
		for (int i = 0; i < f.alternatives.size(); i++) {
			Alternatives temp = new Alternatives(
					(Alternatives) f.alternatives.elementAt(i));
			alternatives.add(temp);
		}
		variables = new ExpressionParser(f.variables);
	}

	/**
	 * method to print the facts of the current flow of the activity diagram
	 * 
	 * @param _textOutput
	 */
	public void printflow(ITextOutput _textOutput) {
		_textOutput.write(" Flow: ");
		for (int i = 0; i < flow.size() - 1; i++) {
			_textOutput.write(((StateVertex) flow.elementAt(i)).getName());
			_textOutput.write(", ");
		}
		if (flow.size() - 1 >= 0) {
			_textOutput.write(((StateVertex) flow.elementAt(flow.size() - 1))
					.getName());
			_textOutput.write(";");
		}
		_textOutput.writeLn();
		_textOutput.write(" Variable values: ");
		variables.printVars(_textOutput);
		_textOutput.writeLn();
	}

	/**
	 * method to calculate all possible flows after an additional step
	 * 
	 * @return
	 */
	public Vector nextflows() {
		// all possible flows after an additional step
		Vector next = new Vector();
		// checking if current state is a final state
		if (isFinalState((StateVertex) flow.lastElement()) == true) {
			next.add(new Flow(this));
			return next;
		}
		// checking all synchronization zones
		for (int i = 0; i < alternatives.size(); i++) {
			StateVertex fork = ((Alternatives) alternatives.elementAt(i)).fork;
			// processing all transitions with no choice state as their source
			for (int j = 0; j < ((Alternatives) alternatives.elementAt(i)).normal
					.size(); j++) {
				Flow ftemp = new Flow(this);
				Transition trans = ((Transition) ((Alternatives) alternatives
						.elementAt(i)).normal.elementAt(j));
				// checking if transition can be chosen
				if (ftemp.chkTransition(trans) == true) {
					StateVertex state = trans.getTarget();
					ftemp.flow.add(state);
					// checking for final state
					if (isFinalState(state) == true) {
						// deleting alternatives
						ftemp.alternatives = new Vector();
						// checking for junction state
						// } else if (isJunctionState(state) == true) {
						// // substituting current transition with transition
						// following the junction state
						// Iterator it_Tran = state.getOutgoing().iterator();
						// Transition ntrans = (Transition) it_Tran.next();
						// ((Alternatives)ftemp.alternatives.elementAt(i)).normal.setElementAt(ntrans,
						// j);
						// checking for choice state
					} else if ((isChoiceState(state) == true)
							|| (isJunctionState(state) == true)) {
						// deleting current transition from alternatives
						// adding transitions following choice state to
						// alternatives
						Iterator it_Tran = state.getOutgoing().iterator();
						Vector ctemp = new Vector();
						while (it_Tran.hasNext()) {
							Transition ntrans = (Transition) it_Tran.next();
							ctemp.add(ntrans);
						}
						((Alternatives) ftemp.alternatives.elementAt(i)).normal
								.remove(j);
						((Alternatives) ftemp.alternatives.elementAt(i)).choices
								.add(ctemp);
						// checking for fork state
					} else if (isForkState(state) == true) {
						// deleting current transition from alternatives
						// adding an additional synchronization zone
						// adding transitions following fork states to
						// alternatives in the new synchronization zone
						Iterator it_Tran = state.getOutgoing().iterator();
						Vector ctemp = new Vector();
						while (it_Tran.hasNext()) {
							Transition ntrans = (Transition) it_Tran.next();
							ctemp.add(ntrans);
						}
						((Alternatives) ftemp.alternatives.elementAt(i)).normal
								.remove(j);
						addFork(state, fork);
						Alternatives atemp = new Alternatives(state, ctemp);
						ftemp.alternatives.add(atemp);
						// checking for join state
					} else if (isJoinState(state) == true) {
						// deleting current transition from alternatives
						// in case of no more elements in the synchronization
						// zone and no other synchronization zone embedded:
						// - deleting current synchronization zone
						// - adding transition after fork state to alternatives
						// of the parent synchronization zone
						((Alternatives) ftemp.alternatives.elementAt(i)).normal
								.remove(j);
						if (((Alternatives) ftemp.alternatives.elementAt(i)).normal
								.isEmpty()
								&& ((Alternatives) ftemp.alternatives
										.elementAt(i)).choices.isEmpty()) {
							if (ftemp.hasChild(fork) == false) {
								ftemp.alternatives.remove(i);
								Iterator it_Tran = state.getOutgoing()
										.iterator();
								Transition ntrans = (Transition) it_Tran.next();
								((Alternatives) ftemp.alternatives
										.elementAt(ftemp.getIndexOfParent(fork))).normal
										.add(ntrans);
							}
						}
						// following state is no pseudo state
					} else {
						// substituting current transition with transition
						// following the state
						Iterator it_Tran = state.getOutgoing().iterator();
						Transition ntrans = (Transition) it_Tran.next();
						((Alternatives) ftemp.alternatives.elementAt(i)).normal
								.setElementAt(ntrans, j);
					}
					next.add(ftemp);
					// transition can not be chosen
				} else {
					// adding source of transition to current flow
					ftemp.flow.add(trans.getSource());
					next.add(ftemp);
				}
			}
			// processing all transitions with choice state as their source
			for (int j = 0; j < ((Alternatives) alternatives.elementAt(i)).choices
					.size(); j++) {
				Vector ctrans = ((Vector) ((Alternatives) alternatives
						.elementAt(i)).choices.elementAt(j));
//				// indicating if one of the transitions can be chosen
//				boolean cont = false;
				// processing all outgoing transitions
				for (int k = 0; k < ctrans.size(); k++) {
					Flow ftemp = new Flow(this);
					Transition trans = (Transition) ctrans.elementAt(k);
					// checking if transition can be chosen
					if (ftemp.chkTransition(trans) == true) {
						StateVertex state = trans.getTarget();
						ftemp.flow.add(state);
						// checking for final state
						if (isFinalState(state) == true) {
							// deleting alternatives
							ftemp.alternatives = new Vector();
							// checking for junction state
						} else if (isJunctionState(state) == true) {
							// deleting all current transitions from
							// alternatives
							// adding transition after junction state to
							// alternatives
							Iterator it_Tran = state.getOutgoing().iterator();
							Transition ntrans = (Transition) it_Tran.next();
							((Alternatives) ftemp.alternatives.elementAt(i)).choices
									.remove(j);
							((Alternatives) ftemp.alternatives.elementAt(i)).normal
									.add(ntrans);
							// checking for choice state
						} else if (isChoiceState(state) == true) {
							// substituting all current transitions with all
							// transitions after choice state
							Iterator it_Tran = state.getOutgoing().iterator();
							Vector ctemp = new Vector();
							while (it_Tran.hasNext()) {
								Transition ntrans = (Transition) it_Tran.next();
								ctemp.add(ntrans);
							}
							((Alternatives) ftemp.alternatives.elementAt(i)).choices
									.setElementAt(ctemp, j);
							// checking for fork state
						} else if (isForkState(state) == true) {
							// deleting all current transitions from
							// alternatives
							// adding an additional synchronization zone
							// adding transitions following fork states to
							// alternatives in the new synchronization zone
							Iterator it_Tran = state.getOutgoing().iterator();
							Vector ctemp = new Vector();
							while (it_Tran.hasNext()) {
								Transition ntrans = (Transition) it_Tran.next();
								ctemp.add(ntrans);
							}
							((Alternatives) ftemp.alternatives.elementAt(i)).choices
									.remove(j);
							addFork(state, fork);
							Alternatives atemp = new Alternatives(state, ctemp);
							ftemp.alternatives.add(atemp);
							// checking for join state
						} else if (isJoinState(state) == true) {
							// deleting all current transitions from
							// alternatives
							// in case of no more elements in the
							// synchronization zone and no other synchronization
							// zone embedded:
							// - deleting current synchronization zone
							// - adding transition after fork state to
							// alternatives of the parent synchronization zone
							((Alternatives) ftemp.alternatives.elementAt(i)).choices
									.remove(j);
							if (((Alternatives) ftemp.alternatives.elementAt(i)).normal
									.isEmpty()
									&& ((Alternatives) ftemp.alternatives
											.elementAt(i)).choices.isEmpty()) {
								if (ftemp.hasChild(fork) == false) {
									ftemp.alternatives.remove(i);
									Iterator it_Tran = state.getOutgoing()
											.iterator();
									Transition ntrans = (Transition) it_Tran
											.next();
									((Alternatives) ftemp.alternatives
											.elementAt(ftemp
													.getIndexOfParent(fork))).normal
											.add(ntrans);
								}
							}
							// following state is no pseudo state
						} else {
							// deleting all current transitions from
							// alternatives
							// adding transition after state to alternatives
							Iterator it_Tran = state.getOutgoing().iterator();
							Transition ntrans = (Transition) it_Tran.next();
							((Alternatives) ftemp.alternatives.elementAt(i)).choices
									.remove(j);
							((Alternatives) ftemp.alternatives.elementAt(i)).normal
									.add(ntrans);
						}
						next.add(ftemp);
						// if one transition can be chosen no other transitions
						// are checked
						// break;
					}
				}
			}
		}
		return next;
	}

	/**
	 * method to check if two flows of one activity diagram are equal
	 * 
	 * @param f
	 * @return
	 */
	public boolean isEqual(Flow f) {
		if (flow.size() != f.flow.size())
			return false;
		for (int i = 0; i < flow.size(); i++) {
			if (((StateVertex) flow.elementAt(i)) != ((StateVertex) f.flow
					.elementAt(i)))
				return false;
		}
		if (variables.isEqual(f.variables) == false)
			return false;
		return true;
	}

	/**
	 * method to check for choice states
	 * 
	 * @param state
	 * @return
	 */
	private static boolean isChoiceState(StateVertex state) {
		if (state.getClass().getName().endsWith("Pseudostate$Impl")) {
			if (((Pseudostate) state).getKind().toString().equals("pk_choice")) {
				return true;
			} else {
				return false;
			}
		} else
			return false;
	}

	/**
	 * method to check for junction states
	 * 
	 * @param state
	 * @return
	 */
	private static boolean isJunctionState(StateVertex state) {
		if (state.getClass().getName().endsWith("Pseudostate$Impl")) {
			if (((Pseudostate) state).getKind().toString().equals("pk_junction")) {
				return true;
			} else {
				return false;
			}
		} else
			return false;
	}

	/**
	 * method to check for fork states
	 * 
	 * @param state
	 * @return
	 */
	private static boolean isForkState(StateVertex state) {
		if (state.getClass().getName().endsWith("Pseudostate$Impl")) {
			if (((Pseudostate) state).getKind().toString().equals("pk_fork")) {
				return true;
			} else {
				return false;
			}
		} else
			return false;
	}

	/**
	 * method to check for join states
	 * 
	 * @param state
	 * @return
	 */
	private static boolean isJoinState(StateVertex state) {
		if (state.getClass().getName().endsWith("Pseudostate$Impl")) {
			if (((Pseudostate) state).getKind().toString().equals("pk_join")) {
				return true;
			} else {
				return false;
			}
		} else
			return false;
	}

	/**
	 * method to check for final states
	 * 
	 * @param state
	 * @return
	 */
	private static boolean isFinalState(StateVertex state) {
		if (state.getClass().getName().endsWith("FinalState$Impl")) {
			return true;
		} else
			return false;
	}

	/**
	 * method to check if the condition of the transition tr is true
	 * 
	 * @param tr
	 * @return
	 */
	private boolean chkTransition(Transition tr) {
		boolean res = true;
		if (tr != null) {
			// checking condition if present
			if (tr.getGuard() != null)
				res = variables.condition(tr.getGuard().getExpression()
						.getBody());
			// processing effect if present
			if ((res == true) && (tr.getEffect() != null))
				variables.assignment(tr.getEffect().getScript().getBody());
		}
		return res;
	}

	/**
	 * method to extend the hierarchy with another fork state
	 * 
	 * @param fork
	 * @param parent
	 */
	private static void addFork(StateVertex fork, StateVertex parent) {
		if (children.indexOf(fork) == -1) {
			children.add(fork);
			parents.add(parent);
		}
	}

	/**
	 * method to get the parent of the fork state
	 * 
	 * @param fork
	 * @return
	 */
	private static StateVertex getParent(StateVertex fork) {
		if (children.indexOf(fork) == -1)
			return null;
		else if (parents.elementAt(children.indexOf(fork)) == null)
			return null;
		else
			return (StateVertex) parents.elementAt(children.indexOf(fork));
	}

	/**
	 * method to check for children
	 * 
	 * @param parent
	 * @return
	 */
	private boolean hasChild(StateVertex parent) {
		if (parents.indexOf(parent) == -1)
			return false;
		else {
			StateVertex child = null;
			if (children.elementAt(parents.indexOf(parent)) != null)
				child = (StateVertex) children.elementAt(parents
						.indexOf(parent));
			for (int i = 0; i < alternatives.size(); i++) {
				if (((Alternatives) alternatives.elementAt(i)).fork == child)
					return true;
			}
			return false;
		}
	}

	/**
	 * method to get the index of the parent of the fork state
	 * 
	 * @param child
	 * @return
	 */
	private int getIndexOfParent(StateVertex child) {
		StateVertex parent = null;
		if (parents.elementAt(children.indexOf(child)) != null)
			parent = (StateVertex) parents.elementAt(children.indexOf(child));
		for (int i = 0; i < alternatives.size(); i++) {
			if (((Alternatives) alternatives.elementAt(i)).fork == parent)
				return i;
		}
		return -1;
	}

}