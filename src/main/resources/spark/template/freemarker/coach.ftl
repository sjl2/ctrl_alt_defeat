<#assign content>
  <div class="modal fade new-player" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-lg">
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" aria-label="Close">
            <span aria-hidden="true">&times;</span>
          </button>
          <h4 class="modal-title">Create New Player</h4>
        </div>
        <form role="form" id="player-form">
          <div class="modal-body">

            <div class="form-group">
              <label for="name-input">Name:</label>
              <input class="form-control" type="text" name="name" id="name-input" required>
            </div>
            <div class="form-group">
              <label for="jersey">Jersey Number:</label>
              <input id="jersey" class="form-control" type="number" name="number" min="0" max="99" required>
            </div>
            <div class="form-group">
              <label for="select-team"> Select Team:</label>
              <select id ="select-team" class="form-control" name="team">
                <#list allTeams as team>
                  <option value="${team.getID()}">${team.getText()}</option>
                </#list>
              </select>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
            <button type="button submit" class="btn btn-primary" id="createPlayer">Submit</button>
          </div>
        </form>
      </div>
    </div>
  </div>

  <div class="modal fade new-team" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-lg">
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" aria-label="Close">
            <span aria-hidden="true">&times;</span>
          </button>
          <h4 class="modal-title">Create New Team</h4>
        </div>
        <form role="form" id="team-form">
          <div class="modal-body">
            <div class="form-group">
              <label for="team-name-input">Name:</label>
              <input class="form-control" type="text" name="name" id="team-name-input" required>
            </div>
            <div class="form-group">
              <label for="coach-input">Coach:</label>
              <input class="form-control" type="text" name="coach" id="coach-input" required>
            </div>
            <div class="form-group">
              <label for="color1-input">Primary Color:</label>
              <input id="color1-input" class="form-control" type="color" name="color1">
            </div>
            <div class="form-group">
              <label for="color2-input">Secondary Color:</label>
              <input id="color2-input" class="form-control" type="color" name="color2">
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
            <button type="button submit" class="btn btn-primary" id="createPlayer">Submit</button>
          </div>
        </form>
      </div>
    </div>
  </div>

  <div class = "container-fluid">

    <div class="modal fade" id="multipleresults">
      <div class="modal-dialog">
          <div class="modal-content">
              <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">Multiple matches.  Were you looking for...</h4>
              </div>
              <div class="modal-body" id="linkList">
                
              </div>
          </div><!-- /.modal-content -->
        </div><!-- /.modal-dialog -->
    </div><!-- /.modal -->


      <nav class="navbar navbar-default navigation">
         <div class="container-fluid">

          <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a class="navbar-brand" href="/dashboard">
              <img id = "thumb" alt="Brand" src="/images/Basketball-small.png">
          </a>
         <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
            <ul class="nav navbar-nav">
              <li><a href="/dashboard"> Dashboard <span class="sr-only">(current)</span></a></li>
              <li><a href="/playmaker">PlayMaker</a></li>
              <li class="dropdown">
                <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">Make Stuff <span class="caret"></span></a>
                <ul class="dropdown-menu" role="menu">
                  <li><a id="make-new-team">New Team</a></li>
                  <li><a id="make-new-player">New Player</a></li>
                </ul>
              </li>
              <li><a href="/whiteboard">Whiteboard</a></li>
              <li><a href="/dashboard/analytics">Analytics</a></li>
            </ul>

            <form class="navbar-form navbar-right" role="search">
              <div class="input-append btn-group">
                <input class="span2" size="16" type="text"  placeholder="Search for Player or Team" style = "width : 300px;height:34px; border-color:#2e6da4" autocomplete = "off" onkeypress = "suggestions(event)" list="suggestions1" id="playerTeamSearch">
                <datalist id="suggestions1"></datalist>
                  <a class="btn btn-primary dropdown-toggle" data-toggle="dropdown" style="float:right;">
                    <span class="caret"></span>
                  </a>
                  <button type="button" id="searchButton" class="btn btn-primary" style="float:right" data-player="true" onclick="textSearch()">Player Search</button>
                <ul class="dropdown-menu dropdown-menu-right">
                  <li><a onclick = "setTextSearch(true)"><i class="icon-pencil"></i> Search as Player</a></li>
                  <li><a onclick = "setTextSearch(false)"><i class="icon-trash"></i> Search as Team</a></li>
                  
                </ul>
              </div>
            </form>
          </div>
         </div>
      </nav>

    <datalist id="suggestions1"></datalist>
      <div class="alert alert-danger alert-dismissible" role="alert" id="error" hidden>${errorMessage}</div>
      <div id = "coachContent"> 
        ${coachContent} 
      </div>

  </div>
  <script src="/js/coach.js"></script>
 <link rel="stylesheet" href="/css/coach.css">

</#assign>
<#include "main.ftl">
