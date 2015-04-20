	// STAT GLOBALS
	var clickedPoint; 
	var clickedStat; 
	var clickedPlayer; 
	var mousedowninsomething = false;
	var subState = false;

	var court = document.getElementById("court");
	var controls = document.getElementById("content");

	//size of court
	court_width = 940 / 1.5;
	court_height = 500 / 1.5;
	court.setAttribute("style", "width:" + Math.round(court_width) + "px; height:" + Math.round(court_height) + "px");
	court.style.width = Math.round(court_width);
	court.style.height = Math.round(court_height);

	var positions = ["PG", "SG", "SF", "PF", "C"];
	var stats = [["Block", "DefensiveFoul", "DefensiveRebound", "FreeThrow"],
				["MissedFreeThrow", "MissedThreePointer", "MissedTwoPointer", "OffensiveFoul"],
				["OffensiveRebound", "Steal", "TechnicalFoul", "Turnover"],
				["ThreePointer", "TwoPointer", "STEWART\nRANGE!!!!"]];

	var paper = Raphael(controls, 1000, 500);
	var court_paper = Raphael(court, court.width, court.height);
	
	court_paper.image("images/Basketball-Court.png", 0, 0, court_paper.width, court_paper.height);

	var texts = paper.set();
	var buttons = paper.set();
	var boxes = paper.set();


	for (var i = 0; i < 4; i++) {
		for (var j = 0; j < 4; j++) {
			var tempButton = paper.circle(170 + 70 + 90 * i, 42 + 90 * j, 35).attr({fill: '#8a8a89', "stroke-width" : 2})
				.data("thing", "stat");
			var tempText = paper.text(170 + 70 + 90 * i, 42 + 90 * j, stats[i][j]);
			tempText.box = tempButton;
			tempButton.glowColor = "#646a00";
			tempButton.clickAccent = "#a4a4a2";
			tempButton.normalColor = "#8a8a89";
			
			tempButton.statID = stats[i][j];

			texts.push(tempText);
			buttons.push(tempButton);


		}
	}


	var sendStat = paper.rect(750, 5, 50, 50).attr({fill : "red", "stroke-width" : 2});
	sendStat.click(function (e) {addStat();});
	paper.text(775, 60, "Send Stat");

	var homeTimeout = paper.rect(130, 350, 50, 50).attr({fill : "darkorange"})
	.click(function(e) {
		$.post("/stats/timeout", {h : true}, function(responseJSON){});
	});
	paper.text(155, 375, "T.O.");
	var awayTimeout = paper.rect(570, 350, 50, 50).attr({fill : "darkorange"})
	.click(function(e) {
		$.post("/stats/timeout", {h : false}, function(responseJSON){});
	});
	paper.text(595, 375, "T.O.");

	

buttons.mousedown(function(e) {
		this.attr({fill: this.clickAccent});
		mousedowninsomething = true;

	});
	buttons.mouseup(function(e) {
		clickThing(this);
		mousedowninsomething = false;

	});
	buttons.mouseout(function(e) {
		if (mousedowninsomething) {
			clickThing(this);
			mousedowninsomething = false;
		}
	});

buttons.mouseover(function (event) {
	    this.g = this.glow({
	        opacity: 0.85,
	        color: this.glowColor,
	        width: 15
	    });
	}).mouseout(function (event) {
	    this.g.remove();
	});

$("#court").click(function(e) {
		if (!(clickedPoint === undefined)) clickedPoint.remove();
		clickedPoint = court_paper.circle(e.offsetX, e.offsetY, 2)
			.data("ratioX", e.offsetX / court_width)
			.data("ratioY", e.offsetY / court_height);

	});


var home = {};
var away = {};
$.get("/game/roster", function(responseJSON) {
		var res = JSON.parse(responseJSON);
		console.log(res);
		home.primary = res[0];
		home.secondary = res[1];
		home.roster = [];
		console.log(res[4].players);
		home.roster.push(res[4].players.HomePG);
		home.roster.push(res[4].players.HomeSG);
		home.roster.push(res[4].players.HomeSF);
		home.roster.push(res[4].players.HomePF);
		home.roster.push(res[4].players.HomeC);
		for (var i = 0; i < res[5].players.length; i++) {
			home.roster.push(res[5].players[i]);
		}

		away.primary = res[2];
		away.secondary = res[3];
		away.roster = [];
		away.roster.push(res[4].players.AwayPG);
		away.roster.push(res[4].players.AwaySG);
		away.roster.push(res[4].players.AwaySF);
		away.roster.push(res[4].players.AwayPF);
		away.roster.push(res[4].players.AwayC);
		for (var i = 0; i < res[5].players.length; i++) {
			away.roster.push(res[5].players[i]);
		}

		console.log("away ", away.roster);


		var homeColor = home.primary;
	var homeClick = "#a2591d";
	var homeAccent = home.secondary;

	var awayColor = away.primary;
	var awayClick = "#0a390c";
	var awayAccent = away.secondary;

	



	for (var i = 0; i < 5; i++) {
		console.log(home);
		var tempBox = paper.rect(130, 35 + 55 * i, 50, 50, 10).attr({fill: homeColor, stroke: 'black', 'stroke-width': 2})
			.data("thing", "player").data("bench", false).data("home", true);
		var tempText = paper.text(155, 35 + 55 * i + 25, home.roster[i].number).attr({"font-family": "Arial", "font-size":16});
		tempText.box = tempBox;
		tempBox.glowColor = homeAccent;
		tempBox.clickAccent = homeClick;
		tempBox.normalColor = homeColor;
		tempBox.t = tempText;
		tempText.toFront();

		tempBox.player = home.roster[i];

		texts.push(tempText);
		boxes.push(tempBox);
	}
	for (var i = 5; i < home.roster.length; i++) {
		var tempBox = paper.rect(50, 35 + 55 * (i - 5), 50, 50, 10).attr({fill: homeColor, stroke: 'black', 'stroke-width': 2})
			.data("thing", "player").data("bench", true).data("home", true);
		var tempText = paper.text(75, 35 + 55 * (i-5) + 25, home.roster[i].number).attr({"font-family": "Arial", "font-size":16});
		tempText.box = tempBox;
		tempBox.glowColor = homeAccent;
		tempBox.clickAccent = homeClick;
		tempBox.normalColor = homeColor;
		tempBox.t = tempText;
		tempText.toFront();

		tempBox.player = home.roster[i];

		texts.push(tempText);
		boxes.push(tempBox);
	}


	for (var i = 0; i < 5; i++) {
		var tempBox = paper.rect(570, 35 + 55 * i, 50, 50, 10).attr({fill: awayColor, stroke: 'black', 'stroke-width': 2})
			.data("thing", "player").data("bench", false).data("home", false);
		var tempText = paper.text(595, 35 + 55 * i + 25, away.roster[i].number).attr({"font-family": "Arial", "font-size":16});
		tempText.box = tempBox;
		tempBox.glowColor = awayAccent;
		tempBox.clickAccent = awayClick;
		tempBox.normalColor = awayColor;
		tempBox.t = tempText;
		tempText.toFront();

		tempBox.player = away.roster[i];


		texts.push(tempText);
		boxes.push(tempBox);
	}
	for (var i = 5; i < away.roster.length; i++) {
		var tempBox = paper.rect(650, 35 + 55 * (i - 5), 50, 50, 10).attr({fill: awayColor, stroke: 'black', 'stroke-width': 2})
			.data("thing", "player").data("bench", true).data("home", false);
		var tempText = paper.text(675, 35 + 55 * (i-5) + 25, away.roster[i].number).attr({"font-family": "Arial", "font-size":16});
		tempText.box = tempBox;
		tempBox.glowColor = awayAccent;
		tempBox.clickAccent = awayClick;
		tempBox.normalColor = awayColor;
		tempBox.t = tempText;
		tempText.toFront();

		tempBox.player = away.roster[i];

		texts.push(tempText);
		boxes.push(tempBox);
	}

	

	boxes.mousedown(function(e) {
		this.attr({fill: this.clickAccent});
		mousedowninsomething = true;
	});
	boxes.mouseup(function(e) {
		clickThing(this);
		mousedowninsomething = false;

	});
	boxes.mouseout(function(e) {
		if (mousedowninsomething) {
			clickThing(this);
			mousedowninsomething = false;
		}
	});

	

	texts.mousedown(function(e) {
		this.box.attr({fill: this.box.clickAccent});
	});
	texts.mouseup(function(e) {
		//this.box.attr({fill: this.box.normalColor});
		clickThing(this.box);
	});



	boxes.mouseover(function (event) {
	    this.g = this.glow({
	        opacity: 0.85,
	        color: this.glowColor,
	        width: 15
	    });
	}).mouseout(function (event) {
	    this.g.remove();
	});

	

	texts.mouseover(function(e) {
		this.box.g = this.box.glow({
			opacity: 0.85,
	        color: this.box.glowColor,
	        width: 15
		});
	}).mouseout(function (e) {
		this.box.g.remove();
	});


		
	});



var flipPossession = paper.rect(750, 70, 50, 50).attr({fill : "blue", "stroke-width" : 2}).data("home", true);
	sendStat.click(function (e) {addStat();});
	paper.text(775, 125, "Flip Possession");
	var a = paper.path("M755,75,755,115,795,95z").attr({fill : "Yellow"});
	flipPossession.awayPath = a;
	var h = paper.path("M795,75,795,115,755,95z").attr({fill : "Yellow"});
	flipPossession.awayPath = h;
	a.hide();
	flipPossession.click(function(e) {fp()});
	a.click(function(e) {fp()});		
	h.click(function(e) {fp()});	

function fp() {
	if (flipPossession.data("home")) {
			flipPossession.data("home", false);
			h.hide();
			a.show();
	} else {
			flipPossession.data("home", true);
			a.hide();
			h.show();
	}
	$.post("/stats/changepossession", {}, function(responseJSON) {});

}

function sub() {
	var inPlayer = $("#inPlayer")[0].value;
	var outPlayer = $("#outPlayer")[0].value;

	var h = $("#h").is(":checked");

	var inBox;
	var outBox;

	boxes.forEach(function(t) {
		if (t.data("bench") && t.data("home") == h) {
			if (t.player.number == parseInt(inPlayer)) {
				inBox = t;
			}
		} else if (t.data("home") == h) {
			if (t.player.number == parseInt(outPlayer)) outBox = t;
		}
	});

	if (!(inBox === undefined || outBox === undefined) && inBox.data("home") == outBox.data("home")) {
		var temp = inBox.player;
		inBox.player = outBox.player;
		outBox.player = temp;
		inBox.t.attr({"text" : inBox.player.number});
		outBox.t.attr({"text" : outBox.player.number});

		$.post("/stats/sub", {"out" : inBox.player.id, "in" : outBox.player.id, "home" : h}, function(){});
	} else alert("Sub was invalid! Sorry");

	
}


	

function clickThing(b) {
	if (b.data("thing") == "player") {
		if (!(clickedPlayer === undefined)) clickedPlayer.attr({fill: clickedPlayer.normalColor});
		clickedPlayer = b;
		clickedPlayer.attr({fill: clickedPlayer.clickAccent});
	} else if (b.data("thing") == "stat") {
		if (!(clickedStat === undefined)) clickedStat.attr({fill: clickedStat.normalColor});
		clickedStat = b;
		clickedStat.attr({fill: clickedStat.clickAccent});
	}
} 


	function addStat() {
		if (!(clickedPoint === undefined || clickedStat === undefined || clickedPlayer === undefined)) {
		postParameters = {
			x: clickedPoint.data("ratioX"),
			y: clickedPoint.data("ratioY"),
			statID: clickedStat.statID,
			playerID: clickedPlayer.player.id
		};
		clickedPoint.remove();
		clickedPlayer.attr({fill: clickedPlayer.normalColor});
		clickedStat.attr({fill: clickedStat.normalColor});
		console.log("adding stat ", postParameters)
		
		$.post("/stats/add", postParameters, function(responseJSON) {
			console.log(responseJSON);
		});

		}

	}

disable_user_select();

function disable_user_select() {
    $("*").css("-webkit-user-select", "none");
    $("*").css("-moz-user-select", "none");
};
