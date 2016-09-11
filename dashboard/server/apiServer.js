var express = require('express'),
    bodyParser = require('body-parser'),
    app = express();

var config = {
  wsPort: 1337,
  apiPort: 3000
};

app.use('/dashboard', express.static(__dirname + '/static'));
app.use('/pub', express.static(__dirname + '/static'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));

app.get('/test', function(req, res){

  console.log('/test REQUESTED once');
  res.send({data: 'test processed'});
});
app.get('/beaconInfo', function(req, res) {
  console.log('/test REQUESTED');
  res.send({data: 'test processed'});
});
/*app.get('/', function(req, res) {
  console.log(req);
  res.send(JSON.stringify({
    success:true, 
      data:{
      area:'CCI Tecnologico de Monterrey',
      nivelPeligro: 'Tranquilo'
    }
  }));
});*/

app.listen(config.apiPort, function(){
  console.log('API is listening at port ' +  config.apiPort);
});
