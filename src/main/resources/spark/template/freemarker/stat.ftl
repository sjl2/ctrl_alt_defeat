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
