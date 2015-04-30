var paper;

var width, height;

var statNames = ["Points", "Field Goals Made and Attempted", "2 Pointers Made and Attempted", "3 Pointers Made and Attempted",
		 "Free Throws Made and Attempted", "Offensive Rebounds", "Defensive Rebounds",
		 "Rebounds", "Assists", "Steals", "Blocks", "Turnovers", "Personal Fouls"];

$(document).ready(function(){
    console.log("a");
    console.log(document.getElementById("forCharts"));
    width = window.innerWidth / 2.3; 
    height = 14 * width / 15; 
    $("#forCharts").css('width', width);
    paper = Raphael(document.getElementById("forCharts"), width, height);
    paper.shots = paper.set();
    

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

function clickStatType(columnIndex) {
    $("#chart-title").html("<b>" + statNames[columnIndex - 1] + "</b>");
    paper.clear();
    
    var table = $("#player-game-stats")[0];
    if(columnIndex == 2 || columnIndex == 3 || columnIndex == 4 || columnIndex == 5) {
	var xs = [];
	var ys1 = [];
	var ys2 = [];
	for(i = 1; i < table.rows.length; i++) {
	    xs[i - 1] = i;
	    var split = table.rows[i].cells[columnIndex].innerHTML.split(" - ");
	    ys1[i - 1] = parseInt(split[0]);
	    ys2[i - 1] = parseInt(split[1]);
	}

	var chart;
	chart = paper.linechart(15, 0, width - 15, height, xs, ys1, {colors: ["#090"], axis: "0 0 1 1"});
	for(i = 0; i < chart.axis.length; i++) {
	    var axis = chart.axis[i];
	    for(j = 0; j < axis.text.length; j++) {
		axis.text[j][0].childNodes[0].setAttribute("dy", 7.5);
	    }
	}
	chart = paper.linechart(15, 0, width - 15, height, xs, ys2, {colors: ["#00f"]});
	for(i = 0; i < chart.axis.length; i++) {
	    var axis = chart.axis[i];
	    for(j = 0; j < axis.text.length; j++) {
		axis.text[j][0].childNodes[0].setAttribute("dy", 7.5);
	    }
	}
	var legendX = 35;
	var legendY = 25;
	var legendWidth = 90;
	var legendHeight = 60;
	paper.rect(legendX, legendY, legendWidth, legendHeight);
	paper.rect(legendX + 10, legendY + 10, 15, 15).attr("fill", "#090");
	paper.rect(legendX + 10, legendY + 35, 15, 15).attr("fill", "#00f");
	var madeLegend = paper.text(legendX + 30, legendY + 20, "Made").attr("text-anchor", "start");
	var attLegend = paper.text(legendX + 30, legendY + 45, "Attempted").attr("text-anchor", "start");
	$("tspan", madeLegend.node).attr("dy", 7.5);
	$("tspan", attLegend.node).attr("dy", 7.5);
	
    } else {
	var xs = [];
	var ys = [];
	for(i = 1; i < table.rows.length; i++) {
	    xs[i - 1] = i;
	    ys[i - 1] = parseInt(table.rows[i].cells[columnIndex].innerHTML);
	}
	
	var chart = paper.linechart(15, 0, width - 15, height, xs, ys, {colors: ["#090"], axisxstep: 2, axisystep: 2, axis: "0 0 1 1"});
	for(i = 0; i < chart.axis.length; i++) {
	    var axis = chart.axis[i];
	    for(j = 0; j < axis.text.length; j++) {
		axis.text[j][0].childNodes[0].setAttribute("dy", 7.5);
	    }
	}
    }
}

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