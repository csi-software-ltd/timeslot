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
        ['spname','unit_id'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Название"])}</li>'; $('spname').addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Подразделение"])}</li>'; $('unit_id').addClassName('red'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Подразделение"])}</li>'; $('unit_id').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').show();
          jQuery('html,body').animate({scrollTop: 0},'slow');
        } else if(${space?1:0}){
          $("saveinfo").up('div').show();
          jQuery('html,body').animate({scrollTop: 0},'slow');
        } else if(e.responseJSON.space){
          location.assign('${createLink(controller:controllerName,action:'space')}'+'/'+e.responseJSON.space);
        } else
          location.assign('${createLink(controller:controllerName,action:'spaces')}');
      }
      function processAddspworkhoursResponse(e){
        var sErrorMsg = '';
        ['workhours_days'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        ['workhours_date_start','workhours_date_end'].forEach(function(ids){
          if($(ids))
            $(ids).up('span').removeClassName('k-error-colored');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Часы работы"])}</li>'; $('workhours_days').addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Часы работы"])}</li>'; $('workhours_days').addClassName('red'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Срок действия с"])}</li>'; $('workhours_date_start').up('span').addClassName('k-error-colored'); break;
              case 4: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Срок действия по"])}</li>'; $('workhours_date_end').up('span').addClassName('k-error-colored'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorspworkhourslist").innerHTML=sErrorMsg;
          $("errorspworkhourslist").up('div').show();
        } else
          jQuery('#spworkhoursAddForm').slideUp(300, function() { getWorkhours(); });
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
        if(${space?1:0}) $('spacewhours_submit_button').click();
      }
      function init(){
        jQuery('.nav').find('li:visible > a:first').click();
      }
    </g:javascript>
  </head>
  <body onload="init()">
    <h3 class="fleft">${infotext?.header?:'Подразделение'}</h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку объектов</a>
    <div class="clear"></div>
    <g:formRemote name="spaceDetailForm" url="${[action:'updatespacedetail',id:space?.id?:0]}" method="post" onSuccess="processResponse(e)">

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

      <g:csilabel for="spname">Название:</g:csilabel>
      <input type="text" class="fullline" id="spname" name="name" value="${space?.name}"/>
      <g:csilabel for="unit_id">Подразделение:</g:csilabel>
      <g:select name="unit_id" value="${space?.unit_id}" from="${units}" optionValue="name" optionKey="id" />
      <g:csilabel for="is_visible">Видимость:</g:csilabel>
      <g:select name="is_visible" value="${space?.is_visible}" from="['Да','Нет']" keys="${1..0}" />

      <g:csilabel for="description">Описание:</g:csilabel>
      <g:textArea name="description" id="description" value="${space?.description}" />

      <input style="display:none" type="submit" id="submit_button" value="Сохранить" />
      <div class="fright" id="btns" style="padding-top:10px">
        <input type="button" class="spacing" value="Сохранить" onclick="submitForm()" />
      </div>
    </g:formRemote>
    <div class="clear"></div>
  <g:if test="${space}">
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
    <g:formRemote name="spacewhoursForm" url="[action:'spacewhours',id:space.id]" update="details">
      <input type="submit" class="button" id="spacewhours_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
  </g:if>
    <g:form id="returnToListForm" name="returnToListForm" url="${[controller:'personal', action:'spaces', params:[fromDetails:action_id]]}"></g:form>
  </body>
</html>