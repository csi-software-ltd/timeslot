<html>
  <head>
    <title>${context.appname} - Клиенты</title>
    <meta name="layout" content="administrator" />
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
        $('email').value="";
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
      <g:formRemote name="customersForm" url="[controller:'admin', action:'customers']" update="[success:'list']">
        <g:csilabel class="auto" for="email">Email:</g:csilabel>
        <input type="text" id="email" name="email" value="${inrequest?.email}"/>
        <g:csilabel class="auto" for="companyname">Компания:</g:csilabel>
        <input type="text" id="companyname" name="companyname" value="${inrequest?.companyname}"/>
        <g:csilabel class="auto" for="modstatus">Статус:</g:csilabel>
        <g:select class="mini" name="modstatus" value="${inrequest?.modstatus}" from="['Неактивный','Активный']" keys="${0..1}" noSelection="${['-100':'все']}"/>
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