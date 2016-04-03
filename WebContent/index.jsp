<%@ page import="
		taskit.dataManager.DataBase,
		taskit.dataObjects.*,
		java.sql.*,
		java.util.*" 
%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	String url;
	if (session == null || session.getAttribute("user") == null) {
	    url = "login.html";
	}
	else {
	    url = "user.jsp";
	}
	response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
	response.setHeader("Location", url);
%>    
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>TaskIt Page Redirection</title>
</head>
<body>

</body>
</html>