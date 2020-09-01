package timetable;

import java.io.PrintWriter;
import java.util.ArrayList;

/*
 * Created on Oct 27, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * @author David
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Slot {
	
	private String subject;
	private String module; 
	private String fullmod;
	private String classtype;
	private String day;
	private String classSize;
	private String capacity;
	private int start;
	private int end;
	private String weeks;
	private String location;
	private String staff;
	private boolean provisional = false;
	private boolean deleted = false;
	private boolean highlight = false;
	
	private String sep = "\t";

	Slot(String line, boolean prov, boolean del, boolean high, boolean common)
	{
		String [] fields = line.split("\t");
		
		try
		{
			
		String	mcode = fields[0];
		
		// Class type is not always present (e.g. NURSAC, NUR202)
		int dot = mcode.indexOf(".");
		if (dot < 0)
		{
			subject = mcode;
			module = mcode.substring(mcode.length() - 2); // Last 2 digits
			fullmod = mcode;
			classtype = "NA";
		}
		
		if (dot == 3)
		{
			subject = mcode.substring(0,3);
			module = "NA";
			fullmod = "NA";
			classtype = mcode.substring(dot+1);			
		}
		if (dot > 3) 
		{
				subject = mcode.substring(0,4);
				module = mcode.substring(4,dot);
				fullmod = subject + module;
				classtype = mcode.substring(dot+1);
		}
		
		if (!common)
		{
			// < Oct 08
			classSize = fields[1];	
			day = fields[2];
			start = getTimeNum(fields[3]);
			end = getTimeNum(fields[4]);
			weeks = fields[5].replaceAll("\"","");
			location = fields[6];
			if (fields.length > 7) capacity = fields[7]; else capacity = "-";
			if (fields.length > 8) staff = fields[8].replaceAll("\"",""); else staff = "-";
		}
		else
		{
			// > Oct 08
			// Name	Day	Start time	End time	Week pattern	Location	Room Size	Class Size	Staff	Department	Problems

			day = fields[1];
			start = getTimeNum(fields[2]);
			end = getTimeNum(fields[3]);
			weeks = fields[4].replaceAll("\"","");
			location = fields[5];
			capacity = fields[6];
			if (fields.length > 7) classSize = fields[7];
			// if (fields.length > 7) capacity = fields[7]; else capacity = "-";
			if (fields.length > 8) staff = fields[8].replaceAll("\"",""); else staff = "-";
		}
		
		provisional = prov;
		deleted = del;
		highlight = high;
		
		dot = location.indexOf(".");
		location = location.substring(dot+1);
		}
		catch (Exception e) 
		{
			System.err.println("Problems with line " + line);
			System.err.println("   Exception: " + e);
		}
	}
	
	public boolean isSubject(String s) { return subject.contains(s); }
	public boolean isModule(String s) { return fullmod.equals(s); }
	public boolean isClassType(String s) { return classtype.startsWith(s); }
	public boolean isLecture() { return classtype.startsWith("L"); }
	public boolean isTutorial() { return classtype.startsWith("S"); }
	public boolean isPractical() { return classtype.startsWith("P"); }
	public boolean isComputerLab() { return classtype.startsWith("CL"); }
	public boolean isProvisional() { return provisional; }
	public boolean isDeleted() { return deleted; }
	public boolean isHighlight() { return highlight; }
	
	public String getSubject() { return subject; }
	public String getModule() { return module; }
	public String getFullModule() { return fullmod; }
	public String getDay() { return day; }
	public String getWeeks() { return weeks; }
	public String getClassType() { return classtype; }
	public String getLocation() { return location; }
	public int getStart() { return start; }
	public int getEnd() { return end; }
	
	public float getNum(String numstr, String line)
	{
		float	num = 0;
		
		try 
		{ 
			num = Float.parseFloat(numstr); 
		}
		catch (Exception e) 
		{ 
			System.out.println("Failed to parse '" + numstr + "':" + e); 
			System.out.println("   > " + line);
		}
		return num;
	}
	
	// Convert a 24 time rep into a number
	public int getTimeNum(String s)
	{
		int colon = s.indexOf(":");
		String hours = s.substring(0,colon);
		
		return Integer.parseInt(hours);
	}
	
	public void print(PrintWriter out, ArrayList rooms)
	{
		out.print(subject + sep + module + sep + classtype + sep + day + sep + start + sep + end + sep + weeks + sep + location);
		if (rooms != null)
			out.println(sep); //  + getCapacity(rooms));
		else
			out.println();
		
	}

	public String getDetails()
	{
		return subject + sep + module + sep + classtype + sep + day + sep + start + sep + end + sep + weeks + sep + location;
	}	
}
