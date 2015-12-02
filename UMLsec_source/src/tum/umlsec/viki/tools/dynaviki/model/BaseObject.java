package tum.umlsec.viki.tools.dynaviki.model;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author pasha
 */
public abstract class BaseObject {
	public BaseObject(ModelRoot _root) {
		root = _root;
	}
	
	
	
	protected void setRoot(ModelRoot _root) {
		root = _root;
	}
	
	protected ModelRoot getRoot() {
		return root;
	}
	
	
	protected void auxParseCollection(Collection c, String _collectionName) {
		System.out.println(_collectionName + "=====================================");
		for(Iterator it = c.iterator(); it.hasNext(); ) {
			Object ab = it.next();

			System.out.println(ab.getClass() + ": " + ab.toString());
		}
		System.out.println("=======================================================");
	}

	protected abstract void createNodeStructure();
	public abstract void initialise();	
	
	
	protected ModelRoot root;
	
	
}
