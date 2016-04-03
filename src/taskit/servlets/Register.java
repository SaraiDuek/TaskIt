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
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONException;
import org.json.JSONObject;

import taskit.dataManager.DataBase;
import taskit.dataObjects.User;

/**
 * Servlet implementation class Register
 */
@WebServlet("/Register")
public class Register extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private static final int timeout = 1800;     
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Register() {
        super();
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    response.setContentType("application/json");
	    PrintWriter pw = response.getWriter();
	    JSONObject json = new JSONObject();
	    String userId = request.getParameter("username");
//	    String firstName = request.getParameter("firstName");
//	    String lastName = request.getParameter("lastName");
	    String password = request.getParameter("password");
	    User user = null;	    
	    try {
	    	verifyParams(request, userId, password);
//	    	password = DigestUtils.md5Hex(password);
	    }
	    catch (Exception ex) {
			try {
			    json = ServletUtils.errorJsonFormat(ex.getMessage());
			}
			catch (JSONException e) {
			    e.printStackTrace();
			}
			pw.println(json.toString());
			response.setStatus(400);
			return;
	    }
	    user = new User(userId, password, null, null, null, null, null, null, 0, true);
	    Connection conn = null;
	    try {
			conn = DataBase.connectionPool.getConnection();
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			conn.setAutoCommit(false);
			DataBase.addUser(conn, user);
			conn.commit();
			
			//open session for user
			HttpSession session = request.getSession(true);
			session.setMaxInactiveInterval(timeout);
			session.setAttribute("user", userId);
			session.setAttribute("privilege", "user");
			try {
			    json = ServletUtils.successJsonFormat();
			    json.put("privilege", "user");
			}
			catch (JSONException e) {
			    e.printStackTrace();
			}
			pw.print(json.toString());
	    }
	    catch (Exception e) {
			if (conn != null) {
			    try {
			    	conn.rollback();
			    }
			    catch (SQLException e1) {
			    	e1.printStackTrace();
			    }
			    DataBase.closeConnection(conn);
			    conn = null;
			    try {
			    	json = ServletUtils.errorJsonFormat(e.getMessage());
			    }
			    catch (JSONException je) {
			    	je.printStackTrace();
			    }
			    response.setStatus(500);
			    pw.println(json.toString());
			}
	    }
	    finally {
		DataBase.closeConnection(conn);
		try {
		    json = ServletUtils.successJsonFormat();
		}
		catch (JSONException e) {
		    e.printStackTrace();
		}
		pw.println(json.toString());
	    }
	}

	private void verifyParams(HttpServletRequest request, String userId, String password) throws Exception {
	    if (!isValidParam(userId)) {
		throw new Exception("Invalid username");
	    }
	    if (password == null || password.isEmpty()) {
		throw new Exception("Empty password");
	    }
	    //password = DigestUtils.md5Hex(password);
	}
	
	private boolean isValidParam(String param) {
	    return (param != null && param.length() <= 25 && !param.isEmpty());
	}

}
