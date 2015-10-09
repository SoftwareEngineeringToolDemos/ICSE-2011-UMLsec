package tum.umlsec.viki.tools.checkstatic.checks;

import java.util.Iterator;

import org.omg.uml.foundation.core.CorePackage;
import org.omg.uml.foundation.core.Stereotype;
import org.omg.uml.modelmanagement.ModelManagementPackage;
import org.omg.uml.modelmanagement.UmlPackage;
import org.omg.uml.modelmanagement.UmlPackageClass;

import tum.umlsec.viki.framework.ITextOutput;
import tum.umlsec.viki.framework.mdr.IMdrContainer;
import tum.umlsec.viki.tools.checkstatic.StaticCheckerBase;

/**
 * @author Shasha Meng
 *
 */
public class CheckerAllAuto extends StaticCheckerBase
{
  private final String SECRECY_INTEGRITY = "secrecy_integrity";
  private final String FAIR_EXCHANGE = "fair exchange";
  private final String GUARDED_ACCESS = "guarded access";
  private final String PROVABLE = "provable";
  private final String SECURE_DEPENDENCY = "secure dependency";
  private final String SECURE_LINKS = "secure links";

  ITextOutput textOutput;
  boolean return_value=false;

  /* (non-Javadoc)
   * @see tum.umlsec.viki.tools.checkstatic.StaticCheckerBase#check(tum.umlsec.viki.framework.mdr.IMdrWrapper)
   */
  public boolean check(IMdrContainer _mdrContainer, Iterator _parameters, ITextOutput _textOutput) {
    textOutput = _textOutput;
    textOutput.writeLn("======== Properties of the package: Name and Stereotype ");
    org.omg.uml.UmlPackage root ;
    CorePackage corePackage ;

    // replacement for the init function
    root = _mdrContainer.getUmlPackage();
    corePackage = root.getCore();

    ModelManagementPackage mpackage=root.getModelManagement();
    UmlPackageClass upc=mpackage.getUmlPackage();
    for(Iterator itm=upc.refAllOfClass().iterator();itm.hasNext();){
      UmlPackage upackage=(UmlPackage)itm.next();
      if(!upackage.getName().equals("java")&&!upackage.getName().equals("lang"))
        {textOutput.writeLn("The name of package is "+upackage.getName());}
      for(Iterator itst=upackage.getStereotype().iterator();itst.hasNext();){
              Stereotype st=(Stereotype)itst.next();
              String stName=st.getName();
              textOutput.writeLn("The stereotype of the package is "+stName);
              StaticCheckerBase _checker = null;
              if (stName.equals(SECRECY_INTEGRITY)){
                _checker = new CheckerSecrecyIntegrity();
              }else if(stName.equals(FAIR_EXCHANGE)){
                _checker = new CheckerFairExchange();
              }else if(stName.equals(GUARDED_ACCESS)){
                _checker = new CheckerGuardedAccess();
              }else if(stName.equals(PROVABLE)){
                _checker = new CheckerProvable();
              }else if(stName.equals(SECURE_DEPENDENCY)){
                _checker = new CheckerSecureDependency();
              }else if(stName.equals(SECURE_LINKS)){
                _checker = new CheckerSecureLinks();
              }
        return_value=_checker.check(_mdrContainer, _parameters, textOutput);
      }
    }
      return return_value;
  }
}