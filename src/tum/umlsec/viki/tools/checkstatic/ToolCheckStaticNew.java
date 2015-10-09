package tum.umlsec.viki.tools.checkstatic;


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
import tum.umlsec.viki.tools.checkstatic.checks.CheckerAllAuto;

/**
 * @author Shasha Meng
 */
public class ToolCheckStaticNew implements IVikiToolBase, IVikiToolConsole {


  public static final int CID_ALL = 7;

  Vector commands = new Vector();
  IMdrContainer mdrContainer;
  boolean returnValue=false;
  Vector parametersEmpty = new Vector();

  public IVikiToolConsole getConsole() { return this; }
  public IVikiToolGui getGui() { return null; }
  public IVikiToolWeb getWeb() { return null; }

  public IVikiToolBase getBase() {return this;}


  public String getToolName() {return "UMLsec Static Check II";}
  public String getToolDescription() {return "Verifies static model properties";}



  public void initialiseBase(IMdrContainer _mdrContainer)
  {
    mdrContainer = _mdrContainer;
    commands.add(cmdALL);
    }

  public Iterator getConsoleCommands() {
        //commands.add(cmdALL);
        return commands.iterator();
  }

  public void initialiseConsole() {
      int y = 0;
  }

  public void executeConsoleCommand(CommandDescriptor _command, Iterator _parameters, ITextOutput _mainOutput, ILogOutput _auxOutput) {
    StaticCheckerBase _checker = null;

    switch(_command.getId()) {


      case CID_ALL:
        _checker = new CheckerAllAuto();
      break;
      default:
        // TODO throw something nice
    }

    returnValue=_checker.check(mdrContainer, _parameters, _mainOutput);
  }

  CommandDescriptor cmdALL = CommandDescriptor.CommandDescriptorConsole(CID_ALL, "Automation", "Check automatically", true, parametersEmpty);

}