<#assign content>
	<div class = "container">
 		<div class="row">
 			<div class="col-md-9">
 				<div class = "row">
 					<div class = "col-md-11">
 						<div>
 							<button type="button" class="btn btn-default" id = "BlockButton" onclick = "setSelectedStat('Block')">Block</button>
 							<button type="button" class="btn btn-default" id = "DefensiveFoulButton" onclick = "setSelectedStat('DefensiveFoul')">Defensive Foul</button>
 							<button type="button" class="btn btn-default" id = "DefensiveReboundButton" onclick = "setSelectedStat('DefensiveRebound')">Defensive Rebound</button>
 							<button type="button" class="btn btn-default" id = "FreeThrowButton" onclick = "setSelectedStat('FreeThrow')">Made Free Throw</button>
 							<button type="button" class="btn btn-default" id = "MissedFreeThrowButton" onclick = "setSelectedStat('MissedFreeThrow')">Missed Free Throw</button>
 							<button type="button" class="btn btn-default" id = "MissedThreePointerButton" onclick = "setSelectedStat('MissedThreePointer')">Missed Three Pointer</button>
 							<button type="button" class="btn btn-default" id = "MissedTwoPointerButton" onclick = "setSelectedStat('MissedTwoPointer')">Missed Two Pointer</button>
 							<button type="button" class="btn btn-default" id = "OffensiveFoulButton" onclick = "setSelectedStat('OffensiveFoul')">Offensive Foul</button>
 							<button type="button" class="btn btn-default" id = "OffensiveReboundButton" onclick = "setSelectedStat('OffensiveRebound')">Offensive Rebound</button>
 							<button type="button" class="btn btn-default" id = "StealButton" onclick = "setSelectedStat('Steal')">Steal</button>
 							<button type="button" class="btn btn-default" id = "TechnicalFoulButton" onclick = "setSelectedStat('TechnicalFoul')">Technical Foul</button>
 							<button type="button" class="btn btn-default" id = "TurnoverButton" onclick = "setSelectedStat('Turnover')">Turnover</button>
 							<button type="button" class="btn btn-default" id = "ThreePointerButton" onclick = "setSelectedStat('ThreePointer')">Made Three Pointer</button>
 							<button type="button" class="btn btn-default" id = "TwoPointerButton" onclick = "setSelectedStat('TwoPointer')">Made Two Pointer</button>
 							<button type="button" class="btn btn-default" id = "AssistButton" onclick = "setSelectedStat('Assist')">Assist</button>
 						</div>
 					</div>
 				</div>
 				<div class = "row">
 					<div class = "col-md-1" style = "padding : 0px;margin : 0px;">
 						<div id="home_team"></div>
 					</div>
 					<div class="col-md-9">
 						<div id="court"></div>
 					</div>
 					<div class = "col-md-1" style = "padding : 0px;margin : 0px;">
 						<div id="away_team"></div>
 					</div>
 				</div>
 				<div class = "row">
 					<div class = "col-md-11">
 						<div id="control"></div>
 					</div>
 				</div>

 			</div>
 			<div class = "col-md-2">
 				<div id="statFeed"></div>
 			</div>
 		</div>
	</div>
 	<link rel="stylesheet" href="/css/stats_entry.css">


	
 
 <script src="/js/stats_entry.js"></script>
</#assign>
<#include "main.ftl">

