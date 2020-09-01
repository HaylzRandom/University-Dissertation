package timetable;
/*
 * Servlet that will delete all classes
 * from the database
 * 
 * By: Hayley McCafferty 2011/2012
 */
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;


@WebServlet("/DeleteAll")
public class DeleteData extends HttpServlet {
	

	public void doGet(HttpServletRequest request, HttpServletResponse response)
	                    throws IOException, ServletException {	
		
		doPost(request, response);	
		
	} // doGet
  
	public void doPost(HttpServletRequest request, HttpServletResponse response)
	  					 throws IOException, ServletException {
	  response.setContentType("text/html") ;
	
	  PrintWriter out = response.getWriter();
	  
	  HttpSession session = request.getSession();
	  String user=(String)session.getAttribute("Username");
		
	  if (user == null) {
		  response.sendRedirect("Login");
	  }
		
	  else {
	  
		  TimetableUtilities.doHeader(out) ;
		  out.println("<title>Delete Entire Database</title>");
		  
		  deleteTable(out);
		  
		  TimetableUtilities.oneButtonForm(out, response.encodeURL("Admin.jsp"), "Admin Interface!");
			
		  TimetableUtilities.doFooter(out);
	  }
    	  
	} // doPost

	/*
	 * Method will delete all data from the table named timetable
	 */
	private void deleteTable(PrintWriter out) {
		
		Connection con = null;
		
		try {
	  	    con = TimetableUtilities.openConnection();
	    }
	    catch (Exception e) {  // Failed to open the connection
	         out.println("<P>" + e.getMessage());
	         out.println("<p>Problem occurred!");
	         return;
	    }
		
		try {
			
			PreparedStatement pst = con.prepareStatement("DROP TABLE timetable;");
			
			// Set table to not auto commit
			con.setAutoCommit(false);
						
			pst.executeUpdate();
			
			out.println("<p>Table has been deleted");
				
			pst.close();
			con.commit();
			con.close();
			
			
		}
					
		catch(Exception e){						
			out.println("<p>No table exists - not deleted!");	
		}
		
	} // deleteTable
	
} // class DeleteData