////////////////////////////////////////////////////////////////////////////////
//                                                                            //
// DEMO - 8 ZONES OF 1 x 1 meter                                              //
// The zones were previously defined with the Quuppa SPD                      //
// Zones: Zone001-8                                                           //
// Samuel M. - 04.07.2016                                                     //
//                                                                            //
////////////////////////////////////////////////////////////////////////////////

// server informations
var SERVER_IP = "http://160.98.112.244:8080/";
var SERVER_NAME_QPE = "qpe/";

// tag's ID and update rate
var TAG_ID = "b4994c8bc320";
var UPDATE_RATE = 200; // 200 ms / 5 Hz / Quppa's API limitation

// zone and Z coordinate informations
var ZONE_BY_DEFAULT = "Zone000";
var lastVisitedZone = ZONE_BY_DEFAULT; // zone by default, not existing
var Z_COORDINATE_OFFSET = 0.30;
var lastZCoordinate = -1;

// this function is launched ather the entire HTML document is completly loaded
$(document).ready(function() {
  setInterval(getTagZone, UPDATE_RATE);
});

// this function returns a color value from the Z coordinate, given in paramter
function getColor(z) {
  if (z < 1.2) {
    return "green";
  } else if (z < 1.8) {
    return "yellow";
  } else {
    return "red";
  }
}

// this function changes the background color of the visited zone
function getTagZone() {
  $.ajax( {
    type: 'GET',
    url: SERVER_IP + SERVER_NAME_QPE + 'getTagPosition?version=2&tag=' + TAG_ID,
    dataType: "json",
    // if the request is successful, update weather station data table
    success:function(data) {
      // iterate over all tags
      for (var tag in data.tags) {
        // get the tag's reference
        var l_tag = data.tags[tag];

        // visited zone
        var newVisitedZone = l_tag.zones[0];
        // test if the user has moved out from the zones
        if (newVisitedZone === undefined) {
          var id = '#' + lastVisitedZone;
          $(id).css({ fill: "white" });
          lastVisitedZone = ZONE_BY_DEFAULT;
          console.log("INFO: the user isn't in a registered zone");
        } else {
          // get the zone id
          newVisitedZone = l_tag.zones[0].id;
          if (lastVisitedZone == newVisitedZone) {
            // test if the user has moved the tag up/down in the same zone
            var newZCoordinate = parseFloat(l_tag.smoothedPosition[2]);

            // init z value (used only once)
            if (lastZCoordinate == -1) {
              lastZCoordinate = newZCoordinate;
            }

            // compute an offset for the Z coordinate
            var minZ = parseFloat(lastZCoordinate - Z_COORDINATE_OFFSET);
            var maxZ = parseFloat(lastZCoordinate + Z_COORDINATE_OFFSET);

            // test if the current z coordinate is out of the range
            // if yes, compute the background color
            if (!(minZ < newZCoordinate && maxZ > newZCoordinate)) {
              var id = '#' + lastVisitedZone;
              $(id).css({ fill: getColor(newZCoordinate) });

              console.log("INFO: user is moving from " + lastZCoordinate + " to " + newZCoordinate);
              lastZCoordinate = newZCoordinate;
            }
          } else {
            // test if the user has moved to another zone
            var id = '#' + lastVisitedZone;
            console.log("INFO: user is moving from " + lastVisitedZone + " to " + newVisitedZone);

            // change the color of the previous visited zone to white
            $(id).css({ fill: "white" });

            // change the new visited zone to red
            id = '#' + newVisitedZone;
            $(id).css({ fill: getColor(l_tag.smoothedPosition[2]) });

            lastVisitedZone = newVisitedZone;
          }
        }
      }
    },
    // Code to run if the request fails; the raw request and status codes are passed to the function
    error:function(xhr, status, errorThrown) {
      console.log("ERROR: get tag zone: " + errorThrown);
    }
  });
}
