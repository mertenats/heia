  <?php
    // http://www.w3schools.com/php/php_mysql_select.asp
    $servername = "localhost:8889";
    $username = "root";
    $password = "root";
    $dbname = "phplab";

    // Creates a connection to the database
    $conn = new mysqli($servername, $username, $password, $dbname);

    // Checks the connection
    if ($conn->connect_error) {
      die("Connection failed: " . $conn->connect_error);
    }
  ?>