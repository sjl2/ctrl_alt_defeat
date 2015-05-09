$('.modal-chart').modal({ show: false});

$(document).ready(function(){
    console.log("a");
    console.log(document.getElementById("forCharts"));
    var forCharts = $("#forCharts");
    width = 450;
    height = 420; 
    $("#forCharts").css('width', width);
    paper = Raphael(document.getElementById("forCharts"), width, height);
    paper.shots = paper.set();

    $("tr a").click(function(e) {
    	e.stopPropagation();
    });
});

function clickPlayerGame(playerID, gameID) {
    $("#chart-title").html("<b>Shot Chart</b>");
    paper.shots.remove();
    drawShotChart(playerID, true, gameID, paper);
    $('.modal-chart').modal({ show: true});
}

function clickTeamGame(teamID, gameID) {
    $("#chart-title").html("<b>Shot Chart</b>");
    paper.shots.remove();
    drawShotChart(teamID, false, gameID, paper);
    $('.modal-chart').modal({ show: true});
}
