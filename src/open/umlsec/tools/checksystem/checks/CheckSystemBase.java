/**
 * 
 */
package open.umlsec.tools.checksystem.checks;

import java.util.Iterator;

import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.mdr.IMdrContainer;

/**
 * @author ska
 *
 */
public abstract class CheckSystemBase {
	
	public abstract boolean check(IMdrContainer _mdrContainer, 
								Iterator _parameters, ITextOutput _textOutput);

	public abstract boolean check(IMdrContainer _mdrContainer, ITextOutput _textOutput);
}
