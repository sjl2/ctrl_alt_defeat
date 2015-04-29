var prevEntry1 = "";
$('#multipleresults').modal({ show: false});

function suggestions() {
    var spot1 = $("#playerTeamSearch")[0].value;
    $.post("/dashboard/autocomplete", {spot : spot1}, function(responseJSON){
	var res = JSON.parse(responseJSON);

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

function textSearch(isPlayer) {
    console.log("here");
    $.post("/dashboard/search", {searchString : $("#playerTeamSearch")[0].value, isPlayer : isPlayer}, function(responseJSON) {
	console.log(responseJSON);
	console.log("there");
	var res = JSON.parse(responseJSON);
	if (res.errorMessage.length > 0) {
	    //error
	} else {
	    var a;
	    if (isPlayer) a = "player";
	    else a = "team";
	    console.log(res.list);
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
//window.location.href