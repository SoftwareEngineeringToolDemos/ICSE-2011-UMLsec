package tum.umlsec.viki.tools.uml2java.javagenerator;

import java.util.ArrayList;


public class ProtectedMethod {
	private String className;
	private ArrayList<String> methodList = new ArrayList<String>();
	
	public ProtectedMethod(String className) {
		this.className = className;
	}
	
	public ProtectedMethod(String className, String method) {
		this.className = className;
		if (method == null)
			methodList.add(className);
		else
			methodList.add(method);
	}
	
	public ArrayList<String> getMethods() {
		return methodList;
	}
	
	public void addMethod(String method) {
		if (method == null)
			methodList.add(className);
		else
			methodList.add(method);
	}
	
	public String getClassName() {
		return className;
	}

}
