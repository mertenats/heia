// ---------- Initial values ---------------------------------------------------
// base url used by all functions
var JAVA_PROJECT_NAME = 'TP5_CoapProxy';
var BASE_URL = 'http://localhost:8080/' + JAVA_PROJECT_NAME + '/rest/weatherstation/';

var TRUE = 'true';
var FALSE = 'false';

var TEMP_SENSOR_ID = 'temperature';
var PRES_SENSOR_ID = 'pressure';
var HUM_SENSOR_ID = 'humidity';
var STATE_SENSOR_ID = 'state';

// variable used for the timer fonction
// var timerMap = {};
// Map(): https://developer.mozilla.org/fr/docs/Web/JavaScript/Reference/Objets_globaux/Map
var timerMap = new Map();

// timeout value
var TIMER_INTERVAL = 5000; // [ms]

// to clear the timer: clearInterval(timer);
// timing events: http://www.w3schools.com/js/js_timing.asp
//var timer = setInterval(onTimerInterval, TIMER_INTERVAL);

// ---------- Circle color management (weather station preview) ----------------
// colors (CSS classes)
var BLUE = 'blue';
var GREEN = 'green';
var ORANGE = 'orange';
var RED = 'red';

// method called to retrieve the css class for the given temperature
function getColorFromTemperature(p_temperature) {
  // define temperature range
  var HIGH_TEMP = 25;
  var MEDIUM_TEMP = 15;
  var LOW_TEMP = 5;

  if (p_temperature < LOW_TEMP) {
    return BLUE;
  } else if (p_temperature < MEDIUM_TEMP) {
    return GREEN;
  } else if (p_temperature < HIGH_TEMP) {
    return ORANGE;
  }
  return RED;
}

// method called to retrieve the css class for the given pressure
function getColorFromPressure(p_pressure) {
  // define pressure range
  var HIGH_PRES = 980;
  var MEDIUM_PRES = 950;
  var LOW_PRES = 900;

  if (p_pressure < LOW_PRES) {
    return RED;
  } else if (p_pressure < MEDIUM_PRES) {
    return ORANGE;
  } else if (p_pressure < HIGH_PRES) {
    return BLUE;
  }
  return GREEN;
}

// method called to retrieve the css class for the given humidity
function getColorFromHumidity(p_humidity) {
  // define temperature range
  var HIGH_HUM = 75;
  var MEDIUM_HUM = 50;
  var LOW_HUM = 25;

  if (p_humidity < LOW_HUM) {
    return BLUE;
  } else if (p_humidity < MEDIUM_HUM) {
    return GREEN;
  } else if (p_humidity < HIGH_HUM) {
    return ORANGE;
  }
  return RED;
}

function dataObserve(deviceLocation, observationStatus) {
  if (observationStatus) {
    // temperature
    var temperature = new EventSource("http://localhost:8080/TP5_CoapProxy/rest/weatherstation/" + deviceLocation + "/temperature");
    temperature.onmessage = function(event) {
      console.log("Temperature from " + deviceLocation + " " + event.data);
      var id = '#' + deviceLocation + '_' + TEMP_SENSOR_ID;
      $(id).html(event.data);
    };
    temperature.onerror = function(event) {
      console.log("Error temperature from " + deviceLocation + " " + event.data);
    };

    // humidity
    var humidity = new EventSource("http://localhost:8080/TP5_CoapProxy/rest/weatherstation/" + deviceLocation + "/humidity");
    humidity.onmessage = function(event) {
      console.log("Humidity from " + deviceLocation + " " + event.data);
      var id = '#' + deviceLocation + '_' + HUM_SENSOR_ID;
      $(id).html(event.data);
    };
    humidity.onerror = function(event) {
      console.log("Error humidity from " + deviceLocation + " " + event.data);
    };

    // pressure
    var pressure = new EventSource("http://localhost:8080/TP5_CoapProxy/rest/weatherstation/" + deviceLocation + "/pressure");
    pressure.onmessage = function(event) {
      console.log("Pressure from " + deviceLocation + " " + event.data);
      var id = '#' + deviceLocation + '_' + PRES_SENSOR_ID;
      $(id).html(event.data);
    };
    pressure.onerror = function(event) {
      console.log("Error pressure from " + deviceLocation + " " + event.data);
    };
  }
}

function stateObserve(deviceLocation, observationStatus) {
  if (observationStatus) {
    // state
    var state = new EventSource("http://localhost:8080/TP5_CoapProxy/rest/weatherstation/" + deviceLocation + "/state");
    state.onmessage = function(event) {
      console.log("State from " + deviceLocation + " " + event.data);
    };
    state.onerror = function(event) {
      console.log("State from " + deviceLocation + " " + event.data);
    };
  }
}

// ---------- Initial function -------------------------------------------------
// method called after the document is entirely loaded
$(document).ready(function() {
  console.log("LOG: ready() called");

  // clear the timer map
  timerMap.clear();

  // initialization
  // hide all submenus displayed on detailled preview screen
  $("#panel_station").hide();
  $("#panel_preview").hide();
  $("#panel_history").hide();
  $("#panel_settings").hide();
  $("#dropdown_station").hide();
  $("#dropdown_history").hide();
  $("#dropdown_settings").hide();

  // change the title of the page
  $('#page_title').html('<h1>Preview station(s)</h1>');

  // get all the weather station data
  getWeatherStationData();
});

// ---------- Others functions -------------------------------------------------
// method called to retrieve the location from "title_xxxxx" or "link_xxxxx" strings
function getDeviceLocationFromID(p_deviceLocation) {
  var l_deviceLocation = "";
  if (p_deviceLocation.indexOf("title_") != -1) {
    l_deviceLocation = p_deviceLocation.replace("title_", "");
  } else if (p_deviceLocation.indexOf("link_") != -1) {
    l_deviceLocation = p_deviceLocation.replace("link_", "");
  }
  // dispay the preview station screen
  updateDetailledPreviewWeatherStation(l_deviceLocation);
}

// method called for getting all data for all weather stations
function getWeatherStationData() {
  console.log("LOG: getWeatherStationData() called");

  // perform an asynchronous HTTP Ajax request
  $.ajax( {
    // using the GET method
    type: 'GET',
    url: BASE_URL,
    dataType: "json",
    // accepts: {
    //   text: "application/json"
    // },
    // if the request is successful, update weather station data table
    success:function(data) {
      console.log("LOG: getWeatherStationData() called : SUCCESS");
      // iterate over all weather stations
      // the device location is the key of the map stored in data.mWeatherStationMap
      // the content of the elements of the map is the weather station content itself
      for (var deviceLocation in data.mWeatherStationMap) {
        // get a reference to the device
        var l_device = data.mWeatherStationMap[deviceLocation];

        // update the display for this device
        updateDisplay(deviceLocation, l_device);
        stateObserve(deviceLocation, TRUE)
        dataObserve(deviceLocation, TRUE)
      }
    },
    // Code to run if the request fails; the raw request and status codes are passed to the function
    error:function(xhr, status, errorThrown) {
      console.log("LOG: getWeatherStationData() called : ERROR " + errorThrown);
      updateDisplayOnError('#preview_station_container');
    }
  });
}

// method called to display a generic error message at the given id position
function updateDisplayOnError(p_htmlId) {
  var output = "";
  output += '<div class="col-xs-12 col-sm-12">';
  output += ' <div class="alert alert-danger" role="alert">';
  output += '   <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span>';
  output += '   <span class="sr-only">Error:</span>';
  output += '     An error has occured. Try later.';
  output += ' </div>';
  output += '</div>';
  $(p_htmlId).html(output);
}

// method called for updating the display for a given weather station
function updateDisplay(p_deviceLocation, p_device) {
  // update the display
  // implementation is depending on the design of your application
  var output = $('#preview_station_container').html();
  output += '<div id="container_' + p_deviceLocation + '">';

  output += '<div class="row">';
  output += ' <div class="col-xs-6 col-sm-6">';
  output += '   <h2><a id="title_' + p_deviceLocation + '" href="#" onClick="getDeviceLocationFromID(this.id)">' + p_deviceLocation.toUpperCase() + '</a></h2>';
  output += ' </div>';

  output += ' <div class="col-xs-6 col-sm-6 text-right">';
  if (p_device.mState) {
    output += '   <h2><span class="label label-success">ON</span></h2>';
  } else {
    output += '   <h2><span class="label label-danger">OFF</span></h2>';
  }
  output += ' </div>';
  output += '</div>';
  output += '<hr>';
  output += '<div class="row">';

  for (var deviceSensor in p_device.mSensorList) {
    // get the reference of the sensor
    var sensor = p_device.mSensorList[deviceSensor];

    output += ' <div class="col-xs-6 col-sm-4">';
    if (sensor.mSensorId == TEMP_SENSOR_ID) {
      output += '   <div class="circle '+ getColorFromTemperature(sensor.mSensorValue) + '">';
    } else if (sensor.mSensorId == HUM_SENSOR_ID) {
      output += '   <div class="circle '+ getColorFromHumidity(sensor.mSensorValue) + '">';
    } else if (sensor.mSensorId == PRES_SENSOR_ID) {
      output += '   <div class="circle '+ getColorFromPressure(sensor.mSensorValue) + '">';
    } else {
      output += '   <div class="circle">';
    }
    output += '     <div class="circle-inner">';

    if (sensor.mSensorId == TEMP_SENSOR_ID) {
      var id = p_deviceLocation + '_temperature';
      output += '       <div id="' + id + '" class="circle-text">';
    } else if (sensor.mSensorId == HUM_SENSOR_ID) {
      var id = p_deviceLocation + '_humidity';
      output += '       <div id="' + id + '" class="circle-text">';
    } else if (sensor.mSensorId == PRES_SENSOR_ID) {
      var id = p_deviceLocation + '_pressure';
      output += '       <div id="' + id + '" class="circle-text">';
    } else {
      output += '       <div class="circle-text">';
    }

    output += '         ' + Math.round(sensor.mSensorValue) + '' + sensor.mSensorUnit ;
    output += '       </div>';
    output += '     </div>';
    output += '   </div>';
    output += '   <p class="circle-title">' + sensor.mSensorId + '</p>';
    output += ' </div>';
  }

  output += '</div>';
  output += '<div class="row text-right">';
  output += ' <div class="col-xs-12 col-sm-12">';
  output += '   <p><a id="link_' + p_deviceLocation + '" href="#" onClick="getDeviceLocationFromID(this.id)">History / Settings</a></p>';
  output += ' </div>';
  output += '</div>';

  output += '</div>';

  // if the weather station is on
  // add a timer for updating sensor data in timeOutValue secs
  if (p_device.mState) {
    // add the location to the timer map (clé = index)
    timerMap.set(p_deviceLocation, p_deviceLocation);
  }

  $('#preview_station_container').html(output);

  // update the lateral menu
  output = $('#lateral_menu_list_station').html();
  // add a link to the station in the menu
  output += '<li class="list-group-item"><a href="#title_' + p_deviceLocation + '">' + p_deviceLocation.toUpperCase() + '</a></li>';
  $('#lateral_menu_list_station').html(output);

  // update the dropdown menu
  output = $('#dropdown_menu_stations_container').html();
  // add a link to the station in the menu
  output += '<li><a href="#title_' + p_deviceLocation + '">' + p_deviceLocation.toUpperCase() + '</a></li>';
  $('#dropdown_menu_stations_container').html(output);
}

// method called to display all information about a specific station
function updateDetailledPreviewWeatherStation(p_deviceLocation) {
  // change the page title
  $('#page_title').html('<h1>Detailled preview</h1>');

  // clear the timer map
  timerMap.clear();

  // enable right navigation
  $("#panel_station").show();
  $("#panel_preview").show();
  $("#panel_history").show();
  $("#panel_settings").show();

  // enable top navigation
  $("#dropdown_station").show();
  $("#dropdown_history").show();
  $("#dropdown_settings").show();

  // disable navigation elements from the home page
  $("#panel_list_stations").hide();
  $('#dropdown_list_stations').hide();

  // perform an asynchronous HTTP Ajax request
  $.ajax( {
    // using the GET method
    type: 'GET',
    async: true,
    url: BASE_URL + p_deviceLocation + '/',
    dataType: "json",
    accepts: {
      text: "application/json"
    },
    // if the request is successful, update weather station data table
    success:function(data) {
      var output = "";
      output += '<div id="container_' + p_deviceLocation + '">';

      // preview section
      output += '<div class="row">';
      output += ' <div class="col-xs-6 col-sm-6">';
      output += '   <h2 id="title_preview">' + p_deviceLocation.toUpperCase() + '</h2>';
      output += ' </div>';
      output += ' <div id="station_state" class="col-xs-6 col-sm-6 text-right">';

      if (data.mState) {
        output += '   <h2><span class="label label-success">ON</span></h2>';
      } else {
        output += '   <h2><span class="label label-danger">OFF</span></h2>';
      }

      output += ' </div>';
      output += '</div>';
      output += '</div>';
      output += '<hr>';

      output += '<div class="row">';
      for (var deviceSensor in data.mSensorList) {
        var sensor = data.mSensorList[deviceSensor];
        output += ' <div class="col-xs-6 col-sm-4">';

        if (sensor.mSensorId == TEMP_SENSOR_ID) {
          output += '   <div class="circle '+ getColorFromTemperature(sensor.mSensorValue) + '">';
        } else if (sensor.mSensorId == HUM_SENSOR_ID) {
          output += '   <div class="circle '+ getColorFromHumidity(sensor.mSensorValue) + '">';
        } else if (sensor.mSensorId == PRES_SENSOR_ID) {
          output += '   <div class="circle '+ getColorFromPressure(sensor.mSensorValue) + '">';
        } else {
          output += '   <div class="circle">';
        }

        output += '     <div class="circle-inner">';
        output += '       <div class="circle-text">';
        output += '         ' + Math.round(sensor.mSensorValue) + '' + sensor.mSensorUnit ;
        output += '       </div>';
        output += '     </div>';
        output += '   </div>';
        output += '   <p class="circle-title">' + sensor.mSensorId + '</p>';
        output += ' </div>';
      }
      output += '</div>';

      // history section
      output += '<div class="row">';
      output += ' <div class="col-xs-12 col-sm-12">';
      output += '   <h2 id="title_history">History</h2>';
      output += ' </div>';
      output += '</div>';
      output += '<hr>';
      output += '<div class="row">';
      output += ' <div class="col-xs-12 col-sm-12">';
      output += '   <p>This functionality isn\'t implemented yet.</p>';
      output += ' </div>';
      output += '</div>';

      // settings section
      output += '<div class="row">';
      output += ' <div class="col-xs-12 col-sm-12">';
      output += '   <h2 id="title_settings">Settings</h2>';
      output += ' </div>';
      output += '</div>';
      output += '<hr>';
      output += '<div class="row">';
      output += ' <div class="col-xs-6 col-sm-6">';
      output += '   <p>Enable / disable the measurements</p>';
      output += ' </div>';
      output += ' <div class="col-xs-6 col-sm-6 text-right">';
      output += '   <div id="buttons_state" class="btn-group btn-toggle">';

      if (data.mState) {
        output += '     <button id="enabled_button" class="btn btn-xs btn-success active">ON</button>';
        output += '     <button id="disabled_button" class="btn btn-xs btn-default">OFF</button>';
      } else {
        output += '     <button id="enabled_button" class="btn btn-xs btn-default">ON</button>';
        output += '     <button id="disabled_button" class="btn btn-xs btn-danger active">OFF</button>';
      }
      // add a hidden input for storing the current state
      output += '<input type="hidden" id="hidden_state" name="hidden_state" value="' + data.mState + '">';

      output += '   </div>';
      output += ' </div>';
      output += '</div>';

      $('#preview_station_container').html(output);

      /*
      if (data.mState) {
      // if enabled, add it to the timer map
      timerMap.set(p_deviceLocation, p_deviceLocation);
    }
    */

    $('#buttons_state button').click(function() {
      switchWeatherStationState($('#hidden_state').val(), p_deviceLocation);
    });
  },
  // Code to run if the request fails; the raw request and status codes are passed to the function
  error:function(xhr, status, errorThrown) {
    console.log("LOG: updateDetailledPreviewWeatherStation() called : ERROR " + errorThrown);
    updateDisplayOnError('#preview_station_container');
  }
});
}

// method called for switching the state of a given weather station
function switchWeatherStationState(p_state, p_deviceLocation) {
  // set parameter to lower case
  p_state = p_state.toLowerCase();
  var l_state = "";

  // toggle the current state
  if (p_state == TRUE) {
    l_state = FALSE;
  } else if (p_state == FALSE) {
    l_state = TRUE;
  } else {
    // if p_state != true/false --> exit
    console.log("LOG: switchWeatherStationState() : ERROR : state: " + p_state + " location: " + p_deviceLocation);
    return;
  }

  // construct the data to be sent in the PUT http request
  var l_data = {
    mState: l_state
  };

  // perform an asynchronous HTTP Ajax PUT request
  $.ajax( {
    // using the GET method
    type: 'PUT',
    async: true, // asynchronous request
    url: BASE_URL + p_deviceLocation + '/state', // construct the requested URI
    contentType: 'application/json', // set content type to JSON
    data: JSON.stringify(l_data), // transform the JS oject into JSON format
    // if the request is successful, update weather station data table
    success:function(data) {
      console.log("LOG: switchWeatherStationState() called : SUCCESS");

      // edit the toggle button and the indicator label (ON/OFF)
      var output = "";
      if (l_state == TRUE) {
        // add/remove the css classes (button in green/red)
        $('#enabled_button').addClass('active');
        $('#enabled_button').addClass('btn-success');
        $('#disabled_button').removeClass('btn-default');

        $('#disabled_button').removeClass('active');
        $('#disabled_button').removeClass('btn-danger');
        $('#disabled_button').addClass('btn-default');

        // add the location from the timer map
        // timerMap.set(p_deviceLocation, p_deviceLocation);
        // on homepage reload, the timer map is initizialed with the enabled station

        $('#station_state').html('<h2><span class="label label-success">ON</span></h2>');
      } else {
        $('#disabled_button').addClass('active');
        $('#disabled_button').addClass('btn-danger');
        $('#disabled_button').removeClass('btn-default');

        $('#enabled_button').removeClass('active');
        $('#enabled_button').removeClass('btn-success');
        $('#enabled_button').addClass('btn-default');

        // remove the location from the timer map
        timerMap.delete(p_deviceLocation);

        $('#station_state').html('<h2><span class="label label-danger">OFF</span></h2>');
      }
      $('#hidden_state').val(l_state);
    },
    // Code to run if the request fails; the raw request and status codes are passed to the function
    error:function(xhr, status, errorThrown) {
      console.log("LOG: switchWeatherStationState() called : ERROR " + errorThrown);
      updateDisplayOnError('#preview_station_container');
    }
  });
}

// method called every x seconds
function onTimerInterval() {
  if (timerMap.size > 0) {
    // iterate the timer map and refresh every station
    for (var l_deviceLocation of timerMap.values()) {
      console.log("LOG: onTimerInterval() called : timer map entry: " + l_deviceLocation);
      updateDisplayOnTimerInterval(l_deviceLocation);
    }
  } else {
    console.log("LOG: onTimerInterval() called : EMPTY TIMER MAP");
  }
}

// method called for updating the information for a specific station (called by the onTimerInterval() function)
function updateDisplayOnTimerInterval(p_deviceLocation) {
  $.ajax( {
    // using the GET method
    type: 'GET',
    async: true, // asynchronous request
    url: BASE_URL + p_deviceLocation + '/', // construct the requested URI
    dataType: "json",
    accepts: {
      text: "application/json"
    },
    // if the request is successful, update weather station data table
    success:function(data) {
      console.log("LOG: updateDisplayOnTimerInterval() : SUCCESS : station: " + p_deviceLocation);
      var output = "";
      output += '<div class="row">';
      output += ' <div class="col-xs-6 col-sm-6">';
      output += '   <h2><a id="title_' + p_deviceLocation + '" href="#" onClick="getDeviceLocationFromID(this.id)">' + p_deviceLocation.toUpperCase() + '</a></h2>';
      output += ' </div>';

      output += ' <div class="col-xs-6 col-sm-6 text-right">';
      if (data.mState) {
        output += '   <h2><span class="label label-success">ON</span></h2>';
      } else {
        output += '   <h2><span class="label label-danger">OFF</span></h2>';
      }
      output += ' </div>';
      output += '</div>';
      output += '<hr>';
      output += '<div class="row">';

      for (var deviceSensor in data.mSensorListList) {
        // get the reference of the sensor
        var sensor = data.mSensorList[deviceSensor];

        output += ' <div class="col-xs-6 col-sm-4">';
        if (sensor.mSensorId == TEMP_SENSOR_ID) {
          output += '   <div class="circle '+ getColorFromTemperature(sensor.mSensorValue) + '">';
        } else if (sensor.mSensorId == HUM_SENSOR_ID) {
          output += '   <div class="circle '+ getColorFromHumidity(sensor.mSensorValue) + '">';
        } else if (sensor.mSensorId == PRES_SENSOR_ID) {
          output += '   <div class="circle '+ getColorFromPressure(sensor.mSensorValue) + '">';
        } else {
          output += '   <div class="circle">';
        }

        output += '     <div class="circle-inner">';
        output += '       <div class="circle-text">';
        output += '         ' + Math.round(sensor.mSensorValue) + '' + sensor.mSensorUnit ;
        output += '       </div>';
        output += '     </div>';
        output += '   </div>';
        output += '   <p class="circle-title">' + sensor.mSensorId + '</p>';
        output += ' </div>';
      }

      output += '</div>';
      output += '<div class="row text-right">';
      output += ' <div class="col-xs-12 col-sm-12">';
      output += '   <p><a id="link_' + p_deviceLocation + '" href="#" onClick="getDeviceLocationFromID(this.id)">History / Settings</a></p>';
      output += ' </div>';
      output += '</div>';

      var id = '#container_' + p_deviceLocation;
      $(id).html(output);
    },
    // Code to run if the request fails; the raw request and status codes are passed to the function
    error:function(xhr, status, errorThrown) {
      console.log("LOG: updateDisplayOnTimerInterval() called : ERROR " + errorThrown);
      updateDisplayOnError('#preview_station_container');
    }
  });
}
