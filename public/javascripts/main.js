function truncateTransactionId(transactionId){
	return transactionId.substring(0,7) + "...";
}

function log(stmt){
	if(window.console && console.log)
		console.log(stmt);
}

var GENESIS_TX = '0000000000000000000000000000000000000000000000000000000000000000';
var canvas;
var tx;
var connections = [];
var inputCounter = 1;
var outputCounter = 1;

var connectorLineColor = "#456";
var connectorFillColor = "#ABC";

function getCanvas(element){
	
	if(canvas == undefined || canvas == null){
		canvas = Raphael(element, 1000, 5000);
		inputcounter = 1;
		
		log("Created canvas.");
	}
	
	return canvas;
}

function getOutputTx(scriptSigPublicKey){
	$(document).ready(function(){
		log("Getting output transactions for: " + scriptSigPublicKey);
	
		$.ajax({
			type: "GET",
			url: "/output/" + scriptSigPublicKey, 
			contentType: "application/json; charset=utf-8",
			dataType: "json",
			async: false,
			success: function(transactions) {
				for(var i = 0; i < transactions.length; i++){
					transaction = transactions[i];
					log("tx: " + transaction.out.length);
					otx = new Transaction(transaction.hash, transaction.credit, getCanvas("holder"));
					otx.x = 700, otx.y = (outputCounter++) * 120;
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
				});
				
				$(this.set[i].node).click(function (event) {
				    	getTransaction(txID);
				});
			}
		}
	}
	
	this.createNode = function(){
		log("x: " + this.x);
		log("y: " + this.y);
			
		this.node = r.ellipse(this.x,this.y, radius, radius);
			    
    	this.node.attr({
			fill: connectorFillColor
            ,"fill-opacity": 1
			,stroke: connectorLineColor
			,"stroke-width": 3
			,title : txID
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