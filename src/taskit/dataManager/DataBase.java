package taskit.dataManager;

import java.sql.Date;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import org.apache.commons.dbcp2.BasicDataSource;

import taskit.dataObjects.FriendsTupple;
import taskit.dataObjects.Participant;
import taskit.dataObjects.Task;
import taskit.dataObjects.User;


/**
 * Created by b2faviad on 3/14/15.
 */
public class DataBase {

    /*** PROPERTIES ***/
    static private String dbURL = "jdbc:mysql://127.0.0.1:3306/";
    static private String dbName = "hw5";
    static private String dbUserName = "sarai";//"sarai";
    static private String dbPassword = "1234";
    
	static private double resPerPage = 10.0;
    
	static private Connection conn = null;
    /*** PRIVATE METHODS ***/

    public static BasicDataSource connectionPool; 
    static{
    	connectionPool = new BasicDataSource();
		try{
			Class.forName("com.mysql.jdbc.Driver");
		} catch(Exception e ){
			e.printStackTrace();
		}
		connectionPool.setUrl(dbURL + dbName);
		connectionPool.setUsername(dbUserName);
		connectionPool.setPassword(dbPassword);
		connectionPool.setDefaultAutoCommit(false);
		connectionPool.setMaxTotal(100);
		connectionPool.setInitialSize(10);
		connectionPool.setMinIdle(10);
	}
    
    /** connecting to DB **/
    public boolean connect() {
		if (conn != null)
			return true;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(dbURL + dbName, dbUserName, dbPassword);
			return true;
		} catch (Exception e1) {
			System.err.println("Error connecting to DB: " + e1.getMessage());
			e1.printStackTrace();
			try {
				if (conn != null)
					conn.close();
			} catch (Exception e2) {
				System.err
						.println("Error connecting to DB: " + e2.getMessage());
			}
		}
		return false;
	}

    /** close DB connection **/
	public static void closeConnection(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
				conn = null;
			} catch (Exception e1) {
				System.err.println("Error closing connection to DB: "
						+ e1.getMessage());
			}
		}
	}

	/** false for not auto committing transactions **/
	public void setAutoCommit(Connection conn, boolean flag) throws SQLException{
		conn.setAutoCommit(flag);
	}

	/** Isolation levels:
	 	1 = READ UNCOMMITTED
		2 = READ COMMITTED
		3 = REPEATABLE READ (default)
		4 = SERIALIZABLE **/
	public void setIsolation(Connection conn, int level) throws SQLException{
		conn.setTransactionIsolation(level);
	}
	
	/** commit transaction to DB **/
	public void commit(Connection conn) throws SQLException{
		conn.commit();
	}
	
	/** rollback transaction from DB, Undoing any change to data within the current transaction **/
	public void rollback(Connection conn) throws SQLException{
		conn.rollback();
	}
	
	/** delete DB **/
	public void wipeOutDB(Connection conn) throws SQLException{
		PreparedStatement statement1 = conn.prepareStatement("DELETE FROM users;");
		statement1.executeUpdate();
		PreparedStatement statement2 = conn.prepareStatement("DELETE FROM tasks; ");
		statement2.executeUpdate();
		PreparedStatement statement3 = conn.prepareStatement("DELETE FROM friends;");
		statement3.executeUpdate();
		PreparedStatement statement4 = conn.prepareStatement("DELETE FROM participations;");
		statement4.executeUpdate();
	}

    /*** PUBLIC METHODS ***/


    /*** User Functions ***/
	/*******************************************************************************************************/
  
    public static void addUser(Connection conn, User user) throws SQLException, Exception {
		PreparedStatement statement = conn.prepareStatement("INSERT INTO users VALUES(?,?,?,?,?,?,?,?,0,?);");

		// required fields
		if(user._userId == null)
			throw new Exception("user must have a valid ID!");
		statement.setString(1, user._userId.substring(0, Math.min(user._userId.length(), 25)));

		if(user._userPassword == null)
			throw new Exception("user must have a valid password!");
		statement.setString(2, user._userPassword.substring(0,Math.min(1024,user._userPassword.length())));

		//optional fields
		if(user._userFirstName == null)
			statement.setString(3, user._userFirstName);
		else
			statement.setString(3, user._userFirstName.substring(0,Math.min(user._userFirstName.length(),64)));

		if(user._userLastName == null)
			statement.setString(4, user._userLastName);
		else
			statement.setString(4, user._userLastName.substring(0,Math.min(user._userLastName.length(),64)));

		if(user._userAddress == null)
			statement.setString(5, user._userAddress);
		else
			statement.setString(5, user._userAddress.substring(0,Math.min(user._userAddress.length(), 512)));
		
		if(user._userPhone == null)
			statement.setString(6, user._userPhone);
		else
			statement.setString(6, user._userPhone.substring(0,Math.min(user._userPhone.length(), 15)));
		
		if(user._userPhoto == null)
			statement.setString(7, user._userPhoto);
		else
			statement.setString(7, user._userPhoto.substring(0,Math.min(user._userPhoto.length(), 512)));

		statement.setString(8, user._userGender);

		statement.setBoolean(9, user._isActive); 
		
		statement.executeUpdate();
	}
    
    	public static void updateUser(Connection conn, User user) throws SQLException, Exception {
    	PreparedStatement statement = conn.prepareStatement("UPDATE users SET"
    		+ " firstName=?, lastName=?, address=?, phone=?, gender=? WHERE userId=?;");

	// required fields
	if(user._userId == null)
		throw new Exception("user must have a valid ID!");
	statement.setString(6, user._userId.substring(0, Math.min(user._userId.length(), 25)));

	//optional fields
	if(user._userFirstName == null)
		statement.setString(1, user._userFirstName);
	else
		statement.setString(1, user._userFirstName.substring(0,Math.min(user._userFirstName.length(),64)));

	if(user._userLastName == null)
		statement.setString(2, user._userLastName);
	else
		statement.setString(2, user._userLastName.substring(0,Math.min(user._userLastName.length(),64)));

	if(user._userAddress == null)
		statement.setString(3, user._userAddress);
	else
		statement.setString(3, user._userAddress.substring(0,Math.min(user._userAddress.length(), 512)));
	
	if(user._userPhone == null)
		statement.setString(4, user._userPhone);
	else
		statement.setString(4, user._userPhone.substring(0,Math.min(user._userPhone.length(), 15)));

	statement.setString(5, user._userGender);
	
	statement.executeUpdate();
    	}

	public static boolean IsUserInDB(Connection conn, String userId) throws SQLException{
		userId = userId.trim();
		PreparedStatement statement = conn.prepareStatement("SELECT * FROM users WHERE userId=? AND isActive=1;");
		statement.setString(1, userId);
		ResultSet res = statement.executeQuery();
		if(!res.next())
			return false;
		return true;
	}
	
	public static boolean IsUserDeactivatedInDB(Connection conn, String userId) throws SQLException{
		userId = userId.trim();
		PreparedStatement statement = conn.prepareStatement("SELECT * FROM users WHERE userId=? AND isActive=0;");
		statement.setString(1, userId);
		ResultSet res = statement.executeQuery();
		if(!res.next())
			return false;
		return true;
	}
    
	public static User getUser(Connection conn, String userId) throws SQLException, Exception {
		userId = userId.trim();
		
		int balance = getUserBalance(conn, userId);
		
		PreparedStatement statement = conn.prepareStatement("SELECT * FROM users WHERE userId=? AND isActive=1;");
		statement.setString(1, userId);
		ResultSet res = statement.executeQuery();
		if(!res.next())
			return null;
		User user = new User(res.getString("userId"), 
							null, //password
							res.getString("firstName"), 
							res.getString("lastName"),
							res.getString("gender"),
							res.getString("address"), 
							res.getString("phone"), 
							res.getString("photoUri"), 
							balance,
							res.getBoolean("isActive")); 
		return user;
	}
	   
	public static List<User> getAllUsers(Connection conn) throws SQLException, Exception {
		List<User> users = new ArrayList<User>();
		PreparedStatement statement = conn.prepareStatement("SELECT * FROM users WHERE isActive=1;");
		ResultSet res = statement.executeQuery();
		while (res.next()) {
			User user = getUser(conn, res.getString("userId"));
			users.add(user);
		}
		return users;
	}
		
	public static List<User> getUsersPerPage(Connection conn, int page, double numPerPage) throws SQLException, Exception {
			List<User> users = new ArrayList<User>();
			PreparedStatement statement = conn.prepareStatement("SELECT * FROM users WHERE isActive=1 LIMIT ?, ?;");
			statement.setInt(1, (page-1)*((int)numPerPage));
			statement.setInt(2, (int)numPerPage);
			ResultSet res = statement.executeQuery();
			while (res.next()) {
				User user = getUser(conn, res.getString("userId"));
				users.add(user);
			}
			return users;
	}
	
	
	//TODO
	public static List<User> getUsersPerPageBut(Connection conn, String userId,  int page, double numPerPage) throws SQLException, Exception {
		List<User> users = new ArrayList<User>();
		PreparedStatement statement = conn.prepareStatement("SELECT * FROM users WHERE isActive=1 LIMIT ?, ?;");
		statement.setInt(1, (page-1)*((int)numPerPage));
		statement.setInt(2, (int)numPerPage);
		ResultSet res = statement.executeQuery();
		while (res.next()) {
			User user = getUser(conn, res.getString("userId"));
			if(user._userId.equals(userId)) continue;
			users.add(user);
		}
		return users;
}
	
	public static int getNumUserPages(Connection conn, double numPerPage) throws SQLException{
		PreparedStatement statement = conn.prepareStatement("SELECT COUNT(*) FROM users WHERE isActive=1;");
		ResultSet res = statement.executeQuery();
		res.last();
		return (int)Math.ceil(res.getInt(1) / numPerPage); 
	}
	
	public static int getNumUserPagesBut(Connection conn, String userId, double numPerPage) throws SQLException{
		PreparedStatement statement = conn.prepareStatement("SELECT COUNT(*) FROM users WHERE isActive=1;");
		ResultSet res = statement.executeQuery();
		res.last();
		return (int)Math.ceil((res.getInt(1) - 1)/ numPerPage); 
	}
	
    public static int getUserBalance (Connection conn, String userId) throws SQLException, Exception {
		userId = userId.trim();
		
		if (!IsUserInDB(conn, userId)) {
			throw new Exception("User does not exist in the system!");
		}
		
    	int balance = 0;
		PreparedStatement statement1 = conn.prepareStatement("SELECT SUM(costForUser) FROM participants WHERE owner=?;");
   		statement1.setString(1, userId);
   		ResultSet res1 = statement1.executeQuery();
   		if (res1.last()) {
   			balance += -1 * res1.getInt(1);
   		}
   		
   		PreparedStatement statement2 = conn.prepareStatement("SELECT SUM(costForUser) FROM participants WHERE userId=?;");
   		statement2.setString(1, userId);
   		ResultSet res2 = statement2.executeQuery();
   		if (res2.last()) {
   			balance += res2.getInt(1);
   		}
   		
   		return balance;
    }
	
    
    /** deactivate all user's information**/
	public static void removeUser(Connection conn, String userId) throws SQLException, Exception{
		userId = userId.trim();
		
		if (!IsUserInDB(conn, userId)) {
			throw new Exception("User does not exist in the system!");
		}
		
		//deactivates all friends 
		PreparedStatement statement1 = conn.prepareStatement("SELECT friendId FROM friends WHERE userId=?;");
		statement1.setString(1, userId);
		ResultSet res = statement1.executeQuery();
		while (res.next()) {
			removeFriend(conn, userId, res.getString("friendId"));
		}
		
		//deactivates all user's tasks
		PreparedStatement statement2 = conn.prepareStatement("UPDATE tasks SET isActive=0 WHERE owner=?;");
		statement2.setString(1, userId);
		statement2.executeUpdate();
		
		//deactivate user
		PreparedStatement statement3 = conn.prepareStatement("UPDATE users SET isActive=0 WHERE userId=?;");
		statement3.setString(1, userId);
		statement3.executeUpdate();
	}

    /** deactivate all user's information**/
	public static void retrieveUser(Connection conn, String userId, String password) throws SQLException, Exception{
		userId = userId.trim();
		password = password.trim();
		
		if (!IsUserDeactivatedInDB(conn, userId)) {
			throw new Exception("User is not deactivated in the system!");
		}
		
		//activates all friends 
		PreparedStatement statement1 = conn.prepareStatement("SELECT friendId FROM friends WHERE userId=?;");
		statement1.setString(1, userId);
		ResultSet res = statement1.executeQuery();
		while (res.next()) {
			retrieveFriend(conn, userId, res.getString("friendId"));
		}
		
		//activates all user's tasks
		PreparedStatement statement2 = conn.prepareStatement("UPDATE tasks SET isActive=1 WHERE owner=?;");
		statement2.setString(1, userId);
		statement2.executeUpdate();
		
		//activate user
		PreparedStatement statement3 = conn.prepareStatement("UPDATE users SET isActive=1 WHERE userId=?;");
		statement3.setString(1, userId);
		statement3.executeUpdate();
	}
	
	public static boolean verifyPassword(Connection conn, String userId, String password) throws SQLException{
		userId = userId.trim();
		password = password.trim();
		
		PreparedStatement statement = conn.prepareStatement("SELECT * FROM users WHERE userId=? AND password=? AND isActive=1;");
		statement.setString(1, userId);
		statement.setString(2,password);
		ResultSet res = statement.executeQuery();
		return res.next();
	}
	
	public static void updateUsersPassword(Connection conn, String userId, String password) throws SQLException{
		userId = userId.trim();
		password = password.trim();
		
		PreparedStatement statement = conn.prepareStatement("UPDATE users SET password=? WHERE userId=? AND isActive=1;");
		statement.setString(1, password);
		statement.setString(2, userId);
		statement.executeUpdate();
	}
	
    /*** Friends Functions ***/
	/*******************************************************************************************************/

	public static void addFriend(Connection conn, String userId, String friendId) throws SQLException, Exception{	
		userId = userId.trim();
		friendId = friendId.trim();
		
		if (!IsUserInDB(conn, userId)) 
			throw new Exception("User does not exist in the system!");
		
		if (!IsUserInDB(conn, friendId)) 
			throw new Exception("Friend does not exist in the system!");
		
		if(isFriends(conn, userId, friendId))
			throw new Exception("Users are already friends!");

		PreparedStatement statement1 = conn.prepareStatement("INSERT INTO friends VALUES(?,?,1);");
		statement1.setString(1, userId);
		statement1.setString(2, friendId);
		statement1.executeUpdate();

		PreparedStatement statement2 = conn.prepareStatement("INSERT INTO friends VALUES(?,?,1);");
		statement2.setString(1, friendId);
		statement2.setString(2, userId);
		statement2.executeUpdate();
	}
	
	public static boolean isFriends(Connection conn, String userId, String friendId) throws SQLException, Exception{
		userId = userId.trim();
		friendId = friendId.trim();
		
		if (!IsUserInDB(conn, userId)) 
			throw new Exception("User does not exist in the system!");

		if (!IsUserInDB(conn, friendId)) 
			throw new Exception("Friend does not exist in the system!");
			
		PreparedStatement statement = conn.prepareStatement("SELECT * FROM friends WHERE userId=? AND friendId=? AND isActive=1;");
		statement.setString(1, userId);
		statement.setString(2, friendId);
		ResultSet res = statement.executeQuery();
		if(res.next())
			return true;
		return false;
	}
    
	public static boolean IsDeactivatedFriends(Connection conn, String userId, String friendId) throws SQLException, Exception{
		userId = userId.trim();
		friendId = friendId.trim();

		if (!IsUserInDB(conn, friendId)) 
			throw new Exception("Friend does not exist in the system!");
			
		PreparedStatement statement = conn.prepareStatement("select friendId from friends where userId=? AND friendId=? AND isActive=0;");
		statement.setString(1, userId);
		statement.setString(2, friendId);
		ResultSet res = statement.executeQuery();
		if(res.next())
			return true;
		return false;
	}
	
	public static void deleteFriend(Connection conn, String userId, String friendId) throws SQLException, Exception{	
		userId = userId.trim();
		friendId = friendId.trim();
		
		if (!IsUserInDB(conn, userId)) 
			throw new Exception("User does not exist in the system!");
			
		if (!IsUserInDB(conn, friendId)) 
			throw new Exception("Friend does not exist in the system!");
		
		if(!isFriends(conn, userId, friendId))
			throw new Exception("Users are not friends!");

		PreparedStatement statement1 = conn.prepareStatement("DELETE FROM friends WHERE userId=? AND friendId=?");
		statement1.setString(1, userId);
		statement1.setString(2, friendId);
		statement1.executeUpdate();

		PreparedStatement statement2 = conn.prepareStatement("DELETE FROM friends WHERE userId=? AND friendId=?");
		statement2.setString(1, friendId);
		statement2.setString(2, userId);
		statement2.executeUpdate();
	}
	
	public static void removeFriend(Connection conn, String userId, String friendId) throws SQLException, Exception{	
		userId = userId.trim();
		friendId = friendId.trim();
		
		if (!IsUserInDB(conn, userId)) 
			throw new Exception("User does not exist in the system!");
			
		if (!IsUserInDB(conn, friendId)) 
			throw new Exception("Friend does not exist in the system!");
		
		if(!isFriends(conn, userId, friendId))
			throw new Exception("Users are not friends!");

		PreparedStatement statement1 = conn.prepareStatement("UPDATE friends SET isActive=0 WHERE userId=? AND friendId=?");
		statement1.setString(1, userId);
		statement1.setString(2, friendId);
		statement1.executeUpdate();

		PreparedStatement statement2 = conn.prepareStatement("UPDATE friends SET isActive=0 WHERE userId=? AND friendId=?");
		statement2.setString(1, friendId);
		statement2.setString(2, userId);
		statement2.executeUpdate();
	}
	
	public static void retrieveFriend(Connection conn, String userId, String friendId) throws SQLException, Exception{	
		userId = userId.trim();
		friendId = friendId.trim();
			
		if (!IsUserInDB(conn, friendId)) 
			throw new Exception("Friend does not exist in the system!");
		
		if(!IsDeactivatedFriends(conn, userId, friendId))
			throw new Exception("Users are not deactivated friends!");

		PreparedStatement statement1 = conn.prepareStatement("UPDATE friends SET isActive=1 WHERE userId=? AND friendId=?");
		statement1.setString(1, userId);
		statement1.setString(2, friendId);
		statement1.executeUpdate();

		PreparedStatement statement2 = conn.prepareStatement("UPDATE friends SET isActive=1 WHERE userId=? AND friendId=?");
		statement2.setString(1, friendId);
		statement2.setString(2, userId);
		statement2.executeUpdate();
	}
    
	public static List<User> getAllUserFriends(Connection conn, String userId) throws SQLException, Exception {
		userId = userId.trim();
		List<User> friends = new ArrayList<User>();
		PreparedStatement statement = conn.prepareStatement("SELECT friendId FROM friends WHERE userId=? AND isActive=1;");
		statement.setString(1, userId);
		ResultSet res = statement.executeQuery();
		while (res.next()) {
			User friend = getUser(conn, res.getString("friendId"));
			friends.add(friend);
		}
		return friends;
	}
	
	public static  List<User> getUserFriendsPerPage(Connection conn, String userId, int page) throws SQLException, Exception {
	    userId = userId.trim();
	    List<User> friends = new ArrayList<User>();
	    PreparedStatement statement = conn.prepareStatement("SELECT friendId FROM friends WHERE "
	    		+ "userId=? AND isActive=1 LIMIT ?, ?;");
	    statement.setString(1, userId);
	    statement.setInt(2, (int)(resPerPage * page) - 1);
	    statement.setInt(3, (int)resPerPage);
	    ResultSet res = statement.executeQuery();
	    while (res.next()) {
		User friend = getUser(conn, res.getString("friendId"));
		friends.add(friend);
	    }
	    return friends;
	}
	
	public static List<String> getAllUserFriendsIds(Connection conn, String userId) throws SQLException {
		userId = userId.trim();
		List<String> friends = new ArrayList<String>();
		PreparedStatement statement = conn.prepareStatement("SELECT friendId FROM friends WHERE userId=? AND isActive=1;");
		statement.setString(1, userId);
		ResultSet res = statement.executeQuery();
		while (res.next()) {
			friends.add(res.getString("friendId"));
		}
		return friends;
	}
	
    /*** Task Functions ***/
	/*******************************************************************************************************/
    
	public static void addTask (Connection conn, Task task) throws SQLException, Exception{
		PreparedStatement statement = conn.prepareStatement("INSERT INTO tasks VALUES(?,?,?,?,?,?,?,0,?,?,?);");
		
		// required fields
				statement.setString(1, task._taskId.substring(0, Math.min(task._taskId.length(), 24)));
				statement.setInt(2, task._isTask);

				if(task._taskOwner == null || !IsUserInDB(conn, task._taskOwner))
					throw new Exception("Owner does not exist in the system!");
				else
					statement.setString(3, task._taskOwner.substring(0, Math.min(task._taskOwner.length(), 25)));

				if(task._taskTitle == null)
					throw new Exception("Task must have a title!");
				else
					statement.setString(4, task._taskTitle.substring(0, Math.min(task._taskTitle.length(), 128)));
				
				//optional fields
				if(task._taskDescription == null)
					statement.setString(5, task._taskDescription);
				else
					statement.setString(5, task._taskDescription.substring(0, Math.min(task._taskDescription.length(), 512)));

				statement.setInt(6, task._taskCapacity);
				
				statement.setInt(7, task._taskCostPerUser);
				
				statement.setInt(8, task._taskDistance);

				if(task._photoUri == null)
					statement.setString(9, task._photoUri);
				else
					statement.setString(9, task._photoUri.substring(0, Math.min(task._photoUri.length(), 512)));
				
				int isActive = task._isActive ? 1 : 0;
				statement.setInt(10, isActive); 
				
				statement.executeUpdate();
	}

	public static void deleteTask (Connection conn, String taskId) throws SQLException, Exception{
		taskId = taskId.trim();

		if(!isTaskInDB(conn, taskId))
			throw new Exception("Task does not exist in the system!");
		
		PreparedStatement statement1 = conn.prepareStatement("DELETE FROM participants WHERE taskId=?;");
		statement1.setString(1, taskId);
		statement1.executeUpdate();
		
		PreparedStatement statement2 = conn.prepareStatement("DELETE FROM tasks WHERE taskId=?;");
		statement2.setString(1, taskId);
		statement2.executeUpdate();	
	}
	
	public static void updateTask (Connection conn, Task task) throws SQLException, Exception {
	    PreparedStatement update = conn.prepareStatement("UPDATE tasks SET title=?, description=?, capacity=?"
	    	+ ", cost=?, distance=? WHERE taskId=?;");
	       
	    if(task._taskTitle == null)
	    	throw new Exception("Task must have a title!");
	    else
	    	update.setString(1, task._taskTitle.substring(0, Math.min(task._taskTitle.length(), 128)));
		
	    //optional fields
	    if(task._taskDescription == null)
	    	update.setString(2, task._taskDescription);
	    else {
	    	update.setString(2, task._taskDescription.substring(0, Math.min(task._taskDescription.length(), 512)));
	    }
	    
		update.setInt(3, task._taskCapacity);		
		update.setInt(4, task._taskCostPerUser);		
		update.setInt(5, task._taskDistance);
		update.setString(6, task._taskId);
		update.executeUpdate();
	}
	
	public static void removeTask (Connection conn, String taskId) throws SQLException, Exception{
		taskId = taskId.trim();

		if(!isTaskInDB(conn, taskId))
			throw new Exception("Task does not exist in the system!");
		
		PreparedStatement statement = conn.prepareStatement("UPDATE tasks SET isActive=0 WHERE taskId=?;");
		statement.setString(1, taskId);
		statement.executeUpdate();
	}
	
	public static void addUserToTask (Connection conn, String taskId, String userId) throws SQLException, Exception{
		taskId = taskId.trim();

		if(!isTaskInDB(conn, taskId))
			throw new Exception("Task does not exist in the system!");
		
		if(!IsUserInDB(conn, userId))
			throw new Exception("User does not exist in the system!");
		
		PreparedStatement statement1 = conn.prepareStatement("SELECT capacity, numParticipants, isTask, cost, owner FROM tasks WHERE taskId=? AND isActive=1;");
		statement1.setString(1, taskId);
		ResultSet res1 = statement1.executeQuery();
		if(!res1.next())
			throw new Exception("Task does not exist in the system!");
		int capacity = res1.getInt(1);
		int numParticipants = res1.getInt(2);
		int isTask = res1.getInt(3);
		if (isTask == 0) {
		    isTask = -1;
		}
		int cost = res1.getInt(4);
		String owner = res1.getString(5);
		if(capacity == numParticipants)
			throw new Exception("Task is full!");
		if(userId.equals(owner))
			throw new Exception("owner can not join his own tasks!");
		
		PreparedStatement statement2 = conn.prepareStatement("UPDATE tasks SET numParticipants=numParticipants+1 WHERE taskId=?;");
		//statement2.setInt(1, numParticipants + 1);
		statement2.setString(1, taskId);
		statement2.executeUpdate();
		
		PreparedStatement statement3 = conn.prepareStatement("INSERT INTO participants VALUES(?, ?, ?, ?);");
		int realCost = isTask * cost;
		statement3.setString(1, taskId);
		statement3.setString(2, owner);
		statement3.setString(3, userId);
		statement3.setInt(4, realCost);//task = 1, service = -1
		statement3.executeUpdate();
		
		PreparedStatement statement4 = conn.prepareStatement("UPDATE users SET balance=balance+? WHERE userId=?;");
		statement4.setInt(1, realCost);
		statement4.setString(2, userId);
		statement4.executeUpdate();
	}
	
	public static void deleteUserFromTask (Connection conn, String taskId, String userId) throws SQLException, Exception{
		taskId = taskId.trim();
		userId = userId.trim();

//		if(!isTaskInDB(taskId))
//			throw new Exception("Task does not exist in the system!");
		
		if(!IsUserInDB(conn, userId))
			throw new Exception("User does not exist in the system!");
		
		if(! isUserInTask(conn, taskId, userId))
			throw new Exception("User is not in task!");
		
		PreparedStatement statement1 = conn.prepareStatement("DELETE FROM participants WHERE taskId=? AND userId=?;");
		statement1.setString(1, taskId);
		statement1.setString(2, userId);
		statement1.executeUpdate();
		
		PreparedStatement statement2 = conn.prepareStatement("SELECT numParticipants, cost, isTask FROM tasks WHERE taskId=?;");
		statement2.setString(1, taskId);
		ResultSet res1 = statement2.executeQuery();
		if(!res1.next())
			throw new Exception("Task doesn't exist");
		int numParticipants = res1.getInt(1);
		int cost = res1.getInt(2);
		int isTask = res1.getInt(3);
		if (isTask == 0) {
		    isTask = -1;
		}
		int realCost = isTask*cost;
		
		PreparedStatement statement3 = conn.prepareStatement("UPDATE tasks SET numParticipants=? WHERE TaskId=?;");
		statement3.setInt(1, numParticipants - 1);
		statement3.setString(2, taskId);
		statement3.executeUpdate();
		
		PreparedStatement statement4 = conn.prepareStatement("UPDATE users SET balance=balance+? WHERE userId=?;");
		statement4.setInt(1, realCost);
		statement4.setString(2, userId);
		statement4.executeUpdate();
	}
	
	public static Task getTask (Connection conn, String taskId) throws SQLException, Exception{
		taskId = taskId.trim();
		
		PreparedStatement statement = conn.prepareStatement("SELECT * FROM tasks WHERE taskId=? AND isActive=1;");
		statement.setString(1, taskId);
		ResultSet res = statement.executeQuery();
		if(!res.next()) {
		    return null;
		}
		Task task = new Task(res.getString("taskId"),
							res.getInt("isTask"),
							res.getString("owner"), 
							res.getString("Title"),
							res.getString("Description"), 
							res.getInt("Capacity"),
							res.getInt("Cost"), 
							res.getInt("numParticipants"), 
							res.getInt("Distance"),
							res.getString("photoUri"),
							res.getBoolean("isActive")); 
		return task;	
	}
	
	public static List<Task> getAllTasks (Connection conn) throws SQLException, Exception{
		List<Task> tasks = new ArrayList<Task>();
		PreparedStatement statement = conn.prepareStatement("SELECT * FROM tasks WHERE isActive=1;");
		ResultSet res = statement.executeQuery();
		while (res.next()) {
			Task task = DataBase.getTask(conn, res.getString("taskId"));
			tasks.add(task);
		}
		return tasks;	
	}
	
	public static List<Task> getTasksPerPage (Connection conn, int page, double numPerPage) throws SQLException, Exception{		
		List<Task> tasks = new ArrayList<Task>();
		PreparedStatement statement = conn.prepareStatement("SELECT * FROM tasks WHERE isActive=1 LIMIT ?, ?;");
		statement.setInt(1, (page-1)*((int)numPerPage));
		statement.setInt(2, (int)numPerPage);
		ResultSet res = statement.executeQuery();
		while (res.next()) {
			Task task = getTask(conn, res.getString("taskId"));
			tasks.add(task);
		}
		return tasks;
	}
	
	
	public static int getNumTasksPerPage (Connection conn) throws SQLException{
		PreparedStatement statement = conn.prepareStatement("SELECT COUNT(*) FROM tasks WHERE isActive=1;");
		ResultSet res = statement.executeQuery();
		res.last();
		return (int)Math.ceil(res.getInt(1) / resPerPage); 
	}
	
	public static List<User> getUsersInTask (Connection conn, String taskId) throws SQLException, Exception{
		taskId = taskId.trim();
		
		if(!isTaskInDB(conn, taskId))
			throw new Exception("Task does not exist in the system!");
		
		List<User> users = new ArrayList<User>();
		PreparedStatement statement = conn.prepareStatement("SELECT userId FROM participants WHERE taskId=?;");
		statement.setString(1, taskId);
		ResultSet res = statement.executeQuery();
		while (res.next()) {
			User user = getUser(conn, res.getString("userId"));
			users.add(user);
		}
		return users;
	}
	
	public static List<User> getUsersInTaskPerPage (Connection conn, String taskId, int page, double numPerPage) throws SQLException, Exception{
		taskId = taskId.trim();
		
		if(!isTaskInDB(conn, taskId))
			throw new Exception("Task does not exist in the system!");
		
		List<User> users = new ArrayList<User>();
		PreparedStatement statement = conn.prepareStatement("SELECT * FROM participants WHERE taskId=? LIMIT ?, ?;");
		statement.setString(1, taskId);
		statement.setInt(2, (page-1)*((int)numPerPage));
		statement.setInt(3, (int)numPerPage);
		ResultSet res = statement.executeQuery();
		while (res.next()) {
			User user = getUser(conn, res.getString("userId"));
			users.add(user);
		}
		return users;
	}
	
	public static int getNumUsersInTaskPerPage (Connection conn, String taskId, double numPerPage) throws SQLException, Exception{
		taskId = taskId.trim();
		
		if(!isTaskInDB(conn, taskId))
			throw new Exception("Task does not exist in the system!");
		
		PreparedStatement statement = conn.prepareStatement("SELECT COUNT(*) FROM participants WHERE taskId=?;");
		statement.setString(1, taskId);
		ResultSet res = statement.executeQuery();
		res.last();
		return (int)Math.ceil(res.getInt(1) / numPerPage); 
	}
	
	public static List<Task> getAllUserTasks (Connection conn, String userId) throws SQLException, Exception{
		userId = userId.trim();
		
		if(!IsUserInDB(conn, userId))
			throw new Exception("User does not exist in the system!");
		
		List<Task> tasks = new ArrayList<Task>();
		PreparedStatement statement = conn.prepareStatement("SELECT taskId FROM tasks WHERE owner=?;");
		statement.setString(1, userId);
		ResultSet res = statement.executeQuery();
		while (res.next()) {
			Task task = DataBase.getTask(conn, res.getString("taskId"));
			tasks.add(task);
		}
		return tasks;
	}
	
	 public static List<Task> getUserTasksPerPage (Connection conn, String userId, int page, double numPerPage) throws SQLException, Exception{
		userId = userId.trim();
		
		if(!IsUserInDB(conn, userId))
			throw new Exception("User does not exist in the system!");
		
		List<Task> tasks = new ArrayList<Task>();
		PreparedStatement statement = conn.prepareStatement("SELECT * FROM tasks WHERE owner=? LIMIT ?, ?;");
		statement.setInt(2, (page-1)*((int)numPerPage));
		statement.setInt(3, (int)numPerPage);
		statement.setString(1, userId);
		ResultSet res = statement.executeQuery();
		while (res.next()) {
			Task task = getTask(conn, res.getString("taskId"));
			tasks.add(task);
		}
		return tasks;
	}
	
	public static int getNumUserTasksPerPage (Connection conn, String userId, double numPerPage) throws SQLException, Exception{
		userId = userId.trim();
		
		if(!IsUserInDB(conn, userId))
			throw new Exception("User does not exist in the system!");
		
		PreparedStatement statement = conn.prepareStatement("SELECT COUNT(*) FROM tasks WHERE owner=?;");
		statement.setString(1, userId);
		ResultSet res = statement.executeQuery();
		res.last();
		return (int)Math.ceil(res.getInt(1) / numPerPage); 
	}
	
	public static int getNumTasksAvailableToUser(Connection conn, String userId, double numPerPage) throws SQLException, Exception {
	    	userId = userId.trim();
		
		if(!IsUserInDB(conn, userId))
			throw new Exception("User does not exist in the system!");
		
		PreparedStatement statement = conn.prepareStatement("SELECT COUNT(*) FROM tasks WHERE"
			+ " owner IN (SELECT friendId from friends WHERE userId=?);");
		statement.setString(1, userId);
		ResultSet res = statement.executeQuery();
		res.last();
		return (int)Math.ceil(res.getInt(1) / numPerPage);
	}

	public static boolean isTaskInDB (Connection conn, String taskId) throws SQLException, Exception{
		taskId = taskId.trim();
		
		PreparedStatement statement = conn.prepareStatement("SELECT * FROM tasks WHERE taskId=? AND isActive=1;");
		statement.setString(1, taskId);
		ResultSet res = statement.executeQuery();
		if(!res.next())
			return false;
		return true;
		}
	
	static public boolean isTaskActive (Connection conn, String taskId) throws SQLException, Exception{
		taskId = taskId.trim();
		
		PreparedStatement statement = conn.prepareStatement("SELECT * FROM tasks WHERE taskId=? AND isActive=1;");
		statement.setString(1, taskId);
		ResultSet res = statement.executeQuery();
		if(!res.next())
			return false;
		return true;
	}
	
	public static boolean isUserInTask (Connection conn, String taskId, String userId) throws SQLException, Exception{
		taskId = taskId.trim();
		userId = userId.trim();
		
		if(!IsUserInDB(conn, userId))
			throw new Exception("User does not exist in the system!");
		
		if(!isTaskInDB(conn, taskId))
			throw new Exception("Task does not exist in the system!");
		
		PreparedStatement statement = conn.prepareStatement("SELECT * FROM participants WHERE userId=? "
					+ "AND taskId=?;");
		statement.setString(1, userId);
		statement.setString(2, taskId);
		ResultSet res = statement.executeQuery();
		if(!res.next())
			return false;
		return true;
	}
	
	public static List<Task> getUserParticipateTasksPerPage(Connection conn, String userId, int page, double numPerPage) throws SQLException, Exception{
		List<Task> tasks = new ArrayList<Task>();
		PreparedStatement statement = conn.prepareStatement("SELECT * FROM participants WHERE userId=? LIMIT ?, ?;");
		statement.setString(1, userId);
		statement.setInt(2, (page-1)*((int)numPerPage));
		statement.setInt(3, (int)numPerPage);
		ResultSet res = statement.executeQuery();
		while (res.next()) {
			Task task = getTask(conn, res.getString("taskId"));
			tasks.add(task);
		}
		return tasks;
	}

	public static int getNumUserParticipateTasksPerPage(Connection conn, String userId, double numPerPage) throws SQLException, Exception{
		userId = userId.trim();
		
		if(!IsUserInDB(conn, userId))
			throw new Exception("User does not exist in the system!");
		
		PreparedStatement statement = conn.prepareStatement("SELECT COUNT(*) FROM participants WHERE userId=?;");
		statement.setString(1, userId);
		ResultSet res = statement.executeQuery();
		res.last();
		return (int)Math.ceil(res.getInt(1) / numPerPage); 
	}
	
	public static List<Task> getUserFriendsTasksPerPage (Connection conn, String userId, 
		int page, double numPerPage) throws SQLException, Exception{
	    List<Task> tasks = new ArrayList<Task>();
		PreparedStatement statement = conn.prepareStatement("SELECT * FROM tasks WHERE"
			+ " owner IN (SELECT friendId from friends WHERE userId=?) LIMIT ?, ?;");
		statement.setString(1, userId);
		statement.setInt(2, (page-1)*((int)numPerPage));
		statement.setInt(3, (int)numPerPage);
		ResultSet res = statement.executeQuery();
		while (res.next()) {
			Task task = getTask(conn, res.getString("taskId"));
			tasks.add(task);
		}
		return tasks;
	}
	
	public static boolean isTaskFull(Connection conn, String taskId) throws SQLException, Exception{
		taskId = taskId.trim();

		if(!isTaskInDB(conn, taskId))
			throw new Exception("Task does not exist in the system!");
		
		PreparedStatement statement = conn.prepareStatement("SELECT capacity, numParticipants FROM tasks WHERE taskId=? AND isActive=1;");
		statement.setString(1, taskId);
		ResultSet res = statement.executeQuery();
		if(!res.next())
			throw new Exception("Task does not exist in the system!");
		int capacity = res.getInt(1);
		int numParticipants = res.getInt(2);
		if(capacity == numParticipants)
			return true;
		return false;
	}
	
	public static boolean isUserOwnTask(Connection conn, String userId, String taskId) throws SQLException, Exception{
		taskId = taskId.trim();
		userId = userId.trim();

		if(!isTaskInDB(conn, taskId))
			throw new Exception("Task does not exist in the system!");
		
		if(!IsUserInDB(conn, userId))
			throw new Exception("User does not exist in the system!");
		
		PreparedStatement statement1 = conn.prepareStatement("SELECT owner FROM tasks WHERE taskId=? AND isActive=1;");
		statement1.setString(1, taskId);
		ResultSet res1 = statement1.executeQuery();
		if(!res1.next())
			throw new Exception("Task does not exist in the system!");
		String owner = res1.getString(1);
		if(userId.equals(owner))
			return true;
		return false;
	}
	
	public static boolean isTask (Connection conn, String taskId) throws SQLException, Exception{
		taskId = taskId.trim();
		
		if(!isTaskInDB(conn, taskId))
			throw new Exception("Task does not exist in the system!");
		
		PreparedStatement statement = conn.prepareStatement("SELECT * FROM tasks WHERE taskId=?;");
		statement.setString(1, taskId);
		ResultSet res = statement.executeQuery();
		if(!res.next())
			throw new Exception("Task does not exist in the system!");
		boolean isTask = res.getBoolean("isTask");
		if(isTask)
			return true;
		return false;
	}
	
	public static List<User> getUsersSecondCircle(Connection conn, String userId) throws SQLException, Exception {
	    if (!IsUserInDB(conn, userId)) {
		throw new Exception("User does not exist in the system");
	    }
	    PreparedStatement statement = conn.prepareStatement("SELECT a.userId, b.friendId FROM"
	    	+ " (SELECT userId, friendId AS f from friends) a "
	    	+ "INNER JOIN (SELECT userId AS f, friendId FROM friends) b ON a.f=b.f"
	    	+ " WHERE userId<>friendId and userId=?;");
	    statement.setString(1, userId);
	    ResultSet res = statement.executeQuery();
	    List<User> users = new ArrayList<User>();
	    while (res.next()) {
		User user = getUser(conn, res.getString("friendId"));
		users.add(user);
	    }
	    return users;
	}

	static public List<Participant> getAllParticipants(Connection conn) throws SQLException, Exception{
		List<Participant> partList = new ArrayList<Participant>();
		PreparedStatement statement = conn.prepareStatement("SELECT * FROM participants");
		ResultSet res = statement.executeQuery();
		while (res.next()) {
			if(!isTaskActive(conn, res.getString("taskId"))) continue;
			if(!IsUserInDB(conn, res.getString("userId"))) continue;
			Participant part = new Participant(res.getString("userId"), res.getString("taskId"));
			partList.add(part);
		}
		return partList;
	}
	
	static public List<FriendsTupple> getAllFriends(Connection conn) throws SQLException, Exception{
		List<FriendsTupple> friendsList = new ArrayList<FriendsTupple>();
		PreparedStatement statement = conn.prepareStatement("SELECT * FROM friends");
		ResultSet res = statement.executeQuery();
		while (res.next()) {
			if(!IsUserInDB(conn, res.getString("userId"))) continue;
			if(!IsUserInDB(conn, res.getString("friendId"))) continue;
			FriendsTupple tupple = new FriendsTupple(res.getString("userId"), res.getString("friendId"));
			friendsList.add(tupple);
		}
		return friendsList;
	}
	
	static public void deleteAllUserTasks(Connection conn, String userId) throws SQLException, Exception {
		if(!IsUserInDB(conn, userId))
			throw new Exception("User does not exist in the system!");
		
		PreparedStatement statement1 = conn.prepareStatement("DELETE FROM participants WHERE owner=?;");
		statement1.setString(1, userId);
		statement1.executeUpdate();
		
		PreparedStatement statement2 = conn.prepareStatement("DELETE FROM tasks WHERE owner=?;");
		statement2.setString(1, userId);
		statement2.executeUpdate();	
	}
	
	static public void removeUserFromAllTasks(Connection conn, String userId) throws SQLException, Exception {
		if(!IsUserInDB(conn, userId))
			throw new Exception("User does not exist in the system!");
		
		PreparedStatement statement1 = conn.prepareStatement("DELETE FROM participants WHERE userId=?;");
		statement1.setString(1, userId);
		statement1.executeUpdate();
	}
	
	static public List<User> searchForUsersPerPage(Connection conn, String search, int page, double numPerPage) throws SQLException, Exception {
		List<User> users = new ArrayList<User>();
		if(search == null || search.isEmpty()) return users;
		search = "%" + search.toLowerCase() + "%";
	    PreparedStatement statement = conn.prepareStatement(
	    		"SELECT * FROM users WHERE lower(Concat(" + 
	    		"IFNULL(userId, ''), '', IFNULL(firstName, '')," +
	    		" '', IFNULL(lastName, ''), '', IFNULL(phone, ''), ''," + 
	    		" IFNULL(address, ''), '', IFNULL(gender, ''))) LIKE ?  LIMIT ?, ?;");
	   
	    statement.setString(1, search);
		statement.setInt(2, (page-1)*((int)numPerPage));
		statement.setInt(3, (int)numPerPage);
	    ResultSet res = statement.executeQuery();
	    while (res.next()) {
			User user = getUser(conn, res.getString("userId"));
			users.add(user);
	    }		
		return users;
	}
	
	public static int getNumSearchForUsersPerPage(Connection conn, String search, double numPerPage) throws SQLException, Exception{
		if(search == null || search.isEmpty()) return 0;
		search = "%" + search.toLowerCase() + "%";
	    PreparedStatement statement = conn.prepareStatement(
	    		"SELECT COUNT(*) FROM users WHERE lower(Concat(" + 
	    		"IFNULL(userId, ''), '', IFNULL(firstName, '')," +
	    		" '', IFNULL(lastName, ''), '', IFNULL(phone, ''), ''," + 
	    		" IFNULL(address, ''), '', IFNULL(gender, ''))) LIKE ?");
	    ResultSet res = statement.executeQuery();
	    statement.setString(1, search);
		res.last();
		return (int)Math.ceil(res.getInt(1) / numPerPage); 
	}
	
	static public List<Task> searchForUserFriendsTasksPerPage(Connection conn, String userId, String search, int page, double numPerPage) throws SQLException, Exception {
		if(!IsUserInDB(conn, userId))
			throw new Exception("User does not exist in the system!");
		
		List<Task> tasks = new ArrayList<Task>();
		if(search == null || search.isEmpty()) return tasks;
		search = "%" + search.toLowerCase() + "%";
	    PreparedStatement statement = conn.prepareStatement(
	    		"SELECT * FROM tasks WHERE lower(Concat(" + 
	    	    		"IFNULL(title, ''), '', IFNULL(description, ''))) LIKE ? " + 
	    				" AND owner IN (SELECT friendId from friends WHERE userId=?) LIMIT ?, ?;");
	    statement.setString(1, search);
	    statement.setString(2, userId);
		statement.setInt(3, (page-1)*((int)numPerPage));
		statement.setInt(4, (int)numPerPage);
	    ResultSet res = statement.executeQuery();
	    while (res.next()) {
			Task task = getTask(conn, res.getString("taskId"));
			tasks.add(task);
	    }		
		return tasks;
	}
	
	public static int getNumSearchForUserFriendsTasksPerPage(Connection conn, String userId, String search, double numPerPage) throws SQLException, Exception{
		if(!IsUserInDB(conn, userId))
			throw new Exception("User does not exist in the system!");
		
		if(search == null || search.isEmpty()) return 0;
		search = "%" + search.toLowerCase() + "%";
	    PreparedStatement statement = conn.prepareStatement(
	    		"SELECT COUNT(*) FROM tasks WHERE lower(Concat(" + 
	    	    		"IFNULL(title, ''), '', IFNULL(description, ''))) LIKE ? " + 
	    				" AND owner IN (SELECT friendId from friends WHERE userId=?) ");
	    ResultSet res = statement.executeQuery();
	    statement.setString(1, search);
	    statement.setString(2, userId);
		res.last();
		return (int)Math.ceil(res.getInt(1) / numPerPage); 
	}
	
}