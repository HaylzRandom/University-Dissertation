package timetable;
/* Servlet to ask candidate to log in 
 * or register as a new user
 * 
 * By: Hayley McCafferty 2011/2012
*/

import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.* ;

@WebServlet("/Login")
public class Login extends HttpServlet {

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
      session.setMaxInactiveInterval(10);
      
      TimetableUtilities.doHeader(out) ;
      out.println("<title>Login</title>");

      out.println("<h3>You have to log in to use this part of the system</h3>");
      out.println("<hr>");

      out.println("<form method=\"post\" action=\"UserCheck\">");
      out.println("<p>Username: <input type=\"text\" name=\"Username\" size=5></p>");
      out.println("<p>Password: <input type=\"password\" name=\"Password\" size=20></p>");
      out.println("<p><input type=\"submit\" name=\"UserButton\" value=\"Login\"></p>");
      out.println("</form>");
      
      TimetableUtilities.oneButtonForm(out, response.encodeURL("NewUser.html"), "New User?");
      
      TimetableUtilities.oneButtonForm(out, response.encodeURL("Index.html"), "Go back to Index");

      TimetableUtilities.doFooter(out);

   } // doPost

} // class Login

