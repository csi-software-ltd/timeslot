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
      function returnToList(){
        $("returnToListForm").submit();
      }
      function processResponse(e){
        $("saveinfo").up('div').hide();
        $("errorlist").up('div').hide();
        var sErrorMsg = '';
        ['uname'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Название"])}</li>'; $('uname').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').show();
          jQuery('html,body').animate({scrollTop: 0},'slow');
        } else if(${unit?1:0}){
          $("saveinfo").up('div').show();
          jQuery('html,body').animate({scrollTop: 0},'slow');
        } else if(e.responseJSON.unit){
          location.assign('${createLink(controller:controllerName,action:'unit')}'+'/'+e.responseJSON.unit);
        } else
          location.assign('${createLink(controller:controllerName,action:'units')}');
      }
      function processAddunitpriceResponse(e){
        var sErrorMsg = '';
        ['unitprice_price','unitprice_forperiod','unitprice_minperiod','unitprice_days'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Цена"])}</li>'; $('unitprice_price').addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Размерность"])}</li>'; $('unitprice_forperiod').addClassName('red'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Размерность"])}</li>'; $('unitprice_forperiod').addClassName('red'); break;
              case 4: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Мин. резерв"])}</li>'; $('unitprice_minperiod').addClassName('red'); break;
              case 5: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Мин. резерв"])}</li>'; $('unitprice_minperiod').addClassName('red'); break;
              case 6: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Применяется для"])}</li>'; $('unitprice_days').addClassName('red'); break;
              case 7: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Применяется для"])}</li>'; $('unitprice_days').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorunitpricelist").innerHTML=sErrorMsg;
          $("errorunitpricelist").up('div').show();
        } else
          jQuery('#unitpriceAddForm').slideUp(300, function() { getPrices(); });
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
          case 0: getPrices();break;
        }
      }
      function getPrices(){
        if(${unit?1:0}) $('prices_submit_button').click();
      }
      function init(){
        jQuery('.nav').find('li:visible > a:first').click();
      }
    </g:javascript>
  </head>
  <body onload="init()">
    <h3 class="fleft">${infotext?.header?:'Подразделение'}</h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку подразделений</a>
    <div class="clear"></div>
    <g:formRemote name="unitDetailForm" url="${[action:'updateunitdetail',id:unit?.id?:0]}" method="post" onSuccess="processResponse(e)">

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

      <g:csilabel for="uname">Название:</g:csilabel>
      <input type="text" id="uname" name="name" value="${unit?.name}"/>
      <g:csilabel for="contactperson">Контактное лицо:</g:csilabel>
      <input type="text" id="contactperson" name="contactperson" value="${unit?.contactperson}"/>

      <g:csilabel for="description">Описание:</g:csilabel>
      <g:textArea name="description" id="description" value="${unit?.description}" />

      <input style="display:none" type="submit" id="submit_button" value="Сохранить" />
      <div class="fright" id="btns" style="padding-top:10px">
        <input type="button" class="spacing" value="Сохранить" onclick="submitForm()" />
      </div>
    </g:formRemote>
    <div class="clear"></div>
  <g:if test="${unit}">
    <div class="tabs">
      <ul class="nav">
        <li><a href="javascript:void(0)" onclick="viewCell(0)">Цены</a></li>
      </ul>
      <div class="tab-content">
        <div class="inner">
          <div id="details"></div>
        </div>
      </div>
    </div>
    <g:formRemote name="pricesForm" url="[action:'unitprices',id:unit.id]" update="details">
      <input type="submit" class="button" id="prices_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
  </g:if>
    <g:form id="returnToListForm" name="returnToListForm" url="${[controller:'personal', action:'units', params:[fromDetails:action_id]]}"></g:form>
  </body>
</html>