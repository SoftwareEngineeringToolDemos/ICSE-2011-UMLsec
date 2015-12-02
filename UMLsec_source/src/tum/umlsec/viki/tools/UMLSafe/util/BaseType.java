/*
 * Created on 2004-3-19
 * @author Shunwei Shen
 */
 
package tum.umlsec.viki.tools.UMLSafe.util;

/**
 * Function:
 * An interface for the type dependency and link, both of them have
 * name stereotypes, tags and the values of tags.
 */

import java.util.HashMap;

public interface BaseType
{
	public void setName(String _Name);
	public void setStereotype(diagStereotype _stereotype);
	public void setTagAndValue(HashMap _tagandvalue);
	
	public String getName();
	public diagStereotype getStereotype();
	public HashMap getTagandValue();
}
