angular.module('dashboardApp')
  .controller('dashboardCtrl', [
    '$scope',
    'websocket',
    function($scope, websocket){

      $scope.sendData = function(){
        websocket.ws.send('Hi from AngularJS');
      };

      console.info(websocket);
    }
  ]);
