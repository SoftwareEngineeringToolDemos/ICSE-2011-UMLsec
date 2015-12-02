package tum.umlsec.viki.framework.toolbase;

import java.util.Iterator;
import java.util.Vector;

/**
 * @author pasha
 */
public class CommandDescriptor {
	/**
	 * constructor for Console mode
	 * 
	 * @param _id
	 * @param _name
	 * @param _description
	 * @param _enabled
	 * @param _parameters
	 */
	private CommandDescriptor(int _id, String _name, String _description,
			boolean _enabled, Vector _parameters) {
		id = _id;
		name = _name;
		description = _description;
		enabled = _enabled;
		parameters = _parameters;
	}

	/**
	 * "constructor" for GUI mode
	 * 
	 * @param _id
	 * @param _name
	 * @param _description
	 * @param _enabled
	 * @param _parameters
	 * @return
	 */
	public static CommandDescriptor CommandDescriptorConsole(int _id,
			String _name, String _description, boolean _enabled,
			Vector _parameters) {
		return new CommandDescriptor(_id, _name, _description, _enabled,
				_parameters);
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public Iterator getParameters() {
		return parameters.iterator();
	}

	private int id;
	private String name;
	private String description;
	private boolean enabled;
	private Vector parameters;
}
