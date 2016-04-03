package taskit.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import taskit.dataManager.DataBase;
import taskit.dataObjects.User;

/**
 * Servlet implementation class GetUser
 */
@WebServlet("/GetUser")
public class GetUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetUser() {
        super();
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
				json = ServletUtils.errorJsonFormat("cannot get user if not logged in");
				response.setStatus(401);
				pw.println(json.toString());
				return;
			} catch (JSONException e) {
				e.printStackTrace();
				return;
			}
		}
		
		String userId = request.getParameter("userId");
		Connection conn = null;
		User user = null;
		try {
			conn = DataBase.connectionPool.getConnection();
			conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
			user = DataBase.getUser(conn, userId);
			if (user == null) {
			       throw new Exception("Invalid user");
			   }
			json.put("user", user.toJSON());
			json.put("result", "success");
			pw.print(json.toString());
		}
		catch (Exception e) {
			try {
				json.put("result", "failed");
				json.put("error", e.getMessage());
				response.setStatus(500);
			} catch (JSONException jex) {
				// do nothing. suppress exception 
			}
			pw.print(json.toString());
		}
		finally {
		    DataBase.closeConnection(conn);
		}
	}

}
