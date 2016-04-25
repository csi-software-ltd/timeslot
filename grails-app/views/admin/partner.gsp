<html>
  <head>
    <title>${context.appname}: <g:if test="${partner}">Редактирование партнера № ${partner.id}</g:if><g:else>Добавление нового партнера</g:else></title>
    <meta name="layout" content="administrator" />
    <g:javascript>
      function returnToList(){
        $("returnToListForm").submit();
      }
      function loginAsUser(iId){
        <g:remoteFunction controller='admin' action='loginAsUser' onSuccess='processLoginResponse(e)' params="'id='+iId" />
      }
      function processLoginResponse(e){
        window.open('${createLink(controller:'personal',action:'organization')}');
      }
      function processResponse(e){
        $("saveinfo").up('div').hide();
        $("errorlist").up('div').hide();
        var sErrorMsg = '';
        ['fullname','login','email','usergroup_id','password','confirm_password'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Название"])}</li>'; $('pname').addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Адрес"])}</li>'; $('address').addClassName('red'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Город"])}</li>'; $('city').addClassName('red'); break;
              case 4: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Страна"])}</li>'; $('country').addClassName('red'); break;
              case 5: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Тип бизнеса"])}</li>'; $('business_id').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').show();
          jQuery('html,body').animate({scrollTop: 0},'slow');
        } else if(${partner?1:0}){
          $("saveinfo").up('div').show();
          jQuery('html,body').animate({scrollTop: 0},'slow');
        } else
          returnToList();
      }
      function submitForm(iStatus){
        $('modstatus').value = iStatus
        $('submit_button').click();
      }
      function viewCell(iNum){
        var tabs = jQuery('.nav').find('li');
        for(var i=0; i<tabs.length; i++){
          if(i==iNum)
            tabs[i].addClassName('selected');
          else
            tabs[i].removeClassName('selected');
        }
        switch(iNum){
          case 0: getPartnerUsers();break;
          case 1: getSpaces();break;
        }
      }
      function getPartnerUsers(){
        if(${partner?1:0}) $('partnerusers_submit_button').click();
      }
      function getSpaces(){
        if(${partner?1:0}) $('partnerspaces_submit_button').click();
      }
      function init(){
        jQuery('.nav').find('li:visible > a:first').click();
      }
    </g:javascript>
    <style type="text/css">
      label{min-width:160px}
      input.normal{width:202px}      
      input.mini{width:60px!important}
    </style>
  </head>
  <body onload="init();">
    <h3 class="fleft"><g:if test="${partner}">${partner.name}</g:if><g:else>Добавление нового партнера</g:else></h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку партнеров</a>
    <div class="clear"></div>
    <g:formRemote name="partnerForm" url="[action:'updatepartner', id:partner?.id?:0]" method="post" onSuccess="processResponse(e)">

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

    <g:if test="${partner}">
      <g:csilabel for="partner_id" disabled="true">Id:</g:csilabel>
      <input type="text" id="partner_id" disabled value="${partner.id}" />
      <g:csilabel for="status" disabled="true">Статус:</g:csilabel>
      <input type="text" id="status" disabled value="${partner.modstatus?'активный':'неактивный'}" />

      <hr class="admin">
    </g:if>

      <g:csilabel for="pname">Название:</g:csilabel>
      <input type="text" id="pname" name="pname" value="${partner.name}"/>
      <g:csilabel for="web">Web сайт:</g:csilabel>
      <input type="text" id="web" name="web" value="${partner.web}"/>
      <g:csilabel for="email">Email:</g:csilabel>
      <input type="text" id="email" name="email" value="${partner.email}"/>
      <g:csilabel for="tel">Телефон:</g:csilabel>
      <input type="text" id="tel" name="tel" value="${partner.tel}"/>
      <g:csilabel for="country">Страна:</g:csilabel>
      <input type="text" id="country" name="country" value="${partner.country?:'Россия'}"/>
      <g:csilabel for="city">Город:</g:csilabel>
      <input type="text" id="city" name="city" value="${partner.city}"/>
      <g:csilabel for="address">Адрес:</g:csilabel>
      <input type="text" class="fullline" id="address" name="address" value="${partner.address}"/>
      <g:csilabel for="business_id">Тип бизнеса:</g:csilabel>
      <g:select name="business_id" value="${partner.business_id}" from="${Business.list()}" optionValue="name" optionKey="id" noSelection="${['0':'не выбрано']}"/>
      <g:csilabel for="lockin">Отмена брони за:</g:csilabel>
      <input type="text" id="lockin" name="lockin" value="${partner.lockin}"/>
      <br/><g:csilabel for="gmt_id">Часовой пояс:</g:csilabel>
      <g:select name="gmt_id" value="${partner.gmt_id}" from="${Gmt.list()}" optionValue="name" optionKey="id"/>
      <g:csilabel for="timedelta_id">Временной шаг:</g:csilabel>
      <g:select name="timedelta_id" value="${partner.timedelta_id}" from="${Timedelta.list()}" optionKey="id" />
      <g:csilabel for="maxdays">Max период:<br/><small>брони в дн</small></g:csilabel>
      <input type="text" id="maxdays" name="maxdays" value="${partner.maxdays}"/>
      <g:csilabel for="minhours">Min период:<br/><small>брони в ч</small></g:csilabel>
      <input type="text" id="minhours" name="minhours" value="${partner.minhours}"/>
      <g:csilabel for="stype">Тип обслуживания:</g:csilabel>
      <g:select name="stype" value="${partner.stype}" from="['Стандарт','Премиум']" keys="[1,2]"/>
      <g:csilabel for="payway">Платежи:</g:csilabel>
      <g:select name="payway" value="${partner.payway}" from="['Без оплаты','Прием платежей']" keys="[0,1]"/>

      <g:csilabel for="description">Описание:</g:csilabel>
      <label class="auto" for="is_privacy">
        <input type="checkbox" id="is_privacy" name="is_privacy" value="1" <g:if test="${partner.is_privacy}">checked</g:if> />
        Конфиденциальность
      </label>
      <g:textArea name="description" id="description" value="${partner.description}" />

      <hr class="admin" />

      <input type="hidden" id="modstatus" name="modstatus" value="${partner.modstatus}"/>
      <input style="display:none" type="submit" id="submit_button" value="Сохранить" />
      <div class="fright" id="btns" style="padding-top:10px">
        <input type="reset" value="Отменить" onclick="returnToList()" />
        <input type="button" class="spacing" value="Сохранить" onclick="submitForm(${partner.modstatus})" />
      <g:if test="${partner.modstatus==0}">
        <input type="button" class="spacing" value="Активировать" onclick="submitForm(1)"/>
      </g:if><g:else>
        <input type="button" class="spacing" value="Деактивировать" onclick="submitForm(0)"/>
      </g:else>
      </div>
    </g:formRemote>
    <div class="clear"></div>
  <g:if test="${partner}">
    <div class="tabs">
        <ul class="nav">
          <li><a href="javascript:void(0)" onclick="viewCell(0)">Администраторы</a></li>
          <li><a href="javascript:void(0)" onclick="viewCell(1)">Объекты</a></li>
        </ul>
        <div class="tab-content">
          <div class="inner">
            <div id="details"></div>
          </div>
        </div>
      </div>
    </div> 
    <g:formRemote name="partnerusersForm" url="[action:'partnerusers', id:partner.id]" update="[success:'details']">
      <input type="submit" class="button" id="partnerusers_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="partnerspacesForm" url="[action:'partnerspaces', id:partner.id]" update="[success:'details']">
      <input type="submit" class="button" id="partnerspaces_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
  </g:if>
    <g:form id="returnToListForm" name="returnToListForm" url="${[controller:'admin', action:'partnerfilter', params:[fromDetails:action_id]]}"></g:form>
  </body>
</html>