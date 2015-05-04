<#assign dashboardContent>
  <div class = "row" >
    <div class = "col-md-4">
      <div id="scoreboard"></div>
    </div>
    <div class="col-md-4 col-md-offset-2">
      <div id="statTicker"> </div>
    </div>
  </div>
  <div class="row">
  <div class="col-md-9">
  <div class="row">
    <div class = "col-md-2" id="PG"> 
      <ul class="list-group">
        <li class="list-group-item">Name ##</li>
        <li class="list-group-item">Pts: </li>
        <li class="list-group-item">Reb: </li>
        <li class="list-group-item">Ast: </li>
        <li class="list-group-item">PF: </li>
        <li class="list-group-item">TO: </li>
      </ul>
    </div>
    <div class = "col-md-2" id="SG"> 
      <ul class="list-group">
        <li class="list-group-item">Name ##</li>
        <li class="list-group-item">Pts: </li>
        <li class="list-group-item">Reb: </li>
        <li class="list-group-item">Ast: </li>
        <li class="list-group-item">PF: </li>
        <li class="list-group-item">TO: </li>
      </ul>
    </div>
    <div class = "col-md-2" id="SF"> 
      <ul class="list-group">
        <li class="list-group-item">Name ##</li>
        <li class="list-group-item">Pts: </li>
        <li class="list-group-item">Reb: </li>
        <li class="list-group-item">Ast: </li>
        <li class="list-group-item">PF: </li>
        <li class="list-group-item">TO: </li>
      </ul>
    </div>
    <div class = "col-md-2" id="PF">
      <ul class="list-group">
        <li class="list-group-item">Name ##</li>
        <li class="list-group-item">Pts: </li>
        <li class="list-group-item">Reb: </li>
        <li class="list-group-item">Ast: </li>
        <li class="list-group-item">PF: </li>
        <li class="list-group-item">TO: </li>
      </ul>
    </div>
    <div class = "col-md-2" id="C">
      <ul class="list-group">
        <li class="list-group-item">Name ##</li>
        <li class="list-group-item">Pts: </li>
        <li class="list-group-item">Reb: </li>
        <li class="list-group-item">Ast: </li>
        <li class="list-group-item">PF: </li>
        <li class="list-group-item">TO: </li>
      </ul> 
    </div>
    </div>
    </div>
    <div class = "col-md-3">
      <div class="btn-group">
        <button type="button" id="shotChartButton" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
          Select Player <span class="caret"></span>
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
  <script src="/js/dashboard.js"></script>
</#assign>
<#include "dashboard.ftl">
