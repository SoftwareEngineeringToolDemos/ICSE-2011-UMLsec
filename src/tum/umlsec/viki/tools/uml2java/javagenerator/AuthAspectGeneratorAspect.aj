package tum.umlsec.viki.tools.uml2java.javagenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.omg.uml.behavioralelements.activitygraphs.ActionState;
import org.omg.uml.behavioralelements.activitygraphs.ActivityGraph;
import org.omg.uml.behavioralelements.commonbehavior.CallAction;
import org.omg.uml.foundation.core.Operation;
import org.omg.uml.foundation.core.Stereotype;
import org.omg.uml.foundation.core.TaggedValue;

import tum.umlsec.viki.framework.ILogOutput;
import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.toolbase.CommandDescriptor;

public aspect AuthAspectGeneratorAspect {
	// By default, Java + AspectJ code is generated. If the value is set to false, then only Java code is generated
	private boolean aspectj = true;
	private String newline = System.getProperty( "line.separator" );
	private ArrayList<String> protectedActions = new ArrayList<String>();
	private ArrayList<ProtectedMethod> protectedMethods = new ArrayList<ProtectedMethod>();

	// adding a boolean value to the JavaMethod class, which will be true if the method call has to be protected using access control
	private boolean JavaMethod.accessControl = false;
	// adding a list of roles that can call the method
	private ArrayList<String> JavaMethod.roles = new ArrayList<String>();


	public boolean JavaMethod.isACEnabled() {
		return accessControl;
	}

	public void JavaMethod.setACEnabled(boolean value) {
		accessControl = value;
	}

	public ArrayList<String> JavaMethod.getRoles() {
		return roles;
	}

	public void JavaMethod.addRole(String role) {
		roles.add(role);
	}

	public pointcut methodCreation(JavaMethod method, Operation oper)
	:  execution(public tum.umlsec.viki.tools.uml2java.javagenerator.JavaMethod.new(Operation)) && this(method) && args(oper);

	after(JavaMethod method, Operation oper) : methodCreation(method, oper) {
		Iterator<ProtectedMethod> iter = protectedMethods.iterator();
		while (iter.hasNext()) {
			ProtectedMethod protMeth = iter.next();
			if (protMeth.getClassName().equals(oper.getOwner().getName())) {
				Iterator<String> iterMeth = protMeth.getMethods().iterator();
				while (iterMeth.hasNext()) {
					String tmp = iterMeth.next();
					if (tmp.equals(oper.getName())) {
						method.accessControl = true;
					}
				}
			}
		}
	}
	
	public pointcut methodToString(JavaMethod method)
	:  execution(public String tum.umlsec.viki.tools.uml2java.javagenerator.JavaMethod.getBody())
			&& this(method);
	
	/**
	 * Generating java code for the RBAC properties. Only happens when the "aspectj" var. is 
	 * set to false
	 * @param method
	 * @param op
	 */
	String around(JavaMethod method) : methodToString(method){
		if (aspectj)
			return proceed(method);
		System.out.println("Method: " + method.getSignature());
		if (!method.accessControl)
			return proceed(method);
		
		// So far we might write this file several times, which is a bit ridiculous. Should be moved in 
		// order to generate that file only once
		//Writing permission file
		File permissionFile = new File("code" + System.getProperty("file.separator") + method.getParent() + "Permission.java");
		BufferedWriter output = null;
		try {
			output = new BufferedWriter(new FileWriter(permissionFile));
			output.write("import java.security.*;" + newline + newline);
			output.write("public final class " + method.getParent() + "Permission extends BasicPermission {" + newline);
			output.write("    public " + method.getParent() + "Permission(String name) {" + newline);
			output.write("        super(name);" + newline);
			output.write("    }" + newline);
			output.write("" + newline);
			output.write("    public " + method.getParent() + "Permission(String name, String actions) {" + newline);
			output.write("        super(name, actions);" + newline);
			output.write("    }" + newline);
			output.write("}" + newline);
			output.close();
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
		}
		// If we get here, we have to generate OO code for enforcing RBAC *and* this method has to be protected
		String retval = "AccessController.checkPermission(new " + method.getParent() + "Permission(\"" + method.getName() + "\"));";
		System.out.println("Method " + method.getName() + " is now protected");
		retval = retval + proceed(method);
		return retval;
	}

	public pointcut codeGeneration(JavaGenerator generator)
	: execution(public void tum.umlsec.viki.tools.uml2java.javagenerator.JavaGenerator.executeConsoleCommand(..)) && this(generator);

	/**
	 * Before generating the functional code, we go through all the activity diagrams and 
	 * find out which methods have to be protected
	 * @param diagram
	 */
	before(JavaGenerator generator) : codeGeneration(generator) {
		if (generator instanceof ToolRBACAspectJGenerator)
			aspectj = true;
		else
			aspectj = false;
		for(Iterator<ActivityGraph> iter = generator.mdrContainer.getUmlPackage().getActivityGraphs().getActivityGraph().refAllOfClass().iterator(); iter.hasNext(); ) {
			ActivityGraph graph = (ActivityGraph) iter.next();
			/**
			 * We first go through the "protected" tagged value to identify the list of protected actions
			 */
			for (Iterator<TaggedValue> tValIter = graph.getTaggedValue().iterator(); tValIter.hasNext(); ) {
				TaggedValue tVal = (TaggedValue) tValIter.next();
				if (tVal.getType().getName().equals("protected")) {
					Iterator<String> valueIter = tVal.getDataValue().iterator();
					while (valueIter.hasNext()) {
						String val = valueIter.next();
						if (val.length() > 0) {
							// The value should be of the form \[["action_name"]+\]
							// we remove the brackets
							val = val.substring(1, val.length() - 1);
							// we split the string
							String[] aVal = val.split(",");
							//We remove the quotation marks
							for (int i = 0; i < aVal.length; i++) {
								aVal[i] = aVal[i].trim();
								if (aVal[i].startsWith("\"") && aVal[i].endsWith("\"")) {
									aVal[i] = aVal[i].substring(1, aVal[i].length() - 1);
									protectedActions.add(aVal[i]);
									System.out.println("Protected action: " + aVal[i]);
								}
							}
						}
					}
				}
			}
		}

		/**
		 * We look at each action and, for each one that is protected, extract the corresponding operation in the 
		 * class diagram
		 */
		
/**		System.out.println("******* TEST ******");
		Iterator<CallAction> actionsIter = generator.mdrContainer.getUmlPackage().getCommonBehavior().getCallAction().refAllOfClass().iterator();
		while (actionsIter.hasNext()) {
			CallAction action = actionsIter.next();
			System.out.println("CallAction: " + action.getName());
			System.out.println("Operation: " + action.getOperation().getName());
		}
		System.out.println("******* END TEST ******");
*/		
		
		
		for(Iterator<ActionState> iter = generator.mdrContainer.getUmlPackage().getActivityGraphs().getActionState().refAllOfClass().iterator(); iter.hasNext();) {
			ActionState action = (ActionState) iter.next();
			Iterator<String> protectedIter = protectedActions.iterator();
			while (protectedIter.hasNext()) {
				String name = action.getEntry().getName();
				if (name == null)
					name = "";
				String className = null;
				String method = null;
				String protectedAction = protectedIter.next();
				if (action.getName().equals(protectedAction)) {
					if (name.contains(".")) {
						String[] tmp = new String[2];
						className = name.substring(0, name.indexOf("."));
						method = name.substring(name.indexOf(".") + 1);
					}
					else
						className = name;
					boolean done = false;
					Iterator<ProtectedMethod> protIter = protectedMethods.iterator();
					while (protIter.hasNext()) {
						ProtectedMethod protMethod = protIter.next();
						if (protMethod.getClassName().equals(className)) {
							protMethod.addMethod(method);
							if (method != null)
								System.out.println("new protected method: " + className + "." + method);
							else
								System.out.println("new protected method: " + className + "." + className);
							done = true;
						}
					}
					if (!done) {
						protectedMethods.add(new ProtectedMethod(className, method));
						if (method != null)
							System.out.println("new protected method: " + className + "." + method);
						else
							System.out.println("new protected method: " + className + "." + className);
					}
					System.out.println("Protected method: " + action.getEntry().getName());
				}
			}
		}
	}

	/**
	 * Generation of the aspects enforcing the RBAC properties. Only happens when the "aspectj" var. is 
	 * set to true
	 * @param generator
	 */
	after(JavaGenerator generator) : codeGeneration(generator) {
		if (!aspectj)
			return;
		if (protectedMethods.isEmpty())
			return;
		// there exist methods to be protected
		for (Iterator<ProtectedMethod> iter = protectedMethods.iterator(); iter.hasNext(); ) {
			ProtectedMethod methods = iter.next();
			
			//Writing permission file
			File permissionFile = new File("code" + System.getProperty("file.separator") + methods.getClassName() + "Permission.java");
			BufferedWriter output = null;
			try {
				output = new BufferedWriter(new FileWriter(permissionFile));
				output.write("import java.security.*;" + newline + newline);
				output.write("public final class " + methods.getClassName() + "Permission extends BasicPermission {" + newline);
				output.write("    public " + methods.getClassName() + "Permission(String name) {" + newline);
				output.write("        super(name);" + newline);
				output.write("    }" + newline);
				output.write("" + newline);
				output.write("    public " + methods.getClassName() + "Permission(String name, String actions) {" + newline);
				output.write("        super(name, actions);" + newline);
				output.write("    }" + newline);
				output.write("}" + newline);
				output.close();
			} catch (IOException ex) {
				System.out.println(ex.getMessage());
				return;
			}
			
			//mainOutput.writeLn("Generating RBAC aspect");
			File aspectFile = new File("code" + System.getProperty("file.separator") + methods.getClassName() + "AuthAspect.aj");
			output = null;
			try {
				output = new BufferedWriter(new FileWriter(aspectFile));
				output.write("import org.aspectj.lang.JoinPoint;" + newline);
				output.write("import java.security.Permission;" + newline);
				output.write("import auth.AbstractAuthAspect;" + newline);
				output.write("public aspect " + methods.getClassName() + "AuthAspect extends AbstractAuthAspect {" + newline);
				output.write("public pointcut authOperations() :" + newline);
				Iterator<String> iter2 = methods.getMethods().iterator();
				if (iter2.hasNext()) {
					String meth = iter2.next();
					output.write("execution(public void " + meth + "())" + newline);
				}
				while(iter2.hasNext()) {
					String meth = iter2.next();
					output.write("|| execution(public void " + meth + "())" + newline);
				}
				/*			for (Iterator<JavaClass> iter = diagram.getJavaClasses().iterator(); iter.hasNext(); ) {
				JavaClass javaClass = iter.next();
				for (Iterator<JavaMethod> iterMethods = javaClass.getMethods().iterator(); iterMethods.hasNext(); ) {
					JavaMethod method = iterMethods.next();
					if (method.isACEnabled()) {
						output.write("|| execution(" + method.getSignature() + ")" + newline);
					}
				}
			}*/
				output.write(";" + newline);
				output.write("public Permission getPermission(" + newline);
				output.write("JoinPoint.StaticPart joinPointStaticPart) {" + newline);
				output.write("return new " + methods.getClassName() + "Permission(" + newline);
				output.write("joinPointStaticPart.getSignature().getName());" + newline);
				output.write("}" + newline + "}" + newline);
				output.close();
			} catch (IOException ex) {
				//diagram.mainOutput.writeLn("Can't create the Authorization aspect");
				System.out.println(ex.getMessage());
				return;
			}
		}
	}

	public pointcut role(JavaMethod method)
	: execution(public tum.umlsec.viki.tools.uml2java.javagenerator.JavaMethod.new(..)) && this(method);


	public pointcut rbac(ActivityGraph activity)
	: execution(public tum.umlsec.viki.tools.uml2java.javagenerator.ActivityDiagram.new(..)) && this(activity);

	after(ActivityGraph activity): rbac(activity) {
		boolean rbac = false;
		for(Iterator<Stereotype> iter = activity.getStereotype().iterator(); iter.hasNext(); )  {
			Stereotype ster = iter.next();
			if (ster.getName().equals("rbac"))
				rbac = true;
		}
		if (!rbac)
			return;


	}
}
