$("#ownerDelete").click(onDeleteTask);


function onDeleteTask() {
	var contentDiv = $("#content")[0];
	var taskId = contentDiv.attr("taskId");
	var url = "/Task?taskId=" + taskId;
	$.ajax({
		type: "DELETE",
		url: url,
		success: onDeleteTaskSuccess,
		error: onDeleteTaskFailure,
	});
	return false;
}

function onDeleteTaskSuccess() {
	location.href = "/HW5/user.jsp";
}

function onDeleteTaskFailure() {
	alert("failed to delete task");
}

