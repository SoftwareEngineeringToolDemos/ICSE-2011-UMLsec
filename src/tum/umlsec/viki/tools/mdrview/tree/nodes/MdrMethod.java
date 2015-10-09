package tum.umlsec.viki.tools.mdrview.tree.nodes;

import org.omg.uml.foundation.core.Method;

public class MdrMethod extends MdrBehavioralFeature {
    public MdrMethod(Method _mdrMethod) {
        super(_mdrMethod);
        mdrMethod = _mdrMethod;
    }

    public String toString() {
        return mdrMethod.getName();
    }

    public boolean fillChildren() {
        return super.fillChildren();
    }

    private Method mdrMethod;
}
