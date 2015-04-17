canvas = document.getElementById("myCanvas");
ctx = canvas.getContext("2d");

image = new Image();
image.src = "images/Basketball-Court.png";
image.onload = function() {
	ctx.drawImage(image, 0,0, 940, 500);
}

drag = false;
var prevPoint;
ctx.lineWidth = 3;

canvas.addEventListener("mousedown", function(e){drag = true;});
canvas.addEventListener("mouseup", function(e){prevPoint = undefined; drag = false;});
canvas.addEventListener("mouseout", function(e){prevPoint = undefined; drag = false;});
canvas.addEventListener("mousemove", function(e){
	if (drag) {
		ctx.strokeStyle = "rgb(" + Math.floor(Math.random() * 255) +"," + Math.floor(Math.random() * 255) + "," + Math.floor(Math.random() * 255) + ")";
		if ((prevPoint === undefined)) ctx.moveTo(e.offsetX, e.offsetY);
			ctx.lineTo(e.offsetX, e.offsetY);
			ctx.stroke();
			prevPoint = {};
			prevPoint.x = e.offsetX;
			prevPoint.y = e.offsetY;
		//ctx.fillRect(e.offsetX - 2, e.offsetY - 2, 4, 4);
	}
});