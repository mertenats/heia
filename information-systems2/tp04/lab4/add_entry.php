<?php
  // SINF2 2015
require('header.inc.php');
require('menu.inc.php');
?>

<div id="main">
	
	<h2>Action report</h2>
	
	<?php
	// define variables and set to empty values
	$ssn = $lastname = $firstname = $email = "";

	if (isset($_POST['Submit']) && $_SERVER["REQUEST_METHOD"] == 'POST') {
		if (empty($_POST['ssn']) || empty($_POST['lastname'])) {
      ?>
        <p>ERROR. Mandatory fields are missing.</p>
        <p>Go back to the <a href="form.php">form</a>.</p>
      <?php
    // Not more working
    // header('Location: form.php?error=fields');
    /*else if (!is_numeric($_POST['ssn']) || strlen($_POST['ssn']) > 3)
    header('Location: form.php?error=type');*/
    } else {
      // Increments the number of entries
      $_SESSION['count_entry']++;
      add_entry($_POST['ssn'], $_POST['lastname'], $_POST['firstname'], $_POST['email']);
    }
  }
  ?>

</div>

<?php
  require('footer.inc.php');

  function add_entry($ssn, $lastname, $firstname, $email) {
    require_once('db.inc.php');

    $sql_query = "INSERT INTO employee (ssn, lastname, firstname, email) values ('$ssn', '$lastname', '$firstname', '$email')";

    if (!$conn->query($sql_query)) 
      die('Invalid query ' . mysql_error());
    $conn->close();

    echo "<p>Entry successfully added !</p>";
    if ($_SESSION['count_entry'] == 1) {
      echo "<p>This is your first entry</p>";
    } else {
      echo "<p>This is not your first entry! You have already added " . $_SESSION['count_entry'] . " entries.</p>";
    }

    echo "<p>Go back to the <a href='form.php'>form</a> or to the <a href='list.php'>list</a>.</p>";
  }
?>
