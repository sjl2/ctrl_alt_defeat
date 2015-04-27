<#assign coachContent>
<div class="container">
    
  <div>
  <h1>Make a Player</h1>
  <form method="POST" action="/dashboard/new/player/results">

    Player name:<br>
    <input type="text" name="name" required>
    <br>

    Jersey Number:<br>
    <input type="number" name="number" min="0" max="99" required>
    <br>

    Select Team:<br>
    <select name="team">
      <#list teams as team>
	<option value="${team.getID()}">${team.getText()}</option>
      </#list>
    </select>
    <br>

    <input type="radio" name="current" value="true" checked>Current Player
    <input type="radio" name="current" value="false">Former Player
    <br><br>

		<input type="submit" class="submit" value="Submit"> <br>
	</form>
</div>
  <link rel="stylesheet" href="/css/newPlayer.css">
</#assign>
<#include "coach.ftl">
