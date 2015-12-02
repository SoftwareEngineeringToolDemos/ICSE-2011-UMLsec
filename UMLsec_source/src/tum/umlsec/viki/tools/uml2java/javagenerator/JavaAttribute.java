package tum.umlsec.viki.tools.uml2java.javagenerator;

import org.omg.uml.foundation.core.Attribute;

public class JavaAttribute {
	private String name;
	private String type;
	private boolean isStatic;
	private boolean isFinal;
	private String visibility;
	
	public JavaAttribute(Attribute attr) {
		name = attr.getName();
		System.out.println("New attribute: " + name);
		//type = attr.getType().getName();
		visibility = attr.getVisibility().toString().substring(3);
	}
	
	public String toString() {
		String ret = visibility;
		//if (isStatic)
		//	ret = ret + " static";
		//if (isFinal)
		//	ret = ret + " final";
		ret = ret + /*" " + type +*/ " " + name + ";" + System.getProperty("line.separator");
		
		return ret;
	}
}
