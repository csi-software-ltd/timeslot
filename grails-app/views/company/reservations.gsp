<html>
  <head>
    <title>${infotext?.title?:'Timeslot приложение'}</title>
    <meta name="keywords" content="${infotext?.keywords?:''}" />
    <meta name="description" content="${infotext?.description?:''}" />
    <meta name="layout" content="partner" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.default.min.css')}" type="text/css" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.common.min.css')}" type="text/css" />
    <g:javascript library="kendo.culture.ru-RU.min" />
    <g:javascript library="kendo.web.min" />
    <g:javascript>
      function clickPaginate(event){
        event.stop();
        var link = event.element();
        if(link.href == null){
          return;
        }
        new Ajax.Updater(
          { success: $('ajax_wrap') },
          link.href,
          { evalScripts: true });
      }
      function resetFilter(){
        $('title').value="";
        $('modstatus').selectedIndex=0;
      }
      function init(){
        $('form_submit_button').click();
      }
    </g:javascript>
  </head>
  <body onload="init()">
    <g:formRemote name="allForm" url="[controller:'company', action:'reservationlist', params:[linkname:partner.linkname]]" update="[success:'list']">
      <div class="padtop filter">
        <g:csilabel class="auto" for="title">Название:</g:csilabel>
        <input type="text" id="title" name="title" value="${inrequest?.title}"/>
        <g:csilabel class="auto" for="fromtime">Дата брони с:</g:csilabel>
        <g:datepicker class="normal nopad" name="fromtime" value="${String.format('%td.%<tm.%<tY',inrequest?.fromtime?:new Date())}"/>
        <g:csilabel class="auto" for="totime">по:</g:csilabel>
        <g:datepicker class="normal nopad" name="totime" value="${String.format('%td.%<tm.%<tY',inrequest?.totime?:new Date()+7)}"/>
        <g:csilabel class="auto" for="modstatus">Статус:</g:csilabel>
        <g:select class="mini" name="modstatus" value="${inrequest?.modstatus}" from="['Подтверждена','Не подтверждена', 'Отменена']" keys="${[1,0,-1]}" noSelection="${['-100':'все']}"/>
        <div class="fright">
          <input type="button" class="reset spacing" value="Сброс" onclick="resetFilter()"/>
          <input type="submit" id="form_submit_button" value="Показать" />
        </div>
        <div class="clear"></div>
      </div>
    </g:formRemote>
    <div id="list"></div>
  </body>
</html>