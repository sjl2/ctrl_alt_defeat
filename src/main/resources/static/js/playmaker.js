
const FRAME_RATE = 30.0;

var playSpeed = 1;
var playing = false;

var currentFrame = 0;
var maxFrame = 0;

var width = 0;
var height = 0;

var radius = 25;

var courtTopLeftCorner;
var courtBottomRightCorner;

var paper;
var circ;

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
    var loadColumn = $("#load_column");
    loadColumn.css("height", window.innerHeight);

    var container = $("#canvas_container");
    width = container.width();
    height = Math.floor(width / 1.75);
    container.css("height", height);
    var containerTop = ((window.innerHeight - height) / 2);
    container.css("top", containerTop);

    var control = $("#control");
    control.css("top", containerTop + 20);

    var save_area = $("#save");
    save_area.css("top", containerTop - 20);

    var offset = container.offset();
    paper = Raphael(offset.left, offset.top, width, height);

    courtTopLeftCorner = Location(offset.left, offset.top);
    courtBottomRightCorner = Location(offset.left + container.width(), offset.top + container.height());

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

    var ballLoc = Location(width / 2, height / 2);//TODO make ball follow player
    var t = Token(circ, ballLoc);
    circ.drag(onmove, onstart, onend, t, t, t);
    tokens[10] = t;

    $("#previous_frame").on("click", function() {
	if(playing) {
	    stop();
	} else if(currentFrame > 0) {
	    setFrame(currentFrame - 1);
	}
    });

    $("#next_frame").on("click", function() {
	if(playing) {
	    stop();
	} else if(currentFrame < maxFrame) {
	    setFrame(currentFrame + 1);
	}
    });

    $("#first_frame").on("click", function() {
	if(playing) {
	    stop();
	} else {
	    setFrame(0);
	}
    });

    $("#last_frame").on("click", function() {
	if(playing) {
	    stop();
	} else {
	    setFrame(maxFrame);
	}
    });

    $("#go_frame").on("click", function() {
	if(playing) {
	    stop();
	} else {
	    var frame = Number($("#current_frame")[0].value);
	    if(frame < 0) {
		setFrame(0);
	    } else if(frame > maxFrame) {
		setFrame(maxFrame);
	    } else {
		setFrame(frame);
	    }
	}
    });

    $("#play").on("click", function() {
	if(!playing) {
	    if(currentFrame < maxFrame) {
		play();
	    }
	}
    });

    $("#stop").on("click", function() {
	if(playing) {
	    stop();
	}
    });

}

function setFrame(frame) {
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

function onmove(dx, dy, x, y, event) {//TODO limit drag to court graphic
    var insideX = true;
    var insideY = true;
    if(x < courtTopLeftCorner.x) {
	insideX = false;
	this.translate(-this.location.x, 0);
    } else if(x > courtBottomRightCorner.x) {
	insideX = false;
	this.translate(width - this.location.x, 0);
    } 
    if(y < courtTopLeftCorner.y) {
	insideY = false;
	this.translate(0, -this.location.y);
    } else if(y > courtBottomRightCorner.y) {
	insideY = false;
	this.translate(0, height - this.location.y);
    } 
    
    if(insideX){
	this.translate(dx - this.dx, 0);
	this.dx = dx;
    }
    if(insideY) {
	this.translate(0, dy - this.dy);
	this.dy = dy;
    }
}

function onend(event) {
    window.clearInterval(intervalVar);
    var prevPath = grabbedToken.path;
    grabbedToken.path = [];
    for (i = 0; i <= currentFrame; i++) {
	grabbedToken.path[i] = prevPath[i];
    }
    grabbedToken = undefined;
    maxFrame = 0;
    for(i = 0; i < tokens.length; i++) {
	if(tokens[i].path.length - 1 > maxFrame) {
	    maxFrame = tokens[i].path.length - 1;
	}
    }
}

function updatePath() {
    setFrame(currentFrame + 1);
    grabbedToken.path[currentFrame] = grabbedToken.location.copy();
    if(currentFrame > maxFrame) {
	maxFrame = currentFrame;
	for(i = 0; i < tokens.length; i++) {
	    var t = tokens[i];
	    if(t.path[currentFrame] == undefined) {
		t.path[currentFrame] = t.path[currentFrame - 1];
	    }
	}
    }
}

function play() {
    if(playing == false){
	playing = true;
	playTime = currentFrame / FRAME_RATE;
	playIntervalVar = window.setInterval(stepAnimation, 1000.0 / (FRAME_RATE * playSpeed));
	for(i = 0; i < tokens.length; i++) {
	    var t = tokens[i]
	    t.circle.undrag();
	}
    }
}

function stop() {
    if(playing == true) {
	playing = false;
	window.clearInterval(playIntervalVar);
	for(i = 0; i < tokens.length; i++) {
	    var t = tokens[i]
	    t.circle.drag(onmove, onstart, onend, t, t, t);
	}
    }
}

function stepAnimation() {
    if(playing) {
	for(i = 0; i < tokens.length; i++) {
	    var t = tokens[i];
	    var nextLoc;
	    if(currentFrame >= t.path.length) {
		nextLoc = t.path[t.path.length - 1];
	    } else {
		nextLoc = t.path[currentFrame];
	    }
	    t.circle.animate({cx:nextLoc.x, cy:nextLoc.y},
			     1000.0 / (FRAME_RATE * playSpeed),
			     "linear",
			     undefined);
	}

	if(currentFrame >= maxFrame - 1) {
	    stop();
	}
	currentFrame++;
	$("#current_frame")[0].value = currentFrame;
	playTime += (playSpeed / FRAME_RATE)
    }
}

