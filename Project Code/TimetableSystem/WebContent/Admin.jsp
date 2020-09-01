<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css" href="Timetable.css">
<script type="text/javascript">
    function toggle_visibility(id) {
        var e = document.getElementById(id);
           e.style.display = 'block';
        document.getElementById('showMenu').style.display = "none";
        document.getElementById('logout').style.display = "none";
    }
    function toggle_hide(id) {
    	var f = document.getElementById(id);
    		f.style.display = 'none';
    	document.getElementById('showMenu').style.display = "block";
    	document.getElementById('logout').style.display = "block";
    }
   
</script>
<title>Admin Interface</title>
</head>
<body>

<%
		String user=(String)session.getAttribute("Username");
		if (user == null) {
			response.sendRedirect("Login");
		}
			
		else {
			%>

    
<div id="showMenu" style="display:block;">
<form action=none>
  <h1> Admin Timetable Interface </h1>
  <p><input type="button" id="class" name="class" value="Add class" onclick="toggle_visibility('addClass');">&nbsp;
  <input type="button" id="file" name="file" value="Add file" onclick="toggle_visibility('addFile');"> &nbsp;  
  <input type="button" id="timetable" name="timetable" value="Create Timetable" onclick="toggle_visibility('showTimetable');">  &nbsp; 
  <input type="button" id="database" name="database" value="Show Database" onclick="toggle_visibility('showDatabase');">  &nbsp; 
  <input type="button" id="deleteAll" name="deleteAll" value="Delete All Data" onclick="toggle_visibility('deleteData');">&nbsp;
  <input type="button" id="delete" name="delete" value="Delete class" onclick="toggle_visibility('deleteClass');">&nbsp;  
  <p><input type="button" id="add" name="adduser" value="Add User" onclick="toggle_visibility('addUser');">&nbsp;
  <input type="button" id="deleteUse" name="deleteuser" value="Delete User" onclick="toggle_visibility('deleteUser');"></p>
</form>
</div>

<div id="logout" style="display:block;">
<form action="Logout.jsp" method="post">
	<hr><p><input type="submit" value="Logout of System"></p>
</form>	
</div>

<div id="addClass" style="display:none;">
<form method="post" action="Entry">
<h1>Add a single class</h1>
	<h4>Please enter modules:</h4>
	<p>Module: <input type="text" name="Module" size=30></p>
	<p>Type: <select name="Type">
		<option value="CL">Computer Lab</option>
		<option value="L" Selected="selected">Lecture</option>
		<option value="S">Seminar</option>
	</select></p>
	<p>Day: <Select name="Day">
		<option value="Monday">Monday</option>
		<option value="Tuesday">Tuesday</option>
		<option value="Wednesday">Wednesday</option>
		<option value="Thursday">Thursday</option>
		<option value="Friday">Friday</option>
	</Select></p>
	<p>Start Time: <select name="Start">
		<option value="09:00">09:00</option>
		<option value="10:00">10:00</option>
		<option value="11:00">11:00</option>
		<option value="12:00">12:00</option>
		<option value="13:00">13:00</option>
		<option value="14:00">14:00</option>
		<option value="15:00">15:00</option>
		<option value="16:00">16:00</option>
		<option value="17:00">17:00</option>
	</select></p>
	<p>End Time: <select name="End">
		<option value="10:00">10:00</option>
		<option value="11:00">11:00</option>
		<option value="12:00">12:00</option>
		<option value="13:00">13:00</option>
		<option value="14:00">14:00</option>
		<option value="15:00">15:00</option>
		<option value="16:00">16:00</option>
		<option value="17:00">17:00</option>
		<option value="18:00">18:00</option>
	</select></p>
	<p>Building: <select name="Building">
		<option value="C.">Cottrell</option>
		<option value="P.">Pathfoot</option>
	</select></p>
	<p>Location: <input type="text" name="Location" size=30></p>
	<p>Weeks class takes place (e.g. 1-4, 6-10): <input type="text" name="Week" size=30></p>
	<p>Staff: <input type="text" name="Staff" size=30></p>
	<p><input type="submit" value="Submit">&nbsp;<input name="Clear" type="reset" id="ClearClass" value="Clear"></p>
	<p><input type="button" value="Show Menu" onclick="toggle_hide('addClass');"></p>
</form>
</div>

<div id="addFile" style="display:none;">
<form action="FileEntry" enctype="multipart/form-data" method="post">
	<h1>Add Classes via a Text File</h1>
	<p>Browse for a file: <input type="file" name="uploadFile"></p>
	<p><input type="submit" value="Submit file"></p>
	<p><input type="button" value="Show Menu" onclick="toggle_hide('addFile');"></p>
</form>
</div>

<div id="showDatabase" style="display:none;">
<form action="Database" method="post">
	<h1>Show classes inside Database</h1>
	<p><input type="submit" value="Display Database!"></p>
	<p><input type="button" value="Show Menu" onclick="toggle_hide('showDatabase');"></p>
</form>	
</div>

<div id="deleteClass" style="display:none;">
<form action="DisplayTable" method="post">
	<h1>Show classes for specific module</h1>
	<p>Module: <input type="text" name="module"></p>
	<p><input type="submit" value="Show classes">&nbsp;<input name="Clear" type="reset" id="ClearDelete" value="Clear"></p>
	<p><input type="button" value="Show Menu" onclick="toggle_hide('deleteClass');"></p>
</form>
</div>

<div id="deleteData" style="display:none;">
<form action="DeleteAll" method="post">
	<h1>Delete all data - use at own risk!</h1>
	<p><input type="submit" value="Delete all data"></p>
	<p><input type="button" value="Show Menu" onclick="toggle_hide('deleteData');"></p>
</form>	
</div>

<div id="showTimetable" style="display:none;">
<form method="post" action="AdminTable">
<h1>Create Timetable</h1>
	<p>Module Codes: <input type="text" name="Module" size=30> e.g. CSC9P6,CSC9T6</p>
	<p><input type="submit" value="Create Timetable!">&nbsp;<input name="Clear" type="reset" id="ClearTimetable" value="Clear"></p>
	<p><input type="button" value="Show Menu" onclick="toggle_hide('showTimetable');"></p>
</form>
</div>

<div id="addUser" style="display:none;">
<form method="post" action="UserCheck">
<h1>Add a new user </h1>
<h4>Please new user details:</h4>
	<p>Username: <input type="text" name="Username" size=30></p>
	<p>Password: <input type="password" name="Password" size=30></p>
	<p><input type="submit" name="UserButton" value="Add User">&nbsp;<input name="Clear" type="reset" id="ClearUser" value="Clear"></p>
	<p><input type="button" value="Show Menu" onclick="toggle_hide('addUser');"></p>
</form>
</div>

<div id="deleteUser" style="display:none;">
<form method="post" action="DisplayUsers">
<h1>Delete User</h1>
	<p><input type="submit" name="UserButton" value="Delete User"></p>
	<p><input type="button" value="Show Menu" onclick="toggle_hide('deleteUser');"></p>
</form>
</div>

<hr>
<p>Page maintained by <a href="mailto:hmc@cs.stir.ac.uk">Hayley McCafferty</a></p>
<p>University of Stirling</p>
<p>Stirling</p>
<p>FK9 4LA</p>
<a href="http://validator.w3.org/check?uri=referer"><img
      src="http://www.w3.org/Icons/valid-html401" alt="Valid HTML 4.01 Transitional" height="31" width="88"></a>
<% } %>
</body>
</html>