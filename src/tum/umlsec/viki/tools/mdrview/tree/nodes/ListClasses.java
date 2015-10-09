package tum.umlsec.viki.tools.mdrview.tree.nodes;

import java.util.Collection;
import java.util.Iterator;

import javax.swing.JOptionPane;

import org.omg.uml.foundation.core.UmlClass;

public class ListClasses extends ListGeneric {
    public ListClasses(Collection _mdrCollection) {
        super(_mdrCollection);
    }

    public String toString() {
        return "Classes";
    }

    public boolean fillChildren() {
        if(this.getChildCount() > 0 || mdrCollection == null) {
            return false;
        }

        for(Iterator iter = mdrCollection.iterator(); iter.hasNext();) {
            Object o =iter.next();
            if(o instanceof UmlClass) {
                this.add(new MdrUmlClass((UmlClass)o));
            } else {
                JOptionPane.showMessageDialog(null, o.getClass(), "Unknown Element in the NodeClassList", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        return true;
    }

}
