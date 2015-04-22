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
		home.teamID = res[4].players.HomePG.teamID;

		home.onCourt = [];
		console.log(res[4].players);
		home.onCourt.push(res[4].players.HomePG);
		home.onCourt.push(res[4].players.HomeSG);
		home.onCourt.push(res[4].players.HomeSF);
		home.onCourt.push(res[4].players.HomePF);
		home.onCourt.push(res[4].players.HomeC);
		home.bench = [];
		for (var i = 0; i < res[5].players.length; i++) {
			home.bench.push(res[5].players[i]);
		}

		away.primary = res[2];
		away.secondary = res[3];
		away.teamID = res[4].players.AwayPG.teamID;

		away.onCourt = [];
		away.onCourt.push(res[4].players.AwayPG);
		away.onCourt.push(res[4].players.AwaySG);
		away.onCourt.push(res[4].players.AwaySF);
		away.onCourt.push(res[4].players.AwayPF);
		away.onCourt.push(res[4].players.AwayC);
		away.bench = [];
		for (var i = 0; i < res[6].players.length; i++) {
			away.bench.push(res[6].players[i]);
		}



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
		var tempText = paper.text(155, 35 + 55 * i + 25, home.onCourt[i].number).attr({"font-family": "Arial", "font-size":16});
		tempText.box = tempBox;
		tempBox.glowColor = homeAccent;
		tempBox.clickAccent = homeClick;
		tempBox.normalColor = homeColor;
		tempBox.t = tempText;
		tempText.toFront();

		tempBox.player = home.onCourt[i];

		texts.push(tempText);
		boxes.push(tempBox);
	}
	for (var i = 0; i < home.bench.length; i++) {
		var tempBox = paper.rect(50, 35 + 55 * (i), 50, 50, 10).attr({fill: homeColor, stroke: 'black', 'stroke-width': 2})
			.data("thing", "player").data("bench", true).data("home", true);
		var tempText = paper.text(75, 35 + 55 * (i) + 25, home.bench[i].number).attr({"font-family": "Arial", "font-size":16});
		tempText.box = tempBox;
		tempBox.glowColor = homeAccent;
		tempBox.clickAccent = homeClick;
		tempBox.normalColor = homeColor;
		tempBox.t = tempText;
		tempText.toFront();

		tempBox.player = home.bench[i];

		texts.push(tempText);
		boxes.push(tempBox);
	}


	for (var i = 0; i < 5; i++) {
		var tempBox = paper.rect(570, 35 + 55 * i, 50, 50, 10).attr({fill: awayColor, stroke: 'black', 'stroke-width': 2})
			.data("thing", "player").data("bench", false).data("home", false);
		var tempText = paper.text(595, 35 + 55 * i + 25, away.onCourt[i].number).attr({"font-family": "Arial", "font-size":16});
		tempText.box = tempBox;
		tempBox.glowColor = awayAccent;
		tempBox.clickAccent = awayClick;
		tempBox.normalColor = awayColor;
		tempBox.t = tempText;
		tempText.toFront();

		tempBox.player = away.onCourt[i];


		texts.push(tempText);
		boxes.push(tempBox);
	}
	for (var i = 0; i < away.bench.length; i++) {
		var tempBox = paper.rect(650, 35 + 55 * (i), 50, 50, 10).attr({fill: awayColor, stroke: 'black', 'stroke-width': 2})
			.data("thing", "player").data("bench", true).data("home", false);
		var tempText = paper.text(675, 35 + 55 * (i) + 25, away.bench[i].number).attr({"font-family": "Arial", "font-size":16});
		tempText.box = tempBox;
		tempBox.glowColor = awayAccent;
		tempBox.clickAccent = awayClick;
		tempBox.normalColor = awayColor;
		tempBox.t = tempText;
		tempText.toFront();

		tempBox.player = away.bench[i];

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


	var subWindow = paper.rect(10, 10, 500, 300).attr({fill : "white", "stroke-width" : 3}).mousemove(function(e) {
		if (!(this.currentMove === undefined)) {
			this.currentMove.attr({cx : e.offsetX});
			this.currentMove.attr({cy : e.offsetY});
		}
	});
	subWindow.ornaments = [];
	subWindow.currentMove = undefined;

	var benchDots = paper.set();
	var starterBoxes = paper.set();
	var subtexts = paper.set();
	var counts = {};
	counts.homeBench = 0;
	counts.homeOn = 0;
	counts.awayBench = 0;
	counts.awayOn = 0;

	boxes.forEach(function(obj) {
		console.log(obj.data("bench"));
		if (obj.data("bench")) {
			var temp;
			if (obj.data("home")) {
				var temp = paper.circle(40,40 + 40 * counts.homeBench,10).attr({fill : "lightblue"});
				temp.defaultX = 40;
				temp.defaultY = 40 + 40 * counts.homeBench;
				counts.homeBench += 1;

			} else {
				var temp = paper.circle(440,40 + 40 * counts.awayBench,10).attr({fill : "lightblue"});
				temp.defaultX = 440;
				temp.defaultY = 40 + 40 * counts.awayBench;
				counts.awayBench += 1;
			}
			temp.player = obj.player;
			var tempTexts = paper.text(temp.defaultX, temp.defaultY, temp.player.number);
			tempTexts.circ = temp;
			temp.number = tempTexts;
			subtexts.push(tempTexts);

			benchDots.push(temp);
		} else {
			if (obj.data("home")){
				var temp = paper.rect(200, 15 + 60 * counts.homeOn, 45, 45);
				temp.defaultX = 200 + 22.5;
				temp.defaultY = 15 + 60 * counts.homeOn + 22.5;
				counts.homeOn += 1;

			} else {
				var temp = paper.rect(300, 15 + 60 * counts.awayOn, 45, 45);
				temp.defaultX = 300 + 22.5;
				temp.defaultY = 15 + 60 * counts.awayOn + 22.5;
				counts.awayOn += 1;

			}
			temp.player = obj.player;
			var tempTexts = paper.text(temp.defaultX, temp.defaultY, temp.player.number);
			temp.number = tempTexts;
			subtexts.push(tempTexts);
			starterBoxes.push(temp);
		}
	});

	benchDots.forEach(function(o) {
		subWindow.ornaments.push(o);
		makeDraggable(o, subWindow);
	});	
	starterBoxes.forEach(function(o) {
		subWindow.ornaments.push(o);
	});
	subtexts.forEach(function(o) {
		subWindow.ornaments.push(o);
		o.mousedown(function (e) {
			subWindow.currentMove = o.circ;
		});
		o.mousemove(function (e) {
				if (!(subWindow.currentMove === undefined)) {
					subWindow.currentMove.attr({cx : e.layerX, cy : e.layerY});
				}
			
		});
		o.mouseup(function (e) {
			subWindow.currentMove.attr({cx : subWindow.currentMove.defaultX, cy : subWindow.currentMove.defaultY});
			subWindow.currentMove = undefined;
		});
	});

	for(var i = 0; i < subWindow.ornaments.length; i++) subWindow.ornaments[i].hide();

	var openSub = paper.rect(750, 130, 50, 50).attr({fill : "black", "stroke-width" : 2}).data("open", false);
	openSub.subWindow = subWindow;
	openSub.subWindow.hide();
	openSub.click(function(e) {showSubWindow(this)});

	function makeDraggable(obj, sw) {
	obj.mousedown(function (e) {
		sw.currentMove = this;
		console.log("a");
	});
	obj.mousemove(function (e) {
		if (sw.currentMove !== undefined) {
			sw.currentMove.attr({cx : e.offsetX});
			sw.currentMove.attr({cy : e.offsetY});

		}
	});
	obj.mouseup(function (e) {
		sw.currentMove.attr({cx : sw.currentMove.defaultX});
		sw.currentMove.attr({cy : sw.currentMove.defaultY});
		sw.currentMove = undefined;


		starterBoxes.forEach(function (o) {
			if (Raphael.isPointInsideBBox(o.getBBox(), e.offsetX, e.offsetY)) {
				console.log(o.player);
				if (o.player.teamID == obj.player.teamID) {
					console.log(o.data("home"), " ", obj.player.number, " ", o.player.number);
					sub(o.player.teamID == home.teamID, obj.player.number, o.player.number);
					obj.hide();

					var temp = o.player;
					o.player = obj.player;
					obj.player = temp;
					console.log(o.number, obj.number);

					var a = o.number.attr("text");
					o.number.attr({text : obj.number.attr("text")});
					obj.number.attr({text : a});
				}
			}
		});

	});
	}
		
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

function showSubWindow(obj) {
	if (obj.data("open")) {
		for (var i = 0; i < obj.subWindow.ornaments.length; i++) {
			obj.subWindow.ornaments[i].hide();
		}
		obj.subWindow.hide();

		obj.data("open", false);
	} else {
		obj.subWindow.show();
		obj.subWindow.toFront();
		for (var i = 0; i < obj.subWindow.ornaments.length; i++) {
			obj.subWindow.ornaments[i].show();
			obj.subWindow.ornaments[i].toFront();
		}
		obj.data("open", true);
	}
}

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

function sub(h, inPlayer, outPlayer) {

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
		var t;
		if (inBox.data("home")) t = home;
		else t = away;

		var courtSpot;
		var benchSpot;
		for (var i = 0; i < t.onCourt.length; i++) {
			if (t.onCourt[i] == outBox.player) courtSpot = i;
		}
		for (var j = 0; j < t.bench.length; j++) {
			if (t.onCourt[j] == outBox.player) courtSpot = j;
		}
		var temp = t.onCourt[courtSpot];
		t.onCourt[courtSpot] = t.bench[benchSpot];
		t.bench[benchSpot] = temp;

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
