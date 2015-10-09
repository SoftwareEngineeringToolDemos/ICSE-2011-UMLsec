package tum.umlsec.viki.tools.dynaviki.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import org.omg.uml.foundation.core.Operation;
import org.omg.uml.foundation.core.Parameter;
import org.omg.uml.foundation.datatypes.ParameterDirectionKindEnum;

import tum.umlsec.viki.tools.dynaviki.model.interfaces.ITreeNodeCollection;

/**
 * @author pasha
 */
public class MD_Operation extends BaseObject implements ITreeNodeCollection {
	public MD_Operation(ModelRoot _root, Operation _operation, MD_Class _parentClassModel) {
		super(_root);
		mdrOperation = _operation;
		parentClassModel = _parentClassModel;		
	}


	protected void createNodeStructure() {
		children.removeAllElements();
		
		children.add(parameters);
	}
	
	public void initialise() {
		createNodeStructure();
		
		for (Iterator _iter = mdrOperation.getParameter().iterator(); _iter.hasNext();) {
			Parameter _parameter = (Parameter)_iter.next();
			
			if(_parameter.getKind() == ParameterDirectionKindEnum.PDK_RETURN) {
				continue;
			}
			
			MD_OperationParameter _newModel = new MD_OperationParameter(getRoot(), _parameter);
			parameters.add(_newModel);
			_newModel.initialise();
		}		
	}
	
	
	

	public Collection getChildren() {
		return children;
	}

	public String getName() {
		return mdrOperation.getName();
	}

	public String getNodeName() {
		return mdrOperation.getName();
	}

	public String getNodeText() {
		return null;
	}

	public String getPromelaEventName() {
		return "E_" + parentClassModel.getName() + "_" + getName();
	}


	public int getParameterCount() {
		return parameters.size();
	}






	private Operation mdrOperation; 
	MD_Class parentClassModel;
	
	
	private AuxVector parameters = new AuxVector("Parameters");
	private Vector children = new Vector();
}
