package tum.umlsec.viki.tools.statechart2prolog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collection;
import java.util.Vector;


/**
 * Class to manage output of the Statechart2Prolog plug-in.
 * @author Dimitri Kopjev: kopjev@in.tum.de	
 */
public class Expression {
	
	/** StringBuffer to save output text */
	private StringBuffer body;
	
	/** Counter for tab-symbols. */
	private int indentCounter=0;
	
	/** Counter for space-symbols. */
	private int SpaceCounter=0;


	/**
	 * Class constructor.
	 */
	public Expression(){
			body= new StringBuffer("");
	}

	/**
	 * Appends text from file.
	 * @param ImportFileName Name of the file to import.
	 */
	public void appendFromFile(String ImportFileName) throws FileNotFoundException {	
		File file;
		FileReader in = null;
		try{
			file= new File(ImportFileName);
			in = new FileReader(file);
			char[] buffer = new char[1024];
			int len;
			while((len = in.read(buffer)) != -1){
				String s = new String(buffer,0,len);
				body.append(s);
			}
		}catch(Exception e){
			body.append("File:"+ImportFileName);
			// OK, I know this is ugly and needs to be fixed. Just need to figure out how this strange method is working.
			throw new FileNotFoundException(e.getMessage());
		}
		
		// close in
		finally{
			try{
				if(in!=null){
					in.close();
				}
			}catch(Exception e){
				body.append("File:"+ImportFileName);
				throw new FileNotFoundException(e.getMessage());
			}
		}
	}
	
	
	/**
	 * Appends a string.
	 * @param str String to append.
	 */
	public void append(String str){
			body.append(str);
	}
	
	
	/**
	 * Appends a StringBuffer.
	 * @param sb StringBuffer to append.
	 */
	public void append(StringBuffer sb){
			body.append(sb);
	}	
	
	
	/**
	 * Appends another Expression.
	 * @param exp Expression to append.
	 */
	public void append(Expression exp){
			body.append(exp.toString());
	}
	
	
	/**
	 * Appends a line.
	 * @param str Line to append.
	 */
	public void appendln(String str){
			newline();		
			appendInd();
			appendSp();
			body.append(str);		
	}	
	
	
	/**
	 * Appends a line with brackets check.
	 * @param str Line to be checked and append.
	 */
	public void checkedAppendln(String str){
		if(IsBracketesOK(str)){
			appendln(str);			
		} else {
			appendln("%Brackets not match in following line: ");
			appendln(str);
		}		
	}


	/**
	 * Appends a line separator.
	 */	
	public void newline(){
		body.append(System.getProperty("line.separator"));
	}
	
	
	/**
	 * Appends SpaceCounter spaces.
	 */
	private void appendSp(){
		body.append(repeat(' ',SpaceCounter));
	}


	/**
	 * Increases SpaceCounter at number rep.
	 * @param rep The number to increase SpaceCounter at.
	 */	
	public void pushSp(int rep){
		SpaceCounter= SpaceCounter+rep;
	}
	
	
	/**
	 * Decreases SpaceCounter at number rep.
	 * @param rep The number to decrease SpaceCounter at.
	 */
	public void popSp(int rep){
		SpaceCounter=SpaceCounter-rep;
	}
	
	
	/**
	 * Appends indentCounter tabs.
	 */	
	public void appendInd(){
		body.append(repeat('\t',indentCounter));		
	}
	
	
	/**
	 * Increments indentCounter.
	 */
	public void pushInd(){
		body.append(repeat('\t',indentCounter));
		indentCounter++;
	}
	
	
	/**
	 * Decrements indentCounter.
	 */
	public void popInd(){
		if (indentCounter>0) indentCounter--;
	}
	
	
	/**
	* Produce a String of a given repeating character.
	* @param c the character to repeat.
    * @param count the number of times to repeat.
	* @return String, e.g. repeat('*',4) returns "****".
	*/
	public final static String repeat (char c, int count){
		char[] s = new char[count];
		for ( int i=0; i < count; i++ ){
			s[i] = c;
		}
		return new String(s).intern();
	}
	
	
	/**
	 * Converts Expression to String.
	 * @return Expression as String.
	 */
	public String toString(){
		return body.toString();
	}
	
	
	/**
	 * Indicates if the Expression is emppty.
	 * @return true if the Expression is empty.
	 */
	public boolean isEmpty(){
		return body.length()==0;
	}
	
	
	/**
	 * Checks brackets of total Expressionbody.
	 * @return true if brackets match.
	 */
	public  boolean IsBracketesOK(){
		return 	IsBracketesOK(body.toString());
	}
	
	
	/**
	 * Checks brackets of the string.
	 * @param Exp : String to be checked.
	 * @return true if in this "[([{]" matches "[)]}]".
	 */
	public static boolean IsBracketesOK(String Exp){
		int i = 0;
		int B1cnt = 0;
		int B2cnt = 0;
		int B3cnt = 0;

		if(Exp == null){
			// empty is well-formed
			return true;
		}
		// otherweise
		for(;i<Exp.length();i++){
			// check if current char is a left bracket,  
			// System.out.println(Exp.charAt(i));
			char temp =Exp.charAt(i);
			switch(temp){
				case '(': B1cnt++;break;
				case '[': B2cnt++;break;
				case '{': B3cnt++;break;
				// not allow right bracket come before left bracket
				case '}': B3cnt--; if(B3cnt<0){return false;}break;
				case ']': B2cnt--; if(B2cnt<0){return false;}break;
				case ')': B1cnt--; if(B1cnt<0){return false;}break;
			}
		}
		if(B1cnt==0 && B2cnt==0 && B3cnt==0){
			// all right 
			return true;	
		}else {
			// there is something not well-formed
			return false;
		}
	}
	
	
	/**
	 * Analyse well-formed expression, get the Semantik 1 level Args<br>
	 * e.g. (a,(),(((),()),()),(())) has  args a, (),  (((),()),()),(()).<br>
	 * @param sep Separator.
	 * @param Exp Expression string.
	 * @return Collection of arguments (String).
	 */	
	public static Collection sepArgs(String sep, String Exp) {
		// () and {} [] to recognize
		// conc( | enc(sign(conc(kgen(ArgS_1_2), ArgS_1_1), inv(k_s)), ArgS_1_2),  sign (conc(s, k_s), inv(k_ca) ) ) |)
		String exp = Exp;
		sep=",";
		Vector result = new Vector();	
		int parambegin = 0;
		String param = "";

		while(parambegin<exp.length()){
			param = getFirstBlock(sep, exp.substring(parambegin));
			if(param!=null){
				if(exp.substring(parambegin,parambegin+param.length()).trim()!=""){			
					result.add(exp.substring(parambegin,parambegin+param.length()).trim());
				}
			}else{
				break;
			}
						
			// remain part
			parambegin = parambegin+param.length()+ sep.length();				
		}	
		if (param==null){
			return null;
		}else {
			return result;
		}
	}
	

	/**
	 * Gets first block of the parameter string<br>
	 * e.g.<br>
	 * "a,(),(((),()),()),(())" get a<br>
	 * "(((),()),()),(())" get  (((),()),())<br>
	 * ""get ""<br>
	 * "((((),()),()),(())" (a "(" too much) get null<br>
	 * not trim().<br>
	 * @param sep Separator.
	 * @param paramseq Parameter sequence.
	 * @return first complete parameter.
	 */
	public static String getFirstBlock(String sep, String paramseq){
		int i = 0;
		int j = 0;
		String lBrackets = "([{";
		String rBrackets = ")]}";
		int lBcnt = 0;
		int rBcnt = 0;
		
		if(paramseq == null){
			return null;
		}
		// otherweise
		for(;i<paramseq.length();i++){
			// check if current char is a left bracket,  
			if(lBrackets.indexOf(paramseq.charAt(i))>=0){
				lBcnt++; 	
			}
			// check if current char is a right bracket,  
			if(rBrackets.indexOf(paramseq.charAt(i))>=0){	
				rBcnt++;
			}
			if(paramseq.indexOf(sep,j)==i){
				j=i+sep.length();
				if(lBcnt==rBcnt){
					// index of first complete parameter's end, plus 1(first index of sep)
					return paramseq.substring(0,i);
				}
			}
		}
		// it's end, check wether sth is wrong
		if(lBcnt==rBcnt){
			// all right , there is one parameter, 
			return paramseq;	
		}else {
			// there is something not well-formed
			return null;
		}
	}
}
