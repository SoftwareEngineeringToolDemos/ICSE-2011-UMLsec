package tum.umlsec.viki.framework.web;	

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Iterator;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tum.umlsec.viki.framework.toolbase.CommandDescriptor;
import tum.umlsec.viki.framework.toolbase.CommandParameterDescriptor;
import tum.umlsec.viki.framework.toolbase.IVikiToolBase;
import tum.umlsec.viki.framework.toolbase.IVikiToolWeb;
import tum.umlsec.viki.tools.UMLSafe.ToolSafeTest;
import tum.umlsec.viki.tools.activityparser.ToolActivity;
import tum.umlsec.viki.tools.berechtigungen.ToolBerechtigungen;
import tum.umlsec.viki.tools.berechtigungen2.ToolBerechtigungen2;
import tum.umlsec.viki.tools.checkstatic.ToolCheckStatic;
import tum.umlsec.viki.tools.checkstatic.ToolCheckStaticNew;
import tum.umlsec.viki.tools.dynaviki.VikiToolDynamic;
import tum.umlsec.viki.tools.jmapper.VikiToolJmapper;
import tum.umlsec.viki.tools.mdrview.VikiToolMdrViewer;
import tum.umlsec.viki.tools.permission.PermissionChecks;
import tum.umlsec.viki.tools.sapperm.ToolSAPPerm;
import tum.umlsec.viki.tools.sequence2prolog.ToolSequence2Prolog;
import tum.umlsec.viki.tools.sequenceanalyser.ToolSequenceAnalyser;
import tum.umlsec.viki.tools.sequencediagram.ToolSequenceDiagramWeb;
import tum.umlsec.viki.tools.sequenceparser.ToolSequence;
import tum.umlsec.viki.tools.statechart2prolog.ToolStatechart2Prolog;
import tum.umlsec.viki.tools.statechartexport.ToolExport;
import tum.umlsec.viki.tools.statechartparser.ToolStatechart;
import tum.umlsec.viki.tools.subsystemparser.ToolSubsystem;
import tum.umlsec.viki.tools.uml2java.ToolJAVACodeGenerator;
import tum.umlsec.viki.tools.umlseChNotationAnalyser.ToolUMLseChNotationAnalyser;
import tum.umlsec.viki.tools.umlseChStaticCheck.ToolUMLseChStaticCheck;
import tum.umlsec.viki.tools.umlsecNotationAnalyser.ToolUMLsecNotationAnalyser;

import com.oreilly.servlet.MultipartRequest;

/**
 * @author pasha
 */
@SuppressWarnings("serial")
public class LoaderWeb extends HttpServlet {
	private static final String RP_SELECTED_TOOL = "SelectedTool";
	private static final String RP_SELECTED_COMMAND = "SelectedCommand";
	
	
	
	private static final String PAGEID = "PageId";
	private static final int PAGEID_FILE_AND_TOOL = 1;
	private static final int PAGEID_COMMAND = 2;
	private static final int PAGEID_PARAMETERS = 3;
//	private static final int PAGE
	
	
	
	private void StartPage(PrintWriter _writer) {
		_writer.println("<html>");
		_writer.println("<head>");
		_writer.println("<title>UMLsec model verifier</title>");
		_writer.println("</head>");
		_writer.println("<body bgcolor=white>");
	}
	
	private void EndPage(PrintWriter _writer) {
		_writer.println("<hr><table border=0 width=100%><tr>");
		_writer.println("<td width=30%><p align='left'><a href='mailto:juerjens@in.tum.de'><b>Report a problem</b></a></p></td>");
		_writer.println("<td width=40%><p align='left'><a href='http://www4.in.tum.de/csduml/interface/'><b>Tutorial page</b></a></p></td>");
		_writer.println("<td width=30%><p align='right'><a href='vikiweb'><b>Reset</b></a></p></td>");
		
		_writer.println("</tr></table></body>");
		_writer.println("</html>");
	}
	
	public void init(ServletConfig _config) throws ServletException {
		super.init(_config);
		resourceRoot = _config.getInitParameter("ResourceRoot");
		if(resourceRoot == null) {
			throw new ServletException("The ResourceRoot init parameter must be set to the directory where umlMetamodel folder with UML metamodel can be found.");
		}

		
	}
	
	
	
	
	
	
	
	
	private void InitialiseWebFramework() {
		
		frameworkWeb = new FrameworkWeb();
		tools = new IVikiToolBase[23]; //New Tool
		toolsWeb = new InstalledWebToolDescriptor[23]; //New Tool
		 
		tools[0] = new VikiToolDynamic();
		tools[1] = new ToolCheckStatic();
		tools[2] = new ToolCheckStaticNew();
		tools[3] = new VikiToolMdrViewer();
//		tools[4] = new VikiToolSample();
//		tools[5] = new VikiToolSample2();
		tools[4] = new ToolActivity(); 
		tools[5] = new ToolSequence();
		tools[6] = new ToolStatechart();
		tools[7] = new ToolSubsystem();
		tools[8] = new ToolBerechtigungen();
		tools[9] = new PermissionChecks();
		tools[10] = new ToolExport();
		tools[11] = new ToolSequenceAnalyser();
		tools[12] = new ToolStatechart2Prolog();
		tools[13] = new ToolBerechtigungen2();
		tools[14] = new ToolSafeTest();
		tools[15] = new ToolSequenceDiagramWeb();
		tools[16] = new ToolSAPPerm();
		tools[17] = new ToolSequence2Prolog();
		tools[18] = new VikiToolJmapper();
		tools[19] = new ToolJAVACodeGenerator();
		tools[20] = new ToolUMLsecNotationAnalyser(); //New Tool
        tools[21] = new ToolUMLseChNotationAnalyser(); //New Tool
        tools[22] = new ToolUMLseChStaticCheck(); //New Tool
			  
		frameworkWeb.initialiseBase(tools, resourceRoot, tempFolder);
		
		for(int i = 0; i < tools.length; i++) {
			installTool(i, tools[i]);
		}		
	}

	void CreateTempDirectory() throws IOException {
		File _f = File.createTempFile("viki", "web");
		tempFolder = _f.getAbsolutePath();
		_f.delete();
		_f.mkdir();
	}

	private void installTool(int _index, IVikiToolBase _toolBase) {
		IVikiToolWeb _toolWeb = _toolBase.getWeb();
		if(_toolWeb == null) {
			_toolWeb = new DefaultWebWrapper(_toolBase);
		}
		InstalledWebToolDescriptor _toolDescriptor = new InstalledWebToolDescriptor(_toolWeb);
		toolsWeb[_index] = _toolDescriptor;
		
		_toolWeb.initialiseWeb(); 
	}



	
	private void RenderSection_SelectTool(PrintWriter _writer, String _extraMessage) {
		currentTool = null;
		currentCommand = null;
		if(_extraMessage != null && _extraMessage.length() > 0) {
			_writer.println(_extraMessage);
		}
		_writer.println("<h1>Select a tool:</h1>");
			
		_writer.println("<form method='POST' ENCTYPE='multipart/form-data'>");
		_writer.println("<input type='hidden' name='" + PAGEID + "' value='" + PAGEID_FILE_AND_TOOL + "'>");
		
		_writer.println("Model file: <input type='file' name='modelFile' size='20'><br>");
		_writer.println("<table border='0'>");
		
		
		for (int i = 0; i < frameworkWeb.getTools().length; i++) {
			IVikiToolBase _tool = frameworkWeb.getTools()[i];
			_writer.println("<tr>");
			_writer.println("<td align='right'>" + _tool.getToolName() + "</td>");
			_writer.println("<td width='30'>&nbsp;</td>");
			_writer.println("<td width='20'><input type='radio' name='" + RP_SELECTED_TOOL + "' value='" + asString(i) + "'></td>");
			_writer.println("</tr>");
		}
		_writer.println("</table>");
		_writer.println("<input type='submit' value='Submit'>");
		_writer.println("</form>");
	}
	
	
	@SuppressWarnings("unchecked")
	private void RenderSection_SelectCommand(PrintWriter _writer) {

		
		_writer.println("<h1>Select a command:</h1>");
		_writer.println("<form method='POST'>");
		_writer.println("<input type='hidden' name='" + PAGEID + "' value='" + PAGEID_COMMAND + "'>");
		_writer.println("<table border='0'>");

		for (Iterator _iter = currentTool.getWebCommands(); _iter.hasNext();) {
			CommandDescriptor _command = (CommandDescriptor) _iter.next();
			
			_writer.println("<tr>");
			_writer.println("<td align='right'>" + _command.getName() + "</td>");
			_writer.println("<td width='30'>&nbsp;</td>");
			_writer.print("<td width='20'><input type='radio' name='" + RP_SELECTED_COMMAND + "' value='" + _command.getId() + "' ");
			if(_command.isEnabled() == false) {
				_writer.print("disabled ");
			}
			_writer.println("></td>");
			_writer.println("</tr>");
		}
	
		_writer.println("</table>");
		
		_writer.println("<input type='submit' value='Submit'>");
		_writer.println("</form>");
	}

	String getParameterName(CommandParameterDescriptor _d) {
		return "param" + (new Integer(_d.getId())).toString();
	}

	@SuppressWarnings("unchecked")
	private void RenderSection_CollectParameters(PrintWriter _writer) throws ServletException {	
		_writer.println("<h1>Enter command parameters:</h1>");
		_writer.println("<form method='POST' ENCTYPE='multipart/form-data'>");
		_writer.println("<input type='hidden' name='" + PAGEID + "' value='" + PAGEID_PARAMETERS + "'>");
		_writer.println("<table border='0'>");

		for (Iterator iter = currentCommand.getParameters(); iter.hasNext();) {
			CommandParameterDescriptor _parameter = (CommandParameterDescriptor)iter.next();
			_writer.println("<tr>");
			
			_writer.println("<td align='right'>" + _parameter.getDescription() + " (" + _parameter.getTypeAsString() + "):</td>");
			_writer.println("<td width='30'>&nbsp;</td><td>");

			String _paramName = getParameterName(_parameter);
			switch(_parameter.getType()) {
				case CommandParameterDescriptor.TypeString:
					_writer.println("<input type='text' name='" + _paramName + "' size='20'>");
				break;
				case CommandParameterDescriptor.TypeInteger:
					_writer.println("<input type='text' name='" + _paramName + "' size='20'>");
				break;
				case CommandParameterDescriptor.TypeDouble:
					_writer.println("<input type='text' name='" + _paramName + "' size='20'>");
				break;
				case CommandParameterDescriptor.TypeFile:
					_writer.println("<input type='file' name='" + _paramName + "' size='20'>");
				break;
				default:
					throw new ServletException("Unknown parameter type recived from tool"); 
			}
			_writer.println("</td></tr>");
		}
	
		_writer.println("</table>");
		_writer.println("<input type='submit' value='Submit'>");
		_writer.println("</form>");
		
	}

	/**
	 * Respond to a GET request for the content produced by
	 * this servlet.
	 *
	 * @param request The servlet request we are processing
	 * @param response The servlet response we are producing
	 *
	 * @exception IOException if an input/output error occurs
	 * @exception ServletExc	eption if a servlet error occurs
	 */
	public void doGet(HttpServletRequest _request, HttpServletResponse _response) throws IOException, ServletException {
		currentTool = null;
		currentCommand = null;


		try {
			CreateTempDirectory();
		} catch (IOException x) {
			throw new ServletException("Cannot create temporary directory.");
		}
		InitialiseWebFramework();
		
		

		_response.setContentType("text/html");
		_response.setDateHeader("Expires", System.currentTimeMillis() + 100);
		PrintWriter _writer = _response.getWriter();
		StartPage(_writer);
			RenderSection_SelectTool(_writer, null);
		EndPage( _writer);
	}




	int readPageId(HttpServletRequest _request) throws IOException {
		String _pageId = _request.getParameter(PAGEID);
		if(_pageId == null) {
			currentMultipartRequest = new MultipartRequest(_request, tempFolder);
			_pageId = currentMultipartRequest.getParameter(PAGEID);
		}
		if(_pageId == null) {
			return 0;
		}
		return Integer.parseInt(_pageId);
	}


	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest _request, HttpServletResponse _response) throws IOException, ServletException {
		if(tempFolder == null) {
			throw new ServletException("No temp folder initialise, probably GET request was cashed."); 
		}
		
		_response.setContentType("text/html");
		PrintWriter _writer = _response.getWriter();
		
		StartPage(_writer);
		int _pageId = readPageId(_request);
		
		
		boolean _restart = true;		
		switch(_pageId) {
			case PAGEID_FILE_AND_TOOL:
				String _parameterSelectedTool = currentMultipartRequest.getParameter(RP_SELECTED_TOOL);
				if(_parameterSelectedTool == null) {
					break;
				}
				InstalledWebToolDescriptor _iwtd = toolsWeb[Integer.parseInt(_parameterSelectedTool)]; 
				currentTool = _iwtd.getToolInterface();
				
				Enumeration _files = currentMultipartRequest.getFileNames();
				if(!_files.hasMoreElements()) {
					throw new ServletException("No model file given");
				}
				String _fileParamName = (String)_files.nextElement();
				File _f = currentMultipartRequest.getFile(_fileParamName);
				frameworkWeb.getMdrContainer().loadFromFile(_f); 
				RenderSection_SelectCommand(_writer);
				_restart = false;
			break;
			
			case PAGEID_COMMAND:
				String _parameterSelectedCommand = _request.getParameter(RP_SELECTED_COMMAND);
				if(_parameterSelectedCommand == null) {
					RenderSection_SelectCommand(_writer);
					_restart = false;
					break;					
				}
				int _selectedCommandId = Integer.parseInt(_parameterSelectedCommand);
				currentCommand = null;
				for (Iterator _iter = currentTool.getWebCommands(); _iter.hasNext();) {
					CommandDescriptor _command = (CommandDescriptor)_iter.next();
					if(_command.getId() == _selectedCommandId) {
						currentCommand = _command;
						break;
					}
				}
				if(currentCommand == null) {
					throw new ServletException("No such command");
				}
				
				if(currentCommand.getParameters().hasNext() == false) {
					TextCollector _mainText = new TextCollector();
					TextCollector _auxText = new TextCollector();
					currentTool.executeWebCommand(currentCommand, null, _mainText, _auxText);
					RenderSection_SelectCommand(_writer);
					_writer.print("<hr>");
					_writer.print("<pre>");
					_writer.print(_mainText.getText());
					_writer.print("</pre>");
					_writer.print("<hr>");
					_writer.print("<pre>");
					_writer.print(_auxText.getText());
					_writer.print("</pre>");
				} else {
					RenderSection_CollectParameters(_writer);
				}
				_restart = false; 
			break;
			
			case PAGEID_PARAMETERS:
				for (Iterator iter = currentCommand.getParameters(); iter.hasNext();) {
					CommandParameterDescriptor _parameter = (CommandParameterDescriptor)iter.next();
					String _paramName = getParameterName(_parameter);
					switch(_parameter.getType()) {
						case CommandParameterDescriptor.TypeString:
							String _stringParameter = currentMultipartRequest.getParameter(_paramName); 
							_parameter.setValue(_stringParameter);
						break;
						case CommandParameterDescriptor.TypeInteger:
							String _intParameter = currentMultipartRequest.getParameter(_paramName); 
							_parameter.setValue(new Integer(_intParameter));
						break;
						case CommandParameterDescriptor.TypeDouble:
							String _doubleParameter = currentMultipartRequest.getParameter(_paramName); 
							_parameter.setValue(new Double(_doubleParameter));
						break;
						case CommandParameterDescriptor.TypeFile:
							File _fileParameter = currentMultipartRequest.getFile(_paramName);
							_parameter.setValue(_fileParameter);
						break;
						default:
							throw new ServletException("Unknown parameter type recived from tool"); 
					}
				}

				TextCollector _mainText = new TextCollector();
				TextCollector _auxText = new TextCollector();
				currentTool.executeWebCommand(currentCommand, currentCommand.getParameters(), _mainText, _auxText);
				RenderSection_SelectCommand(_writer);
				_writer.print("<hr>");
				_writer.print("<pre>");
				_writer.print(_mainText.getText());
				_writer.print("</pre>");
				_writer.print("<hr>");
				_writer.print("<pre>");
				_writer.print(_auxText.getText());
				_writer.print("</pre>");
				_restart = false;
			
			break;			
		}

		if(_restart == true) {
			RenderSection_SelectTool(_writer, "Please start anew.");
		}
		
		
		EndPage(_writer);
		return;
		
		
		
		
		
		
		
		
		
		

		
		
//		
//		
//		
//		
//		
//		
//		
//		
//		
//// RENDER SELECT COMMAND PAGE		
//		
//
//
//		
//		DisplayParameters(_writer, _request);
//		EndPage( _writer);
	}





	/**
	 * @param _writer
	 * @param _request
	 */
	@SuppressWarnings({ "unused", "unchecked" })
	private void DisplayParameters(PrintWriter _writer, HttpServletRequest _request) {
		_writer.println("<h1>DEBUG: Request parameters</h1>");
				
		for(Enumeration e = _request.getParameterNames() ; e.hasMoreElements() ;) {
			String _name = (String)e.nextElement();
			_writer.println(_name + ": " + _request.getParameter( _name) + "<br>");
		}
	}



	String asString(int _value) {
		return (new Integer(_value)).toString(); 
	}



	IVikiToolBase []tools;
	InstalledWebToolDescriptor [] toolsWeb;

	IVikiToolWeb currentTool;
	CommandDescriptor currentCommand;

	FrameworkWeb frameworkWeb;
	
	String resourceRoot;
	
	MultipartRequest currentMultipartRequest;
	String tempFolder = null;
}



