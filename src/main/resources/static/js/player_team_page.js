var paper;

$(document).ready(function(){
	console.log("a");
	console.log(document.getElementById("forCharts"));
	paper = Raphael(document.getElementById("forCharts"), 450, 420);
	paper.shots = paper.set();
	paper.image("/images/Basketball-Court-half.png",0,0,450,420).attr({"fill" : "white"});
	paper.rect(0,0,450,420).attr({fill : "rgba(150,150,150,.3)"});

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

function updatePlayer() {
	console.log($("#playerFormName")[0].value, $("#playerFormNumber")[0].value);
	alert("Wish we could!");
}

function updateTeam() {
	console.log($("#teamFormName")[0].value, $("#teamFormCoach")[0].value, $("#teamFormPrimary")[0].value, $("#teamFormSecondary")[0].value)
	alert("Wish we could!");
}