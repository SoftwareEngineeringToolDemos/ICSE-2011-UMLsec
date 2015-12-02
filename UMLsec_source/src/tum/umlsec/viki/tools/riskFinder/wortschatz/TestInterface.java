/*
 * ClientInterface.java
 *
 * Created on 26. April 2004, 12:06
 */

package tum.umlsec.viki.tools.riskFinder.wortschatz;

/**
 *
 * @author  Administrator
 */
public interface TestInterface {
    public String ping() throws Exception;
    public void setUsername( String strUsername );
    public void setPassword( String strPassword );
}
