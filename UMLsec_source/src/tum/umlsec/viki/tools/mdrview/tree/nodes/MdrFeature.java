package tum.umlsec.viki.tools.mdrview.tree.nodes;

import org.omg.uml.foundation.core.Feature;

public class MdrFeature extends MdrModelElement {
    public MdrFeature(Feature _mdrFeature) {
        super(_mdrFeature);
        mdrFeature = _mdrFeature;
    }

    public boolean fillChildren() {
        return super.fillChildren();
    }

    private Feature mdrFeature;
}
