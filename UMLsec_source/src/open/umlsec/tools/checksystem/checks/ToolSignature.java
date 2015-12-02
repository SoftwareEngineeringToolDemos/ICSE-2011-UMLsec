/**
 * 
 */
package open.umlsec.tools.checksystem.checks;

import java.util.Iterator;
import java.util.Vector;

/**
 * @author ska
 *
 */
public class ToolSignature{
	
	private Vector<String> 				tags;
	private Vector<String> 				stereotypes;
	private Vector<CommandSignature> 	commands;
	private Vector<Integer>				results;
	private String						constraint = "none";
	
	private int Id;
	private int nrOfTagMatches;
	private int nrOfStereotypeMatches;

	private boolean signatureComplete;
	
	public ToolSignature (int id) {
		Id 			= id;
		tags 		= new Vector<String>();
		stereotypes = new Vector<String>();
		commands 	= new Vector<CommandSignature>();
		results		= new Vector<Integer>();
		
		resetQueryResult();
	}
	
	public void setConstraint(String stereotype, int commandId){
		if (commandId >= 0){
			addCommand(commandId);
			for (Iterator<CommandSignature> it = commands.iterator(); it.hasNext();){
				CommandSignature cmd = it.next();
				if (cmd.getcommandId() == commandId){
					cmd.setConstraint(stereotype);
				}
			}
		} else {
			constraint = stereotype;			
		}
	}

	public void addStereotype(String stereotype, int commandId){
		if (commandId >= 0){
			addCommand(commandId);
			for (Iterator<CommandSignature> it = commands.iterator(); it.hasNext();){
				CommandSignature cmd = it.next();
				if (cmd.getcommandId() == commandId){
					cmd.addStereotype(stereotype);
				}
			}
		} else {
			stereotypes.add(stereotype);			
		}
	}

	public void addTag(String tag, int commandId){
		if (commandId >= 0){
			addCommand(commandId);
			for (Iterator<CommandSignature> it = commands.iterator(); it.hasNext();){
				CommandSignature cmd = it.next();
				if (cmd.getcommandId() == commandId){
					cmd.addTag(tag);
				}
			}
		} else {
			tags.add(tag);
		}
	}
	
	public boolean hasConstraint(String stereotype){
		boolean bFoundCmd 	= false;
		boolean bFound 		= false;
		boolean bRet		= false;
		
		if (constraint.equals(stereotype)){
			bFound 	= true;
			bRet	= bFound;
		}
		
		/* check also within commands if any */
		if (commands.size() != 0){
			for (Iterator<CommandSignature> cmdIt = commands.iterator(); cmdIt.hasNext();){
				CommandSignature cmd = cmdIt.next();
				bFoundCmd = cmd.hasConstraint(stereotype);
				if (bFoundCmd == true){
					signatureComplete 	= true;
					bRet				= bFoundCmd;
					//break;
				}
			}
		} else {
			if (bFound == true){
				signatureComplete = true;
			}
		}
		
		return bRet;
	}
	
	public boolean hasStereotype(String stereotype){
		boolean bFoundCmd 	= false;
		boolean bFound 		= false;
		boolean bRet		= false;
		
		for (Iterator<String> it = stereotypes.iterator(); it.hasNext();){
			String st = it.next();
			
			if (st.equals(stereotype)){
				nrOfStereotypeMatches++;
				if ((nrOfStereotypeMatches == stereotypes.size()) &&
						nrOfTagMatches == tags.size()){
					bFound 	= true;
					bRet	= bFound;
				}
				break;
			}
		}
		
		/* check also within commands if any */
		if (commands.size() != 0){
			for (Iterator<CommandSignature> cmdIt = commands.iterator(); cmdIt.hasNext();){
				CommandSignature cmd = cmdIt.next();
				bFoundCmd = cmd.hasStereotype(stereotype);
				if (bFoundCmd == true){
					signatureComplete 	= true;
					bRet				= bFoundCmd;
					//break;
				}
			}
		} else {
			if (bFound == true){
				signatureComplete = true;
			}
		}
		
		return bRet;
	}

	public boolean hasTag(String tag){
		boolean bFoundCmd 	= false;
		boolean bFound 		= false;
		boolean bRet		= false;
		for (Iterator<String> it = tags.iterator(); it.hasNext();){
			String st = it.next();
			
			if (st.equals(tag)){
				nrOfTagMatches++;
				if ((nrOfStereotypeMatches == stereotypes.size()) &&
						nrOfTagMatches == tags.size()){
					bFound 	= true;
					bRet	= bFound;
				}
				break;
			}
		}

		/* check also within commands if any */
		if (commands.size() != 0){
			for (Iterator<CommandSignature> cmdIt = commands.iterator(); cmdIt.hasNext();){
				CommandSignature cmd = cmdIt.next();
				bFoundCmd = cmd.hasTag(tag);
				if (bFoundCmd == true){
					signatureComplete 	= true;
					bRet				= bFoundCmd;
					//break;
				}
			}
		} else {
			if (bFound == true){
				signatureComplete = true;
			}
		}

		return bRet;
	}
	
	public int getId(){
		return Id;
	}
	
	public Vector<Integer> getQueryResult(){
		
		/* the first id is always the tool id, subsequent ids are commands */
		if (signatureComplete == true){
			if (results.size() == 0){
				results.add(Id);
			}
		}
		if (commands.size() != 0){
			for (Iterator<CommandSignature> cmdIt = commands.iterator(); cmdIt.hasNext();){
				CommandSignature cmd = cmdIt.next();
				
				if (cmd.getStatus() == true){
					results.add(cmd.getcommandId());
				}
			}
		}
		
		return results;
	}
	
	public void resetQueryResult(){
		nrOfStereotypeMatches 	= 0;
		nrOfTagMatches 			= 0;
		signatureComplete 		= false;
		
		results.clear();
		
		constraint = "none";

		/* reset also within commands if any */
		if (commands.size() != 0){
			for (Iterator<CommandSignature> cmdIt = commands.iterator(); cmdIt.hasNext();){
				CommandSignature cmd = cmdIt.next();
				
				cmd.resetStatus();
			}
		}
	}
	
	private void addCommand(int id){
		boolean exists = false;
		for (Iterator<CommandSignature> cmdIt = commands.iterator(); cmdIt.hasNext();){
			CommandSignature cmd = cmdIt.next();
			if (cmd.getcommandId() == id){
				/* already there */
				exists = true;
				break;
			}
		}
		if (exists == false){
			commands.add(new CommandSignature(id));
		}
	}
}


class CommandSignature {
	
	private Vector<String> 	tags;
	private Vector<String> 	stereotypes;
	private Vector<String> 	tagsMatches;
	private Vector<String> 	stereotypesMatches;
	private String			constraint = "none";
	
	private int commandId;
	private int nrOfTagMatches;
	private int nrOfStereotypeMatches;
	
	private boolean signatureComplete;

	public CommandSignature(int id){
		commandId			= id;
		tags 				= new Vector<String>();
		stereotypes 		= new Vector<String>();
		tagsMatches			= new Vector<String>();
		stereotypesMatches 	= new Vector<String>();
		resetStatus();
	}

	public void setConstraint(String stereotype){
		constraint = stereotype;
	}

	public void addStereotype(String stereotype){
		stereotypes.add(stereotype);
	}

	public void addTag(String tag){
		tags.add(tag);
	}
	
	public boolean hasConstraint(String stereotype){
		boolean bFound = false;

		if (constraint.equals(stereotype)){
			bFound = true;
			signatureComplete = true;
		}

		return bFound;
	}

	public boolean hasStereotype(String stereotype){
		boolean bFound = false;

		for (Iterator<String> it = stereotypes.iterator(); it.hasNext();){
			String st = it.next();
			if (st.equals(stereotype)){
				if (checkDuplicity(stereotype, null) == false){
					nrOfStereotypeMatches++;
					if ((nrOfStereotypeMatches == stereotypes.size()) &&
							nrOfTagMatches == tags.size()){
						bFound = true;
						signatureComplete = true;
					}
					break;
				}
			}
		}

		return bFound;
	}

	public boolean hasTag(String tag){
		boolean bFound = false;
		
		for (Iterator<String> it = tags.iterator(); it.hasNext();){
			String st = it.next();
			if (st.equals(tag)){
				if (checkDuplicity(null, tag) == false){
					nrOfTagMatches++;
					if ((nrOfStereotypeMatches == stereotypes.size()) &&
							nrOfTagMatches == tags.size()){
						bFound = true;
						signatureComplete = true;
					}
					break;
				}
			}
		}

		return bFound;
	}
	
	
	public int getcommandId(){
		return commandId;
	}
	
	public boolean getStatus(){
		return signatureComplete;
	}
	
	public void resetStatus(){
		nrOfStereotypeMatches 	= 0;
		nrOfTagMatches 			= 0;
		signatureComplete 		= false;
		tagsMatches.clear();
		stereotypesMatches.clear();
		constraint = "none";
	}
	
	private boolean checkDuplicity(String stereotype, String tag){
		boolean bDuplicate 	= false;
		boolean bfoundSt	= false;
		boolean bfoundTag	= false;
		
		if (tag != null){
			for (Iterator<String> itTags = tagsMatches.iterator(); itTags.hasNext();){
				String el = itTags.next();
				if (el.equals(tag)){
					bDuplicate = true;
					bfoundTag = true;
					break;
				}
			}
			if (bfoundTag == false){
				tagsMatches.add(tag);
			}
		}
		if (stereotype != null){
			for (Iterator<String> itSt = stereotypesMatches.iterator(); itSt.hasNext();){
				String el = itSt.next();
				if (el.equals(stereotype)){
					bDuplicate = true;
					bfoundSt = true;
					break;
				}
			}
			if (bfoundSt == false){
				stereotypesMatches.add(stereotype);
			}
		}
		
		return bDuplicate;
	}
}

