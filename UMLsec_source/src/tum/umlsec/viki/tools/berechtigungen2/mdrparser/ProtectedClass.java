/*
 * Created on 19.08.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package tum.umlsec.viki.tools.berechtigungen2.mdrparser;


import tum.umlsec.viki.tools.berechtigungen.xmlparser.NumberGeneratorBID;
/**
 * @author josef
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ProtectedClass {

	static int id = 0;
	private String oname;
	private int oid;

	public ProtectedClass(String name)
	{
		id = NumberGeneratorBID.getNextBID();
		oname =  name;
		oid = id;
	}

	public String getProtName()
	{
		return oname;
	}
	
	public String getProtID()
	{
		return String.valueOf(oid);
	}
	
	public static void resetProtectedId()
	{
		id = 0;
	}
	
	public void addName(String pname)
	{
		oname = oname+":"+pname;
	}
}
