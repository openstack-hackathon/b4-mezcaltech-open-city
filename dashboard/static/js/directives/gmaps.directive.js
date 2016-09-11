angular.module('dashboardApp').directive('gmaps', [
  'GMAPS_KEY',
  function(GMAPS_KEY){
    return {
      restrict: 'AE',
      link: function(scope, element, attributes){

        var gmaps = google.maps;

        var mapOptions = {
          zoom: 5,
          center: new gmaps.LatLng(40.0000, -98.0000),
          mapTypeId: gmaps.MapTypeId.TERRAIN
        };

        var map = new gmaps.Map(element, mapOptions);
        console.log(map);
      }
    }
  }]
);

angular.module('dashboardApp').directive('mapCanvas', function(){
		return {
			restric: 'EA',
			//template: '<div id="gmap"></div>',
			//replace: true,
			link: function(scope, element, attrs){
				if(attrs.check != "1")
				{
					var coors =  attrs.center.split(',');
					var map, infoWindow;
					var mapOptions = {
						center: new google.maps.LatLng(parseFloat(coors[0]),parseFloat(coors[1])),
						zoom: parseInt(attrs.zoom),
						mapTypeId: google.maps.MapTypeId.ROADMAP,
						scrollwheel: false
					};
					map = initMap(0);
					setMarker(map, new google.maps.LatLng(parseFloat(coors[0]),parseFloat(coors[1])),attrs.marker);
				}
				element.bind('click', function() {
					var coors =  attrs.center.split(',');
					var map, infoWindow;
					var mapOptions = {
						center: new google.maps.LatLng(parseFloat(coors[0]),parseFloat(coors[1])),
						zoom: parseInt(attrs.zoom),
						mapTypeId: google.maps.MapTypeId.ROADMAP,
						scrollwheel: false
					};
					map = initMap(1,map,mapOptions);
					setMarker(map, new google.maps.LatLng(parseFloat(coors[0]),parseFloat(coors[1])),attrs.marker);
				});
				function initMap(check,mapResults=null,mapOptionsResults=null){
					if(map=== void 0){
						if(check==1){
						map = new google.maps.Map(element.parents(".resultados").find(".ubication")[0],mapOptionsResults);}
						else{
						map = new google.maps.Map(element[0],mapOptions);}
						return map;
					}
				}

				function setMarker(map, position, data){
					var data = JSON.parse(data);
					var marker;
					var markers = [];
					var markerOptions = {
						position: position,
						map: map,
						title: data.title,
						icon: 'https://maps.google.com/mapfiles/ms/icons/green-dot.png'
					};
					var baseContent = '<div id="iw-container">' +
					                    '<div class="iw-title">'+data.title+'</div>' +
					                    '<div class="iw-content">' +
					                    '<div class="iw-subTitle">'+data.subtitle+'</div>' +
					                    '<img src="'+data.image+'" alt="Porcelain Factory of Vista Alegre" height="115" width="83">' +
					                    '<p>'+data.content+'</p>' +
					                    '<div class="iw-subTitle">Contacts</div>' +
					                    '<p>'+data.addr+'<br>'+
						            '</div>' +
					                    '<div class="iw-bottom-gradient"></div>' +
					                  '</div>';


					marker = new google.maps.Marker(markerOptions);
					markers.push(marker);
					google.maps.event.addListener(marker, 'click', function(){
						if(infoWindow !== void 0){
							infoWindow.close();
						}
						var infoWindowOptions = {
							content: baseContent
						};
						infoWindow = new google.maps.InfoWindow(infoWindowOptions);
						infoWindow.open(map, marker);
					});
				}
			}
		}
	});
