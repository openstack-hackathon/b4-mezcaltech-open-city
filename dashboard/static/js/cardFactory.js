var cardFactory = (function(){

  function createCard(emergencyInfo){

    var card = document.createElement('div'),
        title = document.createElement('h5'),
        time = document.createElement('h6'),
        description = document.createElement('p');

    card.className = 'card-panel teal white-text bg-img'

    title.innerHTML = emergencyInfo.emergency.type;
    time.innerHTML = emergencyInfo.date;
    description.innerHTML = emergencyInfo.emergency.description;

    card.appendChild(title);
    card.appendChild(time);
    card.appendChild(description);

    document.getElementById('alerts').appendChild(card);

    card = null;
    title = null;
    time = null;
    description = null;
  }

  return {
    addCard: createCard
  };

})();
