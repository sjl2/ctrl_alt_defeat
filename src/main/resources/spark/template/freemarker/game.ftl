<#assign content>
  <script src="/js/game.js"></script>
  <link rel="stylesheet" href="/css/game.css">

  <h2> 
    <#assign home = game.getHome()>
    <#assign away = game.getAway()>
    <a href="/team/${away.getID()}">${away.getName()}</a> @  
    <a href="/team/${home.getID()}">${home.getName()}</a> 
    (${game.getDate().toString()})
  </h2>


  <table id="boxscore">
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
      	<td><a href="/player/${row.getPlayer().getID()}">${row.getPlayer().getName()}</a></td>
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
    <tr id="team_stats">
        <#assign teamStats = game.getHomeBoxScore().getTeamStats()>
        <td></td>
        <td></td>
        <td>${teamStats.getMinutes()}</td>
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
        <td>${teamStats.getPoints()}</td>
    </tr>
  </table>

  <table id="boxscore">
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
        <td><a href="/player/${row.getPlayer().getID()}">${row.getPlayer().getName()}</a></td>
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

</#assign>
<#include "main.ftl">
