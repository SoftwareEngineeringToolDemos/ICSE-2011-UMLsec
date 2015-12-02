// define the packagefolder
package tum.umlsec.viki.tools.umlsecNotationAnalyser.checks;

// important imports
import java.util.*;
import org.omg.uml.foundation.core.*;
import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.mdr.IMdrContainer;
import tum.umlsec.viki.tools.checkstatic.StaticCheckerBase;
import tum.umlsec.viki.UmlTypeScanner;

import org.omg.uml.behavioralelements.activitygraphs.*;
import org.omg.uml.behavioralelements.collaborations.*;
import org.omg.uml.behavioralelements.statemachines.*;

// Checking Class extends the StaticCheckerBase - forces to implement the "check"-Method
public class DumpAllModelElements extends StaticCheckerBase {

    //Output Textbox Stream
	ITextOutput textOutput;

    // DumpAllModelElements check-method
    public boolean check(IMdrContainer _mdrContainer, Iterator _parameters, ITextOutput _textOutput) {
        //GUI-Outputcontainer for check-messages
        textOutput = _textOutput;

        CorePackage corePackage ;
        corePackage = _mdrContainer.getUmlPackage().getCore();

        UmlTypeScanner umlident = new UmlTypeScanner();
        
        //-------------------------------------------------

        //None of the diagramtypes could be identified
        if (!umlident.identifiable(corePackage)) return false;

        //-------------------------------------------------
        
        textOutput.writeLn("Modeltyp: " + umlident.diagramType(corePackage));
        textOutput.writeLn("");

        textOutput.writeLn("====[ Dumping all model elements ]====");

        String act_elem = "";
        for(Iterator it = corePackage.getModelElement().refAllOfType().iterator();it.hasNext();){
          ModelElement me = (ModelElement) it.next();
          String name = umlident.modelElementType(me.getClass().getName());
          if(!name.equals(act_elem)){
            act_elem = name;
            textOutput.writeLn("");
            textOutput.writeLn("--- All " + act_elem + "s ---");
          }
          if(name.equals("TaggedValue")){
            TaggedValue tagValue = (TaggedValue) me;
            textOutput.writeLn("- " + umlident.modelElementType(tagValue.getModelElement().getClass().getName()) + ":" + tagValue.getModelElement().getName() + " | " + tagValue.getType().getName() + " | Value: " + tagValue.getDataValue().iterator().next().toString());
          }else{           
            if(me.getName() == null || me.getName().equals("")){
              textOutput.writeLn("- " + act_elem + " with no name");
            }else{
              textOutput.writeLn("- " + me.getName());
            }
          }
        }

        return true;
    }
}
