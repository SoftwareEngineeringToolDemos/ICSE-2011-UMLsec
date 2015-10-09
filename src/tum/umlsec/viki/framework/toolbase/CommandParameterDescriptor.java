package tum.umlsec.viki.framework.toolbase;

import java.io.File;

import tum.umlsec.viki.framework.ExceptionProgrammLogicError;




/**
 * @author pasha
 */
public class CommandParameterDescriptor {
	public static final int TypeString = 1;
	public static final int TypeFile = 2;
	public static final int TypeInteger = 3;
	public static final int TypeDouble = 4;
	
	
	private CommandParameterDescriptor(int _id, int _type, String _description) {
		id = _id;
		type = _type; 
		description = _description;
	}
	private CommandParameterDescriptor(int _id, int _type, String _description, int _min, int _max) {
		id = _id;
		type = _type; 
		description = _description;
		minInt = _min;
		maxInt = _max;
	}
	private CommandParameterDescriptor(int _id, int _type, String _description, double _min, double _max) {
		id = _id;
		type = _type; 
		description = _description;
		minDouble = _min;
		maxDouble = _max;
	}	
	
	public static CommandParameterDescriptor CommandParameterDescriptorString(int _id, String _description) {
		return new CommandParameterDescriptor(_id, TypeString, _description);
	}
	public static CommandParameterDescriptor CommandParameterDescriptorFile(int _id, String _description) {
		return new CommandParameterDescriptor(_id, TypeFile, _description);
	}
	public static CommandParameterDescriptor CommandParameterDescriptorInteger(int _id, String _description, int _min, int _max) {
		return new CommandParameterDescriptor(_id, TypeInteger, _description, _min, _max);
	}
	public static CommandParameterDescriptor CommandParameterDescriptorDouble(int _id, String _description, double _min, double _max) {
		return new CommandParameterDescriptor(_id, TypeDouble, _description, _min, _max);
	}

	public int getId() {
		return id;	
	}
	public String getDescription() {
		return description;
	}
	public int getType() {
		return type;
	}
	

	public Object getValue() { return value; }
	public void setValue(Object _value) {
		switch(type) {
			case TypeString:
				if(!(_value instanceof String)) {
					throw new ExceptionProgrammLogicError("Wrong Command Parameter type: not a String");
				}
			break;
			
			case TypeFile:
				if(!(_value instanceof File)) {
					throw new ExceptionProgrammLogicError("Wrong Command Parameter type: not a File");
				}
			break;
			
			case TypeInteger:
				if(!(_value instanceof Integer)) {
					throw new ExceptionProgrammLogicError("Wrong Command Parameter type: not an Int");
				}
			break;
			
			case TypeDouble:
				if(!(_value instanceof Double)) {
					throw new ExceptionProgrammLogicError("Wrong Command Parameter type: not a Double");
				}
			break;

			default:			
				throw new ExceptionProgrammLogicError("Unknown Command Parameter type");
		}
		value = _value; 
	}

	public String getAsString() {	return (String)value;					}
	public File getAsFile() {		return (File)value;						}
	public int getAsInteger() {		return ((Integer)value).intValue();		}
	public double getAsDouble() {	return ((Double)value).doubleValue();	}
	
	public String getTypeAsString() {
		switch(type) {
			case TypeString:
				return "String";
			
			case TypeFile:
				return "File"; 
							
			case TypeInteger:
				return "Integer";
			
			case TypeDouble:
				return "Double";
			
			default:
				throw new ExceptionProgrammLogicError("Unknown type in CommandParameterDescriptor::getTypeAsString");
		}
	}


	int id;
	String description;
	int type;
	
	int minInt;
	int maxInt;
	
	double minDouble;
	double maxDouble;
	
	Object value;
}
