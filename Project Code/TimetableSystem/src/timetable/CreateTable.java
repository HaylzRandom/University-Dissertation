package timetable;
/*
 * Servlet that will display a timetable for multiple modules
 * for Student and Staff users 
 * 
 * By: Hayley McCafferty 2011/2012
 */
import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.util.Date;
import java.sql.*;

@WebServlet("/CreateTable")
public class CreateTable extends HttpServlet {

	private final String[] days={"Monday","Tuesday","Wednesday","Thursday","Friday"};
	private final String[] start={"09:00","10:00","11:00","12:00","13:00","14:00","15:00","16:00","17:00"};
	
	public final int DAYS = days.length;
	public final int HOURS = start.length;
	
	public void doGet(HttpServletRequest request,
                    HttpServletResponse response)
                    throws IOException,
                           ServletException {
	  doPost(request,response);  
	  
	} // doGet
  
  	public void doPost(HttpServletRequest request,
		  			HttpServletResponse response)
		  			throws IOException,
		  					ServletException {
	  
	  response.setContentType("text/html") ;

	  PrintWriter out = response.getWriter() ;
	  
	  String name = request.getParameter("Name");
	  String module = request.getParameter("Module");
	  String button = request.getParameter("UserButton");
	  
	    
	  if (name == null || module == null || name.equals("") || module.equals("")) {
		  TimetableUtilities.doHeader(out) ;
		  out.println("<p>Missing values! Please try again</p>");
		  if (button.equals("Create Table")) {
			  TimetableUtilities.oneButtonForm(out, response.encodeURL("Staff.html"), "Staff Interface");
		  }
		  else {
			  TimetableUtilities.oneButtonForm(out, response.encodeURL("Student.html"), "Student Interface");
		  }
	  }
	  else {
		  
	  TimetableUtilities.doHeader(out) ;
	  out.println("<title>Create Timetable</title>");
	  out.println("<p class='noprint'>Hello " + name + "!</p>");
	  out.println("<p>");
	  module.toUpperCase();
	  connectDB(module, out);
	  
	  out.println("<p class='printcolour'><strong>Key </strong><em>[Module Code] [Type] [Room] </em></p>");
	  out.println("<p class='printcolour'><em>Type</em>: L-Lecture, S-Seminar/Tutorial, CL-Computer Lab, P-Practical Lab<br/>");
	  out.println("<em>Room</em>: 'C.' Cottrell, 'P.' Pathfoot</p>");
	  
	  out.println("<p class='printcolour'><b>NOTE:</b> Not all classes will run, please check with your department.</p>");
    
	  Date today = new Date();
   
	  out.println("<p class='noprint'>Timetable created on <b>" + today + "</b></p>");
   
	  TimetableUtilities.oneButtonForm(out, response.encodeURL("Index.html"), "Go back to menu");

	  TimetableUtilities.doFooter(out);
	   
	  }
	    
  	} // doPost
  
  	/*
  	 * Method will set up class information to be displayed in individual cells on
  	 * a timetable
  	 */
	public String getCellData(ResultSet rs) {
	
		StringBuffer sb = new StringBuffer();
		
		try {
			while (rs.next()) {
				sb.append(String.format("%s.%s<br />%s<br />%s<br />",
					rs.getString("module"),rs.getString("type"),rs.getString("location"),rs.getString("week")));
			}
		}	
		
		catch (SQLException e) {
			System.err.println("Problems building cell data " + e);
		}
		
		return sb.toString();
		
	} // getCellData

	
	/*
	 * Method will select appropriate information from database.
	 * Pass to getCellData for displaying
	 */

	public void createTable(Statement stmt, String modules, PrintWriter out) {
		
		String query="";
		
		String [][] cells = new String[DAYS][HOURS];
				
		String [] mods = modules.split(",");
		
		StringBuffer conditions = new StringBuffer();
		boolean notfirst = false;
		conditions.append(" (");
		for (String s : mods) {
			if (notfirst) conditions.append(" OR ");
			conditions.append(String.format("module = '%s'",s.toUpperCase()));
			notfirst = true;
		}
		conditions.append(")");
		
		// Loop through start times 
		for (int s=0; s<HOURS; s++)
		{
			// Loop through days
			for (int d=0;d<DAYS;d++) 
			{
				
				query = String.format(" SELECT module, type, day, start, end, week, location " +
						"FROM timetable WHERE %s AND start = '%s' AND day = '%s' ORDER BY start;",conditions,start[s],days[d]);
			
				ResultSet rs;
				
				try {				
						rs = stmt.executeQuery(query);
						cells[d][s] = getCellData(rs);
	
				} 
				catch (SQLException e) {
					out.print("<p>No table exists! Try again later!</p>");
				}
		
			}	
		}
		
		printTableFromCells(cells, out);
		
	} // createTable
	
	/*
	 * Method will display timetable with days across the top and time 
	 * down the left hand side
	 */
	
	public void printTableFromCells(String cells[][], PrintWriter out)
	{
		out.println("<table>");
		
		// Header row of days
		out.print("<tr><th>&nbsp;</th>");
		for (int d=0; d<DAYS; d++) {
			out.print(String.format("<th class='printcolour'>%s</th>",days[d]));
		}
		
		out.println("</tr>");
				
		for (int h=0; h<HOURS; h++) {
			
			out.print(String.format("<tr><th class='printcolour'>%s</th>",start[h]));
			
			for (int d=0; d<DAYS; d++) {
				out.print(String.format("<td class='printcolour' align='center'>%s</td>",cells[d][h]));
			}
			
			out.println("</tr>");
		}
		
		out.println("</table>");
		
	} // printTableFromCells
	
	/*
	 * Method sets up a connection to the database at beginning.
	 * After table is displayed it will close the connection
	 */
	public void connectDB(String course, PrintWriter out) {
		
		Connection con = null;
		
		try {
	  	    con = TimetableUtilities.openConnection();
	    }
	    catch (Exception e) {  // Failed to open the connection
	         out.println("<p>" + e.getMessage() + "</p>");
	         TimetableUtilities.doFooter(out);
	         return;
	    }
		
		try {
					
			Statement stmt = con.createStatement();
	
			con.setAutoCommit(false);
						
			createTable(stmt,course,out);
		        
			// Closes database connection
			stmt.close();
			con.commit();
			con.close();
		}
		catch (SQLException e) {
			out.println("<p>SQLException: " + e.getMessage() + "</p>") ;
		}
		
	} // connectDB
  
  
} // class CreateTable
