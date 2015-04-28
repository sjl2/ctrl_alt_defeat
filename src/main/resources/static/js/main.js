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
	postParams = {};
	postParams.player = true;
	postParams.id = playerID;
	$.post("/dashboard/shotchart", postParams, function(responseJSON) {
		for (var i=0; i<res.makes.length; i++) {
			var centerX = x * shotChart.width;
			var centerY = y * shotChart.height;
			paper.circle(centerX, centerY, 4).attr({stroke : "green", "stroke-width" : 2});
		}
		for (var i=0; i<res.misses.length; i++) {
			var centerX = x * shotChart.width;
			var centerY = y * shotChart.height;
			return shotChart.path("M" + (centerX - 3) + "," + (centerY - 3) + "L" + (centerX + 3) + "," + (centerY + 3) +
				"M" + (centerX + 3) + "," + (centerY - 3) + "L" + (centerX - 3) + "," + (centerY + 3)).attr({"stroke" : "red", "stroke-width" : 2});
	
		}
	});
}