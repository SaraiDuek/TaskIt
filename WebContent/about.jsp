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
                                    <li class="current-page">
                                        <a href="#"><i class="icon-file"></i><br />About</a>
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
                        <h2>About Us /</h2>
                        <p>Below you can find more information about our project</p>
                    </div>
                </div>
            </div>
        </div>

        <!-- About Us Text -->
        <div class="about-us container">
            <div class="row">
                <div class="about-us-text span12">
                    <h4>About</h4>
                    <p>TaskIt is a platform that enables you and your community to trade your favors using time as exchange. This way you'll know who do you want to ask for favors :)</p>
                    <h4>Special Features</h4>
                    <p><a href="rdf.jsp">RDF Query</a></p>
                    <p><a href="search.jsp">Graph Search </a></p>
                    <h4>System</h4>
                    <p>We used MySQL for Database with InnoDB</p>
                    <h4>Project Movie</h4>
                    <iframe width="560" height="315" src="https://www.youtube.com/embed/ZlsyMrB6sq8" frameborder="0" allowfullscreen></iframe>
                    <h4>Remarks</h4>
                    <p>Works best with Google Chrome</p>
                </div>
            </div>
        </div>

        <!-- Meet Our Team -->
        <div class="team container">
            <div class="team-title">
                <h3>Meet Our Team</h3>
            </div>
            <div id="team" class="row">
                <div class="team-text span3">
                    <img src="assets/img/team/1.jpg" alt="">
                    <h4>Sarai Duek</h4>
                    <p>MSc student at the Technion</p>
                    <p>shedvarod@gmail.com</p>
                    <div class="social-links">
                        <a target="blank" class="facebook" href="https://www.facebook.com/sarai.duek"></a>
                        <a target="blank" class="twitter" href="https://twitter.com/shedvarod"></a>
                        <a target="blank" class="linkedin" href="https://www.linkedin.com/pub/sarai-duek"></a>
                    </div>
                </div>
                <div class="team-text span3">
                    <img src="assets/img/team/2.jpg" alt="">
                    <h4>Aviad Rom</h4>
                    <p>BSc student at the Technion</p>
                    <p>aviadrom@campus.technion.ac.il</p>
                    <p></p>
                    <div class="social-links">
                        <a target="blank" class="facebook" href="https://www.facebook.com/aviad.rom.9?fref=ts"></a>
                        <a target="blank" class="linkedin" href="https://il.linkedin.com/in/aviadrom"></a>
                    </div>
                </div>
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
</div>
    </body>

</html>

