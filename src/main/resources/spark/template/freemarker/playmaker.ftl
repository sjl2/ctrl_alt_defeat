<#assign content>

  <div id="load_column">
    <table id="plays"></table>
  </div>
  <div id="save">
    <input type="text" id="play_name"></input>
    <button id="save_play">Save Play</button>
  </div>
  <div id="canvas_container"></div>
  <div id="control">
    <button id="first_frame">First Frame</button>
    <button id="previous_frame">Previous Frame</button>
    <input type="text" value="0" id="current_frame"></input>
    <button id="next_frame">Next Frame</button>
    <button id="last_frame">Last Frame</button>
    <br>
    <button id="go_frame">Go To Frame</button>
    <br>
    <button id="play">Play</button>
    <button id="stop">Stop</button>
  </div>

  <script src="js/playmaker.js"></script>
  <link rel="stylesheet" href="css/playmaker.css">
</#assign>
<#include "main.ftl">
