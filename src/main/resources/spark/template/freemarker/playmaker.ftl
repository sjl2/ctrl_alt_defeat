<#assign coachContent>

  <script src="/js/playmaker.js"></script>
  <link rel="stylesheet" href="/css/playmaker.css">
  
  <div id="wrapper">
    <div id="sidebar-wrapper">
      <div id="load_header">
	<button id="delete_plays">Delete Plays</button>
      </div>
      <table id="plays"></table>
    </div>
    <div id="page-content-wrapper">
      <div class="container-fluid">
	<div class="row" id="above_court">
	  <span id="editing_name"></span>
	  <div class="col-md-1">
	    <button class="btn btn-primary" id="hide-sidebar"><</button>
	  </div>
	  <div class="col-md-8"></div>
	  <div class="col-md-3" id="save">
	    <button class="pull-right btn btn-primary" id="save_play">Save Play</button>
	    <input type="text" class="pull-right" id="play_name">
	  </div>
	</div>
	<div class="row" id="canvas_container" tabindex="0"></div>
	<div class="row" id="control">
	  <input type="range" id="frame_number" min="0" max="0">
	  <input type="text" value="0" id="current_frame" readonly>
	  <br>
	  <button class="btn btn-primary" id="play">Play</button>
	  <button class="btn btn-primary" id="stop">Stop</button>
	</div>
      </div>
    </div>
  </div>


</#assign>
<#include "coach.ftl">
