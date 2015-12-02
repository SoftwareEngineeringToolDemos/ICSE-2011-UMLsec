package tum.umlsec.viki.tools.mdrview.tree.nodes;

import java.util.Collection;
import java.util.Iterator;

import javax.swing.JOptionPane;

import org.omg.uml.foundation.core.Attribute;
import org.omg.uml.foundation.core.Method;
import org.omg.uml.foundation.core.Operation;


public class ListMethods extends ListGeneric {
    public ListMethods(Collection _collection) {
        super(_collection);
    }

    public String toString() {
        return "Methods";
    }

    public boolean fillChildren() {
        if(this.getChildCount() > 0) {
            return false;
        }

        for(Iterator itf = mdrCollection.iterator(); itf.hasNext(); ) {
            Object _feature = itf.next();

            if(_feature instanceof Attribute) {
            } else if(_feature instanceof Operation) {
            } else if(_feature instanceof Method) {
                MdrMethod _node = new MdrMethod((Method)_feature);
                this.add(_node);
            } else {
                JOptionPane.showMessageDialog(null, _feature.getClass(), "Unknown UmlClass Feature", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        return true;
    }

}
