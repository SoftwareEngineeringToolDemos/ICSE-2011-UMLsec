package tum.umlsec.viki.tools.dynaviki.model.objectgraph;

import java.util.Collection;
import java.util.Vector;

import tum.umlsec.viki.tools.dynaviki.model.MD_Object;
import tum.umlsec.viki.tools.dynaviki.model.ModelRoot;



/**
 * @author pasha
 */
public class OGGraph {





	public OGGraph(ModelRoot _modelRoot) {
		modelRoot = _modelRoot;
	}

	public Collection getAllVertexes() {
		return allVertexes;	
	}
	
	public void addVertex(MD_Object _object) {
		allVertexes.add(new OGVertexObject(modelRoot, _object));
	}

	
	
	Vector allVertexes = new Vector();
	
	ModelRoot modelRoot;
}
