package tum.umlsec.viki.tools.statechart2prolog;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.omg.uml.UmlPackage;
import org.omg.uml.behavioralelements.commonbehavior.Action;
import org.omg.uml.behavioralelements.statemachines.CompositeState;
import org.omg.uml.behavioralelements.statemachines.Event;
import org.omg.uml.behavioralelements.statemachines.FinalState;
import org.omg.uml.behavioralelements.statemachines.Guard;
import org.omg.uml.behavioralelements.statemachines.Pseudostate;
import org.omg.uml.behavioralelements.statemachines.SimpleState;
import org.omg.uml.behavioralelements.statemachines.StateMachine;
import org.omg.uml.behavioralelements.statemachines.StateMachineClass;
import org.omg.uml.behavioralelements.statemachines.StateMachinesPackage;
import org.omg.uml.behavioralelements.statemachines.StateVertex;
import org.omg.uml.behavioralelements.statemachines.Transition;
import org.omg.uml.foundation.core.Parameter;
import org.omg.uml.foundation.core.TaggedValue;

import tum.umlsec.viki.framework.Config;
import tum.umlsec.viki.framework.ILogOutput;
import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.PropertyNotSetException;
import tum.umlsec.viki.framework.mdr.IMdrContainer;


/**
 * Statechart2Prolog plug-in body. Implements the main functionality of the tool.
 * @author Dimitri Kopjev: kopjev@in.tum.de	
 */
public class Statechart2Prolog {
	private Logger logger = Logger.getLogger("statechart2prolog.Statechart2Prolog");

	/** UML package to process. */
	private UmlPackage root;
	
	/** Standard text output. */
	private ITextOutput output;
	
	/** Standard log output. */
	private ILogOutput log;
	
	/** Instance of Expression for a header of the Prolog file. */
	private Expression header = new Expression();
	
	/** Instance of Expression used to output the main output text of the tool. */
	private Expression exp = new Expression();

	/** Instance of Expression for a footer of the Prolog file. */
	private Expression footer = new Expression();

	/** Instance of Expression that includes header, exp and footer. */
	private Expression text = new Expression();
	
	/** Hashtable to mark transitions to prevent circles. */
	private Hashtable StateRegister = new Hashtable();

	/** Hashtable to save termdepths for terms with already known depth. */
	private Hashtable Termdepth = new Hashtable();

	/** Vector to cache the output while processing a transition. */
	private Vector transOutput;

	/** Vector to save all keys of the model. */
	private Vector keys;
	
	/** Vector to save all variables of the model. */
	private Vector var;

	/** Vector to save variables of current transition. */
	private Vector varloc;
	
	/** Vector for just founded symmetric keys. */
	private Vector symkey_tmp;
	
	/** Vector for symmetric keys. */
	private Vector symkeys;
	
	/** Current transitions name. */
	private String transname;
	
	/** String for error messages by runProlog. */
	private String err = "";
	
	/** Global counter of transitions. */
	private int trnr;
	
	/** Local counter of transitions inside of state. */
	private int trNr;

	/** Indicate if the current transition is the first one in a state. */
	private boolean firsttrans;


	/**
	 *	Class constructor that initializes some class variables.
	 *	@param _mdrContainer MDR Container that includes the UML Diagram.
	 *	@param _textOutput ITextOutput object used by framework to output of main text messages.
	 *	@param _logOutput ILogOutput object used by framework to output of log messages.
	 * @throws FileNotFoundException 
	 * @throws FileNotFoundException 
	 * @throws Exception 
	 */	
	public Statechart2Prolog(IMdrContainer _mdrContainer, ITextOutput _textOutput, ILogOutput _logOutput) throws FileNotFoundException {
		root = _mdrContainer.getUmlPackage();
		output = _textOutput;
		log = _logOutput;
		keys = new Vector();
		keys.addElement("k_a");
		var = new Vector();
		varloc = new Vector();
		transOutput = new Vector();
		symkeys = new Vector();
		symkey_tmp = new Vector();
		trNr = 0;
		String resourceRoot = System.getProperty("tum.umlsec.viki.resourceRoot");
		
		// load the Prolog header
		String headerFile = resourceRoot+"Prolog"+File.separator+"Prologheader.txt";
		logger.debug("Loading header file : " + headerFile);
		header.appendFromFile(headerFile);
 	}


	/**
	 * Generates Prolog code from given Statechart diagrams and execute it.
	 * @param _mdrContainer MDR Container that includes the UML Diagram.
	 * @param _mainOutput ITextOutput object used by framework to output of main text messages.
	 * @param _timeout Timeout in seconds.
	 * @throws PropertyNotSetException if the prolog_path hasn't been set
	 */
	public void runProlog(IMdrContainer _mdrContainer, ITextOutput _mainOutput, int _timeout) throws PropertyNotSetException{
		Vector errors = new Vector();
		Vector output = new Vector();
		String out = "";
		String cmd;
		String line = null;
		// seconds to mseconds
		final int timeout = _timeout*1000;
		
		//if (File.separator.equals("/")) cmd = System.getProperty("user.dir")+File.separator+"pl";
		//else {
		//	File file= new File("c:\\Programme\\pl\\bin\\plcon.exe");
		//	File file1= new File("c:\\Program Files\\pl\\bin\\plcon.exe");
		//	if(file.exists()) cmd="c:\\Programme\\pl\\bin\\plcon";
		//	else if(file1.exists()) cmd="c:\\Program Files\\pl\\bin\\plcon";
		//	else cmd = "plcon";
		//}
		
		// Getting prolog exe path. Aborting if not set.
		cmd = Config.getProperty("prolog_path");
		System.out.println(cmd);
		if ((cmd == null) || (cmd.equals(""))) {
			_mainOutput.writeLn("Can't find prolog_path property in configuration file. Aborting.");
			throw new PropertyNotSetException("prolog_path");
		}
		
		// generate Prolog file
		extract(root, false);
		
		// Call SWI-Prolog

		// create command for execution
		String command = cmd + " -f " + System.getProperty("java.io.tmpdir")+File.separator+"myUML.pl" + " -t output_depth";
		err = "";
		
			// OS call
			try {
				final Process p = Runtime.getRuntime().exec(command);
				Thread destroyProcess = new Thread() {
					public void run() {
						try {
							sleep(timeout);
							} catch (InterruptedException ie) {
								err = ("Failure: " + ie);
							} finally {
								p.destroy();
								err = "\nTimeout of "+timeout/1000+" seconds reached.\nNo answer concerning the security could been derived.\n";
								}
						}
				};
				destroyProcess.start();
				
				// read output of SWI Prolog
				if (err.equals("")) {
					BufferedReader lsOut = new BufferedReader(new InputStreamReader (p.getInputStream()));
					BufferedReader lsErr = new BufferedReader(new InputStreamReader (p.getErrorStream()));
					while ( (line = lsOut.readLine()) != null){
						output.addElement(line);			
					}
					while ( (line = lsErr.readLine()) != null ){
						errors.addElement(line);			
					}
				}
			} catch (Exception e) {
				log.appendLogLn(e.getMessage());
			}

			// prepare results for output
			for ( int j = 0; j < output.size(); j++) out += output.elementAt(j) + "\n";
			if (err=="") {
				if (out.trim()=="") out = "\nProtocol is secure. No attack possibilities found.\n";
				else out = "\nProtocol is insecure. Following attack possibility was found:\n" + out;
			}
			else out = err;
						
			// output results
			log.appendLogLn(out+"\n");			
		}


	/**
	 * Starts processing loaded data and creating of Prolog code.
	 */	
	public void extract(){
		extract(root, true);
	}


	/**
	 * Method to initialize the processing of all UML data:<br>
	 *  - extracts StateMachinepackage from given UML model and starts the method to process them,<br>
	 *  - extracts tagged values of model with attackers initial knowledge and secret descriptions,<br>
	 *  - generates first message of the protocol from attackers initial knowledge,<br>
	 *  - defines the attackers goal and the search range in Prolog.<br>
	 * @param umlpackage UML model to extract.
	 * @param display Indicates if the created Prolog code schould be displayed. 
	 */
	private void extract(UmlPackage umlpackage, boolean display){
		if(umlpackage!=null){

			// extract tagged value "initial knowledge"
			boolean attackersknowledgefound=false;
			for(Iterator it_tagv=umlpackage.getCore().getTaggedValue().refAllOfClass().iterator();it_tagv.hasNext();){
				TaggedValue tagv=(TaggedValue)it_tagv.next();
				String t_Name=tagv.getType().getName();
				if (t_Name.equalsIgnoreCase("initial knowledge")) {
					for(Iterator it_v=tagv.getDataValue().iterator();it_v.hasNext();){
						String value=(String)it_v.next();
						exp.appendln("% Function msg(Term, Cond, Step, Who)");
						exp.newline();
						exp.appendln("msg(Term, Cond, Step, 'Nobody') :-");
						exp.pushInd();
						exp.appendln("Step = 0,");
						exp.appendln("Cond = [],");
						exp.appendln("Term = "+concmodif2(concmodif(value)).replaceAll(" ","").replaceAll("symenc","enc").replaceAll("symsign","sign")+".");
						exp.newline();
						exp.popInd();
						break;
					}
					attackersknowledgefound=true;
					break;
				}
			}
			if(!attackersknowledgefound) {
				log.appendLogLn("Error: No tagged value \"initial knowledge\" found. The creation couldn't be completed.\n");
				}
				
			// start processing the package of StateMachienes
			extract(umlpackage.getStateMachines());
			boolean secretfound=false;
			for(Iterator it_tagv=umlpackage.getCore().getTaggedValue().refAllOfClass().iterator();it_tagv.hasNext();){
				TaggedValue tagv=(TaggedValue)it_tagv.next();
				String t_Name=tagv.getType().getName();
				if (t_Name.equalsIgnoreCase("secret")) {
					for(Iterator it_v=tagv.getDataValue().iterator();it_v.hasNext();){
						String value=(String)it_v.next();
						exp.newline();
						exp.appendln("%------------------------ Attackers goal -------------------------");
						exp.newline();
						exp.appendln("goal :- knows("+concmodif2(concmodif(value)).replaceAll(" ","").replaceAll("symenc","enc").replaceAll("symsign","sign")+","+trNr+").");
						exp.newline();
						exp.appendln("%------------------------ Range settings  -------------------------");
						exp.newline();
						exp.appendln("range(0,"+trNr+").");
						exp.newline();
						break;
					}
					secretfound=true;
					break; }
			}
			if(!secretfound) {
				log.appendLogLn("Error: No tagged value \"secret\" found. The creation couldn't be completed.\n");
			}
		} else {
		log.appendLogLn("Error: No model found.\n");
		}
		
		// output the generated Prolog code and save it 
		buildText();
		if (display) output.write(text.toString());
		export(System.getProperty("java.io.tmpdir")+File.separator+"myUML.pl");
	}


	/**
	 * Method to process StateMachine Packages:<br>
	 *  - extracts all StateMachines from the Package and call extract(StateMachine) for each of them,<br>
	 *  - generate lists of symmetric and asymmetric keys and add them to Prolog code.<br>
	 * @param stateMachinesPackage Package of StateMachines to process.
	 */
	private void extract(StateMachinesPackage stateMachinesPackage){
		StateMachineClass stateMachineClasses;
		StateMachine stateMachine;
		if(stateMachinesPackage!=null){
			stateMachineClasses = stateMachinesPackage.getStateMachine();
			Iterator it_stm_I = stateMachineClasses.refAllOfClass().iterator();
			
			// process a first StateMachine 			
			if(it_stm_I.hasNext()){
				stateMachine = (StateMachine)it_stm_I.next();
				transname = stateMachine.getContext().getName();
				exp.appendln("% StateMachine of "+transname);
				exp.appendln("% "+stateMachine.getContext().getName()+"--> Attacker");
				firsttrans = true;
				trnr = 0;
				extract(stateMachine);
			}
			
			// process other StateMachines
			while(it_stm_I.hasNext()) {
				stateMachine = (StateMachine)it_stm_I.next();
				transname = stateMachine.getContext().getName();
			    exp.appendln("% StateMachine of "+transname);
				exp.appendln("% "+stateMachine.getContext().getName()+"--> Attacker");
				firsttrans = true;
				trnr = 0;
				extract(stateMachine);			
			}
			
			// add all symmetric keys to Prolog code
			for(int i=0; i<symkeys.size(); i++) {
				exp.appendln("symkey("+(String)symkeys.elementAt(i)+").");
				for(int j=0; j<keys.size(); j++)
					if(((String)keys.elementAt(j)).equals((String)symkeys.elementAt(i))) { keys.removeElementAt(j); break; }
			}
			
			// add all asymmetric keys to Prolog code
			for(int i=0; i<keys.size(); i++) {
				exp.appendln("key("+(String)keys.elementAt(i)+").");
			}
			exp.newline();
		} else {
			log.appendLogLn("there are no StateMachine");	
		}
	}
	
	
	/**
	 * Method to process a StateMachine:<br>
	 *  - gets a transition that begins at the initial state and call the processing method for them.<br>
	 * @param stateMachine StateMachine to process.
	 */
	private void extract(StateMachine stateMachine){
		Transition transition;
		
		// StateMachine isn't empty and has transitions
		if(stateMachine != null && !stateMachine.getTransitions().isEmpty()){
			if(!var.isEmpty()) var.removeAllElements();
			Iterator it_trans = stateMachine.getTransitions().iterator();
			while( it_trans.hasNext()) {
				transition =(Transition)it_trans.next();
				if(transition.getSource() instanceof Pseudostate){

					// begin with initial State
					if( ((Pseudostate)transition.getSource()).getKind().toString() == "pk_initial"){						
						extract(transition);
						break;
					}
				}
			}
			exp.newline();
		}else{
			log.appendLogLn("sth wrong at StateMachine\n");
		}
	}
	
	
	/**
	 * Method to process a transition:<br>
	 *  - extracts event, guard and action of the transition,<br>
	 *  - generates Prolog code for each transition,<br>
	 *  - initiates processing of the next transition.<br>
	 * @param transition Transition to process.
	 */
	private void extract(Transition transition){
		if(transition != null){
			moveVars();
			
			// trans has an event, a guard or an action
			if(hasEvent(transition) || hasGuard(transition) || hasAction(transition)){

				// begin to generate a transition's block of the Prolog code
				trNr++; trnr++;
				String transn = transname.replaceAll(" ","");
				if (transn.length()>6) transn=transn.substring(0,6);
				exp.appendln("");
				exp.appendln("% Transition: ");
				extract(transition.getSource());
				exp.append(" ---> ");
				extract(transition.getTarget());
				exp.appendln("msg(Term, Cond, Step, '"+transn+"') :-");
				exp.pushInd();
				exp.appendln("StepMin1 is Step - 1,");
					
				// generate code that includes stateinformation
				if(firsttrans) { exp.appendln("not(state("+transn.toLowerCase()+",_,StepMin1)),"); firsttrans = false; } 
				else exp.appendln("state("+transn.toLowerCase()+","+(trnr-1)+",StepMin1),");
					
				// initiate processing of the transition's trigger and guard
				extract(transition.getTrigger(),transition.getGuard());
					
				// initiate processing of the transition's effect
				extract(transition.getEffect());
				loadVars();
				transitionOut();
				exp.appendln("change_state("+transn.toLowerCase()+","+trnr+",Step).");
				exp.popInd();
				if(transition.getTarget()!=null){

					// check no circle!!! there is always an OutgoingElement, but its iterator may be empty!!
					if(StateRegister.get(transition.getTarget())== null && transition.getTarget().getOutgoing()!=null){
						
						// regist target to pervent circle
						StateRegister.put(transition.getTarget(), new Integer(1));
						Iterator it_trans=transition.getTarget().getOutgoing().iterator();
						
						// exract all outgoing
						if(it_trans.hasNext()){
							extract((Transition)it_trans.next());
							while(it_trans.hasNext()){
								extract((Transition)it_trans.next());
							}
						}
					}else{	//circle do nothing					
				}
				}else{
					exp.appendln("target null");
				}
			}
			else if(transition.getTarget()!=null){ //trans has no action

				// no circle!!!there is always OutgoingElement, but its iterator may be empty!!
				if(StateRegister.get(transition.getTarget())== null && transition.getTarget().getOutgoing()!=null){
					
					// regist target to prevent circle
					StateRegister.put(transition.getTarget(), new Integer(1));
					Iterator it_trans=transition.getTarget().getOutgoing().iterator();
					if(it_trans.hasNext()){
						extract((Transition)it_trans.next());
						while(it_trans.hasNext()){
							extract((Transition)it_trans.next());
						}
					}
				} else{	//circle do nothing
				}
			}
		}else{
			exp.appendln("sth wrong at Transition\n");
		}
	}
	

	/**
	 * Method to process events and guards:<br>
	 *  - generates Prolog code for an events and a guard.<br>
	 * @param event Event to process.
	 * @param guard Guard to process.
	 */
	private void extract(Event event, Guard guard){
		Parameter param;
		Vector args = new Vector();
		String cond = "[]";
		String guardExp="";
		String tmp="";

		// event exists => create a vector args with parameters
		if(event != null) {
			Iterator it_params = event.getParameter().iterator();
			while(it_params.hasNext()) {
				param = (Parameter)it_params.next();
				args.addElement(param.getName().replaceAll(" ",""));
				
				// generate cond-string, e.g. "cond=[Argp_1_1,Argp_1_2]"
				if (!cond.equals("[]")) cond = "["+cond+","+param.getName().replaceAll(" ","")+"]";
				else cond = param.getName().replaceAll(" ","");
			}
		}
		
		// guard exists => generate prolog expressions for this guard
		if(guard != null){ 

			// separate conjunction, assume it's in form [condition1 & condition2 & ...]
			guardExp = guard.getExpression().getBody();
			Termdepth.clear();
			String Exps[]=guardExp.split("&");
			for(int i=0;i<Exps.length;i++){
				tmp=""+(i+1);
				String cName = "Cond"+tmp;
				String expr = concmodif2(concmodif(getContentFromBracket((String)Exps[i]).trim()));
				boolean flag = false;
				
				// eliminate second part of the guard
				for(int j=0; j<args.size(); j++)
					if (expr.endsWith((String)args.elementAt(j))) {
						expr = expr.substring(0, expr.length()-((String)args.elementAt(j)).length()).trim();
						if(expr.endsWith(",")) expr = expr.substring(0, expr.length()-1).trim();
						flag = true;
						getVars((String)args.elementAt(j));
						cName = (String)args.elementAt(j);
						args.removeElementAt(j);
						break;
						}
				if (!flag) expr = "["+expr+"]";

				// get keys from the expression
				getkeys(expr); getsymkeys(expr);
				
				// add definitions of symmetric keys to the prolog code
				if (!symkey_tmp.isEmpty())
					for(int j=0; j<symkey_tmp.size(); j++) {
						transOutput.insertElementAt("symkey("+(String)symkey_tmp.elementAt(j)+"),", 0);
						}
				
				// add prepared expression to the output vector 
				transOutput.addElement(cName+" = "+expr.replaceAll(" ","").replaceAll("symenc","enc").replaceAll("symsign","sign")+",");
				String Depth = ""+termdepth(expr); 
				transOutput.addElement("knows("+cName+","+Depth+",StepMin1),");
				Termdepth.put(cName,Depth);
				getVars(expr);

			}
			
			// Cond0 initialisation. Cond0 includes all variables that were not defined in the guard 
			if(!args.isEmpty()) {
				String cond0 = (String)args.elementAt(0);
				getVars(cond0);
				if(args.size()>1)
					for(int i=1; i<args.size(); i++) {
						getVars((String)args.elementAt(i));
						cond0 = "["+cond0+","+(String)args.elementAt(i)+"]";
					}
				transOutput.addElement("Cond0 = "+cond0.replaceAll("symenc","enc").replaceAll("symsign","sign")+",");
				String Depth = ""+termdepth(cond0); 
				transOutput.addElement("knows(Cond0,"+Depth+",StepMin1),");
				Termdepth.put(cond0,Depth);
			}
		}
		
		// add cond string to the output vector
		transOutput.addElement("Cond = "+cond+",");
	}


	/**
	 * Method to process effects:<br>
	 *  - generates Prolog code for an effect,<br>
	 *  - completes the msg() predicate with belege(var,Var) for each variable Var.<br>
	 * @param effect Effect to process.
	 */
	private void extract(Action effect){
		String effectExp="";
		String param;
		if(effect != null){
			effectExp=effect.getScript().getBody();
			if(getContentFromBracket(effectExp)!=null){
				effectExp=getContentFromBracket(effectExp).trim();

				// if it's well-formed get all 1 level arguments
				if(Expression.IsBracketesOK(effectExp)){					
					Iterator it_Args= Expression.sepArgs(",", effectExp).iterator();
					if(it_Args.hasNext()){
						String arg=(String)it_Args.next();
						String arg1=concmodif2(concmodif(arg));
						getVars(arg1);

						// generate keys for each kgen() in argument
						int index = arg1.indexOf("kgen(");
						if(index>=0) {
							String key = arg1.substring(index+5,arg1.indexOf(")",index)).toLowerCase().trim()+"_key";
							symkeys.addElement(key);
							arg1=arg1.substring(0,index)+key+arg1.substring(arg1.indexOf(")")+1);
						}

						// get keys from the argument
						getkeys(arg1); getsymkeys(arg1);
						
						// add definitions of symmetric keys to the prolog code
						if (!symkey_tmp.isEmpty())
							for(int j=0; j<symkey_tmp.size(); j++) {
								transOutput.insertElementAt("symkey("+(String)symkey_tmp.elementAt(j)+"),", 0);
								}
						transOutput.addElement("Term = "+arg1.replaceAll(" ","").replaceAll("symenc","enc").replaceAll("symsign","sign")+",");
					}
				} else{
					exp.appendln("%Bracketes not match:");
					log.appendLogLn("Error: Bracketes not match:");
					exp.appendln(effectExp);
					log.appendLogLn(effectExp);
				}
			}				
		} else{
			
			// if there is no effect, set Term to "[]"
			transOutput.addElement("Term = [],");
		}
		
		// filter new variales to set
		Vector temp = new Vector();
		temp = (Vector)varloc.clone();
		for(int i=0; i<var.size(); i++) {
			for(int j=0; j<temp.size(); j++)
				if(((String)temp.elementAt(j)).equals((String)var.elementAt(i))) {
					temp.removeElementAt(j);
					i--;
					break;
					}
		}
		
		// set new variables
		if (!temp.isEmpty()) {
			for(int i=0; i<temp.size(); i++) {
				transOutput.addElement("belege("+((String)temp.elementAt(i)).toLowerCase()+","+(String)temp.elementAt(i)+"),");
			}
		}
	}
	

	/**
	 * Method to extract a pseudostate.
	 * @param pseudo The pseudostate to process.
	 */
	private void extract(Pseudostate pseudo){
		if(pseudo != null){
			exp.append("Pseudo("+pseudo.getKind()+")");
		} 
	}

	
	/**
	 * Method to extract a simple state.
	 * @param simple The simple state to process.
	 */
	private void extract(SimpleState simple){
		if(simple != null){
			if(simple.getName()!=null){ //express name:do/Activity
				if(simple.getDoActivity()!=null){
					exp.append(simple.getName()+":");
					exp.append("do/"+simple.getDoActivity().getScript().getBody());
				}else{
					exp.append(simple.getName());
				}
			}
		}	
	}
	
	
	/**
	 * Method to extract a final state.
	 * @param finalstate The final state to process.
	 */
	private void extract(FinalState finalstate){
		if(finalstate != null){
			exp.append("Final");
		}
	}

	
	/**
	 * Method to extract a composite state.
	 * @param compo The composite state to process.
	 */
	private void extract(CompositeState compo) {
		StateVertex sv = null;
		if (compo != null) {
			if (compo.getName() != null) {
				exp.append("Compo:" + compo.getName() + "(");
			} else {
				exp.append("Compo(");
			}
			Iterator it_states = compo.getSubvertex().iterator();
			if (it_states.hasNext()) {
				sv = (StateVertex) it_states.next();
				extract(sv);
			}
			while (it_states.hasNext()) {
				sv = (StateVertex) it_states.next();
				exp.append("; ");
				extract(sv);
			}
			exp.append(")");
		}
	}


	/**
	 * Method to extract a statevertex.
	 * @param sv The statevertex to process.
	 */
	private void extract(StateVertex sv) {
		if (sv != null) {
			if (sv instanceof CompositeState) {
				extract((CompositeState) sv);
			} else if (sv instanceof Pseudostate) {
				extract((Pseudostate) sv);
			} else if (sv instanceof SimpleState) {
				extract((SimpleState) sv);
			} else if (sv instanceof FinalState) {
				extract((FinalState) sv);
			} else {
				exp.append("state_else");
			}
		} else {
			return;
		}
	}

	
	/**
	 * Indicates if the given transition has an event.
	 * @param trans Transition to check for existence of events.
	 * @return True if the transition trans has an event.
	 */
	private boolean hasEvent(Transition trans){
		
		if(trans.getTrigger()!=null){
			if (!trans.getTrigger().getParameter().isEmpty()){
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * Indicates if the given transition has a guard.
	 * @param trans Transition to check for existence of guards.
	 * @return True if the transition trans has a guard.
	 */
	private boolean hasGuard(Transition trans){
			
		if(trans.getGuard()!=null){
			if (trans.getGuard().getExpression().getBody()!= ""){
				return true;
			}
		}
		return false;
	}	


	/**
	 * Indicates if the given transition has an effect.
	 * @param trans Transition to check for existence of effects.
	 * @return True if the transition trans has an effect.
	 */
	private boolean hasAction(Transition trans){
			
		if(trans.getEffect()!=null){
			if (trans.getEffect().getScript().getBody()!= ""){
				return true;
			}
		}
		return false;
	}
	

	/**
	 * Extracts content from brakets.
	 * @param str String with brackets.
	 * @return String without brackets.
	 */	
	private String getContentFromBracket(String str){		
		if(str.lastIndexOf(")")-str.indexOf("(")>0  ){
			return str.substring(str.indexOf("(")+1,str.lastIndexOf(")"));
		}else {
			return null;
		}
	}


	/**
	 * Method to save generated Prolog code in a file.
	 * @param ExportFileName The name of the file to create.
	 */
	private void export(String ExportFileName){
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(ExportFileName));
			Expression geneInfo = new Expression();
			geneInfo.appendln("%----------------------------------------------------------------");
			geneInfo.appendln("%      ----Generated from UMLsec Tool Statechart2Prolog----      ");
			geneInfo.appendln("%----------------------------------------------------------------");
			geneInfo.appendln("");
			geneInfo.newline();
			out.write(geneInfo.toString());
			out.write(text.toString());
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Outputs a string line-by-line.
	 * @param str String to process.
	 * @param _mainOutput Output destination.
	 */
	void displayString( String str, ITextOutput _mainOutput ){
		String out = "";
		for (int i = 0; i < str.length(); i ++) {
			if (str.charAt(i) != '\n' )
				out += str.charAt(i);	
			else {
				_mainOutput.writeLn(out);
				out = "";
			}
	 	}

		 // if the last line ends not with "\n", output them
		 if (out != "")
			_mainOutput.writeLn(out);
	}


	/**
	 * Builds text from header, exp and footer.
	 */
	private void buildText(){
		text.append(header);		
		text.append(exp);
		text.append(footer);
		if(!text.IsBracketesOK()){
			text.newline();
			text.appendln("%This file has Bracket not be matched!");
		}	
	}


	/**
	 * Moves content of transOutput to exp.
	 */
	private void transitionOut() {
		for(int i=0; i<transOutput.size(); i++)
			exp.checkedAppendln((String)transOutput.elementAt(i));
		transOutput.removeAllElements();
	}
	
	
	/**
	 * Method to replace all "conc(a,b,...)" with "[[a,b],...]".
	 * @param term String to be changed.
	 * @return Changed string.
	 */
	private String concmodif(String term) {
		int concindex=term.indexOf("conc(");
		if(concindex == -1) return term;
		else {
			
			// build the header part of the result and call the same method for the ending part
			String vorconc="";
			if(concindex>0) vorconc = term.substring(0,concindex);
			String result=vorconc+"["+concmodif(term.substring(concindex+5));
			
			// search the 1st level arguments of conc()
			int bracketcount = 1;
			int openedbracketindex = -1, closedbracketindex = -1;
			while(bracketcount>0) {
				openedbracketindex = result.indexOf("(",concindex+1);
				closedbracketindex = result.indexOf(")",concindex+1);
				if(openedbracketindex<closedbracketindex & openedbracketindex>0){
					bracketcount++;
					concindex = openedbracketindex;
					}
				else {
					bracketcount--;
					concindex = closedbracketindex;
					}
			}
			
			// build the ending part of the result
			result = result.substring(0,concindex)+"]"+result.substring(concindex+1);
			return result;
		}
	}
	
	
	/**
	 * Method to replace all "a::b" with "[a,b]".
	 * @param term String to be changed.
	 * @return Changed string.
	 */
 	private String concmodif2(String term) {
		String result=term;
		int concindex=result.indexOf("::");
		if(concindex==0) return concmodif2(result.substring(2));
 		while (concindex != -1) {
 			
 			// search for the index of the last term in the front of "::"
			int index=lastWordIndex(result.substring(0,concindex));
			String vorconc = result.substring(0,index);
			String midconc = result.substring(index,concindex);
			String postconc = result.substring(concindex+2);
			result=vorconc+"["+midconc+","+postconc;

			// search for the index of the last character of the next term after "::"
			int endconc=firstWordIndex(postconc);
			result = result.substring(0,concindex+endconc+3)+"]"+result.substring(concindex+endconc+3);
			concindex=result.indexOf("::");
		}
		return result;
	}


	/**
	 * Searches for the index of the last term in the string.
	 * @param st String to process.
	 * @return Index of the last term in the string st.
	 */
	private int lastWordIndex(String st) {
		int bracketcount=0;
		for (int i=st.length()-1; i>0; i--) {
			char tmp=st.charAt(i);
			boolean found=false;
			switch (tmp) {
				case '[': bracketcount++; found=true; break;
				case '(':
					bracketcount++; found=true;
					if(bracketcount==0) {
						while(i>0 && st.charAt(i-1)==' ') i--;
						if(st.substring(0,i).endsWith("symenc"))
							i-=7;
						else if (st.substring(0,i).endsWith("symsign"))
							i-=8;
						else if(st.substring(0,i).endsWith("enc") || st.substring(0,i).endsWith("inv"))
							i-=4;
						else if(st.substring(0,i).endsWith("hash") || st.substring(0,i).endsWith("sign") || st.substring(0,i).endsWith("kgen"))
							i-=5;
					}
					break;
				case ']':
				case ')': bracketcount--; found=true; break;
				case ':':
				case ',':
				case ';':
				case '.':
				case '{':
				case '}': found=true; break;
			}
			if (found && bracketcount>=0) return i+1;
		}
		return 0;
	}
	
	
	/**
	 * Searches for the index of the last character of the first term in the given string.
	 * @param st String to process.
	 * @return Index of the last character of the first term in the string st.
	 */
	private int firstWordIndex(String st) {
		int bracketcount=0;
		for (int i=0; i<st.length()-1; i++) {
			char tmp=st.charAt(i);
			boolean found=false;
			switch (tmp) {
				case '[':
				case '(': bracketcount++; found=true; break;
				case ']':
				case ')': bracketcount--; if(bracketcount==0) i++; found=true; break;
				case ':':
				case ',':
				case ';':
				case '.':
				case '{':
				case '}': found=true; break;
			}
			if (found && bracketcount<=0) return i-1;
		}					
		return st.length()-1;
	}
	
	
	/**
	 * Calculates the depth of a term.
	 * @param term The term to calculate.
	 * @return The depth of the given term.
	 */
	private int termdepth(String term) {
		String workterm = term.trim();
		
		// termdepth([a,b]) = termdepth(a) + termdepth(b)
		if(workterm.startsWith("[") && workterm.endsWith("]")) {
			int bracketcount=0;
			for(int i=1; i<workterm.length()-1; i++) {
				char tmp=workterm.charAt(i);
				switch (tmp) {
					case '(':
					case '[': bracketcount++; break;
					case ')':
					case ']': bracketcount--; break;
					case ',': if (bracketcount==0)
									return termdepth(workterm.substring(1,i))+termdepth(workterm.substring(i+1,workterm.length()-1));
									break;
				}
			}
		}
		
		// termdepth(symenc(a,b)) = termdepth(a) + termdepth(b)
		else if(workterm.startsWith("symenc(") && workterm.endsWith(")")) {
			int bracketcount=0;
			for(int i=7; i<workterm.length()-1; i++) {
				char tmp=workterm.charAt(i);
				switch (tmp) {
					case '(':
					case '[': bracketcount++; break;
					case ')':
					case ']': bracketcount--; break;
					case ',': if (bracketcount==0)
								return termdepth(workterm.substring(7,i))+termdepth(workterm.substring(i+1,workterm.length()-1));
								break;
				}
			}
		}
		
		// termdepth(symsign(a,b)) = termdepth(a) + termdepth(b)
		else if(workterm.startsWith("symsign(") && workterm.endsWith(")")) {
			int bracketcount=0;
			for(int i=8; i<workterm.length()-1; i++) {
				char tmp=workterm.charAt(i);
				switch (tmp) {
					case '(':
					case '[': bracketcount++; break;
					case ')':
					case ']': bracketcount--; break;
					case ',': if (bracketcount==0)
								return termdepth(workterm.substring(8,i))+termdepth(workterm.substring(i+1,workterm.length()-1));
								break;
				}
			}
		}

		// termdepth(enc(a,b)) = termdepth(a) + termdepth(b)
		else if(workterm.startsWith("enc(") && workterm.endsWith(")")) {
			int bracketcount=0;
			for(int i=4; i<workterm.length()-1; i++) {
				char tmp=workterm.charAt(i);
				switch (tmp) {
					case '(':
					case '[': bracketcount++; break;
					case ')':
					case ']': bracketcount--; break;
					case ',': if (bracketcount==0)
								return termdepth(workterm.substring(4,i))+termdepth(workterm.substring(i+1,workterm.length()-1));
								break;
				}
			}
		}		
		
		// termdepth(sign(a,b)) = termdepth(a) + termdepth(b)
		else if(workterm.startsWith("sign(") && workterm.endsWith(")")) {
			int bracketcount=0;
			for(int i=5; i<workterm.length(); i++) {
				char tmp=workterm.charAt(i);
				switch (tmp) {
					case '(':
					case '[': bracketcount++; break;
					case ')':
					case ']': bracketcount--; break;
					case ',': if (bracketcount==0)
								return termdepth(workterm.substring(5,i))+termdepth(workterm.substring(i+1,workterm.length()-1));
								break;
				}
			}
		}	
		
		// termdepth(inv(a)) = termdepth(a)
		else if(workterm.startsWith("inv(") && workterm.endsWith(")"))
			return termdepth(workterm.substring(4,workterm.length()-1));			

		// termdepth(hash(a)) = termdepth(a)+1
		else if(workterm.startsWith("hash(") && workterm.endsWith(")"))
			return termdepth(workterm.substring(5,workterm.length()-1))+1;
		
		// if the term is a bound variable look at the hashtable
		else if(Termdepth.get(workterm)!=null) return Integer.parseInt((String)Termdepth.get(workterm));
		else return 1;
		return 0;
	}


	/**
	 * Searches for any asymmetric keys in the string.
	 * @param term String with keys.
	 */	
	private void getkeys(String term) {
		String workterm = term+".";
		
		// a key must begin with "k_"
		int index = workterm.indexOf("k_");
		if(index != -1) {
			if(index==0 || workterm.charAt(index-1)=='[' || workterm.charAt(index-1)=='(' || workterm.charAt(index-1)==' ' || workterm.charAt(index-1)==',') {
				for(int i=index+2; i<workterm.length(); i++) {
								char tmp=workterm.charAt(i);
								switch (tmp) {
									case ')':
									case ']':
									case ' ':
									case '.':
									case ',': addkey(workterm.substring(index,i)); getkeys(term.substring(index+2,i)); getkeys(term.substring(i)); return;
								}
				}
			}
		}	
	}


	/**
	 * Searches for any symmetric keys in the string.
	 * @param term String with keys.
	 */
	private void getsymkeys(String term) {
		String workterm = term+".";
		int index, shift;
		
		// search for symenc or symsign. There must be always someone symmetric key
		int index1 = workterm.indexOf("symenc");
		int index2 = workterm.indexOf("symsign");
		if(index1<index2 && index1!=(-1)) { index = index1; shift = 6; } 
		else if (index2!=(-1)) { index = index2; shift = 7; }
		else return;
		int bracket=0;
		symkey_tmp.removeAllElements();
		for(int i=index+shift; i<workterm.length(); i++) {
			char tmp=workterm.charAt(i);
			switch (tmp) {
				case '(':
				case '[': bracket++; break;
				case ')':
				case ']': bracket--; break;
				case ',': if (bracket==1) { addsymkey(workterm.substring(i+1,workterm.indexOf(')',i)).trim()); getsymkeys(term.substring(index+shift,i)); getsymkeys(term.substring(i)); return; }
			}
		}
	}
	

	/**
	 * Adds an asymmetric key to the vector keys.
	 * @param key The key to add.
	 */
	private void addkey(String key) {
		boolean exist=false;
		for(int i=0; i<keys.size(); i++)
			if(((String)keys.elementAt(i)).equals(key)) exist=true;
		if(!exist) keys.addElement(key);
	}
	
	
	/**
	 * Adds a symmetric key to the vector symkeys.
	 * @param key The key to add.
	 */
	private void addsymkey(String key) {
		if (key.charAt(0)==key.toLowerCase().charAt(0)) {
			boolean exist=false;
			for(int i=0; i<symkeys.size(); i++)
				if(((String)symkeys.elementAt(i)).equals(key)) exist=true;
			if(!exist) symkeys.addElement(key);
		}
		else {
			boolean exist=false;
			for(int i=0; i<symkey_tmp.size(); i++)
				if(((String)symkey_tmp.elementAt(i)).equals(key)) exist=true;
			if(!exist) symkey_tmp.addElement(key);
		}
	}
	
	
	/**
	 * Searches for variables in the string.
	 * @param term String to process .
	 */
	private void getVars(String term) {
		String workterm = term+".";
		for(int index=0; index<workterm.length(); index++) {
			char a = workterm.charAt(index);

			// every character in upper case is begin of a variable
			// lets search for the end of this variable
			if(a>='A' && a<='Z') {
				if(index==0 || workterm.charAt(index-1)=='[' || workterm.charAt(index-1)=='(' || workterm.charAt(index-1)==' ' || workterm.charAt(index-1)==',') {
					for(int i=index+2; i<workterm.length(); i++) {
									boolean found=false;
									char tmp=workterm.charAt(i);
									switch (tmp) {
										case ')':
										case ']':
										case ' ':
										case '.':
										case ',': addVar(workterm.substring(index,i)); found=true; break;
									}
									if(found) {index=i; break;} 
					}
				}
			}	
		}
	}
	
	
	/**
	 * Adds a variable to the vector varloc.
	 * @param variable Variable to add.
	 */
	private void addVar(String variable) {
		boolean exist=false;
		if(!varloc.isEmpty())
			for(int i=0; i<varloc.size(); i++)
				if(((String)varloc.elementAt(i)).equals(variable)) exist=true;
		if(!exist) varloc.addElement(variable);
	}


	/**
	 * Moves all variables from the vector varloc to the vector var.
	 */
	private void moveVars() {
		if(varloc.isEmpty()) return;
		if(var.isEmpty()) { var.addAll(varloc); varloc.removeAllElements(); return; }			
		for(int i=0; i<varloc.size(); i++) {
			boolean exist=false;
			for(int j=0; j<var.size(); j++)
				if(((String)varloc.elementAt(i)).equals((String)var.elementAt(j))) exist=true;
			
			// only a new variable will be added
			if(!exist) var.addElement(varloc.elementAt(i));
		}
		varloc.removeAllElements();
	}


	/**
	 * Generates folowig Prolog string: var(variable,Variable).
	 * This makes the Prolog program load the variable to use it in the message.
	 */
	private void loadVars() {
		if(varloc.isEmpty() || var.isEmpty()) return;			
		for(int i=0; i<varloc.size(); i++) {
			boolean exist=false;
			for(int j=0; j<var.size(); j++)
				if(((String)varloc.elementAt(i)).equals((String)var.elementAt(j))) exist=true;
			if(exist) exp.checkedAppendln("var("+((String)varloc.elementAt(i)).toLowerCase()+","+(String)varloc.elementAt(i)+"),");
		}
	}

}