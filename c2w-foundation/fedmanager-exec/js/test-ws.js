var WebSocket = require('ws');
var ws = new WebSocket('ws://localhost:8083/fedmgr-ws');

var args = process.argv;
var CMD = "GET_STATUS";

if(args && args.length) {
    CMD = args[0];
}

ws.on('open', function open() {
    ws.send(CMD);
});

ws.on('message', function(data, flags) {
    console.log(data);
});

ws.on('error', function(msg) {
    console.log('error: ' + msg);
});