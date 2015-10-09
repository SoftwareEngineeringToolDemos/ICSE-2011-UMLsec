/**
 * UML model parser for sequence diagrams:
 */

package tum.umlsec.viki.tools.sequencediagram;

import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Pattern;

import org.omg.uml.UmlPackage;
import org.omg.uml.behavioralelements.commonbehavior.CommonBehaviorPackage;
import org.omg.uml.behavioralelements.commonbehavior.LinkClass;
import org.omg.uml.behavioralelements.commonbehavior.Stimulus;
import org.omg.uml.behavioralelements.commonbehavior.StimulusClass;
import org.omg.uml.foundation.core.CorePackage;
import org.omg.uml.foundation.core.TagDefinition;
import org.omg.uml.foundation.core.TaggedValue;

import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.mdr.IMdrContainer;



// class to calculate all possible flows which fit the sequence diagram
public class SequenceParser 
{
    Vector subMessageVector = new Vector();
	SequenceMessage[] messages;
	
	//save forall variables
	Vector forallVariable = new Vector();

	/**
	 * method to parse the subsystem and the given sequence diagram
	 */
	public void check(IMdrContainer _mdrContainer, Iterator _parameters, ITextOutput _mainOutput) 
	{
		
	
	//get UmlPackage from Container
	UmlPackage root = _mdrContainer.getUmlPackage();
	//get CorePackage from UmlPackage
	CorePackage corePackage = root.getCore();
	//get special Packages from Corepackage
	CommonBehaviorPackage commonbehavior = (CommonBehaviorPackage)root.getCommonBehavior();
		//get SitimulusClass
		StimulusClass stimulusClasses = (StimulusClass)commonbehavior.getStimulus();
		//get LinkClass
		LinkClass linkclass = (LinkClass)commonbehavior.getLink();
		
		
		int n = 0;       
		// saving the number of messages in the sequence diagram
		for (Iterator it_sti_I = stimulusClasses.refAllOfClass().iterator(); it_sti_I.hasNext();) 
		{
			Stimulus sti = (Stimulus)it_sti_I.next();
			n++;
		}
		messages = new SequenceMessage[n];
		//array SequenceMessage[] to initialize
		for (int noMessage = 0; noMessage < n; noMessage++) {
			messages[noMessage] = new SequenceMessage();
		}
		
		//get number of Objects
		Vector objects = new Vector();
		int objectsCount = 0;
		for (Iterator it_sti_I = stimulusClasses.refAllOfClass().iterator(); it_sti_I.hasNext();) 
		{
			Stimulus sti = (Stimulus)it_sti_I.next();
			if(!objects.contains(sti.getReceiver().getName()))				
			{
				objects.add(sti.getReceiver().getName());
			}		
		}
		objectsCount = objects.size();
		
		//get, how many messages to each objects
		String[] objStr = new String[objectsCount];
		int[] objInt = new int[objectsCount];
		for (int i = 0; i < objectsCount; i++)
		{
			objStr[i] = "";
			objInt[i] = 0;
		}
		
		int c = 0;
		for (Iterator it_sti_I = stimulusClasses.refAllOfClass().iterator(); it_sti_I.hasNext();) 
		{
			Stimulus sti = (Stimulus)it_sti_I.next();
			boolean exist = false;
			for(int i = 0; i < objectsCount; i++)
			{
				if(objStr[i].equals(sti.getReceiver().getName()))				
				{
					objInt[i]++;
					exist = true;
				}
			}
			if(!exist)
			{
				objStr[c] = sti.getReceiver().getName();
				objInt[c]++;
				c++;
			}
		
		}				
		
		//saving all messages of the sequence diagram
		int m = n;// the no. of the message
		for (Iterator it_sti_I = stimulusClasses.refAllOfClass().iterator(); it_sti_I.hasNext();) 
		{
			Stimulus sti = (Stimulus)it_sti_I.next();
			
			m--;
			
			try
			{
				//get the number of the current Stimulus
				//int m = Integer.parseInt(sti.getName().substring(sti.getName().lastIndexOf('_')+1));
				String stimulusName = sti.getName();
				//save the sender
				messages[m].sender = sti.getSender().getName();
				System.out.println("Message "+(m));
				System.out.print("Sender:  ");
				System.out.print(messages[m].sender);
				//save the recevier
				messages[m].receiver = sti.getReceiver().getName();
				System.out.print("Receiver:  ");
				System.out.println(messages[m].receiver);
				//save the message
				messages[m].messageName = sti.getDispatchAction().getName();
				System.out.print("Message: ");
				System.out.println(messages[m].messageName);
							
				//at first, save the submessages im Vector
				subMessageVector = this.splitSubMessage(messages[m].messageName);								

				//finally, save the submessages im HashMap
				int numberOfToMessage = 0;
				for(int i = 0; i < objectsCount; i++)
				{
					if(objStr[i].equals(sti.getReceiver().getName()))				
					{
						numberOfToMessage = i;
					}
				}
				for(int l = 0; l < subMessageVector.size(); l++)
				{
					String subMessageTag = "";
					String subMessageValue = "";
					
					subMessageTag = "Arg_"+ messages[m].receiver +"_"+ objInt[numberOfToMessage] + "_" + (l+1);
					
					subMessageValue = (String)subMessageVector.elementAt(l);
					
					if (!IsBracketesOK(subMessageValue))
					{
						_mainOutput.writeLn("Bracketes of teil message "+m + " is not well-formed.");
						_mainOutput.writeLn("=>  " + subMessageValue);
						return;	
					}
	
					
					subMessageValue = binaryop2func("::","conc",subMessageValue);
					subMessageValue = binaryop2func("!=","unequal",subMessageValue);
					subMessageValue = binaryop2func("=","equal",subMessageValue);
					subMessageValue = binaryop2func("<=","smaller",subMessageValue);
					
					System.out.println(" m = "+ m );
					System.out.println("subMessage = " +subMessageValue);
					
					pickupForallVariable(subMessageValue, forallVariable);
									
					messages[m].subMessage.put(subMessageTag, subMessageValue);
				}
				objInt[numberOfToMessage]--;

				
				//save conditions and predicates in appropriate Vector
				for(Iterator sti_link_I = sti.getTaggedValue().iterator(); sti_link_I.hasNext();)				
				{
					TaggedValue link = (TaggedValue) sti_link_I.next();
					TagDefinition l_TagDefiontion = link.getType();
					String l_TagName = l_TagDefiontion.getName();
					
					if (l_TagName.equalsIgnoreCase("condition") || l_TagName.equalsIgnoreCase("predicate"))
					{
						String link_TagValue = null;
							for (Iterator link_IterDataVal = link.getDataValue().iterator(); link_IterDataVal.hasNext();)
							{
								link_TagValue = (String) link_IterDataVal.next();
								link_TagValue = binaryop2func("::","conc",link_TagValue);
								link_TagValue = binaryop2func("!=","unequal",link_TagValue);
								link_TagValue = binaryop2func("=","equal",link_TagValue);
								link_TagValue = binaryop2func("<=","smaller",link_TagValue);
								pickupForallVariable(link_TagValue, forallVariable);					
								//System.out.println("condition = "+link_TagValue);
								if(l_TagName.equalsIgnoreCase("condition"))
									messages[m].condition.add(link_TagValue);
								if(l_TagName.equalsIgnoreCase("predicate"))
									messages[m].predicate.add(link_TagValue);
							}
					}
				}		
			}
			catch(Exception e) 
			{
				_mainOutput.writeLn(e.getMessage());
				System.out.println(e.getMessage());
			}
		}
	}

	/** 
	 * method to split a string, here the original message will be split.
	 * @param st
	 */
	private Vector splitOriginalMessage(String st) 
	{
		Vector subItemVector = new Vector();
		int amountOfLeftbracket = 0;
		int amountOfRightbracket = 0;
		int i = 0; //the Position of the first '('
		int j = 0; //the Position of the first ')'
		int l = 0; //the Position of the first ','
		String first = "";
		String rest = "";
		String subMessageOutput = "no";
		
		rest = st.trim();
		//System.out.println("rest = "+rest);
		while (!rest.equals("") || subMessageOutput.equals("yes"))
		{
			
			i = rest.indexOf('(');
			j = rest.indexOf(')');
			l = rest.indexOf(',');
			
			if (i != -1 && i < j && subMessageOutput.equals("no"))			
				{
					if(i < l) amountOfLeftbracket ++;
					if(i > l && amountOfLeftbracket !=0) amountOfLeftbracket ++;
					if(i > l && l == -1) amountOfLeftbracket ++;
				}
				
			if (i == -1 && j == -1 && l == -1 && subMessageOutput.equals("no"))
			{
				first = rest;
				rest = "";				
			}
			else
			{
				if(first.equals("") && amountOfLeftbracket == 0 && subMessageOutput.equals("no"))
					first = rest.substring(0,l);				
			}


			if (amountOfLeftbracket != 0)
			{
				if (i != -1 && i < j)
				{	
					first = first.concat(rest.substring(0,i+1));
					rest = rest.substring(i+1);
				}
				else 
				{
					amountOfLeftbracket --;
					first = first.concat(rest.substring(0,j+1));
					rest = rest.substring(j+1);
					if(amountOfLeftbracket == 0)	subMessageOutput = "yes";			
				}
			}
			else
			{
				//System.out.println("first = "+first);
			subItemVector.add(first);
				first = "";
				rest = rest.substring(rest.indexOf(',')+1);
//				if (rest.charAt(1) == ' ') 
//					rest = rest.substring(rest.indexOf(' ')+1);
				subMessageOutput = "no";				
			}			
		}
		//System.out.println("-----------");
		return subItemVector;
	}
	
	// method to split the original message 
	private Vector splitSubMessage(String st) 
	{
		//subItems of the message
		Vector  subItemVector= new Vector();
		
		st = st.substring(st.indexOf('(')+1 , st.length()-1);		
		//////System.out.println("st = "+st);		
		subItemVector = splitOriginalMessage(st);
		return subItemVector;
	}
	
	/** 
	 * pick up all Quantal Variables
	 * @param st
	 * @param forallVariable
	 */
	private Vector pickupForallVariable(String st, Vector forallVariable)
	{

		//the first Positon in Sting
		int l = 0; //leftBracket position
		int r = 0; //rightBracket position
		int c  = 0; // comma position
		int s = 0; // space position
		int minp = 0;
		String first = "";
		
		int temp = 0;
		temp = st.indexOf("?");
		while(!st.equalsIgnoreCase("") && st.indexOf("?")== -1)
		{
			l = st.indexOf('(');
			r = st.indexOf(')');
			c = st.indexOf(',');
			s = st.indexOf(' ');

			if (l ==0 || r==0 || c==0 ||s==0)
			{
				st = st.substring(1);
			}else
			{	
				if (l == -1 && r == -1 && c == -1 && s == -1) 
					{first = st; st = "";}
				else
				{
					if (l == -1) l = 1000;
					if (r == -1) r = 1000;
					if (c == -1) c = 1000;
					if (s == -1) s = 1000;			
					minp =Math.min(Math.min(Math.min(l,r),c),s);
					first = st.substring(0,minp);
					st = st.substring(minp);					
				}

				if (first.substring(0,1).equals("B")||first.substring(0,1).equals("C")
				||first.substring(0,1).equals("D")||first.substring(0,1).equals("E")||first.substring(0,1).equals("F")
				||first.substring(0,1).equals("G")||first.substring(0,1).equals("H")||first.substring(0,1).equals("I")
				||first.substring(0,1).equals("J")||first.substring(0,1).equals("K")||first.substring(0,1).equals("L")
				||first.substring(0,1).equals("M")||first.substring(0,1).equals("N")||first.substring(0,1).equals("O")
				||first.substring(0,1).equals("P")||first.substring(0,1).equals("Q")||first.substring(0,1).equals("R")
				||first.substring(0,1).equals("S")||first.substring(0,1).equals("T")||first.substring(0,1).equals("U")
				||first.substring(0,1).equals("V")||first.substring(0,1).equals("W")||first.substring(0,1).equals("X")
				||first.substring(0,1).equals("Y")||first.substring(0,1).equals("Z")
				)//if the first alphabetic character of the word is uppercase letter
					{
								if (!forallVariable.contains(first)) forallVariable.add(first); 						
					}						
			}
		}				
		return forallVariable;
	}
    
	
	public SequenceMessage[] getMessage()
	{
		return this.messages;
	}
	
	public Vector getForallVariable()
	{
		return this.forallVariable;
	}


	/** 
	 * convert binary operation such like A op B into func(A, B) in a String 
	 * @param op : binary operator name
	 * @param func : new function name
	 * @param exp : String to be changed
	 * @return changed String <br>
	 * e.g binaryop2func("=", "equal", exp)
	 */
	public static String binaryop2func(String op,String func, String exp) 
	{
	  int i = 0;
	  int j = 0;
	  String lBrackets = "(";
	  String rBrackets = ")";
	  int lBcnt = 0;
	  int rBcnt = 0;
	  String left = "";
	  String right = "";
		
	  if(exp == null)
	  {
		  return null;
	  }
	  //otherweise
	  if(exp.indexOf(op) <=0)
	  {
		  return exp;
	  }else
	  {
		  //get left part
		  for(i=exp.indexOf(op)-1;i>=0;i--)
		  {
			  System.out.println(exp.charAt(i));
				
			  //check if current char is a left bracket,  
			  if(lBrackets.indexOf(exp.charAt(i))>=0)
			  {
				  lBcnt++; 	
			  }
			  //check if current char is a right bracket,  
			  if(rBrackets.indexOf(exp.charAt(i))>=0)
			  {
				  rBcnt++;
			  } 
			System.out.println("exp lenght = " + exp.length());
			  if(lBcnt>rBcnt|| i==0 && lBcnt==rBcnt || i==exp.length()+1 && lBcnt==rBcnt || Pattern.matches("[,:]",""+exp.charAt(i)) && lBcnt==rBcnt || Pattern.matches("[,!=]",""+exp.charAt(i)+exp.charAt(i+1)) && lBcnt==rBcnt || Pattern.matches("[,<=]",""+exp.charAt(i)+exp.charAt(i+1)) && lBcnt==rBcnt)
			  {
				  if(i==0)
				  	left=exp.substring(i,exp.indexOf(op));
				  else
				  	left=exp.substring(i+1,exp.indexOf(op));
				  break;
			  }
		  }
		  if(i<0)
		  {
			  left=exp.substring(0,exp.indexOf(op));
		  }
		  //get right part
		  lBcnt =0;
		  rBcnt =0;
		  for(i=exp.indexOf(op)+op.length();i<exp.length();i++)
		  {
			  System.out.println(exp.charAt(i));
			  //check if current char is a left bracket,  
			  if(lBrackets.indexOf(exp.charAt(i))>=0)
			  {
				  lBcnt++; 	
			  }
			  //check if current char is a right bracket,  
			  if(rBrackets.indexOf(exp.charAt(i))>=0)
			  {
				  rBcnt++;
			  } 
			  
			  if(lBcnt<rBcnt|| i==0 && lBcnt==rBcnt || i==exp.length()-1 && lBcnt==rBcnt || Pattern.matches("[,:]",""+exp.charAt(i)) && lBcnt==rBcnt || Pattern.matches("[,!=]",""+exp.charAt(i)+exp.charAt(i+1)) && lBcnt==rBcnt || Pattern.matches("[,<=]",""+exp.charAt(i)+exp.charAt(i+1)) && lBcnt==rBcnt)
			  {
			  	  if (i==exp.length()-1)
					right=exp.substring(exp.indexOf(op)+op.length(),i+1);	
				  else
				  	right=exp.substring(exp.indexOf(op)+op.length(),i);
				  break;
			  }
		  }
		  if(i==exp.length())
		  {
			  right=exp.substring(exp.indexOf(op)+op.length(),i);
		  }
		  String regex=left+op+right;
		  String replacement =  func+"("+left.trim()+", "+right.trim()+")";
		  exp=exp.replaceFirst("\\Q"+regex+"\\E", replacement );
		  System.out.println(exp);
		  return binaryop2func(op,func,exp);
	  }
	}
	
	/**
	 * check, if the sum of right bracket is equal to the sum of left bracket
	 * @param Exp
	 * @return
	 */
	public static boolean IsBracketesOK (String Exp)
	{
		int i = 0;
		int B1cnt = 0;
		int B2cnt = 0;
		int B3cnt = 0;

		if(Exp == null)
		{
			return true;//empty is well-formed
		}
		//otherweise
		for(;i<Exp.length();i++)
		{ 
			char temp =Exp.charAt(i);
			switch(temp)
			{
				case '(': B1cnt++;break;
				case '[': B2cnt++;break;
				case '{': B3cnt++;break;
				//not allow right bracket come before left bracket
				case '}': B3cnt--; if(B3cnt<0){return false;}break;
				case ']': B2cnt--; if(B2cnt<0){return false;}break;
				case ')': B1cnt--; if(B1cnt<0){return false;}break;
			}
		}
		
		if(B1cnt==0 && B2cnt==0 && B3cnt==0)
		{
			return true;	
		}
		else 
		{
			//there is something not well-formed
			return false;
		}
	}
}