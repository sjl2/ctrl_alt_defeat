<#assign coachContent>

  <script src="/js/playmaker.js"></script>
  <link rel="stylesheet" href="/css/playmaker.css">
  
  <div id="wrapper">
    <div class="row">
      <div class="col-md-2 toggled" id="load-sidebar-col">
	<div id="load-sidebar-wrapper">
	  <div id="load_column" class="pull-left">
	    <div id="load_header">
	      <button id="delete_plays" class="btn btn-primary">Delete Plays</button>
	    </div>
	    <div id="load_content">
	      <ul id="plays" class="list-group"></ul>
	    </div>
	  </div>
	  <div id="shown">
	    <button class="btn btn-primary" id="hide-load-sidebar">
	      <span class="glyphicon glyphicon-folder-open" id="load-hide-img">
	      </span>
	    </button>
	  </div>
	</div>
      </div>

      <div class="col-md-8" id="page-content-wrapper">
	<div class="row" id="above_court">
	  <div class="col-md-3">
	    <button class="btn btn-primary" id="edit_pos">
	      Edit Start Locations
	    </button>
	  </div>
	  <div class="col-md-2 well well-sm" id="editing_name">1</div>
	  <div class="col-md-3"></div>
	  <div class="col-md-4" id="save">
	    <button class="pull-right btn btn-primary" id="save_play">
	      <span class="glyphicon glyphicon-floppy-save"></span>
	    </button>
	    <input type="text" class="pull-right" id="play_name">
	  </div>
	</div>
	<div class="row" id="canvas_container" tabindex="0"></div>
	<div class="row" id="control">
	  <input type="range" id="frame_number" min="0" max="0">
	  <div class="well well-sm" id="current_frame">0</div>
	  <br>
	  <button class="btn btn-primary" id="play">
	    <span class="glyphicon glyphicon-play"></span> Play
	  </button>
	  <button class="btn btn-primary" id="stop">
	    <span class="glyphicon glyphicon-stop"></span> Stop
	  </button>
	</div>
      </div>
      <div class="col-md-2 toggled" id="settings-sidebar-col">
	<div id="settings-sidebar-wrapper">
	  <div id="shown" class="pull-left">
	    <button class="btn btn-primary" id="hide-settings-sidebar">
	      <span class="glyphicon glyphicon-cog" id="settings-hide-img">
	      </span>
	    </button>
	  </div>
	  <div id="settings_column" class="pull-right">
	    <p>Playback Speed</p>
	    <input type="range" min="0.5" max="3" step="0.05" value="1" id="playSpeed">
	    <p>Token Size</p>
	    <input type="range" min="0.01" max="0.05" step="0.0005" value="0.025" id="tokenRadius">
	  </div>
	</div>
      </div>
    </div>
  </div>


</#assign>
<#include "coach.ftl">
