package tum.umlsec.viki.tools.modulTemplate;


import java.util.Iterator;

import org.apache.log4j.Logger;

import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.mdr.IMdrContainer;
import tum.umlsec.viki.framework.toolbase.CommandParameterDescriptor;
import tum.umlsec.viki.tools.checkstatic.StaticCheckerBase;

/**
 * @author Marc Peschke
 *
 */

// here you have to add your algorithms

public class CheckerModulTemplate extends StaticCheckerBase {
	private static Logger logger = Logger.getLogger("TemplateLogger");
	String pattern = "";
	
	public boolean check(IMdrContainer mdrContainer, Iterator parameters, ITextOutput textOutput) {
		
		// gets Value of the parameter
		for (; parameters.hasNext();) {
	       CommandParameterDescriptor _parameter = (CommandParameterDescriptor) parameters.next();
	                if(_parameter.getId() == ModulTemplate.CID_TEMPLATE) {
	                	pattern = _parameter.getAsString();
	            }
		}
		
		ITextOutput textOutputlocal;
		textOutputlocal = textOutput;
		textOutputlocal.writeLn("Template is working.....");
		
		return true;

	}

}
