var scoreboard = Raphael(document.getElementById("scoreboard"), 400, 200);
scoreboard.text(50, 15, "HOME").attr({"text-anchor" : "middle", "font-size" : 20});
scoreboard.text(350, 15, "AWAY").attr({"text-anchor" : "middle", "font-size" : 20});
scoreboard.text(200, 20, "PERIOD").attr({"font-size" : 17});
scoreboard.text(20, 150, "Team\nFouls").attr({"font-size" : 15});
scoreboard.text(380, 150, "Team\nFouls").attr({"font-size" : 15});
scoreboard.text(80, 150, "Timeouts").attr({"font-size" : 15});
scoreboard.text(320, 150, "Timeouts").attr({"font-size" : 15});
scoreboard.homeScore = scoreboard.text(50, 50, "00").attr({"font-size" : 40});
scoreboard.awayScore = scoreboard.text(350, 50, "00").attr({"font-size" : 40});
scoreboard.period = scoreboard.text(200, 50, "1").attr({"font-size" : 20});
scoreboard.possession = {};
scoreboard.possession.away = scoreboard.path("M180,70L220,85L180,100z");
scoreboard.possession.home = scoreboard.path("M220,70L180,85L220,100z");
scoreboard.possession.away.hide();
scoreboard.homeBonus = {};
scoreboard.homeBonus.text = scoreboard.text(0, 110, "BONUS").attr({"text-anchor" : "start", "font-size" : 15});
scoreboard.homeBonus.single = scoreboard.circle(65, 110, 4);
scoreboard.homeBonus.single = scoreboard.circle(78, 110, 4);
scoreboard.awayBonus = {};
scoreboard.awayBonus.text = scoreboard.text(400, 110, "BONUS").attr({"text-anchor" : "end", "font-size" : 15});
scoreboard.awayBonus.single = scoreboard.circle(335, 110, 4);
scoreboard.awayBonus.single = scoreboard.circle(322, 110, 4);
scoreboard.homeFouls = scoreboard.text(20, 180, "0").attr({"font-size" : 20});
scoreboard.awayFouls = scoreboard.text(380, 180, "0").attr({"font-size" : 20});
scoreboard.homeTimeouts = scoreboard.text(80, 180, "??").attr({"font-size" : 20});
scoreboard.awayTimeouts = scoreboard.text(320, 180, "??").attr({"font-size" : 20});
scoreboard.noGameInProgress = {};
scoreboard.noGameInProgress.box = scoreboard.rect(25, 25, 350, 150).attr({fill : "gray"});
scoreboard.noGameInProgress.text = scoreboard.text(200, 100, "No Game\n In Progress!").attr({"font-size" : 50});

$.get("/dashboard/getgame", {}, function(responseJSON) {
	console.log(responseJSON);
	var res = JSON.parse(responseJSON);
	console.log(res);
	if (res.isGame) {
		scoreboard.noGameInProgress.box.hide();
		scoreboard.noGameInProgress.text.hide();
		var g = res.game;
		scoreboard.homeTimeouts.attr({text : g.homeTimeouts});
		scoreboard.awayTimeouts.attr({text : g.awayTimeouts});
	}
});



function newGame() {
	var postParameters = {
		opponent: $("#opponent")[0].value,
		is_home: $("#is_home")[0].checked
	};

	$.post("/game/start", postParameters, function(responseJSON){
		//responseJSON should have who we're playing(maybe it'll have the rosters)
		
		if (responseJSON.length > 0) {
			alert(responseJSON); 
		} else {
			alert("Game Started!");
		}
	});
}