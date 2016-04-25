<html>
  <head>
    <title>${context.appname} - Брони</title>
    <meta name="layout" content="administrator" />
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
        $('customername').value="";
        $('companyname').value="";
        $('modstatus').selectedIndex=0;
        return true;
      }
      function init(){
        $('form_submit_button').click();
      }
    </g:javascript>
  </head>
  <body onload="init()">
    <div class="clear"></div>
    <div id="filter" class="padtop filter">
      <g:formRemote name="customersForm" url="[controller:'admin', action:'bookings']" update="[success:'list']">
        <g:csilabel class="auto" for="customername">Клиент:</g:csilabel>
        <input type="text" id="customername" name="customername" value="${inrequest?.customername}"/>
        <g:csilabel class="auto" for="fromtime">Дата брони с:</g:csilabel>
        <g:datepicker class="normal nopad" name="fromtime" value="${String.format('%td.%<tm.%<tY',inrequest?.fromtime?:new Date())}"/>
        <g:csilabel class="auto" for="totime">по:</g:csilabel>
        <g:datepicker class="normal nopad" name="totime" value="${String.format('%td.%<tm.%<tY',inrequest?.totime?:new Date()+1)}"/>
        <br/><g:csilabel class="auto" for="companyname">Компания:</g:csilabel>
        <input type="text" id="companyname" name="companyname" value="${inrequest?.companyname}"/>
        <g:csilabel class="auto" for="modstatus">Статус:</g:csilabel>
        <g:select class="mini" name="modstatus" value="${inrequest?.modstatus}" from="['Подтверждена','Не подтверждена', 'Отменена']" keys="${[1,0,-1]}" noSelection="${['-100':'все']}"/>
        <div class="fright">
          <input type="button" class="reset spacing" value="Сброс" onclick="resetFilter()"/>
          <input type="submit" id="form_submit_button" value="Показать" />
        </div>
        <div class="clear"></div>
      </g:formRemote>
    </div>
    <div id="list"></div>
  </body>
</html>