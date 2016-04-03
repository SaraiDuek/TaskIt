package taskit.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;

import taskit.dataManager.DataBase;
import taskit.dataObjects.User;

/**
 * Servlet implementation class UpdateUser
 */
@WebServlet("/UpdateUser")
public class UpdateUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateUser() {
        super();
        // TODO Auto-generated constructor stub
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
		    json = ServletUtils.errorJsonFormat("cannot update user if not logged in");    
		}
		catch (JSONException e) {
		    e.printStackTrace();
		}
		pw.println(json.toString());
		return;
	    }
	    //PreparedStatement statement = conn.prepareStatement("UPDATE users SET"
		//+ "firstName=?, lastName=?, address=?, phone=?, gender=? WHERE userId=?;");
	    String userId = (String) request.getSession(false).getAttribute("user");
	    String firstName = request.getParameter("firstName");
	    String lastName = request.getParameter("lastName");
	    String address = request.getParameter("address");
	    String phone = request.getParameter("phone");
	    String gender = request.getParameter("gender");
	    
	    User usr;
	    Connection conn = null;
	    try {
		conn = DataBase.connectionPool.getConnection();
		conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		conn.setAutoCommit(false);
		usr = DataBase.getUser(conn, userId);
		usr._userFirstName = firstName;
		usr._userLastName = lastName;
		usr._userAddress = address;
		usr._userPhone = phone;
		usr._userGender = gender;
		DataBase.updateUser(conn, usr);
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
			json = ServletUtils.errorJsonFormat("failed to update");
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
