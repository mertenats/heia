<?php
  // SINF2 2015
  require('header.inc.php');
  require('menu.inc.php');
?>

<div id="main">
  <h2>Add entry</h2>
  <form action="add_entry.php" name="myform" method="POST">
    <table>
      <tr>
        <td><b>SSN</b><sup>*</sup> :</td>
        <td><input type="text" name="ssn" size="3" maxlength="3" /> (max. 3 digits)</td>
      </tr>
      <tr>
        <td><b>Name</b><sup>*</sup> :</td>
        <td><input type="text" name="lastname" size="20" /></td>
      </tr>
      <tr>
        <td>Firstname :</td>
        <td><input type="text" name="firstname" size="20" /></td>
      </tr>
      <tr>
        <td>E-mail :</td>
        <td><input type="text" name="email" size="20" /></td>
      </tr>
      <tr>
        <td>
          <input type="reset" value="Clear Form">
        </td>
        <td align="right" bgcolor="#ccddff">
          <input type="Submit" value="Add" name="Submit">
        </td>
      </tr>
    </table>
    <br>
    <i>
	<?php
		if (isset($_GET['error']) && $_GET['error'] == 'fields')
			echo '<span style="color:red">Please fill in the form carefully.</span>';
		else
			echo '(*) mandatory';
	?>				
	</i>
  </form>

</div>

<?php
  require('footer.inc.php');
?>