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
    int tasksPage = 1;
    if (request.getParameter("taskspage") != null && Integer.parseInt(request.getParameter("taskspage")) > 0) 
        tasksPage = Integer.parseInt(request.getParameter("taskspage")); 
    String search = request.getParameter("search");
    int count = 0;
%>

<%      Connection conn = null;
        User user = null;
        double displayTasksPerPage = 4;
        int numTasksPerPage = 1;
        boolean isSearch = false;
        List<Task> tasksList = null;
        Map<String, Task> participating = new HashMap<String, Task>();
        try {
            conn = DataBase.connectionPool.getConnection();
            conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            user = DataBase.getUser(conn, userId);
            
            //get user tasks
            if(search != null && !search.isEmpty()) {
            	isSearch = true;
            	tasksList = DataBase.searchForUserFriendsTasksPerPage(conn, userId, search, tasksPage, displayTasksPerPage);
            	numTasksPerPage = DataBase.getNumSearchForUserFriendsTasksPerPage(conn, userId, search, displayTasksPerPage);
            } else {
            	search = "";
            	isSearch = false;
            	tasksList = DataBase.getUserFriendsTasksPerPage(conn, userId, tasksPage, displayTasksPerPage);
            	numTasksPerPage = DataBase.getNumTasksAvailableToUser(conn, userId, displayTasksPerPage);
            }
            if(numTasksPerPage == 0) numTasksPerPage = 1;
            if(tasksPage > numTasksPerPage)
                tasksPage = numTasksPerPage;
            for (Task task : tasksList) {
        		Task temp = task;
        		if (DataBase.isUserInTask(conn, temp._taskId, userId)) {
        		    participating.put(temp._taskId, temp);
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
                                    <li class="current-page">
                                        <a href="#"><i class="icon-tasks"></i><br />Tasks</a>
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
                        <h2 id="contentTitle" userid=<%out.print("\"" + userId +"\""); %>>Tasks / Services</h2>
                        <p>Here you can find new tasks and services</p>
                    </div>
                </div>
            </div>
        </div>

        <!-- Portfolio -->
        <div class="portfolio portfolio-page container">
            <div class="row">
                <div class="testimonial-list span12">
                    <div class="tabbable tabs-below">
                        <div class="tab-content">
                            <div class="tab-pane active" id="A">
                                <form action="" method="post">
                                	<% if(isSearch)
                                			out.print("<input autofocus class=\"search\" type=\"text\" id=\"&tasksPage=" + tasksPage + "\" maxlength=\"64\" name=\"search\" value=\"" + search + "\">");
                                		else
                                			out.print("<input autofocus class=\"search\" type=\"text\" id=\"&tasksPage=" + tasksPage + "\" maxlength=\"64\" name=\"search\" placeholder=\"search for a task...\">");
                                	%>
                                	<a id="search-btn" class="btn-default disabled" type="submit"><i class="icon-search button-prot"></i></a>
                                </form>
                            </div>
                        </div>
                   </div>
                </div>
            </div>
            <div class="row">
                <div class="portfolio-navigator span12">
                    <h4 class="filter-portfolio">
                        <a class="all" id="active-imgs" href="#">All</a> /
                        <a class="mytask" id="" href="#">Tasks I'm In</a> /
                        <a class="myservice" id="" href="#">Services I'm In</a> /
                        <a class="task" id="" href="#">Available Tasks</a> /
                        <a class="service" id="" href="#">Available Services</a> 
                    </h4>
                </div>
            </div>
            <div class="row">
                <ul class="portfolio-img">
                    <% if (tasksList != null) { 
                        String dataType = "mytask";
                        for (Task task : tasksList) {
                        	String isTask = "Service";
                    		if(task._isTask == 1) isTask = "Task";
                        	if(participating.containsKey(task._taskId)){ 
                                if(task._isTask == 1) dataType = "myTask";
                                else dataType = "myservice";
                            } else {
                                if(task._isTask == 1) dataType = "task";
                                else dataType = "service";
                            }
                            out.print("<li data-id=\"" + count++ + "\" data-type=\"" + dataType + "\" class=\"span3\">");
                            out.print("<div class=\"work\">" + 
                            			"<img src=\"assets/img/taskcard.png\">");
                                            out.print("<h4>" + task._taskTitle + "</h4>" +
                			                    	"<p>" + isTask + "       " + task._numParticipants + "/" + task._taskCapacity + "       Cost: " + task._taskCostPerUser + "</p>" +
                                            "<div class=\"icon-awesome button-prot\">" +
                                                "<a href=\"taskpage.jsp?taskId=" + task._taskId + "\"><i class=\"button-prot icon-search \" id=\"" + task._taskId + "\"></i></a>");
                                                if(participating.get(task._taskId) != null)
                                                    out.print("<a><i class=\"icon-minus button-prot\" id=\"" + task._taskId + "&tasksPage=" + tasksPage + "\"></i></a>");
                                                else
                                                    out.print("<a><i class=\"icon-plus button-prot\" id=\"" + task._taskId + "&tasksPage=" + tasksPage + "\"></i></a>");
                                            out.print("</div>" +
                                        "</div></li>");
                        }
                    }
                    %>
                </ul>
            </div>
           <div class="scroll">
                <nav id="nav-arrows" class="nav-arrows hidden-xs hidden-sm visible-md visible-lg">
                    <% out.print("<a href=\"tasks.jsp?taskspage=" + (tasksPage - 1) + "&search=" + search + "\" class=\"sl-prev line\"><</a>"); %>
                    <p class="line"><%=tasksPage%>/<%=numTasksPerPage%></p>
                    <% boolean isNextAvailable = (tasksPage != numTasksPerPage);
                    String href = isNextAvailable ? "\"tasks.jsp?taskspage=" + (tasksPage + 1) + "&search=" + search + "\"" : "\"\"";
                    out.print("<a href=" + href + " class=\"sl-next line\">></a>");%>
                </nav>
            </div>
        </div>

        <!-- Footer -->
        <footer>
            <div class="container">
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

