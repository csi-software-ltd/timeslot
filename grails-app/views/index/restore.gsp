<html>
  <head>
    <title>Timeslot приложение. Восстановление пароля.</title>
    <meta name="layout" content="main" />
    <g:javascript>
      function processResponse(e){
        $("restsuccess").up('div').hide();
        $("restoreErrorlist").up('div').hide();
        var sErrorMsg = '';
        ['rest_login'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Email"])}</li>'; $('rest_login').addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Email"])}</li>'; $('rest_login').addClassName('red'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.not.exist.message",args:["Пользователя", "Email"])}</li>'; $('rest_login').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("restoreErrorlist").innerHTML=sErrorMsg;
          $("restoreErrorlist").up('div').show();
        } else
          $("restsuccess").up('div').show();
      }
    </g:javascript>
  </head>
  <body>
    <div class="grid_3">&nbsp;</div>
    <div class="grid_6 padtop">
      <h3 style="float:right">Восстановление учетной записи</h3>
      <div class="clear"></div>
      <div class="info-box" style="display:none;margin-top:0">
        <span class="icon icon-info-sign icon-3x"></span>
        <ul id="restsuccess">
          <li>Инструкции по восстановлению пароля высланы на указанный email.</li>
        </ul>
      </div>
      <div class="error-box" style="display:none">
        <span class="icon icon-warning-sign icon-3x"></span>
        <ul id="restoreErrorlist">
        </ul>
      </div>
      <g:formRemote name="restoreForm" url="${[controller:'index',action:'rest']}" method="post" autocomplete='off' onSuccess="processResponse(e)">
        <label for="rest_login">Email:</label>
        <span class="input-prepend">
          <span class="add-on"><i class="icon-lock"></i></span>
          <input type="text" class="nopad normal" name="login" id="rest_login" placeholder="Email"/>
        </span><br/>
        <input type="submit" class="fright" value="Восстановить" />
      </g:formRemote>
    </div>
  </body>
</html>