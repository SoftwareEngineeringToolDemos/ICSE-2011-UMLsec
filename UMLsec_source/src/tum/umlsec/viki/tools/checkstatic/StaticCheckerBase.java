package tum.umlsec.viki.tools.checkstatic;

//import java.io.File;

import java.util.Iterator;

import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.mdr.IMdrContainer;

/**
 * @author Shasha Meng
 */
public abstract class StaticCheckerBase {
	
	public abstract boolean check(IMdrContainer _mdrContainer, Iterator _parameters, ITextOutput _textOutput);
	
	/*ITextOutput log;
	File _tmpFile = null;
	String fileName;
	
	public void loadXMLFile(String fileName)
	{
		this.fileName = fileName;
		FileInputStream inStream;
		_tmpFile = new File(fileName);
		inStream = new FileInputStream(_tmpFile);
		String xmlDocumentPath = "file: " + _tmpFile.getAbsolutePath();
		
			try
			{
			
				_tmpFile = new File(adresse);
				if(_tmpFile.exists())
				{
					log.writeLn("Das schreiben der XML-Datei war erfolgreich.");
				}
			}
			catch(Exception ee)
			{
				System.out.println(ee);
			}
		
		}
		*/
}

