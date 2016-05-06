<?php
  // Start the session
  // http://www.w3schools.com/php/php_sessions.asp
  session_start();
  // Creates a session variable to store the number of entries during the session
  if (!isset($_SESSION['count_entry'])) 
    $_SESSION['count_entry'] = 0;
?>

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width; initial-scale=1.0;">
  <link rel="stylesheet" href="css/sinf2_php.css">
  <title>SINF2 PHP Lab</title>
</head>

<body>
  <div id="header">
    <img src="img/empl_db.png">Employee Database
  </div>