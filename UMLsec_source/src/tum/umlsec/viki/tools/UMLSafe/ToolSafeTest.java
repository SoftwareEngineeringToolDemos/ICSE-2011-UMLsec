/*
 * Created on 2003-10-12
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package tum.umlsec.viki.tools.UMLSafe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import tum.umlsec.viki.framework.ExceptionProgrammLogicError;
import tum.umlsec.viki.framework.ILogOutput;
import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.mdr.IMdrContainer;
import tum.umlsec.viki.framework.toolbase.CommandDescriptor;
import tum.umlsec.viki.framework.toolbase.CommandParameterDescriptor;
import tum.umlsec.viki.framework.toolbase.IVikiToolBase;
import tum.umlsec.viki.framework.toolbase.IVikiToolConsole;
import tum.umlsec.viki.framework.toolbase.IVikiToolGui;
import tum.umlsec.viki.framework.toolbase.IVikiToolWeb;
import tum.umlsec.viki.tools.UMLSafe.checks.SafeDependencyDiagram;
import tum.umlsec.viki.tools.UMLSafe.checks.SafeLinksDiagram;

/**
 * @author Shunwei
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ToolSafeTest
	implements IVikiToolBase, IVikiToolConsole
{
	private IMdrContainer mdrContainer;
	private Vector commands = new Vector();
	private Vector _parameters = new Vector();
//    private HashMap parameterMap = new HashMap();
	private Vector parametersEmpty = new Vector();
	
	public static ArrayList SLIndexList;
	public static final int CID_SL_XML_FILE = 0;
	public static final int CID_SD_XML_FILE = 1;
	private final int CID_SAFE_LINKS = 2;
    private final int CID_SAFE_DEPENDENCY = 3;

	private CommandDescriptor commandSafeLinks;
    private CommandDescriptor commandSafeDependency;
    private CommandParameterDescriptor safelinksXmlFile = CommandParameterDescriptor.CommandParameterDescriptorFile(CID_SL_XML_FILE, 
    			"Please input the absolute path of the xml file, if the data is already in the UML Diagram, please input " + ToolSafeLinksConstants.NOFILE + " :");
    private CommandParameterDescriptor safedependencyXmlFile = CommandParameterDescriptor.CommandParameterDescriptorFile(CID_SD_XML_FILE, 
				"Please input the absolute path of the xml file:");

	/**
	 * default Constructor
	 *
	 */
	public ToolSafeTest()
	{}
	
	public IVikiToolConsole getConsole()
	{
		return this;
	}
	
	public IVikiToolGui getGui()
	{
		return null;
	}
	
	public IVikiToolWeb getWeb()
	{
		return null;
	}

	public IVikiToolBase getBase()
	{
		return this;
	}
	
	public String getToolName()
	{
		return "UMLsafe Static Check";
	}
	
	public String getToolDescription()
	{
		return "Verifies the model properties of UMLsafe";
	}
	
	public void initialiseBase(IMdrContainer _mdrContainer)
	{
		mdrContainer = _mdrContainer;
        Vector _safelinksXmlFile = new Vector();
        _safelinksXmlFile.add(safelinksXmlFile);
        Vector _safedependencyXmlFile = new Vector();
        _safedependencyXmlFile.add(safedependencyXmlFile);
        commandSafeLinks = CommandDescriptor.CommandDescriptorConsole(CID_SAFE_LINKS, "Safe Links", "Check the safety of the links", true, _safelinksXmlFile);
        commandSafeDependency = CommandDescriptor.CommandDescriptorConsole(CID_SAFE_DEPENDENCY, "Safe Dependency", "Check the safety of the cliet and supplier of the dependency",
                true, _safedependencyXmlFile);
        commands = new Vector();
        commands.add(commandSafeLinks);
        commands.add(commandSafeDependency);
	}

	public void initialiseConsole()
	{
	}
	
	public Iterator getConsoleCommands()
	{
		return commands.iterator();
	}
	
	public void executeConsoleCommand(CommandDescriptor _command, Iterator _parameters, ITextOutput _mainOutput, ILogOutput _auxOutput)
	{
		boolean checker;
        switch(_command.getId())
		{
			case CID_SAFE_LINKS: 
				SafeLinksDiagram safelinksdiagram = new SafeLinksDiagram();
				checker = safelinksdiagram.check(mdrContainer, _parameters, _mainOutput);
			break;
            case CID_SAFE_DEPENDENCY:
                SafeDependencyDiagram safedenpendency = new SafeDependencyDiagram();
                checker = safedenpendency.check(mdrContainer, _parameters, _mainOutput);
            break;
			
			default:
				throw new ExceptionProgrammLogicError("Unknown command");
		}
        _mainOutput.writeLn("********************************************************************************************");
		if (checker)
		{
			_mainOutput.writeLn("**********                The Diagram is successfully checked!!!                  **********");
		}
		else
		{
			_mainOutput.writeLn("**********             The Diagram check is not completely successful!!!           *********");
		}
		_mainOutput.writeLn("********************************************************************************************");
	}
}
