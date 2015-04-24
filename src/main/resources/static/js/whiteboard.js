//from the ftl
// width="1008.8" height="572" 
//we want 780 442

canvas = document.getElementById("myCanvas");
ctx = canvas.getContext("2d");

drag = false;
var prevPoint;
ctx.lineWidth = 3;


canvas.addEventListener("mousedown", function(e){drag = true;});
canvas.addEventListener("mouseup", function(e){prevPoint = undefined; drag = false;});
canvas.addEventListener("mouseout", function(e){prevPoint = undefined; drag = false;});
canvas.addEventListener("mousemove", function(e){
	if (document.getElementById("draw").checked) {
		if (drag) {
		ctx.strokeStyle = "rgb(" + Math.floor(Math.random() * 255) +"," + Math.floor(Math.random() * 255) + "," + Math.floor(Math.random() * 255) + ")";

		if ((prevPoint === undefined)) {
			ctx.beginPath();
			ctx.moveTo(e.offsetX, e.offsetY);
		}
			ctx.lineTo(e.offsetX, e.offsetY);
			ctx.stroke();
			prevPoint = {};
			prevPoint.x = e.offsetX;
			prevPoint.y = e.offsetY;
		}
	} else {
		ctx.closePath();
		if (drag) {
		
		if ((prevPoint === undefined)) ctx.moveTo(e.offsetX, e.offsetY);
			var eraser = 20;
			ctx.clearRect(e.offsetX - eraser / 2, e.offsetY - eraser / 2, eraser, eraser);
		}
	}
	
});

function clearAll() {
	ctx.clearRect(0,0,canvas.width, canvas.height);
}