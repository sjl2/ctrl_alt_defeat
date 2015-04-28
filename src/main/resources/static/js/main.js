//GLOBAL HEAT MAP VARIABLES
var epsilon_x = .1;
var epsilon_y = .05;

function drawHeatMap(playerID, gameIDs, paper) {

	var grid;
	for(var i=0; i< (1/epsilon_x); i++) {
		for (var j=0; j< (.5/epsilon_y); j++) {
			var temp = {};
			temp.made = 0;
			temp.missed = 0;
			grid[i][j] = temp;
		}
	}

	$.post("/dashboard/shotchart", postParams, function(responseJSON) {
		var res = JSON.parse(responseJSON);
		for (var i=0; i<res.makes.length; i++) {
			grid[Math.floor(res.makes[i].x / epsilon_x)][Math.floor(res.makes[i].y / epsilon_y)].made += 1;
		}
		for (var i=0; i<res.misses.length; i++) {
			grid[Math.floor(res.makes[i].x / epsilon_x)][Math.floor(res.makes[i].y / epsilon_y)].made += 1;
		}

	});
}

function drawShotChart(playerID, gameID, paper) {
	console.log(playerID, gameID);
	postParams = {};
	postParams.player = true;
	postParams.id = playerID;
	postParams.gameID = gameID;
	postParams.currentGame = false;
	$.post("/dashboard/shotchart", postParams, function(responseJSON) {
		var res = JSON.parse(responseJSON);
		console.log(res);
		for (var i=0; i<res.makes.length; i++) {
			var centerX = res.makes[i].x * paper.width;
			var centerY = res.makes[i].y * paper.height;
			paper.shots.push(paper.circle(centerX, centerY, 4).attr({stroke : "green", "stroke-width" : 2}));
		}
		for (var i=0; i<res.misses.length; i++) {
			var centerX = res.misses[i].x * paper.width;
			var centerY = res.misses[i].y * paper.height;
			paper.shots.push(paper.path("M" + (centerX - 3) + "," + (centerY - 3) + "L" + (centerX + 3) + "," + (centerY + 3) +
				"M" + (centerX + 3) + "," + (centerY - 3) + "L" + (centerX - 3) + "," + (centerY + 3)).attr({"stroke" : "red", "stroke-width" : 2}));
	
		}
	});
}