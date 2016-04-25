<html>
  <head>
    <title>Timeslot приложение. Подтверждение учетной записи.</title>
    <meta name="layout" content="main" />
  </head>
  <body>
    <div class="grid_3">&nbsp;</div>
    <div class="grid_6 padtop">
      <h3 style="float:right">Подтверждение учетной записи</h3>
      <div class="clear"></div>
      <div class="info-box" style="${!confirm_user?'display:none':''}">
        <span class="icon icon-info-sign icon-3x"></span>
        <ul id="restsuccess">
          <li>Вы успешно подтвердили свой email.</li>
        </ul>
      </div>
      <div class="error-box" style="${confirm_user?'display:none':''}">
        <span class="icon icon-warning-sign icon-3x"></span>
        <ul id="confirmErrorlist">
          <li>${message(code:"error.confirm.message")}</li>
        </ul>
      </div>
    </div>
  </body>
</html>