canvas = document.getElementById("myCanvas");
ctx = canvas.getContext("2d");

image = new Image();
image.src = "images/Basketball-Court.png";
image.onload = function() {
	ctx.drawImage(image, 0,0, 940, 500);
}

drag = false;

canvas.addEventListener("mousedown", function(e){drag = true;});
canvas.addEventListener("mouseup", function(e){drag = false;});
canvas.addEventListener("mouseout", function(e){drag = false;});
canvas.addEventListener("mousemove", function(e){
	if (drag) {
		ctx.fillStyle = "rgb(" + Math.floor(Math.random() * 255) +"," + Math.floor(Math.random() * 255) + "," + Math.floor(Math.random() * 255) + ")";
		ctx.fillRect(e.offsetX - 2, e.offsetY - 2, 4, 4);
	}
});