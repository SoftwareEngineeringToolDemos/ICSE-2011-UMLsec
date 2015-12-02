
package tum.umlsec.viki.tools.berechtigungen.xmlcreater;

public class Activity
{
	private String nameActivity;
	private int activityID;
	
	public Activity(String name, int id)
	{
		nameActivity = name;
		activityID = id;
	}
	
	public int getActivityID()
	{
		return activityID;
	}
	
	public String getActivityName()
	{
		return nameActivity;
	}
	
} // End of class