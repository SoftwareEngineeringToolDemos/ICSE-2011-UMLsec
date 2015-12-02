package tum.umlsec.viki.tools.mdrview.tree.nodes;

import org.omg.uml.foundation.core.Classifier;

public class MdrClassifier extends MdrGeneralizableElement {
    public MdrClassifier(Classifier _mdrClassifier) {
        super(_mdrClassifier);
        parentNamespace = new MdrNamespace(_mdrClassifier);
        mdrClassifier = _mdrClassifier;
    }

    public boolean fillChildren() {
        if(this.getChildCount() > 0) {
            return false;
        }


// add whatever parent wants to add
        super.fillChildren();

// add the "indirectly" inherited Namespace class
        this.add(parentNamespace);

// for "ourselves" add the Feature collection
        AbstractNode _node;
        _node = new ListAttributes(mdrClassifier.getFeature());
        this.add(_node);

        /*
        _node = new NodeMethodList(mdrClassifier.getFeature());
        this.add(_node);
        */

        _node = new ListOperations(mdrClassifier.getFeature());
        this.add(_node);

        return true;
    }

    private Classifier mdrClassifier;
    private MdrNamespace parentNamespace;
}
