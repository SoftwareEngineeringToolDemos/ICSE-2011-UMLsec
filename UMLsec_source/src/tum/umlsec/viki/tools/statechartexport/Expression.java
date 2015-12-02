package tum.umlsec.viki.tools.statechartexport;

import java.io.File;
import java.io.FileReader;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author chent@in.tum.de <br>
 *         A help class with useful methods for String, check, separate,
 *         filtrate arguments or some other convertion.
 *  
 */
public class Expression {
    private StringBuffer body;

    private int SpaceCounter = 0;

    public Expression() {

        body = new StringBuffer("");

    }

    /**
     * append a String to this expression
     * 
     * @param str
     */
    public void append(String str) {
        body.append(str);
    }

    /**
     * append a StringBuffer to this expression
     * 
     * @param sb
     */
    public void append(StringBuffer sb) {
        body.append(sb);
    }

    /**
     * append a Expression to this expression
     * 
     * @param exp
     */
    public void append(Expression exp) {
        body.append(exp.toString());
    }

    /**
     * append a newline and a String to this expression
     * 
     * @param str
     */
    public void appendln(String str) {
        newline();
        appendSp();
        body.append(str);

    }

    /**
     * append a newline and a StringBuffer to this expression
     * 
     * @param sb
     */
    public void appendln(StringBuffer sb) {
        newline();
        appendSp();
        body.append(sb);
    }

    /**
     * check brackets and append String
     * 
     * @param str
     *            String to be checked and append
     */
    public void checkedAppendln(String str) {
        if (IsBracketesOK(str)) {
            appendln(str);
        } else {
            appendln("%Brackets not match in following line: ");
            appendln(str);
        }

    }

    /**
     * check brackets and append String
     * 
     * @param sb
     *            StringBuffer to be checked and append
     */
    public void checkedAppendln(StringBuffer sb) {
        checkedAppendln(sb.toString());
    }

    /**
     * Import text from file into Expression
     * 
     * @param ImportFileName
     */
    public void appendFromFile(String ImportFileName) {

        File file;
        FileReader in = null;
        try {
            file = new File(ImportFileName);
            in = new FileReader(file);
            char[] buffer = new char[1024];
            int len;
            while ((len = in.read(buffer)) != -1) {
                String s = new String(buffer, 0, len);
                body.append(s);
            }

        } catch (Exception e) {
            body.append("File:" + ImportFileName);
            e.printStackTrace();
        } finally {//close in
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                body.append("File:" + ImportFileName);
                e.printStackTrace();
            }
        }

    }

    /**
     * append whitespace ' ', basical method for intent
     *  
     */
    private void appendSp() {
        body.append(repeat(' ', SpaceCounter));

    }

    /**
     * Put some space head for later block, used at block begin and popSp(int
     * rep) at block end
     * 
     * @param rep :
     *            how many " " should be put
     */
    public void pushSp(int rep) {
        //TestCode:body.append("\npush"+indentCounter+"\\t");
        SpaceCounter = SpaceCounter + rep;

    }

    /**
     * reduce space head for later block
     * 
     * @param rep :
     *            how many " " should be put
     */
    public void popSp(int rep) {

        SpaceCounter = SpaceCounter - rep;
        //Testcode:body.append("pop"+indentCounter+"\\t");

    }

    /**
     * Put indent for later block , used at block begin and popInd() at block
     * end
     *  
     */
    public void pushInd() {
        pushSp(4);

    }

    /**
     * reduce indent for later block
     *  
     */

    public void popInd() {
        popSp(4);
    }

    /**
     * make a newline with system default line separator
     */
    public void newline() {
        body.append(System.getProperty("line.separator"));
    }

    /**
     * convert Expression to String
     */
    public String toString() {
        return body.toString();
    }

    /**
     * show if Expression is empty
     */
    public boolean isEmpty() {
        return body.length() == 0;
    }

    /**
     * check if this expression has correct bracketes Paar.
     */
    public boolean IsBracketesOK() {
        return IsBracketesOK(body.toString());
    }

    /**
     * Produce a String of a given repeating character.
     * 
     * @param c
     *            the character to repeat
     * @param count
     *            the number of times to repeat
     * @return String, e.g. repeat('*',4) returns "****"
     */
    public final static String repeat(char c, int count) {
        char[] s = new char[count];
        for (int i = 0; i < count; i++) {
            s[i] = c;
        }
        return new String(s).intern();
    } // end repeat

    /**
     * check Bracketes is well-formed or not
     * 
     * @param Exp :
     *            to be checked String
     * @return true if in this "[([{]" matches "[)]}]"
     */

    public static boolean IsBracketesOK(String Exp) {
        int i = 0;
        int B1cnt = 0;
        int B2cnt = 0;
        int B3cnt = 0;

        if (Exp == null) {
            //			empty is well-formed
            return true;
        }
        //		otherweise
        for (; i < Exp.length(); i++) {
            //			check if current char is a left bracket,
            char temp = Exp.charAt(i);
            switch (temp) {
            case '(':
                B1cnt++;
                break;
            case '[':
                B2cnt++;
                break;
            case '{':
                B3cnt++;
                break;
            //not allow right bracket come before left bracket
            case '}':
                B3cnt--;
                if (B3cnt < 0) {
                    return false;
                }
                break;
            case ']':
                B2cnt--;
                if (B2cnt < 0) {
                    return false;
                }
                break;
            case ')':
                B1cnt--;
                if (B1cnt < 0) {
                    return false;
                }
                break;

            }

        }

        if (B1cnt == 0 && B2cnt == 0 && B3cnt == 0) {
            //			all right
            return true;
        } else {
            //			there is something not well-formed
            return false;
        }

    }

    /**
     * Get the the String in the broadst "("")" from a expression
     * 
     * @param str
     * @return the String in the broadst "("")" <br>
     *         e.g get "message" from "send(message)"
     */

    public static String getContentFromBracket(String str) {
        if (str.lastIndexOf(")") - str.indexOf("(") > 1) {
            return str.substring(str.indexOf("(") + 1, str.lastIndexOf(")"));
        } else {
            return null;
        }
    }

    /**
     * Analyse well-formed expression, get the Semantik 1 level arguments
     * 
     * @param sep
     *            the seperator
     * @param Exp
     *            the expression String
     * @return Collection of arguments(String) <br>
     * 
     * e.g. (a,(),(((),()),()),(())) has args "a", "()", "(((),()),())", "(())"
     */

    public static Collection sepArgs(String sep, String Exp) {
        String exp = Exp;
        Vector result = new Vector();

        int parambegin = 0;
        String param = "";

        while (parambegin < exp.length()) {
            param = getFirstBlock(sep, exp.substring(parambegin));
            if (param != null) {
                if (exp.substring(parambegin, parambegin + param.length())
                        .trim() != "") {
                    //					System.out.println(exp.substring(parambegin,parambegin+param.length()));
                    result.add(exp.substring(parambegin,
                            parambegin + param.length()).trim());
                }
            } else {
                break;
            }

            //			remain part
            parambegin = parambegin + param.length() + sep.length();
        }

        if (param == null) {
            return null;
        } else {
            return result;
        }

    }

    /**
     * You can use it to get the first Paramter of expression, which seperated
     * in the highst level
     * 
     * @param sep
     *            seperator
     * @param paramseq
     *            parameter'sequence
     * @return first complete parameter <br>
     *         e.g. <br>
     *         "a,(),(((),()),()),(())" get "a" <br>
     *         "(((),()),()),(())" get "(((),()),())" <br>
     *         ""get ""<br>
     *         "((((),()),()),(())" (a "(" too much) get null, so you should
     *         give a well-formed entry <br>
     *         not trim() <br>
     */

    public static String getFirstBlock(String sep, String paramseq) {
        int i = 0;
        int j = 0;
        String lBrackets = "([{";
        String rBrackets = ")]}";
        int lBcnt = 0;
        int rBcnt = 0;

        if (paramseq == null) {
            return null;
        }
        //		otherweise
        for (; i < paramseq.length(); i++) {
            //			check if current char is a left bracket,
            if (lBrackets.indexOf(paramseq.charAt(i)) >= 0) {
                lBcnt++;
            }
            //			check if current char is a right bracket,
            if (rBrackets.indexOf(paramseq.charAt(i)) >= 0) {

                rBcnt++;
            }

            if (paramseq.indexOf(sep, j) == i) {
                j = i + sep.length();
                if (lBcnt == rBcnt) {
                    //					index of first complete parameter's end, plus 1(first
                    // index of sep)
                    return paramseq.substring(0, i);
                }
            }
        }
        //		it's end, check wether sth is wrong
        if (lBcnt == rBcnt) {
            //			all right , there is one parameter,
            return paramseq;
        } else {
            //			there is something not well-formed
            return null;
        }

    }

    /**
     * filtrate strings whiche matches pattern in expression
     * 
     * @param pattern :
     *            pattern of variable
     * @param Exp :
     *            a sentence
     * @return Hashtable of variables (String) <br>
     *         e.g. <br>
     *         get all arguments begin with alphabet in upcase <br>
     *         Args = filtrate("[A-Z][\\w]*",Exp); <br>
     */
    public static Hashtable filtrate(String pattern, Expression Exp) {
        return filtrate(pattern, Exp.toString());
    }

    /**
     * filtrate strings whiche matches pattern in expression
     * 
     * @param pattern :
     *            pattern of variable
     * @param Exp :
     *            a sentence
     * @return Hashtable of variables (String) <br>
     *         e.g. <br>
     *         get all arguments begin with alphabet in upcase <br>
     *         Args = filtrate("[A-Z][\\w]*",Exp); <br>
     */
    public static Hashtable filtrate(String pattern, String Exp) {
        return filtrate(pattern, Exp, 0);

    }

    /**
     * filtrate strings whiche matches pattern in expression
     * 
     * @param pattern :
     *            pattern of variable
     * @param Exp :
     *            a sentence
     * @param gNr :
     *            group number
     * @return Hashtable of variables (String) <br>
     *         e.g. <br>
     *         get all arguments begin with alphabet in upcase <br>
     *         Args = filtrate("[A-Z][\\w]*", Exp, 0); <br>
     */
    public static Hashtable filtrate(String pattern, Expression Exp, int gNr) {
        return filtrate(pattern, Exp.toString(), gNr);
    }

    /**
     * filtrate strings whiche matches pattern in expression
     * 
     * @param pattern :
     *            pattern of variable
     * @param Exp :
     *            a sentence
     * @param gNr :
     *            group number
     * @return Hashtable of variables (String) <br>
     *         e.g. <br>
     *         get all arguments begin with alphabet in upcase <br>
     *         Args = filtrate("[A-Z][\\w]*", Exp, 0); <br>
     */
    public static Hashtable filtrate(String pattern, String Exp, int gNr) {
        //Why Hashtable not String, because if ArgS_1_1 then ArgS_1 mybe not be
        // seen
        //	  Hashtable accept
        Hashtable ht_ac = new Hashtable();

        Pattern p_ac = Pattern.compile(pattern);
        Matcher m_ac = p_ac.matcher(Exp);

        for (int i = 0; m_ac.find(); i = m_ac.end()) {
            //		  if not regists in Hashtable accept & not except
            if (!ht_ac.containsKey(m_ac.group(gNr))) {
                //			  then it's a new one, regist it
                ht_ac.put(m_ac.group(gNr), new Integer(1));
            }

        }

        return ht_ac;

    }

    /*
     * public static Collection sort(Hashtable args){
     *  }
     */
    /**
     * Concatenate a collection of String with delimiter into one StringBuffer
     * 
     * @param delim
     *            delimiter
     * @param Args
     *            Collection
     * @return args' sequence in StringBuffer
     */
    public static StringBuffer Col2StringBuffer(String delim, Collection Args) {
        StringBuffer result = new StringBuffer();
        if (Args == null) {
            System.out.println("Args Null");
            return null;
        }
        Iterator it_Args = Args.iterator();
        if (it_Args.hasNext()) {
            result.append((String) it_Args.next());
        }
        while (it_Args.hasNext()) {
            result.append(delim + (String) it_Args.next());
        }
        return result;
    }

    /**
     * Concatenate a collection of String with delimiter into one String It will
     * call Col2StringBuffer()
     * 
     * @param delim
     *            delimiter
     * @param Args
     *            Collection
     * @return args' sequence in String
     */
    public static String Col2String(String delim, Collection Args) {
        StringBuffer result = Col2StringBuffer(delim, Args);
        return result.toString();
    }

    /**
     * convert binary operation such like A op B into func(A, B) in a String
     * 
     * @param op :
     *            binary operator name
     * @param func :
     *            new function name
     * @param exp :
     *            String to be changed
     * @return changed String <br>
     *         e.g binaryop2func("=", "equal", exp)
     */
    public static String binaryop2func(String op,String func, String exp) {
		
	  int i = 0;
	  int j = 0;
	  String lBrackets = "(";
	  String rBrackets = ")";
	  int lBcnt = 0;
	  int rBcnt = 0;
	  String left = "";
	  String right = "";
		
	  if(exp == null){
		  return null;
	  }
//	  otherweise
	  if(exp.indexOf(op) <=0){
		  return exp;
	  }else{
			
//		  left part
		  for(i=exp.lastIndexOf(op)-1;i>=0;i--){
//			  System.out.println(exp.charAt(i));
				
//			  check if current char is a left bracket,  
			  if(lBrackets.indexOf(exp.charAt(i))>=0){
				  lBcnt++; 	
			  }
//			  check if current char is a right bracket,  
			  if(rBrackets.indexOf(exp.charAt(i))>=0){
				  rBcnt++;
			  } 
				
			
			  if(lBcnt>rBcnt|| Pattern.matches("[,:]",""+exp.charAt(i)) && lBcnt==rBcnt){
				  left=exp.substring(i+1,exp.lastIndexOf(op));
				  break;
			  }

		  }
		  if(i<0){
			  left=exp.substring(0,exp.lastIndexOf(op));
		  }
			
//		  right part
		  lBcnt =0;
		  rBcnt =0;
		  for(i=exp.lastIndexOf(op)+op.length();i<exp.length();i++){
//			  System.out.println(exp.charAt(i));
//			  check if current char is a left bracket,  
			  if(lBrackets.indexOf(exp.charAt(i))>=0){
				  lBcnt++; 	
			  }
//			  check if current char is a right bracket,  
			  if(rBrackets.indexOf(exp.charAt(i))>=0){
				  rBcnt++;
			  } 
			
			  if(lBcnt<rBcnt|| Pattern.matches("[,:]",""+exp.charAt(i)) && lBcnt==rBcnt){
				  right=exp.substring(exp.lastIndexOf(op)+op.length(),i);
				  break;
			  }

		  }

		  if(i==exp.length()){
			  right=exp.substring(exp.lastIndexOf(op)+op.length(),i);
		  }
		  String regex=left+op+right;
		  String replacement =  func+"("+left.trim()+", "+right.trim()+")";
		  exp=exp.replaceFirst("\\Q"+regex+"\\E", replacement );
//		 System.out.println(exp);
		  return binaryop2func(op,func,exp);
	  }	

  }
}