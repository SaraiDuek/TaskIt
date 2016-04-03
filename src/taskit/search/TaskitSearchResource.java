package taskit.search;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import org.json.*;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import taskit.dataManager.DataBase;
import taskit.dataObjects.User;
import taskit.servlets.ServletUtils;

public class TaskitSearchResource extends ServerResource{
	
	@SuppressWarnings("unchecked")
	@Get
	public Representation represent() {
	    //Initialize - get parameters
	    int maxDist = -1;
	    //String user = (String) this.getRequestAttributes().get("u");//getAttribute("u");//
	    String user = getAttribute("userName");
	    Map<String, String> resultUsers = new HashMap<String,String>();
	    JSONObject result = new JSONObject();
	    try {
	    	maxDist = Integer.parseInt(getQueryValue("maxDist"));
	    }
	    catch (Exception e) {
		setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Invalid parametes. maxDist should be an integer greater than or equal to than 1");
		try {
		    result = ServletUtils.errorJsonFormat("Invalid parametes. maxDist should be an integer greater than or equal to than 1");
		}
		catch (JSONException e1) {
		    e1.printStackTrace();
		}
		return new StringRepresentation(result.toString(), MediaType.APPLICATION_JSON);
		 
	    }
	    if (maxDist < 1 || user == null || user.isEmpty()) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Invalid parametes. use: .../search/u/<username>?maxDist=");
			try {
			    result = ServletUtils.errorJsonFormat("Invalid parametes. use: .../search/u/<username>?maxDist=<maxDistInteger>");
			    result.put("maxDist", maxDist);
			    
			    result.put("user", user);
			}
			catch (JSONException e1) {
			    e1.printStackTrace();
			}
			return new StringRepresentation(result.toString(), MediaType.APPLICATION_JSON);
	    }
	    resultUsers.put(user, user);
	    Map<Integer, Set<User>> users = new HashMap<Integer, Set<User>>();
	    Map<Integer, Set<String>> usersIds = new HashMap<Integer, Set<String>>();
	    //Access DB
	    Connection conn = null;
	    try {
			conn = DataBase.connectionPool.getConnection();
			conn.setAutoCommit(false);
			Set<User> rootUserList = new HashSet<User>();
			User root = DataBase.getUser(conn, user);
			if (root == null) {
			    setStatus(Status.CLIENT_ERROR_NOT_FOUND, "User does not be exist");
			    try {
				    result = ServletUtils.errorJsonFormat("User does not exist");
				    result.put("user", user);
				}
				catch (JSONException e1) {
				    e1.printStackTrace();
				}
				return new StringRepresentation(result.toString(), MediaType.APPLICATION_JSON);
			}
			rootUserList.add(root);
			users.put(0, rootUserList);
			//getAllUserFriends
			for (int i = 1; i <= maxDist; i++) {
			    Set<String> circleUserIdSet = new HashSet<String>();
			    Set<User> circleUserSet = new HashSet<User>();
				    for (User u : users.get(i-1)) {
						List<String> uFriends = DataBase.getAllUserFriendsIds(conn, u._userId);
						for (String uf : uFriends) {
						    if (!user.equals(uf) && resultUsers.get(uf) == null) {
						    	circleUserIdSet.add(uf);
						    	resultUsers.put(uf, uf);
						    }
						}
			    }
			    for (String uid : circleUserIdSet) {
				User usr = DataBase.getUser(conn, uid);
				circleUserSet.add(usr);
			    }
			    users.put(i, circleUserSet);
			    usersIds.put(i, circleUserIdSet);
			}
			conn.commit();
	    }
	    catch (SQLException se) {
			setStatus(Status.SERVER_ERROR_INTERNAL, "failed to retreive information");
			try {
			    if (conn != null){
				conn.rollback();
			    }
			} catch (SQLException e1) {
			    e1.printStackTrace();
			}
	    }
	    catch (Exception e) {
	    	e.printStackTrace();
	    }
	    finally {
	    	DataBase.closeConnection(conn);
	    }
	    
	    try {
		    for (int i = 1; i < users.size(); i++) {
				Set<User> currSet = users.get(i);
				JSONArray currArray = new JSONArray();
				for (User usr : currSet) {
				    currArray.put(usr.toJSON());
				}
			    result.put(String.format("dist_%d", i), currArray);
			}
	    }
	    catch (JSONException e) {
	    	e.printStackTrace();
	    }
		
	    //return
	    return new StringRepresentation(result.toString(), MediaType.APPLICATION_JSON);
	}

}
