$(".icon-minus").click(onRemoveFromTask);
$(".icon-plus").click(onRegisterToTask);
$("#search-btn").click(onSearch);
$(".search").keyup(onChange);

var curr = "";

function onChange() {
	   if ($(this).val() == "") {
		   location.href = "/HW5/tasks.jsp";
	   } 
}


function onSearch() {
	var search = $('.search').val() + $('.search').attr('id');
	location.href = "/HW5/tasks.jsp?search=" + search;
	return false;
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

function onRegisterToTask() {
	var clickedElement = event.currentTarget;
	var taskId = clickedElement.id;
	curr = clickedElement.getElementsByClassName("icon-plus")[0];
	if (!curr) {
		curr = clickedElement;
	}
	var url = "/HW5/TaskParticipateController?taskId=" + taskId;
	$.ajax({
		type : "POST",
		url : url,
		success : onAddSuccess,
		error :onAddFailure,
	});
	
	return false;
}

function onSearchSuccess() {
}

function onSearchFailure() {
	alert("failed to search");
}

function onAddSuccess() {
	window.location.reload();
	curr.className = "icon-minus";
	$(".icon-minus").click(onRemoveFromTask);
}

function onAddFailure() {
	alert("failed to register to task/service");
}

function onRemoveSuccess() {
	window.location.reload()
	curr.className = "icon-plus";
	$(".icon-plus").click(onAddFriend);
}

function onRemoveFailure() {
	alert("failed to unregister from task/service");
}