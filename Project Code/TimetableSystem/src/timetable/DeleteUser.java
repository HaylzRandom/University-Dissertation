package timetable;
/*
 * Servlet that will delete a single user from
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


@WebServlet("/DeleteUser")
public class DeleteUser extends HttpServlet {

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
			out.println("<title>Delete a User</title>");
	
			deleteUser(out, request);
	
			TimetableUtilities.oneButtonForm(out, response.encodeURL("Admin.jsp"), "Go back to menu");
		
			TimetableUtilities.doFooter(out);
		}
		
	} // doPost

	/*
	 * Method will take user details from interface and 
	 * delete the selected user from database
	 */
	private void deleteUser(PrintWriter out, HttpServletRequest request)  {

		String name = request.getParameter("Name");
		String password = request.getParameter("Passcode");
		
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
		
			String query = String.format("DELETE FROM users WHERE username = ? AND password = ?");
			
		    PreparedStatement pstmt = con.prepareStatement(query);
		    // Set the value
		    pstmt.setString(1, name);
		    pstmt.setString(2, password);
		    pstmt.executeUpdate();
	
			con.setAutoCommit(false);
		        
			// Closes database connection
			pstmt.close();
			con.commit();
			con.close();
			out.println("<h4>User deleted: " + name + "</h4>");
		
		}
		
		catch (SQLException e) {
			out.println("<p>Problem occurred!</p>");
		}
		
	} // deleteUser



} // class DeleteUser
