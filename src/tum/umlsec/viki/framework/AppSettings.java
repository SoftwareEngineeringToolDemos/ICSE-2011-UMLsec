/**
 * Created by IntelliJ IDEA.
 * User: pasha
 * Date: Mar 26, 2003
 * Time: 3:48:55 PM
 * To change this template use Options | File Templates.
 */
package tum.umlsec.viki.framework;

import java.awt.Rectangle;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * @author pasha
 *
 * A repository for storing various program settings in a file across program runs.
 */
public class AppSettings implements IAppSettings {
    private final String STORAGE = "umlsec.properties";

	private final String PROP_MODELDIR = "ModelDirectory";
	private final String PROP_MAXIMIZED = "Maximized";
	private final String PROP_WINDOWLEFT = "WindowLeft";
	private final String PROP_WINDOWTOP = "WindowTop";
	private final String PROP_WINDOWWIDTH = "WindowWidth";
	private final String PROP_WINDOWHEIGHT = "WindowHeight";
	private final String PROP_SPLITTERPOSITION = "SplitterPosition";


    public AppSettings() {
        loadProperties();
    }
    private void loadProperties() {
        try {
            InputStream in = new FileInputStream(STORAGE);
            props.load(in);
            in.close();
        } catch (IOException x) {
            System.out.println("!!!! Error loading properties");
            x.printStackTrace();
        }
    }
    public void saveProperties() {
        try {
            OutputStream storage = new FileOutputStream(STORAGE, false);
            props.store(storage, "UMLsec application properties");
            storage.close();
        } catch (IOException x) {
            System.out.println("!!!! Error saving properties");
            x.printStackTrace();
        }
    }


    public String getModelDirectory() {
        return props.getProperty(PROP_MODELDIR, System.getProperty("user.home"));
    }
    public void setModelDirectory(String dir) {
        props.setProperty(PROP_MODELDIR, dir);
        saveProperties();
    }


	public int getSplitterPosition() {
		return Integer.parseInt(props.getProperty(PROP_SPLITTERPOSITION, "300"));
	}
	public void setSplitterPosition(int _splitterPosition) {
		props.setProperty(PROP_SPLITTERPOSITION, (new Integer(_splitterPosition)).toString());
		saveProperties();
	}


    public boolean getMaximized() {
    	String _ms = props.getProperty(PROP_MAXIMIZED, "0");
    	
    	if(_ms.compareTo("0") == 0) {
    		return false; 
    	} else {
    		return true;
    	}	
    }
    public void setMaximized(boolean _maximized) {
		props.setProperty(PROP_MAXIMIZED, _maximized? "1": "0");
		saveProperties();
    }
    

	public Rectangle getWindowRec() {
		Rectangle _r = new Rectangle();
		
		
		_r.x = Integer.parseInt(props.getProperty(PROP_WINDOWLEFT, "70"));
		_r.y = Integer.parseInt(props.getProperty(PROP_WINDOWTOP, "70"));
		_r.width = Integer.parseInt(props.getProperty(PROP_WINDOWWIDTH, "700"));
		_r.height = Integer.parseInt(props.getProperty(PROP_WINDOWHEIGHT, "500"));
		
		if(_r.x < 50 || _r.y < 50 || _r.width < 50 || _r.height < 50) {
			_r.x = 70;
			_r.y = 70;
			_r.width = 700;
			_r.height = 500;
		}
		
		return _r;
	}
    
    
    public void setWindowRec(Rectangle _r) {
		props.setProperty(PROP_WINDOWLEFT, (new Integer(_r.x)).toString());
		props.setProperty(PROP_WINDOWTOP, (new Integer(_r.y)).toString());
		props.setProperty(PROP_WINDOWWIDTH, (new Integer(_r.width)).toString());
		props.setProperty(PROP_WINDOWHEIGHT, (new Integer(_r.height)).toString());
		
		saveProperties();
    }
    


	public String getToolSetting(String _toolName, String _settingName, String _default) {
		return props.getProperty(_toolName + "." + _settingName, _default);
	}
	public void setToolSetting(String _toolName, String _settingName, String _value) {
		props.setProperty(_toolName + "." + _settingName, _value);
		saveProperties();
	}
	
	public int getToolSetting(String _toolName, String _settingName, int _default) {
		return Integer.parseInt(props.getProperty(_toolName + "." + _settingName, (new Integer(_default)).toString()));
		
		
		
	}
	public void setToolSetting(String _toolName, String _settingName, int _value) {
		props.setProperty(_toolName + "." + _settingName, (new Integer(_value)).toString());
		saveProperties();
	}
	






    Properties props =  new Properties();

}

