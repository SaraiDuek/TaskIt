
<!-- this jsp expects to get taskId parameter-->
<%@ page import="taskit.dataManager.DataBase,
        taskit.dataObjects.*,
        java.sql.*,
        java.util.*" 
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String userId = null; //todo
	if (session == null || session.getAttribute("user") == null) {
	    userId = "Guest";
	    response.sendRedirect(response.encodeRedirectUrl(request.getContextPath()));
	} else {
	    userId = (String)session.getAttribute("user");
	}  
    int usersPage = 1;
    if (request.getParameter("userspage") != null && Integer.parseInt(request.getParameter("userspage")) > 0) 
        usersPage = Integer.parseInt(request.getParameter("userspage")); 

    String taskId = request.getParameter("taskId");
%>

<% 
        Connection conn = null;
        Task task = null;
        double displayUsersPerPage = 4;
        int numUsersPerPage = 1;
        List<User> usersList = new ArrayList<User>();
        List<String> friends = new ArrayList<String>();
        boolean isTask = true, isOwner = false, isParticipant = false;
        try {
            conn = DataBase.connectionPool.getConnection();
            conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            task = DataBase.getTask(conn, taskId);
            isTask = DataBase.isTask(conn, taskId);
            isOwner = DataBase.isUserOwnTask(conn, userId, taskId);
            if(!isOwner){
            	isParticipant = DataBase.isUserInTask(conn, taskId, userId);
            }
            //get user tasks
            usersList = DataBase.getUsersInTaskPerPage(conn, taskId, usersPage, displayUsersPerPage);
            numUsersPerPage = DataBase.getNumUsersInTaskPerPage(conn, taskId, displayUsersPerPage);
            if(numUsersPerPage == 0) numUsersPerPage = 1;
            if(usersPage > numUsersPerPage)
                usersPage = numUsersPerPage;  
            for (User user : usersList) {
        		if (DataBase.isFriends(conn, userId, user._userId)) {
        		    String friendId = user._userId;
        		    friends.add(friendId);
        		}
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            DataBase.closeConnection(conn);
        }
%>


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
        <div id="content" taskId=<%out.print("\"" + taskId + "\""); %> >
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
                                    <li >
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
                        <i class="icon-tasks page-title-icon"></i>
                        <% if(isTask)
                                out.print("<h2>Task</h2>");
                           else
                                out.print("<h2>Service</h2>"); %>
                        <p><% out.print(task._taskTitle); %></p>
                    </div>
                </div>
            </div>
        </div>

        <!-- My Profile -->
        <div class="testimonials container">
            <div class="testimonials-title">
            <% if(isTask)
                    out.print("<h3>Task Info</h3>");
               else
                    out.print("<h3>Service Info</h3>"); %>
            </div>
            <div class="row">
                <div class="testimonial-list span12">
                    <div class="tabbable tabs-below">
                        <div class="tab-content">
                            <div class="tab-pane active" id="A">
                                <table style="width:100%">
                                    <tr>
                                    <td>
                                        <img id="taskPhoto" src=<% out.print("\"assets/img/task.png\""); %>>
                                    </td>
                                    <td>
                                        <table>
                                            <tr>
                                                <td><p><%out.print(task._taskTitle); %></p></td>
                                                <td><p><%out.print(task._numParticipants + "/" + task._taskCapacity); %></p></td>
                                            </tr>
                                            <tr>
                                                <td><p><%if(task._taskDescription != null) out.print(task._taskDescription); %></p></td>
                                            </tr>
                                            <tr>
                                                <td><p><%out.print(task._taskOwner); %></p></td>
                                                <td><p>Cost <%out.print(task._taskCostPerUser); %></p></td>
                                            </tr>
                                            <tr>
                                            </tr>
                                        </table>
                                    </td>
                                    </tr>
                                </table>
                            </div>
                        </div>
                        <% 
                        	//if(isOwner) { //TODO connect buttons
                        	//	out.print("<a id=\"editTaskBtn\" data-toggle=\"modal\" data-target=\"#editTaskModal\"><button type=\"button\" class=\"nav-btn nav nav-tabs\">Edit</button></a>");
                           	//	out.print("<a href=\"\"><button id=\"ownerDelete\" type=\"button\" class=\"nav-btn nav nav-tabs\">Delete</button></a>"); //go back to user.jsp 
                           	//} else if(isParticipant) 
                           	//	out.print("<a href=\"\"><button type=\"button\" class=\"nav-btn nav nav-tabs\">Leave Task</button></a>"); 
                            //else
                            //	out.print("<a href=\"\"><button type=\"button\" class=\"nav-btn nav nav-tabs\">Join Task</button></a>");
                         %>
                   </div>
                </div>
            </div>
        </div>

        <!-- My Tasks and Services -->
        <div class="portfolio container">
            <div class="portfolio-title">
                <h3>Participants</h3>
            </div>
            <div class="row" id="myTasksAndServices">
                <% if (usersList != null) {
                    for (User user : usersList) {
                    	String first = "", last = "";
                    	if(user._userFirstName != null) first = user._userFirstName;
                    	if(user._userLastName != null) last = user._userLastName;
                    	if(user._userFirstName == null && user._userLastName == null) first = "<br />";
                    	out.print("<div class=\"work span3\">" +
                                "<img src="); if(user._userPhoto != null) 
                                            out.print("\"" + user._userPhoto + "\">");
                                          else 
                                            out.print("\"assets/img/usercard.png\">");                                	
                                out.print("<h4>" + user._userId + "</h4><p>" + first + " " + last + "</p><p> Balance: " + user._userBalance + "</p>" +
                                            "<div class=\"icon-awesome button-prot\">");
	                            	if(!userId.equals(user._userId)){        
	                            		out.print("<a href=\"userpage.jsp?userId=" + user._userId + "\"><i class=\"icon-search\" id=\"" + user._userId + "\"></i></a>" );
	                                    if(friends.contains(user._userId))
	                                        out.print("<a><i class=\"icon-minus button-prot\" id=\"" + user._userId + "\"></i></a>");
	                                    else
	                                        out.print("<a><i class=\"icon-plus button-prot\" id=\"" + user._userId + "\"></i></a>");
	                            	}
                                out.print("</div>" +
                            "</div>");
                    }
                }
                %>
            </div>
            <div class="scroll">
                <nav id="nav-arrows" class="nav-arrows hidden-xs hidden-sm visible-md visible-lg">
                    <% out.print("<a href=\"taskpage.jsp?taskId=" + taskId + "&userspage=" + (usersPage - 1) + "\" class=\"sl-prev line\"><</a>"); %>
                    <p class="line"><%=usersPage%>/<%=numUsersPerPage%></p>
                    <%  
	                    boolean isNextAvailable = (usersPage != numUsersPerPage);
	                    String href = isNextAvailable ? "\"taskpage.jsp?taskId=" + taskId + "&userspage=" + (usersPage + 1) + "\"" : "\"\"";
	                    out.print("<a href=" + href + " class=\"sl-next line\">></a>");
                    %>
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
        <script src="assets/js/friends.js"></script>
        <script src="assets/js/taskpage.js"></script>
</div>
    </body>

</html>

