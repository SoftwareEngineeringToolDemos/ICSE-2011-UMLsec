/**
 * @author Erwin Yukselgil
 * Created on 06.04.2005
 * Version 1.0
 * 
 * This class creates a sorted structure for all elements of all statemachine given and
 * provides rules for all components and several general rules;
 * these are for testing in a ATP with TPTP-Syntax
 * 
 * 
 * Structure of Statemachines
 * 
 * HashMap allStateMachines: Key  : (String : name of statemachine)
 * 						     Value: (HashMap: aStateMachine)
 * 
 * HashMap aStateMachine: Key  : (String : type of component)
 * 						  Value: (HashMap: componentsOfStateMachine)
 * 
 * HashMap componentsOfStateMachine: Key  : (String : name of a component)
 * 							  		 Value: (HashMap: elementsOfComponents)
 * 
 * HashMap elementsOfComponents: Key  : (String: type of element)
 * 								 Value: (String: expression of element) 
 *
 *
 *
 * HashMap: All Messages: Key  : (String: Target State Machine Name)
 * 				 		  Value: (HashMap: Activities Of Target State Machine)
 * 
 * HashMap: Activities Of Target State Machine: Key  : (String: Type of Activity/Action)
 * 					  		   			   Value: (HashMap: Messages From A State Machine)
 * 
 * HashMap: Messages From A State Machine: Key  : (String: Source State Machine Name)
 * 										   Value: (HashMap: Affected Components By) 
 * 
 * HashMap: Affected Components By: Key  : (String: Expression)
 * 			 					    Value: (Vector: affected Componentes)
 * 
 *
 *
 *
 */

package tum.umlsec.viki.tools.uml2tptp.statechartreader;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.omg.uml.behavioralelements.statemachines.CompositeState;
import org.omg.uml.behavioralelements.statemachines.FinalState;
import org.omg.uml.behavioralelements.statemachines.Pseudostate;
import org.omg.uml.behavioralelements.statemachines.SimpleState;
import org.omg.uml.behavioralelements.statemachines.StateMachinesPackage;
import org.omg.uml.behavioralelements.statemachines.StateVertex;
import org.omg.uml.behavioralelements.statemachines.Transition;
import org.omg.uml.foundation.core.Parameter;

import tum.umlsec.viki.framework.mdr.IMdrContainer;


class Statechartreader{
    
    
    private IMdrContainer mdrContainer;

    private HashMap allActivities = new HashMap();   
    private HashMap allStateMachines = new HashMap();
    private HashMap transitionsWithNoTrigger = new HashMap();
    private HashMap allMessages = new HashMap();
    private InputOutputRules inputOutputRules; 
    
    
    /**
     * this constructur must be used when instantiating Statechartreader
     * @param _mdrContainer
     */
    Statechartreader(IMdrContainer _mdrContainer){
        mdrContainer = _mdrContainer;
        inputOutputRules = new InputOutputRules();
        initialise();
    }

    
    /**
     * initialise() starts filling the Hash Maps with the elements of the Statechartdiagramms 
     *
     */
    private void initialise(){
        
        StateMachinesPackage _stateMachinesPackage;
        String _actualStateMachineName;
        String _tempString;
        HashMap _aStateMachine;
        HashMap _componentsOfStateMachine;
        Iterator _tempIterator;
        
        _stateMachinesPackage = mdrContainer.getUmlPackage().getStateMachines();
                
//		setting maps for all intitial states (look at beginning of code for HashMap structure)
        _tempIterator = _stateMachinesPackage.getPseudostate().refAllOfClass().iterator();
                
        while(_tempIterator.hasNext()){
            _tempString = new String();    
            Pseudostate _initialState;
            
            _initialState = (Pseudostate) _tempIterator.next();            
            _actualStateMachineName = getStateMachineOf(_initialState);
            
            if(allStateMachines.containsKey(_actualStateMachineName)){
                _aStateMachine = (HashMap) allStateMachines.get(_actualStateMachineName);
                
                if(_aStateMachine.containsKey("initialState")){
                    _componentsOfStateMachine = (HashMap) _aStateMachine.get("initialState");
                }
                else{
                    _componentsOfStateMachine = new HashMap();
                }
            }
            else{
                _aStateMachine = new HashMap();
                _componentsOfStateMachine = new HashMap();
            }
            
            _tempString = correctName(_initialState.getName()); 
            _componentsOfStateMachine.put(_tempString, setMapOf(_actualStateMachineName,_initialState));
            _aStateMachine.put("initialState", _componentsOfStateMachine);
            allStateMachines.put(_actualStateMachineName, _aStateMachine);            
        }
        
//      setting maps for all composite states (look at beginning of code for HashMap structure)
        if(_stateMachinesPackage.getCompositeState() != null){
            _tempIterator = _stateMachinesPackage.getCompositeState().refAllOfClass().iterator();
            
            while(_tempIterator.hasNext()){
                _tempString = new String();
                CompositeState _compositeState;
                
                _compositeState = (CompositeState) _tempIterator.next();
                
                if(_compositeState.getContainer() != null){
                    _actualStateMachineName = getStateMachineOf(_compositeState);
                    
                    if(allStateMachines.containsKey(_actualStateMachineName)){
                        _aStateMachine = (HashMap) allStateMachines.get(_actualStateMachineName);
                        
                        if(_aStateMachine.containsKey("compositeState")){
                            _componentsOfStateMachine = (HashMap) _aStateMachine.get("compositeState");
                        }
                        else{
                            _componentsOfStateMachine = new HashMap();
                        }
                    }
                    else{
                        _aStateMachine = new HashMap();
                        _componentsOfStateMachine = new HashMap();
                    }
                    
                    _tempString = correctName(_compositeState.getName());
                    _componentsOfStateMachine.put(_tempString, setMapOf(_actualStateMachineName,_compositeState));
                    _aStateMachine.put("compositeState", _componentsOfStateMachine);
                    allStateMachines.put(_actualStateMachineName, _aStateMachine);            
                }
            }    
        }
        
//      setting maps for all simple states (look at beginning of code for HashMap structure)
        if(_stateMachinesPackage.getSimpleState() != null){
            _tempIterator = _stateMachinesPackage.getSimpleState().refAllOfClass().iterator();
                     
            while(_tempIterator.hasNext()){
                _tempString = new String();
                SimpleState _simpleState;
                
                _simpleState = (SimpleState) _tempIterator.next();

                _actualStateMachineName = getStateMachineOf(_simpleState);
                
                if(allStateMachines.containsKey(_actualStateMachineName)){
                    _aStateMachine = (HashMap) allStateMachines.get(_actualStateMachineName);
                    
                    if(_aStateMachine.containsKey("simpleState")){
                        _componentsOfStateMachine = (HashMap) _aStateMachine.get("simpleState");
                    }
                    else{
                        _componentsOfStateMachine = new HashMap();
                    }
                }
                else{
                    _aStateMachine = new HashMap();
                    _componentsOfStateMachine = new HashMap();
                }
                
                _tempString = correctName(_simpleState.getName());
                _componentsOfStateMachine.put(_tempString, setMapOf(_actualStateMachineName,_simpleState));
                _aStateMachine.put("simpleState", _componentsOfStateMachine);
                allStateMachines.put(_actualStateMachineName, _aStateMachine);            
            }   
        }    
        
//      setting maps for all final states (look at beginning of code for HashMap structure)
        _tempIterator = _stateMachinesPackage.getFinalState().refAllOfClass().iterator();
        
        while(_tempIterator.hasNext()){
            _tempString = new String();
            FinalState _finalState;
            
            _finalState = (FinalState) _tempIterator.next();

            _actualStateMachineName = getStateMachineOf(_finalState);
            
            if(allStateMachines.containsKey(_actualStateMachineName)){
                _aStateMachine = (HashMap) allStateMachines.get(_actualStateMachineName);
                
                if(_aStateMachine.containsKey("finalState")){
                    _componentsOfStateMachine = (HashMap) _aStateMachine.get("finalState");
                }
                else{
                    _componentsOfStateMachine = new HashMap();
                }
            }
            else{
                _aStateMachine = new HashMap();
                _componentsOfStateMachine = new HashMap();
            }
            
            _tempString = correctName(_finalState.getName());
            _componentsOfStateMachine.put(_tempString, setMapOf(_actualStateMachineName,_finalState));
            _aStateMachine.put("finalState", _componentsOfStateMachine);
            allStateMachines.put(_actualStateMachineName, _aStateMachine);            
        }
        
//      setting maps for all transitions (look at beginning of code for HashMap structure)
        _tempIterator = _stateMachinesPackage.getTransition().refAllOfClass().iterator();
        
        while(_tempIterator.hasNext()){
            _tempString = new String();
            Transition _transition;
            
            _transition = (Transition) _tempIterator.next();
            
            _actualStateMachineName = getStateMachineOf(_transition);
                
            if(allStateMachines.containsKey(_actualStateMachineName)){
                _aStateMachine = (HashMap) allStateMachines.get(_actualStateMachineName);
                
                if(_aStateMachine.containsKey("transition")){
                    _componentsOfStateMachine = (HashMap) _aStateMachine.get("transition");
                }
                else{
                    _componentsOfStateMachine = new HashMap();
                }
            }
            else{
                _aStateMachine = new HashMap();
                _componentsOfStateMachine = new HashMap();
            }
            
            _tempString = correctName(_transition.getName());
            _componentsOfStateMachine.put(_tempString, setMapOf(_actualStateMachineName,_transition));
            _aStateMachine.put("transition", _componentsOfStateMachine);
            allStateMachines.put(_actualStateMachineName, _aStateMachine);
        }
    }
    
    
    /**
     * this is the uppermost method called by TPTPGenerator to obtain all Rules
     * @return Vector containing Vectors for each StateMachine and for general Rules
     */
    Vector getRulesOfAllStateMachines(){
        
        Vector _result = new Vector();
        Vector _tempVector;
        Iterator _tempIterator;
        
        _tempVector = createRulesOfAllInitialStatesOfAllStateMachines();
        _result.addElement(_tempVector);
        
        
        _tempVector = createGeneralRules();
        _result.addElement((_tempVector));
        
        //getting rules for each statemachine
        _tempIterator = allStateMachines.keySet().iterator();
        
        while(_tempIterator.hasNext()){
            _result.addElement(createRulesOfStateMachine((String) _tempIterator.next()));
        }
        
        return _result;
    }
    
    
    /**
     * creating general rules valid for all state machines 
     * and calls the creation of rules for the in- and ouput 
     * @return
     */
    private Vector createGeneralRules(){
        Vector _result = new Vector();
        
        _result.addAll(createRuleOfInputAndOutput());
        _result.addElement("%----------------Empty Trigger, Guard, Effect Rule--------------------");
        _result.addElement("input_formula(emptyGuard,axiom, (![S,N] : guard(S,empty,N))).");
        _result.addElement("input_formula(emptyTrigger,axiom, (![S,N] : trigger(S,empty,N))).");
        _result.addElement("");
        
        return _result;
    }
    
    
    /**
     * second uppermost method called by getRulesOfAllStateMachines(); gets rules for each statemachine
     * @param _actualStateMachine
     * @return Vector containing rules of _actualStateMachine as Strings 
     */
    private Vector createRulesOfStateMachine(String _actualStateMachine){
                
        Vector _result = new Vector();
        
        _result.addAll(createRuleOfInitialState(_actualStateMachine));
        _result.addAll(createRulesOfSimpleStates(_actualStateMachine));
        _result.addAll(createRulesOfCompositeStates(_actualStateMachine));
        _result.addAll(createRulesOfFinalStates(_actualStateMachine));
        _result.addAll(createRulesOfTransitions(_actualStateMachine));
        
        return _result;        
    }
    
    
    /**
     * this method creates general rules concerning the in- and output 
     * and calls returnRules() to obtain specific rules  
     * @return Vector containing rules as String
     */
    private Vector createRuleOfInputAndOutput(){
        
        Vector _result = new Vector();
        
        String _aRule = new String();
        Vector _allStateMachines = new Vector();
        
        String _actualStateMachineName;
        HashMap _activitiesOfTargetStateMachine;
        HashMap _messagesFromAStateMachine;
        HashMap _affectedComponentsBy;
        Vector _affectedComponent;
        Vector _transitionsOfAStateMachineWithNoTrigger;
        Vector _partOfRule_TransitionsOfAStateMachineWithNoTrigger = new Vector();
        Vector _partOfRule_TransitionsOfAStateMachineWithATrigger = new Vector();
        String _messageID;
        String _messageIDs;
        Vector _messages;
        String _transitions;
        String _transitionsWithATrigger;
        String _transitionsWithNoTrigger;
        Iterator _messageIterator;
        Iterator _stateMachineWithActivitiesIterator;
        
        String _partOfRule_allStateMachines = new String();
        String _partOfRule_GlobalCounter = new String();
        
        _allStateMachines.addAll(allStateMachines.keySet());
        
        for(int i = 0; i < allStateMachines.keySet().size(); i++){
                    
            if(_partOfRule_GlobalCounter.length() == 0){
                _partOfRule_GlobalCounter = (String) _allStateMachines.get(i) + "_counter(N)";
                _partOfRule_allStateMachines = "(S = " + (String) _allStateMachines.get(i) + ")"; 
            }
            else{
                _partOfRule_GlobalCounter = _partOfRule_GlobalCounter + " & "  + (String) _allStateMachines.get(i) + "_counter(N)";
                _partOfRule_allStateMachines = _partOfRule_allStateMachines + " | (S = " + (String) _allStateMachines.get(i) + ")";
            }                
        }    

        if(allActivities.keySet().size() != 0){
            
            
            for(int i = 0; i < _allStateMachines.size(); i++){
                _transitionsWithNoTrigger = new String();
                _actualStateMachineName = (String) _allStateMachines.get(i);
	            
                if(transitionsWithNoTrigger.containsKey(_actualStateMachineName)){
                    _transitionsOfAStateMachineWithNoTrigger = (Vector) transitionsWithNoTrigger.get(_actualStateMachineName);
    				
    				for(int j = 0; j < _transitionsOfAStateMachineWithNoTrigger.size(); j++){
    				    
    				    if(_transitionsWithNoTrigger.length() == 0){
    	                    _transitionsWithNoTrigger = "transition(" + _actualStateMachineName + "," + _transitionsOfAStateMachineWithNoTrigger.get(j) + ",succ(N))";    
    	                }
    	                else{
    	                    _transitionsWithNoTrigger = _transitionsWithNoTrigger + " | transition(" + _actualStateMachineName + "," + _transitionsOfAStateMachineWithNoTrigger.get(j) + ",succ(N))";
    	                }
    				}
    				_aRule = "((S = " + _actualStateMachineName + ") & (" + _transitionsWithNoTrigger + "))";
    				_partOfRule_TransitionsOfAStateMachineWithNoTrigger.addElement(_aRule);    
                }                
            }
            
	        _stateMachineWithActivitiesIterator = allActivities.keySet().iterator();
	        
	        while(_stateMachineWithActivitiesIterator.hasNext()){
	            _transitionsWithATrigger = new String();
	            
	            _actualStateMachineName = (String) _stateMachineWithActivitiesIterator.next();
	            _activitiesOfTargetStateMachine = (HashMap) allActivities.get(_actualStateMachineName);
	            
	            if(_activitiesOfTargetStateMachine.containsKey("triggerWithMessage")){
	                _messageIDs = new String();
		            _messagesFromAStateMachine = (HashMap) _activitiesOfTargetStateMachine.get("triggerWithMessage");
		            _affectedComponentsBy = (HashMap) _messagesFromAStateMachine.get("none");
	                _messageIterator = _affectedComponentsBy.keySet().iterator();
	                
	                while(_messageIterator.hasNext()){
	                    _transitions = new String();
	                    
	                    
	                    _messageID = (String) _messageIterator.next();
	                    _affectedComponent = (Vector) _affectedComponentsBy.get(_messageID);
	                    
	                    for(int j = 0; j < _affectedComponent.size(); j++){
	                        
	                        if(_transitions.length() == 0){
	                            _transitions = "transition(" + _actualStateMachineName + "," + _affectedComponent.get(j) + ",succ(N))";    
	                        }
	                        else{
	                            _transitions = _transitions + " | transition(" + _actualStateMachineName + "," + _affectedComponent.get(j) + ",succ(N))";
	                        }      
	                    }
	                    
	                    if(_transitionsWithATrigger.length() == 0){
	                        _transitionsWithATrigger = "((MsgID = " + _messageID + ") & (" + _transitions + "))";
	                    }
	                    else{
	                        _transitionsWithATrigger = _transitionsWithATrigger + " | ((MsgID = " + _messageID + ") & (" + _transitions + "))";
	                    }
	                     	                    
	                    if(_messageIDs.length() == 0){
	                        _messageIDs = "(MsgID = " + _messageID + ")";	                        
	                    }
	                    else{
	                        _messageIDs = _messageIDs + " | (MsgID = " + _messageID + ")";
	                    }
	                }
	                
					_partOfRule_TransitionsOfAStateMachineWithATrigger.addElement("((S = " + _actualStateMachineName + ") & (" + _transitionsWithATrigger + "))");
	            }
	        }
	        
	        _result.addElement("");
	        _result.addElement("%----------------Global Counter Rule--------------------");
	        _aRule = "input_formula(globalCounter,axiom, (![N] : ((" + _partOfRule_GlobalCounter + ") => global(N)))).";
	        _result.addElement(_aRule);
	        _result.addElement("");

	        _result.addElement("%----------------All State Machine Names--------------------");
	        _aRule = "input_formula(allStateMachines,axiom, (![S] : (stateMachine(S) <=> (" + _partOfRule_allStateMachines + ")))).";
	        _result.addElement(_aRule);
	        _result.addElement("");

	        if(allMessages.keySet().size() > 0){
	            _allStateMachines.clear();
	            _allStateMachines.addAll(allMessages.keySet());

		        for(int i = 0; i < _allStateMachines.size(); i++){
		            
		            if(i == 0){
		                _aRule = "((S = " + (String) _allStateMachines.get(i) + ")";
			            
			            _messages = (Vector) allMessages.get(_allStateMachines.get(i));
			            
			            for(int j = 0; j < _messages.size(); j++){
			                
			                if(j == 0){
			                    _aRule = _aRule + " & ((MsgID = " + (String) _messages.get(j) + ")";
			                }
			                else{
			                    _aRule = _aRule + " | (MsgID = " + (String) _messages.get(j) + ")";
			                }
			            }
			            
			            _aRule = _aRule +"))";    
		            }
		            else{
		                _aRule = _aRule + " | ((S = " + (String) _allStateMachines.get(i) + ")";
			            
			            _messages = (Vector) allMessages.get(_allStateMachines.get(i));
			            
			            for(int j = 0; j < _messages.size(); j++){
			                
			                if(j == 0){
			                    _aRule = _aRule + " & ((MsgID = " + (String) _messages.get(j) + ")";
			                }
			                else{
			                    _aRule = _aRule + " | (MsgID = " + (String) _messages.get(j) + ")";
			                }
			            }
			            
			            _aRule = _aRule +"))";     
		            }	              
		        }
		        
		        _result.addElement("%----------------All Messages--------------------");
		        _aRule = "input_formula(allMessages,axiom, (![S,MsgID] : (stateMachineWithMessages(S,MsgID) <=> (" + _aRule + ")))).";
		        _result.addElement(_aRule);
		        _result.addElement("");
	        }
	        
	        _result.addElement("%------------------------General Input- Output Rules----------------------");
	        _result.addElement("");
	        _aRule = "input_formula(initQueue,axiom, (![S,MsgID] : ((stateMachineWithMessages(S,MsgID))" +
	        												" => (queuePointer(S,MsgID,0,0) & queueSize(S,MsgID,0,0))))).";
	        _result.addElement(_aRule);
	        _result.addElement("");
	        
	        _aRule = "input_formula(effectOccures,axiom, (![S1,S2,MsgID,N] : ((effect(S1,S2,MsgID,N))" +
															" => (hasEffectToSend(S1,N))))).";								
	        _result.addElement(_aRule);
	        _result.addElement("");
	        
	        _aRule = "input_formula(noEffectOccures,axiom, (![S,N] : ((effect(S,none,empty,N))" +
	        												" => (hasNoEffectToSend(S,N))))).";
	        _result.addElement(_aRule);
	        _result.addElement("");
	        /*	        
	        for(int i = 0; i < _partOfRule_TransitionsOfAStateMachineWithATrigger.size(); i++){
	            _aRule = "input_formula(toTrigger,axiom, " +
	            		"(![S,MsgID,N] : (((" + _partOfRule_TransitionsOfAStateMachineWithATrigger.get(i) + ") & stateMachineWithMessages(S,MsgID))" +
						" => (hasTrigger(S,MsgID,succ(N)))))).";						
		        _result.addElement(_aRule);
				_result.addElement("");
	        }
	        */
	        for(int i = 0; i < _partOfRule_TransitionsOfAStateMachineWithATrigger.size(); i++){
	            if(i == 0){
	                _aRule = "(" + _partOfRule_TransitionsOfAStateMachineWithATrigger.get(i) + ")";
	            }
	            else{
	                _aRule = _aRule + " | (" + _partOfRule_TransitionsOfAStateMachineWithATrigger.get(i) + ")";
	            }
			}
	        _aRule = "input_formula(toTrigger,axiom, (![S,MsgID,N] : (stateMachineWithMessages(S,MsgID) & " + _aRule + ") => (hasTrigger(S,MsgId,succ(N))))).";						
	        _result.addElement(_aRule);
	        _result.addElement("");   
	        
	        for(int i = 0; i < _partOfRule_TransitionsOfAStateMachineWithNoTrigger.size(); i++){
	            if(i == 0){
	                _aRule = "(" + _partOfRule_TransitionsOfAStateMachineWithNoTrigger.get(i) + ")";
	            }
	            else{
	                _aRule = _aRule + " | (" + _partOfRule_TransitionsOfAStateMachineWithNoTrigger.get(i) + ")";
	            }
			}
	        _aRule = "input_formula(noTrigger,axiom, (![S,N] : ((" + _aRule + ") => (hasNoTrigger(S,succ(N)))))).";						
	        _result.addElement(_aRule);
	        _result.addElement("");   
	        
	        _result.addElement("%-------------------------Traffic Control-----------------------");
	        _result.addAll(inputOutputRules.returnRules());
	        
	    }
        
        return _result;
    }
    
    
    /**
     * a general rule to obtain all initial states of all statemachines
     * @return Vector containing a rule as a String
     */
    private Vector createRulesOfAllInitialStatesOfAllStateMachines(){
        Vector _result = new Vector();
        Vector _initialStates;
        Iterator _initialStatesIterator;
        String _allInitialStates = new String();
        String _aRule = new String();
        
        _initialStates = getAllInitialStates();
        _initialStatesIterator = _initialStates.iterator();
        
        while(_initialStatesIterator.hasNext()){
            
            if(_allInitialStates.length() == 0){
                _allInitialStates = "((S = " + (String) _initialStatesIterator.next() + ") & (I = " + (String) _initialStatesIterator.next() + ") & (N = 0))";                
            }
            else{
                _allInitialStates = _allInitialStates + " | ((S = " + (String) _initialStatesIterator.next() + ") & (I = " + (String) _initialStatesIterator.next() + ") & (N = 0))";
            }
        }
                
        _result.addElement("%-------Initial States of State Machines---------");
        _aRule = "input_formula(initialStates, axiom, (![S,I,N] : (initial(S,I,N) <=> (" + _allInitialStates + ")))).";
        _result.addElement(_aRule);
        
        return _result;
    }
    
    
    /**
     * called by createRulesOfStateMachine(String _actualStateMachine) to get a rule for an initial state of a statemachine
     * @param _actualStateMachine
     * @return Vector containing a rule as String
     */
    private Vector createRuleOfInitialState(String _actualStateMachine){
        
        Vector _result = new Vector();
        HashMap _initialStates;
        HashMap _aInitialState;
        String _aRule;
        String _aInitialStateName;
        Iterator _initialStatesIterator;
        
        _initialStates = (HashMap) ((HashMap) allStateMachines.get(_actualStateMachine)).get("initialState");
        _initialStatesIterator = _initialStates.keySet().iterator();
        
        _result.addElement("%----------------------A State Machine----------------");
        _result.addElement("");
        _result.addElement("%-----------Rule Of Initial State-----------");
        
        while(_initialStatesIterator.hasNext()){
            _aRule = new String();
            
            _aInitialStateName = (String) _initialStatesIterator.next();
            _aInitialState = (HashMap) _initialStates.get(_aInitialStateName);
            _aRule = "initial(" + _actualStateMachine + "," + _aInitialStateName + ",N) => (" + _actualStateMachine + "_counter(succ(N)) & " + _aInitialState.get("outgoing") + ")";
            _aRule = "input_formula(ruleOf_InitialStateOf_" +  _aInitialState.get("container") + ",axiom, (![N] : (" + _aRule + "))).";
            _result.addElement(_aRule);
        }
        
        return _result;
    }
    
    
    /**
     * called by createRulesOfStateMachine(String _actualStateMachine) to get rules for simple states of a statemachine
     * @param _actualStateMachine
     * @return Vector containing rules for each simple state as String
     */    
    private Vector createRulesOfSimpleStates(String _actualStateMachine){
        
        Vector _result = new Vector();
        HashMap _simpleStates;
        HashMap _aSimpleState;
        String _aRule;
        String _aSimpleStateName;
        Iterator _statesIterator;
        
        if(((HashMap) allStateMachines.get(_actualStateMachine)).get("simpleState") != null){
            _simpleStates = (HashMap) ((HashMap) allStateMachines.get(_actualStateMachine)).get("simpleState");
            _statesIterator = _simpleStates.keySet().iterator();
            
            _result.addElement("%-------Rules Of Simple State---------");
            
            while(_statesIterator.hasNext()){
                
                _aSimpleStateName = (String) _statesIterator.next();
                _aSimpleState = (HashMap) _simpleStates.get(_aSimpleStateName);
                _aRule = "(state(" + _actualStateMachine + "," + _aSimpleStateName + ",N)) => (" + _actualStateMachine + "_counter(succ(N)) & " + _aSimpleState.get("outgoing") + " & " + _aSimpleState.get("exitAction") + ")";
                _aRule = "input_formula(ruleOf" + _aSimpleState.get("depth") + "_" + _aSimpleStateName + ",axiom, (![N] : (" + _aRule + "))).";
                _result.addElement(_aRule);
            }
        }
        
        return _result;
    }
    
    
    /**
     * called by createRulesOfStateMachine(String _actualStateMachine) to get rules for composite states of a statemachine
     * @param _actualStateMachine
     * @return Vector containing rules for each composite state as String
     */    
    private Vector createRulesOfCompositeStates(String _actualStateMachine){
        
        Vector _result = new Vector();
        HashMap _compositeStates;
        HashMap _aCompositeState;
        String _aRule;
        String _aCompositeStateName;
        
        if(((HashMap) allStateMachines.get(_actualStateMachine)).get("compositeState") != null){
            _compositeStates = (HashMap) ((HashMap) allStateMachines.get(_actualStateMachine)).get("compositeState");
            Iterator _statesIterator;
            
            _statesIterator = _compositeStates.keySet().iterator();
            
            _result.addElement("%-------Initial,Final States Of Composite States---------"); 
        
            while(_statesIterator.hasNext()){
                _aRule = new String();
                
                _aCompositeStateName = (String) _statesIterator.next();            
                _aCompositeState = (HashMap) _compositeStates.get(_aCompositeStateName);
                
                _aRule = "(state(" + _actualStateMachine + "," + _aCompositeStateName + ",N)) => (" + _aCompositeState.get("subinitial") + ")";
                _aRule = "input_formula(initialOf_" + _aCompositeState.get("typeOfState") + _aCompositeState.get("depth") + "_" + _aCompositeStateName + ",axiom, (![N] : (" + _aRule + "))).";
                
                _result.addElement(_aRule);
                
                _aRule = _aCompositeState.get("subfinal") + " => (" + _actualStateMachine + "_counter(succ(N)) & " + _aCompositeState.get("outgoing") + " & " + _aCompositeState.get("exitAction") + ")";
                _aRule = "input_formula(leaving_" + _aCompositeState.get("typeOfState") + _aCompositeState.get("depth") + "_" + _aCompositeStateName + ",axiom, (![N] : (" + _aRule + "))).";
              
                _result.addElement(_aRule);
            }
        }
        
        return _result;
    }
    
    
    /**
     * called by createRulesOfStateMachine(String _actualStateMachine) to get rules for final states of a statemachine
     * @param _actualStateMachine
     * @return Vector containing rules for each final state as String
     */    
    private Vector createRulesOfFinalStates(String _actualStateMachine){
        
        Vector _result = new Vector();
        HashMap _allFinalStates;
        HashMap _aFinalState;
        Iterator _statesIterator;
        String _aFinalStateName;
        String _aRule = new String();
        String _aRule2 = new String();
        
        _allFinalStates = (HashMap) ((HashMap) allStateMachines.get(_actualStateMachine)).get("finalState");
        _statesIterator = _allFinalStates.keySet().iterator();
        
        _result.addElement("%-------Final States Of State Machines---------");
        
        while(_statesIterator.hasNext()){
            _aFinalStateName = (String) _statesIterator.next();
            _aFinalState = (HashMap) _allFinalStates.get(_aFinalStateName);
            
            if(((String)_aFinalState.get("depth")).length() == 0){
                if(_aRule.length() <= 0){
                    _aRule = "(F = " + _aFinalStateName + ")";
                }
                else{
                    _aRule = _aRule + " | (F = " + _aFinalStateName + ")";
                }
            }
        }
        
        _aRule = "(global(N) & final(" + _actualStateMachine + ",F,N) & (" + _aRule + ")) => (" + _actualStateMachine + "_counter(succ(N)) & final(" + _actualStateMachine + ",F,succ(N)))";
        _aRule = "input_formula(endOf_" + _actualStateMachine + ",axiom, (![F,N] : (" + _aRule + "))).";
        _result.addElement(_aRule);
        
        return _result;
    }
    
    
    /**
     * called by createRulesOfStateMachine(String _actualStateMachine) to get rules for transitions of a statemachine
     * @param _actualStateMachine
     * @return Vector containing rules for each transition as String
     */    
    private Vector createRulesOfTransitions(String _actualStateMachine){
        
        Vector _result = new Vector();
        String _aGeneralRule;
        String _aRule;        
        HashMap _stateMachine; 
        HashMap _transitions;
        HashMap _aTransition;
        String _aTransitionName;
        HashMap _statesOfAType;
        String _targetStateName;
        HashMap _targetState;
        Iterator _transitionIterator;
        
        _stateMachine = (HashMap) allStateMachines.get(_actualStateMachine);
        _transitions = (HashMap) _stateMachine.get("transition");
        _transitionIterator = _transitions.keySet().iterator();
        
        _result.addElement("%---------------Transition Rules---------------------");
        
        while(_transitionIterator.hasNext()){
            _aGeneralRule = new String();
            _aRule = new String();
            
            _aTransitionName = (String) _transitionIterator.next();
            _aTransition = (HashMap) _transitions.get(_aTransitionName);
            _targetStateName = (String) _aTransition.get("target");
             
            if(_stateMachine.containsKey("compositeState")){
                _statesOfAType = (HashMap)_stateMachine.get("compositeState");
                
                if(_statesOfAType.containsKey(_targetStateName)){
                    _targetState = (HashMap) _statesOfAType.get(_targetStateName);
                    _aRule = "((global(N) & transition(" + _actualStateMachine + "," + _aTransitionName + ",N) & " + _aTransition.get("trigger") + " & " + _aTransition.get("guard") +  ") => (state(" + _actualStateMachine + "," + _targetStateName + ",N) & " + _aTransition.get("effect") + " & " + _targetState.get("entryAction") + " & " + _targetState.get("doActivity") + "))";
                    //waiting a cycle if some statements are not true
                    _aRule = _aRule + " & ((global(N) & transition(" + _actualStateMachine + "," + _aTransitionName + ",N) & ~(" + _aTransition.get("trigger") + " & " + _aTransition.get("guard") +  ")) => (" + _actualStateMachine + "_counter(succ(N)) & transition(" + _actualStateMachine + "," + _aTransitionName + ",succ(N))))";
                    _aRule = "(" + _aRule + ")";
                }
            }
            
            if(_stateMachine.containsKey("simpleState")){
                _statesOfAType = (HashMap)_stateMachine.get("simpleState");
                
                if(_statesOfAType.containsKey(_targetStateName)){
                    _targetState = (HashMap) _statesOfAType.get(_targetStateName);
                    _aRule = "((global(N) & transition(" + _actualStateMachine + "," + _aTransitionName + ",N) & " + _aTransition.get("trigger") + " & " + _aTransition.get("guard") +  ") => (state(" + _actualStateMachine + "," + _targetStateName + ",N) & " + _aTransition.get("effect") + " & " + _targetState.get("entryAction") + " & " + _targetState.get("doActivity") + "))";
                    //waiting a cycle if some statements are not true
                    _aRule = _aRule + " & ((global(N) & transition(" + _actualStateMachine + "," + _aTransitionName + ",N) & ~(" + _aTransition.get("trigger") + " & " + _aTransition.get("guard") +  ")) => (" + _actualStateMachine + "_counter(succ(N)) & transition(" + _actualStateMachine + ","  + _aTransitionName + ",succ(N))))";
                    _aRule = "(" + _aRule + ")";                    
                }
            }
            
            if(_stateMachine.containsKey("finalState")){
                _statesOfAType = (HashMap)_stateMachine.get("finalState");
                
                if(_statesOfAType.containsKey(_targetStateName)){
                    _targetState = (HashMap) _statesOfAType.get(_targetStateName);
                    _aRule = "((global(N) & transition(" + _actualStateMachine + "," + _aTransitionName + ",N) & " + _aTransition.get("trigger") + " & " + _aTransition.get("guard") +  ") => (final(" + _actualStateMachine + "," + _targetStateName + ",N)" + " & " + _aTransition.get("effect") + "))";
                    _aRule = _aRule + " & ((global(N) & transition(" + _actualStateMachine + "," + _aTransitionName + ",N) & ~(" + _aTransition.get("trigger") + " & " + _aTransition.get("guard") +  ")) => (" + _actualStateMachine + "_counter(succ(N)) & transition(" + _actualStateMachine + ","  + _aTransitionName + ",succ(N))))";
                    _aRule = "(" + _aRule + ")";
                }
            }
                          
            _aRule = "input_formula(ruleOf_" + _aTransitionName + ",axiom, (![N] : (" + _aRule + "))).";
            
            _result.addElement(_aRule);
        }
        
        return _result;
    }
    
    
    /**
     * called by createRulesOfAllInitialStatesOfAllStateMachines() to get the initial state of all statemachines
     * @return Vector containing
     */
    private Vector getAllInitialStates(){
        
        Vector _result = new Vector();
        Iterator _stateMachineIterator;
        String _actualStateMachineName;
        
        _stateMachineIterator = allStateMachines.keySet().iterator();
        
        while(_stateMachineIterator.hasNext()){
             _actualStateMachineName = (String) _stateMachineIterator.next();
             _result.addElement(_actualStateMachineName);
            _result.addElement(getInitialState(_actualStateMachineName));
        }
        
        return _result;
    }
    
    
    /**
     * called by createRulesOfAllInitialStatesOfAllStateMachines() to obtain the initial state of 
     * a statemachine without the initial states of composite states
     * @param _actualStateMachine
     * @return String for the name of an initial state 
     */
    private String getInitialState(String _actualStateMachine){
        
        String _result = new String();
        HashMap _statesOfAType;
        HashMap _aInitialState;
        String _aInitialStateName;
        Iterator _initialStatesIterator;
        
        _statesOfAType = (HashMap) ((HashMap) allStateMachines.get(_actualStateMachine)).get("initialState");
        _initialStatesIterator = _statesOfAType.keySet().iterator();
        
        while(_initialStatesIterator.hasNext()){
            _aInitialStateName = (String) _initialStatesIterator.next();
            _aInitialState = (HashMap) _statesOfAType.get(_aInitialStateName);
            
            if(((String)_aInitialState.get("container")).equals(_actualStateMachine)){
                _result = _aInitialStateName;
            }
        } 
        
        return _result;
    }
    
    
    /**
     * called by initialise() to get the name of a state machine of a component to use it as a key
     * @param _actualState
     * @return StateMachine
     */
    private String getStateMachineOf(Pseudostate _actualState){
        
        String _result = new String();
        CompositeState _tempCompositeState;
        
        _tempCompositeState = _actualState.getContainer();
        
        while(_tempCompositeState.getStateMachine() == null) {
            _tempCompositeState = _tempCompositeState.getContainer();
        }
        
        _result = correctName(_tempCompositeState.getStateMachine().getName());
        return _result;
    }
    
    
    /**
     * called by initialise() to get the name of a state machine of a component to use it as a key
     * @param _actualState
     * @return StateMachine
     */    
    private String getStateMachineOf(CompositeState _actualState){
        
        String _result = new String(); 
        CompositeState _tempCompositeState;
        
        _tempCompositeState = _actualState.getContainer();
        
        while(_tempCompositeState.getStateMachine() == null) {
            _tempCompositeState = _tempCompositeState.getContainer();
        }
        
        _result = correctName(_tempCompositeState.getStateMachine().getName());
        
        return _result;
    }    
    
    
    /**
     * called by initialise() to get the name of a state machine of a component to use it as a key
     * @param _actualState
     * @return StateMachine
     */
    private String getStateMachineOf(SimpleState _actualState){
        
        String _result = new String();
        CompositeState _tempCompositeState;
        
        _tempCompositeState = _actualState.getContainer();
        
        while(_tempCompositeState.getStateMachine() == null) {
            _tempCompositeState = _tempCompositeState.getContainer();
        }
        _result = correctName(_tempCompositeState.getStateMachine().getName());
        
        return _result;
    }    
    
    
    /**
     * called by initialise() to get the name of a state machine of a component to use it as a key
     * @param _actualState
     * @return StateMachine
     */
    private String getStateMachineOf(FinalState _actualState){
        
        String _result = new String();
        CompositeState _tempCompositeState;
        
        _tempCompositeState = _actualState.getContainer();
        
        while(_tempCompositeState.getStateMachine() == null) {
            _tempCompositeState = _tempCompositeState.getContainer();
        }
        
        _result = correctName(_tempCompositeState.getStateMachine().getName());
        
        return _result;
    }    
    
    
    /**
     * called by initialise() to get the name of a state machine of a component to use it as a key
     * @param _actualTransition
     * @return StateMachine
     */
    private String getStateMachineOf(Transition _actualTransition){
        
        String _result = new String();
        
        _result = correctName(_actualTransition.getStateMachine().getName());            
        
        return _result;
    } 
     
    
    /**
     * called by initialise() the Hash Maps; 
     * this Method sets the needed parts of a component for creating the rules;
     * all Keys of a component are listed below 
     * @param _actualState
     * @return HashMap containing Strings as values 
     */
    private HashMap setMapOf(String _actualStateMachineName, Pseudostate _actualState){
        
        /*
         * Map Keys:
         * depth
         * container
         * typeOfState
         * outgoing
         */
        
        HashMap _mapOf = new HashMap();
        
        String _tempString = new String();
        String _aTransitionName;
        Vector _allOutgoing;
        Transition _aTransition;
        CompositeState _tempState;
        
        _tempState = _actualState.getContainer();
        _tempString = "";
        
        //depth of state
        if(_tempState.getName().equals("state_machine_top")){
            _mapOf.put("container", _actualStateMachineName);
            _mapOf.put("depth", _tempString);
        }
        else{
            _tempString = correctName(_tempState.getName());
            _mapOf.put("container", _tempString);        
            //_tempString = "Sub";
            
            while( ! (_tempState.getName().equals("state_machine_top"))){
                _tempState = _tempState.getContainer();
                _tempString = _tempString + "_Sub";
            }
            _mapOf.put("depth", _tempString);
            
        }       
        
        _tempString = "";
        
        //type of state
        _mapOf.put("typeOfState", "initialState");
        
        //outgoing transitions of state
        _allOutgoing = new Vector();
        
        _allOutgoing.addAll(_actualState.getOutgoing());
        
        if(_allOutgoing.size() > 1){
            _tempString = "(";
            
            for(int i = 0; i < _allOutgoing.size(); i++){
                _aTransitionName = new String();
                
                if(i != 0){
                    _tempString = _tempString + " | (";
                }
           
                _aTransition = (Transition)_allOutgoing.get(i);
                _aTransitionName = "transition(" + _actualStateMachineName + "," + correctName(_aTransition.getName()) + ",succ(N))";
                
                for(int l = 0; l < i; l++ ){
                    _aTransition = (Transition)_allOutgoing.get(l);
                    _tempString = _tempString + "~transition(" + _actualStateMachineName + "," + correctName(_aTransition.getName()) + ",succ(N)) & ";
                }
                
                _tempString = _tempString + _aTransitionName;
                
                for(int m = i+1; m < _allOutgoing.size(); m++){
                    _aTransition = (Transition)_allOutgoing.get(m);
                    _tempString = _tempString + " & ~transition(" + _actualStateMachineName + "," + correctName(_aTransition.getName()) + ",succ(N))";
                }    
            
                        
                
                _tempString = _tempString + ")";
            }    
            _tempString = "(" + _tempString + ")"; 
            _mapOf.put("outgoing", _tempString);
        }
        else{
            _aTransition = (Transition)_allOutgoing.get(0);
            _tempString = "transition(" + _actualStateMachineName + "," + correctName(_aTransition.getName()) + ",succ(N))";    
            
            _mapOf.put("outgoing", _tempString);
        }
           
        return _mapOf;
    }
    

    /**
     * called by initialise() the Hash Maps; 
     * this Method sets the needed parts of a component for creating the rules;
     * all Keys of a component are listed below 
     * @param _actualState
     * @return HashMap containing Strings as values 
     */
    private HashMap setMapOf(String _actualStateMachineName, CompositeState _actualState){
        
        /*
         * Map Keys:
         * depth
         * typeOfState
         * entryAction
         * exitAction
         * doActivity
         * outgoing incl. internalTransitions
         * subinitial
         * subfinal
         */
        
        HashMap _mapOf = new HashMap();
        CompositeState _tempState;
        String _tempString1 = new String();
        String _tempString2 = new String();
        Vector _allOutgoing;
        Transition _aTransition;
        Iterator _tempIterator;
        
        _tempState = _actualState.getContainer();
        _tempString1 = "";
        _tempString2 = "";
        
        //depth of state
        if(_tempState.getName().equals("state_machine_top")){
            _mapOf.put("depth", _tempString1);
        }
        else{
          //  _tempString1 = "Sub";
            
            while( ! (_tempState.getName().equals("state_machine_top"))){
                _tempState = _tempState.getContainer();
                _tempString1 = _tempString1 + "_Sub";
            }
            _mapOf.put("depth", _tempString1);
        }
        
        _tempString1 = "";
        _tempString2 = "";
         
        //concurrent or sequential state
        if(_actualState.isConcurrent()){
            _mapOf.put("typeOfState", "concurrentState");
        }
        else{
            _mapOf.put("typeOfState", "sequentialState");
        }
        
        //entryAction
        if(_actualState.getEntry() != null){
            _tempString1 = _actualState.getEntry().getName() + "( ";
            _tempString2 = _actualState.getEntry().getScript().getBody() + ")";
            
            _tempString1 = parseEntry(_actualStateMachineName,_tempString1, _tempString2);
            _mapOf.put("entryAction", _tempString1);
        }
        else{
            _tempString1 = "entry(" + _actualStateMachineName + ",empty,N)";
            _mapOf.put("entryAction", _tempString1);
        }
        
        _tempString1 = "";
        _tempString2 = "";
        
        //exitAction
        if(_actualState.getExit() != null){
            _tempString1 = _actualState.getExit().getName() + "( ";
            _tempString2 = _actualState.getExit().getScript().getBody() + ")";
            
            _tempString1 = parseExit(_actualStateMachineName,_tempString1, _tempString2);
            _mapOf.put("exitAction", _tempString1);
        }
        else{
            _tempString1 = "exit(" + _actualStateMachineName + ",empty,N)";
            _mapOf.put("exitAction", _tempString1);
        }
        
        _tempString1 = "";
        _tempString2 = "";
        
        //doActivity
        if(_actualState.getDoActivity() != null){
            _tempString1 = _actualState.getDoActivity().getName() + "( ";
            _tempString2 = _actualState.getDoActivity().getScript().getBody() + ")";
            
            _tempString1 = parseDo(_actualStateMachineName,_tempString1, _tempString2);
            _mapOf.put("doActivity", _tempString1);
        }
        else{
            _tempString1 = "do(" + _actualStateMachineName + ",empty,N)";
            _mapOf.put("doActivity", _tempString1);
        }
        
        _tempString1 = "";
        _tempString2 = "";
        
        //outgoing Transitions incl. internal Transition
        _allOutgoing = new Vector();
        
        _allOutgoing.addAll(_actualState.getOutgoing());
        
        if(_actualState.getInternalTransition().isEmpty()){
            _allOutgoing.addAll(_actualState.getInternalTransition());
        }
        
        if(_allOutgoing.size() > 1){
            _tempString1 = "(";
            
            for(int i = 0; i < _allOutgoing.size(); i++){
                String _aTransitionName = new String();
                
                if(i != 0){
                    _tempString1 = _tempString1 + " | (";
                }
                
                _aTransition = (Transition)_allOutgoing.get(i);
                _aTransitionName = "transition(" + _actualStateMachineName + "," + correctName(_aTransition.getName()) + ",succ(N))";
                
                for(int l = 0; l < i; l++ ){
                    _aTransition = (Transition)_allOutgoing.get(l);
                    _tempString1 = _tempString1 + "~transition(" + _actualStateMachineName + "," + correctName(_aTransition.getName()) + ",succ(N)) & ";
                }
                
                _tempString1 = _tempString1 + _aTransitionName;
                
                for(int m = i+1; m < _allOutgoing.size(); m++){
                    _aTransition = (Transition)_allOutgoing.get(m);
                    _tempString1 = _tempString1 + " & ~transition(" + _actualStateMachineName + "," + correctName(_aTransition.getName()) + ",succ(N))";
                }
                
                _tempString1 = _tempString1 + ")";
            }
            
            _tempString1 = "(" + _tempString1 + ")";
                        
            _mapOf.put("outgoing", _tempString1);
        }
        else{
            _aTransition = (Transition)_allOutgoing.get(0);
            _tempString1 = "transition(" + _actualStateMachineName + "," + correctName(_aTransition.getName()) + ",succ(N))";
        
            _mapOf.put("outgoing", _tempString1);
        }
        
        _tempString1 = "";
        _tempString2 = "";
        
        //getting subinitial and subfinal states
        _tempIterator = _actualState.getSubvertex().iterator();
        
        while(_tempIterator.hasNext()){
            StateVertex _aSubState;
            
            _aSubState = (StateVertex) _tempIterator.next();
            
            String teststring = new String();
            teststring = _aSubState.getClass().toString(); 
            
            if(_aSubState.getClass().toString().equals("class org.omg.uml.behavioralelements.statemachines.Pseudostate$Impl")){
                
                if(_tempString1.length() == 0){
                    _tempString1 = "(initial(" + _actualStateMachineName + "," + correctName(((Pseudostate) _aSubState).getName()) + ",N)"; 
                }
                else{
                    _tempString1 = _tempString1 + " & initial(" + _actualStateMachineName + "," + correctName(((Pseudostate) _aSubState).getName()) + ",N)";
                }
            }
            if(_aSubState.getClass().toString().equals("class org.omg.uml.behavioralelements.statemachines.FinalState$Impl")){
                
                if(_tempString2.length() == 0){
                    _tempString2 = "(final(" + _actualStateMachineName + "," + correctName(((FinalState) _aSubState).getName()) + ",N)"; 
                }
                else{
                    _tempString2 = _tempString2 +  " & final(" + _actualStateMachineName + "," + correctName(((FinalState) _aSubState).getName()) + ",N)";
                }
            }
        }
        
        _tempString1 = _tempString1 + ")";
        _tempString2 = _tempString2 + ")";        
                
        _mapOf.put("subinitial", _tempString1);
        _mapOf.put("subfinal",_tempString2);
        
        return _mapOf;
    }
    
    
    /**
     * last method called by initialise() the Hash Maps; 
     * this Method sets the needed parts of a component for creating the rules;
     * all Keys of a component are listed below 
     * @param _actualState
     * @return HashMap containing Strings as values 
     */
    private HashMap setMapOf(String _actualStateMachineName, SimpleState _actualState){
        
        /*
         * Map Keys:
         * depth
         * typeOfState
         * entryAction
         * exitAction
         * doActivity
         * outgoing incl. internalTransitions
         */
        
        HashMap _mapOf = new HashMap();
        
        String _tempString1 = new String();
        String _tempString2 = new String();
        Vector _allOutgoing;
        Iterator _tempIterator;
        Transition _aTransition;
        CompositeState _tempState;

        
        _tempState = _actualState.getContainer();
        _tempString1 = "";
        _tempString2 = "";
        
        //depth of state
        if(_tempState.getName().equals("state_machine_top")){
            _mapOf.put("depth", _tempString1);
        }
        else{
           // _tempString1 = "Sub";
            
            while( ! (_tempState.getName().equals("state_machine_top"))){
                _tempState = _tempState.getContainer();
                _tempString1 = _tempString1 + "_Sub";
            }
            _mapOf.put("depth", _tempString1);
        }        
        
        //type of state
        _mapOf.put("typeOfState", "simpleState");
        
        _tempString1 = "";
        _tempString2 = "";
        
        //entryAction
        if(_actualState.getEntry() != null){
            _tempString1 = _actualState.getEntry().getName() + "( ";
            _tempString2 = _actualState.getEntry().getScript().getBody() + ")";
            
            _tempString1 = parseEntry(_actualStateMachineName,_tempString1, _tempString2);
            _mapOf.put("entryAction", _tempString1);
        }
        else{
            _tempString1 = "entry(" + _actualStateMachineName + ",empty,N)";
            _mapOf.put("entryAction", _tempString1);
        }
        
        _tempString1 = "";
        _tempString2 = "";
        
        //exitAction
        if(_actualState.getExit() != null){
            _tempString1 = _actualState.getExit().getName() + "( ";
            _tempString2 = _actualState.getExit().getScript().getBody() + ")";
            
            _tempString1 = parseExit(_actualStateMachineName,_tempString1, _tempString2);
            _mapOf.put("exitAction", _tempString1);
        }
        else{
            _tempString1 = "exit(" + _actualStateMachineName + ",empty,N)";
            _mapOf.put("exitAction", _tempString1);
        }
        
        _tempString1 = "";
        _tempString2 = "";
        
        //doActivity
        if(_actualState.getDoActivity() != null){
            _tempString1 = _actualState.getDoActivity().getName() + "( ";
            _tempString2 = _actualState.getDoActivity().getScript().getBody();
            _tempString1 = parseDo(_actualStateMachineName,_tempString1, _tempString2);
            _mapOf.put("doActivity", _tempString1);
        }
        else{
            _tempString1 = "do(" + _actualStateMachineName + ",empty,N)";
            _mapOf.put("doActivity", _tempString1);
        }
        
        _tempString1 = "";
        _tempString2 = "";
        
        //outgoing Transitions including internal transitions
        _allOutgoing = new Vector();
        
        _allOutgoing.addAll(_actualState.getOutgoing());
        
        if(_actualState.getInternalTransition().isEmpty()){
            _allOutgoing.addAll(_actualState.getInternalTransition());
        }
        
        if(_allOutgoing.size() > 1){
            _tempString1 = "(";
            
            for(int i = 0; i < _allOutgoing.size(); i++){
                String _aTransitionName = new String();
                
                if(i != 0){
                    _tempString1 = _tempString1 + " | (";
                }
                
                _aTransition = (Transition)_allOutgoing.get(i);
                _aTransitionName = "transition(" + _actualStateMachineName + "," + correctName(_aTransition.getName()) + ",succ(N))";
                
                for(int l = 0; l < i; l++ ){
                    _aTransition = (Transition)_allOutgoing.get(l);
                    _tempString1 = _tempString1 + "~transition(" + _actualStateMachineName + "," + correctName(_aTransition.getName()) + ",succ(N)) & ";
                }
                
                _tempString1 = _tempString1 + _aTransitionName;
                
                for(int m = i+1; m < _allOutgoing.size(); m++){
                    _aTransition = (Transition)_allOutgoing.get(m);
                    _tempString1 = _tempString1 + " & ~transition(" + _actualStateMachineName + "," + correctName(_aTransition.getName()) + ",succ(N))";
                }
                
                _tempString1 = _tempString1 + ")";
            } 
            _tempString1 = "(" + _tempString1 + ")";
            _mapOf.put("outgoing", _tempString1);
        }
        else{
            _aTransition = (Transition)_allOutgoing.get(0);
            _tempString1 = "transition(" + _actualStateMachineName + "," + correctName(_aTransition.getName()) + ",succ(N))";
            _mapOf.put("outgoing", _tempString1);
        }
        
        return _mapOf;
    }
    
    
    /**
     * last method called by initialise() the Hash Maps; 
     * this Method sets the needed parts of a component for creating the rules;
     * all Keys of a component are listed below 
     * @param _actualState
     * @return HashMap containing Strings as values 
     */
    private HashMap setMapOf(String _actualStateMachineName, FinalState _actualState){
        /*
         * Map Keys:
         * depth
         * typeOfState
         */
        
        HashMap _mapOf = new HashMap();
        CompositeState _tempState;
        String _tempString1;
        
        _tempState = _actualState.getContainer();
        _tempString1 = "";
        
        //depth of state
        if(_tempState.getName().equals("state_machine_top")){
            _mapOf.put("depth", _tempString1);
        }
        else{
          //  _tempString1 = "Sub";
            
            while( ! (_tempState.getName().equals("state_machine_top"))){
                _tempState = _tempState.getContainer();
                _tempString1 = _tempString1 + "_Sub";
            }
            
            _mapOf.put("depth", _tempString1);
        }   
        
        _tempString1 = "";
        
        //type of state
        _mapOf.put("typeOfState", "finalState");
        
        return _mapOf;
    }
    
    
    /**
     * last method called by initialise() the Hash Maps; 
     * this Method sets the needed parts of a component for creating the rules;
     * all Keys of a component are listed below 
     * @param _actualTransition
     * @return HashMap containing Strings as values 
     */
    private HashMap setMapOf(String _actualStateMachineName, Transition _actualTransition){
        
        /*
         * Map Keys:
         * guard
         * trigger
         * effect
         * source
         * target
         */
        
        HashMap _mapOf = new HashMap();
        String _tempString1 = new String();
        String _tempString2 = new String();
        Vector _transitionsWithNoTrigger;
        Iterator _tempIterator;
        StateVertex _targetState; 
     
        _tempString1 = "";
        _tempString2 = "";
 
        //guard of Transition
        if(_actualTransition.getGuard() != null){
            _tempString1 = _actualTransition.getGuard().getName() + "( ";
            _tempString2 = _actualTransition.getGuard().getExpression().getBody() + ")";
            
            _tempString1 = parseGuard(_actualStateMachineName,_tempString1, _tempString2); 
            
            _mapOf.put("guard", _tempString1);
        }
        else{
            _tempString1 = "guard(" + _actualStateMachineName + ",empty,N)";
            _mapOf.put("guard", _tempString1);
        }
 
        _tempString1 = "";
        _tempString2 = "";
 
        //trigger Event
        if(_actualTransition.getTrigger() != null){
            _tempString1 = _actualTransition.getTrigger().getName() + "( ";
            
            _tempIterator = _actualTransition.getTrigger().getParameter().iterator();
            
            while(_tempIterator.hasNext()){
                _tempString2 = _tempString2 + ((Parameter) _tempIterator.next()).getName() + "; ";                
            }
            
            _tempString1 = _tempString1 + ")";
            _tempString1 = parseTrigger(_actualStateMachineName,_tempString1, _tempString2, correctName(_actualTransition.getName()));
            
            _mapOf.put("trigger",_tempString1 );    
        }
        else{
            _tempString1 = "trigger(" + _actualStateMachineName + ",empty,N)";
            _mapOf.put("trigger", _tempString1);
            
            if(transitionsWithNoTrigger.containsKey(_actualStateMachineName)){
                _transitionsWithNoTrigger = (Vector) transitionsWithNoTrigger.get(_actualStateMachineName);
            }
            else{
                _transitionsWithNoTrigger = new Vector();
                
            }            
            
            _transitionsWithNoTrigger.addElement(correctName(_actualTransition.getName()));
            transitionsWithNoTrigger.put(_actualStateMachineName,_transitionsWithNoTrigger);
        }
 
        _tempString1 = "";
        _tempString2 = "";
 
        //effect
        if(_actualTransition.getEffect() != null){
            _tempString1 = _actualTransition.getEffect().getName() + "( ";
            _tempString2 = _actualTransition.getEffect().getScript().getBody() + ")";
            
            _tempString1 = parseEffect(_actualStateMachineName,_tempString1, _tempString2);
            _mapOf.put("effect", _tempString1);
        }
        else{
            _tempString1 = "effect(" + _actualStateMachineName + ",none,empty,N)";
            _mapOf.put("effect", _tempString1);
        }
                
        //source state
        _mapOf.put("source", correctName(_actualTransition.getSource().getName()));
        
        //target state
        _mapOf.put("target", correctName(_actualTransition.getTarget().getName()));
        
        return _mapOf;
    }
    
    
    /**
     * called by uniquenessOfStateMachines() in TPTPGenerator
     * @return Vector containing names of state machines as strings 
     */
    Vector getAllStateMachines(){
        Vector _result = new Vector();
        
        _result.addAll(allStateMachines.keySet());
        
        return _result;
    }
    
    
    /**
     * called by uniquenessOfStates() in TPTPGenerator;
     * @return Vector containing Vectors, each contianing Names of all states of a statemachine
     */
    Vector getAllStates(){
        
            Vector _result = new Vector();
            Iterator _stateMachineName;
            
            _stateMachineName = allStateMachines.keySet().iterator();
            
            while(_stateMachineName.hasNext()){
                _result.addElement(getStatesOf((String) _stateMachineName.next()));
            }
                    
            return _result;        
        }
    

    /**
     * called by getAllStates() to obtain Names of all states of a statemachine
     * @param _actualStateMachine
     * @return Vector containg Strings
     */
    private Vector getStatesOf(String _actualStateMachine){
        
        Vector _states = new Vector();
        HashMap _statesOfAType;
        Iterator _statesTypeIterator;
        Iterator _statesIterator;
        String _aTypeOfStates;
        String _aState;
        
        _statesTypeIterator = ((HashMap) allStateMachines.get(_actualStateMachine)).keySet().iterator();
        
        while(_statesTypeIterator.hasNext()){
            _statesOfAType = new HashMap();
            _aTypeOfStates = new String();
            
            _aTypeOfStates = (String) _statesTypeIterator.next();
            
            if( ! (_aTypeOfStates.equals("transition"))){
                
                _statesOfAType = (HashMap) ((HashMap) allStateMachines.get(_actualStateMachine)).get(_aTypeOfStates);
                _statesIterator = _statesOfAType.keySet().iterator();
                
                while(_statesIterator.hasNext()){
                    _aState = new String();
                    _aState = (String) _statesIterator.next();
                    _states.addElement(_aState);
                }                
            }
        }
        
        return _states;
    }

    
    /**
     * called by uniquenessOfTransitions() in TPTPGenerator;
     * @return Vector containing Names of all transitions of all statemachines
     */
    Vector getAllTransitions(){
        
        Vector _result = new Vector();
        Iterator _stateMachineIterator;
        
        _stateMachineIterator = allStateMachines.keySet().iterator();
        
        while(_stateMachineIterator.hasNext()){
            _result.addAll(getTransitionsOf((String) _stateMachineIterator.next()));            
        }
        
        return _result;                    
    }
    

    /**
     * called by getAllTransitions() to obtain Names of all transitions of a statemachine
     * @param _actualStateMachine
     * @return Vector containg Strings
     */
    private Vector getTransitionsOf(String _actualStateMachine){
        
        Vector _result = new Vector();
        HashMap _transitions = new HashMap();
        Iterator _transitionsIterator;
        
        _transitions = (HashMap) ((HashMap) allStateMachines.get(_actualStateMachine)).get("transition");
        _transitionsIterator = _transitions.keySet().iterator();
        
        while(_transitionsIterator.hasNext()){
            _result.addElement((String)_transitionsIterator.next());
        }        
        
        return _result;
    }

    
    /**
     * called by uniquenessOfMessages() in TPTPGenerator;
     * @return Vector containing Names of all messages of all statemachines
     */
    Vector getAllMessages(){        
        Vector _result = new Vector();
        Iterator _stateMachineIterator;
        String _stateMachineName;
        Vector _messages = new Vector();
        
        _stateMachineIterator = allStateMachines.keySet().iterator();
        
        while(_stateMachineIterator.hasNext()){
            _stateMachineName = (String) _stateMachineIterator.next();
            
            if(allMessages.containsKey(_stateMachineName)){
                _messages = (Vector) allMessages.get(_stateMachineName);
            }
            
            for(int i = 0; i < _messages.size(); i++){
                if( ! (_result.contains(_messages.get(i)))){
                    _result.addElement(_messages.get(i));
                }
            }
        }
        
        return _result;                    
    }    
    
    
    /**
     * for ease of use; this Method corrects all Strings for the TPTP-Syntax
     * @param _aName
     * @return String in correct form
     */
    private String correctName(String _aName){
        
        String _result = new String();
        
        _result = _aName.substring(0,1).toLowerCase() + _aName.substring(1);
        
        return _result;
    }
    
     
     /**
      * parses the entry action of a state
      * @param _name
      * @param _expression
      * @return the proper form for TPTP-Syntax
      */
     private String parseEntry(String _actualStateMachineName,String _name, String _expression){
        String _result = new String();
        
        _result = "entry(" + _actualStateMachineName + ",empty,N)";            
        
        return _result;
     }
     
     
     /**
      * parses the exit action of a state
      * @param _name
      * @param _expression
      * @return the proper form for TPTP-Syntax
      */
     private String parseExit(String _actualStateMachineName,String _name, String _expression){
         String _result = new String();
        
         _result = "exit(" + _actualStateMachineName + ",empty,N)";                          
         
         return _result;
     }

     
     /**
      * parses the do activity of a state
      * @param _name
      * @param _expression
      * @return the proper form for TPTP-Syntax
      */
     private String parseDo(String _actualStateMachineName,String _name, String _expression){
         String _result = new String();
      
         _result = "do(" + _actualStateMachineName + ",empty,N)";                          
         
         return _result;
     }
     
     
     /**
      * parses the trigger event of a transition
      * @param _name
      * @param _expression
      * @param _stateMachine
      * @param _actualTransition
      * @return the proper form for TPTP-Syntax
      */     
     private String parseTrigger(String _actualStateMachineName, String _name, String _parameters, String _actualTransition){
         String _result = new String();
         HashMap _activitiesOfTargetStateMachine;
         HashMap _messagesFromAStateMachine;
         HashMap _affectedComponentsBy;
         Vector _transitionsWithATrigger;
         Vector _messages;
         String _msgID = new String();
         
         if(_name.startsWith("msg")){
             _msgID = _name;
             _msgID = _msgID.replaceAll("\n", "");
             _msgID = _msgID.replaceAll(" ", "");
             _msgID = _msgID.substring(0,_msgID.indexOf("("));
             
             if(allMessages.containsKey(_actualStateMachineName)){
                 _messages = (Vector) allMessages.get(_actualStateMachineName);
                 
                 if( ! (_messages.contains(_msgID))){
                     _messages.addElement(_msgID);
                     allMessages.put(_actualStateMachineName,_messages);
                 }                 
             }
             else{
                 _messages = new Vector();
                 _messages.addElement(_msgID);
                 allMessages.put(_actualStateMachineName,_messages);
             }

             if(allActivities.containsKey(_actualStateMachineName)){
                 
                 _activitiesOfTargetStateMachine = (HashMap) allActivities.get(_actualStateMachineName);
                 
                 if(_activitiesOfTargetStateMachine.containsKey("triggerWithMessage")){
                     _messagesFromAStateMachine = (HashMap) _activitiesOfTargetStateMachine.get("triggerWithMessage");
                     
                     if(_messagesFromAStateMachine.containsKey("none")){
                         _affectedComponentsBy = (HashMap) _messagesFromAStateMachine.get("none");
                         
                         if(_affectedComponentsBy.containsKey(_msgID)){
                             _transitionsWithATrigger = (Vector) _affectedComponentsBy.get(_msgID);
                         }
                         else{
                             _transitionsWithATrigger = new Vector();
                         }
                     }
                     else{
                         _affectedComponentsBy = new HashMap();
                         _transitionsWithATrigger = new Vector();
                     }
                 }
                 else{
                     _messagesFromAStateMachine = new HashMap();
                     _affectedComponentsBy = new HashMap();
                     _transitionsWithATrigger = new Vector();;
                 }
             }
             else{
                 _activitiesOfTargetStateMachine = new HashMap();
                 _messagesFromAStateMachine = new HashMap();
                 _affectedComponentsBy = new HashMap();
                 _transitionsWithATrigger = new Vector();
             }
             
             _transitionsWithATrigger.addElement(_actualTransition);
             _affectedComponentsBy.put(_msgID,_transitionsWithATrigger);
             _messagesFromAStateMachine.put("none",_affectedComponentsBy);
             _activitiesOfTargetStateMachine.put("triggerWithMessage",_messagesFromAStateMachine);
             allActivities.put(_actualStateMachineName,_activitiesOfTargetStateMachine);
         }
         else{
             _msgID = "true";             
         }
         
         _result = "trigger(" + _actualStateMachineName + "," + _msgID + ",N)";
         
         return _result;
     }
        
     
     /**
      * parses the guard of a transition
      * @param _name
      * @param _expression
      * @return proper form for TPTP-Syntax 
      */
     private String parseGuard(String _actualStateMachineName,String _name, String _expression){
        String _result = new String();
      
        _result = "guard(" + _actualStateMachineName + ",empty,N)";

        return _result;
     }
     
     
     /**
      * parses the effect of a transition
      * @param _name
      * @param _expression
      * @return proper form for TPTP-Syntax 
      */
     private String parseEffect(String _actualStateMachineName, String _name, String _expression){
         String _result = new String();
         HashMap _activitiesOfTargetStateMachine;
         HashMap _messagesFromAStateMachine;
         HashMap _affectedComponentsBy;
         Vector _transitionsWithATrigger;
         Vector _affectedComponents;
         Vector _messages;
         String _targetStateMachineName = new String();
         String _msgID = new String();
         
         if(_name.startsWith("send(")){
             _targetStateMachineName = _expression.substring(0, _expression.indexOf("."));
             _targetStateMachineName = _targetStateMachineName.replaceAll("\n", "");
             _targetStateMachineName = _targetStateMachineName.replaceAll(" ", "");
             
             _msgID = _expression.substring(_expression.indexOf(".") + 1,_expression.indexOf("("));
             _msgID = _msgID.replaceAll("\n", "");
             _msgID = _msgID.replaceAll(" ", "");
             
             if(allMessages.containsKey(_targetStateMachineName)){
                 _messages = (Vector) allMessages.get(_targetStateMachineName);
                 
                 if( ! (_messages.contains(_msgID))){
                     _messages.addElement(_msgID);
                     allMessages.put(_targetStateMachineName,_messages);
                 }                 
             }
             else{
                 _messages = new Vector();
                 _messages.addElement(_msgID);
                 allMessages.put(_targetStateMachineName,_messages);
             }
             
             if(allActivities.containsKey(_targetStateMachineName)){
                 _activitiesOfTargetStateMachine = (HashMap) allActivities.get(_targetStateMachineName);
                 
                 if(_activitiesOfTargetStateMachine.containsKey("effect")){
                     _messagesFromAStateMachine = (HashMap) _activitiesOfTargetStateMachine.get("effect");
                     
                     if(_messagesFromAStateMachine.containsKey(_actualStateMachineName)){
                         _affectedComponentsBy = (HashMap) _messagesFromAStateMachine.get(_actualStateMachineName);
                         
                         if(_affectedComponentsBy.containsKey(_msgID)){
                             _affectedComponents = (Vector) _affectedComponentsBy.get(_msgID);
                         }
                         else{
                             _affectedComponents = new Vector();
                         }
                     }
                     else{
                         _affectedComponentsBy = new HashMap();
                         _affectedComponents = new Vector();                         
                     }
                 }
                 else{
                     _messagesFromAStateMachine = new HashMap();
                     _affectedComponentsBy = new HashMap();
                     _affectedComponents = new Vector();
                 }
             }
             else{
                 _activitiesOfTargetStateMachine = new HashMap();
                 _messagesFromAStateMachine = new HashMap();
                 _affectedComponentsBy = new HashMap();
                 _affectedComponents = new Vector();
             }
          
             _affectedComponentsBy.put(_msgID,_affectedComponents);
         	_messagesFromAStateMachine.put(_actualStateMachineName,_affectedComponentsBy);
         	_activitiesOfTargetStateMachine.put("effect",_messagesFromAStateMachine);
         	allActivities.put(_targetStateMachineName,_activitiesOfTargetStateMachine);
         	
         	_result = "effect(" + _actualStateMachineName + "," + _targetStateMachineName + "," + _msgID + ",N)";
         }
         else{
             _result = "effect(" + _actualStateMachineName + ",none,empty,N)";
         }
         
         return _result;
     }
}
