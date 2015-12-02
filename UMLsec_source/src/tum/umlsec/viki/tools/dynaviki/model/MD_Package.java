package tum.umlsec.viki.tools.dynaviki.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import org.omg.uml.foundation.core.UmlClass;

import tum.umlsec.viki.tools.dynaviki.model.interfaces.ITreeNodeCollection;

/**
 * @author pasha
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class MD_Package extends BaseObject implements ITreeNodeCollection {
	public MD_Package(ModelRoot _root, org.omg.uml.modelmanagement.UmlPackage _p) {
		super(_root);
		mdrPackage = _p;
	}
	
	
	protected void createNodeStructure() {
		children.removeAllElements();		
		
	}


	
	

	public void initialise() {
		createNodeStructure();
	
	
		System.out.println(mdrPackage.getName());
		auxParseCollection(mdrPackage.getOwnedElement(), "owned");
	
		for(Iterator _it = mdrPackage.getOwnedElement().iterator(); _it.hasNext(); ) {
			Object _x = _it.next();
			
			if(_x instanceof org.omg.uml.modelmanagement.UmlPackage) {
				MD_Package _p = getRoot().findPackage((org.omg.uml.modelmanagement.UmlPackage)_x);
				children.add(_p);
				continue;
			}

			if(_x instanceof UmlClass) {
				MD_Class _nc = getRoot().findClass((UmlClass)_x);
				children.add(_nc);
				continue;
			}

			if(_x instanceof org.omg.uml.behavioralelements.commonbehavior.Object) {
				MD_Object _object = getRoot().findObject((org.omg.uml.behavioralelements.commonbehavior.Object)_x);
				children.add(_object);
				continue;
			}

			
			
		}
		
		
		// TODO
	}
	




	public Collection getChildren() {
		return children;
	}

	public String getName() {
		return mdrPackage.getName();
	}

	public String getNodeName() {
		return mdrPackage.getName();
	}

	public String getNodeText() {
		return null;
	}

	public org.omg.uml.modelmanagement.UmlPackage getMdrPackage() {
		return mdrPackage;
	}






	private org.omg.uml.modelmanagement.UmlPackage mdrPackage;

	private Vector children = new Vector();
}
