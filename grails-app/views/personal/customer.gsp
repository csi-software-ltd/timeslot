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
        ['email','firstname','partnergroup_id'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Email"])}</li>'; $('email').addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Email"])}</li>'; $('email').addClassName('red'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.not.unique2.message",args:["Клиент", "Email"])}</li>'; $('email').addClassName('red'); break;
              case 4: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Имя"])}</li>'; $('firstname').addClassName('red'); break;
              case 5: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Группа"])}</li>'; $('partnergroup_id').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').show();
          jQuery('html,body').animate({scrollTop: 0},'slow');
        } else if(${customer?1:0}){
          $("saveinfo").up('div').show();
          jQuery('html,body').animate({scrollTop: 0},'slow');
        } else if(e.responseJSON.customer){
          location.assign('${createLink(controller:controllerName,action:'customer')}'+'/'+e.responseJSON.customer);
        } else
          location.assign('${createLink(controller:controllerName,action:'customers')}');
      }
      function submitForm(){
        $('submit_button').click();
      }
    </g:javascript>
  </head>
  <body>
    <h3 class="fleft">${infotext?.header?:'Клиент'}</h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку клиентов</a>
    <div class="clear"></div>
    <g:formRemote name="customerDetailForm" url="${[action:'updatecustomerdetail',id:customer?.customer_id?:0]}" method="post" onSuccess="processResponse(e)">

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

    <g:if test="${customer}">
      <g:csilabel for="login" disabled="true">Email:</g:csilabel>
      <input type="text" id="login" disabled value="${customer.email}" />
      <g:csilabel for="inputdate" disabled="true">Дата заведения:</g:csilabel>
      <input type="text" id="inputdate" disabled value="${String.format('%td.%<tm.%<tY %<tT',customer.inputdate)}" />
      <g:csilabel for="status" disabled="true">Статус:</g:csilabel>
      <input type="text" id="status" disabled value="${customer.modstatus?'активный':'неактивный'}" />

      <hr class="admin">
    </g:if><g:else>
      <g:csilabel for="email">Email:</g:csilabel>
      <input type="text" id="email" name="email" value="" />

      <hr class="admin">
    </g:else>

      <g:csilabel for="firstname">Имя:</g:csilabel>
      <input type="text" id="firstname" name="firstname" value="${customer?.firstname}"/>
      <g:csilabel for="lastname">Фамилия:</g:csilabel>
      <input type="text" id="lastname" name="lastname" value="${customer?.lastname}"/>
      <g:csilabel for="company">Компания:</g:csilabel>
      <input type="text" id="company" name="company" value="${customer?.company}"/>
      <g:csilabel for="phone">Телефон:</g:csilabel>
      <input type="text" id="phone" name="phone" value="${customer?.phone}"/>
      <g:csilabel for="partnergroup_id">Группа:</g:csilabel>
      <g:select name="partnergroup_id" value="${customer?.partnergroup_id}" from="${Partnergroup.findAllByPartner_id(user.partner_id)}" optionValue="name" optionKey="id"/>
      <g:csilabel for="is_trust">Доверие:</g:csilabel>
      <g:select name="is_trust" value="${customer?.is_trust}" from="['Нет','Есть']" keys="${0..1}" />

      <hr class="admin">

      <input style="display:none" type="submit" id="submit_button" value="Сохранить" />
      <div class="fright" id="btns" style="padding-top:10px">
        <input type="button" class="spacing" value="Сохранить" onclick="submitForm()" />
      </div>
    </g:formRemote>
    <div class="clear"></div>
    <g:form id="returnToListForm" name="returnToListForm" url="${[controller:'personal', action:'customers', params:[fromDetails:action_id]]}"></g:form>
  </body>
</html>