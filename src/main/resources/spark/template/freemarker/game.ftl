<#assign coachContent>
  <script src="/js/game.js"></script>
  <link rel="stylesheet" href="/css/game.css">

  <div class="container">
    <div class = "jumbotron">
      <h2> 
        <#assign home = game.getHome()>
        <#assign away = game.getAway()>
        <a href="/team/${away.getID()}">${away.getName()}</a> @  
        <a href="/team/${home.getID()}">${home.getName()}</a> 
        (${game.getDate().toString()})
      </h2>
      <h3>Home: ${game.getHomeScore()} </h3>
      <h3>Away: ${game.getAwayScore()} </h3>
    </div>

    <div class="row">
      <table class="table table-hover boxscore team-stats">
        <tr>
          <th>Team</th>
          <th>PTS</th>
          <th>FGM-A</th>
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
        <tr>
            <#assign homeStats = game.getHomeBoxScore().getTeamStats()>
            <td>${home.getName()}</td>
            <td>${homeStats.getPoints()}</td>
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
        <tr>
            <#assign teamStats = game.getAwayBoxScore().getTeamStats()>
            <td>${away.getName()}</td>
            <td>${teamStats.getPoints()}</td>            
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

    <div class="row">
      <table class="table table-hover boxscore">
        <tr>
          <th>Home: ${home.getName()}</th>
        </tr>
        <tr>
          <th>#</th>
          <th>Name</th>
          <th>MIN</th>
          <th>FGM-A</th>
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
          <th>PTS</th>
        </tr>
        <#list game.getHomeBoxScore().getAllPlayerStats() as row>
          <tr> 
            <td>${row.getPlayer().getNumber()}</td>
            <td><a href="/dashboard/player/${row.getPlayer().getID()}">${row.getPlayer().getName()}</a></td>
            <td>${row.getMinutes()}</td>
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
            <td>${row.getPoints()}</td>
          </tr>
        </#list>
      </table>
    </div>

    <div class="row">
      <table class="table table-hover boxscore">
        <tr>
          <th>Home: ${away.getName()}</th>
        </tr>
        <tr>
          <th>#</th>
          <th>Name</th>
          <th>MIN</th>
          <th>FGM-A</th>
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
          <th>PTS</th>
        </tr>
        <#list game.getAwayBoxScore().getAllPlayerStats() as row>
          <tr> 
            <td>${row.getPlayer().getNumber()}</td>
            <td><a href="/dashboard/player/${row.getPlayer().getID()}">${row.getPlayer().getName()}</a></td>
            <td>${row.getMinutes()}</td>
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
            <td>${row.getPoints()}</td>
          </tr>
        </#list>
      </table>
    </div>
  </div>
</#assign>
<#include "coach.ftl">
