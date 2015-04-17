<#assign content>


	<h1>Welcome to the Coach's Dashboard!</h1>

	<div id="wrapper">
		<div id="scoreboard" width = "300px" height = "300px"> the scoreboard</div>
		<div id="playmaker_div" width = "300px" height = "300px"> <a href="/playmaker">Playmaker</a> </div>
		<div id="stats_display" width = "300px" height = "300px"> Fancy in-game stats </div>
		<div id="new_game" width = "300px" height = "300px">
			<p>Enter opponent and click the button</p>
			<input type="text" id = "opponent"></input>
			<button id="new_game_button" onclick = "newGame();">Make a new Game!</button>
			
		</div>
		<div id="new_team"> <a href = "/dashboard/newTeam"> Make a new team!</a> </div>
		<div id="new_player"> <a href = "/dashboard/newPlayer"> Make a new player!</a> </div>

	</div>
 <script src="js/dashboard.js"></script>
 <link rel="stylesheet" href="css/dashboard.css">

</#assign>
<#include "main.ftl">