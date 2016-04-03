package taskit.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Servlet implementation class Logout
 */
@WebServlet(description = "Session Logout", urlPatterns={"/Logout"})
public class Logout extends HttpServlet {
	private static final long serialVersionUID = 1L;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Logout() {
        super();
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		PrintWriter pw = response.getWriter();
		HttpSession session = request.getSession(false);
		if(session != null){
			session.removeAttribute("user");
			session.invalidate();
		}
		JSONObject json = new JSONObject();
		try {
			json = ServletUtils.successJsonFormat();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		pw.print(json.toString());
	}

}
