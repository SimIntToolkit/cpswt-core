var WebSocket = require('ws');
var ws = new WebSocket('ws://localhost:8083/api/fedmgr-ws');

var args = process.argv;
var CMD = "GET_STATUS";

if(args && args.length > 2) {
    CMD = args[2];
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