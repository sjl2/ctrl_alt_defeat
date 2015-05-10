var prevEntry1 = "";
$('#multipleresults').modal({ show: false});
$('#editUser').modal({ show: false});
$('.new-player').modal({ show: false});
$('.new-team').modal({ show: false});


$("#make-new-player").on("click", function (e) {
    $.get("/dashboard/get/teams", {}, function(res) {
	var teams = res.teams;
	$("#select-team").html("");
	$.each(teams, function(index, value) {
	    $("#select-team").
		append($("<option></option>")
		       .val(value.id)
		       .text(value.text));
	});
    }, "json");
    $(".new-player").modal('show');
});

$("#make-new-team").on("click", function (e) {
    $(".new-team").modal('show');
});

$('#player-form').submit(function() {
    $.post('/dashboard/new/player', $('#player-form').serialize(), function () {
	bootbox.alert("Player created!", function () {
	    $(".new-player").modal('hide');
	});
    }); 
    return false;
});

$('#team-form').submit(function() {
    $.post('/dashboard/new/team', $('#team-form').serialize(), function () {
	bootbox.alert("Team created!", function () {
	    $(".new-team").modal('hide');
	});
    }); 
    return false;
});

$("#logoutButton").on("click", function() {
    $.post("/login/logout", {}, function(res) {
	window.location.href = "/login";
    });
});

function doNothing(e) {
	if (e.which == 13) e.preventDefault();
}

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

function textSearch() {

    $.post("/dashboard/search", {searchString : $("#playerTeamSearch")[0].value}, function(responseJSON) {
	console.log(responseJSON);
	var res = JSON.parse(responseJSON);
	console.log(res);
	if (res.errorMessage.length > 0) {
	    bootbox.alert(res.errorMessage);
	    console.log("here");
	} else {
		if (res.players.length == 0) {
			if (res.teams.length == 1) {
				window.location.href = "/team/view/" + res.teams[0].id;
			} else {
				for (var i=0; i<res.teams.length; i++) {
		    		$("#linkList").append("<a href=\"/team/view/" + res.teams[i].id + "\">" + res.teams[i].name  + ")</a><br>");
				}	
				$('#multipleresults').modal('show');
			}
		} else if (res.teams.length == 0) {
			if (res.players.length == 1) {
				window.location.href = "/player/view/" + res.players[0].id;				
			} else {
				for (var i=0; i<res.players.length; i++) {
		    		$("#linkList").append("<a href=\"/" + "player" + "/view/" + res.players[i].id + "\">" + res.players[i].name + " (#" + res.players[i].number + " " + res.players[i].teamName + ")</a><br>");
				}
				$('#multipleresults').modal('show');
			}
		} else {
			for (var i=0; i<res.teams.length; i++) {
		    		$("#linkList").append("<a href=\"/team/view/" + res.teams[i].id + "\">" + res.teams[i].name  + ")</a><br>");
			}
			for (var i=0; i<res.players.length; i++) {
		    		$("#linkList").append("<a href=\"/" + "player" + "/view/" + res.players[i].id + "\">" + res.players[i].name + " (#" + res.players[i].number + " " + res.players[i].teamName + ")</a><br>");
			}
			$('#multipleresults').modal('show');

		}
	}
    });
}

function updateEditUsers() {
    $.get("/users/get",
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
