$("#querySubmit").click(onQuerySubmit);

function onQuerySubmit() {
	var query = $("#query").val();
	var url="/HW5/RDF?query=" + query;
	$.ajax({
		type : "POST",
		url : url,
		success : onQuerySuccess,
		error : onQueryFailure,
	});
	return false;
}

function onQuerySuccess(data) {
	resultElement = $("#result")[0];
	resultElement.textContent = data.rdfResult;
}

function onQueryFailure() {
	alert("failed to get query results");
}