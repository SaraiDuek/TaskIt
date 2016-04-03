$("#querySubmit").click(onQuerySubmit);

dist="";
function onQuerySubmit() {
	var query = $("#query").val();
	if (isNaN(query)) {
		alert("please enter a positive integer");
		return;
	}
	if (query < 1) {
		alert("please enter a positive integer");
		return;
	}
	var user = $("#user")[0].textContent;
	var url="/HW5/search/u/" + user + "?maxDist="  + query;
	dist = query;
	$.ajax({
		type : "GET",
		url : url,
		success : onQuerySuccess,
		error : onQueryFailure,
	});
	return false;
}

function onQuerySuccess(data) {
	resultElement = $("#result")[0];
	result = ""
	for (i = 1; i <= dist; i++) {
		curr = "dist_" + i;
		result = result + "[" + JSON.stringify(data[curr]) + "]";
	};
	result = "{" + result + "}";
	resultElement.textContent = result;
}

function onQueryFailure() {
	alert("failed to get query results");
}