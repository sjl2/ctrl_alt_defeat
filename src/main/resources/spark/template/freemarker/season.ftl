<table class="table table-hover boxscore" id="player-game-stats">
  <tr>
    <th>Game</th>
    <th data-toggle="modal" data-target=".modal-chart" onclick="clickStatType(1)" class="stat-type">PTS</th>
    <th data-toggle="modal" data-target=".modal-chart" onclick="clickStatType(2)" class="stat-type"h>FGM-A</th>
    <th data-toggle="modal" data-target=".modal-chart" onclick="clickStatType(3)" class="stat-type">2PM-A</th>
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
    <#if isPlayer>
      <#assign player = row.getPlayer()>
	<tr data-toggle="modal" data-target=".modal-chart" onclick="clickPlayerGame(${player.getID()}, ${row.getGameID()})"> 
    <#else>
      <#assign team = row.getTeam()>
	<tr data-toggle="modal" data-target=".modal-chart" onclick="clickTeamGame(${team.getID()}, ${row.getGameID()})"> 
    </#if>
      <#assign link = db.getGameLink(row.getGameID())>
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
