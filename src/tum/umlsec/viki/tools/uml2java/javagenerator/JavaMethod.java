package tum.umlsec.viki.tools.uml2java.javagenerator;

import java.util.ArrayList;
import java.util.Iterator;

import org.omg.uml.foundation.core.Operation;
import org.omg.uml.foundation.core.Parameter;

public class JavaMethod {
	private String visibility;
	private boolean isStatic;
	private String rtnVal = null;
	private String name;
	private String parent;
	private ArrayList<JavaParameter> parameters = new ArrayList<JavaParameter>();
	
	public JavaMethod(Operation op) {
		name = op.getName();
		System.out.println("Parent class: " + op.getOwner().getName());
		parent = op.getOwner().getName();
		visibility = op.getVisibility().toString().substring(3);
		for (Iterator<Parameter> iter = op.getParameter().iterator(); iter.hasNext(); ) {
			Parameter param = iter.next();
			// "real" parameter
			if (param.getKind().toString().equals("pdk_in")) {
				parameters.add(new JavaParameter(param));
			// return value
			} else if (param.getKind().toString().equals("pdk_return")){
				try {
				rtnVal = param.getType().getName();
				System.out.println("return: " + param.getType().getName());
				} catch (NullPointerException e) {
					rtnVal = null;
				}
			}
			// shouldn't be anything else
			else {
				throw new RuntimeException("Unexpected parameter kind: " + param.getKind().toString());
			}
		}
	}
	
	/**
	 * 
	 * @return the method's signature in Java
	 */
	public String getSignature() {
		String sig = visibility;
		if (isStatic)
			sig = sig + " static";
		if (rtnVal != null)
			sig = sig + " " + rtnVal;
		else
			sig = sig + " void";
		sig = sig + " " + name;
		sig = sig + "(";
//		for (int i = 0; i < parameters.size(); i++) {
//			if (i != 0)
//				sig = sig + ", ";
//			sig = sig + parameters.get(i).toString();
//		}
		sig = sig + ")";
		return sig;
	}
	
	public String getName() {
		return name;
	}
	
	/**
	 * Generate the code of the method body
	 * @return a String containing the method body
	 */
	public String getBody() {
		return "";
	}
	
	/**
	 * Returns the name of the parent class
	 * @return a String with the name of the parent class
	 */
	public String getParent() {
		return parent;
	}
	
	public String toString() {
		String ret = getSignature() + " {" + System.getProperty("line.separator");
		ret = ret + getBody();
		ret = ret + "}" + System.getProperty("line.separator");
		return ret;
	}
}
