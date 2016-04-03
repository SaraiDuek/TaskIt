package taskit.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;

import taskit.dataManager.DataBase;

/**
 * Servlet implementation class AddFriend
 */
@WebServlet("/AddFriend")
public class AddFriend extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddFriend() {
        super();
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
		throws ServletException, IOException {
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
		return;
	    }
	    String friendId = request.getParameter("friendId");
	    if (friendId == null || friendId.isEmpty()) {
		try {
		    json = ServletUtils.errorJsonFormat("friendId parameter is empty or doesn't exist");
		}
		catch (JSONException e) {
		    e.printStackTrace();
		}
		response.setStatus(400);
		pw.println(json.toString());
		return;
	    }
	    HttpSession session = request.getSession(false);
	    String userId = (String) session.getAttribute("user");
	    Connection conn = null;
	    try {
		conn = DataBase.connectionPool.getConnection();
		conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		conn.setAutoCommit(false);
		DataBase.addFriend(conn, userId, friendId);
		conn.commit();
		json = ServletUtils.successJsonFormat();
		pw.println(json.toString());
	    }
	    catch (JSONException je) {
		je.printStackTrace();
		DataBase.closeConnection(conn);
		return;
	    }
	    catch (Exception ex) {
		if (conn != null) {
		    try {
			json = ServletUtils.errorJsonFormat("failed to add friend");
			json.put("exception", ex.getMessage());
			pw.println(json.toString());
			response.setStatus(500);
			conn.rollback();
		    }
		    catch (Exception e) {
			e.printStackTrace();
		    }
		}
	    }
	    finally {
		DataBase.closeConnection(conn);
	    }
	    
	}

}
