<?php
  // SINF2 2015
  require('header.inc.php');
  require('menu.inc.php');

  if (!isset($_POST['ssn_id'])) {
    echo "The parameter ssn was not set or sent correctly";
  } else if (!is_numeric($_POST['ssn_id'])) {
    echo "The parameter ssn is not a number";
  } else {
    require_once('db.inc.php');

    // Prepares the query
    // http://www.w3schools.com/php/php_mysql_prepared_statements.asp
    $stmt = $conn->prepare("DELETE FROM employee WHERE ssn = ?");
    $stmt->bind_param("i", $_POST[ssn_id]); // "i" for integer
    $stmt->execute();

    $stmt->close();
    $conn->close();

    // Redirection to the list
    // No more working...
    // header('Location: list.php');   
?>

<div id="main">
  <h2>Action report</h2>
  <p>The employee has been successfully deleted.</p>
  <p>Go back to the <a href="list.php">list</a>.</p>
</div>

<?php
  }
  require('footer.inc.php');
?>