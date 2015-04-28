<#assign coachContent>
  <script src="/js/player_team_page.js"></script> 
  <script type="text/javascript">
    var id = ${team.getID()};
    var isPlayer = false; 
  </script> 
  <link rel="stylesheet" href="/css/player_team_page.css">

  <div class="container">
    <div class = "jumbotron">
      <h2>
        <a href="/team/view/${team.getID()}">${team.getName()}</a>
      </h2>
      <h3>Coach: ${team.getCoach()}</h3>
    </div>

    <div id="forCharts" style="height:500px"></div>

    <div class="row">
      <div class="col-md-3">
        <h3>Roster:</h3>
        <ul class="list-group">
          <#list team.getPlayers() as player>
            <#assign link = player.getLink()>
            <li class="list-group-item">
              <a href="${link.getURL()}">${link.getText()}</a>
            </li>
          </#list>
        <ul>
      </div>
    </div>


    <div class="row">
      <div class="col-md-2 col-md-offset-10">
        <select class="form-control" id="season">
          <#list years as year>
            <option value="${year}">${year - 1} - ${year}</option>
          </#list>
        </select>
      </div>
    </div>

    <div class="row" id="season">
      <table class="table table-hover boxscore">
        <tr>
          <th>Game</th>
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
        <#list rows as row>
          <tr onclick="clickPlayerGame(${team.getID()}, ${row.getGameID()})"> 
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
          <tr class="player-stats"> 
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
          <tr class="player-stats"> 
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
