package tum.umlsec.viki.tools.mdrview.tree.nodes;

import org.omg.uml.foundation.core.Namespace;

public class MdrNamespace extends MdrModelElement {
    public MdrNamespace(Namespace _mdrNamespace) {
        super(_mdrNamespace);
        mdrNamespace = _mdrNamespace;
    }

    public String toString() {
        return "Namespace: " + mdrNamespace.getName();
    }


    public boolean fillChildren() {
        return super.fillChildren();


        /*

        _node = new _NodeNamespaceOwnedElementList(mdlUmlClass);
        this.add(_node);

        for(Iterator it = parentNamespace.getOwnedElement().iterator(); it.hasNext(); ) {
            Object _ownedElement = it.next();

            if(_ownedElement instanceof StateMachine) {
                MdrStateMachine _node = new MdrStateMachine((StateMachine)_ownedElement);
                this.add(_node);
            } else {
                JOptionPane.showMessageDialog(UmlSecStatecharts.getInstance(), _ownedElement.getClass(), "Unknown Namespace Owned Element", JOptionPane.INFORMATION_MESSAGE);
            }
        }


        */
    }

    private Namespace mdrNamespace;

}
