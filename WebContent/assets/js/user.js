$("#addModalSubmitBtn").click(onAddTaskSubmit);
$(".icon-minus.partTask").click(onRemoveFromTask);
$(".icon-pencil").click(onEditTask);
$("#updateModalSubmitBtn").click(onUpdateSubmit);
$("#editUserSubmitBtn").click(onEditUserSubmit);
$("#editUserBtn").click(onEditUserToggle);
$(".deleteUser").click(onDeleteUser);
$(".deleteTask").click(onDeleteTask);
$("#addModalCloseBtn").click(onCancelAddTask);
$("#removeTasks").click(onRemoveAll);
$("#leaveTasks").click(onLeaveAll);
$("#changePassSubmit").click(onChangePass);

var curr="";

function onAddTaskSubmit() {
	var taskType = $("#taskType").val();
	var isTask = 0;
	if (taskType == "Task") {
		isTask = 1;
	}
	var owner = $("#uid")[0].textContent;
	var title = $("#title").val();
	var taskId = owner + title + Math.random().toString();
	var description = $("#desc").val();
	var capacity = $("#capacity").val();
	var cost = $("#cost").val();
	var distance = 1;
	
	//var photo = $("#photo").val(); 
	var url = "/HW5/Task?taskId=" + taskId + "&isTask=" + isTask; 
		url = url + "&title=" + title + "&cost=" + cost + "&capacity=" + capacity + "&distance=" + distance;
		url = url + "&description=" + description;
	$.ajax({
		type : "POST",
		url : url,
		success : onAddSuccess,
		error :onAddFailure,
	});
	
	return false;
}

function onAddSuccess() {
	location.href = "/HW5/user.jsp";
}

function onAddFailure(data) {
	alert(data);
}

function onRemoveFromTask() {
	var clickedElement = event.currentTarget;
	var taskId = clickedElement.id;
	curr = clickedElement.getElementsByClassName("icon-minus")[0];
	if (!curr) {
		curr = clickedElement;
	}
	var url = "/HW5/TaskParticipateController?taskId=" + taskId;
	$.ajax({
		type : "DELETE",
		url : url,
		success : onRemoveSuccess,
		error :onRemoveFailure,
	});
	
	return false;
}

function onRemoveSuccess() {
	location.reload();
}

function onRemoveFailure() {
	alert("failed to unregister from task/service");
}


var currTask ="";

function onEditTask() {
	$("#editTaskModal").modal();
	var clickedElement = event.currentTarget;
	var taskId = clickedElement.id;
	var url = "/HW5/Task?taskId=" + taskId;
	$.getJSON(url).success(function(data) {
		currTask = data.task;
		$("#updateTitle").val(currTask.title);
		$("#updateCapacity").val(currTask.capacity);
		$("#updateCost").val(currTask.costPerUser);
		$("#updateDesc").val(currTask.description);
//		$("#taskType").val(currTask.taskType);
	})
		.fail(function(data) {
			alert(data.error);
		});
	
	return false;
}

function onUpdateSubmit() {
	var title = $("#updateTitle").val();
	var taskId = currTask.taskId;
	var description = $("#updateDesc").val();
	var capacity = $("#updateCapacity").val();
	var cost = $("#updateCost").val();
	var distance = 1;
	
	var url = "/HW5/Task?taskId=" + taskId + "&title=" + title + "&cost=" + cost;
		url = url + "&capacity=" + capacity + "&distance=" + distance + "&description=" + description;
	$.ajax({
		type : "PUT",
		url : url,
		success : onUpdateSuccess,
		error :onUpdateFailure,
	});
	
}

function onCancelAddTask() {
	$("#taskType").val("");
	$("#title").val("");
	$("#desc").val("");
	$("#capacity").val("");
	$("#cost").val("");
}

function onUpdateSuccess() {
	location.reload();
	$("#updateModalCloseBtn")[0].click();
}

function onUpdateFailure(data) {
	alert("failed to update task/service");
}

function onEditUserToggle() {
	$("#editUserModal").modal();
	var userId = $("#uid")[0].textContent;
	var url = "/HW5/GetUser?userId=" + userId;
	$.getJSON(url).success(function(data) {
		var user = data.user;
		$("#firstName").val(user.firstName);
		$("#lastName").val(user.lastName);
		$("#address").val(user.address);
		$("#gender").val(user.gender);
		$("#phone").val(user.phone);
	})
		.fail(function(data) {
			alert(data.error);
		});
	
	return false;
}

function onEditUserSubmit() {
	var userId = $("#uid")[0].textContent;
	var firstName = $("#firstName").val();
	var lastName = $("#lastName").val();
	var address = $("#address").val();
	var gender = $("#gender").val();
	var phone = $("#phone").val();
	var url = "/HW5/UpdateUser?firstName=" + firstName + "&lastName=" + lastName;
	url = url + "&address=" + address + "&gender=" + gender + "&phone=" + phone;
	$.ajax({
		type : "POST",
		url : url,
		success : onEditUserSuccess,
		error :onEditUserFailure,
	});
}

function onEditUserSuccess() {
	location.reload();
}

function onEditUserFailure() {
	alert("failed to update user profile");
}

function onDeleteUser() {
	var url="/HW5/DeleteProfile";
	$.ajax({
		type: "POST",
		url: url,
		success: onDeleteUserSuccess,
		error: onDeleteUserFailure,
	});
	return false;
}

function onDeleteUserSuccess() {
	$("#logout")[0].click();
}

function onDeleteUserFailure() {
	alert("failed to delete profile");
}

function onDeleteTask() {
	var taskId = currTask.taskId;
	var url="/HW5/Task?taskId=" + taskId;
	$.ajax({
		type : "DELETE",
		url : url,
		success : onDeleteTaskSuccess,
		error :onDeleteTaskFailure,
	});
	return false;
}

function onDeleteTaskSuccess() {
	$("#editTaskModal").modal();
	location.reload();
}

function onDeleteTaskFailure(data) {
	alert("failed to delete task");
}

function onRemoveAll() {
	var url = "/HW5/RemoveAllTasks";
	$.ajax({
		type: "POST",
		url: url,
		success: function(){location.reload();},
		failure: onRemoveAllFailure,
	})
}

function onLeaveAll() {
	var url = "/HW5/RemoveAllTasks";
	$.ajax({
		type: "POST",
		url: url,
		success: function(){location.reload();},
		failure: onLeaveAllFailure,
	})
}

function onRemoveAllFailure() {
	alert("failed to remove all tasks and services");
}

function onLeaveAllFailure() {
	alert("failed to leave all tasks and services");
}

function onChangePass() {
	var old = $("#oldPass").val();
	var newPass = $("#newPass").val();
	var con = $("#conNewPass").val();
	if (old.indexOf(" ") > -1) {
		alert("old password cannot contain spaces");
		return;
	}
	if (newPass.indexOf(" ") > -1 || con.indexOf(" ") > -1) {
		alert("password cannot contain spaces");
		return;
	}
	if (newPass != con) {
		alert("password confirmation must be identical to the new password");
		return;
	}
	url = "/HW5/ChangePassword?old=" + old + "&pass=" + newPass;
	$.ajax({
		type: "POST",
		url: url,
		success: onChangeSuccess,
		error: onChangeFailure,
	});
	return false;
}

function onChangeSuccess() {
	alert("password changed successfully, will now log out.");
	$.ajax({
		type : "POST",
		url : "/HW5/Logout",
		success : function(){location.href="/HW5";},
		error : function(){location.href="/HW5";},
	});
	return false;
}

function onChangeFailure() {
	alert("failed to change password");
}