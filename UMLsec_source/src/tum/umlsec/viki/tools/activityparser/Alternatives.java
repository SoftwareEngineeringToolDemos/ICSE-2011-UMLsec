package tum.umlsec.viki.tools.activityparser;

import java.util.Vector;

import org.omg.uml.behavioralelements.statemachines.StateVertex;
import org.omg.uml.behavioralelements.statemachines.Transition;

/**
 * class to save all transitions that can be continued with in the next step
 * within a synchronization zone (all elements in-between corresponding forks
 * and joins or all elements not enclosed by synchronisation bars)
 * 
 *  @author (refactoring) Bianca Batsch, bianca.batsch@isst.fraunhofer.de
 * 
 */
public class Alternatives {

	/**
	 * fork state of the synchronization zone (initial state in case of no
	 * synchronization)
	 */
	public StateVertex fork;
	/**
	 * transitions with a choice state as their source
	 */
	public Vector choices;
	/**
	 * all other transitions
	 */
	public Vector normal;

	public Alternatives(StateVertex f, Transition t) {
		fork = f;
		normal = new Vector();
		normal.add(t);
		choices = new Vector();
	}

	public Alternatives(StateVertex f, Vector v) {
		fork = f;
		normal = v;
		choices = new Vector();
	}

	public Alternatives(Alternatives a) {
		fork = a.fork;
		normal = new Vector(a.normal);
		choices = new Vector();
		for (int i = 0; i < a.choices.size(); i++) {
			Vector temp = new Vector((Vector) a.choices.elementAt(i));
			choices.add(temp);
		}
	}

}
