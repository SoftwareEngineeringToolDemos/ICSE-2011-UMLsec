/**
 * 
 */
package open.umlsec.tools.checksystem.gui;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * @author ska
 *
 */
public class FileFilterXML extends FileFilter {

	/* (non-Javadoc)
	 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
	 */
	public boolean accept(File f) {
		boolean accept = f.isDirectory();
		if(!accept) {
			String suffix = getSuffix(f);
			if(suffix != null) {
				if(suffix.equalsIgnoreCase("xml")) {
					accept = true;
				}
			}
		}
		return accept;
	}

	/* (non-Javadoc)
	 * @see javax.swing.filechooser.FileFilter#getDescription()
	 */
	public String getDescription() {
		return "Requirements file (*.xml)";
	}

	public static String getSuffix(File f) {
		String s = f.getPath();
		String suffix = null;

		int i = s.lastIndexOf('.');
		if(i > 0 && i < s.length() - 1) {
			suffix = s.substring(i + 1).toLowerCase();
		}
		return suffix;
	}
	
	
}
