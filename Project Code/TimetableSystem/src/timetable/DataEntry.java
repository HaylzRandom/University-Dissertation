package timetable;
/*
 * Servlet that will take in a single class details
 * from Admin user interface and insert into the database
 * 
 * By: Hayley McCafferty 2011/2012
 */
import java.io.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;


@WebServlet("/Entry")
public class DataEntry extends HttpServlet {
	

	public void doGet(HttpServletRequest request, HttpServletResponse response)
	                    throws IOException, ServletException {
	
	    doPost(request, response);
	    	
	} // doGet
  
	public void doPost(HttpServletRequest request, HttpServletResponse response)
	  					 throws IOException, ServletException {
		
		response.setContentType("text/html") ;
		
		PrintWriter out = response.getWriter() ;
			
		HttpSession session = request.getSession();
		String user=(String)session.getAttribute("Username");
		
		String module = request.getParameter("Module");
		String type = request.getParameter("Type");
		String day = request.getParameter("Day");
		String start = request.getParameter("Start");
		String end = request.getParameter("End");
		String building = request.getParameter("Building");
		String location = request.getParameter("Location");
		String week = request.getParameter("Week");
		String staff = request.getParameter("Staff");
		
		String room = building + location;
	    
		if (user == null) {
			response.sendRedirect("Login");
		}
	
		else {
			
			if (staff == null || staff.equals("")) {
				staff = "";
			}
			
			if (module == null || module.equals("") || location == null || location.equals("") || week == null || week.equals("")) {
				 TimetableUtilities.doHeader(out) ;
				 out.println("<p><b>Missing values! Please enter a module code, a location and the weeks class takes place</b></p>");
				 TimetableUtilities.oneButtonForm(out, response.encodeURL("Admin.jsp"), "Admin Interface");
			}
			
			else {

				String mod = module.toUpperCase();
				String roomNum = room.toUpperCase();
			
				TimetableUtilities.doHeader(out) ;
				out.println("<title>Enter Class Information</title>");
			
				insert(mod, type, day, start, end, roomNum, week, staff, out);
				TimetableUtilities.oneButtonForm(out, response.encodeURL("Admin.jsp"), "Go back to menu");
				
				TimetableUtilities.doFooter(out);
			}
		}
		
	} // doPost
  
	/*
	 * Method will take values passed in from the interface and 
	 * insert them into the database
	 */
	public void insert(String module, String type, String day, String start, String end, String room, String week, String staff, PrintWriter out) {
		  
		String query = "?";
		
		Connection con = null;
		
		try {
	  	    con = TimetableUtilities.openConnection();
	    }
	    catch (Exception e) {  // Failed to open the connection
	    	 System.err.println("<p>" + e.getMessage() + "</p>");
	         out.println("<p>Problem occurred!</p>");
	         TimetableUtilities.doFooter(out);
	         return;
	    }
		
		try {
			
			Statement statement = con.createStatement();
			PreparedStatement stat = null;
			
			// Set table to not auto commit
			con.setAutoCommit(false);
			
			// Create new table and drop if exists 
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS timetable (id INTEGER PRIMARY KEY NOT NULL, module, type, day, start, end, week, location, room, classroom, staff, division, problem);");
			
			query = "INSERT INTO timetable (module, type, day, start, end, week, location, room, classroom, staff, division, problem) " +
				"VALUES (?,?,?,?,?,?,?,?,?,?,?,?);";
			
			stat = con.prepareStatement(query);
			stat.setString(1, module);
			stat.setString(2, type);
			stat.setString(3, day);
			stat.setString(4, start);
			stat.setString(5, end);
			stat.setString(6, week);
			stat.setString(7, room);
			stat.setString(8, "");
			stat.setString(9, "");
			stat.setString(10, staff);
			stat.setString(11, "");
			stat.setString(12, "");
			stat.executeUpdate();
			
			// Closes database connection
			stat.close();
			con.commit();
			con.close();
			
			out.println("<p>Table updated with: </p>"); 
			out.println("<p><b>Module:</b> " + module + "." + type + "<br /> <b>Day:</b> " + day + "<br /><b>Start Time:</b> " + start + 
					"<br /><b>End Time:</b> " + end + "<br /><b>Week(s):</b> " + week + "<br /><b>Location:</b> " + room + "<br /><b>Staff:</b></> " + staff + "</p>");
		}
						
		catch(SQLException e) {						
			out.println("<p>Problem occurred!</p>");										
		}
							
	} // insert

} // class DataEntry
