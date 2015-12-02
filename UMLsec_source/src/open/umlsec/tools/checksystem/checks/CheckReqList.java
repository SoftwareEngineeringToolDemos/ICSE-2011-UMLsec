/**
 * 
 */
package open.umlsec.tools.checksystem.checks;

import java.io.File;
import java.util.Iterator;
import java.util.Vector;

import open.umlsec.tools.checksystem.gui.SystemVerificationLoader;

import org.apache.log4j.Logger;
import org.omg.uml.UmlPackage;
import org.omg.uml.behavioralelements.usecases.UseCase;
import org.omg.uml.behavioralelements.usecases.UseCasesPackage;
import org.omg.uml.foundation.core.CorePackage;
import org.omg.uml.foundation.core.TaggedValue;

import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.mdr.IMdrContainer;

/**
 * @author ska
 *
 */
public class CheckReqList extends CheckSystemBase {
	private static Logger logger = Logger.getLogger("CheckSystemBase");

	private boolean 		bResult 	= false;
	private XMLReqParser 	xmlParser 	= null;
	private Vector<String> 	reqIds		= null;
	
	public CheckReqList(){
		xmlParser 	= new XMLReqParser();
		reqIds 		= new Vector<String>();
	}
	
	/* (non-Javadoc)
	 * @see open.umlsec.tools.checksystem.CheckSystemBase#check(tum.umlsec.viki.framework.mdr.IMdrContainer, java.util.Iterator, tum.umlsec.viki.framework.ITextOutput)
	 */
	@Override
	public boolean check(IMdrContainer mdrContainer, Iterator parameters,
			ITextOutput textOutput) {
		return bResult;
	}
	/* (non-Javadoc)
	 * @see open.umlsec.tools.checksystem.CheckSystemBase#check(tum.umlsec.viki.framework.mdr.IMdrContainer, java.util.Iterator, tum.umlsec.viki.framework.ITextOutput)
	 */
	@Override
	public boolean check(IMdrContainer mdrContainer, ITextOutput textOutput) {
		
		readFromDiagram(mdrContainer);
		
		/* set result temporarily to true */
		bResult = true;
		
		for (int i = 0; i < reqIds.size(); i++){
			for (int j = 0; j < xmlParser.getRequirementList().size(); j++){
				String id = xmlParser.getRequirementList().elementAt(j).getIDString();
				if (reqIds.elementAt(i).equals(id)){
					xmlParser.getRequirementList().elementAt(j).setStatus("ANALYSED");
				}
				
				/* verify if all requirments have been analysed */
				if (xmlParser.getRequirementList().elementAt(
										j).getStatus().equals("NOT ANALYSED")){
					bResult = false;
				}
			}
		}
		
		return bResult;
	}

	
	private void readFromDiagram(IMdrContainer mdrContainer){
		CorePackage 	corePackage;
		UmlPackage 		root;
		UseCasesPackage usecases;
		
		mdrContainer = SystemVerificationLoader.getFramework().getMdrContainer();
		root = mdrContainer.getUmlPackage();
		corePackage = root.getCore();
		
		usecases = root.getUseCases();
		
		for (Iterator i = usecases.getUseCase().refAllOfClass().iterator(); i.hasNext();){
			UseCase useCase = (UseCase)i.next();

			System.out.println("Use case: " + useCase.getName());
			
			for (Iterator j = useCase.getTaggedValue().iterator(); j.hasNext();){
				TaggedValue tag = (TaggedValue)j.next();
				
				if (tag.getType().getName().equals("requirement_id")){
					for (Iterator itData = tag.getDataValue().iterator(); itData.hasNext();){
						String value = (String)itData.next();
						System.out.println("Requirement ID: " + value);
						reqIds.add(value);
					}
				}
			}
		}
	}

	public void readFromFile(File f){
		if (xmlParser != null){
			xmlParser.setXmlFile(f);
			xmlParser.parseFile();
		}
	}
	
	public Vector<Requirement> getReqFromXMLFile(){
		return xmlParser.getRequirementList();
	}
}
