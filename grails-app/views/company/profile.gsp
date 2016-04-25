<html>
  <head>
    <title>${infotext?.title?:'Timeslot приложение'}</title>
    <meta name="keywords" content="${infotext?.keywords?:''}" />
    <meta name="description" content="${infotext?.description?:''}" />
    <meta name="layout" content="partner" />
    <style type="text/css">
      label{min-width:160px}
    </style>
    <g:javascript>
      function processResponse(e){
        $("saveinfo").up('div').hide();
        $("errorlist").up('div').hide();
        var sErrorMsg = '';
        ['pname'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["ФИО"])}</li>'; $('pname').addClassName('red'); break;
              case 2: sErrorMsg+='<li>Некорректный пароль. Пароль не менее ${Tools.getIntVal(Dynconfig.findByName('customer.passwordlength')?.value,7)} знаков из больших и маленьких латинских букв и цифр</li>'; $("password").addClassName('red'); break;
              case 3: sErrorMsg+='<li>Пароли не совпадают</li>'; $("password").addClassName('red'); $("password2").addClassName('red'); break;
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
      function submitForm(){
        $('submit_button').click();
      }
    </g:javascript>
  </head>
  <body>
    <h3 class="fleft">${infotext?.header?:'Профиль'}</h3>
    <div class="clear"></div>
    <g:formRemote name="profileDetailForm" url="${[controller:'company', action:'updateprofile', params:[linkname:partner.linkname]]}" method="post" onSuccess="processResponse(e)">

      <div class="info-box" style="display:none;margin-top:0">
        <span class="icon icon-info-sign icon-3x"></span>
        <ul id="saveinfo">
          <li>Изменения сохранены!</li>
        </ul>
      </div>
      <div class="error-box" style="display:none">
        <span class="icon icon-warning-sign icon-3x"></span>
        <ul id="errorlist">
        </ul>
      </div>

      <g:csilabel for="email" disabled="true">Email:</g:csilabel>
      <input type="text" id="email" name="email" disabled value="${profile.email}"/>
      <g:csilabel for="is_emailconfirmed" disabled="true">Статус email:</g:csilabel>
      <input type="text" id="is_emailconfirmed" class="${!profile.is_emailconfirmed?'red':''}" name="is_emailconfirmed" disabled value="${profile.is_emailconfirmed?'подтвержден':'не подтвержден'}"/>

      <g:csilabel for="pname">ФИО:</g:csilabel>
      <input type="text" id="pname" class="fullline" name="name" value="${profile.name}"/>
      <g:csilabel for="phone">Телефон:</g:csilabel>
      <input type="text" id="phone" name="phone" value="${profile.phone}"/>

      <hr class="admin">

      <g:csilabel for="password">Пароль:</g:csilabel>
      <input type="password" id="password" name="password" value=""/>
      <g:csilabel for="password2">Повтор:</g:csilabel>
      <input type="password" id="password2" name="password2" value=""/>

      <hr class="admin">

      <input style="display:none" type="submit" id="submit_button" value="Сохранить" />
      <div class="fright" id="btns" style="padding-top:10px">
        <input type="button" class="spacing" value="Сохранить" onclick="submitForm()" />
      </div>
    </g:formRemote>
    <div class="clear"></div>
  </body>
</html>