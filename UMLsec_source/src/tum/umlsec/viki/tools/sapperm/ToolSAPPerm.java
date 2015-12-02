/*
 * ToolSAPPerm.java
 *
 * Created on 23. August 2004, 15:39
 */
/**
 *
 * @author  Milen Ivanov
 */

package tum.umlsec.viki.tools.sapperm;

import java.io.File;
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
import tum.umlsec.viki.tools.sapperm.mdrparser.MDRParser2;
import tum.umlsec.viki.tools.sapperm.xmlparser.XMLDOMParser2;

public class ToolSAPPerm implements IVikiToolBase, IVikiToolConsole {
    
    private static final int CID_SAPPERM_PARSE = 1;
    private static final int CID_SAPPERM_CHECK = 2;
    
    private static final int CPID_FILE1 = 1;
    private static final int CPID_FILE2 = 2;
    
    Vector _parameters1 = new Vector();
    Vector _parameters2 = new Vector(); 
    
    public static MDRParser2 mdrparser;
    public static XMLDOMParser2 xmldomparser;
       
    Vector commands = new Vector();
    
//    private Vector activities;
    
    IMdrContainer mdrContainer;

    CommandParameterDescriptor parameterFile1 = CommandParameterDescriptor.CommandParameterDescriptorFile(CPID_FILE1, "File");
    CommandParameterDescriptor parameterFile2 = CommandParameterDescriptor.CommandParameterDescriptorFile(CPID_FILE2, "File");
    
    CommandDescriptor cmd01 = CommandDescriptor.CommandDescriptorConsole(CID_SAPPERM_PARSE, "Parse model and data", "First command", true, _parameters1);
    CommandDescriptor cmd02 = CommandDescriptor.CommandDescriptorConsole(CID_SAPPERM_CHECK, "Analyse Model", "Second command", true, _parameters2);
        
    /** Creates a new instance of ToolSAPPerm */
    public ToolSAPPerm() {
    }
    public IVikiToolConsole getConsole() { return this; }
    public IVikiToolGui getGui() { return null; }
    public IVikiToolWeb getWeb() { return null; }
    
    public IVikiToolBase getBase() { return this; }
    
    public String getToolName() {	return "Activity-Diagram/Permission Analyzer II";	}
    public String getToolDescription() {	return "Activity-Diagram/Permission Analyzer II";	}

    
    public void initialiseBase(IMdrContainer _mdrContainer) {
        mdrContainer = _mdrContainer;
  
        _parameters1.add(parameterFile1);
        _parameters2.add(parameterFile2);
        
        commands.add(cmd01);
        commands.add(cmd02);
    }
    
        
    
    public void initialiseConsole() {
    }
    public Iterator getConsoleCommands() {
        return commands.iterator();
    }
    public void executeConsoleCommand(CommandDescriptor _command, Iterator _parameters, ITextOutput _mainOutput, ILogOutput _auxOutput) {
        
        String url = "";
        switch(_command.getId()) {
            case CID_SAPPERM_PARSE:
                _mainOutput.writeLn("Execute parsing of UML model ........");
                
                mdrparser = new MDRParser2(_mainOutput);
                mdrparser.parseUMLDiagram(mdrContainer);
                _mainOutput.writeLn("Parsing UML OK!");
                
                File _xmlModel = new File("");
                boolean _foundFile1 = false;
                for (; _parameters.hasNext();) {
                    CommandParameterDescriptor _parameter = (CommandParameterDescriptor) _parameters.next();
                    if(_parameter.getId() == ToolSAPPerm.CPID_FILE1) {
                        _xmlModel = _parameter.getAsFile();
                        _foundFile1 = true;
                    }                    
                }
                    if(_foundFile1 == false) {
                        throw new ExceptionProgrammLogicError("Required parameter missing");
                    }
                
                url = _xmlModel.getAbsolutePath();
                
                xmldomparser = new XMLDOMParser2(_mainOutput);
                xmldomparser.loadXMLFile(url);
                
                xmldomparser.parseModelPerm();
                _mainOutput.writeLn("Parsing model permissions OK!");
                _mainOutput.writeLn("************************");
                
               break;
               
            case CID_SAPPERM_CHECK:
                _mainOutput.writeLn("************************");
                _mainOutput.writeLn("Executing check command ........");
                
                 File _xmlSAP = new File("");
                boolean _foundFile2 = false;
                for (; _parameters.hasNext();) {
                    CommandParameterDescriptor _parameter = (CommandParameterDescriptor) _parameters.next();                
                     if(_parameter.getId() == ToolSAPPerm.CPID_FILE2) {
                        _xmlSAP = _parameter.getAsFile();
                        _foundFile2 = true;
                     }
                }      
                if(_foundFile2 == false){
                    throw new ExceptionProgrammLogicError("Required parameter missing");
                }
                
                url = _xmlSAP.getAbsolutePath();
                
 //               xmldomparser = new XMLDOMParser2(_mainOutput);
                xmldomparser.loadXMLFile(url);
                
                xmldomparser.parse();
				ToolSAPPerm.xmldomparser.log = _mainOutput;
                _mainOutput.writeLn("Parsing SAP permissions OK!");
                _mainOutput.writeLn("Ckecking model for inconsistencies......");
                
                CheckPermissions cp = new CheckPermissions();
                cp.run();
                
               break;
               
            default:
                throw new ExceptionProgrammLogicError("Unknown command");
        }
        
        
    }
    
    
}
