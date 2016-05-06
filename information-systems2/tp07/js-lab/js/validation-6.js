//===========
// Phase 5
//===========

var FLD_BG_ERR = '#fdc';
var FLD_BG_OK = 'white';

window.addEventListener('load', function() {
  var submitForm = document.getElementById('addressform');
  submitForm.addEventListener('submit', checkZipFormat);
});

function isEmpty(str) {
  return (!str || 0 === str.length);
}

function checkZipFormat(evt) {
  // Gets the zip code element
  var zipCode = document.getElementById('zip').value.trim();
  console.log(zipCode);

  if (!isEmpty(zipCode)) {
    if (zipCode.match(/^\d{4}$/)) {
      console.log("Zip code: 4 digits");


      checkZipValue(zipCode);


      return true;
    } else {
      console.log("Zip code: not 4 digits");


      return false;
    }
  }
}

function checkZipValue(zipCode) {
  var xhttp = new XMLHttpRequest();
  var url = "ZipCodeLookup?zipcode=";
  url += zipCode;
  console.log(url);

  xhttp.open("GET", url, false);
  xhttp.send();
  handleZipLookupResponse(xhttp.responseText);

}

function handleZipLookupResponse(data) {
  console.log(data);
}


