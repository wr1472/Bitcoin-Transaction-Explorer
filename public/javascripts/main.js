function truncateTransactionId(transactionId){
	return transactionId.substring(0,7) + "...";
}

function log(stmt){
	if(window.console && console.log)
		console.log(stmt);
}

var GENESIS_TX = '0000000000000000000000000000000000000000000000000000000000000000';
var CANVAS_RESIZE_CONST = 160;
var NODE_SPACING_CONST = 120;

var canvas;
var tx;
var connections = [];
var inputCounter = 1;
var outputCounter = 1;

var connectorLineColor = "#456";
var connectorFillColor = "#ABC";
var zpd;

function createCanvas(element, height){
	
	if(canvas == undefined || canvas == null){
		canvas = Raphael(element, "100%", height);
		inputcounter = 1;
		
		log("Created canvas.");
	}
	else
		canvas.setSize("100%", height);
	
	zpd = new RaphaelZPD(canvas, { zoom: true, pan: true, drag: false });
	
	return canvas;
}

function getCanvas(){
	return canvas;
}

function getObjectTransaction(hash, credit, inputSize){
	initialCanvasHeight = 600;
	
	// Dynamically resize canvas if we're going to go over initial size.
	if(inputSize > 4){
		log("Resizing canvas for inputs: " + inputSize);
		initialCanvasHeight = inputSize * CANVAS_RESIZE_CONST;
	}
	
	
	tx = new Transaction(hash, credit, createCanvas("holder", initialCanvasHeight));
	tx.x = 500, tx.y = 300;
	tx.init();
	
	log("Canvas height for inputs: " + getCanvas().height);
}

function getInputTx(hash, scriptSig){
	//var height = $("holder").style.height;
	//log("Canvas height: " + height);
	
	itx = new Transaction(hash, null, getCanvas());
	itx.x = 300, itx.y = (inputCounter++) * NODE_SPACING_CONST;
	itx.init();
	connections.push(getCanvas("holder").connection(
			tx.getNode(), itx.getNode(), connectorLineColor, "#000", scriptSig));
}

function getOutputTx(scriptSigPublicKey, inputTX){
	$(document).ready(function(){
		log("Getting output transactions for: " + scriptSigPublicKey);
	
		$.ajax({
			type: "GET",
			url: "/output/" + scriptSigPublicKey + "/" + inputTX, 
			contentType: "application/json; charset=utf-8",
			dataType: "json",
			async: false,
			success: function(transactions) {

				var resize = transactions.length * CANVAS_RESIZE_CONST;
				
				// Dynamically resize canvas if we're going to go over current size.
				if(resize > getCanvas().height){
					getCanvas().setSize("100%", resize);
				}
				
				log("Canvas height for outputs: " + getCanvas().height);
			
				for(var i = 0; i < transactions.length; i++){
					transaction = transactions[i];
										
					otx = new Transaction(transaction.hash, transaction.credit, getCanvas());
					otx.x = 700, otx.y = (outputCounter++) * NODE_SPACING_CONST;
					otx.init();
					connections.push(getCanvas("holder").connection(
							otx.getNode(), tx.getNode(), connectorLineColor, "#000", scriptSigPublicKey));
				}
			},
			error: function(err) {
				alert(err.message.toString());
					if (err.status == 200) {
							console.log(err);
					}
					else { alert('Error:' + err.responseText + '  Status: ' + err.status); }
			}
		});
	});
}

function getTransaction(txID){
	log("Getting transaction: " + txID);
	window.location = "/transaction/" + txID;
	getCanvas().clear();
}

function Transaction(txID, credit, r){
	var radius = 50;
	var x,y = 0;
	var node, text, set;
	
	this.init = function(){
		this.set = r.set();
		this.set.push(
			this.createNode(),
			this.createText()
		);
		
		for (var i = 0, ii = this.set.length; i < ii; i++) {
			if(txID != GENESIS_TX) {
				this.set[i].attr({
					cursor: "pointer"
					,title : txID		    
				});
				
				$(this.set[i].node).click(function (event) {
				    	getTransaction(txID);
				});
			}
		}
	}
	
	this.createNode = function(){
		//log("x: " + this.x);
		//log("y: " + this.y);
			
		this.node = r.ellipse(this.x,this.y, radius, radius);
			    
    	this.node.attr({
			fill: connectorFillColor
            ,"fill-opacity": 1
			,stroke: connectorLineColor
			,"stroke-width": 5
		});
				
		
		return this.node;
	}
	
	this.getNode = function(){
		return this.node;
	}
	
	this.createText = function() {
		log("credit: " + credit);
		
		if(credit == undefined || credit == null)
			this.text = r.text(this.x,this.y, truncateTransactionId(txID));
		else {
			credit = Math.round(credit * 10000000) / 10000000;
			this.text = r.text(this.x,this.y, truncateTransactionId(txID) + "\nBTC " + credit);
		}
		
		this.text.attr({
			fill: "#AD0303"
			,"font-size": 11
			,"font-family": "helvetica"
		});
		
		return this.text;
	}
	
	this.getText = function(){
		return this.text;
	}
};
	
Raphael.fn.connection = function(obj1, obj2, line, bg, address) {
	log(address);
    if (obj1.line && obj1.from && obj1.to) {
        line = obj1;
        obj1 = line.from;
        obj2 = line.to;
    }
	
    var bb1 = obj1.getBBox(),
        bb2 = obj2.getBBox(),
        p = [{
            x: bb1.x + bb1.width / 2,
            y: bb1.y - 1
        }, {
            x: bb1.x + bb1.width / 2,
            y: bb1.y + bb1.height + 1
        }, {
            x: bb1.x - 1,
            y: bb1.y + bb1.height / 2
        }, {
            x: bb1.x + bb1.width + 1,
            y: bb1.y + bb1.height / 2
        }, {
            x: bb2.x + bb2.width / 2,
            y: bb2.y - 1
        }, {
            x: bb2.x + bb2.width / 2,
            y: bb2.y + bb2.height + 1
        }, {
            x: bb2.x - 1,
            y: bb2.y + bb2.height / 2
        }, {
            x: bb2.x + bb2.width + 1,
            y: bb2.y + bb2.height / 2
        }],
        d = {},
        dis = [];
    for (var i = 0; i < 4; i++) {
        for (var j = 4; j < 8; j++) {
            var dx = Math.abs(p[i].x - p[j].x),
                dy = Math.abs(p[i].y - p[j].y);
            if ((i == j - 4) || (((i != 3 && j != 6) || p[i].x < p[j].x) && ((i != 2 && j != 7) || p[i].x > p[j].x) && ((i != 0 && j != 5) || p[i].y > p[j].y) && ((i != 1 && j != 4) || p[i].y < p[j].y))) {
                dis.push(dx + dy);
                d[dis[dis.length - 1]] = [i, j];
            }
        }
    }
    if (dis.length == 0) {
        var res = [0, 4];
    } else {
        res = d[Math.min.apply(Math, dis)];
    }
    var x1 = p[res[0]].x,
        y1 = p[res[0]].y,
        x4 = p[res[1]].x,
        y4 = p[res[1]].y;
    dx = Math.max(Math.abs(x1 - x4) / 2, 10);
    dy = Math.max(Math.abs(y1 - y4) / 2, 10);
    var x2 = [x1, x1, x1 - dx, x1 + dx][res[0]].toFixed(3),
        y2 = [y1 - dy, y1 + dy, y1, y1][res[0]].toFixed(3),
        x3 = [0, 0, 0, 0, x4, x4, x4 - dx, x4 + dx][res[1]].toFixed(3),
        y3 = [0, 0, 0, 0, y1 + dy, y1 - dy, y4, y4][res[1]].toFixed(3);
    var path = ["M", x1.toFixed(3), y1.toFixed(3), "C", x2, y2, x3, y3, x4.toFixed(3), y4.toFixed(3)].join(",");
    if (line && line.line) {
        line.bg && line.bg.attr({
            path: path
        });
        line.line.attr({
            path: path
        });
    } else {
        var color = typeof line == "string" ? line : "#000";
        return {
            bg: bg && bg.split && this.path(path).attr({
                stroke: bg.split("|")[0],
                fill: "none",
                "stroke-width": bg.split("|")[1] || 3
                ,title : address
            }),
            line: this.path(path).attr({
                stroke: color,
                fill: "none"
                ,"stroke-width": 3
                ,title: address
            }),
            from: obj1,
            to: obj2
        };
    }
};