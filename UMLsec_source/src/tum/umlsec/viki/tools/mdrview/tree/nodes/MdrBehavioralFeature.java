package tum.umlsec.viki.tools.mdrview.tree.nodes;

import org.omg.uml.foundation.core.BehavioralFeature;

public class MdrBehavioralFeature extends MdrFeature {
    public MdrBehavioralFeature(BehavioralFeature _mdrBehavioralFeature) {
        super(_mdrBehavioralFeature);
        mdrBehavioralFeature = _mdrBehavioralFeature;
    }

    public boolean fillChildren() {
        return super.fillChildren();
    }


    private BehavioralFeature mdrBehavioralFeature;
}
