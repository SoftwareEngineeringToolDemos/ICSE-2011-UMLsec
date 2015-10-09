package tum.umlsec.viki.tools.sequenceanalyser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.omg.uml.UmlPackage;
import org.omg.uml.behavioralelements.commonbehavior.CommonBehaviorPackage;
import org.omg.uml.behavioralelements.collaborations.CollaborationsPackage;

import org.omg.uml.behavioralelements.collaborations.Message;
import org.omg.uml.behavioralelements.collaborations.MessageClass;
import org.omg.uml.foundation.core.CorePackage;
import org.omg.uml.foundation.core.TagDefinition;
import org.omg.uml.foundation.core.TaggedValue;
import org.omg.uml.foundation.core.TaggedValueClass;

import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.mdr.IMdrContainer;

/**
 * @author Andreas Gilg
 *
 * Objekte der Klasse SeqMsg stellen eine Nachricht im Sequenzdiagramm dar.
 * Zus�tzlich sind Klassenmethoden enthalten um die Nachrichten bearbeiten zu k�nnen. 
 */
class SeqMsg {

	public String sender;
	public String receiver;
	public String functionName;
	public String message;
	public int no_messages;
	public Vector single_messages;
	public String condition;
	public Vector single_condition;
	public String all_conditions;
	public String all_messages;
	public int message_no;
	
    
	public SeqMsg() {
		// sender enthält Absender der Nachricht
		sender = "";
		// receiver enthält Empfänger der Nachricht
		receiver = "";
		// functionName enthlt den Funktions-Name
		functionName = "";
		// message enthÄlt Nachricht, wie sie im Sequenzdiagramm steht inkl. Bedingung
		message = "";
		// Anzahl der Einzelnachrichten/Teilnachrichten
		no_messages = 0;
		// Teilnachrichten in Vektor-Form
		single_messages = new Vector();
		// Bedingung extrahiert aus message
		condition = "";
		// Teilbedingungen in Vektor-Form
		single_condition = new Vector();
		// Alle Bedingungen verknÄpft, wie sie spÄter im TPTP-File stehen
		all_conditions = "";
		// Alle Teilnachrichten verknÄpft, wie sie spÄter im TPTP-File stehen
		all_messages = "";
		// Nachrichten-Nummer aus dem Diagramm
		message_no = -1;

	}


	/*
 	* Methode gibt fÄr Testzwecke alle Variablen der Klasse aus
 	*/
 	public void printMessage( ITextOutput _textOutput, Vector vars) {
		_textOutput.write(sender);
		_textOutput.write(" -> ");
		_textOutput.write(receiver);
		_textOutput.write(" : ");
		_textOutput.write(message);
		
		_textOutput.writeLn();
		_textOutput.writeLn("Funktionsname: " + functionName);
		Vector var = getVars(message);
		_textOutput.write("getVars: ");
			
		for ( int k = 0; k < var.size(); k++)
			_textOutput.write(k+": "+var.get(k)+" ; ");

		_textOutput.writeLn();

		var = getConstants(message);
		_textOutput.write("getConstants: ");
			
		for ( int k = 0; k < var.size(); k++)
			_textOutput.write(k+": "+var.get(k)+" ; ");

		_textOutput.writeLn();

		var = getVarsQuantified(condition);
		_textOutput.write("getVarsQuantified: ");
			
		for ( int k = 0; k < var.size(); k++)
			_textOutput.write(k+": "+var.get(k)+" ; ");
		_textOutput.writeLn();

		var = getVarsNotQuantified();
		_textOutput.write("getVarsNotQuantified: ");
			
		for ( int k = 0; k < var.size(); k++)
			_textOutput.write(k+": "+var.get(k)+" ; ");
		
		_textOutput.writeLn();
		_textOutput.writeLn();
		_textOutput.writeLn("Conditions: ");
		_textOutput.writeLn(all_conditions);
		_textOutput.writeLn("Message: ");
		_textOutput.writeLn(all_messages);
		_textOutput.writeLn();
		_textOutput.writeLn("Whole_Message: ");
		_textOutput.writeLn(message);
		_textOutput.writeLn();
		_textOutput.writeLn("Corrected_Message: ");
		_textOutput.writeLn(correctSyntaxOfVars(message, vars));
		_textOutput.writeLn();
		_textOutput.writeLn("Single_Condition: Anzahl="+single_condition.size());
		for ( int k = 0; k < single_condition.size(); k++)
			_textOutput.writeLn(k+": "+single_condition.get(k)+" ; ");
		_textOutput.writeLn();
		_textOutput.writeLn("MainMessage: Anzahl="+no_messages);
		for ( int k = 0; k < single_messages.size(); k++)
			_textOutput.writeLn(k+": "+single_messages.get(k)+" ; ");
		_textOutput.writeLn();
//		SequenceAnalyser seq = new SequenceAnalyser();
//		seq.testKlammer(message, _textOutput);

		_textOutput.writeLn();
	
	}

	/*
	 * Methode berechnet zusÄtzliche Klassen-Attribute, 
	 * die nicht direkt aus dem Sequenzdiagramm ermittelt werden kÄnnen
	 */
	public void makeSeqMsg(IMdrContainer mdrContainer){
		
		// Ermittle Bedingung
		SequenceAnalyser SA = new SequenceAnalyser();
		String guardNotation = SA.getGuardNotation(mdrContainer);
		
		if (guardNotation.equals("old"))
			condition = this.extractCondition();
		else
		{
			Vector ve = SA.getTaggedValue(mdrContainer, "guard_"+this.message_no);
			
			if (ve.size() >= 1)
			{
				condition = (String) ve.elementAt(0);
				// Entferne umschlieÄende Klammern
				condition = delBrackets(condition);
			}
		}
		
		
		single_messages = this.getMessages(this.extractMainMessage(), ",");
		no_messages = single_messages.size();
		single_condition = this.getMessages(this.condition, "&");
		functionName = extractFunctionName();
		
		// Ersetzungsregeln
		String s = "";
		

		// Ersetze in Bedingungen
		for ( int k = 0; k < single_condition.size(); k++)
		{
			// Bearbeite alle Bedingungen
			s = (String) single_condition.elementAt(k);
			
			s = replaceAll(s);

			// Speichere Daten in Vektor
			single_condition.setElementAt( s , k);
		}

		// Ersetze in Nachrichten
		for ( int k = 0; k < single_messages.size(); k++)
		{
			// Bearbeite alle Nachrichten
			s = (String) single_messages.elementAt(k);
			
			s = replaceAll(s);

			// Speichere Daten in Vektor
			single_messages.setElementAt( s, k);
		}


		// Setze Teilbedingungen wieder zusammen
		if (single_condition.size() == 0)
			all_conditions = "true";
		else
		{
			for ( int k = 0; k < single_condition.size(); k++)
			{
				s = (String) single_condition.elementAt(k);
			
				if ( !s.equals(""))
				{
					if ( all_conditions.equals("") )
						all_conditions = s;
					else
						all_conditions = all_conditions + " & " + s;
				}
			}
		}
		
		// Setze Teilnachrichten wieder zusammen
		for ( int k = single_messages.size()-1; k >= 0; k--)
		{
			s = (String) single_messages.elementAt(k);
			
			if ( !s.equals(""))
			{
				if ( all_messages == "" )
					all_messages = s;
				else
					all_messages = "conc( " + s + ",  " + all_messages + " )";
			}
		}
		
		 
	
	}


	/*
	 * Methode ersetzt a = b durch equal(a, b)
	 */

	public String replaceEqual(String s){
	
	
		// Ersetze " a = b " durch equal( a, b ) 
		if ( s.indexOf("=") == -1 )
			return s.trim();
		else 
		{
			String str = s.trim();
			String out = s;
			int left = 0;
			str = delBrackets(str);
			boolean notEqual = false;
			int len = str.length();
			int test = str.indexOf("=");
	
		
			// Falls ">" oder "<" vorkommen, breche ab
			if ( (str.indexOf(">") == test-1) || (str.indexOf(">") == test+1) )
				return s;
			if ( (str.indexOf("<") == test-1) || (str.indexOf("<") == test+1) )
				return s;
			if ( str.indexOf("!") == test-1 )
				notEqual = true;
			
			// Teste, ob Term in Quantifizierung oder mit &,| zusammengesetzt
			for (int i = 0; i < str.length(); i++)
			{
				// Term zusammengesetzt
				if ( (str.charAt(i) == '&') || (str.charAt(i) == '|') )
				{
					out = replaceEqual(str.substring(0, i)) + " " + str.charAt(i) + " " + replaceEqual(str.substring(i+1, str.length())); 
					return out;
				}
				// Term besitzt Quantifizierung
				else if ( str.charAt(i) ==':'  && (i < str.length()) && (str.charAt(i+1) != ':'))
				{
					out = "( " + replaceEqual(str.substring(0, i+1)) + " (" + replaceEqual(str.substring(i+1, str.length())) + ") )";
					return out;
				} 
				
			}
			
			// Erzeuge neuen String
			if (notEqual)
				out = "~equal( "+ str.substring(left, test-1).trim() + ", " + str.substring( test+1, len).trim() + " )";
			else
				out = "equal( "+ str.substring(left, test).trim() + ", " + str.substring( test+1, len).trim() + " )";
				
			
			// RÄckgabe
			return out;
		}
	}
	
	

/*
 * Methode ersetzt a :: b durch conc(a, b)
 */

public String replaceConc(String s){
	
	
	// Ersetze " a :: b " durch conc( a, b ) 
	if ( s.indexOf("::") == -1 )
		return s;
	else 
	{
		// LÄsche Leerzeichen und umschlieÄende Klammern
		s = s.trim();
		String str = delBrackets(s);
		boolean hadBrackets = false;
		
		if ( ! str.equals(s) )
			hadBrackets = true;
			 
		
		// Ermittle Position von "::"
		int len = str.length();
		int test = str.indexOf("::");
		int klammer = 0;
		String a = "";
		String b = "";
		int begin = 0;
		int end = 0;

		// Suche Teilstrings, die konkateniert werden sollen 
		for (int i = test; i >= 0; i--)
		{
			if ( str.charAt(i) == ')')
				klammer ++;
			else if ( str.charAt(i) == '(')
				klammer --;
			
			if ( (klammer < 0) || ( (i == 0)&& (klammer == 0)) || ( (str.charAt(i) == ',')&& (klammer==0)) )
			{
				if (str.charAt(i) == ',' || (str.charAt(i) == '(' && klammer < 0) )
				{
					a = str.substring( i+1, test);
					begin = i+1;
				}
				else
				{
					a = str.substring( i, test);
					begin = i;
				}
				break;
			}
		}
		
		klammer = 0;
		
		for (int i = test+2; i < len; i++)
		{
			if ( str.charAt(i) == ')')
				klammer ++;
			else if ( str.charAt(i) == '(')
				klammer --;
			
			if ( (klammer > 0) || ( (i == len-1)&& (klammer== 0)) || ( (str.charAt(i) == ',')&& (klammer==0)) )
			{
				if (str.charAt(i) == ',' || (str.charAt(i) == ')' && klammer > 0))
				{
					b = str.substring( test+2, i); 
					end = i-1;
				} 
				else
				{
					b = str.substring( test+2, i+1); 
					end = i;
				} 
				break;
			}
		}		
					
		// Erzeuge neuen String
		String out = (str.substring(0, begin).trim()+" conc( "+a.trim()+", "+b.trim()+" )"+str.substring(end+1, len).trim()).trim();
		
		// Ersetze weitere Vorkommen
		String s_neu = out;

		do{
			out = s_neu;
			s_neu = replaceConc(out);
		} while (!out.equals(s_neu));
		
		
		// Falls Eingabe umschlieÄende Klammern hatte, werden diese wieder hinzugefÄgt.
		if ( hadBrackets )
			out = "( " + out + " )";
			
		// RÄckgabe
		return out;
			
	}
}




	/*
	 * Funktion extrahiert Bedingung aus Nachricht (alte Notation)
	 */
	 public String extractCondition(){
 		
		String bed = message.trim();
		int left_bracket = 0;
		int right_bracket = 0 ;
		int begin = 0;
		int end = 0;
		
		
		// Falls Nachricht nicht mit "[" beginnt, muss keine Bedingung entfernt werden
		if (bed.charAt(0) == '[')
		{

			// Ermittle Beginn und Ende der Bedingung anhand der eckigen Klammern
			for ( int i=0; i < bed.length(); i++)
			{
				if (bed.charAt(i) == '[')
				{
					if ( left_bracket == 0)
						begin = i+1;
					left_bracket ++;
				}
				
				else if (bed.charAt(i) == ']')
				{
					right_bracket ++;
					if ( right_bracket == left_bracket)
						end = i-1;
				}	
				
			}
		}
		
		// extrahiere Bedingung, falls vorhanden
		if ( (right_bracket == left_bracket) && ( right_bracket != 0) && (left_bracket != 0) )
			bed = bed.substring(begin, end+1).trim(); 
		else 
			bed = ""; 

		// RÄckgabe
		return bed;
 	}


	/*
	 * Funktion extrahiert Hauptnachricht
	 */
	public String extractMainMessage() {
		
		
		String msg = message.trim();
		
		// Entferne Bedingung ( [...] ), verwende Rest als Hauptnachricht 
		int left_bracket = 0;
		int right_bracket = 0 ;
		int begin = 0;
		int end = 0;
		
		// Falls Nachricht nicht mit "[" beginnt, muss keine Bedingung entfernt werden
		if (msg.charAt(0) == '[')
		{
			// Ermittle Beginn und Ende der Bedingung anhand der eckigen Klammern
			for ( int i=0; i < msg.length(); i++)
			{
				if (msg.charAt(i) == '[')
				{
					if ( left_bracket == 0)
						begin = i+1;
					left_bracket ++;
				}
				
				else if (msg.charAt(i) == ']')
				{
					right_bracket ++;
					if ( right_bracket == left_bracket)
						end = i;
				}	
			
			}
			
			// Entferne Bedingung, falls vorhanden
			if ( (right_bracket == left_bracket) && ( right_bracket != 0)  )
				msg = msg.substring(end+1, msg.length()).trim(); 
		}
		

		// Ermittle Beginn der Hauptnachricht
		int test = msg.indexOf("(");
		
		// Falls Hauptnachricht gefunden, wird diese extrahiert
		if( test != -1)
		{
			msg = msg.substring(test+1);
			if ( msg.endsWith(")"))
				msg = msg.substring(0, msg.length()-1);
		}
	
		// RÄckgabe
		return msg;
	}



	/*
	 * Methode ersetzt a^-1 durch inv(a)
	 */

	public String replaceInv(String s){


		if ( s.indexOf("^-1") == -1 )
			return s;
		else 
		{
			// LÄsche Leerzeichen
			s = s.trim();
			
			int index = s.indexOf("^-1");
			String arg = "";

			
			for (int i = index-1; i >= 0; i--)
			{
				if ( s.substring(i, i+1).matches("\\w") )
					arg = s.charAt(i) + arg;
				else
					break;
			}
			
			if ( (arg.charAt(0) == '_') && (arg.length() > 1))
				arg = arg.substring(1);

			if ( arg.length() == 0)
				return s;
			else
				s = s.substring(0, index-arg.length()) + "inv(" + arg + ")" + s.substring(index+3);
			 
			
		}

		// Ersetze weitere Vorkommen
		String s_neu = s;

		do{
			s = s_neu;
			s_neu = replaceInv(s);
		} while (!s.equals(s_neu));

		//RÄckgabe
		return s;
	}
	
	/*
	* Funktion fÄhrt alle Ersetzungen durch
	* a = b => equal(a,b)
	* a::b	=> conc(a,b)
	* {E}_K => enc(E,K)
	* <E>_K => dec(E,K)
	* [E]_K => sign(E,K)
	* /E\_K => ext(E,K)
	* K^-1  => inv(K)
	*/

   public String replaceAll(String s){


	// Ersetze kryptographische Funktionen
	s = replaceInv(s);
	s = replaceKrypto(s, '[', "]_", "sign");
	s = replaceKrypto(s, '{', "}_", "enc");
	s = replaceKrypto(s, '<', ">_", "dec");
	s = replaceKrypto(s, '/', "\\_", "ext");
	
	//s = replaceConc(s); 
	s = points2conc(s); 
	s = replaceEqual(s);

	// Behebe Fehler von tptp2X
	s = delBlankBetweenFuncNameAndBracket(s);	

   return s;
   }



	
	/*
	 * Methode ersetzt kryptographische Funktionen 
	 * 
	 */

	public String replaceKrypto(String s, char begin, String end, String func_name){

		// Falls end nicht vorkommt, breche ab
		if ( s.indexOf(end) == -1 )
			return s;
		else 
		{
			// LÄsche Leerzeichen
			s = s.trim();
			
			// Suche Vorkommen von end
			int index = s.indexOf(end);
			String arg = "";
			String arg2 = "";

			// Finde erstes Argument
			for (int i = index-1; i >= 0; i--)
			{
				// Falls begin gefunden, breche ab
				if ( s.charAt(i) == begin )
				{	
					arg = s.charAt(i) + arg;
					break;
				}
				else
					arg = s.charAt(i) + arg;
			}
			
			
			//Finde zweites Argument 
			arg2 = findSndArg(s.substring(index+2));
			
			// Anfang und Ende korrekt erkannt
			if ( (arg.charAt(0) == begin) && (arg.length() > 1))
			{
				String s_neu = s.substring(0, index-arg.length()) + func_name + "(" + arg.substring(1) + "," + arg2 + ")" + s.substring(index+2+arg2.length());
				
				// Ersetze weitere Vorkommen
				do{
					s = s_neu;
					s_neu = replaceKrypto(s, begin, end, func_name);

				} while (!s.equals(s_neu));
				
				return s;			
			}
			// Fehler bei der Erkennung der Argumente, breche ab 
			else
				return s;			 
		}

	}
	
	/*
	 *  Funktion findet zweites Argument; Hilfsfunktion fÄr replaceKrypto
	 */
	 public String findSndArg(String s)
	 {
	 	
		String arg2= "";
		// Klammer1 == (
		int klammer1 = 0;
		// Klammer2 == {
		int klammer2 = 0;
		// Klammer3 == [
		int klammer3 = 0;
		// Klammer4 == <
		int klammer4 = 0;
		// Klammer5 == /
		int klammer5 = 0;
			
		for (int i = 0; i < s.length(); i++)
		{
			arg2 += s.charAt(i);

			if ( s.charAt(i) == ')')
				klammer1 --;
			else if ( s.charAt(i) == '(')
				klammer1 ++;
			else if ( s.charAt(i) == '{')
				klammer2 ++;
			else if ( s.charAt(i) == '}')
				klammer2 --;
			else if ( s.charAt(i) == '[')
				klammer3 ++;
			else if ( s.charAt(i) == ']')
				klammer3 --;
			else if ( s.charAt(i) == '<')
				klammer4 ++;
			else if ( s.charAt(i) == '>')
				klammer4 --;
			else if ( s.charAt(i) == '/')
				klammer5 ++;
			else if ( s.charAt(i) == '\\')
				klammer5 --;
			
			// Ende zweites Argument
			if ( (klammer1 < 0) || (klammer2 < 0) || (klammer3 < 0) || (klammer4 < 0) || (klammer5 < 0) || ( (i == s.length()-1) && (klammer1== 0)) || ( (s.charAt(i) == ',') && (klammer1==0)) )
				{
					if ( (s.charAt(i) == ',') || (s.charAt(i) == '}') || (s.charAt(i) == ']') || (s.charAt(i) == '>') || (s.charAt(i) == '\\') || ( (s.charAt(i) == ')') && (klammer1 < 0) ))
						arg2 = arg2.substring( 0, arg2.length()-1); 
					break;
				}
		}
	 	
	 	return arg2;
	 }


	/*
	 * Funktion extrahiert Funktionsname
	 */
	public String extractFunctionName() {
		
		
		String msg = message.trim();
		String name = "error";
		
		// Entferne Bedingung ( [...] ), verwende Rest als Hauptnachricht 
		int left_bracket = 0;
		int right_bracket = 0 ;
		int begin = 0;
		int end = 0;

		// Falls Nachricht nicht mit "[" beginnt, muss keine Bedingung entfernt werden
		if (msg.charAt(0) == '[')
		{
			// Ermittle Beginn und Ende der Bedingung anhand der eckigen Klammern
			for ( int i=0; i < msg.length(); i++)
			{
				if (msg.charAt(i) == '[')
				{
					if ( left_bracket == 0)
						begin = i+1;
					left_bracket ++;
				}
				
				else if (msg.charAt(i) == ']')
				{
					right_bracket ++;
					if ( right_bracket == left_bracket)
						end = i;
				}	
			
			}
			
			// entferne Bedingung, falls vorhanden
			if ( (right_bracket == left_bracket) && ( right_bracket != 0)  )
				msg = msg.substring(end+1, msg.length()).trim(); 
		}
						

		// Ermittle Beginn der Hauptnachricht
		int test = msg.indexOf("(");
		
		// Falls Hauptnachricht gefunden, wird diese entfernt
		if( test != -1)
			name = msg.substring(0, test).trim();
	
		// RÄckgabe
		return name;
	}


	/*
	 * Funktion extrahiert aus einer Nachricht ( x, y, z ) 
	 * alle Teil-Nachrichten x y z in Form eines Vectors
	 * Das Trennzeichen ( im Beispiel "," ) wird bei Funktionsaufruf Äbergeben
	 */
	public Vector getMessages( String message, String trennzeichen){
		
		Vector msgs = new Vector();
		String conc = "";
		int i = 0;
		int klammer_auf = 0;
		int klammer_zu = 0;
		// Zerlege Nachricht bei allen Trennzeichen. Falls zweiwertige Funktionen vorhanden sind, werden diese auch zwischen den Argumenten aufgeteilt
		String[] msg = message.split(trennzeichen);

		// Setze Funktionen innerhalb einer Nachricht wieder zusammen. 
		for ( int j = 0; j < msg.length; j++)
		{
			conc = conc + msg[j];
			klammer_auf=0;
			klammer_zu=0;

			int t = conc.length();
			
			// ZÄhle Äffnende und schlieÄende Klammern
			for (int m = 0; m < conc.length(); m++)
			{
				
				if ( conc.charAt(m) == '(')
					klammer_auf++;
				else if ( conc.charAt(m) == ')')
					klammer_zu++;
			}
			
			// Falls Anzahl Äffnender und schlieÄender Klammern gleich, ist Funktion vollstÄndig und wird im Vektor abgelegt 
			if (klammer_auf == klammer_zu)
			{
				if (!conc.equals(""))
				{
					msgs.add(conc.trim());
					i ++;
				}
				conc = "";				
			}
			// Falls nicht, wird der nÄchste Teil der Nachricht hinzugefÄgt und von vorne begonnen
			else if ( klammer_auf > klammer_zu )
				conc = conc +trennzeichen;
		}
		
		// Falls Klammerung falsch, speichere fehlerhaften String als letztes Element im Vektor
		if (!conc.equals(""))
		{
			msgs.add(conc.trim());
			i ++;
		}


			// RÄckgabe Vektor mit Einzelnachrichten
			return msgs;
	}	



	/*
	 * Funktion liefert alle Variablen eines Strings in Form eines Vectors zurÄck
	 */
	public Vector getVars( String msg){
		
		Vector vars = new Vector();
		String var = "";
	
	
		// Durchlaufe den String Zeichen fÄr Zeichen
		for ( int j = 0; j < msg.length(); j++)
		{
			// Falls eine Klammer geÄffnet wird, war der vorausgehende Teilstring ein Funktionsname
			if ( msg.charAt(j) == '(' )
				var = "";
			// Falls ein Komma oder eine schlieÄende Klammer auftaucht, handelt es sich bei dem Teilstring um ein Argument/Variable, diese wird im Vector abgelegt
			else if ( (msg.charAt(j) == ',') || (msg.charAt(j) == ')') )
			{
				var = var.trim();
	
				if ( !var.equals("") )
				{
					// FÄge nur ein, falls noch nicht vorhanden
					if ( !vars.contains(var) && var.matches("[A-Z]\\w*"))
						vars.add(var) ; 
					var = "";
				}
			}
			// Falls keiner der obigen FÄlle eintritt, wird das Zeichen gemerkt und mit dem nÄchsten Zeichen fortgefahren
			else
				var = var + msg.charAt(j);

		}

		if (!var.trim().equals(""))
		{
			var = var.trim();
	
			// FÄge nur ein, falls noch nicht vorhanden
			if ( !var.equals("") && !vars.contains(var) && var.matches("[A-Z]\\w*"))
				vars.add(var) ; 
		}
	
		// RÄckgabe, Vector mit Variablen ohne Doppelvorkommen	
		return vars;
	
	}
	
	
	/*
	* Funktion liefert alle Konstanten eines Strings in Form eines Vectors zurÄck
	*/
   public Vector getConstants( String msg){
		
	   Vector vars = new Vector();
	   String var = "";
	
	
	   // Durchlaufe den String Zeichen fÄr Zeichen
	   for ( int j = 0; j < msg.length(); j++)
	   {
		   // Falls eine Klammer geÄffnet wird, war der vorausgehende Teilstring ein Funktionsname
		   if ( msg.charAt(j) == '(' )
			   var = "";
		   // Falls ein Komma oder eine schlieÄende Klammer auftaucht, handelt es sich bei dem Teilstring um ein Argument/Variable, diese wird im Vector abgelegt
		   else if ( (msg.charAt(j) == ',') || (msg.charAt(j) == ')') )
		   {
			   var = var.trim();
	
			   if ( !var.equals("") )
			   {
				   // FÄge nur ein, falls noch nicht vorhanden
				   if ( !vars.contains(var) && var.matches("[a-z]\\w*"))
					   vars.add(var) ; 
				   var = "";
			   }
		   }
		   // Falls keiner der obigen FÄlle eintritt, wird das Zeichen gemerkt und mit dem nÄchsten Zeichen fortgefahren
		   else
			   var = var + msg.charAt(j);

	   }

	
	   // RÄckgabe, Vector mit Variablen ohne Doppelvorkommen	
	   return vars;
	
   }
	
   /*
   * Funktion Ändert GroÄ- und Kleinschreibung der Variablen und Konstanten
   * Konstanten werden klein geschrieben,
   * Variablen werden groÄ geschrieben.
   */
  public String correctSyntaxOfVars( String msg, Vector vars ){
		
	  int begin = 0;
	  int end = 0;
	  int last_cut = 0;
	  String var = "";
	  String result = ""; 
	
	
	  // Durchlaufe den String Zeichen fÄr Zeichen
	  for ( int j = 0; j < msg.length(); j++)
	  {
		  // Falls eine Klammer geÄffnet wird, war der vorausgehende Teilstring ein Funktionsname
		  if ( msg.charAt(j) == '(' || msg.charAt(j) == '[' )
		  {
		  	  var = "";
		  	  begin = j+1;
		  	  result += msg.substring(last_cut, j+1);
		  	  last_cut = j+1;
		  }
		  // Falls ein Komma oder eine schlieÄende Klammer auftaucht, handelt es sich bei dem Teilstring um ein Argument/Variable
		  else if ( (msg.charAt(j) == ',') || (msg.charAt(j) == ')') || (msg.charAt(j) == ']') )
		  {
			  var = var.trim();
			  end = j-1;
	
			  if ( !var.equals("") )
			  {
				  // Berichtige Schreibweise
				  if (vars.contains(var.toLowerCase()))
				  		var = var.toUpperCase().substring(0,1) + var.substring(1);
				  else
						var = var.toLowerCase().substring(0,1) + var.substring(1);
			  
				  result += " " + var + " " + msg.charAt(j);
				  var = "";
 			  }
 			  else
					result += msg.charAt(j);
 
			  last_cut = j+1;

		  }
		  // Falls keiner der obigen FÄlle eintritt, wird das Zeichen gemerkt und mit dem nÄchsten Zeichen fortgefahren
		  else
			  var = var + msg.charAt(j);

	  }

	  if ( !var.equals("") )
		{
			// Berichtige Schreibweise
			if (vars.contains(var))
				  var = var.toUpperCase();
				  		
			result += " " + var ;
				  
			var = "";
		}
	
	
	  // RÄckgabe, Vector mit Variablen ohne Doppelvorkommen	
	  return result;
	
  }
  
  
	/*
	* Funktion liefert alle Variablen, die quantifiziert sind, in Form eines Vectors zurÄck
	*/
   public Vector getVarsQuantified( String msg){
		
	   Vector vars = new Vector();
	   String var = "";
	   boolean start = false;
	
	
	   // Durchlaufe den String Zeichen fÄr Zeichen
	   for ( int j = 0; j < msg.length(); j++)
	   {
		   // Falls eine Klammer geÄffnet wird, beginnt der Variablenname
		   if ( msg.charAt(j) == '[' )
		   {
			   var = "";
			   start = true;
		   }
		   // Falls ein Komma oder eine schlieÄende Klammer auftaucht, handelt es sich bei dem Teilstring um ein Argument/Variable, diese wird im Vector abgelegt
		   else if ( start && ((msg.charAt(j) == ',') || (msg.charAt(j) == ']')) )
		   {
			   var = var.trim();

			   if ( !var.equals("") )
			   {
				   // FÄge nur ein, falls noch nicht vorhanden
				   if ( !vars.contains(var) && var.matches("[A-Z]\\w*"))
					   vars.add(var) ; 
				   var = "";
			   }
			   if (msg.charAt(j) == ']')
			   		start = false;
		   }
		   // Falls eine runde Klammer auftaucht, handelt es sich nicht um einen Quantifier
		   else if ( start && ((msg.charAt(j) == '(') || (msg.charAt(j) == ')')) )
		   {
		   		var = "";
		   		//start = false;
		   }
		   // Falls keiner der obigen FÄlle eintritt, wird das Zeichen gemerkt und mit dem nÄchsten Zeichen fortgefahren
		   else if (start)
			   var = var + msg.charAt(j);

	   }

	
	   // RÄckgabe, Vector mit Variablen ohne Doppelvorkommen	
	   return vars;
	
   }
   
   /*
   * Funktion liefert alle Variablen, die quantifiziert sind, in Form eines Vectors zurÄck
   * Dabei spielt GroÄ-/Kleinschreibung keine Rolle
   */
  public Vector getArgsQuantified( String msg){
		
	  Vector vars = new Vector();
	  String var = "";
	  boolean start = false;
	
	
	  // Durchlaufe den String Zeichen fÄr Zeichen
	  for ( int j = 0; j < msg.length(); j++)
	  {
		  // Falls eine Klammer geÄffnet wird, beginnt der Variablenname
		  if ( msg.charAt(j) == '[' )
		  {
			  var = "";
			  start = true;
		  }
		  // Falls ein Komma oder eine schlieÄende Klammer auftaucht, handelt es sich bei dem Teilstring um ein Argument/Variable, diese wird im Vector abgelegt
		  else if ( start && ((msg.charAt(j) == ',') || (msg.charAt(j) == ']')) )
		  {
			  var = var.trim();

			  if ( !var.equals("") )
			  {
				  // FÄge nur ein, falls noch nicht vorhanden
				  if ( !vars.contains(var) && var.matches("[A-Za-z]\\w*"))
					  vars.add(var) ; 
				  var = "";
			  }
			  if (msg.charAt(j) == ']')
				   start = false;
		  }
		  // Falls eine runde Klammer auftaucht, handelt es sich nicht um einen Quantifier
		  else if ( start && ((msg.charAt(j) == '(') || (msg.charAt(j) == ')')) )
		  {
			   var = "";
			   //start = false;
		  }
		  // Falls keiner der obigen FÄlle eintritt, wird das Zeichen gemerkt und mit dem nÄchsten Zeichen fortgefahren
		  else if (start)
			  var = var + msg.charAt(j);

	  }

	
	  // RÄckgabe, Vector mit Variablen ohne Doppelvorkommen	
	  return vars;
	
  }
	
   /*
	* Funktion ermittelt alle Variablen, die nicht quantifiziert sind. 
	*/
	
   public Vector getVarsNotQuantified( ){
		
	   Vector vars = new Vector();
	   Vector testVars = new Vector();
	   Vector testVarsQuant = new Vector();
		
	   // ÄberprÄfe Einzel-Bedingungen nach nicht quantifizierte Variablen
	   for ( int k = 0; k < single_condition.size(); k++)
	   {
		   testVars = getVars((String) single_condition.elementAt(k));
		   testVarsQuant = getVarsQuantified((String) single_condition.elementAt(k));
			
		   for (int i = 0; i < testVars.size(); i++)
		   {
			   if (!testVarsQuant.contains(testVars.elementAt(i)) && !vars.contains(testVars.elementAt(i)))
				   vars.addElement(testVars.elementAt(i));		
		   }
	   }

	   // ÄberprÄfe Einzel-Nachrichten nach nicht quantifizierte Variablen
	   for ( int k = 0; k < single_messages.size(); k++)
	   {
		   testVars = getVars((String) single_messages.elementAt(k));
		   testVarsQuant = getVarsQuantified((String) single_messages.elementAt(k));
			
		   for (int i = 0; i < testVars.size(); i++)
		   {
			   if (!testVarsQuant.contains(testVars.elementAt(i)) && !vars.contains(testVars.elementAt(i)))
				   vars.addElement(testVars.elementAt(i));		
		   }
	   }
		
	   // RÄckgabe		
	   return vars;
			
   }
   
   /*
   * Funktion ermittelt alle Variablen, die nicht quantifiziert sind. 
   * Eingabe: String
   */
	
  public Vector getVarsNotQuantified( String msg ){
		
	  Vector vars = new Vector();
	  Vector testVars = getVars(msg);
	  Vector testVarsQuant = getVarsQuantified(msg);
		
	  for ( int k = 0; k < testVars.size(); k++)
	  {
	  	String var = (String) testVars.elementAt(k);
	  	
	  	if ( !testVarsQuant.contains(var) && !vars.contains(var) )
				  vars.addElement(var);		
		  
	  }

	  // RÄckgabe		
	  return vars;
			
  }
	
	
	
	
	/*
	 * Methode testet String auf richtige Klammerung
	 * RÄckgabe:
	 * 0 korrekt geklammert
	 * <0 Anzahl schlieÄende Klammern zu groÄ
	 * >0 Anzahl Äffnender Klammern zu groÄ
	 * RÄckgabewert entspricht der Anzahl fehlender Klammern
	 **/
	 
	 int testKlammer(String str){
	 	
		int klammer = 0;
	 	
		for ( int i = 0; i < str.length(); i++){
	 		
			if (str.charAt(i) == '(')
				klammer++;
			else if ( str.charAt(i) == ')')
				klammer--;

			if ( klammer < 0)
				return klammer;
		}
	 	
		return klammer;
	 }	
	 
	 
	/*
	* Hilfsfunktion fÄr replaceEqual
	* Entfernt umschlieÄende Klammern, d.h. (a+b) -> a+b
	**/
	 
	String delBrackets(String str){
	 	
		if ( str.startsWith("(") && str.endsWith(")") )
		{
			int klammer = 0;
	 	
			for ( int i = 0; i < str.length(); i++){
	 		
				if (str.charAt(i) == '(')
					klammer++;
				else if ( str.charAt(i) == ')')
					klammer--;

				if ( (klammer == 0) && (i < str.length()-1) )
					return str;
					
			}
			
			if ( klammer == 0 )
				return str.substring(1, str.length()-1);
			else
				return str;
		}

		else return str;
	 	
	}


	/*
	* Funktion lÄscht Leerzeichen zwischen dem Funktionsnamen und der Äffnenden Klammer,
	* da dies bei der Konvertierung nach DFG einen Fehler verursacht.
	**/
	 
	String delBlankBetweenFuncNameAndBracket(String str){
	 	
		int i = 0;
		
		while (i < str.length()-1)
		{
	 		if (str.substring(i).matches("\\w +\\(.*"))
	 		{
	 			str = str.substring(0, i+1)+str.substring(i+2);
	 			i--;
	 		}
	 		i++;
		}
		
		return str;
	 	
	}
	
		
	/*
	* Hilfsfunktion fÄr replaceEqual
	* Ermittelt Endposition eines korrekt geklammerten Ausdrucks 
	**/
	 
	int getPosOfBracket(String str){
	 	
		int pos = str.length()-1;
		int klammer = 0;
	 	
		for ( int i = 0; i < str.length(); i++){
	 		
			if (str.charAt(i) == '(')
				klammer++;
			else if ( str.charAt(i) == ')')
				klammer--;

			if ( (klammer == 0) && (i < str.length()-1) )
					return i;
		}					
		
			
		return pos;

	}
	
	/*
	 * Funktion von Teng
	 * ersetzt :: durch conc(), setzt letztes Element der Liste auf eol
	 * zur Korrektur der head()-Axiome
	 */
	
	private static String points2conc(String exp) {
		 //System.out.println(exp);
		 int i = 0;
		 int j = 0;
		 String op = "::";
		 String func = "conc";
		 String lBrackets = "(";
		 String rBrackets = ")";
		 int lBcnt = 0;
		 int rBcnt = 0;
		 String left = "";
		 String right = "";

		 Pattern p = Pattern.compile("^conc\\(.*");
		 Matcher m;

		 if (exp == null) {
			 return null;
		 }
		 //	  otherweise
		 if (exp.indexOf(op) <= 0) {
			 return exp;
		 } else {

			 //		  left part
			 for (i = exp.lastIndexOf(op) - 1; i >= 0; i--) {
				 //			  System.out.println(exp.charAt(i));

				 //			  check if current char is a left bracket,
				 if (lBrackets.indexOf(exp.charAt(i)) >= 0) {
					 lBcnt++;
				 }
				 //			  check if current char is a right bracket,
				 if (rBrackets.indexOf(exp.charAt(i)) >= 0) {
					 rBcnt++;
				 }

				 if (lBcnt > rBcnt
						 || Pattern.matches("[,:]", "" + exp.charAt(i))
						 && lBcnt == rBcnt) {
					 left = exp.substring(i + 1, exp.lastIndexOf(op));
					 break;
				 }

			 }
			 if (i < 0) {
				 left = exp.substring(0, exp.lastIndexOf(op));
			 }

			 //		  right part
			 lBcnt = 0;
			 rBcnt = 0;
			 for (i = exp.lastIndexOf(op) + op.length(); i < exp.length(); i++) {
				 //			  System.out.println(exp.charAt(i));
				 //			  check if current char is a left bracket,
				 if (lBrackets.indexOf(exp.charAt(i)) >= 0) {
					 lBcnt++;
				 }
				 //			  check if current char is a right bracket,
				 if (rBrackets.indexOf(exp.charAt(i)) >= 0) {
					 rBcnt++;
				 }

				 if (lBcnt < rBcnt
						 || Pattern.matches("[,:]", "" + exp.charAt(i))
						 && lBcnt == rBcnt) {
					 right = exp.substring(exp.lastIndexOf(op) + op.length(), i);
					 break;
				 }

			 }

			 if (i == exp.length()) {
				 right = exp.substring(exp.lastIndexOf(op) + op.length(), i);
			 }
			 String regex = left + op + right;

			 m = p.matcher(right.trim());
			 String replacement = "";

			 if (m.matches()) {
				 //System.out.println("not last atom!");
				 replacement = func + "(" + left.trim() + ", " + right.trim()
						 + ")";
			 } else {
				 //System.out.println("last atom!");
				 //System.out.println("right:" + right);
				 replacement = func + "(" + left.trim() + ", " + "conc("
						 + right.trim() + ", eol))";
			 }
			 exp = exp.replaceFirst("\\Q" + regex + "\\E", replacement);
            
			 exp = points2conc(exp);
            
			 return exp ;
		 }

	 }
	
	
}	


/**
 * @author Andreas Gilg
 *
 * Die Klasse SequenceAnalyser enthÄlt alle Funktionen, die fÄr die Funktionsweise 
 * des Tools gebraucht werden.
 */
public class SequenceAnalyser {
	
	// Klassenvariablen dienen zur Konfiguration
	
	// Konfiguration fÄr Windows 
	String cmd = "run-e-setheo";
	String cmdSpass = "c:\\programme\\SPASS\\SPASS.exe";
	String cmdTptp2x = "c:\\programme\\TPTP-v2.7.0\\TPTP2X\\tptp2X.main";
	String cmdpl = "c:\\programme\\pl\\bin\\plwin.exe";

	// Konfiguration fÄr Linux
//	String cmd1 = System.getProperty("user.dir")+File.separator+"run-e-setheo";
//	String cmdSpass1 = System.getProperty("user.dir")+File.separator+"SPASS";
//	String cmdTptp2x1 = System.getProperty("user.dir")+File.separator+"tptp2X";

	String cmd1 = "/opt/e-setheo/bin/run-e-setheo";
	String cmdSpass1 = "SPASS";
	String cmdTptp2x1 = "/opt/TPTP-v2.7.0/TPTP2X/tptp2X";


	// TemporÄrer Pfad zum Ablegen der TPTP-Datei
	//String tmpPath = "c:\\temp\\";
	//String tmpPath = System.getProperty("java.io.tmpdir");
	String tmpPath;

	SequenceAnalyser() {
		if (System.getProperty("os.name").matches("Windows.*")) {
			tmpPath = System.getProperty("java.io.tmpdir");
		} else {
			tmpPath = System.getProperty("java.io.tmpdir") + File.separator;
		}
	}

	// ZeitbeschrÄnkung fÄr e-setheo
	String maxTime = "20";

	// Ermittelt alle Nachrichten, die im vorliegenden Sequenzdiagramm verschickt werden
	public SeqMsg[] messages(IMdrContainer _mdrContainer, ITextOutput _mainOutput) {

	
	UmlPackage root = _mdrContainer.getUmlPackage();
	CorePackage corePackage = root.getCore();
	CommonBehaviorPackage commonbehavior = (CommonBehaviorPackage)root.getCommonBehavior();
	CollaborationsPackage collaborations;
	collaborations=root.getCollaborations();
	//StimulusClass stimulusClasses = (StimulusClass)commonbehavior.getStimulus();
	MessageClass messageClasses=collaborations.getMessage();
	
	
		// Ermittle Anzahl der Messages
		int n = 0;
		for (Iterator it_mesg_I = messageClasses.refAllOfClass().iterator(); it_mesg_I.hasNext();) {
			Message sti = (Message)it_mesg_I.next();
			n++;
		}

		// Initialisiere Array von SeqMsg
		SeqMsg[] messages = new SeqMsg[n];
		for (int m = 0; m < n; m++) {
			messages[m] = new SeqMsg();
		}
		
		// Speichere Messages
		for (Iterator it_mesg_I = messageClasses.refAllOfClass().iterator(); it_mesg_I.hasNext();) {
			Message mesg = (Message)it_mesg_I.next();
			int m = Integer.parseInt(mesg.getName());
			messages[m-1].message_no = m;
			messages[m-1].sender = mesg.getSender().getName();
			messages[m-1].receiver = mesg.getReceiver().getName();
			messages[m-1].message = mesg.getAction().getName();
			messages[m-1].makeSeqMsg(_mdrContainer);
			
			}
		
		// RÄckgabewert		
		return messages;
	}


/*
 *  Methode korrigiert Schreibweise von Variablen und Konstanten
 */
public void correctVarsForDummies(SeqMsg[] messages, IMdrContainer _mdrContainer){
		
	
	// Variablen fÄr Dummies
	String varsForDummies = getVarsForDummies(_mdrContainer);

		// Falls true korrigiere Schreibweisen
		if (varsForDummies.equals("true"))
		{
			Vector v = getAllVariables(messages, _mdrContainer);
			
			// Korrigiere fÄr jede Nachricht alle mÄglichen Strings 
			for (int i = 0; i < messages.length; i++)
			{
				messages[i].message = messages[i].correctSyntaxOfVars(messages[i].message, v);
				messages[i].condition = messages[i].correctSyntaxOfVars(messages[i].condition, v);
				messages[i].all_conditions = messages[i].correctSyntaxOfVars(messages[i].all_conditions, v);
				messages[i].all_messages = messages[i].correctSyntaxOfVars(messages[i].all_messages, v);
			
				for (int m = 0; m < messages[i].single_messages.size(); m++)
					messages[i].single_messages.setElementAt( messages[i].correctSyntaxOfVars(( (String) messages[i].single_messages.elementAt(m)), v), m);

				for (int m = 0; m < messages[i].single_condition.size(); m++)
					messages[i].single_condition.setElementAt( messages[i].correctSyntaxOfVars(( (String) messages[i].single_condition.elementAt(m)), v), m);
					
			}
		}
}


/*
 *  Funktion testet alle Nachrichten auf richtige Klammerung
 *  und gibt alle falsch geklammerten Nachrichten in einem String aus.
 */
	public String testBracketsAllMessages(SeqMsg[] messages){
		
		String out = "";
		int k = 0;
		
		// Teste alle Messages auf richtige Klammerung
		for (int i = 0; i < messages.length; i++)
		{
			k = messages[i].testKlammer(messages[i].message);
			if (k < 0)
				out += "% Message "+ (i+1) +" contains too many closing brackets !!\n";
			else if (k > 0)
				out += "% Message "+ (i+1) +" contains too many opening brackets !!\n";
		}
		

	 	if ( !out.equals(""))
			out = "% Warning: Wrong number of brackets in messages: \n" + out + "\n";

		return out;
	
	}
	
	/*
	 * Methode gibt fÄr Testzwecke alle Nachrichten aus.
	 */
	
	public void printMessages(SeqMsg[] messages, ITextOutput _mainOutput, IMdrContainer _mdrContainer){

		Vector v = getAllVariables(messages, _mdrContainer);

	
		// Gebe Messages aus
		for( int m = 0; m < messages.length; m++ ) {
			_mainOutput.writeLn("Nachricht "+ messages[m].message_no);
			messages[m].printMessage(_mainOutput, v);
		}
		
		// Gebe Sender aus
		_mainOutput.writeLn();

		v = getAllSenders(messages);
		
		for( int m = 0; m < v.size(); m++ ) {
			_mainOutput.write("Sender "+ (m+1));
			_mainOutput.writeLn((String)v.elementAt(m));
		}

		// Gebe EmpfÄnger aus
		_mainOutput.writeLn();

		v = getAllReceivers(messages);
		
		for( int m = 0; m < v.size(); m++ ) {
			_mainOutput.write("EmpfÄnger "+ (m+1));
			_mainOutput.writeLn((String)v.elementAt(m));
		}

		// Gebe alle Variablen aus
		_mainOutput.writeLn();

		v = getAllVariables(messages, _mdrContainer);
		
		for( int m = 0; m < v.size(); m++ ) {
			_mainOutput.write("Var_"+ (m+1)+ ": ");
			_mainOutput.writeLn((String)v.elementAt(m));
		}

		_mainOutput.writeLn();
		_mainOutput.writeLn();

		_mainOutput.writeLn("OS: "+ System.getProperty("os.name"));
		// Fertig
		_mainOutput.writeLn("Finished.");
	
	}

	public void runESetheo(IMdrContainer _mdrContainer, ITextOutput _mainOutput, boolean status){
	
			// Variablen initialisieren
			SeqMsg[] messages = messages( _mdrContainer, _mainOutput);
			String line = null;
			Vector output = new Vector();
			String out = "";
			String error = "";
			String fileName = "test";
			String argNotation = getArgNotation(_mdrContainer);
		
			// Erzeuge TPTP-Datei
			String tptp = getTPTP(messages, _mdrContainer);
			fileName = writeString(tptp, tmpPath, fileName, "tptp", _mainOutput);
			
			// Teste Nachrichten auf richtige Klammerung
			out += testBracketsAllMessages(messages);
			// Teste Variablen-Konsistenz
			out += checkVarsString(messages, argNotation);
			// Teste Klammerung der tptp-Datei
			out += testKlammer(tptp);

		
			// Aufruf e-setheo
			if ( !fileName.equals(""))
			{
				// Erzeuge Befehls-Kommando fÄr e-setheo
				String command1 = cmd + " " + tmpPath + fileName + " " + maxTime;
				String command2 = cmd1 + " " + tmpPath + fileName + " " + maxTime;
				//_mainOutput.writeLn(command);
		
				// Betriebssystemaufruf
				output = systemCall(command1, command2);
			
				// Ermittle Ergebnis aus Standardausgabe
				for ( int j = 0; j < output.size(); j++){
			
					if (status)
						out += output.elementAt(j) + "\n";
					else
						if ( ((String)output.elementAt(j)).matches(".*has status.*") || ((String)output.elementAt(j)).matches("Failure.*"))
							out += output.elementAt(j) + "\n";
					
				}
		
				if (!status)
					out = "E-SETHEO's result:\n" + out;
				
				// Gebe Ergebnis aus
				displayString (out, _mainOutput);
			
				// LÄsche Datei
				deleteFile( tmpPath, fileName, _mainOutput);
			}
		}

	/*
	 * Funktion fÄhrt einen Betriebssystemaufruf durch mit dem Kommando command1, 
	 * fÄhrt dies zu einem Fehler wird command2 aufgrufen.
	 */
	
	Vector systemCall(String command1, String command2){
	
		Vector output = new Vector();
		String line = null;
	
		try {
				Process p = Runtime.getRuntime().exec(command1);
				BufferedReader lsOut = new BufferedReader(new InputStreamReader (p.getInputStream()));
			
				while ( (line = lsOut.readLine()) != null){
					output.addElement(line);			
				}
			
			} catch (Exception e) {
	
				if (command2.equals(""))
					output.addElement("Failure: " + e);
				else
				{
					try {
						Process p = Runtime.getRuntime().exec(command2);
						BufferedReader lsOut = new BufferedReader(new InputStreamReader (p.getInputStream()));
			
						while ( (line = lsOut.readLine()) != null){
							output.addElement(line);			
						}
					
					} catch (Exception e1) {
						output.addElement("Failure: " + e1);
						}
				}
				}
				
				return output;
	}

	public void runSpass(IMdrContainer _mdrContainer, ITextOutput _mainOutput, boolean status){
	
			// Variablen initialisieren
			SeqMsg[] messages = messages( _mdrContainer, _mainOutput);
			String line = null;
			Vector output = new Vector();
			String out = "";
			String error = "";
			String fileName = "test";
			String dfgFileName = "";
			String plProg = "";
			String plFileName= "";
			String argNotation = getArgNotation(_mdrContainer);
		
			// Erzeuge TPTP-Datei
			String tptp = getTPTP(messages, _mdrContainer);
			fileName = writeString(tptp, tmpPath, fileName, "tptp", _mainOutput);
		
			// Teste Nachrichten auf richtige Klammerung
			out += testBracketsAllMessages(messages);
			// Teste Variablen-Konsistenz
			out += checkVarsString(messages, argNotation);
			// Teste Klammerung der tptp-Datei
			out += testKlammer(tptp);
		
			// Konvertiere TPTP nach DFG
			if (!fileName.equals("") && testFile(tmpPath+fileName))
			{
				if (System.getProperty("os.name").matches("Windows.*"))
				{
					plProg = ":- ['"+cmdTptp2x.replaceAll("\\\\","\\\\\\\\")+"'].\n";
					// @bugfix buerger
					// corrected the command so that prolog finds the files
					plProg += ":- tptp2X('"+tmpPath.replaceAll("\\\\","/")+fileName+"',100,none,dfg,'"+tmpPath.replaceAll("\\\\","/")+"').\n";
					// OLD:
					// plProg += ":- tptp2X('"+tmpPath.replaceAll("\\\\","\\\\\\\\")+fileName+"',100,none,dfg,'').\n";
					plProg += ":- halt.\n";
					plFileName = writeString(plProg, tmpPath, "convert", "pl", _mainOutput);
					if (!plFileName.equals("") && testFile(tmpPath+plFileName))
					{
						String command = cmdpl + " " + tmpPath+plFileName;
						systemCall(command, "");
						
						// Teste ob Konvertierung erfolgreich
						dfgFileName = fileName.substring(0,(fileName.length()-4))+"dfg";
			
						if (!testFile(tmpPath+dfgFileName))
						{
							dfgFileName = "";
							_mainOutput.writeLn("Failure: Error converting  TPTP-File to DFG!");
						}
					}
				}
				else
				{
					String command = cmdTptp2x1 + " -f dfg -d " + tmpPath + " " + tmpPath+fileName;

					systemCall(command, "");

					// Teste ob Konvertierung erfolgreich
					dfgFileName = fileName.substring(0,(fileName.length()-4))+"dfg";
					if (!testFile(tmpPath+dfgFileName))
					{
						dfgFileName = "";
						_mainOutput.writeLn("Failure: Error converting TPTP-File to DFG!");
					}
				}
			}
		
			// Aufruf SPASS
			if ( !dfgFileName.equals("") && testFile(tmpPath+dfgFileName))
			{
				// Korrigiere TPTP2x-Fehler
				correctTptp2xError( dfgFileName, tmpPath, _mainOutput);
				
				// Erzeuge Befehls-Kommando fÄr SPASS
				String command1 = cmdSpass  + " " + tmpPath + dfgFileName;
				String command2 = cmdSpass1 + " " + tmpPath + dfgFileName;
				//_mainOutput.writeLn(command1);
				// Betriebssystemaufruf
				output = systemCall(command1, command2);
						
				// Ermittle Ergebnis aus Standardausgabe
				for ( int j = 0; j < output.size(); j++){
			
					if (status)
						out += output.elementAt(j) + "\n";
					else
						if ( ((String)output.elementAt(j)).matches(".*SPASS.*") || ((String)output.elementAt(j)).matches("Failure.*"))
							out += output.elementAt(j) + "\n";
					
				}
		
				
				// Gebe Ergebnis aus
				displayString (out, _mainOutput);
							
			}
			// LÄsche TPTP-Datei
			if (!fileName.equals(""))
				deleteFile( tmpPath, fileName, _mainOutput);
						
			// LÄsche DFG-Datei
			if (!dfgFileName.equals(""))
				deleteFile( tmpPath, dfgFileName, _mainOutput);
		
			// LÄsche PL-Datei
			if (!plFileName.equals(""))
				 deleteFile( tmpPath, plFileName, _mainOutput);
			
		}
/*
 * Funktion testet, ob die Datei str existiert.
 */
 
 boolean testFile(String str){
 	
 	File test = new File(str);
 	
 	return test.exists();
 }


	/*
	 * Methode liefert TPTP Eingabe fÄr ATP als Ausgabe auf Console/GUI
	 */
	public String getTPTP(SeqMsg[] messages, IMdrContainer _mdrContainer){
		
		// Ermittle, ob Reihenfolge der Nachrichten im Protokoll beachtet wird.
		// D.h Verschachtelung der Implikationen ja/nein
		String order = getOrder(_mdrContainer);
		Vector msg_order_false = getMsgOrderFalse(_mdrContainer);
		
		// Variablen fÄr Dummies
		correctVarsForDummies(messages, _mdrContainer);

		// Erzeuge Argumente/Variablen
		String argNotation = getArgNotation(_mdrContainer);
		Vector args = getAllArguments(messages, _mdrContainer);
		
		// Teste Nachrichten auf richtige Klammerung
		String out = testBracketsAllMessages(messages);
		// Teste Variablen-Konsistenz
		out += checkVarsString(messages, argNotation);
		
		// ATP Rules
		out += testKlammer(getATPRules(messages, _mdrContainer));
		out += getATPRules(messages, _mdrContainer);
		
		int i = 0;
		
		// Ermittle Sendernamen
		Vector v = getAllSenders(messages);
		
		// Facts
		//out +=  testKlammer(getFacts(_mdrContainer));
		out += getFacts(_mdrContainer);

		// Initial Knowledge
		//out +=  testKlammer(getInitialKnowledge(_mdrContainer));
		out += getInitialKnowledge(_mdrContainer);
		
		// Beginn
		out += "%----------------------- Main Protocol Specification ---------------------------\n";
		out += "\n";
		out += "input_formula(protocol,axiom,(\n";
		
		// Ermittle alle Variablen, die ALL-quantifiziert werden
		for ( int m = 0; m < args.size(); m++)
		{
			if ( m == 0 )
				out += " ![" + args.get(m);
			else
				out += ", " + args.get(m);
				
		}
			
		if ( args.size() > 0)
			out += "] : (\n";
			
		// For-Schleife Äber alle Sender
		for ( int s = 0; s < v.size(); s++ ){
		
			String current_sender = (String) v.elementAt(s);	
		
			// FÄge Kommentar hinzu und VerknÄpfung (&) zwischen den einzelnen Nachrichten
			if ( s == 0)
				out += " % " + current_sender + " -> Attacker\n" ;
			else
				out += " &  % " + current_sender + " -> Attacker\n";


			out += "( " + getTPTPBySender(messages, current_sender, argNotation, order, msg_order_false) + "\n)\n";
	}
			
		
		//SchlieÄe Klammer
		if ( args.size() > 0)
			out += " ) ";
			
			
		out += " ) ).\n";
			
		// Angriff
		out += "\n";
		out += getAttack(_mdrContainer);
					
		// Fertig
		out += "\n";
	
		// Teste richtige Klammerung der TPTP-Datei
		out += testKlammer(out);
		out += "% Finished\n";
	
	
		// RÄckgabe
		return out;		
	}
	
	
	/*
	* Hilfsfunktion fÄr getTPTP. Liefert Teil der TPTP-Ausgabe fÄr einen bestimmten Sender
	* 
	*/
		 
   public String getTPTPBySender(SeqMsg[] messages, String currentSender, String argNotation, String order, Vector msg_order_false){ 
	
	   String args = "";
	   String args_to_add = "";
	   boolean first_unordered = false;
	   int i = 0;
	   String out = "";
	
	   // For-Schleife Äber alle Nachrichten
	   for( int m = 0; m < messages.length; m++ ) {
	
			// Falls aktuelles Objekt der Absender der aktuellen Nachricht ist	
		   if (messages[m].sender.equals(currentSender))
		   {
				i++;
				args = getArgumentsString(messages, m, argNotation, false);

				// Falls von einer vorherigen Nachricht noch Argumente vorhanden sind, fÄge diese hinzu
				if (!args_to_add.equals(""))
				{
					args = args_to_add + " & " + args;
					args_to_add = "";
				}
	
				// & VerknÄpfung				
			   if ( i != 1)
			   {
				   if ( order.equals("false") || msg_order_false.contains( Integer.toString(m+1)))
						i --;
			   			
				   out += tab(i) + " & ( ( ";
			   }
			   else
			   {
			   	    out += "\n"+ tab(i) + "   ( ( ";
			   	    // Behebe Fehler falls erste betrachtete Nachricht ungeordnet verschickt wird
					if ( order.equals("true") && msg_order_false.contains( Integer.toString(m+1)))
	   					first_unordered = true;
			   }
			   					
			   // Argumente aus vorherigen Nachrichten
			   out +=  args + "\n";
						
			   // Bedingung der Nachricht
				out +=  tab(i) + "     & " + messages[m].all_conditions + " )\n";
				
			   // Conclusion
			   if ( !messages[m].all_messages.equals(""))
					out +=  tab(i) + "    => knows(" + messages[m].all_messages + ")\n";
			   else
				   out +=  tab(i) + "    => true\n";
	   			   
				// Falls Reihenfolge nicht berÄcksichtigt werden soll, schlieÄe Klammer
				if ( order.equals("false") || msg_order_false.contains( Integer.toString(m+1)) )
					out += tab(i) + "   )\n";

		   }
		   // Falls Nachricht empfangen und keine Nachricht verschickt wird
		   else if (messages[m].receiver.equals(currentSender))
		   {
		   		// Ermittle, ob keine Nachricht verschickt wird
				boolean noSend = true;
		   		
				for (int k = m+1;  k < messages.length; k++ )
				{
					if (messages[k].sender.equals(currentSender))
					{
							noSend = false;
							break;
					}						
					else if (messages[k].receiver.equals(currentSender))
							break;
				}
		   	 	
		   	 	// Falls keine Nachricht verschickt wird
				if (noSend)
				{
					// Falls Nachrichten nicht geordnet verschickt werden, sammle Argumente und fÄge diese spÄter hinzu
					if (order.equals("false") || msg_order_false.contains( Integer.toString(m+1)))
					{
						if (args_to_add.equals(""))
							args_to_add = getArgumentsString(messages, m, argNotation, true);
						else
							args_to_add = args_to_add + " & " + getArgumentsString(messages, m, argNotation, true);
					}
					else
					{
						i++;
						args = getArgumentsString(messages, m, argNotation, true);
	
						// Falls von einer vorherigen Nachricht noch Argumente vorhanden sind, fÄge diese hinzu
						if (!args_to_add.equals(""))
						{
							args = args_to_add + " & " + args;
							args_to_add = "";
						}
			
						// & VerknÄpfung				
					   if ( i != 1)
					   {
						   if ( order.equals("false"))
								i --;
				   			
						   out += tab(i) + " & ( ( ";
					   }
					   else
						   out += "\n"+ tab(i) + "   ( ( ";
						
					   // Argumente aus  Nachrichten
					   out +=  args + "\n";
							
					   // Bedingung der Nachricht
					   out +=  tab(i) + "     & true )\n";
					
					   // Conclusion
					   out +=  tab(i) + "    => true\n";
		   			   
						// Falls Reihenfolge nicht berÄcksichtigt werden soll, schlieÄe Klammer
						if ( order.equals("false") )
							out += tab(i) + "   )\n";
					}
				}
		   }


		}
		
		// Falls noch Argumente vorhanden sind, fÄge diese jetzt hinzu
		if (!args_to_add.equals(""))
		{
			if ( i != 0)
					out += tab(i) + " & ( ( ";
			else
				out += "\n"+ tab(i) + "   ( ( ";
							
			out +=  args_to_add + "\n";
			out +=  tab(i) + "     & true )\n";
			out +=  tab(i) + "    => true\n";
			out +=  tab(i) + "   )\n";
			args_to_add = "";
		}

		   			   
		// schlieÄende Klammern, falls Reihenfolge berÄcksichtigt wird
		if (order.equals("true"))
		{
			if (first_unordered)
				i--;
				
			for (int j = i; j > 0; j--)
				out += tab(j) + "   )\n";
		}

	   return out;
   }
   
   
   
 
  
  /*
  * Hilfsfunktion fÄr getTPTPbySender. Liefert Teil der TPTP-Ausgabe fÄr eine bestimmte Nachricht
  * 
  */
 public String getTPTPByMsg(SeqMsg[] messages, int m, int i, boolean isReceiver, String argNotation, String order, boolean withTrue){ 
	
	 String args = getArgumentsString(messages, m, argNotation, isReceiver);
	 String condition = messages[m].all_conditions;
	 String message = messages[m].all_messages;
	 String out = "";
	 
	 // Falls keine Nachricht verschickt wird, ist Bedingung und Nachricht "true"
	 if (isReceiver)
	 {
	 	condition = "true";
	 	message = "true";
	 }
	
	 
	 if ( args.equals("true") && messages[m].all_conditions.equals("true") && !withTrue) 
			out += tab(i) + "   ( ( ";
	 else
			out += tab(i) + "   ( ";

 
					
	 // Argumente aus vorherigen Nachrichten
	 if (!args.equals("true") || withTrue)
		   out +=  args + "\n";
		   
	if ( args.equals("true") && messages[m].all_conditions.equals("true") && !withTrue) 
		out +=  tab(i) + "     & ";
	else
		   out += " )\n";
	
						
	 // Bedingung der Nachricht
	 if (!messages[m].all_conditions.equals("true") || withTrue)
	 {
	 		if (args.equals("true") && !withTrue)
	 			   out +=  tab(i) + "     & " + messages[m].all_conditions ;
	 		else
					out +=  tab(i) + "       " + messages[m].all_conditions ;

	 }
	 

	if ( args.equals("true") && messages[m].all_conditions.equals("true") && !withTrue) 
		   out += " \n";
	else
		   out += " )\n";

				
	 // Conclusion
	 if ( !messages[m].all_messages.equals(""))
			out +=  tab(i) + "    => knows(" + messages[m].all_messages + ")\n";
	 else
			out +=  tab(i) + "    => true\n";
	   			   
	  // Falls Reihenfolge nicht berÄcksichtigt werden soll, schlieÄe Klammer
	  if ( order.equals("false") )
			  out += tab(i) + "   )\n";


	 return out;
 }
 
 
	
	/*
	 * Hilfsfunktion zum EinrÄcken des Ausgabetextes. 
	 */
	String tab (int i ){
		
		String out = "";
		
		for (int j = 0; j < i; j++){
		
		out += "  ";
		
		}
		
		return out;
	}
	
	/*
	 * Funktion ermittelt die Bezeichnung der Argumente, die mit der letzten Nachricht empfangen wurde.
	 * argNotation "old" :
	 * ArgX_A_B steht fÄr b. Argument in der a. Nachricht die X empfangen hat.
	 * argNotation "new" :
	 * msg_1 bedeutet 1. Argument der Nachricht mit dem Namen "msg"
	 */
	Vector getArgumentsOfPreviousReceivedMsg(SeqMsg[] messages, int current, String argNotation){

		Vector args = new Vector();
		String sender = messages[current].sender;
		
		// Ermittle vorhergehende empfangene Nachricht
		for ( int k = current-1; k >= 0; k-- )
		{
			if ( messages[k].receiver.equals(sender) )
			{	
				args = getArgumentsOfCurrentMsg(messages, k, argNotation);
				break;
			}
			else if ( messages[k].sender.equals(sender) )
				break;  
		}
	
		
		// RÄckgabe
		return args;
	}
	
	/*
	 * Funktion ermittelt die Bezeichnung der Argumente der aktuellen Nachricht.
	 * argNotation "old" :
	 * ArgX_A_B steht fÄr b. Argument in der a. Nachricht die X empfangen hat.
	 * argNotation "new" :
	 * msg_1 bedeutet 1. Argument der Nachricht mit dem Namen "msg"
	 */
	Vector getArgumentsOfCurrentMsg(SeqMsg[] messages, int current, String argNotation){

		Vector args = new Vector();
		int noOfArgsReceived = messages[current].no_messages;
		int noOfCurrentMessage = 0;
		String functionName = "";
		
		// Falls mehr als 0 Argumente empfangen, fahre fort...
		if ( (noOfArgsReceived > 0) && (current >= 0))
		{
		
			// Unterscheide zw. neuer und alter Notation
			if ( argNotation.equals("new") )
			{
				functionName = messages[current].functionName;

				// Bei Variablen muss erster Buchstabe groÄ geschrieben sein
				// ÄberprÄfe Variablen auf richtige Schreibweise
				if (!functionName.matches("[A-Z]\\w*"))
				{
						functionName = functionName.substring(0,1).toUpperCase() + functionName.substring(1);  
				}
			
		
				// Erzeuge Variablennamen
				for ( int k = 1; k <= noOfArgsReceived; k++)
					args.add(functionName + "_" + k);

			}				
			else
			{
		
				// Ermittle wieviel Nachrichten der Sender bereits empfangen hat	
				for ( int k = current; k >= 0; k-- )
				{
					if ( messages[k].receiver.equals(messages[current].receiver) )
						noOfCurrentMessage ++;  
				}
			
				// Erzeuge Variablennamen
				for ( int k = 1; k <= noOfArgsReceived; k++)
					args.add("Arg" + messages[current].receiver + "_" + noOfCurrentMessage + "_" + k);
	
			}	
		
		}
		
		// RÄckgabe
		return args;
	}
	
/*
 * Methode benutzt Ergebnis von getArguments und erzeugt daraus einen
 * String der Form knows(a) & knows(b) usw.
 * Falls keine Nachrichten erhalten wurde, wird "true" zurÄckgegeben
 * Falls getCurrent == true werden die Argumente der aktuellen Nachricht ermittelt
 * sonst die Argumente der zuvor empfangenen Nachricht
 */
	String getArgumentsString(SeqMsg[] messages, int no, String argNotation, boolean getCurrent){

		String out = "";
		Vector args = new Vector();
		if(getCurrent)
			args = getArgumentsOfCurrentMsg(messages, no, argNotation);
		else
			args = getArgumentsOfPreviousReceivedMsg(messages, no, argNotation);
				
		// Erzeuge String
		if (args.size() == 0)
			out = "true";
		else
		{
			for (int k = 0; k < args.size(); k++)
			{
				if ( k == 0)
					out += "knows(" + args.get(k) + ")";
				else
					out += " & knows(" + args.get(k) + ")";
			}
		}
		
		return out;

	}
	
	/*
	* Methode ermittelt alle Argumente als String der Form knows(a) & knows(b) usw.
	* Es werden alle Argumente der Nachrichten ermittelt, die der Absender der aktuellen Nachricht
	* zuvor empfangen hat bis er selbst wieder als Sender auftritt.
	*/
	   String getAllPreviousArgumentsString(SeqMsg[] messages, int m, String argNotation, String order, Vector msg_order_false){

		   String out = "";
		   String currentSender = messages[m].sender;
		   
		   //Falls order=true und msg_order_false=leer dann verwende Funktion getArgumentsString
			if (order.equals("true") && msg_order_false.isEmpty())
			{
				String s = getArgumentsString(messages, m, argNotation, false);
 				return s;
			}

		   
		   // Durchlaufe alle vorherigen Nachrichten
			 for (int k = m-1;  k >= 0; k-- )
			 {
			 	// Falls der aktuelle Sender == EmpfÄnger der Nachricht, dann ermittle die Argumente der aktuellen Nachricht 
				 if (messages[k].receiver.equals(currentSender))
				 {
				 	if (out.equals(""))
						out = getArgumentsString(messages, k, argNotation, true);
					else
						out = out + " & " + getArgumentsString(messages, k, argNotation, true);
				 }						
				 else if (messages[k].sender.equals(currentSender))
					break;
			 }
		   	 	
		   	// Falls keine Argumente vorhanden, dann "true"
			if(out.equals(""))
				out = "true";
				
		   return out;

	   }


	/*
	 * Funktion liefert alle Variablen, die im vorliegenden Protokoll vorkommen.
	 */
	Vector getAllArguments(SeqMsg[] messages, IMdrContainer _mdrContainer){

		Vector args = new Vector();
		Vector test = new Vector();
		String argNotation = getArgNotation(_mdrContainer);
		String varsForDummies = getVarsForDummies(_mdrContainer);
		
		// Ermittle ArgX ...
		for ( int i = 0; i < messages.length; i++)
		{
			args.addAll(getArgumentsOfCurrentMsg(messages, i, argNotation));
		}
		
		if (varsForDummies.equals("false"))
		{
			// Ermittle alle Variablen, die nicht quantifiziert sind
			for ( int i = 0; i < messages.length; i++)
			{
				test = messages[i].getVarsNotQuantified();
				
				for ( int j = 0; j < test.size(); j++)
					if (!args.contains(test.elementAt(j)))
						args.addElement(test.elementAt(j));
			}
		}
		
		
		return args;
	}


	/*
	 * Funktion liefert alle Sender-Namen
	 */
	Vector getAllSenders(SeqMsg[] messages){

		Vector senders = new Vector();
		
		for ( int i = 0; i < messages.length; i++)
		{
			if ( !senders.contains(messages[i].sender) )
				senders.addElement(messages[i].sender);
		}
	
		return senders;
	}

	/*
	 * Funktion liefert alle Objekte, die als Sender oder EmpfÄnger vorkommen
	 */
	Vector getAllObjects(SeqMsg[] messages){

		Vector receivers = getAllReceivers(messages);
		Vector senders = getAllSenders(messages);
	
		senders = addVector(receivers, senders);
	
		return senders;
	}
	
	/*
	* Funktion kombiniert zwei Vectoren ohne Doppelvorkommen
	*/
   Vector addVector(Vector one, Vector two){

	
	   for ( int i = 0; i < two.size(); i++)
	   {
		   if ( !one.contains(two.elementAt(i)) )
			   one.addElement(two.elementAt(i));
	   }
	
	   return one;
   }
   
   /*
   * Funktion liefert alle vorkommenden Variablen und Konstanten
   */
  Vector getAllAtoms(SeqMsg[] messages, IMdrContainer _mdrContainer){

	String argNotation = getArgNotation(_mdrContainer);
	//Vector result = getAllArguments(messages, _mdrContainer);
	Vector result = new Vector();
	result = addVector(result, getTaggedValue(_mdrContainer, "initial knowledge"));

	  for ( int m = 0; m < messages.length; m++)
	  {
		// Ermittle alle Konstanten in Nachricht
		result = addVector(result, messages[m].getConstants(messages[m].message));
		// Ermittle alle Variablen in Nachricht
		//result = addVector(result, messages[m].getVars(messages[m].message));
		// Ermittle alle Konstanten in Bedingung
		result = addVector(result, messages[m].getConstants(messages[m].all_conditions));
		// Ermittle alle Variablen in Bedingung
		//result = addVector(result, messages[m].getVars(messages[m].all_conditions));
	  }
	
	  return result;
  }
  
  /*
  * Funktion liefert alle Argumente (ArgS_1_1 oder Init_1 usw.) 
  * und quantifizierten Variablen in Bedingungen in Kleinbuchstaben
  */
 Vector getAllVariables(SeqMsg[] messages, IMdrContainer _mdrContainer){

   String argNotation = getArgNotation(_mdrContainer);
   Vector result = new Vector();

	// Ermittle ArgX ...
	for ( int i = 0; i < messages.length; i++)
	{
		result.addAll(getArgumentsOfCurrentMsg(messages, i, argNotation));
	}
		
	for ( int m = 0; m < messages.length; m++)
	{
		// Ermittle alle Variablen in Bedingung
		result = addVector(result, messages[m].getArgsQuantified(messages[m].all_conditions));
	}
			 
			 
	for ( int m = 0; m < result.size(); m++)
	{
		// Wandle alle Variablen in Kleinbuchstaben um
		result.setElementAt( ((String) result.elementAt(m)).toLowerCase(), m);
	}
	
	 return result;
 }
   
   /*
   * Funktion gibt alle Werte des zweiten Vektors zurÄck, die nicht im ersten 
   * enthalten sind
   */
  Vector compareVectorsNotIn(Vector one, Vector two){

	Vector neu = new Vector();
	
	  for ( int i = 0; i < two.size(); i++)
	  {
		  if ( !one.contains(two.elementAt(i)) )
			  neu.addElement(two.elementAt(i));
	  }
	
	  return neu;
  }

	/*
	 * Funktion liefert alle EmpfÄnger-Namen
	 */
	Vector getAllReceivers(SeqMsg[] messages ){

		Vector receivers = new Vector();
		
		
		for ( int i = 0; i < messages.length; i++)
		{
			if ( !receivers.contains(messages[i].receiver) )
				receivers.addElement(messages[i].receiver);
		}
	
		return receivers;
	}

/*
 * Methode gibt ATP Regeln aus
 */

	String getATPRules(SeqMsg[] messages, IMdrContainer _mdrContainer){
	
		String out = "\n%------------------------ Asymmetrical Encryption -------------------------\n" + 
			"\n" +
			"input_formula(enc_equation,axiom,(\n" +
			"! [E1,E2] :\n" +
			"( ( knows(enc(E1, E2))\n" + 
			"  & knows(inv(E2)) )\n" + 
			" => knows(E1) ) )).\n" + 
			"\n" +
			"%---------------------- Symmetrical Encryption -----------------------------\n" + 
			"\n" +
			"input_formula(symenc_equation,axiom,(\n" +
			"! [E1,E2] :\n" + 
			"( ( knows(symenc(E1, E2))\n" + 
			"  & knows(E2) )\n" + 
			" => knows(E1) ) )).\n" + 
			"\n" +
			"%--------------------------- Signature -------------------------------------\n" + 
			"\n" +
			"input_formula(sign_equation,axiom,(\n" + 
			"! [E,K] :\n" + 
			"( ( knows(sign(E, inv(K) ) )\n" + 
			"  & knows(K) )\n" + 
			" => knows(E) ) )).\n" + 
			"\n" +
			"%---- Basic Relations on Knowledge where conc, enc, symenc and sign is included ----\n" + 
			"\n" +
			"input_formula(construct_message_1,axiom,(\n" + 
			"! [E1,E2] :\n" + 
			"( ( knows(E1)\n" + 
			"  & knows(E2) )\n" + 
			"=> ( knows(conc(E1, E2))\n" + 
			"   & knows(enc(E1, E2))\n" + 
			"   & knows(symenc(E1, E2))\n" +
			"   & knows(dec(E1, E2))\n" +
			"   & knows(symdec(E1, E2))\n" +
			"   & knows(ext(E1, E2))\n" +
			"   & knows(sign(E1, E2)) ) ) )).\n" + 
			"\n" +
			"input_formula(construct_message_2,axiom,(\n" + 
			"! [E1,E2] :\n" + 
			"( ( knows(conc(E1, E2)) )\n" + 
			"=> ( knows(E1)\n" + 
			"  	& knows(E2) ) ) )).\n" + 
			"\n" +
			"%---- Basic Relations on Knowledge where head, tail and hash is included ----\n" + 
			"\n" +
			"input_formula(construct_message_3,axiom,(\n" + 
			"! [E] :\n" + 
			"( knows(E)\n" +
			"=> ( knows(head(E))\n" +
			"   & knows(tail(E))\n" +
			"   & knows(hash(E)) ) ) )).\n" +
			"\n" +
			"%--------------------------- decryption, signature verifikation -----------------\n" + 
			"\n" +
			"input_formula(dec_axiom,axiom,(\n" + 
			"! [E,K] :\n" + 
			"( equal( dec(enc(E, K), inv(K)), E ) ) )).\n" + 
			"\n" +
			"input_formula(symdec_axiom,axiom,(\n" + 
			"! [E,K] :\n" + 
			"( equal( symdec(symenc(E, K), K), E ) ) )).\n" + 
			"\n" +
			"input_formula(sign_axiom,axiom,(\n" + 
			"! [E,K] :\n" + 
			"( equal( ext(sign(E, inv(K)), K), E ) ) )).\n" + 
			"\n" +
			"%--------------------------- head, tail, fst, snd, thd, frth -------------------------------------\n" + 
			"\n" +
			"input_formula(head_axiom,axiom,(\n" + 
			"! [X,Y] :\n" + 
			"( equal( head(conc(X,Y)), X ) ) )).\n" + 
			"\n" +
			"input_formula(tail_axiom,axiom,(\n" + 
			"! [X,Y] :\n" + 
			"( equal( tail(conc(X,Y)), Y ) ) )).\n" + 
			"\n" +
			"input_formula(fst_axiom,axiom,(\n" + 
			"! [X] :\n" + 
			"( equal( fst(X), head(X) ) ) )).\n" + 
			"\n" +
			"input_formula(snd_axiom,axiom,(\n" + 
			"! [X] :\n" + 
			"( equal( snd(X), head(tail(X)) ) ) )).\n" + 
			"\n" +
			"input_formula(thd_axiom,axiom,(\n" + 
			"! [X] :\n" + 
			"( equal( thd(X), head(tail(tail(X))) ) ) )).\n" + 
			"\n" +
			"input_formula(frth_axiom,axiom,(\n" + 
			"! [X] :\n" + 
			"( equal( frth(X), head(tail(tail(tail(X)))) ) ) )).\n" + 
			"\n" +
			"%--------------------------- mac -------------------------------------\n" + 
			"\n" +
			"input_formula(symmac_axiom,axiom,(\n" + 
			"! [X,Y] :\n" + 
			"( (knows(X) & knows(Y)) => knows(mac(X, Y)) ) )).\n" + 
			//"\n" +
			//getATPListRules(messages, _mdrContainer) +
			"\n";
					
			return out;
	}	
	
	
	/*
	 * Funktion liefert ATP-Regeln fÄr Listen 
	 */
	String getATPListRules(SeqMsg[] messages, IMdrContainer _mdrContainer){
	
		String atom = "";
		String out = "\n%------------------------ List Axioms -------------------------\n" +
		"\n" +
		"input_formula(list_atoms1,axiom,(\n" ;

		
		Vector atoms = getAllAtoms(messages, _mdrContainer);
	
		for ( int i = 0; i < atoms.size(); i++)
		 {
				atom = (String)atoms.elementAt(i);
				
				if (!atom.equals(""))
				out += "isAtom(" + atom + ") &\n"; 

		 }

		// Entferne letztes "&"
		out = out.substring(0, out.length()-2);
		
		out += ")).\n";
		
		// Axiome fÄr Operationen
		out += "\ninput_formula(list_atom2,axiom,(\n" + 
		"! [E1,E2] :\n" + 
		"(   isAtom(enc(E1, E2))\n" + 
		"  & isAtom(symenc(E1, E2))\n" +
		"  & isAtom(sign(E1, E2))\n" +
		"  & isAtom(mac(E1,E2))\n" +
		"  & isAtom(fst(E1))\n" +
		"  & isAtom(snd(E1))\n" +
		"  & isAtom(thd(E1))\n" +
		"  & isAtom(frth(E1))\n" +
		"  & isAtom(inv(E1))\n" +
		"  & isAtom(hash(E1))\n" +
		"  & isAtom(head(E1)) ) )).\n" + 
		"\n";
		/* +
		"input_formula(head_axiom2,axiom,(\n" + 
		"! [X] :\n" + 
		"( isAtom(X) \n" +
		" => equal( head(X), X ) ) )).\n"; 
*/
		

		return out;
	}
	
	
	/*
	 * Funktion liefert TaggedValue mit Bezeichnung tag
	 */
	Vector getTaggedValue(IMdrContainer _mdrContainer, String tag){

		// Hole alle TaggedValues aus MDR-Container
		UmlPackage root = _mdrContainer.getUmlPackage();
		CorePackage corePackage = root.getCore();
		TaggedValueClass taVaCl = corePackage.getTaggedValue();
		Vector ve = new Vector();
	
		// Durchlaufe alle TaggedValue
		for (Iterator it = taVaCl.refAllOfClass().iterator(); it.hasNext();) {
	
			TaggedValue va = (TaggedValue)it.next();
			TagDefinition td = va.getType();
			Iterator itDataValue = va.getDataValue().iterator();
		

			// Falls TagType dem Gesuchten entspricht, ermittle DataValue
			if ( td.getName().equals(tag))
			{
				// Falls DataValue nicht leer, fÄge es zu Vektor hinzu
				while(itDataValue.hasNext()){
					String str = (String) itDataValue.next();
					//_mainOutput.writeLn("TaggedValue: " + td.getTagType() + " - " + str);
					if (!str.equals(""))
						ve.add(str);
				}
			}
		}
		
		return ve;
	}
	
	/*
	* Funktion liefert alle TaggedValues
	*/
   void getAllTaggedValues(IMdrContainer _mdrContainer, ITextOutput _mainOutput){

	   // Hole alle TaggedValues aus MDR-Container
	   UmlPackage root = _mdrContainer.getUmlPackage();
	   CorePackage corePackage = root.getCore();
	   TaggedValueClass taVaCl = corePackage.getTaggedValue();
	   //Vector ve = new Vector();
	
	   // Durchlaufe alle TaggedValue
	   for (Iterator it = taVaCl.refAllOfClass().iterator(); it.hasNext();) {
	
		   TaggedValue va = (TaggedValue)it.next();
		   TagDefinition td = va.getType();
		   Iterator itDataValue = va.getDataValue().iterator();
		

		   // Falls TagType dem Gesuchten entspricht, ermittle DataValue
			
		   // Falls DataValue nicht leer, fÄge es zu Vektor hinzu
		   while(itDataValue.hasNext()){
			   String str = (String) itDataValue.next();
			   _mainOutput.writeLn("TaggedValue: " + td.getName() + " - " + str);
		   }
		   
	   }
		
   }
		

	/*
	* Methode liefert Angriff
	*/
 
   String getAttack(IMdrContainer _mdrContainer){
 			
		// Geheimnis des Protokolls ist im TaggedValue "secret" gespeichert.
		Vector ve = getTaggedValue(_mdrContainer, "secret");
		String out = "";
		
		// Ersetze :: und =
		for (int i = 0; i < ve.size(); i++ )
			ve.setElementAt( this.replaceAll((String) ve.elementAt(i)), i);
		
		// Es darf nur ein Geheimnis geben
		if (ve.size() > 0 )
		{
			out = "%------------------------ Conjecture -------------------------\n" + 
				"\n" +
				"input_formula(attack,conjecture,(\n" +
				"   knows(" + ve.elementAt(0) + ") )).\n" ;
		}
		// Falls kein tag secret angegeben, benutze tag conjecture 
		else
		{
			ve = getTaggedValue(_mdrContainer, "conjecture");

			// Ersetze :: und =
			for (int i = 0; i < ve.size(); i++ )
				ve.setElementAt( this.replaceAll((String) ve.elementAt(i)), i);

			if (ve.size() > 0 )
			{
				out = "%------------------------ Conjecture -------------------------\n" + 
					"\n" +
					"input_formula(attack,conjecture,(\n" +
					"   (" + ve.elementAt(0) + ") )).\n" ;
			}
			
		}
		
	   return out;
		
   }
   
   /*
   * Methode liefert Notation der Argumente (new oder old)
   */
 
  String getArgNotation(IMdrContainer _mdrContainer){
 			
	   // Notation ist im TaggedValue "notation" gespeichert.
	   Vector ve = getTaggedValue(_mdrContainer, "notation");
	   String out = "old";
		
	   // PrÄfe, ob Notation == new, dann setze Ergebnis
	   if ( (ve.size() >= 1) && (ve.elementAt(0).equals("new")) )
			out = "new";
					
	  return out;
		
  }	
  
  /*
  * Methode liefert Notation der Guards (new oder old)
  */
 
 String getGuardNotation(IMdrContainer _mdrContainer){
 			
	  // Notation ist im TaggedValue "guard_notation" gespeichert.
	  Vector ve = getTaggedValue(_mdrContainer, "guard_notation");
	  String out = "old";
		
	  // PrÄfe, ob Notation == new, dann setze Ergebnis
	  if ( (ve.size() >= 1) && (ve.elementAt(0).equals("new")) )
		   out = "new";
					
	 return out;
		
 }	
  
 /*
  * Methode ermittlet, ob "Variablen fÄr Dummies" aktiviert ist,
  * d.h GroÄ- und Kleinschreibung der Variablen und Konstanten wird automatisch ermittelt.
  * Variablen beziehen sich entweder auf Argumente (z.B. ArgS_1_1) oder sind in einer Bedingung quantifiziert
  */
 String getVarsForDummies(IMdrContainer _mdrContainer){
 			
	  // Info ist im TaggedValue "variables_for_dummies" gespeichert.
	  Vector ve = getTaggedValue(_mdrContainer, "variables_for_dummies");
	  String out = "false";
		
	  // PrÄfe, ob variables_for_dummies==true, dann setze Ergebnis
	  if ( (ve.size() >= 1) && (ve.elementAt(0).equals("true")) )
		   out = "true";
					
	 return out;
		
 }	
  
 /*
 * Methode liefert Tag "order" mit Hilfe dessen unterschieden wird, 
 * ob Reihenfolge der Nachrichten berÄcksichtigt wird (TPTP)
 */
 
String getOrder(IMdrContainer _mdrContainer){
 			
	 // Notation ist im TaggedValue "notation" gespeichert.
	 Vector ve = getTaggedValue(_mdrContainer, "order");
	 String out = "true";
		
	 // PrÄfe, ob Notation == new, dann setze Ergebnis
	 if ( (ve.size() >= 1) && (ve.elementAt(0).equals("false")) )
		  out = "false";
					
	return out;
		
}	

 /*
 * Funktion liefert Vektor mit den Nachrichtennummer der Nachrichten, 
 * die nicht verschachtelt werden soll.
 */
Vector getMsgOrderFalse(IMdrContainer _mdrContainer){
 			
	 // Nachrichtennummern sind als String im Tag msg_order_false gespeichert
	 Vector ve = getTaggedValue(_mdrContainer, "msg_order_false");
	 String msgs = "";
	 Vector out = new Vector();
		
	 // Ermittle Wert des Tags
	 if ( (ve.size() >= 1) && (!ve.elementAt(0).equals("")) )
	 {
		    msgs = (String) ve.elementAt(0);
		  
			// Zerlege String bei allen Kommas.
			String[] msg = msgs.split(",");
		
			// FÄge Nachrichtennummern in Vektor ein 
			for ( int j = 0; j < msg.length; j++)
			{
				if (!msg[j].equals(""))
					out.add(msg[j].trim());
			}
	 }

	// RÄckgabe			
	return out;
		
}	
	
	/*
	 * Funktion liefert Initial Knowledge des Angreifers
	 */
	String getInitialKnowledge(IMdrContainer _mdrContainer){

		// Vorwissen des Angreifers ist im TaggedValue "initial knowledge" gespeichert.
		Vector ve = getTaggedValue(_mdrContainer, "initial knowledge");
		String out = "";
		
		// Ersetze :: und =
		for (int i = 0; i < ve.size(); i++ )
			ve.setElementAt( this.replaceAll((String) ve.elementAt(i)), i);
		
		
		// Erzeuge String
		if (ve.size() > 0 )
		{
			out = "%----------------------- Attackers Initial Knowledge -----------------------\n" + 
				"\n" +
				"input_formula(previous_knowledge,axiom,(\n";

			for (int i = 0; i < ve.size(); i++)
			{
				if ( i == 0)
					out += " knows(" + ve.elementAt(i) + ")\n";
				else
			 		out += " & knows(" + ve.elementAt(i) + ")\n";
			}
			
			out += " )).\n\n"; 
		}
		
		return out;
		
	}
	
	/*
	 * Funktion liefert Facts, die als tagged values im adversary- Objekt gespeichert sind
	 */
	String getFacts(IMdrContainer _mdrContainer){

		// Facts sind im TaggedValue "fact" gespeichert.
		Vector ve = getTaggedValue(_mdrContainer, "fact");
		String out = "";
		
		// Ersetze :: und =
		for (int i = 0; i < ve.size(); i++ )
			ve.setElementAt( this.replaceAll((String) ve.elementAt(i)), i);

		// Erzeuge String
		if (ve.size() > 0 )
		{
			out = "%----------------------- Facts -----------------------\n" + 
				"\n" +
				"input_formula(facts,axiom,(\n" ;
		
			for (int i = 0; i < ve.size(); i++)
			{
				if ( i == 0)
					out += "   (" + ve.elementAt(i) + ")\n";
				else
					out += " & (" + ve.elementAt(i) + ")\n";
			}
			
			out += " )).\n\n"; 
		}
		
		return out;
		
	}
	
	
	/*
	 *  Methode test die Variablenkonsistenz
	 */
	
	void checkVars( SeqMsg[] messages, String notation, ITextOutput _mainOutput ){
		
		Hashtable vars = new Hashtable();
		
		// Ermittle alle Objekte, die als Sender oder EmpfÄnger vorkommen
		Vector allObjects = getAllObjects(messages);

		for( int m = 0; m < allObjects.size(); m++ )
			vars.put(allObjects.elementAt(m), new Vector());
		
		Vector var = new Vector();
		
		// Durchlaufe alle Messages
		for( int m = 0; m < messages.length; m++ ) {
	
			_mainOutput.writeLn("Message "+ (m+1)+ " - Sender: " + messages[m].sender+ " - Receiver: " + messages[m].receiver);

			// Variablen der aktuellen Nachricht werden dem Vector des EmpfÄngers hinzugefÄgt
			var = getArgumentsOfCurrentMsg( messages, m, notation);
			
			vars.put(messages[m].receiver, addVector((Vector) vars.get(messages[m].receiver), var));
			
			// Variablen des aktuellen Senders
			var = (Vector) vars.get(messages[m].sender);
			_mainOutput.write("Known variables of sender: ");
				
				for ( int k = 0; k < var.size(); k++)
					_mainOutput.write(k+": "+var.get(k)+" ; ");

			_mainOutput.writeLn();

			// Variablen des aktuellen EmpfÄngers
			var = (Vector) vars.get(messages[m].receiver);
			_mainOutput.write("Known variables of receiver: ");
				
				for ( int k = 0; k < var.size(); k++)
					_mainOutput.write(k+": "+var.get(k)+" ; ");

			_mainOutput.writeLn();
			
			// Variablen in der Bedingung
			var = messages[m].getVarsNotQuantified(messages[m].all_conditions);
			_mainOutput.write("Variables in guard: ");
			
			for ( int k = 0; k < var.size(); k++)
				_mainOutput.write(k+": "+var.get(k)+" ; ");

			_mainOutput.writeLn();
			
			// Variablen in der Bedingung
			var = compareVectorsNotIn((Vector) vars.get(messages[m].sender), messages[m].getVarsNotQuantified(messages[m].all_conditions));
			_mainOutput.write("Unknown variables in guard: ");
			
			for ( int k = 0; k < var.size(); k++)
				_mainOutput.write(k+": "+var.get(k)+" ; ");

			_mainOutput.writeLn();

			// Variablen in der Nachricht
			var = messages[m].getVars(messages[m].all_messages);
			_mainOutput.write("Variables in message: ");
				
			for ( int k = 0; k < var.size(); k++)
				_mainOutput.write(k+": "+var.get(k)+" ; ");
	
			_mainOutput.writeLn();

			// Variablen in der Nachricht
			var = compareVectorsNotIn((Vector) vars.get(messages[m].sender), messages[m].getVars(messages[m].all_messages));
			_mainOutput.write("Unknown variables in message: ");
				
			for ( int k = 0; k < var.size(); k++)
				_mainOutput.write(k+": "+var.get(k)+" ; ");
	
			_mainOutput.writeLn();
			
	
				_mainOutput.writeLn();
	
	}


	}
	
	/*
	*  Methode test die Variablenkonsistenz und liefert String mit Ergebnis
	*/
	
   String checkVarsString( SeqMsg[] messages, String notation ){
		
	   Hashtable vars = new Hashtable();
	   String out = "";
	   
	   // Ermittle alle Objekte, die als Sender oder EmpfÄnger vorkommen
	   Vector allObjects = getAllObjects(messages);

	   for( int m = 0; m < allObjects.size(); m++ )
		   vars.put(allObjects.elementAt(m), new Vector());
		
	   Vector var = new Vector();
		
	   // Durchlaufe alle Messages
	   for( int m = 0; m < messages.length; m++ ) {
	
		   //_mainOutput.writeLn("Message "+ (m+1)+ " - Sender: " + messages[m].sender+ " - Receiver: " + messages[m].receiver);

		   // Variablen der aktuellen Nachricht werden dem Vector des EmpfÄngers hinzugefÄgt
		   var = getArgumentsOfCurrentMsg( messages, m, notation);
		   vars.put(messages[m].receiver, addVector((Vector) vars.get(messages[m].receiver), var));

			
		   // Variablen in der Bedingung
		   var = messages[m].getVarsNotQuantified(messages[m].all_conditions);
		   var = compareVectorsNotIn((Vector) vars.get(messages[m].sender), messages[m].getVarsNotQuantified(messages[m].all_conditions));
		   
		   if (var.size() > 0)
		   {
		   
		   		out += "% Note: Unknown variables in guard "+(m+1)+": ";
			
		   		for ( int k = 0; k < var.size(); k++)
				  out += var.get(k)+" ; ";

		  		out += "\n";
		   }

		   // Variablen in der Nachricht
		   var = messages[m].getVars(messages[m].all_messages);
		   var = compareVectorsNotIn((Vector) vars.get(messages[m].sender), messages[m].getVars(messages[m].all_messages));
		   
		   if (var.size() > 0 )
		   {
		   
				out += "% Note: Unknown variables in message "+(m+1)+": ";
						
				for ( int k = 0; k < var.size(); k++)
				   out += var.get(k)+" ; ";
			
				out += "\n";
		   }
			
		}

   		return out;
   		
   }
	
	
	/*
	*  Methode gibt String auf Konsole/GUI aus.
	*  Entsprechend den Vorgaben des Frameworks wird jede Zeile getrennt ausgegeben 
	*/
	
   void displayString( String str, ITextOutput _mainOutput ){
		
	   String out = "";
	
	   // Durchlaufe String und gebe jede Zeile getrennt aus
	   for (int i = 0; i < str.length(); i ++)
		{
		 	
		   if (str.charAt(i) != '\n' )
			   out += str.charAt(i);
		   else
		   {
			   _mainOutput.writeLn(out);
			   out = "";
		   }
			 		
		}
		 
		// Falls die letzte Zeile nicht mit "\n" abgeschlossen wurde, gebe diese aus
		if (out != "")
		   _mainOutput.writeLn(out);

   }
	
	
	
	/*
	 * Methode schreibt String str in Datei
	 * RÄckgabewert:
	 * Dateiname, falls erfolgreich
	 * sonst ""
	 */
		
	String writeString( String str, String pathName, String fileName, String extension,  ITextOutput _mainOutput ){
	
		// Erzeuge File-Objekt
		Random r = new Random();
		int a = Math.abs(r.nextInt());
		String file = fileName + "_" + a + "." + extension;
		File file_out = new File(pathName, file);

		// ÄberprÄfe, ob Datei vorhanden. Falls ja, Ändere Namen.
		while (file_out.exists())
		{
			a = Math.abs(r.nextInt());
			file = fileName + "_" + a + "." + extension;
			file_out = new File(pathName, file);
		}


		try{
		
			// Erzeuge neue Datei und schreibe String byteweise in Datei
			file_out.createNewFile();
	
			FileOutputStream out = new FileOutputStream(file_out);
	
			for (int i = 0; i < str.length(); i++){
		
				out.write(str.charAt(i));
			}
	
			// SchlieÄe Datei-Stream
			out.close();
		
		} catch (Exception e){
			_mainOutput.writeLn("Error writing "+extension+"-File!");
			file = "";
			};
			
		return file;

	}
	
	/*
	 * Methode behebt Fehler von tptp2X
	 */
		
	void correctTptp2xError( String filename, String pathName, ITextOutput _mainOutput ){
	
		String str = "";
		
		// Erzeuge File-Objekt
		File file = new File(pathName, filename);

		// ÄberprÄfe, ob Datei vorhanden. 
		if(!file.exists())
		{
			_mainOutput.writeLn("Failure: File "+pathName+filename+" doesn't exist!");
			return ;		
		}


		// Lese Datei
		try{
		
			FileInputStream in = new FileInputStream(file);
	
			int i = in.read();
			
			while (i != -1)
			{
				str += (char) i;
				i = in.read();
			}
			
			// SchlieÄe Datei-Stream
			in.close();
		
		} catch (Exception e){
			_mainOutput.writeLn("Error reading File "+pathName+filename+"!");
			};

		// Korrigiere Fehler; Ersetze "true_p" mit "true"
		str = str.replaceAll(", \\(true_p,0\\)", ""); 
		str = str.replaceAll("true_p", "true"); 
		//_mainOutput.writeLn("correctTPTP2x");
		//displayString(str, _mainOutput);

		// Schreibe Datei
		try{
		
			FileOutputStream out = new FileOutputStream(file);
	
			for (int i = 0; i < str.length(); i++){
		
				out.write(str.charAt(i));
			}
	
			// SchlieÄe Datei-Stream
			out.close();
		
		} catch (Exception e){
			_mainOutput.writeLn("Error writing File "+pathName+filename+"!");
			};
			
	}
	
	/*
	 * Methode lÄscht Datei
	 */
		
	boolean deleteFile( String pathName, String fileName, ITextOutput _mainOutput ){
	
		// Erzeuge File-Objekt
		File file_out = new File(pathName, fileName);

		// ÄberprÄfe, ob Datei vorhanden. Falls nein, gibt Fehlermeldung aus
		if (!file_out.exists())
		{
			_mainOutput.writeLn("Error deleting File: File " + pathName + fileName + " doesn't exist!");
			return false;
		}


		// LÄsche Datei
		try{
		
			file_out.delete();
		
		} catch (Exception e){
			_mainOutput.writeLn("Error deleting File "+ pathName + fileName +" !");
			return false;
			};
			
		return true;

	}
	
		/*
	 * Methode testet String auf richtige Klammerung
	 * RÄckgabe:
	 * String
	 **/
	 
	 String testKlammer(String str){
	 	
		int klammer = 0;
	 	
		for ( int i = 0; i < str.length(); i++){
	 		
			if (str.charAt(i) == '(')
				klammer++;
			else if ( str.charAt(i) == ')')
				klammer--;

			if ( klammer < 0)
				break;
		}
		
		if (klammer < 0 )
			return "% Error: Too many closing brackets!\n";	
		else if (klammer > 0 )
			return "% Error: Too many opening brackets!\n";	
		else 
			return "";
}

/*
* Methode ersetzt '::' durch conc() und '=' durch equal() usw.
**/
	 
String replaceAll(String str){
	
	SeqMsg msg = new SeqMsg();

	str = msg.replaceAll(str);

	//RÄckgabe
	return str;
}


}	
