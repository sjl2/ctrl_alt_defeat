<#assign coachContent>
  <script src="/js/game.js"></script>
  <link rel="stylesheet" href="/css/game.css">
  
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

  <div class="container">
    <div class = "jumbotron" id="game-header">
      <h2> 
        <#assign home = game.getHome()>
        <#assign away = game.getAway()>
        <a href="/team/view/${away.getID()}">${away.getName()}</a> @  
        <a href="/team/view/${home.getID()}">${home.getName()}</a> 
      </h2>
      <h3>${game.getAwayScore()} - ${game.getHomeScore()} </h3>
      <#assign date = game.getDate() >
      <h3>${date.getMonth()} ${date.getDayOfMonth()}, ${date.getYear()}</h3>
    </div>

    <div class="row">
      <div class="col-md-12">
        <div class="panel panel-default">
          <div class="panel-heading"><h3>Team Stats</h3></div>
          <table class="table table-hover boxscore team-stats">
            <tr>
              <th>Team</th>
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
            <tr onclick="clickTeamGame(${home.getID()}, ${game.getID()})">
                <#assign homeStats = game.getHomeBoxScore().getTeamStats()>
                <#assign link = home.getLink()>
                <td><a href=${link.getURL()}>${link.getText()}</a></td>
                <td>${homeStats.getPoints()}</td>
                <td>${homeStats.getFieldGoals()} - ${homeStats.getFieldGoalsA()}</td>
                <td>${homeStats.getTwoPointers()} - ${homeStats.getTwoPointersA()}</td>
                <td>${homeStats.getThreePointers()} - ${homeStats.getThreePointersA()}</td>
                <td>${homeStats.getFreeThrows()} - ${homeStats.getFreeThrowsA()}</td>
                <td>${homeStats.getOffensiveRebounds()}</td>
                <td>${homeStats.getDefensiveRebounds()}</td>
                <td>${homeStats.getRebounds()}</td>
                <td>${homeStats.getAssists()}</td>
                <td>${homeStats.getSteals()}</td>
                <td>${homeStats.getBlocks()}</td>
                <td>${homeStats.getTurnovers()}</td>
                <td>${homeStats.getPersonalFouls()}</td>
            </tr>
            <tr onclick="clickTeamGame(${away.getID()}, ${game.getID()})">
                <#assign teamStats = game.getAwayBoxScore().getTeamStats()>
                <#assign link = away.getLink()>
                <td><a href=${link.getURL()}>${link.getText()}</a></td>
                <td>${teamStats.getPoints()}</td> 
                <td>${teamStats.getFieldGoals()} - ${teamStats.getFieldGoalsA()}</td>        
                <td>${teamStats.getTwoPointers()} - ${teamStats.getTwoPointersA()}</td>
                <td>${teamStats.getThreePointers()} - ${teamStats.getThreePointersA()}</td>
                <td>${teamStats.getFreeThrows()} - ${teamStats.getFreeThrowsA()}</td>
                <td>${teamStats.getOffensiveRebounds()}</td>
                <td>${teamStats.getDefensiveRebounds()}</td>
                <td>${teamStats.getRebounds()}</td>
                <td>${teamStats.getAssists()}</td>
                <td>${teamStats.getSteals()}</td>
                <td>${teamStats.getBlocks()}</td>
                <td>${teamStats.getTurnovers()}</td>
                <td>${teamStats.getPersonalFouls()}</td>
            </tr>
          </table>
        </div>
      </div>
    </div>

    <div class="row">
      <div class="col-md-12">
        <div class="panel panel-default">
          <#assign link = home.getLink()>
          <div class="panel-heading"><h3><a href=${link.getURL()}>${link.getText()}</a></h3></div>
          <table class="table table-hover boxscore">
            <tr>
              <th>#</th>
              <th>Name</th>
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
            <#list game.getHomeBoxScore().getAllPlayerStats() as row>
              <#assign player = row.getPlayer()>
              <tr class="player-stats" onclick="clickPlayerGame(${player.getID()}, ${row.getGameID()})"> 
                <td>${row.getPlayer().getNumber()}</td>
                <#assign link = row.getPlayer().getLink() >
                <td><a href="${link.getURL()}">${link.getText()}</a></td>
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
      </div>
    </div>

    <div class="row">
      <div class="col-md-12">
        <div class="panel panel-default">
          <#assign link = away.getLink()>
          <div class="panel-heading table-header"><h3><a href=${link.getURL()}>${link.getText()}</a></h3></div>
          <table class="table table-hover boxscore">
            <tr>
              <th>#</th>
              <th>Name</th>
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
              <#assign player = row.getPlayer()> 
              <tr class="player-stats" onclick="clickPlayerGame(${player.getID()}, ${row.getGameID()})"> 
                <td>${row.getPlayer().getNumber()}</td>
                <#assign link = player.getLink() >
                <td><a href="${link.getURL()}">${link.getText()}</a></td>
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
      </div>
    </div>
  </div>
</#assign>
<#include "coach.ftl">
