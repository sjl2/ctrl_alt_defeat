var scoreboard = Raphael(document.getElementById("scoreboard"), 400, 200);
scoreboard.isGame = false;
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
scoreboard.possession.home.hide();

scoreboard.homeBonus = {};
scoreboard.homeBonus.text = scoreboard.text(0, 110, "BONUS").attr({"text-anchor" : "start", "font-size" : 15});
scoreboard.homeBonus.singleBonus = scoreboard.circle(65, 110, 4);
scoreboard.homeBonus.doubleBonus = scoreboard.circle(78, 110, 4);
scoreboard.awayBonus = {};
scoreboard.awayBonus.text = scoreboard.text(400, 110, "BONUS").attr({"text-anchor" : "end", "font-size" : 15});
scoreboard.awayBonus.singleBonus = scoreboard.circle(335, 110, 4);
scoreboard.awayBonus.doubleBonus = scoreboard.circle(322, 110, 4);

scoreboard.homeFouls = scoreboard.text(20, 180, "0").attr({"font-size" : 20});
scoreboard.awayFouls = scoreboard.text(380, 180, "0").attr({"font-size" : 20});

scoreboard.homeTimeouts = scoreboard.text(80, 180, "??").attr({"font-size" : 20});
scoreboard.awayTimeouts = scoreboard.text(320, 180, "??").attr({"font-size" : 20});

scoreboard.noGameInProgress = {};
scoreboard.noGameInProgress.box = scoreboard.rect(25, 25, 350, 150).attr({fill : "gray"});
scoreboard.noGameInProgress.text = scoreboard.text(200, 100, "Click To Start\na New Game!").attr({"font-size" : 50})
	.click(function (e) {
		window.location.href = "/dashboard/new/game";
	});

$.get("/dashboard/getgame", {}, function(responseJSON) {
	console.log(responseJSON);
	var res = JSON.parse(responseJSON);
	console.log(res);
	if (res.isGame) {
		scoreboard.isGame = true;
		scoreboard.noGameInProgress.box.hide();
		scoreboard.noGameInProgress.text.hide();
		scoreboard.homeTimeouts.attr({text : res.timeouts});
		scoreboard.awayTimeouts.attr({text : res.timeouts});

		window.setInterval(updateGame, 5000);
		
	}
});


function updateGame() {
	console.log("OMG");
	$.get("/dashboard/updategame", {}, function(responseJSON) {
		var res = JSON.parse(responseJSON);
		scoreboard.homeScore.attr({text : res.homeScore});
		scoreboard.awayScore.attr({text : res.awayScore});
		scoreboard.period.attr({text : res.period});
		if (res.possession) {
			scoreboard.possession.away.hide();
			scoreboard.possession.home.show();
		} else {
			scoreboard.possession.home.hide();
			scoreboard.possession.away.show();
		}
		if (res.homeBonus) scoreboard.homeBonus.singleBonus.attr({fill : "yellow"});
		else scoreboard.homeBonus.singleBonus.attr({fill : "white"});
		if (res.homeDoubleBonus) scoreboard.homeBonus.doubleBonus.attr({fill : "yellow"});
		else scoreboard.homeBonus.doubleBonus.attr({fill : "white"});
		if (res.awayBonus) scoreboard.awayBonus.singleBonus.attr({fill : "yellow"});
		else scoreboard.awayBonus.singleBonus.attr({fill : "white"});
		if (res.awayDoubleBonus) scoreboard.awayBonus.doubleBonus.attr({fill : "yellow"});
		else scoreboard.awayBonus.doubleBonus.attr({fill : "white"});
		scoreboard.homeFouls.attr({text : res.homeFouls});
		scoreboard.awayFouls.attr({text : res.awayFouls});
		scoreboard.homeTimeouts.attr({text : res.homeTimeouts});
		scoreboard.awayTimeouts.attr({text : res.awayTimeouts})
	});
}


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