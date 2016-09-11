angular.module('dashboardApp').service('websocket', [
  'WS_BASE',
  function(WS_BASE){

    var connection;

    if (window.WebSocket){
      connection = new window.WebSocket(WS_BASE);

      connection.onopen = function(){
        console.info('Open connection');
      };

      connection.onerror = function(err){
        console.error(err);
      };

      connection.onmessage = function(message){
        document.getElementById('to').innerText = JSON.parse(message.data).data;
        console.info('Received data: ' +  message.data);
      };
    } else {
      throw new Exception("WS is not supported");
    }

    return {
      ws: connection
    };
  }]
);
