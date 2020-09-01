package timetable;
/*
 * Servlet that will display all classes
 * for a given module
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


@WebServlet("/DisplayTable")
public class DisplayTable extends HttpServlet {
       
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
			String m = request.getParameter("module");
			String module = m.toUpperCase();
				
			TimetableUtilities.doHeader(out) ;
			out.println("<title>" + module + " Classes</title>");

			display(out, module);
		
			TimetableUtilities.oneButtonForm(out, response.encodeURL("Admin.jsp"), "Admin Interface!");
			
			TimetableUtilities.doFooter(out);
		}
		
	} // doPost
	
	/*
	 * Method selects the classes for the module given.
	 * Will pass information to displayTable
	 */

	private void display(PrintWriter out, String module) {
		
		Connection con = null;
		ResultSet rs = null;
				
		try {
	  	    con = TimetableUtilities.openConnection();
	    }
	    catch (Exception e) {  // Failed to open the connection
	         out.println("<P>" + e.getMessage());
	         out.println("Problem occurred!");
	         TimetableUtilities.doFooter(out);
	         return;
	    }
		
		try {
		
			Statement stmt = con.createStatement();
			module.toUpperCase();
			String query = String.format("SELECT * FROM timetable WHERE module = '%s'",module);
			
	    	rs = stmt.executeQuery(query);

			con.setAutoCommit(false);
			displayTable(rs, module, out);
			        
			// Closes database connection
			stmt.close();
			con.commit();
			con.close();
		}
		
		catch(Exception e) {						
			out.println("<p>Module doesn't exist, please try another!");	
		}
	
	} // display

	/*
	 * Will print out all available classes for the module given
	 */
	private void displayTable(ResultSet rs, String module, PrintWriter out) {
		
		int count = 0;
		
		out.println("<table>");
			out.println("<caption><b>Classes for " + module + "</b></caption>");
		out.println("<tr>");		
			out.println("<th>Module</th>");
			out.println("<th>Type</th>");
			out.println("<th>Day</th>");
			out.println("<th>Start Time</th>");
			out.println("<th>End Time</th>");
			out.println("<th>Week</th>");
			out.println("<th>Location</th>");	
		out.println("</tr>");
		
		try {
		    while (rs.next()) {
		    	out.println("<form method=\'post\' action=\'DeleteClass\'>");
				out.println("<tr>");
					out.println("<td><input type='hidden'  name='Module' value='" + rs.getString(2) + "'/>" + rs.getString(2) + "</td>");
					out.println("<td><input type='hidden'  name='Type' value='" + rs.getString(3) + "'/>"+ rs.getString(3)  + "</td>");
					out.println("<td><input type='hidden'  name='Day' value='" + rs.getString(4) + "'/>" + rs.getString(4) + "</td>");
					out.println("<td><input type='hidden'  name='Start' value='" + rs.getString(5) + "'/>" + rs.getString(5) + "</td>");
					out.println("<td><input type='hidden'  name='End' value='" + rs.getString(6) + "'/>" + rs.getString(6) + "</td>");
					out.println("<td><input type='hidden'  name='Week' value='" + rs.getString(7) + "'/>" + rs.getString(7) + "</td>");
					out.println("<td><input type='hidden'  name='Location' value='" + rs.getString(8) + "'/>" + rs.getString(8) + "</td>");			
					out.println("<td><input type='submit' ID='" + count++ + "' value='Delete'/>");
				out.println("</tr>");
				out.println("</form>");
				
		    }
		    
		out.println("</table>");
		
		}
		
		catch (SQLException e) {
			out.println("<P>" + e.getMessage());
		       return;
		}
		
	} // displayTable

} // class DisplayTable
