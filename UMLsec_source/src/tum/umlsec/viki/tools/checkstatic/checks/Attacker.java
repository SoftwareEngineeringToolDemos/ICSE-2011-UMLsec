package tum.umlsec.viki.tools.checkstatic.checks;

/**
 * @author Shasha Meng
 *
 */

public class Attacker
{

	private String name;

	public Attacker()
	{}

	public Attacker(String name)
	{
		this.name=name;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name=name;
	}
}