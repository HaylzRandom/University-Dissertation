package timetable;

/* Java class that contains useful methods
 * that are used throughout the system
 * 
 * By: Hayley McCafferty 2011/2012
*/
import java.io.*;
import java.sql.*;

public class TimetableUtilities {

	// Will create connection to database
   public static Connection openConnection()
      throws Exception {

	String driverName = "org.sqlite.JDBC";
	Class.forName(driverName);
	
	String dbName = "timetable.db";
	String dbJdbc = "jdbc:sqlite";
	String dbUrl = dbJdbc + ":" + dbName;

      try {  // Load the driver
         Class.forName(driverName);
      }
      catch(Exception e) {
         throw new Exception("Failed to load driver "+ e.getMessage());
      }

      try {  // Connect to the database
         Connection con = DriverManager.getConnection(dbUrl);
         return con;
      }
      catch (SQLException e) {
         throw new Exception("Failed to connect to database " + dbUrl + "\nReason: " + e.getMessage());
      }

   } // openConnection

   // Web page header
   public static void doHeader(PrintWriter out) {

      out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">") ;
      out.println("<HTML>") ;
      out.println("<HEAD>") ;
      out.println("<META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=iso-8859-1\">") ;
      out.println("<link href=\"Timetable.css\" rel=\"stylesheet\" type=\"text/css\"");

      out.println("</HEAD>") ;
      out.println("<BODY>") ;
   } // doHeader

   // Web page footer
   public static void doFooter(PrintWriter out) {
      out.println("</BODY>") ;
      out.println("</HTML>") ;

   } // doFooter

   /*
    * Method that will create a one button form to allow
    * a user to go back to the given URL
    */
   public static void oneButtonForm(PrintWriter out, String URL, String buttonText) {

      out.println("<FORM METHOD=GET ACTION=\"" + URL + "\">");
      out.println("<P class='noprint'><INPUT TYPE=\"submit\" VALUE=\"" + buttonText + "\">");
      out.println("</FORM>");

   } // oneButtonForm

} // OnlineTestUtilities
