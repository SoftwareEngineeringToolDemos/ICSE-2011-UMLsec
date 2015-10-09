package tum.umlsec.viki.tools.checkstatic.checks;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Shasha Meng
 *
 */

public class AttackerBean
{
	private String name;

	private List beans = new ArrayList();
	
    public AttackerBean() {}
    
	
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}

	public List getBeans() {
		return beans;
	}
	public void addBean(Bean bean) {
		beans.add(bean);
	}
	
}