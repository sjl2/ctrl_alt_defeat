function startScoreboard(width, height) {
    var scoreboardDiv = $("#scoreboard");
    var w = width;
    var h = height;
    scoreboardDiv.height(h);
    var scoreboard = Raphael(document.getElementById("scoreboard"), w, h);
    scoreboard.rect(0, 0, w, h).attr({fill: "#fff", r:8});
    
    scoreboard.text(w * 3 / 16, h / 8, "HOME").attr("font-size", w / 18);
    scoreboard.text(w * 13 / 16, h / 8, "AWAY").attr("font-size", w / 18);

    scoreboard.homeScore = scoreboard.text(w * 3 / 16, h * 9 / 32, "00").attr("font-size", w / 9);
    scoreboard.awayScore = scoreboard.text(w * 13 / 16, h * 9 / 32, "00").attr("font-size", w / 9);

    scoreboard.text(w / 2, h * 5 / 32, "PERIOD").attr("font-size", w * 17 / 360);

    scoreboard.period = scoreboard.text(w / 2, h * 9 / 32, "1").attr("font-size", w / 18);

    scoreboard.text(w / 8, h * 3 / 4, "Team Fouls").attr("font-size", w / 24);
    scoreboard.text(w * 7 / 8, h * 3 / 4, "Team Fouls").attr("font-size", w / 24);

    scoreboard.homeFouls = scoreboard.text(w / 8, h * 18 / 20, "0").attr("font-size", w / 18);
    scoreboard.awayFouls = scoreboard.text(w * 7 / 8, h * 18 / 20, "0").attr("font-size", w / 18);

    scoreboard.text(w * 7 / 20, h * 3 / 4, "Timeouts").attr("font-size", w / 24);
    scoreboard.text(w * 13 / 20, h * 3 / 4, "Timeouts").attr("font-size", w / 24);

    scoreboard.homeTimeouts = scoreboard.text(w * 7 / 20, h * 9 / 10, "??").attr("font-size", w / 18);
    scoreboard.awayTimeouts = scoreboard.text(w * 13 / 20, h * 9 / 10, "??").attr("font-size", w / 18);


    scoreboard.possession = {};
    scoreboard.possession.away = scoreboard.path("M" + w*17/40 + "," + h*2/5 +
						 "L" + w*23/40 + "," + h/2 +
						 "L" + w*17/40 + "," + h*3/5 + "z");
    scoreboard.possession.home = scoreboard.path("M" + w*23/40 + "," + h*2/5 +
						 "L" + w*17/40 + "," + h/2 +
						 "L" + w*23/40 + "," + h*3/5 + "z");
    scoreboard.possession.home.hide();
    scoreboard.possession.away.hide();

    scoreboard.homeBonus = {};
    scoreboard.homeBonus.text = scoreboard.text(w * 3 / 20, h / 2, "BONUS").attr("font-size", w * 17 / 360);
    scoreboard.homeBonus.singleBonus = scoreboard.circle(w / 4, h / 2, 4);
    scoreboard.homeBonus.doubleBonus = scoreboard.circle(w * 9 / 32, h / 2, 4);
    scoreboard.awayBonus = {};
    scoreboard.awayBonus.text = scoreboard.text(w * 17 / 20, h / 2, "BONUS").attr("font-size", w * 17 / 360);
    scoreboard.awayBonus.singleBonus = scoreboard.circle(w * 3 / 4, h / 2, 4);
    scoreboard.awayBonus.doubleBonus = scoreboard.circle(w * 23 / 32, h / 2, 4);

    scoreboard.noGameBox = scoreboard.rect(0, 0, w, h).attr({fill: "#bbb", r:8});
    scoreboard.noGameText = scoreboard.text(w / 2, h / 2, "No Game").attr({"font-size": w / 12});

    scoreboard.isGame = false;

    window.setInterval(updateScoreboard, 2000);
    updateScoreboard();

    function updateScoreboard() {
	$.get("/scoreboard", {}, function(responseJSON) {
	    var res = JSON.parse(responseJSON);
	    scoreboard.isGame = res.isGame;
	    if(scoreboard.isGame) {
		scoreboard.noGameBox.hide();
		scoreboard.noGameText.hide();
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
		if (res.awayBonus) scoreboard.homeBonus.singleBonus.attr({fill : "red"});
		else scoreboard.homeBonus.singleBonus.attr({fill : "white"});
		if (res.awayDoubleBonus) scoreboard.homeBonus.doubleBonus.attr({fill : "red"});
		else scoreboard.homeBonus.doubleBonus.attr({fill : "white"});
		if (res.homeBonus) scoreboard.awayBonus.singleBonus.attr({fill : "red"});
		else scoreboard.awayBonus.singleBonus.attr({fill : "white"});
		if (res.homeDoubleBonus) scoreboard.awayBonus.doubleBonus.attr({fill : "red"});
		else scoreboard.awayBonus.doubleBonus.attr({fill : "white"});
		scoreboard.homeFouls.attr({text : res.homeFouls});
		scoreboard.awayFouls.attr({text : res.awayFouls});
		scoreboard.homeTimeouts.attr({text : res.homeTimeouts});
		scoreboard.awayTimeouts.attr({text : res.awayTimeouts});
	    } else {
		scoreboard.noGameBox.show();
		scoreboard.noGameText.show();
	    }
	});
    }
}