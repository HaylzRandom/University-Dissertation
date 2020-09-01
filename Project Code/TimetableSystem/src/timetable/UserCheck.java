package timetable;
/* Servlet to check if a user exists
 * or to register a new user to the system
 * 
 * By: Hayley McCafferty 2011/2012
*/

import java.io.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.* ;

@WebServlet("/UserCheck")
public class UserCheck extends HttpServlet {

   public void doGet(HttpServletRequest request,
                     HttpServletResponse response)
      throws IOException, ServletException {

      doPost(request,response);

   } // doGet

   public void doPost(HttpServletRequest request,
                      HttpServletResponse response)
      throws IOException, ServletException {

      response.setContentType("text/html");
      PrintWriter out = response.getWriter();
            
      HttpSession session = request.getSession();
      String user = request.getParameter("Username");
      String button = request.getParameter("UserButton");
      
      String name = request.getParameter("Username");
	  String password = request.getParameter("Password");
	  
	  // If no button has been pressed
	  if (button == null) {
		  TimetableUtilities.doHeader(out) ;
    	  out.println("<title>Error Occurred!</title>");
    	  out.println("<p>A problem has occurred, go back to Index:</p>");
		  TimetableUtilities.oneButtonForm(out, response.encodeURL("Index.html"), "Go back to Index");
		  TimetableUtilities.doFooter(out);
	  }
	  else {
		  
		  // Approach from Login Servlet
		  if (button.equals("Register")) {
			  TimetableUtilities.doHeader(out) ;
	    	  out.println("<title>Register User</title>");
	    	  if (user.equals("") && password.equals("")) {
	    		  out.println("<p>Missing values, please try again!</p>");
	    		  TimetableUtilities.oneButtonForm(out, response.encodeURL("NewUser.html"), "Add User");
	    		  TimetableUtilities.doFooter(out);
	    	  }
	    	  else {
				  newUser(out, response, name, password);
				  TimetableUtilities.oneButtonForm(out, response.encodeURL("Index.html"), "Go back to Index");
				  TimetableUtilities.doFooter(out);
	    	  }
		  }
		  
		  else {
	      
		      if (user == null) {
		    	  response.sendRedirect("Login");
		      }
		      
		      else {
		    	  
		    	  // Approach from Admin Interface
		    	  if (button.equals("Add User")) {
		    		  TimetableUtilities.doHeader(out) ;
			    	  out.println("<title>Add User</title>");
			    	  if (user.equals("") || password.equals("")) {
			    		  out.println("<p>Missing values, please try again!</p>");
			    		  TimetableUtilities.oneButtonForm(out, response.encodeURL("Admin.jsp"), "Admin Interface!");
			    		  TimetableUtilities.doFooter(out);
			    	  }
			    	  else {
			    		  newUser(out, response, name, password);
			    		  TimetableUtilities.oneButtonForm(out, response.encodeURL("Admin.jsp"), "Admin Interface!");
			    		  TimetableUtilities.doFooter(out);
			    	  }
		    	  }
		    	  else {
		    		  
		    	  
				      if (user.equals("") && password.equals("")) {
				    	  TimetableUtilities.doHeader(out) ;
				    	  out.println("<title>Login Error</title>");
						  out.println("<p>Missing values, please try again:</p>");
				    	  TimetableUtilities.oneButtonForm(out, response.encodeURL("Login"), "Log in again");
				    	  TimetableUtilities.doFooter(out);
				      }    	  
				      
				      else {
				 	
				    	  session.setAttribute("Username", name);
				    	  session.setAttribute("Password", password);
				    	  session.setMaxInactiveInterval(120);
				    	  TimetableUtilities.doHeader(out) ;
				    	  out.println("<title>Checking Registration Details</title>");
				
				    	  Connection con = null;
				    	  ResultSet rs = null;
					  	
					  	  try {
					  	    con = TimetableUtilities.openConnection();
					      }
					      catch (Exception e) {  // Failed to open the connection
					    	  TimetableUtilities.doHeader(out) ;
					    	  out.println("<title>Login Error</title>");
							  out.println("<p>No users exists, please register!</p>");
					    	  TimetableUtilities.oneButtonForm(out, response.encodeURL("NewUser.html"), "Register");
					    	  TimetableUtilities.doFooter(out);
					      }
						    
						  	String userName = "";
						  	String pass = "";
					      
					      try {
					    	  
								Statement stmt = con.createStatement();
								String query = "SELECT username, password FROM users " +
												"WHERE username = '" + name + "' AND password = '" + password + "'";
							  	rs = stmt.executeQuery(query);
							  		  	
							  	while (rs.next()) {	  		
							  		userName = rs.getString("username");
							  		pass = rs.getString("password");
							  		session.setAttribute("username", userName);
							  		session.setAttribute("password", pass);
							  	}
							  	
							  	con.setAutoCommit(false);
							  	
							  	checkUser(out, response, name, userName, pass, password);
							  	
							  	stmt.close();
								con.commit();
							  	rs.close();					      				  	
					      } 
					      
					      catch (SQLException e) {
					    	  TimetableUtilities.doHeader(out) ;
					    	  out.println("<title>Login Error</title>");
							  out.println("<p>No users exists, please register!</p>");
					    	  TimetableUtilities.oneButtonForm(out, response.encodeURL("NewUser.html"), "Register");
					    	  TimetableUtilities.doFooter(out);
					      }
													  	
						TimetableUtilities.doFooter(out);
				    
				      }
		    	  }
		      }
		  }
	  }
      
   } // doPost

/*
 * Method will add a new user to the database
 */
private void newUser(PrintWriter out, HttpServletResponse response,
		String name, String password) {
	   String query = "?";
		
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
			
			Statement statement = con.createStatement();
			PreparedStatement stat = null;
			
			// Set table to not auto commit
			con.setAutoCommit(false);
			
			// Create new table and drop if exists 
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY NOT NULL, username, password);");
			
			query = "INSERT INTO users (username, password) " +
				"VALUES (?,?);";
			
			stat = con.prepareStatement(query);
			stat.setString(1, name);
			stat.setString(2, password);
			stat.executeUpdate();
			
			// Closes database connection
			stat.close();
			con.commit();
			con.close();
			
			out.println("<h4>User added: " + name + "</h4>");
		}
		
		catch(SQLException e) {						
			out.println("<p>Problem occurred!</p>");										
		}
	
}

/*
 * Method that will display an appropriate message
 * depending on the outcome
 */
private void checkUser(PrintWriter out, HttpServletResponse response, String name, String userName, String pass, String password) {
		if (userName.equals(name) && password.equals(pass)) {
			out.println("<h3>Welcome " + name + "</h3>");
			out.println("<hr>");
			out.println("<p>You are confirmed user!</p>");
			TimetableUtilities.oneButtonForm(out, response.encodeURL("Admin.jsp"), "Admin Interface!");
		}
		else {
			out.println("<p>Sorry you are not registered</p>");
			TimetableUtilities.oneButtonForm(out, response.encodeURL("Login"), "Log in again");
			TimetableUtilities.oneButtonForm(out, response.encodeURL("NewUser.html"), "Register");
		}	
   } // checkUser


} // class UserCheck