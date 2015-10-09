// define the packagefolder
package tum.umlsec.viki.tools.umlseChStaticCheck.checks;

// important imports
import java.util.*;

import org.apache.log4j.Logger;
import org.omg.uml.foundation.core.*;
import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.mdr.IMdrContainer;
import tum.umlsec.viki.tools.checkstatic.StaticCheckerBase;
import tum.umlsec.viki.UmlTypeScanner;

import org.omg.uml.behavioralelements.activitygraphs.*;
import org.omg.uml.behavioralelements.collaborations.*;
import org.omg.uml.behavioralelements.statemachines.*;

import java.awt.event.*;
import java.io.File;

import tum.umlsec.viki.framework.mdr.MdrContainer;
import javax.swing.JFileChooser;
import tum.umlsec.viki.framework.gui.menuaction.FileFilterUml;

import open.umlsec.tools.checksystem.gui.SystemVerificationLoader;

// Checking Class extends the StaticCheckerBase - forces to implement the "check"-Method
public class SecureDependency2ndModel extends StaticCheckerBase {
	private static Logger logger=Logger.getLogger("umlseChStaticCheck");

    //Output Textbox Stream
	ITextOutput textOutput;

    // DumpAllModelElements check-method
    public boolean check(IMdrContainer _mdrContainer, Iterator _parameters, ITextOutput _textOutput) {
        MdrContainer mdrContainer2 =load2ndModel();
        
        if (mdrContainer2!=null) logger.info("2nd model loaded");
        else logger.fatal("Error loading model");
        
        logger.info("this is only a test to load a second model. The check itself is not realized yet.");

        return true;
    }

    public MdrContainer load2ndModel(){
      JFileChooser fileChooser = new JFileChooser();
      fileChooser.setFileFilter(new FileFilterUml());
      fileChooser.setCurrentDirectory(new File(""));
      int _userSelection = fileChooser.showOpenDialog(null);
      
      if(_userSelection != JFileChooser.APPROVE_OPTION) {
          return null;
      }

      String resourceRoot = SystemVerificationLoader.getFramework().getResourceRootXX();
      String repositoryRoot = SystemVerificationLoader.getFramework().getRepositoryRoot();

      MdrContainer mdrContainer2 = new MdrContainer(resourceRoot, repositoryRoot);
      mdrContainer2.loadFromFile(fileChooser.getSelectedFile());

      return mdrContainer2;
    }
}
