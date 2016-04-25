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
      function init(){
        $('form_submit_button').click();
      }
    </g:javascript>
  </head>
  <body onload="init()">
    <g:formRemote name="allForm" url="[action:'unitlist']" update="[success:'list']">
      <div class="padtop filter">
        <div class="fright">
          <input type="submit" style="display:none" id="form_submit_button" value="Показать" />
          <g:link controller="personal" action="unit" class="button fright">Новое подразделение &nbsp;<i class="icon-angle-right icon-large"></i></g:link>
        </div>
        <div class="clear"></div>
      </div>
    </g:formRemote>
    <div id="list"></div>
  </body>
</html>