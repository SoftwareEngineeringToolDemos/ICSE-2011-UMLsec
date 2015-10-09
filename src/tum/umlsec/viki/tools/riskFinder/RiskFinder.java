package tum.umlsec.viki.tools.riskFinder;

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
import tum.umlsec.viki.tools.checkstatic.StaticCheckerBase;
/**
 * Riskfinder-Plugin für das UMLsec-Framework
 * 
 * @author Marc Peschke
 * marc.peschke@isst.fraunhofer.de<br>
 *
 * Hauptklasse des Riskfinder <br>
 * 
 * erstellt neues Checker-Objekt und ruft Methode check auf, die Analyse ausführt
 */
public class RiskFinder implements IVikiToolBase, IVikiToolConsole {

	public static final int CPID_GETINFO = 0;

	IMdrContainer mdrContainer;
	boolean returnValue;
	ITextOutput _textOutput;
	Vector<CommandParameterDescriptor> modParameters = new Vector<CommandParameterDescriptor>();
	Vector<CommandDescriptor> commands = new Vector<CommandDescriptor>();
	private static StaticCheckerBase checker = null;
	


	public RiskFinder(){
		//logger.info("RiskFinder alive!");
	}
	
	public IVikiToolConsole getConsole(){
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
		return "RiskFinder";
	}
	
	public String getToolDescription() {
		return "Risk Management";
	}

	/**
	 * initialisiert Commanddescriptor und fügt Command hinzu<br>
	 * neuer Commands werden in Auswahlliste sichtbar
	 * @param mdrContainer
	 */	
	public void initialiseBase(IMdrContainer _mdrContainer) {
		
		mdrContainer = _mdrContainer;
		
		//hier wird die Auswahl fuer das rechte Fenster erstellt und weiter unten hinzugefuegt
		CommandDescriptor cmdGetInfo = CommandDescriptor.CommandDescriptorConsole(CPID_GETINFO, "find Risks", "find a critical Activity", true, modParameters);
		
		//fuegt Befehle in Liste hinzu
        commands.add(cmdGetInfo);
		
		}

	public Iterator<CommandDescriptor> getConsoleCommands(){

		return commands.iterator();
	}

	public void initialiseConsole() {
		
	}

	/**
	 * ruft die unterschiedlichen Befehler für die Punkte der Auswahlliste auf
	 * @param CommandDescriptor
	 * @param Iterator über die parameter
	 * @param ITextOutput
	 * @param ILogOutput
	 * 
	 */
	@SuppressWarnings("rawtypes")
	public void executeConsoleCommand(CommandDescriptor command, Iterator _parameters, ITextOutput mainOutput, ILogOutput auxOutput) {
		
		//hier werden die unterschiedlichen Befehle aufgerufen
		switch(command.getId()) {
			case CPID_GETINFO:
				checker = getChecker();
				returnValue = checker.check(mdrContainer, _parameters, mainOutput);
			break;
			
			default:
				throw new ExceptionProgrammLogicError("Unknown command");
		}
		
	}
	
	/**
	 * gibt das checker-Objekt zurück
	 * @return StaticCheckerBase
	 */
	public static StaticCheckerBase getChecker() {
				
		if (checker == null)
			checker = new CheckerRiskfinder();
		
		return checker;
	}
	
}

