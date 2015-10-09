package tum.umlsec.viki.tools.mdrview.tree.nodes;

import java.util.Collection;
import java.util.Iterator;

import org.omg.uml.foundation.core.Element;

public class MdrElement extends AbstractNode {
    public MdrElement(Element _mdrElement) {
        mdrElement = _mdrElement;
    }

    public boolean fillChildren() {
        return false;
    }

    protected void auxParseCollection(Collection c, String _collectionName) {
        System.out.println(_collectionName + "=====================================");
        for(Iterator it = c.iterator(); it.hasNext(); ) {
            Object ab = it.next();

            System.out.println(ab.getClass() + ": " + ab.toString());
        }
        System.out.println("=======================================================");
    }


    private Element mdrElement;
}
