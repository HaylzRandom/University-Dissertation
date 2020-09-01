package timetable;
/*
 * Servlet that will delete a single class from
 * the database
 * 
 * By: Hayley McCafferty 2011/2012
 */
import java.io.*;
import java.sql.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@WebServlet("/DeleteClass")
public class DeleteClass extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response) 
						throws ServletException, IOException {
		
		doPost(request, response);
		
	} // doGet

	public void doPost(HttpServletRequest request, HttpServletResponse response) 
						throws ServletException, IOException {

		response.setContentType("text/html") ;
		
		PrintWriter out = response.getWriter() ;
		
		HttpSession session = request.getSession();
		String user=(String)session.getAttribute("Username");
	    
		if (user == null) {
			response.sendRedirect("Login");
		}
		
		else {
		
			TimetableUtilities.doHeader(out) ;
			out.println("<title>Delete a Class</title>");
	
			deleteClass(out, request);
	
			TimetableUtilities.oneButtonForm(out, response.encodeURL("Admin.jsp"), "Go back to menu");
		
			TimetableUtilities.doFooter(out);
		}
		
	} // doPost
	
	/*
	 * Method will take class details from interface and 
	 * delete selected class from database
	 */

	private void deleteClass(PrintWriter out, HttpServletRequest request)  {

		String module = request.getParameter("Module");
		String type = request.getParameter("Type");
		String day = request.getParameter("Day");
		String start = request.getParameter("Start");
		String end = request.getParameter("End");
		String week = request.getParameter("Week");
		String location = request.getParameter("Location");
		
		out.println("<p><b>Module:</b> " + module + "." + type + "<br /> <b>Day:</b> " + day + "<br /><b>Start Time:</b> " + start + 
				"<br /><b>End Time:</b> " + end + "<br /><b>Week(s):</b> " + week + "<br /><b>Location:</b> " + location + "</p>");
		
		Connection con = null;
		
		try {
	  	    con = TimetableUtilities.openConnection();
	    }
	    catch (Exception e) {  // Failed to open the connection
	         System.err.print("<P>" + e.getMessage());
	         out.println("<p>Problem occurred!</p>");
	         TimetableUtilities.doFooter(out);
	         return;
	    }
		
		try {
			
			con.setAutoCommit(false);
		
			String query = String.format("DELETE FROM timetable WHERE module = ? AND day = ? AND start = ?");
			
		    PreparedStatement pstmt = null;
		    pstmt = con.prepareStatement(query);
		    // Set the value
		    pstmt.setString(1, module);
		    pstmt.setString(2, day);
		    pstmt.setString(3, start);
		    pstmt.executeUpdate();
	
		        
			// Closes database connection
			pstmt.close();
			con.commit();
			con.close();
			out.println("<p>Class deleted!</p>");
		
		}
		
		catch (Exception e) {
			out.println("<p>Problem occurred!</p>");
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		
	} // deleteClass



} // class DeleteClass
