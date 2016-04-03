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
 * Servlet implementation class ChangePassword
 */
@WebServlet("/ChangePassword")
public class ChangePassword extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ChangePassword() {
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
			json.put("error", "cannot delete user profile if not logged in");
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
		String update = request.getParameter("pass");
		String old = request.getParameter("old");
		if (update == null || update.isEmpty() || old == null) {
		    try {
			json.put("result", "failed");
			json.put("error", "cannot have empty password");
			response.setStatus(400);
			pw.println(json.toString());
			return;
		    }
		    catch (JSONException e) {
			e.printStackTrace();
			return;
		    }
		}
		Connection conn = null;
		try {
		    conn = DataBase.connectionPool.getConnection();
		    conn.setAutoCommit(false);
		    conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		    if (DataBase.verifyPassword(conn, userId, old)) {
			DataBase.updateUsersPassword(conn, userId, update);
		    }
		    else {
			json = ServletUtils.errorJsonFormat("failed authentication");
			response.setStatus(401);
			pw.println(json.toString());
			conn.rollback();
			DataBase.closeConnection(conn);
			return;
		    }
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
			json = ServletUtils.errorJsonFormat("Failed to update user password");
			json.put("exception", e);
		    }
		    catch (JSONException e1) {
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
