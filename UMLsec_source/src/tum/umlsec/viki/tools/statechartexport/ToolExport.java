package tum.umlsec.viki.tools.statechartexport;

import java.util.Iterator;
import java.util.Vector;

import tum.umlsec.viki.framework.ExceptionProgrammLogicError;
import tum.umlsec.viki.framework.ILogOutput;
import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.mdr.IMdrContainer;
import tum.umlsec.viki.framework.toolbase.CommandDescriptor;
import tum.umlsec.viki.framework.toolbase.IVikiToolBase;
import tum.umlsec.viki.framework.toolbase.IVikiToolConsole;
import tum.umlsec.viki.framework.toolbase.IVikiToolGui;
import tum.umlsec.viki.framework.toolbase.IVikiToolWeb;
import tum.umlsec.viki.framework.web.TextCollector;

public class ToolExport implements IVikiToolBase, IVikiToolConsole {

    //1.add ID
    public static final int CID_COMMAND01 = 1;

    public static final int CID_RUNESETHEO = 2;
    
    public static final int CID_RESULTONLY = 3;

  

    public IVikiToolConsole getConsole() {
        return this;
    }

    public IVikiToolGui getGui() {
        return null;
    }

    public IVikiToolWeb getWeb() {
        return null;
    }

    public IVikiToolBase getBase() {
        return this;
    }

    public String getToolName() {
        return "Statechart Crypto FOL-Analyzer";
    }

    public String getToolDescription() {
        return "Export State Chart into another format";
    }

    public void initialiseBase(IMdrContainer _mdrContainer) {
        mdrContainer = _mdrContainer;
        parametersEmpty = new Vector();
        commandExportTPTP = CommandDescriptor.CommandDescriptorConsole(
                CID_COMMAND01, "Export to TPTP", "Export into e-SETHEO format",
                true, parametersEmpty);
        commandRunESetheo = CommandDescriptor.CommandDescriptorConsole(
                CID_RUNESETHEO, "Run e-setheo", "Run e-setheo", true,
                parametersEmpty);
        
        commandResultOnly = CommandDescriptor.CommandDescriptorConsole(
                CID_RESULTONLY, "Only show Result", "Export to TPTP & Run e-setheo", true,
                parametersEmpty);
        //2.add command

    }

    public void initialiseConsole() {
    }

    public Iterator getConsoleCommands() {
//        3 add command
        Vector commands = new Vector();
        commands.add(commandExportTPTP);
        commands.add(commandRunESetheo);
        commands.add(commandResultOnly);
        return commands.iterator();
    }

    public void executeConsoleCommand(CommandDescriptor _command,
            Iterator _parameters, ITextOutput _mainOutput, ILogOutput _auxOutput) {
        switch (_command.getId()) {
        case CID_COMMAND01:
            extractor = new Statechart2TPTP(mdrContainer, _mainOutput,
                    _auxOutput);
            extractor.setStatus(Statechart2TPTP.EXPERT);
            extractor.extract();
			initialized=true;
            break;
        //4.add behavir
        case CID_RUNESETHEO:
			if(initialized){
				if (_mainOutput instanceof TextCollector) {//web interface
					extractor =  new Statechart2TPTP(mdrContainer, _mainOutput,_auxOutput);
					extractor.setStatus(Statechart2TPTP.EXPERT);
					extractor.extract();
					extractor.runESetheo();
				} else {//GUI
					extractor.runESetheo();
				}
	
			}else {//if there is no TPTP, then build one
				extractor =  new Statechart2TPTP(mdrContainer, _mainOutput,_auxOutput);
				extractor.setStatus(Statechart2TPTP.EXPERT);
				extractor.extract();
				extractor.runESetheo();
			}
		break;	


        case CID_RESULTONLY:
            extractor = new Statechart2TPTP(mdrContainer, _mainOutput,
                    _auxOutput);
            _mainOutput.write("");
            extractor.setStatus(Statechart2TPTP.NORMALUSER);
            extractor.extract();
            extractor.runESetheo();
            break;
        default:
            throw new ExceptionProgrammLogicError("Unknown command");
        }

    }

    Vector parametersEmpty;

    //5.add Descriptor
    CommandDescriptor commandExportTPTP;

    CommandDescriptor commandRunESetheo;
    
    CommandDescriptor commandResultOnly;
    
    boolean initialized=false;
    
    IMdrContainer mdrContainer;

    Statechart2TPTP extractor;
}