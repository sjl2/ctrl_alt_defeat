<#assign coachContent>
  <div class = "container">
      <div class = "jumbotron">
        <h3> Coach's Whiteboard </h3>
      </div>
      <div class = "row" >
        <div class = "col-md-10">
          <canvas id="myCanvas" width="760" height="431"></canvas>
        </div>
        <div class = "col-md-2" id="input-background">
          <input type="radio" name="groupa" id="draw" value="1" checked><b>Draw</b>
          <br>
          <input type="radio" name="groupa" id="erase" value="0"><b>Erase</b>
          <br>
          <input class="btn btn-default" type="button" value="Clear All" onclick = "clearAll()">
	  <br>
	  <input type="color" id="colorPicker" onchange="updateColor()">
	  <input type="range" min="3" max="20" value="3" oninput="updateStrokeSize()" id="strokeSize">
        </div>
      </div>
  </div>
 
 <script src="/js/whiteboard.js"></script>
 <link rel="stylesheet" href="/css/whiteboard.css">
 <link rel="stylesheet" href="/css/coach.css">

</#assign>
<#include "coach.ftl">
