package tum.umlsec.viki.tools.uml2java.javagenerator;

/**
 * 
 * @author Lionel Montrieux <L.M.C.Montrieux@open.ac.uk>
 * 
 * This is kind of an ugly hack to get the AspectJ code 
 * generator to work. Eventually this class and ToolRBACJavaGenerator 
 * will be refactored
 *
 */
public class ToolRBACAspectJGenerator extends JavaGenerator {
	
	@Override
	public String getToolName() {
		return "Java and AspectJ Code Generator";
	}
}
