
<%@ page import="taskit.dataManager.DataBase,
		taskit.dataObjects.*,
		java.sql.*,
		java.util.*" 
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>



<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html lang="en" xmlns="http://www.w3.org/1999/xhtml">
    <head>

        <title>TaskIt - share tasks and services</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <meta name="description" content=""/>
        <meta name="author" content=""/>

        <!-- CSS -->
        <link rel="stylesheet" href="http://fonts.googleapis.com/css?family=Open+Sans:400italic,400"/>
        <link rel="stylesheet" href="http://fonts.googleapis.com/css?family=Droid+Sans"/>
        <link rel="stylesheet" href="http://fonts.googleapis.com/css?family=Lobster"/>
        <link rel="stylesheet" href="assets/bootstrap/css/bootstrap.min.css"/>
        <link rel="stylesheet" href="assets/prettyPhoto/css/prettyPhoto.css"/>
        <link rel="stylesheet" href="assets/css/flexslider.css"/>
        <link rel="stylesheet" href="assets/css/font-awesome.css"/>
        <link rel="stylesheet" href="assets/css/style.css"/>
        <link rel="stylesheet" href="assets/css/user.css"/>

        <!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
        <!--[if lt IE 9]>
            <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
        <![endif]-->

        <!-- Favicon and touch icons -->
        <link rel="shortcut icon" href="assets/ico/favicon.ico"/>
        <link rel="apple-touch-icon-precomposed" href="assets/ico/apple-touch-icon-144-precomposed.png"/>
        <link rel="apple-touch-icon-precomposed" href="assets/ico/apple-touch-icon-114-precomposed.png"/>
        <link rel="apple-touch-icon-precomposed" href="assets/ico/apple-touch-icon-72-precomposed.png"/>
        <link rel="apple-touch-icon-precomposed" href="assets/ico/apple-touch-icon-57-precomposed.png"/>

    </head>

    <body class="sneak">
    <%
	String userId = null; //todo
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
		double displayTasksPerPage = 3;
		int numTasksPerPage = 1;
		double displayParticipateTasksPerPage = 4;
		int numParticipateTasksPerPage = 1;
		List<Task> tasksList = null;
		List<Task> participateTasksList = null;
		String err = "";
		try {
			conn = DataBase.connectionPool.getConnection();
			conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
			user = DataBase.getUser(conn, userId);
			
			//get user tasks
			displayTasksPerPage = 3;
			tasksList = DataBase.getUserTasksPerPage(conn, userId, tasksPage, displayTasksPerPage);
			numTasksPerPage = DataBase.getNumUserTasksPerPage(conn, userId, displayTasksPerPage);
			if (numTasksPerPage == 0)
			    numTasksPerPage = 1;
            if(tasksPage > numTasksPerPage)
                tasksPage = numTasksPerPage;

			//get tasks user participares in
			displayParticipateTasksPerPage = 4;
			participateTasksList = DataBase.getUserParticipateTasksPerPage(conn, userId, participateTasksPage, displayParticipateTasksPerPage);
			numParticipateTasksPerPage = DataBase.getNumUserParticipateTasksPerPage(conn, userId, displayParticipateTasksPerPage);
			if (numParticipateTasksPerPage == 0)
			    numParticipateTasksPerPage = 1;
            if(participateTasksPage > numParticipateTasksPerPage)
                participateTasksPage = numParticipateTasksPerPage;			
		}
		catch (Exception e) {
			e.printStackTrace();
			err = e.getMessage();
			
		}
		finally {
		    DataBase.closeConnection(conn);
		}
		if (userId.equals("Guest")) {
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
                                    <li class="current-page">
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
                        <p>Your personal space <%=userId %></p>
                    </div>
                </div>
            </div>
        </div>

        <!-- My Profile -->
         <div class="modal fade" id="changePasswordModal">
  			<div class="modal-dialog">
			    <div class="modal-content">
      				<div class="modal-header">
        				<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        				<div class="portfolio-title">
                			<h3>Change Password</h3>
            			</div>
      				</div>
      				<div class="modal-body">
        				<form action="" method="post">
                                    <table style="width:100%; padding:50px;">
                                    <tr>
                                            <td>
                                            	<label for="oldPass">Old Password</label>
                                                <input type="password" id="oldPass" name="oldPass" placeholder="enter current password..." required maxlength="64"/>                                              
                                                <label for="newPass">New Password</label>
                                                <input type="password" id="newPass" name="newPass" placeholder="enter new password..." required maxlength="64"/>
                                                <label for="conNewPass">Confirm new Password</label>
                                                <input type="password" id="conNewPass" name="conNewPass" placeholder="confirm new password..." required maxlength="64"/>
                                            </td>
                                	 </tr>  
                                    </table>
                                </form>
      				</div>
		      		<div class="modal-footer">
		        		<button type="button" id="changePassCloseBtn" class="btn btn-default" data-dismiss="modal">Cancel</button>
		        		<button type="button" id="changePassSubmit" class="btn btn-primary">Submit</button>
		      		</div>
   				</div><!-- /.modal-content -->
  			</div><!-- /.modal-dialog -->
		</div><!-- /.modal -->
        <div class="modal fade" id="editUserModal">
  			<div class="modal-dialog">
			    <div class="modal-content">
      				<div class="modal-header">
        				<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        				<div class="portfolio-title">
                			<h3>Edit profile</h3>
            			</div>
      				</div>
      				<div class="modal-body">
        				<form action="" method="post">
                                    <table style="width:100%; padding:50px;">
                                    <tr>
                                            <td>
                                            	<label for="firstName">First Name</label>
                                                <input type="text" id="firstName" name="firstName" placeholder="enter your first name..." maxlength="64"/>                                              
                                                <label for="phone">Phone</label>
                                                <input type="text" id="phone" name="phone" placeholder="enter your phone number..."/>
                                            </td>
                                            <td>
                                                <label for="lastName">Last Name</label>
                                                <input type="text" id="lastName" name="lastName" placeholder="enter your last name..." maxlength="64"/>
                                                <label for="address">Address</label>
                                                <input type="text" id="address" name="address" placeholder="enter your address..."/>
                                            </td>
                                	 </tr>  
                                	 <tr>
                                		 <td>
                                	 		<div class="dropdown">
                                          	        <label for="gender">Gender</label>
                                                    <select id="gender" class="form-control">
                                                        <option value="Male">Male</option>
                                                        <option value="Female">Female</option>
                                                    </select>
                                                </div>
                                	 	</td>
                                	 	<td></td>
                                	 </tr>
                                    </table>
                                </form>
      				</div>
		      		<div class="modal-footer">
		      			<button type="button" id="deleteUserBtn" class="btn btn-danger deleteUser">Delete Profile</button>
		        		<button type="button" id="editUserCloseBtn" class="btn btn-default" data-dismiss="modal">Cancel</button>
		        		<button type="button" id="editUserSubmitBtn" class="btn btn-primary">Submit</button>
		      		</div>
   				</div><!-- /.modal-content -->
  			</div><!-- /.modal-dialog -->
		</div><!-- /.modal -->
		
        <div class="testimonials container">
            <div class="testimonials-title">
                <h3>My Profile</h3>
            </div>
            <div class="row">
                <div class="testimonial-list span12">
                    <div class="tabbable tabs-below">
                        <div class="tab-content">
                            <div class="tab-pane active" id="A">
                                <table style="width:100%">
                                    <tr>
                                    <td>
                                        <img id="usrPhoto" src="assets/img/user.png" title="" alt=""/>
                                    </td>
                                    <td>
                                        <table>
                                            <tr>
                                                <td><p id="uid"><%=userId %></p></td>
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
                       <a id="editUserBtn" data-toggle="modal" data-target="#editUserModal"><button type="button" class="nav-btn nav nav-tabs">Edit</button></a>
                       <a href=""><button  id="removeTasks" type="button" class="nav-btn nav nav-tabs">Remove All Tasks</button></a>
                       <a id="changePasswordBtn" data-toggle="modal" data-target="#changePasswordModal"><button type="button" class="nav-btn nav nav-tabs">Change Password</button></a>
                       <!-- a href=""><button id="leaveTasks" type="button" class="nav-btn nav nav-tabs">Leave All Tasks</button></a-->
                   </div>
                </div>
            </div>
        </div>

        <!-- My Tasks and Services -->
        <div class="modal fade" id="addTaskModal">
  			<div class="modal-dialog">
			    <div class="modal-content">
      				<div class="modal-header">
        				<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        				<div class="portfolio-title">
                			<h3>Add Task/Service</h3>
            			</div>
      				</div>
      				
      				<div class="modal-body">
        				<form action="" method="post">
                                    <table style="width:100%; padding:50px;">
                                    <tr>
                                            <td>
                                            	<label for="title">Title</label>
                                                <input type="text" id="title" name="title" placeholder="enter task title..." required maxlength="64"/>                                              
                                                <div class="dropdown">
                                                    <label for="taskType">Task/Service</label>
                                                    <select id="taskType" class="form-control" required>
                                                        <option value="Task">Task</option>
                                                        <option value="Service">Service</option>
                                                    </select>
                                                </div>
                                                <label for="capacity">Capacity</label>
                                                <input type="number" id="capacity" name="capacity" value="1" required/>
                                            </td>
                                            <td>
                                                <label for="cost">Cost Per User</label>
                                                <input type="number" id="cost" name="cost" value="1" required/>
                                                <label for="desc">Description</label>
                                                <textarea id="desc" name="desc" placeholder="describe the task/service..." maxlength="512"></textarea>
                                            </td>
                                	 </tr>  
                                    </table>
                                </form>
      				</div>
		      		<div class="modal-footer">
		        		<button type="button" id="addModalCloseBtn" class="btn btn-default" data-dismiss="modal">Cancel</button>
		        		<button type="button" id="addModalSubmitBtn" class="btn btn-primary">Submit</button>
		      		</div>					
   				</div><!-- /.modal-content -->
  			</div><!-- /.modal-dialog -->
		</div><!-- /.modal -->
		
		<!-- My Tasks and Services -->
        <div class="modal fade" id="editTaskModal">
  			<div class="modal-dialog">
			    <div class="modal-content">
      				<div class="modal-header">
        				<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        				<div class="portfolio-title">
                			<h3>Edit Task/Service</h3>
            			</div>
      				</div>
      				<div class="modal-body">
        				<form action="" method="post">
                                    <table style="width:100%; padding:50px;">
                                    <tr>
                                            <td>
                                            	<label for="updateTitle">Title</label>
                                                <input type="text" id="updateTitle" name="updateTitle" placeholder="enter task title..." required maxlength="64"/>                                              
                                                <label for="updateCapacity">Capacity</label>
                                                <input type="number" id="updateCapacity" name="updateCapacity" value="1" required/>
                                            </td>
                                            <td>
                                                <label for="updateCost">Cost Per User</label>
                                                <input type="number" id="updateCost" name="updateCost" value="1" required/>
                                                <label for="updateDesc">Description</label>
                                                <textarea id="updateDesc" name="updateDesc" placeholder="describe the task/service..." maxlength="512"></textarea>
                                                <!-- <label for="updateEnd">End Date</label>
                                                <input type="text" id="updateEnd" name="updateEnd" placeholder="format of MM dd YYYY"/> -->
                                            </td>
                                	 </tr>  
                                    </table>
                                </form>
      				</div>
		      		<div class="modal-footer">
		      			<button type="button" id="deleteTaskBtn" class="btn btn-danger deleteTask">Delete Task</button>
		        		<button type="button" id="updateModalCloseBtn" class="btn btn-default" data-dismiss="modal">Cancel</button>
		        		<button type="button" id="updateModalSubmitBtn" class="btn btn-primary">Submit</button>
		      		</div>
   				</div><!-- /.modal-content -->
  			</div><!-- /.modal-dialog -->
		</div><!-- /.modal -->
		
        <div class="portfolio container">
            <div class="portfolio-title">
                <h3>My Tasks and Services</h3>
            </div>
            <div class="row" id="myTasksAndServices">
                <div class="work span3" id="addTaskService">
                    <img src="assets/img/add.jpg" alt=""/>
                    <h4>Create new task or service</h4>
                    <p>To add a task/service click on <br /> the plus sign </p>
                    <div class="icon-awesome">
                        <a data-toggle="modal" data-target="#addTaskModal"><i id="addTaskBtn" class="icon-plus"></i></a>
                    </div>
                </div>
                <% if (tasksList != null) { 
                	for (Task task : tasksList) {
                		String isTask = "Service";
                		if(task._isTask == 1) isTask = "Task";
	                	out.print("<div class=\"work span3\">" +
	                					"<img src=\"assets/img/taskcard.png\" alt=\"\"/>");
	                			//	if(task._photoUri != null) 
                                  //    			out.print("\"" + task._photoUri + "\">");
									//		  else 
										//		  	out.print("\"assets/img/task.png\">");
		                    		   	out.print("<h4>" + task._taskTitle + "</h4>" +
		    			                    	"<p>" + isTask + "       " + task._numParticipants + "/" + task._taskCapacity + "       Cost: " + task._taskCostPerUser + "</p>" +
		                    			"<div class=\"icon-awesome\">" +
			                        		"<a href=\"taskpage.jsp?taskId=" + task._taskId + "\"><i class=\"icon-search\"></i></a>" +
			                        		"<a data-toggle=\"modal\" data-target=\"#editTaskModal\"><i class=\"icon-pencil\" id=\"" + task._taskId + "\"></i></a>");
		                    			out.print("</div>" +
	                				"</div>");
            		}
                }
                %>
			</div>
            <div class="scroll">
                <nav id="nav-arrows" class="nav-arrows hidden-xs hidden-sm visible-md visible-lg">
                    <% out.print("<a href=\"user.jsp?taskspage=" + (tasksPage - 1) + "\" class=\"sl-prev line\">&lt;</a>"); %>
                    <p class="line"><%=tasksPage%>/<%=numTasksPerPage%></p>
                    <% boolean isNextAvailable = (tasksPage != numTasksPerPage);
                    String href = isNextAvailable ? "\"user.jsp?taskspage=" + (tasksPage + 1) + "\"" : "\"\"";
                    out.print("<a href=" + href + " class=\"sl-next line\">&gt;</a>");%>
                </nav>
            </div>
        </div>

        <!-- Registered Tasks and Services -->
        <div class="portfolio container">
            <div class="portfolio-title">
                <h3>Registered Tasks and Services</h3>
            </div>
            <div class="row">
                <% if (participateTasksList != null) {
                    for (Task task : participateTasksList) {
                    	String isTask = "Service";
                		if(task._isTask == 1) isTask = "Task";
                		out.print("<div class=\"work span3\">" +
                					"<img src=\"assets/img/taskcard.png\" alt=\"\"/>");
	                    		   	out.print("<h4>" + task._taskTitle + "</h4>" +
			                    	"<p>" + isTask + "       " + task._numParticipants + "/" + task._taskCapacity + "       Cost: " + task._taskCostPerUser + "</p>" +
	                    			"<div class=\"icon-awesome\">" +
		                        		"<a href=\"taskpage.jsp?taskId=" + task._taskId + "\"><i class=\"icon-search\"></i></a>" +
		                        		"<a><i id=\"" + task._taskId + "\" class=\"icon-minus partTask\"></i></a>");
	                    			out.print("</div>" +
                				"</div>");
            		}
                }
                %>
            </div>
            <div class="scroll">
                <nav id="nav-arrows" class="nav-arrows hidden-xs hidden-sm visible-md visible-lg">
                    <% out.print("<a href=\"user.jsp?partpage=" + (participateTasksPage - 1) + "\" class=\"sl-prev line\">&lt;</a>"); %>
                    <p class="line"><%=participateTasksPage%>/<%=numParticipateTasksPerPage%></p>
                    <% isNextAvailable = (participateTasksPage != numParticipateTasksPerPage);
                     href = isNextAvailable ? "\"user.jsp?partpage=" + (participateTasksPage + 1) + "\"" : "\"\"";
                    out.print("<a href=" + href + " class=\"sl-next line\">&gt;</a>");%>
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
        <script type="text/javascript" src="assets/js/jquery-1.8.2.min.js"></script>
        <script type="text/javascript" src="assets/bootstrap/js/bootstrap.min.js"></script>
        <script type="text/javascript" src="assets/js/jquery.flexslider.js"></script>
        <script type="text/javascript" src="assets/js/jquery.tweet.js"></script>
        <script type="text/javascript" src="assets/js/jflickrfeed.js"></script>
        <script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=true"></script>
        <script type="text/javascript" src="assets/js/jquery.ui.map.min.js"></script>
        <script type="text/javascript" src="assets/js/jquery.quicksand.js"></script>
        <script type="text/javascript" src="assets/prettyPhoto/js/jquery.prettyPhoto.js"></script>
        <script type="text/javascript" src="assets/js/scripts.js"></script>
        <script type="text/javascript" src="assets/js/user.js"></script>
</div>
    </body>

</html>

