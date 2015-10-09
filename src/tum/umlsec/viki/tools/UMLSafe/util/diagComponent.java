/*
 * Created on 2004-3-19
 * @author Shunwei Shen
 */
 
package tum.umlsec.viki.tools.UMLSafe.util;

/**
 * Function:
 * An abstract class of type dependency and link.
 */

import java.util.HashMap;

public class diagComponent implements BaseType
{
	// name of the Component
	private String name;
	// stereotype of the Component
	private diagStereotype stereotype;
	// tag and its value the Component
	private HashMap tagandvalue;
	
	// set the name of the Component
	public void setName(String _name)
	{
		this.name = _name;
	}

	// set the stereotype of the Component
	public void setStereotype(diagStereotype _stereotype)
	{
		this.stereotype = _stereotype;
	}

	// set the tag and its value of the Component
	public void setTagAndValue(HashMap _tagandvalue)
	{
		this.tagandvalue = _tagandvalue;
	}

	// get the name of the Component
	public String getName()
	{
		return this.name;
	}

	// get the stereotype of the Component
	public diagStereotype getStereotype()
	{
		return this.stereotype;
	}

	// get the tag and its value of the Component
	public HashMap getTagandValue()
	{
		return this.tagandvalue;
	}

}
