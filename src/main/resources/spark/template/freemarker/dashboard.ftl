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
          <div class="col-sm-1"></div>
          <div class="col-sm-5 form-group myStarters" id="myStarters">
            <h3 class="myTeamName" id="myTeam${myTeam.getID()}">The ${myTeam.getName()}</h3>
            <table class="table starters" id="home">
            </table>         
          </div>
          <div class="col-sm-5 form-group opponentStarters" id="opponentStarters">
            <select id="opponent">
            </select>
            <table class="table starters" id="opponent-lineup">
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
