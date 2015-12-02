package tum.umlsec.viki.tools.uml2java.javagenerator;

import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import tum.umlsec.viki.framework.ILogOutput;
import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.mdr.IMdrContainer;
import tum.umlsec.viki.framework.toolbase.CommandDescriptor;
import tum.umlsec.viki.framework.toolbase.CommandParameterDescriptor;
import tum.umlsec.viki.framework.toolbase.IVikiToolBase;
import tum.umlsec.viki.framework.toolbase.IVikiToolConsole;
import tum.umlsec.viki.framework.toolbase.IVikiToolGui;
import tum.umlsec.viki.framework.toolbase.IVikiToolWeb;

public class JavaGenerator implements IVikiToolBase, IVikiToolConsole {
	public static final String directory = "code";
	private final int CID_GENERATE_JAVA = 1;
	public static boolean aspectj = true;
		
	CommandParameterDescriptor parameterOutputFileName;

	@Override
	public IVikiToolConsole getConsole() {
		return this;
	}

	@Override
	public IVikiToolGui getGui() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getToolDescription() {
		return "Generates Java code from UML diagrams, and AspectJ aspects from the UMLsec properties";
	}

	@Override
	public String getToolName() {
		return "Java and AspectJ Code Generator";
	}

	@Override
	public IVikiToolWeb getWeb() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initialiseBase(IMdrContainer container) {
		mdrContainer = container;
		Vector parameters = new Vector();	 
		commands = new Vector();		
	}

	@Override
	public void executeConsoleCommand(CommandDescriptor _command,
			Iterator _parameters, ITextOutput output, ILogOutput output2) {
		switch (_command.getId()) {
		case CID_GENERATE_JAVA:
			output.writeLn("Generating sourcecode ...");
			try {
					ClassDiagram diag = new ClassDiagram(mdrContainer, output);
				} catch (IOException e) {
					output.writeLn("IO Error. Aborting.");
					output.writeLn(e.getMessage());
					e.printStackTrace();
					return;
				}
	    	//diag.createJAVAFile(output);
	    	output.writeLn("Code generation completed");
		}
		
	}

	@Override
	public IVikiToolBase getBase() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator getConsoleCommands() {
		commands = new Vector();
		commands.add(commandSaveJava);
		return commands.iterator();
	}

	@Override
	public void initialiseConsole() {
		// TODO Auto-generated method stub
		
	}
	
	IMdrContainer mdrContainer;

	Vector _parameters = new Vector();
	Vector _parameters2 = new Vector();
	Vector _parametersEmpty = new Vector();
	

	CommandDescriptor commandSaveJava = CommandDescriptor.CommandDescriptorConsole(CID_GENERATE_JAVA, "Generate", "Generate", true, _parametersEmpty); 
	
	Vector commands;

}
