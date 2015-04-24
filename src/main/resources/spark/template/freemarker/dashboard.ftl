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
          <p> stuff will go here, I promise.  we just aren't sure what </p>
        </div>
      </div>
    </div>
    <div id="stats_display" width = "300px" height = "300px"> Fancy in-game stats </div>
    
  <script src="/js/dashboard.js"></script>

</#assign>
<#include "coach.ftl">
