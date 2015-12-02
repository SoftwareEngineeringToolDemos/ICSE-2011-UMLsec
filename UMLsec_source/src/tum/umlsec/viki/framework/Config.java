package tum.umlsec.viki.framework;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This class contains the configuration, as recorded in the configuration file. It's a static class that provides (read/write) access
 * to configuration entries, as well as read and write operations on the configuration file
 * @author Lionel Montrieux
 *
 */
public class Config {
	public static File configFile = null;
	
	private static Map<String, String> properties = null;
	
	/**
	 * Builds a list of available properties. New properties names should be added here
	 */
	private static void listProperties() {
		properties = new HashMap<String, String>();
		properties.put("prolog_path", "");
	}
	
	private static String defaultConfFile = "umlsec.conf";
	
	public static void readConfig(File file) throws IOException, BadConfSyntaxException {
		listProperties();
		// We use the default file if none specified
		if (file == null)
			file = new File(defaultConfFile);
		// If the file doesn't exists, we throw an exception
		if (!file.exists())
			throw new IOException("File " + file.toString() + " doesn't exists");
		// we throw an exception if file is a directory
		if (file.isDirectory())
			throw new IOException(file.toString() + " is a directory");
		if (!file.canRead())
			throw new IOException("Can't read configuration file " + file.toString());

		FileReader input = new FileReader(file);
		BufferedReader buffer = new BufferedReader(input);
		String line;
		line = buffer.readLine();
		while (line != null) {
			line = line.trim();
			// lines starting with a "#" are comments. We also ignore empty lines
			if (!line.startsWith("#") && !line.isEmpty()) {
				String[] item = line.split("=");
				if (item.length != 2)
					throw new BadConfSyntaxException("syntax should be : <key> <value>");
				
				if (properties.containsKey(item[0].trim()))
					properties.put(item[0].trim(), item[1].trim());
				else
					throw new BadConfSyntaxException("property " + item[0] + " isn't valid");
			}
			line = buffer.readLine();
		}	
	}
	
	public static void writeConfig(File file) throws IOException {
		if (file == null) {
			file = new File(defaultConfFile);
		}
		if (file.isDirectory())
			throw new IOException(file.toString() + " is a directory");
		FileWriter output = new FileWriter(file);
		for (Iterator<Entry<String, String>> iter = properties.entrySet().iterator(); iter.hasNext(); ) {
			Entry<String, String> entry = iter.next();
			output.write(entry.getKey() + " = " + entry.getValue() + System.getProperty("line.separator"));
		}
		output.close();
	}
	
	/**
	 * Returns the value of a specified property
	 * @param key the name of the property
	 * @return its value, or null if the property doesn't exist. If the property isn't set, returns an empty String
	 */
	public static String getProperty(String key) {
		if (properties == null)
			listProperties();
		return properties.get(key);
	}
	
	/**
	 * Allows one to set the value of a property
	 * @param key the property name
	 * @param value its value
	 * @throws UnknownPropertyException if the property doesn't exist
	 */
	public static void setProperty(String key, String value) throws UnknownPropertyException {
		if (properties == null)
			listProperties();
		if (!properties.containsKey(key))
			throw new UnknownPropertyException("This property doesn't exists : " + key);
		properties.put(key, value);
	}
}
