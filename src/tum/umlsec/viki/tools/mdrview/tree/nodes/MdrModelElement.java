package tum.umlsec.viki.tools.mdrview.tree.nodes;

import org.omg.uml.foundation.core.ModelElement;

public class MdrModelElement extends MdrElement {
    public MdrModelElement(ModelElement _mdrModelElement) {
        super(_mdrModelElement);
        mdrModelElement = _mdrModelElement;
    }

    public String toString() {
        return mdrModelElement.getName();
    }

    public boolean fillChildren() {
        if(this.getChildCount() > 0) {
            return false;
        }

        super.fillChildren();

        AbstractNode _node;
        _node = new ListStereotypes(mdrModelElement.getStereotype());
        this.add(_node);

        return true;
    }

    private ModelElement mdrModelElement;
}
