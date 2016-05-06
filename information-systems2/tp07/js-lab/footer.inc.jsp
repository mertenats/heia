  <!-- FOOTER -->
  <div id="footer">
     SINF2 JavaScript Lab / &copy; 2016 by R. Scheurer (HEIA-FR)
  </div>

<script> // add class "selected" to active menu entry
  var phase = document.getElementsByTagName('h2')[0].textContent.match(/Phase (\S+)/)[1];
  if (phase == 'Extensions') phase = 'ext';
  var selector = 'a[href="address-'+phase+'.jsp"]';
  document.querySelector(selector).className = 'selected';
</script>

</body>
</html>