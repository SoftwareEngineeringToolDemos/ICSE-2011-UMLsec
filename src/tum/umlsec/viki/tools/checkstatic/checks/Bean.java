package tum.umlsec.viki.tools.checkstatic.checks;

/**
 * @author Shasha Meng
 *
 */

public class Bean
{

	//private String name;
	private String stereotype;
	private String threat1;
	private String threat2;
	private String threat3;
	private String threat4;
	
	
    public Bean (String stereotype, String threat1, String threat2, String threat3, String threat4) {
    this.stereotype = stereotype;
    this.threat1 = threat1;
    this.threat2 = threat2;
    this.threat3 = threat3;
    this.threat4 = threat4;
  }

    //-- used by the data-binding framework
	public Bean() {}
	
	//-- accessors and mutators
    public void setStereotype(String stereotype) {
			this.stereotype = stereotype;
	}

	public String getStereotype() {
			return stereotype;
	}

	public void setThreat1(String threat1) {
			this.threat1 = threat1;
	}

	public String getThreat1() {
			return threat1;
	}

	public void setThreat2(String threat2) {
			this.threat2 = threat2;
	}

	public String getThreat2() {
			return threat2;
	}

	public void setThreat3(String threat3) {
			this.threat3 = threat3;
	}

	public String getThreat3() {
			return threat3;
	}

	public void setThreat4(String threat4) {
			this.threat4 = threat4;
	}

	public String getThreat4() {
			return threat4;
	}

}