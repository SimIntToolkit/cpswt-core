var WebSocket = require('ws');
var ws = new WebSocket('ws://localhost:8083/api/fedmgr-ws');

ws.on('open', function open() {
    ws.send('test');
});

ws.on('message', function(data, flags) {
    console.log(data);
});