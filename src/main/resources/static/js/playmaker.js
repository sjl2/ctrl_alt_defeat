
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

var angle = 0;

var tokens = [];

var intervalVar;
var grabbedToken;

var edittingPlayName = "";

var edittingLocations = false;
var deletingPlays = false;

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

function PlayToken(playName) {
    return {

	playName: playName,

	delete: function() {
	    $.post("/playmaker/delete",
		   {name:playName},
		   updateLoadBar,
		   "json");
	},

	getID: function() {
	    return "playName" + playName.replace(/ /g, "_");
	},

	showDelete: function() {
	    $("#delete" + this.getID()).css("display", "inline");
	},

	hideDelete: function() {
	    $("#delete" + this.getID()).css("display", "none");
	},
	
	getHTML: function() {
	    if(edittingPlayName == playName) {
		return "<td class=\"selected-row\">"
		    + "<button id=\"delete" + this.getID() + "\" class=\"btn btn-danger delete_button\">"
		    + "<span class=\"glyphicon glyphicon-trash\"></span></button>"
		    + "<span id=\"" + this.getID() + "\" class=\"playName\">"
		    + playName + "</span>"
		    + "</td>";
	    } else {
		return "<li class=\"list-group-item\">"
		    + "<button id=\"delete" + this.getID() + "\" class=\"btn btn-danger delete_button\">"
		    + "<span class=\"glyphicon glyphicon-trash\"></span></button>"
		    + "<span id=\"" + this.getID() + "\" class=\"playName\">"
		    + playName + "</span>"
		    + "</li>";
	    }
	}
    }
	
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
    //loadColumn.css("height", window.innerHeight);

    var container = $("#canvas_container");
    width = container.width();
    height = Math.floor(width / 1.75);
    container.css("height", height);
    var containerTop = ((window.innerHeight - height) / 2);
    //container.css("top", containerTop);

    var control = $("#control");
    //control.css("top", containerTop + 20);

    var above_court = $("#above_court");
    var above_court_height = $("#play_name").height();
    //above_court.css("top", containerTop - 40);
    //above_court.css("height", above_court_height);

    var offset = container.offset();
    paper = Raphael(offset.left, offset.top, width, height);

    var loadColumn = $("#load_column");
    var loadHeader = $("#load_header");
    var loadContent = $("#load_content");
    loadContent.css("height", loadColumn.height() - loadHeader.height());

    courtTopLeftCorner = Location(offset.left, offset.top);
    courtBottomRightCorner = Location(offset.left + container.width(), offset.top + container.height());

    var court = paper.image("images/Basketball-Court.png", 0, 0, width, height);
    
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

    var circ = paper.circle(width / 2, height / 2, 27);
    circ.attr("fill", "url(images/Basketball-small.png)");
    var ballLoc = Location(width / 2, height / 2);//TODO make ball follow player
    var t = Token(circ, ballLoc);
    circ.drag(onmove, onstart, onend, t, t, t);
    tokens[10] = t;

    $("#frame_number").on("input", function() {
	setFrame(parseInt(this.value));
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

    $("#delete_plays").on("click", function() {
	if(deletingPlays) {
	    deletingPlays = false;
	    for(playName in existingPlays) {
		existingPlays[playName].hideDelete();
	    }
	    $("#delete_plays")[0].innerHTML = "Delete Plays";
	} else {
	    deletingPlays = true;
	    for(playName in existingPlays) {
		existingPlays[playName].showDelete();
	    }
	    $("#delete_plays")[0].innerHTML = "Stop Deleting Plays";
	}
    });

    $("#hide-sidebar").on("click", function(e) {
	e.preventDefault();
	$("#wrapper").toggleClass("toggled");
	var img = $("#hide-img")[0];
	if($("#wrapper").hasClass("toggled")) {
	    img.className = "glyphicon glyphicon-folder-open";
	} else {
	    img.className = "glyphicon glyphicon-chevron-left";
	}
    });

    $("#edit_pos").on("click", function(e) {
	if(edittingLocations) {
	    $("#edit_pos")[0].className = "btn btn-primary";
	    edittingLocations = false;
	    $("#play").prop("disabled", false);
	    $("#stop").prop("disabled", false);
	    $("#frame_number").prop("disabled", false);
	} else {
	    $("#edit_pos")[0].className = "btn btn-warning";
	    edittingLocations = true;
	    setFrame(0);
	    $("#play").prop("disabled", true);
	    $("#stop").prop("disabled", true);
	    $("#frame_number").prop("disabled", true);
	}
    });

    $.get("/playmaker/playNames",
	  {},
	  updateLoadBar,
	  "json");

    document.onkeydown = function(event) {
	if(document.activeElement.type == "text") {
	    return;
	}
	var keyCode = event.keyCode;
	if(keyCode == 37) {//left
	    if(!edittingLocations) {
		previousFrame();
	    }
	} else if(keyCode == 39) {//right
	    if(!edittingLocations) {
		nextFrame();
	    }
	}
    };

}

function previousFrame() {
    if(currentFrame > 0) {
	setFrame(currentFrame - 1);
    }
}

function nextFrame() {
    if(currentFrame < maxFrame) {
	setFrame(currentFrame + 1);
    }
}

function setFrame(frame) {
    currentFrame = frame;
    if(currentFrame > maxFrame) {
	setMaxFrame(currentFrame);
    }
    $("#frame_number").val(currentFrame);
    $("#current_frame")[0].innerHTML = currentFrame;
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
    if(!edittingLocations) {
	intervalVar = window.setInterval(updatePath, 1000.0 / FRAME_RATE);
    }
}

function onmove(dx, dy, x, y, event) {
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
    if(!edittingLocations) {
	for (i = 0; i <= currentFrame; i++) {
	    grabbedToken.path[i] = prevPath[i];
	}
    } else {
	grabbedToken.path[0] = grabbedToken.location.copy();
    }
    grabbedToken = undefined;
    maxFrame = 0;
    for(i = 0; i < tokens.length; i++) {
	if(tokens[i].path.length - 1 > maxFrame) {
	    setMaxFrame(tokens[i].path.length - 1);
	}
    }
    for(i = 0; i < tokens.length; i++) {
	var t = tokens[i];
	if(t.path.length - 1 < maxFrame) {
	    var path = t.path;
	    var length = path.length;
	    for(j = length; j <= maxFrame; j++) {
		path[j] = path[length - 1];
	    }
	}
    }

}

function updatePath() {
    setFrame(currentFrame + 1);
    grabbedToken.path[currentFrame] = grabbedToken.location.copy();
    if(currentFrame > maxFrame) {
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
	$("#current_frame")[0].innerHTML = currentFrame;
	playTime += (playSpeed / FRAME_RATE)
    }
}

function setEditingName(playName) {
    edittingPlayName = playName;
    $("#editing_name")[0].innerHTML = edittingPlayName;
    $("#editing_name").css("visibility", "visible");
}

function setMaxFrame(frameNumber) {
    maxFrame = frameNumber;
    $("#frame_number").prop("max", maxFrame);
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
    setEditingName(playName);
}

function load(data) {
    var play = data.play;
    var paths = play.paths;
    for(i = 0; i < paths.length; i++) {
	var path = paths[i];
	tokens[i].path = [];
	for(j = 0; j < path.length; j++) {
	    var loc = Location(path[j].x, path[j].y);
	    tokens[i].path[j] = loc;
	}
    }
    setMaxFrame(play.numFrames - 1);
    setFrame(0);
    $.get("/playmaker/playNames",
	  {},
	  updateLoadBar,
	  "json");
}

function updateLoadBar(data) {
    var table = $("#plays");
    var plays = data.plays;
    table.innerHTML = "";
    for(i = 0; i < plays.length; i++) {
	var playToken = PlayToken(plays[i]);
	existingPlays[plays[i]] = playToken;
	table.append(playToken.getHTML());
	$("#" + playToken.getID()).on("click", function() {
	    if(deletingPlays) {
		return;
	    }
	    var playName = this.id.replace(/_/g, " ").substring(8);
	    $.get("/playmaker/load",
		  {
		      name: playName
		  },
		  load,
		  "json");
	    setEditingName(playName);
	});
	$("#delete" + playToken.getID()).on("click", playToken.delete);
    }
    $("#delete_plays")[0].innerHTML = "Delete Plays";
    deletingPlays = false;
}



