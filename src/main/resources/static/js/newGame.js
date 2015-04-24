$(function() {
    
    $("#opponent").on("change", function() {
	var select = $("#opponent")[0];
	var selectedIndex = select.selectedIndex;
	var options = select.options;
	var selectedID = options[selectedIndex].id;
	populateOpponentPlayers(parseInt(selectedID.substring(4)));
    });
    $("#opponent").change();

    function populateOpponentPlayers(teamID) {
	function listOpponentPlayers(players) {
	    var playerList = players.playerList;
	    for(i = 0; i < 5; i++) {
		var select = $("#oppStarter" + i)[0];
		select.innerHTML = "";
		for(j = 0; j < playerList.length; j++) {
		    var option = document.createElement("option");
		    option.id = "player" + playerList[j].id;
		    option.text = playerList[j].name;
		    select.add(option);
		}
	    }
	}

	$.get("/dashboard/getPlayers",
	      {teamID: teamID},
	      listOpponentPlayers,
	      "json");
    }

    $("#createGame").on("click", function() {
	var select = $("#opponent")[0];
	var selectedIndex = select.selectedIndex;
	var options = select.options;
	var selectedID = options[selectedIndex].id.substring(4);

	var isHome = $("#is_home")[0].checked;
	var postPars = {
	    opponent: selectedID,
	    isHome: isHome,
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
		alert(responseJSON); 
	    } else {
		alert("Game Started!");
	    }
	});
    });
    
});