var paper;

var width, height;

var statNames = ["Points", "Field Goals Made and Attempted", "2 Pointers Made and Attempted", "3 Pointers Made and Attempted",
		 "Free Throws Made and Attempted", "Offensive Rebounds", "Defensive Rebounds",
		 "Rebounds", "Assists", "Steals", "Blocks", "Turnovers", "Personal Fouls"];

$(document).ready(function(){
    console.log("a");
    console.log(document.getElementById("forCharts"));
    width = 450;
    height = 420;
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

    $("tr a").click(function(e) {
    	e.stopPropagation();
    });
});

var chart;

function clickStatType(columnIndex) {
    $("#chart-title").html("<b>" + statNames[columnIndex - 1] + "</b>");
    paper.clear();
    
    var table = $("#player-game-stats")[0];
    if(columnIndex == 2 || columnIndex == 3 || columnIndex == 4 || columnIndex == 5) {
	var xs = [];
	var ys1 = [];
	var ys2 = [];
	var minMax = [];
	var maxIndex = table.rows.length - 1;
	for(i = 1; i < table.rows.length; i++) {
	    xs[i - 1] = i - 1;
	    var split = table.rows[i].cells[columnIndex].innerHTML.split(" - ");
	    var y1 = parseInt(split[0]);
	    var y2 = parseInt(split[1]);
	    ys1[i - 1] = y1;
	    ys2[i - 1] = y2;
	    minMax[i - 1] = 0;
	    if(minMax[maxIndex] == undefined) {
		minMax[maxIndex] = Math.max(y1, y2);
	    } else {
		minMax[maxIndex] = Math.max(y1, y2, minMax[maxIndex]);
	    }
	}
	
	var yStep = Math.floor(minMax[maxIndex] / (Math.floor(minMax[maxIndex] / 40) + 1));

	chart = paper.linechart(15, 0, width - 15, height - 15,
				xs, [ys1, ys2, minMax],
				{colors: ["#090", "#00f", "transparent"],
				 axisxstep: xs.length - 1, axisystep: yStep,
				 axis: "0 0 1 1"});
	for(i = 0; i < chart.axis.length; i++) {
	    var axis = chart.axis[i];
	    for(j = 0; j < axis.text.length; j++) {
		axis.text[j][0].childNodes[0].setAttribute("dy", 3.5);
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
	$("tspan", madeLegend.node).attr("dy", 3.5);
	$("tspan", attLegend.node).attr("dy", 3.5);
	
    } else {
	var xs = [];
	var ys = [];
	var minMax = [];
	var maxIndex = table.rows.length - 1;
	for(i = 1; i < table.rows.length; i++) {
	    xs[i - 1] = i - 1;
	    var y1 = parseInt(table.rows[i].cells[columnIndex].innerHTML);
	    ys[i - 1] = y1;
	    minMax[i - 1] = 0;
	    if(minMax[maxIndex] == undefined) {
		minMax[maxIndex] = y1;
	    } else {
		minMax[maxIndex] = Math.max(y1, minMax[maxIndex]);
	    }
	}

	var yStep = Math.floor(minMax[maxIndex] / (Math.floor(minMax[maxIndex] / 40) + 1));

	chart = paper.linechart(15, 0, width - 15, height - 15,
				xs, [ys, minMax],
				    {colors: ["#090", "transparent"],
				     axisxstep: xs.length - 1, axisystep: yStep,
				     axis: "0 0 1 1"});
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

function deletePlayer(playerID) {
    bootbox.confirm("Are you sure you want to delete this player?", function(result) {
	if(result) {
	    $.post("/player/delete", {id: playerID}, 
		   function(response) {
		       console.log(response);
		       if(response.success) {
			   window.location.href = "/dashboard";
		       } else {
			   bootbox.alert("Cannot delete player that has stats");
		       }
		   }, "json");
	}
    });
}

function updatePlayer() {
    console.log($("#playerFormName")[0].value, $("#playerFormNumber")[0].value, $("#playerFormTeam").find(":selected").val(),
        $('#playerIsCurrent').is(':checked'));
    console.log($('#playerIsCurrent').is(':checked'), $('#playerIsRetired').is(':checked'));
    var name = $("#playerFormName")[0].value;
    var number = $("#playerFormNumber")[0].value;
    var teamID = $("#playerFormTeam").find(":selected").val();
    var current = $('#playerIsCurrent').is(':checked');

    console.log("current", current);

    $.post("/player/edit", {id : id, name : name, number: number, teamID : teamID, current: current}, function(responseJSON){
        window.location.href ="/player/view/" + id;
    });
}

function updateTeam() {
    console.log($("#teamFormName")[0].value, $("#teamFormCoach")[0].value, $("#teamFormPrimary")[0].value, $("#teamFormSecondary")[0].value);
    var name = $("#teamFormName")[0].value;
    var coach = $("#teamFormCoach")[0].value;
    var primary = $("#teamFormPrimary")[0].value;
    var secondary = $("#teamFormSecondary")[0].value;

    //return bacon;

    $.post("/team/edit", {id : id, name : name, coach : coach, primary : primary, secondary : secondary}, function(responseJSON) {
        window.location.href ="/team/view/" + id;
    });
}