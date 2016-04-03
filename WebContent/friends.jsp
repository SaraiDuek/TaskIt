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
    if (request.getParameter("usersPage") != null && Integer.parseInt(request.getParameter("usersPage")) > 0) 
        usersPage = Integer.parseInt(request.getParameter("usersPage")); 
    String search = request.getParameter("search");
    int count = 0;
%>

<% 
        Connection conn = null;
        double displayUsersPerPage = 4;
        int numUsersPerPage = 1;
        boolean isSearch = false;
        List<User> usersList = new ArrayList<User>();
        List<String> friends = new ArrayList<String>();
        try {
            conn = DataBase.connectionPool.getConnection();
            conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            
            //get friends
            if(search != null && !search.isEmpty()) {
            	isSearch = true;
            	usersList = DataBase.searchForUsersPerPage(conn, search, usersPage, displayUsersPerPage);
            	numUsersPerPage = DataBase.getNumSearchForUsersPerPage(conn, search, displayUsersPerPage);
            } else {
            	search = "";
            	isSearch = false;
            	usersList = DataBase.getUsersPerPageBut(conn, userId, usersPage, displayUsersPerPage);
            	numUsersPerPage = DataBase.getNumUserPagesBut(conn, userId, displayUsersPerPage);
            }
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
        <link rel="stylesheet" href="assets/css/friends.css">

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
                                    <li>
                                        <a href="tasks.jsp"><i class="icon-tasks"></i><br />Tasks</a>
                                    </li>
                                    <li  class="current-page">
                                        <a href="#"><i class="icon-group"></i><br />Friends</a>
                                    </li>
                                    <li>
                                        <a href="about.jsp"><i class="icon-user"></i><br />About</a>
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
                        <i class="icon-group page-title-icon"></i>
                        <h2>Friends /</h2>
                        <p>View your friends and make new ones</p>
                    </div>
                </div>
            </div>
        </div>

        <div class="portfolio portfolio-page container">
            <div class="row">
                <div class="testimonial-list span12">
                    <div class="tabbable tabs-below">
                        <div class="tab-content">
                            <div class="tab-pane active" id="A">
                                <form method="post">
                                	<% if(isSearch)
                                			out.print("<input autofocus class=\"search\" type=\"text\" id=\"&usersPage=" + usersPage + "\" name=\"search\" maxlength=\"64\" value=\"" + search + "\">");
                                		else
                                			out.print("<input autofocus class=\"search\" type=\"text\" id=\"&usersPage=" + usersPage + "\" name=\"search\" maxlength=\"64\" placeholder=\"search for a user...\">");
                                	%>
                                	<a id="search-btn" class="btn-default" type="submit"><i class="icon-search button-prot"></i></a>
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
                        <a class="friend" href="#">Friends</a> /
                        <a class="non-friend" href="#">Non Friends</a> /
                    </h4>
                </div>
            </div>
            <div class="row">
                <ul class="portfolio-img">
                    <% if (usersList != null) {
                        String dataType = "friend";
                        for (User user : usersList) {
                        	String first = "", last = "";
                        	if(user._userFirstName != null) first = user._userFirstName;
                        	if(user._userLastName != null) last = user._userLastName;
                        	if(user._userFirstName == null && user._userLastName == null) first = "<br />";
                        	if (userId.equals(user._userId)) continue;
                            if (friends.contains(user._userId)) dataType = "friend";
                            else dataType = "non-friend";
                            out.print("<li data-id=\"" + count++ + "\" data-type=\"" + dataType + "\" class=\"span3\">");
                            out.print("<div class=\"work\">" +
                                            "<img src="); if(user._userPhoto != null) 
                                                        out.print("\"" + user._userPhoto + "\">");
                                                      else 
                                                        out.print("\"assets/img/usercard.png\">");
                                            out.print("<h4>" + user._userId + "</h4><p>" + first + " " + last + "</p><p> Balance: " + user._userBalance + "</p>" +
                                            "<div class=\"icon-awesome button-prot\">" +
                                                "<a href=\"userpage.jsp?userId=" + user._userId + "\"><i class=\"icon-search\" id=\"" + user._userId + "\"></i></a>" );
                                                if(dataType.equals("friend"))
                                                    out.print("<a><i class=\"icon-minus button-prot\" id=\"" + user._userId + "&usersPage=" + usersPage + "\"></i></a>");
                                                else
                                                    out.print("<a><i class=\"icon-plus button-prot\" id=\"" + user._userId + "&usersPage=" + usersPage + "\"></i></a>");
                                            out.print("</div>" +
                                        "</div></li>");
                        }
                    }
                    %>
                </ul>
            </div>
            <div class="scroll">
                <nav id="nav-arrows" class="nav-arrows hidden-xs hidden-sm visible-md visible-lg">
                    <% out.print("<a href=\"friends.jsp?usersPage=" + (usersPage - 1) + "&search=" + search + "\" class=\"sl-prev line\"><</a>"); %>
                    <p class="line"><%=usersPage%>/<%=numUsersPerPage%></p>
                    <% boolean isNextAvailable = (usersPage != numUsersPerPage);
                    String href = isNextAvailable ? "\"friends.jsp?usersPage=" + (usersPage + 1) + "&search=" + search + "\"" : "\"\"";
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
        <script src="assets/js/friends.js"></script>
</div>
    </body>

</html>

