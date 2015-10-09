// define the packagefolder
package tum.umlsec.viki.tools.umlseChNotationAnalyser.checks;

// important imports
import java.util.*;
import org.omg.uml.foundation.core.*;
import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.mdr.IMdrContainer;
import tum.umlsec.viki.tools.checkstatic.StaticCheckerBase;
import tum.umlsec.viki.UmlTypeScanner;


// Checking Class extends the StaticCheckerBase - forces to implement the "check"-Method
public class DumpAllUMLseChElements extends StaticCheckerBase {

    //Output Textbox Stream
	ITextOutput textOutput;

    // your check method
	public boolean check(IMdrContainer _mdrContainer, Iterator _parameters, ITextOutput _textOutput) {
      //counters
      int tag_found = 0;
      int stereo_found = 0;
      int dep_stereo_found = 0;
      int tag_exist = 0;
      int stereo_exist = 0;
      int dep_stereo_exist = 0;

      //datastructure for UMLseCh-Delta
      Vector<StructUMLseChDelta> add = new Vector();
      Vector<StructUMLseChDelta> del = new Vector();
      Vector<StructUMLseChDelta> subs = new Vector();

      //dump
      textOutput = _textOutput;
      textOutput.writeLn("=================== Dumping all UMLseCh elements...");
     
      CorePackage corePackage ;
      corePackage = _mdrContainer.getUmlPackage().getCore();

      UmlTypeScanner umlident = new UmlTypeScanner();
      
      // list all the tagged values
      textOutput.writeLn ("======= All UMLseCh Tagged Values");
      TaggedValueClass taggedValueClass = (TaggedValueClass)corePackage.getTaggedValue();
      for (Iterator iter1 = taggedValueClass.refAllOfClass().iterator(); iter1.hasNext();){
        TaggedValue tagValue = (TaggedValue) iter1.next();
        tag_exist += 1;

        if(tagValue!=null && tagValue.getType()!=null && tagValue.getType().getName()!=null && (tagValue.getType().getName().equals("ref")
                || tagValue.getType().getName().equals("pattern")
                || tagValue.getType().getName().startsWith("substitute")
                || tagValue.getType().getName().startsWith("add")
                || tagValue.getType().getName().startsWith("delete")
                || tagValue.getType().getName().startsWith("["))){

          //adds the elements to the delta-datastructure
          if (tagValue.getType().getName().startsWith("add")) add.addElement(new StructUMLseChDelta(tagValue, tagValue.getDataValue().iterator().next().toString()));
          if (tagValue.getType().getName().startsWith("substitute")) subs.addElement(new StructUMLseChDelta(tagValue,  tagValue.getDataValue().iterator().next().toString()));
          if (tagValue.getType().getName().startsWith("delete")) del.addElement(new StructUMLseChDelta(tagValue, tagValue.getDataValue().iterator().next().toString()));

          textOutput.writeLn ("Tagged Value: " + tagValue.getType().getName() + " = " + tagValue.getDataValue().iterator().next().toString());
          textOutput.writeLn ("Model Element: " + umlident.modelElementType(tagValue.getModelElement().getClass().getName()));
          textOutput.writeLn ("Model Element Name: " + tagValue.getModelElement().getName());
          tag_found += 1;

        }
      }

      // list all the stereotypes
      textOutput.writeLn ("======= All UMLseCh Stereotypes");
      StereotypeClass stereotypeClasses = (StereotypeClass)corePackage.getStereotype();
      for (Iterator iter = stereotypeClasses.refAllOfClass().iterator(); iter.hasNext();) {
        Stereotype stereotype = (Stereotype) iter.next();
        stereo_exist += 1;
        if(stereotype.getName().startsWith("substitute") || stereotype.getName().startsWith("add") || stereotype.getName().startsWith("delete")){
          textOutput.writeLn (stereotype.getName());
          stereo_found += 1;
        }
      }

      // list all of the stereotypes of the dependencies
      textOutput.writeLn ("======= All UMLseCh Stereotypes of Dependencies");
      //first list all the dependecies
      DependencyClass dependencyClass_S = corePackage.getDependency();
      for (Iterator iter2 = dependencyClass_S.refAllOfClass().iterator(); iter2.hasNext();) {
        Dependency dependency_S = (Dependency)iter2.next();
        //list all the stereotypes of every dependency
        for (Iterator iter3 =dependency_S.getStereotype().iterator(); iter3.hasNext();) {
          Stereotype stereotype_D = (Stereotype) iter3.next();
          dep_stereo_exist += 1;
          if(stereotype_D.getName().startsWith("substitute") || stereotype_D.getName().startsWith("add") || stereotype_D.getName().startsWith("delete")){
            textOutput.writeLn (stereotype_D.getName());
            dep_stereo_found += 1;
          }
        }
      }
      textOutput.writeLn ("The Command found " + tag_found + " Tagged Values, " + stereo_found + " Stereotypes and " + dep_stereo_found + " Dependency-Stereotypes belonging to UMLseCh,");
      textOutput.writeLn ("but there are " + tag_exist + " Tagged Values, " + stereo_exist + " Stereotypes and " + dep_stereo_exist + " Dependency-Stereotypes in general.");
      
      textOutput.writeLn ("");
      textOutput.writeLn ("##############################################################");
      textOutput.writeLn ("### Add Vector:");

      for(Iterator iter_add = add.iterator(); iter_add.hasNext();){
        StructUMLseChDelta delta_add = (StructUMLseChDelta) iter_add.next();
        textOutput.writeLn ("--- Value: " + delta_add.getValue());
        textOutput.writeLn ("ID    : " + delta_add.getID());
        textOutput.writeLn ("Path  : " + delta_add.getPath());
        textOutput.writeLn ("Parent: [" +  umlident.modelElementType(delta_add.getParent().getClass().getName()) + "] " + delta_add.getParent().getName());
      }
      
      textOutput.writeLn ("");
      textOutput.writeLn ("### Delete Vector:");

      for(Iterator iter_del = del.iterator(); iter_del.hasNext();){
        StructUMLseChDelta delta_del = (StructUMLseChDelta) iter_del.next();
        textOutput.writeLn ("--- Value: " + delta_del.getValue());
        textOutput.writeLn ("ID    : " + delta_del.getID());
        textOutput.writeLn ("Path  : " + delta_del.getPath());
        textOutput.writeLn ("Parent: [" +  umlident.modelElementType(delta_del.getParent().getClass().getName()) + "] " + delta_del.getParent().getName());
      }

      textOutput.writeLn ("");
      textOutput.writeLn ("### Substitute Vector:");

      for(Iterator iter_subs = subs.iterator(); iter_subs.hasNext();){
        StructUMLseChDelta delta_subs = (StructUMLseChDelta) iter_subs.next();
        textOutput.writeLn ("--- Value: " + delta_subs.getValue());
        textOutput.writeLn ("ID    : " + delta_subs.getID());
        textOutput.writeLn ("Path  : " + delta_subs.getPath());
        textOutput.writeLn ("Parent: [" +  umlident.modelElementType(delta_subs.getParent().getClass().getName()) + "] " + delta_subs.getParent().getName());
      }
      
      return true;
    }
}
