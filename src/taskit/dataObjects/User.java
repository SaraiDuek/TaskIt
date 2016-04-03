package taskit.dataObjects;

import org.json.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class User {

	public String _userId;
	public String _userPassword;
	public String _userFirstName;
	public String _userLastName;
	public String _userAddress;
	public String _userPhone;
	public String _userPhoto; //url
	public String _userGender; // M - male, F - female
	public int _userBalance;
	public boolean _isActive;

	
	public User(String username, String password, String firstName, String lastName,
			String gender, String address, String phone, String photo, int balance, boolean isActive) {
		super();
		_userId = username;
		_userPassword = password;
		_userFirstName = firstName;
		_userLastName = lastName;
		_userGender = gender;
		_userAddress = address;
		_userPhone = phone;
		_userPhoto = photo;
		_userBalance = balance;
		_isActive = isActive;
	}
	
	public JSONObject toJSON() throws JSONException {
		JSONObject result = new JSONObject();
		result.put("userId", _userId);
		result.put("firstName", _userFirstName);
		result.put("lastName", _userLastName);
		result.put("address", _userAddress);
		result.put("phone", _userPhone);
		result.put("photoUri", _userPhoto);
		result.put("gender", _userGender);
		result.put("balance", _userBalance);
		return result;
	}
	
	public Document toXml() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document result = null;
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			result = builder.newDocument();
			Element root = result.createElement("User");
			result.appendChild(root);
			attachChild(result, root, "userId", _userId);
			attachChild(result, root, "firstName", _userFirstName);
			attachChild(result, root, "lastName", _userLastName);
			attachChild(result, root, "gender", _userGender);
			attachChild(result, root, "address", _userAddress);
			attachChild(result, root, "phone", _userPhone);
			attachChild(result, root, "photo", _userPhoto);
			attachChild(result, root, "balance", String.format("%d", _userBalance));
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private void attachChild (Document doc, Element parent, String childName, String childValue) {
		Element child = doc.createElement(childName);
		child.setTextContent(childValue);
		parent.appendChild(child);
	}
}
