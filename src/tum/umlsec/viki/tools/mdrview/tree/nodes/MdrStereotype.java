package tum.umlsec.viki.tools.mdrview.tree.nodes;

import org.omg.uml.foundation.core.Stereotype;

public class MdrStereotype extends MdrGeneralizableElement {
    public MdrStereotype(Stereotype _mdrStereotype) {
        super(_mdrStereotype);
        mdrStereotype = _mdrStereotype;
    }



    public boolean fillChildren() {
        auxParseCollection(mdrStereotype.getBaseClass(), "BaseClass");
        auxParseCollection(mdrStereotype.getDefinedTag(), "DefinedTag");


        return super.fillChildren();
    }

    private Stereotype mdrStereotype;
}
