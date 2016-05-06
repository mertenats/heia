//===========
// Phase 2
//===========

var FLD_BG_ERR = '#fdc';
var FLD_BG_OK = 'white';

function validateFormInput(evt) {
  event.preventDefault();
  console.log("The form was submitted.");
}

window.addEventListener('load', function() {
  console.log("Add listener after the page has been loaded.");
  var submitForm = document.getElementById('addressform');
  submitForm.addEventListener('submit', validateFormInput);
});



