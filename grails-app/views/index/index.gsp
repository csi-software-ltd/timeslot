<html>
  <head>
    <title>Timeslot приложение</title>
    <meta name="layout" content="main" />
    <g:javascript>
      function slide(sDir){
        jQuery("#loginDiv").animate({width:'toggle'},350);
        jQuery("#signUpDiv").animate({width:'toggle'},350);
      }
      function processResponse(e){
        $("signupErrorlist").up('div').hide();
        var sErrorMsg = '';
        ['su_org','su_username','su_name2','su_login','su_password','su_password2'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Организация"])}</li>'; $('su_org').addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Имя"])}</li>'; $('su_username').addClassName('red'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Фамилия"])}</li>'; $('su_name2').addClassName('red'); break;
              case 4: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Email"])}</li>'; $('su_login').addClassName('red'); break;
              case 5: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Пароль"])}</li>'; $('su_password').addClassName('red'); break;
              case 6: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Повтор"])}</li>'; $('su_password2').addClassName('red'); break;
              case 7: sErrorMsg+='<li>Некорректный пароль. Пароль не менее ${Tools.getIntVal(Dynconfig.findByName('user.passwordlength')?.value,7)} знаков из больших и маленьких латинских букв и цифр</li>'; $("su_password").addClassName('red'); break;
              case 8: sErrorMsg+='<li>Пароли не совпадают</li>'; $("su_password").addClassName('red'); $("su_password2").addClassName('red'); break;
              case 9: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Email"])}</li>'; $('su_login').addClassName('red'); break;
              case 10: sErrorMsg+='<li>${message(code:"error.not.unique2.message",args:["Пользователь", "Email"])}</li>'; $('su_login').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("signupErrorlist").innerHTML=sErrorMsg;
          $("signupErrorlist").up('div').show();
        } else
          location.assign('${createLink(controller:'personal',action:'organization')}');
      }
    </g:javascript>
  </head>
  <body>
    <div class="grid_3">&nbsp;</div>
    <div id="loginDiv" class="grid_6 padtop" style="max-height:201px">
      <h3 style="float:left">Войти в панель управления</h3>
      <a class="button fright" style="margin:20px 0px;padding:0;line-height: 18px;" href="javascript:void(0)" onclick="slide();">Регистрация &nbsp;<i class="icon-angle-right icon-large"></i></a>
      <div class="clear"></div>
      <g:if test="${flash?.error}">
        <div class="error-box">
          <span class="icon icon-warning-sign icon-3x"></span>
          <ul>
            <g:if test="${flash.error==1}"><li>Не введен логин</li></g:if>
            <g:elseif test="${flash.error==2}"><li>Пароль введен неверно, или пользователя с таким логином не существует</li></g:elseif>
            <g:elseif test="${flash.error==3}"><li>Доступ временно заблокирован</li></g:elseif> 
            <g:elseif test="${flash.error==4}"><li>Доступ для вашего ip заблокирован</li></g:elseif>
            <g:elseif test="${flash.error==5}"><li>Доступ заблокирован</li></g:elseif>
          </ul>
        </div>
      </g:if>
      <g:form url="[controller:'index',action:'login']" method="post" autocomplete='off'>
        <label for="login">Email:</label>
        <span class="input-prepend <g:if test="${flash?.error==1}">red</g:if>">
          <span class="add-on"><i class="icon-lock"></i></span>
          <input type="text" class="nopad normal" name="login" id="login" placeholder="Email"/>
        </span><br/>
        <label for="password">Пароль:</label>
        <span class="input-prepend <g:if test="${flash?.error==2}">red</g:if>">
          <span class="add-on"><i class="icon-key"></i></span>
          <input type="password" class="nopad normal" name="password" placeholder="Пароль"/>
        </span><br/>
        <label class="auto" for="is_remember">
          <input type="checkbox" id="is_remember" name="is_remember" value="1" />
          запомнить
        </label>
        <span>
          <g:link class="button" style="margin:0 15px;padding-top:10px;line-height: 18px;" controller="index" action="restore">Склероз? &nbsp;<i class="icon-angle-right icon-large"></i></g:link>
        </span>
        <input type="submit" class="fright" value="Войти" />
      </g:form>
    </div>
    <div id="signUpDiv" class="grid_6 padtop" style="display:none;max-height:201px">
      <a class="button fleft" style="margin:20px 0px;padding:0;line-height: 18px;" href="javascript:void(0)" onclick="slide();"><i class="icon-angle-left icon-large"></i>&nbsp; Авторизация</a>
      <h3 style="float:right">Регистрация учетной записи</h3>
      <div class="clear"></div>
      <div class="error-box" style="display:none">
        <span class="icon icon-warning-sign icon-3x"></span>
        <ul id="signupErrorlist">
        </ul>
      </div>
      <g:formRemote name="signupForm" url="${[controller:'index',action:'signup']}" method="post" autocomplete='off' onSuccess="processResponse(e)">
        <label for="su_org">Организация:</label>
        <span class="input-prepend">
          <span class="add-on"><i class="icon-lock"></i></span>
          <input type="text" class="nopad normal" name="orgname" id="su_org" placeholder="Название"/>
        </span><br/>
        <label for="su_username">Имя:</label>
        <span class="input-prepend">
          <span class="add-on"><i class="icon-lock"></i></span>
          <input type="text" class="nopad normal" name="username" id="su_username" placeholder="Имя"/>
        </span><br/>
        <label for="su_name2">Фамилия:</label>
        <span class="input-prepend">
          <span class="add-on"><i class="icon-lock"></i></span>
          <input type="text" class="nopad normal" name="name2" id="su_name2" placeholder="Фамилия"/>
        </span><br/>
        <label for="su_login">Email:</label>
        <span class="input-prepend">
          <span class="add-on"><i class="icon-lock"></i></span>
          <input type="text" class="nopad normal" name="login" id="su_login" placeholder="Email"/>
        </span><br/>
        <label for="su_password">Пароль:</label>
        <span class="input-prepend">
          <span class="add-on"><i class="icon-key"></i></span>
          <input id="su_password" type="password" class="nopad normal" name="password" placeholder="Пароль"/>
        </span><br/>
        <label for="su_password2">Повтор:</label>
        <span class="input-prepend">
          <span class="add-on"><i class="icon-key"></i></span>
          <input id="su_password2" type="password" class="nopad normal" name="password2" placeholder="Пароль"/>
        </span><br/>
        <label class="auto" for="is_remember">
          <input type="checkbox" id="is_remember" name="is_remember" value="1" />
          запомнить
        </label>
        <input type="submit" class="fright" value="Войти" />
      </g:formRemote>
    </div>
  </body>
</html>