<?php
  // SINF2 2015
require('header.inc.php');
require('menu.inc.php');
?>

<div id="main">
  <h2>List of DB entries</h2>

  <?php
    require_once('db.inc.php');

    // Retrieves the data from the DB
    $sql = "SELECT * FROM employee";
    $result = $conn->query($sql);

    if ($result->num_rows > 0) {
      // Creates a table with a border of 2 and 4 columns
      echo
        "<table border='2'>".
          "<th>SSN</th>".
          "<th>Name</th>".
          "<th>Firstname</th>".
          "<th>E-mail</th>".
          "<th>Del</th>";

      // Enters the data of each row 
      // Form: http://www.w3schools.com/php/php_forms.asp
      while($row = $result->fetch_assoc()) {
        echo "<tr>".
          "<td>{$row['ssn']}</td>".
          "<td>{$row['lastname']}</td>".
          "<td>{$row['firstname']}</td>".
          "<td>{$row['email']}</td>".
          "<td>".
            "<form action=remove_entry.php method=post>".
              "<input type=image src=img/delete.png />".
              "<input type=hidden name=ssn_id value={$row['ssn']} />".
            "</form>".
          "</td>".
          "</tr>";
      }
      // Closes the table
      echo "</table>";
    } else {
      echo "0 results stored in the database";
    }
    $conn->close();
  ?>
</div>

<?php
  require('footer.inc.php');
?>