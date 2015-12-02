package tum.umlsec.viki.tools.mdrview.tree.nodes;

import org.omg.uml.behavioralelements.statemachines.StateMachine;

public class MdrStateMachine extends MdrModelElement {
    public MdrStateMachine(StateMachine _mdrStateMachine) {
        super(_mdrStateMachine);
        mdrStateMachine = _mdrStateMachine;
    }

    /*
    public String toString() {
        return "State Machine:" + mdrS.getName();
    }
    */

    public boolean fillChildren() {
        return super.fillChildren();

        /*

        StateMachine:
            public org.omg.uml.foundation.core.ModelElement getContext();
            public void setContext(org.omg.uml.foundation.core.ModelElement newValue);

            public org.omg.uml.behavioralelements.statemachines.State getTop();

            public void setTop(org.omg.uml.behavioralelements.statemachines.State newValue);

            public java.util.Collection getTransitions();

            public java.util.Collection getSubmachineState();


        ModelElement:
            public java.util.Collection getClientDependency();
            public java.util.Collection getConstraint();
            public java.util.Collection getTargetFlow();
            public java.util.Collection getSourceFlow();
            public java.util.Collection getComment();
            public java.util.List getTemplateParameter();
            public java.util.Collection getStereotype();
            public java.util.Collection getTaggedValue();


        */


    }



    private StateMachine mdrStateMachine;
}
