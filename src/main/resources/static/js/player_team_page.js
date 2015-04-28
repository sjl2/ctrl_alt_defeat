var paper;

$(document).ready(function(){
	console.log("a");
	console.log(document.getElementById("forCharts"));
	paper = Raphael(document.getElementById("forCharts"), 450, 420);
	paper.shots = paper.set();
	paper.image("/images/Basketball-Court-half.png", 0, 0, 450, 420).attr({"fill" : "white"});

	$("#season").change(function () {

		postParameters = {
			year: $("#season").val(), 
			playerID: id
		}
		console.log("Year: "+ postParameters.year);

		$.post("/player/get/year", postParameters, function (responseHTML) {
				$("#season-player").html(responseHTML); 
		});
	});

});


function clickPlayerGame(playerID, gameID) {
	paper.shots.remove();
	drawShotChart(playerID, gameID, paper);
}
