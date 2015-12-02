/**
 * @author Erwin Yukselgil
 * This class is derived from sample2
 */
package tum.umlsec.viki.tools.uml2tptp;


import java.util.Iterator;
import java.util.Vector;

import tum.umlsec.viki.framework.ILogOutput;
import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.mdr.IMdrContainer;
import tum.umlsec.viki.framework.toolbase.CommandDescriptor;
import tum.umlsec.viki.framework.toolbase.IVikiToolBase;
import tum.umlsec.viki.framework.toolbase.IVikiToolConsole;
import tum.umlsec.viki.framework.toolbase.IVikiToolGui;
import tum.umlsec.viki.framework.toolbase.IVikiToolWeb;
import tum.umlsec.viki.tools.uml2tptp.statechartreader.TPTPGenerator;


public class ToolUml2TPTP implements IVikiToolBase, IVikiToolConsole {


    private IMdrContainer mdrContainer;
	private CommandDescriptor commandTPTP1;
	private CommandDescriptor commandTPTP2;
	private CommandDescriptor commandTPTP3;
	
	private final int CID_TPTP1 = 1;
	
	private Vector parameters = new Vector();
	
	public IVikiToolConsole getConsole() { return this; }
	public IVikiToolGui getGui() { return null; }
	public IVikiToolWeb getWeb() { return null; }
	public IVikiToolBase getBase() { return this; }
	public String getToolName() { return "Uml2TPTP"; } 
	public String getToolDescription() {return "This is a tool for translating UML-Diagrams to TPTP-Syntax";}
	public void initialiseConsole() {	}
	
	public void initialiseBase(IMdrContainer _container) {
		mdrContainer = _container;
			
		commandTPTP1 = CommandDescriptor.CommandDescriptorConsole(CID_TPTP1, "Statecharts2TPTP1", "Translates Statechartdiagramms to TPTP", true, parameters);
	}

	
	public Iterator getConsoleCommands() {
		Vector _commands = new Vector();
		_commands.add(commandTPTP1);
		
		return _commands.iterator();
	}	


	public void executeConsoleCommand(CommandDescriptor _command, Iterator _parameters, ITextOutput _mainOutput, ILogOutput _auxOutput) {

	    Vector _tptpRulesVector = new Vector();
	    Iterator _tptpRulesIterator;
	    
	    switch(_command.getId()) {
			case CID_TPTP1:
			    if(mdrContainer.isEmpty()){
			        _mainOutput.writeLn("Please load a valid UML-Diagramm!");
			    }
			    else{
			        TPTPGenerator _tptpRules = new TPTPGenerator(mdrContainer);

				    _tptpRulesVector = _tptpRules.generateTPTP();
				    _tptpRulesIterator = _tptpRulesVector.iterator();
				    
				    while(_tptpRulesIterator.hasNext()){
				        _mainOutput.writeLn((String)_tptpRulesIterator.next());
				    }
				    
				    break;    
			    }			    			    
		}
	}
}
