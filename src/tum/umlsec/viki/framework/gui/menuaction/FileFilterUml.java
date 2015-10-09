package tum.umlsec.viki.framework.gui.menuaction;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * @author pasha
 *
 */
public class FileFilterUml extends FileFilter {
	public boolean accept(File f) {
		boolean accept = f.isDirectory();
		if(!accept) {
			String suffix = getSuffix(f);
			if(suffix != null) {
				if(suffix.equalsIgnoreCase("xmi") || suffix.equalsIgnoreCase("zargo")) {
					accept = true;
				}
			}
		}
		return accept;
	}

	public String getDescription() {
		return "UMLsec Models (*.xmi, *.zargo)";
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
