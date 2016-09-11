var gmaps = (function(){

  var map;

  function createMarker(info){

    var infoWindow = new google.maps.InfoWindow();

    var marker = new google.maps.Marker({
      position: new google.maps.LatLng(info.lat, info.long),
      title: info.emergency.type
    });

    marker.content = '<div class="infoWindowContent">' + info.emergency.description + '</div>';

    google.maps.event.addListener(marker, 'click', function(){
      infoWindow.setContent('<h2>' + marker.title + '</h2>' + marker.content);
      infoWindow.open(map, marker);
    });

    marker.setMap(map);

  }

  function initMap() {

    if (!map){
      map = new google.maps.Map(
        document.getElementById('map'),
        {
          center: {
            lat: 20.735135,
            lng: -103.455331
          },
          scrollwheel: true,
          zoom: 13
        }
      );
    }
  }

  function openInfoWindow(e, selectedMarker){
    e.preventDefault();
    google.maps.event.trigger(selectedMarker, 'click');
  }

  return {
    addMarker: createMarker,
    init: initMap
  };

})();
