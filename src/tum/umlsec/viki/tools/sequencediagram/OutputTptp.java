
package tum.umlsec.viki.tools.sequencediagram;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;

import tum.umlsec.viki.framework.ExceptionProgrammLogicError;
import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.toolbase.CommandParameterDescriptor;
import tum.umlsec.viki.tools.sequenceparser.ToolSequence;

public class OutputTptp 
{
	
	/**
	 * constructor
	 */
	public OutputTptp() 
	{		
	}
	

	/**
	 * get messages
	 */	
	int n = 0;
	public SequenceMessage[] messages = new SequenceMessage[n];
	String filename;
	Vector forallVariable;
	Iterator parameters;
	public ITextOutput mainOutput;
	private static Logger logger = Logger.getLogger("OutputTptp");
	
	/**
	 * get useful information, for example: messages, file name etc.
	 * @param _messages
	 * @param _forallVariable
	 * @param _filename
	 * @param _parameters
	 */
	public void getMessagesForallvariableFilename(SequenceMessage[] _messages, Vector _forallVariable, String _filename,Iterator _parameters)
	{
		this.messages = _messages;
		this.filename = _filename;
		this.forallVariable = _forallVariable;
		this.parameters = _parameters;
		logger.trace("messages size is " + _messages.length);
		logger.trace("forallVariable size is " + _forallVariable.size());
		logger.trace("filename is " + _filename);
		logger.trace("Parameters are " + _parameters.toString());
	}

	/**
	 * TPTP file output
	 */
	public boolean checkFile(ITextOutput _mainOutput)
	{
		mainOutput = _mainOutput;
		try
		{
			boolean creatNewFile;	
			File f = new File(filename);
		
				//create file: filename.tptp
				creatNewFile = f.createNewFile(); 
				if (creatNewFile == false)
					System.out.println("Create new file error.");
			
			//copy the general regels to the tptp_file
//			boolean copy_ok = copy("..\\viki\\src\\tum\\umlsec\\viki\\tools\\sequencediagram\\TPTPheader.txt",filename);
			boolean copy_ok = copy(System.getProperty("user.dir")+File.separator+"tum"+File.separator+"umlsec"+File.separator+"viki"+File.separator+"tools"+File.separator+"sequencediagram"+File.separator+"TPTPheader.txt",filename);
				if(copy_ok)
			{
				System.out.println("Copy success!");
				return true;
			}
			else
			{
				copy_ok = copy(System.getProperty("user.dir")+File.separator+"WEB-INF"+File.separator+"classes"+File.separator+"tum"+File.separator+"umlsec"+File.separator+"viki"+File.separator+"tools"+File.separator+"sequencediagram"+File.separator+"TPTPheader.txt",filename);
				if(copy_ok)
					{
					System.out.println("Copy success!");
					return true;
					}
				else
				System.out.println("Copy failed!");
				return false;
			}
		}
		catch(IOException e)
		{
			System.out.println(e.getMessage());
			System.out.println("TPTP file error.");
			return false;
		}
	}



	public void writeTptp(ITextOutput _mainOutput)
		{
			logger.trace("Entering writeTptp");
			mainOutput = _mainOutput;
			try
			{

			//write the messges to the tptp_file
			Vector outputMessage = new Vector();
			printATPRules();
			mainOutput.writeLn();
			outputMessage.add("%----------------------- Main Protocol Specification ---------------------------");
			outputMessage.add("input_formula(protocol,axiom,(");		
			outputMessage.add("% RERL PROTOCOL CHECK BEGINNING");
			mainOutput.writeLn("%----------------------- Main Protocol Specification ---------------------------");
			mainOutput.writeLn("input_formula(protocol,axiom,(");
			mainOutput.writeLn("% RERL PROTOCOL CHECK BEGINNING");
			
			//Strin of all arguments and all forallvariables
			String arguments = "";
			String forallvar = "";
			//forall arguments output
			int c = 0;
			logger.trace("messages size is " + messages.length);
			for(int i=0;i<messages.length;i++)
			{

				Set keyset =(Set)messages[i].subMessage.keySet();								
				for (Iterator iKeys = keyset.iterator();iKeys.hasNext(); )
				{										
					String subOutput = (String) iKeys.next();
					if (c!=0) arguments = arguments + ", ";			
					arguments = arguments + subOutput;
					System.out.println("arguments = "+arguments);
					c++;
				}			
			}
			outputMessage.add("! [" + arguments + "]:");
			mainOutput.writeLn("! [" + arguments + "]:");
			
			outputMessage.add("(");
			mainOutput.writeLn("(");
			
			//forall Variable output			
			c = 0;
			for(int i = 0; i < forallVariable.size(); i++)
			{
					if (c!=0) forallvar = forallvar + ", ";			
					forallvar = forallvar + forallVariable.elementAt(i);
					System.out.println("forallvar = "+forallvar);
					c++;	
			}
			if (!forallvar.equals(""))
				{
				outputMessage.add("! [" + forallvar + "]:");
				mainOutput.writeLn("! [" + forallvar + "]:");
				}
						
			readMessage(outputMessage);
			outputMessage.add(")");
			mainOutput.writeLn(")");
			outputMessage.add(")");
			mainOutput.writeLn(")");
			outputMessage.add(").");
			mainOutput.writeLn(").");
			outputMessage.add("% RERL PROTOCOL CHECK ENDING");
			mainOutput.writeLn("% RERL PROTOCOL CHECK ENDING");
			//copy("..\\viki\\TPTP\\TPTPfooter.txt","..\\viki\\src\\tum\\umlsec\\viki\\tools\\sequencediagram\\"+ filename + ".tptp");

			//create a output fileobjekt		
			java.io.File file_out=new java.io.File(filename);
			FileWriter objfile=new FileWriter(file_out, true);
			//write a string to file
			for(int i = 0; i < outputMessage.size(); i++)
			{
				objfile.write((String) outputMessage.elementAt(i));
				objfile.write("\n");
				objfile.flush();					
			}
			objfile.close();//close fileobject
		}
		catch(IOException e)
		{
			System.out.println(e.getMessage());
			System.out.println("TPTP file error.");
		}
		logger.trace("Leave writeTptp");
	}
	
	/**
	 * content of protocol will be output
	 * @param outputPrepare
	 */
	private void readMessage(Vector outputPrepare)
	{
		outputPrepare.add("(");
		mainOutput.writeLn("(");
		for(int i=0;i<messages.length;i++)
		{
			//handle the first message
			if(i==0)
			{
				outputPrepare.add("%" + messages[0].sender + "-> Attacker ( 1. message)");
				outputPrepare.add("(");
				mainOutput.writeLn("%" + messages[0].sender + "-> Attacker ( 1. message)");
			    mainOutput.writeLn("(");
				subMessageOutput(outputPrepare, messages[0].subMessage, "value");										
				outputPrepare.add(")");
				mainOutput.writeLn(")");
			}
			//handle the rest messages
			else
			{
				outputPrepare.add("&");
				mainOutput.writeLn("&");
				
				outputPrepare.add("%" + messages[i].sender + "-> Attacker (" + (i+1) +". message)");
				mainOutput.writeLn("%" + messages[i].sender + "-> Attacker (" + (i+1) +". message)");
				//output conditions
				outputPrepare.add("((");
				mainOutput.writeLn("((");
				subMessageOutput(outputPrepare, messages[i-1].subMessage, "key");
				conditionOutput(outputPrepare, messages[i-1].condition);
				outputPrepare.add(")");
				mainOutput.writeLn(")");
				//output message
				outputPrepare.add("=> ");
				mainOutput.writeLn("=> ");
				outputPrepare.add("(");
				mainOutput.writeLn("(");
				subMessageOutput(outputPrepare, messages[i].subMessage, "value");
				conditionOutput(outputPrepare, messages[i-1].predicate);
				outputPrepare.add(")");
				mainOutput.writeLn(")");
				outputPrepare.add(")");
				mainOutput.writeLn(")");
			}			
		}
		outputPrepare.add(")");
		mainOutput.writeLn(")");
	}
	
	/**
	 * subMessage output. 
	 * when valueOrkey is equal "key",that means,the arguments will be output;
	 * when valueOrkey is equal "value",that means,the teilmessages will be output;
	 * @param outputPrepare
	 * @param hashmap
	 * @param valueOrkey
	 */
	private void subMessageOutput(Vector outputPrepare, HashMap hashmap, String valueOrkey)
	{
		int c = 0;
		
		if (valueOrkey.equals("value"))
		{	
			Iterator testValueKeys = hashmap.keySet().iterator();
			if (!testValueKeys.hasNext()) outputPrepare.add("true");
			else
			{			
				for (Iterator iKeys = hashmap.values().iterator();iKeys.hasNext(); )
				{					
					String subOutput = (String) iKeys.next();
					if(c == 0)
					{
						outputPrepare.add("knows(" + subOutput + ")");
						mainOutput.writeLn("knows(" + subOutput + ")");
					}
					else
					{
						outputPrepare.add("& knows(" + subOutput + ")");
						mainOutput.writeLn("& knows(" + subOutput + ")");
					}
					c++;
				}
			}			
		}
		else if (valueOrkey.equals("key"))
		{	
			Iterator testKeyKeys = hashmap.keySet().iterator();
			
			if (!testKeyKeys.hasNext()) outputPrepare.add("true");
			else
			{
				for (Iterator iKeys = hashmap.keySet().iterator();iKeys.hasNext(); )
				{					
					String subOutput = (String) iKeys.next();
					if(c == 0)
					{
						outputPrepare.add("knows(" + subOutput + ")");
						mainOutput.writeLn("knows(" + subOutput + ")");
					}
					else
					{
						outputPrepare.add("& knows(" + subOutput + ")");
						mainOutput.writeLn("& knows(" + subOutput + ")");
					}
					c++;
				}
			}
					
		}

	}

	/**
	 * conditon or predicate output
	 * @param outputPrepare
	 * @param vector, here may be a conditon vector, may be a predicate vector
	 */
	private void conditionOutput(Vector outputPrepare, Vector vector)
	{
		for(int i = 0; i < vector.size(); i++)
		{
			outputPrepare.add("& " + vector.elementAt(i));
			mainOutput.writeLn("& " + vector.elementAt(i));
		}
	}
	
	/**
	 * copy from a file to another file
	 * @param fileFrom
	 * @param fileTo
	 * @return
	 */
	private boolean copy(String fileFrom,String fileTo)
	{
		try //must try and catch,otherwise will compile error
		{
			//instance the File as file_in and file_out
			java.io.File file_in=new java.io.File(fileFrom);
			java.io.File file_out=new java.io.File(fileTo);
			FileInputStream in1=new FileInputStream(file_in);
			FileOutputStream out1=new FileOutputStream(file_out);
			byte[] bytes=new byte[1024];
			int c;
			while((c=in1.read(bytes))!=-1)
			{
				out1.write(bytes,0,c);
				//mainOutput.write(bytes[c]);
			}
			in1.close();
			out1.close();
			return(true);  //if success then return true
		}
		catch(Exception e)
		{  
			System.out.println("Copy Error!");
			return(false);  //if fail then return false
		}
	}
	
	/**
	 * check, if a file has already exist.
	 * @param _parameters
	 * @return
	 */
	public String checkFileExist(Iterator _parameters)
	{			
		String _filename = "";
		boolean _foundIterations = false; 
		for (; _parameters.hasNext();) {
				CommandParameterDescriptor _parameter = (CommandParameterDescriptor) _parameters.next();
				if(_parameter.getId() == ToolSequence.CPID_DEPTH) {
			_filename = _parameter.getAsString();
			
			if(_filename.lastIndexOf(".tptp")== -1)
				_filename = _filename + ".tptp";
			
			//System.out.println(_filename);
			_foundIterations = true;
				}
		}
		if(_foundIterations == false) {
				throw new ExceptionProgrammLogicError("Required parameter missing");
			}
		
		//check,if the file has exist
		boolean creatNewFile;			
		File f = new File(_filename);
		if(!f.exists())
		{
			return _filename;
		}
		else
		{
			return "exist!";
			//System.out.println( "the file has exist!");
		}
	}

	/**
	 * print ATP Regeln in Console/GUI
	 */
	private void printATPRules()
	{
		String out = "";
		String str = "%------------------------ Asymmetrical Encryption -------------------------\n" + 
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
			"   & knows(sign(E1, E2)) ) ) )).\n" + 
			"\n" +
			"input_formula(construct_message_2,axiom,(\n" + 
			"! [E1,E2] :\n" + 
			"( ( knows(conc(E1, E2)) )\n" + 
			"=> ( knows(E1)\n" + 
			"  	& knows(E2) ) ) )).\n" + 
			"\n";
			
			// travel the string and print each line step by step
			for (int i = 0; i < str.length(); i ++)
			 {
			 	
			 	if (str.charAt(i) != '\n' )
			 		out += str.charAt(i);
			 	else
			 	{
					mainOutput.writeLn(out);
					out = "";
			 	}
				 		
			 }
			 
			 // if the last line not complete with "\n", print out the last line
			 if (out != "")
				mainOutput.writeLn(out);	
	}
	
	/**
	 * only used for Debugging
	 * @param args
	 */
	public static void main(String[] args)
	{
//		OutputTptp _outputTptp = new OutputTptp();
//		_outputTptp.writeTptp();
	}

}
