<#assign coachContent>

  <script src="/js/playmaker.js"></script>
  <link rel="stylesheet" href="/css/playmaker.css">
  
  <div id="wrapper">
    <div id="sidebar-wrapper">
      <div id="load_header">
	<button id="delete_plays" class="btn btn-primary">Delete Plays</button>
      </div>
      <table id="plays"></table>
    </div>
    <div class="row">
      <div class="col-md-2"></div>
      <div class="col-md-8" id="page-content-wrapper">
	<div class="row" id="above_court">
	  <div class="col-md-1">
	    <button class="btn btn-primary" id="hide-sidebar">&lt</button>
	  </div>
	  <div class="col-md-2 well well-sm" id="editing_name">1</div>
	  <div class="col-md-5"></div>
	  <div class="col-md-4" id="save">
	    <button class="pull-right btn btn-primary" id="save_play">Save Play</button>
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
      <div class="col-md-2"></div>
    </div>
  </div>


</#assign>
<#include "coach.ftl">
