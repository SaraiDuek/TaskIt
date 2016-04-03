package taskit.features;


import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;












import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Bag;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.VCARD;

import taskit.dataManager.DataBase;
import taskit.dataObjects.*;

public class RDFModel {
	private static final String BASE = "http://54.68.7.213:8080/HW5/assets/RDF/";

	private static Property p( String localname ) {
        return ResourceFactory.createProperty ( BASE, localname );
}	
	@SuppressWarnings("deprecation")
	public static void getRDF(StringWriter sw, String query) throws SQLException, Exception {
	    Connection conn = null;
		try {
			conn = DataBase.connectionPool.getConnection();
			conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
			conn.setAutoCommit(false);
			
			// create an empty Model
			Model model = ModelFactory.createDefaultModel();
				
			// create a users bag
			Bag usersBag = model.createBag(); 
			
			//get all users create resources and insert to bag
			List<User> usersList = DataBase.getAllUsers(conn);
			Resource u;
			if(usersList != null) {
				for(User user : usersList){
					u = model.createResource(BASE + user._userId).
							addProperty(VCARD.UID, user._userId);
					model.addLiteral (u, p("Balance"), 10);
					if(user._userFirstName != null) u.addProperty(VCARD.Given, user._userFirstName);
					if(user._userLastName != null) u.addProperty(VCARD.Family, user._userLastName);
					if(user._userGender != null) model.addLiteral(u, p("Gender"), user._userGender);
					if(user._userPhone != null) model.addLiteral(u, p("PhoneNumber"), user._userPhone);
					if(user._userAddress != null) u.addProperty(VCARD.MAILER, user._userAddress);
					if(user._userPhoto != null) u.addProperty(VCARD.PHOTO, user._userPhoto);
					usersBag.add(u);
				}		
			}			

			// create a tasks bag
			Bag tasksBag = model.createBag(); 
			
			//get all tasks create resources and insert to bag
			List<Task> tasksList = DataBase.getAllTasks(conn);
			Resource t;
			if(tasksList != null) {	
				for(Task task : tasksList){
					t = model.createResource(BASE + task._taskId);
					model.addLiteral(t, p("TaskId"), task._taskId).
					addLiteral(t, p("TaskTitle"), task._taskTitle).
					addLiteral(t, p("IsTask"), task._isTask).
					addLiteral(t, p("TaskCapacity"), task._taskCapacity).
					addLiteral(t, p("TaskCost"), task._taskCostPerUser).
					addLiteral(t, p("TaskDistance"), task._taskDistance).
					addLiteral(t, p("Participants"), task._numParticipants);
					if(task._taskDescription != null) model.addLiteral(t, p("TaskDescription"), task._taskDescription);
					t.addProperty(p("TaskOwner"), model.getResource(BASE + task._taskOwner));
					tasksBag.add(t);
				}
			}
			
			//get all users create resources and insert to bag
			List<Participant> partList = DataBase.getAllParticipants(conn);
			Resource f;
			if(partList != null) {
				for(Participant part : partList){
					u = model.getResource(BASE + part._userId);
					t = model.getResource(BASE + part._taskId);
					u.addProperty(p("ParticipatesIn"), t);
					t.addProperty(p("ContainsUser"), u);
				}
			}
			
			//get all users create resources and insert to bag
			List<FriendsTupple> friendsList = DataBase.getAllFriends(conn);
			if(friendsList != null) {	
				for(FriendsTupple tupple : friendsList){
					u = model.getResource(BASE + tupple._friend1);
					f = model.getResource(BASE + tupple._friend2);
					u.addProperty(p("FriendOf"), f);
				}
			}

			if(query.isEmpty()) {
				model.write(sw);
				return;
			}
			// now write the model in XML form to a file
			//model.write(System.out);
			//System.out.println("Execute query " + query);
		    
			Query statquery = QueryFactory.create(query);
			QueryExecution qexec = QueryExecutionFactory.create(statquery, model);
			    
			ResultSet results = qexec.execSelect();
			//ResultSetFormatter.out(System.out, results, statquery);
			String resultsAsString = ResultSetFormatter.asText(results);
			sw.write(resultsAsString);
			
		} finally{
			DataBase.closeConnection(conn);
		}
	}
	
	//Query
//	public static void main(String[] args) throws Exception
//	{
//		Connection conn = null;
//		try {
//			conn = DataBase.connectionPool.getConnection();
//			conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
//			conn.setAutoCommit(false);
//			
//			// create an empty Model
//			Model model = ModelFactory.createDefaultModel();
//				
//			// create a users bag
//			Bag usersBag = model.createBag(); 
//			
//			//get all users create resources and insert to bag
//			List<User> usersList = DataBase.getAllUsers(conn);
//			Resource u;
//			if(usersList != null) {
//				for(User user : usersList){
//					u = model.createResource(BASE + user._userId).
//							addProperty(VCARD.UID, user._userId);
//					model.addLiteral (u, p("Balance"), 10);
//					if(user._userFirstName != null) u.addProperty(VCARD.Given, user._userFirstName);
//					if(user._userLastName != null) u.addProperty(VCARD.Family, user._userLastName);
//					if(user._userGender != null) model.addLiteral(u, p("Gender"), user._userGender);
//					if(user._userPhone != null) model.addLiteral(u, p("PhoneNumber"), user._userPhone);
//					if(user._userAddress != null) u.addProperty(VCARD.MAILER, user._userAddress);
//					if(user._userPhoto != null) u.addProperty(VCARD.PHOTO, user._userPhoto);
//					usersBag.add(u);
//				}		
//			}			
//
//			// create a tasks bag
//			Bag tasksBag = model.createBag(); 
//			
//			//get all tasks create resources and insert to bag
//			List<Task> tasksList = DataBase.getAllTasks(conn);
//			Resource t;
//			if(tasksList != null) {	
//				for(Task task : tasksList){
//					t = model.createResource(BASE + task._taskId);
//					model.addLiteral(t, p("TaskId"), task._taskId).
//					addLiteral(t, p("TaskTitle"), task._taskTitle).
//					addLiteral(t, p("IsTask"), task._isTask).
//					addLiteral(t, p("TaskCapacity"), task._taskCapacity).
//					addLiteral(t, p("TaskCost"), task._taskCostPerUser).
//					addLiteral(t, p("TaskDistance"), task._taskDistance).
//					addLiteral(t, p("Participants"), task._numParticipants);
//					if(task._taskDescription != null) model.addLiteral(t, p("TaskDescription"), task._taskDescription);
//					t.addProperty(p("TaskOwner"), model.getResource(BASE + task._taskOwner));
//					tasksBag.add(t);
//				}
//			}
//			
//			//get all users create resources and insert to bag
//			List<Participant> partList = DataBase.getAllParticipants(conn);
//			Resource f;
//			if(partList != null) {
//				for(Participant part : partList){
//					u = model.getResource(BASE + part._userId);
//					t = model.getResource(BASE + part._taskId);
//					u.addProperty(p("ParticipatesIn"), t);
//					t.addProperty(p("ContainsUser"), u);
//				}
//			}
//			
//			//get all users create resources and insert to bag
//			List<FriendsTupple> friendsList = DataBase.getAllFriends(conn);
//			if(friendsList != null) {	
//				for(FriendsTupple tupple : friendsList){
//					u = model.getResource(BASE + tupple._friend1);
//					f = model.getResource(BASE + tupple._friend2);
//					u.addProperty(p("FriendOf"), f);
//				}
//			}
//
//			// now write the model in XML form to a file
//			//model.write(System.out);
//			//model.write(sw);
//
//		// change hint to hash to test hash join, or remove to use default
//		// join settings
//		String q = 
//		" PREFIX ORACLE_SEM_FS_NS: "                         +
//		" <http://oracle.com/semtech#join_method=nl>"        +
//		" PREFIX foaf: <http://xmlns.com/foaf/0.1/>"         +
//		" SELECT ?name1 ?name2 "                             +
//		" WHERE { "                                          +
//		"   graph <http://example.org/alice/foaf.rdf> { "    +
//		"     ?person1 foaf:knows ?person2 . "               +
//		"     ?person1 foaf:name ?name1 . "                  +
//		"     ?person2 foaf:name ?name2 . "                  +
//		"   } "                                              +
//		" } ";
//		    
//		System.out.println("Execute query " + q);
//		    
//		Query query = QueryFactory.create(q);
//		QueryExecution qexec = QueryExecutionFactory.create(query, model);
//		    
//		ResultSet results = qexec.execSelect();
//		ResultSetFormatter.out(System.out, results, query);
//	    
//		} finally{
//			DataBase.closeConnection(conn);
//		}
//	}
}
