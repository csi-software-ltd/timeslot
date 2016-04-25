<html>
  <head>
    <title>Административное приложение: Профиль администратора</title>
    <meta name="layout" content="administrator" />
    <g:javascript>
      function processResponse(e){
        $("saveinfo").up('div').hide();
        $("errorlist").up('div').hide();
        var sErrorMsg = '';
        ['pass','confirm_pass'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Пароль"])}</li>'; $('pass').addClassName('red'); break;
              case 2: sErrorMsg+='<li>Некорректный пароль. Пароль не менее ${Tools.getIntVal(Dynconfig.findByName('user.passwordlength')?.value,7)} знаков из больших и маленьких латинских букв и цифр</li>'; $("pass").addClassName('red'); break;
              case 3: sErrorMsg+='<li>Пароли не совпадают</li>'; $("pass").addClassName('red'); $("confirm_pass").addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').show();
          jQuery('html,body').animate({scrollTop: 0},'slow');
        } else {
          $("saveinfo").up('div').show();
          jQuery('html,body').animate({scrollTop: 0},'slow');
        }
      }
    </g:javascript>
  </head>  
  <body>
    <h3 class="fleft"></h3>
    <div class="clear"></div>
    <g:formRemote class="grid_12" name="passForm" url="[controller:'admin',action:'updateProfile']" method="post" onSuccess="processResponse(e)">

      <div class="info-box" id="msglist" style="display:none"> 
        <span class="icon icon-info-sign icon-3x"></span>
        <ul id="saveinfo">
          <li>Пароль изменен</li>
        </ul>
      </div>
      <div class="error-box" style="display:none">
        <span class="icon icon-warning-sign icon-3x"></span>
        <ul id="errorlist">
          <li></li>
        </ul>
      </div>

      <label for="pass">Новый пароль:</label>
      <input type="password" id="pass" name="pass" />
      <label for="confirm_pass">Повторить:</label>
      <input type="password" id="confirm_pass" name="confirm_pass" />
      <div class="fright">
        <input type="submit" value="Изменить пароль" />
      </div>
    </g:formRemote>

    <div class="clear"></div>
    <hr class="admin" />
    <div class="grid_12">
      <p><i>Последний вход пользователя: <b>${(lastlog?.logtime!=null)?String.format('%td.%<tm.%<tY %<tH:%<tM',lastlog?.logtime):''}</b> с IP адреса <b>${lastlog?.ip}</b>
    <g:if test="${(unsuccess_log_amount)&&(unsuccess_log_amount > unsucess_limit)}">
      <br/><font color="red">Неуспешных попыток доступа за последние 7 дней: <b>${unsuccess_log_amount}</b></font>
    </g:if></i></p>
    </div>
  </body>
</html>