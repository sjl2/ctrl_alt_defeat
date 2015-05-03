<#assign coachContent>
<head>
  <style>
  .jumbotron{background-color : ${myTeam.getPrimary()}; color : ${myTeam.getSecondary()}; -webkit-text-stroke-width: .6px; -webkit-text-stroke-color: black}
  </style>
</head>

  <link rel="stylesheet" href="/css/dashboard.css">
  <div class="modal fade new-game" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-lg">
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
          <div class="col-sm-1"></div>
          <div class="col-sm-5 form-group" class=id="myStarters">
            <h3>The ${myTeam.getName()}</h3>
            <table class="table starters" id="home">
              <#list 0..4 as i>
              <tr>
                <td>${pos[i]}:</td>
                <td>               
                  <select class="playerSelector pull-right" id="myStarter${i}">
                    <#list players as player>
                      <option id="player${player.getID()}" <#if player_index == i>selected</#if>>${player.getName()}</option>
                    </#list>
                  </select>
                </td>
              </tr>
              </#list>
            </table>         
          </div>
          <div class="col-sm-5 form-group" class=id="opponentStarters">
            <select id="opponent">
              <#list teams as team>
               <option value="${team.getID()}">${team.getText()}</option>
              </#list>
            </select>
            <table class="table starters" id="opponent-lineup">
              <#list 0..4 as i>
              <tr>
                <td>${pos[i]}:</td>
                <td>               
                  <select class="pull-right playerSelector" id="oppStarter${i}">
                    <#list opposingPlayers as player>
                      <option id="player${player.getID()}" <#if player_index == i>selected</#if>>${player.getName()}</option>
                    </#list>
                  </select>
                </td>
              </tr>
              </#list>
            </table>         
          </div>
          <div class="col-sm-1"></div>
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
      <h2>Welcome ${myTeam.getName()}, to your <kbd>ctrl + alt + defeat</kbd> Dashboard!</h2>
    </div>
    ${dashboardContent}
  </div>

</#assign>
<#include "coach.ftl">
