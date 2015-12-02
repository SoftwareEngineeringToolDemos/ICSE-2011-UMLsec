
package tum.umlsec.viki.tools.sequence2prolog;


/**
 * @author gurvanov
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

import java.util.Vector;

  class MessageParse{

  	public int 		message_no;			// message number from the diagram
  	public String 	sender;				// sender contains the sender of the message
	public String 	receiver;			// receiver contains the receiver of the message
	public String 	functionName;		// functionName contains the name of the function
	public String 	message;			// message contains the message as it is in the sequence diagram
	public String	messageInPrSyntax;	// contains the message in prolog syntax
	
	public boolean	condFound;			// Contains true if there is condition in guard_X
	public String 	condition;			// the condition found in the guard_X tag of the adversary
	public Vector 	single_condition;	// the subconditions in Vektor-Form in prolog syntax
	public Vector	single_condition_arg;//vector contains the arguments defined in the single_condition
										//f.E. single_condition(0): equal(Arg_1_1, Expression)
										// => single_condition_arg(0): Arg_1_1
	public Vector	single_condition_value; //contains the value with which the argument is defined in single_condition
											//	f.E. single_condition(0): equal(Arg_1_1, Expression)
											// => single_condition_value(0): Expression
	public boolean single_function_found; // shows if any function is found in protokol, kgen for TLS not in it
	public Vector	single_function;	// we save the functions we find in single_condition in here
	public Vector	single_function_name; // the name of the function which is found
	public Vector	single_function_arg;// the argumen, from which the function generates the key or the address or..else
	public Vector	single_function_gen;// the generated ftom the argument key, address or..else

	public String	receive;			// the receive() substring in guard_X
	public boolean	receiveFound;		// shows if there is receive() substring in guard_X
	public String[]  single_receive;	// the sub_receive variables in Array form
	public Vector single_receive_V;		//the same as single_receive but in Vector form		

	public Vector all_Arguments;		// contains all Arguments and Variables (beginning with upper case) found in 
										// 										the message and its gurad_X
	
	public static Vector def_Arg = new Vector();	//contains all Arguments in protokol
	public Vector new_Arg;				//contains the new argument found in the messig, at the end they are saved with prolog function "belege"
	public Vector old_Arg;				//contains all arguments already set with "belege", and which are now retreived with pl funct. "var"
	
	public boolean kgenFound;			// function used in TLS Protokol, generates Key, if true then presen in this message
	public static boolean kgenFound_Stat = false;// function used in TLS Protokol, generates Key, if true then presen in protokol
	public Vector kgenArgument;			// the argument, from which the kgen generate the key
	
	public Vector symKeyVar;			// if in the messages there are any Variables that schoud be symetric keys, the Variable is saved in this Vector
	
	
	public MessageParse(){
		message_no = -1;
		sender = "";
		receiver = "";
		functionName = "";
		message = "";
		messageInPrSyntax = "";
		condFound = false;
		condition = "";
		single_condition = new Vector();
		single_condition_arg = new Vector();
		receiveFound = false;
		receive ="";
		single_receive_V = new Vector();
		single_condition_value = new Vector();
		single_function = new Vector();
		single_function_found = false;
		single_function_name = new Vector();
		single_function_arg = new Vector();
		single_function_gen = new Vector();
		all_Arguments = new Vector();
		new_Arg = new Vector();
		old_Arg = new Vector();
		kgenFound = false;
		kgenArgument = new Vector();
		symKeyVar = new Vector();
	}
	
	
	
	/**
	 **   extracts the function name from the message
	 **/
	public String extractFunctionName() {

		String msg = message.trim();
		String name = "error";
		String rest ="";

		
		int test = msg.indexOf("(");				// shows where the begin of the message is
		
		if( test != -1){							// if the message is found, it is being removed
			name = msg.substring(0, test).trim();
		}	
		return name;
	} //end of		 extractFunctionName()
	
	
	
	/** 
	 * the function converts the message to prolog code
	 */
	public void MessageToPrologSyn(){
		String _tmp = message;												//temporary variable to manipulate the message
		_tmp = _tmp.replaceAll(" ","");										//remove all spaces
		int _start = -1;													//shows where the main message starts(without function name)
		boolean _bracketsOk = checkBrackets(_tmp);							//checks if the brackets ar ok
		if (!_bracketsOk){
			messageInPrSyntax = _tmp + " Error: Something wrong with the Brackets!!!";   //brackes not right
		}
		else{																//brackets OK, go on..
			
			_start = _tmp.indexOf("(");										//the right syntax of a message is MessagName(Message)
			if(_start != -1){												//at least one openning bracket must be found
				_tmp = _tmp.substring(_start + 1,_tmp.length() - 1).trim();	//remove function name and brackets

				_tmp = _tmp.replaceAll(" ","");
				
				boolean symFound = false;									// the following part replaces symenc and symsign
				if(_tmp.indexOf("symenc")!=-1 || _tmp.indexOf("symsign")!=-1)// with enc and sign
																			//also
					symFound = true;										//builds vector symKeyVar
				while(symFound){											//for more details see replyce_sym() function
					_tmp = replace_sym(_tmp);
					symFound = false;
					if(_tmp.indexOf("symenc")!=-1 || _tmp.indexOf("symsign")!=-1)
						symFound = true;
				}
				
				_tmp = replaceKgen(_tmp);									//replaces kgen(Argument) with key
				
				
				
				
				
				
				if (_tmp.length() == 0)
					messageInPrSyntax = "[]";
				else
					messageInPrSyntax = _tmp;									//over
				
			}
			else{															//something wrong, no openning bracket after function name
				messageInPrSyntax = _tmp + " Error: Something wrong with the Brackets!!!";   //brackes not right
			}
		}
	}
	
	
	
	/** 
	 * the function checks if the brackets are OK
	 * the function is being called by MessageToPrologSyN() & parseCondition() !!!
	 */
	public static boolean checkBrackets(String _exp){
		int i = 0;
		//String lBrackets = "([{";
		//String rBrackets = ")]}";
		int B1cnt = 0;
		int B2cnt = 0;
		int B3cnt = 0;
	
		if(_exp == null){													//empty is well-formed

			return true;
		}
		for(;i<_exp.length();i++){											//otherweise			
			//check if current char is a left bracket,  
			//System.out.println(Exp.charAt(i));
			char temp =_exp.charAt(i);
			switch(temp){
				case '(': B1cnt++;break;
				case '[': B2cnt++;break;
				case '{': B3cnt++;break;
				//do not allow right bracket come before left bracket
				case '}': B3cnt--; if(B3cnt<0){return false;}break;
				case ']': B2cnt--; if(B2cnt<0){return false;}break;
				case ')': B1cnt--; if(B1cnt<0){return false;}break;
			}
		}
		if(B1cnt==0 && B2cnt==0 && B3cnt==0){								//all right 
			return true;	
		}else {																//there is something not well-formed
			return false;
		}	
	} // the end of ckeckBrackets()
	
	

	
	
	/** 
	 * the function replaces "kgen(Argument)" with "key"
	 * the function is being called by MessageToPrologSyN() !!!
	 */
	public String replaceKgen(String _msg){
		int _start = -1;						//shows at wchich plase is the kgen(Argument) found, if at all(then variable's value -1)
		int _end = -1;							//shows at wchich plase  kgen(Argument) ends, if at all present(then variable's value -1)
		String _beforeStart;					//temporary save the string befor kgen(Argument)
		String _afterEnd;						//temporary save the string after kgen(Argument)
		_start = _msg.indexOf("kgen");			
		if (_start != -1){						//that is in case kgen is found(right syntah kgen(Argument)
			_end = _msg.indexOf(")",_start);	//that is where it ends, as the right syntax is kgen(Argument)
			
			String _arg = _msg.substring(_start + 5,_end);
			if(!kgenArgument.contains(_arg))
				kgenArgument.addElement(_arg);
			
			_msg = _msg.substring(0,_start) + "Key" + _msg.substring(_end + 1, _msg.length());
												//builds the message
			kgenFound = true;					//shows that kgen was found in the message
			//if (!kgenFound_Stat ) kgenFound_Stat = true;
			kgenFound_Stat = true;
												
			return _msg;
		}
		else{
			return _msg;
		}
		
	} // the end of ReplaceKgen(String _msg)
	
	

	
	
	
	/** 
	 * the function parses the condition and build the single_condition
	 * 
	 */
	public void parseCondition(){
		if (!condFound) return;									//means there is no condition and nothing to parse, return
		String _tmpCond = condition;							//temporary saves the condition
		_tmpCond = _tmpCond.replaceAll(" ","");					//remove all spaces
		_tmpCond = _tmpCond.substring(1,_tmpCond.length()-1);	//[condition] => condition

// eventually also 	"kgen(Argument)" => "key"
		
		boolean symFound = false;									// the following part replaces symenc and symsign
		if(_tmpCond.indexOf("symenc")!=-1 || _tmpCond.indexOf("symsign")!=-1)// with enc and sign
																	//also
			symFound = true;										//builds vector symKeyVar
		while(symFound){											//for more details see replyce_sym() function
			_tmpCond = replace_sym(_tmpCond);
			symFound = false;
			if(_tmpCond.indexOf("symenc")!=-1 || _tmpCond.indexOf("symsign")!=-1)
				symFound = true;
		}
		
		int _concatenationFound = _tmpCond.indexOf("&");		//serches for & in the condition
		if (_concatenationFound == -1){							//in this case condition consits of one single_condition
			if(checkBrackets(_tmpCond))
				single_condition.add(_tmpCond);					//add it to the vector and end
			else
				single_condition.add("something wrong with the brackests");
			return;
		}
		else{													//in this case there are more than one conditions
			
			while(true) {
				if(checkBrackets(_tmpCond.substring(0,_concatenationFound)))
					single_condition.add(_tmpCond.substring(0,_concatenationFound));	//add the part before & to the vector
				else
					single_condition.add("something wrong with the brackests");
				if ((_concatenationFound + 1) >= _tmpCond.length())return;   			//we check if it was the last condition,
																						//and in case it was we break up
				
				_tmpCond = _tmpCond.substring((_concatenationFound + 1), _tmpCond.length());
				_concatenationFound = _tmpCond.indexOf("&");
				if (_concatenationFound == -1) {
					
				if(checkBrackets(_tmpCond))
					single_condition.add(_tmpCond);								//add it to the vector and end
				else
					single_condition.add("something wrong with the brackests");
				return;
				}
			}
			
		}		
	} //end of parseCondition()
	
	
	
	/** 
	 * the function converts the single_condition to prolog syntax
	 * as it saves the new condition on the same place in single_condition
	 * only if it begins with equal(
	 * else the entry is treated as function(it must begin with find_)
	 * and is being saved in single_function
	 * in the end single_condition may become empty => condFound = false
	 */
	public void ConditionToPrSyntax(){
		if (!condFound) return;
		
		

		String _condition = "";
		int _vectorSize = single_condition.size();
		
		for(int i=0;i<_vectorSize;i++){
			int _start = -1;
			int _separator = -1;
			
			_condition = (String)single_condition.elementAt(i);			//save condition temporary
			single_condition.removeElementAt(i);						//remove old from vector
			
			if(_condition.startsWith("equal") || 
					_condition.indexOf("=")!=-1){						//replaces equal(arg1,arg2) with arg1=arg2

				
				
				if(_condition.indexOf("=")==-1) _condition = _condition.substring(6,_condition.length()-1);
				else _condition = _condition.replaceAll("=",",");
				
				String _firstblock = getFirstBlock(_condition);
	
				String _secondblock = _condition.substring(_firstblock.length()+1,_condition.length());
				int which = build_single_cond_arg(_firstblock, _secondblock);
				switch(which){
					case 1:
						_condition = _firstblock + "=" + _secondblock;
						single_condition_arg.addElement(_firstblock);
						single_condition_value.addElement(_secondblock);
						single_condition.insertElementAt(_condition,i);
						break;
					case 2:
						_condition = _secondblock + "=" + _firstblock;
						single_condition_arg.addElement(_secondblock);
						single_condition_value.addElement(_firstblock);
						single_condition.insertElementAt(_condition,i);
						break;
					default:
	
						_condition = "Something wrong, no argument found, or the " +
										"argument is not in the list of the receive() in guard_" + message_no;
						
				System.out.println("index"+i+" zu size "+single_condition.size());
						single_condition.insertElementAt(_condition,i);
				}
			}
			else if(_condition.startsWith("find_")){ 								//it is a function, we leave it as it is,
																					//but we must remove it from single_condition
																					//and put it to single_function vector
				
				if (!single_function_found)	single_function_found = true;			
				single_condition.insertElementAt("no_condition",i);
				single_function.addElement(_condition);
				
				_start = _condition.indexOf("(");
				
				
				single_function_name.addElement(_condition.substring(0,_start));
				String _rest = _condition.substring(_start+1,_condition.length()-1);
				_start = _rest.indexOf(",");
				single_function_arg.addElement(_rest.substring(0, _start));
				_rest = _rest.substring(_start+1,_rest.length());		
				single_function_gen.addElement(_rest);
				
			}
			else{
				//no equal, no find_ ? What?
				System.out.println("no equal, no find_ ? What?");
			}
		}
///
		
////
		
////
		
		
		//tuk da se proveri dali ima equal ili sa samo funkcii
		boolean no_cond;
		no_cond = single_condition.contains("no_condition");
		while(no_cond){
			single_condition.remove("no_condition");
			no_cond = single_condition.contains("no_condition");
		}
		if(single_condition.isEmpty()) condFound = false;
		return;
	}	//end of ConditionToPrSyntax()
	

	
	/** 
	 * This function replaces functions formed as (find_xxx(Argument)) 
	 * found in the messages
	 * an defined Arguments with equal
	 */
	
	public void replaceFunctions(){
		if (single_function_found){
			System.out.println("do tuk stiga");
			boolean _oneIterMore = false;
			if (messageInPrSyntax.indexOf("find_")> -1) _oneIterMore = true;
			while(_oneIterMore){
				messageInPrSyntax = replaceFunction(messageInPrSyntax);
				_oneIterMore = false;
				if (messageInPrSyntax.indexOf("find_")> -1) _oneIterMore = true;
				System.out.println("cikul");
			}
	//		messageInPrSyntax = replaceFunction(messageInPrSyntax);
			String _tmp = "";
			for (int i = 0; i< single_condition.size();i++){
				_tmp = single_condition.elementAt(i).toString();
				single_condition.removeElementAt(i);
				_tmp = replaceFunction(_tmp);
				single_condition.add(i,_tmp);
			}
		}
	}
	
	
	
	
	
	
	public String replaceFunction(String _msg){
		int _start = -1;						//shows at wchich plase is the find_aa(Argument) found, if at all(then variable's value -1)
		int _end = -1;							//shows at wchich plase  find_aa(Argument) ends, if at all present(then variable's value -1)
		String _beforeStart;					//temporary save the string befor find_aa(Argument)
		String _afterEnd;						//temporary save the string after find_aa(Argument)
		for(int i =0;i<single_function_name.size();i++){
			_start = _msg.indexOf(single_function_name.elementAt(i).toString());
			if (_start != -1){
				_end = _msg.indexOf(")",_start);	//that is where it ends, as the right syntax is find_aa(Argument)
				_msg = _msg.substring(0,_start) + single_function_gen.elementAt(i) + _msg.substring(_end+1,_msg.length());
			}
		}
		
		
		return _msg;
	}
	
	
	
	
	/** 
	 * the function returns the first complete block of a sequence
	 * run from ConditionToPrSyntax(){
	 */
	
	public static String getFirstBlock(String _firstBlock){
		
		if (_firstBlock == "") return "";

		int lCounter = 0;
		int rCounter = 0;
		int endOfFirstBlock = 0;
		boolean firstBracketFound = false;					//at least one pair of brackets must be found, otherwise
		
		String _sep = ",";													//the two parts are separated only with ","
		String lBrackets = "([{";
		String rBrackets = ")]}";
		for(int i = 0;i<_firstBlock.length();i++){
 
			if(lBrackets.indexOf(_firstBlock.charAt(i))>=0){  //check whether the curren character is bracket
				lCounter++;
				firstBracketFound = true;
			}
			if(rBrackets.indexOf(_firstBlock.charAt(i))>=0){
				rCounter++;
				firstBracketFound = true;
			} 
			if(_sep.indexOf(_firstBlock.charAt(i))>=0 && firstBracketFound == false ) {	//a comma is found before any bracket is found
																						//means the string may only be separate by comma
				endOfFirstBlock = i;
				break;
			}
			if(lCounter == rCounter && firstBracketFound == true){
				endOfFirstBlock = i;
				break;
			}
		}
		if(!firstBracketFound) 
			_firstBlock = _firstBlock.substring(0,endOfFirstBlock); // while there are no brackets in the first part, after the the first
																	// argument follows coma and we do not need it,
																	// end is endOfFirstBlock
		else
			_firstBlock = _firstBlock.substring(0,endOfFirstBlock+1);//there are brackets around the first argumet,
																	//end by endOfFirst block + 1 , while we need the closing bracket
																	
		
		return _firstBlock;

	}	//end of getFirstBlock(String sep, String _firstBlock)

		
	
	
	
	
	
	/** builds from receive string,
	 * s Array with the single receive arguments
	 * receive => single_receive
	 */
	public void parseReceive(){
		String sep = ",";
		if(!receiveFound) return;
		String _receiveStr = receive;
		_receiveStr = _receiveStr.substring(8,_receiveStr.length()-1);					//receive has the structure receive(param)
		int _counter = 0;																				// => param
		for(int i = 0;i<_receiveStr.length();i++){
			if(sep.indexOf(_receiveStr.charAt(i))>=0) _counter ++;
		}
		
		
		single_receive = new String[_counter+1];
		single_receive = _receiveStr.split(sep);
		
		for (int i = 0; i< single_receive.length;i++){						//copy the Array to Vector
			single_receive_V.addElement(single_receive[i]);					//single_receive => single_receive_V
		}
		
	}//end of parseReceive()
	
	
	
	
	
	

	/** the functio is run from ConditionToPrSyntax()
	 * it returns the place at which the argument is found
	 */
	public int build_single_cond_arg(String arg1, String arg2){
		//we check which one of the argumets corresponds to one in the array single_receive
	
		
		for(int i=0;i<single_receive[i].length();i++){
			if (single_receive[i].compareTo(arg1)==0) return 1;
			if (single_receive[i].compareTo(arg2)==0) return 2;
		}
		return 3;
	}//end of
	
	
	/** builds a vector all_Arguments
	 * that contains all arguments and Variables found
	 * in the message and its guard_X
	 * f.E. Data_1, Init_1, Init_2
	 */
	
	public void build_all_Arguments(){
		String _expression;
		if (condition.length()>0)
			_expression = condition + "&" + messageInPrSyntax;
		else
			_expression = messageInPrSyntax;
		if(single_receive_V.size()>0){
			String _tmp;
			for(int i = 0;i< single_receive_V.size();i++){
				_tmp = (String)single_receive_V.elementAt(i);
				if(!(all_Arguments.contains(_tmp))) all_Arguments.addElement(_tmp); 
			}		
		}
	
		_expression = _expression.replaceAll(" ","");
		String _tmp;
		
		
		
		if (!condFound) 
			build_all_Arguments_sec(_expression);
		else
			for(int i = 0; i<single_condition_arg.size();i++){
				_tmp = (String)single_condition_arg.elementAt(i);
				if(!(all_Arguments.contains(_tmp))) all_Arguments.addElement(_tmp); 
			}
		build_all_Arguments_sec(_expression);

	
	} //end of  build_all_Arguments()
	
		public void build_all_Arguments_sec(String exp){
			String _endChar = "([{,]}):&=";					//characters which may follow the variable, in order to find out where it ends
			String _argument = "";
			int _indexS = -1;
			int _indexE = -1;
			int firstPlace = 0;	 							//used to start the case condition
			String _expression = exp;
			for(int i = 0; i<_expression.length();i++){
				char a = _expression.charAt(i);
				if(a>='A' && a<='Z') {
					_indexS = i;							//eventually one argument found
															//only if the upper case char is not in the middle of a word
					if(_indexS <0) return;
					if (_indexS == 0) 
						firstPlace = 2;
					else
						firstPlace = 1;
					
					switch(firstPlace){
						case (1):        					//we check if the char priort to the beginning of the found
															//argument is a legal charakter(the argument is in the middle of the expression)
							
							if(_endChar.indexOf(_expression.charAt(_indexS-1))>=0){		//check if the character before the beginning is a
																						//legal character("([{,]}):&"), else it must be
																						//a part ot other word
								
								//hier muss nichts gemacht werden, einfach zu case (2)
								
								
							}
							else {   //the character is part of the word, so find the next legtal character, end send the rest of the string for new
									 //search
									 //if the the whole string after is sent, then error occurs as there can be to upper case symbols one after 
									 //the other and in the new iteration it will be interepreted as start of a word
								for(int j = _indexS; j<_expression.length();j++){
									if(_endChar.indexOf(_expression.charAt(j))>=0){	
										_expression = _expression.substring(j +1);
										build_all_Arguments_sec(_expression);
										return;
									}
								return;
								}
								
							}
							
							
						case (2):							//we land here when we want to skip the check ot the character
															//prior to the beginning(as the word starts at  index 0)
							
							//search for the end of the argument
							
							for(int j = _indexS +1; j<_expression.length();j++){				//read next character
								
								if (j == _expression.length()-1){							//the argument is at the end of the string
									_indexE = j;
									_argument  =_expression.substring(_indexS,_indexE);

									if(!all_Arguments.contains(_argument))
										all_Arguments.addElement(_argument);
									return;
								}
								
								
								if(_endChar.indexOf(_expression.charAt(j))>=0 ){				//if character equal one of _endChar = "([{,]})",
									//end found													// 							then the end is found
									_indexE =j;
									_argument  =_expression.substring(_indexS,_indexE);

									if(!all_Arguments.contains(_argument))
										all_Arguments.addElement(_argument);
									build_all_Arguments_sec(_expression.substring(_indexE+1));
									return;
									
								}																
								
							}																
				
					}

				}
			}
			
		} //end of  build_all_Arguments()
		
		
	
		
		/** builds a vector with the argument which are not defined
		 * in condition with equality
		 * That is in fact the difference:
		 * single_receive_V - single_condition_arg
		 */
		public void built_new_old_arg(){
			String _arg = "";										//temporary save the argument in it
			if (def_Arg.size()==0 && all_Arguments.size()>0){   //there are no Argumens till now, all are new, must be saved with "belege"
				
				for(int i=0; i< all_Arguments.size(); i++){
					_arg = all_Arguments.elementAt(i).toString();
					new_Arg.addElement(_arg);					//add the arguments to new_Arg so they can be saved with "belege"
					def_Arg.addElement(_arg);					//add the arguments to def_Arg so they can be any more treated as new
					//old_Arg are not changed, old arguments are in fact the ones that were alreade saved with "belege" and now
					//	must be retreaved with "var",
					//  just here there are now old ones as def_Arg is empty
				}
			}
			else if(def_Arg.size()>0 && all_Arguments.size()>0) {
				for(int i=0; i< all_Arguments.size(); i++){
					_arg = all_Arguments.elementAt(i).toString();
					boolean arg_found = false;
					arg_found = def_Arg.contains(_arg);
					if (arg_found){				// if found, it means it was already saved, and now must be retreaved with "var"
						old_Arg.addElement(_arg); //so we add it to the vector with the old arguments
						//it must not be saved again, so no changes to def_Arg or new_Arg
					}
					else{						// if not found, it means it is a new argument, and must be saved with "belege"
						new_Arg.addElement(_arg);//so we add it to the vector with the new arguments
						def_Arg.addElement(_arg);//we add it to defined arguments vecto, so it won't be treated as new in future
					}
				}
			}
			
		}
		
		
		/** 
		 * replaces symenc with enc in .....
		 * and      symsign with sign
		 * also
		 * save Variables that should be symetric keys in the
		 * Vectror symKeyVar
		 */
		public String replace_sym(String _str){
			int _startSign = -1;
			int _startEnc = -1;
			int _start = -1;
			int _end  = -1;
			char a;
			String _firstPart ="";
			String _key = "";
			_startEnc = _str.indexOf("symenc"); 					//search for symenc in the string
			_startSign = _str.indexOf("symsign");					//search for symsign in the string
			if(_startEnc ==-1 && _startSign==-1) return _str;		// there is no symenc or symsign in string, therefor return
			
			if(_startEnc<_startSign && _startEnc != -1)				//both symenc and symsign are found, but symenc is at firstplace 				
				_start = 1;
			else if(_startSign<_startEnc && _startSign != -1)		//both symenc and symsign are found, but symsign is at firstplace
				_start = 2;
			else if(_startEnc == -1)								//only symsign is found
				_start = 2;
			else if(_startSign == -1)								//only symenc is found
				_start = 1;

			
			switch(_start){										
				case 1://symenc at first place
					_firstPart = getFirstBlock(_str.substring(_startEnc));
					_firstPart = _firstPart.substring(7,_firstPart.length()-1);
					_key = _firstPart.substring(_firstPart.lastIndexOf(",")+1);
					a = _key.charAt(0);
					if(a>='A' && a<='Z') {
						if(!symKeyVar.contains(_key)) symKeyVar.addElement(_key);
					}
					
					_str = _str.replaceFirst("symenc","enc");
					break;
					
				case 2://symsign at first place
					_firstPart = getFirstBlock(_str.substring(_startSign));
					_firstPart = _firstPart.substring(8,_firstPart.length()-1);
					_key = _firstPart.substring(_firstPart.lastIndexOf(",")+1);
					a = _key.charAt(0);
					if(a>='A' && a<='Z') {
						if(!symKeyVar.contains(_key)) symKeyVar.addElement(_key);
					}
					
					_str = _str.replaceFirst("symsign","sign");
					break;
					
				default: _str = "!!!!!!!!error in replace_sym() function(MessageParse class)!!!!!!";	
		}
			
			
			return _str;
		}	
		
}//end of Class
  
