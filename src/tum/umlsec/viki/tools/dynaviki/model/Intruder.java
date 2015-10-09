package tum.umlsec.viki.tools.dynaviki.model;

import java.util.Collection;
import java.util.Vector;

import tum.umlsec.viki.framework.ExceptionProgrammLogicError;
import tum.umlsec.viki.tools.dynaviki.model.interfaces.ITreeNodeCollection;

/**
 * @author pasha
 */
public class Intruder extends BaseObject implements ITreeNodeCollection {
	public static final int INSIDER = 1;
	public static final int OUTSIDER = 2;
	
	
	public Intruder(ModelRoot _root) {
		super(_root);
	}
	
	protected void createNodeStructure() {
		children.removeAllElements();
	}
	
	public void initialise() {
		readSettings();
		createNodeStructure();
		switch(intruderType) {
			case INSIDER:
				children.add(new TreeLeafText(getRoot(), "type: Insider", null));
			break;
			case OUTSIDER:
				children.add(new TreeLeafText(getRoot(), "type: Outsider", null));
			break;
			default:
				children.add(new TreeLeafText(getRoot(), "type: unknown", null));
		}
	}
	
	private void readSettings() {
		
// TODO read from settings		
		intruderType = OUTSIDER;
		if(intruderType != INSIDER && intruderType != OUTSIDER) {
			throw new ExceptionProgrammLogicError("creating unknown intruder type");
		}
		
//		TODO read from settings		
		savedMessageCount = 2;	
	}
	
	
	

	public String getNodeName() {
		return "Intruder";
	}

	public String getNodeText() {
		return null;
	}
	
	public Collection getChildren() {
		return children;
	}
	
	public void completeInitialisation() {
		String _capabilities = "capabilities: ";
		if(canReadMedia) {
			_capabilities += "Read, ";
		}
		if(canWriteMedia) {
			_capabilities += "Write, ";
		}
		if(canDeleteMedia) {
			_capabilities += "Delete, ";
		}
		_capabilities = _capabilities.substring(0, _capabilities.length() - 2);
		
		children.add(new TreeLeafText(getRoot(), _capabilities, null));
	}
	
	
	public void processPhysicalLinkStereotype(MD_Stereotype _stereotype) {
		switch(_stereotype.getStereotypeType()) {
			case MD_Stereotype.LAN:
				if(intruderType == INSIDER) {
					canReadMedia = true;
					canWriteMedia = true;
				}
			break;

			case MD_Stereotype.INTERNET:
				if(intruderType == INSIDER) {
					canReadMedia = true;
					canWriteMedia = true;
					canDeleteMedia = true;
				}
				if(intruderType == OUTSIDER) {
					canReadMedia = true;
					canWriteMedia = true;
					canDeleteMedia = true;
				}
			break;

			case MD_Stereotype.WIRELESS:
				if(intruderType == INSIDER) {
					canReadMedia = true;
					canWriteMedia = true;
				}
				if(intruderType == OUTSIDER) {
					canReadMedia = true;
					canWriteMedia = true;
				}
			break;
		}
	}
	
	public int getSavedMessageCount() {
		return savedMessageCount;
	}
	
	public boolean canRead() {		return canReadMedia; }
	public boolean canWrite() {		return canWriteMedia; }
	public boolean canDelete() {	return canDeleteMedia; }
	
	
	
	
	
	boolean canReadMedia = false;
	boolean canWriteMedia = false;
	boolean canDeleteMedia = false;
	
	int intruderType = 0;
	
	int savedMessageCount;

	private Vector children = new Vector();
}



