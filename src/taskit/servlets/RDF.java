package taskit.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
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
import taskit.features.RDFModel;

/**
 * Servlet implementation class RDF
 */
@WebServlet("/RDF")
public class RDF extends HttpServlet{
	private static final long serialVersionUID = 1L;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RDF() {
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
	    String query = request.getParameter("query");
	    
	    if (query == null) {
	    	query = "";
	    }
		
	    
	   // Connection conn = null; //redundant because "getRDF" opens its own connection
	    try {
//		conn = DataBase.connectionPool.getConnection();
//		conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE); //TODO
//		conn.setAutoCommit(false); 
		StringWriter sw = new StringWriter();
		RDFModel.getRDF(sw, query);
//		conn.commit();
		json = ServletUtils.successJsonFormat();
		json.put("rdfResult", sw.toString());
		pw.println(json.toString());
		sw.close();
	    }
	    catch (JSONException je) {
		je.printStackTrace();
		//DataBase.closeConnection(conn);
		return;
	    }
	    catch (Exception ex) {
//		if (conn != null) {
		    try {
			json = ServletUtils.errorJsonFormat("failed to get SPARQL rdf results");
			json.put("exception", ex.getMessage());
			pw.println(json.toString());
			response.setStatus(500);
		//	conn.rollback();
		    }
		    catch (Exception e) {
			e.printStackTrace();
		    }
//		}
	    }
	    finally {
//		DataBase.closeConnection(conn);
	    }
}
}
