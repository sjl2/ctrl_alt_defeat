//from the ftl
// width="1008.8" height="572" 
//we want 780 442

canvas = document.getElementById("myCanvas");
ctx = canvas.getContext("2d");

drag = false;
ctx.lineWidth = 3;

canvas.addEventListener("mousedown", function(e){
    e.preventDefault();
    drag = true;
    ctx.beginPath();
    ctx.moveTo(e.offsetX, e.offsetY);
    
});
canvas.addEventListener("mouseup", function(e){
    drag = false;
    ctx.closePath();
});
canvas.addEventListener("mouseout", function(e){
    drag = false;
    ctx.closePath();
});
canvas.addEventListener("mousemove", function(e){
    if(!drag) {
	return;
    }
    if (document.getElementById("draw").checked) {
	ctx.globalCompositeOperation = "source-over";
    } else {
	ctx.globalCompositeOperation = "destination-out";
    }
    ctx.lineTo(e.offsetX, e.offsetY);
    ctx.stroke();
    
});

function updateColor() {
    ctx.strokeStyle = $("#colorPicker").val();
    ctx.fillStyle = $("#colorPicker").val();
}

function updateStrokeSize() {
    ctx.lineWidth = $("#strokeSize").val();
}

function clearAll() {
    ctx.clearRect(0,0,canvas.width, canvas.height);
}