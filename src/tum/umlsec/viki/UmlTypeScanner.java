// Identifies important parts of an UML corePackage

// define the packagefolder
package tum.umlsec.viki;

// important imports
import java.util.*;
import org.omg.uml.foundation.core.*;
import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.mdr.IMdrContainer;
import java.util.regex.*;
import open.umlsec.tools.checksystem.gui.SystemVerificationLoader;

public class UmlTypeScanner{

  // Identifies the modelelementtype
  public String modelElementType(String _classname){
    try{
      Pattern pattern = Pattern.compile(".*\\.(.*)\\$Impl");
      Matcher matcher = pattern.matcher(_classname);
      matcher.find();
      return matcher.group(1);
    }
    catch(java.lang.IllegalStateException e){
      System.out.println(e.getMessage());
      return "ERROR";
    }
  }

  //Identifies ID
  public String modelElementID(ModelElement _me){
    try{
      Pattern pattern = Pattern.compile(".*ID: (.*)  MID:.*");
      Matcher matcher = pattern.matcher(_me.toString());
      matcher.find();
      return matcher.group(1);
    }
    catch(java.lang.IllegalStateException e){
      System.out.println(e.getMessage());
      return "ERROR";
    }
  }

  // Identifies the type of the diagram
  public String diagramType(CorePackage _core) {

    boolean classifierRole = false;
    boolean message = false;
    SystemVerificationLoader.logger.trace("Iterating over all diagramelements");
    for(Iterator it = _core.getModelElement().refAllOfType().iterator();it.hasNext();){
      ModelElement me = (ModelElement) it.next();
      if(me != null){
        String typ = this.modelElementType(me.getClass().getName());
        if(!typ.equals("") && !typ.equals("null")){
          System.out.print(typ+" #");

          if(typ.equals("ComponentInstance") || typ.equals("NodeInstance")) return "Deployment Diagram";
          if(typ.equals("ActionState") || typ.equals("PseudoState")) return "Activity Diagram";
          if(typ.equals("Class") || typ.equals("UmlClass")) return "Class Diagram";
          if(typ.equals("UseCase")) return "Use Case Diagram";
          if(typ.equals("Initial")) return "Statechart Diagram";

          if(typ.equals("Message")) message = true;
          if(typ.equals("ClassifierRole")) classifierRole = true;
        }
      }
    }
    if(classifierRole){
      if(message) return "Sequence Diagram";
      else return "Collaboration Diagram";
    }
    
    //Diagramtype couldn't be identified by its components
    System.err.println("Diagramtype couldn't be identified by its components");
    
    return null;
  }

  public boolean identifiable(CorePackage _core){
      return this.diagramType(_core)!=null;
  }

  // Identifies if the type of the diagram is equal to the given Type
  public boolean diagramType(CorePackage _core, DiagramType aType) {
    switch (aType) {
        case DeploymentDiagram:
            return this.diagramType(_core).equals("Deployment Diagram");
        case ActivityDiagram:
            return this.diagramType(_core).equals("Activity Diagram");
        case ClassDiagram:
            return this.diagramType(_core).equals("Class Diagram");
        case UseCaseDiagram:
            return this.diagramType(_core).equals("Use Case Diagram");
        case StatechartDiagram:
            return this.diagramType(_core).equals("Statechart Diagram");
        case SequenceDiagram:
            return this.diagramType(_core).equals("Sequence Diagram");
        case CollaborationDiagram:
            return this.diagramType(_core).equals("Collaboration Diagram");
        default:
            return false;
    }
  }

  // Searches for the Element with the Name _name and compares its type to the parameter _typ
  public boolean isElementType(CorePackage _core, String _name, String _typ){
    for(Iterator it = _core.getModelElement().refAllOfType().iterator();it.hasNext();){
      ModelElement me = (ModelElement) it.next();
      if( this.modelElementType(me.getClass().getName()).equals(_typ) ){
        if(me.getName().equals(_name)){
          return true;
        }
      }
    }
    return false;
  }

  // Filters an UMLseCh Tag for its association to an operation
  public String umlseChOperation(String _value){
    try{
      Pattern pattern = Pattern.compile(".*=(.*)");
      Matcher matcher = pattern.matcher(_value);
      matcher.find();
      return matcher.group(1);
    }
    catch(java.lang.IllegalStateException e){
      System.out.println(e.getMessage());
      return "ERROR";
    }
  }

  //Enumeration
  public static enum DiagramType {
      DeploymentDiagram, ActivityDiagram, ClassDiagram,
      UseCaseDiagram, StatechartDiagram, SequenceDiagram,
      CollaborationDiagram
  }

}

