$(".icon-minus").click(onRemoveFriend);
$(".icon-plus").click(onAddFriend);
$(".userLink").click(onShowUser);
$("#search-btn").click(onSearch);
$(".search").keyup(onChange);

var curr = "";

function onChange() {
	   if ($(this).val() == "") {
		   location.href = "/HW5/friends.jsp";
	   } 
}

function onSearch() {
	var search = $('.search').val() + $('.search').attr('id');
	location.href = "/HW5/friends.jsp?search=" + search;
	return false;
}

function onRemoveFriend() {
	var clickedElement = event.currentTarget;
	var friendId = clickedElement.id;
	curr = clickedElement.getElementsByClassName("icon-minus")[0];
	if (!curr) {
		curr = clickedElement;
	}
	var url = "/HW5/Unfriend?friendId=" + friendId;
	$.ajax({
		type : "POST",
		url : url,
		success : onRemoveSuccess,
		error :onRemoveFailure,
	});
	
	return false;
}

function onAddFriend() {
	var clickedElement = event.currentTarget;
	var friendId = clickedElement.id;
	curr = clickedElement.getElementsByClassName("icon-plus")[0];
	if (!curr) {
		curr = clickedElement;
	}
	var url = "/HW5/AddFriend?friendId=" + friendId;
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
	location.reload();
	curr.className = "icon-minus";
	$(".icon-minus").click(onRemoveFriend);
}

function onAddFailure() {
	alert("failed to add friend");
}

function onRemoveSuccess() {
	location.reload();
	curr.className = "icon-plus";
	$(".icon-plus").click(onAddFriend);
}

function onRemoveFailure() {
	alert("failed to remove friend");
}

function onShowUser() {
	$("#userModal").modal();
}