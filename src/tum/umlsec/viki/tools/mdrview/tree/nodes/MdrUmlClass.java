package tum.umlsec.viki.tools.mdrview.tree.nodes;

import org.omg.uml.foundation.core.UmlClass;

public class MdrUmlClass extends MdrClassifier {
    public MdrUmlClass(UmlClass _mdrUmlClass) {
        super(_mdrUmlClass);
        mdrUmlClass = _mdrUmlClass;
    }

    public String toString() {
        return mdrUmlClass.getName();
    }

    public boolean fillChildren() {
        return super.fillChildren();
    }



    private UmlClass mdrUmlClass;
}
