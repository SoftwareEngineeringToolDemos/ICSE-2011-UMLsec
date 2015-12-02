package tum.umlsec.viki.framework.toolbase.parameterreader.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.toolbase.CommandParameterDescriptor;
import tum.umlsec.viki.framework.toolbase.parameterreader.ParameterBASE;

/**
 * @author pasha
 */
public abstract class ParameterConsoleBASE extends ParameterBASE {
	public ParameterConsoleBASE(ITextOutput _textOutput) {
		textOutput = _textOutput;
	}

	public abstract boolean read(CommandParameterDescriptor _parameter);

	protected String readLine() {
		InputStreamReader inputStreamReader = new InputStreamReader ( System.in );
		BufferedReader stdin = new BufferedReader ( inputStreamReader );
		try {
			return stdin.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}

	ITextOutput textOutput;
}
