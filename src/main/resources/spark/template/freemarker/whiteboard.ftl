<#assign content>
     
		<canvas id="myCanvas" width="1008.8" height="572" ></canvas>
		<div id="new_game" width = "300px" height = "300px">
      		<p>Draw or erase?</p>
      		<input type="radio" name="groupa" id="draw" value="1" checked>Draw
      		<br>
      		<input type="radio" name="groupa" id="erase" value="0">Erase
      		<br>
      		<button onclick = "clearAll()">Clear All</button>
      		
    	</div>
 
 <script src="js/whiteboard.js"></script>
 <link rel="stylesheet" href="css/whiteboard.css">

</#assign>
<#include "main.ftl">