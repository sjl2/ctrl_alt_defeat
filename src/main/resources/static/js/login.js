$(function() {

    $("#submit").on("click", function() {
	var username = $("#username").val();
	var password = $("#password").val();
	if(username == "" || password == "") {
	    alert("Please fill in your username and password");
	    return;
	}
	var postParams = {
	    username: username,
	    password: password
	};
	$.post("/login/login",
	       postParams,
	       function(data) {
		   if(data.clearance == -1) {
		       $("#message")[0].innerHTML = "Invalid username or password";
		   } else if(data.clearance == 0) {
		       //Read-only user
		   } else if(data.clearance == 1) {
		       window.location.href = "/stats";
		   } else if(data.clearance == 2) {
		       window.location.href = "/dashboard";
		   } else {
		       console.log("Invalid clearance value");
		   }
	       },
	       "json");
    });

});