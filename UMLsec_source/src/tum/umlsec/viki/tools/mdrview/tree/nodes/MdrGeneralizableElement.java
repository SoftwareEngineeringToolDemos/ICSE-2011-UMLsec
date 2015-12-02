package tum.umlsec.viki.tools.mdrview.tree.nodes;

import org.omg.uml.foundation.core.GeneralizableElement;

public class MdrGeneralizableElement extends MdrModelElement {
    public MdrGeneralizableElement(GeneralizableElement _mdrGeneralizableElement) {
        super(_mdrGeneralizableElement);
        mdrGeneralizableElement = _mdrGeneralizableElement;
    }

    public boolean fillChildren() {
        return super.fillChildren();
    }

    private GeneralizableElement mdrGeneralizableElement;
}
