package tum.umlsec.viki.tools.uml2java;

import java.io.File;
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


/**
 * @author Ahmed
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */



public class ToolJAVACodeGenerator implements IVikiToolBase, IVikiToolConsole  
{
	
	private final int CID_GENERATE_JAVA = 1;
	public static final int CID_DISPLAY_JAVA= 2;
	public static final int CID_PROTOCOL_ANALYSER= 3;
		
	CommandParameterDescriptor parameterOutputFileName;
	
	String tmpPath = System.getProperty("java.io.tmpdir")+File.separator;
	
	
	public void initialiseBase(IMdrContainer _mdrContainer) 
	{
		mdrContainer = _mdrContainer;
		Vector parameters = new Vector();	 
		commands = new Vector();
	}
	
	
	public Iterator getConsoleCommands() 
	{
		commands = new Vector();
		commands.add(commandSaveJava);
		commands.add(commandDisplayJava);
    	commands.add(commandCheckInformation);
		return commands.iterator();
	}
	
	
	public void initialiseConsole() 
	{
	}
	

	public void executeConsoleCommand(CommandDescriptor _command, Iterator _parameters, ITextOutput _mainOutput, ILogOutput _auxOutput)  
	{
		switch(_command.getId()) 
		{
				 case CID_DISPLAY_JAVA:
				    
				 	JAVAGenerator generator_1 = new JAVAGenerator(mdrContainer, _mainOutput); 
				     String javacode;
				try {
					javacode = generator_1.displayCode();
					_mainOutput.writeLn(javacode);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
					
			    break;
			    
			    
			    case CID_GENERATE_JAVA:
						
			    	_mainOutput.writeLn("Generating JAVA Sourcecode ...");
			    	JAVAGenerator generator = new JAVAGenerator(mdrContainer, _mainOutput);

				     generator.createJAVAFile(_mainOutput);
			    	_mainOutput.writeLn("java Dateien stored at user Home!\n\n\n");
			    	
			    break;	                
				  
			    
			    
			    
			    case CID_PROTOCOL_ANALYSER:
									
					Starter st = new Starter(mdrContainer, _mainOutput);
					String ergebnis = st.Start1("parameter");
					_mainOutput.writeLn("Protokollausgabe:");
					_mainOutput.writeLn("******************************************************************************");
					_mainOutput.writeLn(ergebnis);
					_mainOutput.writeLn("******************************************************************************");
					
					break;
				
		
		}	// End of switch()
	}

	public IVikiToolConsole getConsole() { return this; }
	public IVikiToolGui getGui() { return null; }
	public IVikiToolWeb getWeb() { return null; }

	public IVikiToolBase getBase() { return this; }

	public String getToolName() {			return "Java-Codegenerator";	}
	public String getToolDescription() {	return "Generates Java Code from UML Diagrams";	}

	IMdrContainer mdrContainer;

	Vector _parameters = new Vector();
	Vector _parameters2 = new Vector();
	Vector _parametersEmpty = new Vector();
	

	CommandDescriptor commandCheckInformation = CommandDescriptor.CommandDescriptorConsole(CID_PROTOCOL_ANALYSER, "Protocol Analyser", "Protocol Analyser", true, _parameters);
	CommandDescriptor commandDisplayJava = CommandDescriptor.CommandDescriptorConsole(CID_DISPLAY_JAVA, "Display Java", "Display Java", true, _parametersEmpty);
	CommandDescriptor commandSaveJava = CommandDescriptor.CommandDescriptorConsole(CID_GENERATE_JAVA, "Generate", "Generate", true, _parametersEmpty); 
	
	Vector commands;
		
		
	
}