// STAT GLOBALS
var clickedPoint; 
var clickedStat; 
var clickedPlayer; 
var mousedowninsomething = false;	
var subState = false;
var STAT_EDIT = false;

var court = document.getElementById("court");
var controls = document.getElementById("content");
var buttons = document.getElementById("buttons");
var home_team_div = document.getElementById("home_team");
var away_team_div = document.getElementById("away_team");
var control_div = document.getElementById("control");

//size of court
court_width = 940 / 1.5;
court_height = 500 / 1.5;
court.setAttribute("style", "width:" + Math.round(court_width) + "px; height:" + Math.round(court_height) + "px");
court.style.width = Math.round(court_width);
court.style.height = Math.round(court_height);

var positions = ["PG", "SG", "SF", "PF", "C"];
var stats = [["Block", "DefensiveFoul", "DefensiveRebound", "FreeThrow","MissedFreeThrow", "MissedThreePointer", "MissedTwoPointer", "OffensiveFoul"],
	     ["OffensiveRebound", "Steal", "TechnicalFoul", "Turnover","ThreePointer", "TwoPointer", "STEWART\nRANGE!!!!"]];

var court_paper = Raphael(court, court.width, court.height);
var buttons_paper = Raphael(buttons, buttons.width, 50);
var home_team_paper = Raphael(home_team_div, 80, 360);
var away_team_paper = Raphael(away_team_div, 80, 360);
var control_paper = Raphael(control_div, 831, 90);

var stats_feed = Raphael(document.getElementById("statFeed"), 200, 800);
stats_feed.count = 0;
stats_feed.l = [];
stats_feed.curr = undefined;



court_paper.image("images/Basketball-Court.png", 0, 0, court_paper.width, court_paper.height);

home_team_paper.mainTexts = home_team_paper.set();
home_team_paper.mainBoxes = home_team_paper.set();

away_team_paper.mainTexts = away_team_paper.set();
away_team_paper.mainBoxes = away_team_paper.set();

buttons_paper.mainTexts = buttons_paper.set();
buttons_paper.mainButtons = buttons_paper.set();


for (var i=0; i < 8; i++) {
    for (var j = 0; j < 2; j++) {
	var tempButton = buttons_paper.circle(45 + 90 * i, 45 + 80 * j, 35).attr({fill: '#8a8a89', "stroke-width" : 2})
	    .data("thing", "stat");
	var tempText = buttons_paper.text(45 + 90 * i,45 + 80 * j, stats[j][i]);
	tempText.box = tempButton;
	tempButton.glowColor = "#646a00";
	tempButton.clickAccent = "#a4a4a2";
	tempButton.normalColor = "#8a8a89";
	
	tempButton.statID = stats[j][i];

	buttons_paper.mainTexts.push(tempText);
	buttons_paper.mainButtons.push(tempButton);
    }
}

control_paper.sendStat = control_paper.rect(150, 0, 50, 50).attr({fill : "red", "stroke-width" : 2});
control_paper.sendStat.click(function (e) {
    if (STAT_EDIT) updateStat();
    else addStat();
});
control_paper.sendStat.words = control_paper.text(175, 55, "Send Stat");

control_paper.deleteStat = control_paper.rect(220, 0, 50, 50).attr({fill : "orange", "stroke-width" : 2});
control_paper.deleteStat.click(function (e) {deleteStat();});
control_paper.deleteStat.words = control_paper.text(240, 55, "Delete Stat");
control_paper.deleteStat.hide();
control_paper.deleteStat.words.hide();

control_paper.homeTimeout = control_paper.rect(0, 0, 50, 50).attr({fill : "darkorange"})
    .click(function(e) {
	$.post("/stats/timeout", {h : true}, function(responseJSON){});
    });
control_paper.homeTimeout.words = control_paper.text(25, 55, "T.O.");
control_paper.awayTimeout = control_paper.rect(control_paper.width - 50, 0, 50, 50).attr({fill : "darkorange"})
    .click(function(e) {
	$.post("/stats/timeout", {h : false}, function(responseJSON){});
    });
control_paper.awayTimeout.words = control_paper.text(control_paper.width - 25, 55, "T.O.");



buttons_paper.mainButtons.mousedown(function(e) {
    this.attr({fill: this.clickAccent});
    mousedowninsomething = true;

});
buttons_paper.mainButtons.mouseup(function(e) {
    clickThing(this);
    mousedowninsomething = false;

});
buttons_paper.mainButtons.mouseout(function(e) {
    if (mousedowninsomething) {
	clickThing(this);
	mousedowninsomething = false;
    }
});

buttons_paper.mainButtons.mouseover(function (event) {
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

    var r = JSON.parse(responseJSON);
    console.log(r);


    var res = r.roster;
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

    for (var i = 0; i < r.stats.length; i++) {
	logStat(r.stats[i], r.types[i]);
    }

    var homeColor = home.primary;

    var homeClick = makeDarker(Raphael.color(home.primary).hex);
    var homeAccent = home.secondary;

    var awayColor = away.primary;
    var awayClick = makeDarker(Raphael.color(away.primary).hex);
    var awayAccent = away.secondary;

    
    for (var i = 0; i < 5; i++) {
	var tempBox = home_team_paper.rect(10, 10 + 55 * i, 50, 50, 10).attr({fill: homeColor, stroke: 'black', 'stroke-width': 2})
	    .data("thing", "player").data("bench", false).data("home", true);
	var tempText = home_team_paper.text(35, 10 + 55 * i + 25, home.onCourt[i].number).attr({"font-family": "Arial", "font-size":16});
	tempText.box = tempBox;
	tempBox.glowColor = homeAccent;
	tempBox.clickAccent = homeClick;
	tempBox.normalColor = homeColor;
	tempBox.t = tempText;
	tempText.toFront();

	tempBox.player = home.onCourt[i];

	home_team_paper.mainTexts.push(tempText);
	home_team_paper.mainBoxes.push(tempBox);
    }

    for (var i = 0; i < home.bench.length; i++) {
	var tempBox = home_team_paper.rect(10, 10 + 30 * (i), 50, 25, 10).attr({fill: homeColor, stroke: 'black', 'stroke-width': 2})
	    .data("thing", "player").data("bench", true).data("home", true);
	var tempText = home_team_paper.text(35, 10 + 30 * (i) + 12.5, home.bench[i].number).attr({"font-family": "Arial", "font-size":16});
	tempText.box = tempBox;
	tempBox.glowColor = homeAccent;
	tempBox.clickAccent = homeClick;
	tempBox.normalColor = homeColor;
	tempBox.t = tempText;
	tempText.toFront();

	tempBox.player = home.bench[i];
	tempText.hide();
	tempBox.hide();
	home_team_paper.mainTexts.push(tempText);
	home_team_paper.mainBoxes.push(tempBox);
    }
    home_team_paper.showingBench = false;
    home_team_paper.showBench = home_team_paper.rect(10, 310, 50, 50).attr({fill : homeAccent})
	.click(function(e) {
	    toggleBenchCourt(home_team_paper);
	});
    home_team_paper.showBench.words = home_team_paper.text(35, 335, "Show\nSubs")
	.click(function(e) {
	    toggleBenchCourt(home_team_paper);
	});



    for (var i = 0; i < 5; i++) {
	var tempBox = away_team_paper.rect(10, 10 + 55 * i, 50, 50, 10).attr({fill: awayColor, stroke: 'black', 'stroke-width': 2})
	    .data("thing", "player").data("bench", false).data("home", false);
	var tempText = away_team_paper.text(35, 10 + 55 * i + 25, away.onCourt[i].number).attr({"font-family": "Arial", "font-size":16});
	tempText.box = tempBox;
	tempBox.glowColor = awayAccent;
	tempBox.clickAccent = awayClick;
	tempBox.normalColor = awayColor;
	tempBox.t = tempText;
	tempText.toFront();

	tempBox.player = away.onCourt[i];

	away_team_paper.mainTexts.push(tempText);
	away_team_paper.mainBoxes.push(tempBox);
    }

    for (var i = 0; i < away.bench.length; i++) {
	console.log("but they have a bench...");
	var tempBox = away_team_paper.rect(10, 10 + 30 * (i), 50, 25, 10).attr({fill: awayColor, stroke: 'black', 'stroke-width': 2})
	    .data("thing", "player").data("bench", true).data("home", false);
	var tempText = away_team_paper.text(35, 10 + 30 * (i) + 12.5, away.bench[i].number).attr({"font-family": "Arial", "font-size":16});
	tempText.box = tempBox;
	tempBox.glowColor = awayAccent;
	tempBox.clickAccent = awayClick;
	tempBox.normalColor = awayColor;
	tempBox.t = tempText;
	tempText.toFront();

	tempBox.player = away.bench[i];
	tempBox.hide();
	tempText.hide();
	away_team_paper.mainTexts.push(tempText);
	away_team_paper.mainBoxes.push(tempBox);
    }

    away_team_paper.showingBench = false;
    away_team_paper.showBench = away_team_paper.rect(10, 310, 50, 50).attr({fill : awayAccent})
	.click(function(e) {
	    toggleBenchCourt(away_team_paper);
	});
    away_team_paper.showBench.words = away_team_paper.text(35, 335, "Show\nSubs")
	.click(function(e) {
	    toggleBenchCourt(away_team_paper);
	});


    home_team_paper.mainBoxes.mousedown(function(e) {
	this.attr({fill: this.clickAccent});
	mousedowninsomething = true;
    });
    home_team_paper.mainBoxes.mouseup(function(e) {
	clickThing(this);
	mousedowninsomething = false;

    });
    home_team_paper.mainBoxes.mouseout(function(e) {
	if (mousedowninsomething) {
	    clickThing(this);
	    mousedowninsomething = false;
	}
    });

    away_team_paper.mainBoxes.mousedown(function(e) {
	this.attr({fill: this.clickAccent});
	mousedowninsomething = true;
    });
    away_team_paper.mainBoxes.mouseup(function(e) {
	clickThing(this);
	mousedowninsomething = false;

    });
    away_team_paper.mainBoxes.mouseout(function(e) {
	if (mousedowninsomething) {
	    clickThing(this);
	    mousedowninsomething = false;
	}
    });

    

    home_team_paper.mainTexts.mousedown(function(e) {
	this.box.attr({fill: this.box.clickAccent});
	mousedowninsomething = true;

    });
    home_team_paper.mainTexts.mouseup(function(e) {
	clickThing(this.box);
	mousedowninsomething = false;
    });

    away_team_paper.mainTexts.mousedown(function(e) {
	this.box.attr({fill: this.box.clickAccent});
	mousedowninsomething = true;

    });
    away_team_paper.mainTexts.mouseup(function(e) {
	clickThing(this.box);
	mousedowninsomething = false;
    });

    buttons_paper.mainTexts.mousedown(function(e) {
	this.box.attr({fill: this.box.clickAccent});
	mousedowninsomething = true;

    });
    buttons_paper.mainTexts.mouseup(function(e) {
	clickThing(this.box);
	mousedowninsomething = false;
    });

    home_team_paper.mainTexts.mouseover(function(e) {
	this.box.g = this.box.glow({
	    opacity: 0.85,
	    color: this.box.glowColor,
	    width: 15
	});
    }).mouseout(function (e) {
	this.box.g.remove();
    });

    away_team_paper.mainTexts.mouseover(function(e) {
	this.box.g = this.box.glow({
	    opacity: 0.85,
	    color: this.box.glowColor,
	    width: 15
	});
    }).mouseout(function (e) {
	this.box.g.remove();
    });

    buttons_paper.mainTexts.mouseover(function(e) {
	this.box.g = this.box.glow({
	    opacity: 0.85,
	    color: this.box.glowColor,
	    width: 15
	});
    }).mouseout(function (e) {
	this.box.g.remove();
    });



    home_team_paper.mainBoxes.mouseover(function (event) {
	this.g = this.glow({
	    opacity: 0.85,
	    color: this.glowColor,
	    width: 15
	});
    }).mouseout(function (event) {
	this.g.remove();
    });

    away_team_paper.mainBoxes.mouseover(function (event) {
	this.g = this.glow({
	    opacity: 0.85,
	    color: this.glowColor,
	    width: 15
	});
    }).mouseout(function (event) {
	this.g.remove();
    });

    




    court_paper.subWindow = court_paper.rect(10, 10, 500, 300).attr({fill : "white", "stroke-width" : 3}).mousemove(function(e) {
	if (!(this.currentMove === undefined)) {
	    this.currentMove.attr({cx : e.offsetX});
	    this.currentMove.attr({cy : e.offsetY});
	}
    });
    court_paper.subWindow.ornaments = [];
    court_paper.subWindow.currentMove = undefined;

    var benchDots = court_paper.set();
    var starterBoxes = court_paper.set();
    var subtexts = court_paper.set();
    var counts = {};
    counts.homeBench = 0;
    counts.homeOn = 0;
    counts.awayBench = 0;
    counts.awayOn = 0;

    home_team_paper.mainBoxes.forEach(function (obj) {
	if (obj.data("bench")) {
	    var benchTemp = court_paper.circle(40,40 + 40 * counts.homeBench,10).attr({fill : "lightblue"});
	    benchTemp.defaultX = 40;
	    benchTemp.defaultY = 40 + 40 * counts.homeBench;
	    counts.homeBench += 1;

	    benchTemp.player = obj.player;
	    var tempTexts = court_paper.text(benchTemp.defaultX, benchTemp.defaultY, benchTemp.player.number);
	    tempTexts.circ = benchTemp;
	    benchTemp.number = tempTexts;
	    subtexts.push(tempTexts);
	    benchDots.push(benchTemp);
	} else {
	    var temp = court_paper.rect(200, 15 + 60 * counts.homeOn, 45, 45);
	    temp.defaultX = 200 + 22.5;
	    temp.defaultY = 15 + 60 * counts.homeOn + 22.5;
	    counts.homeOn += 1;

	    temp.player = obj.player;
	    var tempTexts = court_paper.text(temp.defaultX, temp.defaultY, temp.player.number);
	    temp.number = tempTexts;
	    tempTexts.box = temp;
	    subtexts.push(tempTexts);
	    starterBoxes.push(temp);
	}
    });

    away_team_paper.mainBoxes.forEach(function(obj) {
	if (obj.data("bench")) {
	    var temp = court_paper.circle(440,40 + 40 * counts.awayBench,10).attr({fill : "lightblue"});
	    temp.defaultX = 440;
	    temp.defaultY = 40 + 40 * counts.awayBench;
	    counts.awayBench += 1;

	    temp.player = obj.player;
	    var tempTexts = court_paper.text(temp.defaultX, temp.defaultY, temp.player.number);
	    tempTexts.circ = temp;
	    temp.number = tempTexts;
	    subtexts.push(tempTexts);
	    benchDots.push(temp);
	} else {
	    var temp = court_paper.rect(300, 15 + 60 * counts.awayOn, 45, 45);
	    temp.defaultX = 300 + 22.5;
	    temp.defaultY = 15 + 60 * counts.awayOn + 22.5;
	    counts.awayOn += 1;

	    temp.player = obj.player;
	    var tempTexts = court_paper.text(temp.defaultX, temp.defaultY, temp.player.number);
	    temp.number = tempTexts;
	    tempTexts.box = temp;
	    subtexts.push(tempTexts);
	    starterBoxes.push(temp);
	}
    });

    benchDots.forEach(function(o) {
	court_paper.subWindow.ornaments.push(o);
	makeDraggable(o, court_paper.subWindow);
    });	
    starterBoxes.forEach(function(o) {
	court_paper.subWindow.ornaments.push(o);
    });
    subtexts.forEach(function(obj) {
	court_paper.subWindow.ornaments.push(obj);
	obj.mousedown(function (e) {
	    court_paper.subWindow.currentMove = obj.circ;
	});
	obj.mousemove(function (e) {
	    if (!(court_paper.subWindow.currentMove === undefined)) {
		court_paper.subWindow.currentMove.attr({cx : e.layerX, cy : e.layerY});
	    }
	    
	});
	obj.mouseup(function (e) {
	    court_paper.subWindow.currentMove.attr({cx : court_paper.subWindow.currentMove.defaultX, cy : court_paper.subWindow.currentMove.defaultY});

	    starterBoxes.forEach(function (o) {

		if (Raphael.isPointInsideBBox(o.getBBox(), e.layerX, e.layerY)) {

		    if (o.player.teamID == obj.box.player.teamID) {

			sub(o.player.teamID == home.teamID, court_paper.subWindow.currentMove.player.number, o.player.number);
			court_paper.subWindow.currentMove.hide();

			var temp = o.player;
			o.player = court_paper.subWindow.currentMove.player;
			court_paper.subWindow.currentMove.player = temp;


			var a = o.number.attr("text");
			o.number.attr({text : court_paper.subWindow.currentMove.number.attr("text")});
			court_paper.subWindow.currentMove.number.attr({text : a});
		    }
		}
	    });
	    court_paper.subWindow.currentMove = undefined;

	});
    });

    for(var i = 0; i < court_paper.subWindow.ornaments.length; i++) court_paper.subWindow.ornaments[i].hide();

    var openSub = control_paper.rect(600, 0, 50, 50).attr({fill : "black", "stroke-width" : 2}).data("open", false);
    openSub.subWindow = court_paper.subWindow;
    openSub.subWindow.hide();
    openSub.click(function(e) {showSubWindow(this)});

    function makeDraggable(obj, sw) {
	obj.mousedown(function (e) {
	    sw.currentMove = this;
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
		    if (o.player.teamID == obj.player.teamID) {
			sub(o.player.teamID == home.teamID, obj.player.number, o.player.number);
			obj.hide();

			var temp = o.player;
			o.player = obj.player;
			obj.player = temp;

			var a = o.number.attr("text");
			o.number.attr({text : obj.number.attr("text")});
			obj.number.attr({text : a});
		    }
		}
	    });

	});
    }
    
});	

control_paper.flipPossession = control_paper.rect(370, 0, 50, 50).attr({fill : "blue", "stroke-width" : 2}).data("home", true);
control_paper.flipPossession.words = control_paper.text(395, 60, "Flip Possession");
control_paper.flipPossession.a = control_paper.path("M375,5,415,25,375,45z").attr({fill : "Yellow"});
control_paper.flipPossession.h = control_paper.path("M415,5,375,25,415,45z").attr({fill : "Yellow"});
control_paper.flipPossession.a.hide();
control_paper.flipPossession.click(function(e) {fp()});
control_paper.flipPossession.a.click(function(e) {fp()});		
control_paper.flipPossession.h.click(function(e) {fp()});	

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
    if (control_paper.flipPossession.data("home")) {
	control_paper.flipPossession.data("home", false);
	control_paper.flipPossession.h.hide();
	control_paper.flipPossession.a.show();
    } else {
	control_paper.flipPossession.data("home", true);
	control_paper.flipPossession.a.hide();
	control_paper.flipPossession.h.show();
    }
    $.post("/stats/changepossession", {}, function(responseJSON) {});

}

function toggleBenchCourt(paper) {
    paper.mainBoxes.forEach(function (obj) {
	if ((obj.data("bench") && paper.showingBench) || (!(obj.data("bench")) && !(paper.showingBench))) {
	    obj.hide();
	    obj.t.hide();
	} else {
	    obj.show();
	    obj.t.show();
	}
    });
    if (paper.showingBench) paper.showBench.words.attr({"text" : "Show\nSubs"});
    else paper.showBench.words.attr({"text" : "Show\nStarters"});
    paper.showingBench = !(paper.showingBench);
    
}

function sub(h, inPlayer, outPlayer) {

    var inBox;
    var outBox;

    if (h) {
	home_team_paper.mainBoxes.forEach(function (t) {
	    if (t.data("bench")) {
		if (t.player.number == parseInt(inPlayer)) inBox = t;
	    } else {
		if (t.player.number == parseInt(outPlayer)) outBox = t;
	    }
	});
    } else {
	away_team_paper.mainBoxes.forEach(function (t) {
	    if (t.data("bench")) {
		if (t.player.number == parseInt(inPlayer)) inBox = t;
	    } else {
		if (t.player.number == parseInt(outPlayer)) outBox = t;
	    }
	});
    }

    if (!(inBox === undefined || outBox === undefined) && inBox.data("home") == outBox.data("home")) {
	var t;
	if (h) t = home;
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
	clickedPoint = undefined;
	clickedPlayer.attr({fill: clickedPlayer.normalColor});
	clickedPlayer = undefined;
	clickedStat.attr({fill: clickedStat.normalColor});
	clickedStat = undefined;

	console.log(postParameters);
	
	$.post("/stats/add", postParameters, function(responseJSON) {
	    console.log(responseJSON);
	    var r = JSON.parse(responseJSON);
	    var res = r.stat;
	    logStat(res, r.statType);
	    
	});

    }

}

function logStat(res, type) {
    console.log(res);
    var feed = stats_feed.rect(0, stats_feed.count * -50, 25, 25);
    console.log("this should be a color ", home.primary);
    if (res.player.teamID == home.teamID) feed.attr({fill : home.primary});
    else feed.attr({fill : away.primary})
    var txt = stats_feed.text(27, stats_feed.count * -50 + 12.5, "#" + res.player.number + " " + res.player.name + " " + type)
	.attr({"text-anchor" : "start"});
    feed.words = txt;
    feed.stat = res;
    feed.statType = type;
    feed.g = undefined;
    feed.click(function (e) {
	if (feed.g === undefined) {
	    if (stats_feed.curr !== undefined) {
		stats_feed.curr.g.remove();
		stats_feed.curr.g = undefined;
	    }
	    feed.g = feed.glow();
	    stats_feed.curr = feed;
	    STAT_EDIT = true;
	    control_paper.sendStat.attr({"fill" : "green"});
	    control_paper.sendStat.words.attr({"text" : "Update Stat"});
	    control_paper.deleteStat.show();
	    control_paper.deleteStat.words.show();
	} else {
	    feed.g.remove();
	    feed.g = undefined;
	    stats_feed.curr = undefined;

	    STAT_EDIT = false;
	    control_paper.sendStat.attr({"fill" : "red"});
	    control_paper.sendStat.words.attr({"text" : "Send Stat"});

	    control_paper.deleteStat.hide();
	    control_paper.deleteStat.words.hide();
	}
    });
    stats_feed.count++;
    stats_feed.l.push(feed);
    stats_feed.setViewBox(0, (stats_feed.count-1) * -50, stats_feed.width, stats_feed.height);
}

function deleteStat() {
    var i = stats_feed.curr.stat.id;
    stats_feed.curr.words.remove();
    stats_feed.curr.g.remove();
    stats_feed.curr.remove();

    stats_feed.curr.g = undefined;
    stats_feed.curr = undefined;

    STAT_EDIT = false;
    control_paper.sendStat.attr({"fill" : "red"});
    control_paper.sendStat.words.attr({"text" : "Send Stat"});

    control_paper.deleteStat.hide();
    control_paper.deleteStat.words.hide();
    for(var i = 0; i < stats_feed.l.length; i++)  {
	var anim = Raphael.animation({cy : stats_feed.l[i].cy - 50});
	stats_feed.l[i].animate(anim);
    }


    $.post("/stats/delete", {databaseID : i}, function() {});
}

function updateStat() {
    console.log(stats_feed.curr.stat, stats_feed.curr.statType);
    postParameters = {
	x : stats_feed.curr.stat.pos.x,
	y : stats_feed.curr.stat.pos.y,
	statID : stats_feed.curr.statType,
	playerID : stats_feed.curr.stat.player.id,
	databaseID : stats_feed.curr.stat.id
    };

    var player = stats_feed.curr.stat.player;
    
    //database id, statid, playerid, new location
    if (clickedPoint !== undefined) {
	postParameters.x = clickedPoint.data("ratioX");
	postParameters.y = clickedPoint.data("ratioY");
	clickedPoint.remove();
	clickedPoint = undefined;

    }
    if (clickedStat !== undefined) {
	postParameters.statID = clickedStat.statID;
	clickedStat.attr({fill: clickedStat.normalColor});
	clickedStat = undefined;

    }
    if (clickedPlayer !== undefined) {
	postParameters.playerID = clickedPlayer.player.id;
	clickedPlayer.attr({fill: clickedPlayer.normalColor});
	player = clickedPlayer.player;
	clickedPlayer = undefined;

    }

    stats_feed.curr.words.attr({text : "#" + player.number + " " + player.name + " " + postParameters.statID});
    stats_feed.curr.stat.player = player;
    stats_feed.curr.statType = postParameters.statID;
    stats_feed.curr.stat.pos.x = postParameters.x;
    stats_feed.curr.stat.pos.y = postParameters.y;
    stats_feed.curr.g.remove();
    stats_feed.curr.g = undefined;
    stats_feed.curr = undefined;

    STAT_EDIT = false;
    control_paper.sendStat.attr({"fill" : "red"});
    control_paper.sendStat.words.attr({"text" : "Send Stat"});

    control_paper.deleteStat.hide();
    control_paper.deleteStat.words.hide();


    $.post("/stats/update", postParameters, function(responseJSON) {
    });



}


function makeDarker(h) {
    console.log(h);
    var r = parseInt(((h.charAt(0)=="#") ? h.substring(1,7):h).substring(0,2),16);
    var g = parseInt(((h.charAt(0)=="#") ? h.substring(1,7):h).substring(2,4),16);
    var b = parseInt(((h.charAt(0)=="#") ? h.substring(1,7):h).substring(4,6),16);

    return "rgb(" + r * .75 + "," + g * .75 + "," +  b * .75 + ")";
}


disable_user_select();

function disable_user_select() {
    $("*").css("-webkit-user-select", "none");
    $("*").css("-moz-user-select", "none");
};
