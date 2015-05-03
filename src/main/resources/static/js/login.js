$(function() {

    $("#loginForm").on("submit", function() {
	$.post($(this).attr("action"),
	       $(this).serialize(),
	       function(data) {
		   if(data.clearance == -1) {
		       bootbox.alert("Invalid username or password");
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
	return false;
    });

});