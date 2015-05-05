$(document).keypress(function(e) {
    if ( e.keyCode === 13 ) addStat(); //enter key
    else if (e.keyCode === 83) {
        $("#subs").modal('show');
        console.log("subs should've happened");
    }
});

$(function() {
    startScoreboard(250, 150);
})

// STAT GLOBALS
var clickedPoint; 
var clickedStat; 
var clickedPlayer; 
var mousedowninsomething = false;	
var subState = false;
var editingStat;

var subtexts;

var court = document.getElementById("court");
var home_team_div = document.getElementById("home_team");
var away_team_div = document.getElementById("away_team");
var sub_div = document.getElementById("sub_div");
var arrow_div = document.getElementById("arrows");
//size of court
court_width = 940 / 2.15;
court_height = 500 / 2.15;
court.setAttribute("style", "width:" + Math.round(court_width) + "px; height:" + Math.round(court_height) + "px");
court.style.width = Math.round(court_width);
court.style.height = Math.round(court_height);

var positions = ["PG", "SG", "SF", "PF", "C"];
var stats = [["Block", "DefensiveFoul", "DefensiveRebound", "FreeThrow","MissedFreeThrow", "MissedThreePointer", "MissedTwoPointer", "OffensiveFoul"],
["OffensiveRebound", "Steal", "TechnicalFoul", "Turnover","ThreePointer", "TwoPointer", "Assist", "STEWART\nRANGE!!!!"]];


var court_paper = Raphael(court, court.width, court.height);
court_paper.homePossession = false;

var home_team_paper = Raphael(home_team_div, 70, 315);
var away_team_paper = Raphael(away_team_div, 70, 315);
var sub_paper = Raphael(sub_div, 445, 400);
var arrow_paper = Raphael(arrow_div, court.width, 100);

arrow_paper.right = arrow_paper.path("M150,26L100,26L100,35L80,20L100,5L100,14L150,14Z").attr({"stroke-width" : 2});



court_paper.image("images/Basketball-Court.png", 0, 0, court_paper.width, court_paper.height);

home_team_paper.mainTexts = home_team_paper.set();
home_team_paper.mainBoxes = home_team_paper.set();

away_team_paper.mainTexts = away_team_paper.set();
away_team_paper.mainBoxes = away_team_paper.set();

$("#court").click(function(e) {
    if (!(clickedPoint === undefined)) clickedPoint.remove();
    clickedPoint = court_paper.circle(e.offsetX, e.offsetY, 4)
    .attr("fill", "#f00")
    .data("ratioX", e.offsetX / court_width)
    .data("ratioY", e.offsetY / court_height);

});

var home = {};
var away = {};
$.get("/game/roster", function(responseJSON) {
    var r = JSON.parse(responseJSON);
    
    if(r.errorMessage.length > 0) {
       return;
   }
   var res = r.roster;
   console.log(res);
   home.primary = res[0];
   home.secondary = res[1];
   home.teamID = res[4].players.HomePG.teamID;

   home.onCourt = [];
   console.log(res[4].players);
   home.onCourt.push(res[4].players.HomePG);
   home.onCourt.push(res[4].players.HomeSG);
   home.onCourt.push(res[4].players.HomeSF);
   home.onCourt.push(res[4].players.HomePF);
   home.onCourt.push(res[4].players.HomeC);
   home.bench = [];
   for (var i = 0; i < res[5].players.length; i++) {
       home.bench.push(res[5].players[i]);
   }

   away.primary = res[2];
   away.secondary = res[3];
   away.teamID = res[4].players.AwayPG.teamID;

   away.onCourt = [];
   away.onCourt.push(res[4].players.AwayPG);
   away.onCourt.push(res[4].players.AwaySG);
   away.onCourt.push(res[4].players.AwaySF);
   away.onCourt.push(res[4].players.AwayPF);
   away.onCourt.push(res[4].players.AwayC);
   away.bench = [];
   for (var i = 0; i < res[6].players.length; i++) {
       away.bench.push(res[6].players[i]);
   }

   var homeColor = home.primary;
   var homeTextColor = whiteOrBlack(Raphael.color(home.primary));

   var homeClick = makeDarker(Raphael.color(home.primary).hex);
   var homeAccent = home.secondary;

   var awayColor = away.primary;
   var awayTextColor = whiteOrBlack(Raphael.color(away.primary));

   var awayClick = makeDarker(Raphael.color(away.primary).hex);
   var awayAccent = away.secondary;  

   $("#homeTeamLabel")[0].innerHTML = "<strong>" + res[7] + "</strong>";
   $("#awayTeamLabel")[0].innerHTML = "<strong>" + res[8] + "</strong>";


   var rightPoint = 80;
   var leftPoint = arrow_paper.width - 80;
   arrow_paper.right = arrow_paper.path("M150,26L100,26L100,35L80,20L100,5L100,14L150,14Z").attr({"stroke-width" : 2, fill : homeColor}).click(function(e){switchSides()});
   arrow_paper.left = arrow_paper.path("M"+ (leftPoint - 70) + ",26L"+ (leftPoint - 20) + ",26L"+ (leftPoint - 20) + ",35L" + leftPoint + ",20L"+ (leftPoint - 20) + ",5L"+ (leftPoint - 20) + ",14L"+ (leftPoint - 70) + ",14Z")
   .attr({"stroke-width" : 2, fill : awayColor}).click(function(e){switchSides()});


   for (var i = 0; i < 5; i++) {
       var tempBox = home_team_paper.rect(10, 10 + 55 * i, 50, 50, 10).attr({fill: homeColor, stroke: 'black', 'stroke-width': 2})
       .data("thing", "player").data("bench", false).data("home", true);
       var tempText = home_team_paper.text(35, 10 + 55 * i + 25, home.onCourt[i].number).attr({"font-family": "Arial", "font-size":16, stroke:homeTextColor});
       tempText.box = tempBox;
       tempBox.glowColor = homeAccent;
       tempBox.clickAccent = homeClick;
       tempBox.normalColor = homeColor;
       tempBox.t = tempText;
       tempText.toFront();

       tempBox.player = home.onCourt[i];

       home_team_paper.mainTexts.push(tempText);
       home_team_paper.mainBoxes.push(tempBox);
   }

   for (var i = 0; i < home.bench.length; i++) {
       var tempBox = home_team_paper.rect(10, 10 + 30 * (i), 50, 25, 10).attr({fill: homeColor, stroke: 'black', 'stroke-width': 2})
       .data("thing", "player").data("bench", true).data("home", true);
       var tempText = home_team_paper.text(35, 10 + 30 * (i) + 12.5, home.bench[i].number).attr({"font-family": "Arial", "font-size":16, stroke:homeTextColor});
       tempText.box = tempBox;
       tempBox.glowColor = homeAccent;
       tempBox.clickAccent = homeClick;
       tempBox.normalColor = homeColor;
       tempBox.t = tempText;
       tempText.toFront();

       tempBox.player = home.bench[i];
       tempText.hide();
       tempBox.hide();
       home_team_paper.mainTexts.push(tempText);
       home_team_paper.mainBoxes.push(tempBox);
   }
   home_team_paper.showingBench = false;



   for (var i = 0; i < 5; i++) {
       var tempBox = away_team_paper.rect(10, 10 + 55 * i, 50, 50, 10).attr({fill: awayColor, stroke: 'black', 'stroke-width': 2})
       .data("thing", "player").data("bench", false).data("home", false);
       var tempText = away_team_paper.text(35, 10 + 55 * i + 25, away.onCourt[i].number).attr({"font-family": "Arial", "font-size":16, stroke:awayTextColor});
       tempText.box = tempBox;
       tempBox.glowColor = awayAccent;
       tempBox.clickAccent = awayClick;
       tempBox.normalColor = awayColor;
       tempBox.t = tempText;
       tempText.toFront();

       tempBox.player = away.onCourt[i];

       away_team_paper.mainTexts.push(tempText);
       away_team_paper.mainBoxes.push(tempBox);
   }

   for (var i = 0; i < away.bench.length; i++) {
       console.log("but they have a bench...");
       var tempBox = away_team_paper.rect(10, 10 + 30 * (i), 50, 25, 10).attr({fill: awayColor, stroke: 'black', 'stroke-width': 2})
       .data("thing", "player").data("bench", true).data("home", false);
       var tempText = away_team_paper.text(35, 10 + 30 * (i) + 12.5, away.bench[i].number).attr({"font-family": "Arial", "font-size":16, stroke:awayTextColor});
       tempText.box = tempBox;
       tempBox.glowColor = awayAccent;
       tempBox.clickAccent = awayClick;
       tempBox.normalColor = awayColor;
       tempBox.t = tempText;
       tempText.toFront();

       tempBox.player = away.bench[i];
       tempBox.hide();
       tempText.hide();
       away_team_paper.mainTexts.push(tempText);
       away_team_paper.mainBoxes.push(tempBox);
   }

   away_team_paper.showingBench = false;

   home_team_paper.mainBoxes.mousedown(function(e) {
       this.attr({fill: this.clickAccent});
       mousedowninsomething = true;
   });
   home_team_paper.mainBoxes.mouseup(function(e) {
       clickThing(this);
       mousedowninsomething = false;

   });
   home_team_paper.mainBoxes.mouseout(function(e) {
       if (mousedowninsomething) {
           clickThing(this);
           mousedowninsomething = false;
       }
   });

   away_team_paper.mainBoxes.mousedown(function(e) {
       this.attr({fill: this.clickAccent});
       mousedowninsomething = true;
   });
   away_team_paper.mainBoxes.mouseup(function(e) {
       clickThing(this);
       mousedowninsomething = false;

   });
   away_team_paper.mainBoxes.mouseout(function(e) {
       if (mousedowninsomething) {
           clickThing(this);
           mousedowninsomething = false;
       }
   });



   home_team_paper.mainTexts.mousedown(function(e) {
       this.box.attr({fill: this.box.clickAccent});
       mousedowninsomething = true;

   });
   home_team_paper.mainTexts.mouseup(function(e) {
       clickThing(this.box);
       mousedowninsomething = false;
   });

   away_team_paper.mainTexts.mousedown(function(e) {
       this.box.attr({fill: this.box.clickAccent});
       mousedowninsomething = true;

   });
   away_team_paper.mainTexts.mouseup(function(e) {
       clickThing(this.box);
       mousedowninsomething = false;
   });

   home_team_paper.mainTexts.mouseover(function(e) {
       this.box.g = this.box.glow({
           opacity: 0.85,
           color: this.box.glowColor,
           width: 15
       });
   }).mouseout(function (e) {
       this.box.g.remove();
   });

   away_team_paper.mainTexts.mouseover(function(e) {
       this.box.g = this.box.glow({
           opacity: 0.85,
           color: this.box.glowColor,
           width: 15
       });
   }).mouseout(function (e) {
       this.box.g.remove();
   });





   home_team_paper.mainBoxes.mouseover(function (event) {
       this.g = this.glow({
           opacity: 0.85,
           color: this.glowColor,
           width: 15
       });
   }).mouseout(function (event) {
       this.g.remove();
   });

   away_team_paper.mainBoxes.mouseover(function (event) {
       this.g = this.glow({
           opacity: 0.85,
           color: this.glowColor,
           width: 15
       });
   }).mouseout(function (event) {
       this.g.remove();
   });






   sub_paper.subWindow = sub_paper.rect(0, 30, 445, 370).attr({fill : "white", "stroke-width" : 1}).mousemove(function(e) {
       if (!(this.currentMove === undefined)) {
        console.log(sub_paper.subWindow.currentMove);
           this.currentMove.attr({x : e.x-sub_paper.subWindow.mouseDownSpotX + sub_paper.subWindow.currentMove.defaultX, y : e.y- sub_paper.subWindow.mouseDownSpotY + sub_paper.subWindow.currentMove.defaultY});
           this.currentMove.number.attr({x : e.x-sub_paper.subWindow.mouseDownSpotX + sub_paper.subWindow.currentMove.defaultX + 25, y : e.y - sub_paper.subWindow.mouseDownSpotY + sub_paper.subWindow.currentMove.defaultY + 12.5});
       }
   }).mouseup(function(e) {
        if (!(this.currentMove === undefined)) {
            this.currentMove.attr({x : this.currentMove.defaultX});
            this.currentMove.attr({y : this.currentMove.defaultY});
            this.currentMove.number.attr({x : this.currentMove.defaultX + 25, y : this.currentMove.defaultY + 12.5});
            this.currentMove = undefined;
        }
   });
   var words_home = sub_paper.text(80,15, "Home Bench");
   var words_away = sub_paper.text(365,20, "Away Bench");
   var words_main = sub_paper.text(222.5,20, "On The Court").attr({"font-size" : 20});

   $('tspan', words_home.node).attr('dy', 3.5);
   $('tspan', words_away.node).attr('dy', 3.5);
   $('tspan', words_main.node).attr('dy', 3.5);



   sub_paper.subWindow.currentMove = undefined;

   var benchDots = sub_paper.set();
   var starterBoxes = sub_paper.set();

   subtexts = sub_paper.set();
   var counts = {};
   counts.homeBench = 0;
   counts.homeOn = 0;
   counts.awayBench = 0;
   counts.awayOn = 0;

   home_team_paper.mainBoxes.forEach(function (obj) {
       if (obj.data("bench")) {
           //var benchTemp = sub_paper.circle(80 - (40 * Math.floor(counts.homeBench/5)),140 + 40 * (counts.homeBench % 5),10).attr({fill : "lightblue"});
           console.log("here");
           var benchTemp = sub_paper.rect(60, 40 + 30 * (counts.homeBench),50,25, 10).attr({"stroke-width":2, fill : makeDarker(obj.attr("fill"))});
           benchTemp.defaultX = 60;
           benchTemp.defaultY = 40 + 30 * (counts.homeBench);
           counts.homeBench += 1;

           benchTemp.player = obj.player;
           var tempTexts = sub_paper.text(benchTemp.defaultX + 25, benchTemp.defaultY + 12.5, benchTemp.player.number).attr({"font-family": "Arial", "font-size":16, stroke:obj.t.attr("stroke")});
           $('tspan', tempTexts.node).attr('dy', 3.5);
           console.log('words: ', tempTexts);
           console.log('circle: ', benchTemp);
           tempTexts.circ = benchTemp;
           benchTemp.number = tempTexts;
           subtexts.push(tempTexts);
           benchDots.push(benchTemp);
       } else {
           var temp = sub_paper.rect(160, 40 + 75 * counts.homeOn, 50, 50, 10).attr({"stroke-width" : 2, fill : obj.attr("fill")});
           temp.defaultX = 160 + 22.5;
           temp.defaultY = 40 + 75 * counts.homeOn + 22.5;
           counts.homeOn += 1;

           temp.player = obj.player;
           var tempTexts = sub_paper.text(temp.defaultX, temp.defaultY, temp.player.number).attr({"font-family": "Arial", "font-size":16, stroke:obj.t.attr("stroke")});
           $('tspan', tempTexts.node).attr('dy', 3.5);
           temp.number = tempTexts;
           tempTexts.circ = temp;
           subtexts.push(tempTexts);
           starterBoxes.push(temp);
       }
   });

away_team_paper.mainBoxes.forEach(function(obj) {
	if (obj.data("bench")) {
       //var temp = sub_paper.circle(365 + (40 * Math.floor(counts.awayBench/5)) ,140 + 40 * (counts.awayBench % 5),10).attr({fill : "lightblue"});
        var temp = sub_paper.rect(340, 40 + 30 * (counts.awayBench),50,25, 10).attr({"stroke-width":2, fill : makeDarker(obj.attr("fill"))});
       temp.defaultX = 340;
       temp.defaultY = 40 + 30 * (counts.awayBench);
       counts.awayBench += 1;

       temp.player = obj.player;
       var tempTexts = sub_paper.text(temp.defaultX + 25, temp.defaultY + 12.5, temp.player.number).attr({"font-family": "Arial", "font-size":16, stroke:obj.t.attr("stroke")});
       $('tspan', tempTexts.node).attr('dy', 3.5);
       tempTexts.circ = temp;
       temp.number = tempTexts;
       subtexts.push(tempTexts);
       benchDots.push(temp);
   } else {
       var temp = sub_paper.rect(240, 40 + 75 * counts.awayOn, 50, 50, 10).attr({"stroke-width" : 2, fill : obj.attr("fill")});
       temp.defaultX = 240 + 22.5;
       temp.defaultY = 40 + 75 * counts.awayOn + 22.5;
       counts.awayOn += 1;

       temp.player = obj.player;
       var tempTexts = sub_paper.text(temp.defaultX, temp.defaultY, temp.player.number).attr({"font-family": "Arial", "font-size":16, stroke:obj.t.attr("stroke")});
       $('tspan', tempTexts.node).attr('dy', 3.5);
       temp.number = tempTexts;
       tempTexts.circ = temp;
       subtexts.push(tempTexts);
       starterBoxes.push(temp);
   }
});

benchDots.forEach(function(o) {
	makeDraggable(o, sub_paper.subWindow);
});	

subtexts.forEach(function(obj) {
	obj.toFront();
	obj.mousedown(function (e) {
        if (obj.circ.attr("height") == 25) {
       sub_paper.subWindow.currentMove = obj.circ;
       sub_paper.subWindow.mouseDownSpotX = e.x;
       sub_paper.subWindow.mouseDownSpotY = e.y;}
   });
	obj.mousemove(function (e) {
       if (!(sub_paper.subWindow.currentMove === undefined)) {
          sub_paper.subWindow.currentMove.attr({x : e.x-sub_paper.subWindow.mouseDownSpotX + sub_paper.subWindow.currentMove.defaultX, y : e.y- sub_paper.subWindow.mouseDownSpotY + sub_paper.subWindow.currentMove.defaultY});
          sub_paper.subWindow.currentMove.number.attr({x : e.x-sub_paper.subWindow.mouseDownSpotX + sub_paper.subWindow.currentMove.defaultX + 25, y : e.y- sub_paper.subWindow.mouseDownSpotY + sub_paper.subWindow.currentMove.defaultY + 12.5});
      }

  });
	obj.mouseup(function (e) {
       sub_paper.subWindow.currentMove.attr({x : sub_paper.subWindow.currentMove.defaultX, y : sub_paper.subWindow.currentMove.defaultY});
       sub_paper.subWindow.currentMove.number.attr({x : sub_paper.subWindow.currentMove.defaultX + 25, y : sub_paper.subWindow.currentMove.defaultY + 12.5});
       starterBoxes.forEach(function (o) {
            console.log(o.getBBox(), e.x-sub_paper.subWindow.mouseDownSpotX + sub_paper.subWindow.currentMove.defaultX,
                 e.y- sub_paper.subWindow.mouseDownSpotY + sub_paper.subWindow.currentMove.defaultY);
          if (Raphael.isPointInsideBBox(o.getBBox(), 
            e.x-sub_paper.subWindow.mouseDownSpotX + sub_paper.subWindow.currentMove.defaultX + 25, 
            e.y- sub_paper.subWindow.mouseDownSpotY + sub_paper.subWindow.currentMove.defaultY + 12.5)) {
            console.log("inside of here");
              if (o.player.teamID == sub_paper.subWindow.currentMove.player.teamID) {
                 console.log(o.player, obj.circ.player);

                 sub(o.player.teamID == home.teamID, sub_paper.subWindow.currentMove.player.number, o.player.number);
			//sub_paper.subWindow.currentMove.hide();

			var temp = o.player;
			o.player = sub_paper.subWindow.currentMove.player;
			sub_paper.subWindow.currentMove.player = temp;


			var a = o.number.attr("text");
			o.number.attr({text : sub_paper.subWindow.currentMove.number.attr("text")});
			sub_paper.subWindow.currentMove.number.attr({text : a});
      }
  }
});
       sub_paper.subWindow.currentMove = undefined;

   });
});

function makeDraggable(obj, sw) {
	obj.mousedown(function (e) {
        this.toFront();
        this.number.toFront();
       sw.currentMove = this;
       sw.mouseDownSpotX = e.x;
       sw.mouseDownSpotY = e.y;
   });
	obj.mousemove(function (e) {
       if (sw.currentMove !== undefined) {
          sw.currentMove.attr({x : e.x-sub_paper.subWindow.mouseDownSpotX + sub_paper.subWindow.currentMove.defaultX, y : e.y- sub_paper.subWindow.mouseDownSpotY + sub_paper.subWindow.currentMove.defaultY});
          sw.currentMove.number.attr({x : e.x-sub_paper.subWindow.mouseDownSpotX + sub_paper.subWindow.currentMove.defaultX + 25, y : e.y- sub_paper.subWindow.mouseDownSpotY + sub_paper.subWindow.currentMove.defaultY + 12.5});
      }
  });
	obj.mouseup(function (e) {
       sw.currentMove.attr({x : sw.currentMove.defaultX});
       sw.currentMove.attr({y : sw.currentMove.defaultY});
       sw.currentMove.number.attr({x : sw.currentMove.defaultX + 25, y : sw.currentMove.defaultY + 12.5});
       sw.currentMove = undefined;


       starterBoxes.forEach(function (o) {
          if (Raphael.isPointInsideBBox(o.getBBox(), e.offsetX, e.offsetY)) {
              if (o.player.teamID == obj.player.teamID) {
                 sub(o.player.teamID == home.teamID, obj.player.number, o.player.number);
			//obj.hide();

			var temp = o.player;
			o.player = obj.player;
			obj.player = temp;

			var a = o.number.attr("text");
			o.number.attr({text : obj.number.attr("text")});
			obj.number.attr({text : a});
      }
  }
});

   });
}


});	

function switchSides() {
    var a = arrow_paper.right.attr("fill");
    arrow_paper.right.attr({fill : arrow_paper.left.attr("fill")});
    arrow_paper.left.attr({fill : a});
}


function endGame() {
    $.post("/stats/endgame", {}, function(responseJSON) {
        window.location.href = "/stats";
    });
}

function advancePeriod() {
    $.post("/stats/advanceperiod", {}, function(responseJSON) {
        console.log(responseJSON);
    });
}


function fp() {
    if (court_paper.homePossession) {
       court_paper.homePossession = false;

       $("#flipPossession")[0].innerHTML = "Possession <span class=\"glyphicon glyphicon-triangle-right\" aria-hidden=\"true\"></span>";

   } else {
       court_paper.homePossession = true;

       $("#flipPossession")[0].innerHTML = "Possession <span class=\"glyphicon glyphicon-triangle-left\" aria-hidden=\"true\"></span>";

   }
   $.post("/stats/changepossession", {}, function(responseJSON) {});

}

function toggleBenchCourt(b) {
    if (b) paper = home_team_paper;
    else paper = away_team_paper;
    paper.mainBoxes.forEach(function (obj) {
       if ((obj.data("bench") && paper.showingBench) || (!(obj.data("bench")) && !(paper.showingBench))) {
           obj.hide();
           obj.t.hide();
       } else {
           obj.show();
           obj.t.show();
       }
   });
    var button;
    if (b) button = $("#showHomeSubs");
    else button = $("#showAwaySubs");
    if (paper.showingBench) button.html("Show Bench");
    else button.html("Show Court");
    paper.showingBench = !(paper.showingBench);   
}



function sub(h, inPlayer, outPlayer) {

    var inBox;
    var outBox;

    if (h) {
       home_team_paper.mainBoxes.forEach(function (t) {
           if (t.data("bench")) {
              if (t.player.number == parseInt(inPlayer)) inBox = t;
          } else {
              if (t.player.number == parseInt(outPlayer)) outBox = t;
          }
      });
   } else {
       away_team_paper.mainBoxes.forEach(function (t) {
           if (t.data("bench")) {
              if (t.player.number == parseInt(inPlayer)) inBox = t;
          } else {
              if (t.player.number == parseInt(outPlayer)) outBox = t;
          }
      });
   }

   if (!(inBox === undefined || outBox === undefined) && inBox.data("home") == outBox.data("home")) {
       var t;
       if (h) t = home;
       else t = away;

       var courtSpot;
       var benchSpot;
       for (var i = 0; i < t.onCourt.length; i++) {
           if (t.onCourt[i] == outBox.player) courtSpot = i;
       }
       for (var j = 0; j < t.bench.length; j++) {
           if (t.onCourt[j] == outBox.player) courtSpot = j;
       }
       var temp = t.onCourt[courtSpot];
       t.onCourt[courtSpot] = t.bench[benchSpot];
       t.bench[benchSpot] = temp;

       var temp = inBox.player;
       inBox.player = outBox.player;
       outBox.player = temp;
       inBox.t.attr({"text" : inBox.player.number});
       outBox.t.attr({"text" : outBox.player.number});



       $.post("/stats/sub", {"out" : inBox.player.id, "in" : outBox.player.id, "home" : h}, function(){});
   } else alert("Sub was invalid! Sorry");


}


function clickPlayer(paper, id) {
	paper.mainBoxes.forEach(function (obj) {
		if (obj.player.id == id) {
			if (!(clickedPlayer === undefined)) clickedPlayer.attr({fill: clickedPlayer.normalColor});
			clickedPlayer = obj;
			clickedPlayer.attr({fill: clickedPlayer.clickAccent});
		}
	});
}

function clickThing(b) {
    if (b.data("thing") == "player") {
       if (!(clickedPlayer === undefined)) clickedPlayer.attr({fill: clickedPlayer.normalColor});
       clickedPlayer = b;
       clickedPlayer.attr({fill: clickedPlayer.clickAccent});
   }
} 


function addStat() {
    if ((clickedStat.statID != "TwoPointer") && (clickedStat.statID != "MissedTwoPointer") && (clickedStat.statID != "ThreePointer") && (clickedStat.statID != "MissedThreePointer")) {
       if (clickedPoint === undefined) {
           clickedPoint = court_paper.circle(0,0,0).data("ratioX", -1).data("ratioY", -1);
       }
   } else {
       if (clickedPoint === undefined) alert("You must give a location for all field goal attempts");
   }
   if (!(clickedPoint === undefined || clickedStat === undefined || clickedPlayer === undefined)) {
    if (clickedPlayer.data("bench") && !(clickedStat.statID == "TechnicalFoul")) {
        alert("Players on bench can only get technical fouls");
        return;
    }
       postParameters = {
           x: clickedPoint.data("ratioX"),
           y: clickedPoint.data("ratioY"),
           statID: clickedStat.statID,
           playerID: clickedPlayer.player.id
       };
       clickedPoint.remove();
       clickedPoint = undefined;
       clickedPlayer.attr({fill: clickedPlayer.normalColor});
       clickedPlayer = undefined;
       clickedStat.button.style.backgroundColor = "white";
	//clickedStat.statID = undefined;//TODO what should be undefined
	clickedStat = undefined;


	console.log(postParameters);

	$.post("/stats/add", postParameters, function(response) {
       logStat(response);
   });

}

}

function logStat(res, type) {
    console.log(res);
    $("#ticker").prepend(res);
}


function makeDarker(h) {
    console.log(h);
    var r = parseInt(((h.charAt(0)=="#") ? h.substring(1,7):h).substring(0,2),16);
    var g = parseInt(((h.charAt(0)=="#") ? h.substring(1,7):h).substring(2,4),16);
    var b = parseInt(((h.charAt(0)=="#") ? h.substring(1,7):h).substring(4,6),16);

    if (r*0.299 + g*0.587 + b*0.114 > 186) return "rgb(" + r * .6 + "," + g * .6 + "," +  b * .6 + ")";
    else return "rgb(" + r * 1.5 + "," + g * 1.5 + "," +  b * 1.5 + ")";
}

function whiteOrBlack(color) {
    if (color.r*0.299 + color.g*0.587 + color.b*0.114 > 186) return "black";
    else return "white";
}

function setSelectedStat(str) {
    if (clickedStat !== undefined) clickedStat.button.style.backgroundColor = "white";
    clickedStat = {};
    clickedStat.statID = str;
    clickedStat.button = document.getElementById(str + "Button");
    document.getElementById(str + "Button").style.backgroundColor = "rgb(150,150,150)";
}

function timeout(b) {
    if (b) {
        $.post("/stats/timeout", {h : true}, function(responseJSON){});
    }
    else {
        $.post("/stats/timeout", {h : false}, function(responseJSON){});
    }
}

function statEdit(a) {
    if(editingStat == undefined) {
    	console.log("first case");
       editingStat = a;
       a.className = "btn btn-xs btn-primary pull-right editButton";
       a.childNodes[1].className = "glyphicon glyphicon-floppy-save";
       console.log(a.parentNode);
       var x = court_paper.width * a.parentNode.getAttribute("data-statX");
       var y = court_paper.height * a.parentNode.getAttribute("data-statY");
       if (!(clickedPoint === undefined)) clickedPoint.remove();
       clickedPoint = court_paper.circle(x, y, 4).attr("fill", "#f00")
       .data("ratioX", a.parentNode.getAttribute("data-statX"))
       .data("ratioY", a.parentNode.getAttribute("data-statY"));

       clickPlayer(home_team_paper, a.parentNode.getAttribute("data-playerID"));
       clickPlayer(away_team_paper, a.parentNode.getAttribute("data-playerID"));
       setSelectedStat(a.parentNode.getAttribute("data-statType"));

       $("#sendStat").prop("disabled", true);

   } else if(editingStat != a) {
      console.log("second case");
      $(".editButton").removeClass("btn-primary").addClass("btn-warning");
      $(".editButton").children().removeClass("glyphicon-floppy-save").addClass("glyphicon-pencil");
      a.className = "btn btn-xs btn-primary pull-right editButton";
      a.childNodes[1].className = "glyphicon glyphicon-floppy-save";
      editingStat = a;

      var x = court_paper.width * a.parentNode.getAttribute("data-statX");
      var y = court_paper.height * a.parentNode.getAttribute("data-statY");
      if (!(clickedPoint === undefined)) clickedPoint.remove();
      clickedPoint = court_paper.circle(x, y, 4).attr("fill", "#f00")
      .data("ratioX", a.parentNode.getAttribute("data-statX"))
      .data("ratioY", a.parentNode.getAttribute("data-statY"));

      clickPlayer(home_team_paper, a.parentNode.getAttribute("data-playerID"));
      clickPlayer(away_team_paper, a.parentNode.getAttribute("data-playerID"));
      setSelectedStat(a.parentNode.getAttribute("data-statType"));

  } else {
    console.log("third case");

    $(".editButton").removeClass("btn-primary").addClass("btn-warning");
    a.className = "btn btn-xs btn-warning pull-right editButton";
    a.childNodes[1].className = "glyphicon glyphicon-pencil";
    editingStat = undefined;
    $("#sendStat").prop("disabled", false);

    var parent = a.parentNode;
    var changedPoint = false;
    var changedStat = false;
    var changedPlayer = false;
    var player;

    console.log(parent.children);
    postParameters = {
       x : parent.getAttribute("data-statX"),
       y : parent.getAttribute("data-statY"),
       statID : parent.getAttribute("data-statType"),
       playerID : parent.getAttribute("data-playerID"),
       databaseID : parent.getAttribute("data-statID")
   };


   if (clickedPoint !== undefined) {
       changedPoint = true;
       postParameters.x = clickedPoint.data("ratioX");
       postParameters.y = clickedPoint.data("ratioY");
       clickedPoint.remove();
       clickedPoint = undefined;
   }
   if (clickedStat !== undefined) {
       changedStat = true;
       postParameters.statID = clickedStat.statID;
       clickedStat.button.style.backgroundColor = "white";
       clickedStat = undefined;
   }
   if (clickedPlayer !== undefined) {
       changedPlayer = true;
       player = clickedPlayer.player;
       postParameters.playerID = clickedPlayer.player.id;
       clickedPlayer.attr({fill: clickedPlayer.normalColor});
       player = clickedPlayer.player;
       clickedPlayer = undefined;
   }

   console.log(parent.children);
   console.log(parent.children.length);

   for (var i = 0; i<parent.children.length; i++) {
       if(changedPlayer && parent.children[i].getAttribute("data-name") == "player") {
          parent.children[i].innerHTML = (player.name + " #" + player.number);
          parent.setAttribute("data-playerID", player.id);
      }
      if(changedStat && parent.children[i].getAttribute("data-name") == "stat") {
          parent.children[i].innerHTML = (postParameters.statID);
          parent.setAttribute("data-statType", postParameters.statID);
      }
      if(changedPoint && parent.children[i].getAttribute("data-name") == "location") {
          console.log(parent.children[i].style.visibility);
          if (parent.children[i].style.visibility == "hidden") {
            parent.children[i].style.visibility = "visible";
        }
          parent.children[i].innerHTML = (Math.round(postParameters.x * 10000) / 10000 + " " + Math.round(postParameters.y * 10000) / 10000);
          parent.setAttribute("data-statX", postParameters.x);
          parent.setAttribute("data-statY", postParameters.y);
      }
  }



  $.post("/stats/update", postParameters, function(responseJSON) {});
}
}

function deleteEdit(a) {
    var i = a.parentNode.getAttribute("data-statID");
    console.log(a.parentNode.attributes);
    console.log(i);
    a.parentNode.parentNode.removeChild(a.parentNode);
    $.post("/stats/delete", {databaseID : i}, function() {});
}

disable_user_select();

function disable_user_select() {
    $("*").css("-webkit-user-select", "none");
    $("*").css("-moz-user-select", "none");
};