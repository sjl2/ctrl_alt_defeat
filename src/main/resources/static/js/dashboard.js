var paper = Raphael(document.getElementById("content"), 700, 700);
var newGameButton = paper.rect(10, 10, 100, 40);
var newGameText = paper.text(60, 30, "New Game");
newGameButton.click(function() {
	$.post("/game/start", {}, function(responseJSON){
		//responseJSON should have who we're playing(maybe it'll have the rosters)
	});
});