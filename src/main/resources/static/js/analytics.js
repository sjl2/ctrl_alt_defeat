$(document).ready(function(){
    console.log("a");
    console.log(document.getElementById("forCharts"));
    width = window.innerWidth / 2.3; 
    height = 14 * width / 15; 
    $("#forCharts").css('width', width);
    paper = Raphael(document.getElementById("forCharts"), width, height);
    paper.shots = paper.set();
    
});

function seasonHeatMap() {
    var ids = [];
    $("select option:selected").each(function() {
        if (this.id.split("player")[1] != "-1") ids.push(this.id.split("player")[1]);
    });

    var grid = [];
    var max_bin_count = 0;
    var epsilon_x = .05;
    var epsilon_y = .05;

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

    $.post("/analytics/heatmap", {ids : JSON.stringify(ids)}, function(responseJSON) {
        console.log(responseJSON);
        var res = (responseJSON);

        paper.clear();
        paper.image("/images/Basketball-Court-half.png",0,0,width,height).attr({"fill" : "white"});
        paper.rect(0,0,width,height).attr({fill : "rgba(150,150,150,.3)"});

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

    }, "json");

}

function recentShotChart() {
    var ids = [];
    $("select option:selected").each(function() {
        if (this.id.split("player")[1] != "-1") ids.push(this.id.split("player")[1]);
    });

    $.post("/analytics/shotchart", {ids : JSON.stringify(ids)}, function(responseJSON) {
    var res = (responseJSON);
    console.log(res);

    if (res.errorMessage.length == 0) {
        paper.clear();
        paper.image("/images/Basketball-Court-half.png",0,0,width,height).attr({"fill" : "white"});
        paper.rect(0,0,width,height).attr({fill : "rgba(150,150,150,.3)"});

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
    } else {
        $("#error").html(res.errorMessage);
    }


    }, "json");
}

function updateLineUpScore() {
    var ids = [];
    $("select option:selected").each(function() {
        if (this.id.split("player")[1] != "-1") ids.push(this.id.split("player")[1]);
    });


    $.post("/analytics/ranking", {ids : JSON.stringify(ids)}, function(responseJSON) {
        $("#lineupScore")[0].innerHTML = "Lineup Score: " + responseJSON;
    }, "json");
}