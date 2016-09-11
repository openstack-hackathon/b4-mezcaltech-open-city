var webSocketServer = require('websocket').server,
    http = require('http');

var config = {
  wsPort: 1337,
  apiPort: 3000
};

var server = http.createServer(function(req, res){
});

server.listen(config.wsPort, function(){
  console.log('Socket is listening at port ' + config.wsPort);
});

var wsServer = new webSocketServer({ httpServer: server });

wsServer.on('request', function(request){

  var connection = request.accept(null, request.origin);
  console.log("Connection from: " + request.origin);

  console.log(wsServer.connections.length);

  connection.sendUTF(
    JSON.stringify({
      type: 'success',
      data: 'connected'
    })
  );

  connection.on('message', function(message) {
    console.log('Received Message: ' +  message.utf8Data);

    var temp = JSON.parse(message.utf8Data);
    Object.assign(temp, {date: new Date().toLocaleString()});

    wsServer.broadcastUTF(JSON.stringify(temp));
  });
});
