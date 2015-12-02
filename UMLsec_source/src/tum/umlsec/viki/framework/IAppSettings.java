package tum.umlsec.viki.framework;


/**
 * @author pasha
 */
public interface IAppSettings {
	String getToolSetting(String _toolName, String _settingName, String _default);
	void setToolSetting(String _toolName, String _settingName, String _value);
	
	int getToolSetting(String _toolName, String _settingName, int _default);
	void setToolSetting(String _toolName, String _settingName, int _value);
	
}
