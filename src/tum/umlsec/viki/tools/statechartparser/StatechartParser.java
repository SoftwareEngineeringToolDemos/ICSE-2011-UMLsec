/**
 * UML model parser for statechart diagrams:
 *  - returns one possible flow that was chosen by the user
 *  - only one UML diagram (must be a statechart diagram) can be stored in the MDR repository
 *  - pseudo states are included in the resulting flows
 *  - the user can send asynchronous messages to the diagram with the following syntax: messagename(parameter), e.g. msg1(2)
 *  - 'messagename' can be chosen by the user
 *  - 'parameter' must be an integer
 *  - effects and guards for transitions are supported
 *  - triggers for transitions are supported with the following syntax: messagename(parameter), e.g. msg2(x)
 *  - 'messagename' represents the name of the message the transition is waiting for
 *  - 'parameter' is either empty or the name of the variable the integer will be assigned to 
 *  - conditions and assignments are evaluated with integer variables
 *  - each fork state has its own corresponding join state
 *  - synchronization is supported with fork and join states only 
 *  - super states, history states and synchronization states can not be used
 *  - entry and exit actions are not considered
 *  - do-activities are not considered
 */

package tum.umlsec.viki.tools.statechartparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import org.omg.uml.foundation.core.TaggedValueClass;

import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.mdr.IMdrContainer;

// class to save all transitions that can be continued with in the next step within a synchronization zone
// (all elements in-between corresponding forks and joins or all elements not enclosed by synchronisation bars)  
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
    
    private List msglist = new ArrayList();
    // variable values 
    private ExpressionParser variables;
    // input buffer
    private Vector input;
    // output buffer
    private Vector output;
    
    public Flow(StateVertex initialState, Transition t) {
        flow = new Vector();
        flow.add(initialState);
        alternatives = new Vector();
        alternatives.add(new Alternatives(initialState, t));
        variables = new ExpressionParser();
        input = new Vector();
        output = new Vector();
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
        msglist = f.msglist;
    }
    
    // method to get the next transition from the user
    public void nextTransitions(ITextOutput _textOutput) {
        if (isFinalState((StateVertex)flow.lastElement())) return;
        _textOutput.writeLn();
        _textOutput.write("Input Buffer: ");
        for (int i = 0; i < input.size(); i++) {
            _textOutput.write((String)input.elementAt(i));
            _textOutput.write("; ");
        }
        _textOutput.writeLn();
        _textOutput.write("Output Buffer: ");
        for (int i = 0; i < output.size(); i++) {
            _textOutput.write((String)output.elementAt(i));
            _textOutput.write("; ");
        }
        _textOutput.writeLn();
        _textOutput.write("Variable values: ");
        variables.printVars(_textOutput);
        _textOutput.writeLn();
        int n = 1;
        // printing all transitions that can be chosen
        for (int i = 0; i < alternatives.size(); i++) {
            for (int j = 0; j < ((Alternatives)alternatives.elementAt(i)).normal.size(); j++) {
                Transition trans = (Transition)((Alternatives)alternatives.elementAt(i)).normal.elementAt(j);
                _textOutput.write("Choose '");
                _textOutput.write(String.valueOf(n));
                _textOutput.write("' for 'Next Transition' to continue with the following transition: Name: ");
                _textOutput.write(trans.getName());
                _textOutput.write("; Source: ");
                _textOutput.write(trans.getSource().getName());
                _textOutput.write("; Target: ");
                _textOutput.write(trans.getTarget().getName());
                if (trans.getTrigger() != null) {
                    _textOutput.write("; Trigger: ");
                    _textOutput.write(trans.getTrigger().getName());
                }
                if (trans.getGuard() != null) {
                    _textOutput.write("; Guard: ");
                    _textOutput.write(trans.getGuard().getExpression().getBody());
                }
                if (trans.getEffect() != null) {
                    _textOutput.write("; Effect: ");
                    _textOutput.write(trans.getEffect().getScript().getBody());
                }
                _textOutput.writeLn();
                n++;
            }
            for (int j = 0; j < ((Alternatives)alternatives.elementAt(i)).choices.size(); j++) {
                _textOutput.write("Choose '");
                _textOutput.write(String.valueOf(n));
                _textOutput.writeLn("' for 'Next Transition' to continue with one of these transitions:");
                for (int k = 0; k < ((Vector)((Alternatives)alternatives.elementAt(i)).choices.elementAt(j)).size(); k++) {
                    Transition trans = (Transition)((Vector)((Alternatives)alternatives.elementAt(i)).choices.elementAt(j)).elementAt(k);
                    
                    Flow ftemp = new Flow(this);
                    if (ftemp.chkTransition(trans) == true) _textOutput.write(">");
                    else _textOutput.write(" ");
                    _textOutput.write("Name: ");
                    _textOutput.write(trans.getName());
                    _textOutput.write("; Source: ");
                    _textOutput.write(trans.getSource().getName());
                    _textOutput.write("; Target: ");
                    _textOutput.write(trans.getTarget().getName());
                    if (trans.getTrigger() != null) {
                        _textOutput.write("; Trigger: ");
                        _textOutput.write(trans.getTrigger().getName());
                    }
                    if (trans.getGuard() != null) {
                        _textOutput.write("; Guard: ");
                        _textOutput.write(trans.getGuard().getExpression().getBody());
                    }
                    if (trans.getEffect() != null) {
                        _textOutput.write("; Effect: ");
                        _textOutput.write(trans.getEffect().getScript().getBody());
                    }
                    _textOutput.writeLn();
                }
                n++;
            }
        }
    }
    
    // method to print the facts of the current flow of the statechart diagram
    public void printflow(ITextOutput _textOutput) {
        _textOutput.writeLn();
        _textOutput.writeLn();
        _textOutput.writeLn();
        _textOutput.write("Current Flow: ");
        for (int i=0; i < flow.size() - 1; i++) {
            _textOutput.write(((StateVertex)flow.elementAt(i)).getName());
            _textOutput.write(", ");
        }
        if (flow.size() - 1 >= 0) {
            _textOutput.write(((StateVertex)flow.elementAt(flow.size() - 1)).getName());
            _textOutput.write(";");
        }
		_textOutput.writeLn();
		_textOutput.writeLn("Transitions: ");
		for(Iterator it=msglist.iterator();it.hasNext();){
			String msgst = (String)it.next();
			_textOutput.write(msgst);
			_textOutput.write("  ");
		}
		_textOutput.writeLn();
        
        _textOutput.writeLn();
        _textOutput.write("Variable values: ");
        variables.printVars(_textOutput);
        _textOutput.writeLn();
        _textOutput.write("Input buffer: ");
        for (int i = 0; i < input.size(); i++) {
            _textOutput.write((String)input.elementAt(i));
            _textOutput.write("; ");
        }
        _textOutput.writeLn();
        _textOutput.write("Output buffer: ");
        for (int i = 0; i < output.size(); i++) {
            _textOutput.write((String)output.elementAt(i));
            _textOutput.write("; ");
        }
        _textOutput.writeLn();
    }
     
    // method to calculate the next flow after an additional step
    public boolean nextflow(int n) {
        // checking all synchronization zones
		
        for (int i = 0; i < alternatives.size(); i++) {
			
            // processing transitions of states with one outgoing transition only
            if (((Alternatives)alternatives.elementAt(i)).normal.size() <= n) n -= ((Alternatives)alternatives.elementAt(i)).normal.size();
            else {
				
                StateVertex fork = ((Alternatives)alternatives.elementAt(i)).fork;
                Flow ftemp = new Flow(this);
                Transition trans = ((Transition)((Alternatives)alternatives.elementAt(i)).normal.elementAt(n));
                // checking if transition can be chosen
                if (ftemp.chkTransition(trans) == true) {
                	
                    StateVertex state = trans.getTarget();
                    ftemp.flow.add(state);
                    // checking for final state 
                    if (isFinalState(state) == true) {
                        // deleting alternatives
                        alternatives = new Vector();
                        flow = ftemp.flow;
                        variables = ftemp.variables;
                        input = ftemp.input;
                        output = ftemp.output;
                        flow.add(state);
                        return(false);
                    // checking for junction state
                    } else if (isJunctionState(state) == true) {
                        // substituting current transition with transition following the junction state
                        Iterator it_Tran = state.getOutgoing().iterator();
                        Transition ntrans = (Transition) it_Tran.next(); 
                        ((Alternatives)ftemp.alternatives.elementAt(i)).normal.setElementAt(ntrans, n);
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
                        ((Alternatives)ftemp.alternatives.elementAt(i)).normal.remove(n);
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
                        ((Alternatives)ftemp.alternatives.elementAt(i)).normal.remove(n);
                        addFork(state, fork);
                        Alternatives atemp = new Alternatives(state, ctemp);
                        ftemp.alternatives.add(atemp);
                    // checking for join state
                    } else if (isJoinState(state) == true) {
                        // deleting current transition from alternatives
                        // in case of no more elements in the synchronization zone and no other synchronization zone embedded:
                        //  - deleting current synchronization zone
                        //  - adding transition after fork state to alternatives of the parent synchronization zone
                        ((Alternatives)ftemp.alternatives.elementAt(i)).normal.remove(n);
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
                            while (it_Tran.hasNext()) {
                                ntrans = (Transition) it_Tran.next();
                                ctemp.add(ntrans);
                            }
                            ((Alternatives)ftemp.alternatives.elementAt(i)).normal.remove(n);
                            ((Alternatives)ftemp.alternatives.elementAt(i)).choices.add(ctemp);
                        }
                        // one outgoing transition only
                        // substituting current transition with transition following the state
                        else ((Alternatives)ftemp.alternatives.elementAt(i)).normal.setElementAt(ntrans, n);
                    }
                // transition can not be chosen
                } else {
                    // adding source of transition to current flow
                    ftemp.flow.add(trans.getSource());
                }
                flow = ftemp.flow;
                alternatives = ftemp.alternatives;
                variables = ftemp.variables;
                input = ftemp.input;
                output = ftemp.output;
                return(true);
            }
            // processing transitions of states with more than one outgoing transition
            if (((Alternatives)alternatives.elementAt(i)).choices.size() <= n) n -= ((Alternatives)alternatives.elementAt(i)).choices.size();
            else {
				
                StateVertex fork = ((Alternatives)alternatives.elementAt(i)).fork;
                Vector ctrans = ((Vector)((Alternatives)alternatives.elementAt(i)).choices.elementAt(n));
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
                            alternatives = new Vector();
                            flow = ftemp.flow;
                            variables = ftemp.variables;
                            input = ftemp.input;
                            output = ftemp.output;
                            return(false);
                        // checking for junction state
                        } else if (isJunctionState(state) == true) {
                            // deleting all current transitions from alternatives
                            // adding transition after junction state to alternatives
                            Iterator it_Tran = state.getOutgoing().iterator();
                            Transition ntrans = (Transition) it_Tran.next(); 
                            ((Alternatives)ftemp.alternatives.elementAt(i)).choices.remove(n);
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
                            ((Alternatives)ftemp.alternatives.elementAt(i)).choices.setElementAt(ctemp, n);
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
                            ((Alternatives)ftemp.alternatives.elementAt(i)).choices.remove(n);
                            addFork(state, fork);
                            Alternatives atemp = new Alternatives(state, ctemp);
                            ftemp.alternatives.add(atemp);
                        // checking for join state
                        } else if (isJoinState(state) == true) {
                            // deleting all current transitions from alternatives
                            // in case of no more elements in the synchronization zone and no other synchronization zone embedded:
                            //  - deleting current synchronization zone
                            //  - adding transition after fork state to alternatives of the parent synchronization zone 
                            ((Alternatives)ftemp.alternatives.elementAt(i)).choices.remove(n);
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
                                ((Alternatives)ftemp.alternatives.elementAt(i)).choices.setElementAt(ctemp, n);
                            }
                            // one outgoing transition only
                            else {
                                // deleting all current transitions from alternatives
                                // adding transition after state to alternatives
                                ((Alternatives)ftemp.alternatives.elementAt(i)).choices.remove(n);
                                ((Alternatives)ftemp.alternatives.elementAt(i)).normal.add(ntrans);
                            }
                        }
                        cont = true;
                        // if one transition can be chosen no other transitions are checked
                        flow = ftemp.flow;
                        alternatives = ftemp.alternatives;
                        variables = ftemp.variables;
                        input = ftemp.input;
                        output = ftemp.output;
                        return(true);
                    }
                }
                // checking if no transition could be chosen 
                if (cont == false) {
                    Flow ftemp = new Flow(this);
                    // adding source of transition to current flow
                    ftemp.flow.add(((Transition)ctrans.firstElement()).getSource());
                    // in case a trigger was assigned to the transition one message from the input buffer is deleted (if not already empty)
                    if ((((Transition)ctrans.firstElement()).getTrigger() != null) && (ftemp.input.isEmpty() == false)) ftemp.input.remove(0);
                    flow = ftemp.flow;
                    alternatives = ftemp.alternatives;
                    variables = ftemp.variables;
                    input = ftemp.input;
                    output = ftemp.output;
                    return(true);
                }
            }
        }
        return(true);
    }
    
    // method to add a message to the input buffer
    public void addInput(String msg) {
        input.add(msg);
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
				msglist.add(msg);
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
public class StatechartParser {
    
    private Flow flow;
    
    public List slist = new ArrayList();
	public Map nmap = new HashMap();
	public boolean b = true;
	
    public StatechartParser(IMdrContainer _mdrContainer, ITextOutput _textOutput) {
        UmlPackage root = _mdrContainer.getUmlPackage();
	CorePackage corePackage = root.getCore();
	StateMachinesPackage stateMachines = (StateMachinesPackage)root.getStateMachines();
		_textOutput.writeLn("=======running CheckerNoDownFlow...");
	  _textOutput.writeLn("======= All TaggedValues");
	  TaggedValueClass tagvalueClasses=(TaggedValueClass)corePackage.getTaggedValue();
       
	  for(Iterator it_Tag_V= tagvalueClasses.refAllOfClass().iterator(); it_Tag_V.hasNext();) {
		  TaggedValue tagValue=(TaggedValue)it_Tag_V.next();
//list all tagged values of the tagged type "start"
		  if((tagValue.getType()).getTagType().equals("high")||(tagValue.getType()).getTagType().equals("secret")){
			  for(Iterator it_tagVa_A = (tagValue.getDataValue()).iterator(); it_tagVa_A.hasNext();){
				  String tagValue_Da_A=(String)it_tagVa_A.next();
				  if (tagValue_Da_A!=null&&tagValue_Da_A.length()!=0) _textOutput.writeLn("TaggedValue (Data) of secret is " + tagValue_Da_A);
				  if (tagValue_Da_A!=null) {
					  slist.add(tagValue_Da_A);
				  } 
			  }
		  }
	  }
	
	TransitionClass transitionClasses = (TransitionClass)stateMachines.getTransition();
	for(Iterator itt = transitionClasses.refAllOfClass().iterator();itt.hasNext();){
		Transition t=(Transition)itt.next();
		try{
		
		String nameT=t.getTrigger().getName();
		String ef = t.getEffect().getScript().getBody();
		String name=nameT.substring(0, nameT.indexOf('('));
		if (!slist.contains(name)){
			if (nmap==null){
				nmap.put(name,ef);
			} else if (nmap.containsKey(name)){
				String st=(String)nmap.get(name);
				if (!st.equals(ef)){
					_textOutput.writeLn("The output of a non-high or non-secret message "+ nameT+" depends on high or secret information.");
					b = false;
				}
				
			} else{
				nmap.put(name,ef);
			}
		}
		}catch (java.lang.NullPointerException nx){
			System.out.println("java.lang.NullPointerException with t.getTigger " + nx.getMessage());
		}
	}
	if (!b) {_textOutput.writeLn("The UML model violates the requirements of <<no down-flow>>");} 
	else {_textOutput.writeLn("The UML model satisfies the requirements of <<no down-flow>>");}
	
	PseudostateClass pseudostateClasses = (PseudostateClass)stateMachines.getPseudostate();
        Pseudostate initialState;
        // searching for initial states
        initialState = null;
        for (Iterator it_act_I = pseudostateClasses.refAllOfClass().iterator(); it_act_I.hasNext();) {
            initialState = (Pseudostate)it_act_I.next();
            if (initialState.getKind().toString() == "pk_initial") {
                Iterator it_Tran = initialState.getOutgoing().iterator();
                Transition ntrans = (Transition) it_Tran.next();
                flow = new Flow((StateVertex)initialState, ntrans);
            }
        }
    }
    
    // method to start parsing the statechart
    public void init(ITextOutput _textOutput) {
        _textOutput.writeLn("Parsing of the given statechart diagram started."); 
        flow.nextTransitions(_textOutput);
    }
    
    // method to get to the next step
    public boolean nextTransition(int n, ITextOutput _textOutput) {
        if (flow.nextflow(n - 1) == false) {
            flow.printflow(_textOutput);
            _textOutput.writeLn();
            _textOutput.writeLn("Parsing finished.");
            return(false);
        }
        flow.nextTransitions(_textOutput);
        return(true);
    }
    
    // method to add a message to the input buffer
    public void sendMessage(String msg, ITextOutput _textOutput) {
        flow.addInput(msg);
        flow.nextTransitions(_textOutput);
    }
    
    // method to stop parsing the statechart
    public void stopParsing(ITextOutput _textOutput) {
        flow.printflow(_textOutput);
        _textOutput.writeLn();
        _textOutput.writeLn("Parsing finished.");
    }
    
}