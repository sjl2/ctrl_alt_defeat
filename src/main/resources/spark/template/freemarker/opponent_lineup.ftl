<#assign pos=["Point Guard", "Shooting Guard", "Small Forward", "Power Forward", "Center"]>
<#list 0..4 as i>
<tr>
  <td>${pos[i]}:</td>
  <td>               
    <#if myTeam>
      <select class="pull-right playerSelector" id="myStarter${i}">
    <#else>
      <select class="pull-right playerSelector" id="oppStarter${i}">
    </#if>
    <#list players as player>
        <option id="player${player.getID()}" <#if player_index == i>selected</#if> >
          ${player.getName()}
        </option>
      </#list>
    </select>
  </td>
</tr>
</#list>
