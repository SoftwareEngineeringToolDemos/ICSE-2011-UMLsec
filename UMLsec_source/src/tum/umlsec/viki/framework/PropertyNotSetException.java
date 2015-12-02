package tum.umlsec.viki.framework;

public class PropertyNotSetException extends Exception {
	public PropertyNotSetException() {
		super();
	}
	
	public PropertyNotSetException(String message) {
		super(message);
	}
}
