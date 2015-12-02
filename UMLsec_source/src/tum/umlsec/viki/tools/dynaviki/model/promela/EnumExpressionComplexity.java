package tum.umlsec.viki.tools.dynaviki.model.promela;

import tum.umlsec.viki.framework.ExceptionProgrammLogicError;

/**
 * @author pasha
 */
public class EnumExpressionComplexity {
	public static final int Undefined = 1;
	public static final int Simple = 2;
	public static final int Complex = 3;
	
	
	public static String getComplexity(int _c) {
		switch(_c) {
			case Undefined:
				return "Undefined";
				
			case Simple:
				return "Simple";
				
			case Complex:
				return "Complex"; 
				
			default:
				throw new ExceptionProgrammLogicError("Unknown complexity value in EnumExpressionComplexity::getComplexity");
		}
	}
}


