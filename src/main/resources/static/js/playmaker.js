
const FRAME_RATE = 30.0;

var playSpeed = 1;
var playing = false;

var currentFrame = 0;
var maxFrame = 0;

var width = 0;
var height = 0;

var playerRadius;
var ballRadius;

var courtTopLeftCorner;
var courtBottomRightCorner;

var paper;

var tokens = [];
var ball;
var possessionToken;


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
	    existingPlays[playName] = undefined;
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
	    return "<li id=\"" + this.getID() + "\" class=\"list-group-item playNameHolder\">"
		+ "<button id=\"delete" + this.getID() + "\" class=\"btn btn-danger delete_button\">"
		+ "<span class=\"glyphicon glyphicon-trash\"></span></button>"
		+ playName + "</li>";
	}
    }
	
}

function Ball(playerToken, angle) {
    var newBall =  {
	location: Location(0, 0),
	circle: undefined,
	path: [],
	possession: [],
	angle: 0,
	
	checkCollision: function(playerToken) {
	    var dx = this.location.x - playerToken.location.x;
	    var dy = this.location.y - playerToken.location.y;
	    var distanceSquared = dx * dx + dy * dy;
	    return distanceSquared < (playerRadius + ballRadius) * (playerRadius + ballRadius);
	},
	
	setLocationWithXY: function(x, y) {
	    this.circle.attr("cx", x * width);
	    this.circle.attr("cy", y * height);
	    this.location.x = x;
	    this.location.y = y;
	},

	setLocationWithLoc: function(loc) {
	    this.setLocationWithXY(loc.x, loc.y);
	},

	translate: function(dx, dy) {
	    this.circle.attr("cx", this.circle.attr("cx") + dx);
	    this.circle.attr("cy", this.circle.attr("cy") + dy);
	    this.location.translate(dx / width, dy / height);
	},

	getRelativeLocation: function(playerToken, angle) {
	    var x = playerToken.location.x + playerRadius * Math.cos(angle);
	    var y = playerToken.location.y + (width / height) * playerRadius * Math.sin(angle);
	    return Location(x, y);
	},

	setRelativeLocation: function(playerToken) {
	    this.angle = Math.atan2(0.5 - playerToken.location.y, 0.08125 - playerToken.location.x);
	    var x = playerToken.location.x + playerRadius * Math.cos(this.angle);
	    var y = playerToken.location.y + (width / height) * playerRadius * Math.sin(this.angle);
	    this.setLocationWithXY(x, y);
	    return Location(x, y);
	}
    }
    var location = newBall.getRelativeLocation(playerToken, angle);
    newBall.circle = paper.circle(location.x * width, location.y * height, ballRadius * width).attr("fill", "#FA8320");
    newBall.circle.drag(onmove, onballstart, onballend, newBall, newBall, newBall);
    newBall.setLocationWithXY(location.x, location.y);
    newBall.path[0] = newBall.location.copy();
    newBall.possession[0] = 0;
    return newBall;
}

function Token(circle, text, location, index) {
    return {
	index: index,
	circle: circle,
	text: paper.text(location.x * width, location.y * height, text).attr({"font-size": "20", "fill":"#FFFFFF"}),
	path: [location.copy()],
	location: location.copy(),
	translate: function(dx, dy) {
	    this.circle.attr("cx", circle.attr("cx") + dx);
	    this.circle.attr("cy", circle.attr("cy") + dy);
	    this.text.attr("x", circle.attr("cx"));
	    this.text.attr("y", circle.attr("cy"));
	    this.location.translate(dx / width, dy / height);
	    if(possessionToken.index == this.index) {
		ball.setRelativeLocation(possessionToken);
	    }
	},
	setLocationWithXY: function(x, y) {
	    this.circle.attr("cx", x * width);
	    this.circle.attr("cy", y * height);
	    this.text.attr("x", x * width);
	    this.text.attr("y", y * height);
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

    var court = paper.image("images/Basketball-Court-Playmaker.png", 0, 0, width, height);
    
    var startingLocations = [Location(0.35, 0.50),
			     Location(0.26, 0.1754),
			     Location(0.06, 0.8246),
			     Location(0.075, 0.32456),
			     Location(0.19, 0.6666),
			     Location(0.29, 0.5),
			     Location(0.22, 0.24561),
			     Location(0.06, 0.7193),
			     Location(0.075, 0.42982),
			     Location(0.15, 0.59649)];
    var positionAbrevs = ["PG", "SG", "SF", "PF", "C",
			  "PG", "SG", "SF", "PF", "C"];
    playerRadius = 0.025;
    ballRadius = playerRadius * 0.5;
    for(i = 0; i < 10; i++) {
	var loc = startingLocations[i];
	loc.x = loc.x;
	loc.y = loc.y;
	var circ2 = paper.circle(loc.x * width, loc.y * height, playerRadius * width);
	if(i < 5) {
	    circ2.attr("fill", "#337ab7");
	} else {
	    circ2.attr("fill", "#dc2300");
	}
	var t = Token(circ2, positionAbrevs[i], loc, i);
	circ2.drag(onmove, onstart, onend, t, t, t);
	t.text.drag(onmove, onstart, onend, t, t, t);
	tokens[i] = t;
    }
    possessionToken = tokens[0];

    $.get("/playmaker/getPlayerNumbers", {}, function(data) {
	var playerNumbers = JSON.parse(data).playerNumbers;
	if(playerNumbers != undefined) {
	    for(i = 0; i < playerNumbers.length; i++) {
		tokens[i].text.attr("text", playerNumbers[i].toString());
	    }
	}
    });
    
    ballAngle = Math.PI
    ball = Ball(possessionToken, ballAngle);

    $("#frame_number").on("input", function() {
	if(playing) {
	    stop();
	}
	setFrame(parseInt(this.value));
    });
    
    $("#playSpeed").on("input", function() {
	playSpeed = parseFloat($("#playSpeed").val());
    });

    $("#tokenRadius").on("input", function() {
	updateRadii(parseFloat($("#tokenRadius").val()));
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

    $("#hide-load-sidebar").on("click", function(e) {
	e.preventDefault();
	$("#load-sidebar-col").toggleClass("toggled");
	var img = $("#load-hide-img")[0];
	if($("#load-sidebar-col").hasClass("toggled")) {
	    img.className = "glyphicon glyphicon-folder-open";
	} else {
	    img.className = "glyphicon glyphicon-chevron-left";
	}
    });

    $("#hide-settings-sidebar").on("click", function(e) {
	e.preventDefault();
	$("#settings-sidebar-col").toggleClass("toggled");
	var img = $("#settings-hide-img")[0];
	if($("#settings-sidebar-col").hasClass("toggled")) {
	    img.className = "glyphicon glyphicon-cog";
	} else {
	    img.className = "glyphicon glyphicon-chevron-right";
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

function updateRadii(newPlayerRadius) {
    playerRadius = newPlayerRadius;
    ballRadius = playerRadius * 0.5;
    for(i = 0; i < tokens.length; i++) {
	tokens[i].circle.attr("r", playerRadius * width);
    }
    ball.circle.attr("r", ballRadius * width);
    ball.setRelativeLocation(possessionToken);
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
	    t.setLocationWithLoc(t.path[currentFrame]);
	} 
    }
    possessionToken = tokens[ball.possession[currentFrame]];
    ball.setRelativeLocation(possessionToken);
}

function onstart(x, y, event) {
    this.dx = 0;
    this.dy = 0;
    grabbedToken = this;
    if(!edittingLocations) {
	intervalVar = window.setInterval(updatePath, 1000.0 / (FRAME_RATE * playSpeed));
    }
}

function onballstart(x, y, event) {
    this.dx = 0;
    this.dy = 0;
    this.grabbed = true;
}

function onmove(dx, dy, x, y, event) {
    var insideX = true;
    var insideY = true;
    if(x < courtTopLeftCorner.x) {
	insideX = false;
	this.translate(-this.location.x * width, 0);
    } else if(x > courtBottomRightCorner.x) {
	insideX = false;
	this.translate(width - this.location.x * width, 0);
    } 
    if(y < courtTopLeftCorner.y) {
	insideY = false;
	this.translate(0, -this.location.y * height);
    } else if(y > courtBottomRightCorner.y) {
	insideY = false;
	this.translate(0, height - this.location.y * height);
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
	    grabbedToken.path[i] = prevPath[i].copy();
	}
    } else {
	grabbedToken.path[0] = grabbedToken.location.copy();
	if(this.index == possessionToken.index) {
	    ball.path = [];
	    ball.possession = [];
	    ball.path[0] = ball.getRelativeLocation(possessionToken, ballAngle);
	    ball.possession[0] = possessionToken.index;
	}
    }
    grabbedToken = undefined;
    maxFrame = 0;
    for(i = 0; i < tokens.length; i++) {
	if(tokens[i].path.length - 1 > maxFrame) {
	    setMaxFrame(tokens[i].path.length - 1);
	}
    }

}

function onballend(event) {
    this.grabbed = false;
    for(i = 0; i < tokens.length; i++) {
	var t = tokens[i];
	if(this.checkCollision(t)) {
	    possessionToken = t;
	    ball.possession[currentFrame] = possessionToken.index;
	}
    }
    ball.path[currentFrame] = ball.setRelativeLocation(possessionToken);
}

function updatePath() {
    for(i = 0; i < tokens.length; i++) {
	var t = tokens[i];
	if(t.path[currentFrame + 1] == undefined) {
	    t.path[currentFrame + 1] = t.location.copy();
	}
    }
    //grabbedToken.path[currentFrame + 1] = grabbedToken.location.copy();
    ball.path[currentFrame + 1] = ball.setRelativeLocation(possessionToken);
    ball.possession[currentFrame + 1] = possessionToken.index;

    setFrame(currentFrame + 1);

    if(currentFrame > maxFrame) {
	setMaxFrame(currentFrame);
    }
}

function play() {
    if(playing == false){
	playing = true;
	playIntervalVar = window.setInterval(stepAnimation, 1000.0 / (FRAME_RATE * playSpeed));
	for(i = 0; i < tokens.length; i++) {
	    var t = tokens[i]
	    t.circle.undrag();
	}
	ball.circle.undrag();
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
	ball.circle.drag(onmove, onballstart, onballend, ball, ball, ball);
    }
}

function stepAnimation() {
    if(playing) {
	currentFrame++;
	for(i = 0; i < tokens.length; i++) {
	    var t = tokens[i];
	    var nextLoc;
	    nextLoc = t.path[currentFrame];
	    t.location = nextLoc.copy();
	    t.circle.animate({cx:nextLoc.x * width, cy:nextLoc.y * height},
			     1000.0 / (FRAME_RATE * playSpeed),
			     "linear",
			     undefined);
	    t.text.animate({x:nextLoc.x * width, y:nextLoc.y * height},
			     1000.0 / (FRAME_RATE * playSpeed),
			     "linear",
			     undefined);
	}

	var nextLoc;
	nextLoc = ball.path[currentFrame];
	ball.location = nextLoc.copy();
	ball.circle.animate({cx:nextLoc.x * width, cy:nextLoc.y * height},
			 1000.0 / (FRAME_RATE * playSpeed),
			 "linear",
			 undefined);

	if(currentFrame >= maxFrame) {
	    stop();
	}
	possessionToken = tokens[ball.possession[currentFrame]];
	$("#frame_number").val(currentFrame);
	$("#current_frame")[0].innerHTML = currentFrame;
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
	    path[j] = [tokenPath[j].x, tokenPath[j].y];
	}
	paths[i] = path;
    }
    var ballPath = ball.path;
    var path = [];
    for(i = 0; i < ballPath.length; i++) {
	path[i] = [ballPath[i].x, ballPath[i].y];
    }
    var data = {
	name: playName,
	numFrames: maxFrame + 1,
	paths: JSON.stringify(paths),
	ballPath: JSON.stringify(path)
    };
    $.post("/playmaker/save",
	   data,
	   updateLoadBar,
	   "json");
    setEditingName(playName);
}

function load(data) {
    var play = data.play;
    var playerPaths = play.playerPaths;
    for(i = 0; i < playerPaths.length; i++) {
	var path = playerPaths[i];
	tokens[i].path = [];
	for(j = 0; j < path.length; j++) {
	    var loc = Location(path[j].x, path[j].y);
	    tokens[i].path[j] = loc;
	}
    }
    var ballPath = play.ballPath;
    ball.path = [];
    for(i = 0; i < ballPath.length; i++) {
	ball.path[i] = Location(ballPath[i].x, ballPath[i].y);
    }
    setMaxFrame(play.numFrames - 1);
    setFrame(0);
}

function updateLoadBar(data) {
    var table = $("#plays");
    var plays = data.plays;
    table[0].innerHTML = "";
    for(i = 0; i < plays.length; i++) {
	var playToken = PlayToken(plays[i]);
	existingPlays[plays[i]] = playToken;
	table.append(playToken.getHTML());
	$("#delete" + playToken.getID()).on("click", playToken.delete);
    }
    $(".playNameHolder").on("click", function() {
	if(deletingPlays) {
	    return;
	}
	$(".playNameHolder").removeClass("selected-row");
	this.className = "list-group-item playNameHolder selected-row";
	var playName = this.id.replace(/_/g, " ").substring(8);
	$.get("/playmaker/load",
	      {
		  name: playName
	      },
	      load,
	      "json");
	setEditingName(playName);
    });
    $("#delete_plays")[0].innerHTML = "Delete Plays";
    deletingPlays = false;
}



