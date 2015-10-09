package tum.umlsec.viki.tools.mdrview.tree.nodes;

import org.omg.uml.foundation.core.Attribute;

public class MdrAttribute extends MdrStructuralFeature {
    public MdrAttribute(Attribute _mdrAttribute) {
        super(_mdrAttribute);
        mdrAttribute = _mdrAttribute;
    }

    public String toString() {
        return mdrAttribute.getName();
    }


    public boolean fillChildren() {
        return super.fillChildren();
    }

    private Attribute mdrAttribute;
}
