<html>
  <head>
    <title>${infotext?.title?:'Timeslot приложение'}</title>
    <meta name="keywords" content="${infotext?.keywords?:''}" />
    <meta name="description" content="${infotext?.description?:''}" />
    <meta name="layout" content="main" />
    <style type="text/css">
      label{min-width:160px}
    </style>
    <g:javascript>
      function returnToList(){
        $("returnToListForm").submit();
      }
      function processResponse(e){
        $("saveinfo").up('div').hide();
        $("errorlist").up('div').hide();
        var sErrorMsg = '';
        ['email','password','password2','fullname','usergroup_id'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Email"])}</li>'; $('email').addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Email"])}</li>'; $('email').addClassName('red'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.not.unique2.message",args:["Администратор", "Email"])}</li>'; $('email').addClassName('red'); break;
              case 4: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Пароль"])}</li>'; $('password').addClassName('red'); break;
              case 5: sErrorMsg+='<li>${message(code:"error.blank.message",args:["ФИО"])}</li>'; $('fullname').addClassName('red'); break;
              case 6: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Группа"])}</li>'; $('usergroup_id').addClassName('red'); break;
              case 7: sErrorMsg+='<li>Некорректный пароль. Пароль не менее ${Tools.getIntVal(Dynconfig.findByName('user.passwordlength')?.value,7)} знаков из больших и маленьких латинских букв и цифр</li>'; $("password").addClassName('red'); break;
              case 8: sErrorMsg+='<li>Пароли не совпадают</li>'; $("password").addClassName('red'); $("password2").addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').show();
          jQuery('html,body').animate({scrollTop: 0},'slow');
        } else if(${useredit?1:0}){
          $("saveinfo").up('div').show();
          jQuery('html,body').animate({scrollTop: 0},'slow');
        } else if(e.responseJSON.useredit){
          location.assign('${createLink(controller:controllerName,action:'user')}'+'/'+e.responseJSON.useredit);
        } else
          location.assign('${createLink(controller:controllerName,action:'users')}');
      }
      function submitForm(){
        $('submit_button').click();
      }
    </g:javascript>
  </head>
  <body>
    <h3 class="fleft">${infotext?.header?:'Администратор'}</h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку администраторов</a>
    <div class="clear"></div>
    <g:formRemote name="usereditDetailForm" url="${[action:'updateuserdetail',id:useredit?.id?:0]}" method="post" onSuccess="processResponse(e)">

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

    <g:if test="${useredit}">
      <g:csilabel for="login" disabled="true">Login:</g:csilabel>
      <input type="text" id="login" disabled value="${useredit.login}" />
      <g:csilabel for="inputdate" disabled="true">Дата заведения:</g:csilabel>
      <input type="text" id="inputdate" disabled value="${String.format('%td.%<tm.%<tY %<tT',useredit.inputdate)}" />
      <g:csilabel for="is_emailconfirmed" disabled="true">Статус email:</g:csilabel>
      <input type="text" id="is_emailconfirmed" disabled value="${useredit.is_emailconfirmed?'подтвержден':'не подтвержден'}" />
      <g:csilabel for="status" disabled="true">Статус:</g:csilabel>
      <input type="text" id="status" disabled value="${useredit.modstatus?'активный':'неактивный'}" />

      <hr class="admin">
    </g:if><g:else>
      <g:csilabel for="email">Email:</g:csilabel>
      <input type="text" id="email" name="email" value="" />

      <hr class="admin">
    </g:else>

      <g:csilabel for="fullname">ФИО:</g:csilabel>
      <input type="text" id="fullname" name="fullname" value="${useredit?.fullname}"/>
      <g:csilabel for="usergroup_id" disabled="${is_selfedit}">Группа:</g:csilabel>
      <g:select name="usergroup_id" value="${useredit?.usergroup_id}" from="${Usergroup.list()}" optionValue="name" optionKey="id" disabled="${is_selfedit}"/>
      <g:csilabel for="mobile">Телефон:</g:csilabel>
      <input type="text" id="mobile" name="mobile" value="${useredit?.mobile}"/>

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
    <g:form id="returnToListForm" name="returnToListForm" url="${[controller:'personal', action:'users', params:[fromDetails:action_id]]}"></g:form>
  </body>
</html>