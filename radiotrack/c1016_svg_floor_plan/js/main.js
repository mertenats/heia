var SERVER_IP = "http://160.98.31.182:8080/";
var SERVER_NAME_QPE = "qpe/";
var SERVER_NAME_RADIOTRACK = "radiotrack/";

// C1016 physical dimensions
var C1016_WIDTH = 9.35;
var C1016_HEIGHT = 10.64;

// SVG scaling factors and offsets
var SVG_SCALING_FACTOR_WIDTH = 1;
var SVG_SCALING_FACTOR_HEIGHT = 1;
var SVG_OFFSET_WIDTH = 1;
var SVG_OFFSET_HEIGHT = 1;
var SVG_MARGIN_WIDTH = 47.834646;
var SVG_MARGIN_HEIGHT = 54.128352;

// SVG cursor properties
var SVG_CURSOR_RADIUS = 5;
var SVG_CURSOR_FILL_COLOR = "red";

var m_lastVisitedZoneHtmlId = null;
var m_contentZone = null;

var m_zoneIdToHtmlId = new Map();

var BASE_URL = "/Users/mertenats/Documents/Git/RadioTrack/c1016_svg_floor_plan/";

$(document).ready(function() {
  init();

  getTags();
  //getZoneInfomration();

  //drawCircle(-4.675, 5.32);
  //drawCircle(-4.675, -5.32);
  //drawCircle(4.675, 5.32);
  drawCircle(4.675, -5.32);
});

function init() {
  console.log("INFO: SVG width: " + document.getElementById("floorPlan").getAttribute("width"));
  console.log("INFO: SVG height: " + document.getElementById("floorPlan").getAttribute("height"));

  // compute the scaling factor between the svg width/height
  // and the real physical dimensions
  SVG_SCALING_FACTOR_WIDTH = document.getElementById("floorPlan").getAttribute("width") / C1016_WIDTH;
  SVG_SCALING_FACTOR_HEIGHT = document.getElementById("floorPlan").getAttribute("height") / C1016_HEIGHT;
  console.log("INFO: Scalling factor width: " + SVG_SCALING_FACTOR_WIDTH);
  console.log("INFO: Scalling factor height: " + SVG_SCALING_FACTOR_HEIGHT);

  // Quuppa's origine point is placed at the center of the image
  // compute the x and y offset
  SVG_OFFSET_WIDTH = document.getElementById("floorPlan").getAttribute("width") / 2;
  SVG_OFFSET_HEIGHT = document.getElementById("floorPlan").getAttribute("height") / 2;
  console.log("INFO: SVG offset width: " + SVG_OFFSET_WIDTH);
  console.log("INFO: SVG offset height: " + SVG_OFFSET_HEIGHT);

  //SVG_MARGIN_WIDTH = document.getElementById("floorPlan").getAttribute("x");
  //SVG_MARGIN_HEIGHT = document.getElementById("floorPlan").getAttribute("y");
  console.log("INFO: SVG margin width: " + SVG_MARGIN_WIDTH);
  console.log("INFO: SVG margin height: " + SVG_MARGIN_HEIGHT);

  // add a listener to each interactive zone (onMouseOver)
  var interactiveZones = document.getElementsByClassName("interactive");
  for (var i = 0; i < interactiveZones.length; i++) {
    interactiveZones[i].addEventListener('mouseover', showZoneInformation, false);
  }
}

function getTags() {
  $.ajax( {
    type: 'GET',
    url: SERVER_IP + SERVER_NAME_QPE + 'getTagInfo?version=2',
    dataType: "json",
    crossDomain: true,
    // if the request is successful, update weather station data table
    success:function(data) {
      console.log("INFO: " + JSON.stringify(data));

      //console.log("INFO:  get tags: " + data);
    },
    // Code to run if the request fails; the raw request and status codes are passed to the function
    error:function(xhr, status, errorThrown) {
      console.log("ERROR: get tags: " + errorThrown);
    }
  });
}

function getZoneInfomration() {
  $.ajax( {
    async: false,
    url: BASE_URL + 'data/zones.json',
    dataType: 'json',
    success:function(data) {
      contentZone = data;
      console.log("INFO: " + JSON.stringify(data));
    },
    error:function(xhr, status, errorThrown) {
      console.log("ERROR: " + errorThrown);
    }
  });
}

function showZoneInformation() {
  console.log("INFO: HTML zone ID: " + this.id);
  if (this.id != m_lastVisitedZoneHtmlId) {
    for (var i = 0; i < m_contentZone.zones.length; i++) {
      if (m_contentZone.zones[i].htmlId === this.id) {
        console.log("INFO: Zone name: " + m_contentZone.zones[i].name);
        console.log("INFO: Zone description: " + m_contentZone.zones[i].description);

        lastVisitedZoneHtmlId = this.id;
        break;
      }
    }
  }
}

function drawCircle(x, y) {
  // verify if the x,y coordinates are insidde the floor plan
  if ((x < -(C1016_WIDTH / 2)) || (x > (C1016_WIDTH / 2)) || (y > (C1016_HEIGHT / 2)) || (y < -(C1016_HEIGHT / 2))) {
    console.log("ERROR: The coordinates x, y are not inside the floor plan. Coordinates: (" + x + ", " + y + "), Floor plan: (+/- " + C1016_WIDTH / 2 + " [m], +/- " + C1016_HEIGHT / 2 + " [m])");
    return;
  }

  // compute the x,y coordinates according to the SVG floor plan dimension
  var cx, cy;
  if (x < 0 && y > 0) {
    // top left
    cx = (C1016_WIDTH / 2 - Math.abs(x)) * SVG_SCALING_FACTOR_WIDTH;
    cy = (C1016_HEIGHT / 2 - y) * SVG_SCALING_FACTOR_HEIGHT;
  } else if (x < 0 && y < 0) {
    // bottom left
    cx = (C1016_WIDTH / 2 - Math.abs(x)) * SVG_SCALING_FACTOR_WIDTH;
    cy = Math.abs(y) * SVG_SCALING_FACTOR_HEIGHT + SVG_OFFSET_HEIGHT;
  } else if (x >= 0 && y >= 0) {
    // top right
    cx = x * SVG_SCALING_FACTOR_WIDTH + SVG_OFFSET_WIDTH;
    cy = (C1016_HEIGHT / 2 - y) * SVG_SCALING_FACTOR_HEIGHT;
  } else if (x >= 0 && y <= 0) {
    // bottom right
    cx = x * SVG_SCALING_FACTOR_WIDTH + SVG_OFFSET_WIDTH;
    cy = Math.abs(y) * SVG_SCALING_FACTOR_HEIGHT + SVG_OFFSET_HEIGHT;
  } else if (x == 0 && y == 0) {
    cx = SVG_OFFSET_WIDTH;
    cy = SVG_OFFSET_HEIGHT;
  }

  // adjuste the coordinates with the inner floor plan margins
  cx += SVG_MARGIN_WIDTH;
  cy += SVG_MARGIN_HEIGHT;

  console.log("INFO: Cursor x: " + cx);
  console.log("INFO: Cursor y: " + cy);

  // get the cursor reference and set its radius, color and coordinates
  var cursor = document.getElementById("cursor");
  cursor.setAttribute("r", SVG_CURSOR_RADIUS);
  cursor.setAttribute("fill", SVG_CURSOR_FILL_COLOR);
  cursor.setAttribute("cx", cx);
  cursor.setAttribute("cy", cy);
}
