package tum.umlsec.viki.tools;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import org.aspectj.lang.*;


public aspect TraceAspect {
	Logger logger = Logger.getLogger("Trace Aspect");
	
	TraceAspect() {
		logger.setLevel(Level.ALL);
	}
	
	pointcut traceToolMethods()
		: execution(* tum.umlsec.viki.tools..*.*(..)) 
		&& !execution(* tum.umlsec.viki.tools.dynaviki..*.*(..)) 
		&& !execution(* tum.umlsec.viki.tools.mdrview..*.*(..))
		&& !execution(* tum.umlsec.viki.tools.jmapper..*.*(..))
		&& !execution(* *.getConsole(..))
		&& !execution(* *.getConsoleCommands(..))
		&& !execution(* *.getGui(..))
		&& !execution(* *.getToolName(..))
		&& !execution(* *.initialiseConsole(..))
		&& !execution(* *.initialiseBase(..))
		&& !within(TraceAspect);
	
	before() : traceToolMethods() {
		if (logger.isEnabledFor(Level.TRACE)) {
			Signature sig = thisJoinPointStaticPart.getSignature();
			logger.log(Level.TRACE,
					"Entering ["
					+ sig.getDeclaringType().getName() + "."
					+ sig.getName() + "]");
		}
	}
}
