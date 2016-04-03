package taskit.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import taskit.dataManager.DataBase;
import taskit.dataObjects.Task;

/**
 * Servlet implementation class GetTask
 */
@WebServlet(name = "Task", urlPatterns = { "/Task" })
public class TaskController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public TaskController() {
	super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request,
	    HttpServletResponse response) throws ServletException, IOException {
	response.setContentType("application/json");
	PrintWriter pw = response.getWriter();
	JSONObject json = new JSONObject();
	if (!ServletUtils.isLoggedIn(request)) {
	    try {
		json.put("result", "failed");
		json.put("error", "cannot get task if not logged in");
		pw.println(json.toString());
		return;
	    }
	    catch (JSONException e) {
		e.printStackTrace();
		return;
	    }
	}
	String taskId = request.getParameter("taskId");
	Connection conn = null;
	Task task = null;
	try {
	    conn = DataBase.connectionPool.getConnection();
	    conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
	    task = DataBase.getTask(conn, taskId);
	    json.put("task", task.toJSON());
	    json.put("result", "success");
	    pw.print(json.toString());
	}
	catch (Exception e) {
	    try {
		json.put("result", "failed");
		json.put("error", e.getMessage());
	    }
	    catch (JSONException jex) {
		// do nothing. suppress exception
	    }
	    pw.print(json.toString());
	}
	finally {
	    DataBase.closeConnection(conn);
	}

    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request,
	    HttpServletResponse response) throws ServletException, IOException {
	response.setContentType("application/json");
	PrintWriter pw = response.getWriter();
	JSONObject json = new JSONObject();
	if (!ServletUtils.isLoggedIn(request)) {
	    try {
		json = ServletUtils
			.errorJsonFormat("cannot add task if not logged in");
		pw.println(json.toString());
		response.setStatus(401);
		return;
	    }
	    catch (JSONException e) {
		pw.println(e.getMessage());
		response.setStatus(401);
		return;
	    }
	}
	Task task = null;
	String exceptionMsg = null;
	Connection conn = null;
	try {
	    conn = DataBase.connectionPool.getConnection();
	    conn.setAutoCommit(false);
	    conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
	    task = createTaskFromRequestParams(request);
	    if (task == null) {
		throw new Exception("Failed to create Task object");
	    }
	    DataBase.addTask(conn, task);
	    conn.commit();
	    json = ServletUtils.successJsonFormat();
	    pw.println(json.toString());
	}
	catch (SQLException se) {
	    try {
		json = ServletUtils.errorJsonFormat("failed to add task");
		json.put("SQL", se.getMessage());
		response.setStatus(500);
		pw.println(json.toString());
	    }
	    catch (JSONException e) {
		e.printStackTrace();
	    }
	}
	catch (Exception ex) {
	    exceptionMsg = ex.getMessage();
	    response.setStatus(500);
	    if (conn != null) {
		try {
		    conn.rollback();
		    DataBase.closeConnection(conn);
		    conn = null;
		}
		catch (SQLException e) {
		    e.printStackTrace();
		}
	    }
	}
	finally {
	    DataBase.closeConnection(conn);
	    if (task == null) {
		try {
		    json = ServletUtils.errorJsonFormat("failed to add task");
		    if (exceptionMsg != null) {
			json.put("exception", exceptionMsg);
		    }
		    pw.println(json.toString());
		    return;
		}
		catch (JSONException e) {
		    e.printStackTrace();
		    return;
		}
	    }
	}
    }

    /**
     * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
     */
    protected void doDelete(HttpServletRequest request,
	    HttpServletResponse response) throws ServletException, IOException {
	response.setContentType("application/json");
	PrintWriter pw = response.getWriter();
	JSONObject json = new JSONObject();
	if (!ServletUtils.isLoggedIn(request)) {
	    try {
		json = ServletUtils
			.errorJsonFormat("cannot delete task if not logged in");
		pw.println(json.toString());
		return;
	    }
	    catch (JSONException e) {
		e.printStackTrace();
		return;
	    }
	}
	Connection conn = null;
	String taskId = request.getParameter("taskId");
	try {
	    conn = DataBase.connectionPool.getConnection();
	    conn.setAutoCommit(false);
	    conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
	    DataBase.deleteTask(conn, taskId);
	    json = ServletUtils.successJsonFormat();
	    pw.println(json.toString());
	}
	catch (Exception e) {
	    if (conn != null) {
		try {
		    conn.rollback();
		}
		catch (SQLException e1) {
		    e1.printStackTrace();
		}
	    }
	}
	finally {
	    if (conn != null) {
		try {
		    conn.commit();
		}
		catch (SQLException e) {
		    e.printStackTrace();
		}
	    }
	    DataBase.closeConnection(conn);
	    
	}
    }
    
    /**
     * @see HttpServlet#doPut(HttpServletRequest, HttpServletResponse)
     */
    protected void doPut(HttpServletRequest request,
	    HttpServletResponse response) throws ServletException, IOException {
	response.setContentType("application/json");
	PrintWriter pw = response.getWriter();
	JSONObject json = new JSONObject();
	if (!ServletUtils.isLoggedIn(request)) {
	    try {
		json = ServletUtils
			.errorJsonFormat("cannot modify task if not logged in");
		pw.println(json.toString());
		response.setStatus(401);
		return;
	    }
	    catch (JSONException e) {
		e.printStackTrace();
		return;
	    }
	}
	Task task = null;
	String exceptionMsg = null;
	Connection conn = null;
	try {
	    conn = DataBase.connectionPool.getConnection();
	    conn.setAutoCommit(false);
	    conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
	    String taskId = request.getParameter("taskId");
	    task = DataBase.getTask(conn, taskId);
	    if (task == null) {
	    	throw new Exception("Failed to create Task object");
	    }
	    String title = request.getParameter("title");
	    if (title == null || title.isEmpty()) {
		throw new Exception("task must have a title");
	    }
	    task._taskTitle = title;
	    task._taskDescription = request.getParameter("description");
	    task._taskCapacity = Integer.parseInt(request.getParameter("capacity"));
	    task._taskCostPerUser = Integer.parseInt(request.getParameter("cost"));
	    DataBase.updateTask(conn, task);
	}
	catch (SQLException se) {
	    try {
		json = ServletUtils.errorJsonFormat("failed to update task");
		json.put("SQL", se.getMessage());
	    }
	    catch (JSONException e) {
		e.printStackTrace();
	    }
	}
	catch (Exception ex) {
	    exceptionMsg = ex.getMessage();
	    response.setStatus(500);
	    if (conn != null) {
		try {
		    conn.rollback();
		    DataBase.closeConnection(conn);
		    conn = null;
		}
		catch (SQLException e) {
		    e.printStackTrace();
		}
	    }
	}
	finally {
	    if (conn != null) {
		try {
		    conn.commit();
		}
		catch (SQLException e) {
		    e.printStackTrace();
		}
	    }
	    DataBase.closeConnection(conn);
	    if (task == null) {
		try {
		    json = ServletUtils.errorJsonFormat("failed to update task");
		    if (exceptionMsg != null) {
			json.put("exception", exceptionMsg);
		    }
		    pw.println(json.toString());
		    return;
		}
		catch (JSONException e) {
		    e.printStackTrace();
		    return;
		}
	    }
	}
    }

    private Task createTaskFromRequestParams(HttpServletRequest request) {
	Task result = null;
	String taskId = request.getParameter("taskId");
	if (taskId.length() > 100) {
	    taskId = taskId.substring(0, 99);
	}
	int isTask = Integer.parseInt(request.getParameter("isTask"));
	String owner = (String) request.getSession(false).getAttribute("user");
	String title = request.getParameter("title");
	String description = request.getParameter("description");
	int capacity = Integer.parseInt(request.getParameter("capacity"));
	int cost = Integer.parseInt(request.getParameter("cost"));
	int distance = Integer.parseInt(request.getParameter("distance"));
	result = new Task(taskId, isTask, owner, title, description, capacity,
		cost, 0, distance, "", true);
	return result;
    }

}
