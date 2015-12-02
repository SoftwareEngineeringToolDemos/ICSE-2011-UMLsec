package tum.umlsec.viki.tools.permission;

import java.util.Iterator;
import java.util.Vector;

import tum.umlsec.viki.framework.ExceptionProgrammLogicError;
import tum.umlsec.viki.framework.ILogOutput;
import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.mdr.IMdrContainer;
import tum.umlsec.viki.framework.toolbase.CommandDescriptor;
import tum.umlsec.viki.framework.toolbase.CommandParameterDescriptor;
import tum.umlsec.viki.framework.toolbase.IVikiToolBase;
import tum.umlsec.viki.framework.toolbase.IVikiToolConsole;
import tum.umlsec.viki.framework.toolbase.IVikiToolGui;
import tum.umlsec.viki.framework.toolbase.IVikiToolWeb;
import tum.umlsec.viki.tools.permission.permission.PermissionChecker;
import tum.umlsec.viki.tools.permission.prologGenerator.PermissionPrologGenerator;


/**
 * @author Markus
 */
public class PermissionChecks implements IVikiToolBase, IVikiToolConsole {

    public static final int CID_COMMAND01 = 1;
    public static final int CID_COMMAND02 = 2;
	
	

    public IVikiToolConsole getConsole() { return this; }
    public IVikiToolGui getGui() { return null; }
    public IVikiToolWeb getWeb() { return new PermissionChecksWeb(); }


    public IVikiToolBase getBase() {
        return this;
    }
	 
    public void initialiseBase(IMdrContainer _container) {
        mdrContainer = _container;
	
 	Vector _parametersEmpty = new Vector();
        Vector parameterCmd02 = new Vector();
        parameterCmd02.add(parameterPrologFile);

        CommandDescriptor cmd01 = 
            CommandDescriptor.CommandDescriptorConsole(
                    CID_COMMAND01, 
                    "Static model check", 
                    "Check class- and sequencediagramm", 
                    true, parametersEmpty); 
	
        CommandDescriptor cmd02 = 
            CommandDescriptor.CommandDescriptorConsole(
                    CID_COMMAND02, 
                    "Prolog generation", 
                    "Generation of a prolog program for " +
                    "a dynamic check of a sequence diagram",  
                    true, parameterCmd02); 
        
        commands.add(cmd01); 
        commands.add(cmd02); 
    }

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
	
    public void initialiseConsole() {
    }
	


    public void executeConsoleCommand(
            CommandDescriptor _command, 
            Iterator _parameters, 
            ITextOutput _mainOutput,
            ILogOutput _auxOutput) 
    
    {
        switch(_command.getId()) {
            case CID_COMMAND01:{
                PermissionChecker _checker = new PermissionChecker();
                _checker.check(mdrContainer, _parameters, _mainOutput);
            }
            break;  
			
            case CID_COMMAND02:{
                if (_parameters.hasNext()) {
                    CommandParameterDescriptor _p = 
                        (CommandParameterDescriptor) _parameters.next();
                    PermissionPrologGenerator _checker 
                        = new PermissionPrologGenerator();
                    _checker.check(mdrContainer, _mainOutput, _p );
                }
                else 
                    throw new 
                        ExceptionProgrammLogicError("Required parameter missing.");
		break; 
            }
        }
    }

    IMdrContainer mdrContainer;
    Vector parametersEmpty = new Vector();
    CommandParameterDescriptor parameterPrologFile = 
        CommandParameterDescriptor.CommandParameterDescriptorFile(
                            1, 
                            "Prolog File for output" );

    CommandDescriptor cmd01; 
    CommandDescriptor cmd02; 
    Vector commands = new Vector();
	 
}
