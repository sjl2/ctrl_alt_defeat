canvas = document.getElementById("myCanvas");
ctx = canvas.getContext("2d");

image = new Image();
image.src = "images/Basketball-Court.png";
image.onload = function() {
	ctx.drawImage(image, 0,0);
}