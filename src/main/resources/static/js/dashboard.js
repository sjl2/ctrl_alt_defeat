var scoreboard = Raphael(document.getElementById("scoreboard"), 200, 200);
scoreboard.setStart();
scoreboard.text(10,20, "Home score: 00");
scoreboard.text(10,40, "Away score: 00");
scoreboard.text(10,60, "Home timeouts: 00");
scoreboard.text(10,80, "Away timeouts: 00");
scoreboard.text(10,100, "Home fouls: 00");
scoreboard.text(10,120, "Away fouls: 00");
scoreboard.text(10,140, "Possession: Home");
scoreboard.text(10,160, "Quarter: 1");
var st = scoreboard.setFinish();

st.attr({"font-size" : 20, "text-anchor" : "start"});


function newGame() {
	console.log($("#opponent")[0].value);
	$.post("/game/start", {opponent: $("#opponent")[0].value}, function(responseJSON){
		//responseJSON should have who we're playing(maybe it'll have the rosters)
	});
}