package tum.umlsec.viki.tools.UMLSafe.util;


/**
 *
 * @author  Shunwei
 */
public class safeDependencyXml
{
	private String level = null;
    private String goal = null;
    
    /**
     * Default Constructor
     */
    public safeDependencyXml()
    {}
    
    /**
     * get the level of the instance
     */
    public String getLevel()
    {
        return level;
    }

    /**
     * set the level of the instance.
     * 
     * @param the specified level
     */
    public void setLevel(String _level)
    {
        this.level = _level;
    }
    
    /**
     * get the required goal of the Class.
     */
    public String getGoal()
    {
        return goal;
    }

    /**
     * set the required goal of the Class.
     * 
     * @param the specified goal
     */
    public void setGoal(String _goal)
    {
        this.goal = _goal;
    }
}
