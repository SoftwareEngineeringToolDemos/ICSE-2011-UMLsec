
package tum.umlsec.viki.tools.berechtigungen.xmlparser;

public class NumberGeneratorBID
{
	public static int bidNumber = 1;
	
	public static int getNextBID()
	{
		int oldID = bidNumber;
		bidNumber = bidNumber + 1;
		return oldID;
	}
	public static void reset()
	{
		bidNumber = 1;
	}
}