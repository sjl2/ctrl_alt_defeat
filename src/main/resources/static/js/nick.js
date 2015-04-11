var paper = Raphael(document.getElementById("content"), 500, 500);
var bBox1 = paper.rect(100, 100, 120, 50, 10).attr({fill: 'darkorange', stroke: 'black', 'stroke-width': 2});
var text1 = paper.text(bBox1.attrs.x + bBox1.attrs.width / 2, bBox1.attrs.y + bBox1.attrs.height / 2, 'Stat')
.attr({"font-family": "Arial", "font-size": 16});
text1.box = bBox1;

var button1 = paper.set();
button1.push(bBox1);
button1.push(text1);

var bBox2 = paper.rect(100, 155, 120, 50, 10).attr({fill: 'darkorange', stroke: 'black', 'stroke-width': 2});
var text2 = paper.text(bBox2.attrs.x + bBox2.attrs.width / 2, bBox2.attrs.y + bBox2.attrs.height / 2, 'Stat')
.attr({"font-family": "Arial", "font-size": 16});
text2.box = bBox2;


var button2 = paper.set();
button2.push(bBox2);
button2.push(text2);

var bBox3 = paper.rect(100, 210, 120, 50, 10).attr({fill: 'darkorange', stroke: 'black', 'stroke-width': 2});
var text3 = paper.text(bBox3.attrs.x + bBox3.attrs.width / 2, bBox3.attrs.y + bBox3.attrs.height / 2, 'Stat')
.attr({"font-family": "Arial", "font-size": 16});
text3.box = bBox3;


var button3 = paper.set();
button3.push(bBox3);
button3.push(text3);

var bBox4 = paper.rect(100, 265, 120, 50, 10).attr({fill: 'darkorange', stroke: 'black', 'stroke-width': 2});
var text4 = paper.text(bBox4.attrs.x + bBox4.attrs.width / 2, bBox4.attrs.y + bBox4.attrs.height / 2, 'Stat')
.attr({"font-family": "Arial", "font-size": 16});
text4.box = bBox4;


var button4 = paper.set();
button4.push(bBox4);
button4.push(text4);

var buttons = paper.set();
buttons.push(button1);
buttons.push(button2);
buttons.push(button3);
buttons.push(button4);

var text = paper.set();
text.push(text1);
text.push(text2);
text.push(text3);
text.push(text4);

var bBoxes = paper.set();
bBoxes.push(bBox1);
bBoxes.push(bBox2);
bBoxes.push(bBox3);
bBoxes.push(bBox4);

bBoxes.mousedown(function(e) {
	this.attr({fill: '#a2591d'});
});
bBoxes.mouseup(function(e) {
	this.attr({fill: 'darkorange'});
});
bBoxes.mouseout(function(e) {
	this.attr({fill: 'darkorange'});
});
text.mousedown(function(e) {
	this.box.attr({fill: '#a2591d'});
});
text.mouseup(function(e) {
	this.box.attr({fill: 'darkorange'});

});




buttons.mouseover(function (event) {
    console.log("here");
    this.g = this.glow({
        opacity: 0.85,
        color: 'blue',
        width: 15
    });
}).mouseout(function (event) {
    this.g.remove();
});

buttons.click(function(e) {
	console.log("clicked!");
});
