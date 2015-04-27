<#assign content>
  
  <script src="/js/newGame.js"></script>
  <link rel="stylesheet" href="/css/newGame.css">

  <h1>Create New Game</h1>
  
  <#assign pos=["Point Guard", "Shooting Guard", "Small Forward", "Power Forward", "Center"]>
  <div id="myStarters">
    Select starters:
    <br>
    <#list 0..4 as i>
      ${pos[i]}: <select class="playerSelector" id="myStarter${i}">
	<#list players as player>
	  <option id="player${player.getID()}">${player.getName()}</option>
	</#list>
      </select>
      <br>
    </#list>
  </div>

  <div id="selectors">
    Opponent Team:
    <select id="opponent">
      <#list teams as team>
	<option id="team${team.getID()}">${team.getText()}</option>
      </#list>
    </select>

    <br><br>
    
    <input type="radio" name="is_home" id="is_home" value="1" checked>Home Game
    <br>
    <input type="radio" name="is_home" id="is_away" value="0">Away Game

  </div>

  <div id="opponentStarters">
    Opponent Starters:
    <br>
    <#list 0..4 as i>
      ${pos[i]}: <select class="playerSelector" id="oppStarter${i}">
      </select>
      <br>
    </#list>
  </div>
  
  <div id="submit">
    <button id="createGame">Start Game</button>
  </div>

</#assign>
<#include "main.ftl">
