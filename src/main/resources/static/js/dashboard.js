var currentPlayers = {};
currentPlayers["PG"] = undefined;
currentPlayers["SG"] = undefined;
currentPlayers["SF"] = undefined;
currentPlayers["PF"] = undefined;
currentPlayers["C"] = undefined;

$('.new-game').modal({ show: false});

var scoreboard = Raphael(document.getElementById("scoreboard"), 400, 200);
var statTicker = Raphael(document.getElementById("statTicker"), 200, 90);
var shotChart = Raphael(document.getElementById("shotChart"), 300, 280);
shotChart.makes = shotChart.set();
shotChart.misses = shotChart.set();
shotChart.curr = undefined;
shotChart.image("images/Basketball-Court-half.png", 0, 0, 15 * 20, 14 * 20)


statTicker.rect(0,0,200,90).attr({fill : "white"});
statTicker.words = statTicker.text(100, 15, "N/A");
statTicker.ourBar = statTicker.rect(0,30, 0, 25).attr({fill : "red"});
statTicker.ourBar.words = statTicker.text(0, 45, "N/A").attr({"text-anchor" : "start"});
statTicker.theirBar = statTicker.rect(0, 60, 0, 25).attr({fill : "blue"});
statTicker.theirBar.words = statTicker.text(0, 75, "N/A").attr({"text-anchor" : "start"});

statTicker.curr = 0;
statTable = {};
initStatTable();
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
scoreboard.noGameInProgress.text = scoreboard.text(200, 100, "Click To Start\na New Game!").attr({"font-size" : 45})
	.click(function (e) {
		$(".new-game").modal('show');
		//window.location.href = "/dashboard/new/game";
	});

$.get("/dashboard/getgame", {}, function(responseJSON) {
	var res = JSON.parse(responseJSON);
	if (res.isGame) {
		scoreboard.isGame = true;
		scoreboard.noGameInProgress.box.hide();
		scoreboard.noGameInProgress.text.hide();
		scoreboard.homeTimeouts.attr({text : res.timeouts});
		scoreboard.awayTimeouts.attr({text : res.timeouts});
		initStatTable();
		window.setInterval(displayTicker, 2000);
		window.setInterval(updateGame, 2000);
		
	}
});

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
			alert(responseJSON); 
    } else {
    	console.log(responseJSON);
 			alert("Game Started!");
			window.location.href = "/dashboard";
    }
	});
});

function updateGame() {
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
		scoreboard.awayTimeouts.attr({text : res.awayTimeouts});

		updateStats("PG", res.pgStats);
		updateStats("SG", res.sgStats);
		updateStats("SF", res.sfStats);
		updateStats("PF", res.pfStats);
		updateStats("C", res.cStats);

		document.getElementById("pgList").innerHTML = (res.pgStats.player.name);
		currentPlayers["PG"] = res.pgStats.player;
		document.getElementById("sgList").innerHTML = (res.sgStats.player.name);
		currentPlayers["SG"] = res.sgStats.player;
		document.getElementById("sfList").innerHTML = (res.sfStats.player.name);
		currentPlayers["SF"] = res.sfStats.player;
		document.getElementById("pfList").innerHTML = (res.pfStats.player.name);
		currentPlayers["PF"] = res.pfStats.player;
		document.getElementById("cList").innerHTML = (res.cStats.player.name);
		currentPlayers["C"] = res.cStats.player;

		console.log("res ", res);
		if (res.ourFGAttempted == 0) statTable["fieldGoalPercentage"].us = 0;
		else statTable["fieldGoalPercentage"].us = res.ourFGMade / res.ourFGAttempted;
		if (res.our3ptAttempted == 0) statTable["threePointPercentage"].us = 0;
		else statTable["threePointPercentage"].us = res.our3ptMade / res.our3ptAttempted;
		if (res.ourFTAttempted == 0) statTable["freeThrowPercentage"].us = 0;
		else statTable["freeThrowPercentage"].us = res.ourFTMade / res.ourFTAttempted;
		statTable["steals"].us = res.ourSteals;
		statTable["blocks"].us = res.ourBlocks;
		statTable["rebounds"].us = res.ourRebounds;
		statTable["assists"].us = res.ourAssists;
		statTable["turnovers"].us = res.ourTurnovers;
		statTable["fieldGoalMade"].us = res.ourFGMade;
		statTable["fieldGoalAttempted"].us = res.ourFGAttempted;
		statTable["threePointMade"].us = res.our3ptMade;
		statTable["threePointAttempted"].us = res.our3ptAttempted;
		statTable["freeThrowMade"].us = res.ourFTMade;
		statTable["freeThrowAttempted"].us = res.ourFTAttempted;

		if (res.theirFGAttempted == 0) statTable["fieldGoalPercentage"].them = 0;
		else statTable["fieldGoalPercentage"].them = res.theirFGMade / res.theirFGAttempted;
		if (res.their3ptAttempted == 0) statTable["threePointPercentage"].them = 0;
		else statTable["threePointPercentage"].them = res.their3ptMade / res.their3ptAttempted;
		if (res.theirFTAttempted == 0) statTable["freeThrowPercentage"].them = 0;
		else statTable["freeThrowPercentage"].them = res.theirFTMade / res.theirFTAttempted;
		statTable["steals"].them = res.theirSteals;
		statTable["blocks"].them = res.theirBlocks;
		statTable["rebounds"].them = res.theirRebounds;
		statTable["assists"].them = res.theirAssists;
		statTable["turnovers"].them = res.theirTurnovers;
		statTable["fieldGoalMade"].them = res.theirFGMade;
		statTable["fieldGoalAttempted"].them = res.theirFGAttempted;
		statTable["threePointMade"].them = res.their3ptMade;
		statTable["threePointAttempted"].them = res.their3ptAttempted;
		statTable["freeThrowMade"].them = res.theirFTMade;
		statTable["freeThrowAttempted"].them = res.theirFTAttempted;

		if (shotChart.curr !== undefined) shotchart(shotChart.curr);

	});
}

function updateStats(str, pStats) {
	var nameNumber = pStats.player.name + " #" + pStats.player.number;
	var points = 0;
	var rebounds = 0;
	var assists = 0;
	var fouls = 0;
	var turnovers = 0;
	for (var i=0; i<pStats.stats.length; i++) {
		if (pStats.stats[i] == "ThreePM") points += 3;
		else if (pStats.stats[i] == "TwoPM") points += 2;
		else if (pStats.stats[i] == "FTM") points += 1;
		else if (pStats.stats[i] == "ORB") rebounds += 1;
		else if (pStats.stats[i] == "DRB") rebounds += 1;
		else if (pStats.stats[i] == "AST") assists += 1;
		else if (pStats.stats[i] == "DF") fouls += 1;
		else if (pStats.stats[i] == "OF") fouls += 1;
		else if (pStats.stats[i] == "TOV") turnovers += 1;

	}
	var div = document.getElementById(str);
	var htmlString = "<p>" + nameNumber + "</p>" + 
			 "<p> Pts: " + points + "</p>" +
			 "<p> Reb: " + rebounds + "</p>" +
			 "<p> Ast: " + assists + "</p>" + 
			 "<p";
	if (fouls > 4) htmlString += " style = color:red"
	htmlString += "> PF: " + fouls + "</p>" +
			"<p> TO: " + turnovers + "</p>"
	$("#" + str).html(htmlString);
}

function initStatTable() {
	statTable.items = ["fieldGoalPercentage", "threePointPercentage", "freeThrowPercentage", "steals", "blocks", 
		"rebounds", "assists", "turnovers"];
	statTable["fieldGoalPercentage"] = {};
	statTable["fieldGoalPercentage"].us = 0;
	statTable["fieldGoalPercentage"].them = 0;

	statTable["threePointPercentage"] = {};
	statTable["threePointPercentage"].us = 0;
	statTable["threePointPercentage"].them = 0;

	statTable["freeThrowPercentage"] = {};
	statTable["freeThrowPercentage"].us = 0;
	statTable["freeThrowPercentage"].them = 0;

	statTable["steals"] = {};
	statTable["steals"].us = 0;
	statTable["steals"].them = 0;

	statTable["blocks"] = {};
	statTable["blocks"].us = 0;
	statTable["blocks"].them = 0;

	statTable["rebounds"] = {};
	statTable["rebounds"].us = 0;
	statTable["rebounds"].them = 0;

	statTable["assists"] = {};
	statTable["assists"].us = 0;
	statTable["assists"].them = 0;

	statTable["turnovers"] = {};
	statTable["turnovers"].us = 0;
	statTable["turnovers"].them = 0;

	statTable["fieldGoalMade"] = {};
	statTable["fieldGoalMade"].us = 0;
	statTable["fieldGoalMade"].them = 0;

	statTable["fieldGoalAttempted"] = {};
	statTable["fieldGoalAttempted"].us = 0;
	statTable["fieldGoalAttempted"].them = 0;

	statTable["threePointMade"] = {};
	statTable["threePointMade"].us = 0;
	statTable["threePointMade"].them = 0;

	statTable["threePointAttempted"] = {};
	statTable["threePointAttempted"].us = 0;
	statTable["threePointAttempted"].them = 0;

	statTable["freeThrowMade"] = {};
	statTable["freeThrowMade"].us = 0;
	statTable["freeThrowMade"].them = 0;

	statTable["freeThrowAttempted"] = {};
	statTable["freeThrowAttempted"].us = 0;
	statTable["freeThrowAttempted"].them = 0;
}

function displayTicker() {
	var next = statTable.items[statTicker.curr];
	statTicker.words.attr({text : next});
	console.log(next);
	console.log(Math.max(statTable[next].us, statTable[next].them));
	if (next == "fieldGoalPercentage" || next == "threePointPercentage" || next == "freeThrowPercentage") {
		var usPortion = statTable[next].us;
		var themPortion = statTable[next].them;
	} else if(Math.max(statTable[next].us, statTable[next].them) == 0) {
		var usPortion = 0;
		var themPortion = 0;
	} else {
		var usPortion = statTable[next].us / ((statTable[next].us + statTable[next].them));
		var themPortion = statTable[next].them / ((statTable[next].us + statTable[next].them));
	}

	var usPortion;
	var themPortion;
	var usLabel;
	var themLabel;

	if (next == "fieldGoalPercentage") {
		usPortion = statTable[next].us;
		themPortion = statTable[next].them;
		usLabel = statTable["fieldGoalMade"].us + "-" + statTable["fieldGoalAttempted"].us;
		themLabel = statTable["fieldGoalMade"].them + "-" + statTable["fieldGoalAttempted"].them;
	} else if (next == "threePointPercentage") {
		usPortion = statTable[next].us;
		themPortion = statTable[next].them;
		usLabel = statTable["threePointMade"].us + "-" + statTable["threePointAttempted"].us;
		themLabel = statTable["threePointMade"].them + "-" + statTable["threePointAttempted"].them;
	} else if (next == "freeThrowPercentage") {
		usPortion = statTable[next].us;
		themPortion = statTable[next].them;
		usLabel = statTable["freeThrowMade"].us + "-" + statTable["freeThrowAttempted"].us;
		themLabel = statTable["freeThrowMade"].them + "-" + statTable["freeThrowAttempted"].them;
	} else {
		if (Math.max(statTable[next].us, statTable[next].them) == 0) {
			usPortion = 0;
			themPortion = 0;
			usLabel = 0;
			themLabel = 0;
		} else {
			var usPortion = statTable[next].us / ((statTable[next].us + statTable[next].them));
			var themPortion = statTable[next].them / ((statTable[next].us + statTable[next].them));
			usLabel = statTable[next].us;
			themLabel = statTable[next].them;
		}
	}

	var ourAnim = Raphael.animation({width : usPortion * 150}, 200);
	var theirAnim = Raphael.animation({width : themPortion * 150}, 200);
	statTicker.ourBar.words.hide();
	statTicker.theirBar.words.hide();
	statTicker.ourBar.animate(ourAnim, 200);
	statTicker.theirBar.animate(theirAnim, 200);
	statTicker.ourBar.words.attr({x : usPortion * 150 + 3, text : usLabel});
	statTicker.theirBar.words.attr({x : themPortion * 150 + 3, text : themLabel});
	statTicker.ourBar.words.show();
	statTicker.theirBar.words.show();
	if (statTicker.curr == 7) statTicker.curr = 0;
	else statTicker.curr += 1;
}

function makeObjectAroundPoint(b, x, y) {
	var centerX = x * shotChart.width;
	var centerY = y * shotChart.height;
	if (b) {
		return shotChart.circle(centerX, centerY, 4).attr({stroke : "green", "stroke-width" : 2});
	} else {
		return shotChart.path("M" + (centerX - 3) + "," + (centerY - 3) + "L" + (centerX + 3) + "," + (centerY + 3) +
			"M" + (centerX + 3) + "," + (centerY - 3) + "L" + (centerX - 3) + "," + (centerY + 3)).attr({"stroke" : "red", "stroke-width" : 2});
	}
}

function shotchart(arg) {
	shotChart.curr = arg;
	var postParams = {};
	postParams.currentGame = true;
	if (arg == 'us') {
		postParams.us = true;
		postParams.player = false;
		document.getElementById("shotChartTitle").innerHTML = ("Shot Chart for Team");

	} else if (arg == 'them') {
		postParams.us = false;
		postParams.player = false;
		document.getElementById("shotChartTitle").innerHTML = ("Shot Chart for Opponent");

	} else if (arg == 'pg') {
		postParams.player = true;
		postParams.id = currentPlayers["PG"].id;
		document.getElementById("shotChartTitle").innerHTML = ("Shot Chart for " + currentPlayers["PG"].name);

	} else if (arg == 'sg') {
		postParams.player = true;
		postParams.id = currentPlayers["SG"].id;
		document.getElementById("shotChartTitle").innerHTML = ("Shot Chart for " + currentPlayers["SG"].name);

	} else if (arg == 'sf') {
		postParams.player = true;
		postParams.id = currentPlayers["SF"].id;
		document.getElementById("shotChartTitle").innerHTML = ("Shot Chart for " + currentPlayers["SF"].name);

	} else if (arg == 'pf') {
		postParams.player = true;
		postParams.id = currentPlayers["PF"].id;
		document.getElementById("shotChartTitle").innerHTML = ("Shot Chart for " + currentPlayers["PF"].name);

	} else if (arg == 'c') {
		postParams.player = true;
		postParams.id = currentPlayers["C"].id;
		document.getElementById("shotChartTitle").innerHTML = ("Shot Chart for " + currentPlayers["C"].name);

	}

	console.log("params", postParams);

	$.post("/dashboard/shotchart", postParams, function(responseJSON) {
		var res = JSON.parse(responseJSON);
		shotChart.makes.remove();
		shotChart.misses.remove();
		console.log(res);
		for (var i=0; i<res.makes.length; i++) {
			shotChart.makes.push(makeObjectAroundPoint(true, res.makes[i].x, res.makes[i].y));
		}
		for (var i=0; i<res.misses.length; i++) {
			shotChart.misses.push(makeObjectAroundPoint(false, res.misses[i].x, res.misses[i].y));
		}
	});
}
