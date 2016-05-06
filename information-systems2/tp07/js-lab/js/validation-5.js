//===========
// Phase 5
//===========

var FLD_BG_ERR = '#fdc';
var FLD_BG_OK = 'white';

var errId  = "-errmsg";

function checkInputField(fieldName) {
    var msg = "Please fill in the ";
    var field = document.getElementById(fieldName);

    if (field.value.trim() == "") {
        field.style.backgroundColor = FLD_BG_ERR;
        document.getElementById(fieldName+errId).innerHTML = msg + fieldName;
	return false;
    }
    else {
        field.style.backgroundColor = FLD_BG_OK;
        document.getElementById(fieldName+errId).innerHTML = "";
        return true;
    }
}

function checkZipFormat() {
  // Gets the zip code element
  var fieldName = "zip";
  var field = document.getElementById(fieldName);
  var zipCode = field.value.trim();
  console.log(zipCode);

  if (zipCode != "") {
    if (zipCode.match(/^\d{4}$/)) {
      console.log("Zip code: 4 digits");
      field.style.backgroundColor = FLD_BG_OK;
      document.getElementById(fieldName+errId).innerHTML = "";
      return true;
    } else {
      field.style.backgroundColor = FLD_BG_ERR;
      document.getElementById(fieldName+errId).innerHTML = "Zip code: not 4 digits";
      return false;
    }
  }
}

function validateFormInput(evt) {
    if (checkInputField("firstname") & checkInputField("lastname") & checkInputField("address") & checkInputField("zip") & checkZipFormat())
        console.log("The form was submitted.")
    else
        event.preventDefault();
}

window.addEventListener('load', function() {
    document.getElementById('addressform').addEventListener('submit', validateFormInput);
});


