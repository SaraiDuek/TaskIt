package taskit.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import taskit.dataManager.DataBase;

/**
 * Servlet implementation class LeaveAllTasks
 */
@WebServlet("/LeaveAllTasks")
public class LeaveAllTasks extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LeaveAllTasks() {
        super();
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    response.setContentType("application/json");
	    PrintWriter pw = response.getWriter();
	    JSONObject json = new JSONObject();
	    if (!ServletUtils.isLoggedIn(request)) {
		try {
		    json.put("result", "failed");
		    json.put("error", "cannot leave tasks if not logged in");
		    response.setStatus(401);
		    pw.println(json.toString());
		    return;
		}
		catch (JSONException e) {
		    e.printStackTrace();
		    return;
		}
	    }
	    String userId = (String) request.getSession(false).getAttribute("user");
	    Connection conn = null;
	    try {
		conn = DataBase.connectionPool.getConnection();
		conn.setAutoCommit(false);
		conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		DataBase.removeUserFromAllTasks(conn, userId);
		conn.commit();
		json = ServletUtils.successJsonFormat();
	    }
	    catch (Exception e) {
		try {
		    if (conn != null) {
			conn.rollback();
		    }
		}
		catch (SQLException se) {
		    se.printStackTrace();
		}
		try {
		    json = ServletUtils.errorJsonFormat("Failed to leave tasks");
		    json.put("exception", e);
		}
		catch (JSONException e1) {
		    // TODO Auto-generated catch block
		    e1.printStackTrace();
		}
		response.setStatus(500);
	    }
	    finally {
		DataBase.closeConnection(conn);
		pw.println(json.toString());
	    }
	}
	
}
