package tum.umlsec.viki.tools.dynaviki.model.objectgraph;

import tum.umlsec.viki.tools.dynaviki.model.MD_Class;
import tum.umlsec.viki.tools.dynaviki.model.MD_Object;
import tum.umlsec.viki.tools.dynaviki.model.ModelRoot;



/**
 * @author pasha
 */
public class OGVertexObject {
	
	
	
	public OGVertexObject(ModelRoot _modelRoot, MD_Object _objectModel) {
		modelRoot = _modelRoot;
		objectModel = _objectModel;
		initialise();
	}
	
	private void initialise() {
		classModel = objectModel.getClassModel(); 
	}



	MD_Object objectModel;
	MD_Class classModel;
	
	
	ModelRoot modelRoot;
//	Hashtable edges = new Hashtable();  
}
