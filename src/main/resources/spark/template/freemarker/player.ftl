<#assign coachContent>
  <script src="/js/player_team_page.js"></script> 
  <script type="text/javascript">
    var id = ${player.getID()};
    var isPlayer = true; 
  </script> 

  <link rel="stylesheet" href="/css/player_team_page.css">

  <div class="container">
    <div class = "jumbotron">
      <button class="btn btn-xs btn-warning pull-right" data-toggle="modal" data-target="#edit_player_modal">
        <span class="glyphicon glyphicon-pencil"></span>
      </button>
      <h2 id="playerName">${player.getName()} #${player.getNumber()}</h2>
      <h3>
        <a href="/team/view/${player.getTeamID()}">${player.getTeamName()}</a>
      </h3>
      <#if !(player.getCurrent())>
        <h4 style="color:red"> RETIRED </h4>
      </#if>
    </div>

    <div class="modal fade modal-chart" tabindex="-1" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <span id="chart-title"></span>
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
              <span aria-hidden="true">&times;</span>
            </button>
          </div>
          <div id="forCharts"></div>
        </div>
      </div>
    </div>

    <div class="modal fade" tabindex="-1" aria-hidden="true" id = "edit_player_modal">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <span id="chart-title"></span>
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
              <span aria-hidden="true">&times;</span>
            </button>
          </div>
          <div id="forCharts" class="form-group">
              Player name:<br>
              <input type="text" name="name" value = "${player.getName()}" id = "playerFormName">
              <br>
              Jersey Number:<br>
              <input type="number" name="number" min="0" max="99" value = ${player.getNumber()} id = "playerFormNumber">
              <br>
              Select Team:<br>
              <select name="team" id="playerFormTeam">
                <#list teams as team>
                  <option value="${team.getID()}" <#if (team.getID() == player.getTeamID())>selected</#if>>${team.getText()}</option>
                </#list>
              </select>
                      <br>

              <input type="radio" id = "playerIsCurrent" name="current" value="true" <#if (player.getCurrent())>checked</#if>>Current Player
              <input type="radio" id = "playerIsRetired" name="current" value="false" <#if !(player.getCurrent())>checked</#if>>Former Player
                      <br><br>


              <button class = "btn btn-lg btn-success" onclick = "updatePlayer()" data-dismiss="modal" aria-label="Close">
                <span class="glyphicon glyphicon-ok"></span>
                Update Player
              </button>
	      <button class="btn btn-lg btn-danger pull-right" onclick="deletePlayer(${player.getID()})">
		<span class="glyphicon glyphicon-trash"></span>
	      </button>
          </div>
        </div>
      </div>
    </div>

    <div class="row">
      <div class="col-md-2 col-md-offset-10">
        <select class="form-control" id="years">
          <#list years as year>
            <option value="${year}">${year - 1} - ${year}</option>
          </#list>
        </select>
      </div>
    </div>

    <div class="row" id="season">
      <table class="table table-hover boxscore" id="player-game-stats">
        <tr>
          <th>Game</th>
          <th data-toggle="modal" data-target=".modal-chart" onclick="clickStatType(1)" class="stat-type">PTS</th>
          <th data-toggle="modal" data-target=".modal-chart" onclick="clickStatType(2)" class="stat-type">FGM-A</th>
          <th data-toggle="modal" data-target=".modal-chart"  onclick="clickStatType(3)" class="stat-type">2PM-A</th>
          <th data-toggle="modal" data-target=".modal-chart" onclick="clickStatType(4)" class="stat-type">3PM-A</th>
          <th data-toggle="modal" data-target=".modal-chart" onclick="clickStatType(5)" class="stat-type">FTM-A</th>
          <th data-toggle="modal" data-target=".modal-chart" onclick="clickStatType(6)" class="stat-type">OREB</th>
          <th data-toggle="modal" data-target=".modal-chart" onclick="clickStatType(7)" class="stat-type">DREB</th>
          <th data-toggle="modal" data-target=".modal-chart" onclick="clickStatType(8)" class="stat-type">REB</th>
          <th data-toggle="modal" data-target=".modal-chart" onclick="clickStatType(9)" class="stat-type">AST</th>
          <th data-toggle="modal" data-target=".modal-chart" onclick="clickStatType(10)" class="stat-type">STL</th>
          <th data-toggle="modal" data-target=".modal-chart" onclick="clickStatType(11)" class="stat-type">BLK</th>
          <th data-toggle="modal" data-target=".modal-chart" onclick="clickStatType(12)" class="stat-type">TO</th>
          <th data-toggle="modal" data-target=".modal-chart" onclick="clickStatType(13)" class="stat-type">PF</th>
        </tr>
        <#list rows as row>
          <#assign link = db.getGameLink(row.getGameID())>
          <tr data-toggle="modal" data-target=".modal-chart" onclick="clickPlayerGame(${player.getID()}, ${row.getGameID()})" class="stat-row">         
            <td>
              <a class="btn btn-link" href="${link.getURL()}">${link.getText()}</a>
            </td>
            <td>${row.getPoints()}</td>
            <td>${row.getFieldGoals()} - ${row.getFieldGoalsA()}</td>
            <td>${row.getTwoPointers()} - ${row.getTwoPointersA()}</td>
            <td>${row.getThreePointers()} - ${row.getThreePointersA()}</td>
            <td>${row.getFreeThrows()} - ${row.getFreeThrowsA()}</td>
            <td>${row.getOffensiveRebounds()}</td>
            <td>${row.getDefensiveRebounds()}</td>
            <td>${row.getRebounds()}</td>
            <td>${row.getAssists()}</td>
            <td>${row.getSteals()}</td>
            <td>${row.getBlocks()}</td>
            <td>${row.getTurnovers()}</td>
            <td>${row.getPersonalFouls()}</td>
          </tr>
        </#list>
      </table>
    </div>

    <div class="row">
      <h3>Totals</h3>
      <table class="table table-hover boxscore">
        <tr>
          <th>Season</th>
          <th>PTS</th>
          <th>FGM-A</th>
          <th>2PM-A</th>
          <th>3PM-A</th>
          <th>FTM-A</th>
          <th>OREB</th>
          <th>DREB</th>
          <th>REB</th>
          <th>AST</th>
          <th>STL</th>
          <th>BLK</th>
          <th>TO</th>
          <th>PF</th>
        </tr>
        <#list years as year>
          <#assign row = seasonTotals[year_index]>
          <tr data-toggle="modal" data-target=".modal-chart" onclick="clickPlayerSeason(${player.getID()}, ${year})"> 
            <td>${year - 1} - ${year}</td>
            <td>${row.getPoints()}</td>
            <td>${row.getFieldGoals()} - ${row.getFieldGoalsA()}</td>
            <td>${row.getTwoPointers()} - ${row.getTwoPointersA()}</td>
            <td>${row.getThreePointers()} - ${row.getThreePointersA()}</td>
            <td>${row.getFreeThrows()} - ${row.getFreeThrowsA()}</td>
            <td>${row.getOffensiveRebounds()}</td>
            <td>${row.getDefensiveRebounds()}</td>
            <td>${row.getRebounds()}</td>
            <td>${row.getAssists()}</td>
            <td>${row.getSteals()}</td>
            <td>${row.getBlocks()}</td>
            <td>${row.getTurnovers()}</td>
            <td>${row.getPersonalFouls()}</td>
          </tr>
        </#list>
      </table>
    </div>
    <div class="row">
      <h3>Averages</h3>
      <table class="table table-hover boxscore">
        <tr>
          <th>Season</th>
          <th>PTS</th>
          <th>FGM-A</th>
          <th>2PM-A</th>
          <th>3PM-A</th>
          <th>FTM-A</th>
          <th>OREB</th>
          <th>DREB</th>
          <th>REB</th>
          <th>AST</th>
          <th>STL</th>
          <th>BLK</th>
          <th>TO</th>
          <th>PF</th>
        </tr>
        <#list years as year>
          <#assign row = seasonAverages[year_index]>
          <tr> 
            <td>${year - 1} - ${year}</td>
            <td>${row.getPoints()}</td>
            <td>${row.getFieldGoals()} - ${row.getFieldGoalsA()}</td>
            <td>${row.getTwoPointers()} - ${row.getTwoPointersA()}</td>
            <td>${row.getThreePointers()} - ${row.getThreePointersA()}</td>
            <td>${row.getFreeThrows()} - ${row.getFreeThrowsA()}</td>
            <td>${row.getOffensiveRebounds()}</td>
            <td>${row.getDefensiveRebounds()}</td>
            <td>${row.getRebounds()}</td>
            <td>${row.getAssists()}</td>
            <td>${row.getSteals()}</td>
            <td>${row.getBlocks()}</td>
            <td>${row.getTurnovers()}</td>
            <td>${row.getPersonalFouls()}</td>
          </tr>
        </#list>
    </div>
  </div>
</#assign>
<#include "coach.ftl">
