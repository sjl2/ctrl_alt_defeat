var prevEntry1 = "";
$('#multipleresults').modal({ show: false});
$('#editUser').modal({ show: false});

function suggestions(e) {
	console.log(e);
	if(e.which == 13) {
		console.log("here");
		e.preventDefault();
    	textSearch();
    	return false;
  	}
    var spot1 = $("#playerTeamSearch")[0].value;
    $.post("/dashboard/autocomplete", {spot : spot1}, function(responseJSON){
	var res = JSON.parse(responseJSON);
	console.log("res", res);
	var sug1 = document.getElementById("suggestions1");
	var sug1Html = "";				
	if (prevEntry1 != spot1) {
	    for (var i=0; i<5; i++) {
		if (res.res[i] != null)
		    sug1Html = sug1Html + "<option>" + res.res[i] + "</option>";
	    }
	    prevEntry1 = spot1;
	    sug1.innerHTML = sug1Html;
	}
    }); 

}

function setTextSearch(b) {
	if (!b) {
		$("#searchButton")[0].setAttribute("data-player", false);
		$("#searchButton")[0].innerHTML = "Team Search";
	} else {
		$("#searchButton")[0].setAttribute("data-player", true);
		$("#searchButton")[0].innerHTML = "Player Search";
	}
}

function textSearch() {
    
	isPlayer = $("#searchButton")[0].getAttribute("data-player");
	console.log(isPlayer);

    $.post("/dashboard/search", {searchString : $("#playerTeamSearch")[0].value, isPlayer : isPlayer}, function(responseJSON) {
	console.log(responseJSON);
	console.log("there");
	var res = JSON.parse(responseJSON);
	if (res.errorMessage.length > 0) {
	    //error
	} else {
	    var a;
	    if (isPlayer === 'true') a = "player";
	    else a = "team";
	    console.log("the big one: ", a);
	    if (res.list.length == 1) {
		window.location.href ="/" + a + "/view/" + res.list[0].id;
	    } else {
		for (var i=0; i<res.list.length; i++) {
		    $("#linkList").append("<a href=\"/" + a + "/view/" + res.list[i].id + "\">" + res.list[i].name + " (#" + res.list[i].number + " " + res.list[i].teamName + ")</a><br>");
		}	
		$('#multipleresults').modal('show');
	    }
	}
	

    });
}

function updateEditUsers() {
    $.get("/dashboard/get/users",
	  {},
	  function(response) {
	      var users = response.users;
	      var select = $("#usernames")[0];
	      select.innerHTML = "";
	      for(i = 0; i < users.length; i++) {
		  var option = document.createElement("option");
		  option.text = users[i];
		  select.add(option);
	      }
	      $("#newUsername").val(users[0]);
	      $("#newPassword").val("");
	  }, "json");
}

$(function() {
    $("#editForm").on("submit", function(e) {
	if($("#newPassword").val() == $("#confirmPassword").val()){
	    $.post($(this).attr("action"), $(this).serialize(),
		   function(response) {
		       if(response.errorMessage.length == 0) {
			   $("#editUser").modal("hide");
			   bootbox.alert("User updated successfully");
			   updateEditUsers();
		       } else {
			   bootbox.alert(response.errorMessage);
			   return false;
		       }
		   }, "json");
	} else {
	    bootbox.alert("Passwords don't match");
	}
	return false;
    });
    $("#usernames").on("change", function(e) {
	$("#newUsername").val($("#usernames").val());
	$("#newPassword").val("");
    });
    updateEditUsers();
});