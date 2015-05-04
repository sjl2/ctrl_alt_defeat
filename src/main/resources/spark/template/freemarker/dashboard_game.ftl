<#assign dashboardContent>
  <div class = "row" >
    <div class="col-md-1 col-sm-1"></div>
    <div class="col-md-3 col-sm-3">
      <div class="btn-group" id="shotChartButtonGroup">
        <button type="button" id="shotChartButton" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-expanded="false" z-index="6">
          Select Player <span class="caret"></span>
        </button>
        <ul class="dropdown-menu" role="menu">
          <li id="pgList" class="positionList">Name</li>
          <li id="sgList" class="positionList">Name</li>
          <li id="sfList" class="positionList">Name</li>
          <li id="pfList" class="positionList">Name</li>
          <li id="cList" class="positionList">Name</li>
          <li id="usList" class="positionList">Team Shot Chart</li>
          <li id="themList" class="positionList">Opponent Shot Chart</li>
        </ul>
      </div>
      <div  id="shotChart"></div>
    </div>
    <div class = "col-md-4 col-sm-4">
      <div id="scoreboard"></div>
    </div>
    <div class="col-md-3 col-sm-3">
      <div id="statTicker"> </div>
    </div>
    <div class="col-md-1 col-sm-1"></div>
  </div>
  <div class="row">
    <div class="col-md-1"></div>
    <div class="col-md-2 col-sm-3" id="PG"> 
      <ul class="list-group">
        <li class="list-group-item">Name ##</li>
        <li class="list-group-item">Pts: </li>
        <li class="list-group-item">Reb: </li>
        <li class="list-group-item">Ast: </li>
        <li class="list-group-item">PF: </li>
        <li class="list-group-item">TO: </li>
      </ul>
    </div>
    <div class="col-md-2 col-sm-3" id="SG"> 
      <ul class="list-group">
        <li class="list-group-item">Name ##</li>
        <li class="list-group-item">Pts: </li>
        <li class="list-group-item">Reb: </li>
        <li class="list-group-item">Ast: </li>
        <li class="list-group-item">PF: </li>
        <li class="list-group-item">TO: </li>
      </ul>
    </div>
    <div class="col-md-2 col-sm-3" id="SF"> 
      <ul class="list-group">
        <li class="list-group-item">Name ##</li>
        <li class="list-group-item">Pts: </li>
        <li class="list-group-item">Reb: </li>
        <li class="list-group-item">Ast: </li>
        <li class="list-group-item">PF: </li>
        <li class="list-group-item">TO: </li>
      </ul>
    </div>
    <div class="col-md-2 col-sm-3" id="PF">
      <ul class="list-group">
        <li class="list-group-item">Name ##</li>
        <li class="list-group-item">Pts: </li>
        <li class="list-group-item">Reb: </li>
        <li class="list-group-item">Ast: </li>
        <li class="list-group-item">PF: </li>
        <li class="list-group-item">TO: </li>
      </ul>
    </div>
    <div class="col-md-2 col-sm-3" id="C">
      <ul class="list-group">
        <li class="list-group-item">Name ##</li>
        <li class="list-group-item">Pts: </li>
        <li class="list-group-item">Reb: </li>
        <li class="list-group-item">Ast: </li>
        <li class="list-group-item">PF: </li>
        <li class="list-group-item">TO: </li>
      </ul> 
    </div>
    <div class="col-md-1 col-sm-1"></div>
  </div>
  <script src="/js/dashboard.js"></script>
  <script src="/js/scoreboard.js"></script>
  <link rel="stylesheet" href="/css/dashboard_game.css">
</#assign>
<#include "dashboard.ftl">
