package tum.umlsec.viki.tools.mdrview.tree.nodes;

import org.omg.uml.foundation.core.StructuralFeature;

public class MdrStructuralFeature extends MdrFeature {
    public MdrStructuralFeature(StructuralFeature _mdrStructuralFeature) {
        super(_mdrStructuralFeature);
        mdrStructuralFeature = _mdrStructuralFeature;
    }

    public boolean fillChildren() {
        return super.fillChildren();
    }


    private StructuralFeature mdrStructuralFeature;
}
