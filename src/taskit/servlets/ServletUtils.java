package taskit.servlets;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class ServletUtils {

    public static boolean isLoggedIn(HttpServletRequest request) {
	boolean result = true;
	HttpSession session = request.getSession(false);
	if (session == null) {
	    result = false;
	}
	return result;
    }

    public static JSONObject errorJsonFormat(String errMsg) throws JSONException {
	JSONObject result = new JSONObject();
	result.put("result", "failed");
	result.put("error", errMsg);
	return result;
    }
    
    public static JSONObject successJsonFormat() throws JSONException {
	JSONObject result = new JSONObject();
	result.put("result", "success");
	return result;
    }
}
