$(function (){

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

	var paper = Raphael(document.getElementById("content"), 1000, 1000);
	var court_paper = Raphael(document.getElementById("court"), 1000, 1000);

	var boxes = paper.set();
	var texts = paper.set();
	var buttons = paper.set();


	for (var i = 0; i < 5; i++) {
		var tempBox = paper.rect(50, 100 + 55 * i, 120, 50, 10).attr({fill: homeColor, stroke: 'black', 'stroke-width': 2});
		var tempText = paper.text(110, 100 + 55 * i + 25, positions[i]).attr({"font-family": "Arial", "font-size":16});
		tempText.box = tempBox;
		tempBox.glowColor = homeAccent;
		tempBox.clickAccent = homeClick;
		tempBox.normalColor = homeColor;
		tempText.toFront();

		texts.push(tempText);
		boxes.push(tempBox);
	}

	for (var i = 0; i < 5; i++) {
		var tempBox = paper.rect(600, 100 + 55 * i, 120, 50, 10).attr({fill: awayColor, stroke: 'black', 'stroke-width': 2});
		var tempText = paper.text(660, 100 + 55 * i + 25, positions[i]).attr({"font-family": "Arial", "font-size":16});
		tempText.box = tempBox;
		tempBox.glowColor = awayAccent;
		tempBox.clickAccent = awayClick;
		tempBox.normalColor = awayColor;
		tempText.toFront();

		texts.push(tempText);
		boxes.push(tempBox);
	}

	for (var i = 0; i < 4; i++) {
		for (var j = 0; j < 4; j++) {
			var tempButton = paper.circle(170 + 70 + 90 * i, 100 + 90 * j, 35).attr({fill: '#8a8a89', "stroke-width" : 2});
			var tempText = paper.text(170 + 70 + 90 * i, 100 + 90 * j, stats[i][j]);
			tempText.box = tempButton;
			tempButton.glowColor = "#646a00";
			tempButton.clickAccent = "#a4a4a2";
			tempButton.normalColor = "#8a8a89";
			texts.push(tempText);
			buttons.push(tempButton);
		}
	}

	boxes.mousedown(function(e) {
		this.attr({fill: this.clickAccent});
	});
	boxes.mouseup(function(e) {
		this.attr({fill: this.normalColor});
	});
	boxes.mouseout(function(e) {
		this.attr({fill: this.normalColor});
	});

	buttons.mousedown(function(e) {
		this.attr({fill: this.clickAccent});
	});
	buttons.mouseup(function(e) {
		this.attr({fill: this.normalColor});
	});
	buttons.mouseout(function(e) {
		this.attr({fill: this.normalColor});
	});

	texts.mousedown(function(e) {
		this.box.attr({fill: this.box.clickAccent});
	});
	texts.mouseup(function(e) {
		this.box.attr({fill: this.box.normalColor});
	});


	boxes.mouseover(function (event) {
	    console.log("here");
	    this.g = this.glow({
	        opacity: 0.85,
	        color: this.glowColor,
	        width: 15
	    });
	}).mouseout(function (event) {
	    this.g.remove();
	});

	buttons.mouseover(function (event) {
	    console.log("here");
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

	// STAT GLOBALS
	var x; 
	var y; 
	var statID; 
	var playerID; 

	function addStat() {
		var xy = standardizeXY(x, y);
		postParameters = {
			x: xy[0],
			y: xy[1],
			statID: statID,
			playerID
		};

		$.post("/stat/add", postParameters, function(responseJSON) {
			errorMessage = JSON.parseJson(responseJSON);
			if (errorMessage.length > 0) {
				alert(errorMessage);
			}
		})
	}

	// Convert the pixels x and y for this court to the backend's reference pixels
	function standardizeXY(x, y) {

	}

})
