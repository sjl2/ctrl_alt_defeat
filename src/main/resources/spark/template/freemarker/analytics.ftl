<#assign coachContent>
  <script src="/js/analytics.js"></script> 

  <div class="container">
    <div class = "jumbotron">
      <h2>
        Welcome to Analytics
      </h2>  
    </div>
    <div class="row">
      <div class="col-md-2"> 
        <select class="playerSelector pull-right" id="p1">
          <option id = "player-1" selected> --- </option>
          <#list players as player>
            <option id="player${player.getID()}">${player.getName()}</option>
          </#list>
       </select>
      </div>
      <div class="col-md-2">
       <select class="playerSelector pull-right" id="p2">
       <option id = "player-1" selected> --- </option>
          <#list players as player>
            <option id="player${player.getID()}">${player.getName()}</option>
          </#list>
       </select>
      </div>
      <div class="col-md-2">
       <select class="playerSelector pull-right"  id="p3">
       <option id = "player-1" selected> --- </option>
          <#list players as player>
            <option id="player${player.getID()}">${player.getName()}</option>
          </#list>
       </select>
      </div>
      <div class="col-md-2">
       <select class="playerSelector pull-right"  id="p4">
       <option id = "player-1" selected> --- </option>
          <#list players as player>
            <option id="player${player.getID()}">${player.getName()}</option>
          </#list>
       </select>
      </div>
      <div class="col-md-2">
         <select class="playerSelector pull-right"  id="p5">
         <option id = "player-1" selected> --- </option>
          <#list players as player>
            <option id="player${player.getID()}">${player.getName()}</option>
          </#list>
       </select>
      </div>
    </div>

    <div class="row">
    <div class="col-md-7">
    <div class="btn-group pull-left">
          <button type="button" class="btn btn-primary dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
            Select Chart Type <span class="caret"></span>
          </button>
          <ul class="dropdown-menu" role="menu">
            <li onclick="seasonHeatMap()">Season Heat Map</li>
            <li onclick="recentShotChart()">5 Game Shot Chart</li>
          </ul>
        </div>
    <div class="panel panel-default">
      <div class="panel-body">
        <div id="forCharts"></div>
      </div>
    </div>
    </div>
    </div>

  </div>
</#assign>
<#include "coach.ftl">
