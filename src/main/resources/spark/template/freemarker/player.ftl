<#assign coachContent>
  <script src="/js/game.js"></script>
  <link rel="stylesheet" href="/css/game.css">

  <div class="container">
    <div class = "jumbotron">
      <h2>${player.getName()}</h2>
      <h3>
        <a href="/team/view/${player.getTeamID()}">${player.getTeamName()}</a>
      </h3>
    </div>

    <div class="row">
      <#list years as year>
        <option value="${year}">${year - 1} - ${year}</option>
      </#list>
    </div>

    <div class="row" id="schedule-player">
      <table class="table table-hover boxscore">
        <tr>
          <th>Game</th>
          <th>MIN</th>
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
          <tr> 
            <#assign link = db.getGameTitle(row.getGameID())>
            <td>
              <a class="btn btn-link" href="${link.getLink()}">${link.getValue()}</a>
            </td>
            <td>${row.getMinutes()}</td>
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

    <!--
    <div class="row">
      <h3>Totals</h3>
      <table class="table table-hover boxscore">
        <tr>
          <th>Season</th>
          <th>MIN</th>
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
        <#list seasonTotals as row>
          <tr class="player-stats"> 
            <td>${row.getPlayer().getNumber()}</td>
            <td><a href="/player/${row.getPlayer().getID()}">${row.getPlayer().getName()}</a></td>
            <td>${row.getMinutes()}</td>
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
          <th>MIN</th>
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
        <#list game.getAwayBoxScore().getAllPlayerStats() as row>
          <tr class="player-stats"> 
            <td>${row.getPlayer().getNumber()}</td>
            <td><a href="/player/${row.getPlayer().getID()}">${row.getPlayer().getName()}</a></td>
            <td>${row.getMinutes()}</td>
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
  -->
  </div>
</#assign>
<#include "coach.ftl">
