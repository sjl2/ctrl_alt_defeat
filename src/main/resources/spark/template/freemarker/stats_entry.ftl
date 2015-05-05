<#assign content>
<div class = "container">

	<div class="modal fade" id="subs">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
					<h4 class="modal-title">Subs</h4>
					<p>Drag players from the benches onto the court</p>
				</div>
				<div class="modal-body">
					<div id="sub_div" style = "margin:0 auto;width:445px;"></div>
				</div>
			</div><!-- /.modal-content -->
		</div><!-- /.modal-dialog -->
	</div><!-- /.modal -->

	<div class="alert alert-danger alert-dismissible" role="alert" id="error" <#if isGame>hidden</#if>>
		<h2>No Game here.</h2>
	</div>

	<div class="row">
		<div class="col-md-9">
			<div class = "row">
				<div class = "col-md-12">
					<div class="row">
						<div class="col-md-1"></div>
						<div class="col-md-2">
							<button type="button" class="btn btn-default stat-button" id = "TwoPointerButton" onclick = "setSelectedStat('TwoPointer')">Made Two Pointer</button>
						</div>
						<div class="col-md-2">
							<button type="button" class="btn btn-default stat-button" id = "ThreePointerButton" onclick = "setSelectedStat('ThreePointer')">Made Three Pointer</button>
						</div>
						<div class="col-md-2">
							<button type="button" class="btn btn-default stat-button" id = "FreeThrowButton" onclick = "setSelectedStat('FreeThrow')">Made Free Throw</button>
						</div>
						<div class="col-md-2">
							<button type="button" class="btn btn-default stat-button" id = "OffensiveReboundButton" onclick = "setSelectedStat('OffensiveRebound')">Offensive Rebound</button>
						</div>
						<div class="col-md-2">
							<button type="button" class="btn btn-default stat-button" id = "OffensiveFoulButton" onclick = "setSelectedStat('OffensiveFoul')">Offensive Foul</button>
						</div>
					</div>
					<div class="row">
						<div class="col-md-1"></div>
						<div class="col-md-2">
							<button type="button" class="btn btn-default stat-button" id = "MissedTwoPointerButton" onclick = "setSelectedStat('MissedTwoPointer')">Missed Two Pointer</button>
						</div>
						<div class="col-md-2">
							<button type="button" class="btn btn-default stat-button" id = "MissedThreePointerButton" onclick = "setSelectedStat('MissedThreePointer')">Missed Three Pointer</button>
						</div>
						<div class="col-md-2">
							<button type="button" class="btn btn-default stat-button" id = "MissedFreeThrowButton" onclick = "setSelectedStat('MissedFreeThrow')">Missed Free Throw</button>
						</div>
						<div class="col-md-2">
							<button type="button" class="btn btn-default stat-button" id = "DefensiveReboundButton" onclick = "setSelectedStat('DefensiveRebound')">Defensive Rebound</button>
						</div>
						<div class="col-md-2">
							<button type="button" class="btn btn-default stat-button" id = "DefensiveFoulButton" onclick = "setSelectedStat('DefensiveFoul')">Defensive Foul</button>
						</div>
					</div>
					<div class="row">
						<div class="col-md-1"></div>
						<div class="col-md-2">
							<button type="button" class="btn btn-default stat-button" id = "AssistButton" onclick = "setSelectedStat('Assist')">Assist</button>
						</div>
						<div class="col-md-2">
							<button type="button" class="btn btn-default stat-button" id = "TurnoverButton" onclick = "setSelectedStat('Turnover')">Turnover</button>
						</div>
						<div class="col-md-2">
							<button type="button" class="btn btn-default stat-button" id = "StealButton" onclick = "setSelectedStat('Steal')">Steal</button>
						</div>
						<div class="col-md-2">
							<button type="button" class="btn btn-default stat-button" id = "BlockButton" onclick = "setSelectedStat('Block')">Block</button>
						</div>
						<div class="col-md-2">
							<button type="button" class="btn btn-default stat-button" id = "TechnicalFoulButton" onclick = "setSelectedStat('TechnicalFoul')">Technical Foul</button>
						</div>
					</div>
				</div>
			</div>
			<div class="row">
				<div class="col-md-2">
					<h3 id="homeTeamLabel"></h3>
				</div>
				<div class="col-md-3">
				</div>
				<div class="col-md-2">
					<button type="button" class = "btn btn-primary btn-lg" id="sendStat" onclick="addStat()">Send Stat</button>
				</div>
				<div class="col-md-3">
				</div>
				<div class="col-md-2">
					<h3 id="awayTeamLabel"></h3>
				</div>
			</div>
			<div class = "row">
				<div class = "col-md-2">
					<div id="home_team" style="width:70"></div>
				</div>
				<div class="col-md-8">
					<div id="court"></div>
					<div id="arrows" style="height:40px;"></div>
				</div>
				<div class = "col-md-2" >
					<div id="away_team" style="width:70"></div>
				</div>
			</div>
			<div class = "row">
				<div class = "col-md-12">
					<div class="row">
						<div class="col-md-2">
							<button type="button" class = "btn btn-default" id="showHomeSubs" onclick="toggleBenchCourt(true)">Show Bench</button>
							<br>
							<button type="button" class = "btn btn-warning" id="HomeTimeout" onclick="timeout(true)">Timeout</button>
						</div>
						<div class="col-md-3"></div>
						<div class="col-md-2">
							<button type="button" class = "btn btn-default" id="flipPossession" onclick="fp()">
								Possession <span class="glyphicon glyphicon-triangle-right" aria-hidden="true"></span>
							</button>
							<br>
							<button type="button" class = "btn btn-info" id="openSubPerspective" data-toggle="modal" data-target="#subs">Sub Players</button>
						</div>
						<div class="col-md-3"></div>
						<div class="col-md-2">	
							<button type="button" class = "btn btn-default" id="showAwaySubs" onclick="toggleBenchCourt(false)">Show Bench</button>
							<br>
							<button type="button" class = "btn btn-warning" id="AwayTimeout" onclick="timeout(false)">Timeout</button>
						</div>
					</div>

					<button type="button" class="btn btn-danger" onclick="endGame()">End Game</button>
					<button type="button" class="btn btn-info" onclick="advancePeriod()">Advance Period</button>
				</div>
			</div>

		</div>
		<div class = "col-md-3" >
			<div class="row">
				<div class="col-md-12">
					<div id="scoreboard" style="width:250px; height:150px"></div>
				</div>
			</div>
			<div class="row">
				<div class="col-md-12" style = "height:700px;overflow-y : auto">
					<div class="list-group" id="ticker">
						<#list stats as stat>
						<#assign statType = stat.getStatType()>
						<a class="list-group-item" data-statType = ${statType} data-playerName = ${stat.getPlayer().getName()} data-playerID = ${stat.getPlayer().getID()} data-statID = ${stat.getID()} data-statX = ${stat.getLocation().getX()} data-statY = ${stat.getLocation().getY()}>
							<p data-name="player">${stat.getPlayer().getName()} #${stat.getPlayer().getNumber()}</p>
							<button class="btn btn-xs btn-warning pull-right editButton" onclick="statEdit(this)">
								<span class="glyphicon glyphicon-pencil"></span></button>
								<button class="btn btn-xs btn-danger pull-right" onclick="deleteEdit(this)" data-editMode="off">
									<span class="glyphicon glyphicon-trash"></span></button>
									<p name data-name="stat"> ${stat.getStatType()}</p>
									<p data-name="location" <#if (stat.getLocation().getX() < 0.0)> style="visibility:hidden;" </#if>> 
    									${stat.getLocation().getX()}, ${stat.getLocation().getY()} 
    								</p>
								</a>
								</#list>
							</div>
						</div>
					</div>

				</div>
			</div>
		</div>
		<link rel="stylesheet" href = "/css/main.css">
		<link rel="stylesheet" href="/css/stats_entry.css">



		<script src="/js/scoreboard.js"></script>
		<script src="/js/stats_entry.js"></script>
		</#assign>
		<#include "main.ftl">

