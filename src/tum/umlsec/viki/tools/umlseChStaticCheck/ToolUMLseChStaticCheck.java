// define the packagefolder
package tum.umlsec.viki.tools.umlseChStaticCheck;

// import your check methodes classes
import tum.umlsec.viki.tools.umlseChStaticCheck.checks.*;

// important imports
import java.util.Iterator;
import java.util.Vector;
import tum.umlsec.viki.framework.ILogOutput;
import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.mdr.IMdrContainer;
import tum.umlsec.viki.framework.toolbase.CommandDescriptor;
import tum.umlsec.viki.framework.toolbase.IVikiToolBase;
import tum.umlsec.viki.framework.toolbase.IVikiToolConsole;
import tum.umlsec.viki.framework.toolbase.IVikiToolGui;
import tum.umlsec.viki.framework.toolbase.IVikiToolWeb;


// class to integrate the tool into the framework
public class ToolUMLseChStaticCheck implements IVikiToolBase, IVikiToolConsole {

  // parameters to identify check methodes
  public static final int CID_SECUREDEPENDENCY = 1;
  public static final int CID_SECUREDEPENDENCY2NDMODEL = 2;
  // ...
  // depth of the check methodes ids (not sure)
  public static final int CPID_DEPTH = 3;

  Vector commands = new Vector();
  IMdrContainer mdrContainer;
  boolean returnValue=false;
  Vector parametersEmpty = new Vector();

   // method to return the instance that implements the interface for the console
  public IVikiToolConsole getConsole() { return this; }

  // method to return the instance that implements the interface for the GUI
  public IVikiToolGui getGui() { return null; }

  // method to return the instance that implements the interface for the web
  public IVikiToolWeb getWeb() { return null; }

  // method to return the instance that implements the basic functions of the  tool
  public IVikiToolBase getBase() {return this;}

  // method to return the name of the tool
  public String getToolName() {return "UMLseCh Static Check";}

  // method to return the desription of the tool
  public String getToolDescription() {return "To be continued";}

  // method to initialize the tool
  public void initialiseBase(IMdrContainer _mdrContainer)
  {
    mdrContainer = _mdrContainer;
    commands.add(secureDependency);
    commands.add(secureDependency2ndModel);
  }

  // method to initialize the console
  public void initialiseConsole() {
  }

  // method to return the possible commands of the console
  public Iterator getConsoleCommands() {
    //commands.add(cmdALL);
    return commands.iterator();
  }

  // method to execute the chosen commands of the console
  public void executeConsoleCommand(CommandDescriptor _command, Iterator _parameters, ITextOutput _mainOutput, ILogOutput _auxOutput) {
    // Switch to the ID of the method which is selected
    switch(_command.getId()) {
      // static variable with check method ID
      case CID_SECUREDEPENDENCY:
        // calls the checkmethod with all necessary parameters and
        // returns the result of the check
        returnValue = new SecureDependency().check(mdrContainer, _parameters, _mainOutput);
        break;
        case CID_SECUREDEPENDENCY2NDMODEL:
        returnValue = new SecureDependency2ndModel().check(mdrContainer, _parameters, _mainOutput);
        
        break;
      
      default:
        // default return value
        returnValue = false;
    }
  }
  // setting properties of the check methodes
  CommandDescriptor secureDependency = CommandDescriptor.CommandDescriptorConsole(CID_SECUREDEPENDENCY, "Secure Dependency", "Secure Dependency", true, parametersEmpty);
  CommandDescriptor secureDependency2ndModel = CommandDescriptor.CommandDescriptorConsole(CID_SECUREDEPENDENCY, "Secure Dependency (2nd model necessary)", "Secure Dependency (2nd model necessary)", true, parametersEmpty);
}