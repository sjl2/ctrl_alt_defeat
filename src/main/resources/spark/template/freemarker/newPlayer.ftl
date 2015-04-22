<#assign content>
  <h1>Welcome to the Create a Player Screen!</h1>
  <form method="POST" action="/dashboard/new/player/results">

    Player name:<br>
    <input type="text" name="name">
    <br>

    Jersey Number:<br>
    <input type="number" name="number" min="0" max="99">
    <br>

    Select Team:<br>
    <select name="team">
      <#list teams as team>
	<option value="${team.getID()}">${team.getName()}</option>
      </#list>
    </select>
    <br>

    <input type="radio" name="current" value="1" checked>Current Player
    <input type="radio" name="current" value="0">Former Player
    <br><br>

		<input type="submit" class="submit" value="Submit"> <br>
	</form>
  <link rel="stylesheet" href="/css/newPlayer.css">
</#assign>
<#include "main.ftl">
