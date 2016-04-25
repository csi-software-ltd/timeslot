<html>
  <head>
    <title>${infotext?.title?:'Timeslot приложение'}</title>
    <meta name="keywords" content="${infotext?.keywords?:''}" />
    <meta name="description" content="${infotext?.description?:''}" />
    <meta name="layout" content="main" />
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
        $('searchname').value="";
        $('is_trust').selectedIndex=0;
        $('modstatus').selectedIndex=0;
      }
      function init(){
        $('form_submit_button').click();
      }
    </g:javascript>
  </head>
  <body onload="init()">
    <g:formRemote name="allForm" url="[action:'customerlist']" update="[success:'list']">
      <div class="padtop filter">
        <g:csilabel class="auto" for="searchname">Фильтр:</g:csilabel>
        <input type="text" id="searchname" name="searchname" value="${inrequest?.searchname}"/>
        <g:csilabel class="auto" for="is_trust">Доверие:</g:csilabel>
        <g:select class="mini" name="is_trust" value="${inrequest?.is_trust}" from="['Нет','Есть']" keys="${0..1}" noSelection="${['-100':'все']}"/>
        <g:csilabel class="auto" for="modstatus">Статус:</g:csilabel>
        <g:select class="mini" name="modstatus" value="${inrequest?.modstatus}" from="['Неактивный','Активный']" keys="${0..1}" noSelection="${['-100':'все']}"/>
        <div class="fright">
          <g:link controller="personal" action="customer" class="button">Новый клиент &nbsp;<i class="icon-angle-right icon-large"></i></g:link>
          <input type="button" class="reset spacing" value="Сброс" onclick="resetFilter()"/>
          <input type="submit" id="form_submit_button" value="Показать" />
        </div>
        <div class="clear"></div>
      </div>
    </g:formRemote>
    <div id="list"></div>
  </body>
</html>