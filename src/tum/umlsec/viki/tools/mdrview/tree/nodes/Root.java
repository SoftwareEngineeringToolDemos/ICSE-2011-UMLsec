package tum.umlsec.viki.tools.mdrview.tree.nodes;

import tum.umlsec.viki.framework.mdr.IMdrContainer;


/**
 * @author pasha
 */
public class Root extends AbstractNode {
	public Root(IMdrContainer _mdrContainer) {
		mdrContainer = _mdrContainer;
	}
	
	public String toString() {
		return "UMLsec root";
	}

	public boolean fillChildren() {
		if(this.getChildCount() > 0) {
			return false;
		}

		org.omg.uml.UmlPackage _umlPackage = (org.omg.uml.UmlPackage)mdrContainer.getUmlPackage();

		listClasses = new ListClasses(_umlPackage == null? null: _umlPackage.getCore().getUmlClass().refAllOfClass());
		this.add(listClasses);

		return true;
	}

	public ListClasses getNodeClassList() {
		return listClasses;
	}

	ListClasses listClasses;
	IMdrContainer mdrContainer;
}
