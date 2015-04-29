var paper;

$(document).ready(function(){
	console.log("a");
	console.log(document.getElementById("forCharts"));
	var width = window.innerWidth / 2.3; 
	var height = 14 * width / 15; 
	$("#forCharts").css('width', width);
	paper = Raphael(document.getElementById("forCharts"), width, height);
	paper.shots = paper.set();
	paper.image("/images/Basketball-Court-half.png",0,0,width,height).attr({"fill" : "white"});
	paper.rect(0,0,width,height).attr({fill : "rgba(150,150,150,.3)"});

	$("#years").change(function () {

		postParameters = {
			year: $("#years").val(), 
			id: id,
			isPlayer: isPlayer
		}

		$.post("/season/get", postParameters, function (responseHTML) {
				$("#season").html(responseHTML); 
		});
	});

});


function clickPlayerGame(playerID, gameID) {
	$("#chart-title").html("<b>Shot Chart</b>");
	paper.shots.remove();
	drawShotChart(playerID, true, gameID, paper);
}

function clickPlayerSeason(playerID, year) {
	$("#chart-title").html("<b>Heat Map for " + (year - 1) + " - " + year + "</b>");
	paper.shots.remove();
	drawHeatMap(playerID, true, year, paper);
}

function clickTeamGame(teamID, gameID, gameName) {
	$("#chart-title").html("<b>Shot Chart</b>");
	paper.shots.remove();
	drawShotChart(teamID, false, gameID, paper);
}

function clickTeamSeason(teamID, year) {
	$("#chart-title").html("<b>Heat Map for " + (year - 1) + " - " + year + "</b>");
	paper.shots.remove();
	drawHeatMap(teamID, false, year, paper);
}