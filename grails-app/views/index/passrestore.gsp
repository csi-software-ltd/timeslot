<html>
  <head>
    <title>Timeslot приложение. Восстановление пароля.</title>
    <meta name="layout" content="main" />
    <g:javascript>
      function processResponse(e){
        $("prestoreErrorlist").up('div').hide();
        var sErrorMsg = '';
        ['pres_password','pres_password2'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.confirm.message")}</li>'; break;
              case 2: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Пароль"])}</li>'; $('pres_password').addClassName('red'); break;
              case 3: sErrorMsg+='<li>Некорректный пароль. Пароль не менее ${Tools.getIntVal(Dynconfig.findByName('user.passwordlength')?.value,7)} знаков из больших и маленьких латинских букв и цифр</li>'; $("pres_password").addClassName('red'); break;
              case 4: sErrorMsg+='<li>Пароли не совпадают</li>'; $("pres_password").addClassName('red'); $("pres_password2").addClassName('red'); break;
              case 5: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Повтор"])}</li>'; $('pres_password2').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("prestoreErrorlist").innerHTML=sErrorMsg;
          $("prestoreErrorlist").up('div').show();
        } else
          location.assign('${createLink(controller:'personal',action:'organization')}');
      }
    </g:javascript>
  </head>
  <body>
    <div class="grid_3">&nbsp;</div>
    <div class="grid_6 padtop">
      <h3 style="float:right">Восстановление учетной записи</h3>
      <div class="clear"></div>
      <div class="error-box" style="${restore_user?'display:none':''}">
        <span class="icon icon-warning-sign icon-3x"></span>
        <ul id="prestoreErrorlist">
          <li>${message(code:"error.confirm.message")}</li>
        </ul>
      </div>
      <g:formRemote style="${!restore_user?'display:none':''}" name="prestoreForm" url="${[controller:'index',action:'passrest']}" method="post" autocomplete='off' onSuccess="processResponse(e)">
        <label for="pres_password">Пароль:</label>
        <span class="input-prepend">
          <span class="add-on"><i class="icon-key"></i></span>
          <input id="pres_password" type="password" class="nopad normal" name="password" placeholder="Пароль"/>
        </span><br/>
        <label for="pres_password2">Повтор:</label>
        <span class="input-prepend">
          <span class="add-on"><i class="icon-key"></i></span>
          <input id="pres_password2" type="password" class="nopad normal" name="password2" placeholder="Пароль"/>
        </span><br/>
        <input type="hidden" name="scode" value="${restore_user?.scode}" />
        <input type="submit" class="fright" value="Изменить" />
      </g:formRemote>
    </div>
  </body>
</html>