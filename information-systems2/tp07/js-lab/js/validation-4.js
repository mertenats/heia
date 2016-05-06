//===========
// Phase 4
//===========

var FLD_BG_ERR = '#fdc';
var FLD_BG_OK = 'white';

function checkInputField(fieldName) {
    var msg = "Please fill in the ";
    var errId  = "-errmsg";
    var field = document.getElementById(fieldName);

    if (field.value.trim() == "") {
        field.style.backgroundColor = FLD_BG_ERR;
        document.getElementById(fieldName.concat(errId)).innerHTML = msg.concat(fieldName);
	return false;
    }
    else {
        field.style.backgroundColor = FLD_BG_OK;
        document.getElementById(fieldName.concat(errId)).innerHTML = "";
        return true;
    }
}

function validateFormInput(evt) {
    if (checkInputField("firstname") & checkInputField("lastname") & checkInputField("address") & checkInputField("zip"))
        console.log("The form was submitted.")
    else
        event.preventDefault();
}

window.addEventListener('load', function() {
    document.getElementById('addressform').addEventListener('submit', validateFormInput);
});
