
package tum.umlsec.viki.tools.sequence2prolog;

/**
 * @author gurvanov
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Vector;

import org.omg.uml.UmlPackage;
import org.omg.uml.behavioralelements.commonbehavior.CommonBehaviorPackage;
import org.omg.uml.behavioralelements.commonbehavior.Stimulus;
import org.omg.uml.behavioralelements.commonbehavior.StimulusClass;
import org.omg.uml.foundation.core.CorePackage;
import org.omg.uml.foundation.core.TagDefinition;
import org.omg.uml.foundation.core.TaggedValue;
import org.omg.uml.foundation.core.TaggedValueClass;

import tum.umlsec.viki.framework.ILogOutput;
import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.mdr.IMdrContainer;



public class Sequence2Prolog {
	
	public UmlPackage root;
	public ITextOutput output;
	public ILogOutput log;
	public CorePackage corePackage;
	public static TaggedValueClass taVaCl;			// used in method getTaggedValues
	public static String initialKnowledgeStr;		// in this String is saved the initial knowledge of the Adversary
	public static boolean initialKnowledgeFound;	// this variable shows if the Adversar has initial knowledge
	public Vector keys;								// shared for all messages vector, that contains the distinct keys found in them
	public Vector symkeys;							// shared for all messages vector, that contains the distinct symkeys found in them
	public MessageParse[] messages;	
	public FileBuilder text;
	public FileBuilder exp;
	public String err = "";
	
	
	public CommonBehaviorPackage commonbehavior;
	public StimulusClass stimulusclass;
	
	public int nbrOfMessages;
	
	/**
	 * Constructor
	 */
	public Sequence2Prolog(IMdrContainer _mdrContainer, ITextOutput _textOutput, ILogOutput _logOutput){
		 
		output = _textOutput;
		log = _logOutput;
		root = _mdrContainer.getUmlPackage();
		corePackage = root.getCore();		
		taVaCl = corePackage.getTaggedValue();
		
		initialKnowledgeStr = "";
		initialKnowledgeFound= false;
		keys = new Vector();
		symkeys = new Vector();
		text = new FileBuilder();
		exp = new FileBuilder();		
		commonbehavior = root.getCommonBehavior();
		stimulusclass = commonbehavior.getStimulus();
		
		nbrOfMessages = 0;
	}
	
	
	
	
	/************************************************
	 * the initially started function from command01*
	 ************************************************/

	/**
	 * The Function that extract the messages from the MDR
	 *	and saves them in an Array
	 *	
	 */

	public void extract(boolean printOnScreen){
		
		
			
			//		Find out the number of Messages
			
			int n = 0;
			for(Iterator it_sti_I = stimulusclass.refAllOfClass().iterator(); it_sti_I.hasNext();){
				Stimulus sti = (Stimulus)it_sti_I.next();
				n++;		
			}
			
			nbrOfMessages =  n;

			// Define an array for all messages to be saved in
			//------------------------------------------------
			MessageParse[] messages = new MessageParse[n];
			
			getInitKn(); 					//saves it in String initialKnowledgeStr, if there is any,
											//otherwise: initialKnowledgeFound = false;
			
			// parseses all messages and build Array of SeqMsg
			//-------------------------------------------------
			for (Iterator it_sti_I = stimulusclass.refAllOfClass().iterator(); it_sti_I.hasNext();) {
				Stimulus sti = (Stimulus)it_sti_I.next();
				int m = Integer.parseInt(sti.getName());
				messages[m-1] = new MessageParse();									//creates the new message as MessageParse (see down the parameter)
				messages[m-1].message_no = m; 										//saves the number of messages
				messages[m-1].sender = sti.getSender().getName(); 					//saves the sender of the message
				messages[m-1].receiver = sti.getReceiver().getName().toString(); 	//saves the receiver of the message
				messages[m-1].message = 
					replaceConc(sti.getDispatchAction().getName()); 				//saves the message as it is in the sequence diagramm
				messages[m-1].functionName = messages[m-1].extractFunctionName();	//saves the message name
				getGuards(m, messages[m-1]);										//saves the guards to the current message, if there are any
																					//saves receive() in the variable messages[i].receive
																					// + receiveFound = true
																					//saves the rest(the condition) in messages[i].condition
																					// + condFound = true
				
				messages[m-1].parseReceive();										//builds a String [] with the single Argumnets  
																					//				(receive => single_receive)
				
				messages[m-1].parseCondition();										//builts from messages[i].condition => messages[i].single_condition
				
				messages[m-1].ConditionToPrSyntax();								//parses the single_condition; builds single_condition Vector
																					//				in ProLog syntax
																					//also extracts the function found in single_condition Vector
																					//and saves them to single_function Vector
																					//and removes it from single_condition
				
				messages[m-1].MessageToPrologSyn();									//this function builds the prolog "Term"
				
				
				messages[m-1].replaceFunctions();									//replaces the functions found

				Extractkeys(messages[m-1].messageInPrSyntax);									//extract keys (every key has prefix "k_")
				if(initialKnowledgeFound) Extractkeys(initialKnowledgeStr);
				
				ExtractSymkeys(messages[m-1].messageInPrSyntax);									//extract keys (every key has prefix "s_k")
				if(initialKnowledgeFound) ExtractSymkeys(initialKnowledgeStr);
				
				
				
				messages[m-1].build_all_Arguments();								//put in the vector all variables and arguments found
			}
			
			for (int i=0; i<messages.length;i++){ //we strart the function which builds the vectors with the old and the new arguments,
													//which are used to differentiate between the argument values to be saved , and
													//and argument values to be retreaved from the memory
													//WE START THE FUNCTION HERE AND NOT AT THE CYCLE OVER THIS CYCLE; BECAUSE
													//the messages there are being parsed backwords, and for us here is important that they
													//are parsed forwards so we can see which are the new arguments
				
				messages[i].built_new_old_arg();
			}
	//		 s parsvaneto e prikljucheno i sega moje da pochne da se konstruira prolog fila
			
			buildProlog(messages);													//outputs prolog code to FileBuilder text
			
			
			
			
			
			
			if(printOnScreen) output.write(text.toString());
			
			export(System.getProperty("java.io.tmpdir")+File.separator+"geneProlog.pl"); // create the .pl file 
			

	
	//output.writeLn("2: "+messages[1].single_receive_V.elementAt(0));
	//output.writeLn("3: "+messages[2].not_def_arg.elementAt(0));
	
	
	
/*	
		
		
// the part beneath is intended to be only for test purposes
		output.writeLn();
		output.writeLn("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");		
		output.writeLn("%%%%%%%%%%%%%start of the test output%%%%%%%%%%%");
		output.writeLn("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		output.writeLn();
		
		
		if(initialKnowledgeFound)
			output.writeLn("Inital Knowledge of the Adversary: " + initialKnowledgeStr);
		
		output.writeLn();
		
		
		for (int i=0; i<n; i++){
			output.writeLn();
			output.writeLn("message_no: " + messages[i].message_no );
			output.writeLn("functionName: " + messages[i].functionName );
			output.writeLn("sender: " + messages[i].sender);
			output.writeLn("receiver: " + messages[i].receiver);
			output.writeLn("message: " + messages[i].message);
			
			if (messages[i].receiveFound){
				output.writeLn("receive: " + messages[i].receive);
			}
			else{
				output.writeLn("There is no Receive() for this message");
			}
			
			output.writeLn("messageInPrSyntax: " + messages[i].messageInPrSyntax);
			
		
			if (messages[i].condFound) {
				output.writeLn("condFound: "+messages[i].condFound);
				
				output.writeLn("condition " + (i+1)+ ": " + messages[i].condition);
	
				
			}
			else output.writeLn("no condition  for guard_" + (i+1));
			
		
			if (messages[i].condFound) {
				output.writeLn("condFound: "+messages[i].condFound);
				for(int j = 0;j <messages[i].single_condition.size();j++ ){
					output.writeLn("single_condition for guard "+ (i+1) +" Nr" +j + ": " + messages[i].single_condition.elementAt(j));
	
				}
			}
			else output.writeLn("no single_condition for guard_" + (i+1));


		
			if (messages[i].receiveFound) {
			 	for(int j = 0;j <messages[i].single_receive.length;j++ ){
			 		output.writeLn("single_receive: "+ messages[i].single_receive[j]);
				}
			}
			
			if(messages[i].single_receive_V.size()>0){
				for(int j = 0;j <messages[i].single_receive_V.size();j++ ){
			 		output.writeLn("single_receive_V "+ j +" is: "+ messages[i].single_receive_V.elementAt(j));
				}
			}
			
			
			if(messages[i].single_condition_arg.size()>0){
				for(int j = 0;j <messages[i].single_condition_arg.size();j++ ){
			 		output.writeLn("single_condition_arg "+ j +" is: "+ messages[i].single_condition_arg.elementAt(j));
				}
			}
			
			if(messages[i].single_condition_value.size()>0){
				for(int j = 0;j <messages[i].single_condition_value.size();j++ ){
			 		output.writeLn("single_condition_value "+ j +" is: "+ messages[i].single_condition_value.elementAt(j));
				}
			}
			
			

			if(messages[i].all_Arguments.size()>0){
				for(int j = 0;j <messages[i].all_Arguments.size();j++ ){
			 		output.writeLn("all_Arguments: "+ messages[i].all_Arguments.elementAt(j));
				}
			}
		
			if(messages[i].not_def_arg.size()>0){
				for(int j = 0;j <messages[i].not_def_arg.size();j++ ){
			 		output.writeLn("not_def_arg: "+ messages[i].not_def_arg.elementAt(j));
				}
			}
			else
				output.writeLn("all argumets defined");
		
			
			output.writeLn();
			
		}
		
		
		if (keys.size() > 0) {
		 	for(int j = 0;j <keys.size();j++ ){
		 		output.writeLn("Key: "+ keys.elementAt(j));
			}
		}
output.writeLn();
output.writeLn("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");		
output.writeLn("%%%%%%%%%%%%%%%End of the test output%%%%%%%%%%%");
output.writeLn("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");	
output.writeLn();
		
// end of test---------------------------------
	
*/
	
			
			
	}// the end of			extract(UmlPackage _umlPackage)
		
		
		
		
		
	
	/**
	 * Funktion finds TaggedValue labeled "initial knowledge"
	 * and saves them in the variable initialKnowledge
	 */
	
	public void getInitKn(){
		Vector ve = getTaggedValue("initial knowledge");
		if (ve.size() == 0){
			initialKnowledgeFound = false;
		}
		else if (ve.size() == 1){
			initialKnowledgeFound = true;
			initialKnowledgeStr = replaceConc((String)ve.elementAt(0));
		}
		else{
			for (int i = 0; i<ve.size(); i++){
				initialKnowledgeFound = true;
				initialKnowledgeStr = concatenate(replaceConc((String)ve.elementAt(i)),initialKnowledgeStr);
			}

		}

	}// end of		getInitKn()-----------------------

	
	
	
	
	/**
	 * Funktion gets TaggedValue labeled with "guard_X"
	 * and X is the current message number
	 * "condition" - references the condition in the guard_X found in [] brackets
	 * "receive" - reference the first part of the guard_X : receive (X), as
	 * these are the variables expected to be received to continue the protocol
	 */
	public static void getGuards(int _messageNr, MessageParse _message){
		Vector ve = getTaggedValue("guard_" + _messageNr);						//gets the guard_X if there is one
		
		String _tmp = "";														//used to save temp values of the guard in
		String _cond="";
		String _receive="";
		
		boolean _receiveFound = false;											//sets initially _receiveFound to false,
																				//where as _receiveFound show if there is receive() in the guard_X
		
		int _endOfReceive = -1;													//the variable which shows where receive() ends, set initially to -1
		
		if (ve.size() == 1){													//checks if guard_X is empty or not
																			
			
			_tmp = (String)ve.elementAt(0);										//the content of guard_x is saved into _tmp
			_tmp = _tmp.replaceAll(" ","");
			
			
			_receiveFound = _tmp.startsWith("receive");							//boolean, that schows if there is receive() in the guard_X
			
			if(_receiveFound){  												//if receive is found 
				
				_endOfReceive = _tmp.indexOf("&");								//finds the next occurance of &, if it is present
				
				if (_endOfReceive == -1){ 										//in case that there is no &,
																				//from that follows that there is now condition in the guard_X
					
					_message.receiveFound = true;								//sets messages[Nr].receiveFound to true
					_message.receive= _tmp;										//in the string _tmp is only receive()
					_message.condFound = false;									//sets messages[Nr].condFound to false
					_message.condition = "";
					return;														//end
				}
				else{															//if there is &, so there is condition after receive()
					
					_message.receiveFound = true;								//sets messages[Nr].receiveFound to true
					
					_message.receive=
						_tmp.substring(0,_endOfReceive).trim();					//saves the first part of the Guard_X as messages[Nr].receive
					
					_message.condFound = true;									//sets messages[Nr].condFound to true
					
					_message.condition =
						replaceConc(_tmp.substring(_endOfReceive+1,_tmp.length()).trim());	//saves the rest after & in messages[Nr].condition
					
					_message.condition = _message.condition.trim();
					return;														//end
				}
			}
	
			else {																//if no receive found, means the whole string is the condition
				_message.condFound = true;
				_message.condition = replaceConc(_tmp.trim());								//saves _tmp in messages[Nr].condition
			return;																//end											
			
			}
		}
		else{																	//run when guard_X is empty
																				//means there is no trigger, no condition
			
			_message.condFound = false;											//sets messages[Nr].condFound to false
			_message.condition = "";											//saves empty string in messages[Nr].condition
			return;																//end
		}
		
	}				// end of		getGuards(int messageNr)-----------------------
	
	
	
	
	
	/**
	 * Funktion returns TaggedValue, labeled with "tag"
	 */
	public static Vector getTaggedValue(String tag){

		Vector ve = new Vector();
	
		// Durchlaufe alle TaggedValue
		for (Iterator it = taVaCl.refAllOfClass().iterator(); it.hasNext();) {
	
			TaggedValue va = (TaggedValue)it.next();
			TagDefinition td = va.getType();
			Iterator itDataValue = va.getDataValue().iterator();
		

			// Falls TagType dem Gesuchten entspricht, ermittle DataValue
			if ( td.getName().equals(tag))
			{
				// Falls DataValue nicht leer, f�ge es zu Vektor hinzu
				while(itDataValue.hasNext()){
					String str = (String) itDataValue.next();
					//_mainOutput.writeLn("TaggedValue: " + td.getTagType() + " - " + str);
					if (!str.equals(""))
						ve.add(str);
				}
			}
		}
		return ve;
	} // the end of			Vector getTaggedValue(IMdrContainer _mdrContainer, String tag)
	
	
	
	
	
	/**
	 * Function returns a String value composed of the concatenation of
	 * @param str1 and @param str2
	 * Example: str1 ,str2 -> [str1,str2]
	 */
	public static String concatenate(String str1, String str2){
		String sep = ",";			// The separator used
		String concStr = "";		// The string which is going to be composed
		String openBr = "[";		// Opening Bracket
		String closeBr = "]";		// Closing Bracket
		if (str1.length()>= 1 & str2.length()>=1) concStr = openBr + str1 + sep + str2 + closeBr;
		else if (str1.length()==0 & str2.length() == 0) concStr = "";
		else if (str1.length()==0) concStr = str2;
		else concStr = str1;
		return concStr;
	}
	

	/** 
	 * the function extracts the keys from the messages
	 * every key has a prefix "k_"
	 * befor "k_" or "s_key" must either be at the beginning of the string,
	 * or one of the following chars must be found there "([{)]},"
	 * else it is part of other term and no key 
	 * if 3. paramater true, it is beiing searched for key
	 * else it is beiing searched for symkey
	 */
	
	public void Extractkeys(String seq){
		
		String _message = seq;					//temporary save message
		_message = _message.replaceAll(" ","");
		String _foundKey ="";
		int _indexS=-1;							//index, at which the key is found
		int _indexE=-1;							//index, at which the found key ends
		String _endChar = "([{,]})";			//characters which may follow the key, in order to find out where it ends
		
		_indexS = _message.indexOf("k_");		//search for the prefix
		if (_message == "" | _indexS == -1) return;
		
		if(_indexS ==0){
			for(int i = _indexS +1; i<_message.length();i++){				//read next character
				
				if(_endChar.indexOf(_message.charAt(i))>=0){				//if character equal one of _endChar = "([{,]})",
																			// 							then the end is found
					
					_indexE= i;												//saves where the key ends
					
						_foundKey = _message.substring(_indexS,_indexE);	//saves the key
						if(!(keys.contains(_foundKey))) keys.addElement(_foundKey);	//adds it to the vector keys
						Extractkeys(_message.substring(_indexE + 1));									//iteation with the rest of the message
						_indexS = _message.indexOf("k_",_indexE);
						if (_indexS == -1){ 
							return;
						}
						else{
							Extractkeys(_message.substring(_indexE + 1));
							return;
						}					
					
				}			
			}
		}
		else{ //_indexS > 0
			
				for(int i = _indexS +1; i<_message.length();i++){				//read next character
					
					if(_endChar.indexOf(_message.charAt(i))>=0){				//if character equal one of _endChar = "([{,]})",
																				// 							then the end is found
						
						_indexE= i;												//saves where the key ends
						if(_endChar.indexOf(_message.charAt(_indexS-1))>=0){		//check if the character before the "k_" is a
																					//legal character("([{,]})"), else it must be
																					//part of other term f.E. Ack_1_2(not a key )
							_foundKey = _message.substring(_indexS,_indexE);
							if(!(keys.contains(_foundKey))) keys.addElement(_foundKey);	//adds it to the vector keys
							Extractkeys(_message.substring(_indexE + 1));
							_indexS = _message.indexOf("k_",_indexE);
							if (_indexS == -1){ 
								return;
							}
							else{
								Extractkeys(_message.substring(_indexE + 1));
								return;
							}
							
						}
					}				
				}
		}
		
	}// end of Extractkeys(String seq)
	
	
	
	public void ExtractSymkeys(String seq){
		
		String _message = seq;					//temporary save message
		_message = _message.replaceAll(" ","");
		String _foundKey ="";
		int _indexS=-1;							//index, at which the key is found
		int _indexE=-1;							//index, at which the found key ends
		String _endChar = "([{,]})";			//characters which may follow the key, in order to find out where it ends
		
		_indexS = _message.indexOf("sym_k");		//search for the prefix
		if (_message == "" | _indexS == -1) return;
		
		if(_indexS ==0){
			for(int i = _indexS +1; i<_message.length();i++){				//read next character
				
				if(_endChar.indexOf(_message.charAt(i))>=0){				//if character equal one of _endChar = "([{,]})",
																			// 							then the end is found
					
					_indexE= i;												//saves where the key ends
					
						_foundKey = _message.substring(_indexS,_indexE);	//saves the key
						if(!(symkeys.contains(_foundKey))) symkeys.addElement(_foundKey);	//adds it to the vector keys
						ExtractSymkeys(_message.substring(_indexE + 1));									//iteation with the rest of the message
						_indexS = _message.indexOf("sym_k",_indexE);
						if (_indexS == -1){ 
							return;
						}
						else{
							ExtractSymkeys(_message.substring(_indexE + 1));
							return;
						}					
					
				}			
			}
		}
		else{ //_indexS > 0
			
				for(int i = _indexS +1; i<_message.length();i++){				//read next character
					
					if(_endChar.indexOf(_message.charAt(i))>=0){				//if character equal one of _endChar = "([{,]})",
																				// 							then the end is found
						
						_indexE= i;												//saves where the key ends
						if(_endChar.indexOf(_message.charAt(_indexS-1))>=0){		//check if the character before the "k_" is a
																					//legal character("([{,]})"), else it must be
																					//part of other term f.E. Ack_1_2(not a key )
							_foundKey = _message.substring(_indexS,_indexE);
							if(!(symkeys.contains(_foundKey))) symkeys.addElement(_foundKey);	//adds it to the vector keys
							ExtractSymkeys(_message.substring(_indexE + 1));
							_indexS = _message.indexOf("sym_k",_indexE);
							if (_indexS == -1){ 
								return;
							}
							else{
								ExtractSymkeys(_message.substring(_indexE + 1));
								return;
							}
							
						}
					}				
				}
		}
		
	}// end of ExtractSymkeys(String seq)
	
	
	
	
	


	
	/** 
	 * the function calculates the depth of a string(Term or Condition)
	 * 
	 */
	
	private int termdepth(String term) {
		String workterm = term.trim();
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
								return termdepth(workterm.substring(4,i))+termdepth(workterm.substring(i+1,workterm.length()-1));
								break;
				}
			}
		}		
		else if(workterm.startsWith("symsign(") && workterm.endsWith(")")) {
			int bracketcount=0;
			for(int i=8; i<workterm.length(); i++) {
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
		
		else if(workterm.startsWith("inv(") && workterm.endsWith(")"))
			return termdepth(workterm.substring(4,workterm.length()-1));			
		else if(workterm.startsWith("hash(") && workterm.endsWith(")"))
			return termdepth(workterm.substring(5,workterm.length()-1))+1;
		else return 1;
		return 0;
	}// end of termdepth(String term)

	
	
	
	
	
	/**
	 * the finction builds the prolog source 
	 * and saves it in a file
	 */
	
	private void buildProlog(MessageParse[] messages){
		String resourceRoot = System.getProperty("tum.umlsec.viki.resourceRoot");
		// @bugfix buerger:
		// changed the path calculation a bit, since the Prolog-directory
		// is not inside the src-dir anymor and this kind of resource-information
		// should not be part of the source-tree.
		resourceRoot=resourceRoot.substring(0,resourceRoot.lastIndexOf("src"));
		if (System.getProperty("os.name").matches("Windows.*"))
			{resourceRoot=resourceRoot.replaceAll("%20"," ");}
		else
		{resourceRoot=resourceRoot.replaceAll("%20","\\ ");}
		String sep = ",";
		String end = ".";
		
		text.appendFromFile(resourceRoot+File.separator+"Prolog"+File.separator+"Prologheader_2.txt");			//add header
		exp.newline();
		exp.appendln("%-------------------------------------------------------------------------------------");
		exp.appendln("%----------------- Automaticlly generated Messages in Prolog -------------------------");
		exp.appendln("%-------------------------------------------------------------------------------------");
		
		if(initialKnowledgeFound){										//insert adversary's knowledge as message 0, if there is any
			
			exp.appendln("% Function msg(Term, Cond, Step)");
			exp.newline();
			exp.appendln("% Message 0, containing the initial knowledge of the Adversary");
			exp.newline();
			exp.appendln("msg(Term, Cond, Step) :-");
			exp.pushInd();
			exp.appendln("Step = 0,");
			exp.appendln("Cond = [],");
			exp.appendln("Term =" + initialKnowledgeStr + ".");
			exp.newline();
			exp.popInd();
		}
		
		for(int i =0; i < nbrOfMessages;i++){					//build prolog source code for all messages
			
			exp.appendln("%Message number " + messages[i].message_no +", Sender:" + 
					messages[i].sender + " => " + "Receiver: " + messages[i].receiver);
			exp.newline();
			exp.appendln("msg(Term, Cond, Step) :-");
			exp.pushInd();
			
			exp.appendln("Step = " + Integer.toString(messages[i].message_no));
			exp.append(sep);
			
			

			if(messages[i].condFound){ //in case that some arguments are defined with equal()
				exp.appendln("StepMin1 is Step-1,");
				if(messages[i].symKeyVar.size()>0){
					for(int j=0;j<messages[i].symKeyVar.size();j++){
						exp.appendln("symkey(");
						exp.append(messages[i].symKeyVar.elementAt(j).toString());
						exp.append("),");
					}
				}
				
				if(messages[i].old_Arg.size()>0){
					String _arg = "";
					for(int j =0;j<messages[i].old_Arg.size();j++){
						_arg = messages[i].old_Arg.elementAt(j).toString();
						exp.appendln("var(");
						exp.append(messages[i].old_Arg.elementAt(j).toString().toLowerCase());
						exp.append(sep);
						exp.append(messages[i].old_Arg.elementAt(j).toString());
						exp.append("),");
					}
				}
				
				
		
				
				for(int j =0;j<messages[i].single_condition.size();j++){
					exp.appendln(messages[i].single_condition.elementAt(j).toString());
					exp.append(sep);
					
					exp.appendln("knows(" + messages[i].single_condition_arg.elementAt(j).toString());
					exp.append(sep);
					exp.append(Integer.toString(termdepth(messages[i].single_condition_value.elementAt(j).toString())));
					exp.append(sep);
					exp.append("StepMin1)");
					exp.append(sep);
				}

				exp.appendln("Cond = ");
				String _tmp ="";
				for(int j =0; j<messages[i].single_receive_V.size();j++){
					_tmp = concatenate(_tmp,(String)messages[i].single_receive_V.elementAt(j));
				}
				exp.append(_tmp);
				exp.append(sep);

				
				
				
				if(!(messages[i].single_condition.size() == messages[i].single_receive_V.size())){
					
					
					exp.appendln("knows(Cond,");
					int _depth = messages[i].single_receive_V.size() - messages[i].single_condition.size();
					for(int j =0;j<messages[i].single_condition_value.size();j++){
						_depth += termdepth(messages[i].single_condition_value.elementAt(j).toString());
					}
					exp.append(Integer.toString(_depth));
					exp.append(",StepMin1),");
				}


				
			}
			else if(messages[i].receiveFound && !messages[i].condFound){ //in case that no arguments are defined with equal(arg,Value)
																		// but the adversary still gets the message
																		//=> knows(arg,depth,step-1)
				exp.appendln("StepMin1 is Step-1,");
				
				if(messages[i].symKeyVar.size()>0){
					for(int j=0;j<messages[i].symKeyVar.size();j++){
						exp.appendln("symkey(");
						exp.append(messages[i].symKeyVar.elementAt(j).toString());
						exp.append("),");
					}
				}
				
				
				if(messages[i].old_Arg.size()>0){
					String _arg = "";
					for(int j =0;j<messages[i].old_Arg.size();j++){
						_arg = messages[i].old_Arg.elementAt(j).toString();
						exp.appendln("var(");
						exp.append(messages[i].old_Arg.elementAt(j).toString().toLowerCase());
						exp.append(sep);
						exp.append(messages[i].old_Arg.elementAt(j).toString());
						exp.append("),");
					}
				}
				
				
				int nrOfArg=0;															
				exp.appendln("Cond = ");
				String _tmp ="";
				for(int j = messages[i].single_receive_V.size()- 1;j>=0;j--){
					_tmp = concatenate((String)messages[i].single_receive_V.elementAt(j),_tmp);
					nrOfArg ++; //the number of Arguments will be the depth, as we do not know anything about them
				}
				exp.append(_tmp);
				exp.append(sep);
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////			
				exp.appendln("knows(Cond,");
				int _depth = messages[i].single_receive_V.size() - messages[i].single_condition.size();
				for(int j =0;j<messages[i].single_condition_value.size();j++){
					_depth += termdepth(messages[i].single_condition_value.elementAt(j).toString());
				}
				exp.append(Integer.toString(_depth));
				exp.append(",StepMin1),");
				
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////					
				
				

			}
			else{
				if(messages[i].old_Arg.size()>0){
					String _arg = "";
					for(int j =0;j<messages[i].old_Arg.size();j++){
						_arg = messages[i].old_Arg.elementAt(j).toString();
						exp.appendln("var(");
						exp.append(messages[i].old_Arg.elementAt(j).toString().toLowerCase());
						exp.append(sep);
						exp.append(messages[i].old_Arg.elementAt(j).toString());
						exp.append("),");
					}
				}
				
				
				exp.appendln("Cond = [],");
							
			}
			
			
		/*	
			if(messages[i].single_function_found){
			
				
				for(int j =0;j<messages[i].single_function.size();j++){
					exp.appendln(messages[i].single_function.elementAt(j).toString());
					exp.append(sep);
					exp.appendln("knows(");
					exp.append(messages[i].single_function_arg.elementAt(j).toString());
					exp.append(",1,StepMin1),");
				}
			}
			
		*/	
			
			if(messages[i].kgenFound){
				for(int j =0;j<messages[i].kgenArgument.size();j++){
					exp.appendln("keygen(");
					exp.append(messages[i].kgenArgument.elementAt(0).toString());
					exp.append(",Key),");
				}
			}
			
			
			if(messages[i].new_Arg.size()>0){
				for(int j =0;j<messages[i].new_Arg.size();j++){
					exp.appendln("belege(");
					exp.append(messages[i].new_Arg.elementAt(j).toString().toLowerCase());
					exp.append(sep);
					exp.append(messages[i].new_Arg.elementAt(j).toString());
					exp.append("),");
				}
			}
			
			exp.appendln("Term = ");
			exp.append(messages[i].messageInPrSyntax);
			exp.append(end);
			
			exp.popInd();
			exp.newline();
			
		}
		
		if (MessageParse.kgenFound_Stat){
			exp.appendln("%------------- specific knowledge for TLS Protocol-------");	
			if(keys.size()>0){
				
				for(int j =0;j<keys.size();j++){ 
					exp.appendln("symkey(k");
					exp.append(Integer.toString(j+1));
					exp.append(").");
				}
				for(int j =0;j<keys.size();j++){ 
					exp.appendln("keygen(");
					exp.append(keys.elementAt(j).toString());
					exp.append(",k");
					exp.append(Integer.toString(j+1));
					exp.append(").");
				}
			}
		}
		if (keys.size()>0) exp.appendln("%----------------- Asymetric Keys -------------------------");
		for(int j =0;j<keys.size();j++){ 
			exp.appendln("key(");
			exp.append(keys.elementAt(j).toString());
			exp.append(").");
		}
		exp.newline();
		if (symkeys.size()>0) {
			exp.appendln("%----------------- Symetric Keys -------------------------");
//			exp.appendln("kn(inv(Key),1,I) :- symkey(Key), knows(Key,1,I).");
			for(int j =0;j<symkeys.size();j++){ 
				exp.appendln("symkey(");
				exp.append(symkeys.elementAt(j).toString());
				exp.append(").");
			}
		}
		exp.newline();
		exp.appendln("%---------------------Angriffsziel-------------------------");
		exp.appendln("goal :- knows(");
		Vector _secret = new Vector();
		_secret = getTaggedValue("secret");
		exp.append(replaceConc(_secret.elementAt(0).toString()));
	
		exp.append(sep);
		exp.append(Integer.toString(nbrOfMessages));
		exp.append(").");
		
		
		exp.appendln("%---------------------Angriffsbereich----------------------");
		exp.appendln("range(0,");
		exp.append(Integer.toString(nbrOfMessages));
		exp.append(").");
		
		
//		exp.appendln("symkey(s_key).");
		
		text.append(exp);
		text.newline();
		text.appendln("%------------------end-----------------------------------");
		
		
		//set all static variables to their initial values
		MessageParse.def_Arg.removeAllElements();
		MessageParse.kgenFound_Stat = false;
		initialKnowledgeStr = "";
		initialKnowledgeFound= false;
		
	}//		end of buildProlog()
	
	
	
	
	
	
	/**
	 * the finction saves the prolog
	 * code to the specified file
	 */
	private void export(String ExportFileName){
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(ExportFileName));
			out.write(text.toString());
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	} //		end of export(String ExportFileName)
	
	
	

	public void runProlog(int _timeout){
		
		final int timeout = _timeout*1000;
		String line = null;
		Vector errors = new Vector();
		Vector output = new Vector();
		String out = "";
		String cmd;
		if (File.separator.equals("/")) cmd = System.getProperty("user.dir")+File.separator+"pl";
		else {
			File file= new File("c:\\Programme\\pl\\bin\\plcon.exe");
			File file1= new File("c:\\Program Files\\pl\\bin\\plcon.exe");
			if(file.exists()) cmd="c:\\Programme\\pl\\bin\\plcon";
			else if(file1.exists()) cmd="c:\\Program Files\\pl\\bin\\plcon";
			else cmd = "plcon";
		}
		
		extract(false);
		
		// Call SWI-Prolog

		// create command for execution

		//"c:\Program Files\pl\bin\plcon.exe" -f "C:\Documents and Settings\gurvanov\Local Settings\Temp\geneProlog.pl" -t output_depth
		String command = cmd + " -q -f " + System.getProperty("java.io.tmpdir")+File.separator+"geneProlog.pl" + " -t output_depth";
		
		err = "";
			// OS call
			try {
				final Process p = Runtime.getRuntime().exec(command);
				Thread destroyProcess = new Thread() {
				  	public void run() {
						try {
					 		sleep (timeout);
					 		System.out.println("sleep process!!!");
							} catch (InterruptedException ie) {
								err = ("Failure: " + ie);
								
							} finally {
					  			p.destroy();
					  			err = "Timeout of "+timeout/1000+" seconds reached.\nNo answer concerning the security could be derived.\n";
					  			}
						}
				};
				destroyProcess.start();


	
		if (err.equals("")) {
							BufferedReader lsOut = new BufferedReader(new InputStreamReader(p.getInputStream()));
							
							BufferedReader lsErr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
							
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
	 * the function replaces a::b with [a,b]
	 */
	
	public static String replaceConc(String term){	
	
	String result=term;
	int concindex=result.indexOf("::");
	if(concindex==0) return replaceConc(result.substring(2));
		while (concindex != -1)
		{
		int index=lastWordIndex(result.substring(0,concindex));
		String vorconc = result.substring(0,index);
		String midconc = result.substring(index,concindex);

		String postconc = result.substring(concindex+2);
		result=vorconc+"["+midconc+","+postconc;
		int endconc=firstWordIndex(postconc);
	

		result = result.substring(0,concindex+endconc+3)+"]"+result.substring(concindex+endconc+3);
		concindex=result.indexOf("::");
	}
	return result;
	}
	private static int lastWordIndex(String st) {
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
						else if(st.substring(0,i).endsWith("symsign"))
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
	
private static int firstWordIndex(String st) {
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
	
	
	
} // end of class
