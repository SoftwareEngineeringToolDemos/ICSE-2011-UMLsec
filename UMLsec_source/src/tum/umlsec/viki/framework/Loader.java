package tum.umlsec.viki.framework;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import tum.umlsec.viki.framework.console.FrameworkConsole;
import tum.umlsec.viki.framework.gui.FrameworkGui;
import tum.umlsec.viki.framework.toolbase.IVikiToolBase;
import tum.umlsec.viki.tools.UMLSafe.ToolSafeTest;
import tum.umlsec.viki.tools.activityparser.ToolActivity;
import tum.umlsec.viki.tools.berechtigungen.ToolBerechtigungen;
import tum.umlsec.viki.tools.berechtigungen2.ToolBerechtigungen2;
import tum.umlsec.viki.tools.checkstatic.ToolCheckStatic;
import tum.umlsec.viki.tools.checkstatic.ToolCheckStaticNew;
import tum.umlsec.viki.tools.dynaviki.VikiToolDynamic;
import tum.umlsec.viki.tools.jmapper.VikiToolJmapper;
import tum.umlsec.viki.tools.mdrview.VikiToolMdrViewer;
import tum.umlsec.viki.tools.permission.PermissionChecks;
import tum.umlsec.viki.tools.sample.VikiToolSample;
import tum.umlsec.viki.tools.sample2.VikiToolSample2;
import tum.umlsec.viki.tools.sapperm.ToolSAPPerm;
import tum.umlsec.viki.tools.sequence2prolog.ToolSequence2Prolog;
import tum.umlsec.viki.tools.sequenceanalyser.ToolSequenceAnalyser;
import tum.umlsec.viki.tools.sequencediagram.ToolSequenceDiagramLocal;
import tum.umlsec.viki.tools.sequenceparser.ToolSequence;
import tum.umlsec.viki.tools.statechart2prolog.ToolStatechart2Prolog;
import tum.umlsec.viki.tools.statechartexport.ToolExport;
import tum.umlsec.viki.tools.statechartparser.ToolStatechart;
import tum.umlsec.viki.tools.subsystemparser.ToolSubsystem;
//import tum.umlsec.viki.tools.uml2asm.ToolASMCodeGenerator;
import tum.umlsec.viki.tools.uml2java.ToolJAVACodeGenerator;
import tum.umlsec.viki.tools.uml2java.javagenerator.JavaGenerator;
import tum.umlsec.viki.tools.uml2tptp.ToolUml2TPTP;
import tum.umlsec.viki.tools.umlseChNotationAnalyser.ToolUMLseChNotationAnalyser;
import tum.umlsec.viki.tools.umlseChStaticCheck.ToolUMLseChStaticCheck;
import tum.umlsec.viki.tools.umlsecNotationAnalyser.ToolUMLsecNotationAnalyser;

/**
 * @author pasha
 */
public class Loader {
	static IVikiToolBase []tools = {
	    new VikiToolDynamic(), 
		new ToolCheckStatic(), 
		new ToolCheckStaticNew(),
		new VikiToolMdrViewer(), 
		new VikiToolSample(), 
		new VikiToolSample2(), 
		new ToolActivity(),
		new ToolSequence(),
		new ToolStatechart(),
		new ToolSubsystem(),
		new ToolBerechtigungen(),
		new PermissionChecks(),
		new ToolExport(),
		new ToolSequenceAnalyser(),
		new ToolStatechart2Prolog(),
		new ToolBerechtigungen2(),
		new ToolSafeTest(),
		new ToolSequenceDiagramLocal(),
		//new ToolASMCodeGenerator(),
        new ToolUMLsecNotationAnalyser(), /* New Tool */
        new ToolUMLseChNotationAnalyser(), /* New Tool */
        new ToolUMLseChStaticCheck(), /* New Tool */
        new ToolSAPPerm(),
        new ToolUml2TPTP(),
		new ToolSequence2Prolog(),
		new VikiToolJmapper(),
		new ToolJAVACodeGenerator(),
		new JavaGenerator()
	};
	static Logger logger = Logger.getLogger("UMLsec tool");
	
	public static void main(String[] argv) {
		PropertyConfigurator.configure("log4j.cfg");
		logger.info("Entering application");
		FrameworkBase _framework = null;
		// reading configuration file
		try {
			Config.readConfig(null);
		} catch (IOException e) {
			logger.error("Configuration file reader : " + e.getMessage());
		} catch (BadConfSyntaxException e) {
			logger.error("Malformed configuration file : " + e.getMessage());
		}
		
		if(argv.length == 0) {
			_framework = new FrameworkGui();
		} else {
			_framework = new FrameworkConsole(argv);
		}
		_framework.initialiseBase(tools, null, null);
		_framework.run();
	}
}
