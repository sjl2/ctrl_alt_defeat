
const FRAME_RATE = 30.0;

var playSpeed = 1;
var playing = false;

var currentFrame = 0;
var maxFrame = 0;

var width = 0;
var height = 0;

var radius = 25;

var paper;
var circ;

var anim;

var angle = 0;

var tokens = [];

var intervalVar;
var grabbedToken;

function Location(x, y) {
    return {
	x: x,
	y: y,
	translate:function(dx, dy) {
	    this.x += dx;
	    this.y += dy;
	},
	copy:function() {
	    return Location(this.x, this.y);
	}
    };
}

function Token(circle, location) {
    return {
	circle: circle,
	path: [location.copy()],
	location: location.copy(),
	translate: function(dx, dy) {
	    this.circle.attr("cx", circle.attr("cx") + dx);
	    this.circle.attr("cy", circle.attr("cy") + dy);
	    this.location.translate(dx, dy);
	},
	setLocationWithXY: function(x, y) {
	    this.circle.attr("cx", x);
	    this.circle.attr("cy", y);
	    this.location.x = x;
	    this.location.y = y;
	},
	setLocationWithLoc: function(loc) {
	    var locCopy = loc.copy();
	    this.setLocationWithXY(locCopy.x, locCopy.y);
	}
    };
}

window.onload = function() {
    var container = $("#canvas_container");
    width = container.width();
    height = container.height();
    anim = {
	cx:width/2,
	cy:height/2,
    };
    var offset = container.offset();
    paper = Raphael(offset.left, offset.top, width, height);

    var court = paper.image("images/Basketball-Court.png", 0, 0, width, height);
    circ = paper.circle(width / 2, height / 2, radius);
    circ.attr("fill", "url(images/Basketball-small.png)");
    
    var startingLocations = [Location(350, 285),
			     Location(260, 100),
			     Location(60, 470),
			     Location(75, 185),
			     Location(190, 380),
			     Location(290, 285),
			     Location(220, 140),
			     Location(60, 410),
			     Location(75, 245),
			     Location(150, 340)]
    for(i = 0; i < 10; i++) {
	var loc = startingLocations[i];
	var circ2 = paper.circle(loc.x, loc.y, radius);
	if(i < 5) {
	    circ2.attr("fill", "#00f");
	} else {
	    circ2.attr("fill", "#f00");
	}
	var t = Token(circ2, loc);
	circ2.drag(onmove, onstart, onend, t, t, t);
	tokens[i] = t;
    }

//    var ballLoc = Location(width / 2, height / 2);
//    var t = Token(circ, ballLoc);
//    console.log(t.location);
//    circ.drag(onmove, onstart, onend, t, t, t);
//    tokens[11] = t;

    $("#previous_frame").on("click", function() {
	if(currentFrame > 0) {
	    setFrame(currentFrame - 1);
	}
    });

    $("#next_frame").on("click", function() {
	if(currentFrame < maxFrame) {
	    setFrame(currentFrame + 1);
	}
    });

    $("#first_frame").on("click", function() {
	setFrame(0);
    });

    $("#last_frame").on("click", function() {
	setFrame(maxFrame);
    });

    $("#go_frame").on("click", function() {
	var frame = Number($("#go_frame")[0].value);
	if(frame < 0) {
	    setFrame(0);
	} else if(frame > maxFrame) {
	    setFrame(maxFrame);
	} else {
	    setFrame(frame);
	}
    });

    $("#play").on("click", function() {
	if(currentFrame < maxFrame) {
	    play();
	}
    });

    $("#stop").on("click", function() {
	stop();
    });

}

function setFrame(frame) {
    stop();
    currentFrame = frame;
    $("#current_frame")[0].value = currentFrame;
    for(i = 0; i < tokens.length; i++) {
	var t = tokens[i];
	if(t != grabbedToken) {
	    if(currentFrame >= t.path.length) {
		t.setLocationWithLoc(t.path[t.path.length - 1]);
	    } else {
		t.setLocationWithLoc(t.path[currentFrame]);
	    }
	}
    }
}

function onstart(x, y, event) {
    this.dx = 0;
    this.dy = 0;
    grabbedToken = this;
    intervalVar = window.setInterval(updatePath, 1000.0 / FRAME_RATE);
}

function onmove(dx, dy, x, y, event) {
    this.translate(dx - this.dx, dy - this.dy);
    this.dx = dx;
    this.dy = dy;
}

function onend(event) {
    window.clearInterval(intervalVar);
    grabbedToken = undefined;
}

function updatePath() {
    grabbedToken.path[currentFrame] = grabbedToken.location.copy();
    setFrame(currentFrame + 1);
    if(currentFrame > maxFrame) {
	maxFrame = currentFrame;
    }
}

function play() {
    if(playing == false){
	playing = true;
	playTime = currentFrame / FRAME_RATE;
	playIntervalVar = window.setInterval(stepAnimation, 1000.0 / (FRAME_RATE * playSpeed));
    }
}

function stop() {
    if(playing == true) {
	playing = false;
	window.clearInterval(playIntervalVar);
    }
}

function stepAnimation() {
    for(i = 0; i < tokens.length; i++) {
	var t = tokens[i];
	var nextLoc;
	if(currentFrame >= t.path.length) {
	    nextLoc = t.path[t.path.length - 1];
	} else {
	    nextLoc = t.path[currentFrame];
	}
	t.circle.animate({cx:nextLoc.x, cy:nextLoc.y},
			 playSpeed / FRAME_RATE,
			 "linear",
			 undefined);
    }

    if(currentFrame >= maxFrame - 1) {
	window.clearInterval(playIntervalVar);
    }
    currentFrame++;
    $("#current_frame")[0].value = currentFrame;
    playTime += (playSpeed / FRAME_RATE)
}

