
<%@ page import="
		taskit.dataManager.DataBase,
		taskit.dataObjects.*,
		java.sql.*,
		java.util.*" 
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>



<!DOCTYPE html>
<html lang="en">
    <head>

        <meta charset="utf-8">
        <title>TaskIt - share tasks and services</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta name="description" content="">
        <meta name="author" content="">

        <!-- CSS -->
        <link rel="stylesheet" href="http://fonts.googleapis.com/css?family=Open+Sans:400italic,400">
        <link rel="stylesheet" href="http://fonts.googleapis.com/css?family=Droid+Sans">
        <link rel="stylesheet" href="http://fonts.googleapis.com/css?family=Lobster">
        <link rel="stylesheet" href="assets/bootstrap/css/bootstrap.min.css">
        <link rel="stylesheet" href="assets/prettyPhoto/css/prettyPhoto.css">
        <link rel="stylesheet" href="assets/css/flexslider.css">
        <link rel="stylesheet" href="assets/css/font-awesome.css">
        <link rel="stylesheet" href="assets/css/style.css">
        <link rel="stylesheet" href="assets/css/user.css">

        <!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
        <!--[if lt IE 9]>
            <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
        <![endif]-->

        <!-- Favicon and touch icons -->
        <link rel="shortcut icon" href="assets/ico/favicon.ico">
        <link rel="apple-touch-icon-precomposed" sizes="144x144" href="assets/ico/apple-touch-icon-144-precomposed.png">
        <link rel="apple-touch-icon-precomposed" sizes="114x114" href="assets/ico/apple-touch-icon-114-precomposed.png">
        <link rel="apple-touch-icon-precomposed" sizes="72x72" href="assets/ico/apple-touch-icon-72-precomposed.png">
        <link rel="apple-touch-icon-precomposed" href="assets/ico/apple-touch-icon-57-precomposed.png">

    </head>

    <body>
    <%
	String friendId = null; //TODO 
	if (session == null || session.getAttribute("user") == null) {
	    //response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
		//response.setHeader("Location", "/HW5");
		friendId = "Guest";
		response.sendRedirect(response.encodeRedirectUrl(request.getContextPath()));	
	} else {
	    String param = request.getParameter("userId");
	        if (param == null || param.equals("")) {
	            friendId = "Guest";
	            response.sendRedirect(response.encodeRedirectUrl(request.getContextPath() + "friends.jsp"));    
	        }
	        friendId = param;
	}	
	String userId = null; //TODO 
    if (session == null || session.getAttribute("user") == null) {
		userId = "Guest";
		response.sendRedirect(response.encodeRedirectUrl(request.getContextPath()));
    } else {
        userId = (String) session.getAttribute("user");
    } 
    int tasksPage = 1;
    if (request.getParameter("taskspage") != null && Integer.parseInt(request.getParameter("taskspage")) > 0) 
        tasksPage = Integer.parseInt(request.getParameter("taskspage")); 
    int participateTasksPage = 1;
    if (request.getParameter("partpage") != null && Integer.parseInt(request.getParameter("partpage")) > 0) 
        participateTasksPage = Integer.parseInt(request.getParameter("partpage"));
%>

<% 
		Connection conn = null;
		User user = null;
		boolean isFriend  = false;
		double displayTasksPerPage = 4;
		int numTasksPerPage = 1;
		List<Task> tasksList = null;
		List<Task> participateTasksList = null;
		double displayParticipateTasksPerPage = 4;
		int numParticipateTasksPerPage = 1;
        Map<String, Task> participating = new HashMap<String, Task>();
        Map<String, Task> tasksFriendOwns = new HashMap<String, Task>();
        Map<String, Task> ownedTasks = new HashMap<String, Task>();
		String err = "";
		try {
			conn = DataBase.connectionPool.getConnection();
			conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
			user = DataBase.getUser(conn, friendId);
			isFriend = DataBase.isFriends(conn, userId, friendId);
			
			//get user tasks
			displayTasksPerPage = 4;
			tasksList = DataBase.getUserTasksPerPage(conn, friendId, tasksPage, displayTasksPerPage);
			numTasksPerPage = DataBase.getNumUserTasksPerPage(conn, friendId, displayTasksPerPage);
			if (numTasksPerPage == 0)
			    numTasksPerPage = 1;
            if(tasksPage > numTasksPerPage)
                tasksPage = numTasksPerPage;
            for (Task task : tasksList) {
         		Task temp = task;
         		Task tmp = task;
         		if (DataBase.isUserInTask(conn, temp._taskId, userId)) {
         		    participating.put(temp._taskId, temp);
         		}
             }

			//get tasks user participares in
			displayParticipateTasksPerPage = 4;
			participateTasksList = DataBase.getUserParticipateTasksPerPage(conn, friendId, participateTasksPage, displayParticipateTasksPerPage);
			numParticipateTasksPerPage = DataBase.getNumUserParticipateTasksPerPage(conn, friendId, displayParticipateTasksPerPage);
			if (numParticipateTasksPerPage == 0)
			    numParticipateTasksPerPage = 1;
            if(participateTasksPage > numParticipateTasksPerPage)
                participateTasksPage = numParticipateTasksPerPage;
            for (Task task : participateTasksList) {
         		Task temp = task;
         		Task tmp = task;
         		if (DataBase.isUserInTask(conn, temp._taskId, userId)) {
         		    participating.put(temp._taskId, temp);
         		}
         		if (DataBase.isUserOwnTask(conn, userId, task._taskId)) {
         			ownedTasks.put(tmp._taskId, tmp);
         		}
             }
		}
		catch (Exception e) {
			e.printStackTrace();
			err = e.getMessage();
			
		}
		finally {
		    DataBase.closeConnection(conn);
		}
		if (friendId.equals("Guest")) {
	            user = new User("", "", "", "","", "", "", "", 0, true);
	        }
%>
        <div id="content">
            <div id="topWhiteFix"></div>
            <div id="botWhiteFix"></div>
        <!-- Header -->
        <div class="container">
            <div class="header row">
                <div class="span12">
                    <div class="navbar">
                        <div class="navbar-inner">
                            <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
                                <span class="icon-bar"></span>
                                <span class="icon-bar"></span>
                                <span class="icon-bar"></span>
                            </a>
                            <h1>
                                <a class="brand">TaskIt - share tasks and services</a>
                            </h1>
                            <div class="nav-collapse collapse">
                                <ul class="nav pull-right">
                                    <li>
                                        <a href="user.jsp"><i class="icon-user"></i><br />User</a>
                                    </li>
                                    <li>
                                        <a href="tasks.jsp"><i class="icon-tasks"></i><br />Tasks</a>
                                    </li>
                                    <li>
                                        <a href="friends.jsp"><i class="icon-group"></i><br />Friends</a>
                                    </li>
                                    <li>
                                        <a href="about.jsp"><i class="icon-file"></i><br />About</a>
                                    </li>
                                    <li>
                                        <a href="" id="logout"><i class="icon-star"></i><br />Log Out</a>
                                    </li>
                                    <li>
                                    	<a><i></i>Hello<br /><b><% out.print(userId); %></b></a>
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Page Title -->
        <div class="page-title">
            <div class="container">
                <div class="row">
                    <div class="span12">
                        <i class="icon-user page-title-icon"></i>
                        <h2>Profile /</h2>
                        <p><%=friendId %></p>
                    </div>
                </div>
            </div>
        </div>

        <!-- My Profile -->
        
		
        <div class="testimonials container">
            <div class="testimonials-title">
                <h3><%=friendId %></h3>
            </div>
            <div class="row">
                <div class="testimonial-list span12">
                    <div class="tabbable tabs-below">
                        <div class="tab-content">
                            <div class="tab-pane active" id="A">
                                <table style="width:100%">
                                    <tr>
                                    <td>
                                        <img id="usrPhoto" src="assets/img/user.png" title="" alt="">
                                    </td>
                                    <td>
                                        <table>
                                            <tr>
                                                <td><p id="uid"><%=friendId %></p></td>
                                                <td><p>Balance <%out.print(user._userBalance); %></p></td>
                                            </tr>
                                            <tr>
                                                <td><p><%if(user._userFirstName != null) out.print(user._userFirstName + " ");
                                                if(user._userLastName != null) out.print(user._userLastName); %></p></td>
                                                <td><p><%if(user._userGender != null) out.print(user._userGender); %></p></td>
                                            </tr>
                                            <tr>
                                                <td><p><%if(user._userAddress != null) out.print(user._userAddress); %></p></td>
                                                <td><p><%if(user._userPhone != null) out.print(user._userPhone); %></p></td>
                                            </tr>
                                        </table>
                                    </td>
                                    </tr>
                                </table>
                            </div>
                        </div>
                        <%  //TODO connect buttons
						//	if(isFriend) 
                        //   		out.print("<a href=\"\"><button type=\"button\" class=\"nav-btn nav nav-tabs\">Remove Friend</button></a>"); 
                        //    else
                        //    	out.print("<a href=\"\"><button type=\"button\" class=\"nav-btn nav nav-tabs\">Add Friend</button></a>");
                         %>
                   </div>
                </div>
            </div>
        </div>
		<!-- My Tasks and Services -->
        <div class="portfolio container">
            <div class="portfolio-title">
                <h3><%=friendId %>'s Tasks and Services</h3>
            </div>
            <div class="row" id="myTasksAndServices">
                <% if (tasksList != null) { 
                	for (Task task : tasksList) {
                		String isTask = "Service";
                		if(task._isTask == 1) isTask = "Task";
	                	out.print("<div class=\"work span3\">" +
                                "<img src=\"assets/img/taskcard.png\">");
                        out.print("<h4>" + task._taskTitle + "</h4>" +
		                    	"<p>" + isTask + "       " + task._numParticipants + "/" + task._taskCapacity + "       Cost: " + task._taskCostPerUser + "</p>" +
                        "<div class=\"icon-awesome button-prot\">" +
                            "<a href=\"taskpage.jsp?taskId=" + task._taskId + "\"><i class=\"button-prot icon-search \" id=\"" + task._taskId + "\"></i></a>");
                            if(participating.get(task._taskId) != null)
                                out.print("<a><i class=\"icon-minus button-prot\" id=\"" + task._taskId + "\"></i></a>");
                            else
                                out.print("<a><i class=\"icon-plus button-prot\" id=\"" + task._taskId + "\"></i></a>");

                            	
                        out.print("</div>" +
                    "</div></li>");
            		}
                }
                %>
			</div>
            <div class="scroll">
                <nav id="nav-arrows" class="nav-arrows hidden-xs hidden-sm visible-md visible-lg">
                    <% out.print("<a href=\"userpage.jsp?userId=" + friendId + "&taskspage=" + (tasksPage - 1) + "\" class=\"sl-prev line\"><</a>"); %>
                    <p class="line"><%=tasksPage%>/<%=numTasksPerPage%></p>
                    <% boolean isNextAvailable = (tasksPage != numTasksPerPage);
                    String href = isNextAvailable ? "\"userpage.jsp?userId=" + friendId + "&taskspage=" + (tasksPage + 1) + "\"" : "\"\"";
                    out.print("<a href=" + href + " class=\"sl-next line\">></a>");%>
                </nav>
            </div>
        </div>

        <!-- Registered Tasks and Services -->
        <div class="portfolio container">
            <div class="portfolio-title">
                <h3>Tasks and Services for <%=friendId %></h3>
            </div>
            <div class="row">
                <% if (participateTasksList != null) {
                	for (Task task : participateTasksList) {
                		String isTask = "Service";
                		if(task._isTask == 1) isTask = "Task";
	                	out.print("<div class=\"work span3\">" +
                                "<img src=\"assets/img/taskcard.png\">");
                        out.print("<h4>" + task._taskTitle + "</h4>" +
		                    	"<p>" + isTask + "       " + task._numParticipants + "/" + task._taskCapacity + "       Cost: " + task._taskCostPerUser + "</p>" +
                        "<div class=\"icon-awesome button-prot\">" +
                            "<a href=\"taskpage.jsp?taskId=" + task._taskId + "\"><i class=\"button-prot icon-search \" id=\"" + task._taskId + "\"></i></a>");
                            if(participating.get(task._taskId) != null)
                                out.print("<a><i class=\"icon-minus button-prot\" id=\"" + task._taskId + "\"></i></a>");
                            else if(ownedTasks.get(task._taskId) == null)
                                out.print("<a><i class=\"icon-plus button-prot\" id=\"" + task._taskId + "\"></i></a>");
                        out.print("</div>" +
                    "</div></li>");
            		}
                }
                %>
            </div>
            <div class="scroll">
                <nav id="nav-arrows" class="nav-arrows hidden-xs hidden-sm visible-md visible-lg">
                    <% out.print("<a href=\"userpage.jsp?userId=" + friendId + "&partpage=" + (participateTasksPage - 1) + "\" class=\"sl-prev line\"><</a>"); %>
                    <p class="line"><%=participateTasksPage%>/<%=numParticipateTasksPerPage%></p>
                    <% isNextAvailable = (participateTasksPage != numParticipateTasksPerPage);
                     href = isNextAvailable ? "\"userpage.jsp?userId=" + friendId + "&partpage=" + (participateTasksPage + 1) + "\"" : "\"\"";
                    out.print("<a href=" + href + " class=\"sl-next line\">></a>");%>
                </nav>
            </div>
        </div>

        <!-- Footer -->
        <footer>
            <div class="container">
                <div class="row">
                <div class="footer-border"></div> 
                <div class="row">
                    <div class="copyright span4">
                        <p>Copyright 2012 Andia - All rights reserved. Template by http://azmind.com.</p>
                    </div>
                    <div class="social span8">
                        <a class="facebook" href=""></a>
                        <a class="dribbble" href=""></a>
                        <a class="twitter" href=""></a>
                        <a class="pinterest" href=""></a>
                    </div>
                </div>
            </div>
            </div>
        </footer>

        <!-- Javascript -->
        <script src="assets/js/jquery-1.8.2.min.js"></script>
        <script src="assets/bootstrap/js/bootstrap.min.js"></script>
        <script src="assets/js/jquery.flexslider.js"></script>
        <script src="assets/js/jquery.tweet.js"></script>
        <script src="assets/js/jflickrfeed.js"></script>
        <script src="http://maps.google.com/maps/api/js?sensor=true"></script>
        <script src="assets/js/jquery.ui.map.min.js"></script>
        <script src="assets/js/jquery.quicksand.js"></script>
        <script src="assets/prettyPhoto/js/jquery.prettyPhoto.js"></script>
        <script src="assets/js/scripts.js"></script>
        <script src="assets/js/tasks.js"></script>
</div>
    </body>

</html>

