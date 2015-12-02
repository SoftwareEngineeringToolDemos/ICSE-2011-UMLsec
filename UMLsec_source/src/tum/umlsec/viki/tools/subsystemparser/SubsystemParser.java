/**
 * UML model parser for subsystems:
 *  - saves and returns all possible flows
 *  - more UML diagrams (must only be statechart diagrams representing the subsystem) can be stored in the MDR repository
 *  - the names of the statechart diagrams are saved as the tagged value 'Diagram' of the initial states
 *  - pseudo states are included in the resulting flows
 *  - asynchronous messages between statechart diagrams are supported with the following syntax: send(diagramname.messagename(parameter)), e.g. D1.msg1(2) 
 *  - 'send' is a keyword indicating a message
 *  - 'diagramname' represents the name of the diagram the message is sent to
 *  - 'messagename' can be chosen by the user
 *  - 'parameter' must be a valid expression
 *  - effects and guards for transitions are supported
 *  - triggers for transitions are supported with the following syntax: messagename(parameter), e.g. msg2(x)
 *  - 'messagename' represents the name of the message the transition is waiting for
 *  - 'parameter' is either empty or the name of the variable the result of the expression will be assigned to
 *  - conditions and assignments are evaluated with integer variables
 *  - each fork state has its own corresponding join state
 *  - synchronization is supported with fork and join states only 
 *  - super states, history states and synchronization states can not be used
 *  - entry and exit actions are not considered
 *  - do-activities are not considered
 */

package tum.umlsec.viki.tools.subsystemparser;

import java.util.Iterator;
import java.util.Vector;

import org.omg.uml.UmlPackage;
import org.omg.uml.behavioralelements.statemachines.Pseudostate;
import org.omg.uml.behavioralelements.statemachines.PseudostateClass;
import org.omg.uml.behavioralelements.statemachines.StateMachinesPackage;
import org.omg.uml.behavioralelements.statemachines.StateVertex;
import org.omg.uml.behavioralelements.statemachines.Transition;
import org.omg.uml.behavioralelements.statemachines.TransitionClass;
import org.omg.uml.foundation.core.CorePackage;
import org.omg.uml.foundation.core.TaggedValue;

import tum.umlsec.viki.framework.ExceptionProgrammLogicError;
import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.mdr.IMdrContainer;
import tum.umlsec.viki.framework.toolbase.CommandParameterDescriptor;

// class to save all transitions that can be continued with in the next step within a syncronization zone
// (all elements in-between corresponding forks and joins or all elements not enclosed by syncronisation bars)  
class Alternatives {
    
    // fork state of the synchronization zone (initial state in case of no synchronization)
    public StateVertex fork;
    // transitions of states with more than one outgoing transition
    public Vector choices;
    // transitions of states with one outgoing transition only 
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
            Vector temp = new Vector((Vector)a.choices.elementAt(i));
            choices.add(temp);
        }
    }
    
}

// class to save the current flow of one statechart diagram
class Flow {
    
    // hierarchy of fork states in case of synchronization
    private static Vector children = new Vector();
    private static Vector parents = new Vector();
    // states of current flow
    private Vector flow;
    // all transitions that can be continued with in the next step
    private Vector alternatives;
    // variable values 
    private ExpressionParser variables;
    // input buffer
    private Vector input;
    // output buffer
    private Vector output;
    // message sent in last step
    private boolean msgsent;
    
    public Flow(StateVertex initialState, Transition t) {
        flow = new Vector();
        flow.add(initialState);
        alternatives = new Vector();
        alternatives.add(new Alternatives(initialState, t));
        variables = new ExpressionParser();
        input = new Vector();
        output = new Vector();
        msgsent = false;
    }
    
    public Flow(Flow f) {
        flow = new Vector(f.flow);
        alternatives = new Vector();
        for (int i = 0; i < f.alternatives.size(); i++) {
            Alternatives temp = new Alternatives((Alternatives)f.alternatives.elementAt(i));
            alternatives.add(temp);
        }
        variables = new ExpressionParser(f.variables);
        input = new Vector(f.input);
        output = new Vector(f.output);
        msgsent = f.msgsent;
    }
    
    // method to add messages to the input buffer
    public void addInput(Vector v) {
        input.addAll(v);
    }
    
    // method to indicate that no message was sent in the last step
    public void setBuffer() {
        msgsent = false;
    }
    
    // method to get the message sent in the last step
    public String getBuffer() {
        if (msgsent == false) return("");
        else return((String)output.lastElement());
    }
    
    // method to print the facts of the current flow of the statechart diagram
    public void printflow(ITextOutput _textOutput) {
        _textOutput.write(" Flow: ");
        for (int i=0; i < flow.size() - 1; i++) {
            _textOutput.write(((StateVertex)flow.elementAt(i)).getName());
            _textOutput.write(", ");
        }
	  if (flow.size() - 1 >= 0) {
            _textOutput.write(((StateVertex)flow.elementAt(flow.size() - 1)).getName());
            _textOutput.write(";");
        }
        _textOutput.writeLn();
        _textOutput.write(" Variable values: ");
        variables.printVars(_textOutput);
        _textOutput.writeLn();
        _textOutput.write(" Input buffer: ");
        for (int i = 0; i < input.size(); i++) {
            _textOutput.write((String)input.elementAt(i));
            _textOutput.write("; ");
        }
        _textOutput.writeLn();
        _textOutput.write(" Output buffer: ");
        for (int i = 0; i < output.size(); i++) {
            _textOutput.write((String)output.elementAt(i));
            _textOutput.write("; ");
        }
        _textOutput.writeLn();
    }
    
    // method to calculate all possible flows after an additional step
    public Vector nextflows() {
        // all possible flows after an additional step
        Vector next = new Vector();
        // checking if current state is a final state
        if (isFinalState((StateVertex)flow.lastElement()) == true) {
            next.add(new Flow(this));
            return(next);
        }
        // checking all synchronization zones
        for (int i = 0; i < alternatives.size(); i++) {
            StateVertex fork = ((Alternatives)alternatives.elementAt(i)).fork;
            // processing all transitions of states with one outgoing transition only
            for (int j = 0; j < ((Alternatives)alternatives.elementAt(i)).normal.size(); j++) {
                Flow ftemp = new Flow(this);
                Transition trans = ((Transition)((Alternatives)alternatives.elementAt(i)).normal.elementAt(j));
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
                        // substituting current transition with transition following the junction state
                        Iterator it_Tran = state.getOutgoing().iterator();
                        Transition ntrans = (Transition) it_Tran.next(); 
                        ((Alternatives)ftemp.alternatives.elementAt(i)).normal.setElementAt(ntrans, j);
                    // checking for choice state
                    } else if (isChoiceState(state) == true) {
                        // deleting current transition from alternatives
                        // adding transitions following choice state to alternatives
                        Iterator it_Tran = state.getOutgoing().iterator();
                        Vector ctemp = new Vector();
                        while(it_Tran.hasNext()) {
                            Transition ntrans = (Transition) it_Tran.next();
                            ctemp.add(ntrans);
                        }
                        ((Alternatives)ftemp.alternatives.elementAt(i)).normal.remove(j);
                        ((Alternatives)ftemp.alternatives.elementAt(i)).choices.add(ctemp);
                    // checking for fork state
                    } else if (isForkState(state) == true) {
                        // deleting current transition from alternatives
                        // adding an additional synchronization zone
                        // adding transitions following fork states to alternatives in the new synchronization zone
                        Iterator it_Tran = state.getOutgoing().iterator();
                        Vector ctemp = new Vector();
                        while(it_Tran.hasNext()) {
                            Transition ntrans = (Transition) it_Tran.next();
                            ctemp.add(ntrans);
                        }
                        ((Alternatives)ftemp.alternatives.elementAt(i)).normal.remove(j);
                        addFork(state, fork);
                        Alternatives atemp = new Alternatives(state, ctemp);
                        ftemp.alternatives.add(atemp);
                    // checking for join state
                    } else if (isJoinState(state) == true) {
                        // deleting current transition from alternatives
                        // in case of no more elements in the synchronization zone and no other synchronization zone embedded:
                        //  - deleting current synchronization zone
                        //  - adding transition after fork state to alternatives of the parent synchronization zone
                        ((Alternatives)ftemp.alternatives.elementAt(i)).normal.remove(j);
                        if (((Alternatives)ftemp.alternatives.elementAt(i)).normal.isEmpty() && ((Alternatives)ftemp.alternatives.elementAt(i)).choices.isEmpty()) {
                            if (ftemp.hasChild(fork) == false) {
                                ftemp.alternatives.remove(i);
                                Iterator it_Tran = state.getOutgoing().iterator();
                                Transition ntrans = (Transition) it_Tran.next();
                                ((Alternatives)ftemp.alternatives.elementAt(ftemp.getIndexOfParent(fork))).normal.add(ntrans);
                            }
                        }
                    // following state is no pseudo state    
                    } else {
                        Iterator it_Tran = state.getOutgoing().iterator();
                        Transition ntrans = (Transition) it_Tran.next();
                        // checking for other outgoing transitions
                        if (it_Tran.hasNext()) {
                            // deleting current transition from alternatives
                            // adding transitions following state to alternatives
                            Vector ctemp = new Vector();
                            ctemp.add(ntrans);
                            while(it_Tran.hasNext()) {
                                ntrans = (Transition) it_Tran.next();
                                ctemp.add(ntrans);
                            }
                            ((Alternatives)ftemp.alternatives.elementAt(i)).normal.remove(j);
                            ((Alternatives)ftemp.alternatives.elementAt(i)).choices.add(ctemp);
                        }
                        // one outgoing transition only
                        // substituting current transition with transition following the state
                        else ((Alternatives)ftemp.alternatives.elementAt(i)).normal.setElementAt(ntrans, j);
                    }
                    next.add(ftemp);
                // transition can not be chosen
                } else {
                    // adding source of transition to current flow
                    ftemp.flow.add(trans.getSource());
                    next.add(ftemp);
                }
            }
            // processing all transitions of states with more than one outgoing transition
            for(int j = 0; j < ((Alternatives)alternatives.elementAt(i)).choices.size(); j++) {
                Vector ctrans = ((Vector)((Alternatives)alternatives.elementAt(i)).choices.elementAt(j));
                // indicating if one of the transitions can be chosen
                boolean cont = false;
                // processing all outgoing transitions
                for (int k = 0; k < ctrans.size(); k++) {
                    Flow ftemp = new Flow(this);
                    Transition trans = (Transition)ctrans.elementAt(k);
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
                            // deleting all current transitions from alternatives
                            // adding transition after junction state to alternatives
                            Iterator it_Tran = state.getOutgoing().iterator();
                            Transition ntrans = (Transition) it_Tran.next(); 
                            ((Alternatives)ftemp.alternatives.elementAt(i)).choices.remove(j);
                            ((Alternatives)ftemp.alternatives.elementAt(i)).normal.add(ntrans);
                        // checking for choice state
                        } else if (isChoiceState(state) == true) {
                            // substituting all current transitions with all transitions after choice state
                            Iterator it_Tran = state.getOutgoing().iterator();
                            Vector ctemp = new Vector();
                            while(it_Tran.hasNext()) {
                                Transition ntrans = (Transition) it_Tran.next();
                                ctemp.add(ntrans);
                            }
                            ((Alternatives)ftemp.alternatives.elementAt(i)).choices.setElementAt(ctemp, j);
                        // checking for fork state
                        } else if (isForkState(state) == true) {
                            // deleting all current transitions from alternatives
                            // adding an additional synchronization zone
                            // adding transitions following fork states to alternatives in the new synchronization zone
                            Iterator it_Tran = state.getOutgoing().iterator();
                            Vector ctemp = new Vector();
                            while(it_Tran.hasNext()) {
                                Transition ntrans = (Transition) it_Tran.next();
                                ctemp.add(ntrans);
                            }
                            ((Alternatives)ftemp.alternatives.elementAt(i)).choices.remove(j);
                            addFork(state, fork);
                            Alternatives atemp = new Alternatives(state, ctemp);
                            ftemp.alternatives.add(atemp);
                        // checking for join state
                        } else if (isJoinState(state) == true) {
                            // deleting all current transitions from alternatives
                            // in case of no more elements in the synchronization zone and no other synchronization zone embedded:
                            //  - deleting current synchronization zone
                            //  - adding transition after fork state to alternatives of the parent synchronization zone 
                            ((Alternatives)ftemp.alternatives.elementAt(i)).choices.remove(j);
                            if (((Alternatives)ftemp.alternatives.elementAt(i)).normal.isEmpty() && ((Alternatives)ftemp.alternatives.elementAt(i)).choices.isEmpty()) {
                                if (ftemp.hasChild(fork) == false) {
                                    ftemp.alternatives.remove(i);
                                    Iterator it_Tran = state.getOutgoing().iterator();
                                    Transition ntrans = (Transition) it_Tran.next();
                                    ((Alternatives)ftemp.alternatives.elementAt(ftemp.getIndexOfParent(fork))).normal.add(ntrans);
                                }
                            }
                        // following state is no pseudo state    
                        } else {
                            Iterator it_Tran = state.getOutgoing().iterator();
                            Transition ntrans = (Transition) it_Tran.next();
                            // checking for other outgoing transitions
                            if (it_Tran.hasNext()) {
                                // substituting all current transitions with all transitions after state
                                Vector ctemp = new Vector();
                                ctemp.add(ntrans);
                                while(it_Tran.hasNext()) {
                                    ntrans = (Transition) it_Tran.next();
                                    ctemp.add(ntrans);
                                }
                                ((Alternatives)ftemp.alternatives.elementAt(i)).choices.setElementAt(ctemp, j);
                            }
                            // one outgoing transition only
                            else {
                                // deleting all current transitions from alternatives
                                // adding transition after state to alternatives
                                ((Alternatives)ftemp.alternatives.elementAt(i)).choices.remove(j);
                                ((Alternatives)ftemp.alternatives.elementAt(i)).normal.add(ntrans);
                            }
                        }
                        next.add(ftemp);
                        cont = true;
                        // if one transition can be chosen no other transitions are checked
                        break;
                    }
                }
                // checking if no transition could be chosen 
                if (cont == false) {
                    Flow ftemp = new Flow(this);
                    // adding source of transition to current flow
                    ftemp.flow.add(((Transition)ctrans.firstElement()).getSource());
                    // in case a trigger was assigned to the transition one message from the input buffer is deleted (if not already empty)
                    if ((((Transition)ctrans.firstElement()).getTrigger() != null) && (ftemp.input.isEmpty() == false)) ftemp.input.remove(0);
                    next.add(ftemp);
                }
            }
        }
        return(next);
    }
    
    // method to check if two flows of one statechart are equal
    public boolean isEqual(Flow f) {
        if (flow.size() != f.flow.size()) return(false);
        if (input.size() != f.input.size()) return(false);
        if (output.size() != f.output.size()) return(false);
        for (int i = 0; i < flow.size(); i++) {
            if (((StateVertex)flow.elementAt(i)) != ((StateVertex)f.flow.elementAt(i))) return(false);
        }
        for (int i = 0; i < input.size(); i++) {
            if (((String)input.elementAt(i)).equals(((String)f.input.elementAt(i))) == false) return(false);
        }
        for (int i = 0; i < output.size(); i++) {
            if (((String)output.elementAt(i)).equals(((String)f.output.elementAt(i))) == false) return(false);
        }
        if (variables.isEqual(f.variables) == false) return(false);
        return(true);
    }

    // method to check for choice states
    private static boolean isChoiceState(StateVertex state) {
        if (state.getClass().getName().endsWith("Pseudostate$Impl")) {
            if (((Pseudostate)state).getKind().toString() == "pk_choice") {
                    return(true);
                }    
                else {
                    return(false);
                }
        }
        else return(false);
    }
    
    // method to check for junction states
    private static boolean isJunctionState(StateVertex state) {
        if (state.getClass().getName().endsWith("Pseudostate$Impl")) {
            if (((Pseudostate)state).getKind().toString() == "pk_junction") {
                    return(true);
                }    
                else {
                    return(false);
                }
        }
        else return(false);
    }
    
    // method to check for fork states
    private static boolean isForkState(StateVertex state) {
        if (state.getClass().getName().endsWith("Pseudostate$Impl")) {
            if (((Pseudostate)state).getKind().toString() == "pk_fork") {
                    return(true);
                }    
                else {
                    return(false);
                }
        }
        else return(false);
    }
    
    // method to check for join states
    private static boolean isJoinState(StateVertex state) {
        if (state.getClass().getName().endsWith("Pseudostate$Impl")) {
            if (((Pseudostate)state).getKind().toString() == "pk_join") {
                    return(true);
                }    
                else {
                    return(false);
                }
        }
        else return(false);
    }
    
    // method to check for final states
    private static boolean isFinalState(StateVertex state) {
        if (state.getClass().getName().endsWith("FinalState$Impl")) {
                return(true);
        }    
        else return(false);
    }
    
    // method to check if the condition of the transition tr is true
    private boolean chkTransition(Transition tr) {
        // indicating if transition can be chosen
        boolean res = true;
        if (tr != null) {
            ExpressionParser svariables = new ExpressionParser(variables); 
            // checking if a trigger is assigned to the current transition
            if (tr.getTrigger() !=null) {
                // transition can not be chosen if input buffer is empty
                if (input.isEmpty()) return(false);
                String st = tr.getTrigger().getName();
                String msg = st.substring(0, st.indexOf('('));
                // comparing the names of the messages
                if (msg.equals(getMsgN()) == false) {
                    input.remove(0);
                    return(false);
                }
                msg = st.substring(st.indexOf('(') + 1, st.indexOf(')'));
                // the received parameter is assigned to the specified variable (if existing)
                if (msg.equals("") == false) {
                    msg = msg + "=" + getMsgV();
                    variables.assignment(msg);
                }
            }
            // checking condition if present
            if (tr.getGuard() != null) res = variables.condition(tr.getGuard().getExpression().getBody());
            if ((res == false) && (tr.getTrigger() !=null)) variables = svariables;
            if ((res == true) && (tr.getTrigger() !=null)) input.remove(0);
            // processing effect if present
            if ((res == true) && (tr.getEffect() != null)) {
                String st = tr.getEffect().getScript().getBody();
                // checking if effect is an assignment
                if (isMessage(st) == false) variables.assignment(tr.getEffect().getScript().getBody());
                // effect is a message
                else {
                    output.add(getMsg(st));
                    msgsent = true;
                }
            }
        }
        return(res);
    }
    
    // method to get the parameter of the current message from the input buffer
    private String getMsgV() {
        return(((String)input.firstElement()).substring(((String)input.firstElement()).indexOf('(') + 1, ((String)input.firstElement()).indexOf(')')));
    }
    
    // method to get the name of the message from the input buffer
    private String getMsgN() {
        return(((String)input.firstElement()).substring(0, ((String)input.firstElement()).indexOf('(')));
    }
    
    // method to check if an effect is a message
    private static boolean isMessage(String st) {
        if (st.indexOf("send(") != -1) return(true);
        else return(false);
    }
    
    // method to process the message
    private String getMsg(String st) {
        int n = st.indexOf("send(") + 5;
        int k = st.lastIndexOf(')');
        StringBuffer msg = new StringBuffer(st.substring(n, k));
        int m = msg.indexOf("(") + 1;
        int l = msg.lastIndexOf(")");       
        String res = variables.getValue(msg.substring(m, l));
        msg.replace(m, l, res);
        return(msg.toString());
    }
    
    // method to extend the hierarchy with another fork state
    private static void addFork(StateVertex fork, StateVertex parent) {
        if (children.indexOf(fork) == -1) {
            children.add(fork);
            parents.add(parent);
        }
    }
    
    // method to get the parent of the fork state 
    private static StateVertex getParent(StateVertex fork) {
        if (children.indexOf(fork) == -1) return(null);
        else if (parents.elementAt(children.indexOf(fork)) == null) return(null);
        else return((StateVertex)parents.elementAt(children.indexOf(fork)));
    }
    
    // method to check for children
    private boolean hasChild(StateVertex parent) {
        if (parents.indexOf(parent) == -1) return(false);
        else {
            StateVertex child = null;
            if (children.elementAt(parents.indexOf(parent)) != null) child = (StateVertex)children.elementAt(parents.indexOf(parent));
            for (int i = 0; i < alternatives.size(); i++) {
                if (((Alternatives)alternatives.elementAt(i)).fork == child) return(true);
            }
            return(false);
        }
    }
    
    // method to get the index of the parent of the fork state 
    private int getIndexOfParent(StateVertex child) {
        StateVertex parent = null;
        if (parents.elementAt(children.indexOf(child)) != null) parent = (StateVertex)parents.elementAt(children.indexOf(child));
        for (int i = 0; i < alternatives.size(); i++) {
            if (((Alternatives)alternatives.elementAt(i)).fork == parent) return(i);
        }
        return(-1);
    }
    
}

// class to calculate all possible flows with the given length
public class SubsystemParser {
    
    // names of the statechart diagrams
    private Vector diagrams;
    
    // method to print all possible flows with the given length
    public void check(IMdrContainer _mdrContainer, Iterator _parameters, ITextOutput _mainOutput) {
        UmlPackage root = _mdrContainer.getUmlPackage();
	CorePackage corePackage = root.getCore();
	StateMachinesPackage stateMachines = (StateMachinesPackage)root.getStateMachines();
	TransitionClass transitionClasses = (TransitionClass)stateMachines.getTransition();
	PseudostateClass pseudostateClasses = (PseudostateClass)stateMachines.getPseudostate();
	int _iterations = 1;
	boolean _foundIterations = false; 
	for (; _parameters.hasNext();) {
            CommandParameterDescriptor _parameter = (CommandParameterDescriptor) _parameters.next();
                if(_parameter.getId() == ToolSubsystem.CPID_DEPTH) {
		_iterations = _parameter.getAsInteger();
		_foundIterations = true;
            }
	}
	if(_foundIterations == false) {
            throw new ExceptionProgrammLogicError("Required parameter missing");
        }
        Vector flows = new Vector();
        diagrams = new Vector();
        Pseudostate initialState;
        // searching for initial states
        initialState = null;
        Vector oneflow = new Vector();
        for (Iterator it_act_I = pseudostateClasses.refAllOfClass().iterator(); it_act_I.hasNext();) {
            initialState = (Pseudostate)it_act_I.next();
            if (initialState.getKind().toString() == "pk_initial") {
                Iterator it_Tran = initialState.getOutgoing().iterator();
                Transition ntrans = (Transition) it_Tran.next();
                oneflow.add(new Flow((StateVertex)initialState, ntrans));
                Iterator it = initialState.getTaggedValue().iterator();
                while(it.hasNext()) {
                    TaggedValue t = (TaggedValue) it.next();
                    // getting the name of the statechart diagram
                    if (t.getType().getName().equals("Diagram")) {
                        Iterator it2 = t.getDataValue().iterator();
                        diagrams.add((String) it2.next());
                    }
                }
            }
        }
        flows.add(oneflow);
        _mainOutput.writeLn("The following possibilities show the different flows of the given subsystem.");
        for (int j = 0; j < (_iterations-1); j++) {
            flows = allnextflows(flows);
        }
        eliminateDoubles(flows);
        printflows(flows, _mainOutput);
    }
    
    // method to calculate all flows in the next step
    private Vector allnextflows(Vector flows) {
        Vector allnext = new Vector();
        for (int i = 0; i < flows.size(); i++) {
            allnext.addAll(nextflows((Vector)flows.elementAt(i)));
        }
        return(allnext);
    }
    
    // method to calculate all flows in the next step of one possibility
    private Vector nextflows(Vector oneflow) {
        Vector next = new Vector();
        // getting the possible flows in the next step of the first statechart diagram
        Vector temp = ((Flow)oneflow.elementAt(0)).nextflows();
        // alternating the vector to conform to the used data structure
        for (int i = 0; i < temp.size(); i++) {
            Vector temp2 = new Vector();
            temp2.add((Flow)temp.elementAt(i));
            next.add(temp2);
        }
        // processing all other statechart diagrams
        for (int i = 1; i < oneflow.size(); i++) {
            // getting the possible flows in the next step of the current statechart diagram
            temp = ((Flow)oneflow.elementAt(i)).nextflows();
            Vector temp2 = cpVec(next);
            next = new Vector();
            // interleaving the possible flows of the diagrams
            for (int j = 0; j < temp2.size(); j++) {
                Vector temp3 = (Vector)temp2.elementAt(j);
                for (int k = 0; k < temp.size(); k++) {
                    Vector temp4 = new Vector(temp3);
                    temp4.add((Flow)temp.elementAt(k));
                    next.add(temp4);
                }
            }            
        }
        Vector res = new Vector();
        // processing all possible flows of all statechart diagrams
        for (int i = 0; i < next.size(); i++) {
            Vector oflow = ((Vector)next.elementAt(i));
            Vector buffer = new Vector();
            Vector flowperm = new Vector();
            // initializing buffer
            for (int j = 0; j < oflow.size(); j++) {
                Vector temp2 = new Vector();
                buffer.add(temp2);
            }
            // assigning the messages to their target statechart diagrams
            for (int j = 0; j < oflow.size(); j++) {
                if (((Flow)oflow.elementAt(j)).getBuffer().equals("") == false) {
                    String st = ((Flow)oflow.elementAt(j)).getBuffer();
                    ((Vector)buffer.elementAt(getIndex(getMsgReceiver(st)))).add(getMsgOnly(st));
                    ((Flow)oflow.elementAt(j)).setBuffer();
                }
            }
            int n = 1;
            // creating vector of instances of the flow with permutated input buffers of the statechart diagrams
            for (int j = 0; j < buffer.size(); j++) {
                if (((Vector)buffer.elementAt(j)).isEmpty()) {
                    Vector temp2 = new Vector();
                    temp2.add(new Flow((Flow)oflow.elementAt(j)));
                    flowperm.add(temp2);
                } else if (((Vector)buffer.elementAt(j)).size() == 1) {
                    Vector temp2 = new Vector();
                    Flow ftemp = new Flow((Flow)oflow.elementAt(j));
                    ftemp.addInput(((Vector)buffer.elementAt(j)));
                    temp2.add(ftemp);
                    flowperm.add(temp2);
                } else {
                    n *= factorial(((Vector)buffer.elementAt(j)).size());
                    Vector temp2 = permut((Flow)oflow.elementAt(j), (Vector)buffer.elementAt(j));
                    flowperm.add(temp2);
                }
            }           
            Vector next2 = new Vector();
            // initializing vector for results
            for (int j = 0; j < n; j++) {
                Vector temp2 = new Vector();
                for (int k = 0; k < flowperm.size(); k++) {
                    temp2.add(((Vector)flowperm.elementAt(k)).elementAt(0));
                }
                next2.add(temp2);
            }
            // checking if we have to permutate
            if (n == 1) res.addAll(next2);
            else {
                int o = 1;
                // permutating all possibilities
                for (int j = 0; j < flowperm.size(); j++) {
                    if (((Vector)flowperm.elementAt(j)).size() > 1) {
                        int p = 0;
                        n = n / factorial(((Vector)flowperm.elementAt(j)).size());
                        for (int k = 0; k < n; k++) {
                            for (int l = 0; l < factorial(((Vector)flowperm.elementAt(j)).size()); l++) {
                                for (int m = 0; m < o; m++) {
                                    ((Vector)next2.elementAt(p)).setElementAt(((Vector)flowperm.elementAt(j)).elementAt(l), j);
                                    p++;
                                }
                            }
                        }
                        o = o * factorial(((Vector)flowperm.elementAt(j)).size());
                    }
                }
                res.addAll(next2);
            }
        }
        return(res);
    }
    
    // method to calculate all possible sequences of the incoming messages
    private Vector permut(Flow f, Vector buffer) {
        Vector res = new Vector();
        // creating the instances of the flow
        for (int i = 0; i < factorial(buffer.size()); i++) {
            res.add(new Flow(f));
        }
        Vector bufres = new Vector();
        permutrec(new Vector(), buffer, bufres);
        for (int i = 0; i < bufres.size(); i++) {
            ((Flow)res.elementAt(i)).addInput((Vector)bufres.elementAt(i));
        }
        return(res);
    }
    
    // method to calculate all possible permutations recursively
    // perm: current list of chosen elements (empty at the beginning)
    // rest: current list of remaining elements (vector to permutate at the beginning)
    // res: current result (empty at the beginning)
    private void permutrec(Vector perm, Vector rest, Vector res) {
        if (rest.size() == 1) {
            Vector temp = new Vector(perm);
            temp.add(rest.elementAt(0));
            res.add(temp);
        } else {
            for (int i = 0; i < rest.size(); i++) {
                Vector temp = new Vector(rest);
                perm.add(rest.elementAt(i));
                temp.remove(i);
                permutrec(perm, temp, res);
                perm.remove(perm.size() - 1);
            }
        }       
    }
    
    // method to get the receiver of the message
    private static String getMsgReceiver(String st) {
        int n = st.indexOf('.');
        return(st.substring(0, n));
    }
    
    // method to get the message without the receiver
    private static String getMsgOnly(String st) {
        int n = st.indexOf('.') + 1;
        int m = st.lastIndexOf(')') + 1;
        return(st.substring(n, m));
    }
    
    // method to get the index of the statechart diagram
    private int getIndex(String st) {
        for (int i = 0; i < diagrams.size(); i++) {
            if (st.equals((String)diagrams.elementAt(i))) return(i);
        }
        return(-1);
    }
    
    // method to get the factorial of a number
    private static int factorial(int n) {
        int res = 1;
        for (int i = 2; i <= n; i++) res *= i;
        return(res);
    }
    
    // method to copy flows
    private static Vector cpVec(Vector v) {
        Vector temp1 = new Vector();
        for (int i = 0; i < v.size(); i++) {
            Vector temp2 = new Vector((Vector)v.elementAt(i));
            temp1.add(temp2);
        }
        return(temp1);
    }
    
    // method to print all current possible flows
    private void printflows(Vector v, ITextOutput _textOutput) {
        _textOutput.writeLn();
        for (int i=0; i < v.size(); i++) {    
            _textOutput.write("Possibility ");
            _textOutput.write(String.valueOf(i+1));
            _textOutput.writeLn(":");
            _textOutput.writeLn();
            for (int j=0; j < ((Vector)v.elementAt(i)).size(); j++) {
                _textOutput.write("Statechart diagram \"");
                _textOutput.write((String)diagrams.elementAt(j));
                _textOutput.writeLn("\"");
                ((Flow)((Vector)v.elementAt(i)).elementAt(j)).printflow(_textOutput);
                _textOutput.writeLn();
            }
            _textOutput.writeLn();
            _textOutput.writeLn();
        }
        _textOutput.writeLn("Parsing finished.");
    }
    
    // method to check if two flows of all statechart diagrams are equal
    private boolean isEqual(Vector v1, Vector v2) {
        for (int i = 0; i < v1.size(); i++) {
            if (((Flow)v1.elementAt(i)).isEqual((Flow)v2.elementAt(i)) == false) return(false);
        }
        return(true);
    }
    
    // method to eliminate double flows
    private void eliminateDoubles(Vector v) {
        for (int i = 0; i < v.size() - 1; i++) {
            for (int j = i + 1; j < v.size(); j++) {
                if (isEqual((Vector)v.elementAt(i), (Vector)v.elementAt(j))) {
                    v.remove(j);
                    j--;
                }
            }
        }
    }

}