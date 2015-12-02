package tum.umlsec.viki.framework;

/**
 * @author pasha
 */
public interface ILogOutput {
	void appendLog(String _s);
	void appendLogLn(String _s);
	void appendLogLn();
}
