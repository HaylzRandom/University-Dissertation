<%@page import="java.util.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css" href="Timetable.css">
<title>Logout System</title>
</head>
<body>


<%session.invalidate();%>
<p>You have logged out of the System
<form action="Index.html" name="Index" method="post">
<p><input type="submit" value="Go back to Index"></p>
</form>

</body>
</html>