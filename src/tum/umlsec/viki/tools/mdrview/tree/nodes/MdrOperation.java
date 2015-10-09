package tum.umlsec.viki.tools.mdrview.tree.nodes;

import org.omg.uml.foundation.core.Operation;

public class MdrOperation extends MdrBehavioralFeature {
    public MdrOperation(Operation _mdrOperation) {
        super(_mdrOperation);
        mdrOperation = _mdrOperation;
    }

    public String toString() {
        return mdrOperation.getName();
    }


    public boolean fillChildren() {
        return super.fillChildren();
    }

    private Operation mdrOperation;
}
