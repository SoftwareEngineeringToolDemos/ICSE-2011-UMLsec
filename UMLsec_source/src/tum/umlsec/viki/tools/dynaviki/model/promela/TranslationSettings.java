package tum.umlsec.viki.tools.dynaviki.model.promela;

import tum.umlsec.viki.framework.ExceptionProgrammLogicError;

/**
 * @author pasha
 */
public class TranslationSettings {
	
	// TODO read / write settings
		
	// TODO modify settings 
	
	public static final int WarningExpressionNotUmlsecSkipped = 0;
	public static final int WarningExpressionNotUmlsecParsed = 1;
	public static final int WarningInitialValueNotUmlsecSkipped = 2;
	public static final int WarningInitialValueNotUmlsecParsed = 3;
	public static final int WarningConstantWithoutInitialValue = 4;
	public static final int WarningVariableWithoutInitialValue = 5;
	
	
	public static final String WSExpressionNotUmlsecSkipped = "Not a UMLsec expression, skipping";
	public static final String WSExpressionNotUmlsecParsed = "Not a UMLsec expression, parsing";
	public static final String WSInitialValueNotUmlsecSkipped = "Initial value is not a UMLsec expression, skipping";
	public static final String WSInitialValueNotUmlsecParsed = "Initial value is not a UMLsec expression, parsing";
	public static final String WSConstantWithoutInitialValue = "Constant doesn't have initial value. Assuming DV_GARBAGE";
	public static final String WSVariableWithoutInitialValue = "Variable doesn't have initial value. Assuming DV_GARBAGE";
	
	public static final int WarningsTotal = 10;

	

	public TranslationSettings() {
		init();
	}
	
	public void init() {
		
// warnings		
		warnings = new boolean[WarningsTotal];
		warningStrings = new String[WarningsTotal];
		
		warnings[WarningExpressionNotUmlsecSkipped] = true;
		warnings[WarningExpressionNotUmlsecParsed] = true;
		warnings[WarningInitialValueNotUmlsecSkipped] = true;
		warnings[WarningInitialValueNotUmlsecParsed] = false;
		warnings[WarningConstantWithoutInitialValue] = true;
		warnings[WarningVariableWithoutInitialValue] = false;
		
		warningStrings[WarningExpressionNotUmlsecSkipped] = WSExpressionNotUmlsecSkipped;
		warningStrings[WarningExpressionNotUmlsecParsed] = WSExpressionNotUmlsecParsed;
		warningStrings[WarningInitialValueNotUmlsecSkipped] = WSExpressionNotUmlsecSkipped;
		warningStrings[WarningInitialValueNotUmlsecParsed] = WSExpressionNotUmlsecParsed;
		warningStrings[WarningConstantWithoutInitialValue] = WSConstantWithoutInitialValue;
		warningStrings[WarningVariableWithoutInitialValue] = WSVariableWithoutInitialValue; 


// settings		
   		settingEatNonUmlsecExpressions = false;	
   		settingEatNonUmlsecInitialValues = true;
		settingDefineConstVariables = true;
		
	}
	
	
////////// =========================================== WARNINGS	
  public boolean getWarningEnabled(int _index) {
	  if(_index < 0 || _index >= WarningsTotal) {
		  throw new ExceptionProgrammLogicError("Incorrect warning index in TranslationSettings::getWarningEnabled");
	  }
	  return warnings[_index];
  }
	
  public String getWarningText(int _index) {
	  if(_index < 0 || _index >= WarningsTotal) {
		  throw new ExceptionProgrammLogicError("Incorrect warning index in TranslationSettings::getWarningText");
	  }
	  return warningStrings[_index];
  }
	
	
////////// =========================================== SETTINGS	
  public boolean eatNonUmlsecExpressions() {
	  return settingEatNonUmlsecExpressions ;
  }
	
  public boolean eatNonUmlsecInitialValues() {
	  return settingEatNonUmlsecInitialValues;
  }
	
  public boolean defineConstVariables() {
	  return settingDefineConstVariables;
  }
	
	
	
	
	
	
//////// ---------------------------------------------------------------------------------------------	
	
	boolean [] warnings;
	String [] warningStrings;
	
	// if true, we will consume all expressions which we find in the model
	// if false, we eat only expressions for which the model specifies "umlsec" language 
	boolean settingEatNonUmlsecExpressions;
	
	// if true, we will consume all variable initial values which we find in the model
	// if false, we eat only variable initial values for which the model specifies "umlsec" language 
	boolean settingEatNonUmlsecInitialValues;
	
	// if true, we replace the constant variable declarations with DEFINEs
	// (i believe it shall reduce complexity of the promela code)
	boolean settingDefineConstVariables;
	
	
}
