/**
 * UML model parser for activity diagrams:
 *  - saves and returns all possible flows
 *  - only one UML diagram (must be an activity diagram) can be stored in the MDR repository
 *  - pseudo states are included in the resulting flows
 *  - effects and guards for transitions are supported
 *  - conditions and assignments are evaluated with integer variables
 *  - each fork must have its own corresponding join
 */

package tum.umlsec.viki.tools.activityparser;

import java.util.Iterator;
import java.util.Vector;

import org.omg.uml.UmlPackage;
import org.omg.uml.behavioralelements.activitygraphs.ActivityGraphClass;
import org.omg.uml.behavioralelements.activitygraphs.ActivityGraphsPackage;
import org.omg.uml.behavioralelements.statemachines.Pseudostate;
import org.omg.uml.behavioralelements.statemachines.PseudostateClass;
import org.omg.uml.behavioralelements.statemachines.StateMachinesPackage;
import org.omg.uml.behavioralelements.statemachines.StateVertex;
import org.omg.uml.behavioralelements.statemachines.Transition;
import org.omg.uml.behavioralelements.statemachines.TransitionClass;
import org.omg.uml.foundation.core.CorePackage;

import tum.umlsec.viki.framework.ExceptionProgrammLogicError;
import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.mdr.IMdrContainer;
import tum.umlsec.viki.framework.toolbase.CommandParameterDescriptor;

/**
 * class to calculate all possible flows with the given length
 * 
 * @author (refactoring) Bianca Batsch, bianca.batsch@isst.fraunhofer.de
 * 
 */
public class ActivityParser {

	/**
	 * method to print all possible flows with the given length
	 * 
	 * @param _mdrContainer
	 * @param _parameters
	 * @param _mainOutput
	 */
	public void check(IMdrContainer _mdrContainer, Iterator _parameters,
			ITextOutput _mainOutput) {
		
		// <Bianca> unused code: UmlPackage root =
		// _mdrContainer.getUmlPackage();
		
		// <Bianca> unused code: CorePackage corePackage = root.getCore();
		
		// <Bianca> unused code: ActivityGraphsPackage activityPackage =
		// (ActivityGraphsPackage) root.getActivityGraphs();
		
		// <Bianca> unused code: StateMachinesPackage stateMachines =
		// (StateMachinesPackage) activityPackage.getStateMachines();
		
		// <Bianca> unused code: TransitionClass transitionClasses =
		// (TransitionClass) stateMachines.getTransition();
		
		// <Bianca> unused code: ActivityGraphClass activityClasses =
		// activityPackage.getActivityGraph();

		int _iterations = extractParameters(_parameters,
				ToolActivity.CPID_DEPTH);

		Vector flows = searchInitialStates(_mdrContainer);
		_mainOutput
				.writeLn("The following possibilities show the different flows of the given activity diagram.");
		Vector expandedFlows = expandFlows(_iterations, flows);
		eliminateDoubles(expandedFlows);
		printflows(expandedFlows, _mainOutput);
		_mainOutput.writeLn();
		_mainOutput.writeLn();
		_mainOutput.writeLn("Parsing finished.");
	}

	private Vector expandFlows(int _iterations, Vector flows) {
		for (int j = 0; j < (_iterations - 1); j++) {
			flows = allnextflows(flows);
		}
		return flows;
	}

	/**
	 * searching for initial state
	 * 
	 * @param _mdrContainer
	 * @return flows
	 */
	private Vector searchInitialStates(IMdrContainer _mdrContainer) {
		Vector flows = new Vector();

		Pseudostate initialState = null;

		PseudostateClass pseudostateClasses = (PseudostateClass) _mdrContainer
				.getUmlPackage().getActivityGraphs().getStateMachines()
				.getPseudostate();
		Iterator allPseudostates = pseudostateClasses.refAllOfClass()
				.iterator();
		while(allPseudostates.hasNext()){
			initialState = (Pseudostate) allPseudostates.next();
			if (initialState.getKind().toString().equals("pk_initial")) {
				Iterator it_Tran = initialState.getOutgoing().iterator();
				Transition ntrans = (Transition) it_Tran.next();
				flows.add(new Flow((StateVertex) initialState, ntrans));
			}
		}
		return flows;
	}

	private int extractParameters(Iterator _parameters, int parameterId) {
		int _iterations = 1;
		boolean _foundIterations = false;
		// Lade den CPID_DEPTH WERT
		for (; _parameters.hasNext();) { // while(hasNext())
			CommandParameterDescriptor _parameter = (CommandParameterDescriptor) _parameters
					.next();
			if (_parameter.getId() == parameterId) {
				_iterations = _parameter.getAsInteger();
				_foundIterations = true;
			}
		}
		if (_foundIterations == false) {
			throw new ExceptionProgrammLogicError("Required parameter missing");
		}
		return _iterations;
	}

	/**
	 * method to calculate all flows in the next step
	 * 
	 * @param flows
	 * @return
	 */
	private Vector allnextflows(Vector flows) {
		Vector allnext = new Vector();
		for (int i = 0; i < flows.size(); i++) {
			allnext.addAll(((Flow) flows.elementAt(i)).nextflows());
		}
		return allnext;
	}

	/**
	 * method to print all current possible flows
	 * 
	 * @param v
	 * @param _textOutput
	 */
	private void printflows(Vector v, ITextOutput _textOutput) {
		_textOutput.writeLn();
		for (int i = 0; i < v.size(); i++) {
			_textOutput.write("Possibility ");
			_textOutput.write(String.valueOf(i + 1));
			_textOutput.writeLn(":");
			_textOutput.writeLn();
			((Flow) v.elementAt(i)).printflow(_textOutput);
			_textOutput.writeLn();
		}
	}

	/**
	 * method to eliminate double flows
	 * 
	 * @param v
	 */
	private void eliminateDoubles(Vector v) {
		for (int i = 0; i < v.size() - 1; i++) {
			for (int j = i + 1; j < v.size(); j++) {
				if (((Flow) v.elementAt(i)).isEqual((Flow) v.elementAt(j))) {
					v.remove(j);
					j--;
				}
			}
		}
	}

}