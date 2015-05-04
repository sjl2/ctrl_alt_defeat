var prevEntry1 = "";
$('#multipleresults').modal({ show: false});

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
	    //error
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
		    		$("#linkList").append("<a href=\"/" + a + "/view/" + res.players[i].id + "\">" + res.players[i].name + " (#" + res.players[i].number + " " + res.players[i].teamName + ")</a><br>");
				}
				$('#multipleresults').modal('show');
			}
		} else {
			for (var i=0; i<res.teams.length; i++) {
		    		$("#linkList").append("<a href=\"/team/view/" + res.teams[i].id + "\">" + res.teams[i].name  + ")</a><br>");
			}
			for (var i=0; i<res.players.length; i++) {
		    		$("#linkList").append("<a href=\"/" + a + "/view/" + res.players[i].id + "\">" + res.players[i].name + " (#" + res.players[i].number + " " + res.players[i].teamName + ")</a><br>");
			}
			$('#multipleresults').modal('show');

		}
	}
    });
}
//window.location.href