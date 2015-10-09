

package tum.umlsec.viki.tools.sequencediagram;

import java.util.HashMap;
import java.util.Vector;

import tum.umlsec.viki.framework.ITextOutput;

// class to save messages
public class SequenceMessage 
{
    
	public String sender;
	public String receiver;
	public String messageName;
	public Vector condition;
	public Vector predicate;
	public HashMap subMessage;
    
	//construction to save a message
	public SequenceMessage() 
	{
		sender = "";
		receiver = "";
		messageName = "";
		//subMessage of the original message
		subMessage = new HashMap();
		condition = new Vector();
		predicate = new Vector();
	}
    
	/**
	 * method to check if two messages are equal
	 */
	public boolean isEqual(SequenceMessage msg) 
	{
		//check if two messagename are equal
		if (msg.messageName.lastIndexOf(')') - msg.messageName.lastIndexOf('(') == 1) 
		{
			if (msg.messageName.substring(0, msg.messageName.lastIndexOf('(')).equals(messageName.substring(0, messageName.lastIndexOf('('))) == false) return(false);
		}
		else if (messageName.equals(msg.messageName) == false) return(false);
		//check if two recevier are equal
		if (receiver.equals(msg.receiver) == false) return(false);
		//check if two sender are equal
		if (sender.equals(msg.sender) == false) return(false);
		return(true);
	}
    
	/**
	 * method to print a message. This is a help function.
	 */ 
	public void printMessage(ITextOutput _textOutput) 
	{
		_textOutput.write(sender);
		_textOutput.write(" -- ");
		_textOutput.write(messageName);
		_textOutput.write(" -> ");
		_textOutput.write(receiver);
	}
    
}
