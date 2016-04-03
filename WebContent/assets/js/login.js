$("#loginBtn").click(onLogin);
$("#registerBtn").click(onRegister);


function onLogin() {
	var userName = $("#username").val();
	if (userName.indexOf(" ") > -1) {
		alert ("username cannot contain spaces");
		return;
	}
	var password = $("#password").val();
	if (password.indexOf(" ") > -1) {
		alert ("password cannot contain spaces");
		return;
	}
	var url = "/HW5/Login?username=" + userName + "&password=" + password; 
	$.ajax({
		type : "POST",
		url : url,
		success : onLoginSuccess,
		error :onLoginError,
	});
	return false;
}

function onRegister() {
	var userName = $("#username").val();
	if (userName.indexOf(" ") > -1) {
		alert ("username cannot contain spaces");
		return;
	}
	var password = $("#password").val();
	if (password.indexOf(" ") > -1) {
		alert ("password cannot contain spaces");
		return;
	}
	var url = "/HW5/Register?username=" + userName + "&password=" + password;
	$.ajax({
		type : "POST",
		url : url,
		success : onLoginSuccess,
		error :onRegisterError,
	});
	return false;
}

function onLoginSuccess() {
	location.href = "/HW5/user.jsp";
}

function onLoginError() {
	alert("login faild");
}

function onRegisterError() {
	location.href = "/HW5/user.jsp";
}