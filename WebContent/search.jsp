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
    } else {
        userId = (String) session.getAttribute("user");
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
                                    	<a><i></i>Hello<br /><b id="user"><% out.print(userId); %></b></a>
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
                        <i class="icon-file page-title-icon"></i>
                        <h2>Graph Search /</h2>
                        <p>Search for users friends up to a certain distance on your friends graph</p><br/>
                        <p></p>
                    </div>
                </div>
            </div>
        </div>

<!-- About Us Text -->
        <div class="api container">
            <div class="row">
                <div class="about-us-text span12">
                    <h4>About</h4>
                    <p>The graph search API enables you to get data about the users a certain distance on the friends graph</p>
                    <h4>Usage</h4>
                    <p>http://54.68.7.213:8080/HW5/search/u/[userId]?maxDist=[n]<br/>
                    	Where userId is the ID of the user you want to search around, and maxDist is the maximum distance on the friends graph<br/>
                    	you would like to reach</p>
                    	<h4>Results</h4>
			                <p>The results are in a JSON format, according to the following:<br />
			                {dist_1:[ {user container1}...{user container n}], ... ,dist_m:[ {user container1}...{user container k}]}<br />
			                user containers would have the following properties: userId, balance, firstName, lastName, gender, address, phone (some might be empty)</p>
                </div>
            </div>
        </div>

        <!-- My Profile -->
        <div class="testimonials container">
            <div class="testimonials-title">
                <h3>Search</h3>
            </div>
            <div class="row">
                <div class="testimonial-list span12">
                    <div class="tabbable tabs-below">
                        <div class="tab-content">
                            <div class="tab-pane active" id="A">
                                <form method="post">
                                    <table style="width:100%; padding:50px;">
                                        <tr>
                                            <td id="querylong">
                                                <input type="number" id="query" name="query" placeholder="enter max distance for search..." required>
                                            </td>
                                            <td id="queryshort">
                                                <button type="submit" id="querySubmit" class="nav-btn nav nav-tabs">Search</button>   
                                            </td>
                                        </tr>
                                    </table>
                                </form>
                            </div>
                        </div>
                   </div>
                </div>
            </div>
        </div>
        <div class="testimonials container">
            <div class="testimonials-title">
                <h3>Results</h3>
            </div>
            <div class="row">
                <div class="testimonial-list span12">
                    <div class="tabbable tabs-below">
                        <div class="tab-content">
                            <div class="tab-pane active" id="A">
                                <p id="result"></p>
                            </div>
                        </div>
                   </div>
                </div>
            </div>
        </div>
        <!-- Footer -->
        <footer>
            <div class="container">
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
        <script src="assets/js/search.js"></script>
</div>
    </body>

</html>

