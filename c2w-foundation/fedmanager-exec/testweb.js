var http = require("http");
var options = {
  hostname: 'localhost',
  port: 8083,
  path: '/api/fedmgr',
  method: 'POST',
  headers: {
      'Content-Type': 'application/json',
  }
};

var req = http.request(options, function(res) {
  console.log('Status: ' + res.statusCode);
  console.log('Headers: ' + JSON.stringify(res.headers));
  res.setEncoding('utf8');

  var body = '';

  res.on('data', function (chunk) {
  	body += chunk;
    console.log('chunk: ' + chunk);
  });
  res.on('end', function() {
  	console.log('body: ' + body);
  })
});
req.on('error', function(e) {
  console.log('problem with request: ' + e.message);
});
// write data to request body
req.write('{"targetState": "RUNNING"}');
req.end();
