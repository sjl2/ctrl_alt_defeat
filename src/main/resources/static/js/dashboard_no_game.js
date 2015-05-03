$('.new-game').modal({ show: false});

$("#opponent").on("change", function() {
	postParameters = { teamID: $("#opponent").val() };
	$.get("/dashboard/opponent/get", postParameters, function(html) {
		console.log(html);
		$("#opponent-lineup")[0].innerHTML = html;
	})
});

$("#createGame").on("click", function() {
	var opponent = $("#opponent")[0].value;
	var isHome = $("#is_home")[0].selected;
	var postPars = {
	    opponent: opponent,
	    isHome: isHome
	};

	if(isHome) {
	    postPars.hpg = $("#myStarter0")[0].options[$("#myStarter0")[0].selectedIndex].id.substring(6);
	    postPars.hsg = $("#myStarter1")[0].options[$("#myStarter1")[0].selectedIndex].id.substring(6);
	    postPars.hsf = $("#myStarter2")[0].options[$("#myStarter2")[0].selectedIndex].id.substring(6);
	    postPars.hpf = $("#myStarter3")[0].options[$("#myStarter3")[0].selectedIndex].id.substring(6);
	    postPars.hc = $("#myStarter4")[0].options[$("#myStarter4")[0].selectedIndex].id.substring(6);
	    postPars.apg = $("#oppStarter0")[0].options[$("#oppStarter0")[0].selectedIndex].id.substring(6);
	    postPars.asg = $("#oppStarter1")[0].options[$("#oppStarter1")[0].selectedIndex].id.substring(6);
	    postPars.asf = $("#oppStarter2")[0].options[$("#oppStarter2")[0].selectedIndex].id.substring(6);
	    postPars.apf = $("#oppStarter3")[0].options[$("#oppStarter3")[0].selectedIndex].id.substring(6);
	    postPars.ac = $("#oppStarter4")[0].options[$("#oppStarter4")[0].selectedIndex].id.substring(6);
	} else {
	    postPars.hpg = $("#oppStarter0")[0].options[$("#oppStarter0")[0].selectedIndex].id.substring(6);
	    postPars.hsg = $("#oppStarter1")[0].options[$("#oppStarter1")[0].selectedIndex].id.substring(6);
	    postPars.hsf = $("#oppStarter2")[0].options[$("#oppStarter2")[0].selectedIndex].id.substring(6);
	    postPars.hpf = $("#oppStarter3")[0].options[$("#oppStarter3")[0].selectedIndex].id.substring(6);
	    postPars.hc = $("#oppStarter4")[0].options[$("#oppStarter4")[0].selectedIndex].id.substring(6);
	    postPars.apg = $("#myStarter0")[0].options[$("#myStarter0")[0].selectedIndex].id.substring(6);
	    postPars.asg = $("#myStarter1")[0].options[$("#myStarter1")[0].selectedIndex].id.substring(6);
	    postPars.asf = $("#myStarter2")[0].options[$("#myStarter2")[0].selectedIndex].id.substring(6);
	    postPars.apf = $("#myStarter3")[0].options[$("#myStarter3")[0].selectedIndex].id.substring(6);
	    postPars.ac = $("#myStarter4")[0].options[$("#myStarter4")[0].selectedIndex].id.substring(6);
	}

	$.post("/game/start", postPars, function(responseJSON){
	    //responseJSON should have who we're playing(maybe it'll have the rosters)

    if (responseJSON.length > 0) {
    	console.log(responseJSON);
			bootstrap.alert(responseJSON, function () {}); 
    } else {
 			bootbox.alert("Game Started!", function () {
 				window.location.href = "/dashboard";
 			});
    }
	});

});

$("#start-game").on("click", function (e) {
	console.log("starting")
	$(".new-game").modal('show');
});