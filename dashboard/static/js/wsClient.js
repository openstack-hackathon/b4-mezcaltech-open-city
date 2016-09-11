var wsClient = (function(){
  var connection;

  if (window.WebSocket){
    connection = new window.WebSocket('ws://172.16.11.227:1337');

    connection.onopen = function(){
      console.info('Open connection');
    };

    connection.onerror = function(err){
      console.error(err);
    };

    connection.onmessage = function(message){
      var jsonMessage = JSON.parse(message.data);

      if (!jsonMessage.hasOwnProperty('type')){
        gmaps.addMarker(jsonMessage);
        cardFactory.addCard(jsonMessage);
      }
    };

    return connection;

  } else {
    throw new Exception("WS is not supported");
  }
})();
