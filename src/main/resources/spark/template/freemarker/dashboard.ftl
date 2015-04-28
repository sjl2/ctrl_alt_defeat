<#assign coachContent>
<head>
<style>
.jumbotron{background-color : ${myTeam.getPrimary()}; color : ${myTeam.getSecondary()}; -webkit-text-stroke-width: .6px; -webkit-text-stroke-color: black}
</style>
</head>
 	<link rel="stylesheet" href="/css/dashboard.css">

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
