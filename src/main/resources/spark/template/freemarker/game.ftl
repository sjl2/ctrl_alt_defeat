<#assign content>
  <script src="/js/game.js"></script>

  <link rel="stylesheet" href="/css/game.css">

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
    <#list rows as row>
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
