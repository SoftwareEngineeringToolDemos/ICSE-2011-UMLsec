package tum.umlsec.viki.tools.mdrview.tree.nodes;

import java.util.Collection;
import java.util.Iterator;

import javax.swing.JOptionPane;

import org.omg.uml.foundation.core.Stereotype;


public class ListStereotypes extends ListGeneric {
    public ListStereotypes(Collection _collection) {
        super(_collection);
    }

    public String toString() {
        return "Stereotypes";
    }

    public boolean fillChildren() {
        if(this.getChildCount() > 0) {
            return false;
        }

        for(Iterator itf = mdrCollection.iterator(); itf.hasNext(); ) {
            Object _stereotype = itf.next();

            if(_stereotype instanceof Stereotype) {
                MdrStereotype _node = new MdrStereotype((Stereotype)_stereotype);
                this.add(_node);
            } else {
                JOptionPane.showMessageDialog(null, _stereotype.getClass(), "Unknown UmlClass NodeStereotype class", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        return true;
    }

    String name;
}
