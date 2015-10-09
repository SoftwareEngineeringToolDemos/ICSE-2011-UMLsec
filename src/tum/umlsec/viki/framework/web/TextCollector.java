package tum.umlsec.viki.framework.web;

import tum.umlsec.viki.framework.ILogOutput;
import tum.umlsec.viki.framework.ITextOutput;

/**
 * @author pasha
 */
public class TextCollector implements ITextOutput, ILogOutput {

	public void write(String _s) {			sb.append(_s); }
	public void writeLn(String _s) {		sb.append(_s); sb.append("<br>"); }
	public void writeLn() {					sb.append("<br>"); }

	public void appendLog(String _s) {		sb.append(_s); }
	public void appendLogLn(String _s) {	sb.append(_s); sb.append("<br>"); }
	public void appendLogLn() {				sb.append("<br>"); }
	
	public String getText() { return sb.toString(); }

	StringBuffer sb = new StringBuffer();
}
