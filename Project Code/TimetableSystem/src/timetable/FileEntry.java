package timetable;
/*
 * Servlet that parse a text file passed from 
 * user interface.
 * 
 * Contains code by permission of Dr. D. Cairns
 * 
 * By: Hayley McCafferty 2011/2012
 */
import java.io.*;
import java.sql.*;
import java.util.*;
import java.io.BufferedReader;
import java.io.PrintWriter;

import javax.servlet.*;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/FileEntry")
@MultipartConfig
public class FileEntry extends HttpServlet {
	

	public void doGet(HttpServletRequest request, HttpServletResponse response)
	                    throws IOException, ServletException {
	
		doPost(request, response);
	   	
	} // doGet
  
	public void doPost(HttpServletRequest request,
		  			 HttpServletResponse response)
  					 throws IOException, ServletException {
	  	  
		PrintWriter out = response.getWriter() ;
	    
		response.setContentType("text/html") ;
		
		HttpSession session = request.getSession();
		String user=(String)session.getAttribute("Username");
			
		if (user == null) {
			response.sendRedirect("Login");
		}
			
		else {
	
			TimetableUtilities.doHeader(out) ;
			out.println("<title>Add classes via a file</title>");
			
			try {
			
				for (Part part : request.getParts()) {
					  
					  InputStream is = request.getPart(part.getName()).getInputStream();
					  dataFile(is, out);
				}
			}
			catch (Exception e) {
				out.println("<p>Something went wrong, oh dear!");
			}
		
			TimetableUtilities.oneButtonForm(out, response.encodeURL("Admin.jsp"), "Admin Interface!");
				
			TimetableUtilities.doFooter(out);
		}
		
    } // doPost
    
	private boolean	commonFormat = true;

	/*
	 * Will add data to database via a file passed from Admin Interface
	*/
	public void dataFile(InputStream in, PrintWriter out) {
		
		String str = "?";
		String query = "";
		int count = 0;
		ArrayList<Slot> slots = null;
		Slot s = null;
		String name = "-";
		String day = "-";
		String start = "-";
		String end = "-";
		String week = "-";
		String location = "-";
		String room = "-";
		String classroom = "-";
		String staff = "-";
		String division = "-";
		String problem = "-";
		
		int currentLine = 1;

		Connection con = null;
		
		try {
	  	    con = TimetableUtilities.openConnection();
	    }
	    catch (Exception e) {  // Failed to open the connection
	         out.println("<P>" + e.getMessage());
	         out.println("<p>Problem occurred!");
	         TimetableUtilities.doFooter(out);
	         return;
	    }
		
		try {
			
			Statement statement = con.createStatement();
			PreparedStatement stat = null;
			
			// Set table to not auto commit
			con.setAutoCommit(false);

			BufferedReader br = new BufferedReader(new InputStreamReader(in)); 

			// Create new table and drop if exists 
		    statement.executeUpdate("CREATE TABLE IF NOT EXISTS timetable (id INTEGER PRIMARY KEY AUTOINCREMENT, module, type, day, start, end, week, location, room, classroom, staff, division, problem);");
			
			query = "INSERT INTO timetable (module, type, day, start, end, week, location, room, classroom, staff, division, problem) " +
					"VALUES (?,?,?,?,?,?,?,?,?,?,?,?);";
			
			stat = con.prepareStatement(query);

			String strLine;
			ArrayList<String> list = new ArrayList<String>(10);
			int skipLine = 3;
			
				/*
				 * Read through line by line and skipping first three lines.
				 * Add the line to the list to be used later
				 */
			    while ((strLine = br.readLine()) != null){
			    	if (currentLine++ <= skipLine) {
			    		continue;
			    	}
			    	list.add(strLine);			
			    }
			
			br.close();
					
			
			Iterator<String> iter = list.iterator();
			
			// Travels through elements in iterator transferring them into strings 
			// and splitting them up
			while (iter.hasNext()) {
				str = iter.next().toString();  
				String[] splitSt = str.split("\t");

				
				for (int i = 0 ; i < splitSt.length ; i++) {
					
					problem = "?";
					
					slots = addClass(str);
					s = slots.get(0);
					name = s.getModule();
					day = splitSt[1];
					start = splitSt[2];
					end = splitSt[3];
					week = splitSt[4];
					location = splitSt[5];
					// Checks that a value does exist 
					if (splitSt.length > 6)
						room = splitSt[6];
					if (splitSt.length > 7)
						classroom = splitSt[7];
					if (splitSt.length > 8)
						staff = splitSt[8];
					if (splitSt.length > 9)
						division = splitSt[9];
					if (splitSt.length > 10)
						problem = splitSt[10];
					
				}
				
				// Add data to database 
				int index=1;
				for (int slot=0; slot<slots.size(); slot++)
				{
					index = 1;
					String mod = slots.get(slot).getFullModule();
					String type = slots.get(slot).getClassType();
					stat.setString(index++,mod);
					stat.setString(index++,type);
					stat.setString(index++, day);
					stat.setString(index++, start);
					stat.setString(index++, end);
					stat.setString(index++, week);
					stat.setString(index++, location);
					stat.setString(index++, room);
					stat.setString(index++, classroom);
					stat.setString(index++, staff);
					stat.setString(index++, division);
					stat.setString(index++, problem);
					stat.execute();
		
					count++;
				}
				
			 }
			out.println("<p>" + count + " classes added!");
			
			// Closes database connection
			stat.close();
			con.commit();
			con.close();
		}
		
		catch(Exception e){
			
			out.println("<p>Problem occurred!!");	
			
		}
		
	} // dataFile
	
	/*
	 * Code used by permission of Dr D. Cairns, University of Stirling.
	 */
	
	private String padTo(int size, String str, char pad)
	{
		int len = str.length();
		
		if (len >= size) return str;
		
		StringBuffer buf = new StringBuffer(str);
		for (int p=size - len; p>0; p--)
		{
			buf.append(pad);
		}
		return buf.toString();
		
	} // padTo

	/*
	 * Code used by permission of Dr D. Cairns, University of Stirling.
	 */
	// Try to work out what line style (module code) format we have
	private ArrayList<Slot> addClass(String line) throws Exception
	{
		boolean prov = false;
		boolean deleted = false;
		boolean highlight = false;
		
		ArrayList<Slot> sdb = new ArrayList<Slot>();
		
		if (line.startsWith("#") || line.startsWith("+") || line.startsWith("_") || line.startsWith("*"))
		{
			prov = true;
			highlight = (line.startsWith("*"));
			line = line.substring(1,line.length()).trim();
		}
		
		if (line.startsWith("-"))
		{
			deleted = true;
			line = line.substring(1,line.length()).trim();
		}
		
		String	trimmed = line.replaceAll("\"",""); 
		StringTokenizer tk = new StringTokenizer(trimmed,"\t");
		String	modcode = tk.nextToken();
		
		// If we're filtering and the modcode doesn't end with the filter tag, skip this entry
		
		int		dot = modcode.indexOf(".");
		int		slash = modcode.indexOf("/");
		int		hash = modcode.indexOf("#");
		String	subject,module,type,newline;
		String	details = "";
		
		while (tk.hasMoreTokens()) details = details + "\t" + tk.nextToken();
				
		if (dot == 6) // Standard format SSSSMM.C
		{ 
			sdb.add(new Slot(trimmed,prov,deleted,highlight,commonFormat));
			return sdb; 
		} 
		
		if ((dot == 7) && (slash < 0)) // Unit with extra char SSSSMM?
		{
			// System.out.println("Std Dual Unit 1: " + trimmed);
			subject = modcode.substring(0,4);
			module = modcode.substring(4,6);
			type = modcode.substring(dot+1);

			// Create first class component
			newline = subject + module + "." + type + details;
			sdb.add(new Slot(newline,prov,deleted,highlight,commonFormat));
			return sdb;
		}

		if ((dot == 4) && (slash < 0) && (modcode.length() == 10)) // Unit with extra . SSSS.MM.S#?
		{
			subject = modcode.substring(0,4);
			module = modcode.substring(5,7);
			int lastdot = modcode.lastIndexOf("."); 
			type = modcode.substring(lastdot+1);

			// Create first class component
			newline = subject + module + "." + type + details;
			sdb.add(new Slot(newline,prov,deleted,highlight,commonFormat));
			return sdb;
		}
		
		if ((dot == 9) && (slash == 6)) // Dual Unit Type 1 SSSSMM/MM.C
		{
			subject = modcode.substring(0,4);
			module = modcode.substring(4,6);
			type = modcode.substring(dot+1);

			// Create first class component
			newline = subject + module + "." + type + details;
			sdb.add(new Slot(newline,prov,deleted,highlight,commonFormat));
			
			// Create second class component			
			module = modcode.substring(slash+1,slash+3);
			newline = subject + module + "." + type + details;
			sdb.add(new Slot(newline,prov,deleted,highlight,commonFormat));			
			return sdb;
		}
		
		if ((dot == 10) && (slash == 6)) // Dual Unit Type 2 SSSSMM/SMM.C
		{
			subject = modcode.substring(0,4);
			module = modcode.substring(4,6);
			type = modcode.substring(dot+1);

			// Create first class component
			newline = subject + module + "." + type + details;
			sdb.add(new Slot(newline,prov,deleted,highlight,commonFormat));
			
			// Create second class component			
			module = modcode.substring(slash+1,slash+4);
			newline = subject + module + "." + type + details;
			sdb.add(new Slot(newline,prov,deleted,highlight,commonFormat));			
			return sdb;
		}		

		if ((dot == 8) && (slash == 4)) // Dual Unit Type 3 SSSS/SMM.C
		{
			subject = modcode.substring(0,4);
			module = modcode.substring(dot-2,dot);
			type = modcode.substring(dot+1);

			// Create first class component
			newline = subject + module + "." + type + details;
			sdb.add(new Slot(newline,prov,deleted,highlight,commonFormat));
			
			// Create second class component	
			subject = modcode.substring(0,3) + modcode.substring(slash+1,slash+2);
			module = modcode.substring(slash+2,slash+4);
			newline = subject + module + "." + type + details;
			sdb.add(new Slot(newline,prov,deleted,highlight,commonFormat));			
			return sdb;
		}
		
		if ((dot == 16) && (slash == 6)) // Triple Unit Type 3 SSSSMM/MM/SSSSMM.C
		{
			subject = modcode.substring(0,4);
			module = modcode.substring(4,6);
			type = modcode.substring(dot+1);

			// Create first class component
			newline = subject + module + "." + type + details;
			sdb.add(new Slot(newline,prov,deleted,highlight,commonFormat));
			
			// Create second class component	
			module = modcode.substring(slash+1,slash+3);
			newline = subject + module + "." + type + details;
			sdb.add(new Slot(newline,prov,deleted,highlight,commonFormat));	
			
			// Create second class component	
			subject = modcode.substring(dot-6,dot-2);
			module = modcode.substring(dot-2,dot);
			newline = subject + module + "." + type + details;
			sdb.add(new Slot(newline,prov,deleted,highlight,commonFormat));				
			return sdb;
		}
		
		if ((dot == 13) && (slash == 6)) // Dual Unit Type 2 SSSSMM/SSSSMM.C
		{
			subject = modcode.substring(0,4);
			module = modcode.substring(4,6);
			type = modcode.substring(dot+1);

			// Create first class component
			newline = subject + module + "." + type + details;
			Slot s = new Slot(newline,prov,deleted,highlight,commonFormat);
			sdb.add(s);
			
			// Create second class component			
			module = modcode.substring(slash+1,dot);
			newline = module + "." + type + details;
			sdb.add(new Slot(newline,prov,deleted,highlight,commonFormat));			
			return sdb;
		}

		if ((dot == 20) && (slash == 6)) // Triple Unit Type 2 SSSSMM/SSSSMM/SSSSMM.C
		{
			subject = modcode.substring(0,4);
			module = modcode.substring(4,6);
			type = modcode.substring(dot+1);

			// Create first class component
			newline = subject + module + "." + type + details;
			sdb.add(new Slot(newline,prov,deleted,highlight,commonFormat));
			
			// Create second class component
			int lastslash = modcode.lastIndexOf("/");
			module = modcode.substring(slash+1,lastslash);
			newline = module + "." + type + details;
			sdb.add(new Slot(newline,prov,deleted,highlight,commonFormat));
			
			// Create third class component			
			module = modcode.substring(lastslash+1,dot);
			newline = module + "." + type + details;
			sdb.add(new Slot(newline,prov,deleted,highlight,commonFormat));

			return sdb;
		}
		
		if ((dot < 0) && (hash == 6)) // No Type code or dot e.g. CSC9P5#
		{
			subject = modcode.substring(0,4);
			module = modcode.substring(4,6);
			type = "NA";

			// Create class component
			newline = subject + module + "." + type + details;
			sdb.add(new Slot(newline,prov,deleted,highlight,commonFormat));
			
			return sdb;
		}

		if ((dot < 0) && (modcode.length() == 6)) // No Type code or dot e.g. CSC9P5
		{
			subject = modcode.substring(0,4);
			module = modcode.substring(4,6);
			type = "NA";

			// Create class component
			newline = subject + module + "." + type + details;
			sdb.add(new Slot(newline,prov,deleted,highlight,commonFormat));
			
			return sdb;
		}
		
		if ((dot < 0) && modcode.contains(")#")) // e,g, SPA9L5(a)#
		{
			subject = modcode.substring(0,4);
			module = modcode.substring(4,6);
			type = "NA";

			// Create class component
			newline = subject + module + "." + type + details;
			sdb.add(new Slot(newline,prov,deleted,highlight,commonFormat));
			
			return sdb;
		}		
		
		if (dot > 0 && slash < 0) // Irregular code but no multiples
		{ 
			sdb.add(new Slot(trimmed,prov,deleted,highlight,commonFormat));
			return sdb; 
		} 

		if (dot > 0 && slash > 0) // Irregular code with multiples
		{ 
			sdb.add(new Slot(trimmed,prov,deleted,highlight,commonFormat));
			return sdb; 
		}
		
		if (dot < 0 && slash == 6) // No dot but a division - assuming Nursing & Midwifery code
		{
			subject = modcode.substring(0,4);
			module = modcode.substring(4,6);
			type = "S(" + modcode.substring(7) + ")#";

			// Create class component
			newline = subject + module + "." + type + details;
			sdb.add(new Slot(newline,prov,deleted,highlight,commonFormat));
			return sdb;
		}
		
		// Must be a non standard code, grab first word and add .S to 'normalise' it
		String [] parts = modcode.split(" ");
		String first = padTo(6,parts[0] ,'X') + ".S#";
		sdb.add(new Slot(first+details,prov,deleted,highlight,commonFormat));
		
		return sdb;
		
	} // addClass

} // class FileEntry