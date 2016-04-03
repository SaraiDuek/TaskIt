package taskit.dataObjects;

public class Participant {
	public String _userId;
	public String _taskId;

	public Participant(String userId, String taskId) {
		super();
		this._userId = userId;
		this._taskId = taskId;
	}
}
