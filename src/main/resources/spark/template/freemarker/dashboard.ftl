<#assign coachContent>
<head>
  <style>
  .jumbotron{background-color : ${myTeam.getPrimary()}; color : ${myTeam.getSecondary()}; -webkit-text-stroke-width: .6px; -webkit-text-stroke-color: black}
  </style>
</head>

  <link rel="stylesheet" href="/css/dashboard.css">
  <div class="modal fade new-game" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          Create New 
          <select>
            <option id="is_home" name="is_home" value="1" select>
              Home 
            </option>
            <option id="is_away" name="is_home" value="0"> 
              Away 
            </option>
          </select>
          Game
          <button type="button" class="close" data-dismiss="modal" aria-label="Close">
            <span aria-hidden="true">&times;</span>
          </button>
        </div>
        <div class="modal-body row">
          <#assign pos=["Point Guard", "Shooting Guard", "Small Forward", "Power Forward", "Center"]>
          <div class="col-sm-6 form-group" class=id="myStarters">
            <h3>The ${myTeam.getName()}</h3>
            <table class="table starters" id="home">
              <#list 0..4 as i>
              <tr>
                <td>${pos[i]}:</td>
                <td>               
                  <select class="playerSelector" id="myStarter${i}">
                    <#list players as player>
                      <option id="${player.getID()}">${player.getName()}</option>
                    </#list>
                  </select>
                </td>
              </tr>
              </#list>
            </table>         
          </div>
          <div class="col-sm-6 form-group" class=id="opponentStarters">
            <select id="opponent">
              <#list teams as team>
               <option id="team${team.getID()}">${team.getText()}</option>
              </#list>
            </select>
            <table class="table starters" id="away-lineup">
              <#list 0..4 as i>
              <tr>
                <td>${pos[i]}:</td>
                <td>               
                  <select class="playerSelector" id="myStarter${i}">
                    <#list opposingPlayers as player>
                      <option id="player${player.getID()}">${player.getName()}</option>
                    </#list>
                  </select>
                </td>
              </tr>
              </#list>
            </table>         
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
          <button type="button" class="btn btn-primary" id="createGame">Start Game</button>
        </div>
      </div>
    </div>
  </div>

    <div class="container">
      <div class = "jumbotron">
        <h2>Welcome to ${myTeam.getName()}'s Dashboard!</h2>
      </div>
      <div class = "row" >
        <div class = "col-md-4">
          <div id="scoreboard"></div>
        </div>
        <div class="col-md-4 col-md-offset-2">
          <div id="statTicker"> </div>
        </div>
      </div>
      <div class="row">
        <div class = "col-md-1" id="PG"> 
          <p> Name ## </p>
          <p> Pts: </p>
          <p> Reb: </p>
          <p> Ast: </p>
          <p> PF: </p>
          <p> TO: </p>
        </div>
        <div class = "col-md-1" id="SG"> 
          <p> Name  ## </p>
          <p> Pts: </p>
          <p> Reb: </p>
          <p> Ast: </p>
          <p> PF: </p>
          <p> TO: </p>
        </div>
        <div class = "col-md-1" id="SF"> 
          <p> Name  ## </p>
          <p> Pts: </p>
          <p> Reb: </p>
          <p> Ast: </p>
          <p> PF: </p>
          <p> TO: </p>
        </div>
        <div class = "col-md-1" id="PF">
          <p> Name  ## </p>
          <p> Pts: </p>
          <p> Reb: </p>
          <p> Ast: </p>
          <p> PF: </p>
          <p> TO: </p> 
        </div>
        <div class = "col-md-1" id="C">
          <p> Name  ## </p>
          <p> Pts: </p>
          <p> Reb: </p>
          <p> Ast: </p>
          <p> PF: </p>
          <p> TO: </p> 
        </div>
        <div class = "col-md-6">
          <div id="shotChartTitle"></div>

          <div class="btn-group">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
              Action <span class="caret"></span>
            </button>
            <ul class="dropdown-menu" role="menu">
              <li id="pgList" onclick="shotchart('pg')">Name</li>
              <li id="sgList" onclick="shotchart('sg')">Name</li>
              <li id="sfList" onclick="shotchart('sf')">Name</li>
              <li id="pfList" onclick="shotchart('pf')">Name</li>
              <li id="cList" onclick="shotchart('c')">Name</li>
              <li onclick="shotchart('us')">Team Shot Chart</li>
              <li onclick="shotchart('them')">Opponent Shot Chart</li>
            </ul>
          </div>
          <div  id="shotChart"></div>

        </div>
      </div>
    </div>
    
  <script src="/js/dashboard.js"></script>

</#assign>
<#include "coach.ftl">
