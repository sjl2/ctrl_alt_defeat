<#assign pos=["Point Guard", "Shooting Guard", "Small Forward", "Power Forward", "Center"]>
<#list 0..4 as i>
  <tr>
    <td>${pos[i]}:</td>
    <td>               
      <select class="playerSelector" id="myStarter${i}">
        <#list players as player>
          <option id="player${player.getID()}">${player.getName()}</option>
        </#list>
      </select>
    </td>
  </tr>
</#list>