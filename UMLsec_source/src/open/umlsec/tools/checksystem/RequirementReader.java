/**
 * 
 */
package open.umlsec.tools.checksystem;

import java.io.File;
import java.util.Iterator;
import java.util.Vector;

import open.umlsec.tools.checksystem.checks.Requirement;
import open.umlsec.tools.checksystem.checks.XMLReqParser;
import open.umlsec.tools.checksystem.gui.SystemVerificationLoader;

import org.omg.uml.UmlPackage;
import org.omg.uml.behavioralelements.usecases.UseCase;
import org.omg.uml.behavioralelements.usecases.UseCasesPackage;
import org.omg.uml.foundation.core.CorePackage;
import org.omg.uml.foundation.core.TaggedValue;

import tum.umlsec.viki.framework.mdr.IMdrContainer;


/**
 * @author ska
 *
 */
public class RequirementReader {
	private XMLReqParser 	xmlParser = null;

	public RequirementReader(){
		xmlParser = new XMLReqParser();
	}
	
	public void readFromFile(File f){
		if (xmlParser != null){
			xmlParser.setXmlFile(f);
			xmlParser.parseFile();
		}
	}
	
	public void readFromDiagram(){
		CorePackage 	corePackage;
		UmlPackage 		root;
		IMdrContainer 	mdrContainer;
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
					}
				}
			}
		}
	}
	
	public Vector<Requirement> getReqFromXMLFile(){
		return xmlParser.getRequirementList();
	}


}
