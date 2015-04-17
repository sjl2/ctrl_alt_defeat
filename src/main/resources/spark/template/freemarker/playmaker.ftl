<#assign content>
  <script src="js/playmaker.js"></script>
  <link rel="stylesheet" href="css/playmaker.css">

  <div id="load_column">
    <div id="load_header">
      <button id="to_dashboard">Back To Dashboard</button>
      <button id="delete_plays">Delete Plays</button>
    </div>
    <table id="plays"></table>
  </div>
  <div id="above_court">
    <span id="editing_name"></span>
    <div id="save">
      <input type="text" id="play_name"></input>
      <button id="save_play">Save Play</button>
    </div>
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


</#assign>
<#include "main.ftl">
