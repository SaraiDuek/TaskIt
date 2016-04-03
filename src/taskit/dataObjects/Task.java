package taskit.dataObjects;

import java.sql.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Task {
	public String _taskId;
	public int _isTask; //task = +1, service = -1
	public String _taskOwner;
	public String _taskTitle;
	public String _taskDescription;
	public int _taskCapacity;
	public int _taskCostPerUser;
	public int _numParticipants;
	public int _taskDistance;
	public String _photoUri;
	public boolean _isActive;
	
	/**
	 * Task C'tor
	 * @param id
	 * @param description
	 * @param provider
	 * @param capacity
	 * @param distance
	 */
	public Task (String taskId, int isTask, String ownerId, String title, String description, int capacity, int cost, 
			int numParticipants, int distance, String photoUri, boolean isActive) {
		_taskId = taskId;
		_isTask = isTask;
		_taskOwner = ownerId;
		_taskTitle = title;
		_taskDistance = distance;
		_taskDescription = description;
		_taskCostPerUser = cost;
		_numParticipants = numParticipants;
		_taskCapacity = capacity;
		_photoUri = photoUri;
		_isActive = isActive;
	}
	
	public Document toXml() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document result = null;
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			result = builder.newDocument();
			Element root = result.createElement("Task");
			result.appendChild(root);
			attachChild(result, root, "taskId", _taskId);
			attachChild(result, root, "title", _taskTitle);
			attachChild(result, root, "isTask", Integer.toString(_isTask));
			attachChild(result, root, "ownerId", _taskOwner);
			attachChild(result, root, "description", _taskDescription);
			attachChild(result, root, "costPerUser", Integer.toString(_taskCostPerUser));
			attachChild(result, root, "capacity", Integer.toString(_taskCapacity));
			attachChild(result, root, "photoUri", _photoUri);
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public JSONObject toJSON() {
		JSONObject result = new JSONObject();
		try {
			result.put("taskId", _taskId);
			result.put("title", _taskTitle);
			result.put("isTask", _isTask);
			result.put("ownerId", _taskOwner);
			result.put("description", _taskDescription);
			result.put("costPerUser", _taskCostPerUser);
			result.put("capacity", _taskCapacity);
			result.put("photoUri", _photoUri);
		} catch (JSONException e) {
			result = null;
		}
		return result;
	}
	
	private void attachChild (Document doc, Element parent, String childName, String childValue) {
		Element child = doc.createElement(childName);
		child.setTextContent(childValue);
		parent.appendChild(child);
	}
}
