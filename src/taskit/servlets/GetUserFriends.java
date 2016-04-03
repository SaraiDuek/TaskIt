package taskit.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
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
import taskit.dataObjects.User;

/**
 * Servlet implementation class GetUserFriends
 */
@WebServlet("/GetUserFriends")
public class GetUserFriends extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetUserFriends() {
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
			json = ServletUtils.errorJsonFormat("cannot get user's friends if not logged in");
		    }
		    catch (JSONException e) {
			e.printStackTrace();
		    }
		    pw.println(json.toString());
		}
		String userId = request.getParameter("userId");
		Connection conn = null;
		List<User> friends = null;
		try {
			conn = DataBase.connectionPool.getConnection();
			conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
			friends = DataBase.getAllUserFriends(conn, userId);
			JSONArray friendsJson = createFriendsJson(friends);
			json.put("userId", userId);
			json.put("friends", friendsJson);
			json.put("result", "success");
			pw.print(json.toString());
		}
		catch (Exception e) {
			try {
				json.put("result", "failed");
				json.put("error", e.getMessage());
			} catch (JSONException jex) {
				// do nothing. suppress exception 
			}
			pw.print(json.toString());
		}
		//ToDo - Add finally clause to disconnect

	}

	private JSONArray createFriendsJson(List<User> friends) {
		JSONArray friendsJson = new JSONArray(); 
		for (User friend : friends) {
		    try {
			friendsJson.put(friend.toJSON());
		    }
		    catch (JSONException je) {
			je.printStackTrace();
		    }
		}
		return friendsJson;
	}

}
