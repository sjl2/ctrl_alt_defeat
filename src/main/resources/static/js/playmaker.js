
const FRAME_RATE = 30.0;

var playSpeed = 1;
var playing = false;

var currentFrame = 0;
var maxFrame = 0;

var width = 0;
var height = 0;

var radius;

var courtTopLeftCorner;
var courtBottomRightCorner;

var paper;
var circ;

var angle = 0;

var tokens = [];

var intervalVar;
var grabbedToken;

var edittingPlayName = "";

var existingPlays = [];

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

    var above_court = $("#above_court");
    var above_court_height = $("#play_name").height();
    above_court.css("top", containerTop - 20);
    above_court.css("height", above_court_height);

    var offset = container.offset();
    paper = Raphael(offset.left, offset.top, width, height);

    courtTopLeftCorner = Location(offset.left, offset.top);
    courtBottomRightCorner = Location(offset.left + container.width(), offset.top + container.height());

    var court = paper.image("images/Basketball-Court.png", 0, 0, width, height);
    circ = paper.circle(width / 2, height / 2, radius);
    circ.attr("fill", "url(images/Basketball-small.png)");
    
    var startingLocations = [Location(35, 50),
			     Location(26, 17.54),
			     Location(6, 82.46),
			     Location(7.5, 32.456),
			     Location(19, 66.666),
			     Location(29, 50),
			     Location(22, 24.561),
			     Location(6, 71.93),
			     Location(7.5, 42.982),
			     Location(15, 59.649)];
    radius = width * 0.025;
    for(i = 0; i < 10; i++) {
	var loc = startingLocations[i];
	loc.x = loc.x * width / 100;
	loc.y = loc.y * height / 100;
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

    $("#save_play").on("click", function() {
	console.log("test");
	var playName = $("#play_name")[0].value;
	if(playName != ""){
	    if(existingPlays[playName] == undefined) {//saving play with name that doesn't exist 
		save(playName);
	    } else if(edittingPlayName == playName) {//saving the play that is currently being editted
		save(playName);
	    } else if(confirm("Play " + playName + " already exists. Do you want to overwrite it?")){//Trying to overwrite another play
		save(playName);
	    }
	}
    });

    $.get("/playmaker/playNames",
	  {},
	  updateLoadBar,
	  "json");

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

function save(playName) {
    var paths = [];
    for(i = 0; i < tokens.length; i++) {
	var tokenPath = tokens[i].path;
	var path = [];
	for(j = 0; j < tokenPath.length; j++) {
	    path[j] = [Math.round(tokenPath[j].x),
		       Math.round(tokenPath[j].y)];
	}
	paths[i] = path;
    }
    var data = {
	name: playName,
	numFrames: maxFrame + 1,
	paths: JSON.stringify(paths)
    };
    $.post("/playmaker/save",
	   data,
	   updateLoadBar,
	   "json");
    edittingPlayName = playName;
    $("#editing_name")[0].innerHTML = edittingPlayName;
}

function load(data) {
    var play = data.play;
    console.log(play);
}

function updateLoadBar(data) {
    var table = $("#plays")[0];
    var plays = data.plays;
    table.innerHTML = "";
    for(i = 0; i < plays.length; i++) {
	var row = table.insertRow();
	row.innerHTML = "<span id=\"" + plays[i] + "\">" + plays[i] + "</span>";
	$("#" + plays[i]).on("click", function() {
	    $.get("/playmaker/load",
		  plays[i],
		  load,
		  "json");
	});
	existingPlays[plays[i]] = 1;
    }
}

