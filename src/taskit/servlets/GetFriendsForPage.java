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
import taskit.dataObjects.User;

/**
 * Servlet implementation class GetFriendsForPage
 */
@WebServlet("/GetFriendsForPage")
public class GetFriendsForPage extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetFriendsForPage() {
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
		    json = ServletUtils.errorJsonFormat("cannot get friends if not logged in");    
		}
		catch (JSONException e) {
		    e.printStackTrace();
		}
		pw.println(json.toString());
		return;
	    }
	    String userId = (String) request.getSession(false).getAttribute("user");
	    int pageNumber = Integer.parseInt(request.getParameter("page"));
	    Connection conn = null;
	    List<User> friends = null;
	    try {
		conn = DataBase.connectionPool.getConnection();
		conn.setAutoCommit(false);
		conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
		friends = DataBase.getUserFriendsPerPage(conn, userId, pageNumber);
		conn.commit();
	    }
	    catch (Exception e) {
		if (conn != null) {
		    try {
			conn.rollback();
			json = ServletUtils.errorJsonFormat("could not fetch friends");
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
		JSONArray friendsJson = new JSONArray();
		for (User friend : friends) {
		    friendsJson.put(friend.toJSON());
		}
		json.put("friends", friendsJson);
		pw.println(json.toString());
	    }
	    catch (JSONException e) {
		e.printStackTrace();
	    }

	}

}
