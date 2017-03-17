$(document).ready(function() {
	$('#loginForm').submit(function(event) {
		event.preventDefault();
		var name = $('#inputName').val().trim();
		var password = md5($('#inputPassword').val().trim());
		if ((name.length > 0) && (password.length > 0)) {
			sessionStorage.setItem('username', name);
			sessionStorage.setItem('password', password);
			document.location.href = "chat.html";
		}
		else
			alert('Empty message');
	});
});