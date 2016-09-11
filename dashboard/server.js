// Execute the web socket server
require('./server/wsServer.js');

var express = require('express'),
    bodyParser = require('body-parser'),
    beaconsData = require('./server/beaconData.json'),
    app = express();

var config = {
  wsPort: 1337,
  apiPort: 3000
};

app.use('/dashboard', express.static(__dirname + '/static'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
app.get('/test', function(req, res){

  console.log('/test REQUESTED');
  res.send({data: 'test processed'});
});
app.post('/beaconInfo', function(req, res) {
  console.log('Beacon Info');
  console.log(req.body);
  console.log(req.body.id);
  res.send({
    success:true, 
    data: beaconsData[req.body.id]
  });
});

app.listen(config.apiPort, function(){
  console.log('API is listening at port ' +  config.apiPort);
});
