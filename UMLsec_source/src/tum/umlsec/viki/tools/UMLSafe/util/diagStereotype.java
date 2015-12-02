/*
 * Created on 2004-3-19
 * @author Shunwei
 */
 
package tum.umlsec.viki.tools.UMLSafe.util;

/**
 * Function:
 * the class of stereotypes
 */

import java.util.HashMap;

public class diagStereotype
{
	private String name;
	private HashMap tagandvalue;
	
    // set the name of the stereotype
	public void setName(String _name)
	{
		this.name = _name;
	}
	
    // set the tag and its value of the stereotype
	 public void setTagAndValue(HashMap _tagandvalue)
	 {
		 this.tagandvalue = _tagandvalue;
	 }
     
    //  get the name of the stereotype
     public String getName()
     {
         return this.name;
     }
     
    //  get the tag and its value of the stereotype
     public HashMap getTagandValue()
     {
         return this.tagandvalue;
     }
}
