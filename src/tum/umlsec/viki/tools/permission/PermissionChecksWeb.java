package tum.umlsec.viki.tools.permission;

import java.util.Iterator;
import java.util.Vector;

import tum.umlsec.viki.framework.ILogOutput;
import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.mdr.IMdrContainer;
import tum.umlsec.viki.framework.toolbase.CommandDescriptor;
import tum.umlsec.viki.framework.toolbase.IVikiToolBase;
import tum.umlsec.viki.framework.toolbase.IVikiToolWeb;
import tum.umlsec.viki.tools.permission.permission.PermissionChecker;
import tum.umlsec.viki.tools.permission.prologGenerator.PermissionPrologGenerator;


/**
 * @author Markus
 */
public class PermissionChecksWeb implements IVikiToolWeb  {

    private final int CID_COMMAND01 = 1;
    private final int CID_COMMAND02 = 2;

    public String getToolName() {
        return "Sequence-Diagram/Permission Analyzer";
    }

    public String getToolDescription() {
        return "";
    }

    public Iterator getConsoleCommands() {
        // rebuild command vector here
        return this.commands.iterator();
    }
	
	
    public void executeWebCommand(
        CommandDescriptor _command, 
        Iterator _parameters, 
        ITextOutput _mainOutput, 
        ILogOutput _auxOutput) 
    
    {
        switch(_command.getId()) {
            case CID_COMMAND01:{
                PermissionChecker _checker = new PermissionChecker();
                _checker.check(mdrContainer, _parameters, _mainOutput);
                break;  
            }
			
            case CID_COMMAND02:{
                PermissionPrologGenerator _checker = new PermissionPrologGenerator();
                _checker.check(mdrContainer, _mainOutput, null );
                break; 
            }
        }
    }
	
    public IVikiToolBase getBase() {
        return new PermissionChecks();
    }        
	
    public Iterator getWebCommands() {
        return this.commands.iterator();
    }        
	
    public void initialiseWeb() {
        Vector _parametersEmpty = new Vector();

        CommandDescriptor cmd01 = CommandDescriptor.CommandDescriptorConsole(
            CID_COMMAND01, 
            "Static model check", 
            "Check class- and sequencediagramm", 
            true, parametersEmpty); 
	
        CommandDescriptor cmd02 = CommandDescriptor.CommandDescriptorConsole(
            CID_COMMAND02, 
            "Prolog generation", 
            "Generation of a prolog program for a dynamic check of a sequence diagram",  
            true, parametersEmpty); 

        commands.add(cmd01); 
        commands.add(cmd02); 
    }
	
	
    IMdrContainer mdrContainer;
	
    Vector parametersEmpty = new Vector();
    CommandDescriptor cmd01; 
    CommandDescriptor cmd02; 
    Vector commands = new Vector();	 
}
