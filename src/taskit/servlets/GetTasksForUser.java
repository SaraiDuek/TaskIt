package taskit.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import taskit.dataManager.DataBase;
import taskit.dataObjects.Task;

/**
 * Servlet implementation class GetTasksForUser
 */
@WebServlet("/GetTasksForUser")
public class GetTasksForUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetTasksForUser() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    response.setContentType("application/json");
	    PrintWriter pw = response.getWriter();
	    JSONObject json = new JSONObject();
	    if (!ServletUtils.isLoggedIn(request)) {
		try {
		    json = ServletUtils.errorJsonFormat("cannot get user's tasks if not logged in");    
		}
		catch (JSONException e) {
		    e.printStackTrace();
		}
		pw.println(json.toString());
		return;
	    }
	    String userId = request.getParameter("ownerId");
	    int pageNumber = Integer.parseInt(request.getParameter("page"));
	    Connection conn = null;
	    List<Task> tasks = null;
	    try {
		conn = DataBase.connectionPool.getConnection();
		conn.setAutoCommit(false);
		conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
		//tasks = DataBase.getUserTasksPerPage(conn, userId, pageNumber); TODO
		conn.commit();
	    }
	    catch (Exception e) {
		if (conn != null) {
		    try {
			conn.rollback();
			json = ServletUtils.errorJsonFormat("could not fetch tasks for user");
			pw.println(json.toString());
		    }
		    catch (SQLException e1) {
			e1.printStackTrace();
		    }
		    catch (JSONException e1) {
			e1.printStackTrace();
		    }
		}
		response.setStatus(500);
	    }
	    finally {
		DataBase.closeConnection(conn);
		if (response.getStatus() == 500) {
		    return;
		}
	    }
	    try {
		json = ServletUtils.successJsonFormat();
		JSONArray tasksJson = new JSONArray();
		for (Task task : tasks) {
		    tasksJson.put(task.toJSON());
		}
		json.put("tasks", tasksJson);
		pw.println(json.toString());
	    }
	    catch (JSONException e) {
		e.printStackTrace();
	    }
	}

}
