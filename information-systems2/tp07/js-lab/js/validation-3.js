//===========
// Phase 3
//===========

var FLD_BG_ERR = '#fdc';
var FLD_BG_OK = 'white';

function checkInputField(fieldName) {
    if (document.getElementById(fieldName).value.trim() == "") {
        console.error("Please fill in the field firstname.");
	return false;
    }
    return true;
}

function validateFormInput(evt) {
    if (checkInputField("firstname"))
        console.log("The form was submitted.")
    else
        event.preventDefault();
}

window.addEventListener('load', function() {
    document.getElementById('addressform').addEventListener('submit', validateFormInput);
});
