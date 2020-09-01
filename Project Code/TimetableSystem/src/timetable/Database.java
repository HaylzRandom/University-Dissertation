package timetable;

/*
 * Servlet that will display all classes from database in a HTML table
 * 
 * By: Hayley McCafferty 2011/2012
 */
import java.io.*;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.sql.*;

@WebServlet("/Database")
public class Database extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
	                    throws IOException, ServletException {	
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
			out.println("<title>Database Contents</title>");
			
			connectDB(out);
			TimetableUtilities.oneButtonForm(out, response.encodeURL("Admin.jsp"), "Admin Interface!");
						
			TimetableUtilities.doFooter(out);
	    }
			    
	} // doPost
	
	/*
	 * Method will get data from database and print out information 
	 * in a table 
	 */

	private void displayTable(ResultSet rs, PrintWriter out) {
			
		out.println("<table>");
		out.println("<caption><b>Information currently in Database</b></caption>");
		out.println("<tr>");
		
		try {
			
			ResultSetMetaData md = rs.getMetaData();
			for (int i = 2; i < md.getColumnCount()-1; i++) {
				String name = md.getColumnName(i);
				String output = name.substring(0, 1).toUpperCase() + name.substring(1);
				out.println("<th>" + output + "</th>");
			}
			
			out.println("</tr>");
			
			while (rs.next()) {
				
				out.println("<tr>");
				
				for (int a = 2; a < md.getColumnCount()-1; a++) {
					out.println("<td>" + rs.getObject(a) + "</td>");
				}	
				
			}
		}
		catch (SQLException e) {
			out.println("<p>No table exists!</p>");
			out.println("<form method=\"get\" action=\"Admin.jsp\" >");
				out.println("<p><input type=\"submit\" value=\"Enter new data\"></p>");
			out.println("</form>");
		}
		
		
		out.println("</table>");
		out.println("<p>");

	} // displayTable
  
	/*
	 * Method will connect to the database and select information
	 * that is required
	 */
	public void connectDB(PrintWriter out) {
		
		Connection con = null;
		ResultSet rs = null;
		
		try {
	  	    con = TimetableUtilities.openConnection();
	    }
	    catch (Exception e) {  // Failed to open the connection
	    	 System.err.println("<P>" + e.getMessage() + "</p>");
	         out.println("<p>Problem occurred!</p>");
			 out.println("<form method=\"get\" action=\"Admin.jsp\" >");
			 	out.println("<p><input type=\"submit\" value=\"Enter new data\"></p>");
			 out.println("</form>");
	         TimetableUtilities.doFooter(out);
	         return;
	    }
		
		try {
					
			Statement stmt = con.createStatement();
			String achieveQuery = " SELECT * FROM timetable";
	    	rs = stmt.executeQuery(achieveQuery);

			con.setAutoCommit(false);
			displayTable(rs, out);
				        
			// Closes database connection
			stmt.close();
			con.commit();
			con.close();
			
		}
		catch (SQLException e) {
			out.println("<p>No data exists!</p>");
		}
			
	} // connectDB


} // class Database