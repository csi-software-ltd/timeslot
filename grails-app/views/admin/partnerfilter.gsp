<html>
  <head>
    <title>${context.appname} - Партнеры</title>
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
      <g:formRemote name="partnersForm" url="[controller:'admin', action:'partners']" update="[success:'list']">
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