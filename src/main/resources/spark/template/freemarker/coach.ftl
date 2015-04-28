<#assign content>
  <div class = "container-fluid">
      <nav class="navbar navbar-default">
         <div class="container-fluid">

          <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a class="navbar-brand" href="#">
              <img id = "thumb" alt="Brand" src="/images/Basketball-small.png">
          </a>
         <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
            <ul class="nav navbar-nav">
              <li><a href="/dashboard"> Dashboard <span class="sr-only">(current)</span></a></li>
              <li><a href="/playmaker">PlayMaker</a></li>
              <li class="dropdown">
                <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">Make Stuff <span class="caret"></span></a>
                <ul class="dropdown-menu" role="menu">
                  <li><a href="/dashboard/new/team">New Team</a></li>
                  <li><a href="/dashboard/new/player">New Player</a></li>
                </ul>
              </li>
              <li><a href="/whiteboard">Whiteboard</a></li>
            </ul>
            <form class="navbar-form navbar-left" role="search">
              <div class="input-append btn-group">
                <input class="span2" size="16" type="text"  placeholder="Search for Player or Team" style = "width : 300px" autocomplete = "off" onkeyup = "suggestions()" list="suggestions1" id="playerTeamSearch">
                <datalist id="suggestions1"></datalist>
                  <a class="btn btn-primary dropdown-toggle" data-toggle="dropdown" >
                    <span class="caret"></span>
                  </a>
                <ul class="dropdown-menu dropdown-menu-right">
                  <li><a href="#"><i class="icon-pencil"></i> Search as Player</a></li>
                  <li><a href="#"><i class="icon-trash"></i> Search as Team</a></li>
                  
                </ul>
              </div>
            </form>
          </div>
         </div>
      </nav>

    <datalist id="suggestions1"></datalist>

      <div id = "coachContent"> 
        ${coachContent} 
      </div>

  </div>
  <script src="/js/coach.js"></script>
 <link rel="stylesheet" href="/css/coach.css">

</#assign>
<#include "main.ftl">
