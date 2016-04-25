<html>
  <head>
    <title>${infotext?.title?:'Timeslot приложение'}</title>
    <meta name="keywords" content="${infotext?.keywords?:''}" />
    <meta name="description" content="${infotext?.description?:''}" />
    <meta name="layout" content="main" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.default.min.css')}" type="text/css" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.common.min.css')}" type="text/css" />
    <g:javascript library="kendo.culture.ru-RU.min" />
    <g:javascript library="kendo.web.min" />
    <style type="text/css">
      label{min-width:160px}
      .input-append input,.input-append .add-on [class^="icon-"]:before{cursor:default!important}
      .tabs a:hover,.tabs a.active,.tabs a.active i{color:#000}
    </style>
    <g:javascript>
      function processResponse(e){
        $("saveinfo").up('div').hide();
        $("errorlist").up('div').hide();
        var sErrorMsg = '';
        ['pname','address','city','country','business_id'].forEach(function(ids){
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
        } else {
          $("saveinfo").up('div').show();
          jQuery('html,body').animate({scrollTop: 0},'slow');
        }
      }
      function processAddworkhoursResponse(e){
        var sErrorMsg = '';
        ['workhours_days'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Часы работы"])}</li>'; $('workhours_days').addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Часы работы"])}</li>'; $('workhours_days').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorworkhourslist").innerHTML=sErrorMsg;
          $("errorworkhourslist").up('div').show();
        } else
          jQuery('#workhoursAddForm').slideUp(300, function() { getWorkhours(); });
      }
      function submitForm(){
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
          case 0: getWorkhours();break;
        }
      }
      function getWorkhours(){
        $('partnerwhours_submit_button').click();
      }
      function init(){
        jQuery('.nav').find('li:visible > a:first').click();
      }
    </g:javascript>
  </head>
  <body onload="init()">
    <h3 class="fleft">${infotext?.header?:'Параметры организации'}</h3>
    <div class="clear"></div>
    <g:formRemote name="partnerDetailForm" url="${[action:'updateorgdetail']}" method="post" onSuccess="processResponse(e)">

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
    <g:if test="${partner.stype==2}">
      <g:csilabel for="lockin">Отмена брони за:</g:csilabel>
      <input type="text" id="lockin" name="lockin" value="${partner.lockin}"/>
    </g:if>
      <br/><g:csilabel for="gmt_id">Часовой пояс:</g:csilabel>
      <g:select name="gmt_id" value="${partner.gmt_id}" from="${Gmt.list()}" optionValue="name" optionKey="id"/>
      <g:csilabel for="timedelta_id">Временной шаг:</g:csilabel>
      <g:select name="timedelta_id" value="${partner.timedelta_id}" from="${Timedelta.list()}" optionKey="id" />
      <g:csilabel for="maxdays">Max период:<br/><small>брони в дн</small></g:csilabel>
      <input type="text" id="maxdays" name="maxdays" value="${partner.maxdays}"/>
      <g:csilabel for="minhours">Min период:<br/><small>брони в ч</small></g:csilabel>
      <input type="text" id="minhours" name="minhours" value="${partner.minhours}"/>

    <g:if test="${infotext?.itext1}">
      <div class="info-box" style="margin:0">
        <g:rawHtml>${infotext?.itext1?:''}</g:rawHtml>
      </div>
    </g:if><g:else><br/></g:else>

      <g:csilabel for="description">Описание:</g:csilabel>
    <g:if test="${partner.stype==2}">
      <label class="auto" for="is_privacy">
        <input type="checkbox" id="is_privacy" name="is_privacy" value="1" <g:if test="${partner.is_privacy}">checked</g:if> />
        Конфиденциальность
      </label>
    </g:if>
      <g:textArea name="description" id="description" value="${partner.description}" />

      <input style="display:none" type="submit" id="submit_button" value="Сохранить" />
      <div class="fright" id="btns" style="padding-top:10px">
        <input type="button" class="spacing" value="Сохранить" onclick="submitForm()" />
      </div>
    </g:formRemote>
    <div class="clear"></div>
    <div class="tabs">
      <ul class="nav">
        <li><a href="javascript:void(0)" onclick="viewCell(0)">Часы работы</a></li>
      </ul>
      <div class="tab-content">
        <div class="inner">
          <div id="details"></div>
        </div>
      </div>
    </div>
    <g:formRemote name="partnerwhoursForm" url="[action:'partnerwhours']" update="details">
      <input type="submit" class="button" id="partnerwhours_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
  </body>
</html>