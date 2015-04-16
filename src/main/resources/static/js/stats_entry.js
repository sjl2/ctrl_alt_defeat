
	// STAT GLOBALS
	var clickedPoint; 
	var clickedStat; 
	var clickedPlayer; 
	var mousedowninsomething = false;

	var court = document.getElementById("court");
	var controls = document.getElementById("content");

	//size of court
	court_width = 940 / 1.5;
	court_height = 500 / 1.5;
	court.setAttribute("style", "width:" + Math.round(court_width) + "px; height:" + Math.round(court_height) + "px");
	court.style.width = Math.round(court_width);
	court.style.height = Math.round(court_height);

	var homeColor = 'darkorange';
	var homeClick = "#a2591d";
	var homeAccent = "blue";

	var awayColor = 'green';
	var awayClick = "#0a390c";
	var awayAccent = "red";

	var positions = ["PG", "SG", "SF", "PF", "C"];
	var stats = [["Block", "DefensiveFoul", "DefensiveRebound", "FreeThrow"],
				["MissedFreeThrow", "MissedThreePointer", "MissedTwoPointer", "OffensiveFoul"],
				["OffensiveRebound", "Steal", "TechnicalFoul", "Turnover"],
				["ThreePointer", "TwoPointer"]];

	var paper = Raphael(controls, 1000, 1000);
	var court_paper = Raphael(court, court.width, court.height);
	court_paper.image("images/Basketball-Court.png", 0, 0, court_paper.width, court_paper.height);

	var boxes = paper.set();
	var texts = paper.set();
	var buttons = paper.set();


	for (var i = 0; i < 5; i++) {
		var tempBox = paper.rect(50, 5 + 55 * i, 120, 50, 10).attr({fill: homeColor, stroke: 'black', 'stroke-width': 2})
			.data("thing", "player");
		var tempText = paper.text(110, 5 + 55 * i + 25, positions[i]).attr({"font-family": "Arial", "font-size":16});
		tempText.box = tempBox;
		tempBox.glowColor = homeAccent;
		tempBox.clickAccent = homeClick;
		tempBox.normalColor = homeColor;
		tempText.toFront();

		tempBox.playerID = "home" + positions[i];

		texts.push(tempText);
		boxes.push(tempBox);
	}

	for (var i = 0; i < 5; i++) {
		var tempBox = paper.rect(600, 5 + 55 * i, 120, 50, 10).attr({fill: awayColor, stroke: 'black', 'stroke-width': 2})
			.data("thing", "player");
		var tempText = paper.text(660, 5 + 55 * i + 25, positions[i]).attr({"font-family": "Arial", "font-size":16});
		tempText.box = tempBox;
		tempBox.glowColor = awayAccent;
		tempBox.clickAccent = awayClick;
		tempBox.normalColor = awayColor;
		tempText.toFront();

		tempBox.playerID = "away" + positions[i];


		texts.push(tempText);
		boxes.push(tempBox);
	}

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

	buttons.mouseover(function (event) {
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




	$("#court").click(function(e) {
		if (!(clickedPoint === undefined)) clickedPoint.remove();
		clickedPoint = court_paper.circle(e.offsetX, e.offsetY, 2)
			.data("ratioX", e.offsetX / court_width)
			.data("ratioY", e.offsetY / court_height);

	});


	

function clickThing(b) {
	if (b.data("thing") == "player") {
		if (!(clickedPlayer === undefined)) clickedPlayer.attr({fill: clickedPlayer.normalColor});
		clickedPlayer = b;
	} else if (b.data("thing") == "stat") {
		if (!(clickedStat === undefined)) clickedStat.attr({fill: clickedStat.normalColor});
		clickedStat = b;

	}
} 


	function addStat() {
		if (!(clickedPoint === undefined || clickedStat === undefined || clickedPlayer === undefined)) {
		postParameters = {
			x: clickedPoint.data("ratioX"),
			y: clickedPoint.data("ratioY"),
			statID: clickedStat.statID,
			playerID: clickedPlayer.playerID
		};
		clickedPoint.remove();
		clickedPlayer.attr({fill: clickedPlayer.normalColor});
		clickedStat.attr({fill: clickedStat.normalColor});
		console.log("adding stat ", postParameters)
		
		$.post("/stat/add", postParameters, function(responseJSON) {
			errorMessage = JSON.parseJson(responseJSON);
			if (errorMessage.length > 0) {
				alert(errorMessage);
			}
		});

		}
	}

