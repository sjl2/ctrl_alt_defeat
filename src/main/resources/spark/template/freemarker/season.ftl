<table class="table table-hover">
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
    <tr> 
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