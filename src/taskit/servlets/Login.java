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

/**
 * Servlet implementation class Login
 */
@WebServlet(description = "Session login", urlPatterns = { "/Login" })
public class Login extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final int timeout = 1800;
    private static final String admin = "admin";
    private static final String pass = "admin";

    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
	super();
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request,
	    HttpServletResponse response) throws ServletException, IOException {
	response.setContentType("application/json");
	PrintWriter pw = response.getWriter();
	String username = request.getParameter("username");
	String password = request.getParameter("password"); // no need to hash
							    // for Administrator
	JSONObject json = new JSONObject();
	if (admin.equals(username)) {
	    if (pass.equals(password)) {
			HttpSession session = request.getSession(true);
			session.setMaxInactiveInterval(timeout);
			session.setAttribute("user", username);
			session.setAttribute("privilege", "admin");
			try {
			    json = ServletUtils.successJsonFormat();
			    json.put("privilege", "admin");
			}
			catch (JSONException e) {
			    e.printStackTrace();
			}
				pw.print(json.toString());
				return; // set admin rights
		    }
	    else { // login failure (wrong user/password)
			try {
			    json = ServletUtils.errorJsonFormat("Wrong username or password, try again!");
			}
			catch (JSONException e) {
			    e.printStackTrace();
			}
			pw.print(json.toString());
			return;
		}
	}
	Connection conn = null;
//	password = DigestUtils.md5Hex(password);
	boolean result;
	try {
	    conn = DataBase.connectionPool.getConnection();
	    conn.setAutoCommit(false);
	    conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
	    result = DataBase.verifyPassword(conn, username, password); 
	    conn.commit();
	    if (!result) {
				json = ServletUtils.errorJsonFormat("Wrong username or password, try again.");
				pw.print(json.toString()); 
		} else {
			HttpSession session = request.getSession(true);
			session.setMaxInactiveInterval(timeout);
			session.setAttribute("user", username);
			session.setAttribute("privilege", "user");
			json = ServletUtils.successJsonFormat();
			json.put("privilege", "user");
			pw.print(json.toString());
	    }
	}
	catch (Exception e) {
	    try {
			if (conn != null)
			    conn.rollback();
		    }
		    catch (SQLException sqlEx) {
		    	sqlEx.printStackTrace();
		    }
		    try {
		    	json = ServletUtils.errorJsonFormat(e.getMessage());
		    }
		    catch (JSONException e1) {
		    	e1.printStackTrace();
		    }
		    pw.print(json.toString());
	}
	finally {
	    if (conn != null) {
			try {
			    conn.close();
			}
			catch (SQLException e) {
			    // suppress
			}
	    }
	}
    }

}
