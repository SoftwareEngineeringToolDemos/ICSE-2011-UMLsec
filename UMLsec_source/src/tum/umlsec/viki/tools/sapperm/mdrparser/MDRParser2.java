
package tum.umlsec.viki.tools.sapperm.mdrparser;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.omg.uml.UmlPackage;
import org.omg.uml.behavioralelements.activitygraphs.ActivityGraph;
import org.omg.uml.behavioralelements.activitygraphs.ActivityGraphClass;
import org.omg.uml.behavioralelements.activitygraphs.ActivityGraphsPackage;
import org.omg.uml.behavioralelements.statemachines.Pseudostate;
import org.omg.uml.behavioralelements.statemachines.PseudostateClass;
import org.omg.uml.behavioralelements.statemachines.StateMachinesPackage;
import org.omg.uml.behavioralelements.statemachines.StateVertex;
import org.omg.uml.behavioralelements.statemachines.Transition;
import org.omg.uml.behavioralelements.statemachines.TransitionClass;
import org.omg.uml.behavioralelements.usecases.UseCasesPackage;
import org.omg.uml.foundation.core.CorePackage;
import org.omg.uml.foundation.core.Stereotype;
import org.omg.uml.foundation.core.TaggedValue;

import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.mdr.IMdrContainer;
import tum.umlsec.viki.tools.berechtigungen.xmlparser.XMLParser;

public class MDRParser2 {
    private UmlPackage root;
    private CorePackage corePackage;
    private ActivityGraphsPackage activityPackage;
    private UseCasesPackage usecasePackage;
    private StateMachinesPackage stateMachines;
    private TransitionClass transitionClasses;
    private Pseudostate initialState;
    private ActivityGraph actGraph;
    private XMLParser xmlparser;
    
    private Vector allNamesOfActivities;
    private Vector allActivities = new Vector();
    Vector allUser = new Vector();
    
    private String actName = "";
    
    ITextOutput log;
    
    HashMap result = new HashMap();
    
    public MDRParser2(ITextOutput output) {
        log = output;
    }
    
    public Vector parseUMLDiagram(IMdrContainer _mdrWrapper) {
        allNamesOfActivities = new Vector();
        
        root = _mdrWrapper.getUmlPackage();
        corePackage = root.getCore();
        
        activityPackage = (ActivityGraphsPackage)root.getActivityGraphs();
        ActivityGraphClass activityClasses = activityPackage.getActivityGraph();
        
        stateMachines = (StateMachinesPackage)activityPackage.getStateMachines();
        transitionClasses = (TransitionClass)stateMachines.getTransition();
        
        PseudostateClass pseudostateClasses = (PseudostateClass)stateMachines.getPseudostate();
        
        initialState = null;
        for (Iterator it_act_I = pseudostateClasses.refAllOfClass().iterator(); it_act_I.hasNext();) {
            initialState = (Pseudostate)it_act_I.next();
        }
        
        for (Iterator it_act_I_2 = activityClasses.refAllOfClass().iterator(); it_act_I_2.hasNext();) {
            actGraph = (ActivityGraph)it_act_I_2.next();
            for (Iterator it_act_I_3 = actGraph.getTransitions().iterator(); it_act_I_3.hasNext();) {
                Transition tmp_transition = (Transition)it_act_I_3.next();
                StateVertex tmp = (StateVertex)tmp_transition.getTarget();
                if (tmp.getClass().toString().equals("class org.omg.uml.behavioralelements.activitygraphs.ActionState$Impl")) {
                    String _id, _users, _stereotypeName;
                    _id = _users = _stereotypeName = "";
                    MDRStereotype _ster = null;
                    
                    // test, ob diese Aktivitaet bereits behandelt wurde:
                    if(!isNameinVector(allNamesOfActivities, tmp.getName())) {
                        actName = tmp.getName().toString();
                        
                        for (Iterator it_stereotypeIt = tmp.getStereotype().iterator(); it_stereotypeIt.hasNext();) {
                            Stereotype tmp_stereotype = (Stereotype)it_stereotypeIt.next();
                            _stereotypeName = tmp_stereotype.getName();
                            if(_stereotypeName.equals("critical")){
                                _ster = new MDRStereotype(_stereotypeName, null, null);                                
                            }
                            else if(_stereotypeName.equals("Separation of Duty")){
                                _ster = initializeStereotype(tmp_stereotype, _stereotypeName);
                            }
                        }
                        for (Iterator it_taggedValueIt = tmp.getTaggedValue().iterator(); it_taggedValueIt.hasNext();) {
                            TaggedValue tmp_taggedValue = (TaggedValue)it_taggedValueIt.next();
                            String tag = tmp_taggedValue.getType().getName();
                            
                            if (tag.equals("users")) {
                                try {
                                    for(Iterator it_tagVa_A = (tmp_taggedValue.getDataValue()).iterator(); it_tagVa_A.hasNext();) {
                                        String tagValue_Da_A=(String)it_tagVa_A.next();
                                        if (tagValue_Da_A!=null) {
                                            _users =  tagValue_Da_A;
                                        }
                                    }
                                }
                                catch(Exception ep) {
                                    System.out.println("exception bei  find class : "+ep.getMessage());
                                }
                                
                            }
                            
                            if (tag.equals("trans_id")) {
                                try {
                                    for(Iterator it_tagVa_A = (tmp_taggedValue.getDataValue()).iterator(); it_tagVa_A.hasNext();) {
                                        String tagValue_Da_A=(String)it_tagVa_A.next();
                                        if (tagValue_Da_A!=null) {
                                            _id =  tagValue_Da_A;
                                        }
                                    }
                                }
                                catch(Exception ep) {
                                    System.out.println("exception bei  find class : "+ep.getMessage());
                                }
                                
                            }                            
                            
                        }
                        
                    } // end of if(!isNameinVector(allNamesOfActivities, tmp.getName()))
                    
                    MDRActivity2 _tmpMDRactivity =
                    new MDRActivity2(actName, _id, _users, _ster);
                    allActivities.add(_tmpMDRactivity);
                    actName = "";
                    
                }
            }
        }
        
        return getAllActivities();
    }
    
    /**
     * Die Funktion ueberprueft, ob die Aktivitaet bereits behandelt wurde,
     * falls nein wird der Name der Aktivitaet dem Array hinzugefuegt.
     * @param vec Vector mit allen bisher behandelten Aktivitaeten
     * @param name Name der aktuell betrachteten Aktivitaet
     * @return true, falls die Aktivitaet bereits behandelt wurde, false sonst.
     */
    private boolean isNameinVector(Vector vec, String name) {
        
        boolean returnValue;
        
        returnValue = false;
        
        for (int i = 0; i < vec.size(); i++) {
            if (((String)vec.get(i)).equals(name)) {
                //TODO: diese Suche nach anderen Kriterien ermoeglichen!
                //returnValue = true;
                returnValue = false;
                break;
            }
        }
        // diser Name ist nicht im Vector:
        if (!returnValue) {
            allNamesOfActivities.add(name);
        }
        return returnValue;
    }
 
    public MDRStereotype initializeStereotype(Stereotype ster, String name){
        String _stTag, _stValue;
        _stTag = _stValue = "";
        
        for (Iterator it_taggedValueIt = ster.getTaggedValue().iterator(); it_taggedValueIt.hasNext();) {
            TaggedValue tmp_taggedValue = (TaggedValue)it_taggedValueIt.next();
            _stTag = tmp_taggedValue.getType().getName();
            
            if (_stTag.equals("activity")){
                try {
                    for(Iterator it_tagVa_A = (tmp_taggedValue.getDataValue()).iterator(); it_tagVa_A.hasNext();) {
                        String tagValue_Da_A=(String)it_tagVa_A.next();
                        if (tagValue_Da_A!=null) {
                            _stValue =  tagValue_Da_A;
                            break;
                        }
                    }
                    break;
                }
                catch(Exception ep) {
                    System.out.println("exception bei  stereotype : "+ep.getMessage());
                }
            }
       }
        
        return new MDRStereotype(name, _stTag, _stValue);
    }
    
    public Vector getAllUsers() {
        return allUser;
    }
    
    public Vector getAllActivities() {
        return allActivities;
    }
    
    
} // End of class