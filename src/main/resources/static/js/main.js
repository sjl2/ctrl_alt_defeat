//GLOBAL HEAT MAP VARIABLES
var epsilon_x = .05;
var epsilon_y = .05;

function drawHeatMap(id, playerBoolean, year, paper) {

	var grid = [];
	var max_bin_count = 0;

	for(var i=0; i< (1/epsilon_x); i++) {
		grid[i] = [];
		for (var j=0; j< (1/epsilon_y); j++) {
			grid[i][j] = {};
			var temp = {};
			temp.made = 0;
			temp.missed = 0;
			grid[i][j].count = temp;
		}
	}

	postParams = {};
	postParams.player = playerBoolean;
	postParams.id = id;
	postParams.championshipYear = year;
	console.log(postParams);

	$.post("/dashboard/heatmap", postParams, function(responseJSON) {
		var res = JSON.parse(responseJSON);
		for (var i=0; i<res.makes.length; i++) {
			var xSpot = Math.floor(res.makes[i].x / epsilon_x);
			var ySpot = Math.floor(res.makes[i].y / epsilon_y);
			grid[xSpot][ySpot].count.made += 1;
			if (grid[xSpot][ySpot].count.made + grid[xSpot][ySpot].count.missed > max_bin_count) 
				max_bin_count = grid[xSpot][ySpot].count.made + grid[xSpot][ySpot].count.missed;
		}
		for (var i=0; i<res.misses.length; i++) {
			var xSpot = Math.floor(res.misses[i].x / epsilon_x);
			var ySpot = Math.floor(res.misses[i].y / epsilon_y);
			grid[xSpot][ySpot].count.missed += 1;
			if (grid[xSpot][ySpot].count.made + grid[xSpot][ySpot].count.missed > max_bin_count) 
				max_bin_count = grid[xSpot][ySpot].count.made + grid[xSpot][ySpot].count.missed;

		}

		

		for (var i=0; i<grid.length; i++) {
			for (var j=0; j<grid[0].length; j++) {
				var sizeFrac = (grid[i][j].count.made + grid[i][j].count.missed) / max_bin_count;
				console.log(sizeFrac);
				paper.shots.push(paper.circle((i+.5) * epsilon_x * paper.width, (j+.5) * epsilon_y * paper.height, 
					epsilon_y * paper.height * sizeFrac / 1.5)
					.attr({fill : heat(grid[i][j].count.made, grid[i][j].count.missed), "fill-opacity" : .8, "stroke-opacity" : 0}));
			}
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

function heat(a, b) {
	if (a + b != 0) {
		if (a >= b) {
			if (a != 0) {
				console.log("rgb(" + 255 * b/a + "," + 255 + "," + 0 + ")");
				return "rgb(" + 255 * b/a + "," + 255 + "," + 0 + ")";
			}
			else return "green";
		} else {
			if (b != 0) {
				console.log("rgb(" + 255 + "," + 255 * a/b + "," + 0 + ")");
				return "rgb(" + 255 + "," + 255 * a/b + "," + 0 + ")";
			}
			else return "red";
		}
	}
	else return "rgba(255, 255, 255, .7)";
	//else return "red";
}