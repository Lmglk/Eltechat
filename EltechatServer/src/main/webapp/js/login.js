$(document).ready(function() {
	$('#loginForm').submit(function(event) {
		event.preventDefault();
		var name = $('#inputName').val().trim();
		if (name.length > 0) {
			//document.cookie = "userName=" + name;
			sessionStorage.setItem('username', name);
			document.location.href = "chat.html";
		}
		else
			alert('Empty message');
	});
});