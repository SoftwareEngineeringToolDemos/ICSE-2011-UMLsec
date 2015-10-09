package tum.umlsec.viki.tools.uml2java.javagenerator;

import org.omg.uml.foundation.core.Parameter;

/**
 * 
 * @author Lionel Montrieux <L.M.C.Montrieux@open.ac.uk>
 *
 */
public class JavaParameter {
	private String type = null;
	private String name = null;
	
	public JavaParameter(Parameter param) {
		name = param.getName();
	}
	
	public String toString() {
		return name;
	}
	
	public String getType() {
		return type;
	}
	
	public String getName() {
		return name;
	}
}
