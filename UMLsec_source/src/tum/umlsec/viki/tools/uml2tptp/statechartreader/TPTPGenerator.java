/**
 * @author Erwin Yukselgil
 * Created on 06.04.2005
 * Version 1.0
 * 
 * This class provides a general layout and calls all needed methods to create the rules for statemachines in TPTP-Syntax
 * 
 */

package tum.umlsec.viki.tools.uml2tptp.statechartreader;

import java.util.Iterator;
import java.util.Vector;

import tum.umlsec.viki.framework.mdr.IMdrContainer;


public class TPTPGenerator {
    
    private Statechartreader stateMachines;

    
    public TPTPGenerator(IMdrContainer _mdrContainer){        
        stateMachines = new Statechartreader(_mdrContainer);     
    }
    
    
    /**
     * Uppermost Method for creating the TPTP-File
     * @return Vector containing all rules and comments as Strings
     */
    public Vector generateTPTP(){
        
        Vector _tptpRules = new Vector();
        Vector _tempVector = new Vector();
        Iterator _tempIterator;
            
        _tptpRules.addElement("%-----------Uniqueness Of All Elements ----------");
        _tptpRules.addElement("");
        
        _tempVector = uniquenessAxiom();
        _tempIterator = _tempVector.iterator();
        
        while(_tempIterator.hasNext()){
            _tptpRules.addElement((String) _tempIterator.next());
            _tptpRules.addElement("");
        }
        
        _tptpRules.addElement("");
        
        _tptpRules.addElement("%-----------All State Machines------------------");
        _tempVector = allStateMachinesAxiom();
        _tempIterator = _tempVector.iterator();
        
        _tptpRules.addElement("");
        while(_tempIterator.hasNext()){
            _tptpRules.addElement((String) _tempIterator.next());
            _tptpRules.addElement("");
        }       
                
        return _tptpRules;
    }
  
    
    /**
     * This Method creates rules for uniqueness of states and transitions; called by generateTPTP()
     * @return Vector containing rules as String
     */
    private Vector uniquenessAxiom(){
        
        Vector _result = new Vector();
        
        _result.addElement("%------------State Machines---------------");
        _result.addAll(uniquenessOfStateMachines());
        _result.addElement("%------------States---------------");
        _result.addAll(uniquenessOfStates());
        _result.addElement("%------------Transitions---------------");
        _result.addAll(uniquenessOfTransitions());
        _result.addElement("%------------Messages---------------");
        _result.addAll(uniquenessOfMessages());
        
        return _result;
    }
    
    
    /**
     * called by uniquenessAxiom() to create uniquenss rules for states machines
     * @return Vector containing rules as String
     */
    private Vector uniquenessOfStateMachines(){
        
        Vector _result = new Vector();
        Vector _allStateMachines;
        String _uniqueness;
        String _name1;
        String _name2;
        
        _allStateMachines = stateMachines.getAllStateMachines();
        
        for(int i = 0; i < _allStateMachines.size(); i++){
            _name1 = (String) _allStateMachines.get(i);
                        
            for(int j = i+1; j < _allStateMachines.size(); j++){
                _uniqueness = new String();
                
                _name2 = (String) _allStateMachines.get(j);
                _uniqueness = "input_formula(" + _name1 + "_Not_" + _name2 + ",axiom, (" + _name1 + " != " + _name2 + ")).";
                _result.addElement(_uniqueness);            
            }                          
        }
        
        return _result;
    }
    
    
    /**
     * called by uniquenessAxiom() to create uniquenss rules for states
     * @return Vector containing rules as String
     */
    private Vector uniquenessOfStates(){

        Vector _result = new Vector();
        Vector _allStates;
        Vector _tempVector = new Vector();
        Iterator _allStatesIterator;
        String _uniqueness;
        String _name1;
        String _name2;
        
        _allStates = stateMachines.getAllStates();
        _allStatesIterator = _allStates.iterator();
        
        while(_allStatesIterator.hasNext()){
            _tempVector.addAll((Vector) _allStatesIterator.next());
        }
        
        for(int i = 0; i < _tempVector.size(); i++){
            _name1 = (String) _tempVector.get(i);
                        
            for(int j = i+1; j < _tempVector.size(); j++){
                _uniqueness = new String();
                
                _name2 = (String) _tempVector.get(j);
                _uniqueness = "input_formula(" + _name1 + "_Not_" + _name2 + ",axiom, (" + _name1 + " != " + _name2 + ")).";
                _result.addElement(_uniqueness);            
            }                          
        }
        
        return _result;
    }
    
    
    /**
     * called by uniquenessAxiom() to create uniquenss rules for transitions
     * @return Vector containing rules as String
     */   
    private Vector uniquenessOfTransitions(){

        Vector _result = new Vector();
        Vector _allTransitions;
        String _uniqueness = new String();
        String _name1;
        String _name2;
        
        _allTransitions = stateMachines.getAllTransitions();
                
        for(int i = 0; i < _allTransitions.size(); i++){
            _name1 = (String) _allTransitions.get(i);
                        
            for(int j = i+1; j < _allTransitions.size(); j++){
                _name2 = (String) _allTransitions.get(j);
                _uniqueness = "input_formula(" + _name1 + "_Not_" + _name2 + ",axiom, (" + _name1 + " != " + _name2 + ")).";
                _result.addElement(_uniqueness);
            
            }                          
        }
        
        return _result;
    }
    
    
    /**
     * called by uniquenessAxiom() to create uniquenss rules for transitions
     * @return Vector containing rules as String
     */   
    private Vector uniquenessOfMessages(){

        Vector _result = new Vector();
        Vector _messages;
        String _uniqueness;
        String _name1;
        String _name2;
        
        _messages = stateMachines.getAllMessages();
        
        for(int i = 0; i < _messages.size(); i++){
            _name1 = (String) _messages.get(i);
                        
            for(int j = i+1; j < _messages.size(); j++){
                _uniqueness = new String();
                
                _name2 = (String) _messages.get(j);
                _uniqueness = "input_formula(" + _name1 + "_Not_" + _name2 + ",axiom, (" + _name1 + " != " + _name2 + ")).";
                _result.addElement(_uniqueness);            
            }                          
        }       
        
        return _result;
    }
  
    
    /**
     * called by generateTPTP to get all rules of all statemachines
     * @return Vector containing rules as String 
     */
    private Vector allStateMachinesAxiom(){
        
        Vector _allStateMachinesVector = new Vector();
        Vector _result = new Vector();
        
        Iterator _stateMachineIterator;
        
        _allStateMachinesVector = stateMachines.getRulesOfAllStateMachines();
        _stateMachineIterator = _allStateMachinesVector.iterator(); 
        
        while(_stateMachineIterator.hasNext()){
            
            _result.addAll((Vector) _stateMachineIterator.next());                                         
        }
        
        return _result;        
    }
} 
