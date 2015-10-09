package open.umlsec.tools.checksystem.gui;

import open.umlsec.tools.checksystem.ToolSystemVerification;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

import tum.umlsec.viki.framework.FrameworkBase;
import tum.umlsec.viki.framework.toolbase.IVikiToolBase;
import tum.umlsec.viki.tools.activityparser.ToolActivity;
import tum.umlsec.viki.tools.berechtigungen.ToolBerechtigungen;
import tum.umlsec.viki.tools.berechtigungen2.ToolBerechtigungen2;
import tum.umlsec.viki.tools.checkstatic.ToolCheckStatic;
import tum.umlsec.viki.tools.checkstatic.ToolCheckStaticNew;
import tum.umlsec.viki.tools.jmapper.VikiToolJmapper;
import tum.umlsec.viki.tools.permission.PermissionChecks;
import tum.umlsec.viki.tools.riskFinder.RiskFinder;
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
import tum.umlsec.viki.tools.uml2tptp.ToolUml2TPTP;
import tum.umlsec.viki.tools.umlseChNotationAnalyser.ToolUMLseChNotationAnalyser;
import tum.umlsec.viki.tools.umlseChStaticCheck.ToolUMLseChStaticCheck;
import tum.umlsec.viki.tools.umlsecNotationAnalyser.ToolUMLsecNotationAnalyser;
import tum.umlsec.viki.tools.uml2java.javagenerator.ToolRBACAspectJGenerator;
import tum.umlsec.viki.tools.uml2java.javagenerator.ToolRBACJavaGenerator;


/**
 * 
 * @author (GUI) Serge Kater
 * 
 */
public class SystemVerificationLoader extends SingleFrameApplication {
	static IVikiToolBase []tools = {
		new ToolSystemVerification(),
		new ToolCheckStatic(), 
		new ToolCheckStaticNew(),
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
		new ToolSequenceDiagramLocal(),
		//new ToolASMCodeGenerator(), deactivated because causing trouble when compiling within a VM
        new ToolSAPPerm(),
        new ToolUml2TPTP(),
		new ToolSequence2Prolog(),
		new VikiToolJmapper(),
		new ToolJAVACodeGenerator(),
		new RiskFinder(),
        new ToolUMLsecNotationAnalyser(), //New Tool
        new ToolUMLseChNotationAnalyser(), //New Tool
        new ToolUMLseChStaticCheck(), //New Tool
		new ToolRBACJavaGenerator(),
		new ToolRBACAspectJGenerator()
	};
	
	
	/* tool indices must be adapted if tools are added */
	static public final int TOOL_IDX_CHECKSYSTEM 				= 0;
	static public final int TOOL_IDX_CHECKSTATIC 				= 1;
	static public final int TOOL_IDX_CHECKSTATIC2 				= 2;
	static public final int TOOL_IDX_ACTIVITY 					= 3;
	static public final int TOOL_IDX_SEQUENCE 					= 4;
	static public final int TOOL_IDX_STATECHART 				= 5;
	static public final int TOOL_IDX_SUBSYSTEM 					= 6;
	static public final int TOOL_IDX_PERMISSION_ANALYSER_A 		= 7;
	static public final int TOOL_IDX_PERMISSION_ANALYSER_S 		= 8;
	static public final int TOOL_IDX_CRYPTO_FOL_ANALYSER_ST		= 9;
	static public final int TOOL_IDX_CRYPTO_FOL_ANALYSER_SE		= 10;
	static public final int TOOL_IDX_CRYPTO_ATTACK_GENERATOR	= 11;
	static public final int TOOL_IDX_ACTIVITY_RBAC_ANALYSER		= 12;
	static public final int TOOL_IDX_AUTHENTICITY_FOL_ANALYSER	= 13;
	//static public final int TOOL_IDX_ASM_CODE_GENERATOR			= 14; deactivated because causing trouble when compiling within a VM
	static public final int TOOL_IDX_SAP_PERM					= 14;
	static public final int TOOL_IDX_UML2TPTP					= 15;
	static public final int TOOL_IDX_SEQUENCE_TO_PROLOG			= 16;
	static public final int TOOL_IDX_JMAPPER					= 17;
	static public final int TOOL_IDX_JAVA_CODE_GENERATOR		= 18;
	static public final int TOOL_IDX_RISKFINDER					= 19;
	static public final int TOOL_IDX_UMLSECNOTATIONANALYSER		= 20; //New Tool
    static public final int TOOL_IDX_UMLSECHNOTATIONANALYSER	= 21; //New Tool
    static public final int TOOL_IDX_UMLSECHSTATICCHECK     	= 22; //New Tool
    static public final int TOOL_IDX_RBAC_TO_JAVA_GENERATOR		= 23; // UMLsec code generator, Java-only version
    static public final int TOOL_IDX_RBAC_TO_ASPECTJ_GENERATOR	= 24; // UMLsec code generator, AspectJ version

    static public final int TOOL_IDX_MAX                            = 25;
	
	static public Logger logger = Logger.getRootLogger();
	
	static private FrameworkBase framework;
	static private SystemVerificationGuiView mainGui =null;
	static private SystemVerificationLoader loader;
	
    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
    	mainGui = new SystemVerificationGuiView(this);
    	show(mainGui);
    	mainGui.switchToMainTab();
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of SystemVerificationGuiApp
     */
    public static SystemVerificationLoader getApplication() {
        return Application.getInstance(SystemVerificationLoader.class);
    }
	
    public static FrameworkBase getFramework(){
    	return framework;
    }
    
    public static IVikiToolBase[] getTools(){
    	return tools;
    }
	
    public static IVikiToolBase getTool(int iIdx){
    	return tools[iIdx];
    }

    public static SystemVerificationGuiView getGui(){
    	return mainGui;
    }
    
    /**
	 * @param args
	 */
	public static void main(String[] args) {
		PropertyConfigurator.configure("log4j.cfg");
		logger.info("Entering application");
		framework = new SystemVerificationGui();
		
		framework.initialiseBase(tools, null, null);
		framework.run();
		
//		launch(SystemVerificationLoader.class, args);
//		I think this calling is better (Bianca)
		loader.launch(SystemVerificationLoader.class, args);
	}

}
