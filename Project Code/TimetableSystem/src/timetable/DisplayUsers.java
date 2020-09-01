package timetable;
/*
 * Servlet that will display all users
 * that exist in the database currently
 * 
 * By: Hayley McCafferty 2011/2012
 */
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@WebServlet("/DisplayUsers")
public class DisplayUsers extends HttpServlet {
       
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

			display(out);
		
			TimetableUtilities.oneButtonForm(out, response.encodeURL("Admin.jsp"), "Admin Interface!");
			
			TimetableUtilities.doFooter(out);
		}
		
	} // doPost
	
	/*
	 * Method selects all the users that
	 * exist in the database currently
	 */

	private void display(PrintWriter out) {
		
		Connection con = null;
		ResultSet rs = null;
				
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
		
			Statement stmt = con.createStatement();
			String query = String.format("SELECT username, password FROM users");
			
	    	rs = stmt.executeQuery(query);

			con.setAutoCommit(false);
			displayTable(rs, out);
			        
			// Closes database connection
			stmt.close();
			con.commit();
			con.close();
		}
		
		catch(Exception e) {						
			out.println("<p>Problem occurred, try again later!</p>");	
		}
	
	} // display

	/*
	 * Will print out all usernames that currently exist
	 */
	private void displayTable(ResultSet rs, PrintWriter out) {
		
		int count = 0;
		
		out.println("<table>");
			out.println("<caption><b>Current Admin Users</b></caption>");
		out.println("<tr>");		
			out.println("<th>Username</th>");
		out.println("</tr>");
		
		try {
		    while (rs.next()) {
		    	out.println("<form method=\'post\' action=\'DeleteUser\'>");
				out.println("<tr>");
					out.println("<td><input type='hidden'  name='Name' value='" + rs.getString(1) + "'/>" + rs.getString(1) + "</td>");	
					out.println("<input type='hidden' name='Passcode' value='" + rs.getString(2) + "'/>");
					out.println("<td><input type='submit' name='UserButton' ID='" + count++ + "' value='Delete'/>");
				out.println("</tr>");
				out.println("</form>");
				
		    }
		    
		out.println("</table>");
		
		}
		
		catch (SQLException e) {
			out.println("<p>Problem displaying table!</p>");
	        TimetableUtilities.doFooter(out);
		    return;
		}
		
	} // displayTable

} // class DisplayTable
